package com.dryice.episodia.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dryice.episodia.MainActivity;
import com.dryice.episodia.R;
import com.dryice.episodia.RandomShow;
import com.dryice.episodia.model.tv.maze.Search.Show;
import com.dryice.episodia.service.ShowService;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private static final String BASE_URL = "http://api.tvmaze.com/singlesearch/";

    private List<String> mImageNames;
    private List<String> mImages;
    private List<String> mSummaries;
    private List<Long> mIDs;
    private Map<String, String> mNetflixIDs;
    private Context mContext;
    private String titleText;

    private OkHttpClient.Builder httpClient;

    public RecyclerViewAdapter(Context context, List<String> mImageNames, List<String> mImages, List<String> mSummaries, List<Long> mIDs, Map<String, String> mNetflixIDs, String titleText) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = context;
        this.mSummaries = mSummaries;
        this.mIDs = mIDs;
        this.mNetflixIDs = mNetflixIDs;
        this.titleText = titleText;

        // HTTP logger
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case 0:
                View viewTitle = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listtitle, viewGroup, false);
                return new ViewHolderTitle(viewTitle);
            default:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called");

        switch (i) {
            case 0:
                ViewHolderTitle viewHolderTitle = (ViewHolderTitle) viewHolder;
                viewHolderTitle.textView.setText(titleText);
                break;
            default:
                final int position = i - 1;
                Glide.with(mContext)
                        .asBitmap()
                        .load(mImages.get(position))
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                YoYo.with(Techniques.SlideInLeft).duration(300).playOn(viewHolder.image);
                                return false;
                            }
                        })
                        .into(viewHolder.image);

                String name = mImageNames.get(position);
                if (mImageNames.get(position).length() > 33) {
                    name = mImageNames.get(position).substring(0, 30).trim() + "...";
                }
                viewHolder.imageName.setText(name);
                viewHolder.summary.setText(mSummaries.get(position));
                //ViewCompat.setElevation((viewHolder).parentLayout, 12);
                viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));
                        //ViewCompat.setElevation(view, 50);

                        if (MainActivity.isTrendingShowing()) {
                            doSingleSearch(mImageNames.get(position));
                        } else {
                            Activity activity = (Activity) mContext;
                            Intent intent = new Intent(mContext, RandomShow.class);
                            intent.putExtra("id", mIDs.get(position) + "");
                            intent.putExtra("name", mImageNames.get(position));
                            intent.putExtra("imgUrl", mImages.get(position));
                            intent.putExtra("netflixID", mNetflixIDs.get(mImageNames.get(position).trim().toLowerCase()));
                            mContext.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        }
                    }
                });

        }
    }

    private void doSingleSearch(String name) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ShowService showService = retrofit.create(ShowService.class);
        showService.getResults("shows", name).enqueue(new Callback<Show>() {
            @Override
            public void onResponse(Call<Show> call, Response<Show> response) {
                Show show = response.body();
                if (show != null || show.getId() != null) {
                    Activity activity = (Activity) mContext;
                    Intent intent = new Intent(mContext, RandomShow.class);
                    intent.putExtra("id", show.getId() + "");
                    intent.putExtra("name", show.getName());
                    intent.putExtra("imgUrl", show.getShowPoster().getImgUrl());
                    intent.putExtra("netflixID", mNetflixIDs.get(show.getName().trim().toLowerCase()));
                    Log.d(TAG, "onResponse: " + show.getShowPoster().getImgUrl().toString());
                    mContext.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                } else {
                    Toast.makeText(mContext, "Unfortunately, this show is unavailable", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Show> call, Throwable t) {

            }
        });
        //final SearchResultsService searchResultsService = retrofit.create(SearchResultsService.class);
        //        searchResultsService.getResults(SEARCH_RELATIVE, s).enqueue(new Callback<List<SearchResults>>()
    }

    @Override
    public int getItemCount() {
        return (mImageNames.size() + 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView image;
        TextView imageName;
        RelativeLayout parentLayout;
        TextView summary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            summary = itemView.findViewById(R.id.summary);

//            YoYo.with(Techniques.FadeIn).duration(300).playOn(parentLayout);
        }
    }

    public class ViewHolderTitle extends ViewHolder {
        TextView textView;


        public ViewHolderTitle(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_title_text);
        }
    }
}
