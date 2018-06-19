package com.dryice.episodia.service;


import com.dryice.episodia.model.tv.maze.Search.SearchResults;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SearchResultsService {

    @GET("{relative_path}")
    Call<List<SearchResults>> getResults(@Path("relative_path") String relativePath, @Query("q") String searchTerm);
}
