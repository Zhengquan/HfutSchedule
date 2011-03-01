package com.util.GetSchedule;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

public class ParseHtml {
	private DefaultHttpClient defaultHttpClient;
	public ParseHtml(DefaultHttpClient defaultHttpClient) {
		// TODO Auto-generated constructor stub
		this.defaultHttpClient = defaultHttpClient;
	}
	public HashMap<String, String> getUserInformation(){
		HttpGet httpget = new HttpGet("http://210.45.240.29/student/asp/xsxxxxx.asp");
		try {
			HttpResponse response = defaultHttpClient.execute(httpget);
			//System.out.println(EntityUtils.toString(response.getEntity()));
			String s = EntityUtils.toString(response.getEntity());
			//CookiesManager.ReleaseConnection(response);
			//解析数据_个人信息
			String  [] user_Name_pattern = {
					"姓名:(.*)<",
					"学号:(\\d*)\\s*<",
					"性别:[\\s\\S]+?([男,女])[\\s\\S]+?<",
					">(.*)学院",
					">(.*)专业",
					">([0-9]{11})&nbsp;<"
			};
			String [] keys = {"姓名","学号","性别","学院","专业","电话"};
			HashMap<String, String>result = new HashMap<String, String>();
			for( int i =0 ;i<user_Name_pattern.length;i++)
			{
				Pattern pattern = Pattern.compile(user_Name_pattern[i]);
				Matcher matcher = pattern.matcher(s);
				while(matcher.find()){
					result.put(keys[i],matcher.group(1));
			}
			}
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public ArrayList<ArrayList<String>>  getScheduleThisTerm(){
		String schedule_apttern = "<TR height=\"20\"\\s?>([\\s\\S]+?)</TR>";
		HttpGet httpGet = new HttpGet("http://210.45.240.29/student/asp/grkb1.asp");
		try {
			HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
			String s = EntityUtils.toString(httpResponse.getEntity());
			//CookiesManager.ReleaseConnection(httpResponse);
			//匹配table部分
			Pattern pattern = Pattern.compile(schedule_apttern,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(s);
			//用来返回的数组
			ArrayList<ArrayList<String> > result =new ArrayList<ArrayList<String>>();
			int i = 0;
			while(matcher.find()){
				String temp = matcher.group(1);
				Pattern class_patternPattern = Pattern.compile("<TD>(.*?)</TD>");
				Matcher class_matcher = class_patternPattern.matcher(temp);
				result.add(new ArrayList<String>());
				while(class_matcher.find()){
					result.get(i).add(class_matcher.group(1));
				}
				i++;
			}
			//增加未安排
			pattern = Pattern.compile("未安排时间的课程:(.+)/");
			matcher = pattern.matcher(s);
			if(matcher.find()){
				result.add(new ArrayList<String>());
				for (String temp : matcher.group(1).split("/")){
					result.get(i).add(temp);
				}
				i++;
			}
			//增加备注
			pattern = Pattern.compile("<TD>教学班备注：</TD>[\\s\\S]+<TD>(.*)</TD>");
			matcher = pattern.matcher(s);
			if(matcher.find()){
				result.add(new ArrayList<String>());
				String [] memos = matcher.group(1).split("<br>");
				for (int j =0 ;j<memos.length;j++){
					result.get(i).add(memos[j]);
				}
			}
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void shutDownDefaultClent(){
		defaultHttpClient.getConnectionManager().shutdown();
	}
}
