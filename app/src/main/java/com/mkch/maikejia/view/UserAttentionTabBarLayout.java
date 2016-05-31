package com.mkch.maikejia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class UserAttentionTabBarLayout extends LinearLayout {
	//tabbar的回调接口
	public interface IUserAttentionTabBarCallBackListener{
		public void clickItem(int id);//按了某一项后
	}
	IUserAttentionTabBarCallBackListener mUserAttentionTabBarCallBackListener=null;
	public void setOnItemClickListener(IUserAttentionTabBarCallBackListener mUserAttentionTabBarCallBackListener){
		this.mUserAttentionTabBarCallBackListener = mUserAttentionTabBarCallBackListener;
	}
	
	
	LayoutInflater inflater;
	
	private TextView mTvItem0;
	private TextView mTvItem1;
	
	private final static int FLAG_ITEM_0 = 0;
	private final static int FLAG_ITEM_1 = 1;
	
	
	
	public UserAttentionTabBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.layout_user_attention_tabbar, this);
		findView(view);
		initData();
		setListener();
	}

	private void findView(View view) {
		mTvItem0 = (TextView)view.findViewById(R.id.user_attention_item0);
		mTvItem1 = (TextView)view.findViewById(R.id.user_attention_item1);
		
	}

	private void initData() {
		mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
		mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
	}

	private void setListener() {
		mTvItem0.setOnClickListener(new CreativeListItemClickListener());
		mTvItem1.setOnClickListener(new CreativeListItemClickListener());
	}

	private class CreativeListItemClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.user_attention_item0:
				//1-改变文字的颜色;
				changeTabBarItems(FLAG_ITEM_0);
				//2-实现页面的切换
				break;
			case R.id.user_attention_item1:
				changeTabBarItems(FLAG_ITEM_1);
				break;
			}
			//2-实现页面的切换
			if(mUserAttentionTabBarCallBackListener!=null){
				mUserAttentionTabBarCallBackListener.clickItem(view.getId());
			}
			
		}

		
	}
	
	public void changeTabBarItems(int index) {
		switch (index) {
		case FLAG_ITEM_0:
			mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
			mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			break;
		case FLAG_ITEM_1:
			mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
			break;
		}
	}
	
	/**
	 * 设置数据
	 * @param titleItem1
	 * @param titleItem2
	 */
	public void setData(String titleItem1,String titleItem2){
		mTvItem0.setText(titleItem1);
		mTvItem1.setText(titleItem2);
	}
}
