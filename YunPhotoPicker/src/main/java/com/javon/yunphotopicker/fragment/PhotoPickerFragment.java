package com.javon.yunphotopicker.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.javon.yunphotopicker.PhotoPicker;
import com.javon.yunphotopicker.PhotoPreview;
import com.javon.yunphotopicker.R;
import com.javon.yunphotopicker.adapter.PhotoAdapter;
import com.javon.yunphotopicker.adapter.PopupDirectoryListAdapter;
import com.javon.yunphotopicker.event.OnPhotoClickListener;
import com.javon.yunphotopicker.model.Photo;
import com.javon.yunphotopicker.model.PhotoDirectory;
import com.javon.yunphotopicker.utils.MediaStoreHelper;
import com.javon.yunphotopicker.utils.PermissionsConstant;
import com.javon.yunphotopicker.utils.PermissionsUtils;
import com.javon.yunphotopicker.utils.PhotoCaptureManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_GRID_COLUMN;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_PREVIEW_ENABLED;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_SELECTED_PHOTOS;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_SHOW_CAMERA;
import static com.javon.yunphotopicker.PhotoPicker.EXTRA_SHOW_GIF;
import static com.javon.yunphotopicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.fragment
 * Created by javonLiu on 2017/4/25.
 * e-mail : liujunjie00@yahoo.com
 */

public class PhotoPickerFragment extends Fragment {
    private boolean showCamere, showGif, preview;
    private int column;
    private ArrayList<String> selectPhotos;
    public static int COUNT_MAX = 4;
    private RequestManager glide;
    //所有photos的目录
    private List<PhotoDirectory> directories;
    private PhotoCaptureManager captureManager;
    private PhotoAdapter adapter;
    private ListPopupWindow listPopupWindow;
    private PopupDirectoryListAdapter directoryAdapter;
    private RecyclerView recyclerView;
    private TextView tvAll;
    private int SCROLL_THRESHOLD = 30;
    private StaggeredGridLayoutManager layoutManager;


    public static PhotoPickerFragment newInstance(boolean showCamere, boolean showGif, boolean preview, int column, ArrayList<String> selecPhotos) {
        PhotoPickerFragment fragment = new PhotoPickerFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_SHOW_CAMERA, showCamere);
        bundle.putBoolean(EXTRA_SHOW_GIF, showGif);
        bundle.putBoolean(EXTRA_PREVIEW_ENABLED, preview);
        bundle.putInt(EXTRA_GRID_COLUMN, column);
        bundle.putStringArrayList(EXTRA_SELECTED_PHOTOS, selecPhotos);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true); // 该方法在activity销毁时，要保存数据时调用一系列api
        Bundle bundle = getArguments();
        showCamere = bundle.getBoolean(EXTRA_SHOW_CAMERA);
        showGif = bundle.getBoolean(EXTRA_SHOW_GIF);
        preview = bundle.getBoolean(EXTRA_PREVIEW_ENABLED);
        column = bundle.getInt(EXTRA_GRID_COLUMN);
        selectPhotos = bundle.getStringArrayList(EXTRA_SELECTED_PHOTOS);

        directories = new ArrayList<>();
        glide = Glide.with(getActivity());
        directoryAdapter = new PopupDirectoryListAdapter(glide, directories);

        adapter = new PhotoAdapter(getActivity(), glide, directories, selectPhotos, column);
        adapter.setShowCamera(showCamere);
        adapter.setPreviewEnable(preview);

        //读取系统相册数据
        Bundle mediaStoreArgs = new Bundle();
        mediaStoreArgs.putBoolean(EXTRA_SHOW_GIF, showGif);
        MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs,
                new MediaStoreHelper.PhotosResultCallback() {
                    @Override
                    public void onResultCallback(List<PhotoDirectory> dirs) {
                        directories.clear();
                        directories.addAll(dirs);
                        adapter.notifyDataSetChanged();
                        directoryAdapter.notifyDataSetChanged();
                        adjustHeight();
                    }
                });
        captureManager = new PhotoCaptureManager(getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_photo_picker, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
        layoutManager = new StaggeredGridLayoutManager(column, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        tvAll = (TextView) rootView.findViewById(R.id.tv_all);
        listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        listPopupWindow.setAnchorView(tvAll);
        listPopupWindow.setAdapter(directoryAdapter);
        listPopupWindow.setModal(true);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        addListener();
        return rootView;
    }

    public View getSharedElement(){
//        Log.e("picker", "index00 : " + PhotoPicker.CURRENT_ITEM);
        return layoutManager.findViewByPosition(showCamere ? PhotoPicker.CURRENT_ITEM + 1 : PhotoPicker.CURRENT_ITEM);
    }

    public void slidingCurrentIndex(){
        int realIndex = showCamere ? PhotoPicker.CURRENT_ITEM + 1 : PhotoPicker.CURRENT_ITEM;
        if(getSharedElement() == null || !getSharedElement().isShown())
            recyclerView.smoothScrollToPosition(realIndex);
    }

    private void addListener() {

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();

                PhotoDirectory directory = directories.get(position);

                tvAll.setText(directory.getName());

                adapter.setCurrentDirectoryIndex(position);
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                final int index = showCamera ? position - 1 : position;
                ArrayList<String> photos = adapter.getCurrentPhotoPaths();
                PhotoPicker.CURRENT_ITEM = index;
                PhotoPreview.builder().setPhotos(photos)
                        .start(getActivity(), v);

             /*   ImagePreviewFragment imagePagerFragment =
                        ImagePreviewFragment.newInstance(photos, index);
                ((PhotoPickerActivity) getActivity()).addImagePagerFragment(imagePagerFragment);*/
            }
        });

        adapter.setOnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionsUtils.checkCameraPermission(PhotoPickerFragment.this)) return;
                if (!PermissionsUtils.checkWriteStoragePermission(PhotoPickerFragment.this)) return;
                openCamera();
            }
        });

        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPopupWindow.isShowing()) {
                    listPopupWindow.dismiss();
                } else if (!getActivity().isFinishing()) {
                    adjustHeight();
                    listPopupWindow.show();
                }
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.d(">>> Picker >>>", "dy = " + dy);
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    glide.pauseRequests();
                } else {
                    resumeRequestsIfNotDestroyed();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeRequestsIfNotDestroyed();
                }
            }
        });
    }

    private void openCamera() {
        try {
            Intent intent = captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, PhotoCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            // TODO No Activity Found to handle Intent
            e.printStackTrace();
        }
    }


    public PhotoAdapter getPhotoGridAdapter() {
        return adapter;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        captureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        captureManager.onRestoreInstanceState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    private void adjustHeight() {
        if (directoryAdapter == null) return;
        int count = directoryAdapter.getCount();
        count = count < COUNT_MAX ? count : COUNT_MAX;
        if (listPopupWindow != null) {
            listPopupWindow.setHeight(count * getResources().getDimensionPixelOffset(R.dimen.picker_item_directory_height));
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (captureManager == null) {
                FragmentActivity activity = getActivity();
                captureManager = new PhotoCaptureManager(activity);
            }
            captureManager.galleryAddPic();
            if (directories.size() > 0) {
                String path = captureManager.getCurrentPhotoPath();
                PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
                directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
                directory.setCoverPath(path);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PermissionsConstant.REQUEST_CAMERA:
                case PermissionsConstant.REQUEST_EXTERNAL_WRITE:
                    if (PermissionsUtils.checkWriteStoragePermission(this) &&
                            PermissionsUtils.checkCameraPermission(this)) {
                        openCamera();
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (directories == null) {
            return;
        }
        for (PhotoDirectory directory : directories) {
            directory.getPhotoPaths().clear();
            directory.getPhotos().clear();
            directory.setPhotos(null);
        }
        directories.clear();
        directories = null;
    }

    private void resumeRequestsIfNotDestroyed() {
        glide.resumeRequests();
    }
}
