package com.mkch.maikejia.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mkch.maikejia.R;
import com.mkch.maikejia.view.MkfTabBarLayout;
import com.mkch.maikejia.view.MkfTabBarLayout.IMkfTabBarCallBackListener;

public class MkfFragment extends Fragment {

	private MkfTabBarLayout mMkfTabBarLayout;
	private ViewPager mViewPagerMkf;
	
	private final static int FLAG_ITEM_0 = 0;
	private final static int FLAG_ITEM_1 = 1;
	private final static int FLAG_ITEM_2 = 2;
	
	//下划线
	private View tabUnderLine;
	//当前页面
	private int currentIndex;
	//屏幕宽度
	private int screenWidth;
	//页面总个数
	private int fragSize=3;
	//设置预加载界面数量
	private int CACHE_PAGES=2;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mkf, container, false);
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		mMkfTabBarLayout = (MkfTabBarLayout)view.findViewById(R.id.custom_mkf_tabbar);
		mViewPagerMkf = (ViewPager)view.findViewById(R.id.viewPager_mkf);
		mViewPagerMkf.setOffscreenPageLimit(CACHE_PAGES);//设置预加载界面数量
		//初始化屏幕宽度
		DisplayMetrics outMetrics = new DisplayMetrics();
		if (getActivity()!=null) {
			getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
			screenWidth = outMetrics.widthPixels;
		}
		//初始化tab选中后的下划线
		initTabUnderLine(view);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		setListener();
	}

	private void initData() {
		if (getActivity()!=null) {
			mViewPagerMkf.setAdapter(new MkfFragmentPagerAdapter(getActivity().getSupportFragmentManager()));
		}
		
	}

	private void setListener() {
		mMkfTabBarLayout.setOnItemClickListener(new IMkfTabBarCallBackListener() {
			@Override
			public void clickItem(int id) {
				switch (id) {
				case R.id.mkf_item0:
					mViewPagerMkf.setCurrentItem(FLAG_ITEM_0);//点击后设置当前页是显示页
					break;
				case R.id.mkf_item1:
					mViewPagerMkf.setCurrentItem(FLAG_ITEM_1);
					break;
				case R.id.mkf_item2:
					mViewPagerMkf.setCurrentItem(FLAG_ITEM_2);
					break;
				}
			}
		});
		
		mViewPagerMkf.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				mMkfTabBarLayout.changeTabBarItems(position);
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
	private class MkfFragmentPagerAdapter extends FragmentPagerAdapter{
		
		public MkfFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int postion) {
			switch (postion) {
			case FLAG_ITEM_0:
				return new MkfLastNewsFragment();
			case FLAG_ITEM_1:
				return new MkfMksNewsFragment();
			case FLAG_ITEM_2:
				return new MkfCybNewsFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}
		
	}
	
	//初始化tab下划线
	private void initTabUnderLine(View view)
	{
		tabUnderLine = (View)view.findViewById(R.id.tab_under_line);
		LinearLayout.LayoutParams layoutParam = (android.widget.LinearLayout.LayoutParams) tabUnderLine.getLayoutParams();
		layoutParam.width = screenWidth / fragSize;
		tabUnderLine.setLayoutParams(layoutParam);
	}
}
