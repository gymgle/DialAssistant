package com.gymgle.loopcaller;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneListener extends PhoneStateListener {

	Caller mCaller;
	int times = -1;
	static int counter = 0;
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		switch(state) {
		case TelephonyManager.CALL_STATE_IDLE:
			if (counter >= times && times != -1) {
				return;
			}
			if (mCaller != null) {
				mCaller.call();
			}
			if (times != -1) {
				counter++;
			}
			break;
		default:
			break;
		}
	}
	
}
