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
import com.mkch.maikejia.bean.User;

public class UserAttentionUserListAdapter extends BaseAdapter  implements ListAdapter {
	private List<User> mUsers;
	private Context mContext;
	
	public UserAttentionUserListAdapter() {
	}

	public UserAttentionUserListAdapter(Context context,List<User> users) {
		this.mContext = context;
		this.mUsers = users;
	}
	@Override
	public int getCount() {
		return mUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return mUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_user_attention_user_item, null);
		if(mUsers.size()>0){
			User _user = mUsers.get(position);
			SmartImageView ivUserHead = (SmartImageView)view.findViewById(R.id.iv_user_attention_user_list_item_userhead);
			ivUserHead.setImageUrl(_user.getUserImg(), R.drawable.pscenter_headpic_nohead);
			TextView tvUsername = (TextView)view.findViewById(R.id.tv_user_attention_user_list_item_username);
			tvUsername.setText(_user.getUsername());
			TextView tvRegisterTime = (TextView)view.findViewById(R.id.tv_user_attention_user_list_item_registertime);
			tvRegisterTime.setText("加入时间："+_user.getRegisterTime()); 
			
		}
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
