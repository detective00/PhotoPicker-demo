package com.javon.yunphotopicker;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.javon.yunphotopicker.event.OnItemCheckListener;
import com.javon.yunphotopicker.fragment.ImagePreviewFragment;
import com.javon.yunphotopicker.fragment.PhotoPickerFragment;
import com.javon.yunphotopicker.model.Photo;
import com.javon.yunphotopicker.receiver.PhototPickerReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static com.javon.yunphotopicker.PhotoPicker.DEFAULT_COLUMN_NUMBER;
import static com.javon.yunphotopicker.PhotoPicker.DEFAULT_MAX_COUNT;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_GRID_COLUMN;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_MAX_COUNT;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_PREVIEW_ENABLED;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_SELECTED_PHOTOS;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_SHOW_CAMERA;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_SHOW_GIF;


/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker
 * Created by javonLiu on 2017/4/21.
 * e-mail : liujunjie00@yahoo.com
 */

public class PhotoPickerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private PhotoPickerFragment pickerFragment;
    private ImagePreviewFragment imagePagerFragment;
    private MenuItem menuDoneItem;

    private int maxCount = DEFAULT_MAX_COUNT;

    /**
     * to prevent multiple calls to inflate menu
     */
    private boolean menuIsInflated = false;

    private boolean showGif, showCamera, previewEnabled;
    private int columnNumber = DEFAULT_COLUMN_NUMBER;
    private ArrayList<String> selectPhotos = null;

    private PhototPickerReceiver receiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        initActionBar();
        initData();
        addListener();
    }

    private void initActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionbar.setElevation(25);
        }
    }

    private void initData() {

        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
        columnNumber = getIntent().getIntExtra(EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER);
        selectPhotos = getIntent().getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
        showGif = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
        showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        previewEnabled = getIntent().getBooleanExtra(EXTRA_PREVIEW_ENABLED, true);

        pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentByTag("tag");
        if (pickerFragment == null) {
            pickerFragment = PhotoPickerFragment
                    .newInstance(showCamera, showGif, previewEnabled, columnNumber, selectPhotos);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, pickerFragment, "tag")
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        }

        receiver = new PhototPickerReceiver(pickerFragment);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PhotoPicker.SLIDING_INDEX_ACTION);
        registerReceiver(receiver, filter);
    }

    private void addListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    sharedElements.put("图片预览", pickerFragment.getSharedElement());
                }
            });
        }

        pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean onItemCheck(int position, Photo photo, final int selectedItemCount) {

                menuDoneItem.setEnabled(selectedItemCount > 0);

                if (maxCount <= 1) {
                    List<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
                    if (!photos.contains(photo.getPath())) {
                        photos.clear();
                        pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
                    }
                    return true;
                }

                if (selectedItemCount > maxCount) {
                    Toast.makeText(getActivity(), getString(R.string.picker_over_max_count_tips, maxCount),
                            LENGTH_LONG).show();
                    return false;
                }
                menuDoneItem.setTitle(getString(R.string.picker_done_with_count, selectedItemCount, maxCount));
                return true;
            }
        });
    }


    public void addImagePagerFragment(ImagePreviewFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, this.imagePagerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menuIsInflated) {
            getMenuInflater().inflate(R.menu.menu_photo_picker, menu);
            menuDoneItem = menu.findItem(R.id.done);
            if (selectPhotos != null && selectPhotos.size() > 0) {
                menuDoneItem.setEnabled(true);
                menuDoneItem.setTitle(
                        getString(R.string.picker_done_with_count, selectPhotos.size(), maxCount));
            } else {
                menuDoneItem.setEnabled(false);
            }
            menuIsInflated = true;
            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.done) {
            Intent intent = new Intent();
            ArrayList<String> selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
            intent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, selectedPhotos);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public PhotoPickerActivity getActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
