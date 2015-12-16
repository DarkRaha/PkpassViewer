package com.dr.pkpassviewer.entities;


import java.io.*;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dr.util.java.Color;
import com.dr.util.java.FlagInt;
import com.dr.util.java.IOUtil;
import com.dr.util.libs.JsonUtil;
import com.dr.util.os.android.res.Drawables;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.drawable.Drawable;

/**
 * Info from
 * https://developer.apple.com/library/ios/documentation/UserExperience
 * /Reference/PassKit_Bundle/Chapters/PackageStructure.html. <br/>
 * 
 * For make test files: https://www.passsource.com/selectPublicTemplate.php
 * 
 * 
 * 
 * @author Verma Rahul
 * 
 */

public class Pkpass implements Cloneable {
	// TODO move drawables to some image chache or make softref ?

	public static final String MIMETYPE="application/vnd.apple.pkpass";

	public final static String TYPE_BOARDING_PASS = "boardingPass",
			TYPE_COUPON = "coupon", TYPE_EVENT_TICKET = "eventTicket",
			TYPE_GENERIC = "generic", TYPE_STORE_CARD = "storeCard";

	public final static String PKTransitTypeAir = "PKTransitTypeAir",
			PKTransitTypeBoat = "PKTransitTypeBoat",
			PKTransitTypeBus = "PKTransitTypeBus",
			PKTransitTypeGeneric = "PKTransitTypeGeneric",
			PKTransitTypeTrain = "PKTransitTypeTrain";

	public final static String FIELDS_HEADER = "headerFields",
			FIELDS_PRIMARY = "primaryFields",
			FIELDS_SECONDARY = "secondaryFields",
			FIELDS_AUX = "auxiliaryFields", FIELDS_BACK = "backFields";

	/**
	 * The image displayed on the front of the pass in the top left.
	 */
	protected static final int IND_LOGO = 0;

	/**
	 * The pass’s icon. This is displayed in notifications and in emails that
	 * have a pass attached, and on the lock screen.
	 * 
	 * When it is displayed, the icon gets a shine effect and rounded corners.
	 */
	protected static final int IND_ICON = 1;

	/**
	 * The image displayed as the background of the front of the pass.
	 */
	protected static final int IND_BACKGROUND = 2;
	/**
	 * The image displayed on the front of the pass near the barcode.
	 */
	protected static final int IND_FOOTER = 3;
	/**
	 * An additional image displayed on the front of the pass. For example, on a
	 * membership card, the thumbnail could be used to a picture of the
	 * cardholder.
	 */
	protected static final int IND_THUMBNAIL = 4;
	/**
	 * The image displayed behind the primary fields on the front of the pass.
	 */
	protected static final int IND_STRIP = 5;

	protected String pkpassType;

	protected File sourceFile;

	/**
	 * A JSON dictionary that defines the pass. Its contents are described in
	 * detail in Top-Level Keys.
	 */
	protected JSONObject info;

	protected Drawable images[];
	protected Drawable barcode;

	protected int labelColor = 0;
	protected int foregroundColor = 0;
	protected int backgroundColor = 0xffffff;

	protected JSONObject location;
	protected int indLocation;

	protected void clear() {
		images = null;
		pkpassType = null;
		location = null;
		info = null;
		sourceFile = null;
		labelColor = 0;
		foregroundColor = 0;
		backgroundColor = 0xffffff;
	}

	/**
	 * Free references to all drawables (without recycle).
	 */
	public void freeDrawablesAll() {
		if (images == null) {
			return;
		}
		images[Drawables.PKPASS_IMG_BG_IND] = null;
		images[Drawables.PKPASS_IMG_FOOTER_IND] = null;
		images[Drawables.PKPASS_IMG_ICON_IND] = null;
		images[Drawables.PKPASS_IMG_LOGO_IND] = null;
		images[Drawables.PKPASS_IMG_STRIP_IND] = null;
		images[Drawables.PKPASS_IMG_THUMB_IND] = null;
		barcode = null;
		System.gc();
	}

	/**
	 * If bit Drawables.PKPASS_IMG_xx_IND true, than reference to corresponded
	 * drawable will be set to null.
	 * 
	 * @param flags
	 */
	public void freeSelectedDrawables(int flags) {
		if (images == null) {
			return;
		}

		int cnt = images.length;
		for (int i = 0; i < cnt; ++i) {
			if (FlagInt.getBit(flags, i)) {
				images[i] = null;
			}
		}

		if (FlagInt.getBit(flags, Drawables.PKPASS_IMG_BARCODE_IND)) {
			barcode = null;
		}
		System.gc();
	}

	/**
	 * Load data from pkpass file if necessary.
	 * 
	 * @param filePkpass
	 */
	public boolean loadFromFile(File filePkpass, boolean listItem) {
		boolean isReload = true;
		if (!filePkpass.equals(sourceFile)) {
			clear();
			isReload = false;
		}

		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(filePkpass)));

			loadFrom(zis, listItem, isReload);
			initFromJson();
			zis.close();
			sourceFile = filePkpass;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void loadFromStream(InputStream is,  boolean listItem){
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(is);
			loadFrom(zis, listItem, false);
			initFromJson();
			zis.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		sourceFile=null;
	}


	public final File getSourceFile() {
		return sourceFile;
	}

	/**
	 * 
	 * @param file
	 *            non null value, file must exist. Otherwise
	 *            IllegalArgumentException will be raised.
	 */
	public final void setSourceFile(File file) {
		if (file == null || !file.exists() || file.isDirectory()
				|| !file.getName().endsWith(".pkpass")) {
			throw new IllegalArgumentException(
					"Argument must point to real pkpass file, but got "
							+ file.getAbsolutePath());
		}
		this.sourceFile = file;
	}

	protected void loadFrom(ZipInputStream zis, boolean listItem,
			boolean isReload) throws IOException, JSONException {
		ZipEntry ze = null;
		String zipEntryName = null;
		if (images == null) {
			images = new Drawable[Drawables.PKPASS_IMG_COUNT];
		}

		while ((ze = zis.getNextEntry()) != null) {

			zipEntryName = ze.getName();
			if (zipEntryName.endsWith(".png")) {
				if (!listItem
						|| zipEntryName.startsWith(Drawables.PKPASS_IMG_ICON)) {
					Drawables.loadPkpassDrawable(zis, zipEntryName, images,
							isReload);
				}
			} else if (zipEntryName.equals("pass.json") && info == null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				IOUtil.copy(zis, bos);
				String text = bos.toString();
				bos.close();
				info = new JSONObject(text);
			}
		}
	}

	protected void initFromJson() {
		if (info == null) {
			System.out.println("Pkpass initFromJson info null");
			return;
		}

		foregroundColor = Color.parseColorFrom(JsonUtil.getString(info,
				"foregroundColor", "000000"));

		backgroundColor = Color.parseColorFrom(JsonUtil.getString(info,
				"backgroundColor", "ff0000"));

		labelColor = Color.parseColorFrom(JsonUtil.getString(info,
				"labelColor", "000"/* "rgb(0,0,0)" */));

		if (info.has(TYPE_EVENT_TICKET)) {
			pkpassType = TYPE_EVENT_TICKET;
		} else if (info.has(TYPE_STORE_CARD)) {
			pkpassType = TYPE_STORE_CARD;
		} else if (info.has(TYPE_COUPON)) {
			pkpassType = TYPE_COUPON;
		} else if (info.has(TYPE_GENERIC)) {
			pkpassType = TYPE_GENERIC;
		} else if (info.has(TYPE_BOARDING_PASS)) {
			pkpassType = TYPE_BOARDING_PASS;
		}

	}

	// ==============================================================
	public final String getOrganizationName() {
		return JsonUtil.getString(info, "organizationName");
	}

	public final String getDescription() {
		return JsonUtil.getString(info, "description");

	}

	public final String getRelevanDate() {
		return JsonUtil.getString(info, "relevantDate");
	}

	public final int getLocationsCount() {
		try {
			return info.getJSONArray("locations").length();
		} catch (Exception e) {

		}

		return 0;
	}

	public final float getLatitude(int ind) {
		getLocation(ind);
		return JsonUtil.getFloat(location, "latitude", 0.f);
	}

	public final float getLongitude(int ind) {
		getLocation(ind);
		return JsonUtil.getFloat(location, "longitude", 0.f);
	}

	public final String getLocationText(int ind) {
		getLocation(ind);
		return JsonUtil.getString(location, "relevantText", null);
	}

	public final String getLogoText() {
		return JsonUtil.getString(info, "logoText");
	}

	public final int getBackgroundColor() {
		return backgroundColor;
	}

	public final int getForegroundColor() {
		return foregroundColor;
	}

	public final int getLabelColor() {
		return labelColor;
	}

	// =========================================================
	public final int getPrimaryFieldsCount() {
		return getFieldsCount("primaryFields");
	}

	public final String getPrimaryFieldsLabel(int ind) {
		return getFieldsLabel("primaryFields", ind);
	}

	public final String getPrimaryFieldsValue(int ind) {
		return getFieldsValue("primaryFields", ind);
	}

	public final int getHeaderFieldsCount() {
		return getFieldsCount("headerFields");
	}

	public final String getHeaderFieldsLabel(int ind) {
		return getFieldsLabel("headerFields", ind);
	}

	public final String getHeaderFieldsValue(int ind) {
		return getFieldsValue("headerFields", ind);
	}

	public final int getSecondaryFieldsCount() {
		return getFieldsCount("secondaryFields");
	}

	public final String getSecondaryFieldsLabel(int ind) {
		return getFieldsLabel("secondaryFields", ind);
	}

	public final String getSecondaryFieldsValue(int ind) {
		return getFieldsValue("secondaryFields", ind);
	}

	public final int getAuxFieldsCount() {
		return getFieldsCount("auxiliaryFields");
	}

	public final String getAuxFieldsLabel(int ind) {
		return getFieldsLabel("auxiliaryFields", ind);
	}

	public final String getAuxFieldsValue(int ind) {
		return getFieldsValue("auxiliaryFields", ind);
	}

	/**
	 * 
	 * @return one of PKTransitType_xxx contants, or null if pkpass type not
	 *         "boardingPass".
	 */
	public final String getTransitType() {
		try {
			return info.getJSONObject(pkpassType).getString("transitType");

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public final String getPkpassType() {
		return pkpassType;
	}

	// =========================================================
	/**
	 * 
	 * @param fieldType
	 *            one of FIELDS_xxx constant
	 * @return
	 */
	public int getFieldsCount(String fieldType) {
		try {
			return info.getJSONObject(pkpassType).getJSONArray(fieldType)
					.length();

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 
	 * @param fieldType
	 *            one of FIELDS_xxx constant
	 * @param ind
	 * @return
	 */
	public String getFieldsLabel(String fieldType, int ind) {
		try {
			return info.getJSONObject(pkpassType).getJSONArray(fieldType)
					.getJSONObject(ind).getString("label");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param fieldType
	 *            one of FIELDS_xxx constant
	 * @param ind
	 * @return
	 */
	public String getFieldsValue(String fieldType, int ind) {
		try {
			return info.getJSONObject(pkpassType).getJSONArray(fieldType)
					.getJSONObject(ind).getString("value");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// =========================================================
	protected void getLocation(int ind) {
		if (location == null || ind != indLocation) {
			try {
				location = info.getJSONArray("locations").getJSONObject(ind);
				indLocation = ind;
			} catch (Exception e) {

			}
		}
	}

	/**
	 * Label will be bold. value normal.
	 * 
	 * @return
	 */
	public String buildBackFieldsHtmlText() {
		StringBuilder sb = new StringBuilder();
		JSONArray array;

		try {
			array = info.getJSONObject(pkpassType).getJSONArray("backFields");
			int len = array.length();
			JSONObject o;
			for (int i = 0; i < len; ++i) {
				o = array.getJSONObject(i);
				sb.append("<b>").append(o.getString("label")).append("</b>")
						.append(": ");
				sb.append(o.getString("value"));
				sb.append("<br /><br />");
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		return sb.toString();
	}

	// ==============================================================
	public Drawable getLogo() {
		return images[IND_LOGO];
	}

	public Drawable getIcon() {
		return images[IND_ICON];
	}

	public Drawable getBackground() {
		return images[IND_BACKGROUND];
	}

	public Drawable getThumbnail() {
		return images[IND_THUMBNAIL];
	}

	public Drawable getStrip() {
		return images[IND_STRIP];
	}

	public Drawable getFooter() {
		return images[IND_FOOTER];
	}

	/**
	 * 
	 * @param width
	 * @param height
	 * @param keep
	 *            true if you want save result inside pkpass.
	 * @return
	 */
	public Drawable generateBarcode(int width, int height, boolean keep) {
		try {
			barcode = null;
			JSONObject barcodeJson = info.getJSONObject("barcode");
			if (keep) {
				barcode = Drawables.generateBarcode(
						barcodeJson.getString("message"),
						barcodeJson.getString("format"), width, height);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return barcode;
	}

	public final boolean isQrCode() {
		return isBarcode("PKBarcodeFormatQR");
	}

	public final boolean isAztectCode() {
		return isBarcode("PKBarcodeFormatAztec");
	}

	public final boolean isPdf417Code() {
		return isBarcode("PKBarcodeFormatPDF417");
	}

	protected boolean isBarcode(String type) {
		try {
			JSONObject barcodeJson = info.getJSONObject("barcode");
			return type.equals(barcodeJson.getString("format"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public final boolean hasBarcode() {
		return info == null ? false : info.has("barcode");
	}

	/**
	 * 
	 * @return
	 */
	public Drawable getBarcodeImage() {
		return barcode;
	}

	public String getBarcodeMessage() {
		if (info != null) {
			try {
				return info.getJSONObject("barcode").getString("message");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	// ==============================================================

	public static Comparator<Pkpass> getComparatorByOrganization() {
		return new Comparator<Pkpass>() {

			@Override
			public int compare(Pkpass lhs, Pkpass rhs) {
				if (lhs != null) {
					return lhs.getOrganizationName().compareTo(
							rhs == null ? null : rhs.getOrganizationName());
				}

				return rhs == null ? 0 : -1;
			}
		};
	}

	public static Comparator<Pkpass> getComparatorByLastModified(){
		return new Comparator<Pkpass>() {
			@Override
			public int compare(Pkpass lhs, Pkpass rhs) {
				if(lhs!=null){
					long lTime  = lhs.getSourceFile().lastModified();
					long rTime = (rhs==null)?0:rhs.getSourceFile().lastModified();
					if(lTime>rTime){
						return 1;
					}else if(lTime<rTime){
						return -1;
					}
					return 0;
				}


				return rhs==null?0:1;
			}
		};

	}


	@Override
	public Pkpass clone() throws CloneNotSupportedException {
		return (Pkpass) super.clone();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(sourceFile).append(",")
				.append(getOrganizationName()).append("}");
		return sb.toString();
	}

}
