package com.dryice.episodia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dryice.episodia.adapter.RecyclerViewAdapter;
import com.dryice.episodia.model.episodate.MostPopular;
import com.dryice.episodia.model.tv.maze.Search.SearchResults;
import com.dryice.episodia.service.MostPopularService;
import com.dryice.episodia.service.SearchResultsService;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URL_SEARCH= "http://api.tvmaze.com/search/";
    private static final String BASE_URL_TRENDING = "https://www.episodate.com/api/";
    private static final String SEARCH_RELATIVE = "shows";


    @BindView(R.id.search_bar)
    SearchView searchBar;
//    @BindView(R.id.title)
//    TextView title;
    @BindView(R.id.spin_kit)
    SpinKitView spinKit;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.no_results)
    TextView noResults;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;

    //Save most popular shows here so you don't have to reload
    MostPopular popular;

//    RecyclerViewAdapter adapter;
    //for Adapter
    private List<String> mNames = new ArrayList<>();
    private List<String> mImageUrls = new ArrayList<>();
    private List<String> mSummaries = new ArrayList<>();
    private List<Long> mIDs = new ArrayList<>();
    private String titleText;

    private static boolean showingPopular;

    Handler handler;
    Retrofit retrofit;
    HttpLoggingInterceptor logging;
    OkHttpClient.Builder httpClient;

    Map<String, String> netflix = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        rootLayout.requestFocus();

        Log.d(TAG, "onCreate: started.");

        handler = new Handler();

        // used for logging
        logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

//        startActivity(new Intent(MainActivity.this, WalkthroughActivity.class));

        // Show walkthrough
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {

            startActivity(new Intent(MainActivity.this, WalkthroughActivity.class));
            Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
                    .show();
        }


        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();


        getTrending();

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            String searchText;
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!s.equals(searchText)) {
                    showLoader();
                    doTheSearch(s);
                    return true;
                }

                return false;
            }

            Runnable runnable;
            @Override
            public boolean onQueryTextChange(String s) {
                searchText = s;

                showLoader();

                // Remove all previous callbacks.
                handler.removeCallbacks(runnable);

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(searchText.isEmpty() && popular!=null){
                            titleText = getString(R.string.trending);
                            showPopular();
                        }else{
                            titleText = getString(R.string.results);
                            doTheSearch(searchText);
                        }
                    }
                };
                handler.postDelayed(runnable, 300);

                return false;
            }
        });
        searchBar.setQueryHint(getString(R.string.query_hint));

        loadNetflixFile();
    }

    private void loadNetflixFile() {
        String text = "";
        try{
            InputStream is = getAssets().open("netflix.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
        }catch (IOException e){
            e.printStackTrace();
        }

        if(!text.isEmpty()){
            String[] lines = text.split("\\\n");
            for(String line : lines){
                String[] lineElements = line.split("\\(\\$\\)");
                netflix.put(lineElements[0].toLowerCase().trim(), lineElements[1].toLowerCase().trim());
            }
        }

    }

    private void getTrending() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_TRENDING)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        showLoader();
        final MostPopularService mostPopularService = retrofit.create(MostPopularService.class);
        mostPopularService.getResults("most-popular").enqueue(new Callback<com.dryice.episodia.model.episodate.MostPopular>() {
            @Override
            public void onResponse(@NonNull Call<com.dryice.episodia.model.episodate.MostPopular> call, @NonNull Response<com.dryice.episodia.model.episodate.MostPopular> response) {
                popular = response.body();
                showPopular();
            }

            @Override
            public void onFailure(@NonNull Call<com.dryice.episodia.model.episodate.MostPopular> call, @NonNull Throwable t) {

            }
        });
    }

    private void showPopular() {
        showingPopular = true;
        mImageUrls.clear();
        mNames.clear();
        mSummaries.clear();
        for(int i = 0; i<popular.getShows().size(); i++){
            if(popular.getShows().get(i).getPoster() != null){
                mImageUrls.add(popular.getShows().get(i).getPoster());
            } else {
                mImageUrls.add("http://deviantpics.com/images/2018/06/12/no_image.png");
            }

            mSummaries.add(getString(R.string.network) + ": " + popular.getShows().get(i).getNetwork());
            mNames.add(popular.getShows().get(i).getName());
            initRecyclerView();
            titleText = getString(R.string.trending);
            hideLoader();
        }
    }

    private void doTheSearch(String s) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_SEARCH)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final SearchResultsService searchResultsService = retrofit.create(SearchResultsService.class);
        searchResultsService.getResults(SEARCH_RELATIVE, s).enqueue(new Callback<List<SearchResults>>() {
            @Override
            public void onResponse(@NonNull Call<List<SearchResults>> call, @NonNull Response<List<SearchResults>> response) {
                Log.d(TAG, "onResponse: SUCCESS");
                List<SearchResults> results = response.body();

                mImageUrls.clear();
                mNames.clear();
                mSummaries.clear();
                mIDs.clear();
//                adapter.notifyDataSetChanged();

                if(results!=null && results.size() == 0){
                    noResults();
                    showingPopular = false;
                }else{
                    titleText = getString(R.string.results);
                    if (results != null) {
                        for(int i = 0; i<results.size(); i++){
                            if(results.get(i).getShow().getShowPoster() != null){
                                mImageUrls.add(results.get(i).getShow().getShowPoster().getImgUrl());
                            } else {
                                mImageUrls.add("http://deviantpics.com/images/2018/06/12/no_image.png");
                            }

                            String name = results.get(i).getShow().getName();
                            String summary = "";
                            if(results.get(i).getShow().getSummary()!=null){
                                summary = results.get(i).getShow().getSummary();
                                summary = Html.fromHtml(summary).toString();
                            }
    //                        if(name.length() > 33){
    //                            name = name.substring(0,30).trim() + "...";
    //                        }
                            if(summary.length() > 100){
                                summary = summary.substring(0,100).trim() + "...";
                            }
                            if(summary.length() > 50 && name.length() >20){
                                summary = summary.substring(0,50).trim() + "...";
                            }
                            Log.d(TAG, "onResponse: " + summary);
                            Log.d(TAG, "onResponse: " + mSummaries.toString());

                            mIDs.add(results.get(i).getShow().getId());
                            mNames.add(name);
                            mSummaries.add(summary);
                            initRecyclerView();
                        }
                    }

                    showingPopular = false;
                    hideLoader();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SearchResults>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: FAILURE");
            }
        });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls, mSummaries, mIDs, netflix, titleText);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void showLoader(){
        spinKit.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noResults.setVisibility(View.GONE);
    }

    private void hideLoader() {
        spinKit.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        noResults.setVisibility(View.GONE);
    }

    private void noResults() {
        hideLoader();
        spinKit.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noResults.setVisibility(View.VISIBLE);
    }

    public static boolean isTrendingShowing(){
        return showingPopular;
    }

    @OnTouch(R.id.recycler_view)
    boolean onRecyclerTouch(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
        }
    }
}
