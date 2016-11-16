package com.community.yuequ.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;

public class DialogManager {

	public static ProgressDialog getProgressDialog(Context context,
			String message) {
		return getProgressDialog(context, message, null);
	}

	public static ProgressDialog getProgressDialog(Context context,
			String message, OnCancelListener listener) {

		ProgressDialog dialog = new ProgressDialog(context);

		dialog.setMessage(message);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(listener);
		
		return dialog;
	}

}
