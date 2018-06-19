package com.dryice.episodia.model.episodate;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MostPopular implements Serializable {
    @SerializedName("tv_shows")
    ArrayList<Show> shows;

    public ArrayList<Show> getShows() {
        return shows;
    }
}
