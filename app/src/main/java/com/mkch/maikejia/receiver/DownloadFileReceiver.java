package com.mkch.maikejia.receiver;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class DownloadFileReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("jlj-receiver", "onreceiver1");
		if(intent.getAction().equals("com.maikejia.downloadComplete")){
			Log.d("jlj-receiver", "onreceiver2");
			Intent install = new Intent(Intent.ACTION_VIEW);
            String pathString = intent.getStringExtra("downloadFile");
            install.setDataAndType(Uri.fromFile(new File(pathString)), "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
		}
	}

}
