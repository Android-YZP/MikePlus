package com.mkch.maikejia.adapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.CreativeDetailActivity;
import com.mkch.maikejia.bean.Comment;
import com.mkch.maikejia.bean.Reply;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.view.CustomListViewWithHeight;
import com.mkch.maikejia.view.CustomSmartImageView;

public class CreativeDetailCommentListAdapter extends BaseAdapter  implements ListAdapter {
	private List<Comment> mComments;
	private Context mContext;
	private CreativeDetailActivity mCreativeDetailActivity;
	
	public CreativeDetailCommentListAdapter() {
	}

	public CreativeDetailCommentListAdapter(Context context,List<Comment> mComments) {
		this.mContext = context;
		this.mComments = mComments;
		mCreativeDetailActivity = (CreativeDetailActivity)context;
	}
	@Override
	public int getCount() {
		return mComments.size();
	}

	@Override
	public Object getItem(int position) {
		return mComments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_creative_detail_comment_item, null);
		if(mComments.size()>0){
			Comment _comment = mComments.get(position);
			CustomSmartImageView ivCommentUserHead = (CustomSmartImageView)view.findViewById(R.id.iv_creative_detail_comment_item_headpic);
//			Log.d(CommonConstants.LOGCAT_TAG_NAME+"_userimg_"+position, "="+_comment.getCommentUser().getUserImg());
			ivCommentUserHead.setImageUrl(_comment.getCommentUser().getUserImg(), R.drawable.creative_no_img);
			TextView tvCommentUserName = (TextView)view.findViewById(R.id.tv_creativedetail_comment_item_username);
			tvCommentUserName.setText(_comment.getCommentUser().getUsername());
			TextView tvCommentTime = (TextView)view.findViewById(R.id.tv_creativedetail_comment_item_datetime);
			tvCommentTime.setText(_comment.getCreateTime()); 
			TextView tvCommentReplyNum = (TextView)view.findViewById(R.id.tv_creativedetail_comment_item_reply_title);
			tvCommentReplyNum.setText("回复("+_comment.getReplyCount()+")"); 
			
			final String _username = _comment.getCommentUser().getUsername();
			final int _commentId = _comment.getId();
			tvCommentReplyNum.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					Log.d(CommonConstants.LOGCAT_TAG_NAME+"_tvCommentReplyNum", "tvCommentReply is "+ _username);
					mCreativeDetailActivity.openImInput(_username,1,_commentId);
				}
			});
			
			
			final TextView tvCommentContent = (TextView)view.findViewById(R.id.tv_creativedetail_comment_item_content);
			Html.ImageGetter imageGetter=new Html.ImageGetter() {

				@Override
				public Drawable getDrawable(String source) {
					LevelListDrawable d = new LevelListDrawable();
			        Drawable empty = mContext.getResources().getDrawable(R.drawable.banner_circle_nor);
			        d.addLevel(0, 0, empty);
//			        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
			        d.setBounds(0, 0, 36, 36);

			        new LoadImage(tvCommentContent).execute(source, d);

			        return d;
				}
			   
			};
			Spanned spanned = Html.fromHtml(_comment.getText(), imageGetter, null);
			tvCommentContent.setText(spanned); 
			
			//回复列表
			final List<Reply> replys = _comment.getReplys();
			if (replys!=null&&replys.size()>0) {
				CustomListViewWithHeight lvCommentReplyList = (CustomListViewWithHeight)view.findViewById(R.id.lv_creativedetail_comment_reply_list);
				CreativeDetailCommentReplyListAdapter creativeDetailCommentReplyListAdapter = new CreativeDetailCommentReplyListAdapter(mContext, replys,_commentId);
				lvCommentReplyList.setAdapter(creativeDetailCommentReplyListAdapter);
				CommonUtil.setListViewHeight(lvCommentReplyList);
				lvCommentReplyList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapterView, View view,
							int position, long id) {
						Reply _reply = replys.get(position);
						String _username2 = _reply.getUser().getUsername();
						mCreativeDetailActivity.openImInput(_username2, 2, _commentId);
					}
				});
			}
			
		}
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;
        private TextView mTv;
        public LoadImage() {
			super();
		}

        public LoadImage(TextView mTv) {
        	this.mTv = mTv;
		}
        
		@Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
//            Log.d(CommonConstants.LOGCAT_TAG_NAME, "doInBackground " + source);
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
//            Log.d(CommonConstants.LOGCAT_TAG_NAME, "onPostExecute drawable " + mDrawable);
//            Log.d(CommonConstants.LOGCAT_TAG_NAME, "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
//                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setBounds(0, 0, 36, 36);
                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                CharSequence t = mTv.getText();
                mTv.setText(t);
            }
        }
    }
	
}
