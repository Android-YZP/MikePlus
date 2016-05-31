package com.mkch.maikejia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mkch.maikejia.R;

public class CreativeDeployTitleActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	private EditText mEtCreativeTitle;
	private EditText mEtCreativeIntro;
	
	
	private Button mBtnNextStep;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creative_deploy_title);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		
		mEtCreativeTitle = (EditText)findViewById(R.id.et_deploy_title);
		mEtCreativeIntro = (EditText)findViewById(R.id.et_deploy_intro);
		
		mBtnNextStep = (Button)findViewById(R.id.btn_deploy_title_next_step);
	}

	private void initData() {
		mTvTitle.setText("发布创意");
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				CreativeDeployTitleActivity.this.finish();
			}
		});
		
		mBtnNextStep.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String _title = "";
				String _intro = "";
				if(mEtCreativeTitle!=null){
					
					_title = mEtCreativeTitle.getText().toString();
					if(_title==null||_title.equals("")){
						Toast.makeText(CreativeDeployTitleActivity.this, "亲，您未填写创意标题", Toast.LENGTH_SHORT).show();
						return;
					}
					
				}
				if(mEtCreativeIntro!=null){
					_intro = mEtCreativeIntro.getText().toString();
					if(_intro==null||_intro.equals("")){
						Toast.makeText(CreativeDeployTitleActivity.this, "亲，您未填写创意简介", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				Intent _intent = new Intent(CreativeDeployTitleActivity.this,CreativeDeployDescActivity.class);
				_intent.putExtra("_title", _title);
				_intent.putExtra("_intro", _intro);
				
				startActivity(_intent);
				
				CreativeDeployTitleActivity.this.finish();
			}
		});
	}
}
