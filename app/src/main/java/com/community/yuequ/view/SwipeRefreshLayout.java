package com.community.yuequ.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout {
    public SwipeRefreshLayout(Context context) {
        super(context);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //Fix for support lib bug, happening when onDestroy is triggered.
            return true;
        }
    }

    @Override
    public boolean canChildScrollUp() {
        return super.canChildScrollUp();



    }
}
