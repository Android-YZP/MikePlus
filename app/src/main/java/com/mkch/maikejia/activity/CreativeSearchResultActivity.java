package com.mkch.maikejia.activity;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.mkch.maikejia.R;
import com.mkch.maikejia.adapter.HomeHotCreativeListAdapter;
import com.mkch.maikejia.bean.Creative;
import com.mkch.maikejia.bean.SearchHistory;
import com.mkch.maikejia.business.ICreativeBusiness;
import com.mkch.maikejia.business.imp.CreativeBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;

public class CreativeSearchResultActivity extends Activity {
	private ImageView mIvBack;
	private EditText mEtKeyword;
	private TextView mTvSearchOrClean;
	private String mKeyword;
	//创意列表
	private ImageView mIvCreativeListNone;
	private PullToRefreshListView mPullToRefreshListView;
	private HomeHotCreativeListAdapter mHomeHotCreativeListAdapter;
	private List<Creative> mCreativeList;
	
	//业务层
	private ICreativeBusiness mCreativeBusiness = new CreativeBusinessImp();
	private int pageNo = 1;
	private int pageSize = 10;
	
	private static ProgressDialog mProgressDialog = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_creative_search_result);
		initView();
		initData();
		setListener();
	}
	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_creative_search_result_back);
		mEtKeyword = (EditText)findViewById(R.id.et_creative_search_result);
		mTvSearchOrClean = (TextView)findViewById(R.id.tv_creative_search_result_cancel);
		//创意列表
		mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.lv_creative_search_creative_list);
		mIvCreativeListNone = (ImageView)findViewById(R.id.iv_creative_search_creative_none);
		
	}
	private void initData() {
		//获取搜索值并放入搜索头文本框
		Bundle _bundle = getIntent().getExtras();
		if(_bundle!=null){
			mKeyword = _bundle.getString("keyword");
			mEtKeyword.setText(mKeyword);
		}
		
		//从网络中获取搜索结果
		loadFirstPageData();
		
	}
	
	/**
	 * 从网络中加载第一页的数据
	 */
	private void loadFirstPageData() {
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
					sendErrorMessage("搜索创意："+CommonConstants.MSG_GET_ERROR);
				}
			}

			
		}).start();
	}
	
	/**
	 * 得到第一页的数据
	 * @throws Exception
	 */
	private void getFirstPageData() throws Exception {
		pageNo = 1;//重置页数
		String result = mCreativeBusiness.getSearchCreativeList(pageNo,pageSize,mKeyword);
		Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_search_creative_list_getSearchCreativeList", result);
		JSONObject jsonObj = new JSONObject(result);
		boolean Success = jsonObj.getBoolean("success");
		int totalPage = jsonObj.getInt("totalPage");
		if(Success){
			JSONArray _json_array = jsonObj.getJSONArray("datas");
			if(pageNo>totalPage||(_json_array!=null&&_json_array.length()==0)){
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
			handler.sendEmptyMessage(CommonConstants.FLAG_GET_SEARCH_CREATIVE_LIST_SUCCESS);
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
					String result = mCreativeBusiness.getSearchCreativeList(pageNo,pageSize,mKeyword);
					Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_find_creative_list_getSearchCreativeList", result);
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
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_SEARCH_CREATIVE_LIST_AGAIN_SUCCESS);
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
					sendErrorMessage("搜索创意："+CommonConstants.MSG_GET_ERROR);
				}
			}
		}).start();
	}
	
	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				CreativeSearchResultActivity.this.finish();
			}
		});
		
		mEtKeyword.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if(actionId==EditorInfo.IME_ACTION_SEARCH){
					String keyword = textView.getText().toString();
					if(keyword==null||keyword.equals("")){
						Toast.makeText(CreativeSearchResultActivity.this, "您未填写关键字", Toast.LENGTH_SHORT).show();
						textView.setText("");
						textView.setFocusable(true);
						return false;
					}
					mKeyword = keyword;//重置关键词
					saveKeywordToHistory(keyword);//保存搜索信息到历史
					//重新搜索并刷新listview
					loadFirstPageDataAgain();
					
					return true;
				}
				return false;
			}
		});
		
		mTvSearchOrClean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEtKeyword.setText("");
			}
		});
		
		mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Creative _creative = mCreativeList.get(position-1);
				Intent _intent = new Intent(CreativeSearchResultActivity.this,CreativeDetailActivity.class);
				_intent.putExtra("creative_id", _creative.getId());
				CreativeSearchResultActivity.this.startActivity(_intent);
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
	}
	
	
	private static class MyHandler extends Handler{
		private final WeakReference<Activity> mActivity;
		public MyHandler(CreativeSearchResultActivity activity) {
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
				((CreativeSearchResultActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_GET_SEARCH_CREATIVE_LIST_SUCCESS:
				((CreativeSearchResultActivity)mActivity.get()).updateSearchCreativeListFromNet();
				break;
			case CommonConstants.FLAG_GET_SEARCH_CREATIVE_LIST_AGAIN_SUCCESS:
				((CreativeSearchResultActivity)mActivity.get()).updateSearchCreativeListFromNetByRefresh();
				break;
			default:
				break;
			}
			((CreativeSearchResultActivity)mActivity.get()).CallOnRefreshComplete();
			((CreativeSearchResultActivity)mActivity.get()).setListViewNoneVisible();
			
		}
	}
	
	private MyHandler handler = new MyHandler(this);
	
	private void showTip(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	/**
	 * 显示listview为none的可见
	 */
	public void setListViewNoneVisible() {
		mIvCreativeListNone.setVisibility(View.VISIBLE);
		mPullToRefreshListView.setEmptyView(mIvCreativeListNone);
		
	}
	public void CallOnRefreshComplete() {
		// Call onRefreshComplete when the list has been refreshed.
		mPullToRefreshListView.onRefreshComplete();
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
	 * 从网络初次加载发现-创意列表
	 */
	private void updateSearchCreativeListFromNet() {
		mHomeHotCreativeListAdapter = new HomeHotCreativeListAdapter(this,mCreativeList);
		mPullToRefreshListView.setAdapter(mHomeHotCreativeListAdapter);
		pageNo++;
	}
	/**
	 * 从网络更新发现-创意列表
	 */
	private void updateSearchCreativeListFromNetByRefresh(){
		mHomeHotCreativeListAdapter.notifyDataSetChanged();
		mPullToRefreshListView.getRefreshableView().setSelection((pageNo-1)*pageSize);
		pageNo++;
	}
	
	/**
	 * 保存搜索信息到历史
	 * @param keyword 关键字
	 */
	protected void saveKeywordToHistory(String keyword) {
		SearchHistory searchHistory = CommonUtil.getSearchHistory(this);
		
		if(searchHistory!=null){
			List<String> mKeywordHistoryList = searchHistory.getHistoryNames();
			mKeywordHistoryList.add(0,keyword);
			CommonUtil.saveSearchHistory(searchHistory, this);
		}else{
			searchHistory = new SearchHistory();
			List<String> mKeywordHistoryList = new ArrayList<String>();
			mKeywordHistoryList.add(keyword);
			searchHistory.setHistoryNames(mKeywordHistoryList);
			CommonUtil.saveSearchHistory(searchHistory, this);
		}
	}
	
	/**
	 * 加载重新输入关键词后的第一页的数据
	 */
	private void loadFirstPageDataAgain() {
		//开启副线程-获取创意列表
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mCreativeList.clear();
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
					sendErrorMessage("搜索创意："+CommonConstants.MSG_GET_ERROR);
				}
			}

			
		}).start();
	}
}
