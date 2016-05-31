package com.mkch.maikejia.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	/**
	 * 
	 * @param str
	 */
	private static void showTip(Context context,String str){
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
}
