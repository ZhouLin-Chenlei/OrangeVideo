package com.community.yuequ.gui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.community.yuequ.R;
import com.community.yuequ.gui.PayListActivity;
import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.pay.SPUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/27.
 */
public class PayListAdapter extends RecyclerView.Adapter<PayListAdapter.ViewHolder>{
    private PayListActivity activity;
    ArrayList<OrderTip> mOrderTips;
    SPUtils smsPayUtils;
    public PayListAdapter(PayListActivity activity, ArrayList<OrderTip> orderTips, SPUtils smsPayUtils) {
        this.activity = activity;
        this.mOrderTips = orderTips;
        this.smsPayUtils = smsPayUtils;
    }

    @Override
    public PayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = View.inflate(parent.getContext(), R.layout.paylist_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PayListAdapter.ViewHolder holder, int position) {
        final OrderTip orderTip = mOrderTips.get(position);
        if(OrderTip.TYPE_ONCE.equals(orderTip.type)){
            holder.pay_name.setText("按次订购"+orderTip.money+"元");

        }else if(OrderTip.TYPE_MONTHLY.equals(orderTip.type)){
            holder.pay_name.setText("包月订购"+orderTip.money+"元");
        }
        holder.btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsPayUtils.showOrderTips(activity.getProgramId(),orderTip);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mOrderTips==null)
            return 0;
        return mOrderTips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView pay_name;
        public Button btn_pay;
        public ViewHolder(View itemView) {
            super(itemView);
            pay_name = (TextView) itemView.findViewById(R.id.pay_name);
            btn_pay = (Button) itemView.findViewById(R.id.btn_pay);
        }
    }
}
