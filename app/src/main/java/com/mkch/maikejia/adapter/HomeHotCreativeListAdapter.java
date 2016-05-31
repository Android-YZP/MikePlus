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
import com.mkch.maikejia.view.CustomSmartImageView;

public class HomeHotCreativeListAdapter extends BaseAdapter  implements ListAdapter {
	private List<Creative> mCreatives;
	private Context mContext;
	
	public HomeHotCreativeListAdapter() {
	}

	public HomeHotCreativeListAdapter(Context context,List<Creative> creatives) {
		this.mContext = context;
		this.mCreatives = creatives;
	}
	@Override
	public int getCount() {
		return mCreatives.size();
	}

	@Override
	public Object getItem(int position) {
		return mCreatives.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
//		ViewHolder holder;
//		if(view == null){
//			view = LayoutInflater.from(mContext).inflate(R.layout.lv_home_creative_item, null);
//			holder = new ViewHolder();
//			holder.ivHomeCreativeItemTitle = (SmartImageView)view.findViewById(R.id.iv_home_creative_item_titlepic);
//			holder.tvHomeCreativeTitle = (TextView)view.findViewById(R.id.tv_home_creative_item_title);
//			holder.tvHomeCreativeDesc = (TextView)view.findViewById(R.id.tv_home_creative_item_desc);
//			holder.tvHomeCreativeUsername = (TextView)view.findViewById(R.id.tv_home_creative_item_username);
//			holder.ivUserHeadpic = (CustomSmartImageView)view.findViewById(R.id.iv_home_creative_item_userhead);
//			holder.tvHomeCreativeDatatime = (TextView)view.findViewById(R.id.tv_home_creative_item_datetime);
//			view.setTag(holder);
//		}else{
//			holder = (ViewHolder)view.getTag();
//		}
//		Creative _creative = mCreatives.get(position);
//		holder.ivHomeCreativeItemTitle.setImageUrl(_creative.getTitleImg(), R.drawable.creative_no_img);
//		holder.tvHomeCreativeTitle.setText(_creative.getTitle());
//		holder.tvHomeCreativeDesc.setText(_creative.getDesc()); 
//		holder.tvHomeCreativeUsername.setText(_creative.getUsername()); 
//		holder.ivUserHeadpic.setImageUrl(_creative.getUserImg(), R.drawable.creative_no_img);
//		holder.tvHomeCreativeDatatime.setText(_creative.getReleaseDate()); 
//		return view;
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_home_creative_item, null);
		if(mCreatives.size()>0){
			Creative _creative = mCreatives.get(position);
			SmartImageView ivHomeCreativeItemTitle = (SmartImageView)view.findViewById(R.id.iv_home_creative_item_titlepic);
			ivHomeCreativeItemTitle.setImageUrl(_creative.getTitleImg(), R.drawable.creative_no_img);
			TextView tvHomeCreativeTitle = (TextView)view.findViewById(R.id.tv_home_creative_item_title);
			tvHomeCreativeTitle.setText(_creative.getTitle());
			TextView tvHomeCreativeDesc = (TextView)view.findViewById(R.id.tv_home_creative_item_desc);
			tvHomeCreativeDesc.setText(_creative.getDesc()); 
			TextView tvHomeCreativeUsername = (TextView)view.findViewById(R.id.tv_home_creative_item_username);
			tvHomeCreativeUsername.setText(_creative.getUsername()); 
			CustomSmartImageView ivUserHeadpic = (CustomSmartImageView)view.findViewById(R.id.iv_home_creative_item_userhead);
			ivUserHeadpic.setImageUrl(_creative.getUserImg(), R.drawable.creative_no_img);
			TextView tvHomeCreativeDatatime = (TextView)view.findViewById(R.id.tv_home_creative_item_datetime);
			tvHomeCreativeDatatime.setText(_creative.getReleaseDate()); 
		}
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private static class ViewHolder{
		private static SmartImageView ivHomeCreativeItemTitle;
		private static TextView tvHomeCreativeTitle;
		private static TextView tvHomeCreativeDesc;
		private static TextView tvHomeCreativeUsername;
		private static CustomSmartImageView ivUserHeadpic;
		private static TextView tvHomeCreativeDatatime;
		
		
	}
}
