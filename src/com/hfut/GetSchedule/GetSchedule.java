package com.hfut.GetSchedule;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.util.GetSchedule.CookiesManager;

public class GetSchedule extends Activity {
    /** Called when the activity is first created. */
	private Button mButton1;
	private Button mButton2;
	private EditText mEditText1;
	private EditText mEditText2;
	private ProgressBar progressBar1;
	private Handler handler;
	private TextView textView4;
	
	private static final int  LoginSuccess = 0x127;
	private static final int  LoginFailed = 0x128;
	private static final int LOGING_THREAD = 0x129;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mButton1 = (Button)findViewById(R.id.button1);
        mButton2 = (Button)findViewById(R.id.button2);
        mEditText1 = (EditText)findViewById(R.id.edittext1);
        mEditText2 = (EditText)findViewById(R.id.edittext2);
        progressBar1 = (ProgressBar)findViewById(R.id.progressbar1);
        textView4 = (TextView)findViewById(R.id.textview4);
        
        textView4.setVisibility(TextView.INVISIBLE);
        createHandler();
        initialClickedListener();
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
					Intent intent  = new Intent();
					intent.setClass(GetSchedule.this, ScheduleView.class);
					Bundle bundle = new Bundle();
					String user = mEditText1.getText().toString();
					String password = mEditText2.getText().toString();
					bundle.putString("user",user);
					bundle.putString("password",password);
					//测试用
					bundle.putBoolean("IS_LOGGED",true);
					intent.putExtras(bundle);
					mButton1.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_back));
					progressBar1.setVisibility(ProgressBar.INVISIBLE);
					textView4.setText("Login Success!");
					startActivity(intent);
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
				mEditText1.setText("");
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
					, mEditText1.getText().toString()
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