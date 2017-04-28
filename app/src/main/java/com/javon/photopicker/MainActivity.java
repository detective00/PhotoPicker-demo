package com.javon.photopicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.javon.yunphotopicker.PhotoPicker;
import com.javon.yunphotopicker.PhotoPreview;
import com.javon.yunphotopicker.model.Photo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.javon.yunphotopicker.PhotoPicker.REQUEST_CODE;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> selectPhotos = new ArrayList<>();
    private String[] remotes = {"http://yfun.infunfs.com/yffs/upload/product/20160624163130121.jpg",
            "http://yfun.infunfs.com/yffs/upload/product/20170423233932000257293.jpg",
            "http://yfun.infunfs.com/yffs/upload/product/20170423233617549102411.jpg",
            "http://yfun.infunfs.com/yffs/upload/product/20170423233342119541622.jpg",
            "http://yfun.infunfs.com/yffs/upload/product/20170423214431356615382.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_normal).setOnClickListener(onNormalListener); //调用正常相册
        findViewById(R.id.btn_special).setOnClickListener(onSpecialListener); //相册中显示gif格式动画
        findViewById(R.id.btn_single).setOnClickListener(onSingleListener); // 有且只能选中一张图片
        findViewById(R.id.btn_no_capture).setOnClickListener(onNoCaptureListener); //相册中无调用拍照功能
        findViewById(R.id.btn_no_preview).setOnClickListener(onNoPreviewListener); //相册中的图片不可预览
        findViewById(R.id.btn_save_remote).setOnClickListener(onSaveRemoteListener); //保存网络图片
    }

    /**
     * 调用正常相册
     */
    private View.OnClickListener onNormalListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PhotoPicker.builder().setGridColumnCount(4)
                    .setShowCamera(true)
                    .setPhotoCount(15)
                    .setSelected(selectPhotos)
                    .setPreviewEnabled(true)
                    .start(MainActivity.this);
        }
    };

    /**
     * 相册中显示gif格式动画
     */
    private View.OnClickListener onSpecialListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PhotoPicker.builder().setGridColumnCount(5)
                    .setShowCamera(true)
                    .setPhotoCount(15)
                    .setSelected(selectPhotos)
                    .setShowGif(true)
                    .setPreviewEnabled(true)
                    .start(MainActivity.this);
        }
    };

    /**
     * 有且只能选中一张图片
     */
    private View.OnClickListener onSingleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PhotoPicker.builder().setGridColumnCount(3)
                    .setPhotoCount(1)
                    .setSelected(selectPhotos)
                    .setShowGif(true)
                    .start(MainActivity.this);
        }
    };

    /**
     * 相册中无调用拍照功能
     */
    private View.OnClickListener onNoCaptureListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PhotoPicker.builder().setGridColumnCount(4)
                    .setShowCamera(false)
                    .setPhotoCount(1)
                    .setSelected(selectPhotos)
                    .setShowGif(true)
                    .setPreviewEnabled(true)
                    .start(MainActivity.this);
        }
    };

    /**
     * 相册中的图片不可预览
     */
    private View.OnClickListener onNoPreviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PhotoPicker.builder().setGridColumnCount(4)
                    .setPhotoCount(1)
                    .setSelected(selectPhotos)
                    .setShowGif(true)
                    .setPreviewEnabled(false)
                    .start(MainActivity.this);
        }
    };

    /**
     * 相册中的图片不可预览
     */
    private View.OnClickListener onSaveRemoteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<String> result = new ArrayList<>();
            result.addAll(Arrays.asList(remotes));
            PhotoPreview.builder().setPhotos(result)
                    .setRemoteImage(true).start(MainActivity.this, view);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.EXTRA_SELECTED_PHOTOS);
            }
            selectPhotos.clear();
            if (photos != null) {
                selectPhotos.addAll(photos);
            }
            StringBuffer sb = new StringBuffer("所选图片路径：");
            for (String itemStr : selectPhotos){
                sb.append(itemStr).append(",");
            }
            sb.substring(0, sb.length() - 1);
            Toast.makeText(this, "已选图片：" + selectPhotos.size() + " 张, " + sb.toString(), Toast.LENGTH_LONG).show();
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
