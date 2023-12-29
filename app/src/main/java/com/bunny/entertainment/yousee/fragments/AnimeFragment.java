package com.bunny.entertainment.yousee.fragments;

import static com.bunny.entertainment.yousee.utils.Constants.MYANIMELIST_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.activities.CalenderActivity;
import com.bunny.entertainment.yousee.activities.GenresActivity;
import com.bunny.entertainment.yousee.activities.SearchActivity;
import com.bunny.entertainment.yousee.activities.ViewAllActivity;
import com.bunny.entertainment.yousee.adapters.AnimeAdapterFragment;
import com.bunny.entertainment.yousee.adapters.TopAiringAnimeAdapterFragment;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;
import com.bunny.entertainment.yousee.utils.Constants;
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


public class AnimeFragment extends Fragment {

    String topAnimeTvSeries = MYANIMELIST_URL + "/topanime.php?type=tv&limit=0";
    String topAnimeMovies = MYANIMELIST_URL + "/topanime.php?type=movie&limit=0";
    String popularAnime = MYANIMELIST_URL + "/topanime.php?type=bypopularity&limit=0";
    String topUpcomingAnime = MYANIMELIST_URL + "/topanime.php?type=upcoming&limit=0";
    ArrayList<AnimeListModel> topAiringAnimeThisSeasonArrayList;
    ArrayList<AnimeListModel> topAiringAnimeThisSeasonModelArrayList;
    ArrayList<AnimeListModel> topAiringPreviousSeasonArrayList;
    ArrayList<AnimeListModel> topAiringPreviousSeasonModelArrayList;
    ArrayList<AnimeListModel> topAiringUpcomingSeasonArrayList;
    ArrayList<AnimeListModel> topAiringUpcomingSeasonModelArrayList;
    ArrayList<AnimeListModel> topAnimeTvSeriesArrayList;
    ArrayList<AnimeListModel> topAnimeTvSeriesModelArrayList;
    ArrayList<AnimeListModel> topAnimeMoviesArrayList;
    ArrayList<AnimeListModel> topAnimeMoviesModelArrayList;
    ArrayList<AnimeListModel> popularAnimeArrayList;
    ArrayList<AnimeListModel> popularAnimeModelArrayList;
    ArrayList<AnimeListModel> upcomingAnimeArrayList;
    ArrayList<AnimeListModel> upcomingAnimeModelArrayList;
    TopAiringAnimeAdapterFragment topAiringAnimeAdapterFragment;
    AnimeAdapterFragment animeTopTvSeriesAdapterFragment;
    AnimeAdapterFragment animeTopMoviesAdapterFragment;
    AnimeAdapterFragment popularAnimeAdapterFragment;
    AnimeAdapterFragment upcomingAnimeAdapterFragment;
    RecyclerView recyclerView_for_top_airing, recyclerView_top_AnimeTvSeries, recyclerView_for_top_AnimeMovies, recyclerView_for_popularAnime, recyclerView_for_upcomingAnime;
    RelativeLayout maintenanceLayout, view_all_top_airing_container, view_all_top_AnimeTvSeries_container, view_all_top_AnimeMovies_container, view_all_popularAnime_container, view_all_upcomingAnime_container;
    TextView topAiring_textview, top_AnimeTvSeries_textview, top_AnimeMovies_textview, popularAnime_textview, upcomingAnime_textview;
    ProgressBar progressBar_top_airing, progressBar_top_AnimeTvSeries, progressBar_top_AnimeMovies, progressBar_popularAnime, progressBar_upcomingAnime;
    TextView lastSeason_btn, thisSeason_btn, upcomingSeason_btn, maintenanceText;
    ImageView genresThumbnail, calenderThumbnail;
    RelativeLayout genres_card, calender_card;
    SnapHelper snapHelper;
    boolean isUserScrolling = false;
    boolean myAnimeListLoadError = false;
    Timer autoScrollTimer;
    int currentItem = 0;
    Animation startAnimation;
    WebView webView_for_top_airing;
    SwipeRefreshLayout swipe_refresh_layout;
    TextInputEditText edittext_search;
    TextInputLayout textview_search;
    boolean showTopAiring;
    String lastTopAiringButton = "this_season";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anime, container, false);

        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);

        recyclerView_for_top_airing = view.findViewById(R.id.recyclerView_for_top_airing);
        progressBar_top_airing = view.findViewById(R.id.progressBar_top_airing);
        view_all_top_airing_container = view.findViewById(R.id.view_all_top_airing_container);
        topAiring_textview = view.findViewById(R.id.topAiring_textview);
        //webView_for_top_airing = view.findViewById(R.id.webView_for_top_airing);
        maintenanceLayout = view.findViewById(R.id.maintenanceLayout);
        maintenanceText = view.findViewById(R.id.maintenanceText);
        lastSeason_btn = view.findViewById(R.id.lastSeason_btn);
        thisSeason_btn = view.findViewById(R.id.thisSeason_btn);
        upcomingSeason_btn = view.findViewById(R.id.upcomingSeason_btn);

        recyclerView_top_AnimeTvSeries = view.findViewById(R.id.recyclerView_top_AnimeTvSeries);
        progressBar_top_AnimeTvSeries = view.findViewById(R.id.progressBar_top_AnimeTvSeries);
        view_all_top_AnimeTvSeries_container = view.findViewById(R.id.view_all_top_AnimeTvSeries_container);
        top_AnimeTvSeries_textview = view.findViewById(R.id.top_AnimeTvSeries_textview);

        recyclerView_for_top_AnimeMovies = view.findViewById(R.id.recyclerView_for_top_AnimeMovies);
        progressBar_top_AnimeMovies = view.findViewById(R.id.progressBar_top_AnimeMovies);
        view_all_top_AnimeMovies_container = view.findViewById(R.id.view_all_top_AnimeMovies_container);
        top_AnimeMovies_textview = view.findViewById(R.id.top_AnimeMovies_textview);

        recyclerView_for_popularAnime = view.findViewById(R.id.recyclerView_for_popularAnime);
        progressBar_popularAnime = view.findViewById(R.id.progressBar_popularAnime);
        view_all_popularAnime_container = view.findViewById(R.id.view_all_popularAnime_container);
        popularAnime_textview = view.findViewById(R.id.popularAnime_textview);

        recyclerView_for_upcomingAnime = view.findViewById(R.id.recyclerView_for_upcomingAnime);
        progressBar_upcomingAnime = view.findViewById(R.id.progressBar_upcomingAnime);
        view_all_upcomingAnime_container = view.findViewById(R.id.view_all_upcomingAnime_container);
        upcomingAnime_textview = view.findViewById(R.id.upcomingAnime_textview);

        edittext_search = view.findViewById(R.id.edittext_search);
        textview_search = view.findViewById(R.id.textview_search);
        genresThumbnail = view.findViewById(R.id.genresThumbnail);
        calenderThumbnail = view.findViewById(R.id.calenderThumbnail);
        genres_card = view.findViewById(R.id.genres_card);
        calender_card = view.findViewById(R.id.calender_card);


        startAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right_long);

        snapHelper = new PagerSnapHelper();

        if (getActivity() != null) {
            if (InternetUtils.isInternetAvailable(getActivity())) {
                scrapTopAiringThisSeason();
                scrapTopAnimeTvSeries();
                scrapTopAnimeMovies();
                scrapPopularAnime();
                scrapUpcomingAnime();
            }
        }

        initializeButtons();


        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeButtons() {

        lastSeason_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAutoScroll();
                topAiringAnimeAdapterFragment.setData(topAiringPreviousSeasonArrayList);
                topAiringAnimeAdapterFragment.notifyDataSetChanged();
                recyclerView_for_top_airing.scrollToPosition(0);
                currentItem = 0;
                lastTopAiringButton = "previous_season";
                topAiring_textview.setText(R.string.seasonal_anime_previous_season);
                recyclerView_for_top_airing.startAnimation(startAnimation);
                startAutoScrollTimer();
            }
        });
        thisSeason_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAutoScroll();
                topAiringAnimeAdapterFragment.setData(topAiringAnimeThisSeasonArrayList);
                topAiringAnimeAdapterFragment.notifyDataSetChanged();
                recyclerView_for_top_airing.scrollToPosition(0);
                currentItem = 0;
                lastTopAiringButton = "this_season";
                topAiring_textview.setText(R.string.seasonal_anime_this_season);
                recyclerView_for_top_airing.startAnimation(startAnimation);
                startAutoScrollTimer();
            }
        });
        upcomingSeason_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAutoScroll();
                topAiringAnimeAdapterFragment.setData(topAiringUpcomingSeasonArrayList);
                topAiringAnimeAdapterFragment.notifyDataSetChanged();
                recyclerView_for_top_airing.scrollToPosition(0);
                currentItem = 0;
                lastTopAiringButton = "upcoming_season";
                topAiring_textview.setText(R.string.seasonal_anime_upcoming_season);
                recyclerView_for_top_airing.startAnimation(startAnimation);
                startAutoScrollTimer();
            }
        });

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getActivity() != null){
                    if(InternetUtils.isInternetAvailable(getActivity())){
                        lastTopAiringButton = "this_season";
                        currentItem = 0;
                        scrapTopAiringThisSeason();
                        scrapTopAnimeTvSeries();
                        scrapTopAnimeMovies();
                        scrapPopularAnime();
                        scrapUpcomingAnime();
                    }
                }
            }
        });

        edittext_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("whereFrom", "AnimeFragment");
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        view_all_top_airing_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Top Airing : AnimeFragment");
                if(lastTopAiringButton.equals("previous_season")) {
                    DataHolder.setTopAiringAnimeListModelArrayList(topAiringPreviousSeasonModelArrayList);
                    intent.putExtra("whichSeason","previous_season");
                } else if (lastTopAiringButton.equals("this_season")) {
                    intent.putExtra("whichSeason","this_season");
                    DataHolder.setTopAiringAnimeListModelArrayList(topAiringAnimeThisSeasonModelArrayList);
                } else if (lastTopAiringButton.equals("upcoming_season")) {
                    DataHolder.setTopAiringAnimeListModelArrayList(topAiringUpcomingSeasonModelArrayList);
                    intent.putExtra("whichSeason","upcoming_season");
                }
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_top_AnimeTvSeries_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Top Rated Tv Series : AnimeFragment");
                DataHolder.setTopRatedTvSeriesAnimeListModelArrayList(topAnimeTvSeriesModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_top_AnimeMovies_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Top Rated Movies : AnimeFragment");
                DataHolder.setTopRatedMoviesAnimeListModelArrayList(topAnimeMoviesModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_popularAnime_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Popular Anime : AnimeFragment");
                DataHolder.setPopularAnimeListModelArrayList(popularAnimeModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_upcomingAnime_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "Top Upcoming Anime : AnimeFragment");
                DataHolder.setTopUpcomingAnimeListModelArrayList(upcomingAnimeModelArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        genres_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GenresActivity.class);
                intent.putExtra("whereFrom", "AnimeFragment");
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        calender_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CalenderActivity.class);
                intent.putExtra("whereFrom", "AnimeFragment");
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void scrapUpcomingAnime() {

        upcomingAnimeModelArrayList = new ArrayList<>();
        upcomingAnimeArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {

                Document doc = Jsoup.connect(topUpcomingAnime).get();
                Elements boxAllMainData = doc.select("table.top-ranking-table");
                Elements animeData = boxAllMainData.select("tr.ranking-list");

                int total = 0;
                for (Element eachAnime : animeData) {
                    total++;
                    Elements detail = eachAnime.select("div.detail");
                    Elements ratingInfo = eachAnime.select("td.score");

                    String titleMain = detail.select("div.di-ib").select("h3.fl-l").select("a.hoverinfo_trigger").text();

                    String malID = detail.select("div.di-ib").select("h3.fl-l").select("a").attr("href");
                    int startIndex = malID.indexOf("anime/") + "anime/".length();
                    int endIndex = malID.indexOf("/", startIndex);
                    malID = malID.substring(startIndex, endIndex);

                    String rating = ratingInfo.select("span.text").text();
                    if (rating.contains(".")) {
                        rating = rating.substring(0, 3);
                    }

                    String image = eachAnime.select("td.title").select("a.hoverinfo_trigger").select("img").attr("data-src");
                    if (image.isEmpty()) {
                        image = eachAnime.select("td.title").select("a.hoverinfo_trigger").select("img").attr("src");
                    }
                    String data1 = image.substring(0, image.indexOf(".net/") + 4);
                    String data2 = image.substring(image.indexOf("/images"), image.indexOf("?s="));
                    image = data1 + data2;

                    String episodes = detail.select("div.information").text();
                    if (episodes.contains("(")) {
                        episodes = episodes.substring(0, episodes.indexOf(")") + 1);
                    } else {
                        episodes = "-";
                    }


                    AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, null, total);
                    upcomingAnimeModelArrayList.add(animeListModel);
                    if (total <= 20) {
                        upcomingAnimeArrayList.add(animeListModel);
                    }
                }
                handler.post(this::showUpcomingAnime);

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!myAnimeListLoadError){
                            Toast.makeText(getActivity(), "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                            myAnimeListLoadError = true;
                        }
                        if(swipe_refresh_layout.isRefreshing()){
                            swipe_refresh_layout.setRefreshing(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
            });
        });
        executor.shutdown();

    }

    private void showUpcomingAnime() {
        if (getActivity() != null) {
            upcomingAnimeAdapterFragment = new AnimeAdapterFragment(upcomingAnimeArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_upcomingAnime.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_upcomingAnime.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_upcomingAnime.setAdapter(upcomingAnimeAdapterFragment);
            view_all_upcomingAnime_container.setVisibility(View.VISIBLE);
            upcomingAnime_textview.setVisibility(View.VISIBLE);
            progressBar_upcomingAnime.setVisibility(View.GONE);

        }
    }


    private void scrapPopularAnime() {

        popularAnimeModelArrayList = new ArrayList<>();
        popularAnimeArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {

                Document doc = Jsoup.connect(popularAnime).get();
                Elements boxAllMainData = doc.select("table.top-ranking-table");
                Elements animeData = boxAllMainData.select("tr.ranking-list");

                int total = 0;
                for (Element eachAnime : animeData) {
                    total++;
                    Elements detail = eachAnime.select("div.detail");
                    Elements ratingInfo = eachAnime.select("td.score");

                    String titleMain = detail.select("div.di-ib").select("h3.fl-l").select("a.hoverinfo_trigger").text();

                    String malID = detail.select("div.di-ib").select("h3.fl-l").select("a").attr("href");
                    int startIndex = malID.indexOf("anime/") + "anime/".length();
                    int endIndex = malID.indexOf("/", startIndex);
                    malID = malID.substring(startIndex, endIndex);

                    String rating = ratingInfo.select("span.text").text();
                    if (rating.contains(".")) {
                        rating = rating.substring(0, 3);
                    }

                    String image = eachAnime.select("td.title").select("a.hoverinfo_trigger").select("img").attr("data-src");
                    if (image.isEmpty()) {
                        image = eachAnime.select("td.title").select("a.hoverinfo_trigger").select("img").attr("src");
                    }
                    String data1 = image.substring(0, image.indexOf(".net/") + 4);
                    String data2 = image.substring(image.indexOf("/images"), image.indexOf("?s="));
                    image = data1 + data2;

                    String episodes = detail.select("div.information").text();
                    if (episodes.contains("(")) {
                        episodes = episodes.substring(0, episodes.indexOf(")") + 1);
                    } else {
                        episodes = "-";
                    }

                    AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, null, total);
                    popularAnimeModelArrayList.add(animeListModel);
                    if (total <= 20) {
                        popularAnimeArrayList.add(animeListModel);
                    }
                }
                handler.post(this::showPopularAnime);

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!myAnimeListLoadError){
                            Toast.makeText(getActivity(), "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                            myAnimeListLoadError = true;
                        }
                        if(swipe_refresh_layout.isRefreshing()){
                            swipe_refresh_layout.setRefreshing(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
            });
        });
        executor.shutdown();
    }

    private void showPopularAnime() {
        if (getActivity() != null) {

            popularAnimeAdapterFragment = new AnimeAdapterFragment(popularAnimeArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_popularAnime.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_popularAnime.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_popularAnime.setAdapter(popularAnimeAdapterFragment);
            view_all_popularAnime_container.setVisibility(View.VISIBLE);
            popularAnime_textview.setVisibility(View.VISIBLE);
            progressBar_popularAnime.setVisibility(View.GONE);

        }
    }

    private void scrapTopAnimeMovies() {
        topAnimeMoviesModelArrayList = new ArrayList<>();
        topAnimeMoviesArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {

                Document doc = Jsoup.connect(topAnimeMovies).get();
                Elements boxAllMainData = doc.select("table.top-ranking-table");
                Elements animeData = boxAllMainData.select("tr.ranking-list");

                int total = 0;
                for (Element eachAnime : animeData) {
                    total++;
                    Elements detail = eachAnime.select("div.detail");
                    Elements ratingInfo = eachAnime.select("td.score");

                    String titleMain = detail.select("div.di-ib").select("h3.fl-l").select("a.hoverinfo_trigger").text();

                    String malID = detail.select("div.di-ib").select("h3.fl-l").select("a").attr("href");
                    int startIndex = malID.indexOf("anime/") + "anime/".length();
                    int endIndex = malID.indexOf("/", startIndex);
                    malID = malID.substring(startIndex, endIndex);

                    String rating = ratingInfo.select("span.text").text();
                    if (rating.contains(".")) {
                        rating = rating.substring(0, 3);
                    }

                    String image = eachAnime.select("td.title").select("a.hoverinfo_trigger").select("img").attr("data-src");
                    if (image.isEmpty()) {
                        image = eachAnime.select("td.title").select("a.hoverinfo_trigger").select("img").attr("src");
                    }
                    String data1 = image.substring(0, image.indexOf(".net/") + 4);
                    String data2 = image.substring(image.indexOf("/images"), image.indexOf("?s="));
                    image = data1 + data2;

                    String episodes = detail.select("div.information").text();
                    if (episodes.contains("(")) {
                        episodes = episodes.substring(episodes.indexOf("(") + 1, episodes.indexOf("eps")).trim();
                    } else {
                        episodes = "-";
                    }


                    AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, null, total);
                    topAnimeMoviesModelArrayList.add(animeListModel);
                    if (total <= 20) {
                        topAnimeMoviesArrayList.add(animeListModel);
                    }
                }
                handler.post(this::showTopAnimeMovies);

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!myAnimeListLoadError){
                            Toast.makeText(getActivity(), "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                            myAnimeListLoadError = true;
                        }
                        if(swipe_refresh_layout.isRefreshing()){
                            swipe_refresh_layout.setRefreshing(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
            });
        });
        executor.shutdown();
    }

    private void showTopAnimeMovies() {
        if (getActivity() != null) {

            animeTopMoviesAdapterFragment = new AnimeAdapterFragment(topAnimeMoviesArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_top_AnimeMovies.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_top_AnimeMovies.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_top_AnimeMovies.setAdapter(animeTopMoviesAdapterFragment);
            view_all_top_AnimeMovies_container.setVisibility(View.VISIBLE);
            top_AnimeMovies_textview.setVisibility(View.VISIBLE);
            progressBar_top_AnimeMovies.setVisibility(View.GONE);

        }
    }

    private void scrapTopAnimeTvSeries() {
        topAnimeTvSeriesModelArrayList = new ArrayList<>();
        topAnimeTvSeriesArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {

                Document doc = Jsoup.connect(topAnimeTvSeries).get();
                Elements boxAllMainData = doc.select("table.top-ranking-table");
                Elements animeData = boxAllMainData.select("tr.ranking-list");

                int total = 0;
                for (Element eachAnime : animeData) {
                    total++;
                    Elements detail = eachAnime.select("div.detail");
                    Elements ratingInfo = eachAnime.select("td.score");

                    String titleMain = detail.select("div.di-ib").select("h3.fl-l").select("a.hoverinfo_trigger").text();

                    String malID = detail.select("div.di-ib").select("h3.fl-l").select("a").attr("href");
                    int startIndex = malID.indexOf("anime/") + "anime/".length();
                    int endIndex = malID.indexOf("/", startIndex);
                    malID = malID.substring(startIndex, endIndex);

                    String rating = ratingInfo.select("span.text").text();
                    if (rating.contains(".")) {
                        rating = rating.substring(0, 3);
                    }

                    String image = eachAnime.select("td.title").select("a.hoverinfo_trigger").select("img").attr("data-src");
                    if (image.isEmpty()) {
                        image = eachAnime.select("td.title").select("a.hoverinfo_trigger").select("img").attr("src");
                    }
                    String data1 = image.substring(0, image.indexOf(".net/") + 4);
                    String data2 = image.substring(image.indexOf("/images"), image.indexOf("?s="));
                    image = data1 + data2;

                    String episodes = detail.select("div.information").text();
                    if (episodes.contains("(")) {
                        episodes = episodes.substring(episodes.indexOf("(") + 1, episodes.indexOf("eps")).trim();
                    } else {
                        episodes = "-";
                    }


                    AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, null, total);
                    topAnimeTvSeriesModelArrayList.add(animeListModel);
                    if (total <= 20) {
                        topAnimeTvSeriesArrayList.add(animeListModel);
                    }
                    // System.out.println(episodes);
                }
                handler.post(this::showTopAnimeTvSeries);

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!myAnimeListLoadError){
                            Toast.makeText(getActivity(), "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                            myAnimeListLoadError = true;
                        }
                        if(swipe_refresh_layout.isRefreshing()){
                            swipe_refresh_layout.setRefreshing(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
            });
        });
        executor.shutdown();
    }

    private void showTopAnimeTvSeries() {
        if (getActivity() != null) {
            animeTopTvSeriesAdapterFragment = new AnimeAdapterFragment(topAnimeTvSeriesArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_top_AnimeTvSeries.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_top_AnimeTvSeries.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_top_AnimeTvSeries.setAdapter(animeTopTvSeriesAdapterFragment);
            view_all_top_AnimeTvSeries_container.setVisibility(View.VISIBLE);
            top_AnimeTvSeries_textview.setVisibility(View.VISIBLE);
            progressBar_top_AnimeTvSeries.setVisibility(View.GONE);
        }
    }

    public void scrapTopAiringUpcomingSeason() {
        topAiringUpcomingSeasonModelArrayList = new ArrayList<>();
        topAiringUpcomingSeasonArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {

                Document doc = Jsoup.connect(Constants.topAiringUpcomingSeason).get();
                Elements boxAllMainData = doc.select("div.seasonal-anime-list");
                Elements animeData = boxAllMainData.select("div.js-anime-category-producer");
                // System.out.println(boxAllMainData.outerHtml());
                int total = 0;
                for (Element eachAnime : animeData) {
                    total++;
                    Elements title = eachAnime.select("div.title");
                    Elements info = eachAnime.select("div.prodsrc");

                    String titleMain = title.select("div.title-text").select("h2.h2_anime_title").select("a.link-title").text();

                    String malID = title.select("div.title-text").select("h2.h2_anime_title").select("a").attr("href");
                    int startIndex = malID.indexOf("anime/") + "anime/".length();
                    int endIndex = malID.indexOf("/", startIndex);
                    malID = malID.substring(startIndex, endIndex);

                    String rating = title.select("span.js-score").text();
                    if (rating.contains(".")) {
                        rating = rating.substring(0, 3);
                    }

                    Elements boxGenres = eachAnime.select("div.genres-inner");
                    ArrayList<String> genresList = new ArrayList<>();
                    for (Element genreElement : boxGenres) {
                        String genreText = genreElement.text();
                        genresList.add(genreText);
                    }

                    String image = eachAnime.select("div.image").select("a img").attr("data-src");
                    if (image.isEmpty()) {
                        image = eachAnime.select("div.image").select("a img").attr("src");
                    }

                    Elements episodeSpans = info.select("span.item");
                    String episodes = "";
                    for (Element span : episodeSpans) {
                        String text = span.text();
                        if (text.contains("eps")) {
                            // Extract the episode count
                            episodes = text.split(" ")[0];
                        }
                    }

                    AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, genresList, total);
                    topAiringUpcomingSeasonModelArrayList.add(animeListModel);
                    if (total <= 20) {

                        topAiringUpcomingSeasonArrayList.add(animeListModel);
                    }
                }

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!myAnimeListLoadError){
                            Toast.makeText(getActivity(), "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                            myAnimeListLoadError = true;
                        }
                        if(swipe_refresh_layout.isRefreshing()){
                            swipe_refresh_layout.setRefreshing(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
            });
        });
        executor.shutdown();
    }

    public void scrapTopAiringPreviousSeason() {

        topAiringPreviousSeasonModelArrayList = new ArrayList<>();
        topAiringPreviousSeasonArrayList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {

                Document doc = Jsoup.connect(Constants.topAiringPreviousSeason).get();
                Elements boxAllMainData = doc.select("div.seasonal-anime-list");
                Elements animeData = boxAllMainData.select("div.js-anime-category-producer");
                // System.out.println(boxAllMainData.outerHtml());
                int total = 0;
                for (Element eachAnime : animeData) {
                    total++;
                    Elements title = eachAnime.select("div.title");
                    Elements info = eachAnime.select("div.prodsrc");

                    String titleMain = title.select("div.title-text").select("h2.h2_anime_title").select("a.link-title").text();

                    String malID = title.select("div.title-text").select("h2.h2_anime_title").select("a").attr("href");
                    int startIndex = malID.indexOf("anime/") + "anime/".length();
                    int endIndex = malID.indexOf("/", startIndex);
                    malID = malID.substring(startIndex, endIndex);

                    String rating = title.select("span.js-score").text();
                    if (rating.contains(".")) {
                        rating = rating.substring(0, 3);
                    }

                    Elements boxGenres = eachAnime.select("div.genres-inner");
                    ArrayList<String> genresList = new ArrayList<>();
                    for (Element genreElement : boxGenres) {
                        String genreText = genreElement.text();
                        genresList.add(genreText);
                    }

                    String image = eachAnime.select("div.image").select("a img").attr("data-src");
                    if (image.isEmpty()) {
                        image = eachAnime.select("div.image").select("a img").attr("src");
                    }

                    Elements episodeSpans = info.select("span.item");
                    String episodes = "";
                    for (Element span : episodeSpans) {
                        String text = span.text();
                        if (text.contains("eps")) {
                            // Extract the episode count
                            episodes = text.split(" ")[0];
                        }
                    }

                    AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, genresList, total);
                    topAiringPreviousSeasonModelArrayList.add(animeListModel);
                    if (total <= 20) {
                        topAiringPreviousSeasonArrayList.add(animeListModel);
                    }
                }
                handler.post(() -> {
                    lastSeason_btn.setVisibility(View.VISIBLE);
                    lastSeason_btn.startAnimation(startAnimation);
                    thisSeason_btn.setVisibility(View.VISIBLE);
                    thisSeason_btn.startAnimation(startAnimation);
                    upcomingSeason_btn.setVisibility(View.VISIBLE);
                    upcomingSeason_btn.startAnimation(startAnimation);
                });

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!myAnimeListLoadError){
                            Toast.makeText(getActivity(), "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                            myAnimeListLoadError = true;
                        }
                        if(swipe_refresh_layout.isRefreshing()){
                            swipe_refresh_layout.setRefreshing(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });
        executor.shutdown();
    }

    public void scrapTopAiringThisSeason() {

        topAiringAnimeThisSeasonArrayList = new ArrayList<>();
        topAiringAnimeThisSeasonModelArrayList = new ArrayList<>();


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {

                Document doc = Jsoup.connect(Constants.topAiringThisSeason).get();


                Elements boxAllMainData = doc.select("div.seasonal-anime-list");
                Elements animeData = boxAllMainData.select("div.js-anime-category-producer");
                // System.out.println(boxAllMainData.outerHtml());
                int total = 0;
                for (Element eachAnime : animeData) {
                    total++;
                    Elements title = eachAnime.select("div.title");
                    Elements info = eachAnime.select("div.prodsrc");

                    String titleMain = title.select("div.title-text").select("h2.h2_anime_title").select("a.link-title").text();

                    String malID = title.select("div.title-text").select("h2.h2_anime_title").select("a").attr("href");
                    int startIndex = malID.indexOf("anime/") + "anime/".length();
                    int endIndex = malID.indexOf("/", startIndex);
                    malID = malID.substring(startIndex, endIndex);

                    String rating = title.select("span.js-score").text();
                    if (rating.contains(".")) {
                        rating = rating.substring(0, 3);
                    }


                    Elements boxGenres = eachAnime.select("div.genres-inner");
                    ArrayList<String> genresList = new ArrayList<>();
                    for (Element genreElement : boxGenres) {
                        String genreText = genreElement.text();
                        genresList.add(genreText);
                    }

                    String image = eachAnime.select("div.image").select("a img").attr("data-src");
                    if (image.isEmpty()) {
                        image = eachAnime.select("div.image").select("a img").attr("src");
                    }
                    //System.out.println(image);

                    Elements episodeSpans = info.select("span.item");
                    String episodes = "";
                    for (Element span : episodeSpans) {
                        String text = span.text();
                        if (text.contains("eps") || text.contains("ep")) {
                            // Extract the episode count
                            episodes = text.split(" ")[0];
                        }
                    }

                    AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, genresList, total);
                    topAiringAnimeThisSeasonModelArrayList.add(animeListModel);
                    if (total <= 20) {
                        topAiringAnimeThisSeasonArrayList.add(animeListModel);
                        if (total == 20) {
                            handler.post(() -> {
                                showTopAiring = true;
                                showTopAiring();
                            });
                        }
                    }
                }
                if (!showTopAiring) {
                    handler.post(this::showTopAiring);
                }
                //  System.out.println(total);
            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!myAnimeListLoadError){
                            Toast.makeText(getActivity(), getString(R.string.myanimelist_error), Toast.LENGTH_LONG).show();
                            myAnimeListLoadError = true;
                        }
                        if(swipe_refresh_layout.isRefreshing()){
                            swipe_refresh_layout.setRefreshing(false);
                        }
                    }
                });
            } catch (Exception e) {
                if(e.getMessage() != null &&e.getMessage().contains("Status=503")){
                    handler.post(()-> {
                        maintenanceText.setText(R.string.myanimelist_error);
                        maintenanceLayout.setVisibility(View.VISIBLE);
                    });
                }
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

            topAiringAnimeAdapterFragment = new TopAiringAnimeAdapterFragment(topAiringAnimeThisSeasonArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_top_airing.setLayoutAnimation(controller);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
            recyclerView_for_top_airing.setLayoutManager(gridLayoutManager);
            snapHelper.attachToRecyclerView(recyclerView_for_top_airing);
            topAiring_textview.setVisibility(View.VISIBLE);
            progressBar_top_airing.setVisibility(View.GONE);
            recyclerView_for_top_airing.setAdapter(topAiringAnimeAdapterFragment);
            view_all_top_airing_container.setVisibility(View.VISIBLE);
            DataHolder.setTopAiringAnimeListModelArrayList(topAiringAnimeThisSeasonModelArrayList); //because i have used its thumbnail elsewhere
            scrapTopAiringPreviousSeason();
            scrapTopAiringUpcomingSeason();
            //for genres random image using top airing images
            // DataHolder.setTopAiringDramaListModelArrayList(topAiringDramaListModelArrayList);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAutoScrollTimer();
                }
            }, 2000);


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

            textview_search.setVisibility(View.VISIBLE);
            textview_search.startAnimation(startAnimation);

            Random random = new Random();
            int randomNumber = random.nextInt(10);
            int randomNumber2 = random.nextInt(10);
            try {

                Glide.with(genresThumbnail.getContext())
                        .load(topAiringAnimeThisSeasonArrayList.get(randomNumber).getAnimeThumbnail())
                        .transform(new BlurTransformation(8, 2))
                        .into(genresThumbnail);
                Glide.with(calenderThumbnail.getContext())
                        .load(topAiringAnimeThisSeasonArrayList.get(randomNumber2).getAnimeThumbnail())
                        .transform(new BlurTransformation(8, 2))
                        .into(calenderThumbnail);

            }catch (Exception e){
                e.printStackTrace();
            }

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
        if (topAiringAnimeAdapterFragment != null && topAiringAnimeAdapterFragment.getItemCount() > 0) {
            int nextItem = (currentItem + 1) % topAiringAnimeAdapterFragment.getItemCount();
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