package com.dr.util.os.android;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

/**
 * 
 * @author Verma Rahul
 * 
 */
public class ViewUtils {

	/**
	 * Scale height thus width/height=ratio. I used it for ImageView when want
	 * scale drawable to width of screen, invoked in resume.
	 * 
	 * @param v
	 * @param ratio
	 */
	public static void scaleHeight(View v, float ratio) {
		LayoutParams params = v.getLayoutParams();
		params.height = (int) ((float) v.getWidth() / ratio);
		v.setLayoutParams(params);
		
	}

	public static void scaleWidth(View v, float ratio){
		LayoutParams params = v.getLayoutParams();
		params.width = (int) ((float) v.getHeight() * ratio);
		v.setLayoutParams(params);
	}
	
	/**
	 * 
	 * @param v
	 * @param pxWidth
	 * @param pxHeight
	 */
	public static void setSize(View v, int pxWidth, int pxHeight){
		LayoutParams params = v.getLayoutParams();
		params.height = pxWidth;
		params.width = pxHeight;
		v.setLayoutParams(params);
	}
	
	
	public static void setTextOrHide(TextView v, String s) {
		System.out.println("ViewUtils setTextOrHide s="+s);
		v.setVisibility((s == null || s.length() == 0) ? View.GONE
				: View.VISIBLE);
		v.setText(s);
	}

	
	
}
