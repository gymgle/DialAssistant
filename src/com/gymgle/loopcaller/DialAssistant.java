package com.gymgle.loopcaller;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class DialAssistant extends Activity {

	private Button mBtnOK;
	private Button mBtnCancel;
	private EditText mTxtPhoneNumber;
	private EditText mTxtLoopTimes;
	private CheckBox mCbDeadLoop;
	private Caller mCaller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBtnOK = (Button) findViewById(R.id.btnOk);
		mBtnCancel = (Button) findViewById(R.id.btnCancel);
		mTxtPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
		mTxtLoopTimes = (EditText) findViewById(R.id.etLoopTimes);
		mCbDeadLoop = (CheckBox) findViewById(R.id.cbDeadLoop);
		
		EventListener listener = new EventListener();
		mCbDeadLoop.setOnCheckedChangeListener(listener);
		mBtnOK.setOnClickListener(listener);
		mBtnCancel.setOnClickListener(listener);
		
		readConfig();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_about:
				String strAbout = getString(R.string.app_name).toString() + getString(R.string.version_name).toString() + getString(R.string.item_about).toString();
				new AlertDialog.Builder(DialAssistant.this).setMessage(strAbout).create().show();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	class EventListener implements OnCheckedChangeListener, OnClickListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (buttonView.getId() == R.id.cbDeadLoop) {
				mTxtLoopTimes.setEnabled(!isChecked);
			}
		}
		
		@Override
		public void onClick(View arg0) {
			if (arg0.getId() == R.id.btnOk)	{
				String phoneNumber = mTxtPhoneNumber.getText().toString();
				if ("".equals(phoneNumber)) {
					return;
				}
				String times = mTxtLoopTimes.getText().toString();
				if ("".equals(times) && !mCbDeadLoop.isChecked()) {
					return;
				}
				if (mCbDeadLoop.isChecked()) {
					times = "-1";
				}
				if (mCaller == null) {
					mCaller = new Caller(DialAssistant.this);
					mCaller.phoneNumber = phoneNumber;
				}
				
				writeConfig();
				
				mCaller.call();
				
				PhoneListener.counter = 0;
				//Start service to listen phone state
				Intent intent = new Intent("StopLoopCallService");
				intent.putExtra("phonenumber", phoneNumber);
				intent.putExtra("times", times);
				
				//Stop service
				stopService(intent);
				//Start service
				startService(intent);
			}
			
			if (arg0.getId() == R.id.btnCancel) {
				Intent intent = new Intent("StopLoopCallService");
				stopService(intent);
				Toast.makeText(getApplicationContext(), "“—Õ£÷π÷ÿ≤¶", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void readConfig() {
		ConfigHelper helper = new ConfigHelper();
		mTxtPhoneNumber.setText(helper.readPhoneNum());
		mTxtLoopTimes.setText(helper.readLoopTimes());
	}
	
	private void writeConfig() {
		String phone = mTxtPhoneNumber.getText().toString();
		String times = mTxtLoopTimes.getText().toString();
		ConfigHelper helper = new ConfigHelper();
		helper.writePhoneNum(phone);
		helper.writeLoopTimes(times);
	}
	
	class ConfigHelper {
		
		SharedPreferences info;
		
		public ConfigHelper() {
			info = DialAssistant.this.getSharedPreferences("info", 0);
		}
		
		String readPhoneNum() {
			return info.getString("PHONENUMBER", "");
		}
		
		String readLoopTimes() {
			return info.getString("LOOPTIMES", "");
		}
		
		void writePhoneNum(String phone) {
			Editor edit = info.edit();
			edit.putString("PHONENUMBER", phone);
			edit.commit();
		}
		
		void writeLoopTimes(String times) {
			Editor edit = info.edit();
			edit.putString("LOOPTIMES", times);
			edit.commit();
		}
	}
}
