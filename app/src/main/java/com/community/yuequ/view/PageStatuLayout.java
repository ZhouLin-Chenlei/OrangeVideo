package com.community.yuequ.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.community.yuequ.R;

/**
 * Created by Administrator on 2016/4/28.
 */
public class PageStatuLayout {

    private View stateView;
    private TextView statuTextView;
    private View loadingView;

    public PageStatuLayout(Activity context, int res) {
        stateView = View.inflate(context, res, null);
        statuTextView = (TextView) stateView.findViewById(R.id.tv_status);
        loadingView = stateView.findViewById(R.id.progressBar);
    }

    public PageStatuLayout(Activity context) {
        stateView = context.findViewById(R.id.ll_status);
        statuTextView = (TextView) stateView.findViewById(R.id.tv_status);
        loadingView = stateView.findViewById(R.id.progressBar);
    }

    public PageStatuLayout(View context) {
        stateView = context.findViewById(R.id.ll_status);
        statuTextView = (TextView) stateView.findViewById(R.id.tv_status);
        loadingView = stateView.findViewById(R.id.progressBar);
    }


    public PageStatuLayout setText(String text) {
        if (TextUtils.isEmpty(text)) {
            statuTextView.setVisibility(View.GONE);
        } else {
            statuTextView.setVisibility(View.VISIBLE);
            statuTextView.setText(text);
        }
        return this;
    }


    public PageStatuLayout show() {
        stateView.setVisibility(View.VISIBLE);
        return this;
    }
    public PageStatuLayout setReloadListener(View.OnClickListener listener) {
        stateView.setOnClickListener(listener);
        return this;
    }

    public View build() {
        return stateView;
    }

    public PageStatuLayout hide() {

        stateView.setVisibility(View.GONE);
        return this;
    }

    public PageStatuLayout setProgressBarVisibility(boolean visibile) {

        loadingView.setVisibility(visibile ? View.VISIBLE : View.GONE);

        return this;
    }


}
