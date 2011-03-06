package com.util.GetSchedule;

import android.app.Activity;
import android.content.SharedPreferences;
public class SchedulePreferenceManager {
	public static void createSchedulePreference(
			Activity activity,String user,String password,boolean autologin){
		SharedPreferences sharedPreferences = activity.getSharedPreferences("schedule_preference",Activity.MODE_PRIVATE);
		sharedPreferences.edit()
		.putString("user", user)
		.putString("password", password)
		.putBoolean("autologin", autologin)
		.commit();
	}
	public static String returnPass(Activity activity){
		SharedPreferences sharedPreferences = activity.getSharedPreferences("schedule_preference",Activity.MODE_PRIVATE);
        return sharedPreferences.getString("password", "");
	}
	
	public static boolean returnAutolog(Activity activity){
		SharedPreferences sharedPreferences = activity.getSharedPreferences("schedule_preference",Activity.MODE_PRIVATE);
        	return sharedPreferences.getBoolean("autologin", false);
	}
	public static String [] returnUserArray(Activity activity){
		SharedPreferences sharedPreferences = activity.getSharedPreferences("schedule_preference",Activity.MODE_PRIVATE);
		String []result={sharedPreferences.getString("user", null)};
		return result;
	}
	public static String returnUserByPreference(Activity activity){
		SharedPreferences sharedPreferences = activity.getSharedPreferences("schedule_preference",Activity.MODE_PRIVATE);
    	return sharedPreferences.getString("user", null);
	}
}
