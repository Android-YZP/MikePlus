package com.mkch.maikejia.fragment;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.UserNoticeDetailActivity;
import com.mkch.maikejia.adapter.UserMessageListAdapter;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.bean.UserMessage;
import com.mkch.maikejia.business.IUserBusiness;
import com.mkch.maikejia.business.imp.UserBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;

public class UserNoticeListSocailFragment extends Fragment {
	private PullToRefreshListView mPullToRefreshListView;
	private UserMessageListAdapter mUserMessageListAdapter;
	private List<UserMessage> mUserMessages;
	
	private TextView mTvUserMessagesListNone;
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	
	private static int pageNo = 1;
	private static int pageSize = 10;
	private static int type = 2;//社区消息
	
	private static User mUser;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fans_list, container, false);
		findView(view);
		return view;
	}
	
	private void findView(View view) {
		mPullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.lv_user_fans_list);
		mTvUserMessagesListNone = (TextView)view.findViewById(R.id.tv_user_fans_none);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		setListenser();
	}

	private void initData() {
		if (getActivity()!=null) {
			mUser = CommonUtil.getUserInfo(getActivity());
		}
		//初始化通知列表
		initDataMessagesList();
	}

	/**
	 * 从网络中首次获取通知列表数据
	 */
	private void initDataMessagesList() {
		//开启副线程-获取通知列表
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mUserMessages = new ArrayList<UserMessage>();
					//得到第一页的数据
					getFirstPageData();
				} catch (ConnectTimeoutException e) {
					e.printStackTrace();
					sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT);
				}catch (SocketTimeoutException e) {
					e.printStackTrace();
					sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
				}catch (ServiceException e) {
					e.printStackTrace();
					sendErrorMessage(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					sendErrorMessage("系统公告："+CommonConstants.MSG_GET_ERROR);
				}
			}
		}).start();
	}

	private void setListenser() {
		//刷新listview
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				//刷新第一页的数据
				loadFirstPageData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				//加载第二页的数据
				loadNextPageData();
			}

			
			
		});
		//点击listview某一项
//		mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int position,
//					long id) {
//				UserMessage _user_message = mUserMessages.get(position-1);
//				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_comments_item_click", "|通知="+_user_message.toString());
//				int _notice_id = _user_message.getId();
//		if (getActivity()!=null) {
//				Intent _intent = new Intent(getActivity(),UserNoticeDetailActivity.class);
//				_intent.putExtra("_notice_id", _notice_id);
//				getActivity().startActivity(_intent);
//		}
//			}
//		});
		
		ListView mlvUserAttentionCreativeList = mPullToRefreshListView.getRefreshableView();
		mlvUserAttentionCreativeList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
		
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_comments_item_long_click", "|移除通知");
				menu.add(1, 1, 0, "移除通知");//groupID=1 itemID=1 orderId=0
			}
		});
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int _position = info.position-1;
		UserMessage _user_message = mUserMessages.get(_position);
		switch (item.getItemId()) {
		case 1:
			Log.d(CommonConstants.LOGCAT_TAG_NAME+"_item_click_0", "移除通知");
			if(_user_message.getId()!=0){
				int messageId = _user_message.getId();
				userMessageCancel(messageId,_position);//发起网络请求移除通知
			}
			break;
		}
		return super.onContextItemSelected(item);
	}
	/**
	 * 发起网络请求移除通知
	 * @param contentId
	 */
	private void userMessageCancel(final int messageId,final int position) {
		//开启副线程-移除通知
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBusiness.deleteUserMessage(messageId,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_deleteUserMessage", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						mUserMessages.remove(position);
						//移除成功
						handler.sendEmptyMessage(CommonConstants.FLAG_CANCEL_USER_MESSAGES_SYS_PUB_SUCCESS);
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
				}catch (ServiceException e) {
					e.printStackTrace();
					sendErrorMessage(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					sendErrorMessage("移除通知："+CommonConstants.MSG_GET_ERROR);
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
	 * 加载第一页的数据
	 */
	private void loadFirstPageData() {
		//开启副线程-获取通知列表
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);//沉睡2S
					//得到第一页的数据
					getFirstPageData();
				} catch (ConnectTimeoutException e) {
					e.printStackTrace();
					sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT);
				}catch (SocketTimeoutException e) {
					e.printStackTrace();
					sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
				}catch (ServiceException e) {
					e.printStackTrace();
					sendErrorMessage(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					sendErrorMessage("社区消息："+CommonConstants.MSG_GET_ERROR);
				}
			}

			
		}).start();
	}
	
	/**
	 * 获取第一页数据
	 * @throws Exception
	 */
	private void getFirstPageData() throws Exception {
		pageNo = 1;//重置页数
		String result = mUserBusiness.getUserMessagesList(pageNo,pageSize,mUser,type);
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_getUserMessagesList", result);
		JSONObject jsonObj = new JSONObject(result);
		boolean Success = jsonObj.getBoolean("success");
		int totalPage = jsonObj.getInt("totalPage");
		if(Success){
			JSONArray _json_array = jsonObj.getJSONArray("datas");
			if(pageNo>totalPage||(_json_array!=null&&_json_array.length()==0)){
				handler.sendEmptyMessage(CommonConstants.FLAG_GET_LIST_NO_DATA);
				return;
			}
			if(_json_array.length()>0){
				mUserMessages.clear();//重置list
				for (int i = 0; i < _json_array.length(); i++) {
					JSONObject _jsonObject = _json_array.getJSONObject(i);
					UserMessage _user_message = new UserMessage();
					_user_message.setId(JsonUtils.getInt(_jsonObject, "id",0));
					_user_message.setTitle(JsonUtils.getString(_jsonObject, "title"));
					_user_message.setType(JsonUtils.getInt(_jsonObject,"type",0));
					_user_message.setStatus(JsonUtils.getBoolean(_jsonObject, "status"));
					_user_message.setDate(JsonUtils.getString(_jsonObject, "date"));
					_user_message.setRedirectType(JsonUtils.getInt(_jsonObject, "redirectType",0));
					_user_message.setRedirectId(JsonUtils.getInt(_jsonObject, "redirectId",0));
					mUserMessages.add(_user_message);
				}
			}
			//获取成功
			handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_SUCCESS);
		}else{
			//获取错误代码，并查询出错误文字
			String errorMsg = jsonObj.getString("errorMsg");
			sendErrorMessage(errorMsg);
		}
	}
	
	/**
	 * 加载第二页的数据
	 */
	private void loadNextPageData() {
		//开启副线程-获取通知列表
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					String result = mUserBusiness.getUserMessagesList(pageNo,pageSize,mUser,type);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_getUserMessagesList_again", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					int totalPage = jsonObj.getInt("totalPage");
					if(Success){
						JSONArray _json_array = jsonObj.getJSONArray("datas");
						if(pageNo>totalPage||(_json_array!=null&&_json_array.length()==0)){
							String errorMsg = "没有更多数据了";
							sendErrorMessage(errorMsg);
							return;
						}
						if(_json_array.length()>0){
							for (int i = 0; i < _json_array.length(); i++) {
								JSONObject _jsonObject = _json_array.getJSONObject(i);
								UserMessage _user_message = new UserMessage();
								_user_message.setId(JsonUtils.getInt(_jsonObject, "id",0));
								_user_message.setTitle(JsonUtils.getString(_jsonObject, "title"));
								_user_message.setType(JsonUtils.getInt(_jsonObject,"type",0));
								_user_message.setStatus(JsonUtils.getBoolean(_jsonObject, "status"));
								_user_message.setDate(JsonUtils.getString(_jsonObject, "date"));
								_user_message.setRedirectType(JsonUtils.getInt(_jsonObject, "redirectType",0));
								_user_message.setRedirectId(JsonUtils.getInt(_jsonObject, "redirectId",0));
								mUserMessages.add(_user_message);
							}
						}
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_AGAIN_SUCCESS);
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
				}catch (ServiceException e) {
					e.printStackTrace();
					sendErrorMessage(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					sendErrorMessage("社区消息："+CommonConstants.MSG_GET_ERROR);
				}
			}
		}).start();
	}
	
	
	//处理消息队列
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int flag = msg.what;
			String errorMsg = null;
			switch (flag) {
			case 0:
				errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
				try {
					if(getActivity()!=null) Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case CommonConstants.FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_SUCCESS:
				updateUserMessagesListFromNet();
				break;
			case CommonConstants.FLAG_GET_USER_MESSAGES_LIST_SYS_PUB_AGAIN_SUCCESS:
				updateUserMessagesListFromNetByRefresh();
				break;
			case CommonConstants.FLAG_CANCEL_USER_MESSAGES_SYS_PUB_SUCCESS:
				updateUserMessagesListFromNetByCancel();
				break;
			case CommonConstants.FLAG_GET_LIST_NO_DATA:
				break;
			default:
				break;
			}
			// Call onRefreshComplete when the list has been refreshed.
			mPullToRefreshListView.onRefreshComplete();
			
			setListNoDataEmptyView(errorMsg);
		}

		
	};
	/**
	 * 从网络初次加载-社区消息列表
	 */
	private void updateUserMessagesListFromNet() {
		if (getActivity()!=null) {
			mUserMessageListAdapter = new UserMessageListAdapter(getActivity(), mUserMessages);
			mPullToRefreshListView.setAdapter(mUserMessageListAdapter);
			pageNo++;
		}
	}
	/**
	 * 移除某个通知后，刷新UI通知列表
	 */
	protected void updateUserMessagesListFromNetByCancel() {
		mUserMessageListAdapter.notifyDataSetChanged();
	}

	/**
	 * 从网络更新社区消息列表
	 */
	private void updateUserMessagesListFromNetByRefresh(){
		mUserMessageListAdapter.notifyDataSetChanged();
		mPullToRefreshListView.getRefreshableView().setSelection((pageNo-1)*pageSize);
		pageNo++;
		
	}

	/**
	 * 设置空数据时的emptyView
	 * @param errorMsg
	 */
	protected void setListNoDataEmptyView(String errorMsg) {
		if(errorMsg!=null&&!errorMsg.equals("")){
			mTvUserMessagesListNone.setText(errorMsg);
		}else{
			mTvUserMessagesListNone.setText("没有数据");
		}
		mPullToRefreshListView.setEmptyView(mTvUserMessagesListNone);
	}
}
