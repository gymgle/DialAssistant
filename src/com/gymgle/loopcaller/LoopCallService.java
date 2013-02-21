package com.gymgle.loopcaller;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class LoopCallService extends Service {

	private PhoneListener mPhoneListener;
	private TelephonyManager phoneMgr;
	private WindowManager mWinMgr;
	private WindowManager.LayoutParams mParams;
	private View view;
	private int statusBarHeight;	//Height of statusbar
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		view = LayoutInflater.from(this).inflate(R.layout.floating, null);

		mWinMgr = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams();
		mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.format = PixelFormat.TRANSPARENT;
		mParams.gravity = Gravity.LEFT | Gravity.TOP;
		mWinMgr.addView(view, mParams);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("StopLoopCallService");
				stopService(intent);
				Toast.makeText(getApplicationContext(), "“—Õ£÷π÷ÿ≤¶", Toast.LENGTH_SHORT).show();
			}
		});
		
		view.setOnTouchListener(new OnTouchListener() {
			float[] temp = new float[] {0f, 0f};
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					temp[0] = event.getX();
					temp[1] = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					refreshView((int) (event.getRawX() - temp[0]), (int) (event.getRawY() - temp[1]));
					break;
				default:
					break;
				}
				return false;
			}
		});
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
			mWinMgr.removeView(view);
		}
	}
	
	/**
	 * Refresh the floating view
	 * @param x x-axis after moved
	 * @param y y-axis after moved
	 */
	public void refreshView(int x, int y) {
		//Get height of StatusBar
		if (statusBarHeight == 0) {
			View rootView = view.getRootView();
			Rect r = new Rect();
			rootView.getWindowVisibleDisplayFrame(r);
			statusBarHeight = r.top;
		}
		mParams.x = x;
		mParams.y = y - statusBarHeight;
		mWinMgr.updateViewLayout(view, mParams);
	}
}