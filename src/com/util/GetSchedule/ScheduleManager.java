package com.util.GetSchedule;

import java.util.Calendar;
import java.util.ArrayList;

public class ScheduleManager {
	//Constructor
	public ScheduleManager(){};
	//ArrangeMent Of Schedule
	//n=0 today ;1 tomorrow...
	public static ArrayList<String> getDaysSchedule
	(ArrayList<ArrayList<String>> scheduleData,int n) {
		int real_week = getRealWeekDay();
		ArrayList<String> result = new ArrayList<String>();
		for(int i =0; i<11;i++){
			result.add(scheduleData.get(i).get((real_week+n%7-1)%7));
		}
			return result;
	}
	/**获取真正的星期
	 * 周一:1
	 * 周二:2
	 * .....
	 * 周日:7
	**/
	public static int getRealWeekDay(){
		Calendar calendar = Calendar.getInstance();
		int current_week = calendar.get(Calendar.DAY_OF_WEEK);
		int real_week = (current_week-1)+7*((8-current_week)/7);
		return real_week;
	}
	
}
