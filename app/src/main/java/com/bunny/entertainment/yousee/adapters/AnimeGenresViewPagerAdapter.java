package com.bunny.entertainment.yousee.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bunny.entertainment.yousee.fragments.AnimeGenresFragment;
import com.bunny.entertainment.yousee.models.anime.AnimeAllGenresModel;

import java.util.ArrayList;

public class AnimeGenresViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<ArrayList<AnimeAllGenresModel>> animeGenresViewPagerModelArrayList;

    public AnimeGenresViewPagerAdapter(FragmentActivity fragmentActivity, ArrayList<ArrayList<AnimeAllGenresModel>> animeGenresViewPagerModelArrayList) {
        super(fragmentActivity);
        this.animeGenresViewPagerModelArrayList = animeGenresViewPagerModelArrayList;
    }
    public AnimeGenresViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return new AnimeGenresFragment(animeGenresViewPagerModelArrayList, position);
    }

    @Override
    public int getItemCount() {
        // Since you only have one fragment
        return animeGenresViewPagerModelArrayList.size();
    }


}
