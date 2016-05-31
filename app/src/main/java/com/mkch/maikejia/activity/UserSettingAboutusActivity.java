package com.mkch.maikejia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class UserSettingAboutusActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_setting_aboutus);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		
	}

	private void initData() {
		mTvTitle.setText("关于我们");
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserSettingAboutusActivity.this.finish();
			}
		});
		
	}
}
