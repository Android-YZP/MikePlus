package com.mkch.maikejia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class HomeCatesListAdapter extends BaseAdapter  implements ListAdapter {
	private Integer[] catesPics = new Integer[]{R.drawable.tab_edu,R.drawable.tab_kitchen,R.drawable.tab_child,R.drawable.tab_dig,
			R.drawable.tab_recreation,R.drawable.tab_healthy,R.drawable.tab_house,R.drawable.tab_trip,R.drawable.tab_other};
	private String[] catesTitles = new String[]{"教育","厨房","儿童","数码","娱乐","健康","家居","旅行","其他"};
	private Context mContext;
	
	public HomeCatesListAdapter() {
	}

	public HomeCatesListAdapter(Context context) {
		this.mContext = context;
	}
	@Override
	public int getCount() {
		return catesTitles.length;
	}

	@Override
	public Object getItem(int position) {
		return catesTitles[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = LayoutInflater.from(mContext).inflate(R.layout.gv_home_cates_item, null);
		TextView tvHomeCatesItem = (TextView)view.findViewById(R.id.tv_home_cates_item);
		tvHomeCatesItem.setText(catesTitles[position]);
		ImageView ivHomeCatesItem = (ImageView)view.findViewById(R.id.iv_home_cates_item);
		ivHomeCatesItem.setImageResource(catesPics[position]);
		return view;
	}


}
