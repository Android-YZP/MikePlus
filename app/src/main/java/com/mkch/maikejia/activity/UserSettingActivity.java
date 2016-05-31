package com.mkch.maikejia.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.Calendar;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mkch.maikejia.R;
import com.mkch.maikejia.bean.AppVersion;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.business.IUserBusiness;
import com.mkch.maikejia.business.imp.UserBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.service.DownloadFileService;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;
import com.mkch.maikejia.view.UserSettingInfoList;
import com.mkch.maikejia.view.UserSettingInfoList.IUserSettingInfoCallBackListener;
import com.mkch.maikejia.view.UserSettingInfoListItem;

public class UserSettingActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	private UserSettingInfoList mUserSettingInfoList;//个人资料三个选项的选择
//	private ToggleButton mTBtnIsOnPush;//推送设置开关
	private UserSettingInfoListItem mUserSettingInfoListItemAboutUs;//关于我们
	private UserSettingInfoListItem mUserSettingInfoListItemCheckUpdate;//检查更新
	
	private LinearLayout mLineLogout;
	private TextView mTvLogout;
	
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	private User mUser;
	
	private AppVersion mAppVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_setting);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		
		mUserSettingInfoList = (UserSettingInfoList)findViewById(R.id.customview_user_setting_info);
//		mTBtnIsOnPush = (ToggleButton)findViewById(R.id.tbtn_user_setting_ispush);
		
		mUserSettingInfoListItemAboutUs = (UserSettingInfoListItem)findViewById(R.id.customview_user_setting_list_item_aboutus);
		mUserSettingInfoListItemCheckUpdate = (UserSettingInfoListItem)findViewById(R.id.customview_user_setting_list_item_checkupdate);
	
		mLineLogout = (LinearLayout)findViewById(R.id.line_user_setting_logout);
		mTvLogout = (TextView)findViewById(R.id.tv_user_setting_logout);
	}

	private void initData() {
		mTvTitle.setText("设置");
		mUserSettingInfoListItemAboutUs.setData(R.drawable.pscenter_user_setting_guanyu, "关于我们");
		String versionName = "(v"+CommonUtil.getAppVersion(this).getVersionName()+")";
		mUserSettingInfoListItemCheckUpdate.setData(R.drawable.pscenter_user_setting_jianchagx, "检查更新"+versionName);
		mUser = CommonUtil.getUserInfo(this);
		if(mUser!=null){
			mLineLogout.setVisibility(View.VISIBLE);
		}
	}

	private void setListener() {
		
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserSettingActivity.this.finish();
			}
		});
		
		mUserSettingInfoList.setOnItemClickListener(new IUserSettingInfoCallBackListener() {
			
			@Override
			public void clickItem(int viewid) {
				if(mUser==null){
					Toast.makeText(UserSettingActivity.this, "亲，请您先登录", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Intent _intent = null;
				switch (viewid) {
				case R.id.customview_user_setting_list_item_psinfo:
					Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_setting_click", "psinfo");
					_intent = new Intent(UserSettingActivity.this, UserSettingPsInfoActivity.class);
					UserSettingActivity.this.startActivity(_intent);
					break;
				case R.id.customview_user_setting_list_item_uppwd:
					Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_setting_click", "uppwd");
					_intent = new Intent(UserSettingActivity.this, UserSettingUpdatePwdActivity.class);
					UserSettingActivity.this.startActivity(_intent);
					break;
				case R.id.customview_user_setting_list_item_security:
					Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_setting_click", "security");
					_intent = new Intent(UserSettingActivity.this, UserSettingSecurityActivity.class);
					UserSettingActivity.this.startActivity(_intent);
					break;
				default:
					break;
				}
			}
		});
		
//		mTBtnIsOnPush.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
//				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_setting_mTBtnIsOnPush", "status="+status);
//			}
//		});
		
		mUserSettingInfoListItemAboutUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_setting_mUserSettingInfoListItemAboutUs", "mUserSettingInfoListItemAboutUs");
//				Intent _intent = new Intent(UserSettingActivity.this, UserSettingAboutusActivity.class);
//				UserSettingActivity.this.startActivity(_intent);
				//关于我们
				Intent _intent = new Intent(UserSettingActivity.this,ArticleDetailActivity.class);
				_intent.putExtra("_article_title", "关于我们");
				_intent.putExtra("_article_id", 2);//关于我们
				UserSettingActivity.this.startActivity(_intent);
				
			}
		});
		
//		//检查更新
//		mUserSettingInfoListItemCheckUpdate.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View view) {
//				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_setting_mUserSettingInfoListItemCheckUpdate", "mUserSettingInfoListItemCheckUpdate");
//				//从网络中获取-是否需要更新
//				checkAppUpdate();
//			}
//		});
		
		//检查更新
		mUserSettingInfoListItemCheckUpdate.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
            	//从网络中获取-是否需要更新
            	checkAppUpdate();
            }
        });
		
		//退出登录
		mTvLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				new AlertDialog.Builder(UserSettingActivity.this)
				.setTitle("提示")
				.setMessage("您确定退出登录吗？")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CommonUtil.clearUserInfo(UserSettingActivity.this);
						UserSettingActivity.this.finish();
						
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
				
				
			}
		});
	}

	public abstract class NoDoubleClickListener implements OnClickListener {
		
		public abstract void onNoDoubleClick(View v);

        public static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onNoDoubleClick(v);
            } 
        }   
    }
	
	
	/**
	 * 从网络中获取-是否需要更新
	 */
	protected void checkAppUpdate() {
		//开启副线程-提交创意
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBusiness.checkAppUpdate();
					
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_checkAppUpdate", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						fullAppVersion(jsonObj);//填充appVersion对象
						handler.sendEmptyMessage(CommonConstants.FLAG_CHECK_APP_UPDATE_SUCCESS);
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
					sendErrorMessage("检查更新："+CommonConstants.MSG_GET_ERROR);
				}
			}
			/**
			 * 填充appVersion对象
			 * @param jsonObj
			 */
			private void fullAppVersion(JSONObject jsonObj) {
				mAppVersion = new AppVersion();
				mAppVersion.setPath(JsonUtils.getString(jsonObj,"path"));
				mAppVersion.setFileSize(JsonUtils.getString(jsonObj,"fileSize"));
				String _version_code_str = JsonUtils.getString(jsonObj,"versionCode");
				if(_version_code_str!=null&&!_version_code_str.equals("")){
					mAppVersion.setVersionCode(Integer.parseInt(_version_code_str));
				}else{
					mAppVersion.setVersionCode(0);
				}
				mAppVersion.setVersionName(JsonUtils.getString(jsonObj,"versionName"));
			}
		}).start();
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
		public MyHandler(UserSettingActivity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			int flag = msg.what;
			switch (flag) {
			case 0:
				String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
				((UserSettingActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_CHECK_APP_UPDATE_SUCCESS:
				((UserSettingActivity)mActivity.get()).showUpdateDialog();
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
	 * 弹出是否更新
	 */
	public void showUpdateDialog() {
		AppVersion _current_version = CommonUtil.getAppVersion(this);
		int _current_code = _current_version.getVersionCode();
		if(mAppVersion.getVersionCode()>_current_code){
			//弹出是否更新
			chooseDownload();
		}else{
			Toast.makeText(this, "当前版本已经为最新版本", Toast.LENGTH_SHORT).show();
		}
	}
	
	private BroadcastReceiver receiver;
	public void chooseDownload() {
		// 弹出确定退出对话框
		new AlertDialog.Builder(this)
		.setTitle("检查更新")
		.setMessage("您确定更新吗?当前版本:"+CommonUtil.getAppVersion(this).getVersionName()+",最新版本:"+mAppVersion.getVersionName()+",大小:"+mAppVersion.getFileSize()+"M")
		.setPositiveButton("更新", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//启动intentservice
				Intent downloadIntent = new Intent(UserSettingActivity.this, DownloadFileService.class);
				Bundle bundle = new Bundle();
		          bundle.putString("url", mAppVersion.getPath());
		          downloadIntent.putExtras(bundle);
		          startService(downloadIntent);
		          Toast.makeText(UserSettingActivity.this, "已开始下载最新版本的app", Toast.LENGTH_LONG).show();
		          // 设置广播接收器，当新版本的apk下载完成后自动弹出安装界面
		         IntentFilter intentFilter = new IntentFilter("com.maikejia.downloadComplete");
		         receiver = new BroadcastReceiver() {
		 
		             public void onReceive(Context context, Intent intent) {
		                 Intent install = new Intent(Intent.ACTION_VIEW);
		                 String pathString = intent.getStringExtra("downloadFile");
		                 install.setDataAndType(Uri.fromFile(new File(pathString)), "application/vnd.android.package-archive");
		                 install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		                 context.startActivity(install);
		             }
		         };
		         registerReceiver(receiver, intentFilter);
				
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

}
