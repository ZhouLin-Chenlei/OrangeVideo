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

public class InputPhoneNumberDialog extends DialogFragment {


    public static InputPhoneNumberDialog newInstance() {
        InputPhoneNumberDialog frag = new InputPhoneNumberDialog();
        return frag;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
        final EditText phonenumber_edit = ((EditText) textEntryView.findViewById(R.id.phonenumber_edit));
        return new AlertDialog.Builder(getActivity())
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.alert_dialog_text_phonenumber)
                .setView(textEntryView)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FragmentActivity activity = getActivity();
                        if(activity instanceof PhoneNumberCallBack){
                            ((PhoneNumberCallBack)activity).phoneNumber(phonenumber_edit.getText().toString());
                        }

                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
    }

    
    public static interface PhoneNumberCallBack{
    	public void phoneNumber(String phoneNumber);
    }
}
