package com.mkch.maikejia.activity;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.mkch.maikejia.util.CheckUtil;
import com.mkch.maikejia.util.CommonUtil;

public class UserSettingUpdatePwdActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	//旧密码、新密码和确认密码
	private EditText mEtOldPwd;
	private EditText mEtNewPwd;
	private EditText mEtAgainPwd;
	private Button mBtnSure;
	
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	private User mUser;
	
	private static ProgressDialog mProgressDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_setting_update_pwd);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		//用户名、密码和确认密码
		mEtOldPwd = (EditText)findViewById(R.id.et_user_setting_update_pwd_oldpwd);
		mEtNewPwd = (EditText)findViewById(R.id.et_user_setting_update_pwd_newpwd);
		mEtAgainPwd = (EditText)findViewById(R.id.et_user_setting_update_pwd_againpwd);
		
		mBtnSure = (Button)findViewById(R.id.btn_user_setting_update_pwd_commit);
	}

	private void initData() {
		mTvTitle.setText("修改密码");
		mUser = CommonUtil.getUserInfo(this);
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserSettingUpdatePwdActivity.this.finish();
			}
		});
		//修改
		mBtnSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String oldpwd = mEtOldPwd.getText().toString();
				String newpwd = mEtNewPwd.getText().toString();
				String againpwd = mEtAgainPwd.getText().toString();
				if(oldpwd==null||oldpwd.equals("")){
					Toast.makeText(UserSettingUpdatePwdActivity.this, "您未填写原密码", Toast.LENGTH_SHORT).show();
					return;
				}
				if(newpwd==null||newpwd.equals("")){
					Toast.makeText(UserSettingUpdatePwdActivity.this, "您未填写新密码", Toast.LENGTH_SHORT).show();
					return;
				}
				if(againpwd==null||againpwd.equals("")){
					Toast.makeText(UserSettingUpdatePwdActivity.this, "您未填写确认密码", Toast.LENGTH_SHORT).show();
					return;
				}
				if(!newpwd.equals(againpwd)){
					Toast.makeText(UserSettingUpdatePwdActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
					return;
				}
				//检查密码、确认密码的输入规则是否输入有误
				if(newpwd.length()<6||newpwd.length()>15||!CheckUtil.checkPassword(newpwd)){
					Toast.makeText(UserSettingUpdatePwdActivity.this, "密码格式是6到15位的字母数字组成", Toast.LENGTH_SHORT).show();
					return;
				}
				
				userUpdatePwdFromNet(oldpwd,newpwd,againpwd);//用户修改密码
			}

			/**
			 * 用户修改密码
			 * @param username
			 * @param password
			 * @param againpwd
			 */
			private void userUpdatePwdFromNet(final String oldpwd,final String newpwd,final String againpwd) {
				//弹出加载进度条
				mProgressDialog = ProgressDialog.show(UserSettingUpdatePwdActivity.this, "请稍等", "正在玩命修改中...",true,true);
				//开启副线程-修改密码
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String result = mUserBusiness.updateUserPwd(oldpwd,newpwd,againpwd,mUser);
							Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_setting_updateUserPwd", result);
							JSONObject jsonObj = new JSONObject(result);
							boolean Success = jsonObj.getBoolean("success");
							if(Success){
								//获取成功
								handler.sendEmptyMessage(CommonConstants.FLAG_UPDATE_USER_PWD_SUCCESS);
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
							e.printStackTrace();
							//what = 0;sendmsg 0;
							CommonUtil.sendErrorMessage("修改密码："+CommonConstants.MSG_GET_ERROR,handler);
						}
					}
				}).start();
			}
		});
	}
	
	private static class MyHandler extends Handler{
		private final WeakReference<Activity> mActivity;
		public MyHandler(UserSettingUpdatePwdActivity activity) {
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
				((UserSettingUpdatePwdActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_UPDATE_USER_PWD_SUCCESS:
				((UserSettingUpdatePwdActivity)mActivity.get()).updateUserPwdSuccess();
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

	/**
	 * 修改密码-成功
	 */
	public void updateUserPwdSuccess() {
		Toast.makeText(this, "修改密码成功", Toast.LENGTH_LONG).show();
		CommonUtil.clearUserInfo(this);//清除用户信息
		//跳转到用户登录界面
		Intent _intent = new Intent(this,UserLoginActivity.class);
		startActivity(_intent);
		this.finish();
	}

	
}
