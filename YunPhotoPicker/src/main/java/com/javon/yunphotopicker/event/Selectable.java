package com.javon.yunphotopicker.event;


import com.javon.yunphotopicker.model.Photo;

import java.util.List;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker
 * Created by javonLiu on 2017/4/21.
 * e-mail : liujunjie00@yahoo.com
 */
public interface Selectable {

    /**
     * 判断照片是否被选中
     *
     * @param photo Photo of the item to check
     * @return true if the item is selected, false otherwise
     */
    boolean isSelected(Photo photo);

    /**
     * 选中或是取消指定的照片
     *
     * @param photo Photo of the item to toggle the selection status for
     */
    void toggleSelection(Photo photo);

    /**
     * 清除所有选中状态
     */
    void clearSelection();

    /**
     * 获取选中照片总数
     *
     * @return Selected items count
     */
    int getSelectedItemCount();

    /**
     * 获取所有选中照片
     *
     * @return List of selected photos
     */
    List<String> getSelectedPhotos();


}
