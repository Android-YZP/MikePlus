package com.mkch.maikejia.fragment;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.CreativeDetailActivity;
import com.mkch.maikejia.adapter.UserCreativeListAdapter;
import com.mkch.maikejia.bean.Creative;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.business.ICreativeBusiness;
import com.mkch.maikejia.business.imp.CreativeBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;
/**
 * 我的创意
 * @author JLJ
 *
 */
public class UserCreativeListFragment extends Fragment {
	private PullToRefreshListView mPullToRefreshListView;
	private UserCreativeListAdapter mUserCreativeListAdapter;
	private List<Creative> mCreativeList;
	
	private ImageView mIvCreativeListNone;
	//业务层
	private ICreativeBusiness mCreativeBusiness = new CreativeBusinessImp();
	
	private User mUser;
	
	private int pageNo = 1;
	private int pageSize = 10;
	
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
		mUser = (User)CommonUtil.getUserInfo(getActivity());
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
					sendErrorMessage("我的创意："+CommonConstants.MSG_GET_ERROR);
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
				Intent _intent = new Intent(getActivity(),CreativeDetailActivity.class);
				_intent.putExtra("creative_id", _creative.getId());
				getActivity().startActivity(_intent);
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
		
//		ListView mlvUserCreativeList = mPullToRefreshListView.getRefreshableView();
		//长按listview中的item
//		mlvUserCreativeList.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
//					int position, long id) {
//				Creative _creative = mCreativeList.get(position-1);
//				Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_creative_item_long_click", "|"+_creative.toString());
//				//弹出相应的对话框-1草稿-可以修改可以删除；其他只有删除
////				View _view = LayoutInflater.from(getActivity()).inflate(R.layout.user_creative_dialog_item, null);
//				
//				int _status=_creative.getStatus();
//				String[] items=null;
//				switch (_status) {
//				case 0:
//					items = new String[]{"修改","删除"};
//					break;
//				case 1:
//					items = new String[]{"删除"};
//					break;
//				default:
//					break;
//				}
//				
//				final String[] itemsArray = items.clone();
//				new AlertDialog.Builder(getActivity()).setTitle(null)
////				.setView(_view)
//				.setItems(itemsArray, new OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						if(itemsArray[which].equals("修改")){
//							Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_creative_item_long_click", "|修改");
//						}else if(itemsArray[which].equals("删除")){
//							Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_creative_item_long_click", "|删除");
//							//删除该用户的该创意
//							
//						}
//					}
//				}).show();
//				return true;
//			}
//		});
		
//		mlvUserCreativeList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
//			
//			@Override
//			public void onCreateContextMenu(ContextMenu menu, View v,
//					ContextMenuInfo menuInfo) {
//				menu.add(0, 0, 0, "修改");
//				menu.add(0, 1, 0, "删除");
//			}
//		});
	}
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
//		int position = info.position;
//		Creative _creative = mCreativeList.get(position-1);
//		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_creative_item_long_click", "|"+_creative.toString());
//		switch (item.getItemId()) {
//		case 0:
//			Log.d(CommonConstants.LOGCAT_TAG_NAME+"_item_click_0", "修改");
//			break;
//		case 1:
//			Log.d(CommonConstants.LOGCAT_TAG_NAME+"_item_click_1", "删除");
//			break;
//		default:
//			break;
//		}
//		return super.onContextItemSelected(item);
//	}
	
	
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
					sendErrorMessage("我的创意："+CommonConstants.MSG_GET_ERROR);
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
		String result = mCreativeBusiness.getUserCreativeList(pageNo,pageSize,mUser);
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_user_getUserCreativeList", result);
		JSONObject jsonObj = new JSONObject(result);
		boolean Success = jsonObj.getBoolean("success");
		if(Success){
			int totalPage = jsonObj.getInt("totalPage");
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
					_creative.setViews(JsonUtils.getInt(_jsonObject, "views", 0));
					_creative.setCommentsCount(JsonUtils.getInt(_jsonObject, "commentsCount", 0));
					_creative.setReleaseDate(JsonUtils.getString(_jsonObject, "releaseDate"));
					_creative.setStatus(JsonUtils.getInt(_jsonObject, "status", 0));
					_creative.setTitleImg((JsonUtils.getString(_jsonObject,"titleImg")!=null&&!JsonUtils.getString(_jsonObject,"titleImg").equals(""))?(JsonUtils.getString(_jsonObject,"titleImg")+"!200.200"):null);
					_creative.setDesc(JsonUtils.getString(_jsonObject, "desc"));
					_creative.setAgentStatus(JsonUtils.getInt(_jsonObject, "agentStatus", 0));
					_creative.setAssessStatus(JsonUtils.getInt(_jsonObject, "assessStatus", 0));
					mCreativeList.add(_creative);
				}
			}
			//获取成功
			handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_CREATIVE_LIST_SUCCESS);
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
					String result = mCreativeBusiness.getUserCreativeList(pageNo,pageSize,mUser);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_find_creative_list_getFindCreativeList", result);
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
								_creative.setViews(JsonUtils.getInt(_jsonObject, "views", 0));
								_creative.setCommentsCount(JsonUtils.getInt(_jsonObject, "commentsCount", 0));
								_creative.setReleaseDate(JsonUtils.getString(_jsonObject, "releaseDate"));
								_creative.setTitleImg((JsonUtils.getString(_jsonObject,"titleImg")!=null&&!JsonUtils.getString(_jsonObject,"titleImg").equals(""))?(JsonUtils.getString(_jsonObject,"titleImg")+"!200.200"):null);
								_creative.setDesc(JsonUtils.getString(_jsonObject, "desc"));
								_creative.setAgentStatus(JsonUtils.getInt(_jsonObject, "agentStatus", 0));
								_creative.setAssessStatus(JsonUtils.getInt(_jsonObject, "assessStatus", 0));
								mCreativeList.add(_creative);
							}
						}
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_CREATIVE_LIST_AGAIN_SUCCESS);
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
					sendErrorMessage("我的创意："+CommonConstants.MSG_GET_ERROR);
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
			case CommonConstants.FLAG_GET_USER_CREATIVE_LIST_SUCCESS:
				updateUserCreativeListFromNet();
				break;
			case CommonConstants.FLAG_GET_USER_CREATIVE_LIST_AGAIN_SUCCESS:
				updateUserCreativeListFromNetByRefresh();
				break;
			case CommonConstants.FLAG_GET_LIST_NO_DATA:
				break;
			default:
				break;
			}
			// Call onRefreshComplete when the list has been refreshed.
			mPullToRefreshListView.onRefreshComplete();
			mIvCreativeListNone.setVisibility(View.VISIBLE);
			mPullToRefreshListView.setEmptyView(mIvCreativeListNone);
		}

		
	};
	/**
	 * 从网络初次加载发现-创意列表
	 */
	private void updateUserCreativeListFromNet() {
		mUserCreativeListAdapter = new UserCreativeListAdapter(getActivity(),mCreativeList);
		mPullToRefreshListView.setAdapter(mUserCreativeListAdapter);
		pageNo++;
	}
	/**
	 * 从网络更新发现-创意列表
	 */
	private void updateUserCreativeListFromNetByRefresh(){
		mUserCreativeListAdapter.notifyDataSetChanged();
		mPullToRefreshListView.getRefreshableView().setSelection((pageNo-1)*pageSize);
		pageNo++;
	}

}
