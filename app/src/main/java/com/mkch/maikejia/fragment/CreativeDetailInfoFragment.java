package com.mkch.maikejia.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class CreativeDetailInfoFragment extends Fragment {
	private View mRootView;
	private TextView mTvCreativeSource;
	private TextView mTvCreativeInfo;

	public interface OnDetailInfoDataInitListener{
		public abstract void detailInfoDataInit();
	}
	
	private OnDetailInfoDataInitListener onDetailInfoDataInitListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onDetailInfoDataInitListener = (OnDetailInfoDataInitListener)activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_creative_detail_info, container, false);
		initView(mRootView);
		return mRootView;
	}
	
	private void initView(View view) {
		mTvCreativeSource = (TextView)view.findViewById(R.id.tv_creativedetail_source_info_content);
		mTvCreativeInfo = (TextView)view.findViewById(R.id.tv_creativedetail_detail_info_content);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		setListener();
	}

	private void initData() {
		// TODO Auto-generated method stub
		onDetailInfoDataInitListener.detailInfoDataInit();
	}

	private void setListener() {
		// TODO Auto-generated method stub
		
	}
}
