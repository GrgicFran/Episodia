package com.dryice.episodia.model.tv.maze.Search;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Show implements Serializable {

    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("summary")
    private String summary;
    @SerializedName("image")
    private ShowPoster showPoster;


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public ShowPoster getShowPoster() {
        return showPoster;
    }
}
