package com.dr.pkpassviewer.controllers.pkpasslist;

import java.util.Arrays;

import com.dr.pkpassviewer.entities.Pkpass;
import com.dr.pkpassviewer.managers.PkpassManager;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * 
 * @author Verma Rahul
 *
 */
public class PkpassListLoader extends AsyncTaskLoader<Pkpass[]> {
	protected Pkpass[] result;
	
	public PkpassListLoader(Context context) {
		super(context);
	}

	
	@Override
	protected void onStartLoading() {
		if(result!=null){
			deliverResult(result);
		}else{
			forceLoad();
		}
	}
	
	
	@Override
	protected void onReset() {
       result = null;
	}
	
	@Override
	public Pkpass[] loadInBackground() {
		PkpassManager pkpassManager = PkpassManager.getSharedInstance();
		pkpassManager.refresh();
		result =pkpassManager.toArray();
		Arrays.sort(result, Pkpass.getComparatorByOrganization());
		return result;
	}

}
