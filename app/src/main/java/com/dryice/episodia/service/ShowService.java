package com.dryice.episodia.service;

import com.dryice.episodia.model.tv.maze.Search.SearchResults;
import com.dryice.episodia.model.tv.maze.Search.Show;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShowService {
    @GET("{relative_path}")
    Call<Show> getResults(@Path("relative_path") String relativePath, @Query("q") String searchTerm);
}
