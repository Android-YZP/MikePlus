package com.mkch.maikejia.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.bean.Creative;
import com.mkch.maikejia.bean.MkfNew;
import com.mkch.maikejia.view.CustomSmartImageView;

public class MkfNewsListAdapter extends BaseAdapter  implements ListAdapter {
	private List<MkfNew> mNews;
	private Context mContext;
	
	public MkfNewsListAdapter() {
	}

	public MkfNewsListAdapter(Context context,List<MkfNew> mNews) {
		this.mContext = context;
		this.mNews = mNews;
	}
	@Override
	public int getCount() {
		return mNews.size();
	}

	@Override
	public Object getItem(int position) {
		return mNews.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_mkf_new_item, null);
		if(mNews.size()>0){
			MkfNew _mkf_new = mNews.get(position);
			SmartImageView ivHomeCreativeItemTitle = (SmartImageView)view.findViewById(R.id.iv_mkf_new_item_titlepic);
			ivHomeCreativeItemTitle.setImageUrl(_mkf_new.getTitleImg(), R.drawable.creative_no_img);
			TextView tvMkfNewTitle = (TextView)view.findViewById(R.id.tv_mkf_new_item_title);
			tvMkfNewTitle.setText(_mkf_new.getTitle());
			TextView tvMkfNewDesc = (TextView)view.findViewById(R.id.tv_mkf_new_item_desc);
			tvMkfNewDesc.setText(_mkf_new.getDesc()); 
			TextView tvMkfNewPublishTime = (TextView)view.findViewById(R.id.tv_mkf_new_item_datetime);
			tvMkfNewPublishTime.setText(_mkf_new.getPublishTime()); 
			TextView tvMkfViews = (TextView)view.findViewById(R.id.tv_mkf_new_item_views);
			tvMkfViews.setText("浏览 "+String.valueOf(_mkf_new.getViews())); 
		}
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
