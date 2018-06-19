package com.dryice.episodia.service;

import com.dryice.episodia.model.tv.maze.Episodes.Episode;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EpisodesListService {
    @GET("{relative_path}")
    Call<List<Episode>> getResults(@Path("relative_path") String relativePath);
}
