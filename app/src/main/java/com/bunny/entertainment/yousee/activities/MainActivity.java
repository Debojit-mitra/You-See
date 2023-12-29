package com.bunny.entertainment.yousee.activities;

import static com.bunny.entertainment.yousee.utils.Constants.API_CHECK_UPDATER_URL;
import static com.bunny.entertainment.yousee.utils.Constants.API_GENRES_URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bunny.entertainment.yousee.BuildConfig;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.fragments.DramaFragment;
import com.bunny.entertainment.yousee.fragments.HomeFragment;
import com.bunny.entertainment.yousee.fragments.AnimeFragment;
import com.bunny.entertainment.yousee.fragments.ShowsFragment;
import com.bunny.entertainment.yousee.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction fragmentTransaction;
    FrameLayout main_frame_layout;
    boolean exit = false;
    private final Fragment mDramaFragment = new DramaFragment();
    private final Fragment mShowFragment = new ShowsFragment();
    private final Fragment mHomeFragment = new HomeFragment();
    private final Fragment mAnimeFragment = new AnimeFragment();
    Fragment fragment = null;
    AnimatedBottomBar animatedBottomBar;
    String myApiURL, updaterMainUrl, genresApiURL;
    BottomSheetDialog bottomSheetDialog;
    View bsView;
    TextView textView_update_btn, textView_notNow_btn, releaseDate_text, Changelogs_text, textView_new_update_available;
    LottieAnimationView custom_animationView;
    LinearLayout linear1, linear2, linear3;
    FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences dramaPreferences;
    boolean genresNpointLinkWorking = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        checkMYAPI();

        animatedBottomBar = findViewById(R.id.animatedBottomBar);
        main_frame_layout = findViewById(R.id.main_frame_layout);

        dramaPreferences = getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
        String MainActivityFragmentSelectedLast = dramaPreferences.getString("MainActivityFragmentSelectedLast", null);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(MainActivityFragmentSelectedLast == null){
            fragmentTransaction.replace(R.id.main_frame_layout, mDramaFragment);
        } else {
            if(MainActivityFragmentSelectedLast.equals("mDramaFragment")){
                animatedBottomBar.selectTabAt(0, true);
                fragmentTransaction.replace(R.id.main_frame_layout, mDramaFragment);
            } else if (MainActivityFragmentSelectedLast.equals("mShowFragment")) {
                animatedBottomBar.selectTabAt(1, true);
                fragmentTransaction.replace(R.id.main_frame_layout, mShowFragment);
            } else if (MainActivityFragmentSelectedLast.equals("mHomeFragment")) {
                animatedBottomBar.selectTabAt(2, true);
                fragmentTransaction.replace(R.id.main_frame_layout, mHomeFragment);
            } else if (MainActivityFragmentSelectedLast.equals("mAnimeFragment")) {
                animatedBottomBar.selectTabAt(3, true);
                fragmentTransaction.replace(R.id.main_frame_layout, mAnimeFragment);
            }
        }
        fragmentTransaction.commit();

        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            //Fragment fragment = null;
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.zoom_out, R.anim.nothing_fast);
                if (tab1.getId() == R.id.drama) {
                    fragment = mDramaFragment;
                    if (getSupportFragmentManager().getFragments().contains(mDramaFragment)) {
                        fragmentTransaction.show(mDramaFragment);
                    } else {
                        fragmentTransaction.add(R.id.main_frame_layout, fragment);
                        fragmentTransaction.show(fragment);
                    }
                    fragmentTransaction.hide(mHomeFragment);
                    fragmentTransaction.hide(mShowFragment);
                    fragmentTransaction.hide(mAnimeFragment);
                    dramaPreferences.edit().putString("MainActivityFragmentSelectedLast", "mDramaFragment").apply();

                } else if (tab1.getId() == R.id.shows) {
                    fragment = mShowFragment;
                    if (getSupportFragmentManager().getFragments().contains(mShowFragment)) {
                        fragmentTransaction.show(mShowFragment);
                    } else {
                        fragmentTransaction.add(R.id.main_frame_layout, fragment);
                        fragmentTransaction.show(fragment);
                    }
                    fragmentTransaction.hide(mDramaFragment);
                    fragmentTransaction.hide(mAnimeFragment);
                    fragmentTransaction.hide(mHomeFragment);
                    dramaPreferences.edit().putString("MainActivityFragmentSelectedLast", "mShowFragment").apply();

                } else if (tab1.getId() == R.id.home) {
                    fragment = mHomeFragment;
                    if (getSupportFragmentManager().getFragments().contains(mHomeFragment)) {
                        fragmentTransaction.show(mHomeFragment);
                    } else {
                        fragmentTransaction.add(R.id.main_frame_layout, fragment);
                        fragmentTransaction.show(fragment);
                    }
                    fragmentTransaction.hide(mShowFragment);
                    fragmentTransaction.hide(mDramaFragment);
                    fragmentTransaction.hide(mAnimeFragment);
                    dramaPreferences.edit().putString("MainActivityFragmentSelectedLast", "mHomeFragment").apply();

                } else if (tab1.getId() == R.id.anime) {
                    fragment = mAnimeFragment;
                    if (getSupportFragmentManager().getFragments().contains(mAnimeFragment)) {
                        fragmentTransaction.show(mAnimeFragment);
                    } else {
                        fragmentTransaction.add(R.id.main_frame_layout, fragment);
                        fragmentTransaction.show(fragment);
                    }
                    fragmentTransaction.hide(mShowFragment);
                    fragmentTransaction.hide(mDramaFragment);
                    fragmentTransaction.hide(mHomeFragment);
                    dramaPreferences.edit().putString("MainActivityFragmentSelectedLast", "mAnimeFragment").apply();
                }
                fragmentTransaction.commit();
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });
    }

    private void checkMYAPI() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            if(genresNpointLinkWorking){
                updaterMainUrl = API_CHECK_UPDATER_URL;
            } else {
                updaterMainUrl = "https://gist.githubusercontent.com/Debojit-mitra/ef1be0916ab656712b03271d289619ee/raw/YouSeeUpdater.json";
            }


            try {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, updaterMainUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("myApiURL")) {
                                myApiURL = jsonObject.getString("myApiURL");
                            }
                            if (myApiURL != null && !API_CHECK_UPDATER_URL.equals(myApiURL)) {
                                Constants.updateMyConsumetApi(myApiURL);
                            }
                            if (jsonObject.has("genresApiURL")) {
                                genresApiURL = jsonObject.getString("genresApiURL");
                            }
                            if (genresApiURL != null && !API_GENRES_URL.equals(genresApiURL)) {
                                Constants.updateApiGenresUrl(genresApiURL);
                            }
                            initializeViews(handler);
                            checkUpdatesAndAppUsage(jsonObject, handler);

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Toast.makeText(MainActivity.this, String.valueOf(error.networkResponse), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        if(!genresNpointLinkWorking){
                            initializeViews(handler);
                            networkErrorCheckOldData(handler);
                        } else {
                            genresNpointLinkWorking = false;
                            checkMYAPI();
                        }
                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initializeViews(Handler handler) {
        handler.post(() -> {
            bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetTheme);
            bsView = LayoutInflater.from(MainActivity.this).inflate(R.layout.updater_layout, findViewById(R.id.main_bottomSheet_layout));
            textView_new_update_available = bsView.findViewById(R.id.textView_new_update_available);
            textView_update_btn = bsView.findViewById(R.id.textView_update_btn);
            textView_notNow_btn = bsView.findViewById(R.id.textView_notNow_btn);
            releaseDate_text = bsView.findViewById(R.id.releaseDate_text);
            Changelogs_text = bsView.findViewById(R.id.Changelogs_text);

            linear1 = bsView.findViewById(R.id.linear1);
            linear2 = bsView.findViewById(R.id.linear2);
            linear3 = bsView.findViewById(R.id.linear3);
        });
    }

    private void networkErrorCheckOldData(Handler handler) {

        //dramaPreferences.edit().putString("usingAppAllowed", null).apply();
        String appAllowed =  dramaPreferences.getString("usingAppAllowed", null);

        if (appAllowed != null && appAllowed.equalsIgnoreCase("no")) {
            bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetTheme);
            bsView = LayoutInflater.from(MainActivity.this).inflate(R.layout.updater_layout, findViewById(R.id.main_bottomSheet_layout));
            handler.post(() -> {
                custom_animationView = bsView.findViewById(R.id.custom_animationView);
                textView_new_update_available.setText(getString(R.string.app_is_under_maintenance));

                custom_animationView.setAnimation("maintenance.json");
                custom_animationView.setVisibility(View.VISIBLE);
                custom_animationView.playAnimation();

                textView_notNow_btn.setVisibility(View.GONE);
                linear3.setVisibility(View.VISIBLE);
                textView_new_update_available.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,10,60,60);
                textView_update_btn.setLayoutParams(layoutParams);

                textView_update_btn.setText(getString(R.string.okay));

                textView_update_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.exit(0);
                    }
                });

                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.setContentView(bsView);
                bottomSheetDialog.show();
            });
        }


    }

    private void checkUpdatesAndAppUsage(JSONObject jsonObject, Handler handler) {

        try {
            String appVersion = BuildConfig.VERSION_NAME;
            String latestVersion = jsonObject.getString("latestVersion");
            String appAllowed = jsonObject.getString("usingAppAllowed");

            dramaPreferences.edit().putString("usingAppAllowed", appAllowed).apply();

            String extractedAppVersion = appVersion.replaceAll("[^\\d]", "");
            double extractedAppVersionFloat = Double.parseDouble(extractedAppVersion);

            String extractedLatestVersion = latestVersion.replaceAll("[^\\d]", "");
            double extractedLatestVersionFloat = Double.parseDouble(extractedLatestVersion);

            if (extractedAppVersionFloat < extractedLatestVersionFloat && !BuildConfig.DEBUG) {
                String downloadUrl, updateCancelable, updateRelease, releaseNotes;
                downloadUrl = jsonObject.getString("download_url");
                updateCancelable = jsonObject.getString("update_cancelable");
                updateRelease = jsonObject.getString("update_release");
                releaseNotes = jsonObject.getString("releaseNotes");

                handler.post(() -> {
                    //UI Thread work here



                    linear1.setVisibility(View.VISIBLE);
                    linear2.setVisibility(View.VISIBLE);
                    linear3.setVisibility(View.VISIBLE);

                    textView_new_update_available.setText(getString(R.string.new_update_available));

                    if (updateCancelable.equalsIgnoreCase("no")) {
                        textView_notNow_btn.setVisibility(View.GONE);
                        bottomSheetDialog.setCancelable(false);
                    }
                    releaseDate_text.setText(updateRelease);
                    Changelogs_text.setText(releaseNotes);

                    textView_notNow_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.dismiss();
                        }
                    });

                    textView_update_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
                            defaultBrowser.setData(Uri.parse(downloadUrl));
                            startActivity(defaultBrowser);
                        }
                    });


                    //  bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetDialog.setContentView(bsView);
                    bottomSheetDialog.show();

                });

            }

            if (appAllowed.equalsIgnoreCase("no")) {
                handler.post(() -> {
                    custom_animationView = bsView.findViewById(R.id.custom_animationView);
                    textView_new_update_available.setText(getString(R.string.app_is_under_maintenance));

                    /*RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.
                    textView_new_update_available.setLayoutParams(layoutParams);*/

                    custom_animationView.setAnimation("maintenance.json");
                    custom_animationView.setVisibility(View.VISIBLE);
                    custom_animationView.playAnimation();

                    textView_notNow_btn.setVisibility(View.GONE);
                    linear3.setVisibility(View.VISIBLE);
                    textView_new_update_available.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0,10,60,60);
                    textView_update_btn.setLayoutParams(layoutParams);

                    textView_update_btn.setText(getString(R.string.okay));

                    textView_update_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.exit(0);
                        }
                    });

                    bottomSheetDialog.setCancelable(false);
                    bottomSheetDialog.setContentView(bsView);
                    bottomSheetDialog.show();
                });
            }


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press Back again to Exit!", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}