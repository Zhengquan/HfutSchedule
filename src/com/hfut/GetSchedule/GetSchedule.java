package com.hfut.GetSchedule;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.util.GetSchedule.CookiesManager;
import com.util.GetSchedule.SchedulePreferenceManager;

public class GetSchedule extends Activity {
    /** Called when the activity is first created. */
	private Button mButton1;
	private Button mButton2;
	private AutoCompleteTextView mAutoComplete1;
	private EditText mEditText2;
	private ProgressBar progressBar1;
	private Handler handler;
	private TextView textView4;
	private CheckBox mchBox1;
	private CheckBox mchBox2;
	private boolean autologin;
	private static final int  LoginSuccess = 0x127;
	private static final int  LoginFailed = 0x128;
	private static final int LOGING_THREAD = 0x129;
	public static final int NEED_EXIT = 0x130;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mButton1 = (Button)findViewById(R.id.button1);
        mButton2 = (Button)findViewById(R.id.button2);
        mAutoComplete1 = (AutoCompleteTextView)findViewById(R.id.autocomplete1);
        mEditText2 = (EditText)findViewById(R.id.edittext2);
        progressBar1 = (ProgressBar)findViewById(R.id.progressbar1);
        textView4 = (TextView)findViewById(R.id.textview4);
        mchBox1 = (CheckBox)findViewById(R.id.check1);
        mchBox2 = (CheckBox)findViewById(R.id.check2);
        textView4.setVisibility(TextView.INVISIBLE);
        //测试用
        createHandler();
        //根据Preference中的数据填充表单
        fillControlByPreference();
        //设置CheckBox的响应事件
        initCheckBox();
        initialAutoComplete();
        initialClickedListener();
        if(SchedulePreferenceManager.returnAutolog(this)){
        	forwardToNextActivity();
        }
	}
    
	private void fillControlByPreference() {
		// TODO Auto-generated method stub
		mchBox1.setChecked(true);
        mAutoComplete1.setText(SchedulePreferenceManager.returnUserByPreference(this));
        mEditText2.setText(SchedulePreferenceManager.returnPass(this));
	}

	private void initCheckBox() {
		// TODO Auto-generated method stub
		mchBox2.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//自动登录需要记下用户名密码等
				if(mchBox2.isChecked())
					mchBox1.setChecked(true);
			}
		});
	}

	private void initialAutoComplete() {
		// TODO Auto-generated method stub
    	//初始化AutoComplete
		String[]users = SchedulePreferenceManager.returnUserArray(this);
		ArrayAdapter<String>arrayAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_dropdown_item_1line
				, users);
		mAutoComplete1.setAdapter(arrayAdapter);
	}

	private void createHandler() {
		// TODO Auto-generated method stub
    	handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
				case LoginSuccess:
					forwardToNextActivity();
					break;
				case LoginFailed:
    				progressBar1.setVisibility(ProgressBar.INVISIBLE);
    				mButton1.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_back));
					textView4.setText("登陆失败");
					break;
				default:
					progressBar1.setVisibility(ProgressBar.VISIBLE);
					textView4.setText("登陆中...");
					break;
				}
			}
    		
    	};
	}
	private void forwardToNextActivity(){
		Intent intent  = new Intent();
		intent.setClass(GetSchedule.this, ScheduleView.class);
		Bundle bundle = new Bundle();
		String user = mAutoComplete1.getText().toString();
		String password = mEditText2.getText().toString();
		autologin = mchBox2.isChecked();
		bundle.putString("user",user);
		bundle.putString("password",password);
		if(mchBox1.isChecked()){
			//Remember Me:把用户名，密码等写入Preference 文件
			SchedulePreferenceManager.createSchedulePreference(GetSchedule.this, user, password, autologin);
		}
		intent.putExtras(bundle);
		mButton1.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_back));
		progressBar1.setVisibility(ProgressBar.INVISIBLE);
		textView4.setText("Login Success!");
		//跳转到主界面,知道返回NEED_EXIT结果
		startActivityForResult(intent, NEED_EXIT);
		finish();
	}
	private void initialClickedListener() {
		mButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				textView4.setVisibility(TextView.VISIBLE);
				mButton1.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_focus));
				LoginThread loginThread = new LoginThread();
				loginThread.start();
			}
		});
		mButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				mAutoComplete1.setText("");
				mEditText2.setText("");
			}
		});
	}
	
	public class LoginThread extends Thread{
		public LoginThread() {}
		@Override
		public void run() {
			CookiesManager cookiesManager = new CookiesManager(
					 "http://210.45.240.29/student/html/s_index.htm"
					,"http://210.45.240.29/pass.asp"
					, mAutoComplete1.getText().toString()
					, mEditText2.getText().toString());
			boolean log_ok = false;
			try {
				log_ok = cookiesManager.GenerateCookies();
				Message msg_login = new Message();
				msg_login.what = LOGING_THREAD;
				GetSchedule.this.handler.sendMessage(msg_login);
				if(log_ok){
					Message msg = new Message();
					msg.what = LoginSuccess;
					GetSchedule.this.handler.sendMessageDelayed(msg,500);
				}else{
					Message msg = new Message();
					msg.what = LoginFailed;
					GetSchedule.this.handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}