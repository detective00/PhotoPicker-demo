package com.javon.yunphotopicker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.javon.yunphotopicker.R;
import com.javon.yunphotopicker.model.PhotoDirectory;
import com.javon.yunphotopicker.utils.GlideUtils;
import com.javon.yunphotopicker.utils.TDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.adapter
 * Created by javonLiu on 2017/4/25.
 * e-mail : liujunjie00@yahoo.com
 */

/**
 * 图库目录列表适配器
 */
public class PopupDirectoryListAdapter extends BaseAdapter {


    private List<PhotoDirectory> directories = new ArrayList<>();
    private RequestManager glide;

    public PopupDirectoryListAdapter(RequestManager glide, List<PhotoDirectory> directories) {
        this.directories = directories;
        this.glide = glide;
    }


    @Override public int getCount() {
        return directories.size();
    }


    @Override public PhotoDirectory getItem(int position) {
        return directories.get(position);
    }


    @Override public long getItemId(int position) {
        return directories.get(position).hashCode();
    }


    @Override public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
            convertView = mLayoutInflater.inflate(R.layout.list_picker_directory_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bindData(directories.get(position));

        return convertView;
    }

    private class ViewHolder {

        public ImageView ivCover;
        public TextView tvName;
        public TextView tvCount;

        public ViewHolder(View rootView) {
            ivCover = (ImageView) rootView.findViewById(R.id.iv_dir_cover);
            tvName  = (TextView)  rootView.findViewById(R.id.tv_dir_name);
            tvCount = (TextView)  rootView.findViewById(R.id.tv_dir_count);
        }

        public void bindData(PhotoDirectory directory) {
            GlideUtils.loadImage(glide, ivCover, directory.getCoverPath(), TDevice.dip2px(ivCover.getContext(), 60), TDevice.dip2px(ivCover.getContext(), 60), 0.1f);
            tvName.setText(directory.getName());
            tvCount.setText(tvCount.getContext().getString(R.string.picker_image_count, directory.getPhotos().size()));
        }
    }

}