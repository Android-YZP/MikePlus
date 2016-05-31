package com.mkch.maikejia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class CreativeDetailTabBarLayout extends LinearLayout {
	//tabbar的回调接口
	public interface ICreativeDetailTabBarCallBackListener{
		public void clickItem(int id);//按了某一项后
	}
	ICreativeDetailTabBarCallBackListener creativeDetailTabBarCallBackListener=null;
	public void setOnItemClickListener(ICreativeDetailTabBarCallBackListener creativeDetailTabBarCallBackListener){
		this.creativeDetailTabBarCallBackListener = creativeDetailTabBarCallBackListener;
	}
	
	
	LayoutInflater inflater;
	
	private TextView mTvItem0;
	private TextView mTvItem1;
	
	private final static int FLAG_ITEM_0 = 0;
	private final static int FLAG_ITEM_1 = 1;
	
	
	public CreativeDetailTabBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.layout_creative_detail_tabbar, this);
		findView(view);
		initData();
		setListener();
	}

	private void findView(View view) {
		mTvItem0 = (TextView)view.findViewById(R.id.tv_creativedetail_tabbar_detail_info_tab);
		mTvItem1 = (TextView)view.findViewById(R.id.tv_creativedetail_tabbar_comment_tab);
	}

	private void initData() {
		mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
		mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
	}

	private void setListener() {
		mTvItem0.setOnClickListener(new CreativeDetailInfoItemClickListener());
		mTvItem1.setOnClickListener(new CreativeDetailInfoItemClickListener());
	}

	private class CreativeDetailInfoItemClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.tv_creativedetail_tabbar_detail_info_tab:
				//1-改变文字的颜色;
				changeTabBarItems(FLAG_ITEM_0);
				//2-实现页面的切换
				break;
			case R.id.tv_creativedetail_tabbar_comment_tab:
				changeTabBarItems(FLAG_ITEM_1);
				break;
			}
			//2-实现页面的切换
			if(creativeDetailTabBarCallBackListener!=null){
				creativeDetailTabBarCallBackListener.clickItem(view.getId());
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
}
