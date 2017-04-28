package com.javon.yunphotopicker.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.javon.yunphotopicker.PhotoPicker;
import com.javon.yunphotopicker.R;
import com.javon.yunphotopicker.model.PhotoDirectory;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.HEIGHT;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.WIDTH;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.utils
 * Created by javonLiu on 2017/4/25.
 * e-mail : liujunjie00@yahoo.com
 */

public class MediaStoreHelper {

    public final static int INDEX_ALL_PHOTOS = 0;


    public static void getPhotoDirs(FragmentActivity activity, Bundle args, PhotosResultCallback resultCallback) {
        activity.getSupportLoaderManager()
                .initLoader(0, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    }

    private static class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private Context context;
        private PhotosResultCallback resultCallback;

        public PhotoDirLoaderCallbacks(Context context, PhotosResultCallback resultCallback) {
            this.context = context;
            this.resultCallback = resultCallback;
        }

        @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PhotoDirectoryLoader(context, args.getBoolean(PhotoPicker.EXTRA_SHOW_GIF, false));
        }

        @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data == null)  return;
            List<PhotoDirectory> directories = new ArrayList<>();
            PhotoDirectory photoDirectoryAll = new PhotoDirectory();
            photoDirectoryAll.setName(context.getString(R.string.picker_all_image));
            photoDirectoryAll.setId("ALL");

            //相册中图片分类，以每个图片目录中的第一张图片设为封面图
            while (data.moveToNext()) {

                int imageId  = data.getInt(data.getColumnIndexOrThrow(_ID));
                String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
                String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
                String path = data.getString(data.getColumnIndexOrThrow(DATA));
                int height = data.getInt(data.getColumnIndexOrThrow(HEIGHT));
                int width = data.getInt(data.getColumnIndexOrThrow(WIDTH));
                long size = data.getInt(data.getColumnIndexOrThrow(SIZE));

                if (size < 1) continue;

                PhotoDirectory photoDirectory = new PhotoDirectory();
                photoDirectory.setId(bucketId);
                photoDirectory.setName(name);

                if (!directories.contains(photoDirectory)) {
                    photoDirectory.setCoverPath(path);
                    photoDirectory.addPhoto(imageId, path, height, width);
                    photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                    directories.add(photoDirectory);
                } else {
                    directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path, height, width);
                }

                photoDirectoryAll.addPhoto(imageId, path, height, width);
            }
            if (photoDirectoryAll.getPhotoPaths().size() > 0) {
                photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
            }
            directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);
            if (resultCallback != null) {
                resultCallback.onResultCallback(directories);
            }
        }

        @Override public void onLoaderReset(Loader<Cursor> loader) {

        }
    }


    public interface PhotosResultCallback {
        void onResultCallback(List<PhotoDirectory> directories);
    }

}
