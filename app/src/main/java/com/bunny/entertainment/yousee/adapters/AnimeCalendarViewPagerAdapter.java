package com.bunny.entertainment.yousee.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bunny.entertainment.yousee.fragments.AnimeCalendarFragment;
import com.bunny.entertainment.yousee.fragments.AnimeGenresFragment;
import com.bunny.entertainment.yousee.models.anime.AnimeAllGenresModel;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;

import java.util.ArrayList;

public class AnimeCalendarViewPagerAdapter extends FragmentStateAdapter {


    private ArrayList<ArrayList<AnimeListModel>> animeCalendarViewPagerModelArrayList;

    public AnimeCalendarViewPagerAdapter(FragmentActivity fragmentActivity, ArrayList<ArrayList<AnimeListModel>> animeCalendarViewPagerModelArrayList) {
        super(fragmentActivity);
        this.animeCalendarViewPagerModelArrayList = animeCalendarViewPagerModelArrayList;
    }
    public AnimeCalendarViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return new AnimeCalendarFragment(animeCalendarViewPagerModelArrayList, position);
    }

    @Override
    public int getItemCount() {
        // Since you only have one fragment
        return animeCalendarViewPagerModelArrayList.size();
    }

}
