package com.mkch.maikejia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ScrollView;
/**
 * 解决上下滑动和左右滑动的冲突
 * @author JLJ
 *
 */
public class IndexScrollView extends ScrollView {

	private float xStartLocation;
	private float yStartLocation;
	private float xDistance;
	private float yDistance;
	
	public IndexScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 事件分发
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 事件拦截
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//当指尖按下的时候获取开始地点的x值和y值
				xStartLocation = ev.getX();
				yStartLocation = ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				//当滑动的时候算出他们的x距离和y距离
				float xCurrentLocation = ev.getX();
				float yCurrentLocation = ev.getY();
				xDistance += Math.abs(xCurrentLocation-xStartLocation);
				yDistance += Math.abs(yCurrentLocation-yStartLocation);
				//滑动结束之后把最新的x、y值填上
				xStartLocation = xCurrentLocation;
				yStartLocation = yCurrentLocation;
				if(xDistance>yDistance){
					//说明是左右滑动
					return false;
				}
//				if(yDistance>xDistance){
//					this.requestDisallowInterceptTouchEvent(false);
//				}
				break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	
}
