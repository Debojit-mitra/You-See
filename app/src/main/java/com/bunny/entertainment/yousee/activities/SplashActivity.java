package com.bunny.entertainment.yousee.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;

import com.bunny.entertainment.yousee.BuildConfig;
import com.bunny.entertainment.yousee.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    ImageView splash_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //dark_mode_theme
        SharedPreferences options = getSharedPreferences("optionsPreference", Context.MODE_PRIVATE);
        String savedValueTheme_mode = options.getString("dark_mode", "null");
        if (!savedValueTheme_mode.equals("null")) {
            if (savedValueTheme_mode.equals("ON")) {
                AppCompatDelegate
                        .setDefaultNightMode(
                                AppCompatDelegate
                                        .MODE_NIGHT_YES);
            } else {
                AppCompatDelegate
                        .setDefaultNightMode(
                                AppCompatDelegate
                                        .MODE_NIGHT_NO);
            }

        }

        FirebaseApp.initializeApp(SplashActivity.this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(SplashActivity.this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);


        //checking if android version is A12 or more to run new method splashScreen and if less then A12, run the traditional method of splash screen
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splash_logo = findViewById(R.id.splash_logo);
            splash_logo.setVisibility(View.GONE);
            SplashScreen splashScreen = SplashScreen.installSplashScreen(SplashActivity.this);
            splashScreen.setKeepOnScreenCondition(() -> false);
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
                final ObjectAnimator fadeOut = ObjectAnimator.ofFloat(
                        splashScreenView,
                        "alpha",
                        1f,
                        0.0f);
                // fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setDuration(800);

                nextStep();

                // Call SplashScreenView.remove at the end of your custom animation.
                fadeOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splashScreenView.remove();
                    }
                });
                // Run your animation.
                fadeOut.start();

            });
        } else {*/
            //setTheme(R.style.Theme_OtakuSenpai_SplashScreen);

        Intent intent = getIntent();
        if(intent != null && Intent.ACTION_VIEW.equals(intent.getAction())){
            Uri data = intent.getData();
            Log.e("malId", String.valueOf(data));
            if(data != null){
                String[] parts = data.toString().split("/");
                String animeId = parts[3];
                Intent intent1 = new Intent(SplashActivity.this, AnimeActivity.class);
                intent1.putExtra("malId", "38524");
                startActivity(intent1);
            }
        } else {
            loadAppAsUsual();
        }


        }

    private void loadAppAsUsual(){
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }, 800);
        }


    //}

    private void nextStep() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}