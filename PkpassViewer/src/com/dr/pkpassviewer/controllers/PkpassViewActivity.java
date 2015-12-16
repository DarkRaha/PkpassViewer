package com.dr.pkpassviewer.controllers;



import com.dr.pkpassviewer.entities.Pkpass;

import com.dr.pkpassviewer.managers.PkpassManager;

import dr.android.pkpassviewer.R;

import android.net.Uri;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.io.InputStream;

/**
 * Default activity for show PkpassDetailFragment.
 * 
 * @author Verma Rahul
 * 
 */
public class PkpassViewActivity extends Activity implements PkpassOwner,
		LoaderCallbacks<Pkpass> {

	private Pkpass pkpass;
	private Uri uriPkpass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.replace(R.id.container, new PkpassDetailFragment()).commit();
		}

		getLoaderManager().initLoader(0, null, this);
	}

	

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public Pkpass getPkpass() {
		return pkpass;
	}

	// ======================================================================
	public void onClickInfo(View v) {
		PkpassBackFieldsFragment pi = new PkpassBackFieldsFragment();

		FragmentManager fm = getFragmentManager();
		fm.beginTransaction()
				.setCustomAnimations(R.animator.flip_right_in,
						R.animator.flip_right_out, R.animator.flip_left_in,
						R.animator.flip_left_out).replace(R.id.container, pi)
				.addToBackStack("PkpassInfo").commit();
	}

	// ====================================================================
	public void onClickSaveDelete(View v) {

		if (pkpass != null) {
			PkpassManager m = PkpassManager.getSharedInstance();

			if (m.exist(pkpass)) {
				onDelete();
				return;
			}

			onSave();
		}
	}

	protected void onSave() {
		AsyncTask<Void, Void, Void> saveTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				PkpassManager m = PkpassManager.getSharedInstance();

				if(pkpass.getSourceFile()==null){
					InputStream is=null;
					try {
					    is = getApplicationContext().getContentResolver().openInputStream(getIntent().getData());
						m.addPkpass(pkpass, is);
						is.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}else {
					m.addPkpass(pkpass);
				}


				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Toast.makeText(PkpassViewActivity.this,
						R.string.toast_pkpass_saved, Toast.LENGTH_SHORT).show();
			}

		};

		saveTask.execute();
	}

	protected void onDelete() {
		if (!pkpass.getSourceFile().exists()) {
			Toast.makeText(this, R.string.toast_pkpass_already_deleted,
					Toast.LENGTH_SHORT).show();
			return;
		}

		Builder builder = new Builder(this);
		builder.setCancelable(true).setMessage(R.string.dlg_msg_delete_pkpass)
				.setPositiveButton(R.string.dlg_positive,

				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						final PkpassManager m = PkpassManager
								.getSharedInstance();
						AsyncTask<Void, Void, Void> saveTask = new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {
								m.removePkpass(pkpass);
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								Toast.makeText(PkpassViewActivity.this,
										R.string.toast_pkpass_deleted,
										Toast.LENGTH_SHORT).show();
							}
						};

						saveTask.execute();
					}
				});

		builder.setNegativeButton(R.string.dlg_negative,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	// ==================================================
	@Override
	public Loader<Pkpass> onCreateLoader(int id, Bundle args) {
		String path = null;
		try {
			path = getIntent().getData().getPath();


		} catch (Exception e) {
			e.printStackTrace();
			
		}

		return new PkpassLoader(this, path, getIntent().getData());
	}

	@Override
	public void onLoadFinished(Loader<Pkpass> loader, Pkpass data) {
		pkpass = data;
		final Fragment f = getFragmentManager().findFragmentById(R.id.container);
		
		if (f instanceof PkpassDetailFragment) {
			System.out.println("Pkpass detail try show  pkpass="+pkpass);
			((PkpassDetailFragment) f).viewData();
			

			
		}
	}

	@Override
	public void onLoaderReset(Loader<Pkpass> loader) {

	}

}
