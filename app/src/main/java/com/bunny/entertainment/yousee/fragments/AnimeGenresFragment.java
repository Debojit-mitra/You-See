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
import android.widget.ProgressBar;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.AnimeGenresAdapter;
import com.bunny.entertainment.yousee.models.anime.AnimeAllGenresModel;

import java.util.ArrayList;

public class AnimeGenresFragment extends Fragment {

    RecyclerView recyclerView_for_genres;
    AnimeGenresAdapter animeGenresAdapter;
    ArrayList<ArrayList<AnimeAllGenresModel>> animeGenresViewPagerModelArrayList;
    int position;

    public AnimeGenresFragment(ArrayList<ArrayList<AnimeAllGenresModel>> animeGenresViewPagerModelArrayList, int position) {
        this.animeGenresViewPagerModelArrayList = animeGenresViewPagerModelArrayList;
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
        View view = inflater.inflate(R.layout.fragment_anime_genres, container, false);

        recyclerView_for_genres = view.findViewById(R.id.recyclerView_for_genres);

        animeGenresAdapter = new AnimeGenresAdapter(animeGenresViewPagerModelArrayList.get(position), requireContext());
        Animation animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.recyclerviewscroll_top_bottom_fast); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_genres.setLayoutAnimation(controller);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView_for_genres.setLayoutManager(gridLayoutManager);
        recyclerView_for_genres.setAdapter(animeGenresAdapter);
        toggleProgressBar();
        recyclerView_for_genres.setVisibility(View.VISIBLE);


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
        if (context instanceof ProgressBarListener) {
            progressBarListener = (ProgressBarListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement ProgressBarListener");
        }
    }


}