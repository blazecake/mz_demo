package com.nicolls.usercalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
 
	/**保存用户日程SharePre*/
	public static final String USER_CALENDAR="_USER_CALENDAR";
	/**保存用户日程SharePre ID*/
	public static final String CALENDAR_ID="_CALENDAR_ID";
	/**传值*/
	public static final String CAL_ID="_CAL_ID";
	public static final String CAL_NAME="_CAL_NAME";
	
	private EditText mNameET;
	private Button mBtnColor;
	private int color=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mNameET=(EditText) findViewById(R.id.et_name);
		mBtnColor=(Button)findViewById(R.id.bt_color);
		String calID=getCalendarID();
		if(!TextUtils.equals("0", calID)){//已创建，直接进入日程管理
			enterNext(calID);
		}
		
		findViewById(R.id.main_btn_cal).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_btn_cal:
			Intent intent=new Intent(this, CalendarActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**颜色选择*/
	public void onColorSelectAction(View view){
		AlertDialog.Builder builder=new Builder(this);
		builder.setItems(new String[]{"红色","黄色","绿色","蓝色","青色"}, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				color=which;
				String colorName="";
				switch(color){
				case 0:colorName="红色";break;
				case 1:colorName="黄色";break;
				case 2:colorName="绿色";break;
				case 3:colorName="蓝色";break;
				case 4:colorName="青色";break;
				default:colorName="红色";break;
				}
				mBtnColor.setText(colorName);
			}
		});
		AlertDialog dialog=builder.create();
		dialog.show();
	}
	
	/**完成*/
	public void onCompletedAction(View rview){
		if(TextUtils.isEmpty(mNameET.getText())){
			Toast.makeText(this, "请填写日程名称", Toast.LENGTH_SHORT).show();
		}else{
			String name=mNameET.getText().toString();
			int calColor=Color.RED;
			switch(color){
			case 0:calColor=Color.RED;break;
			case 1:calColor=Color.YELLOW;break;
			case 2:calColor=Color.GREEN;break;
			case 3:calColor=Color.BLUE;break;
			case 4:calColor=Color.CYAN;break;
			default:calColor=Color.RED;break;
			}
			createCalendar(name,calColor);
		}
	}
	
	/**从本地获取已经创建的日程ID，如果ID为0说明并没有创建否则已经创建*/
	private String getCalendarID(){
		SharedPreferences sp=getSharedPreferences(USER_CALENDAR, Context.MODE_PRIVATE);
		return sp.getString(CALENDAR_ID, "0");
	}
	
	/**保存用户新建的日程ID*/
	private void saveCalendarID(String calID){
		SharedPreferences sp=getSharedPreferences(USER_CALENDAR, Context.MODE_PRIVATE);
		sp.edit().putString(CALENDAR_ID, calID).commit();
	}
	
	/**进入日程管理*/
	private void enterNext(String calID){
		Intent intent=new Intent(this,CalendarActivity.class);
		intent.putExtra(CAL_ID, calID);
		intent.putExtra(CAL_NAME, mNameET.getText().toString());
		startActivity(intent);
		finish();
	}
	
	/**创建日程*/
	@SuppressLint("NewApi")
	private void createCalendar(String name,int color){
		ContentValues values=new ContentValues();
//		values.put(Calendars.ACCOUNT_NAME, "zhou.mao@mingdao.com");
//		values.put(Calendars.ACCOUNT_TYPE, "LOCAL");
		values.put(Calendars.NAME, name);
		values.put(Calendars.CALENDAR_DISPLAY_NAME, name);
		/**显示事件颜色*/
		values.put(Calendars.CALENDAR_COLOR, color);
		/**权限级别*/
//		values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		/**事件可见*/
		values.put(Calendars.VISIBLE, 1);
		values.put(Calendars.SYNC_EVENTS, 1);
		values.put(Calendars.OWNER_ACCOUNT, "blazeCake");
		/**注释的都是不可添加的，因为用户建的日程是无法使用这些属性*/
//			values.put("canModifyTimeZone",1);
//			values.put("canPartiallyUpdate",0);
//			values.put("maxReminders",5);
//			values.put("allowedReminders","0,1");
		
//			values.put("allowedAvailability","0,1");
//			values.put("allowedAttendeeTypes","0,1,2");
//			values.put("deleted",1);
		
		Uri curi=asSyncAdapter(CalendarContract.Calendars.CONTENT_URI, "zhou.mao@mingdao.com", "Mingdao");
		
		/**新增用户日程*/
		Uri uri=getContentResolver().insert(curi, values);
		/**获取返回的新建日程ID*/
		Long calId= Long.parseLong(uri.getLastPathSegment());
		if(calId!=0){
			Toast.makeText(this, "新建日程成功",Toast.LENGTH_SHORT).show();
			saveCalendarID(calId+"");
			enterNext(calId+"");
		}else{
			Toast.makeText(this, "新建日程失败",Toast.LENGTH_SHORT).show();
		}
	}
	
	static Uri asSyncAdapter(Uri uri, String account, String accountType) {
	    return uri.buildUpon()
	        .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
	        .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
	        .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();
	 }

	
	
}
