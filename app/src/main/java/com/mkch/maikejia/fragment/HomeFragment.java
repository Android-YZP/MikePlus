package com.mkch.maikejia.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.image.SmartImageView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.activity.CreativeDetailActivity;
import com.mkch.maikejia.activity.CreativeListActivity;
import com.mkch.maikejia.activity.CreativeSearchActivity;
import com.mkch.maikejia.activity.StartBusinessActivity;
import com.mkch.maikejia.adapter.HomeCatesListAdapter;
import com.mkch.maikejia.adapter.HomeHotCreativeListAdapter;
import com.mkch.maikejia.bean.Creative;
import com.mkch.maikejia.bean.HomeBanner;
import com.mkch.maikejia.business.ICreativeBusiness;
import com.mkch.maikejia.business.IUserBusiness;
import com.mkch.maikejia.business.imp.CreativeBusinessImp;
import com.mkch.maikejia.business.imp.UserBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.JsonUtils;
import com.mkch.maikejia.view.AdScrollLayout;

public class HomeFragment extends Fragment {
    //下拉刷新
    private PullToRefreshScrollView mPullToRefreshScrollView;

    //topbar
    private Button mBtnSearch;
    private int mScrollX;

    //广告轮播图片
    private AdScrollLayout mAdScrollLayout;
    private ViewPager mViewPagerAdScroll;
    ArrayList<View> bitMap = new ArrayList<View>();

    private TextView mTvBannerNone;

    //轮播数据
    private List<HomeBanner> mHomeBannerList;
    private MyAdPagerAdapter myAdPagerAdapter;

    //领域分类
    private GridView mGvHomeCates;
    private ListAdapter mHomeCatesListAdapter;

    //热门创意列表
    private ListView mLvHomeHotCreative;
    private HomeHotCreativeListAdapter mHomeHotCreativeListAdapter;
    //热门创意数据
    private List<Creative> mHomeCreativeList = new ArrayList<Creative>();

//	//热点设计列表
//	private ListView mLvHomeHotDesign;
//	private HomeHotCreativeListAdapter mHomeHotDesignListAdapter;
//	//热点设计数据
//	private List<Creative> mHomeDesignList= new ArrayList<Creative>();

    //热门创意-更多
    private TextView mTvHotCreativeMore;
    private TextView mTvHotCreativeNone;//不显示创意
    //热点设计-更多
//	private TextView mTvHotDesignMore;//先隐藏
    private TextView mTvHotDesignNone;//不显示设计
    private boolean isLast = true;
    private boolean isFast;

    private String mHotCreativeNoneStr;
    private String mHotDesignNoneStr;
    //业务层
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private ICreativeBusiness mCreativeBusiness = new CreativeBusinessImp();
    private HorizontalScrollView mHsvScroll;
    private View lastOne;
    private View fastOne;
    private ImageView mIvArrow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findView(view);
        return view;
    }

    /**
     * 找控件
     *
     * @param view
     */
    private void findView(View view) {
        mPullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptrsv_home_scroll);

        //搜索
        mBtnSearch = (Button) view.findViewById(R.id.btn_index_search);
        mHsvScroll = (HorizontalScrollView) view.findViewById(R.id.hsv_scrollview);
        mIvArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        lastOne = view.findViewById(R.id.last_one);
        fastOne = view.findViewById(R.id.fast_one);

        mAdScrollLayout = (AdScrollLayout) view.findViewById(R.id.myAdScroll);
        mViewPagerAdScroll = mAdScrollLayout.getViewPager();
        mTvBannerNone = (TextView) view.findViewById(R.id.tv_home_banner_none);
        //领域分类
        mGvHomeCates = (GridView) view.findViewById(R.id.gv_home_cates);
        //热门创意列表
        mLvHomeHotCreative = (ListView) view.findViewById(R.id.lv_home_hot_creative);
//		//热点设计列表
//		mLvHomeHotDesign = (ListView)view.findViewById(R.id.lv_home_hot_design);
        //更多
        mTvHotCreativeMore = (TextView) view.findViewById(R.id.tv_home_creative_more);
//		mTvHotDesignMore = (TextView)view.findViewById(R.id.tv_home_design_more);//先隐藏

        mTvHotCreativeNone = (TextView) view.findViewById(R.id.tv_home_hot_creative_none);
//		mTvHotDesignNone = (TextView)view.findViewById(R.id.tv_home_hot_design_none);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();//初始化数据
        setListener();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消定时器
        if (mAdScrollLayout != null) {
            mAdScrollLayout.cancelPageFromTime();

        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //广告图片
        initDataBanner();
        //领域分类
        mHomeCatesListAdapter = new HomeCatesListAdapter(getActivity());
        mGvHomeCates.setAdapter(mHomeCatesListAdapter);
        //创意列表和热点设计
        initDataCreativeOrDesignList();

        mHsvScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_SCROLL:
                        boolean lastVisible = isViewVisible(mHsvScroll, lastOne);
                        Log.d("HomeFragment1", "viewVisible:" + lastVisible);
                        boolean fastVisible = isViewVisible(mHsvScroll, fastOne);
                        Log.d("HomeFragment2", "viewVisible:" + fastVisible);


                        if (lastVisible && isLast) {
                            isLast = false;
                            mIvArrow.setImageResource(R.drawable.tab_left_arrow);
                        }


                        if (fastVisible && !isLast) {
                            isLast = true;
                            mIvArrow.setImageResource(R.drawable.tab_right_arrow);
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 判断HorizontalScrollView里面的子控件是否显示出来了
     *
     * @param hs
     * @param view
     * @return
     */
    private boolean isViewVisible(HorizontalScrollView hs, View view) {
        Rect scrollBounds = new Rect();
        hs.getHitRect(scrollBounds);
        if (!view.getLocalVisibleRect(scrollBounds)
                || scrollBounds.height() < view.getHeight()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 初始化数据-banner
     */
    private void initDataBanner() {

        myAdPagerAdapter = new MyAdPagerAdapter();
        //设置适配器
        mViewPagerAdScroll.setAdapter(myAdPagerAdapter);
        //先取消清空上一次定时器
        if (mAdScrollLayout != null) {
            mAdScrollLayout.cancelPageFromTime();

        }
        //开启轮播
        mAdScrollLayout.setPageFromTime(3000);

        //开启副线程-发送请求获取banner数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mUserBusiness.getHomeBannerList();
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_banner_list", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        JSONArray _json_array = jsonObj.getJSONArray("datas");
                        if (_json_array.length() > 0) {
                            mHomeBannerList = new ArrayList<HomeBanner>();
                            for (int i = 0; i < _json_array.length(); i++) {
                                JSONObject _jsonObject = _json_array.getJSONObject(i);
                                HomeBanner _homeBanner = new HomeBanner();
                                _homeBanner.setImg(JsonUtils.getString(_jsonObject, "img"));
                                _homeBanner.setDesc(JsonUtils.getString(_jsonObject, "desc"));
                                _homeBanner.setRedirectType(JsonUtils.getInt(_jsonObject, "redirectType", 0));
                                _homeBanner.setRedirectId(JsonUtils.getInt(_jsonObject, "redirectId", 0));
                                mHomeBannerList.add(_homeBanner);
                            }
                        }
//						Log.d(CommonConstants.LOGCAT_TAG_NAME+"_mHomeBannerList", mHomeBannerList.toString());
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_BANNER_LIST_SUCCESS);
                    } else {
                        //获取错误代码，并查询出错误文字
                        String errorMsg = jsonObj.getString("errorMsg");
                        CommonUtil.sendErrorMessage(errorMsg, handler);
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(), handler);
                } catch (Exception e) {
                    //what = 0;sendmsg 0;
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage("广告轮播：" + CommonConstants.MSG_GET_ERROR, handler);
                }
            }
        }).start();
    }


    /**
     * 初始化数据-创意列表和热点设计
     */
    private void initDataCreativeOrDesignList() {
        //开启副线程-获取热门创意列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mCreativeBusiness.getHomeHotCreativeList();
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_hot_creative_list", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        //填充创意列表
                        fullCreativeList(jsonObj);
//						Log.d(CommonConstants.LOGCAT_TAG_NAME+"_mHomeHotCreativeList", mHomeCreativeList.toString());
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_HOME_HOT_CREATIVE_LIST_SUCCESS);
                    } else {
                        //获取错误代码，并查询出错误文字
//						String errorMsg = jsonObj.getString("errorMsg");
//						CommonUtil.sendErrorMessage(errorMsg,handler);
                        mHotCreativeNoneStr = jsonObj.getString("errorMsg");
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_HOME_HOT_CREATIVE_NONE_SUCCESS);
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                    mHotCreativeNoneStr = e.getMessage();
                    handler.sendEmptyMessage(CommonConstants.FLAG_GET_HOME_HOT_CREATIVE_NONE_SUCCESS);
//					CommonUtil.sendErrorMessage(e.getMessage(),handler);
                } catch (Exception e) {
                    //what = 0;sendmsg 0;
//					CommonUtil.sendErrorMessage("热门创意："+CommonConstants.MSG_GET_ERROR,handler);
                    mHotCreativeNoneStr = "热门创意：" + CommonConstants.MSG_GET_ERROR;
                    handler.sendEmptyMessage(CommonConstants.FLAG_GET_HOME_HOT_CREATIVE_NONE_SUCCESS);
                }
            }
        }).start();

//		//开启副线程-获取热点设计列表
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					String result = mCreativeBusiness.getHomeHotDesignList();
//					Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_hot_design_list", result);
//					JSONObject jsonObj = new JSONObject(result);
//					boolean Success = jsonObj.getBoolean("success");
//					if(Success){
//						//填充设计列表
//						fullDesignList(jsonObj);
////								Log.d(CommonConstants.LOGCAT_TAG_NAME+"_mHomeHotCreativeList", mHomeCreativeList.toString());
//						//获取成功
//						handler.sendEmptyMessage(CommonConstants.FLAG_GET_HOME_HOT_DESIGN_LIST_SUCCESS);
//					}else{
//						//获取错误代码，并查询出错误文字
////						String errorMsg = jsonObj.getString("errorMsg");
////						CommonUtil.sendErrorMessage(errorMsg,handler);
//						mHotDesignNoneStr = jsonObj.getString("errorMsg");
//						handler.sendEmptyMessage(CommonConstants.FLAG_GET_HOME_HOT_DESIGN_NONE_SUCCESS);
//					}
//				} catch (ServiceException e) {
//					e.printStackTrace();
//					mHotDesignNoneStr = e.getMessage();
//					handler.sendEmptyMessage(CommonConstants.FLAG_GET_HOME_HOT_DESIGN_NONE_SUCCESS);
//				} catch (Exception e) {
//					//what = 0;sendmsg 0;
////					CommonUtil.sendErrorMessage("热点设计："+CommonConstants.MSG_GET_ERROR,handler);
//					mHotDesignNoneStr = "热点设计："+CommonConstants.MSG_GET_ERROR;
//					handler.sendEmptyMessage(CommonConstants.FLAG_GET_HOME_HOT_DESIGN_NONE_SUCCESS);
//				}
//			}
//		}).start();
    }

    /**
     * 填充创意列表
     *
     * @param jsonObj
     * @throws Exception
     */
    protected void fullCreativeList(JSONObject jsonObj) throws Exception {
        JSONArray _json_array = jsonObj.getJSONArray("datas");
        if (_json_array.length() > 0) {
            mHomeCreativeList.clear();
            for (int i = 0; i < _json_array.length(); i++) {
                JSONObject _jsonObject = _json_array.getJSONObject(i);
                Creative _creative = new Creative();
                _creative.setId(JsonUtils.getString(_jsonObject, "id"));
                _creative.setTitle(JsonUtils.getString(_jsonObject, "title"));
                _creative.setUrl(JsonUtils.getString(_jsonObject, "url"));
                _creative.setTitleImg((JsonUtils.getString(_jsonObject, "titleImg") != null && !JsonUtils.getString(_jsonObject, "titleImg").equals("")) ? (JsonUtils.getString(_jsonObject, "titleImg") + "!200.200") : null);
                _creative.setUsername(JsonUtils.getString(_jsonObject, "username"));
                _creative.setUserImg((JsonUtils.getString(_jsonObject, "userImg") != null && !JsonUtils.getString(_jsonObject, "userImg").equals("")) ? (JsonUtils.getString(_jsonObject, "userImg") + "!60.60") : null);
                _creative.setDesc(JsonUtils.getString(_jsonObject, "desc"));
                _creative.setReleaseDate(JsonUtils.getString(_jsonObject, "releaseDate"));
                mHomeCreativeList.add(_creative);
            }
        }
    }
    /**
     * 填充设计列表
     * @param jsonObj
     * @throws Exception
     */
//	protected void fullDesignList(JSONObject jsonObj) throws Exception {
//		JSONArray _json_array = jsonObj.getJSONArray("datas");
//		if(_json_array.length()>0){
//			mHomeDesignList.clear();
//			for (int i = 0; i < _json_array.length(); i++) {
//				JSONObject _jsonObject = _json_array.getJSONObject(i);
//				Creative _creative = new Creative();
//				_creative.setId(JsonUtils.getString(_jsonObject, "id"));
//				_creative.setTitle(JsonUtils.getString(_jsonObject,"title"));
//				_creative.setUrl(JsonUtils.getString(_jsonObject,"url"));
//				_creative.setTitleImg((JsonUtils.getString(_jsonObject,"titleImg")!=null&&!JsonUtils.getString(_jsonObject,"titleImg").equals(""))?(JsonUtils.getString(_jsonObject,"titleImg")+"!200.200"):null);
//				_creative.setUsername(JsonUtils.getString(_jsonObject,"username"));
//				_creative.setUserImg((JsonUtils.getString(_jsonObject,"userImg")!=null&&!JsonUtils.getString(_jsonObject,"userImg").equals(""))?(JsonUtils.getString(_jsonObject,"userImg")+"!60.60"):null);
//				_creative.setDesc(JsonUtils.getString(_jsonObject,"desc"));
//				_creative.setReleaseDate(JsonUtils.getString(_jsonObject,"releaseDate"));
//				mHomeDesignList.add(_creative);
//			}
//		}
//	}

    /**
     * 设置监听器
     */
    private void setListener() {
        //下拉刷新
        mPullToRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
//				initDataCreativeOrDesignList();
                initData();
            }

        });
        //分类
        mGvHomeCates.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long id) {
                //0~8
                Intent _intent = new Intent(getActivity(), CreativeListActivity.class);
                _intent.putExtra("position", position+1);
                startActivity(_intent);
            }
        });
        //热门创意
        mLvHomeHotCreative.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long id) {
                Creative _creative = mHomeCreativeList.get(position);
                Intent _intent = new Intent(getActivity(), CreativeDetailActivity.class);
                _intent.putExtra("creative_id", _creative.getId());
                getActivity().startActivity(_intent);
            }
        });

//		//热点设计
//		mLvHomeHotDesign.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int position,
//					long id) {
//				Creative _creative = mHomeDesignList.get(position);
//				Intent _intent = new Intent(getActivity(),CreativeDetailActivity.class);
//				_intent.putExtra("creative_id", _creative.getId());
//				getActivity().startActivity(_intent);
//			}
//		});

        mTvHotCreativeMore.setOnClickListener(new MoreOnClickListener());
//		mTvHotDesignMore.setOnClickListener(new MoreOnClickListener());//先隐藏
        //搜索
        mBtnSearch.setOnClickListener(new MoreOnClickListener());

    }

    /**
     * 自定义点击监听类
     *
     * @author JLJ
     */
    private class MoreOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            Intent _intent = null;
            switch (view.getId()) {
                case R.id.tv_home_creative_more:
                    _intent = new Intent(getActivity(), CreativeListActivity.class);
                    startActivity(_intent);
                    break;
//			case R.id.tv_home_design_more://先隐藏
//				_intent = new Intent(getActivity(), CreativeListActivity.class);
//				startActivity(_intent);
//				break;
                case R.id.btn_index_search:
                    _intent = new Intent(getActivity(), CreativeSearchActivity.class);
                    startActivity(_intent);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * ViewPager的适配器
     *
     * @author JLJ
     */
    private class MyAdPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return bitMap.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(bitMap.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            View v = bitMap.get(position);
            //为了解决异常报错-The specified child already has a parent. You must call removeView() on the child's parent first
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }

            ((ViewPager) container).addView(v);
            return v;
        }
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
                    initDefaultBannerList(errorMsg);
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    break;
                case CommonConstants.FLAG_GET_BANNER_LIST_SUCCESS:
                    updateBannerListFromNet();
                    break;
                case CommonConstants.FLAG_GET_HOME_HOT_CREATIVE_LIST_SUCCESS:
                    updateHotCreativeListFromNet();
                    break;
//			case CommonConstants.FLAG_GET_HOME_HOT_DESIGN_LIST_SUCCESS:
//				updateHotDesignListFromNet();
//				break;
                case CommonConstants.FLAG_GET_HOME_HOT_CREATIVE_NONE_SUCCESS:
                    changeCreativeListViewEmpty();
                    break;
                case CommonConstants.FLAG_GET_HOME_HOT_DESIGN_NONE_SUCCESS:
                    changeDesignListViewEmpty();
                    break;
                default:
                    break;
            }
            if (mPullToRefreshScrollView != null && mPullToRefreshScrollView.isRefreshing()) {
                mPullToRefreshScrollView.onRefreshComplete();
            }
        }


    };

    //初始化默认轮播列表
    private void initDefaultBannerList(String errorMsg) {
        //隐藏banner，显示提示文本
        mAdScrollLayout.setVisibility(View.GONE);
        mTvBannerNone.setText(errorMsg);
        mTvBannerNone.setVisibility(View.VISIBLE);
    }


    //获取数据后-刷新banner数据
    private void updateBannerListFromNet() {
        //显示banner，隐藏提示文本
        mAdScrollLayout.setVisibility(View.VISIBLE);
        mTvBannerNone.setVisibility(View.GONE);

        bitMap.clear();//清除原数据
        if (mHomeBannerList != null && mHomeBannerList.size() > 0) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            for (int i = 0; i < mHomeBannerList.size(); i++) {
                View _view = inflater.inflate(R.layout.ad_scroll_item_img, null);
                SmartImageView _siv = (SmartImageView) _view.findViewById(R.id.iv_ad_scroll_img);
                _siv.setImageUrl(mHomeBannerList.get(i).getImg(), R.drawable.banner2);
                final int position = i;
                _siv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //点击某一个创意后
                        HomeBanner _home_banner = mHomeBannerList.get(position);
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_banner_" + position, "redirectId=" + _home_banner.getRedirectId());

                        if (_home_banner.getRedirectId() != 0) {
                            if (_home_banner.getRedirectType() == 1) {
                                Intent _intent = new Intent(getActivity(), CreativeDetailActivity.class);
                                _intent.putExtra("creative_id", String.valueOf(_home_banner.getRedirectId()));
                                getActivity().startActivity(_intent);
                            } else if (_home_banner.getRedirectType() == 2) {
                                Intent _intent = new Intent(getActivity(), StartBusinessActivity.class);
                                startActivity(_intent);
                            }
                        }


                    }
                });
                bitMap.add(_view);
            }

            mAdScrollLayout.setSize(mHomeBannerList.size());
        }
        myAdPagerAdapter.notifyDataSetChanged();
    }

    //获取数据后-刷新热门创意的数据
    private void updateHotCreativeListFromNet() {
        mTvHotCreativeNone.setVisibility(View.GONE);
        //热门创意
        mHomeHotCreativeListAdapter = new HomeHotCreativeListAdapter(getActivity(), mHomeCreativeList);
        mLvHomeHotCreative.setAdapter(mHomeHotCreativeListAdapter);
//		mHomeHotCreativeListAdapter.notifyDataSetChanged();
        CommonUtil.setListViewHeight(mLvHomeHotCreative);
    }

//	//获取数据后-刷新热点设计的数据
//	private void updateHotDesignListFromNet() {
//		mTvHotDesignNone.setVisibility(View.GONE);
//		//热点设计
//		mHomeHotDesignListAdapter = new HomeHotCreativeListAdapter(getActivity(), mHomeDesignList);
//		mLvHomeHotDesign.setAdapter(mHomeHotDesignListAdapter);
////		mHomeHotDesignListAdapter.notifyDataSetChanged();
//		CommonUtil.setListViewHeight(mLvHomeHotDesign);
//	}

    /**
     * 更新创意列表失败时显示错误信息
     */
    protected void changeCreativeListViewEmpty() {
        if (mHotCreativeNoneStr != null && !mHotCreativeNoneStr.equals("")) {
            Toast.makeText(getActivity(), mHotCreativeNoneStr, Toast.LENGTH_SHORT).show();//提示信息
            mTvHotCreativeNone.setText(mHotCreativeNoneStr);
            mTvHotCreativeNone.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 更新设计列表失败时显示错误信息
     */
    protected void changeDesignListViewEmpty() {
        if (mHotDesignNoneStr != null && !mHotDesignNoneStr.equals("")) {
            mTvHotDesignNone.setText(mHotDesignNoneStr);
            mTvHotDesignNone.setVisibility(View.VISIBLE);
        }

    }
}
