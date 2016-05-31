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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.CreativeDetailActivity;
import com.mkch.maikejia.bean.Reply;
import com.mkch.maikejia.bean.Reply;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.view.CustomSmartImageView;

public class CreativeDetailCommentReplyListAdapter extends BaseAdapter  implements ListAdapter {
	private List<Reply> mReplys;
	private Context mContext;
	private CreativeDetailActivity mCreativeDetailActivity;
	private int mCommentId;
	
	public CreativeDetailCommentReplyListAdapter() {
	}

	public CreativeDetailCommentReplyListAdapter(Context context,List<Reply> mReplys,int mCommentId) {
		this.mContext = context;
		this.mReplys = mReplys;
		this.mCreativeDetailActivity = (CreativeDetailActivity)context;
		this.mCommentId = mCommentId;
	}
	@Override
	public int getCount() {
		return mReplys.size();
	}

	@Override
	public Object getItem(int position) {
		return mReplys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_creative_detail_comment_reply_item, null);
		if(mReplys.size()>0){
			Reply _reply = mReplys.get(position);
			CustomSmartImageView ivReplyUserHead = (CustomSmartImageView)view.findViewById(R.id.iv_creative_detail_comment_reply_item_headpic);
			ivReplyUserHead.setImageUrl(_reply.getUser().getUserImg(), R.drawable.creative_no_img);
			TextView tvReplyUserName = (TextView)view.findViewById(R.id.tv_creativedetail_comment_reply_item_username);
			tvReplyUserName.setText(_reply.getUser().getUsername());
			TextView tvReplyCreateTime = (TextView)view.findViewById(R.id.tv_creativedetail_comment_reply_item_datetime);
			tvReplyCreateTime.setText(_reply.getCreateTime()); 
			TextView tvCommentReplyNum = (TextView)view.findViewById(R.id.tv_creativedetail_comment_reply_item_reply_title);
			int type = _reply.getType();//回复内容中的类型
			
			final String _username = _reply.getUser().getUsername();
			tvCommentReplyNum.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					Log.d(CommonConstants.LOGCAT_TAG_NAME+"_tvCommentReplyNum", "tvCommentReplySon is "+ _username);
					mCreativeDetailActivity.openImInput(_username,2,mCommentId);
				}
			});
			
			
			StringBuffer replycontent = new StringBuffer();
			if(type==1){
				replycontent.append(_reply.getText());
			}else if (type==2) {
				replycontent.append("@"+_reply.getReplyUser().getUsername()+",");
				replycontent.append(_reply.getText());
			}
			final TextView tvCommentContent = (TextView)view.findViewById(R.id.tv_creativedetail_comment_reply_item_content);
			Html.ImageGetter imageGetter=new Html.ImageGetter() {

				@Override
				public Drawable getDrawable(String source) {
					LevelListDrawable d = new LevelListDrawable();
			        Drawable empty = mContext.getResources().getDrawable(R.drawable.banner_circle_nor);
			        d.addLevel(0, 0, empty);
			        d.setBounds(0, 0, 36, 36);

			        new LoadImage(tvCommentContent).execute(source, d);

			        return d;
				}
			   
			};
			Spanned spanned = Html.fromHtml(replycontent.toString(), imageGetter, null);
			tvCommentContent.setText(spanned); 
			
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
//          Log.d(CommonConstants.LOGCAT_TAG_NAME, "doInBackground " + source);
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
