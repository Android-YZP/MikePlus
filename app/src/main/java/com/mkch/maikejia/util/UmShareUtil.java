package com.mkch.maikejia.util;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class UmShareUtil {
	
	public static void shareSomething(UMSocialService mController,UMImage umimage, String shareTitle, String shareContent, String targetUrl){
		
		// 设置分享内容=============================================
		mController.setShareContent(shareTitle+"，"+targetUrl);
		// 设置分享图片, 参数2为图片的url地址
		mController.setShareMedia(umimage);
		
		//设置微信好友分享内容=============================================
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		//设置分享文字
		weixinContent.setShareContent(shareContent);
		//设置title
		weixinContent.setTitle(shareTitle);
		//设置分享内容跳转URL
		weixinContent.setTargetUrl(targetUrl);
		//设置分享图片
		weixinContent.setShareImage(umimage);
		mController.setShareMedia(weixinContent);
		
		//设置微信朋友圈分享内容=============================================
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(shareContent);
		//设置朋友圈title
		circleMedia.setTitle(shareTitle);
		circleMedia.setShareImage(umimage);
		circleMedia.setTargetUrl(targetUrl);
		mController.setShareMedia(circleMedia);
		
		
		//QQ分享内容=============================================
	  	QQShareContent qqShareContent = new QQShareContent();
		//设置分享文字
		qqShareContent.setShareContent(shareContent);
		//设置分享title
		qqShareContent.setTitle(shareTitle);
		//设置分享图片
		qqShareContent.setShareImage(umimage);
		//设置点击分享内容的跳转链接
		qqShareContent.setTargetUrl(targetUrl);
		mController.setShareMedia(qqShareContent);
		
		//QQ空间分享内容=============================================
		QZoneShareContent qzone = new QZoneShareContent();
		//设置分享文字
		qzone.setShareContent(shareContent);
		//设置点击消息的跳转URL
		qzone.setTargetUrl(targetUrl);
		//设置分享内容的标题
		qzone.setTitle(shareTitle);
		//设置分享图片
		qzone.setShareImage(umimage);
		mController.setShareMedia(qzone);
		
	}


	public static void initShareArgs(UMSocialService mController,
			final FragmentActivity activity) {
		mController.getConfig().removePlatform( SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
		
		//添加微信平台到分享列表
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appID = "wx4433fba6694b4829";
		String appSecret = "b024db56942cca1f9dbf5a720e1fcafe";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(activity,appID,appSecret);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(activity,appID,appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		
		//分享提交回调--
		SnsPostListener mSnsPostListener  = new SnsPostListener() {

	        @Override
	    public void onStart() {

	    }

	    @Override
	    public void onComplete(SHARE_MEDIA platform, int stCode,
	        SocializeEntity entity) {
	      if (stCode == 200) {
	        Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT)
	            .show();
//	      } else {
//	        Toast.makeText(activity,
//	            "分享失败，错误码: " + stCode, Toast.LENGTH_SHORT)
//	            .show();
	      }
	    }
	  };
	  mController.registerListener(mSnsPostListener);
	  
	  
	  //添加QQ到分享列表
	  //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
	  UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, "1105283336",
	                  "ygvq7u7q50mkWBAZ");
	  qqSsoHandler.addToSocialSDK(); 
	  
	  //添加QQ控件到分享列表
	  //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
	  QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, "1105283336",
	                  "ygvq7u7q50mkWBAZ");
	  qZoneSsoHandler.addToSocialSDK();
	}


	public static void initShareArgs(UMSocialService mController,
			final Activity activity) {
		mController.getConfig().removePlatform( SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
		
		//添加微信平台到分享列表
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appID = "wx4433fba6694b4829";
		String appSecret = "b024db56942cca1f9dbf5a720e1fcafe";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(activity,appID,appSecret);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(activity,appID,appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		
		//分享提交回调--
		SnsPostListener mSnsPostListener  = new SnsPostListener() {

	        @Override
	    public void onStart() {

	    }

	    @Override
	    public void onComplete(SHARE_MEDIA platform, int stCode,
	        SocializeEntity entity) {
	      if (stCode == 200) {
	        Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT)
	            .show();
//	      } else {
//	        Toast.makeText(activity,
//	            "分享失败，错误码: " + stCode, Toast.LENGTH_SHORT)
//	            .show();
	      }
	    }
	  };
	  mController.registerListener(mSnsPostListener);
	  
	  
	  //添加QQ到分享列表
	  //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
	  UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, "1105283336",
	                  "ygvq7u7q50mkWBAZ");
	  qqSsoHandler.addToSocialSDK(); 
	  
	  //添加QQ控件到分享列表
	  //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
	  QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, "1105283336",
	                  "ygvq7u7q50mkWBAZ");
	  qZoneSsoHandler.addToSocialSDK();
		
	}
}
