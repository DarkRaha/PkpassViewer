package com.dr.pkpassviewer.controllers;

import com.dr.pkpassviewer.entities.Pkpass;

import dr.android.pkpassviewer.R;
import android.app.Fragment;
import android.os.Bundle;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * 
 * 
 * @author Verma Rahul
 *
 */
public class PkpassBackFieldsFragment extends Fragment {

	protected EditText info;
	protected Pkpass pkpass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pkpass_back_fields_view, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		info = (EditText) view.findViewById(R.id.pkvInfo);
	}

	@Override
	public void onResume() {
		super.onResume();
		pkpass = ((PkpassOwner) getActivity()).getPkpass();
		initBackFields();
	}

	protected void initBackFields() {
		info.setText(null);
		if (pkpass != null) {
			String backFieldsText = pkpass.buildBackFieldsHtmlText();
			info.setText(Html.fromHtml(backFieldsText));
		}
	}
}