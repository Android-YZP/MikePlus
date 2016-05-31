package com.mkch.maikejia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;

import com.mkch.maikejia.R;
import com.mkch.maikejia.fragment.FindFragment;
import com.mkch.maikejia.fragment.HomeFragment;
import com.mkch.maikejia.fragment.MkfFragment;
import com.mkch.maikejia.fragment.PsCenterFragment;
import com.mkch.maikejia.view.IndexTabBarLayout;
import com.mkch.maikejia.view.IndexTabBarLayout.IIndexTabBarCallBackListener;

public class IndexActivity extends FragmentActivity {

	private IndexTabBarLayout mIndexTabBarLayout;//底部整个控件
	private final static int FLAG_HOME=0;
	private final static int FLAG_CATES=1;
	private final static int FLAG_CITIZENS=2;
	private final static int FLAG_PSCENTER=3;
	
	private ViewPager mViewPager;
	
	private int CACHE_PAGES = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		initView();
		setListener();
	}
	
	

	/**
	 * 初始化界面
	 */
	private void initView() {
//		initPageContent(new HomeFragment());
		findView();
	}

	/**
	 * 初始化第一页初始页(动态切换页面)
	 */
/*	private void initPageContent(Fragment fragment) {
		FragmentManager _manager = getFragmentManager();
		FragmentTransaction _ft = _manager.beginTransaction();
		_ft.replace(R.id.myContent, fragment);
		_ft.commit();
	}*/



	/**
	 * 找到所有视图
	 */
	private void findView() {
		mIndexTabBarLayout=(IndexTabBarLayout)findViewById(R.id.myIndexTabBarLayout);
		mViewPager = (ViewPager)findViewById(R.id.myViewPager);
		mViewPager.setOffscreenPageLimit(CACHE_PAGES);//设置预加载界面数量
		mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				mIndexTabBarLayout.changeTabBarItems(position);
			}
			
			@Override
			public void onPageScrolled(int postion, float percent, int pxLocation) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int position) {
				
			}
		});
	}

	/**
	 * 设置点击切换界面监听
	 */
	private void setListener() {
		mIndexTabBarLayout.setOnItemClickListener(new IIndexTabBarCallBackListener() {
			@Override
			public void clickItem(int id) {
				switch (id) {
				case R.id.index_home_item:
//					initPageContent(new HomeFragment());
					mViewPager.setCurrentItem(FLAG_HOME);//点击后设置当前页是主页
					break;
				case R.id.index_cates_item:
//					initPageContent(new CatesFragment());
					mViewPager.setCurrentItem(FLAG_CATES);
					break;
				case R.id.index_citizens_item:
//					initPageContent(new CitizensFragment());
					mViewPager.setCurrentItem(FLAG_CITIZENS);
					break;
				case R.id.index_pscenter_item:
//					initPageContent(new PsCenterFragment());
					mViewPager.setCurrentItem(FLAG_PSCENTER);
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
	private class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		
		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int postion) {
			switch (postion) {
			case FLAG_HOME:
				return new HomeFragment();
			case FLAG_CATES:
				return new FindFragment();
			case FLAG_CITIZENS:
				return new MkfFragment();
			case FLAG_PSCENTER:
				return new PsCenterFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 4;
		}
		
	}
	
	public void showTip(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	private long exitTime=0;
	/**
	 * 第二次点击返回，退出
	 */
	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {  
            // ToastUtil.makeToastInBottom("再按一次退出应用", MainMyselfActivity);  
            Toast.makeText(IndexActivity.this, "再按一次退出麦客加", Toast.LENGTH_SHORT).show();  
            exitTime = System.currentTimeMillis();  
            return;  
        }  
		IndexActivity.this.finish();
		super.onBackPressed();
	}
}
