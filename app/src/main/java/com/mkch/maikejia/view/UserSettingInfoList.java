package com.mkch.maikejia.view;

import com.mkch.maikejia.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class UserSettingInfoList extends LinearLayout {
	public interface IUserSettingInfoCallBackListener{
		public abstract void clickItem(int viewid);
	}
	
	private IUserSettingInfoCallBackListener mUserSettingInfoCallBackListener = null;
	public void setOnItemClickListener(IUserSettingInfoCallBackListener mUserSettingInfoCallBackListener){
		this.mUserSettingInfoCallBackListener = mUserSettingInfoCallBackListener;
	}

	private LayoutInflater mLayoutInflater;
	private UserSettingInfoListItem mItemPsInfo;
	private UserSettingInfoListItem mItemUppwd;
	private UserSettingInfoListItem mItemSecurity;
	
	
	public UserSettingInfoList(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		mLayoutInflater = LayoutInflater.from(getContext());
		View view = mLayoutInflater.inflate(R.layout.layout_user_setting_info_list, this);
		
		mItemPsInfo = (UserSettingInfoListItem)view.findViewById(R.id.customview_user_setting_list_item_psinfo);
		mItemUppwd = (UserSettingInfoListItem)view.findViewById(R.id.customview_user_setting_list_item_uppwd);
		mItemSecurity = (UserSettingInfoListItem)view.findViewById(R.id.customview_user_setting_list_item_security);
		
		initData();
		setListener();
	}

	private void initData() {
		mItemPsInfo.setData(R.drawable.pscenter_user_setting_gerenziliao, "个人资料");
		mItemUppwd.setData(R.drawable.pscenter_user_setting_xiugaimima, "修改密码");
		mItemSecurity.setData(R.drawable.pscenter_user_setting_anquanzx, "安全中心");
	}

	private void setListener() {
		// TODO Auto-generated method stub
		mItemPsInfo.setOnClickListener(new UserSettingItemOnClickListener());
		mItemUppwd.setOnClickListener(new UserSettingItemOnClickListener());
		mItemSecurity.setOnClickListener(new UserSettingItemOnClickListener());
	}

	private class UserSettingItemOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			mUserSettingInfoCallBackListener.clickItem(view.getId());
		}
		
	}
}
