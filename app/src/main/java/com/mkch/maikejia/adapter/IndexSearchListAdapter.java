package com.mkch.maikejia.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.CreativeSearchActivity;
import com.mkch.maikejia.bean.SearchHistory;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.util.CommonUtil;
/**
 * 搜索历史
 * @author JLJ
 *
 */
public class IndexSearchListAdapter extends BaseAdapter  implements ListAdapter {
	private List<String> keywords;
	private Context mContext;
	private CreativeSearchActivity mCreativeSearchActivity;
	
	public IndexSearchListAdapter() {
	}

	public IndexSearchListAdapter(Context context,List<String> keywords) {
		this.mContext = context;
		this.keywords = keywords;
		this.mCreativeSearchActivity = (CreativeSearchActivity)context;
	}
	@Override
	public int getCount() {
		return keywords.size();
	}

	@Override
	public Object getItem(int position) {
		return keywords.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(final int position, View view, ViewGroup viewGroup) {
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_index_search_history_item, null);
		TextView tvHomeCatesItem = (TextView)view.findViewById(R.id.tv_index_search_listview_history_item_keyword);
		tvHomeCatesItem.setText(keywords.get(position));
		ImageView ivCancelKeyword = (ImageView)view.findViewById(R.id.iv_index_search_listview_history_item_cancel);
		ivCancelKeyword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mCreativeSearchActivity.notifyHistoryKeywordsChanged(position);
			}
		});
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}
}
