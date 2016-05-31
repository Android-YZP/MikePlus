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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.CreativeDetailActivity;
import com.mkch.maikejia.adapter.UserAttentionUserListAdapter;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.business.IUserBusiness;
import com.mkch.maikejia.business.imp.CreativeBusinessImp;
import com.mkch.maikejia.business.imp.UserBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;

public class UserFansListFragment extends Fragment {
	private PullToRefreshListView mPullToRefreshListView;
	private UserAttentionUserListAdapter mUserFansListAdapter;
	private List<User> mUserList;
	
	private TextView mTvUserFansListNone;
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	
	private int pageNo = 1;
	private int pageSize = 10;
	
	private User mUser;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fans_list, container, false);
		findView(view);
		return view;
	}
	
	private void findView(View view) {
		mPullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.lv_user_fans_list);
		mTvUserFansListNone = (TextView)view.findViewById(R.id.tv_user_fans_none);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		setListenser();
	}

	private void initData() {
		mUser = CommonUtil.getUserInfo(getActivity());
		//初始化粉丝列表
		initDataFansList();
	}

	/**
	 * 从网络中首次获取创意列表数据
	 */
	private void initDataFansList() {
		//开启副线程-获取粉丝列表
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mUserList = new ArrayList<User>();
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
					sendErrorMessage("我的粉丝："+CommonConstants.MSG_GET_ERROR);
				}
			}
		}).start();
	}

	private void setListenser() {
		mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_fans_item_click", "|="+position);
			}
		});
		
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
		
		ListView mlvUserAttentionCreativeList = mPullToRefreshListView.getRefreshableView();
		mlvUserAttentionCreativeList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
		
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_fans_item_long_click", "|移除粉丝");
				menu.add(0, 0, 0, "移除粉丝");//groupID=0 itemID=0 orderId=0
			}
		});
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int _position = info.position-1;
		User _user = mUserList.get(_position);
		switch (item.getItemId()) {
		case 0:
			Log.d(CommonConstants.LOGCAT_TAG_NAME+"_item_click_0", "移除粉丝");
			if(_user.getId()!=0){
				int friendId = _user.getId();
				userAttentionCancel(friendId,_position);//发起网络请求移除粉丝
			}
			break;
		}
		return super.onContextItemSelected(item);
	}
	/**
	 * 发起网络请求移除粉丝
	 * @param contentId
	 */
	private void userAttentionCancel(final int friendId,final int position) {
		//开启副线程-移除粉丝
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBusiness.deleteUserFans(friendId,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_deleteUserFans", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						mUserList.remove(position);
						//移除成功
						handler.sendEmptyMessage(CommonConstants.FLAG_CANCEL_USER_FANS_SUCCESS);
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
					sendErrorMessage("移除粉丝："+CommonConstants.MSG_GET_ERROR);
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
		//开启副线程-获取粉丝列表
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
					sendErrorMessage("我的粉丝："+CommonConstants.MSG_GET_ERROR);
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
		String result = mUserBusiness.getUserFansList(pageNo,pageSize,mUser);
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_getUserFansList", result);
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
				mUserList.clear();//重置list
				for (int i = 0; i < _json_array.length(); i++) {
					JSONObject _jsonObject = _json_array.getJSONObject(i);
					User _user = new User();
					_user.setId(JsonUtils.getInt(_jsonObject, "userId",0));
					_user.setUsername(JsonUtils.getString(_jsonObject, "username"));
					_user.setUserImg((JsonUtils.getString(_jsonObject,"userImg")!=null&&!JsonUtils.getString(_jsonObject,"userImg").equals(""))?(JsonUtils.getString(_jsonObject,"userImg")+"!60.60"):null);
					_user.setRegisterTime(JsonUtils.getString(_jsonObject, "registerTime"));
					mUserList.add(_user);
				}
			}
			//获取成功
			handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_FANS_LIST_SUCCESS);
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
		//开启副线程-获取粉丝列表
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					String result = mUserBusiness.getUserFansList(pageNo,pageSize,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_getUserFansList_again", result);
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
								User _user = new User();
								_user.setId(JsonUtils.getInt(_jsonObject, "userId",0));
								_user.setUsername(JsonUtils.getString(_jsonObject, "username"));
								_user.setUserImg((JsonUtils.getString(_jsonObject,"userImg")!=null&&!JsonUtils.getString(_jsonObject,"userImg").equals(""))?(JsonUtils.getString(_jsonObject,"userImg")+"!60.60"):null);
								_user.setRegisterTime(JsonUtils.getString(_jsonObject, "registerTime"));
								mUserList.add(_user);
							}
						}
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_FANS_LIST_AGAIN_SUCCESS);
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
					sendErrorMessage("我的粉丝："+CommonConstants.MSG_GET_ERROR);
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
					Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case CommonConstants.FLAG_GET_USER_FANS_LIST_SUCCESS:
				updateUserFansListFromNet();
				break;
			case CommonConstants.FLAG_GET_USER_FANS_LIST_AGAIN_SUCCESS:
				updateUserFansListFromNetByRefresh();
				break;
			case CommonConstants.FLAG_CANCEL_USER_FANS_SUCCESS:
				updateUserFansListFromNetByCancel();
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
	 * 从网络初次加载-我的粉丝列表
	 */
	private void updateUserFansListFromNet() {
		mUserFansListAdapter = new UserAttentionUserListAdapter(getActivity(),mUserList);
		mPullToRefreshListView.setAdapter(mUserFansListAdapter);
		pageNo++;
	}
	/**
	 * 取消关注某个粉丝后，刷新UI粉丝列表
	 */
	protected void updateUserFansListFromNetByCancel() {
		mUserFansListAdapter.notifyDataSetChanged();
	}

	/**
	 * 从网络更新我的粉丝列表
	 */
	private void updateUserFansListFromNetByRefresh(){
		mUserFansListAdapter.notifyDataSetChanged();
		mPullToRefreshListView.getRefreshableView().setSelection((pageNo-1)*pageSize);
		pageNo++;
		
	}

	/**
	 * 设置空数据时的emptyView
	 * @param errorMsg
	 */
	protected void setListNoDataEmptyView(String errorMsg) {
		if(errorMsg!=null&&!errorMsg.equals("")){
			mTvUserFansListNone.setText(errorMsg);
		}else{
			mTvUserFansListNone.setText("没有数据");
		}
		mPullToRefreshListView.setEmptyView(mTvUserFansListNone);
	}
	
}
