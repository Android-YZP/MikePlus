package com.mkch.maikejia.activity;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mkch.maikejia.R;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.business.IUserBusiness;
import com.mkch.maikejia.business.imp.UserBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.view.CustomSmartImageView;

public class UserSettingPsInfoActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	private TextView mTvCompleted;
	
	private CustomSmartImageView mIvUserHeadpic;
	
//	private TextView mTvUsername;
//	private EditText mEtRealname;
	private EditText mEtUsersign;
	
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	private String mRealname;
	private String mUsersign;
	
	private static ProgressDialog mProgressDialog = null;
	private User mUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_setting_info);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_user_setting_info_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_user_setting_info_topbar_title);
		mTvCompleted = (TextView)findViewById(R.id.tv_user_setting_info_topbar_completed);
		
		mUser = CommonUtil.getUserInfo(this);
		
		mIvUserHeadpic = (CustomSmartImageView)findViewById(R.id.iv_user_setting_info_headpic);
//		mTvUsername = (TextView)findViewById(R.id.tv_user_setting_info_username);
//		mEtRealname = (EditText)findViewById(R.id.et_user_setting_info_realname);
		mEtUsersign = (EditText)findViewById(R.id.et_user_setting_info_usersign);
	}

	private void initData() {
		mTvTitle.setText("个人资料");
		if(mUser!=null){
			mIvUserHeadpic.setImageUrl(mUser.getUserImg(), R.drawable.pscenter_user_setting_gerenziliao);
			
//			mTvUsername.setText(mUser.getUsername());
//			mEtRealname.setText(mUser.getRealname());
			mEtUsersign.setText(mUser.getUserSignature());
			
		}
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserSettingPsInfoActivity.this.finish();
			}
		});
		
		mTvCompleted.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
//				mRealname = mEtRealname.getText().toString().trim();
				mUsersign = mEtUsersign.getText().toString().trim();
				//从网络-修改个人资料
				updateUserInfoFromNet();
			}
		});
	}

	/**
	 * 从网络-修改个人资料
	 * @param _realname
	 * @param _usersign
	 */
	protected void updateUserInfoFromNet() {
		//弹出加载进度条
		mProgressDialog = ProgressDialog.show(UserSettingPsInfoActivity.this, "请稍等", "正在玩命修改中...",true,true);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBusiness.updateUserSettingPsInfo(mUsersign,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_updateUserSettingPsInfo", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_UPDATE_USER_INFO_SUCCESS);
					}else{
						//获取错误代码，并查询出错误文字
						String errorMsg = jsonObj.getString("errorMsg");
						CommonUtil.sendErrorMessage(errorMsg,handler);
					}
				} catch (ConnectTimeoutException e) {
					e.printStackTrace();
					CommonUtil.sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT,handler);
				}catch (SocketTimeoutException e) {
					e.printStackTrace();
					CommonUtil.sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT,handler);
				}
				catch (ServiceException e) {
					e.printStackTrace();
					CommonUtil.sendErrorMessage(e.getMessage(),handler);
				} catch (Exception e) {
					//what = 0;sendmsg 0;
					CommonUtil.sendErrorMessage("安全中心-验证验证码："+CommonConstants.MSG_GET_ERROR,handler);
				}
			}
		}).start();
	}
	
	
	private static class MyHandler extends Handler{
		private final WeakReference<Activity> mActivity;
		public MyHandler(UserSettingPsInfoActivity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			if(mProgressDialog!=null){
				mProgressDialog.dismiss();
			}
			int flag = msg.what;
			switch (flag) {
			case 0:
				String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
				((UserSettingPsInfoActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_UPDATE_USER_INFO_SUCCESS:
				//修改成功
				((UserSettingPsInfoActivity)mActivity.get()).updateUserInfo();
				break;
			default:
				break;
			}
		}
	}
	
	private MyHandler handler = new MyHandler(this);
	
	private void showTip(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void updateUserInfo() {
		mUser.setRealname(mRealname);
		mUser.setUserSignature(mUsersign);
		CommonUtil.saveUserInfo(mUser, this);
		Toast.makeText(this, "已修改", Toast.LENGTH_LONG).show();
		UserSettingPsInfoActivity.this.finish();
	}
}
