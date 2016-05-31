package com.mkch.maikejia.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mkch.maikejia.R;
import com.mkch.maikejia.adapter.ViewPagerAdapter;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.DataCleanManager2;

/**
 * @author yangyu
 *	功能描述：主程序入口类
 */
public class MainActivity extends Activity implements OnClickListener,OnPageChangeListener {
	//定义ViewPager对象
	private ViewPager viewPager;
	
	//定义ViewPager适配器
	private ViewPagerAdapter vpAdapter;
	
	//定义一个ArrayList来存放View
	private ArrayList<View> views;

	//引导图片资源
    private static final int[] pics = {R.drawable.guide1,R.drawable.guide2,R.drawable.guide3};
    
    //底部小点的图片
    private ImageView[] points;
    
    //记录当前选中位置
    private int currentIndex;
    
  	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(isFirstInstall()){
			int userversion = 0;//标记-没安装过
			goGuaidPages(userversion);//去引导页
			CommonUtil.createShortCut(this);
		}else{
			int userversion = this.getFirstInstallUserVersion();
			
//			//如果发现用户不是首次安装
			//当前是最新版，就不用清除数据
			int oldversioncode = this.getOldVersionCode();
			int currentCode = CommonUtil.getAppVersion(this).getVersionCode();
			Log.d("_userversion", currentCode+"-"+oldversioncode);
			if(currentCode>oldversioncode){
				userversion = 1;//覆盖安装，已经安装
			}
			
			//说明覆盖安装
			if(userversion!=0){
				Log.i("_userversion_fugai_clean", "clean");
				DataCleanManager2.cleanApplicationData(MainActivity.this, new String[0]);
				userversion=0;//清0
				goGuaidPages(userversion);//去引导页
				return;
			}
			
			
			//立即跳转到首页
			Intent _intent = new Intent(MainActivity.this, IndexActivity.class);
			MainActivity.this.startActivity(_intent);
			MainActivity.this.finish();
		}
		
	}

	private int getOldVersionCode() {
		SharedPreferences _SP = getSharedPreferences("firstInstall", MODE_PRIVATE);
  		int userversion = _SP.getInt("versioncode", 0);
  		return userversion;
	}

	private void goGuaidPages(int userversion) {
		setContentView(R.layout.activity_main);
		initView();
		initData();	
		this.saveFirstInstall(userversion);
	}


	/**
	 * 初始化组件
	 */
	private void initView(){
		//实例化ArrayList对象
		views = new ArrayList<View>();
		
		//实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		
		//实例化ViewPager适配器
		vpAdapter = new ViewPagerAdapter(views);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		//定义一个布局并设置参数
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                														  LinearLayout.LayoutParams.MATCH_PARENT);
       
        //初始化引导图片列表
        for(int i=0; i<pics.length; i++) {
        	if(i==2){
        		RelativeLayout relaLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.layout_guaid_end, null);
        		Button btnGuaidEnd = (Button) relaLayout.findViewById(R.id.btn_guaid_end);
        		btnGuaidEnd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent _intent = new Intent(MainActivity.this, IndexActivity.class);
						MainActivity.this.startActivity(_intent);
						MainActivity.this.finish();
					}
				});
        		views.add(relaLayout);
        	}else{
        		ImageView iv = new ImageView(this);
                iv.setLayoutParams(mParams);
                iv.setBackgroundResource(pics[i]);
//              iv.setImageResource(pics[i]);
                views.add(iv);
        	}
            
        } 
        
        //设置数据
        viewPager.setAdapter(vpAdapter);
        //设置监听
        viewPager.setOnPageChangeListener(this);
        
        //初始化底部小点
        initPoint();
	}
	
	
	/**
	 * 初始化底部小点
	 */
	private void initPoint(){
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);       
		
        points = new ImageView[pics.length];

        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
        	//得到一个LinearLayout下面的每一个子元素
        	points[i] = (ImageView) linearLayout.getChildAt(i);
        	//默认都设为灰色
        	points[i].setEnabled(true);
        	//给每个小点设置监听
        	points[i].setOnClickListener(this);
        	//设置位置tag，方便取出与当前位置对应
        	points[i].setTag(i);
        }
        
        //设置当面默认的位置
        currentIndex = 0;
        //设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
	}
	
	/**
	 * 当滑动状态改变时调用
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}
	
	/**
	 * 当当前页面被滑动时调用
	 */

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}
	
	/**
	 * 当新的页面被选中时调用
	 */

	@Override
	public void onPageSelected(int position) {
		//设置底部小点选中状态
        setCurDot(position);
	}

	/**
	 * 通过点击事件来切换当前的页面
	 */
	@Override
	public void onClick(View v) {
		 int position = (Integer)v.getTag();
         setCurView(position);
         setCurDot(position);		
	}

	/**
	 * 设置当前页面的位置
	 */
	private void setCurView(int position){
         if (position < 0 || position >= pics.length) {
             return;
         }
         viewPager.setCurrentItem(position);
     }

     /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon){
         if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
             return;
         }
         points[positon].setEnabled(false);
         points[currentIndex].setEnabled(true);

         currentIndex = positon;
     }
	
    /**
     * 保存用户首次安装版本号
     * @param userversion
     */
    public void saveFirstInstall(int userversion){
		//获取指定Key的SharedPreferences对象
		SharedPreferences _SP = getSharedPreferences("firstInstall", MODE_PRIVATE);
		//获取编辑
		SharedPreferences.Editor _Editor = _SP.edit();
		//按照指定Key放入数据
		_Editor.putBoolean("isfirst", false);
		_Editor.putInt("userversion", userversion);
		int currentCode = CommonUtil.getAppVersion(this).getVersionCode();//覆盖versioncode
		_Editor.putInt("versioncode", currentCode);
		//提交保存数据
		_Editor.commit();
	}
    
  /**
   * 是否首次安装
   * @return
   */
  	public Boolean isFirstInstall(){
  		SharedPreferences _SP = getSharedPreferences("firstInstall", MODE_PRIVATE);
  		Boolean isfirst = _SP.getBoolean("isfirst", true);
  		return isfirst;
  	}
  	
  /**
   * 获取用户版本号
   * @return
   */
  	public int getFirstInstallUserVersion(){
  		SharedPreferences _SP = getSharedPreferences("firstInstall", MODE_PRIVATE);
  		int userversion = _SP.getInt("userversion", 1);
  		return userversion;
  	}
  	
  	
}
