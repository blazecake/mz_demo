package com.nicolls.usercalendar;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class CalendarObserver extends ContentObserver {

	private Context context;
	private Handler handler;
	
	public CalendarObserver(Context context,Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.handler=handler;
	}

	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
		handler.obtainMessage(3,"events have chage").sendToTarget();
	}



}
