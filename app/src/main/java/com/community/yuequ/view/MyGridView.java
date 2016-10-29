package com.community.yuequ.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 不能在一个拥有Scrollbar的组件中嵌入另一个拥有Scrollbar的组件，因为这不科学，会混淆滑动事件，导致只显示一到两行数据。那么就换一种思路，首先让子控件的内容全部显示出来，禁用了它的滚动。如果超过了父控件的范围则显示父控件的scrollbar滚动显示内容，思路是这样，一下是代码。
 * 具体的方法是自定义GridView组件，继承自GridView。重载onMeasure方法
 * @author fwz
 */
public class MyGridView extends GridView  
{  
    public MyGridView(Context context,
                      AttributeSet attrs)
    {  
        super(context, attrs);  
    }  
  
    
    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}


	public MyGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	/** 
     * 设置不滚动 
     */  
	@Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
    {  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
  
    }  
  
}
