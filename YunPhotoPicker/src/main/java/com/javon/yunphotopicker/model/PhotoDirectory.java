package com.javon.yunphotopicker.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker
 * Created by javonLiu on 2017/4/21.
 * e-mail : liujunjie00@yahoo.com
 */


/**
 * 相册中每个子相册目录model创建
 */
public class PhotoDirectory implements Parcelable {

    private String id;
    private String coverPath;
    private String name;
    private long dateAdded;
    private List<Photo> photos = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoDirectory)) return false;
        PhotoDirectory directory = (PhotoDirectory) o;
        if (!id.equals(directory.id)) return false;
        return name.equals(directory.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dataAdded) {
        this.dateAdded = dataAdded;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<String> getPhotoPaths() {
        List<String> paths = new ArrayList<>(photos.size());
        for (Photo photo : photos) {
            paths.add(photo.getPath());
        }
        return paths;
    }

    public void addPhoto(int id, String path, int height, int width) {
        photos.add(new Photo(id, path, height, width));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.coverPath);
        dest.writeString(this.name);
        dest.writeLong(this.dateAdded);
        dest.writeTypedList(photos);
    }

    public PhotoDirectory() {
    }

    protected PhotoDirectory(Parcel in) {
        this.id = in.readString();
        this.coverPath = in.readString();
        this.name = in.readString();
        this.dateAdded = in.readLong();
        this.photos = in.createTypedArrayList(Photo.CREATOR);
    }

    public static final Parcelable.Creator<PhotoDirectory> CREATOR = new Parcelable.Creator<PhotoDirectory>() {
        public PhotoDirectory createFromParcel(Parcel source) {
            return new PhotoDirectory(source);
        }

        public PhotoDirectory[] newArray(int size) {
            return new PhotoDirectory[size];
        }
    };

    @Override
    public String toString() {
        return "PhotoDirectory{" +
                "id='" + id + '\'' +
                ", coverPath='" + coverPath + '\'' +
                ", name='" + name + '\'' +
                ", dateAdded=" + dateAdded +
                ", photos=" + photos +
                '}';
    }
}
