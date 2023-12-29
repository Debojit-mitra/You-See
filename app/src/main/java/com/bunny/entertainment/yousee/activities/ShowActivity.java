package com.bunny.entertainment.yousee.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.fragments.InfoFragment;
import com.bunny.entertainment.yousee.fragments.WatchFragment;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class ShowActivity extends AppCompatActivity {

    String dramaId;
    FragmentTransaction fragmentTransaction;
    FrameLayout main_frame_layout;
    private final Fragment mInfoFragment = new InfoFragment();
    private final Fragment mWatchFragment = new WatchFragment();
    Fragment fragment = null;
    AnimatedBottomBar animatedBottomBar;
    SharedPreferences dramaPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        dramaId = getIntent().getStringExtra("dramaId");


        animatedBottomBar = findViewById(R.id.animatedBottomBar);
        main_frame_layout = findViewById(R.id.main_frame_layout);

        Bundle bundle = new Bundle();
        bundle.putString("dramaId",dramaId);
        mInfoFragment.setArguments(bundle);

        dramaPreferences = getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        String fragmentSelectedLast = dramaPreferences.getString(dramaId+"FragmentSelectedLast", null);

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
                    dramaPreferences.edit().putString(dramaId+"FragmentSelectedLast", "mInfoFragment").apply();
                    fragmentTransaction.show(mInfoFragment);
                    fragmentTransaction.hide(mWatchFragment);

                }else if (tab1.getId() == R.id.watch) {
                    dramaPreferences.edit().putString(dramaId+"FragmentSelectedLast", "mWatchFragment").apply();
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