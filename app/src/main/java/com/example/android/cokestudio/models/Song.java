package com.example.android.cokestudio.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jayabrata Dhakai on 3/18/2017.
 */

public class Song implements Serializable {

    private String song;
    private String url;
    private String artists;
    @SerializedName("cover_image")
    private String coverImageUrl;

    public String getSong() {
        return song;
    }

    public String getUrl() {
        return url;
    }

    public String getArtists() {
        return artists;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }
}
