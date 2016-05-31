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
import com.mkch.maikejia.util.CommonUtil;

public class CreativeDeployDescActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	private EditText mEtCreativeDescSource;
	private EditText mEtCreativeDescDetail;
	
	private CheckBox mCbCreativeIsRead;
	private TextView mTvCreativeIsRead;
	
	private TextView mTvCreativeDescProtocol;
	
	private Button mBtnCreativeCommit;
	
	
	
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
		
	private static ProgressDialog mProgressDialog = null;
	
	private User mUser;
	private String mTitle;
	private String mIntro;
	private String mSource;
	private String mDetail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creative_deploy_desc);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		
		mEtCreativeDescSource = (EditText)findViewById(R.id.et_deploy_desc_source);
		mEtCreativeDescDetail = (EditText)findViewById(R.id.et_deploy_desc_detail);
		
		mBtnCreativeCommit = (Button)findViewById(R.id.btn_deploy_desc_commit);
		mTvCreativeDescProtocol = (TextView)findViewById(R.id.tv_deploy_desc_read_protocal);
		mTvCreativeIsRead = (TextView)findViewById(R.id.tv_deploy_protocal_isread);
		
		mCbCreativeIsRead = (CheckBox)findViewById(R.id.cb_deploy_protocal_isread);
	}

	private void initData() {
		mTvTitle.setText("发布创意");
		mUser = CommonUtil.getUserInfo(this);
		Bundle _bundle = getIntent().getExtras();
		if(_bundle!=null){
			mTitle = _bundle.getString("_title");
			mIntro = _bundle.getString("_intro");
		}
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				CreativeDeployDescActivity.this.finish();
			}
		});
		
		mBtnCreativeCommit.setOnClickListener(new CreativeDepolyDescOnClickListener());
		mTvCreativeDescProtocol.setOnClickListener(new CreativeDepolyDescOnClickListener());
		mTvCreativeIsRead.setOnClickListener(new CreativeDepolyDescOnClickListener());
	}
	
	private class CreativeDepolyDescOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_deploy_desc_commit:
				//提交
				UserCommitCreative();
				break;
			case R.id.tv_deploy_desc_read_protocal:
				//平台指南
				Intent _intent = new Intent(CreativeDeployDescActivity.this,ArticleDetailActivity.class);
				_intent.putExtra("_article_title", "平台指南");
				_intent.putExtra("_article_id", 7);//创意指南
				CreativeDeployDescActivity.this.startActivity(_intent);
				break;
			case R.id.tv_deploy_protocal_isread:
				//是否已读
				if(mCbCreativeIsRead.isChecked()){
					mCbCreativeIsRead.setChecked(false);
				}else{
					mCbCreativeIsRead.setChecked(true);
				}
				break;
			default:
				break;
			}
		}
		
	}

	/**
	 * 用户提交创意
	 */
	public void UserCommitCreative() {
		if(mEtCreativeDescSource!=null){
			mSource = mEtCreativeDescSource.getText().toString();
			if(mSource==null||mSource.equals("")){
				Toast.makeText(CreativeDeployDescActivity.this, "亲，您未填写创意来源", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		if(mEtCreativeDescDetail!=null){
			mDetail = mEtCreativeDescDetail.getText().toString();
			if(mDetail==null||mDetail.equals("")){
				Toast.makeText(CreativeDeployDescActivity.this, "亲，您未填写创意详情", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		if(!mCbCreativeIsRead.isChecked()){
			Toast.makeText(CreativeDeployDescActivity.this, "亲，您未阅读平台协议", Toast.LENGTH_SHORT).show();
			return;
		}
		//弹出加载进度条
		mProgressDialog = ProgressDialog.show(CreativeDeployDescActivity.this, "请稍等", "正在玩命保存中...",true,true);
		//开启副线程-提交创意
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean draft = true;//true 草稿;false 正式
					String result = mUserBusiness.userCommitCreative(mTitle,mIntro,mSource,mDetail,draft,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_userCommitCreative", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_CREATIVE_DETAIL_SUCCESS);
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
					sendErrorMessage("发布创意："+CommonConstants.MSG_GET_ERROR);
				}
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
		public MyHandler(CreativeDeployDescActivity activity) {
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
				((CreativeDeployDescActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_GET_CREATIVE_DETAIL_SUCCESS:
				((CreativeDeployDescActivity)mActivity.get()).deployCreativeSuccess();
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
	 * 发布创意成功
	 */
	public void deployCreativeSuccess() {
		Toast.makeText(this, "已保存到个人中心，请到平台完善", Toast.LENGTH_LONG).show();
		CreativeDeployDescActivity.this.finish();
	}
}
