package com.mkch.maikejia.activity;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.image.SmartImageView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.adapter.CreativeDetailCommentListAdapter;
import com.mkch.maikejia.bean.CUser;
import com.mkch.maikejia.bean.Channel;
import com.mkch.maikejia.bean.Comment;
import com.mkch.maikejia.bean.CommentUser;
import com.mkch.maikejia.bean.ContentAssess;
import com.mkch.maikejia.bean.CreativeDetail;
import com.mkch.maikejia.bean.Pic;
import com.mkch.maikejia.bean.Reply;
import com.mkch.maikejia.bean.User;
import com.mkch.maikejia.bean.UserStatus;
import com.mkch.maikejia.business.ICreativeBusiness;
import com.mkch.maikejia.business.imp.CreativeBusinessImp;
import com.mkch.maikejia.config.CommonConstants;
import com.mkch.maikejia.exception.ServiceException;
import com.mkch.maikejia.fragment.CreativeDetailCommentFragment;
import com.mkch.maikejia.fragment.CreativeDetailCommentFragment.OnDetailCommentDataInitListener;
import com.mkch.maikejia.fragment.CreativeDetailInfoFragment;
import com.mkch.maikejia.fragment.CreativeDetailInfoFragment.OnDetailInfoDataInitListener;
import com.mkch.maikejia.util.CommonUtil;
import com.mkch.maikejia.util.InputTools;
import com.mkch.maikejia.util.JsonUtils;
import com.mkch.maikejia.util.UmShareUtil;
import com.mkch.maikejia.view.AdScrollLayout;
import com.mkch.maikejia.view.CreativeDetailTabBarLayout;
import com.mkch.maikejia.view.CreativeDetailTabBarLayout.ICreativeDetailTabBarCallBackListener;
import com.mkch.maikejia.view.CustomListViewWithHeight;
import com.mkch.maikejia.view.CustomSmartImageView;
import com.mkch.maikejia.view.CustomViewPager;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng_social_sdk_res_lib.BuildConfig;

/**
 * 我的任务
 * 将PICS数组传到下一个界面
 * 1.查看什么时候获得完整PIC数据
 * 2.没有获取到数据之前,添加进度条,
 * 3.用Gosn解析成字符串之后,传给下一个界面
 */
public class CreativeDetailActivity extends FragmentActivity implements OnDetailInfoDataInitListener, OnDetailCommentDataInitListener {
    private ImageView mIvBack;
    private TextView mTvTitle;

    //广告轮播图片
//	private AdScrollLayout mAdScrollLayout;
    private ViewPager mViewPagerAdScroll;
    ArrayList<View> bitMap = new ArrayList<View>();

    //轮播数据
    private List<Pic> mBannerList;
    private MyAdPagerAdapter creativeDetailAdPagerAdapter;
    private ImageView mIvCreative_detail_no_banner;

    private CreativeDetailTabBarLayout mCreativeDetailTabBarLayout;
    private CustomViewPager mViewPagerCreativeDetailContent;
    private final static int FLAG_ITEM_0 = 0;
    private final static int FLAG_ITEM_1 = 1;

    //业务层
    private ICreativeBusiness mCreativeBusiness = new CreativeBusinessImp();
    private CreativeDetail _creative_detail;
    private User mUser;

    //详情页各个控件
    private TextView mTvCreativeTitle;
    private TextView mTvCreativeDesc;

    private TextView mTvCreativeReleaseDate;
    private TextView mTvCreativeChannel;
    private TextView mTvCreativeViews;
    private TextView mTvCreativeStatus;

    private CustomSmartImageView mSivUserHead;
    private TextView mTvUsername;
    private TextView mTvUserStatusAttention;

    private TextView mTvCreativeCollections;
    private TextView mTvCreativeUps;
    private TextView mTvCreativeDowns;
    //fragment
    private Fragment creativeDetailInfoFragment;
    private Fragment creativeDetailCommentFragment;

    //bottom底部
    private LinearLayout mLineBottomAction;
    private LinearLayout mLineBottomComment;

    //关注、赞、踩
    private ImageView mIvBottomCollect;
    private ImageView mIvBottomUp;
    private ImageView mIvBottomDown;
    private ImageView mIvBottomShare;
    private LinearLayout mLineBottomCollect;
    private LinearLayout mLineBottomUp;
    private LinearLayout mLineBottomDown;
    private LinearLayout mLineBottomShare;

    private boolean _attention = false;
    private boolean _collect = false;
    private boolean _up = false;
    private boolean _down = false;

    //发表评论
    private EditText mEtCommentText;
    private TextView mTvCommentAdd;
    private int mCommentId;
    private String mUsername;
    private int mType;


    //分享按钮
//	private ImageView ivWebShare;
    private String mWebTitle;
    private String mWebId;

    // 首先在您的Activity中添加如下成员变量
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private String mGson_str;
    private SmartImageView mIvAdPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creative_detail);
        initView();
        initData();
        setListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消定时器
//		if(mAdScrollLayout!=null){
//			mAdScrollLayout.cancelPageFromTime();
//
//		}
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_common_topbar_back);
        mTvTitle = (TextView) findViewById(R.id.tv_common_topbar_title);

        mCreativeDetailTabBarLayout = (CreativeDetailTabBarLayout) findViewById(R.id.view_creative_detail_tabbar);
        mViewPagerCreativeDetailContent = (CustomViewPager) findViewById(R.id.viewpager_creative_detail_info);
        //详情页各个控件
        mTvCreativeTitle = (TextView) findViewById(R.id.tv_creativedetail_titlebar_title);
        mTvCreativeDesc = (TextView) findViewById(R.id.tv_creativedetail_titlebar_desc);

        mTvCreativeReleaseDate = (TextView) findViewById(R.id.tv_creativedetail_titlebar_sencond_datetime);
        mTvCreativeChannel = (TextView) findViewById(R.id.tv_creativedetail_titlebar_sencond_cate);
        mTvCreativeViews = (TextView) findViewById(R.id.tv_creativedetail_titlebar_sencond_visitnum);
        mTvCreativeStatus = (TextView) findViewById(R.id.tv_creativedetail_titlebar_sencond_status);

        mSivUserHead = (CustomSmartImageView) findViewById(R.id.iv_creativedetail_userbar_headpic);
        mTvUsername = (TextView) findViewById(R.id.tv_creativedetail_userbar_username);
        mTvUserStatusAttention = (TextView) findViewById(R.id.tv_creativedetail_userbar_attention_user);
        mIvAdPic = (SmartImageView) findViewById(R.id.iv_creative_detail_ad_pic);
        mTvCreativeCollections = (TextView) findViewById(R.id.tv_creativedetail_bottom_attention_num);
        mTvCreativeUps = (TextView) findViewById(R.id.tv_creativedetail_bottom_up_num);
        mTvCreativeDowns = (TextView) findViewById(R.id.tv_creativedetail_bottom_down_num);

        //广告产品
//		mAdScrollLayout = (AdScrollLayout)findViewById(R.id.ad_creative_detail);
//		mViewPagerAdScroll = mAdScrollLayout.getViewPager();
        //底部bottom动作
        mLineBottomAction = (LinearLayout) findViewById(R.id.include_creative_detail_bottom_action);
        mLineBottomComment = (LinearLayout) findViewById(R.id.include_creative_detail_bottom_comment_commit);
        //关注、赞、踩
        mIvBottomCollect = (ImageView) findViewById(R.id.iv_creativedetail_bottombar_attention);
        mIvBottomUp = (ImageView) findViewById(R.id.iv_creativedetail_bottombar_up);
        mIvBottomDown = (ImageView) findViewById(R.id.iv_creativedetail_bottombar_down);
        mIvBottomShare = (ImageView) findViewById(R.id.iv_creativedetail_bottombar_share);

        mLineBottomCollect = (LinearLayout) findViewById(R.id.line_creativedetail_bottombar_attention);
        mLineBottomUp = (LinearLayout) findViewById(R.id.line_creativedetail_bottombar_up);
        mLineBottomDown = (LinearLayout) findViewById(R.id.line_creativedetail_bottombar_down);
        mLineBottomShare = (LinearLayout) findViewById(R.id.line_creativedetail_bottombar_share);

        //发表评论
        mEtCommentText = (EditText) findViewById(R.id.et_creativedetail_bottom_comment_text);
        mTvCommentAdd = (TextView) findViewById(R.id.tv_creativedetail_bottom_comment_add);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mTvTitle.setText("创意详情");

        UmShareUtil.initShareArgs(mController, this);//umeng分享

        mViewPagerCreativeDetailContent.setAdapter(new CreativeDetailInfoFragmentPagerAdapter(getSupportFragmentManager()));

        //获取创意ID
        Bundle _bundle = getIntent().getExtras();
        String cid = null;
        if (_bundle != null) {
            cid = _bundle.getString("creative_id");
            mWebId = _bundle.getString("creative_id");//文章ID-分享用
        }
        if (cid != null && !cid.equals("")) {
            String userId = null;
            mUser = CommonUtil.getUserInfo(this);
            if (mUser != null) {
                if (mUser.getId() != 0) {
                    userId = String.valueOf(mUser.getId());
                }
            }
            //获取详情数据并更新UI界面
            initDataForCreativeDetail(cid, userId);
        }


    }

    /**
     * 获取详情数据-从网络
     *
     * @param cid    创意ID
     * @param userId 用户ID
     */
    private void initDataForCreativeDetail(final String cid, final String userId) {
        //开启副线程-获取创意详情
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mCreativeBusiness.getCreativeDetail(cid, userId);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_getCreativeDetail", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        fullCreativeDetail(jsonObj);//填充创意详情对象
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_creative_detail_toString", _creative_detail.toString());
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_CREATIVE_DETAIL_SUCCESS);
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
                    sendErrorMessage("创意详情：" + CommonConstants.MSG_GET_ERROR);
                }
            }

            /**
             * 填充创意详情对象
             * @param jsonObj
             * @throws JSONException
             */
            private void fullCreativeDetail(JSONObject jsonObj) throws JSONException {
                _creative_detail = new CreativeDetail();
                _creative_detail.setId(JsonUtils.getInt(jsonObj, "id", 0));
                //分类
                JSONObject _jsonobj_channel = JsonUtils.getObj(jsonObj, "channel");
                if (_jsonobj_channel != null && _jsonobj_channel.length() > 0) {
                    Channel _channel = new Channel();
                    _channel.setId(JsonUtils.getInt(_jsonobj_channel, "id", 0));
                    _channel.setName(JsonUtils.getString(_jsonobj_channel, "name"));
                    _channel.setColor(JsonUtils.getString(_jsonobj_channel, "color"));
                    _creative_detail.setChannel(_channel);
                }

                _creative_detail.setTitle(JsonUtils.getString(jsonObj, "title"));
                mWebTitle = JsonUtils.getString(jsonObj, "title");//分享标题
                _creative_detail.setDesc(JsonUtils.getString(jsonObj, "desc"));
                _creative_detail.setTxt(JsonUtils.getString(jsonObj, "txt"));
                _creative_detail.setTxt1(JsonUtils.getString(jsonObj, "txt1"));
                _creative_detail.setStatus(JsonUtils.getInt(jsonObj, "status"));
                _creative_detail.setAssessStatus(JsonUtils.getInt(jsonObj, "assessStatus", 0));
                _creative_detail.setDesignStatus(JsonUtils.getInt(jsonObj, "designStatus", 0));
                _creative_detail.setAgentStatus(JsonUtils.getInt(jsonObj, "agentStatus", 0));
                //图片
                JSONArray _jsonarray_pics = JsonUtils.getArray(jsonObj, "pics");
                if (_jsonarray_pics != null && _jsonarray_pics.length() > 0) {
                    List<Pic> _pics = new ArrayList<Pic>();
                    for (int i = 0; i < _jsonarray_pics.length(); i++) {
                        JSONObject _jsonObject = _jsonarray_pics.getJSONObject(i);
                        Pic pic = new Pic();
                        pic.setImgPath(JsonUtils.getString(_jsonObject, "imgPath"));
                        pic.setDesc(JsonUtils.getString(_jsonObject, "desc"));
                        _pics.add(pic);
                    }
                    _creative_detail.setPics(_pics);
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    Log.d("YZP", _creative_detail.getPics().toString() + "YZP");
                    Gson gson = new Gson();
                    mGson_str = gson.toJson(_creative_detail.getPics());
                    Log.d("_gson_", mGson_str);//传出这个zifu
                }

                _creative_detail.setReleaseDate(JsonUtils.getString(jsonObj, "releaseDate"));
                _creative_detail.setRemainTime(JsonUtils.getString(jsonObj, "remainTime"));
                //提交者
                JSONObject _jsonobj_cuser = JsonUtils.getObj(jsonObj, "user");
                if (_jsonobj_cuser != null && _jsonobj_cuser.length() > 0) {
                    CUser _cuser = new CUser();
                    _cuser.setId(JsonUtils.getInt(_jsonobj_cuser, "id", 0));
                    _cuser.setUsername(JsonUtils.getString(_jsonobj_cuser, "username"));
                    _cuser.setUserImg(JsonUtils.getString(_jsonobj_cuser, "userImg"));
                    _cuser.setCreatives(JsonUtils.getInt(_jsonobj_cuser, "creatives", 0));
                    _cuser.setDesigns(JsonUtils.getInt(_jsonobj_cuser, "designs", 0));
                    _cuser.setFans(JsonUtils.getInt(_jsonobj_cuser, "fans", 0));
                    _creative_detail.setUser(_cuser);
                }
                _creative_detail.setViews(JsonUtils.getInt(jsonObj, "views", 0));
                _creative_detail.setCollections(JsonUtils.getInt(jsonObj, "collections", 0));
                _creative_detail.setUps(JsonUtils.getInt(jsonObj, "ups", 0));
                _creative_detail.setDowns(JsonUtils.getInt(jsonObj, "downs", 0));
                //评估
                JSONObject _jsonobj_contentAssess = JsonUtils.getObj(jsonObj, "contentAssess");
                if (_jsonobj_contentAssess != null && _jsonobj_contentAssess.length() > 0) {
                    ContentAssess _contentAssess = new ContentAssess();
                    _contentAssess.setCreateTime(JsonUtils.getString(_jsonobj_contentAssess, "createTime"));
                    _contentAssess.setDraft(JsonUtils.getBoolean(_jsonobj_contentAssess, "draft"));
                    _contentAssess.setText(JsonUtils.getString(_jsonobj_contentAssess, "text"));
                    _contentAssess.setUsername(JsonUtils.getString(_jsonobj_contentAssess, "username"));
                    _creative_detail.setContentAssess(_contentAssess);
                }
                //登录用户的状态
                JSONObject _jsonobj_userStatus = JsonUtils.getObj(jsonObj, "userStatus");
                if (_jsonobj_userStatus != null && _jsonobj_userStatus.length() > 0) {
                    UserStatus _userStatus = new UserStatus();
                    _userStatus.setCollect(JsonUtils.getBoolean(_jsonobj_userStatus, "collect"));
                    _userStatus.setUp(JsonUtils.getBoolean(_jsonobj_userStatus, "up"));
                    _userStatus.setDown(JsonUtils.getBoolean(_jsonobj_userStatus, "down"));
                    _userStatus.setAttention(JsonUtils.getBoolean(_jsonobj_userStatus, "attention"));

                    _creative_detail.setUserStatus(_userStatus);
                }

            }
        }).start();
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        //返回按钮
        mIvBack.setOnClickListener(new CreativeDetailOnClickListener());
        //关注作者
        mTvUserStatusAttention.setOnClickListener(new CreativeDetailOnClickListener());
        //关注创意
        mLineBottomCollect.setOnClickListener(new CreativeDetailOnClickListener());
        //赞
        mLineBottomUp.setOnClickListener(new CreativeDetailOnClickListener());
        //踩
        mLineBottomDown.setOnClickListener(new CreativeDetailOnClickListener());
        //分享
        mLineBottomShare.setOnClickListener(new CreativeDetailOnClickListener());
        //评论框
//		mEtCommentText.setOnFocusChangeListener(new OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View view, boolean hasFocus) {
//				if(!hasFocus){
//					mEtCommentText.setText("");
//					mEtCommentText.setHint("发表评论@创意");
//					mCommentId = 0;//发表评论
//					//关闭输入法
//					if(!InputTools.KeyBoard(mEtCommentText)){
//						InputTools.HideKeyboard(mEtCommentText);
//					}
//				}
//				
//			}
//		});
        //评论框点击
        mEtCommentText.setOnClickListener(new CreativeDetailOnClickListener());
        //发布评论
        mTvCommentAdd.setOnClickListener(new CreativeDetailOnClickListener());
        //详情介绍和评论
        mViewPagerCreativeDetailContent.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mCreativeDetailTabBarLayout.changeTabBarItems(position);

                switch (position) {
                    case 0:
                        //显示action组
                        //隐藏评论
                        mLineBottomAction.setVisibility(View.VISIBLE);
                        mLineBottomComment.setVisibility(View.GONE);
                        break;
                    case 1:
                        //隐藏action组
                        //显示评论
                        mLineBottomAction.setVisibility(View.GONE);
                        mLineBottomComment.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int postion, float percent, int pxLocation) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });
        //顶端图片的点击事件

        mIvAdPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGson_str != null) {
                    Intent intent = new Intent(CreativeDetailActivity.this, AdGalleryActivity.class);
                    intent.putExtra("mgonsn", mGson_str);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreativeDetailActivity.this, "正在加载数据,请稍后....", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCreativeDetailTabBarLayout.setOnItemClickListener(new ICreativeDetailTabBarCallBackListener() {

            @Override
            public void clickItem(int id) {
                switch (id) {
                    case R.id.tv_creativedetail_tabbar_detail_info_tab:
                        mViewPagerCreativeDetailContent.setCurrentItem(FLAG_ITEM_0);
                        //显示action组
                        //隐藏评论
                        mLineBottomAction.setVisibility(View.VISIBLE);
                        mLineBottomComment.setVisibility(View.GONE);
                        break;

                    case R.id.tv_creativedetail_tabbar_comment_tab:
                        mViewPagerCreativeDetailContent.setCurrentItem(FLAG_ITEM_1);
                        //隐藏action组
                        //显示评论
                        mLineBottomAction.setVisibility(View.GONE);
                        mLineBottomComment.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
//		//umeng分享
//		//分享按钮
//		mIvBottomShare.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View view) {
//				Log.i("jlj-webview-title","title="+mWebTitle);
//				
//				//分享的内容
//				String shareTitle = (mWebTitle==null||mWebTitle.equals(""))?"麦客加创意":mWebTitle;
//				String shareContent = "麦客加，为梦想而造。";
//				String targetUrl = CommonConstants.CREATIVE_DETAIL_WAP+mWebId;
//				UMImage umimage = new UMImage(CreativeDetailActivity.this, R.drawable.ic_launcher);
//				
//				//设置分享面板点击后的内容
//				UmShareUtil.shareSomething(mController, umimage, shareTitle, shareContent, targetUrl);
//				
//				 // 是否只有已登录用户才能打开分享选择页
//		        mController.openShare(CreativeDetailActivity.this, false);
//			}
//		});
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
     * 自定义ViewPager的适配器
     *
     * @author JLJ
     */
    private class CreativeDetailInfoFragmentPagerAdapter extends FragmentPagerAdapter {

        public CreativeDetailInfoFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int postion) {
            switch (postion) {
                case FLAG_ITEM_0:
                    creativeDetailInfoFragment = new CreativeDetailInfoFragment();
                    return creativeDetailInfoFragment;
                case FLAG_ITEM_1:
                    creativeDetailCommentFragment = new CreativeDetailCommentFragment();
                    return creativeDetailCommentFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    /**
     * 处理消息队列
     *
     * @author JLJ
     */
    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(CreativeDetailActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
//			if(progressDialog!=null){
//				progressDialog.dismiss();
//			}
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    ((CreativeDetailActivity) mActivity.get()).showTip(errorMsg);
                    break;
                case CommonConstants.FLAG_GET_CREATIVE_DETAIL_SUCCESS:
                    ((CreativeDetailActivity) mActivity.get()).updateCreativeDetailUI();
                    break;
                case CommonConstants.FLAG_GET_CREATIVE_DETAIL_COMMENT_LIST_SUCCESS:
                    ((CreativeDetailActivity) mActivity.get()).updateCreativeCommentUI();
                    break;
                case CommonConstants.FLAG_GET_MEMBER_ATTENTION_SAVE_SUCCESS:
                    ((CreativeDetailActivity) mActivity.get()).updateMemeberIsAttentionUI(CommonConstants.FLAG_GET_MEMBER_ATTENTION_SAVE_SUCCESS);
                    break;
                case CommonConstants.FLAG_GET_MEMBER_ATTENTION_DELETE_SUCCESS:
                    ((CreativeDetailActivity) mActivity.get()).updateMemeberIsAttentionUI(CommonConstants.FLAG_GET_MEMBER_ATTENTION_DELETE_SUCCESS);
                    break;
                case CommonConstants.FLAG_GET_MEMBER_CONTENT_COLLECT_SUCCESS:
                    ((CreativeDetailActivity) mActivity.get()).updateCreativeIsCollectUI();
                    break;
                case CommonConstants.FLAG_GET_MEMBER_CONTENT_UP_SUCCESS:
                    ((CreativeDetailActivity) mActivity.get()).updateCreativeIsUpUI();
                    break;
                case CommonConstants.FLAG_GET_MEMBER_CONTENT_DOWN_SUCCESS:
                    ((CreativeDetailActivity) mActivity.get()).updateCreativeIsDownUI();
                    break;
                case CommonConstants.FLAG_GET_MEMBER_COMMENT_ADD_SUCCESS:
                    ((CreativeDetailActivity) mActivity.get()).updateSendCreativeCommentUI();
                    break;
                case CommonConstants.FLAG_GET_MEMBER_COMMENT_REPLY_ADD_SUCCESS:
                    ((CreativeDetailActivity) mActivity.get()).updateSendCreativeCommentUI();
                    break;
                default:
                    break;
            }
        }
    }

    private MyHandler handler = new MyHandler(this);

    /**
     * 提示信息
     *
     * @param str
     */
    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    /**
     * 更新UI数据-创意详细信息
     */
    public void updateCreativeDetailUI() {

        String.valueOf(Log.d("CreativeDetailActivity", "_creative_detail.getPics().get(0):" + _creative_detail.getPics().get(0)));
        mIvAdPic.setImageUrl(_creative_detail.getPics().get(0).getImgPath(), R.drawable.creative_no_img);
        mIvAdPic.setScaleType(ImageView.ScaleType.FIT_XY);
        mTvCreativeTitle.setText(_creative_detail.getTitle());
        mTvCreativeDesc.setText(_creative_detail.getDesc());

        mTvCreativeReleaseDate.setText("发布于：" + _creative_detail.getReleaseDate());
        mTvCreativeChannel.setText("分类：" + _creative_detail.getChannel().getName());
        mTvCreativeViews.setText("浏览量：" + _creative_detail.getViews());
        //0:草稿；1:审核中；2：审核通过；3：已结束；5：审核未通过；6：已过期
        String _creative_status = null;
        switch (_creative_detail.getStatus()) {
            case 0:
                _creative_status = "草稿";
                break;
            case 1:
                _creative_status = "审核中";
                break;
            case 2:
                _creative_status = "审核通过";
                break;
            case 3:
                _creative_status = "已结束";
                break;
            case 5:
                _creative_status = "审核未通过";
                break;
            case 6:
                _creative_status = "已过期";
                break;
        }
        mTvCreativeStatus.setText("状态：" + _creative_status);
        String userImg = null;
        if (_creative_detail.getUser() != null && _creative_detail.getUser().getUserImg() != null && !_creative_detail.getUser().getUserImg().equals("")) {
            userImg = _creative_detail.getUser().getUserImg() + "!60.60";
        }
        mSivUserHead.setImageUrl(userImg, R.drawable.creative_no_img);
        mTvUsername.setText(_creative_detail.getUser().getUsername());
//		mTvUserStatusAttention.setText(_creative_detail.getCollections());

        mTvCreativeCollections.setText("关注(" + _creative_detail.getCollections() + ")");
        mTvCreativeUps.setText("赞(" + _creative_detail.getUps() + ")");
        mTvCreativeDowns.setText("踩(" + _creative_detail.getDowns() + ")");
        //更新用户状态-是否关注作者或创意、赞、踩
        UserStatus _userStatus = _creative_detail.getUserStatus();
        if (_userStatus != null) {
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_userstatus", _userStatus.toString());
            _attention = _userStatus.isAttention();
            _collect = _userStatus.isCollect();
            _up = _userStatus.isUp();
            _down = _userStatus.isDown();
            if (_attention) {
                mTvUserStatusAttention.setText("已关注");
            } else {
                mTvUserStatusAttention.setText("关注TA");
            }
            if (_collect) {
                mIvBottomCollect.setImageResource(R.drawable.creative_detail_bottom_guanzhu_sel);
            } else {
                mIvBottomCollect.setImageResource(R.drawable.creative_detail_bottom_guanzhu);
            }
            if (_up) {
                mIvBottomUp.setImageResource(R.drawable.creative_detail_bottom_zan_sel);
            } else {
                mIvBottomUp.setImageResource(R.drawable.creative_detail_bottom_zan);
            }
            if (_down) {
                mIvBottomDown.setImageResource(R.drawable.creative_detail_bottom_cai_sel);
            } else {
                mIvBottomDown.setImageResource(R.drawable.creative_detail_bottom_cai);
            }
        }

        //更新创意详情fragment
        updateCreativeDetailInfoFragment();
        //广告图片
        initDataBanner();
        //更新创意评论fragment
        updateCreativeCommentFragment();
    }

    //创意来源和创意详情
    private TextView mTvCreativeSource;
    private TextView mTvCreativeInfo;

    /**
     * 更新创意详情fragment
     */
    private void updateCreativeDetailInfoFragment() {
        if (creativeDetailInfoFragment != null) {
            View _view_creative_detail_info = creativeDetailInfoFragment.getView();
            if (_view_creative_detail_info != null) {
                mTvCreativeSource = (TextView) _view_creative_detail_info.findViewById(R.id.tv_creativedetail_source_info_content);
                mTvCreativeInfo = (TextView) _view_creative_detail_info.findViewById(R.id.tv_creativedetail_detail_info_content);
                if (_creative_detail.getTxt() != null && !_creative_detail.getTxt().equals("")) {
                    mTvCreativeSource.setText(Html.fromHtml(_creative_detail.getTxt()));
                }
                if (_creative_detail.getTxt1() != null && !_creative_detail.getTxt1().equals("")) {
                    mTvCreativeInfo.setText(Html.fromHtml(_creative_detail.getTxt1()));
                }

            }
        }

    }

    //评论
    private CustomListViewWithHeight mlv_creative_detail_comment;
    private TextView mtv_creative_detail_comment_none;
    private List<Comment> mComments;
    private CreativeDetailCommentListAdapter mCreativeDetailCommentListAdapter;

    /**
     * 更新评论UI
     */
    public void updateCreativeCommentUI() {
        if (mComments != null && mComments.size() > 0) {
            mCreativeDetailCommentListAdapter = new CreativeDetailCommentListAdapter(this, mComments);
            mlv_creative_detail_comment.setAdapter(mCreativeDetailCommentListAdapter);
//			CommonUtil.setListViewHeightWithSon(mlv_creative_detail_comment);
        }
    }

    /**
     * 更新创意评论fragment
     */
    private void updateCreativeCommentFragment() {
        if (creativeDetailCommentFragment != null) {
            View _view_creative_detail_comment = creativeDetailCommentFragment.getView();
            if (_view_creative_detail_comment != null) {
                mlv_creative_detail_comment = (CustomListViewWithHeight) _view_creative_detail_comment.findViewById(R.id.lv_creative_detail_comment);
                mtv_creative_detail_comment_none = (TextView) _view_creative_detail_comment.findViewById(R.id.tv_creative_detail_comment_none);
                mlv_creative_detail_comment.setEmptyView(mtv_creative_detail_comment_none);
                final String contentId = String.valueOf(_creative_detail.getId());
                if (contentId != null && !contentId.equals("")) {
                    //创意评论fragment开启副线程获取评论数据
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //差登录用户id
                                String result = mCreativeBusiness.getCreativeDetailCommentList(contentId, null, null);
                                Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_creative_detail_getCreativeDetailCommentList", result);
                                JSONObject jsonObj = new JSONObject(result);
                                boolean Success = jsonObj.getBoolean("success");
                                if (Success) {
                                    fullComments(jsonObj);//填充评论数据
                                    //获取成功
                                    handler.sendEmptyMessage(CommonConstants.FLAG_GET_CREATIVE_DETAIL_COMMENT_LIST_SUCCESS);
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
                                sendErrorMessage("创意详情：" + CommonConstants.MSG_GET_ERROR);
                            }
                        }

                        /**
                         * 填充评论数据
                         * @param jsonObj
                         * @throws JSONException
                         */
                        private void fullComments(JSONObject jsonObj) throws JSONException {
                            JSONArray _datas = JsonUtils.getArray(jsonObj, "datas");
                            if (_datas != null && _datas.length() > 0) {
                                mComments = new ArrayList<Comment>();
                                for (int i = 0; i < _datas.length(); i++) {
                                    JSONObject _json_obj = _datas.getJSONObject(i);
                                    Comment _comment = new Comment();
                                    _comment.setId(JsonUtils.getInt(_json_obj, "id"));
                                    CommentUser _commentUser = new CommentUser();
                                    JSONObject _json_obj_commentUser = JsonUtils.getObj(_json_obj, "commentUser");
                                    _commentUser.setUserImg((JsonUtils.getString(_json_obj_commentUser, "userImg") != null && !JsonUtils.getString(_json_obj_commentUser, "userImg").equals("")) ? (JsonUtils.getString(_json_obj_commentUser, "userImg") + "!60.60") : null);
                                    _commentUser.setUsername(JsonUtils.getString(_json_obj_commentUser, "username"));
                                    _comment.setCommentUser(_commentUser);

                                    JSONArray _json_array_replys = JsonUtils.getArray(_json_obj, "replys");
                                    if (_json_array_replys != null && _json_array_replys.length() > 0) {
                                        List<Reply> _replys = new ArrayList<Reply>();
                                        for (int j = 0; j < _json_array_replys.length(); j++) {
                                            Reply _reply = new Reply();
                                            JSONObject _json_obj_reply = _json_array_replys.getJSONObject(j);
                                            _reply.setId(JsonUtils.getInt(_json_obj_reply, "id", 0));
                                            _reply.setType(JsonUtils.getInt(_json_obj_reply, "type", 1));
                                            CommentUser _reply_commUser = new CommentUser();//评论的用户
                                            JSONObject _json_commentUser = JsonUtils.getObj(_json_obj_reply, "user");
                                            _reply_commUser.setUsername(JsonUtils.getString(_json_commentUser, "username"));
                                            _reply_commUser.setUserImg((JsonUtils.getString(_json_commentUser, "userImg") != null && !JsonUtils.getString(_json_commentUser, "userImg").equals("")) ? (JsonUtils.getString(_json_commentUser, "userImg") + "!60.60") : null);
                                            _reply.setUser(_reply_commUser);

                                            CommentUser _reply_replyUser = new CommentUser();//回复评论的用户
                                            JSONObject _json_replyUser = JsonUtils.getObj(_json_obj_reply, "replyUser");
                                            _reply_replyUser.setUsername(JsonUtils.getString(_json_replyUser, "username"));
                                            _reply_replyUser.setUserImg((JsonUtils.getString(_json_replyUser, "userImg") != null && !JsonUtils.getString(_json_replyUser, "userImg").equals("")) ? (JsonUtils.getString(_json_replyUser, "userImg") + "!60.60") : null);
                                            _reply.setReplyUser(_reply_replyUser);

                                            _reply.setText(JsonUtils.getString(_json_obj_reply, "text"));
                                            _reply.setCreateTime(JsonUtils.getString(_json_obj_reply, "createTime"));
                                            _replys.add(_reply);
                                        }
                                        _comment.setReplys(_replys);
                                    }


                                    _comment.setText(JsonUtils.getString(_json_obj, "text"));
                                    _comment.setCreateTime(JsonUtils.getString(_json_obj, "createTime"));
                                    _comment.setReplyCount(JsonUtils.getInt(_json_obj, "replyCount", 0));
                                    mComments.add(_comment);
                                }

                            }
                        }
                    }).start();
                }

            }

        }

        setCommentFragmentListener();//设置评论监听
    }

    /**
     * 设置评论监听
     */
    private void setCommentFragmentListener() {
        if (mlv_creative_detail_comment != null) {
            mlv_creative_detail_comment.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                        long id) {
                    adapterView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                    Comment _comment = mComments.get(position);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_item_click", _comment.getText());
                    int _commentId = _comment.getId();
                    String _username = _comment.getCommentUser().getUsername();
                    int _type = 1;
                    openImInput(_username, _type, _commentId);//打开输入法


                }

            });
        }
    }

    /**
     * 初始化数据-banner
     */
    private void initDataBanner() {
        mBannerList = _creative_detail.getPics();
        if (mBannerList != null && mBannerList.size() > 0) {
            LayoutInflater inflater = getLayoutInflater();
            for (int i = 0; i < mBannerList.size(); i++) {
                View _view = inflater.inflate(R.layout.ad_scroll_item_img, null);
                SmartImageView _siv = (SmartImageView) _view.findViewById(R.id.iv_ad_scroll_img);
                _siv.setImageUrl(mBannerList.get(i).getImgPath() + "!400.300", R.drawable.creative_no_img);
                bitMap.add(_view);
            }
//			mAdScrollLayout.setSize(mBannerList.size());
            //创建适配器
            creativeDetailAdPagerAdapter = new MyAdPagerAdapter();
            //设置适配器
//			mViewPagerAdScroll.setAdapter(creativeDetailAdPagerAdapter);
            //先取消清空上一次定时器
//			if(mAdScrollLayout!=null){
//				mAdScrollLayout.cancelPageFromTime();
//			}
//			//开启轮播
//			mAdScrollLayout.setPageFromTime(3000);
        } else {
            mIvCreative_detail_no_banner = (ImageView) findViewById(R.id.iv_creative_detail_no_banner);
            mIvCreative_detail_no_banner.setVisibility(View.VISIBLE);
//			mAdScrollLayout.setVisibility(View.GONE);
        }


    }

    @Override
    public void detailInfoDataInit() {
        // TODO Auto-generated method stub

    }

    @Override
    public void detailCommentDataInit() {
        // TODO Auto-generated method stub

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

    private class CreativeDetailOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_common_topbar_back:
                    CreativeDetailActivity.this.finish();
                    break;
                case R.id.tv_creativedetail_userbar_attention_user:
                    //关注作者
                    userActionAttentionUserFromNet();
                    break;
                case R.id.line_creativedetail_bottombar_attention:
                    //关注创意
                    userActionAttentionCreativeFromNet();
                    break;
                case R.id.line_creativedetail_bottombar_up:
                    //赞
                    userActionUpCreativeFromNet();
                    break;
                case R.id.line_creativedetail_bottombar_down:
                    //踩
                    userActionDownCreativeFromNet();
                    break;
                case R.id.line_creativedetail_bottombar_share:
                    //分享
//				userActionShareCreative();
                    Toast.makeText(CreativeDetailActivity.this, "暂不支持分享创意！", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.et_creativedetail_bottom_comment_text:
                    //点击了评论框
//				userActionCommentEditText();
                    userCheckIsLogin();
                    break;
                case R.id.tv_creativedetail_bottom_comment_add:
                    //点击了发表评论
                    userActionCommentAdd();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 关注用户
     */
    public void userActionAttentionUserFromNet() {
        //未登录用户，提示登录
        if (mUser == null) {
            Toast.makeText(this, "亲，请先去个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
        //是否关注过该创意提交人、是否关注过创意、是否赞、是否踩
        if (_attention) {
            //关注了
            //开启副线程-取消关注该创意的提交人
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String result = mCreativeBusiness.getMemberAttentionDelete(mUser, _creative_detail.getUser().getId());
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_creative_detail_getMemberAttentionDelete", result);
                        JSONObject jsonObj = new JSONObject(result);
                        boolean Success = jsonObj.getBoolean("success");
                        if (Success) {
                            //获取成功
                            handler.sendEmptyMessage(CommonConstants.FLAG_GET_MEMBER_ATTENTION_DELETE_SUCCESS);
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
                        sendErrorMessage("取消关注用户：" + CommonConstants.MSG_GET_ERROR);
                    }
                }
            }).start();
        } else {
            //未关注
            //开启副线程-关注该创意的提交人
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String result = mCreativeBusiness.getMemberAttentionSave(mUser, _creative_detail.getUser().getId());
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_creative_detail_getMemberAttentionSave", result);
                        JSONObject jsonObj = new JSONObject(result);
                        boolean Success = jsonObj.getBoolean("success");
                        if (Success) {
                            //获取成功
                            handler.sendEmptyMessage(CommonConstants.FLAG_GET_MEMBER_ATTENTION_SAVE_SUCCESS);
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
                        sendErrorMessage("关注用户：" + CommonConstants.MSG_GET_ERROR);
                    }
                }
            }).start();
        }


    }

    /**
     * 检查用户是否登录
     */
    public void userCheckIsLogin() {
        //未登录用户，提示登录
        if (mUser == null) {
            Toast.makeText(this, "亲，请先去个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 刷新是否关注用户UI
     *
     * @param flagGetMemberAttentionSaveSuccess
     */
    public void updateMemeberIsAttentionUI(int flagGetMemberAttentionSaveSuccess) {
        switch (flagGetMemberAttentionSaveSuccess) {
            case CommonConstants.FLAG_GET_MEMBER_ATTENTION_SAVE_SUCCESS:
                Toast.makeText(this, "已关注", Toast.LENGTH_SHORT).show();
                _attention = true;//已关注，下次点击，调用取消关注接口
                mTvUserStatusAttention.setText("已关注");
                break;
            case CommonConstants.FLAG_GET_MEMBER_ATTENTION_DELETE_SUCCESS:
                Toast.makeText(this, "已取消关注", Toast.LENGTH_SHORT).show();
                _attention = false;
                mTvUserStatusAttention.setText("关注TA");
                break;
            default:
                break;
        }
    }

    /**
     * 关注创意
     */
    public void userActionAttentionCreativeFromNet() {
        //未登录用户，提示登录
        if (mUser == null) {
            Toast.makeText(this, "亲，请先去个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
        //不管是否关注了创意，都通过该接口-关注或取消关注
        //开启副线程-关注创意或取消关注(已经关注了的，再点一下是取消关注)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mCreativeBusiness.getMemberContentCollect(mUser, _creative_detail.getId());
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_creative_detail_getMemberContentCollect", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        //获取成功
                        mCollectNum = JsonUtils.getInt(jsonObj, "count", 0);
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_MEMBER_CONTENT_COLLECT_SUCCESS);
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
                    sendErrorMessage("关注创意：" + CommonConstants.MSG_GET_ERROR);
                }
            }
        }).start();
    }

    private int mCollectNum;//关注数量

    /**
     * 刷新是否关注创意UI
     */
    public void updateCreativeIsCollectUI() {
        if (_collect) {
            //以前是已关注该创意，点击之后改为取消关注，更新关注数量
            Toast.makeText(this, "已取消关注", Toast.LENGTH_SHORT).show();
            _collect = false;
            mIvBottomCollect.setImageResource(R.drawable.creative_detail_bottom_guanzhu);
            mTvCreativeCollections.setText("关注(" + String.valueOf(mCollectNum) + ")");
        } else {
            //以前是未关注该创意，点击之后改为已关注，更新关注数量
            Toast.makeText(this, "已关注", Toast.LENGTH_SHORT).show();
            _collect = true;
            mIvBottomCollect.setImageResource(R.drawable.creative_detail_bottom_guanzhu_sel);
            mTvCreativeCollections.setText("关注(" + String.valueOf(mCollectNum) + ")");
        }
    }

    /**
     * 赞
     */
    public void userActionUpCreativeFromNet() {
        //未登录用户，提示登录
        if (mUser == null) {
            Toast.makeText(this, "亲，请先去个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
        //不管是否赞了创意，都通过该接口-赞或取消赞
        //开启副线程-赞或取消赞(已经赞了的，再点一下是取消赞)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mCreativeBusiness.getMemberContentUp(mUser, _creative_detail.getId());
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_creative_detail_getMemberContentUp", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        //获取成功
                        mUpNum = JsonUtils.getInt(jsonObj, "count", 0);
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_MEMBER_CONTENT_UP_SUCCESS);
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
                    sendErrorMessage("赞创意：" + CommonConstants.MSG_GET_ERROR);
                }
            }
        }).start();

    }

    private int mUpNum;//赞数量

    /**
     * 刷新是否赞了创意UI
     */
    public void updateCreativeIsUpUI() {
        if (_up) {
            //以前是已赞了该创意，点击之后改为取消赞，更新赞数量
            Toast.makeText(this, "已取消赞", Toast.LENGTH_SHORT).show();
            _up = false;
            mIvBottomUp.setImageResource(R.drawable.creative_detail_bottom_zan);
            mTvCreativeUps.setText("赞(" + String.valueOf(mUpNum) + ")");
        } else {
            //以前是未赞该创意，点击之后改为已赞，更新赞数量
            Toast.makeText(this, "已赞", Toast.LENGTH_SHORT).show();
            _up = true;
            mIvBottomUp.setImageResource(R.drawable.creative_detail_bottom_zan_sel);
            mTvCreativeUps.setText("赞(" + String.valueOf(mUpNum) + ")");
        }
    }

    /**
     * 踩
     */
    public void userActionDownCreativeFromNet() {
        //未登录用户，提示登录
        if (mUser == null) {
            Toast.makeText(this, "亲，请先去个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
        //不管是否踩了创意，都通过该接口-踩或取消踩
        //开启副线程-踩或取消踩(已经踩了的，再点一下是取消踩)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mCreativeBusiness.getMemberContentDown(mUser, _creative_detail.getId());
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_creative_detail_getMemberContentDown", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        //获取成功
                        mDonwNum = JsonUtils.getInt(jsonObj, "count", 0);
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_MEMBER_CONTENT_DOWN_SUCCESS);
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
                    sendErrorMessage("踩创意：" + CommonConstants.MSG_GET_ERROR);
                }
            }
        }).start();

    }

    private int mDonwNum;//踩数量

    /**
     * 刷新是否踩了创意UI
     */
    public void updateCreativeIsDownUI() {
        if (_down) {
            //以前是已踩了该创意，点击之后改为取消踩，更新踩数量
            Toast.makeText(this, "已取消踩", Toast.LENGTH_SHORT).show();
            _down = false;
            mIvBottomDown.setImageResource(R.drawable.creative_detail_bottom_cai);
            mTvCreativeDowns.setText("踩(" + String.valueOf(mDonwNum) + ")");
        } else {
            //以前是未踩该创意，点击之后改为已踩，更新踩数量
            Toast.makeText(this, "已踩", Toast.LENGTH_SHORT).show();
            _down = true;
            mIvBottomDown.setImageResource(R.drawable.creative_detail_bottom_cai_sel);
            mTvCreativeDowns.setText("踩(" + String.valueOf(mDonwNum) + ")");
        }
    }

    /**
     * 分享创意
     */
    public void userActionShareCreative() {
        //umeng分享
        //分享的内容
        String shareTitle = (mWebTitle == null || mWebTitle.equals("")) ? "麦客加创意" : mWebTitle;
        String shareContent = "麦客加，为梦想而造。";
        String targetUrl = CommonConstants.CREATIVE_DETAIL_WAP + mWebId;
        UMImage umimage = new UMImage(CreativeDetailActivity.this, R.drawable.ic_launcher);

        //设置分享面板点击后的内容
        UmShareUtil.shareSomething(mController, umimage, shareTitle, shareContent, targetUrl);

        // 是否只有已登录用户才能打开分享选择页
        mController.openShare(CreativeDetailActivity.this, false);
    }

    /**
     * 评论或回复
     */
    public void userActionCommentAdd() {
        this.hideInput();//手动隐藏输入法
        //未登录用户，提示登录
        if (mUser == null) {
            Toast.makeText(this, "亲，请先去个人中心登录", Toast.LENGTH_SHORT).show();
            return;
        }
        String _comment_text = mEtCommentText.getText().toString();
        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_creative_detail_mCommentToUserId", "mCommentToUserId=" + mCommentId);
        if (_comment_text != null && !_comment_text.equals("")) {
            if (mCommentId == 0) {
                //评论
                //开启副线程-提交评论内容，再发送刷新界面消息
                sendCommentContentForCreative(_comment_text);
            } else {
                //回复
                //开启副线程-提交回复内容，再发送刷新界面消息
                sendReplyContentForCreative(_comment_text, mCommentId);
            }
            mEtCommentText.setText("");//评论之后-清空评论内容
        } else {
            Toast.makeText(this, "您未填写评论内容", Toast.LENGTH_SHORT).show();
        }


    }

//	/**
//	 * 点击了评论框
//	 */
//	public void userActionCommentEditText() {
//		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_click_editText", String.valueOf(InputTools.KeyBoard(mEtCommentText)));
//		//是否可打开输入法
//		if(InputTools.KeyBoard(mEtCommentText)){
//			int _type = 1;//评论
//			int _commentId = 0;//评论创意
//			String _username="";
//			openImInput(_username, _type, _commentId);
//		}else{
//			InputTools.HideKeyboard(mEtCommentText);
//		}
//		
//	}

    /**
     * 提交创意评论
     *
     * @param _comment_text 评论内容
     */
    private void sendCommentContentForCreative(final String _comment_text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mCreativeBusiness.getMemberCommentAdd(mUser, _creative_detail.getId(), _comment_text);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_creative_detail_getMemberCommentAdd", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_MEMBER_COMMENT_ADD_SUCCESS);
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
                    sendErrorMessage("发表评论：暂时只支持文字评论");
                }
            }
        }).start();

    }

    /**
     * 发送评论的回复内容
     *
     * @param _comment_text    回复内容
     * @param replyToCommentId 回复的评论id
     */
    private void sendReplyContentForCreative(final String _comment_text,
                                             final int replyToCommentId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mCreativeBusiness.getMemberCommentReplyAdd(mUser, replyToCommentId, mUsername, mType, _comment_text);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_creative_detail_getMemberCommentReplyAdd", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_MEMBER_COMMENT_REPLY_ADD_SUCCESS);
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
                    sendErrorMessage("回复评论：暂时只支持文字回复");
                }
            }
        }).start();
    }

    /**
     * 发送创意评论后-刷新UI界面
     */
    public void updateSendCreativeCommentUI() {

        Toast.makeText(this, "已发表", Toast.LENGTH_SHORT).show();
        updateCreativeCommentFragment();//更新创意评论fragment

    }

    /**
     * 打开输入法，填充用户信息和评论ID
     *
     * @param username
     * @param type
     * @param commentId
     */
    public void openImInput(String username, int type, int commentId) {
        mCommentId = commentId;//评论或者回复的ID
        mUsername = username;//对谁发表
        mType = type;//1回复他人的评论  2回复他人的回复
        //发表评论框获取焦点
        mEtCommentText.setFocusable(true);
        mEtCommentText.setFocusableInTouchMode(true);
        mEtCommentText.requestFocus();

        if (mEtCommentText.getText().toString() == null || mEtCommentText.getText().toString().equals("")) {
            if (commentId == 0) {
                mEtCommentText.setHint("发表评论@创意");
            } else {
                if (mType == 1) {
                    mEtCommentText.setHint("回复他人的评论@" + username);
                } else if (mType == 2) {
                    mEtCommentText.setHint("回复他人的回复@" + username);
                }

            }
        }
        if (commentId != 0) {
            //是否可打开输入法
            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_openImInput", String.valueOf(InputTools.KeyBoard(mEtCommentText)));
            if (InputTools.KeyBoard(mEtCommentText)) {
                InputTools.ShowKeyboard(mEtCommentText);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    //重置成-给创意发表评论
                    mEtCommentText.setText("");
                    mEtCommentText.setHint("发表评论@创意");
                    mCommentId = 0;//发表评论
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }


    /**
     * 是否需要隐藏输入法
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
//			if (event.getX() > left && event.getX() < right
//					&& event.getY() > top && event.getY() < bottom) {
            if (event.getX() > left
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 手动隐藏输入法
     */
    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mEtCommentText.getWindowToken(), 0);
        }
    }
}
