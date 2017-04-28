package com.javon.yunphotopicker.event;


import com.javon.yunphotopicker.model.Photo;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker
 * Created by javonLiu on 2017/4/21.
 * e-mail : liujunjie00@yahoo.com
 */
public interface OnItemCheckListener {
    /**
     * @param position          所选图片的位置
     * @param path              所选图片
     * @param selectedItemCount 已选数量
     * @return enable check
     */
    boolean onItemCheck(int position, Photo path, int selectedItemCount);
}
