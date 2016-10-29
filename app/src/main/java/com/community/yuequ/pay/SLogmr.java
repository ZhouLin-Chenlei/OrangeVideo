package com.community.yuequ.pay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.community.yuequ.Session;
import com.community.yuequ.util.Log;

public class SLogmr extends BroadcastReceiver {
    /** Tag string for our debug logs */
    private static final String TAG = "SLogmr";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        Object[] pdus = (Object[]) extras.get("pdus");
        String fromAddress = null;
        StringBuilder body = new StringBuilder();// 短信内容
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
            fromAddress = message.getOriginatingAddress();
            String messageBody = message.getMessageBody();
            body.append(messageBody);
            break;
        }

        if(Slog.checkHaveUpPort(fromAddress) && !TextUtils.isEmpty(body.toString())){//如果接收到的短信是上行端口发来的则处理
            Intent sintent = new Intent(context,Slog.class);
            sintent.putExtra("fromAddress",fromAddress);
            sintent.putExtra("body",body.toString());
            sintent.putExtra(Slog.ACTION,Slog.ACTION_TAKE_BACK);
            context.startService(sintent);

        }
    }
}
