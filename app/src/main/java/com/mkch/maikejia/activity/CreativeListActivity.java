package com.mkch.maikejia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkch.maikejia.R;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.fragment.CreativeListChildFragment;
import com.mkch.maikejia.fragment.CreativeListDigFragment;
import com.mkch.maikejia.fragment.CreativeListEduFragment;
import com.mkch.maikejia.fragment.CreativeListHealthyFragment;
import com.mkch.maikejia.fragment.CreativeListHouseFragment;
import com.mkch.maikejia.fragment.CreativeListKitchenFragment;
import com.mkch.maikejia.fragment.CreativeListOtherFragment;
import com.mkch.maikejia.fragment.CreativeListRecreationFragment;
import com.mkch.maikejia.fragment.CreativeListTripFragment;
import com.mkch.maikejia.view.CreativeListTabBarLayout;
import com.mkch.maikejia.view.CreativeListTabBarLayout.ICreativeListTabBarCallBackListener;

public class CreativeListActivity extends FragmentActivity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	private CreativeListTabBarLayout mCreativeListTabBarLayout;//整个顶部tabbar控件
	
	private final static int FLAG_ITEM_0 = 0;
	private final static int FLAG_ITEM_1 = 1;
	private final static int FLAG_ITEM_2 = 2;
	private final static int FLAG_ITEM_3 = 3;
	private final static int FLAG_ITEM_4 = 4;
	private final static int FLAG_ITEM_5 = 5;
	private final static int FLAG_ITEM_6 = 6;
	private final static int FLAG_ITEM_7 = 7;
	private final static int FLAG_ITEM_8 = 8;
	
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_creative_list);
		initView();
		setListener();
	}

	private void initView() {
		findView();
		initData();
	}

	private void findView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		
		mCreativeListTabBarLayout = (CreativeListTabBarLayout)findViewById(R.id.view_creative_list_tabbar);
		mViewPager = (ViewPager)findViewById(R.id.view_pager_creative_list);
	}
	
	private void initData() {
		mTvTitle.setText("创意");
		
		mViewPager.setAdapter(new CreativeListFragmentPagerAdapter(getSupportFragmentManager()));
		//获取传来的数据;并设置当前选中项
		Bundle _bundle = getIntent().getExtras();
		int position = 0;
		if(_bundle!=null){
			position = _bundle.getInt("position");
		}
		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_click_cate_position", position+"");
		mViewPager.setCurrentItem(position);
		mCreativeListTabBarLayout.changeTabBarItems(position);
//		mCreativeListTabBarLayout.setTabBarPosition(position);
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				CreativeListActivity.this.finish();
			}
		});
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
					
					@Override
					public void onPageSelected(int position) {
						mCreativeListTabBarLayout.changeTabBarItems(position);
//						mCreativeListTabBarLayout.setTabBarPosition(position);
					}
					
					@Override
					public void onPageScrolled(int postion, float percent, int pxLocation) {
						
					}
					
					@Override
					public void onPageScrollStateChanged(int position) {
						
					}
				});
		
		
		mCreativeListTabBarLayout.setOnItemClickListener(new ICreativeListTabBarCallBackListener() {
			@Override
			public void clickItem(int id) {
				switch (id) {
				case R.id.find_creative_item0:
					mViewPager.setCurrentItem(FLAG_ITEM_0);//点击后设置当前页是主页
					break;
				case R.id.find_creative_item1:
					mViewPager.setCurrentItem(FLAG_ITEM_1);
					break;
				case R.id.find_creative_item2:
					mViewPager.setCurrentItem(FLAG_ITEM_2);
					break;
				case R.id.find_creative_item3:
					mViewPager.setCurrentItem(FLAG_ITEM_3);
					break;
				case R.id.find_creative_item4:
					mViewPager.setCurrentItem(FLAG_ITEM_4);
					break;
				case R.id.find_creative_item5:
					mViewPager.setCurrentItem(FLAG_ITEM_5);
					break;
				case R.id.find_creative_item6:
					mViewPager.setCurrentItem(FLAG_ITEM_6);
					break;
				case R.id.find_creative_item7:
					mViewPager.setCurrentItem(FLAG_ITEM_7);
					break;
				case R.id.find_creative_item8:
					mViewPager.setCurrentItem(FLAG_ITEM_8);
					break;
					
				}
			}
		});
	}
	
	/**
	 * 自定义ViewPager的适配器
	 * @author JLJ
	 *
	 */
	private class CreativeListFragmentPagerAdapter extends FragmentPagerAdapter{
		
		public CreativeListFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int postion) {
			switch (postion) {
			case FLAG_ITEM_0:
//				getIntent().putExtra("cate2", 2);
				return new CreativeListEduFragment();
			case FLAG_ITEM_1:
//				getIntent().putExtra("cate3", 3);
				return new CreativeListKitchenFragment();
			case FLAG_ITEM_2:
//				getIntent().putExtra("cate4", 4);
				return new CreativeListChildFragment();
			case FLAG_ITEM_3:
//				getIntent().putExtra("cate5", 5);
				return new CreativeListDigFragment();
			case FLAG_ITEM_4:
//				getIntent().putExtra("cate6", 6);
				return new CreativeListRecreationFragment();
			case FLAG_ITEM_5:
//				getIntent().putExtra("cate7", 7);
				return new CreativeListHealthyFragment();
			case FLAG_ITEM_6:
//				getIntent().putExtra("cate8", 8);
				return new CreativeListHouseFragment();
			case FLAG_ITEM_7:
//				getIntent().putExtra("cate10", 10);
				return new CreativeListTripFragment();
			case FLAG_ITEM_8:
//				getIntent().putExtra("cate11", 11);
				return new CreativeListOtherFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 9;
		}
		
	}
}
