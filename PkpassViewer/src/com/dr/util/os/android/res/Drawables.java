package com.dr.util.os.android.res;

import java.util.zip.ZipInputStream;

import com.dr.util.libs.BarcodeEncoder;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * 
 * @author Verma Rahul
 * 
 */
public class Drawables {

	public final static int PKPASS_IMG_COUNT = 6;
	public final static String PKPASS_IMG_LOGO = "logo";
	public final static String PKPASS_IMG_ICON = "icon";
	public final static String PKPASS_IMG_BG = "background";
	public final static String PKPASS_IMG_FOOTER = "footer";
	public final static String PKPASS_IMG_THUMB = "thumbnail";
	public final static String PKPASS_IMG_STRIP = "strip";

	public final static int PKPASS_IMG_LOGO_IND = 0;
	public final static int PKPASS_IMG_ICON_IND = 1;
	public final static int PKPASS_IMG_BG_IND = 2;
	public final static int PKPASS_IMG_FOOTER_IND = 3;
	public final static int PKPASS_IMG_THUMB_IND = 4;
	public final static int PKPASS_IMG_STRIP_IND = 5;
	public final static int PKPASS_IMG_BARCODE_IND = 6;

	protected final static String prefix[] = new String[] { PKPASS_IMG_LOGO,
			PKPASS_IMG_ICON, PKPASS_IMG_BG, PKPASS_IMG_FOOTER,
			PKPASS_IMG_THUMB, PKPASS_IMG_STRIP };

	/**
	 * Genetate barcode through zxing library
	 * 
	 * @param msg
	 *            message from pkpass
	 * @param format
	 *            barcode format string in apple notation
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static Drawable generateBarcode(String msg, String format,
			int width, int height) {

		try {
			@SuppressWarnings("deprecation")
			BitmapDrawable drw = new BitmapDrawable(
					new BarcodeEncoder().getBitmap(msg, format, width, height));		
			return drw;
		} catch (Exception e) {			
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Decode zipentry, and save BitmapDrawable in images. If array already has
	 * image, then it will be replaced.
	 * 
	 * @param zis
	 * @param zipEntryName
	 *            if value not start from one of PKPASS_IMG_xxx, then loading
	 *            will be skipped. Pkpass can have to files for one image like
	 *            logo.png and logo@2x.png
	 * @param images
	 *            array with length PKPASS_IMG_COUNT
	 * @param skipIsAlready
	 * @return true if image loaded or already loaded, otherwise false.
	 */
	public static boolean loadPkpassDrawable(ZipInputStream zis,
			String zipEntryName, Drawable[] images, boolean skipIsAlready) {

		int ind = -1;
		for (int i = 0; i < prefix.length; ++i) {
			if (zipEntryName.startsWith(prefix[i])) {
				ind = i;
				break;
			}
		}

		if (ind >= 0) {
			if (images[ind] != null) {
				if (skipIsAlready) {
					return true;
				}

				Bitmap bm = ((BitmapDrawable) images[ind]).getBitmap();
				bm.recycle();
			}
			images[ind] = BitmapDrawable.createFromStream(zis, zipEntryName);
			return true;
		}

		return false;
	}

}
