package com.dr.pkpassviewer.controllers;

import com.dr.pkpassviewer.controllers.pkpasslist.PkpassListFragment;
import com.dr.pkpassviewer.controllers.pkpasslist.PkpassListFragment.OnPkpassView;
import com.dr.pkpassviewer.entities.Pkpass;

import dr.android.pkpassviewer.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Main activity, show PkpassListFragment as root fragment.
 * 
 * @author Verma Rahul
 * 
 */
public class PkpassActivity extends Activity implements OnPkpassView {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getFragmentManager().beginTransaction()
				.replace(R.id.container, new PkpassListFragment()).commit();
		if (startPkpassViewActivity(getIntent(), null)) {
			getIntent().setData(null);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (!startPkpassViewActivity(intent, null)) {
			super.onNewIntent(intent);
		}
	}

	@Override
	public void onPkpassView(Pkpass pkpass) {				
		startPkpassViewActivity(null, Uri.fromFile(pkpass.getSourceFile()));
	}

	protected boolean startPkpassViewActivity(Intent argIntent, Uri uri) {
		if (argIntent != null) {
			String dataString = argIntent.getDataString();
			if (dataString != null && dataString.endsWith(".pkpass")) {
				uri = argIntent.getData();
			}
		}

		if (uri != null) {
			Intent intent = new Intent(this, PkpassViewActivity.class);
			intent.setData(uri);
			startActivity(intent);
			return true;
		}

		return false;
	}

}
