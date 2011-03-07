package com.hfut.GetSchedule;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends Activity {
	private TextView mTextView1;
	private Button mButton1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_layout);
		mTextView1 = (TextView)findViewById(R.id.textview_help);
		mButton1 = (Button)findViewById(R.id.button_have_know);
		mButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				setResult(GetSchedule.NEED_EXIT);
				finish();
			}
		});
		mTextView1.setText(getResources().getString(R.string.help));
	}

}
