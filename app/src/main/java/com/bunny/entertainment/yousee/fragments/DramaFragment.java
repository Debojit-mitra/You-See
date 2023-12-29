package com.bunny.entertainment.yousee.fragments;

import static com.bunny.entertainment.yousee.utils.Constants.MY_DRAMALIST_URL;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.activities.GenresActivity;
import com.bunny.entertainment.yousee.activities.SearchActivity;
import com.bunny.entertainment.yousee.activities.ViewAllActivity;
import com.bunny.entertainment.yousee.adapters.TopAiringAdapterFragment;
import com.bunny.entertainment.yousee.adapters.ShowsAdapterFragment;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.utils.DataHolder;
import com.bunny.entertainment.yousee.utils.InternetUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class DramaFragment extends Fragment {

    String topAiring = MY_DRAMALIST_URL+"/shows/top_airing?page=1";
    String topShows = MY_DRAMALIST_URL+"/shows/top?page=1";
    String popularShows = MY_DRAMALIST_URL+"/shows/popular?page=1";
    String upcomingShows = MY_DRAMALIST_URL+"/shows/upcoming?page=1";
    String varietyShows = MY_DRAMALIST_URL+"/shows/variety?page=1";
    String topMovies = MY_DRAMALIST_URL+"/movies/top?page=1";
    String popularMovies = MY_DRAMALIST_URL+"/movies/popular?page=1";
    String newestMovies = MY_DRAMALIST_URL+"/movies/newest?page=1";
    String upcomingMovies = MY_DRAMALIST_URL+"/movies/upcoming?page=1";
    ArrayList<DramaListModel> topAiringDramaListModelArrayList;
    ArrayList<DramaListModel> topAiringArrayList;
    ArrayList<DramaListModel> topShowsDramaListModelArrayList;
    ArrayList<DramaListModel> topShowsArrayList;
    ArrayList<DramaListModel> popularShowsDramaListModelArrayList;
    ArrayList<DramaListModel> popularShowsArrayList;
    ArrayList<DramaListModel> upcomingShowsDramaListModelArrayList;
    ArrayList<DramaListModel> upcomingShowsArrayList;
    ArrayList<DramaListModel> varietyShowsDramaListModelArrayList;
    ArrayList<DramaListModel> varietyShowsArrayList;
    ArrayList<DramaListModel> topMoviesDramaListModelArrayList;
    ArrayList<DramaListModel> topMoviesArrayList;
    ArrayList<DramaListModel> popularMoviesDramaListModelArrayList;
    ArrayList<DramaListModel> popularMoviesArrayList;
    ArrayList<DramaListModel> newestMoviesDramaListModelArrayList;
    ArrayList<DramaListModel> newestMoviesArrayList;
    ArrayList<DramaListModel> upcomingMoviesDramaListModelArrayList;
    ArrayList<DramaListModel> upcomingMoviesArrayList;
    RecyclerView recyclerView_for_top_airing, recyclerView_for_top_shows, recyclerView_for_popular_shows, recyclerView_for_upcoming_shows,
            recyclerView_for_variety_shows, recyclerView_for_top_movies, recyclerView_for_popular_movies, recyclerView_for_newest_movies, recyclerView_for_upcoming_movies;
    TopAiringAdapterFragment topAiringAdapterFragment;
    ShowsAdapterFragment topShowsAdapterFragment;
    ShowsAdapterFragment popularShowsAdapterFragment;
    ShowsAdapterFragment upcomingShowsAdapterFragment;
    ShowsAdapterFragment varietyShowsAdapterFragment;
    ShowsAdapterFragment topMoviesShowsAdapterFragment;
    ShowsAdapterFragment popularMoviesAdapterFragment;
    ShowsAdapterFragment newestMoviesAdapterFragment;
    ShowsAdapterFragment upcomingMoviesAdapterFragment;
    SnapHelper snapHelper;
    Boolean isUserScrolling = false;
    Timer autoScrollTimer;
    int currentItem = 0;
    int randomNumber, randomNumber2;
    ProgressBar progressBar_top_airing, progressBar_top_shows, progressBar_popular_shows, progressBar_upcoming_shows, progressBar_variety_shows,
            progressBar_top_movies, progressBar_popular_movies, progressBar_newest_movies, progressBar_upcoming_movies;
    RelativeLayout view_all_top_airing_container, view_all_top_shows_container, view_all_popular_shows_container, view_all_upcoming_shows_container,
            view_all_variety_shows_container, view_all_top_movies_container, view_all_popular_movies_container, view_all_newest_movies_container,
            view_all_upcoming_movies_container;
    TextView topShows_textview, popularShows_textview, upcomingShows_textview, varietyShows_textview, topAiring_textview, topMovies_textview, popularMovies_textview,
            newestMovies_textview, upcomingMovies_textview;
    TextInputEditText edittext_search;
    TextInputLayout textview_search;
    ImageView genresThumbnail, calenderThumbnail;
    RelativeLayout genres_card, calender_card;
    Animation startAnimation;
    SwipeRefreshLayout swipe_refresh_layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drama, container, false);

        recyclerView_for_top_airing = view.findViewById(R.id.recyclerView_for_top_airing);
        progressBar_top_airing = view.findViewById(R.id.progressBar_top_airing);
        view_all_top_airing_container = view.findViewById(R.id.view_all_top_airing_container);
        topAiring_textview = view.findViewById(R.id.topAiring_textview);

        recyclerView_for_top_shows = view.findViewById(R.id.recyclerView_for_top_shows);
        view_all_top_shows_container = view.findViewById(R.id.view_all_top_shows_container);
        progressBar_top_shows = view.findViewById(R.id.progressBar_top_shows);
        topShows_textview = view.findViewById(R.id.topShows_textview);

        recyclerView_for_popular_shows = view.findViewById(R.id.recyclerView_for_popular_shows);
        view_all_popular_shows_container = view.findViewById(R.id.view_all_popular_shows_container);
        popularShows_textview = view.findViewById(R.id.popularShows_textview);
        progressBar_popular_shows = view.findViewById(R.id.progressBar_popular_shows);

        recyclerView_for_upcoming_shows = view.findViewById(R.id.recyclerView_for_upcoming_shows);
        view_all_upcoming_shows_container = view.findViewById(R.id.view_all_upcoming_shows_container);
        upcomingShows_textview = view.findViewById(R.id.upcomingShows_textview);
        progressBar_upcoming_shows = view.findViewById(R.id.progressBar_upcoming_shows);

        recyclerView_for_variety_shows = view.findViewById(R.id.recyclerView_for_variety_shows);
        view_all_variety_shows_container = view.findViewById(R.id.view_all_variety_shows_container);
        varietyShows_textview = view.findViewById(R.id.varietyShows_textview);
        progressBar_variety_shows = view.findViewById(R.id.progressBar_variety_shows);

        recyclerView_for_top_movies = view.findViewById(R.id.recyclerView_for_top_movies);
        view_all_top_movies_container = view.findViewById(R.id.view_all_top_movies_container);
        topMovies_textview = view.findViewById(R.id.topMovies_textview);
        progressBar_top_movies = view.findViewById(R.id.progressBar_top_movies);

        recyclerView_for_popular_movies = view.findViewById(R.id.recyclerView_for_popular_movies);
        view_all_popular_movies_container = view.findViewById(R.id.view_all_popular_movies_container);
        popularMovies_textview = view.findViewById(R.id.popularMovies_textview);
        progressBar_popular_movies = view.findViewById(R.id.progressBar_popular_movies);

        recyclerView_for_newest_movies = view.findViewById(R.id.recyclerView_for_newest_movies);
        view_all_newest_movies_container = view.findViewById(R.id.view_all_newest_movies_container);
        newestMovies_textview = view.findViewById(R.id.newestMovies_textview);
        progressBar_newest_movies = view.findViewById(R.id.progressBar_newest_movies);

        recyclerView_for_upcoming_movies = view.findViewById(R.id.recyclerView_for_upcoming_movies);
        view_all_upcoming_movies_container = view.findViewById(R.id.view_all_upcoming_movies_container);
        upcomingMovies_textview = view.findViewById(R.id.upcomingMovies_textview);
        progressBar_upcoming_movies = view.findViewById(R.id.progressBar_upcoming_movies);

        edittext_search = view.findViewById(R.id.edittext_search);
        textview_search = view.findViewById(R.id.textview_search);
        genresThumbnail = view.findViewById(R.id.genresThumbnail);
        calenderThumbnail = view.findViewById(R.id.calenderThumbnail);
        genres_card = view.findViewById(R.id.genres_card);
        calender_card = view.findViewById(R.id.calender_card);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);

        startAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right_long);

        snapHelper = new PagerSnapHelper();

        if (getActivity() != null) {
            if (InternetUtils.isInternetAvailable(getActivity())) {
                scrapTopAiring();
                scrapTopShows();
                scrapPopularShows();
                scrapUpcomingShows();
                scrapVarietyShows();
                scrapTopMovies();
                scrapPopularMovies();
                scrapNewestMovies();
                scrapUpcomingMovies();
            }
        }

        buttonsClickListener();

        swipeRefresh();

        return view;
    }



    private void swipeRefresh() {

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getActivity() != null){
                    if(InternetUtils.isInternetAvailable(getActivity())){
                        scrapTopAiring();
                        scrapTopShows();
                        scrapPopularShows();
                        scrapUpcomingShows();
                        scrapVarietyShows();
                        scrapTopMovies();
                        scrapPopularMovies();
                        scrapNewestMovies();
                        scrapUpcomingMovies();
                    }
                }
            }
        });


    }

    private void buttonsClickListener() {

        view_all_top_airing_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Top Airing");
                //DataHolder.setTopAiringDramaListModelArrayList(topAiringDramaListModelArrayList); //declared below
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_top_shows_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Top Shows");
                DataHolder.setTopShowsDramaListModelArrayList(topShowsDramaListModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_popular_shows_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Popular Shows");
                DataHolder.setPopularShowsDramaListModelArrayList(popularShowsDramaListModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_upcoming_shows_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Upcoming Shows");
                DataHolder.setUpcomingShowsDramaListModelArrayList(upcomingShowsDramaListModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_variety_shows_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Variety Shows");
                DataHolder.setVarietyShowsDramaListModelArrayList(varietyShowsDramaListModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_top_movies_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Top Movies");
                DataHolder.setTopMoviesDramaListModelArrayList(topMoviesDramaListModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_popular_movies_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Popular Movies");
                DataHolder.setPopularMoviesDramaListModelArrayList(popularMoviesDramaListModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_newest_movies_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Newest Movies");
                DataHolder.setNewestMoviesDramaListModelArrayList(newestMoviesDramaListModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_upcoming_movies_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Upcoming Movies");
                DataHolder.setUpcomingMoviesDramaListModelArrayList(upcomingMoviesDramaListModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        edittext_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("whereFrom", "DramaFragment");
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        genres_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GenresActivity.class);
                intent.putExtra("whereFrom", "DramaFragment");
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        calender_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "Coming sunn!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void scrapUpcomingMovies() {

        upcomingMoviesDramaListModelArrayList = new ArrayList<>();
        upcomingMoviesArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                Document doc = Jsoup.connect(upcomingMovies).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                int dataNumber = 0;

                for (Element e : box.select("div.box-body")) {

                    Elements E1 = e.select("div.item");
                    String thumbImage = E1.select("img").attr("data-src");
                    String myDramaListDramaId = E1.select("a").attr("href");

                    Elements E2 = e.select("h6");
                    String dramaName = E2.select("a").text();

                    Elements E3 = e.select("span.text-muted");
                    String countrySpecifiedDramaAndEpisodes = E3.text();

                    Elements E4 = e.select("p").select("span");
                    String dramaRating = E4.text();

                    String dramaCountry = countrySpecifiedDramaAndEpisodes.substring(0, countrySpecifiedDramaAndEpisodes.indexOf("-")).trim();
                    //String dramaReleasedYear = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf("-") + 1, countrySpecifiedDramaAndEpisodes.indexOf(",")).trim();
                    //String totalEpisodes = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf(",") + 1, countrySpecifiedDramaAndEpisodes.indexOf("episode")).trim();

                    dataNumber++;
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, "", dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    upcomingMoviesDramaListModelArrayList.add(dramaListModel);
                    if (upcomingMoviesArrayList.size() <= 9) {
                        upcomingMoviesArrayList.add(dramaListModel);
                    }
                }

                handler.post(this::showUpcomingMovies);

            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {

                //UI Thread work here

            });
        });
        executor.shutdown();
    }

    private void showUpcomingMovies() {
        if (getActivity() != null) {
            upcomingMoviesAdapterFragment = new ShowsAdapterFragment(upcomingMoviesArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_upcoming_movies.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_upcoming_movies.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_upcoming_movies.setAdapter(upcomingMoviesAdapterFragment);
            view_all_upcoming_movies_container.setVisibility(View.VISIBLE);
            upcomingMovies_textview.setVisibility(View.VISIBLE);
            progressBar_upcoming_movies.setVisibility(View.GONE);
        }
    }


    private void scrapNewestMovies() {

        newestMoviesDramaListModelArrayList = new ArrayList<>();
        newestMoviesArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                Document doc = Jsoup.connect(newestMovies).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                int dataNumber = 0;

                for (Element e : box.select("div.box-body")) {

                    Elements E1 = e.select("div.item");
                    String thumbImage = E1.select("img").attr("data-src");
                    String myDramaListDramaId = E1.select("a").attr("href");

                    Elements E2 = e.select("h6");
                    String dramaName = E2.select("a").text();

                    Elements E3 = e.select("span.text-muted");
                    String countrySpecifiedDramaAndEpisodes = E3.text();

                    Elements E4 = e.select("p").select("span");
                    String dramaRating = E4.text();

                    String dramaCountry = countrySpecifiedDramaAndEpisodes.substring(0, countrySpecifiedDramaAndEpisodes.indexOf("-")).trim();
                    //String dramaReleasedYear = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf("-") + 1, countrySpecifiedDramaAndEpisodes.indexOf(",")).trim();
                    //String totalEpisodes = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf(",") + 1, countrySpecifiedDramaAndEpisodes.indexOf("episode")).trim();

                    dataNumber++;
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, "", dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    newestMoviesDramaListModelArrayList.add(dramaListModel);
                    if (newestMoviesArrayList.size() <= 9) {
                        newestMoviesArrayList.add(dramaListModel);
                    }

                }

                //  Log.e("e", String.valueOf(topAiringArrayList.size()));
                handler.post(this::showNewestMovies);



            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {

                //UI Thread work here

            });
        });
        executor.shutdown();

    }

    private void showNewestMovies() {
        if (getActivity() != null) {
            newestMoviesAdapterFragment = new ShowsAdapterFragment(newestMoviesArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_newest_movies.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_newest_movies.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_newest_movies.setAdapter(newestMoviesAdapterFragment);
            view_all_newest_movies_container.setVisibility(View.VISIBLE);
            newestMovies_textview.setVisibility(View.VISIBLE);
            progressBar_newest_movies.setVisibility(View.GONE);
        }
    }

    private void scrapPopularMovies() {

        popularMoviesDramaListModelArrayList = new ArrayList<>();
        popularMoviesArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                Document doc = Jsoup.connect(popularMovies).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                int dataNumber = 0;

                for (Element e : box.select("div.box-body")) {

                    Elements E1 = e.select("div.item");
                    String thumbImage = E1.select("img").attr("data-src");
                    String myDramaListDramaId = E1.select("a").attr("href");

                    Elements E2 = e.select("h6");
                    String dramaName = E2.select("a").text();

                    Elements E3 = e.select("span.text-muted");
                    String countrySpecifiedDramaAndEpisodes = E3.text();

                    Elements E4 = e.select("p").select("span");
                    String dramaRating = E4.text();

                    String dramaCountry = countrySpecifiedDramaAndEpisodes.substring(0, countrySpecifiedDramaAndEpisodes.indexOf("-")).trim();
                    //String dramaReleasedYear = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf("-") + 1, countrySpecifiedDramaAndEpisodes.indexOf(",")).trim();
                    //String totalEpisodes = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf(",") + 1, countrySpecifiedDramaAndEpisodes.indexOf("episode")).trim();

                    dataNumber++;
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, "", dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    popularMoviesDramaListModelArrayList.add(dramaListModel);
                    if (popularMoviesArrayList.size() <= 9) {
                        popularMoviesArrayList.add(dramaListModel);
                    }

                }

                //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                handler.post(this::showPopularMovies);



            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {

                //UI Thread work here

            });
        });
        executor.shutdown();
    }

    private void showPopularMovies() {
        if (getActivity() != null) {
            popularMoviesAdapterFragment = new ShowsAdapterFragment(popularMoviesArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_popular_movies.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_popular_movies.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_popular_movies.setAdapter(popularMoviesAdapterFragment);
            view_all_popular_movies_container.setVisibility(View.VISIBLE);
            popularMovies_textview.setVisibility(View.VISIBLE);
            progressBar_popular_movies.setVisibility(View.GONE);
        }
    }

    private void scrapTopMovies() {
        topMoviesDramaListModelArrayList = new ArrayList<>();
        topMoviesArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                Document doc = Jsoup.connect(topMovies).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                int dataNumber = 0;

                for (Element e : box.select("div.box-body")) {

                    Elements E1 = e.select("div.item");
                    String thumbImage = E1.select("img").attr("data-src");
                    String myDramaListDramaId = E1.select("a").attr("href");

                    Elements E2 = e.select("h6");
                    String dramaName = E2.select("a").text();

                    Elements E3 = e.select("span.text-muted");
                    String countrySpecifiedDramaAndEpisodes = E3.text();

                    Elements E4 = e.select("p").select("span");
                    String dramaRating = E4.text();

                    String dramaCountry = countrySpecifiedDramaAndEpisodes.substring(0, countrySpecifiedDramaAndEpisodes.indexOf("-")).trim();
                    //String dramaReleasedYear = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf("-") + 1, countrySpecifiedDramaAndEpisodes.indexOf(",")).trim();
                    //String totalEpisodes = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf(",") + 1, countrySpecifiedDramaAndEpisodes.indexOf("episode")).trim();

                    dataNumber++;
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, "", dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    topMoviesDramaListModelArrayList.add(dramaListModel);
                    if (topMoviesArrayList.size() <= 9) {
                        topMoviesArrayList.add(dramaListModel);
                    }

                }

                //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                //UI Thread work here
                handler.post(this::showTopMovies);


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {

                //UI Thread work here

            });
        });
        executor.shutdown();
    }

    private void showTopMovies() {
        if (getActivity() != null) {
            topMoviesShowsAdapterFragment = new ShowsAdapterFragment(topMoviesArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_top_movies.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_top_movies.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_top_movies.setAdapter(topMoviesShowsAdapterFragment);
            view_all_top_movies_container.setVisibility(View.VISIBLE);
            topMovies_textview.setVisibility(View.VISIBLE);
            progressBar_top_movies.setVisibility(View.GONE);
        }
    }

    private void scrapVarietyShows() {

        varietyShowsDramaListModelArrayList = new ArrayList<>();
        varietyShowsArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                Document doc = Jsoup.connect(varietyShows).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                int dataNumber = 0;

                for (Element e : box.select("div.box-body")) {

                    Elements E1 = e.select("div.item");
                    String thumbImage = E1.select("img").attr("data-src");
                    String myDramaListDramaId = E1.select("a").attr("href");

                    Elements E2 = e.select("h6");
                    String dramaName = E2.select("a").text();

                    Elements E3 = e.select("span.text-muted");
                    String countrySpecifiedDramaAndEpisodes = E3.text();

                    Elements E4 = e.select("p").select("span");
                    String dramaRating = E4.text();

                    String dramaCountry = countrySpecifiedDramaAndEpisodes.substring(0, countrySpecifiedDramaAndEpisodes.indexOf("-")).trim();
                    String dramaReleasedYear = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf("-") + 1, countrySpecifiedDramaAndEpisodes.indexOf(",")).trim();
                    String totalEpisodes = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf(",") + 1, countrySpecifiedDramaAndEpisodes.indexOf("episode")).trim();

                    dataNumber++;
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, totalEpisodes, dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    varietyShowsDramaListModelArrayList.add(dramaListModel);
                    if (varietyShowsArrayList.size() <= 9) {
                        varietyShowsArrayList.add(dramaListModel);
                    }

                }

                //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                //UI Thread work here
                handler.post(this::showVarietyShows);


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {

                //UI Thread work here

            });
        });
        executor.shutdown();
    }

    private void showVarietyShows() {
        if (getActivity() != null) {
            varietyShowsAdapterFragment = new ShowsAdapterFragment(varietyShowsArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_variety_shows.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_variety_shows.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_variety_shows.setAdapter(varietyShowsAdapterFragment);
            view_all_variety_shows_container.setVisibility(View.VISIBLE);
            varietyShows_textview.setVisibility(View.VISIBLE);
            progressBar_variety_shows.setVisibility(View.GONE);
        }
    }

    private void scrapUpcomingShows() {

        upcomingShowsDramaListModelArrayList = new ArrayList<>();
        upcomingShowsArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                Document doc = Jsoup.connect(upcomingShows).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                int dataNumber = 0;
                for (Element e : box.select("div.box-body")) {

                    Elements E1 = e.select("div.item");
                    String thumbImage = E1.select("img").attr("data-src");
                    String myDramaListDramaId = E1.select("a").attr("href");

                    Elements E2 = e.select("h6");
                    String dramaName = E2.select("a").text();

                    Elements E3 = e.select("span.text-muted");
                    String countrySpecifiedDramaAndEpisodes = E3.text();

                    Elements E4 = e.select("p").select("span");
                    String dramaRating = E4.text();

                    String dramaCountry = "", dramaReleasedYear = "", totalEpisodes = "";

                    try {

                        dramaCountry = countrySpecifiedDramaAndEpisodes.substring(0, countrySpecifiedDramaAndEpisodes.indexOf("-")).trim();
                        dramaReleasedYear = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf("-") + 1, countrySpecifiedDramaAndEpisodes.indexOf(",")).trim();
                        totalEpisodes = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf(",") + 1, countrySpecifiedDramaAndEpisodes.indexOf("episode")).trim();

                    } catch (Exception exception) {
                        Log.e("ExceptionInUpcoming", exception.getMessage());
                    }

                    dataNumber++;
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, totalEpisodes, dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    upcomingShowsDramaListModelArrayList.add(dramaListModel);
                    if (upcomingShowsArrayList.size() <= 9) {
                        upcomingShowsArrayList.add(dramaListModel);
                    }

                }

                //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                //UI Thread work here
                handler.post(this::showUpcomingShows);


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });
        executor.shutdown();
    }

    private void showUpcomingShows() {
        if (getActivity() != null) {
            upcomingShowsAdapterFragment = new ShowsAdapterFragment(upcomingShowsArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_upcoming_shows.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_upcoming_shows.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_upcoming_shows.setAdapter(upcomingShowsAdapterFragment);
            view_all_upcoming_shows_container.setVisibility(View.VISIBLE);
            upcomingShows_textview.setVisibility(View.VISIBLE);
            progressBar_upcoming_shows.setVisibility(View.GONE);
        }

    }

    private void scrapPopularShows() {

        popularShowsDramaListModelArrayList = new ArrayList<>();
        popularShowsArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                Document doc = Jsoup.connect(popularShows).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                int dataNumber = 0;
                for (Element e : box.select("div.box-body")) {

                    Elements E1 = e.select("div.item");
                    String thumbImage = E1.select("img").attr("data-src");
                    String myDramaListDramaId = E1.select("a").attr("href");

                    Elements E2 = e.select("h6");
                    String dramaName = E2.select("a").text();

                    Elements E3 = e.select("span.text-muted");
                    String countrySpecifiedDramaAndEpisodes = E3.text();

                    Elements E4 = e.select("p").select("span");
                    String dramaRating = E4.text();

                    String dramaCountry = "", dramaReleasedYear = "", totalEpisodes = "";

                    try {

                        dramaCountry = countrySpecifiedDramaAndEpisodes.substring(0, countrySpecifiedDramaAndEpisodes.indexOf("-")).trim();
                        dramaReleasedYear = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf("-") + 1, countrySpecifiedDramaAndEpisodes.indexOf(",")).trim();
                        totalEpisodes = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf(",") + 1, countrySpecifiedDramaAndEpisodes.indexOf("episode")).trim();

                    } catch (Exception exception) {
                        Log.e("ExceptionInPopular", exception.getMessage());
                    }

                    dataNumber++;
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, totalEpisodes, dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    popularShowsDramaListModelArrayList.add(dramaListModel);
                    if (popularShowsArrayList.size() <= 9) {
                        popularShowsArrayList.add(dramaListModel);
                    }

                }

                //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                //UI Thread work here
                handler.post(this::showPopularShows);


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });
        executor.shutdown();
    }

    private void showPopularShows() {

        if (getActivity() != null) {
            popularShowsAdapterFragment = new ShowsAdapterFragment(popularShowsArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_popular_shows.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_popular_shows.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_popular_shows.setAdapter(popularShowsAdapterFragment);
            view_all_popular_shows_container.setVisibility(View.VISIBLE);
            popularShows_textview.setVisibility(View.VISIBLE);
            progressBar_popular_shows.setVisibility(View.GONE);

        }

    }

    private void scrapTopShows() {
        topShowsDramaListModelArrayList = new ArrayList<>();
        topShowsArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                Document doc = Jsoup.connect(topShows).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                int dataNumber = 0;
                for (Element e : box.select("div.box-body")) {

                    Elements E1 = e.select("div.item");
                    String thumbImage = E1.select("img").attr("data-src");
                    String myDramaListDramaId = E1.select("a").attr("href");

                    Elements E2 = e.select("h6");
                    String dramaName = E2.select("a").text();

                    Elements E3 = e.select("span.text-muted");
                    String countrySpecifiedDramaAndEpisodes = E3.text();

                    Elements E4 = e.select("p").select("span");
                    String dramaRating = E4.text();


                    String dramaCountry = "", dramaReleasedYear = "", totalEpisodes = "";

                    try {

                        dramaCountry = countrySpecifiedDramaAndEpisodes.substring(0, countrySpecifiedDramaAndEpisodes.indexOf("-")).trim();
                        dramaReleasedYear = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf("-") + 1, countrySpecifiedDramaAndEpisodes.indexOf(",")).trim();
                        totalEpisodes = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf(",") + 1, countrySpecifiedDramaAndEpisodes.indexOf("episode")).trim();

                    } catch (Exception exception) {
                        Log.e("ExceptionInTopShows", exception.getMessage());
                    }

                    dataNumber++;
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, totalEpisodes, dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    topShowsDramaListModelArrayList.add(dramaListModel);
                    if (topShowsArrayList.size() <= 9) {
                        topShowsArrayList.add(dramaListModel);
                    }

                }

                //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                //UI Thread work here
                handler.post(this::showTopShows);


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });
        executor.shutdown();
    }

    private void showTopShows() {

        if (getActivity() != null) {
            topShowsAdapterFragment = new ShowsAdapterFragment(topShowsArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_top_shows.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_top_shows.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_top_shows.setAdapter(topShowsAdapterFragment);
            view_all_top_shows_container.setVisibility(View.VISIBLE);
            topShows_textview.setVisibility(View.VISIBLE);
            progressBar_top_shows.setVisibility(View.GONE);
        }
    }


    private void scrapTopAiring() {

        topAiringDramaListModelArrayList = new ArrayList<>();
        topAiringArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                Document doc = Jsoup.connect(topAiring).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                int dataNumber = 0;
                for (Element e : box.select("div.box-body")) {

                    Elements E1 = e.select("div.item");
                    String thumbImage = E1.select("img").attr("data-src");
                    String myDramaListDramaId = E1.select("a").attr("href");

                    Elements E2 = e.select("h6");
                    String dramaName = E2.select("a").text();

                    Elements E3 = e.select("span.text-muted");
                    String countrySpecifiedDramaAndEpisodes = E3.text();

                    Elements E4 = e.select("p").select("span");
                    String dramaRating = E4.text();

                    String dramaCountry = countrySpecifiedDramaAndEpisodes.substring(0, countrySpecifiedDramaAndEpisodes.indexOf("-")).trim();
                    String dramaReleasedYear = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf("-") + 1, countrySpecifiedDramaAndEpisodes.indexOf(",")).trim();
                    String totalEpisodes = countrySpecifiedDramaAndEpisodes.substring(countrySpecifiedDramaAndEpisodes.indexOf(",") + 1, countrySpecifiedDramaAndEpisodes.indexOf("episode")).trim();

                    dataNumber++;
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, totalEpisodes, dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    topAiringDramaListModelArrayList.add(dramaListModel);
                    if (topAiringArrayList.size() <= 9) {
                        topAiringArrayList.add(dramaListModel);
                    }

                }

                //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                //UI Thread work here
                handler.post(this::showTopAiring);


            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "My Drama List Error! wait until website is fixed.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });
        executor.shutdown();
    }

    private void showTopAiring() {

        if (getActivity() != null) {
            topAiringAdapterFragment = new TopAiringAdapterFragment(topAiringArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_top_airing.setLayoutAnimation(controller);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
            recyclerView_for_top_airing.setLayoutManager(gridLayoutManager);
            snapHelper.attachToRecyclerView(recyclerView_for_top_airing);
            topAiring_textview.setVisibility(View.VISIBLE);
            progressBar_top_airing.setVisibility(View.GONE);
            recyclerView_for_top_airing.setAdapter(topAiringAdapterFragment);
            view_all_top_airing_container.setVisibility(View.VISIBLE);

            //for genres random image using top airing images
            DataHolder.setTopAiringDramaListModelArrayList(topAiringDramaListModelArrayList);

            startAutoScrollTimer();

            recyclerView_for_top_airing.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        isUserScrolling = true;
                        swipe_refresh_layout.setEnabled(false);
                        stopAutoScroll();
                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isUserScrolling = false;
                        swipe_refresh_layout.setEnabled(true);
                        if (recyclerView.getLayoutManager() != null) {
                            currentItem = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        }
                        startAutoScrollTimer();
                    }
                }
            });

            Random random = new Random();
            randomNumber = random.nextInt(10);
            randomNumber2 = random.nextInt(10);
            try {

                Glide.with(genresThumbnail.getContext())
                        .load(topAiringDramaListModelArrayList.get(randomNumber).getDramaThumbnail())
                        .transform(new BlurTransformation(8, 2))
                        .into(genresThumbnail);
                Glide.with(calenderThumbnail.getContext())
                        .load(topAiringDramaListModelArrayList.get(randomNumber2).getDramaThumbnail())
                        .transform(new BlurTransformation(8, 2))
                        .into(calenderThumbnail);

            }catch (Exception e){
                e.printStackTrace();
            }

            textview_search.setVisibility(View.VISIBLE);
            textview_search.startAnimation(startAnimation);
            genres_card.setVisibility(View.VISIBLE);
            genres_card.startAnimation(startAnimation);
            calender_card.setVisibility(View.VISIBLE);
            calender_card.startAnimation(startAnimation);

            swipe_refresh_layout.setRefreshing(false);
        }
    }

    private void startAutoScrollTimer() {
        if (!isUserScrolling && autoScrollTimer == null) {
            autoScrollTimer = new Timer();
            autoScrollTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scrollToNextItem();
                            }
                        });
                    }
                }
            }, 4000, 4000);
        }
    }

    public void scrollToNextItem() {
        if (topAiringAdapterFragment != null && topAiringAdapterFragment.getItemCount() > 0) {
            int nextItem = (currentItem + 1) % topAiringAdapterFragment.getItemCount();
            recyclerView_for_top_airing.smoothScrollToPosition(nextItem);
            currentItem = nextItem;
        }
    }

    public void stopAutoScroll() {
        if (autoScrollTimer != null) {
            autoScrollTimer.cancel();
            autoScrollTimer = null;
        }
    }

    @Override
    public void onPause() {
        stopAutoScroll();
        super.onPause();
    }

    @Override
    public void onStop() {
        stopAutoScroll();
        super.onStop();
    }

    @Override
    public void onResume() {
        startAutoScrollTimer();
        super.onResume();
    }
}