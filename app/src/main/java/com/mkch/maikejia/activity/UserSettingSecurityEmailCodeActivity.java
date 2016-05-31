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
import android.text.Html;
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
import com.mkch.maikejia.util.CommonUtil;

public class UserSettingSecurityEmailCodeActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	private Button mBtnCommitCode;
	
	//邮箱号码和验证码
	private String mEmail;
	private String mEmailCode;
	
	//邮箱介绍信息
	private TextView mTvEmailInfo;
	private TextView mTvGetEmailAgain;
	
	//验证码
	private EditText mEtEmailCode;
	
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	
	private static ProgressDialog mProgressDialog = null;
	
	//定义倒计时handler
	private static Handler getcodeHandler;
	private int num;
	private User mUser;
	
	private Thread mDownTimeThread;
	private boolean mStopThread = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_setting_security_email_code);
		initView();
		initData();
		setListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mStopThread = true;//中断线程
	}
	
	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		mBtnCommitCode = (Button)findViewById(R.id.btn_user_setting_security_email_getcode_code_commit);
		
		//邮箱号码介绍信息
		mTvEmailInfo = (TextView)findViewById(R.id.tv_user_setting_security_email_intro);
		//验证码
		mEtEmailCode = (EditText)findViewById(R.id.et_user_setting_security_email_code);
	}

	private void initData() {
		mTvTitle.setText("邮箱验证码");
		mUser = (User)CommonUtil.getUserInfo(this);
		//初始化email字符串和显示说明信息
		Bundle _bundle = getIntent().getExtras();
		if(_bundle!=null){
			mEmail = _bundle.getString("_email");
			mTvEmailInfo.setText(Html.fromHtml("您的邮箱<font color='#d81759'>"+mEmail+"</font>会收到一条含有4位数字的验证码"));
		}
		//60s后重新获取验证码
		canGetSmsCodeAgain();
	}

	/**
	 * 是否可以再次发送验证码
	 */
	private void canGetSmsCodeAgain() {
		//倒计时获取验证码
		mTvGetEmailAgain = (TextView)findViewById(R.id.tv_user_setting_security_email_code_getcode_again);
		getcodeHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				//接文字更新页面
				int flag = msg.what;
				if(flag==CommonConstants.FLAG_GET_REG_MOBILEMGS_VALIDATE_CAN_GET_AGAIN_SUCCESS){
					int num = msg.getData().getInt("number");
					if(num==0){
						//倒计时结束
						mTvGetEmailAgain.setText("重新发送验证码");
					}else{
						//倒计时
						mTvGetEmailAgain.setText("重新发送验证码("+num+"s)");
					}
				}
				
			}
		};
		getCodeFromNet();
	}

	private void getCodeFromNet() {
		mDownTimeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				int i;
				for (i = 60; i >=0; i--) {
					try {
						if(mStopThread){
							break;
						}
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putInt("number", i);
					msg.what = CommonConstants.FLAG_GET_REG_MOBILEMGS_VALIDATE_CAN_GET_AGAIN_SUCCESS;
					msg.setData(data);
					getcodeHandler.sendMessage(msg);
				}
				
			}
		});
		mDownTimeThread.start();
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserSettingSecurityEmailCodeActivity.this.finish();
			}
		});
		mTvGetEmailAgain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String getcodeText = mTvGetEmailAgain.getText().toString();
				if (getcodeText!=null&&getcodeText.equals("重新发送验证码")) {
					//重新发送验证码到该邮箱
					checkEmailFromNetGetSmsCodeAgain(mEmail);
				}else{
					Toast.makeText(UserSettingSecurityEmailCodeActivity.this, "邮箱验证码正在发送,请耐心等待", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mBtnCommitCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mEmailCode = mEtEmailCode.getText().toString();
				if(mEmailCode!=null&&!mEmailCode.equals("")){
					//弹出加载进度条
					mProgressDialog = ProgressDialog.show(UserSettingSecurityEmailCodeActivity.this, "请稍等", "正在玩命提交中...",true,true);
					
					//开启副线程-验证是否是该邮箱收到了验证码
					checkEmailCodeFromNet(mEmailCode);
					//若检查不通过、提示报错信息；检查通过，跳转到下一个界面
					//进度条消失
				}else{
					Toast.makeText(UserSettingSecurityEmailCodeActivity.this, "您未填写邮箱验证码", Toast.LENGTH_SHORT).show();
				}

			}

			/**
			 * 开启副线程-验证是否是该邮箱收到了验证码
			 * @param emailCode
			 */
			private void checkEmailCodeFromNet(final String emailCode) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String result = mUserBusiness.getUserSettingSecurityEmailUpdate(mEmail,emailCode,mUser);
							Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_security_code_getUserSettingSecurityEmailUpdate", result);
							JSONObject jsonObj = new JSONObject(result);
							boolean Success = jsonObj.getBoolean("success");
							if(Success){
								//获取成功
								handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_SETTING_SECURITY_CODE_CHECK_SUCCESS);
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
							CommonUtil.sendErrorMessage("安全中心-验证邮箱验证码："+CommonConstants.MSG_GET_ERROR,handler);
						}
					}
				}).start();
			}
		});
	}
	
	
	private static class MyHandler extends Handler{
		private final WeakReference<Activity> mActivity;
		public MyHandler(UserSettingSecurityEmailCodeActivity activity) {
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
				((UserSettingSecurityEmailCodeActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_GET_USER_SETTING_SECURITY_CODE_CHECK_SUCCESS:
				//修改成功
				((UserSettingSecurityEmailCodeActivity)mActivity.get()).checkSuccess();
				break;
			case CommonConstants.FLAG_GET_USER_SETTING_SECURITY_CODE_SEND_AGAIN_SUCCESS:
				//重新发送验证码
				((UserSettingSecurityEmailCodeActivity)mActivity.get()).sendAgainSuccess();
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
	 * 重新发送成功
	 */
	public void sendAgainSuccess() {
		getCodeFromNet();//重新倒计时
		Toast.makeText(UserSettingSecurityEmailCodeActivity.this, "验证码已发送到邮箱,请注意查收", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 检查成功-修改成功
	 */
	public void checkSuccess() {
		//覆盖保存
		mUser.setHasEmail(true);
		CommonUtil.saveUserInfo(mUser, this);
		Toast.makeText(UserSettingSecurityEmailCodeActivity.this, "已绑定", Toast.LENGTH_LONG).show();
		this.finish();
	}

	//重新获取验证码
	private void checkEmailFromNetGetSmsCodeAgain(final String email) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBusiness.getUserSettingSecurityEmailCode(email, mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_getUserSettingSecurityEmailCode_again", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_SETTING_SECURITY_CODE_SEND_AGAIN_SUCCESS);
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
					CommonUtil.sendErrorMessage("安全中心-重新获取邮箱验证码："+CommonConstants.MSG_GET_ERROR,handler);
				}
			}
		}).start();
	}
}
