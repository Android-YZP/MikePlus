package com.mkch.maikejia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkch.maikejia.R;
import com.mkch.maikejia.fragment.UserAttentionCreativeListFragment;
import com.mkch.maikejia.fragment.UserAttentionUserListFragment;
import com.mkch.maikejia.view.UserAttentionTabBarLayout;
import com.mkch.maikejia.view.UserAttentionTabBarLayout.IUserAttentionTabBarCallBackListener;

public class UserAttentionActivity extends FragmentActivity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	private UserAttentionTabBarLayout mUserAttentionTabBarLayout;
	private ViewPager mViewPagerUserAttention;
	
	private final static int FLAG_ITEM_0 = 0;
	private final static int FLAG_ITEM_1 = 1;
	
	//下划线
	private View tabUnderLine;
	//当前页面
	private int currentIndex;
	//屏幕宽度
	private int screenWidth;
	//页面总个数
	private int fragSize=2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_attention);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		
		mUserAttentionTabBarLayout = (UserAttentionTabBarLayout)findViewById(R.id.custom_user_attention_tabbar);
		mViewPagerUserAttention = (ViewPager)findViewById(R.id.viewPager_user_attention);
		//初始化屏幕宽度
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screenWidth = outMetrics.widthPixels;
		//初始化tab选中后的下划线
		initTabUnderLine();
	}

	private void initData() {
		mTvTitle.setText("我的关注");
		mViewPagerUserAttention.setAdapter(new UserAttentionFragmentPagerAdapter(getSupportFragmentManager()));
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserAttentionActivity.this.finish();
			}
		});
		mUserAttentionTabBarLayout.setOnItemClickListener(new IUserAttentionTabBarCallBackListener() {
			@Override
			public void clickItem(int id) {
				switch (id) {
				case R.id.user_attention_item0:
					mViewPagerUserAttention.setCurrentItem(FLAG_ITEM_0);//点击后设置当前页是显示页
					break;
				case R.id.user_attention_item1:
					mViewPagerUserAttention.setCurrentItem(FLAG_ITEM_1);
					break;
				}
			}
		});
		
		mViewPagerUserAttention.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				mUserAttentionTabBarLayout.changeTabBarItems(position);
				currentIndex = position;//当前页
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				//从左到右
				if(currentIndex == position) {
					LinearLayout.LayoutParams layoutParam = (android.widget.LinearLayout.LayoutParams) tabUnderLine
							.getLayoutParams();
					layoutParam.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / fragSize) + currentIndex * (screenWidth / fragSize));
					tabUnderLine.setLayoutParams(layoutParam);
				}
				//从右到左
				else if(currentIndex > position){
					LinearLayout.LayoutParams layoutParam = (android.widget.LinearLayout.LayoutParams) tabUnderLine
							.getLayoutParams();
					layoutParam.leftMargin = (int) (-(1-positionOffset) * (screenWidth * 1.0 / fragSize) + currentIndex * (screenWidth / fragSize));
					tabUnderLine.setLayoutParams(layoutParam);
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int position) {
				
			}
		});
	}
	
	/**
	 * 自定义ViewPager的适配器
	 * @author JLJ
	 *
	 */
	private class UserAttentionFragmentPagerAdapter extends FragmentPagerAdapter{
		
		public UserAttentionFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int postion) {
			switch (postion) {
			case FLAG_ITEM_0:
				return new UserAttentionCreativeListFragment();
			case FLAG_ITEM_1:
				return new UserAttentionUserListFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}
		
	}
	
	//初始化tab下划线
	private void initTabUnderLine()
	{
		tabUnderLine = (View)findViewById(R.id.user_attention_tab_under_line);
		LinearLayout.LayoutParams layoutParam = (android.widget.LinearLayout.LayoutParams) tabUnderLine.getLayoutParams();
		layoutParam.width = screenWidth / fragSize;
		tabUnderLine.setLayoutParams(layoutParam);
	}
}
