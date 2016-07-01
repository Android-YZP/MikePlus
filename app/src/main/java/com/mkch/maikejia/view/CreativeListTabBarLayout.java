package com.mkch.maikejia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkch.maikejia.R;

public class CreativeListTabBarLayout extends LinearLayout {
    //tabbar的回调接口
    public interface ICreativeListTabBarCallBackListener {
        public void clickItem(int id);//按了某一项后
    }

    ICreativeListTabBarCallBackListener creativeListTabBarCallBackListener = null;

    public void setOnItemClickListener(ICreativeListTabBarCallBackListener creativeListTabBarCallBackListener) {
        this.creativeListTabBarCallBackListener = creativeListTabBarCallBackListener;
    }


    LayoutInflater inflater;

    private TextView mTvItem0;
    private TextView mTvItem1;
    private TextView mTvItem2;
    private TextView mTvItem3;
    private TextView mTvItem4;
    private TextView mTvItem5;
    private TextView mTvItem6;
    private TextView mTvItem7;
    private TextView mTvItem8;
    private TextView mTvItem9;

    private final static int FLAG_ITEM_0 = 0;
    private final static int FLAG_ITEM_1 = 1;
    private final static int FLAG_ITEM_2 = 2;
    private final static int FLAG_ITEM_3 = 3;
    private final static int FLAG_ITEM_4 = 4;
    private final static int FLAG_ITEM_5 = 5;
    private final static int FLAG_ITEM_6 = 6;
    private final static int FLAG_ITEM_7 = 7;
    private final static int FLAG_ITEM_8 = 8;
    private final static int FLAG_ITEM_9 = 9;

    private HorizontalScrollView mHorizontalScrollView;

    public CreativeListTabBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_find_creative_tabbar, this);
        findView(view);
        initData();
        setListener();
    }

    private void findView(View view) {
        mTvItem0 = (TextView) view.findViewById(R.id.find_creative_item0);
        mTvItem1 = (TextView) view.findViewById(R.id.find_creative_item1);
        mTvItem2 = (TextView) view.findViewById(R.id.find_creative_item2);
        mTvItem3 = (TextView) view.findViewById(R.id.find_creative_item3);
        mTvItem4 = (TextView) view.findViewById(R.id.find_creative_item4);
        mTvItem5 = (TextView) view.findViewById(R.id.find_creative_item5);
        mTvItem6 = (TextView) view.findViewById(R.id.find_creative_item6);
        mTvItem7 = (TextView) view.findViewById(R.id.find_creative_item7);
        mTvItem8 = (TextView) view.findViewById(R.id.find_creative_item8);
        mTvItem9 = (TextView) view.findViewById(R.id.find_creative_item9);
        mHorizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.hsv_find_creative_tabbar);
    }

    private void initData() {
        mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
        mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
        mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
        mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
        mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
        mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
        mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
        mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
        mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
        mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
    }

    private void setListener() {
        mTvItem0.setOnClickListener(new CreativeListItemClickListener());
        mTvItem1.setOnClickListener(new CreativeListItemClickListener());
        mTvItem2.setOnClickListener(new CreativeListItemClickListener());
        mTvItem3.setOnClickListener(new CreativeListItemClickListener());
        mTvItem4.setOnClickListener(new CreativeListItemClickListener());
        mTvItem5.setOnClickListener(new CreativeListItemClickListener());
        mTvItem6.setOnClickListener(new CreativeListItemClickListener());
        mTvItem7.setOnClickListener(new CreativeListItemClickListener());
        mTvItem8.setOnClickListener(new CreativeListItemClickListener());
        mTvItem9.setOnClickListener(new CreativeListItemClickListener());
    }

    private class CreativeListItemClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.find_creative_item0:
                    //1-改变文字的颜色;
                    changeTabBarItems(FLAG_ITEM_0);
                    //2-实现页面的切换
                    //3-设置tabbar的偏移量
                    setTabBarPosition(FLAG_ITEM_0);
                    break;
                case R.id.find_creative_item1:
                    changeTabBarItems(FLAG_ITEM_1);
                    setTabBarPosition(FLAG_ITEM_1);
                    break;
                case R.id.find_creative_item2:
                    changeTabBarItems(FLAG_ITEM_2);
                    setTabBarPosition(FLAG_ITEM_2);
                    break;
                case R.id.find_creative_item3:
                    changeTabBarItems(FLAG_ITEM_3);
                    setTabBarPosition(FLAG_ITEM_3);
                    break;
                case R.id.find_creative_item4:
                    changeTabBarItems(FLAG_ITEM_4);
                    setTabBarPosition(FLAG_ITEM_4);
                    break;
                case R.id.find_creative_item5:
                    changeTabBarItems(FLAG_ITEM_5);
                    setTabBarPosition(FLAG_ITEM_5);
                    break;
                case R.id.find_creative_item6:
                    changeTabBarItems(FLAG_ITEM_6);
                    setTabBarPosition(FLAG_ITEM_6);
                    break;
                case R.id.find_creative_item7:
                    changeTabBarItems(FLAG_ITEM_7);
                    setTabBarPosition(FLAG_ITEM_7);
                    break;
                case R.id.find_creative_item8:
                    changeTabBarItems(FLAG_ITEM_8);
                    setTabBarPosition(FLAG_ITEM_8);
                    break;
                case R.id.find_creative_item9:
                    changeTabBarItems(FLAG_ITEM_9);
                    setTabBarPosition(FLAG_ITEM_9);
                    break;
            }
            //2-实现页面的切换
            if (creativeListTabBarCallBackListener != null) {
                creativeListTabBarCallBackListener.clickItem(view.getId());
            }
        }


    }

    public void setTabBarPosition(int index) {
        int scrollpx = 50;
        switch (index) {
            case FLAG_ITEM_0:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_0 * scrollpx, 0);
                break;
            case FLAG_ITEM_1:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_1 * scrollpx, 0);
                break;
            case FLAG_ITEM_2:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_2 * scrollpx, 0);
                break;
            case FLAG_ITEM_3:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_3 * scrollpx, 0);
                break;
            case FLAG_ITEM_4:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_4 * scrollpx, 0);
                break;
            case FLAG_ITEM_5:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_5 * scrollpx, 0);
                break;
            case FLAG_ITEM_6:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_6 * scrollpx, 0);
                break;
            case FLAG_ITEM_7:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_7 * scrollpx, 0);
                break;
            case FLAG_ITEM_8:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_8 * scrollpx, 0);
                break;
            case FLAG_ITEM_9:
                mHorizontalScrollView.smoothScrollTo(FLAG_ITEM_9 * scrollpx, 0);
                break;
        }
    }

    public void changeTabBarItems(int index) {
        switch (index) {
            case FLAG_ITEM_0:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                break;
            case FLAG_ITEM_1:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                break;
            case FLAG_ITEM_2:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                break;
            case FLAG_ITEM_3:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                break;
            case FLAG_ITEM_4:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                break;
            case FLAG_ITEM_5:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                break;
            case FLAG_ITEM_6:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                break;
            case FLAG_ITEM_7:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                break;
            case FLAG_ITEM_8:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                break;
            case FLAG_ITEM_9:
                mTvItem0.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem1.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem2.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem3.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem4.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem5.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem6.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem7.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem8.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_unsel));
                mTvItem9.setTextColor(getResources().getColor(R.color.main_tabbar_item_text_font_color_sel));
                break;
        }
        this.setTabBarPosition(index);
    }
}
