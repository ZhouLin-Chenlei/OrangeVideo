package com.community.yuequ.pay;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.community.yuequ.modle.OrderTip;

import java.util.List;

/**
 * 短信工具类
 * @author Administrator
 *
 */
public class SoTools {
	public static final String SMS_RECIPIENT_EXTRA = "com.community.yuequ.SMS_RECIPIENT";
	public static final String ACTION_SMS_SENT = "com.community.yuequ.SMS_SENT_ACTION";
	private static final String TAG="SoTools";

	/**
	 *
	 * @param context
	 * @param orderTip
     * @return
     */
	public static boolean SendSMS(Context context,String address, String content, OrderTip orderTip) {
		
		boolean sendStatus = false;
		if(!hasSIMCard(context)){
			Toast.makeText(context, "未检测到手机卡，请插入手机卡后重试！", Toast.LENGTH_LONG).show();
			return sendStatus;
		}
		try {
			
			SmsManager sms = SmsManager.getDefault();
            List<String> messages = sms.divideMessage(content);
           
            for (String message : messages) {
            	Intent sendIntent = new Intent(ACTION_SMS_SENT);
				sendIntent.putExtra("OrderTip",orderTip);

            	Intent delIntent = new Intent(SMS_RECIPIENT_EXTRA);
				delIntent.putExtra("OrderTip",orderTip);
                sms.sendTextMessage(address,
                					null, 
                					message, 
                					PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), sendIntent, PendingIntent.FLAG_UPDATE_CURRENT), 
                        			PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), delIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                        			);
            }

			sendStatus=true;//能跑到这才算是发出了，成不成功要一会监听才知道

//			Intent intent2 = new Intent(context,b.class);
//			intent2.putExtra("CheckSms", mCheckSms);
//			intent2.putExtra("WHAT", b.SMS_SEND_TIMEOUT);
//			context.startService(intent2);
		} catch (Exception e) {
			e.printStackTrace();
			sendStatus = false;
		}
		return sendStatus;
	}



	//检查SIM卡
	public static boolean hasSIMCard(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if(tm.getSimState() == TelephonyManager.SIM_STATE_READY)
			return true;
		else
			return false;
	}
	
}
