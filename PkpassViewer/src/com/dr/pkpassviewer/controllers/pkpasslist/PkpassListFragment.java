package com.dr.pkpassviewer.controllers.pkpasslist;

import java.io.File;

import com.dr.pkpassviewer.entities.Pkpass;
import com.dr.pkpassviewer.managers.PkpassManager;
import com.dr.pkpassviewer.managers.PkpassManager.PkpassManagerListener;
import com.dr.util.os.android.SpecFilesDirs;

import dr.android.pkpassviewer.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * @author Verma Rahul
 * 
 */
public class PkpassListFragment extends Fragment implements
		LoaderCallbacks<Pkpass[]>, PkpassManagerListener {

	private static final String DELETE_PKPASS_FILE = "PkpassListFragment.DELETE_PKPASS_FILE";

	public static interface OnPkpassView {
		public void onPkpassView(Pkpass pkpass);
	}

	protected ListView lstPkpass;
	protected PkpassListAdapter lstPkpassAdapter = new PkpassListAdapter();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View ret = inflater.inflate(R.layout.pkpass_list, container, false);
		SpecFilesDirs.getSharedInstance(getActivity());
		return ret;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		lstPkpass = (ListView) view.findViewById(R.id.pkpass_list);
		lstPkpass.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onPkpassView((Pkpass) parent.getItemAtPosition(position));
			}
		});

		lstPkpass
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						Pkpass pkpassDeleting = (Pkpass) parent
								.getItemAtPosition(position);
						PkpassDeleteAlert alert = PkpassDeleteAlert
								.newInstance(pkpassDeleting);

						alert.show(getFragmentManager(), DELETE_PKPASS_FILE);
						
						return true;
					}
				});

		lstPkpass.setAdapter(lstPkpassAdapter);
		PkpassManager.getSharedInstance().addListener(this);
	}

	/**
	 * Default implementation invoke activity through OnPkpassView interface.
	 * 
	 * @param pkpass
	 */
	protected void onPkpassView(Pkpass pkpass) {
		((OnPkpassView) getActivity()).onPkpassView(pkpass);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PkpassManager.getSharedInstance().removeListener(this);
	}

	// =======================================================================

	@Override
	public Loader<Pkpass[]> onCreateLoader(int id, Bundle args) {
		return new PkpassListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Pkpass[]> loader, Pkpass[] data) {
		lstPkpassAdapter.setData(data);
	}

	@Override
	public void onLoaderReset(Loader<Pkpass[]> loader) {

	}

	// ========================================================================
	@Override
	public void onDatasetChanged(PkpassManager pkpassManager, Pkpass item) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				getLoaderManager().getLoader(0).forceLoad();
			}
		});

	}

	// ========================================================================
	public static class PkpassDeleteAlert extends DialogFragment {

		public static PkpassDeleteAlert newInstance(Pkpass pkpass) {
			PkpassDeleteAlert frag = new PkpassDeleteAlert();
			Bundle args = new Bundle();

			args.putSerializable(DELETE_PKPASS_FILE, pkpass.getSourceFile());

			frag.setArguments(args);
			return frag;
		}

		private Pkpass pkpassDeleting;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			File fileDeleting = (File) getArguments().getSerializable(
					DELETE_PKPASS_FILE);
			pkpassDeleting = PkpassManager.getSharedInstance().getPkpass(
					fileDeleting);

			return new AlertDialog.Builder(getActivity())
					.setMessage(R.string.dlg_msg_delete_pkpass)
					.setPositiveButton(R.string.dlg_positive,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (pkpassDeleting != null) {
										PkpassManager.getSharedInstance()
												.removePkpass(pkpassDeleting);
									}
								}
							})
					.setNegativeButton(R.string.dlg_negative,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									//
								}
							}).create();
		}
	}

}
