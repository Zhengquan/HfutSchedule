package com.util.GetSchedule;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class ScheduleDataBaseHelper extends SQLiteOpenHelper {
	private static String COLUMNS = 
		"(Mon text,Tues text,Wed text,Thur text,Fri text,Sat text,Sun text)";
	private static String []COLUMN_ARRAY ={"Mon","Tues","Wed","Thur","Fri","Sat","Sun"};
	
	public ScheduleDataBaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory,version);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		super.close();
	}
	//创建数据表
	public void createTable(String tablename){
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		String sql = "CREATE table \""+tablename +"\""+ COLUMNS;
		sqLiteDatabase.execSQL(sql);
	}	
	//插入一行记录_辅助函数
	private long insertItem(String tablename,ArrayList<String>strArray){
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		for(int i=0;i<strArray.size();i++)
			cv.put(COLUMN_ARRAY[i], strArray.get(i));
		return sqLiteDatabase.insert(tablename, null, cv);
	}
	private Cursor selectItem(String tablename,String[] columns){
		SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.query(tablename
				, columns
				, null
				, null
				, null
				, null
				, null);
		return cursor;
	}
	//删除表
	public void dropTable(String tablename){
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		String sql = "DROP TABLE \"" +tablename+"\"";
		sqLiteDatabase.execSQL(sql);
	}
	//用于填充一个表的记录
	public void insertStudentSchedule(String tablename,ArrayList<ArrayList<String>> array){
		for(int i =0;i<array.size()-2;i++){
			ArrayList<String>temp = array.get(i);
			insertItem(tablename, temp);
		}
	}
	//用于返回一个表的所有数据，并以ArrayList格式存放
	public ArrayList<ArrayList<String>> getStudentSchedule(String tablename){
		ArrayList<ArrayList<String>>result = new ArrayList<ArrayList<String>>();
		ArrayList<String>temp = new ArrayList<String>();
		Cursor cursor = selectItem(tablename, COLUMN_ARRAY);
		{
			for(int i=0;i<6;i++)
				temp.add(cursor.getString(i));
			result.add(temp);
			temp.clear();
		}while(cursor.moveToNext());
		return result;
	}
}
