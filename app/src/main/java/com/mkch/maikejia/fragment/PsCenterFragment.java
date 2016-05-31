package com.mkch.maikejia.fragment;

import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.UserAttentionActivity;
import com.mkch.maikejia.activity.UserCommentActivity;
import com.mkch.maikejia.activity.UserCreativeListActivity;
import com.mkch.maikejia.activity.UserFansListActivity;
import com.mkch.maikejia.activity.UserLoginActivity;
import com.mkch.maikejia.activity.UserNoticeActivity;
import com.mkch.maikejia.activity.UserRegPhoneActivity;
import com.mkch.maikejia.activity.UserSettingActivity;
import com.mkch.maikejia.activity.UserSettingPsInfoActivity;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.business.IUserBusiness;
import com.mkch.maikejia.business.imp.UserBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;
import com.mkch.maikejia.view.CustomSmartImageView;
import com.mkch.maikejia.view.PsCenterUserInfo;
import com.mkch.maikejia.view.UserSettingInfoListItem;

public class PsCenterFragment extends Fragment {
	private PullToRefreshScrollView mPullToRefreshScrollView;
	//设置
	private Button mBtnSetting;
	
	private TextView mTvLogin;
	private TextView mTvReg;
	
	private LinearLayout mLineUserInfo;
	private PsCenterUserInfo mLineUserInfoLogined;
	//登录状态下
	private CustomSmartImageView mUserHeadpic;
	private TextView mTvUserName;
	private TextView mTvUserSign;
	private TextView mTvCreativeNum;
	private TextView mTvDesignNum;
	private TextView mTvFansNum;
	
	//我的创意
	private UserSettingInfoListItem mUserSettingInfoListItemCreative;
	//我的关注
	private UserSettingInfoListItem mUserSettingInfoListItemAttention;
	//我的粉丝
	private UserSettingInfoListItem mUserSettingInfoListItemFans;
	//我的评论
	private UserSettingInfoListItem mUserSettingInfoListItemComment;
	//我的通知
	private UserSettingInfoListItem mUserSettingInfoListItemNotice;
	
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	private User mPscenterUser;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pscenter, container, false);
		findView(view);
		
		return view;
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		mPscenterUser = CommonUtil.getUserInfo(getActivity());//初始化用户信息
		initData();
	}
	
	/**
	 * 查找所有view
	 * @param view
	 */
	private void findView(View view) {
		mPullToRefreshScrollView = (PullToRefreshScrollView)view.findViewById(R.id.ptrsv_pscenter_scroll);
		
		mBtnSetting = (Button)view.findViewById(R.id.btn_pscenter_topbar_setting);
		
		mTvLogin = (TextView)view.findViewById(R.id.tv_pscenter_login);
		mTvReg = (TextView)view.findViewById(R.id.tv_pscenter_reg);
		
		mLineUserInfo = (LinearLayout)view.findViewById(R.id.line_pscenter_user_info);
		mLineUserInfoLogined = (PsCenterUserInfo)view.findViewById(R.id.line_pscenter_user_info_logined);
		
		mUserHeadpic = (CustomSmartImageView)view.findViewById(R.id.csiv_pscenter_user_info_headpic);
		mTvUserName = (TextView)view.findViewById(R.id.tv_pscenter_user_info_username);
		mTvUserSign = (TextView)view.findViewById(R.id.tv_pscenter_user_info_sign);
		
		mTvCreativeNum = (TextView)view.findViewById(R.id.tv_pscenter_user_info_creative_num);
		mTvDesignNum = (TextView)view.findViewById(R.id.tv_pscenter_user_info_design_num);
		mTvFansNum = (TextView)view.findViewById(R.id.tv_pscenter_user_info_fans_num);
		//列表跳转
		mUserSettingInfoListItemCreative = (UserSettingInfoListItem)view.findViewById(R.id.customview_pscenter_creative_list);
		mUserSettingInfoListItemAttention = (UserSettingInfoListItem)view.findViewById(R.id.customview_pscenter_user_attention);
		mUserSettingInfoListItemFans = (UserSettingInfoListItem)view.findViewById(R.id.customview_pscenter_user_fans);
		mUserSettingInfoListItemComment = (UserSettingInfoListItem)view.findViewById(R.id.customview_pscenter_user_comment);
		mUserSettingInfoListItemNotice = (UserSettingInfoListItem)view.findViewById(R.id.customview_pscenter_user_notice);
		initViewComponent();//初始化组合组件
	}
	/**
	 * 初始化组合组件
	 */
	private void initViewComponent() {
		mUserSettingInfoListItemCreative.setData(R.drawable.pscenter_list_creative, "我的创意");
		mUserSettingInfoListItemAttention.setData(R.drawable.pscenter_list_guanzhu, "我的关注");
		mUserSettingInfoListItemFans.setData(R.drawable.pscenter_list_fensi, "我的粉丝");
		mUserSettingInfoListItemComment.setData(R.drawable.pscenter_list_comment, "我的评论");
		mUserSettingInfoListItemNotice.setData(R.drawable.pscenter_list_notice, "我的通知");
	}


	/**
	 * 初始化数据
	 */
	private void initData() {
		
		if(mPscenterUser!=null){
			mLineUserInfo.setVisibility(View.GONE);
			mLineUserInfoLogined.setVisibility(View.VISIBLE);
			mUserHeadpic.setImageUrl(mPscenterUser.getUserImg(), R.drawable.creative_no_img);
			mTvUserName.setText(mPscenterUser.getUsername());
			//开启副线程-获取用户详情
			getUserDetailFromNet(mPscenterUser.getId());
		}else{
			mLineUserInfo.setVisibility(View.VISIBLE);
			mLineUserInfoLogined.setVisibility(View.GONE);
			//清0数据
			mTvCreativeNum.setText("0");
			mTvDesignNum.setText("0");
			mTvFansNum.setText("0");
			
			if(mPullToRefreshScrollView!=null&&mPullToRefreshScrollView.isRefreshing()){
				// Call onRefreshComplete when the list has been refreshed.
				mPullToRefreshScrollView.onRefreshComplete();
			}
			
		}
	}
	/**
	 * 获取用户详情数据
	 * @param userid
	 */
	private void getUserDetailFromNet(final int userid) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBusiness.getUserDetail(userid);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_pscenter_user_detail", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						fullPscenterUserDetail(jsonObj);
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_PSCENTER_LOGINED_USER_INFO_SUCCESS);
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
					CommonUtil.sendErrorMessage("个人中心："+CommonConstants.MSG_GET_ERROR,handler);
				}
			}
			/**
			 * 填充个人信息
			 * @param jsonObj
			 */
			private void fullPscenterUserDetail(JSONObject jsonObj) {
				mPscenterUser = CommonUtil.getUserInfo(getActivity());//初始化用户信息
				mPscenterUser.setId(JsonUtils.getInt(jsonObj, "id"));
				mPscenterUser.setUsername(JsonUtils.getString(jsonObj, "username"));
				mPscenterUser.setUserImg((JsonUtils.getString(jsonObj,"userImg")!=null&&!JsonUtils.getString(jsonObj,"userImg").equals(""))?(JsonUtils.getString(jsonObj,"userImg")+"!120.120"):null);
				mPscenterUser.setCreatives(JsonUtils.getInt(jsonObj, "creatives"));
				mPscenterUser.setDesigns(JsonUtils.getInt(jsonObj, "designs"));
				mPscenterUser.setFans(JsonUtils.getInt(jsonObj, "fans"));
				mPscenterUser.setUserSignature((JsonUtils.getString(jsonObj, "userSignature")!=null&&!JsonUtils.getString(jsonObj, "userSignature").equals(""))?JsonUtils.getString(jsonObj, "userSignature"):"他很忙,什么都没留下");
				mPscenterUser.setHasMobile(JsonUtils.getBoolean(jsonObj, "hasMobile"));
				mPscenterUser.setHasEmail(JsonUtils.getBoolean(jsonObj, "hasEmail"));
				mPscenterUser.setUnreadMsgCount(JsonUtils.getInt(jsonObj, "unreadMsgCount", 0));
				CommonUtil.saveUserInfo(mPscenterUser, getActivity());//重新覆盖最新的用户信息
			}
		}).start();
	}



	/**
	 * 设置监听器
	 */
	private void setListener() {
		mPullToRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				initData();
			}
		});
		
		mBtnSetting.setOnClickListener(new MyPscenterOnClickListener());
		mLineUserInfoLogined.setOnClickListener(new MyPscenterOnClickListener());
		
		mTvLogin.setOnClickListener(new MyPscenterOnClickListener());
		mTvReg.setOnClickListener(new MyPscenterOnClickListener());
		
		mUserSettingInfoListItemCreative.setOnClickListener(new MyPscenterOnClickListener());//我的创意
		mUserSettingInfoListItemAttention.setOnClickListener(new MyPscenterOnClickListener());//我的关注
		mUserSettingInfoListItemFans.setOnClickListener(new MyPscenterOnClickListener());//我的粉丝
		mUserSettingInfoListItemComment.setOnClickListener(new MyPscenterOnClickListener());//我的评论
		mUserSettingInfoListItemNotice.setOnClickListener(new MyPscenterOnClickListener());//我的通知
	}
	/**
	 * 自定义点击监听类
	 * @author JLJ
	 *
	 */
	private class MyPscenterOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent _intent = null;
			switch (v.getId()) {
			case R.id.btn_pscenter_topbar_setting:
				_intent = new Intent(getActivity(), UserSettingActivity.class);
				getActivity().startActivity(_intent);
				break;
			case R.id.line_pscenter_user_info_logined:
				_intent = new Intent(getActivity(), UserSettingPsInfoActivity.class);
				getActivity().startActivity(_intent);
				break;
			case R.id.tv_pscenter_login:
				_intent = new Intent(getActivity(), UserLoginActivity.class);
				getActivity().startActivity(_intent);
				break;
			case R.id.tv_pscenter_reg:
				_intent = new Intent(getActivity(), UserRegPhoneActivity.class);
				getActivity().startActivity(_intent);
				break;
			case R.id.customview_pscenter_creative_list:
				if(mPscenterUser!=null){
					_intent = new Intent(getActivity(), UserCreativeListActivity.class);
					getActivity().startActivity(_intent);
				}else{
					Toast.makeText(getActivity(), "亲，请先登录", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.customview_pscenter_user_attention:
				if(mPscenterUser!=null){
					_intent = new Intent(getActivity(), UserAttentionActivity.class);
					getActivity().startActivity(_intent);
				}else{
					Toast.makeText(getActivity(), "亲，请先登录", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.customview_pscenter_user_fans:
				if(mPscenterUser!=null){
					_intent = new Intent(getActivity(), UserFansListActivity.class);
					getActivity().startActivity(_intent);
				}else{
					Toast.makeText(getActivity(), "亲，请先登录", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.customview_pscenter_user_comment:
				if(mPscenterUser!=null){
					_intent = new Intent(getActivity(), UserCommentActivity.class);
					getActivity().startActivity(_intent);
				}else{
					Toast.makeText(getActivity(), "亲，请先登录", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.customview_pscenter_user_notice:
				if(mPscenterUser!=null){
					_intent = new Intent(getActivity(), UserNoticeActivity.class);
					getActivity().startActivity(_intent);
				}else{
					Toast.makeText(getActivity(), "亲，请先登录", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
		
	}
	
	//处理消息队列
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int flag = msg.what;
			switch (flag) {
			case 0:
				String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
				Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
				break;
			case CommonConstants.FLAG_GET_PSCENTER_LOGINED_USER_INFO_SUCCESS:
				updatePsCenterUserInfoFromNet();
				break;
			default:
				break;
			}
			
			if(mPullToRefreshScrollView!=null&&mPullToRefreshScrollView.isRefreshing()){
				mPullToRefreshScrollView.onRefreshComplete();
			}
		}


		
	};
	/**
	 * 刷新UI中的用户信息
	 */
	private void updatePsCenterUserInfoFromNet() {
		mUserHeadpic.setImageUrl(mPscenterUser.getUserImg(), R.drawable.creative_no_img);
		mTvUserName.setText(mPscenterUser.getUsername());
		mTvUserSign.setText("签名:"+mPscenterUser.getUserSignature());
		mTvCreativeNum.setText(String.valueOf(mPscenterUser.getCreatives()));
		mTvDesignNum.setText(String.valueOf(mPscenterUser.getDesigns()));
		mTvFansNum.setText(String.valueOf(mPscenterUser.getFans()));
	}
}
