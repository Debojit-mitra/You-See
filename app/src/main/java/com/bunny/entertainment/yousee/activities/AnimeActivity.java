package com.bunny.entertainment.yousee.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.fragments.InfoFragment_3;
import com.bunny.entertainment.yousee.fragments.WatchFragment_3;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class AnimeActivity extends AppCompatActivity {

    String malId;
    FragmentTransaction fragmentTransaction;
    FrameLayout main_frame_layout;
    private final Fragment mInfoFragment = new InfoFragment_3();
    private final Fragment mWatchFragment = new WatchFragment_3();
    Fragment fragment = null;
    AnimatedBottomBar animatedBottomBar;
    SharedPreferences dramaPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime);

        Intent intent = getIntent();
        if(intent != null && Intent.ACTION_VIEW.equals(intent.getAction())){
            Uri data = intent.getData();
            if(data != null){
                int segmentsCount = data.getPathSegments().size();
                malId = data.getPathSegments().get(segmentsCount - 2);
            }
        }else {
            malId = getIntent().getStringExtra("malId");
        }

        Log.e("malId(AnimeActivity)", ": "+malId);

        animatedBottomBar = findViewById(R.id.animatedBottomBar);
        main_frame_layout = findViewById(R.id.main_frame_layout);

        Bundle bundle = new Bundle();
        bundle.putString("malId", malId);
        mInfoFragment.setArguments(bundle);

        dramaPreferences = getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        String fragmentSelectedLast = dramaPreferences.getString("/anime/"+malId+"FragmentSelectedLast", null);

        if(fragmentSelectedLast == null){
            fragmentTransaction.replace(R.id.main_frame_layout, mInfoFragment);
        } else {
            if(fragmentSelectedLast.contains("mInfoFragment")){
                fragmentTransaction.replace(R.id.main_frame_layout, mInfoFragment);
            } else if (fragmentSelectedLast.contains("mWatchFragment")) {
                animatedBottomBar.selectTabAt(1, true);
                fragmentTransaction.replace(R.id.main_frame_layout, mInfoFragment);
                fragmentTransaction.hide(mInfoFragment);
                fragmentTransaction.add(R.id.main_frame_layout, mWatchFragment);
            }
        }
        fragmentTransaction.commit();

        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.zoom_out, R.anim.nothing_fast);

                if(tab1.getId() == R.id.info){
                    dramaPreferences.edit().putString("/anime/"+malId+"FragmentSelectedLast", "mInfoFragment").apply();
                    fragmentTransaction.show(mInfoFragment);
                    fragmentTransaction.hide(mWatchFragment);

                }else if (tab1.getId() == R.id.watch) {
                    dramaPreferences.edit().putString("/anime/"+malId+"FragmentSelectedLast", "mWatchFragment").apply();
                    fragment = mWatchFragment;
                    if (getSupportFragmentManager().getFragments().contains(mWatchFragment)) {
                        fragmentTransaction.show(mWatchFragment);
                    } else {
                        fragmentTransaction.add(R.id.main_frame_layout, fragment);
                        fragmentTransaction.show(fragment);
                    }
                    fragmentTransaction.hide(mInfoFragment);
                }
                fragmentTransaction.commit();

            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}