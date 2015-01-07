package com.nicolls.usercalendar;

import java.util.Calendar;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EventCalendarActivity extends Activity implements OnClickListener{

	private String calID="",calName="";
	private LinearLayout layoutEvent,layoutCurd;
	private EditText mEtTitle,mEtDescription,mEtAlarm,mEtTitleUpdate;
	private int flag=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		calID=getIntent().getStringExtra(MainActivity.CAL_ID);
		calName=getIntent().getStringExtra(MainActivity.CAL_NAME);
		layoutEvent=(LinearLayout) findViewById(R.id.layout_event);
		layoutCurd=(LinearLayout) findViewById(R.id.layout_curd);
		mEtTitle=(EditText) findViewById(R.id.event_et_title);
		mEtDescription=(EditText) findViewById(R.id.event_et_description);
		mEtAlarm=(EditText) findViewById(R.id.event_et_alarm);
		mEtTitleUpdate=(EditText) findViewById(R.id.event_et_title_update);
		SHView(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
/**显示操作 true显示操作按钮 false显示编辑按钮*/
	private void SHView(boolean t){
		if(t){
			layoutEvent.setVisibility(View.VISIBLE);
			layoutCurd.setVisibility(View.GONE);
		}else{
			layoutEvent.setVisibility(View.GONE);
			layoutCurd.setVisibility(View.VISIBLE);
		}
	}
	/**添加按钮*/
	public void onAdd(View view){
		mEtTitleUpdate.setVisibility(View.GONE);
		SHView(false);
		flag=0;
	}
	/**更新按钮*/
	public void onUpdate(View view){
		mEtTitleUpdate.setVisibility(View.VISIBLE);
		SHView(false);
		flag=1;
	}
	/**添加*/
	@SuppressLint("NewApi")
	public void addCalendar(){
		long startMillis = 0; 
		long endMillis = 0;     
		//开始时间结束时间，在此定死为当前时间相隔20分钟
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(Calendar.MINUTE, beginTime.getTime().getMinutes()+10);
		startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(Calendar.MINUTE, endTime.getTime().getMinutes()+30);
		endMillis = endTime.getTimeInMillis();

		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.CALENDAR_ID, calID);
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.HAS_ALARM,1);
		values.put(Events.TITLE, mEtTitle.getText().toString());
		values.put(Events.DESCRIPTION, mEtDescription.getText().toString());
		values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID().toString());
		values.put(Events.EVENT_END_TIMEZONE, TimeZone.getDefault().getID().toString());
		Uri uri = cr.insert(Events.CONTENT_URI, values);
		Long eventId= Long.parseLong(uri.getLastPathSegment());
    	ContentValues remider = new ContentValues();
    	remider.put(Reminders.EVENT_ID, eventId );
        //提前10分钟有提醒
    	remider.put( Reminders.MINUTES, mEtAlarm.getText().toString() );
    	remider.put(Reminders.METHOD, Reminders.METHOD_ALERT );
        getContentResolver().insert(Reminders.CONTENT_URI, remider);
        Toast.makeText(this, "添加事件成功", Toast.LENGTH_SHORT).show();
        SHView(true);
	}
	
	/**更新*/
	@SuppressLint("NewApi")
	public void updateCalendar(){
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.TITLE, mEtTitle.getText().toString());
		values.put(Events.DESCRIPTION, mEtDescription.getText().toString());
		int rows=0;
		rows=cr.update(Events.CONTENT_URI, values, Events.CALENDAR_ID+"=?"+" and "+Events.TITLE+"=?", new String[]{calID,mEtTitle.getText().toString()});
		if(rows>0){
			Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
		}
		SHView(true);
	}
	/**删除用户的所有事件 */
	@SuppressLint("NewApi")
	public void onDeleted(View view){
		
		int rows=getContentResolver().delete(Events.CONTENT_URI, Events.CALENDAR_ID+"=?", new String[]{calID+""});
			if(rows>0){
				Toast.makeText(this, "删除用户事件成功", Toast.LENGTH_SHORT).show();
			}
		else{
			Toast.makeText(this, "删除用户事件失败", Toast.LENGTH_SHORT).show();
		}
		SHView(true);
	}
	private boolean isEmpty(){
		if(mEtTitle.getText().toString().isEmpty()||mEtDescription.getText().toString().isEmpty()){
			Toast.makeText(this, "此输入不能为空", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(layoutEvent.getVisibility()==View.GONE){
			SHView(true);
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	public void onConfirm(View view){
		switch(flag){
		case 0://添加
			addCalendar();
			break;
		case 1://更新
			updateCalendar();
			break;
		}
	}
	
}
