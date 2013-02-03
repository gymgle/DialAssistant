package com.gymgle.loopcaller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Caller {

	private Context mContext;
	String phoneNumber;
	private Intent intent;
	
	public Caller(Context context){
		mContext = context;
	}

	void call() {
		if (intent == null) {
			intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		try {
			mContext.startActivity(intent);
		} catch (Exception e) {
			return;
		}
	}
}
