package com.hfut.GetSchedule;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.util.GetSchedule.ParseHtml;
import com.util.GetSchedule.CookiesManager;
import com.util.GetSchedule.ScheduleManager;

public class ScheduleView extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		//初始化TextView控件
		
		TextView textViewArraya = (TextView)findViewById(R.id.schedule1);//0,1
		TextView textViewArrayb = (TextView)findViewById(R.id.schedule2);//2,3
		TextView textViewArrayc = (TextView)findViewById(R.id.schedule4);//4,5
		TextView textViewArrayd = (TextView)findViewById(R.id.schedule5);//6,7
		
		
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		CookiesManager cookiesManager = new CookiesManager(
				 "http://210.45.240.29/student/html/s_index.htm"
				,"http://210.45.240.29/pass.asp"
				,bundle.getString("user")
				,bundle.getString("password"));
		try {
			cookiesManager.GenerateCookies();
			ParseHtml parseHtml = new ParseHtml(cookiesManager.defaultHttpClient);
			ArrayList<ArrayList<String>> scheduleData = parseHtml.getScheduleThisTerm();
			parseHtml.getUserInformation();
			ArrayList<String> schedule = ScheduleManager.getDaysSchedule(scheduleData, 1);
			
			textViewArraya.setText(schedule.get(0));
			textViewArrayb.setText(schedule.get(2));
			textViewArrayc.setText(schedule.get(4));
			textViewArrayd.setText(schedule.get(6));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
