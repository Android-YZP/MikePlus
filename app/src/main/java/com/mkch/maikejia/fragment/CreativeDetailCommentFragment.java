package com.mkch.maikejia.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class CreativeDetailCommentFragment extends Fragment {
	public interface OnDetailCommentDataInitListener{
		public abstract void detailCommentDataInit();
	}
	
	private OnDetailCommentDataInitListener onDetailCommentDataInitListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onDetailCommentDataInitListener = (OnDetailCommentDataInitListener)activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private View mRootView;
	//评论
	private ListView mlv_creative_detail_comment;
	private TextView mtv_creative_detail_comment_none;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_creative_detail_comment, container, false);
		initView(mRootView);
		return mRootView;
	}
	
	private void initView(View view) {
		mlv_creative_detail_comment = (ListView)view.findViewById(R.id.lv_creative_detail_comment);
		mtv_creative_detail_comment_none = (TextView)view.findViewById(R.id.tv_creative_detail_comment_none);
		mlv_creative_detail_comment.setEmptyView(mtv_creative_detail_comment_none);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		setListener();
	}

	private void initData() {
		// TODO Auto-generated method stub
		
	}

	private void setListener() {
		// TODO Auto-generated method stub
		
	}
}
