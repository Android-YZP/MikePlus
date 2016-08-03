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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.CreativeDetailActivity;
import com.mkch.maikejia.activity.MkfArticleDetailActivity;
import com.mkch.maikejia.adapter.MkfNewsListAdapter;
import com.mkch.maikejia.bean.MkfChannel;
import com.mkch.maikejia.bean.MkfNew;
import com.mkch.maikejia.business.IMkfBusiness;
import com.mkch.maikejia.business.imp.MkfBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.JsonUtils;

public class MkfLastNewsFragment extends Fragment {
    private PullToRefreshListView mPullToRefreshListView;
    private MkfNewsListAdapter mMkfNewsListAdapter;
    private List<MkfNew> mMkfNewList;

    private TextView mTvMkfNewListNone;
    //业务层
    private IMkfBusiness mMkfBusiness = new MkfBusinessImp();

    private int pageNo = 1;
    private int pageSize = 10;
    private int channelId = 36;//麦客风
    private Boolean isRefresh = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mkf_new_list, container, false);
        findView(view);
        return view;
    }

    private void findView(View view) {
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_mkf_new_list);
        mTvMkfNewListNone = (TextView) view.findViewById(R.id.tv_mkf_new_list_none);
        mPullToRefreshListView.setEmptyView(mTvMkfNewListNone);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setListenser();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initData() {
        //初始化麦客风文章列表
        initDataMkfList();
    }

    /**
     * 从网络中首次获取麦客风文章列表数据
     */
    private void initDataMkfList() {
        //开启副线程-获取麦客风文章列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mMkfNewList = new ArrayList<MkfNew>();
                    //得到第一页的数据
                    getFirstPageData();
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    sendErrorMessage(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorMessage("麦客风：" + CommonConstants.MSG_GET_ERROR);
                }
            }
        }).start();
    }

    private void setListenser() {
        mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long id) {
                MkfNew _mkf_new = mMkfNewList.get(position - 1);
                Log.d(CommonConstants.LOGCAT_TAG_NAME + "_mkf_new_id", "_mkf_new_id=" + _mkf_new.getId());
                Intent _intent = new Intent(getActivity(), MkfArticleDetailActivity.class);
                _intent.putExtra("_article_title", "麦客风");
                _intent.putExtra("_article_id", _mkf_new.getId());
                getActivity().startActivity(_intent);
            }
        });

        mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                //刷新第一页的数据
                loadFirstPageData();
                isRefresh = true;
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                //加载第二页的数据
                loadNextPageData();
            }


        });

    }

    /**
     * 发送错误信息到消息队列
     *
     * @param errorMsg
     */
    private void sendErrorMessage(String errorMsg) {
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
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    sendErrorMessage(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorMessage("麦客风：" + CommonConstants.MSG_GET_ERROR);
                }
            }


        }).start();
    }

    /**
     * 获取第一页数据
     *
     * @throws Exception
     */
    private void getFirstPageData() throws Exception {
        pageNo = 1;//重置页数
        String result = mMkfBusiness.getArticlePage(pageNo, pageSize, channelId);
        Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_mkf_new_list_getArticlePage", result);
        JSONObject jsonObj = new JSONObject(result);
        boolean Success = jsonObj.getBoolean("success");
        int totalPage = jsonObj.getInt("totalPage");
        if (Success) {
            JSONArray _json_array = jsonObj.getJSONArray("datas");
            if (pageNo > totalPage || (_json_array != null && _json_array.length() == 0)) {
                handler.sendEmptyMessage(CommonConstants.FLAG_GET_LIST_NO_DATA);
                return;
            }
            if (_json_array.length() > 0) {
                mMkfNewList.clear();//重置list
                for (int i = 0; i < _json_array.length(); i++) {
                    JSONObject _jsonObject = _json_array.getJSONObject(i);
                    fullMkfList(_jsonObject);//填充麦客风列表
                }
            }
            //获取成功
            handler.sendEmptyMessage(CommonConstants.FLAG_GET_MKF_NEW_LIST_SUCCESS);
        } else {
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
                    String result = mMkfBusiness.getArticlePage(pageNo, pageSize, channelId);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "result_mkf_new_list_getArticlePage_again", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    int totalPage = jsonObj.getInt("totalPage");
                    if (Success) {
                        JSONArray _json_array = jsonObj.getJSONArray("datas");
                        if (pageNo > totalPage || (_json_array != null && _json_array.length() == 0)) {
                            String errorMsg = "没有更多数据了";
                            sendErrorMessage(errorMsg);
                            handler.sendEmptyMessage(CommonConstants.FLAG_GET_MKF_NEW_LIST_AGAIN_FAIL);
                            return;
                        }
                        if (_json_array.length() > 0) {
                            for (int i = 0; i < _json_array.length(); i++) {
                                JSONObject _jsonObject = _json_array.getJSONObject(i);
                                fullMkfList(_jsonObject);//填充麦客风列表
                            }
                        }
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_MKF_NEW_LIST_AGAIN_SUCCESS);
                    } else {
                        //获取错误代码，并查询出错误文字
                        String errorMsg = jsonObj.getString("errorMsg");
                        sendErrorMessage(errorMsg);
                    }
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    sendErrorMessage(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorMessage("麦客风：" + CommonConstants.MSG_GET_ERROR);
                }
            }
        }).start();
    }

    /**
     * 填充麦客风文章列表
     *
     * @param _jsonObject
     */
    protected void fullMkfList(JSONObject _jsonObject) {
        MkfNew _mkf_new = new MkfNew();
        _mkf_new.setId(JsonUtils.getInt(_jsonObject, "id", 0));
        //麦客风分类
        MkfChannel _channel = null;
        JSONObject _json_channel = JsonUtils.getObj(_jsonObject, "channel");
        if (_json_channel != null) {
            _channel = new MkfChannel();
            _channel.setId(JsonUtils.getInt(_json_channel, "id", 0));
            _channel.setName(JsonUtils.getString(_jsonObject, "name"));
            _mkf_new.setChannel(_channel);
        }
        _mkf_new.setTitleImg((JsonUtils.getString(_jsonObject, "titleImg") != null && !JsonUtils.getString(_jsonObject, "titleImg").equals("")) ? (JsonUtils.getString(_jsonObject, "titleImg") + "!200.200") : null);
        _mkf_new.setTitle(JsonUtils.getString(_jsonObject, "title"));
        _mkf_new.setDesc(JsonUtils.getString(_jsonObject, "desc"));
        _mkf_new.setKeywords(JsonUtils.getString(_jsonObject, "keywords"));
        _mkf_new.setPublishTime(JsonUtils.getString(_jsonObject, "publishTime"));
        _mkf_new.setViews(JsonUtils.getInt(_jsonObject, "views"));
        mMkfNewList.add(_mkf_new);
    }


    //处理消息队列
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            String errorMsg = null;
            switch (flag) {
                case 0:
                    errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    break;
                case CommonConstants.FLAG_GET_MKF_NEW_LIST_AGAIN_FAIL:
                    Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    break;
                case CommonConstants.FLAG_GET_MKF_NEW_LIST_SUCCESS:
                    updateMkfListFromNet();
                    if (isRefresh) {
                        Toast.makeText(getContext(), "数据刷新成功", Toast.LENGTH_SHORT).show();
                        isRefresh = false;
                    }
                    break;
                case CommonConstants.FLAG_GET_MKF_NEW_LIST_AGAIN_SUCCESS:
                    updateMkfListFromNetByRefresh();
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
     * 从网络初次加载发现-麦客风文章列表
     */
    private void updateMkfListFromNet() {
        mMkfNewsListAdapter = new MkfNewsListAdapter(getActivity(), mMkfNewList);
        mPullToRefreshListView.setAdapter(mMkfNewsListAdapter);
        pageNo++;
    }

    /**
     * 从网络更新发现-麦客风文章列表
     */
    private void updateMkfListFromNetByRefresh() {
        mMkfNewsListAdapter.notifyDataSetChanged();
        mPullToRefreshListView.getRefreshableView().setSelection((pageNo - 1) * pageSize);
        pageNo++;
    }

    /**
     * 设置空数据时的emptyView
     *
     * @param errorMsg
     */
    protected void setListNoDataEmptyView(String errorMsg) {
        if (errorMsg != null && !errorMsg.equals("")) {
            mTvMkfNewListNone.setText(errorMsg);
        } else {
            mTvMkfNewListNone.setText("没有数据");
        }
        mPullToRefreshListView.setEmptyView(mTvMkfNewListNone);
    }

}
