package com.dr.pkpassviewer.controllers;

import java.lang.ref.WeakReference;

import android.app.Fragment;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dr.pkpassviewer.entities.Pkpass;
import com.dr.pkpassviewer.layouts.FlowLayouter;
import com.dr.pkpassviewer.managers.PkpassManager;
import com.dr.util.java.DateUtil;
import com.dr.util.os.android.ViewUtils;

import dr.android.pkpassviewer.R;

/**
 * 
 * @author Verma Rahul
 * 
 */
public class PkpassDetailFragment extends Fragment {
		
	protected Pkpass pkpass;

	protected ViewGroup header, detail, container, primary_secondary;

	protected RelativeLayout aux, secondary, primary;

	protected TextView headerTitle, hdrTitle, hdrValue;
	protected ImageView logo;

	protected ImageView imgBarcode, imgStrip, imgThumbnail;
	protected TextView txtBarcode;
	protected TextView txtRelevantDate;
	protected Button btSaveDel;
	protected FlowLayouter flowLayouter = new FlowLayouter();

	int lenPrimary, lenSecondary;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pkpass_view, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		header = (ViewGroup) view.findViewById(R.id.pkvHeader);
		detail = (ViewGroup) view.findViewById(R.id.pkvDetail);
		container = (ViewGroup) view.findViewById(R.id.pkvView);
		primary_secondary = (ViewGroup) view
				.findViewById(R.id.pkvPrimarySecondary);
		aux = (RelativeLayout) view.findViewById(R.id.pkvAuxalary);
		primary = (RelativeLayout) view.findViewById(R.id.pkvPrimary);

		// ------------------------------------------
		headerTitle = (TextView) view.findViewById(R.id.pkvTitle);
		hdrTitle = (TextView) view.findViewById(R.id.pkvHdrTitle);
		hdrValue = (TextView) view.findViewById(R.id.pkvHdrValue);

		// ------------------------------------------

		logo = (ImageView) view.findViewById(R.id.pkvLogo);
		imgStrip = (ImageView) view.findViewById(R.id.pkvStrip);

		imgBarcode = (ImageView) view.findViewById(R.id.imgBarcode);
		imgThumbnail = (ImageView) view.findViewById(R.id.pkvThumbnail);
		txtBarcode = (TextView) view.findViewById(R.id.txtBarcode);
		secondary = (RelativeLayout) view.findViewById(R.id.pkvSecondary);
		txtRelevantDate = (TextView) view.findViewById(R.id.pkvDate);

		btSaveDel = (Button) view.findViewById(R.id.pkvSave);
		// root = (ScrollView) view.findViewById(R.id.root);

	}

	@Override
	public void onResume() {
		super.onResume();		
		getView().post(new Runnable() {

			@Override
			public void run() {
				try {
					viewData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void viewData() {
		
		pkpass =( (PkpassOwner) getActivity()).getPkpass();
		if (pkpass == null) {
			System.out.println("PkpassDetail pkpass=null");
			return;
		}

		lenPrimary = pkpass.getPrimaryFieldsCount();
		lenSecondary = pkpass.getSecondaryFieldsCount();

		Drawable bg = pkpass.getBackground();
		if (bg == null) {
			bg = new ColorDrawable(pkpass.getBackgroundColor());
		}

		LayoutParams lParams = container.getLayoutParams();
		lParams.width = LayoutParams.FILL_PARENT;
		lParams.height = LayoutParams.FILL_PARENT;
		container.setLayoutParams(lParams);
		container.setBackgroundDrawable(bg);

		initHeader();
		initStrip();
		initThumnail();
		initContainerPrimary();
		initContainerSpecPrimarySecondary();
		initContainerAux();
		initContainerSecondary();
		initBarcode();
		initRelevanteDate();

		if (PkpassManager.getSharedInstance().exist(pkpass)) {
			btSaveDel.setBackgroundResource((PkpassManager.getSharedInstance()
					.exist(pkpass)) ? R.drawable.delete : R.drawable.ic_save);
		}

	}

	// =================================================

	protected void initBarcode() {
		int width, height;
		if (pkpass.hasBarcode()) {

			if (pkpass.isPdf417Code()) {
				width = getResources().getDimensionPixelSize(
						R.dimen.pkpass_detail_imgBarcode_Width);
				height = (int) ((float) width / 2.3f);
			} else {
				width = getResources().getDimensionPixelSize(
						R.dimen.pkpass_detail_imgBarcode_Height_Qr);
				height = width;

				imgBarcode.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!pkpass.isPdf417Code()) {
							getFragmentManager()
									.beginTransaction()
									.replace(PkpassDetailFragment.this.getId(),
											new PkpassBarcodeDetailFragment())
									.addToBackStack("barcode").commit();
						}
					}
				});

			}

			imgBarcode.setVisibility(View.VISIBLE);

			if (pkpass.getBarcodeImage() == null) {
				final WeakReference<ImageView> refImgBarcode = new WeakReference<ImageView>(
						imgBarcode);
				final int imgWidth = width;
				final int imgHeight = height;

				new AsyncTask<Void, Void, Drawable>() {

					protected void onPostExecute(Drawable result) {
						ImageView imageView = refImgBarcode.get();
						if (imageView != null) {
							imageView.setImageDrawable(result);
							imageView.requestLayout();
						}
					}

					@Override
					protected Drawable doInBackground(Void... params) {
						return pkpass
								.generateBarcode(imgWidth, imgHeight, true);
					};

				}.execute();
			}else{
				imgBarcode.setImageDrawable(pkpass.getBarcodeImage());
			}

			String barcodeMessage = pkpass.getBarcodeMessage();
			if (barcodeMessage == null || barcodeMessage.length() == 0
					|| !pkpass.isPdf417Code()) {
				txtBarcode.setVisibility(View.GONE);
			} else {
				txtBarcode.setVisibility(View.VISIBLE);
				txtBarcode.setText(barcodeMessage);
				txtBarcode.setTextColor(pkpass.getForegroundColor());
			}

		} else {
			imgBarcode.setVisibility(View.GONE);
			txtBarcode.setVisibility(View.GONE);
		}

		imgBarcode.requestLayout();
		txtBarcode.requestLayout();
	}

	protected void initRelevanteDate() {
		String relevantedate = pkpass.getRelevanDate();
		if (relevantedate != null && relevantedate.length() > 0) {
			relevantedate = DateUtil.convert(DateUtil.DATTIME_DOT,
					relevantedate);
			txtRelevantDate.setVisibility(View.GONE);
			txtRelevantDate.setText("Действительно до " + relevantedate);
			txtRelevantDate.setTextColor(pkpass.getForegroundColor());
		} else {
			txtRelevantDate.setVisibility(View.GONE);
		}
	}

	protected boolean isSpecPrimarySecondary() {
		return (lenPrimary == 1 && lenSecondary == 0)
				|| (lenPrimary == 1 && lenSecondary == 1);
	}

	protected void initContainerSpecPrimarySecondary() {
		int foregroundColor = pkpass.getForegroundColor();
		int labelColor = pkpass.getLabelColor();

		primary_secondary.removeAllViews();
		View keyValue;

		TextView tv;

		if (lenPrimary == 1 && lenSecondary == 0) {

			keyValue = getActivity().getLayoutInflater().inflate(
					R.layout.li_primary_big, null);
			tv = (TextView) keyValue.findViewById(R.id.pkvPrimaryTitle);
			tv.setText(pkpass.getPrimaryFieldsLabel(0));
			tv.setTextColor(foregroundColor);
			tv = (TextView) keyValue.findViewById(R.id.pkvPrimaryValue);
			tv.setText(pkpass.getPrimaryFieldsValue(0));
			tv.setTextColor(foregroundColor);
			primary_secondary.addView(keyValue);

		} else if (lenPrimary == 1 && lenSecondary == 1) {

			keyValue = getActivity().getLayoutInflater().inflate(
					R.layout.li_primary_secondary, null);
			tv = (TextView) keyValue.findViewById(R.id.pkvTitle);
			tv.setText(pkpass.getPrimaryFieldsLabel(0));
			tv.setTextColor(labelColor);

			tv = (TextView) keyValue.findViewById(R.id.pkvValue);
			tv.setText(pkpass.getPrimaryFieldsValue(0));
			tv.setTextColor(foregroundColor);
			primary_secondary.addView(keyValue);

			keyValue = getActivity().getLayoutInflater().inflate(
					R.layout.li_primary_secondary, null);
			tv = (TextView) keyValue.findViewById(R.id.pkvTitle);
			tv.setText(pkpass.getSecondaryFieldsLabel(0));
			tv.setTextColor(labelColor);

			tv = (TextView) keyValue.findViewById(R.id.pkvValue);
			tv.setText(pkpass.getSecondaryFieldsValue(0));
			tv.setTextColor(foregroundColor);
			primary_secondary.addView(keyValue);
			primary_secondary.requestLayout();
		}

	}

	protected void initHeader() {
		int foregroundColor = pkpass.getForegroundColor();
		int labelColor = pkpass.getLabelColor();

		logo.setBackgroundDrawable(pkpass.getLogo());

		logo.post(new Runnable() {

			@Override
			public void run() {
				Drawable drw = pkpass.getLogo();
				if (drw != null) {
					ViewUtils.scaleWidth(logo, (float) drw.getIntrinsicWidth()
							/ (float) drw.getIntrinsicHeight());
				}
			}
		});

		headerTitle.setText(pkpass.getLogoText());
		headerTitle.setTextColor(foregroundColor);

		if (pkpass.getHeaderFieldsCount() > 0) {
			hdrTitle.setText(pkpass.getHeaderFieldsLabel(0));
			hdrTitle.setTextColor(labelColor);
			hdrValue.setText(pkpass.getHeaderFieldsValue(0));
			hdrValue.setTextColor(foregroundColor);
		} else {
			hdrTitle.setText(null);
			hdrValue.setText(null);
		}
	}

	protected void initContainerPrimary() {
		if (isSpecPrimarySecondary()) {
			return;
		}

		fillContainerFlow(primary, Pkpass.FIELDS_PRIMARY,
				R.layout.li_key_value_big);
	}

	protected void initStrip() {
		Drawable drw = pkpass.getStrip();
		if (drw != null) {
			imgStrip.setImageDrawable(pkpass.getStrip());
			ViewUtils.scaleHeight(imgStrip, (float) drw.getIntrinsicWidth()
					/ (float) drw.getIntrinsicHeight());
			imgStrip.setVisibility(View.VISIBLE);
		} else {
			imgStrip.setVisibility(View.GONE);
		}
	}

	protected void initThumnail() {
		Drawable drw = pkpass.getThumbnail();
		if (drw != null) {
			imgThumbnail.setImageDrawable(drw);
			ViewUtils.scaleHeight(imgStrip, (float) drw.getIntrinsicWidth()
					/ (float) drw.getIntrinsicHeight());
		}
	}

	protected void initContainerSecondary() {
		if (isSpecPrimarySecondary()) {
			return;
		}

		fillContainerFlow(secondary, Pkpass.FIELDS_SECONDARY,
				R.layout.li_key_value_norm);
	}

	protected void initContainerAux() {
		fillContainerFlow(aux, Pkpass.FIELDS_AUX, R.layout.li_key_value_norm);
	}

	protected void fillContainerFlow(final RelativeLayout layout,
			String fieldsType, int resView) {
		int foregroundColor = pkpass.getForegroundColor();
		int labelColor = pkpass.getLabelColor();
		layout.removeAllViews();
		View keyValue;
		int len = pkpass.getFieldsCount(fieldsType);

		TextView tv;
		for (int i = 0; i < len; ++i) {
			keyValue = getActivity().getLayoutInflater().inflate(resView, null);
			tv = (TextView) keyValue.findViewById(R.id.pkvTitle);
			tv.setText(pkpass.getFieldsLabel(fieldsType, i));
			tv.setTextColor(labelColor);
			tv = (TextView) keyValue.findViewById(R.id.pkvValue);
			tv.setText(pkpass.getFieldsValue(fieldsType, i));
			tv.setTextColor(foregroundColor);
			layout.addView(keyValue);
		}

		layout.requestLayout();
		layout.post(new Runnable() {

			@Override
			public void run() {
				flowLayouter.flow(layout);
				layout.requestLayout();
			}
		});

	}

	@SuppressWarnings("deprecation")
	protected void setBackground(View img, Drawable drw) {
		// if (Build.VERSION.SDK_INT >= 16) {
		// img.setBackground(drw);
		// } else {
		img.setBackgroundDrawable(drw);
		// }
	}

}
