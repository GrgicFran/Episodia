package com.dryice.episodia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dryice.episodia.model.tv.maze.Episodes.EpisodePoster;
import com.dryice.episodia.model.tv.maze.Episodes.Episode;
import com.dryice.episodia.service.EpisodesListService;
import com.github.ybq.android.spinkit.SpinKitView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RandomShow extends AppCompatActivity {

    private static final String TAG = "RandomShow";
    private static final String BASE_URL_EPISODES = "http://api.tvmaze.com/shows/";
    private static final String BASE_NETFLIX = "http://www.netflix.com/title/";

    private String showPoster;

    String netflixId;
    String watchUrl;

    Retrofit retrofit;
    HttpLoggingInterceptor logging;
    OkHttpClient.Builder httpClient;

    List<Episode> episodes = new ArrayList<>();
    Map<String, String> netflix = new HashMap<>();

    int seasonsNumber;
    int episodesNumber;

    int randomSeason;
    int randomEpisode;

    String showName;

    @BindView(R.id.show_title)
    TextView showNameText;
    @BindView(R.id.poster)
    RoundedImageView poster;
    @BindView(R.id.episode_name)
    TextView episodeTitle;
    @BindView(R.id.episode_number)
    TextView episodeNumber;
    @BindView(R.id.try_again_button)
    RelativeLayout tryAgain;
    @BindView(R.id.episode_summary)
    TextView episodeSummary;
    @BindView(R.id.netflix_button)
    RelativeLayout netflixButton;

    @BindView(R.id.spinner)
    SpinKitView spinKit;
    @BindView(R.id.main_container)
    RelativeLayout mainContainer;

    private Random randomGenerator = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_show);
        Log.d(TAG, "onCreate: started");
        ButterKnife.bind(this);

        // HTTP logger
        logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Intent i = getIntent();
        Log.d(TAG, "onCreate: " + i.getStringExtra("id"));
        Long showID = Long.parseLong(i.getStringExtra("id"));
        showName = i.getStringExtra("name");
        showPoster = i.getStringExtra("imgUrl");
        netflixId = i.getStringExtra("netflixID");
        watchUrl = BASE_NETFLIX + netflixId;
        Log.d(TAG, "onCreate netflixid: " + netflixId);
        Log.d(TAG, "onCreate: " + showPoster);

        showLoader();
        getEpisodeList(showID);

        netflixButton.setVisibility(View.GONE);

    }

    private int getEpisodeNumber(int season) {
        List<Integer> episodes = new ArrayList<>();
        for (int i = 0; i < this.episodes.size(); i++) {
            if (this.episodes.get(i).getSeasonNumber().equals(season)) {
                episodes.add(this.episodes.get(i).getSeasonNumber());
            }
        }
        return episodes.size();
    }

    private int getSeasonsNumber() {
        Set<Integer> seasonNumbers = new HashSet<>();
        for (int i = 0; i < episodes.size(); i++) {
            seasonNumbers.add(episodes.get(i).getSeasonNumber());
        }
        return seasonNumbers.size();
    }

    private void getEpisodeList(Long showID) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_EPISODES + showID + "/")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final EpisodesListService episodesListService = retrofit.create(EpisodesListService.class);
        episodesListService.getResults("episodes").enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(@NonNull Call<List<Episode>> call, @NonNull Response<List<Episode>> response) {
                Log.d(TAG, "onResponse: WOOO");
                episodes = response.body();
                generateRandom();
                hideLoader();
            }

            @Override
            public void onFailure(@NonNull Call<List<Episode>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: Eh, to ti je tak stari kad ne znas programirat");
            }
        });
    }

    private void generateRandom() {
        YoYo.with(Techniques.FadeOut).playOn(poster);
        seasonsNumber = getSeasonsNumber();
        randomSeason = generateRandomSeason();
        episodesNumber = getEpisodeNumber(randomSeason);
        randomEpisode = generateRandomEpisode();
        updateRandomShowUI();

    }

    @SuppressLint("SetTextI18n")
    private void updateRandomShowUI() {
        showNameText.setText(showName);
        String episodeName = getEpisodeName(randomSeason, randomEpisode);
        episodeTitle.setText(episodeName);
        if (seasonsNumber > 0) {
            episodeNumber.setText(getString(R.string.season) + " " + randomSeason + ", " + getString(R.string.episode) + " " + randomEpisode);
        } else {
            episodeNumber.setText(getString(R.string.not_released));
        }
        String summary = getEpisodeSummary(randomSeason, randomEpisode);
        try {
            summary = Html.fromHtml(summary).toString();
        } catch (NullPointerException e) {
            summary = "";
        }
        episodeSummary.setText(summary.trim());
        episodeSummary.setVisibility(View.VISIBLE);
        if (episodeSummary.getText().toString().isEmpty()) {
            episodeSummary.setVisibility(View.GONE);
        }

        // TODO: There was a crash here: IllegalArgumentException... If nothing surround it with try/catch
        Glide.with(this)
                .asBitmap()
                .load(getEpisodePoster(randomSeason, randomEpisode))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        YoYo.with(Techniques.FadeIn).playOn(poster);
                        return false;
                    }
                })
                .into(poster);


        if (netflixId != null && !netflixId.isEmpty()) {
            netflixButton.setVisibility(View.VISIBLE);
        }
    }

    private String getEpisodeName(int seasonNumber, int episodeNumber) {
        for (Episode episode : episodes) {
            if (episode.getSeasonNumber() == seasonNumber && episode.getEpisodeNumber() == episodeNumber) {
                return episode.getEpisodeName();
            }
        }
        return "Unknown name";
    }

    private String getEpisodeSummary(int seasonsNumber, int episodesNumber) {
        for (Episode episode : episodes) {
            if (episode.getSeasonNumber() == seasonsNumber && episode.getEpisodeNumber() == episodesNumber) {
                return episode.getEpisodeSummary();
            }
        }
        return "";
    }

    private String getEpisodePoster(int seasonsNumber, int episodesNumber) {
        for (Episode episode : episodes) {
            if (episode.getSeasonNumber() == seasonsNumber && episode.getEpisodeNumber() == episodesNumber) {
                EpisodePoster poster = episode.getPoster();
                if (poster != null) {
                    return episode.getPoster().getPosterUrl();
                } else if (showPoster != null && !showPoster.isEmpty()) {
                    return showPoster;
                }

            }
        }
        return "http://deviantpics.com/images/2018/06/12/no_image.png";
    }

    private int generateRandomEpisode() {
        if (episodesNumber > 0) {
            return randomGenerator.nextInt(episodesNumber) + 1;
        } else {
            return 0;
        }
    }

    private int generateRandomSeason() {
        int season;
        if (seasonsNumber > 1) {
            season = randomGenerator.nextInt(seasonsNumber);
            season += episodes.get(0).getSeasonNumber();
            return season;
        } else if (seasonsNumber == 1) {
            return 1;
        }
        return 0;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @OnClick(R.id.try_again_button)
    void onTryAgainClick() {
        generateRandom();
//        YoYo.with(Techniques.Tada).playOn(tryAgain);
    }

    @OnClick(R.id.netflix_button)
    void openNetflix() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.launch.UIWebViewActivity");
            intent.setData(Uri.parse(watchUrl));
            startActivity(intent);
        } catch (Exception e) {
            // netflix app isn't installed, send to website.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(watchUrl));
            startActivity(intent);
        }
    }

    private void showLoader() {
        spinKit.setVisibility(View.VISIBLE);
        mainContainer.setVisibility(View.GONE);
    }

    private void hideLoader() {
        spinKit.setVisibility(View.GONE);
        mainContainer.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn).playOn(mainContainer);
    }


    @Override
    protected void onPause() {
        super.onPause();
//        fadeOutMain();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRandomShowUI();
    }
}
