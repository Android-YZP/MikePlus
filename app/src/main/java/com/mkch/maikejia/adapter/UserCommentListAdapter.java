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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.adapter.CreativeDetailCommentListAdapter.LoadImage;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.bean.UserComment;

public class UserCommentListAdapter extends BaseAdapter  implements ListAdapter {
	private List<UserComment> mUserComments;
	private Context mContext;
	
	public UserCommentListAdapter() {
	}

	public UserCommentListAdapter(Context context,List<UserComment> mUserComments) {
		this.mContext = context;
		this.mUserComments = mUserComments;
	}
	@Override
	public int getCount() {
		return mUserComments.size();
	}

	@Override
	public Object getItem(int position) {
		return mUserComments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_user_comment_receive_list_item, null);
		if(mUserComments.size()>0){
			UserComment _user_comment = mUserComments.get(position);
			TextView tvUserCommentReceiveListTitle = (TextView)view.findViewById(R.id.tv_user_comment_receive_list_item_title);
			String _username = _user_comment.getUsername();
			if(_username!=null&&!_username.equals("")){
				tvUserCommentReceiveListTitle.setText(_username+" 对 "+_user_comment.getTitle()+" 发表了评论");
			}else{
				
				tvUserCommentReceiveListTitle.setText("您对 "+_user_comment.getTitle()+" 发表了评论");
			}
			
			TextView tvUserCommentReceiveListRegisterTime = (TextView)view.findViewById(R.id.tv_user_comment_receive_list_item_registertime);
			tvUserCommentReceiveListRegisterTime.setText(_user_comment.getTime()); 
			
			final TextView tvUserCommentReceiveListContent = (TextView)view.findViewById(R.id.tv_user_comment_receive_list_item_content);
			Html.ImageGetter imageGetter=new Html.ImageGetter() {

				@Override
				public Drawable getDrawable(String source) {
					LevelListDrawable d = new LevelListDrawable();
			        Drawable empty = mContext.getResources().getDrawable(R.drawable.banner_circle_nor);
			        d.addLevel(0, 0, empty);
//			        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
			        d.setBounds(0, 0, 36, 36);

			        new LoadImage(tvUserCommentReceiveListContent).execute(source, d);

			        return d;
				}
			   
			};
			Spanned spanned = Html.fromHtml(_user_comment.getText(), imageGetter, null);
			tvUserCommentReceiveListContent.setText(spanned); 
			
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
