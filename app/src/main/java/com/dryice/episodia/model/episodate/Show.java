package com.dryice.episodia.model.episodate;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Show implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("image_thumbnail_path")
    private String poster;
    @SerializedName("network")
    private String network;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPoster() {
        return poster;
    }

    public String getNetwork() {
        return network;
    }
}
