package com.community.yuequ.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class ShareHelper implements IWeiboHandler.Response{
	public static final int SHARE_CLIENT = 1;
	public static final int SHARE_ALL_IN_ONE = 2;
	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;
	private final int mShareType = SHARE_ALL_IN_ONE;

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;
	private Activity context;

	

	public ShareHelper(Activity context, Bundle savedInstanceState) {
		this.context = context;
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(context, Contants.WX_APP_ID, true);
		api.registerApp(Contants.WX_APP_ID);

		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context,Contants.WeiBo_APP_KEY);
		mWeiboShareAPI.registerApp();
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(context.getIntent(), this);
		}

	}

	/**
	 * @see {@link Activity#onNewIntent}
	 */
	public void onNewIntent(Intent intent) {
		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}



	public boolean isWXAppInstalled() {
		return api.isWXAppInstalled() && api.isWXAppSupportAPI();
	}

	public void sendTextToWX(boolean isTimeline, String text) {
		if (!isWXAppInstalled()) {
			Toast.makeText(context, "请您安装微信后再分享！", Toast.LENGTH_LONG)
					.show();
			return;
		}
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = text;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;

		// 调用api接口发送数据到微信
		api.sendReq(req);

	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	public void sendSms(String text) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", text);
		context.startActivity(intent);

	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。
	 * 
	 * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
	 */
	public void sendMessage(String text) {

		if (mShareType == SHARE_CLIENT) {
			if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
				int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
				if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
					sendMultiMessage(text);
				} else {
					sendSingleMessage(text);
				}
			} else {

				Toast.makeText(context,
						R.string.weibosdk_demo_not_support_api_hint,
						Toast.LENGTH_SHORT).show();
			}
		} else if (mShareType == SHARE_ALL_IN_ONE) {
			sendMultiMessage(text);
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
	 * {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
	 * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
	 */
	private void sendMultiMessage(String text) {

		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		if (!TextUtils.isEmpty(text)) {
			weiboMessage.textObject = getTextObj(text);
		}
		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		if (mShareType == SHARE_CLIENT) {
			mWeiboShareAPI.sendRequest(context, request);
		} else if (mShareType == SHARE_ALL_IN_ONE) {
			AuthInfo authInfo = new AuthInfo(context, Contants.WeiBo_APP_KEY,Contants.REDIRECT_URL, Contants.SCOPE);
			Oauth2AccessToken accessToken = AccessTokenKeeper
					.readAccessToken(context.getApplicationContext());
			String token = "";
			if (accessToken != null) {
				token = accessToken.getToken();
			}
			mWeiboShareAPI.sendRequest(context, request, authInfo, token,
					new WeiboAuthListener() {
						@Override
						public void onWeiboException(WeiboException arg0) {

						}

						@Override
						public void onComplete(Bundle bundle) {
							// TODO Auto-generated method stub
							Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
							AccessTokenKeeper.writeAccessToken(context.getApplicationContext(), newToken);
							Toast.makeText(context.getApplicationContext(),R.string.weibosdk_demo_toast_share_success,Toast.LENGTH_LONG).show();
						}

						@Override
						public void onCancel() {
							Toast.makeText(context.getApplicationContext(),R.string.weibosdk_demo_toast_share_canceled,Toast.LENGTH_SHORT).show();
						}
					});
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()}
	 * < 10351 时，只支持分享单条消息，即 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
	 */
	private void sendSingleMessage(String text) {

		// 1. 初始化微博的分享消息
		// 用户可以分享文本、图片、网页、音乐、视频中的一种
		WeiboMessage weiboMessage = new WeiboMessage();
		if (!TextUtils.isEmpty(text)) {
			weiboMessage.mediaObject = getTextObj(text);
		}
		// if (hasImage) {
		// weiboMessage.mediaObject = getImageObj();
		// }
		// if (hasWebpage) {
		// weiboMessage.mediaObject = getWebpageObj();
		// }
		// if (hasMusic) {
		// weiboMessage.mediaObject = getMusicObj();
		// }
		// if (hasVideo) {
		// weiboMessage.mediaObject = getVideoObj();
		// }

		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(context, request);
	}

	/**
	 * 创建文本消息对象。
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj(String text) {
		TextObject textObject = new TextObject();
		textObject.text = text;
		return textObject;
	}

	/**
	 * 创建图片消息对象。
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj() {
		ImageObject imageObject = new ImageObject();
		// BitmapDrawable bitmapDrawable = (BitmapDrawable)
		// mImageView.getDrawable();
		// // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
		// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_logo);
		// imageObject.setImageObject(bitmap);
		return imageObject;
	}

	/**
	 * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
	 *            微博请求数据对象
	 * @see {@link IWeiboShareAPI#handleWeiboRequest}
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(context.getApplicationContext(),
					R.string.weibosdk_demo_toast_share_success,
					Toast.LENGTH_LONG).show();

			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(context.getApplicationContext(),
					R.string.weibosdk_demo_toast_share_canceled,
					Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(
					context.getApplicationContext(),
					context.getString(R.string.weibosdk_demo_toast_share_failed)
							+ "Error Message: " + baseResp.errMsg,
					Toast.LENGTH_LONG).show();
			break;
		}
	}

}
