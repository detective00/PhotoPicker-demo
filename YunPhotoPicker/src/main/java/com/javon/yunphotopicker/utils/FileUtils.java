package com.javon.yunphotopicker.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.utils
 * Created by javonLiu on 2017/4/27.
 * e-mail : liujunjie00@yahoo.com
 */

public class FileUtils {
    private static final String HOST = "/com.aiten.yunphoto";
    public static String getApplicationCache(Context context) {
        String cache = "";
        File external = context.getExternalCacheDir();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cache = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + HOST;
        } else if (external != null) {
            cache = external.getAbsolutePath() + HOST;
        } else {
            cache = context.getCacheDir().getAbsolutePath() + HOST;
        }
        File cacheFile = new File(cache);
        if (!cacheFile.exists())
            cacheFile.mkdir();
        Log.e("cache", "cache : " + cacheFile.getAbsolutePath());
        return cacheFile.getAbsolutePath();
    }


}
