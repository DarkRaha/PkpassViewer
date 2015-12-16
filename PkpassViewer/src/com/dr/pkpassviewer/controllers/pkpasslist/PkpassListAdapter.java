package com.dr.pkpassviewer.controllers.pkpasslist;

import com.dr.pkpassviewer.entities.Pkpass;

import dr.android.pkpassviewer.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Verma Rahul
 * 
 */
public class PkpassListAdapter extends BaseAdapter {

	protected Pkpass[] items;
	protected int resLayout = R.layout.pkpass_li;
	protected int idImage, idOrganization, idDescription, idDate,
			idLocationText;

	public PkpassListAdapter() {
		initIdResources();
	}

	protected void initIdResources() {
		idImage = R.id.li_pkpass_imgIcon;
		idOrganization = R.id.li_pkpass_organization;
		idDescription = R.id.li_pkpass_description;
		idDate = R.id.li_pkpass_date;
		idLocationText = R.id.li_pkpass_txtLocation;
	}

	public final void setData(Pkpass[] items) {
		this.items = items;
		notifyDataSetChanged();
	}

	@Override
	public final int getCount() {
		return items != null ? items.length : 0;
	}

	@Override
	public final Pkpass getItem(int position) {
		return items != null ? items[position] : null;
	}

	@Override
	public final long getItemId(int position) {
		return items != null ? items[position].getSourceFile().hashCode() : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		Holder holder;
		Pkpass pkpass = getItem(position);

		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			v = inflater.inflate(resLayout, parent, false);
			holder = new Holder();
			v.setTag(holder);
			initHolder(v, holder);

		} else {
			holder = (Holder) v.getTag();
		}

		assignData(v, holder, pkpass, position);

		return v;
	}

	protected void initHolder(View v, Holder holder) {
		holder.img = (ImageView) v.findViewById(idImage);
		holder.organizationName = (TextView) v.findViewById(idOrganization);
		holder.date = (TextView) v.findViewById(idDate);
		holder.description = (TextView) v.findViewById(idDescription);
		holder.location = (TextView) v.findViewById(idLocationText);
	}

	protected void assignData(View v, Holder holder, Pkpass pkpass, int position) {
		int fgColor = pkpass.getForegroundColor();

		if (holder.img != null) {
			holder.img.setImageDrawable(pkpass.getIcon());
		}

		if (holder.organizationName != null) {
			holder.organizationName.setText(pkpass.getOrganizationName());
			holder.organizationName.setTextColor(fgColor);
		}

		if (holder.date != null) {
			holder.date.setText(pkpass.getRelevanDate());
			holder.date.setTextColor(fgColor);
		}

		if (holder.description != null) {
			holder.description.setText(pkpass.getDescription());
			holder.description.setTextColor(fgColor);
		}

		if(holder.location!=null){
			holder.location.setText(pkpass.getLocationText(0));
			holder.location.setTextColor(fgColor);
		}
		
		v.setBackgroundColor(pkpass.getBackgroundColor());
	}

	protected static class Holder {
		public ImageView img;
		public TextView organizationName;
		public TextView date;
		public TextView description;
		public TextView location;
	}

}
