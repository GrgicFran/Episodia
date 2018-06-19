package com.dryice.episodia;

import android.Manifest;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class WalkthroughActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

//        setZoomAnimation();

        // Note here that we DO NOT use setContentView();

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Find your favorite show", "Find a show you want to watch a random episode of",R.drawable.i_2, getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Generate a random episode", "The app will generate a random episode for you", R.drawable.i_3,  getResources().getColor(R.color.randomEpisode)));
        addSlide(AppIntroFragment.newInstance("Wanna stream it right now?", "If the show is available, you get the button to go to it directly (various streaming platforms supported)", R.drawable.i_4, getResources().getColor(R.color.netflixRed)));
        addSlide(AppIntroFragment.newInstance("Have no idea what to watch?", "Just browse the list of currently trending shows", R.drawable.i_1, getResources().getColor(R.color.trending)));

        // OPTIONAL METHODS
//        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

//        // Hide Skip/Done button.
//        showSkipButton(true);
//        setProgressButtonEnabled(false);
//        showSkipButton(true);

        doneButton.setVisibility(View.VISIBLE);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(false);
//        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
