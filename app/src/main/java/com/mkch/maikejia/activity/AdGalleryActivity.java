package com.mkch.maikejia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.image.SmartImageView;
import com.mkch.maikejia.R;
import com.mkch.maikejia.bean.Pic;

import java.util.List;

public class AdGalleryActivity extends Activity {

    private android.support.v4.view.ViewPager mVpAdScroll;
    private List<Pic> mPics;
    private ImageView mIvBack;
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_gallery);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdGalleryActivity.this.finish();
            }
        });
    }


    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_common_topbar_back);
        mTvTitle = (TextView) findViewById(R.id.tv_common_topbar_title);
        this.mVpAdScroll = (ViewPager) findViewById(R.id.vp_ad_scroll);
    }

    private void initData() {
        mTvTitle.setText("创意展示");

        //解析出传过来的数据
        Gson gson = new Gson();
        Intent i = getIntent();
        String s = i.getStringExtra("mgonsn");
        mPics = gson.fromJson(s,
                new TypeToken<List<Pic>>() {
                }.getType());
        Log.d("AdGalleryActivity", mPics.toString());
        mVpAdScroll.setAdapter(new TopNewsAdapter());
    }

    class TopNewsAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return mPics.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SmartImageView mSiv = new SmartImageView(AdGalleryActivity.this);
            LinearLayout.LayoutParams _layoutParams = new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mSiv.setLayoutParams(_layoutParams);
            mSiv.setImageUrl(mPics.get(position).getImgPath(),R.drawable.creative_no_img);
//            ImageView image = new ImageView(mActivity);
            mSiv.setScaleType(ImageView.ScaleType.FIT_CENTER);// 基于控件大小填充图片
            container.addView(mSiv);
//            image.setOnTouchListener(new TopNewsTouchListener());// 设置触摸监听
            return mSiv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }



}
