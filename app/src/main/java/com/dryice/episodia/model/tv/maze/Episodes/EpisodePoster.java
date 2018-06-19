package com.dryice.episodia.model.tv.maze.Episodes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EpisodePoster implements Serializable{

    @SerializedName("original")
    private String posterUrl;

    public String getPosterUrl() {
        return posterUrl;
    }
}
