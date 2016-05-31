package com.mkch.maikejia.activity;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
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
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.business.IUserBusiness;
import com.mkch.maikejia.business.imp.UserBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;

public class StartBusinessActivity extends Activity {
	private ImageView mIvHome;
	private TextView mTvTitle;
	private ImageView mIvPscenter;
	
	private PullToRefreshWebView mWvArticleContent;
	private WebView m_web_view;
	
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	private User mUser;
	
	private String mSessionId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_business);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvHome = (ImageView)findViewById(R.id.iv_start_business_topbar_home);
		mTvTitle = (TextView)findViewById(R.id.tv_start_business_topbar_title);
		mIvPscenter = (ImageView)findViewById(R.id.iv_start_business_topbar_pscenter);
		//文章内容
		mWvArticleContent = (PullToRefreshWebView)findViewById(R.id.wv_start_business_content);
	}

	private void initData() {
		//topbar标题
		mTvTitle.setText("创业大赛");
		
		mUser = CommonUtil.getUserInfo(StartBusinessActivity.this);
		//从网络中验证用户登录状态
		checkInitLoginedFromNet();
		 
		
		m_web_view = mWvArticleContent.getRefreshableView();
		WebSettings _webSettings = m_web_view.getSettings();
		_webSettings.setJavaScriptEnabled(true);
		_webSettings.setDomStorageEnabled(true);
		_webSettings.setBlockNetworkImage(false);
		_webSettings.setDefaultTextEncodingName("UTF-8");
		m_web_view.loadUrl(CommonConstants.START_BUSINESS_WAP);
		m_web_view.setWebViewClient(new MyWebViewClient());
		m_web_view.setDownloadListener(new DownloadListener() {  
	    	  
            @Override  
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {  
                // 监听下载功能，当用户点击下载链接的时候，直接调用系统的浏览器来下载  
  
                Uri uri = Uri.parse(url);  
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
                startActivity(intent);  
            }  
        });
		
		
	}

	private void setListener() {
		//返回麦客加首页
		mIvHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				StartBusinessActivity.this.finish();
			}
		});
		//重新载入
		mWvArticleContent.setOnRefreshListener(new OnRefreshListener<WebView>() {

			@Override
			public void onRefresh(PullToRefreshBase<WebView> refreshView) {
//				m_web_view.loadUrl(CommonConstants.START_BUSINESS_WAP);
				m_web_view.loadUrl(m_web_view.getUrl());
				if(mWvArticleContent!=null&&mWvArticleContent.isRefreshing()){
					mWvArticleContent.onRefreshComplete();
				}
			}
		});
		//个人中心
		mIvPscenter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//从网络中验证用户登录状态，并通知ui跳转操作
				checkStartBussinessLoginedFromNet();
			}
		});
	}

	/**
	 * 从网络中验证用户登录状态，初始化
	 */
	protected void checkInitLoginedFromNet() {
		//开启副线程-提交创意
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String verifyCode = "";
					if(mUser!=null){
						verifyCode = mUser.getVerifyCode();
					}
					Map<String, String> _map_result = mUserBusiness.startBusinessUserCheckLogin(verifyCode);
					String result = _map_result.get("result");
					
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_startBusinessUserCheckLogin_init", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = JsonUtils.getBoolean(jsonObj, "success");
					if(Success){
//						mSessionId = _map_result.get("sessionId");
						mSessionId = JsonUtils.getString(jsonObj, "sessionId");
						Log.d(CommonConstants.LOGCAT_TAG_NAME+"_mSessionId", "_mSessionId="+mSessionId);
					}
					handler.sendEmptyMessage(CommonConstants.FLAG_GET_WAP_LOGINED_INIT);
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
					sendErrorMessage("检查登录："+CommonConstants.MSG_GET_ERROR);
				}
			}
		}).start();
	}
	
	
	/**
	 * 从网络中验证用户登录状态，并通知ui跳转操作
	 */
	protected void checkStartBussinessLoginedFromNet() {
		if(mUser==null){
			Toast.makeText(StartBusinessActivity.this, "亲，请先去个人中心登录", Toast.LENGTH_SHORT).show();
			return;
		}
		//开启副线程-提交创意
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String verifyCode = mUser.getVerifyCode();
					Map<String, String> _map_result = mUserBusiness.startBusinessUserCheckLogin(verifyCode);
					String result = _map_result.get("result");
					
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_startBusinessUserCheckLogin", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = JsonUtils.getBoolean(jsonObj, "success");
					if(Success){
//						mSessionId = _map_result.get("sessionId");
						mSessionId = JsonUtils.getString(jsonObj, "sessionId");
						Log.d(CommonConstants.LOGCAT_TAG_NAME+"_mSessionId", "_mSessionId="+mSessionId);
						
					}else{
						//获取错误代码，并查询出错误文字
						String errorMsg = "亲，请您先去个人中心登录";
						sendErrorMessage(errorMsg);
						return;
					}
					//获取成功
					handler.sendEmptyMessage(CommonConstants.FLAG_GET_WAP_LOGINED);
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
					sendErrorMessage("检查登录："+CommonConstants.MSG_GET_ERROR);
				}
			}
		}).start();
	}

	private class MyWebViewClient extends WebViewClient{
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			m_web_view.loadData("<div style='text-align:center;'>加载失败，请检查您的网络</div>", "text/html; charset=UTF-8", null);
		}
		
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
		public MyHandler(StartBusinessActivity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			int flag = msg.what;
			switch (flag) {
			case 0:
				String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
				((StartBusinessActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_GET_WAP_LOGINED:
				((StartBusinessActivity)mActivity.get()).isLogined();
				break;
			case CommonConstants.FLAG_GET_WAP_LOGINED_INIT:
				((StartBusinessActivity)mActivity.get()).isLoginedInit();
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
	 * 用户初始进来的时候
	 */
	public void isLoginedInit() {
		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_logined_init", mSessionId + ",appWapLogin=true");
		CookieSyncManager.createInstance(this);    
		CookieManager cookieManager = CookieManager.getInstance(); 
//		cookieManager.setCookie(CommonConstants.START_BUSINESS_WAP, mSessionId);//JSESSIONID=SDFDSFSDFSDFSDFSDFDSF;
//		cookieManager.setCookie(CommonConstants.START_BUSINESS_WAP, "appWapLogin=true");
		cookieManager.setCookie(".maikejia.com", "JSESSIONID="+mSessionId);//JSESSIONID=SDFDSFSDFSDFSDFSDFDSF;
		cookieManager.setCookie("m.dasai.maikejia.com", "appWapLogin=true");
		
		CookieSyncManager.getInstance().sync(); 
		
	}

	/**
	 * 用户是登录状态
	 */
	public void isLogined() {
		CookieSyncManager.createInstance(this);    
		CookieManager cookieManager = CookieManager.getInstance();
//		cookieManager.setCookie(CommonConstants.START_BUSINESS_PSCENTER_WAP, mSessionId);
//		cookieManager.setCookie(CommonConstants.START_BUSINESS_PSCENTER_WAP, "appWapLogin=true");  
		cookieManager.setCookie(".maikejia.com", "JSESSIONID="+mSessionId);
		cookieManager.setCookie("m.dasai.maikejia.com", "appWapLogin=true");  
		CookieSyncManager.getInstance().sync(); 
		//跳转到个人中心页面
		m_web_view.loadUrl(CommonConstants.START_BUSINESS_PSCENTER_WAP);
	}
	
	/**
	 * 返回上一页web页
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && m_web_view.canGoBack()) {
			m_web_view.goBack();// 返回前一个页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
