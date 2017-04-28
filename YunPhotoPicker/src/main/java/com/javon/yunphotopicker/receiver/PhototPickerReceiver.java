package com.javon.yunphotopicker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.javon.yunphotopicker.PhotoPicker;
import com.javon.yunphotopicker.fragment.PhotoPickerFragment;


/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.receiver
 * Created by javonLiu on 2017/4/26.
 * e-mail : liujunjie00@yahoo.com
 */

public class PhototPickerReceiver extends BroadcastReceiver {

    private PhotoPickerFragment fragment;

    public PhototPickerReceiver(PhotoPickerFragment fragment) {
        this.fragment = fragment;
    }


    public static void sendBroadcast(Context context) {
        Intent intent = new Intent(PhotoPicker.SLIDING_INDEX_ACTION);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (fragment != null)
            fragment.slidingCurrentIndex();
    }

}
