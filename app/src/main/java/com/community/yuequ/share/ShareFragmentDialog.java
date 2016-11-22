package com.community.yuequ.share;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.community.yuequ.R;

/**
 * Created by Administrator on 2016/11/17.
 */

public class ShareFragmentDialog extends DialogFragment implements View.OnClickListener{
    public static final int SHARE_SINA = 1;
    public static final int SHARE_WEIXIN = 2;
    public static final int SHARE_WEIXIN_RELATIONS= 3;

    public static ShareFragmentDialog newInstance() {
        ShareFragmentDialog dialog = new ShareFragmentDialog();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = View.inflate(getContext(),R.layout.third_share_layout,null);
        ViewGroup share_sina = (ViewGroup) view.findViewById(R.id.ll_share_sina);
        ViewGroup ll_share_weixin = (ViewGroup) view.findViewById(R.id.ll_share_weixin);
        ViewGroup ll_share_weixin_relations = (ViewGroup) view.findViewById(R.id.ll_share_weixin_relations);
        share_sina.setOnClickListener(this);
        ll_share_weixin.setOnClickListener(this);
        ll_share_weixin_relations.setOnClickListener(this);


        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }

    @Override
    public void onClick(View view) {
        dismiss();
        FragmentActivity activity = getActivity();
        switch (view.getId()){
            case R.id.ll_share_sina:
                if(activity instanceof ShareListener){
                    ((ShareListener)activity).share(SHARE_SINA);
                }

                break;
            case R.id.ll_share_weixin:

                if(activity instanceof ShareListener){
                    ((ShareListener)activity).share(SHARE_WEIXIN);
                }
                break;
            case R.id.ll_share_weixin_relations:

                if(activity instanceof ShareListener){
                    ((ShareListener)activity).share(SHARE_WEIXIN_RELATIONS);
                }
                break;

        }
    }

    public interface ShareListener{
         void share(int code);
    }
}
