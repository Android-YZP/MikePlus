package com.mkch.maikejia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.CreativeDeployTitleActivity;
import com.mkch.maikejia.activity.CreativeListActivity;
import com.mkch.maikejia.activity.CreativeSearchActivity;
import com.mkch.maikejia.activity.StartBusinessActivity;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.util.CommonUtil;

public class FindFragment extends Fragment {
	private ImageView mIv_index_find;
	
	private RelativeLayout mLineStartBusiness;
	private LinearLayout mLineCreativeList;
	
	private LinearLayout mLineDeployCreative;
	
	private Button m_btn_find_search;
	
	private User mPscenterUser;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_index_find, container, false);
		
		findView(view);
		return view;
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		setListenser();
	}

	@Override
	public void onResume() {
		super.onResume();
//		mPscenterUser = CommonUtil.getUserInfo(getActivity());//初始化用户信息
	}



	private void findView(View view) {
		mIv_index_find = (ImageView)view.findViewById(R.id.iv_index_find);
		
		m_btn_find_search = (Button)view.findViewById(R.id.btn_find_search);
		
		mLineStartBusiness = (RelativeLayout)view.findViewById(R.id.line_find_maikebei);
		mLineCreativeList = (LinearLayout)view.findViewById(R.id.line_find_idea);
		mLineDeployCreative = (LinearLayout)view.findViewById(R.id.line_find_deploy);
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		
	}
	/**
	 * 设置监听
	 */
	private void setListenser() {
		m_btn_find_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent _intent = new Intent(getActivity(), CreativeSearchActivity.class);
				startActivity(_intent);
				
			}
		});
		
		//发布创意
		mLineDeployCreative.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
//				if(mPscenterUser!=null){
//					Intent _intent = new Intent(getActivity(), CreativeDeployTitleActivity.class);
//					startActivity(_intent);
//				}else{
//					if(getActivity()!=null) Toast.makeText(getActivity(), "亲，请先去个人中心登录", Toast.LENGTH_SHORT).show();
//				}
				
				if(getActivity()!=null) Toast.makeText(getActivity(), "请到麦客加网站发布创意", Toast.LENGTH_SHORT).show();
			}
		});
		
		//创意列表
		mLineCreativeList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent _intent = new Intent(getActivity(), CreativeListActivity.class);
				startActivity(_intent);
			}
		});
		
		//麦客杯创业大赛
		mLineStartBusiness.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent _intent = new Intent(getActivity(), StartBusinessActivity.class);
				startActivity(_intent);
			}
		});
		
		//麦客杯创业大赛banner
		mIv_index_find.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent _intent = new Intent(getActivity(), StartBusinessActivity.class);
				startActivity(_intent);
			}
		});
	}
}
