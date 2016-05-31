package com.mkch.maikejia.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class UserSettingInfoListItem extends LinearLayout {

	private LayoutInflater mLayoutInflater;
	private ImageView mIvItemPic;
	private TextView mTvItemTitle;
	
	public UserSettingInfoListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		mLayoutInflater = LayoutInflater.from(getContext());
		View view = mLayoutInflater.inflate(R.layout.layout_user_setting_info_list_item, this);
		mIvItemPic = (ImageView)view.findViewById(R.id.iv_user_setting_list_item_pic);
		mTvItemTitle = (TextView)view.findViewById(R.id.tv_user_setting_list_item_title);
		
		initData();
		setListener();
	}

	private void initData() {
		// TODO Auto-generated method stub
	}

	private void setListener() {
		// TODO Auto-generated method stub
		
	}

	public void setData(int picRes,String title){
		mIvItemPic.setImageResource(picRes);
		mTvItemTitle.setText(Html.fromHtml(title));
	}
}
