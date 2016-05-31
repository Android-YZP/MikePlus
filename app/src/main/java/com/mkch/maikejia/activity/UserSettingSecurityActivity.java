package com.mkch.maikejia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkch.maikejia.R;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.view.UserSettingInfoListItem;

public class UserSettingSecurityActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	private UserSettingInfoListItem mUserSettingInfoListItemPhoneBind;
	private UserSettingInfoListItem mUserSettingInfoListItemEmailBind;
	private TextView mTvLevel;
	
	private User mUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_setting_security);
		initView();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
		setListener();
	}
	
	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		
		mUserSettingInfoListItemPhoneBind = (UserSettingInfoListItem)findViewById(R.id.customview_user_setting_security_phone_bind);
		mUserSettingInfoListItemEmailBind = (UserSettingInfoListItem)findViewById(R.id.customview_user_setting_security_email_bind);
		mTvLevel = (TextView)findViewById(R.id.tv_user_setting_security_level_info);
	}

	private void initData() {
		mTvTitle.setText("安全中心");
		
		mUser = CommonUtil.getUserInfo(this);
		
		int _level_num = 0;
		//当前是否绑定手机或邮箱
		boolean _has_mobile = mUser.isHasMobile();
		boolean _has_email = mUser.isHasEmail();
		String _is_bind_mobile = "（<font color='#d81759'>已绑定</font>）";
		String _is_bind_email = "（<font color='#d81759'>已绑定</font>）";
		if(!_has_mobile){
			_is_bind_mobile = "（未绑定）";
			mUserSettingInfoListItemPhoneBind.setData(R.drawable.user_setting_bind_phone, "绑定手机"+_is_bind_mobile);
		}else{
			_level_num++;//1
			mUserSettingInfoListItemPhoneBind.setData(R.drawable.user_setting_bind_phone_ybd, "绑定手机"+_is_bind_mobile);
		}
		if(!_has_email){
			_is_bind_email = "（未绑定）";
			mUserSettingInfoListItemEmailBind.setData(R.drawable.user_setting_bind_email, "绑定邮箱"+_is_bind_email);
		}else{
			_level_num++;//2
			mUserSettingInfoListItemEmailBind.setData(R.drawable.user_setting_bind_email_ybd, "绑定邮箱"+_is_bind_email);
		}
		
		
		
		
		String _level_str = null;
		if(_level_num==0){
			_level_str = "低";
		}else if(_level_num==1){
			_level_str = "中";
		}else if (_level_num==2) {
			_level_str = "高";
		}
		mTvLevel.setText(Html.fromHtml("你目前的安全等级为<font color='#d81759'>"+_level_str+"</font>,同时绑定手机和邮箱将会提高安全等级"));
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserSettingSecurityActivity.this.finish();
			}
		});
		//点击了绑定手机
		mUserSettingInfoListItemPhoneBind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_setting_security_mUserSettingInfoListItemPhoneBind", "mUserSettingInfoListItemPhoneBind");
				Intent _intent = new Intent(UserSettingSecurityActivity.this, UserSettingSecurityPhoneActivity.class);
				UserSettingSecurityActivity.this.startActivity(_intent);
			}
		});
		//点击了绑定邮箱
		mUserSettingInfoListItemEmailBind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_setting_security_mUserSettingInfoListItemEmailBind", "mUserSettingInfoListItemEmailBind");
				Intent _intent = new Intent(UserSettingSecurityActivity.this, UserSettingSecurityEmailActivity.class);
				UserSettingSecurityActivity.this.startActivity(_intent);
			}
		});
	}
}
