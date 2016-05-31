package com.mkch.maikejia.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.mkch.maikejia.R;
import com.mkch.maikejia.adapter.IndexSearchListAdapter;
import com.mkch.maikejia.bean.SearchHistory;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.util.CommonUtil;

public class CreativeSearchActivity extends Activity {
	private ImageView mIvBack;
	private EditText mEtKeyword;//搜索框
	private TextView mTvSearchOrClean;
	
	private ListView mListViewKeyword;//历史列表
	private IndexSearchListAdapter mIndexSearchListAdapter;
	private List<String> mKeywordHistoryList;
	
	private TextView mTvCleanHistory;//清理历史数据
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_creative_search);
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
		setListener();
	}
	
	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_creative_search_back);
		mTvSearchOrClean = (TextView)findViewById(R.id.tv_creative_search_cancel);
		
		mListViewKeyword = (ListView)findViewById(R.id.lv_index_search_history);
		mEtKeyword = (EditText)findViewById(R.id.et_creative_search);
		
		mTvCleanHistory = (TextView)findViewById(R.id.tv_creative_search_clean_history);
	}
	private void initData() {
		initDataKeywordHistory();
		
		mIndexSearchListAdapter = new IndexSearchListAdapter(this,mKeywordHistoryList);
		mListViewKeyword.setAdapter(mIndexSearchListAdapter);
	}
	/**
	 * 初始化历史搜索词
	 */
	private void initDataKeywordHistory() {
		SearchHistory searchHistory = CommonUtil.getSearchHistory(this);
		if(searchHistory!=null){
			mKeywordHistoryList = searchHistory.getHistoryNames();
		}else{
			mKeywordHistoryList = new ArrayList<String>();
		}
		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_CreativeSearchActivity_initDataKeywordHistory", "size = "+mKeywordHistoryList.size());
		
	}
	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				CreativeSearchActivity.this.finish();
			}
		});
		
//		mEtKeyword.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				if(s!=null&&!s.equals("")){
//					mTvSearchOrClean.setText("搜索");
//				}
//			}
//		});
		
		mEtKeyword.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if(actionId==EditorInfo.IME_ACTION_SEARCH){
					String keyword = textView.getText().toString();
					if(keyword==null||keyword.equals("")){
						Toast.makeText(CreativeSearchActivity.this, "您未填写关键字", Toast.LENGTH_SHORT).show();
						textView.setText("");
						textView.setFocusable(true);
						return false;
					}
					saveKeywordToHistory(keyword);//保存搜索信息到历史
					Intent _intent = new Intent(CreativeSearchActivity.this,CreativeSearchResultActivity.class);
					_intent.putExtra("keyword", keyword);
					startActivity(_intent);
					CreativeSearchActivity.this.finish();
					return true;
				}
				return false;
			}
		});
		
		mListViewKeyword.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				String _keyword = mKeywordHistoryList.get(position);
				Intent _intent = new Intent(CreativeSearchActivity.this,CreativeSearchResultActivity.class);
				_intent.putExtra("keyword", _keyword);
				startActivity(_intent);
				CreativeSearchActivity.this.finish();
			}
		});
		
		mTvCleanHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				SearchHistory searchHistory = CommonUtil.getSearchHistory(CreativeSearchActivity.this);
				if(searchHistory!=null){
					mKeywordHistoryList.clear();
					searchHistory.setHistoryNames(mKeywordHistoryList);
					CommonUtil.saveSearchHistory(searchHistory, CreativeSearchActivity.this);
					mIndexSearchListAdapter.notifyDataSetChanged();
				}
			}
		});
		
		mTvSearchOrClean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEtKeyword.setText("");
			}
		});
	}
	/**
	 * 保存搜索信息到历史
	 * @param keyword 关键字
	 */
	protected void saveKeywordToHistory(String keyword) {
		SearchHistory searchHistory = CommonUtil.getSearchHistory(this);
		
		if(searchHistory!=null){
			mKeywordHistoryList = searchHistory.getHistoryNames();
			mKeywordHistoryList.add(0,keyword);
			CommonUtil.saveSearchHistory(searchHistory, this);
			Log.d(CommonConstants.LOGCAT_TAG_NAME+"_CreativeSearchActivity_saveKeywordToHistory!=null", " = "+searchHistory.toString());
		}else{
			searchHistory = new SearchHistory();
			mKeywordHistoryList.add(keyword);
			searchHistory.setHistoryNames(mKeywordHistoryList);
			CommonUtil.saveSearchHistory(searchHistory, this);
			Log.d(CommonConstants.LOGCAT_TAG_NAME+"_CreativeSearchActivity_saveKeywordToHistory==null", " = "+searchHistory.toString());
		}
	}
	
	public void notifyHistoryKeywordsChanged(int location){
		SearchHistory searchHistory = CommonUtil.getSearchHistory(this);
		if(searchHistory!=null){
			mKeywordHistoryList.remove(location);
			searchHistory.setHistoryNames(mKeywordHistoryList);
			CommonUtil.saveSearchHistory(searchHistory, this);
			mIndexSearchListAdapter.notifyDataSetChanged();
		}
	}
}
