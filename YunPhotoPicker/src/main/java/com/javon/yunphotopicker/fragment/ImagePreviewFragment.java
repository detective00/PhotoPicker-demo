package com.javon.yunphotopicker.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.javon.yunphotopicker.PhotoPicker;
import com.javon.yunphotopicker.R;
import com.javon.yunphotopicker.adapter.PhotoPreviewAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.javon.yunphotopicker.PhotoPreview.EXTRA_PHOTOS;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker.fragment
 * Created by javonLiu on 2017/4/25.
 * e-mail : liujunjie00@yahoo.com
 */

public class ImagePreviewFragment extends Fragment {

    private ArrayList<String> photos;
    private ViewPager viewPager;
    private PhotoPreviewAdapter adapter;
    private FloatingActionButton saveFab;


    public static ImagePreviewFragment newInstance(ArrayList<String> photos){
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(EXTRA_PHOTOS, photos);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            photos = bundle.getStringArrayList(EXTRA_PHOTOS);
        }
        if(photos == null)
            photos = new ArrayList<>();
        adapter = new PhotoPreviewAdapter(Glide.with(getActivity()), photos);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_pager, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.vp_photos);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(PhotoPicker.CURRENT_ITEM);
        viewPager.setOffscreenPageLimit(5);
        return rootView;
    }


    public void setPhotos(List<String> paths) {
        this.photos.clear();
        this.photos.addAll(paths);
        viewPager.setCurrentItem(PhotoPicker.CURRENT_ITEM);
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public ViewPager getViewPager() {
        return viewPager;
    }


    public ArrayList<String> getPaths() {
        return photos;
    }


    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        photos.clear();
        photos = null;
        if (viewPager != null) {
            viewPager.setAdapter(null);
        }
    }

}
