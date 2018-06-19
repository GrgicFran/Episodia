package com.dryice.episodia.model.tv.maze.Episodes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Episode implements Serializable {

    @SerializedName("name")
    String episodeName;
    @SerializedName("season")
    Integer seasonNumber;
    @SerializedName("number")
    Integer episodeNumber;
    @SerializedName("image")
    EpisodePoster poster;
    @SerializedName("summary")
    String episodeSummary;
    @SerializedName("url")
    String url;

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public String getEpisodeSummary() {
        return episodeSummary;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public EpisodePoster getPoster() {
        return poster;
    }

    public String getUrl() {
        return url;
    }
}
