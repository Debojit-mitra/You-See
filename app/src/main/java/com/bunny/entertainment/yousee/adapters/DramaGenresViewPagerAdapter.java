package com.bunny.entertainment.yousee.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bunny.entertainment.yousee.fragments.AnimeGenresFragment;
import com.bunny.entertainment.yousee.fragments.DramaGenresFragment;
import com.bunny.entertainment.yousee.models.GenresModel;
import com.bunny.entertainment.yousee.models.anime.AnimeAllGenresModel;

import java.util.ArrayList;

public class DramaGenresViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<ArrayList<GenresModel>> dramaGenresViewPagerModelArrayList;

    public DramaGenresViewPagerAdapter(FragmentActivity fragmentActivity, ArrayList<ArrayList<GenresModel>> dramaGenresViewPagerModelArrayList) {
        super(fragmentActivity);
        this.dramaGenresViewPagerModelArrayList = dramaGenresViewPagerModelArrayList;
    }
    public DramaGenresViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return new DramaGenresFragment(dramaGenresViewPagerModelArrayList, position);
    }

    @Override
    public int getItemCount() {
        // Since you only have one fragment
        return dramaGenresViewPagerModelArrayList.size();
    }


}
