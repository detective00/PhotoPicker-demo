package com.javon.yunphotopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.javon.yunphotopicker.R;
import com.javon.yunphotopicker.event.OnItemCheckListener;
import com.javon.yunphotopicker.event.OnPhotoClickListener;
import com.javon.yunphotopicker.model.Photo;
import com.javon.yunphotopicker.model.PhotoDirectory;
import com.javon.yunphotopicker.utils.GlideUtils;
import com.javon.yunphotopicker.utils.MediaStoreHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.adapter
 * Created by javonLiu on 2017/4/25.
 * e-mail : liujunjie00@yahoo.com
 */

public class PhotoAdapter extends SelectableAdapter<PhotoAdapter.PhotoViewHolder> {

    private RequestManager glide;
    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;

    public final static int ITEM_TYPE_CAMERA = 1;
    public final static int ITEM_TYPE_PHOTO = 2;
    private final static int COL_NUMBER_DEFAULT = 3;

    private boolean hasCamera = true;  //是否显示拍照功能，默认显示
    private boolean previewEnable = true;//照片是否可以预览，默认可预览

    private int imageSize;
    private int columnNumber = COL_NUMBER_DEFAULT; //网格布局默认显示3列


    public PhotoAdapter(Context context, RequestManager requestManager, List<PhotoDirectory> photoDirectories) {
        this.photoDirectories = photoDirectories;
        this.glide = requestManager;
        setColumnNumber(context, columnNumber);
    }

    public PhotoAdapter(Context context, RequestManager requestManager, List<PhotoDirectory> photoDirectories, ArrayList<String> selectPhotos, int colNum) {
        this(context, requestManager, photoDirectories);
        setColumnNumber(context, colNum);
        selectedPhotos = new ArrayList<>();
        if (selectPhotos != null) selectedPhotos.addAll(selectPhotos);
    }

    /**
     * 计算图片宽度大小
     * @param context
     * @param columnNumber
     */
    private void setColumnNumber(Context context, int columnNumber) {
        this.columnNumber = columnNumber;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / columnNumber;
    }

    /**
     * 判断是拥有拍照功能
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_picker_photo_item, parent, false);
        final PhotoViewHolder holder = new PhotoViewHolder(itemView);
        //拍照item样式设定
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.vSelected.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onClick(view);
                    }
                }
            });
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {

        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {// 图片item数据加载绑定
            List<Photo> photos = getCurrentPhotos();
            final Photo photo;
            if (showCamera()) {
                photo = photos.get(position - 1);
            } else {
                photo = photos.get(position);
            }
            GlideUtils.loadImage(glide, holder.ivPhoto, photo.getPath(), imageSize, imageSize, 0.5f);
            final boolean isChecked = isSelected(photo);

            holder.vSelected.setSelected(isChecked);
            holder.ivPhoto.setSelected(isChecked);

            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPhotoClickListener != null) {
                        int pos = holder.getAdapterPosition();
                        if (previewEnable) {
                            onPhotoClickListener.onClick(view, pos, showCamera());
                        } else {
                            holder.vSelected.performClick();
                        }
                    }
                }
            });
            holder.vSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    boolean isEnable = true;
                    if (onItemCheckListener != null) {
                        isEnable = onItemCheckListener.onItemCheck(pos, photo,
                                getSelectedPhotos().size() + (isSelected(photo) ? -1 : 1));
                    }
                    if (isEnable) {
                        toggleSelection(photo);
                        notifyItemChanged(pos);
                    }
                }
            });

        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_picker_camera_selector);
        }
    }


    @Override
    public int getItemCount() {
        int photosCount =
                photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
        if (showCamera()) {
            return photosCount + 1;
        }
        return photosCount;
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
        }
    }


    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }


    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }


    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }


    public ArrayList<String> getSelectedPhotoPaths() {
        ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());

        for (String photo : selectedPhotos) {
            selectedPhotoPaths.add(photo);
        }

        return selectedPhotoPaths;
    }


    public void setShowCamera(boolean hasCamera) {
        this.hasCamera = hasCamera;
    }

    public void setPreviewEnable(boolean previewEnable) {
        this.previewEnable = previewEnable;
    }

    public boolean showCamera() {
        return (hasCamera && currentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
    }

    /**
     * 图片资源回收
     * @param holder
     */
    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        Glide.clear(holder.ivPhoto);
        super.onViewRecycled(holder);
    }
}
