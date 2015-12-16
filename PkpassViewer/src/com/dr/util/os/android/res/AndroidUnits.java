package com.dr.util.os.android.res;

import android.content.Context;

/**
 * 
 * @author Verma Rahul
 *
 */
public class AndroidUnits {

	public static float dpFromPx(final Context context, final float px) {   
		return px / context.getResources().getDisplayMetrics().density;
	}

	public static float pxFromDp(final Context context, final float dp) {
	    return dp * context.getResources().getDisplayMetrics().density;
	}
	
}
