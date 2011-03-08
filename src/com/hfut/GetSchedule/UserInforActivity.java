package com.hfut.GetSchedule;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserInforActivity extends Activity {
	private TextView mText1;
	private Button mButton1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_layout);
		mText1 = (TextView)findViewById(R.id.textview_help);
		mButton1 = (Button)findViewById(R.id.button_have_know);
		Bundle bundle = this.getIntent().getExtras();
		String infor = bundle.getString("USER_INFOR");
		mText1.setText(infor);
		
		mButton1.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				finish();
			}
		});
		
	}
}
