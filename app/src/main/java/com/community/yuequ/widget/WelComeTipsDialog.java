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

/**
 * Created by Administrator on 2016/6/1.
 */
public class WelComeTipsDialog extends DialogFragment{

    public static WelComeTipsDialog newInstance(String tips) {
        WelComeTipsDialog dialog = new WelComeTipsDialog();
        Bundle bundle = new Bundle();
        bundle.putString("tips",tips);
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
        String tips = null;
        if(getArguments()!=null){
            tips = getArguments().getString("tips");
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.tips)
                .setMessage(tips)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FragmentActivity activity = getActivity();
                        if(activity instanceof DialogConfListener){
                            ((DialogConfListener)activity).conf();
                        }

                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        FragmentActivity activity = getActivity();
                        if(activity instanceof DialogConfListener){
                            ((DialogConfListener)activity).cancel();
                        }
                    }
                })
                .create();
    }
}
