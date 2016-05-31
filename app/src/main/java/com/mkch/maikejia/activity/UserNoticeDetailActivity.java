package com.mkch.maikejia.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.bean.UserMessage;
import com.mkch.maikejia.business.IUserBusiness;
import com.mkch.maikejia.business.imp.UserBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;

public class UserNoticeDetailActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	private int mNoticeId;
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	private UserMessage mUserMessage;
	
	private PullToRefreshScrollView mSvUserNoticeDetail;
	private TextView mTvNoticeTitle;
	private TextView mTvNoticetime;
	private TextView mTvNoticeContent;
	
	private User mUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_notice_detail);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		//文章内容
		mSvUserNoticeDetail = (PullToRefreshScrollView)findViewById(R.id.sv_user_notice_detail);
		
		mTvNoticeTitle = (TextView)findViewById(R.id.tv_user_notice_detail_title);
		mTvNoticetime = (TextView)findViewById(R.id.tv_user_notice_detail_time);
		mTvNoticeContent = (TextView)findViewById(R.id.tv_user_notice_detail_content);
	}

	private void initData() {
		mTvTitle.setText("通知详情");
		mUser = CommonUtil.getUserInfo(this);
		//获取文章id
		Bundle _bundle = getIntent().getExtras();
		if(_bundle!=null){
			mNoticeId = _bundle.getInt("_notice_id");
		}
		if(mNoticeId!=0){
			//发起网络请求获取详细信息
			getNoticeDetailFromNet();
		}
		
	}

	/**
	 * 发起网络请求获取详细信息
	 */
	private void getNoticeDetailFromNet() {
		//开启副线程-获取通知详情
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBusiness.getUserNoticeDetail(mNoticeId,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_user_getUserNoticeDetail", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						fullUserNoticeArticleDetail(jsonObj);//填充通知消息文章详情对象
						Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_user_notice_mUserMessage_toString", mUserMessage.toString());
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_NOTICE_DETAIL_SUCCESS);
					}else{
						//获取错误代码，并查询出错误文字
						String errorMsg = jsonObj.getString("errorMsg");
						sendErrorMessage(errorMsg);
					}
				} catch (ConnectTimeoutException e) {
					e.printStackTrace();
					sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT);
				}catch (SocketTimeoutException e) {
					e.printStackTrace();
					sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
				}
				catch (ServiceException e) {
					e.printStackTrace();
					sendErrorMessage(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					sendErrorMessage("通知详情："+CommonConstants.MSG_GET_ERROR);
				}
			}
			/**
			 * 填充文章详情对象
			 * @param jsonObj
			 * @throws JSONException
			 */
			private void fullUserNoticeArticleDetail(JSONObject _jsonObject) throws JSONException {
				
				mUserMessage = new UserMessage();
				mUserMessage.setId(JsonUtils.getInt(_jsonObject, "id", 0));
				mUserMessage.setTitle(JsonUtils.getString(_jsonObject, "title"));
				mUserMessage.setDate(JsonUtils.getString(_jsonObject, "time"));
				mUserMessage.setContent(JsonUtils.getString(_jsonObject, "content"));
			}
		}).start();
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserNoticeDetailActivity.this.finish();
			}
		});
		
		mSvUserNoticeDetail.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				initData();//重新刷新数据
			}
		});
				
		
		
	}
	
	/**
	 * 发送错误信息到消息队列
	 * @param errorMsg
	 */
	private void sendErrorMessage(String errorMsg){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putSerializable("ErrorMsg", errorMsg);
		msg.setData(data);
		handler.sendMessage(msg);
	}
	
	/**
	 * 处理消息队列
	 * @author JLJ
	 *
	 */
	private class MyHandler extends Handler{
		private final WeakReference<Activity> mActivity;
		public MyHandler(UserNoticeDetailActivity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			int flag = msg.what;
			switch (flag) {
			case 0:
				String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
				((UserNoticeDetailActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_GET_USER_NOTICE_DETAIL_SUCCESS:
				((UserNoticeDetailActivity)mActivity.get()).updateUserNoticeDetailUI();
				break;
			default:
				break;
			}
			if(mSvUserNoticeDetail!=null&&mSvUserNoticeDetail.isRefreshing()){
				mSvUserNoticeDetail.onRefreshComplete();
			}
		}
	}
	
	private MyHandler handler = new MyHandler(this);
	/**
	 * 提示信息
	 * @param str
	 */
	private void showTip(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 更新文章内容-刷新UI
	 */
	public void updateUserNoticeDetailUI() {
		if(mUserMessage!=null){
			mTvNoticeTitle.setText(Html.fromHtml(mUserMessage.getTitle()));
			mTvNoticetime.setText(mUserMessage.getDate());
			
			Html.ImageGetter imageGetter=new Html.ImageGetter() {

				@Override
				public Drawable getDrawable(String source) {
					LevelListDrawable d = new LevelListDrawable();
			        Drawable empty = getResources().getDrawable(R.drawable.banner_circle_nor);
			        d.addLevel(0, 0, empty);
			        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

			        new LoadImage(mTvNoticeContent).execute(source, d);

			        return d;
				}
			   
			};
			Spanned spanned = Html.fromHtml(mUserMessage.getContent(), imageGetter, null);
			mTvNoticeContent.setText(spanned);
		}
		
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
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                CharSequence t = mTv.getText();
                mTv.setText(t);
            }
        }
    }
	
}
