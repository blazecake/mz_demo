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
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateCalendarActivity extends Activity {

	private String calID="";
	private EditText mNameET;
	private int color=0;
	private Button mBtnColor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		calID=getCalendarID();
		mNameET=(EditText)findViewById(R.id.et_name);
		mNameET.setText(getIntent().getStringExtra(MainActivity.CAL_NAME));
		mBtnColor=(Button)findViewById(R.id.bt_color);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
	
	@SuppressLint("NewApi")
	public void onCompletedAction(View view){
		int calColor=Color.RED;
		int m=0;
		switch(color){
		case 0:calColor=Color.RED;break;
		case 1:calColor=Color.YELLOW;break;
		case 2:calColor=Color.GREEN;break;
		case 3:calColor=Color.BLUE;break;
		case 4:calColor=Color.CYAN;break;
		default:calColor=Color.RED;break;
		}
		ContentValues values=new ContentValues();
		String name=mNameET.getText().toString();
		values.put(Calendars.ACCOUNT_NAME,mNameET.getText().toString());
		values.put(Calendars.CALENDAR_DISPLAY_NAME,mNameET.getText().toString());
		values.put(Calendars.CALENDAR_COLOR,calColor);
		/**更新用户日程*/
		if("0".equals(calID)){
			getContentResolver().insert(CalendarContract.Calendars.CONTENT_URI, values);
		}else{
			m=getContentResolver().update(CalendarContract.Calendars.CONTENT_URI, values, CalendarContract.Calendars._ID+"=?", new String[]{calID});
		}
		if(m>0){
			Toast.makeText(this, "更新日程成功", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**从本地获取已经创建的日程ID，如果ID为0说明并没有创建否则已经创建*/
	private String getCalendarID(){
		SharedPreferences sp=getSharedPreferences(MainActivity.USER_CALENDAR,Context.MODE_PRIVATE);
		return sp.getString(MainActivity.CALENDAR_ID, "0");
	}
}
