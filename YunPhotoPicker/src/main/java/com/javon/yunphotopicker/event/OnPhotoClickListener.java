package com.javon.yunphotopicker.event;

import android.view.View;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker
 * Created by javonLiu on 2017/4/21.
 * e-mail : liujunjie00@yahoo.com
 */
public interface OnPhotoClickListener {
    /**
     * @param v          被点击控件
     * @param position   点击位置
     * @param showCamera 是否现实camera按钮
     */
    void onClick(View v, int position, boolean showCamera);

}
