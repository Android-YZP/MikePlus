package com.mkch.maikejia.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.bean.Creative;

public class UserCreativeListAdapter extends BaseAdapter  implements ListAdapter {
	private List<Creative> mCreatives;
	private Context mContext;
	
	public UserCreativeListAdapter() {
	}

	public UserCreativeListAdapter(Context context,List<Creative> creatives) {
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
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_user_creative_item, null);
		if(mCreatives.size()>0){
			Creative _creative = mCreatives.get(position);
			SmartImageView ivUserCreativeItemTitle = (SmartImageView)view.findViewById(R.id.iv_user_creative_item_titlepic);
			ivUserCreativeItemTitle.setImageUrl(_creative.getTitleImg(), R.drawable.creative_no_img);
			TextView tvUserCreativeTitle = (TextView)view.findViewById(R.id.tv_user_creative_item_title);
			tvUserCreativeTitle.setText(_creative.getTitle());
			TextView tvUserCreativeDesc = (TextView)view.findViewById(R.id.tv_user_creative_item_desc);
			tvUserCreativeDesc.setText(_creative.getDesc()); 
			TextView tvUserCreativeDatatime = (TextView)view.findViewById(R.id.tv_user_creative_item_datetime);
			tvUserCreativeDatatime.setText(_creative.getReleaseDate()); 
			
			TextView tvUserCreativeCommentCount = (TextView)view.findViewById(R.id.tv_user_creative_item_comment_count);
			tvUserCreativeCommentCount.setText("评论("+String.valueOf(_creative.getCommentsCount())+")"); 
			
			TextView tvUserCreativeViews = (TextView)view.findViewById(R.id.tv_user_creative_item_views);
			tvUserCreativeViews.setText("浏览("+String.valueOf(_creative.getViews())+")"); 
			
			ImageView ivStatus = (ImageView)view.findViewById(R.id.iv_user_creative_item_status_pic);
			int _status = _creative.getStatus();//0草稿1审核中2审核通过3已结束5审核未通过6已过期
			switch (_status) {
			case 0:
				ivStatus.setImageResource(R.drawable.user_creative_status_caogao);
				break;
			case 1:
				ivStatus.setImageResource(R.drawable.user_creative_status_shenhez);
				break;
			case 2:
				ivStatus.setImageResource(R.drawable.user_creative_status_shenhetg);
				break;
			case 3:
				ivStatus.setImageResource(R.drawable.user_creative_status_yijiesu);
				break;
			case 5:
				ivStatus.setImageResource(R.drawable.user_creative_status_shenheweitg);
				break;
			case 6:
				ivStatus.setImageResource(R.drawable.user_creative_status_yiguoqi);
				break;
			default:
				break;
			}
		}
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
