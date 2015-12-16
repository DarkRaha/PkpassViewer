package com.dr.util.os.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 
 * 
 * @author Verma Rahul
 *
 */
public class CommonIntent {

	/**
	 * 
	 * @param c
	 * @param latitude
	 * @param longitude
	 */
	public static void showMap(Context c, float latitude, float longitude) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("geo:" + latitude + "," + longitude));
		c.startActivity(intent);
	}

	/**
	 * geo:0,0?q=lat,lng(label)
	 * 
	 * @param c
	 * @param latitude
	 * @param longitude
	 * @param title
	 */
	public static void searchOnMap(Context c, float latitude, float longitude,
			String title) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		StringBuilder sb = new StringBuilder();
		try{
		sb.append("geo:0,0?q=").append(latitude).append(longitude).append("(")
				.append(URLEncoder.encode(title, "utf-8") ).append(")");
		}catch(UnsupportedEncodingException e){
			;
		}
	
		intent.setData(Uri.parse(sb.toString()));
		c.startActivity(intent);
	}

}
