package com.bunny.entertainment.yousee.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.AnimeAdapterFragment;
import com.bunny.entertainment.yousee.adapters.AnimeGenresAdapter;
import com.bunny.entertainment.yousee.models.anime.AnimeAllGenresModel;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;

import java.util.ArrayList;

public class AnimeCalendarFragment extends Fragment {

    RecyclerView recyclerView_for_calendar;
    AnimeAdapterFragment animeAdapterFragment;
    ArrayList<ArrayList<AnimeListModel>> animeCalendarViewPagerModelArrayList;
    int position;

    public AnimeCalendarFragment(ArrayList<ArrayList<AnimeListModel>> animeCalendarViewPagerModelArrayList, int position) {
        this.animeCalendarViewPagerModelArrayList = animeCalendarViewPagerModelArrayList;
        this.position = position;
    }
    public interface ProgressBarListener {
        void showProgressBar(boolean show);
    }
    private ProgressBarListener progressBarListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anime_calendar, container, false);

        recyclerView_for_calendar = view.findViewById(R.id.recyclerView_for_calendar);

        animeAdapterFragment = new AnimeAdapterFragment(animeCalendarViewPagerModelArrayList.get(position), requireContext());
        Animation animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.recyclerviewscroll_left_right_fast); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_calendar.setLayoutAnimation(controller);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView_for_calendar.setLayoutManager(gridLayoutManager);
        recyclerView_for_calendar.setHasFixedSize(true);
        recyclerView_for_calendar.setAdapter(animeAdapterFragment);
        toggleProgressBar();
        recyclerView_for_calendar.setVisibility(View.VISIBLE);


        return view;
    }
    private void toggleProgressBar() {
        if (progressBarListener != null) {
            progressBarListener.showProgressBar(false);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AnimeCalendarFragment.ProgressBarListener) {
            progressBarListener = (ProgressBarListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement ProgressBarListener");
        }
    }
}