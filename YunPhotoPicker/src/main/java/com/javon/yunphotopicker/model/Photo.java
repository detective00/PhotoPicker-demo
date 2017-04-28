package com.javon.yunphotopicker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * project:PhotoPicker-demo
 * package:com.javon.yunphotopicker
 * Created by javonLiu on 2017/4/21.
 * e-mail : liujunjie00@yahoo.com
 */


/**
 * 相册中每张图片，model创建
 */
public class Photo implements Parcelable {
    private int id;
    private String path;
    private int height, width;

    public Photo(int id, String path, int height, int width) {
        this.id = id;
        this.path = path;
        this.height = height;
        this.width = width;
    }

    public Photo(int id, String path) {
        this.id = id;
        this.path = path;
        this.height = 0;
        this.width = 0;
    }

    public Photo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (id != photo.id) return false;
        return !(path != null ? !path.equals(photo.path) : photo.path != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.path);
        dest.writeInt(this.height);
        dest.writeInt(this.width);
    }

    protected Photo(Parcel in) {
        this.id = in.readInt();
        this.path = in.readString();
        this.height = in.readInt();
        this.width = in.readInt();
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
