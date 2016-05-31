package com.mkch.maikejia.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mkch.maikejia.R;

public class AdScrollLayout extends LinearLayout {

	private ViewPager mViewPagerAdScroll;
//	private ImageView mIvPoint1;
//	private ImageView mIvPoint2;
//	private ImageView mIvPoint3;
	private LinearLayout mLineAdScrollPoints;
	
	private Timer mTimer;
	private TimerTask mTimerTask;
	private static int index = 0;//初始化0位第一页
	private static int size = 0;//轮播数量,原本是3
	public AdScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		setListener();
	}

	
	/**
	 * 初始化界面
	 */
	private void initView() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view=inflater.inflate(R.layout.ad_scroll, this);
		findView(view);
		
	}

	/**
	 * 找控件初始化
	 */
	private void findView(View view) {
		mViewPagerAdScroll = (ViewPager)view.findViewById(R.id.view_pager_ad_scroll);
//		ImageView mIvPoint1 = (ImageView)view.findViewById(R.id.iv_ad_scroll_point1);
//		ImageView mIvPoint2 = (ImageView)view.findViewById(R.id.iv_ad_scroll_point2);
//		ImageView mIvPoint3 = (ImageView)view.findViewById(R.id.iv_ad_scroll_point3);
		mLineAdScrollPoints = (LinearLayout)view.findViewById(R.id.line_ad_scroll_points);
		
	}
	
	
	/**
	 * 设置ViewPager滑动监听
	 */
	private void setListener() {
		mViewPagerAdScroll.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
//				Log.d("jlj-maikejia-position", position+",");
				if(position>=size){
					position = 0;
				}
				index = position;
				notifyUpdatePic();
			}
			
			@Override
			public void onPageScrolled(int position, float percent, int pxlocation) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int position) {
				
			}
		});
	}
	
	/**
	 * 按一定时间自动轮播定时器来切换ViewPager的图片
	 * @param delayTime 延迟时间
	 */
	public void setPageFromTime(int delayTime){
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
//				initPic(index);//这里需要更新界面的地方在主线程handler中实现
				//handler发送消息
				notifyUpdatePic();
				index++;//切换显示下一页
				if(index>=size){
					index=0;
				}
				
			}

			
		};
		mTimer.schedule(mTimerTask, delayTime,delayTime);//多久之后，每隔多久执行
	}
	
	/**
	 * 停止定时器
	 */
	public void cancelPageFromTime(){
		if(mTimer!=null){
			mTimer.cancel();
			
		}
	}
	//handler处理消息
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				int _index = msg.getData().getInt("index");
				initPic(_index);
				break;

			}
		}
		
	};
	
	/**
	 * 更新界面修改图片-发送数据
	 * @param index 索引页数
	 */
	private void notifyUpdatePic() {
		Log.d("jlj-maikejia-index", index+",");
		Bundle _bundle = new Bundle();
		_bundle.putInt("index", index);
		Message _msg = new Message();
		_msg.setData(_bundle);
		_msg.what=100;
		mHandler.sendMessage(_msg);
	}
	
	/**
	 * 更新界面
	 * @param index 界面索引值
	 */
	private void initPic(int index) {
		//1-更新界面
		//2-更新下面点的显示位置
		mViewPagerAdScroll.setCurrentItem(index);
//		switch (index) {
//		case 0://表示当前的第一页
//			mIvPoint1.setBackgroundResource(R.drawable.banner_circle_sel);
//			mIvPoint2.setBackgroundResource(R.drawable.banner_circle_nor);
//			mIvPoint3.setBackgroundResource(R.drawable.banner_circle_nor);
//			break;
//		case 1:
//			mIvPoint1.setBackgroundResource(R.drawable.banner_circle_nor);
//			mIvPoint2.setBackgroundResource(R.drawable.banner_circle_sel);
//			mIvPoint3.setBackgroundResource(R.drawable.banner_circle_nor);
//			break;
//		case 2:
//			mIvPoint1.setBackgroundResource(R.drawable.banner_circle_nor);
//			mIvPoint2.setBackgroundResource(R.drawable.banner_circle_nor);
//			mIvPoint3.setBackgroundResource(R.drawable.banner_circle_sel);
//			break;
//		}
		
		for (int i = 0; i < size; i++) {
			if(mLineAdScrollPoints.getChildAt(i)!=null){
				if(index==i){
					mLineAdScrollPoints.getChildAt(i).setBackgroundResource(R.drawable.banner_circle_sel);
				}else{
					mLineAdScrollPoints.getChildAt(i).setBackgroundResource(R.drawable.banner_circle_nor);
				}
			}
		}
	}

	/**
	 * 获取本组合控件中的ViewPager控件
	 * @return
	 */
	public ViewPager getViewPager(){
		return mViewPagerAdScroll;
	}


	public void setSize(int size) {
		this.size = size;
		if(size>0){
			mLineAdScrollPoints.removeAllViews();
		}
		for (int i = 0; i < size; i++) {
			ImageView iv = new ImageView(getContext());
			int width_or_height = (int)getResources().getDimension(R.dimen.index_banner_cicycle_width_or_height);
			LayoutParams layoutParams= new LayoutParams(width_or_height, width_or_height);
			int margin_left_or_right = (int)getResources().getDimension(R.dimen.index_banner_cicycle_margin_left_or_right);
			layoutParams.setMargins(margin_left_or_right, 0, margin_left_or_right, 0);
			iv.setLayoutParams(layoutParams);
			if(i==0){
				iv.setBackgroundResource(R.drawable.banner_circle_sel);
			}else{
				iv.setBackgroundResource(R.drawable.banner_circle_nor);
			}
			mLineAdScrollPoints.addView(iv);
		}
	}
	
	
}
