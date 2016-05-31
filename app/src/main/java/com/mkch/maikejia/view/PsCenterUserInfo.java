package com.mkch.maikejia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.mkch.maikejia.R;
import com.mkch.maikejia.config.CommonConstants;
/**
 * 个人中心
 * @author JLJ
 *
 */
public class PsCenterUserInfo extends LinearLayout {

	private LayoutInflater mLayoutInflater;
	
	public PsCenterUserInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		mLayoutInflater = LayoutInflater.from(getContext());
		View view = mLayoutInflater.inflate(R.layout.layout_pscenter_user_info, this);
		
		initData();
		setListener();
	}

	private void initData() {
		
	}
	
	private void setListener() {
		
	}
	

}
