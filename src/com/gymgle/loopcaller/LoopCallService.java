package com.gymgle.loopcaller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LoopCallService extends Service {

	private PhoneListener mPhoneListener;
	TelephonyManager phoneMgr;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		String phonenumber = "";
		String times = "";
		Object object  = intent.getExtras().get("phonenumber");
		if (object instanceof String) {
			phonenumber = (String) object;
		}
		object = intent.getExtras().get("times");
		if (object instanceof String) {
			times = (String) object;
			Init(phonenumber, times);
		}
		
		return START_NOT_STICKY;
	}
	
	private void Init(String phonenumber, String times) {
		mPhoneListener = new PhoneListener();
		mPhoneListener.mCaller = new Caller(this);
		mPhoneListener.mCaller.phoneNumber = phonenumber;
		mPhoneListener.times = Integer.valueOf(times);
		
		phoneMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneMgr.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
		
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (phoneMgr != null) {
			PhoneListener.counter = 0;
			phoneMgr.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
		}
	}

}