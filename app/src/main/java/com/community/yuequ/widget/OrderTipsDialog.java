package com.community.yuequ.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.community.yuequ.R;
import com.community.yuequ.imple.DialogConfListener;
import com.community.yuequ.imple.OrderTipsOneListener;
import com.community.yuequ.modle.OrderTip;

/**
 * Created by Administrator on 2016/6/1.
 */
public class OrderTipsDialog extends DialogFragment {
    int programId;
    OrderTip orderTip;
    public static OrderTipsDialog newInstance(int programId,OrderTip orderTip) {
        OrderTipsDialog dialog = new OrderTipsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("programId",programId);
        bundle.putParcelable("OrderTip",orderTip);
        dialog.setArguments(bundle);
        return dialog;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if(bundle!=null){
            programId = bundle.getInt("programId");
            orderTip =  bundle.getParcelable("OrderTip");
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.tips)
                .setMessage(orderTip.order_tips)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FragmentActivity activity = getActivity();
                        if(activity instanceof OrderTipsOneListener){
                            ((OrderTipsOneListener)activity).one_confirm(programId,orderTip);
                        }

                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        FragmentActivity activity = getActivity();
                        if(activity instanceof OrderTipsOneListener){
                            ((OrderTipsOneListener)activity).one_cancel(programId,orderTip);
                        }
                    }
                })
                .create();
    }

}
