package com.community.yuequ.pay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.widget.ConfirmOrderTipsDialog;
import com.community.yuequ.widget.OrderTipsDialog;

/**
 * Created by Administrator on 2016/6/1.
 */
public class SPUtils {
    public static final String TIPS_SWITCH_CLOSE = "0";
    public static final String TIPS_SWITCH_OPEN = "1";
    private AppCompatActivity mActivity;
//    private int programId;
//    private OrderTip orderTip;
    public SPUtils(AppCompatActivity activity){
        this.mActivity = activity;

    }

    /**
     * 购买的第一步
     * @param orderTip
     */
    public void showOrderTips(int programId, OrderTip orderTip) {

        //有提示语显示购买提示语
        if(TIPS_SWITCH_OPEN.equals(orderTip.order_tips_switch) && !TextUtils.isEmpty(orderTip.order_tips)){
            OrderTipsDialog tipsDialog = OrderTipsDialog.newInstance(programId,orderTip);
            tipsDialog.show(mActivity.getSupportFragmentManager(),"dialog");

        }else if(TIPS_SWITCH_OPEN.equals(orderTip.confirm_order_tips_switch) && !TextUtils.isEmpty(orderTip.confirm_order_tips)){//没有提示语,有确认提示语
            ConfirmOrderTipsDialog orderTipsDialog = ConfirmOrderTipsDialog.newInstance(programId,orderTip);
            orderTipsDialog.show(mActivity.getSupportFragmentManager(),"dialog");
        }else{//直接发送短信
            sendSms(programId,orderTip);
        }
    }

    /**
     * 购买的第二步
     * @param orderTip
     */
    public void showConfirmOrderTips(int programId, OrderTip orderTip) {

        if(TIPS_SWITCH_OPEN.equals(orderTip.confirm_order_tips_switch) && !TextUtils.isEmpty(orderTip.confirm_order_tips)){//没有提示语,有确认提示语
            ConfirmOrderTipsDialog orderTipsDialog = ConfirmOrderTipsDialog.newInstance(programId,orderTip);
            orderTipsDialog.show(mActivity.getSupportFragmentManager(),"dialog");
        }else{//直接发送短信
            sendSms(programId,orderTip);
        }
    }


    public void sendSms(int programId, OrderTip orderTip) {
//        this.programId = programId;
//        this.orderTip =orderTip;
        Intent intent = new Intent(mActivity,Slog.class);
        intent.putExtra("programId",programId);
        intent.putExtra("OrderTip",orderTip);
        intent.putExtra(Slog.ACTION,Slog.ACTION_SENDSMS);
        mActivity.startService(intent);
    }
}
