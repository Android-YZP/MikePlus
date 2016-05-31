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
import android.widget.CheckBox;
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

public class UserSettingSecurityPhoneActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	private Button mBtnCommitPhone;
	//手机号
	private EditText mEtPhone;
	
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	private String mPhone;
	private User mUser;
	
	private static ProgressDialog mProgressDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_setting_security_phone);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		mBtnCommitPhone = (Button)findViewById(R.id.btn_user_reg_getcode_phone_commit);
		
		mEtPhone = (EditText)findViewById(R.id.et_user_reg_phone);
	}

	private void initData() {
		mTvTitle.setText("绑定新手机");
		mUser = CommonUtil.getUserInfo(this);
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserSettingSecurityPhoneActivity.this.finish();
			}
		});
		mBtnCommitPhone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserCommitPhoneNumer();//用户提交手机号码
			}
		});
	}
	
	
	private static class MyHandler extends Handler{
		private final WeakReference<Activity> mActivity;
		public MyHandler(UserSettingSecurityPhoneActivity activity) {
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
				((UserSettingSecurityPhoneActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_GET_USER_SETTING_SECURITY_CODE_SUCCESS:
				((UserSettingSecurityPhoneActivity)mActivity.get()).goNextActivity();
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
	 * 进入下一个注册流程
	 */
	public void goNextActivity() {
		Intent _intent = new Intent(UserSettingSecurityPhoneActivity.this,UserSettingSecurityPhoneCodeActivity.class);
		_intent.putExtra("_phone", mPhone);
		startActivity(_intent);
		UserSettingSecurityPhoneActivity.this.finish();
	}
	
	/**
	 * 用户提交手机号码
	 */
	public void UserCommitPhoneNumer() {
		mPhone = mEtPhone.getText().toString();
		if(mPhone!=null&&!mPhone.equals("")){
			if(!CheckUtil.checkMobile(mPhone)){
				Toast.makeText(UserSettingSecurityPhoneActivity.this, "手机号格式输入有误", Toast.LENGTH_LONG).show();
				return;
			}
			
			//弹出加载进度条
			mProgressDialog = ProgressDialog.show(UserSettingSecurityPhoneActivity.this, "请稍等", "正在玩命获取中...",true,true);
			
			//开启副线程-检查手机号码是否存在
			checkPhoneFromNet(mPhone);
			//若检查不通过、提示报错信息；检查通过，跳转到下一个界面
			//进度条消失
		}else{
			Toast.makeText(UserSettingSecurityPhoneActivity.this, "您未填写手机号", Toast.LENGTH_SHORT).show();
		}
		
	}
	/**
	 * 开启副线程-检查手机号码是否存在
	 * @param phone
	 */
	private void checkPhoneFromNet(final String phone) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBusiness.getUserSettingSecurityCode(phone,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_user_setting_security_getUserSettingSecurityCode", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_SETTING_SECURITY_CODE_SUCCESS);
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
					CommonUtil.sendErrorMessage("安全中心-获取验证码："+CommonConstants.MSG_GET_ERROR,handler);
				}
			}
		}).start();
	}
}
