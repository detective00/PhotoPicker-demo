package com.javon.yunphotopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.javon.yunphotopicker.R;
import com.javon.yunphotopicker.utils.GlideUtils;
import com.javon.yunphotopicker.utils.TDevice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.adapter
 * Created by javonLiu on 2017/4/26.
 * e-mail : liujunjie00@yahoo.com
 */

public class PhotoPreviewAdapter extends PagerAdapter {

    private List<String> paths = new ArrayList<>();
    private RequestManager glide;
    private Bitmap saveBitmap;

    public PhotoPreviewAdapter(RequestManager glide, List<String> paths) {
        this.paths = paths;
        this.glide = glide;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        final Context context = container.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.list_picker_pager_item, container, false);
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_pager);
        final String path = paths.get(position);
        GlideUtils.loadImage(glide, imageView, path, TDevice.getScreenWidth(imageView.getContext()), TDevice.getScreenHeight(imageView.getContext()), 0.1f); // 800 800
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (context instanceof Activity) {
                    if (!((Activity) context).isFinishing()) {
                        ((Activity) context).onBackPressed();
                    }
                }
            }
        });
        container.addView(itemView);
        return itemView;
    }


    @Override public int getCount() {
        return paths.size();
    }


    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Glide.clear((View) object);
    }

    @Override
    public int getItemPosition (Object object) { return POSITION_NONE; }

}
