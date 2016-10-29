package com.community.yuequ.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.modle.InitMsg;
import com.community.yuequ.modle.UpgradeInfo;

public class UpgradeDialog extends DialogFragment {
    boolean is_force = false;
    UpgradeInfo upgrade;
    public static UpgradeDialog newInstance() {
        UpgradeDialog frag = new UpgradeDialog();
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Session session = Session.get(getContext());
        InitMsg initMsg = session.getInitMsg();

        if(initMsg!=null){
            upgrade = initMsg.upgrade;
            is_force = "1".equals(upgrade.is_force_up);

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.alert_dialog_upgradetip)
                .setPositiveButton(R.string.alert_dialog_upgrade, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (getActivity() instanceof UpgradeListener) {
                            ((UpgradeListener)getActivity()).upgrade(upgrade);
                        }

                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (getActivity() instanceof UpgradeListener) {
                            ((UpgradeListener)getActivity()).cancel(is_force);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
//        alertDialog .setCancelable(false);
        return alertDialog;
    }

    
    public interface UpgradeListener{
        void upgrade(UpgradeInfo upgrade);
        void cancel(boolean is_force);
    }
}
