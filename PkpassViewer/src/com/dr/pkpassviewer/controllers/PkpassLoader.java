package com.dr.pkpassviewer.controllers;

import java.io.File;
import android.app.Activity;
import android.content.AsyncTaskLoader;
import com.dr.pkpassviewer.entities.Pkpass;
import com.dr.pkpassviewer.managers.PkpassManager;
import android.net.Uri;

/**
 * 
 * @author Verma Rahul
 * 
 */
public class PkpassLoader extends AsyncTaskLoader<Pkpass> {
	protected Pkpass result;
	protected String path;
	protected Uri uri;
	
	public PkpassLoader(Activity context, String path, Uri uri) {
		super(context);
		this.path=path;
		this.uri=uri;
	}

	@Override
	protected void onStartLoading() {
		if (result != null) {
			deliverResult(result);
		} else {
			forceLoad();
		}
	}

	@Override
	protected void onReset() {
		result = null;
	}

	@Override
	public Pkpass loadInBackground() {
		
		if (path == null) {
			return null;
		}


		File file = new File(path);
		if(file.exists()){
			result = PkpassManager.getSharedInstance().getPkpass(file);
			if (result == null) {
				result = new Pkpass();
			}

			result.loadFromFile(file, false);
		}else{
			result = new Pkpass();
			try {
				result.loadFromStream( getContext().getContentResolver().openInputStream(uri), false);
			}catch(Exception e){
				e.printStackTrace();
			}
		}




		return result;
	}


}