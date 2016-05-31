package com.mkch.maikejia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class UserCreativeListActivity extends FragmentActivity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	private ImageView mIvDeploy;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_creative_list);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_user_creative_list_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_user_creative_list_topbar_title);
		mIvDeploy = (ImageView)findViewById(R.id.iv_user_creative_list_topbar_deploy);
		
	}

	private void initData() {
		mTvTitle.setText("我的创意");
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserCreativeListActivity.this.finish();
			}
		});
		mIvDeploy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent _intent = new Intent(UserCreativeListActivity.this,CreativeDeployTitleActivity.class);
				UserCreativeListActivity.this.startActivity(_intent);
			}
		});
	}
}
