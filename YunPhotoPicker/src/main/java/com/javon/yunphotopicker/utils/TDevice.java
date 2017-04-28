package com.javon.yunphotopicker.utils;

import android.content.Context;
import android.view.WindowManager;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.utils
 * Created by javonLiu on 2017/4/26.
 * e-mail : liujunjie00@yahoo.com
 */

public class TDevice {
    /**
     * 将px类型的尺寸转换成dp类型的尺寸
     *
     * @param pxValue
     * @param context
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * @方法名称: sp2px
     * @描述: 将sp值转换为px值
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度(像素)
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高度(像素)
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getHeight();
    }
}
