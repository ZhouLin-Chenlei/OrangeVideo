/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.community.yuequ.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.community.yuequ.R;

/**
 *
 */
public class WillRatioLinearLayout extends LinearLayout {
    private int widthPercent = 1;
    private int heightPercent = 1;

    public WillRatioLinearLayout(Context context) {
        super(context);

    }

    public WillRatioLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WillRatioLinearLayout);
        widthPercent = a.getInt(R.styleable.WillRatioLinearLayout_widthPercent,1);
        heightPercent = a.getInt(R.styleable.WillRatioLinearLayout_heightPercent,1);

        a.recycle();
    }

    public WillRatioLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);



    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int fourThreeHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthSpec) * heightPercent / widthPercent,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, fourThreeHeight);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
