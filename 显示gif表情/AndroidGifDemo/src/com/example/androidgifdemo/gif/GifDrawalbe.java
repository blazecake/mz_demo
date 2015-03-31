package com.example.androidgifdemo.gif;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;

public class GifDrawalbe extends AnimationDrawable {

	public GifDrawalbe(Context context, int id) {
		GifHelper helper = new GifHelper();
		helper.read(context.getResources().openRawResource(id));
		
		System.out.println("---------------------con:"+context.getResources().openRawResource(id).toString());
		int gifCount = helper.getFrameCount();
		if (gifCount <= 0) {
			return;
		}
		BitmapDrawable bd = new BitmapDrawable(null, helper.getImage());
		addFrame(bd, helper.getDelay(0));
		for (int i = 1; i < helper.getFrameCount(); i++) {
			addFrame(new BitmapDrawable(null, helper.nextBitmap()), helper.getDelay(i));
		}
		setBounds(0, 0, helper.getImage().getWidth(), helper.getImage().getHeight());
		bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
		invalidateSelf();
	}

}
