package com.hfut.GetSchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.util.GetSchedule.ParseHtml;
import com.util.GetSchedule.CookiesManager;
import com.util.GetSchedule.ScheduleDataBaseHelper;
import com.util.GetSchedule.ScheduleManager;
import com.util.GetSchedule.SchedulePreferenceManager;

public class ScheduleView extends Activity {
	private TextView textViewDate ;
	private Handler handler;
	private TextView []textViewArray;
	private TextView others;
	private Bundle bundle;
	
	private Button mButton_next;
	private Button mButton_former;
	private ArrayList<ArrayList<String>> scheduleData;
	private boolean current_logged;
	
	private int next_count;
	
	//当前的用户信息
	private String user;
	private String password;
	private String user_information;
	private String other_information;
	//当前用户的数据在数据库中存储的table名称
	private String table_name;
	private static final int UPDATE_DATE_TEXTVIEW = 0x111;
	private static final String [] weekend ={
		 "星期一"
		,"星期二"
		,"星期三"
		,"星期四"
		,"星期五"
		,"星期六"
		,"星期曰"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		textViewArray = new TextView[5];
		//初始化TextView控件
		textViewArray[0] = (TextView)findViewById(R.id.schedule1);//0,1
		textViewArray[1] = (TextView)findViewById(R.id.schedule2);//2,3
		textViewArray[2]= (TextView)findViewById(R.id.schedule4);//4,5
		textViewArray[3]= (TextView)findViewById(R.id.schedule5);//6,7
		textViewArray[4] = (TextView)findViewById(R.id.schedule3);
		
		textViewDate = (TextView)findViewById(R.id.textview3);
		mButton_former =(Button)findViewById(R.id.button_former);
		mButton_next = (Button)findViewById(R.id.button_Next);
		others = (TextView)findViewById(R.id.schedule6);
		
		//创建Handler
		createHandler();
		//创建bundle
		bundle = new Bundle();
		/**根据登录界面传送过来的current_logged,
		 * 来判断当前用户是否已经登录过
		 * 如果没有登录过:则联网，生成Cookie，传输数据，
		 * 并保存至数据库中。
		 * 如果当前用户已经完成登录，则直接从数据库中读取
		 * 数据，来初始化scheduleData等
		 * */
		initialScheduleData();
		//数据初始化完成,完成按钮的响应
		initButtonListener();
		//取得当天的课程分布,并显示在UI上
		ArrayList<String> schedule = ScheduleManager.getDaysSchedule(scheduleData, 0);
		setTextScheduleView(schedule);
		
		//创建界面管理进程
		UIManagerThread uiManagerThread = new UIManagerThread();
		uiManagerThread.start();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(Menu.NONE,Menu.FIRST, Menu.NONE,"个人信息")
		.setIcon(android.R.drawable.ic_menu_info_details);
		
		menu.add(Menu.NONE,Menu.FIRST + 1,Menu.NONE,"刷新")
		.setIcon(android.R.drawable.ic_popup_sync);
		
		menu.add(Menu.NONE,Menu.FIRST + 2,Menu.NONE,"注销")
		.setIcon(android.R.drawable.ic_dialog_alert);
		
		menu.add(Menu.NONE, Menu.FIRST + 3, Menu.NONE,"反馈")
		.setIcon(android.R.drawable.ic_menu_agenda);
		
		menu.add(Menu.NONE,Menu.FIRST + 4, Menu.NONE,"关于&帮助")
		.setIcon(android.R.drawable.ic_menu_help);
		
		menu.add(Menu.NONE,Menu.FIRST + 5,Menu.NONE,"退出")
		.setIcon(android.R.drawable.ic_dialog_map);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case Menu.FIRST :
			Intent intent_infor = new Intent();
			intent_infor.setClass(this, UserInforActivity.class);
			bundle.putString("USER_INFOR", user_information);
			intent_infor.putExtras(bundle);
			startActivity(intent_infor);
 			break;
		case Menu.FIRST + 3:
			//发送邮件
			Intent email_intent = new Intent(android.content.Intent.ACTION_SEND);
			email_intent.setType("plain/text*");
			//放入数据
			String [] users_to = {"yangzhengquan@gmail.com"};
			email_intent.putExtra(Intent.EXTRA_EMAIL,users_to);
			email_intent.putExtra(Intent.EXTRA_SUBJECT, "Report A Schedule Bug Or Suggestion");
			startActivity(Intent.createChooser(email_intent, "发送邮件..."));
			break;
		case Menu.FIRST + 1:
			refeshDataByNetConnect();
			break;
		case Menu.FIRST + 5:
			setResult(GetSchedule.NEED_EXIT);
			finish();
			break;
		case Menu.FIRST + 2:
			//注销会删除当前用户的数据库表和配置文件
			deleteUserData();
			break;
		case Menu.FIRST + 4:
			Intent intent = new Intent();
			intent.setClass(this, HelpActivity.class);
			startActivityForResult(intent,GetSchedule.NEED_EXIT);
		}
		return super.onContextItemSelected(item);
	}
	private void refeshDataByNetConnect() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
		.setTitle("是否更新数据?")
		.setIcon(android.R.drawable.ic_menu_rotate)
		.setMessage("更新数据需要比较长的时间,是否更新数据?")
		.setPositiveButton(
				"确定", 
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						//删除已经存储的数据
						Toast.makeText(ScheduleView.this, 
								"正在删除已有数据，请耐心等待..."
								, Toast.LENGTH_SHORT)
								.show();
						ScheduleDataBaseHelper scheduleDataBaseHelper = new ScheduleDataBaseHelper(ScheduleView.this
								,"schedule"
								,null
								,1);
						scheduleDataBaseHelper.dropTable("_"+user);
						scheduleDataBaseHelper.close();
						//下载并存储数据
						Toast.makeText(ScheduleView.this, 
								"正在下载、更新数据，请耐心等待..."
								, Toast.LENGTH_LONG)
								.show();
						downloadDataAndStore();
						Toast.makeText(ScheduleView.this, 
								"数据下载完成，正在载入新的数据,请耐心等待....!"
								, Toast.LENGTH_SHORT)
								.show();
						fillDataFromDB();
						//更新完成
						Toast.makeText(ScheduleView.this, 
								"恭喜，数据更新完成!"
								, Toast.LENGTH_SHORT)
								.show();
						return;
					}
				})
		.setNegativeButton(
				"取消",
				new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface arg0, int arg1) {
						return;
					}
				})
		.show();
		
	}
	private void deleteUserData() {
		// TODO Auto-generated method stub
		//删除数据表
		new AlertDialog.Builder(this)
		.setTitle("Warning")
		.setMessage(getResources().getString(R.string.warning))
		.setIcon(android.R.drawable.stat_sys_warning)
		.setPositiveButton(
				"确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						//确定则删除数据
						ScheduleDataBaseHelper scheduleDataBaseHelper = new ScheduleDataBaseHelper(ScheduleView.this
								,"schedule"
								,null
								,1);
						scheduleDataBaseHelper.dropTable("_"+user);
						scheduleDataBaseHelper.close();
						//重置配置文件
						SchedulePreferenceManager.createSchedulePreference
						(ScheduleView.this,"","", false);
						//清空备注
						SchedulePreferenceManager.addDataByKey(ScheduleView.this, user+"_unarr", "");
						SchedulePreferenceManager.addDataByKey(ScheduleView.this, user+"_comm", "");
						SchedulePreferenceManager.addDataByKey(ScheduleView.this, user+"_informations","");
						setResult(GetSchedule.NEED_EXIT);
						finish();
					}
				})
		.setNegativeButton(
				"取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						return;
					}
				})
			.show();
	}
	
	private void initialScheduleData() {
		// TODO Auto-generated method stub
		
		bundle = this.getIntent().getExtras();
		user = bundle.getString("user");
		password = bundle.getString("password");
		ScheduleDataBaseHelper scheduleDataBaseHelper = new ScheduleDataBaseHelper(this
				,"schedule"
				,null
				,1);
		current_logged = scheduleDataBaseHelper.currentHadLogged(user);
		//关闭数据库
		scheduleDataBaseHelper.close();
		//初始化table_name
		table_name = "_"+user;	
		if(current_logged){
			//读取数据
			fillDataFromDB();
			return;
		}
		/*没有登录则下载数据并存储
		 * 下载的时候已经完成了数据的填充
		 * 故不需要再次读出
		*/
		downloadDataAndStore();
	}
	private void fillDataFromDB() {
		// TODO Auto-generated method stub
		ScheduleDataBaseHelper scheduleDataBaseHelper = new ScheduleDataBaseHelper(this
				,"schedule"
				,null
				,1);
		scheduleData = scheduleDataBaseHelper.getStudentSchedule(table_name);
		scheduleDataBaseHelper.close();
		
		other_information = SchedulePreferenceManager.returnDataByKey(this, user+"_unarr")
		+ "\n" +SchedulePreferenceManager.returnDataByKey(this, user+"_comm");
		//填充用户信息
		user_information = SchedulePreferenceManager.returnDataByKey(this, user+"_informations");
	}
	void downloadDataAndStore(){
		CookiesManager cookiesManager = new CookiesManager(
				 "http://210.45.240.29/student/html/s_index.htm"
				,"http://210.45.240.29/pass.asp"
				,user
				,password);
		try {
			cookiesManager.GenerateCookies();
			ParseHtml parseHtml = new ParseHtml(cookiesManager.defaultHttpClient);
			scheduleData = parseHtml.getScheduleThisTerm();
			//构造备注数组
			ArrayList<String>unarrnge = scheduleData.get(11);
			ArrayList<String>comments = scheduleData.get(12);
			//获取个人信息
			HashMap<String, String> user_infor = parseHtml.getUserInformation();
			//存储至配置文件
			storeOtherInfromationToPreference(unarrnge,comments,user_infor);
			//下载完成后，利用ScheduleDataBaseHelper类完成数据的存储工作
			ScheduleDataBaseHelper scheduleDataBaseHelper = new ScheduleDataBaseHelper(this
					,"schedule"
					,null
					,1);
			//创建数据表
			scheduleDataBaseHelper.createTable(table_name);
			//存储数据之Schedule的table_name表中
			scheduleDataBaseHelper.insertStudentSchedule(table_name, scheduleData);
			//关闭数据库
			scheduleDataBaseHelper.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void storeOtherInfromationToPreference(ArrayList<String>unarrange,ArrayList<String>comments,HashMap<String,String> user_infor) {
		// TODO Auto-generated method stub
		//添加未安排课程
		String unarr = "";
		for(String temp : unarrange)
			unarr += temp;
		String comm = "";
		for(String temp : comments)
			comm +=temp;
		String informations = "";
		informations += "姓名:"+user_infor.get("姓名")+"\n";
		informations += "学号:"+user_infor.get("学号")+"\n";
		informations += "专业:"+user_infor.get("专业")+"\n";
		informations += "性别:"+user_infor.get("性别")+"\n";
		informations += "学院:"+user_infor.get("学院")+"\n";
		
		//存储至配置文件中
		SchedulePreferenceManager.addDataByKey(this, user+"_unarr", unarr);
		SchedulePreferenceManager.addDataByKey(this, user+"_comm", comm);
		SchedulePreferenceManager.addDataByKey(this, user+"_informations",informations);
		
		other_information = SchedulePreferenceManager.returnDataByKey(this, user+"_unarr")
		+ "\n" +SchedulePreferenceManager.returnDataByKey(this, user+"_comm");
		//填充用户信息
		user_information = SchedulePreferenceManager.returnDataByKey(this, user+"_informations");
	}
	void initButtonListener(){
		next_count = 0;
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
		others.setText(other_information);
		for(int i=0;i<4;i++)
			textViewArray[i].setText(schedule.get(i*2));
		int real_week = ScheduleManager.getRealWeekDay();
		if(next_count%7 == 0)
			textViewArray[4].setTextColor(Color.rgb(0, 100, 0));
		else
			textViewArray[4].setTextColor(R.color.textview_week_default);
		textViewArray[4].setText(weekend[(next_count+real_week-1)%7]);
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
					//更新顶端的当前Date-Time视图
					Date date = new Date();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月-d日EE ,a HH:MM:ss",Locale.CHINA);
					String formattedStr = simpleDateFormat.format(date);
					textViewDate.setText(formattedStr);
					//更新课表中的星期(显示内容对应的星期)
				break;
				}
			}
		};
	}
	public class UIManagerThread extends Thread {
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
