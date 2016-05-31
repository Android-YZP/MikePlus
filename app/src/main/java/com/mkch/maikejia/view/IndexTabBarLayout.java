package com.mkch.maikejia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class IndexTabBarLayout extends LinearLayout {
	//tabbar的回调接口
	public interface IIndexTabBarCallBackListener{
		public void clickItem(int id);//按了某一项后
	}
	IIndexTabBarCallBackListener indexTabBarCallBackListener=null;
	public void setOnItemClickListener(IIndexTabBarCallBackListener indexTabBarCallBackListener){
		this.indexTabBarCallBackListener = indexTabBarCallBackListener;
	}
	
	RelativeLayout mHomeLayout;
	RelativeLayout mCatesLayout;
	RelativeLayout mCitizensLayout;
	RelativeLayout mPsCenterLayout;
	
	LayoutInflater inflater;
	
	private final static int FLAG_HOME=0;
	private final static int FLAG_FIND=1;
	private final static int FLAG_MKF=2;
	private final static int FLAG_PSCENTER=3;
	
	public IndexTabBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	private void initView() {
		inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.layout_index_tabbar, this);
		findView(view);
		initData();
		setListener();
	}
	
	/**
	 * 找到所有layout控件
	 * @param view
	 */
	private void findView(View view) {
		mHomeLayout = (RelativeLayout)view.findViewById(R.id.index_home_item);
		mCatesLayout = (RelativeLayout)view.findViewById(R.id.index_cates_item);
		mCitizensLayout = (RelativeLayout)view.findViewById(R.id.index_citizens_item);
		mPsCenterLayout = (RelativeLayout)view.findViewById(R.id.index_pscenter_item);
		
	}

	/**
	 * 初始化底部每个item的数据
	 */
	private void initData() {
		//首页
		mHomeLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_home_sel);
		TextView _tvHome = (TextView)mHomeLayout.findViewById(R.id.tv_index_tab_item_title);
		_tvHome.setText("首页");
		_tvHome.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
		//发现
		mCatesLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_find);
		TextView _tvCates = (TextView)mCatesLayout.findViewById(R.id.tv_index_tab_item_title);
		_tvCates.setText("发现");
		_tvCates.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
		//麦客风
		mCitizensLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_mkf);
		TextView _tvCitizens = (TextView)mCitizensLayout.findViewById(R.id.tv_index_tab_item_title);
		_tvCitizens.setText("麦客风");
		_tvCitizens.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
		//我的
		mPsCenterLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_user);
		TextView _tvPsCenter = (TextView)mPsCenterLayout.findViewById(R.id.tv_index_tab_item_title);
		_tvPsCenter.setText("我的");
		_tvPsCenter.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
	}
	
	/**
	 * 点击事件监听
	 */
	private void setListener() {
		mHomeLayout.setOnClickListener(new MyItemClickListener());
		mCatesLayout.setOnClickListener(new MyItemClickListener());
		mCitizensLayout.setOnClickListener(new MyItemClickListener());
		mPsCenterLayout.setOnClickListener(new MyItemClickListener());
	}
	
	private class MyItemClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.index_home_item:
				//1-改变图片;改变文字的颜色;改变layout的背景
				changeTabBarItems(FLAG_HOME);
				//2-实现页面的切换
				break;
			case R.id.index_cates_item:
				changeTabBarItems(FLAG_FIND);
				break;
			case R.id.index_citizens_item:
				changeTabBarItems(FLAG_MKF);
				break;
			case R.id.index_pscenter_item:
				changeTabBarItems(FLAG_PSCENTER);
				break;
			}
			if(indexTabBarCallBackListener!=null){
				indexTabBarCallBackListener.clickItem(view.getId());
			}
		}

		
		
	}
	
	/**
	 * 点击某个tabitem，切换以下内容：改变图片;改变文字的颜色;改变layout的背景
	 * @param index 索引值
	 */
	public void changeTabBarItems(int index) {
		TextView _tvHome;
		TextView _tvCates;
		TextView _tvCitizens;
		TextView _tvPsCenter;
		
		switch (index) {
		case FLAG_HOME:
			//首页
			mHomeLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_home_sel);
			_tvHome = (TextView)mHomeLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvHome.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
			//发现
			mCatesLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_find);
			_tvCates = (TextView)mCatesLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvCates.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			//麦客风
			mCitizensLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_mkf);
			_tvCitizens = (TextView)mCitizensLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvCitizens.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			//我的
			mPsCenterLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_user);
			_tvPsCenter = (TextView)mPsCenterLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvPsCenter.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			break;
		case FLAG_FIND:
			//首页
			mHomeLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_home);
			_tvHome = (TextView)mHomeLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvHome.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			
			//发现
			mCatesLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_find_sel);
			_tvCates = (TextView)mCatesLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvCates.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
			
			//麦客风
			mCitizensLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_mkf);
			_tvCitizens = (TextView)mCitizensLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvCitizens.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			
			//我的
			mPsCenterLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_user);
			_tvPsCenter = (TextView)mPsCenterLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvPsCenter.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			
			break;
		case FLAG_MKF:
			//首页
			mHomeLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_home);
			_tvHome = (TextView)mHomeLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvHome.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			
			//发现
			mCatesLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_find);
			_tvCates = (TextView)mCatesLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvCates.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			//麦客风
			mCitizensLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_mkf_sel);
			_tvCitizens = (TextView)mCitizensLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvCitizens.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
			//我的
			mPsCenterLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_user);
			_tvPsCenter = (TextView)mPsCenterLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvPsCenter.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			break;
		case FLAG_PSCENTER:
			//首页
			mHomeLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_home);
			_tvHome = (TextView)mHomeLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvHome.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			
			//发现
			mCatesLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_find);
			_tvCates = (TextView)mCatesLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvCates.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			//麦客风
			mCitizensLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_mkf);
			_tvCitizens = (TextView)mCitizensLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvCitizens.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
			//我的
			mPsCenterLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.bottom_btn_user_sel);
			_tvPsCenter = (TextView)mPsCenterLayout.findViewById(R.id.tv_index_tab_item_title);
			_tvPsCenter.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
			break;
		}
	}
	
}
