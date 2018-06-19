package com.dryice.episodia.model.tv.maze.Search;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SearchResults implements Serializable{

    @SerializedName("show")
    private Show show;


    public Show getShow() {
        return show;
    }

}
