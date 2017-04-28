package com.javon.yunphotopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker
 * Created by javonLiu on 2017/4/22.
 * e-mail : liujunjie00@yahoo.com
 */

public class PhotoPreview {

    public final static int REQUEST_CODE = 666;

    public final static String EXTRA_CURRENT_ITEM = "CURRENT_ITEM";
    public final static String EXTRA_PHOTOS       = "PHOTOS";
    public final static String EXTRA_SHOW_DELETE  = "SHOW_DELETE";
    public final static String EXTRA_IS_REMOTE  = "IS_REMOTE";


    public static PhotoPreviewBuilder builder() {
        return new PhotoPreviewBuilder();
    }


    public static class PhotoPreviewBuilder {
        private Bundle mPreviewOptionsBundle;
        private Intent mPreviewIntent;

        public PhotoPreviewBuilder() {
            mPreviewOptionsBundle = new Bundle();
            mPreviewIntent = new Intent();
        }

        /**
         * Send the Intent from an Activity with a custom request code
         *
         * @param activity    Activity to receive result
         * @param requestCode requestCode for result
         */
        public void start(@NonNull Activity activity, View view, int requestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        view, "图片预览");
                activity.startActivityForResult(getIntent(activity), requestCode, options.toBundle());
            }else{
                activity.startActivityForResult(getIntent(activity), requestCode);
            }
        }

        /**
         * Send the Intent with a custom request code
         *
         * @param fragment    Fragment to receive result
         * @param requestCode requestCode for result
         */
        public void start(@NonNull Context context, @NonNull android.support.v4.app.Fragment fragment, View view, int requestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(fragment.getActivity(),
                        view, "图片预览");
                fragment.startActivityForResult(getIntent(context), requestCode, options.toBundle());
            }else{
                fragment.startActivityForResult(getIntent(context), requestCode);
            }
        }

        /**
         * Send the Intent with a custom request code
         *
         * @param fragment    Fragment to receive result
         */
        public void start(@NonNull Context context, @NonNull android.support.v4.app.Fragment fragment, View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(fragment.getActivity(),
                        view, "图片预览");
                fragment.startActivityForResult(getIntent(context), REQUEST_CODE, options.toBundle());
            }else{
                fragment.startActivityForResult(getIntent(context), REQUEST_CODE);
            }
        }

        /**
         * Send the crop Intent from an Activity
         *
         * @param activity Activity to receive result
         */
        public void start(@NonNull Activity activity, View view) {
            start(activity, view, REQUEST_CODE);
        }

        /**
         * Get Intent to start {@link PhotoPickerActivity}
         *
         * @return Intent for {@link PhotoPickerActivity}
         */
        private Intent getIntent(@NonNull Context context) {
            mPreviewIntent.setClass(context, PhotoPagerActivity.class);
            mPreviewIntent.putExtras(mPreviewOptionsBundle);
            return mPreviewIntent;
        }

        public PhotoPreviewBuilder setPhotos(ArrayList<String> photoPaths) {
            mPreviewOptionsBundle.putStringArrayList(EXTRA_PHOTOS, photoPaths);
            return this;
        }

        public PhotoPreviewBuilder setCurrentItem(int currentItem) {
            mPreviewOptionsBundle.putInt(EXTRA_CURRENT_ITEM, currentItem);
            return this;
        }

        public PhotoPreviewBuilder setShowDeleteButton(boolean showDeleteButton) {
            mPreviewOptionsBundle.putBoolean(EXTRA_SHOW_DELETE, showDeleteButton);
            return this;
        }

        public PhotoPreviewBuilder setRemoteImage(boolean isRemote) {
            mPreviewOptionsBundle.putBoolean(EXTRA_IS_REMOTE, isRemote);
            return this;
        }
    }
}