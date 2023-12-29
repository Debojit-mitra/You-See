package com.bunny.entertainment.yousee.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.fragments.InfoFragment_2;
import com.bunny.entertainment.yousee.fragments.WatchFragment_2;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class ShowsActivity_2 extends AppCompatActivity {

    String dramaId, mediaType;
    FragmentTransaction fragmentTransaction;
    FrameLayout main_frame_layout;
    private final Fragment mInfoFragment = new InfoFragment_2();
    private final Fragment mWatchFragment = new WatchFragment_2();
    Fragment fragment = null;
    AnimatedBottomBar animatedBottomBar;
    SharedPreferences dramaPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows2);

        dramaId = getIntent().getStringExtra("dramaId");
        mediaType = getIntent().getStringExtra("mediaType");


        animatedBottomBar = findViewById(R.id.animatedBottomBar);
        main_frame_layout = findViewById(R.id.main_frame_layout);

        Bundle bundle = new Bundle();
        bundle.putString("dramaId",dramaId);
        bundle.putString("mediaType", mediaType);
        mInfoFragment.setArguments(bundle);

        dramaPreferences = getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        String fragmentSelectedLast = dramaPreferences.getString(dramaId+"FragmentSelectedLastShowsActivity_2", null);

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
                    dramaPreferences.edit().putString(dramaId+"FragmentSelectedLastShowsActivity_2", "mInfoFragment").apply();
                    fragmentTransaction.show(mInfoFragment);
                    fragmentTransaction.hide(mWatchFragment);

                }else if (tab1.getId() == R.id.watch) {
                    dramaPreferences.edit().putString(dramaId+"FragmentSelectedLastShowsActivity_2", "mWatchFragment").apply();
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