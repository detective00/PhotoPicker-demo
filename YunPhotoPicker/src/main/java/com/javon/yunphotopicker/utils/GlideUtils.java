package com.javon.yunphotopicker.utils;

import android.graphics.Bitmap;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.javon.yunphotopicker.R;

import java.io.File;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.utils
 * Created by javonLiu on 2017/4/25.
 * e-mail : liujunjie00@yahoo.com
 */

public class GlideUtils {

    public static void loadImage(RequestManager manager, ImageView iv, String path, int width, int height, float multiplier) {
        manager.load(path)
                .centerCrop()
                .dontAnimate()
                .dontTransform()
                .thumbnail(multiplier)
                .override(width, height)
                .placeholder(R.mipmap.ic_photo_default_bg)
                .error(R.mipmap.ic_photo_load_error_bg)
                .into(iv);
    }

}
