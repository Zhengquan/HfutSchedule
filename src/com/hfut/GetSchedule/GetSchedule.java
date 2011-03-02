package com.hfut.GetSchedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.util.GetSchedule.CookiesManager;

public class GetSchedule extends Activity {
    /** Called when the activity is first created. */
	private Button mButton1;
	private Button mButton2;
	private EditText mEditText1;
	private EditText mEditText2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mButton1 = (Button)findViewById(R.id.button1);
        mButton2 = (Button)findViewById(R.id.button2);
        mEditText1 = (EditText)findViewById(R.id.edittext1);
        mEditText2 = (EditText)findViewById(R.id.edittext2);
        initialClickedListener();
	}
    private void initialClickedListener() {
		mButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				mButton1.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_focus));
				CookiesManager cookiesManager = new CookiesManager(
						 "http://210.45.240.29/student/html/s_index.htm"
						,"http://210.45.240.29/pass.asp"
						, mEditText1.getText().toString()
						, mEditText2.getText().toString());

				try {
					boolean login_success = cookiesManager.GenerateCookies();
					if(login_success){
						Intent intent  = new Intent();
						intent.setClass(GetSchedule.this, ScheduleView.class);
						Bundle bundle = new Bundle();
						String user = mEditText1.getText().toString();
						String password = mEditText2.getText().toString();
						bundle.putString("user",user);
						bundle.putString("password",password);
						intent.putExtras(bundle);
						startActivity(intent);
					}else{
						//登陆失败
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				mEditText1.setText("");
				mEditText2.setText("");
			}
		});
	}
}