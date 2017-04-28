package com.javon.yunphotopicker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.widget
 * Created by javonLiu on 2017/4/21.
 * e-mail : liujunjie00@yahoo.com
 */

public class SquareItemLayout extends RelativeLayout {
    public SquareItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareItemLayout(Context context) {
        super(context);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        heightMeasureSpec =
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY); // 高度随内容匹配，匹配最大高度，而无须嵌套scrollview
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}