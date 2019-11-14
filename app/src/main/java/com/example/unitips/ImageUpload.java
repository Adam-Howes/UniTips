package com.example.unitips;

import android.media.Image;

public class ImageUpload {
    private String mImageID;
    private String mName;
    private String mImageUrl;

    public ImageUpload() {
        // Empty constructor needed
    }

    public ImageUpload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "no Name";
        }

        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
