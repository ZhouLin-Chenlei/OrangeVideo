package com.community.yuequ.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.community.yuequ.R;
import com.community.yuequ.imple.OrderTipsTowListener;
import com.community.yuequ.modle.OrderTip;

/**
 * Created by Administrator on 2016/6/1.
 */
public class GenderChoiceDialog extends DialogFragment {
    private String[] genderArr = {"男","女"};
    public static GenderChoiceDialog newInstance() {
        GenderChoiceDialog dialog = new GenderChoiceDialog();
        Bundle bundle = new Bundle();
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

        return new AlertDialog.Builder(getActivity())
                .setItems(genderArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FragmentActivity activity = getActivity();
                        if(activity instanceof ChoiceGenderListener){
                            ((ChoiceGenderListener) activity).getGender(genderArr[i]);
                        }
                    }
                }).create();
    }


    public interface ChoiceGenderListener{
        public void getGender(String gender);
    }
}
