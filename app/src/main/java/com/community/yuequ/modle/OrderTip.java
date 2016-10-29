package com.community.yuequ.modle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/5/31.
 */
public class OrderTip implements Parcelable {
    public static final String TYPE_MONTHLY = "1";//1：包月订购
    public static final String TYPE_ONCE = "2";//2：按次订购

    public int id;
    public String type;//计费类型,1：包月订购，2：按次订购
    public String order_tips;//订购提示语
    public String confirm_order_tips;//确认订购提示语
    public String order_success_tips;//成功订购提示语
    public String order_error_tips;//订购失败提示语
    public String gust_tips;//游客提示语
    public double money;//计费金额
    public String order_tips_switch;//订购提示语开关（0：关，1：开）
    public String confirm_order_tips_switch;//确认提示语开关（0：关，1：开）
    public String order_directive;//计费指令
    public String order_port;//计费端口号
    public String compare_text_1;//对比文字1
    public String compare_text_2;//对比文字2
    public String up_port;//上行端口
    public String down_port;//下行端口
    public String take_msg_switch;//代回开关（0：关，1：开）
    public String is_del_msg_swtich;//是否删除短信（0：关，1：开）
    public String net_type;//（1：移动，2：联通，3：电信）




   /***************************************************************************************************/
    public int programId;//计费的节目id
    public long transactionID;//计费的时间戳

    /***************************************************************************************************/
    public OrderTip(){}

    public OrderTip(Parcel in){
        id = in.readInt();
        type = in.readString();
        order_tips = in.readString();
        confirm_order_tips = in.readString();
        order_success_tips = in.readString();
        order_error_tips = in.readString();
        gust_tips = in.readString();
        money = in.readDouble();
        order_tips_switch = in.readString();
        confirm_order_tips_switch = in.readString();
        order_directive = in.readString();
        order_port = in.readString();
        compare_text_1 = in.readString();
        compare_text_2 = in.readString();
        up_port = in.readString();
        down_port = in.readString();
        take_msg_switch = in.readString();
        is_del_msg_swtich = in.readString();
        net_type = in.readString();

    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(type);
        dest.writeString(order_tips);
        dest.writeString(confirm_order_tips);
        dest.writeString(order_success_tips);
        dest.writeString(order_error_tips);
        dest.writeString(gust_tips);
        dest.writeDouble(money);
        dest.writeString(order_tips_switch);
        dest.writeString(confirm_order_tips_switch);
        dest.writeString(order_directive);
        dest.writeString(order_port);
        dest.writeString(compare_text_1);
        dest.writeString(compare_text_2);
        dest.writeString(up_port);
        dest.writeString(down_port);
        dest.writeString(take_msg_switch);
        dest.writeString(is_del_msg_swtich);
        dest.writeString(net_type);
    }
    public static final Parcelable.Creator<OrderTip> CREATOR = new Creator<OrderTip>() {
        @Override
        public OrderTip[] newArray(int size) {
            return new OrderTip[size];
        }

        @Override
        public OrderTip createFromParcel(Parcel in) {
            return new OrderTip(in);
        }
    };
}
