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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.bean.MkfChannel;
import com.mkch.maikejia.bean.MkfNew;
import com.mkch.maikejia.business.IMkfBusiness;
import com.mkch.maikejia.business.imp.MkfBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.JsonUtils;
import com.mkch.maikejia.util.UmShareUtil;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;

public class MkfArticleDetailActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	private ImageView mIvShare;
	
	private int mArticleId;
	//业务层
	private IMkfBusiness mMkfBusiness = new MkfBusinessImp();
	private MkfNew mMkfNew;
	
	private TextView mTvArticleTitle;
	private TextView mTvArticleDatetime;
	private TextView mTvArticleViews;
	private PullToRefreshWebView mWvArticleContent;
	private WebView m_web_view;
	private TextView mTvArticleContent;
	
	private int mScreenWidth;
	
	//分享按钮
//	private ImageView ivWebShare;
	private String mWebTitle;
	private String mWebId;
	private String mActivityTitle;
	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mkf_article_detail);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_mkf_article_detail_topbar_back);
		mIvShare = (ImageView)findViewById(R.id.iv_mkf_article_detail_topbar_share);
		mTvTitle = (TextView)findViewById(R.id.tv_mkf_article_detail_topbar_title);
		//文章内容
//		mTvArticleTitle = (TextView)findViewById(R.id.tv_mkf_article_title);
//		mTvArticleDatetime = (TextView)findViewById(R.id.tv_mkf_article_datetime);
//		mTvArticleViews = (TextView)findViewById(R.id.tv_mkf_article_views);
		mWvArticleContent = (PullToRefreshWebView)findViewById(R.id.wv_mkf_article_content);
//		mTvArticleContent = (TextView)findViewById(R.id.tv_mkf_article_content);
	}

	private void initData() {
		
		
		UmShareUtil.initShareArgs(mController,this);//umeng分享
		//获取文章id
		Bundle _bundle = getIntent().getExtras();
		if(_bundle!=null){
			mArticleId = _bundle.getInt("_article_id");
			mWebId = String.valueOf(mArticleId);//文章ID-分享用
			mActivityTitle = _bundle.getString("_article_title");
			//改成webview
			m_web_view = mWvArticleContent.getRefreshableView();
			WebSettings _webSettings = m_web_view.getSettings();
			_webSettings.setJavaScriptEnabled(true);
			_webSettings.setDomStorageEnabled(true);
			_webSettings.setBlockNetworkImage(false);
			_webSettings.setDefaultTextEncodingName("UTF-8");
			
			m_web_view.loadUrl(CommonConstants.MKF_DETAIL_WAP + mArticleId);
			m_web_view.setWebViewClient(new MyWebViewClient());
		}
		
		mTvTitle.setText(mActivityTitle);
		
//		if(mArticleId!=0){
//			//发起网络请求获取详细信息
//			getArticleDetailFromNet();
//		}
//		//获取屏幕宽度-margin
//		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
//		mScreenWidth = wm.getDefaultDisplay().getWidth()-30;
		
	}

	/**
	 * 发起网络请求获取详细信息
	 */
	private void getArticleDetailFromNet() {
		//开启副线程-获取创意详情
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mMkfBusiness.getArticleDetail(mArticleId);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_mkf_getArticleDetail", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						fullMkfArticleDetail(jsonObj);//填充麦客风文章详情对象
						Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_mkf_mMkfNew_toString", mMkfNew.toString());
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_MKF_DETAIL_SUCCESS);
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
					sendErrorMessage("麦客风文章详情："+CommonConstants.MSG_GET_ERROR);
				}
			}
			/**
			 * 填充文章详情对象
			 * @param jsonObj
			 * @throws JSONException
			 */
			private void fullMkfArticleDetail(JSONObject _jsonObject) throws JSONException {
				
				mMkfNew = new MkfNew();
				mMkfNew.setId(JsonUtils.getInt(_jsonObject, "id", 0));
				//麦客风分类
				MkfChannel _channel = null;
				JSONObject _json_channel = JsonUtils.getObj(_jsonObject, "channel");
				if(_json_channel!=null){
					_channel = new MkfChannel();
					_channel.setId(JsonUtils.getInt(_json_channel, "id", 0));
					_channel.setName(JsonUtils.getString(_jsonObject, "name"));
					mMkfNew.setChannel(_channel);
				}
				mMkfNew.setTitleImg((JsonUtils.getString(_jsonObject,"titleImg")!=null&&!JsonUtils.getString(_jsonObject,"titleImg").equals(""))?(JsonUtils.getString(_jsonObject,"titleImg")+"!200.200"):null);
				mMkfNew.setTitle(JsonUtils.getString(_jsonObject, "title"));
				mMkfNew.setDesc(JsonUtils.getString(_jsonObject, "desc"));
				mMkfNew.setKeywords(JsonUtils.getString(_jsonObject, "keywords"));
				mMkfNew.setPublishTime(JsonUtils.getString(_jsonObject, "publishTime"));
				mMkfNew.setContent(JsonUtils.getString(_jsonObject, "content"));
				mMkfNew.setViews(JsonUtils.getInt(_jsonObject, "views"));
			}
		}).start();
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				MkfArticleDetailActivity.this.finish();
			}
		});
		mIvShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//分享
				//umeng分享
				//分享的内容
				String shareTitle = (mWebTitle==null||mWebTitle.equals(""))?"麦客风":mWebTitle;
				String shareContent = "麦客加，为梦想而造";
				String targetUrl = CommonConstants.MKF_DETAIL_WAP + mWebId;
				UMImage umimage = new UMImage(MkfArticleDetailActivity.this, R.drawable.ic_launcher);
				
				//设置分享面板点击后的内容
				UmShareUtil.shareSomething(mController, umimage, shareTitle, shareContent, targetUrl);
				
				 // 是否只有已登录用户才能打开分享选择页
		        mController.openShare(MkfArticleDetailActivity.this, false);
			}
		});
		
		mWvArticleContent.setOnRefreshListener(new OnRefreshListener<WebView>() {

			@Override
			public void onRefresh(PullToRefreshBase<WebView> refreshView) {
				// TODO Auto-generated method stub
				if(mArticleId!=0){
					m_web_view.loadUrl(CommonConstants.MKF_DETAIL_WAP + mArticleId);
					if(mWvArticleContent!=null&&mWvArticleContent.isRefreshing()){
						mWvArticleContent.onRefreshComplete();
					}
				}
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
	private static class MyHandler extends Handler{
		private final WeakReference<Activity> mActivity;
		public MyHandler(MkfArticleDetailActivity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			int flag = msg.what;
			switch (flag) {
			case 0:
				String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
				((MkfArticleDetailActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_GET_MKF_DETAIL_SUCCESS:
				((MkfArticleDetailActivity)mActivity.get()).updateArticleDetailUI();
				break;
			default:
				break;
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
	public void updateArticleDetailUI() {
		if(mMkfNew!=null){
			mTvArticleTitle.setText(mMkfNew.getTitle());
			mTvArticleDatetime.setText(mMkfNew.getPublishTime());
			mTvArticleViews.setText("浏览量："+mMkfNew.getViews());
			
			//乱码
//			mWvArticleContent.loadData(mMkfNew.getContent(), "text/html", "UTF-8");
			
//			mWvArticleContent.getSettings().setDefaultTextEncodingName("UTF-8");
//			mWvArticleContent.loadData(mMkfNew.getContent(), "text/html; charset=UTF-8", null);//这种写法可以正确解码
		
			
//			mWvArticleContent.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放
//			mWvArticleContent.getSettings().setLoadWithOverviewMode(true);
			
			//文字占一半
//			mWvArticleContent.getSettings().setTextSize(TextSize.LARGER);
//			mWvArticleContent.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
//			mWvArticleContent.loadDataWithBaseURL(null, mMkfNew.getContent(), "text/html",  "utf-8", null);
			
			Html.ImageGetter imageGetter=new Html.ImageGetter() {

				@Override
				public Drawable getDrawable(String source) {
					LevelListDrawable d = new LevelListDrawable();
			        Drawable empty = getResources().getDrawable(R.drawable.creative_no_img);
			        d.addLevel(0, 0, empty);
			        d.setBounds(0, 0, mScreenWidth, (mScreenWidth*3)/4);
			        new LoadImage(mTvArticleContent).execute(source, d);
			        return d;
				}
			   
			};
			mTvArticleContent.setText(Html.fromHtml(mMkfNew.getContent(),imageGetter,null));
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
                
                mDrawable.setBounds(0, 0, mScreenWidth, (mScreenWidth*3)/4);
                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                CharSequence t = mTv.getText();
                mTv.setText(t);
            }
        }
    }
	
	private class MyWebViewClient extends WebViewClient{
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			m_web_view.loadData("<div style='text-align:center;'>加载失败，请检查您的网络</div>", "text/html; charset=UTF-8", null);
		}
	}
}
