package com.dryice.episodia.service;

import com.dryice.episodia.model.episodate.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MostPopularService {
    @GET("{relative_path}")
    Call<MostPopular> getResults(@Path("relative_path") String relativePath);
}
