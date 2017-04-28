package com.javon.yunphotopicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.javon.yunphotopicker.fragment.ImagePreviewFragment;
import com.javon.yunphotopicker.receiver.PhototPickerReceiver;
import com.javon.yunphotopicker.utils.FileUtils;
import com.javon.yunphotopicker.utils.PermissionsConstant;
import com.javon.yunphotopicker.utils.PermissionsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.javon.yunphotopicker.PhotoPreview.EXTRA_IS_REMOTE;
import static com.javon.yunphotopicker.PhotoPreview.EXTRA_PHOTOS;
import static com.javon.yunphotopicker.PhotoPreview.EXTRA_SHOW_DELETE;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker
 * Created by javonLiu on 2017/4/22.
 * e-mail : liujunjie00@yahoo.com
 */

public class PhotoPagerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar actionBar;
    private ImagePreviewFragment pagerFragment;
    private boolean showDelete, isRemote;
    private FloatingActionButton saveFab;
    private List<String> paths;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initData();
        initView();
        addListener();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            updateActionBarTitle();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(25);
            }
        }
        saveFab = (FloatingActionButton) findViewById(R.id.fab_save);
        saveFab.setVisibility(isRemote ? View.VISIBLE : View.GONE);
    }

    private void initData() {
        paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        showDelete = getIntent().getBooleanExtra(EXTRA_SHOW_DELETE, false);
        isRemote = getIntent().getBooleanExtra(EXTRA_IS_REMOTE, false);

        if (pagerFragment == null) {
            pagerFragment =
                    (ImagePreviewFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        }
        pagerFragment.setPhotos(paths);
    }

    private void addListener() {
        pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                PhotoPicker.CURRENT_ITEM = position;
                PhototPickerReceiver.sendBroadcast(PhotoPagerActivity.this);
                updateActionBarTitle();
            }
        });
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PermissionsUtils.checkWriteStoragePermission(PhotoPagerActivity.this))
                    saveFile();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Toast.makeText(PhotoPagerActivity.this, "图片保存出错啦...", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Bundle bundle = msg.getData();
                    String filePath = bundle.getString("img_file");
                    if(!TextUtils.isEmpty(filePath)){
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
                    }
                    Toast.makeText(PhotoPagerActivity.this, "图片保存成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(PhotoPagerActivity.this, "远程图片不存在", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(PhotoPagerActivity.this, "图片已存在", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void saveFile(){
        String path = paths.get(PhotoPicker.CURRENT_ITEM);
        if (TextUtils.isEmpty(path)) {
            handler.sendEmptyMessage(2);
        } else {
            int start = path.lastIndexOf("/");
            String imageName = path.substring(start + 1, path.length());
            final File file = new File(FileUtils.getApplicationCache(this), imageName);
            if(file.exists()){
                handler.sendEmptyMessage(3);
            }else{
                Glide.with(this).load(path).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            assert resource != null;
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                            // 最后通知图库更新
                            Message msg = Message.obtain();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("img_file", file.getAbsolutePath());
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(0);
                        }
                    }
                });
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PermissionsConstant.REQUEST_EXTERNAL_WRITE:
                    if (PermissionsUtils.checkWriteStoragePermission(this)) {
                        saveFile();
                    }
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showDelete) {
            getMenuInflater().inflate(R.menu.menu_photo_preview, menu);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.delete) {
            final int index = pagerFragment.getCurrentItem();

            final String deletedPath = pagerFragment.getPaths().get(index);

            Snackbar snackbar = Snackbar.make(pagerFragment.getView(), R.string.picker_deleted_a_photo,
                    Snackbar.LENGTH_LONG);

            if (pagerFragment.getPaths().size() <= 1) {

                // show confirm dialog
                new AlertDialog.Builder(this)
                        .setTitle(R.string.picker_confirm_to_delete)
                        .setPositiveButton(R.string.picker_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                pagerFragment.getPaths().remove(index);
                                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(R.string.picker_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            } else {

                snackbar.show();

                pagerFragment.getPaths().remove(index);
                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
            }

            snackbar.setAction(R.string.picker_undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pagerFragment.getPaths().size() > 0) {
                        pagerFragment.getPaths().add(index, deletedPath);
                    } else {
                        pagerFragment.getPaths().add(deletedPath);
                    }
                    pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                    pagerFragment.getViewPager().setCurrentItem(index, true);
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoPicker.CURRENT_ITEM = 0;
    }

    public void updateActionBarTitle() {
        if (actionBar != null) actionBar.setTitle(
                getString(R.string.picker_image_index, pagerFragment.getViewPager().getCurrentItem() + 1,
                        pagerFragment.getPaths().size()));
    }
}
