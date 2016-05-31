package com.mkch.maikejia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
/**
 * 自动调整高度ListView
 * @author JLJ
 *
 */
public class CustomListViewWithHeight extends ListView {

	public CustomListViewWithHeight(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
    }  
}
