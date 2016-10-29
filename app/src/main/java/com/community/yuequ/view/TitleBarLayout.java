package com.community.yuequ.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.community.yuequ.R;

/**
 * modou
 */
public class TitleBarLayout {

    private ImageButton mBackButton;
    private TextView mTitleTextView;
    private View mTitle;

    public TitleBarLayout(Activity context, int res) {
        mTitle = View.inflate(context, res, null);
        mTitleTextView = (TextView) mTitle.findViewById(R.id.tv_title_s);
        mBackButton = (ImageButton) mTitle.findViewById(R.id.btn_back);
    }

    public TitleBarLayout(Activity context) {
        mTitle = context.findViewById(R.id.rl_titlebar);
        mTitleTextView = (TextView) mTitle.findViewById(R.id.tv_title_s);
        mBackButton = (ImageButton) mTitle.findViewById(R.id.btn_back);
    }

    public TitleBarLayout(View context) {
        mTitle = context.findViewById(R.id.rl_titlebar);
        mTitleTextView = (TextView) mTitle.findViewById(R.id.tv_title_s);
        mBackButton = (ImageButton) mTitle.findViewById(R.id.btn_back);
    }


    public TitleBarLayout setText(String text) {
        if (TextUtils.isEmpty(text)) {
            mTitleTextView.setVisibility(View.GONE);
        } else {
            mTitleTextView.setVisibility(View.VISIBLE);
            mTitleTextView.setText(text);
        }
        return this;
    }


    public TitleBarLayout show() {
        mTitle.setVisibility(View.VISIBLE);
        return this;
    }

    public View build() {
        return mTitle;
    }

    public TitleBarLayout hide() {

        mTitle.setVisibility(View.GONE);
        return this;
    }

    public TitleBarLayout setLeftButtonVisibility(boolean visibile) {

        mBackButton.setVisibility(visibile ? View.VISIBLE : View.GONE);

        return this;
    }
    public TitleBarLayout setLeftButtonClickListener(View.OnClickListener clickListener) {

        mBackButton.setOnClickListener(clickListener);
        return this;
    }

}
