package com.nicolls.usercalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class CalendarActivity extends Activity {

	private String calID = "";
	private String calName = "";
	
	/**日程改变监听*/
	private CalendarObserver calObserver;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		calObserver=new CalendarObserver(this, mHandler);
		//注册日程事件监听
		getContentResolver().registerContentObserver(Events.CONTENT_URI, true, calObserver);
		calID = getIntent().getStringExtra(MainActivity.CAL_ID);
		calName = getIntent().getStringExtra(MainActivity.CAL_NAME);
	}
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			/**当监听到改变时，做业务操作*/
			Log.i("tag", "now ");
			String msgStr=(String)msg.obj;
				System.out.println(msgStr+"----------------日程日程");
				Toast.makeText(CalendarActivity.this, "日程事件修改被触发", Toast.LENGTH_SHORT).show();
		}	
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/** 更新日程 */
	public void onUpdateAction(View view) {
		Intent intent = new Intent(this, UpdateCalendarActivity.class);
		intent.putExtra(MainActivity.CAL_ID, calID);
		intent.putExtra(MainActivity.CAL_NAME, calName);
		startActivity(intent);
	}

	/** 日程事件 */
	public void onEventAction(View view) {
		Intent intent = new Intent(this, EventCalendarActivity.class);
		intent.putExtra(MainActivity.CAL_ID, calID);
		intent.putExtra(MainActivity.CAL_NAME, calName);
		startActivity(intent);
	}

	/** 重置日程 */
	@SuppressLint("NewApi")
	public void onResetAction(View view) {
		Cursor cursor = getContentResolver().query(
				CalendarContract.Calendars.CONTENT_URI, null, null, null, null);
		String id = "";
		int row = 0;
		cursor.moveToLast();
		id = cursor.getString(cursor
				.getColumnIndex(CalendarContract.Calendars._ID));
		if(TextUtils.equals(getCalendarID(), id)){
			row = getContentResolver().delete(
					CalendarContract.Calendars.CONTENT_URI,
					CalendarContract.Calendars._ID + "=?", new String[] { id });
			if(row>0){
				Toast.makeText(this, "重置成功", Toast.LENGTH_SHORT).show();
				delectedCal();
			}
		}else{
			Toast.makeText(this, "重置失败", Toast.LENGTH_SHORT).show();
		}
	}
	/**从本地获取已经创建的日程ID，如果ID为0说明并没有创建否则已经创建*/
	private String getCalendarID(){
		SharedPreferences sp=getSharedPreferences(MainActivity.USER_CALENDAR,Context.MODE_PRIVATE);
		return sp.getString(MainActivity.CALENDAR_ID, "0");
	}
	/**删除日程ID*/
	private void delectedCal(){
		SharedPreferences sp=getSharedPreferences(MainActivity.USER_CALENDAR,Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}
}
