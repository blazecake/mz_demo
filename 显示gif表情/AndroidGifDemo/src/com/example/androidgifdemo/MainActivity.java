package com.example.androidgifdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.example.androidgifdemo.gif.Expressions;
import com.example.androidgifdemo.view.MyEditTextEx;
import com.example.androidgifdemo.view.MyTextViewEx;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener, OnPageChangeListener{
	private int[] expressionImages1 = null;
	private int[] expressionImages2 = null;
	private int[] expressionImages3 = null;
	private String[] expressionImageNames1 = null;
	private String[] expressionImageNames2 = null;
	private String[] expressionImageNames3 = null;
	
	private Dialog dialog = null;
	private ViewPager viewPager = null;
	private ArrayList<GridView> grids = null;
	private GridView gView1 = null, gView2 = null, gView3 = null;
	
	private Button btnGif = null;
	private MyEditTextEx editText = null;
	private MyTextViewEx textView = null;
	
	/*
	 * 引入表情
	 */
	private void initExpressions() {
		expressionImages1 = Expressions.expressionImgs1;
		expressionImages2 = Expressions.expressionImgs2;
		expressionImages3 = Expressions.expressionImgs3;
		expressionImageNames1 = Expressions.expressionImgNames1;
		expressionImageNames2 = Expressions.expressionImgNames2;
		expressionImageNames3 = Expressions.expressionImgNames3;
	}
	
	/*
	 * 初始化表情选择对话框
	 */
	private void initDialogEx() {
		this.initViewPager();
		dialog = new Dialog(this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
		dialog.setContentView(viewPager);
	}
	
	/*
	 * 初始化布局，3页表情每页24个
	 */
	private void initViewPager() {
		viewPager = (ViewPager) LayoutInflater.from(getApplicationContext()).inflate(R.layout.gif_dialog, null);
		grids = new ArrayList<GridView>();
		gView1 = new GridView(getApplicationContext());
		gView2 = new GridView(getApplicationContext());
		gView3 = new GridView(getApplicationContext());
		
		gView1.setNumColumns(6);
		gView2.setNumColumns(6);
		gView3.setNumColumns(6);
		
		gView1.setVerticalSpacing(10);
		gView2.setVerticalSpacing(10);
		gView3.setVerticalSpacing(10);
		
		gView1.setGravity(Gravity.CENTER);
		gView2.setGravity(Gravity.CENTER);
		gView3.setGravity(Gravity.CENTER);
		
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		// 生成24个表情
		for (int i = 0; i < 24; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", expressionImages1[i]);
			listItems.add(listItem);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), listItems, R.layout.singleexpression,
				new String[] { "image" }, new int[] { R.id.image });
		gView1.setAdapter(simpleAdapter);
		gView1.setOnItemClickListener(this);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return grids.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(grids.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(grids.get(position));
				return grids.get(position);
			}
		};
		
		grids.add(gView1);
		grids.add(gView2);
		grids.add(gView3);
		viewPager.setAdapter(mPagerAdapter);
		viewPager.setOnPageChangeListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initExpressions();
		initDialogEx();
		
		editText = (MyEditTextEx) findViewById(R.id.edit);
		textView = (MyTextViewEx) findViewById(R.id.text);
		
		btnGif = (Button) findViewById(R.id.btn_gif);
		btnGif.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		editText.destroy(); //销毁editText
		textView.destroy(); //销毁textView
	}

	@Override
	public void onPageSelected(int arg0) {
		switch (arg0) {
		case 1:
			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			// 生成24个表情
			for (int i = 0; i < 24; i++) {
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("image", expressionImages2[i]);
				listItems.add(listItem);
			}

			SimpleAdapter simpleAdapter = new SimpleAdapter(
					getApplicationContext(), listItems,
					R.layout.singleexpression, new String[] { "image" },
					new int[] { R.id.image });
			gView2.setAdapter(simpleAdapter);
			gView2.setOnItemClickListener(this);
			break;
		case 2:
			List<Map<String, Object>> listItems1 = new ArrayList<Map<String, Object>>();
			// 生成24个表情
			for (int i = 0; i < 24; i++) {
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("image", expressionImages3[i]);
				listItems1.add(listItem);
			}

			SimpleAdapter simpleAdapter1 = new SimpleAdapter(
					getApplicationContext(), listItems1,
					R.layout.singleexpression, new String[] { "image" },
					new int[] { R.id.image });
			gView3.setAdapter(simpleAdapter1);
			gView3.setOnItemClickListener(this);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == gView1) {
			editText.insertGif(expressionImageNames1[position].substring(1, expressionImageNames1[position].length() - 1));
		} else if (parent == gView2) {
			editText.insertGif(expressionImageNames2[position].substring(1, expressionImageNames2[position].length() - 1));
		} else if (parent == gView3) {
			editText.insertGif(expressionImageNames3[position].substring(1, expressionImageNames3[position].length() - 1));
		}
		
		textView.insertGif(editText.getText().toString());
		
		dialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		if (v == btnGif) {
			dialog.show();
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}
}
