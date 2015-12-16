package com.dr.pkpassviewer.controllers;

import com.dr.pkpassviewer.entities.Pkpass;


import dr.android.pkpassviewer.R;
import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Show qr and aztec codes in bigger variant, also show text of code.
 * 
 * @author Verma Rahul
 * 
 */
public class PkpassBarcodeDetailFragment extends Fragment {

	protected ImageView imgBarcode;
	protected TextView txtBarcode;
	protected Pkpass pkpass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.qr_aztec_big, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		pkpass = ((PkpassOwner) getActivity()).getPkpass();
		imgBarcode = (ImageView) view.findViewById(R.id.idBarcodeImage);
		txtBarcode = (TextView) view.findViewById(R.id.idBarcodeText);
		imgBarcode.setBackgroundDrawable(pkpass.getBarcodeImage());
		txtBarcode.setText(pkpass.getBarcodeMessage());
		view.setBackgroundColor(pkpass.getBackgroundColor());
		txtBarcode.setTextColor(pkpass.getForegroundColor());

		imgBarcode.post(new Runnable() {

			@Override
			public void run() {
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgBarcode
						.getLayoutParams();
				params.height = imgBarcode.getWidth();
				params.width = imgBarcode.getWidth();
				imgBarcode.setLayoutParams(params);
				imgBarcode.requestLayout();
			}
		});
	}

}
