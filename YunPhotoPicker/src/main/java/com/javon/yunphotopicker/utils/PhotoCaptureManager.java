package com.javon.yunphotopicker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.utils
 * Created by javonLiu on 2017/4/25.
 * e-mail : liujunjie00@yahoo.com
 */

/***
 * 当fragment销毁时，保存当前获取到的图片路径
 */
public class PhotoCaptureManager {

    private final static String CAPTURED_PHOTO_PATH_KEY = "CURRENT_PHOTO_PATH";
    public static final int REQUEST_TAKE_PHOTO = 321;

    private String currentPhotoPath;
    private Context context;

    public PhotoCaptureManager(Context context) {
        this.context = context;
    }

    private File createImageFile() throws IOException {
        // 创建图片
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) {
            if (!storageDir.mkdir()) {
                Log.e("TAG", "Throwing Errors....");
                throw new IOException();
            }
        }

        File image = new File(storageDir, imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /***
     * 调用系统拍摄api
     * @return
     * @throws IOException
     */
    public Intent dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File file = createImageFile();
            Uri photoFile;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authority = context.getApplicationInfo().packageName + ".provider";
                photoFile = FileProvider.getUriForFile(this.context.getApplicationContext(), authority, file);
            } else {
                photoFile = Uri.fromFile(file);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
            }
        }
        return takePictureIntent;
    }

    /**
     * 保存图片，更新相册库
     */
    public void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        if (TextUtils.isEmpty(currentPhotoPath)) {
            return;
        }
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && currentPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, currentPhotoPath);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            currentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
    }


}
