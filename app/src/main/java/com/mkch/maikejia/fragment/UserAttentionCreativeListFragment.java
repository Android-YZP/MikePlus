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
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.CreativeDetailActivity;
import com.mkch.maikejia.adapter.HomeHotCreativeListAdapter;
import com.mkch.maikejia.bean.Creative;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.business.ICreativeBusiness;
import com.mkch.maikejia.business.imp.CreativeBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;

public class UserAttentionCreativeListFragment extends Fragment {
	private PullToRefreshListView mPullToRefreshListView;
	private HomeHotCreativeListAdapter mUserAttentionCreativeListAdapter;
	private List<Creative> mCreativeList;
	
	private ImageView mIvCreativeListNone;
	//业务层
	private ICreativeBusiness mCreativeBusiness = new CreativeBusinessImp();
	
	private int pageNo = 1;
	private int pageSize = 10;
	
	private User mUser;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_creative_list, container, false);
		findView(view);
		return view;
	}
	
	private void findView(View view) {
		mPullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.lv_find_creative_list);
		mIvCreativeListNone = (ImageView)view.findViewById(R.id.iv_find_creative_none);
		
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
		//初始化创意列表
		initDataCreativeList();
	}

	/**
	 * 从网络中首次获取创意列表数据
	 */
	private void initDataCreativeList() {
		//开启副线程-获取创意列表
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mCreativeList = new ArrayList<Creative>();
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
					sendErrorMessage("我关注的创意："+CommonConstants.MSG_GET_ERROR);
				}
			}
		}).start();
	}

	private void setListenser() {
		mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Creative _creative = mCreativeList.get(position-1);
				if (getActivity()!=null) {
					Intent _intent = new Intent(getActivity(), CreativeDetailActivity.class);
					_intent.putExtra("creative_id", _creative.getId());
					getActivity().startActivity(_intent);
				}
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
				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_creative_item_long_click", "|取消关注创意");
				menu.add(0, 0, 0, "取消关注");
			}
		});
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int _position = info.position-1;
		Creative _creative = mCreativeList.get(_position);
		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_creative_item_long_click", "|"+_creative.toString());
		switch (item.getItemId()) {
		case 0:
			Log.d(CommonConstants.LOGCAT_TAG_NAME+"_item_click_0", "取消关注");
			if(_creative.getId()!=null&&!_creative.getId().equals("")){
				int contentId = Integer.parseInt(_creative.getId());
				userAttentionCancel(contentId,_position);//发起网络请求取消关注
			}
			break;
		}
		return super.onContextItemSelected(item);
	}
	/**
	 * 发起网络请求取消关注
	 * @param contentId
	 */
	private void userAttentionCancel(final int contentId,final int position) {
		//开启副线程-取消关注创意
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mCreativeBusiness.deleteUserAttentionCreative(contentId,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_attention_deleteUserAttentionCreative", result);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						mCreativeList.remove(position);
						//取消成功
						handler.sendEmptyMessage(CommonConstants.FLAG_CANCEL_USER_ATTENTION_CREATIVE_SUCCESS);
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
					sendErrorMessage("取消关注创意："+CommonConstants.MSG_GET_ERROR);
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
		//开启副线程-获取创意列表
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
					sendErrorMessage("我关注的创意："+CommonConstants.MSG_GET_ERROR);
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
		String result = mCreativeBusiness.getUserAttentionCreativeList(pageNo,pageSize,mUser);
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_attention_getUserAttentionCreativeList", result);
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
				mCreativeList.clear();//重置list
				for (int i = 0; i < _json_array.length(); i++) {
					JSONObject _jsonObject = _json_array.getJSONObject(i);
					Creative _creative = new Creative();
					_creative.setId(JsonUtils.getString(_jsonObject, "id"));
					_creative.setTitle(JsonUtils.getString(_jsonObject, "title"));
					_creative.setUrl(JsonUtils.getString(_jsonObject, "url"));
					_creative.setTitleImg((JsonUtils.getString(_jsonObject,"titleImg")!=null&&!JsonUtils.getString(_jsonObject,"titleImg").equals(""))?(JsonUtils.getString(_jsonObject,"titleImg")+"!200.200"):null);
					_creative.setUsername(JsonUtils.getString(_jsonObject, "username"));
					_creative.setUserImg((JsonUtils.getString(_jsonObject,"userImg")!=null&&!JsonUtils.getString(_jsonObject,"userImg").equals(""))?(JsonUtils.getString(_jsonObject,"userImg")+"!60.60"):null);
					_creative.setDesc(JsonUtils.getString(_jsonObject, "desc"));
					_creative.setReleaseDate(JsonUtils.getString(_jsonObject, "releaseDate"));
					mCreativeList.add(_creative);
				}
			}
			//获取成功
			handler.sendEmptyMessage(CommonConstants.FLAG_GET_FIND_CREATIVE_LIST_SUCCESS);
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
		//开启副线程-获取创意列表
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					String result = mCreativeBusiness.getUserAttentionCreativeList(pageNo,pageSize,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_attention_getUserAttentionCreativeList_again", result);
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
								Creative _creative = new Creative();
								_creative.setId(JsonUtils.getString(_jsonObject, "id"));
								_creative.setTitle(JsonUtils.getString(_jsonObject, "title"));
								_creative.setUrl(JsonUtils.getString(_jsonObject, "url"));
								_creative.setTitleImg((JsonUtils.getString(_jsonObject,"titleImg")!=null&&!JsonUtils.getString(_jsonObject,"titleImg").equals(""))?(JsonUtils.getString(_jsonObject,"titleImg")+"!200.200"):null);
								_creative.setUsername(JsonUtils.getString(_jsonObject, "username"));
								_creative.setUserImg((JsonUtils.getString(_jsonObject,"userImg")!=null&&!JsonUtils.getString(_jsonObject,"userImg").equals(""))?(JsonUtils.getString(_jsonObject,"userImg")+"!60.60"):null);
								_creative.setDesc(JsonUtils.getString(_jsonObject, "desc"));
								_creative.setReleaseDate(JsonUtils.getString(_jsonObject, "releaseDate"));
								mCreativeList.add(_creative);
							}
						}
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_FIND_CREATIVE_LIST_AGAIN_SUCCESS);
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
					sendErrorMessage("我关注的创意："+CommonConstants.MSG_GET_ERROR);
				}
			}
		}).start();
	}
	
	
	//处理消息队列
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int flag = msg.what;
			switch (flag) {
			case 0:
				String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
				try {
					if(getActivity()!=null) Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case CommonConstants.FLAG_GET_FIND_CREATIVE_LIST_SUCCESS:
				updateUserAttentionCreativeListFromNet();
				break;
			case CommonConstants.FLAG_GET_FIND_CREATIVE_LIST_AGAIN_SUCCESS:
				updateUserAttentionCreativeListFromNetByRefresh();
				break;
			case CommonConstants.FLAG_CANCEL_USER_ATTENTION_CREATIVE_SUCCESS:
				updateUserAttentionCreativeListFromNetByCancel();
				break;
			case CommonConstants.FLAG_GET_LIST_NO_DATA:
				break;
			default:
				break;
			}
			// Call onRefreshComplete when the list has been refreshed.
			mPullToRefreshListView.onRefreshComplete();
			mPullToRefreshListView.setEmptyView(mIvCreativeListNone);
		}

		
	};
	/**
	 * 从网络初次加载我关注的-创意列表
	 */
	private void updateUserAttentionCreativeListFromNet() {
		if (getActivity()!=null) {
			mUserAttentionCreativeListAdapter = new HomeHotCreativeListAdapter(getActivity(), mCreativeList);
			mPullToRefreshListView.setAdapter(mUserAttentionCreativeListAdapter);
			pageNo++;
		}
	}
	/**
	 * 取消关注某个创意后，刷新UI创意列表
	 */
	protected void updateUserAttentionCreativeListFromNetByCancel() {
		mUserAttentionCreativeListAdapter.notifyDataSetChanged();
	}

	/**
	 * 从网络更新我关注的-创意列表
	 */
	private void updateUserAttentionCreativeListFromNetByRefresh(){
		mUserAttentionCreativeListAdapter.notifyDataSetChanged();
		mPullToRefreshListView.getRefreshableView().setSelection((pageNo-1)*pageSize);
		pageNo++;
	}

}
