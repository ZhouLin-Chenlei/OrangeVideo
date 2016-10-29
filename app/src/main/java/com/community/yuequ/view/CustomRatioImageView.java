package com.community.yuequ.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.community.yuequ.R;

/**
 * 远程会诊item 的layout 根据屏幕大小和图片尺寸比例自适应item大小
 * @author zhoulin
 *
 */
public class CustomRatioImageView extends ImageView {
	public static final int DEFAULT_ASPECT_RATIO_WIDTH = 1;
    public static final int DEFAULT_ASPECT_RATIO_HEIGHT = 1;

//    private int mAspectRatioWidth = DEFAULT_ASPECT_RATIO_WIDTH;
//    private int mAspectRatioHeight = DEFAULT_ASPECT_RATIO_HEIGHT;

    private float Ratio = 1.0f;
	public CustomRatioImageView(Context context) {
		super(context);
	}

	public CustomRatioImageView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		init(context,attrs);
	}

	public CustomRatioImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);

	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public CustomRatioImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context,attrs);
	}


	private void init(Context context, AttributeSet attrs){
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomRatio, 0, 0);
		try {
			int mAspectRatioWidth = ta.getInteger(R.styleable.CustomRatio_rrWidth, DEFAULT_ASPECT_RATIO_WIDTH);
			int mAspectRatioHeight = ta.getInteger(R.styleable.CustomRatio_rrHeight, DEFAULT_ASPECT_RATIO_HEIGHT);

			Ratio = (mAspectRatioHeight* 1.0f)/mAspectRatioWidth;
		}finally{
			ta.recycle();
		}

	}

	@SuppressWarnings("unused")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        // Children are just made to fill our space.

        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int)(childWidthSize * Ratio), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

	public double getRatio() {
		return Ratio;
	}

	public void setRatio(float ratio) {
		Ratio = ratio;
		invalidate();
	}
	
	
}
