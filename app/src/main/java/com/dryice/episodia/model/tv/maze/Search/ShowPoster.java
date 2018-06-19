package com.dryice.episodia.model.tv.maze.Search;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShowPoster implements Serializable{

    @SerializedName("original")
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }
}
