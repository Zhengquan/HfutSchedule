package com.hfut.GetSchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.util.GetSchedule.ParseHtml;
import com.util.GetSchedule.CookiesManager;
import com.util.GetSchedule.ScheduleManager;

public class ScheduleView extends Activity {
	private TextView textViewDate ;
	private Handler handler;
	private TextView textViewArraya;
	private TextView textViewArrayb;
	private TextView textViewArrayc;
	private TextView textViewArrayd;
	private TextView textViewArraye;
	
	private Button mButton_next;
	private Button mButton_former;
	private ArrayList<ArrayList<String>> scheduleData;
	
	private int next_count;
	private static final int UPDATE_DATE_TEXTVIEW = 0x111;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		//初始化TextView控件
		textViewArraya = (TextView)findViewById(R.id.schedule1);//0,1
		textViewArrayb = (TextView)findViewById(R.id.schedule2);//2,3
		textViewArrayc = (TextView)findViewById(R.id.schedule4);//4,5
		textViewArrayd = (TextView)findViewById(R.id.schedule5);//6,7
		textViewDate = (TextView)findViewById(R.id.textview3);
		textViewArraye = (TextView)findViewById(R.id.schedule3);
		mButton_former =(Button)findViewById(R.id.button_former);
		mButton_next = (Button)findViewById(R.id.button_Next);
		
		//创建Handler
		createHandler();
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
			scheduleData = parseHtml.getScheduleThisTerm();
			//parseHtml.getUserInformation();
			ArrayList<String> schedule = ScheduleManager.getDaysSchedule(scheduleData, 0);
			DateManagerThread dateManagerThread = new DateManagerThread();
			setTextScheduleView(schedule);
			dateManagerThread.start();
			initButtonListener();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void initButtonListener(){
		next_count = 0;
		textViewArraye.setText("  中午  ");
		mButton_next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				next_count++;
				ArrayList<String> schedule = ScheduleManager.getDaysSchedule(scheduleData, next_count);
				setTextScheduleView(schedule);
			}
		});
		mButton_former.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(next_count == 0){
					next_count = 7;
				}
				ArrayList<String> schedule = ScheduleManager.getDaysSchedule(scheduleData, --next_count);
				setTextScheduleView(schedule);
			}
		});
	}
	void setTextScheduleView(ArrayList<String> schedule){
		textViewArraya.setText(schedule.get(0));
		textViewArrayb.setText(schedule.get(2));
		textViewArrayc.setText(schedule.get(4));
		textViewArrayd.setText(schedule.get(6));
	}
	private void createHandler() {
		// TODO Auto-generated method stub
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
				case UPDATE_DATE_TEXTVIEW:
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月-d日EE ,a HH:MM:ss",Locale.CHINA);
					String formattedStr = simpleDateFormat.format(date);
					textViewDate.setText(formattedStr);
				break;
				}
			}
		};
	}
	public class DateManagerThread extends Thread {
		@Override
		public void run() {
			while(true){
				try {
					Message msg = new Message();
					msg.what = UPDATE_DATE_TEXTVIEW;
					ScheduleView.this.handler.sendMessage(msg);
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
