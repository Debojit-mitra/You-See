package com.bunny.entertainment.yousee.activities;

import static com.bunny.entertainment.yousee.utils.Constants.MYANIMELIST_URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.CastAdapter;
import com.bunny.entertainment.yousee.adapters.ViewAllAdapterActivity;
import com.bunny.entertainment.yousee.adapters.ViewAllAdapterActivity2;
import com.bunny.entertainment.yousee.adapters.ViewAllAdapterActivity3;
import com.bunny.entertainment.yousee.models.CastListModel;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.models.ShowsListModel;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;
import com.bunny.entertainment.yousee.utils.Constants;
import com.bunny.entertainment.yousee.utils.CustomItemAnimator;
import com.bunny.entertainment.yousee.utils.DataHolder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewAllActivity extends AppCompatActivity {

    String whereFrom, totalResult, total, castUrl;
    String showImage, showName, showEpisodes, showCountry, showRating;
    ArrayList<CastListModel> castListModelArrayList;
    ArrayList<DramaListModel> dramaListModelArrayList;
    ArrayList<ShowsListModel> showsListModelArrayList;
    ArrayList<AnimeListModel> animeListModelArrayList;
    RecyclerView recyclerView_for_top_airing;
    ViewAllAdapterActivity topAiringAdapterActivity;
    ViewAllAdapterActivity2 showFragmentAllAdapterActivity;
    ViewAllAdapterActivity3 animeFragmentAdapterActivity;
    CastAdapter castAdapter;
    String link, getShows, mediaType;
    TextView totalResults, topShowAll_TextVIew, textView_noResult_found, textView_extra;
    boolean isBottom = false;
    int pageNumber = 1;
    int maxPageNumber = 1;
    int dataNumber = 20;
    int dataNumberForContinueWatching = 5;
    int dataNumberForWorkedON = 6;
    int wLoop;
    ExecutorService executor;
    ProgressBar main_ProgressBar;
    int dataLoadInDramaRecycler = 6, maxDataLoad;
    SharedPreferences dramaPreferences;
    boolean myAnimeListLoadError = false;
    final int maxMALPageLimit = 1000;
    int MALPage = 50;
    int malTotal = 50;
    int pageForAnimeGenre = 0;
    int totalAnime = 0;
    String nameOfGenre, genreURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        whereFrom = getIntent().getStringExtra("whereFrom");

        recyclerView_for_top_airing = findViewById(R.id.recyclerView_for_top_airing);
        totalResults = findViewById(R.id.totalResults);
        topShowAll_TextVIew = findViewById(R.id.topShowAll_TextVIew);
        textView_extra = findViewById(R.id.textView_extra);
        main_ProgressBar = findViewById(R.id.main_ProgressBar);
        textView_noResult_found = findViewById(R.id.textView_noResult_found);


        if (whereFrom.equals("Top Airing")) {
            topShowAll_TextVIew.setText(R.string.top_airing);
            dramaListModelArrayList = new ArrayList<>(DataHolder.getTopAiringDramaListModelArrayList());
            link = "https://mydramalist.com/shows/top_airing?page=";
            topAll();
        } else if (whereFrom.equals("Top Shows")) {
            topShowAll_TextVIew.setText(R.string.top_shows);
            dramaListModelArrayList = new ArrayList<>(DataHolder.getTopShowsDramaListModelArrayList());
            link = "https://mydramalist.com/shows/top?page=";
            topAll();
        } else if (whereFrom.equals("Popular Shows")) {
            topShowAll_TextVIew.setText(R.string.popular_shows);
            dramaListModelArrayList = new ArrayList<>(DataHolder.getPopularShowsDramaListModelArrayList());
            link = "https://mydramalist.com/shows/popular?page=";
            topAll();
        } else if (whereFrom.equals("Upcoming Shows")) {
            topShowAll_TextVIew.setText(R.string.upcoming_shows);
            dramaListModelArrayList = new ArrayList<>(DataHolder.getUpcomingShowsDramaListModelArrayList());
            link = "https://mydramalist.com/shows/upcoming?page=";
            topAll();
        } else if (whereFrom.equals("Variety Shows")) {
            topShowAll_TextVIew.setText(R.string.variety_shows);
            dramaListModelArrayList = new ArrayList<>(DataHolder.getVarietyShowsDramaListModelArrayList());
            link = "https://mydramalist.com/shows/variety?page=";
            topAll();
        } else if (whereFrom.equals("Top Movies")) {
            topShowAll_TextVIew.setText(R.string.top_movies);
            dramaListModelArrayList = new ArrayList<>(DataHolder.getTopMoviesDramaListModelArrayList());
            link = "https://mydramalist.com/movies/top?page=";
            topAll();
        } else if (whereFrom.equals("Popular Movies")) {
            topShowAll_TextVIew.setText(R.string.popular_movies);
            dramaListModelArrayList = new ArrayList<>(DataHolder.getPopularMoviesDramaListModelArrayList());
            link = "https://mydramalist.com/movies/popular?page=";
            topAll();
        } else if (whereFrom.equals("Newest Movies")) {
            topShowAll_TextVIew.setText(R.string.newest_movies);
            dramaListModelArrayList = new ArrayList<>(DataHolder.getNewestMoviesDramaListModelArrayList());
            link = "https://mydramalist.com/movies/newest?page=";
            topAll();
        } else if (whereFrom.equals("Upcoming Movies")) {
            topShowAll_TextVIew.setText(R.string.upcoming_movies);
            dramaListModelArrayList = new ArrayList<>(DataHolder.getUpcomingMoviesDramaListModelArrayList());
            link = "https://mydramalist.com/movies/upcoming?page=";
            topAll();
        } else if (whereFrom.equals("Genre Adapter")) {
            topShowAll_TextVIew.setText(getIntent().getStringExtra("genreName"));
            link = getIntent().getStringExtra("genreLink");
            scrapTotalResults();
        } else if (whereFrom.equals("CastDetails")) {
            String topShowAll = "Cast & Credits";
            String dramaName = getIntent().getStringExtra("dramaName");
            topShowAll_TextVIew.setText(topShowAll);
            textView_extra.setText(dramaName);
            textView_extra.setVisibility(View.VISIBLE);
            scrapCastAndDetails(getIntent().getStringExtra("dramaId"));
        } else if (whereFrom.equals("fromShowPeopleDetailsActivity")) {
            topShowAll_TextVIew.setText(getIntent().getStringExtra("name"));
            textView_extra.setText(R.string.worked_on);
            textView_extra.setVisibility(View.VISIBLE);
            loadWorkedOn();
        } else if (whereFrom.equals("continue watching")) {
            topShowAll_TextVIew.setText(R.string.continue_watching);
            loadContinueWatching();
        } else if (whereFrom.equals("PopularShowsFragment")) {
            topShowAll_TextVIew.setText(R.string.popular_shows);
            showsListModelArrayList = new ArrayList<>(DataHolder.getPopularShowsListModelArrayList());
            link = "https://api.themoviedb.org/3/tv/popular?language=en-US&page=";
            showFragmentAll();
        } else if (whereFrom.equals("TopRatedShowsFragment")) {
            topShowAll_TextVIew.setText(R.string.top_rated_shows);
            showsListModelArrayList = new ArrayList<>(DataHolder.getTopRatedShowsListModelArrayList());
            link = "https://api.themoviedb.org/3/tv/top_rated?language=en-US&page=";
            showFragmentAll();
        } else if (whereFrom.equals("OnTheAirShowsFragment")) {
            topShowAll_TextVIew.setText(R.string.on_the_air);
            showsListModelArrayList = new ArrayList<>(DataHolder.getOnTheAirShowsListModelArrayList());
            link = "https://api.themoviedb.org/3/tv/on_the_air?language=en-US&page=";
            showFragmentAll();
        } else if (whereFrom.equals("PopularMoviesShowsFragment")) {
            topShowAll_TextVIew.setText(R.string.popular_movies);
            showsListModelArrayList = new ArrayList<>(DataHolder.getPopularMoviesListModelArrayList());
            link = "https://api.themoviedb.org/3/movie/popular?language=en-US&page=";
            showFragmentAll();
        } else if (whereFrom.equals("TopRatedMoviesShowsFragment")) {
            topShowAll_TextVIew.setText(R.string.top_rated_movies);
            showsListModelArrayList = new ArrayList<>(DataHolder.getTopRatedMoviesListModelArrayList());
            link = "https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=";
            showFragmentAll();
        } else if (whereFrom.equals("NowOnCinemasMoviesShowsFragment")) {
            topShowAll_TextVIew.setText(R.string.now_on_cinemas);
            showsListModelArrayList = new ArrayList<>(DataHolder.getNowOnCinemasMoviesListModelArrayList());
            link = "https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=";
            showFragmentAll();
        } else if (whereFrom.equals("UpcomingMoviesShowsFragment")) {
            topShowAll_TextVIew.setText(R.string.upcoming_movies);
            showsListModelArrayList = new ArrayList<>(DataHolder.getUpcomingMoviesListModelArrayList());
            link = "https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=";
            showFragmentAll();
        } else if (whereFrom.equals("Top Airing : AnimeFragment")) {
            String whichSeason = getIntent().getStringExtra("whichSeason");
            if (whichSeason != null) {
                if (whichSeason.equals("previous_season")) {
                    String prevSeason = "Seasonal Anime :\nPrevious Season";
                    topShowAll_TextVIew.setText(prevSeason);
                } else if (whichSeason.equals("this_season")) {
                    String thisSeason = "Seasonal Anime :\nThis Season";
                    topShowAll_TextVIew.setText(thisSeason);
                } else if (whichSeason.equals("upcoming_season")) {
                    String thisSeason = "Seasonal Anime :\nUpcoming Season";
                    topShowAll_TextVIew.setText(thisSeason);
                }
            }
            animeListModelArrayList = new ArrayList<>(DataHolder.getTopAiringAnimeListModelArrayList());
            String results = animeListModelArrayList.size() + " Results";
            totalResults.setText(results);
            animeFragmentTopAiring();
        } else if (whereFrom.equals("Top Rated Tv Series : AnimeFragment")) {
            link = MYANIMELIST_URL + "/topanime.php?type=tv&limit=";
            topShowAll_TextVIew.setText(R.string.top_rated_anime_series);
            totalResults.setText(R.string._1000_results);
            animeListModelArrayList = new ArrayList<>(DataHolder.getTopRatedTvSeriesAnimeListModelArrayList());
            animeFragmentTopRatedTvSeries();
        } else if (whereFrom.equals("Top Rated Movies : AnimeFragment")) {
            link = MYANIMELIST_URL + "/topanime.php?type=movie&limit=";
            topShowAll_TextVIew.setText(R.string.top_rated_anime_movies);
            totalResults.setText(R.string._1000_results);
            animeListModelArrayList = new ArrayList<>(DataHolder.getTopRatedMoviesAnimeListModelArrayList());
            animeFragmentTopRatedTvSeries();
        } else if (whereFrom.equals("Popular Anime : AnimeFragment")) {
            link = MYANIMELIST_URL + "/topanime.php?type=bypopularity&limit=";
            topShowAll_TextVIew.setText(R.string.popular_anime);
            totalResults.setText(R.string._1000_results);
            animeListModelArrayList = new ArrayList<>(DataHolder.getPopularAnimeListModelArrayList());
            animeFragmentTopRatedTvSeries();
        } else if (whereFrom.equals("Top Upcoming Anime : AnimeFragment")) {
            link = MYANIMELIST_URL + "/topanime.php?type=upcoming&limit=";
            topShowAll_TextVIew.setText(R.string.top_upcoming_anime);
            totalResults.setText(R.string._1000_results);
            animeListModelArrayList = new ArrayList<>(DataHolder.getTopUpcomingAnimeListModelArrayList());
            animeFragmentTopRatedTvSeries();
        } else if (whereFrom.equals("AnimeGenre Adapter")) {
            link = getIntent().getStringExtra("genreLink");
            System.out.println(link);
            nameOfGenre = getIntent().getStringExtra("genreName");
            String res = "", nameofGenre2 = "";
            if (nameOfGenre != null && nameOfGenre.contains("(")) {
                res = nameOfGenre.substring(nameOfGenre.indexOf("(") + 1, nameOfGenre.indexOf(")"));
                String result = res + " Results";
                totalResults.setText(result);
                if (res.contains(",")) {
                    res = res.replace(",", "");
                }
                nameofGenre2 = nameOfGenre.substring(0, nameOfGenre.indexOf("("));
                topShowAll_TextVIew.setText(nameofGenre2);
            } else {
                res = String.valueOf(1);
                topShowAll_TextVIew.setText(nameOfGenre);
            }
            animeListModelArrayList = new ArrayList<>();
            assert nameOfGenre != null;
            if (res.equals("1")) {
                maxPageNumber = 1;
            } else {
                maxPageNumber = Integer.parseInt(res) / 100;
                System.out.println("maxPageNumber: "+maxPageNumber);
                if (maxPageNumber % 100 != 0) {
                    maxPageNumber++;
                } else if (maxPageNumber == 0){
                    maxPageNumber = 1;
                }
            }
            scrapBasedOnGenre();
        }

    }

    private void scrapBasedOnGenre() {
        if (pageForAnimeGenre < maxPageNumber) {
            pageForAnimeGenre++;
            if (pageForAnimeGenre > 1) {
                animeFragmentAdapterActivity.showLoading();
                recyclerView_for_top_airing.scrollToPosition(animeListModelArrayList.size());
            }

            if(nameOfGenre.contains("(")){
                genreURL = Constants.MYANIMELIST_URL + link + "?page=" + pageForAnimeGenre;
            } else {
                genreURL = link;
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {

                    Document doc = Jsoup.connect(genreURL).get();
                    Elements boxAllMainData = doc.select("div.seasonal-anime-list");
                    Elements animeData = boxAllMainData.select("div.js-anime-category-producer");
                    // System.out.println(boxAllMainData.outerHtml());

                    for (Element eachAnime : animeData) {
                        totalAnime++;
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
                            } else if(text.contains("ep")){
                                episodes = "1";
                            }
                        }

                        AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, genresList, totalAnime);
                        animeListModelArrayList.add(animeListModel);
                    }

                    handler.post(() -> {
                        if (pageForAnimeGenre == 1) {
                            animeFragmentTopRatedTvSeries();
                        } else {
                            animeFragmentAdapterActivity.notifyItemInserted(animeListModelArrayList.size());
                            animeFragmentAdapterActivity.hideLoading();
                            isBottom = false;
                        }
                    });

                } catch (SocketTimeoutException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (!myAnimeListLoadError) {
                                Toast.makeText(ViewAllActivity.this, "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                                myAnimeListLoadError = true;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {

            animeFragmentAdapterActivity.showEnd();

        }
    }

    private void animeFragmentTopRatedTvSeries() {
        animeFragmentAdapterActivity = new ViewAllAdapterActivity3(animeListModelArrayList, ViewAllActivity.this);

        Animation animation = AnimationUtils.loadAnimation(ViewAllActivity.this, R.anim.recyclerviewscroll_top_bottom); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_top_airing.setLayoutAnimation(controller);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewAllActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView_for_top_airing.setLayoutManager(linearLayoutManager);
        recyclerView_for_top_airing.setHasFixedSize(true);
        recyclerView_for_top_airing.setAdapter(animeFragmentAdapterActivity);
        recyclerView_for_top_airing.setItemAnimator(new CustomItemAnimator());
        main_ProgressBar.setVisibility(View.GONE);

        recyclerView_for_top_airing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!isBottom) {
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isBottom = true;
                        if (whereFrom.contains("AnimeGenre Adapter")) {
                            scrapBasedOnGenre();
                        } else {
                            ScrapTopRatedTvSeries();
                        }
                    }
                } /*else {
                    animeFragmentAdapterActivity.showEnd();
                }*/

            }
        });

    }

    private void ScrapTopRatedTvSeries() {

        if (MALPage <= maxMALPageLimit) {
            animeFragmentAdapterActivity.showLoading();
            recyclerView_for_top_airing.scrollToPosition(animeListModelArrayList.size());

            String linkMain = link + MALPage;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here
                try {

                    Document doc = Jsoup.connect(linkMain).get();
                    Elements boxAllMainData = doc.select("table.top-ranking-table");
                    Elements animeData = boxAllMainData.select("tr.ranking-list");


                    for (Element eachAnime : animeData) {
                        malTotal++;
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


                        AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, null, malTotal);
                        if (MALPage != 0) {
                            animeListModelArrayList.add(animeListModel);
                        }
                    }
                    MALPage = MALPage + 50;

                    handler.post(() -> {
                        //UI Thread work here
                        animeFragmentAdapterActivity.notifyItemInserted(animeListModelArrayList.size());
                        animeFragmentAdapterActivity.hideLoading();
                        isBottom = false;
                    });


                } catch (SocketTimeoutException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (!myAnimeListLoadError) {
                                Toast.makeText(ViewAllActivity.this, "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                                myAnimeListLoadError = true;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.post(() -> {
                });
            });
        } else {
            if(animeFragmentAdapterActivity != null){
                animeFragmentAdapterActivity.showEnd();
            }
        }
    }

    private void animeFragmentTopAiring() {

        animeFragmentAdapterActivity = new ViewAllAdapterActivity3(animeListModelArrayList, ViewAllActivity.this);

        Animation animation = AnimationUtils.loadAnimation(ViewAllActivity.this, R.anim.recyclerviewscroll_top_bottom); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_top_airing.setLayoutAnimation(controller);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewAllActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView_for_top_airing.setLayoutManager(linearLayoutManager);
        recyclerView_for_top_airing.setHasFixedSize(true);
        recyclerView_for_top_airing.setAdapter(animeFragmentAdapterActivity);
        main_ProgressBar.setVisibility(View.GONE);

        recyclerView_for_top_airing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!isBottom) {
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isBottom = true;
                    }
                } else {
                    animeFragmentAdapterActivity.showEnd();
                }

            }
        });
    }

    private void showFragmentAll() {

        showFragmentAllAdapterActivity = new ViewAllAdapterActivity2(showsListModelArrayList, ViewAllActivity.this);

        Animation animation = AnimationUtils.loadAnimation(ViewAllActivity.this, R.anim.recyclerviewscroll_top_bottom); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_top_airing.setLayoutAnimation(controller);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewAllActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView_for_top_airing.setLayoutManager(linearLayoutManager);
        recyclerView_for_top_airing.setHasFixedSize(true);
        recyclerView_for_top_airing.setAdapter(showFragmentAllAdapterActivity);
        main_ProgressBar.setVisibility(View.GONE);

        total = showsListModelArrayList.get(0).getTotalResults();
        String setTotalResults = total + " total results";
        totalResults.setText(setTotalResults);
        maxPageNumber = Integer.parseInt(showsListModelArrayList.get(0).getTotalPages().trim());
        System.out.println(maxPageNumber);

        recyclerView_for_top_airing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!isBottom) {
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isBottom = true;
                        loadNextPageFromFragment();
                    }
                }

            }
        });

    }

    private void loadNextPageFromFragment() {

        pageNumber++;
        if (pageNumber <= maxPageNumber) {
            showFragmentAllAdapterActivity.showLoading();
            recyclerView_for_top_airing.smoothScrollToPosition(showsListModelArrayList.size());
            if (whereFrom.equals("PopularMoviesShowsFragment") || whereFrom.equals("TopRatedMoviesShowsFragment") || whereFrom.equals("NowOnCinemasMoviesShowsFragment")
                    || whereFrom.equals("UpcomingMoviesShowsFragment")) {
                getShows = link + pageNumber + "&region=IN";
                mediaType = "movie";
            } else {
                getShows = link + pageNumber;
                mediaType = "tv";
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {


                    RequestQueue queue = Volley.newRequestQueue(ViewAllActivity.this);

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, getShows, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("results");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObjectContent = jsonArray.getJSONObject(i);
                                    String TMDBid = jsonObjectContent.getString("id");
                                    String name = "";
                                    if (jsonObjectContent.has("name")) {
                                        name = jsonObjectContent.getString("name");
                                    } else if (jsonObjectContent.has("title")) {
                                        name = jsonObjectContent.getString("title");
                                    }
                                    String imageUrl = jsonObjectContent.getString("poster_path");
                                    imageUrl = "https://image.tmdb.org/t/p/w200" + imageUrl;

                                 /*   if(jsonObjectContent.has("media_type")){
                                        mediaType  = jsonObjectContent.getString("media_type");
                                    }*/
                                    String rating = "";
                                    if (jsonObjectContent.has("vote_average")) {
                                        rating = jsonObjectContent.getString("vote_average");
                                        rating = rating.substring(0, rating.indexOf(".") + 2);
                                        if (!rating.contains(".")) {
                                            rating = rating + ".0";
                                        }
                                    }


                                    dataNumber++;
                                    ShowsListModel showsListModel = new ShowsListModel(name, rating, imageUrl, "", "", mediaType, TMDBid, String.valueOf(dataNumber));
                                    showsListModelArrayList.add(showsListModel);
                                    //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                                }


                                handler.post(() -> {
                                    //UI Thread work here
                                    showFragmentAllAdapterActivity.notifyItemInserted(showsListModelArrayList.size());
                                    showFragmentAllAdapterActivity.hideLoading();
                                    isBottom = false;
                                });

                                //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            handler.post(() -> {
                                //UI Thread work here

                            });
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            return headers;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(stringRequest);

                    if (pageNumber == maxPageNumber) {
                        showFragmentAllAdapterActivity.showEnd();
                        recyclerView_for_top_airing.smoothScrollToPosition(showsListModelArrayList.size()); //before was +1
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }
    }

    private void loadContinueWatching() {
        dramaPreferences = getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);

        ArrayList<DramaListModel> continueWatchingDramaDetailsArrayList = DataHolder.getContinueWatchingDramaDetailsArrayList();
        ArrayList<String> continueWatchingDramaIDsArrayListSaved = getDramaListFromPrefs();

        if (continueWatchingDramaDetailsArrayList != null && continueWatchingDramaIDsArrayListSaved != null) {
            String totalResults = continueWatchingDramaIDsArrayListSaved.size() + " total";
            this.totalResults.setText(totalResults);

            topAiringAdapterActivity = new ViewAllAdapterActivity(continueWatchingDramaDetailsArrayList, ViewAllActivity.this);

            Animation animation = AnimationUtils.loadAnimation(ViewAllActivity.this, R.anim.recyclerviewscroll_top_bottom); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_top_airing.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewAllActivity.this, LinearLayoutManager.VERTICAL, false);
            recyclerView_for_top_airing.setLayoutManager(linearLayoutManager);
            recyclerView_for_top_airing.setHasFixedSize(true);
            recyclerView_for_top_airing.setAdapter(topAiringAdapterActivity);
            main_ProgressBar.setVisibility(View.GONE);

            recyclerView_for_top_airing.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!isBottom) {
                        if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                            isBottom = true;
                            loadMoreContinueWatching(continueWatchingDramaIDsArrayListSaved, continueWatchingDramaDetailsArrayList);
                        }
                    }
                }
            });

        }
    }

    private void loadMoreContinueWatching(ArrayList<String> continueWatchingDramaIDsArrayListSaved, ArrayList<DramaListModel> continueWatchingDramaDetailsArrayList) {

        int currentDataSize = continueWatchingDramaDetailsArrayList.size();
        int maxDataLoadSize = continueWatchingDramaIDsArrayListSaved.size();
        System.out.println(currentDataSize+" : "+maxDataLoadSize);
        Collections.reverse(continueWatchingDramaIDsArrayListSaved);

        if (currentDataSize < maxDataLoadSize) {
            topAiringAdapterActivity.showLoading();
            recyclerView_for_top_airing.smoothScrollToPosition(continueWatchingDramaDetailsArrayList.size());

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here
                try {
                    for (wLoop = currentDataSize; wLoop < Math.min(maxDataLoadSize, currentDataSize + 5); wLoop++) {
                        if (wLoop >= continueWatchingDramaDetailsArrayList.size()) {
                        //System.out.println("hehe: "+maxDataLoadSize+"  :  "+currentDataSize);
                        if (continueWatchingDramaIDsArrayListSaved.get(wLoop).contains("@-")) {
                            String dataGot = continueWatchingDramaIDsArrayListSaved.get(wLoop);
                            String showId = dataGot.substring(0, dataGot.indexOf("@"));
                            String showMediaType = dataGot.substring(dataGot.indexOf("-") + 1);
                            //System.out.println("dataGotViewAllActivity: " + dataGot + "  " + showMediaType);
                            try {
                                RequestQueue queue = Volley.newRequestQueue(ViewAllActivity.this);

                                String showUrl = Constants.TMDB_URL_MAIN + showMediaType + "/" + showId + "?language=en-US";

                                Map<String, String> headers = new HashMap<>();
                                headers.put("accept", "application/json");
                                headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                                StringRequest stringRequest = new StringRequest(Request.Method.GET, showUrl, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            // JSONArray jsonArray = jsonObject.getJSONArray("results");

                                            String showName = "", showImage = "", showRating = "", showEpisodes = "";
                                            if (jsonObject.has("name")) {
                                                showName = jsonObject.getString("name");
                                            } else if (jsonObject.has("title")) {
                                                showName = jsonObject.getString("title");
                                            } else if (jsonObject.has("original_name")) {
                                                showName = jsonObject.getString("original_name");
                                            } else if (jsonObject.has("original_title")) {
                                                showName = jsonObject.getString("original_title");
                                            }

                                            if (showMediaType.contains("tv")) {
                                                if (jsonObject.has("number_of_episodes")) {
                                                    showEpisodes = jsonObject.getString("number_of_episodes");
                                                } else {
                                                    showEpisodes = getString(R.string.n_a);
                                                }
                                            } else {
                                                showEpisodes = "1";
                                            }

                                            showImage = jsonObject.getString("poster_path");
                                            showImage = "https://image.tmdb.org/t/p/w300" + showImage;

                                            if (jsonObject.has("vote_average")) {
                                                showRating = jsonObject.getString("vote_average");
                                                showRating = showRating.substring(0, showRating.indexOf(".") + 2);
                                            } else {
                                                showRating = getString(R.string.n_a);
                                            }
                                            dataNumberForContinueWatching++;
                                            DramaListModel dramaListModel = new DramaListModel(showName, "", showEpisodes, showRating, showImage, showMediaType, showId, String.valueOf(dataNumberForContinueWatching));
                                            continueWatchingDramaDetailsArrayList.add(dramaListModel);

                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        return headers;
                                    }
                                };
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                queue.add(stringRequest);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else if ((continueWatchingDramaIDsArrayListSaved.get(wLoop).contains("*-"))){
                            String animeID = continueWatchingDramaIDsArrayListSaved.get(wLoop);
                            animeID = animeID.substring(0, animeID.indexOf("*-"));
                            String animeUrl = Constants.MYANIMELIST_URL + "/anime/" + animeID;
                            System.out.println(animeUrl);
                            Document doc = Jsoup.connect(animeUrl).get();
                            Elements box = doc.select("div#contentWrapper");

                            String animeName, animeImage = "", animeEP ="", animeRating;
                            animeName = box.select("h1.title-name").text();
                            //animeNameEnglish = box.select("p.title-english.title-inherit").text();

                            Element imgElement = doc.selectFirst("img[itemprop=image]");
                            if (imgElement != null) {
                                animeImage = imgElement.attr("data-src");
                            }
                            Elements divs = box.select("div.leftside").select("div.spaceit_pad");

                            for (Element div : divs) {
                                String text = div.text();
                                if (text.startsWith("Episodes:")) {
                                    animeEP = text.replace("Episodes:", "").trim();
                                    break;
                                }
                            }
                            Elements rightSide = doc.select("div.rightside");
                            Elements rating_Rank_PopEle = rightSide.select("div.anime-detail-header-stats");
                            animeRating = rating_Rank_PopEle.select("div.fl-l.score").select("div.score-label").text();


                            // System.out.println("showId: "+showId+" : "+showMediaType);
                            dataNumberForContinueWatching++;
                            DramaListModel dramaListModel = new DramaListModel(animeName, "", animeEP, animeRating, animeImage, "Anime", animeID, String.valueOf(dataNumberForContinueWatching));
                            continueWatchingDramaDetailsArrayList.add(dramaListModel);

                        } else {
                            String dramaUrl = "https://mydramalist.com" + continueWatchingDramaIDsArrayListSaved.get(wLoop);
                            int dramaIndex = 0, dramaEpisodesIndex = 0, showRatingIndex = 0;

                            Document doc = Jsoup.connect(dramaUrl).get();
                            Elements box = doc.select("div.col-sm-4");
                            String dramaImage = box.select("img.img-responsive").attr("src"); //1
                            String dramaName, dramaEpisodes, showRating = "";

                            Elements box5 = doc.select("div.col-lg-4"); //main
                            Elements box6 = box5.select("div.box.clear.hidden-sm-down").select("div.box-body").select("ul.list").select("li");

                            try {
                                dramaIndex = findIndexWithText(box6, "Drama");
                                if (dramaIndex == -1) {
                                    dramaIndex = findIndexWithText(box6, "TV Show");
                                }
                                if (dramaIndex == -1) {
                                    dramaIndex = findIndexWithText(box6, "Special");
                                }
                                if (dramaIndex == -1) {
                                    dramaIndex = findIndexWithText(box6, "Movie");
                                }

                                dramaEpisodesIndex = findIndexWithText(box6, "Episodes");
                                showRatingIndex = findIndexWithText(box6, "Score");

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }


                            if (dramaIndex != -1) {
                                dramaName = box6.get(dramaIndex).text();
                                dramaName = dramaName.substring(dramaName.indexOf(":") + 1).trim();
                            } else {
                                dramaName = getString(R.string.n_a);
                            }

                            if (dramaEpisodesIndex != -1) {
                                dramaEpisodes = box6.get(dramaEpisodesIndex).text();
                                dramaEpisodes = dramaEpisodes.substring(dramaEpisodes.indexOf(":") + 1).trim();
                            } else {
                                dramaEpisodes = getString(R.string.n_a);
                            }

                            if (showRatingIndex != -1) {
                                showRating = box6.get(showRatingIndex).text();
                                showRating = showRating.substring(showRating.indexOf(":") + 1, showRating.indexOf("(")).trim();
                                if (showRating.contains("N/A")) {
                                    showRating = getString(R.string._0_0);
                                }
                            }
                            dataNumberForContinueWatching++;
                            DramaListModel dramaListModel = new DramaListModel(dramaName, "", dramaEpisodes, showRating, dramaImage, "", continueWatchingDramaIDsArrayListSaved.get(wLoop), String.valueOf(dataNumberForContinueWatching));
                            continueWatchingDramaDetailsArrayList.add(dramaListModel);
                        }
                    }

                    }
                    handler.post(() -> {
                        //UI Thread work here
                        topAiringAdapterActivity.notifyItemInserted(currentDataSize+Math.min(maxDataLoadSize, currentDataSize + 5));
                        topAiringAdapterActivity.hideLoading();

                        isBottom = false;
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            });

        } else {
            topAiringAdapterActivity.showEnd();
            recyclerView_for_top_airing.smoothScrollToPosition(continueWatchingDramaDetailsArrayList.size() + 1);
        }


    }

    private void loadWorkedOn() {
        ArrayList<String> peopleWorkIdModelArrayList = DataHolder.getPeopleWorkIdModelArrayList();
        ArrayList<DramaListModel> viewAllWorkArrayList = DataHolder.getViewAllWorkArrayList();

        String setTotalResults = peopleWorkIdModelArrayList.size() + " total results";
        totalResults.setText(setTotalResults);

        topAiringAdapterActivity = new ViewAllAdapterActivity(viewAllWorkArrayList, ViewAllActivity.this);

        Animation animation = AnimationUtils.loadAnimation(ViewAllActivity.this, R.anim.recyclerviewscroll_top_bottom); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_top_airing.setLayoutAnimation(controller);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewAllActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView_for_top_airing.setLayoutManager(linearLayoutManager);
        recyclerView_for_top_airing.setHasFixedSize(true);
        recyclerView_for_top_airing.setAdapter(topAiringAdapterActivity);
        main_ProgressBar.setVisibility(View.GONE);

        recyclerView_for_top_airing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!isBottom) {
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isBottom = true;
                        loadMoreWorkedOn(peopleWorkIdModelArrayList, viewAllWorkArrayList);

                    }
                }

            }
        });
    }

    private void loadMoreWorkedOn(ArrayList<String> peopleWorkIdModelArrayList, ArrayList<DramaListModel> viewAllWorkArrayList) {

        maxDataLoad = peopleWorkIdModelArrayList.size();

        if (dataLoadInDramaRecycler != maxDataLoad) {
            topAiringAdapterActivity.showLoading();
            recyclerView_for_top_airing.smoothScrollToPosition(viewAllWorkArrayList.size());
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here
                try {
                    int limit = Math.min(maxDataLoad, 8 + dataLoadInDramaRecycler);
                    for (int i = dataLoadInDramaRecycler; i < limit; i++) {

                        String showUrl = "https://mydramalist.com" + peopleWorkIdModelArrayList.get(i);
                        try {

                            int showIndex = 0, showEpisodesIndex = 0, showCountryIndex = 0, showRatingIndex = 0;

                            Document doc = Jsoup.connect(showUrl).get();
                            Elements box = doc.select("div.col-sm-4");
                            showImage = box.select("img.img-responsive").attr("src"); //1

                            Elements box5 = doc.select("div.col-lg-4").select("div.box.clear.hidden-sm-down").select("div.box-body").select("ul.list").select("li");
                            ; //main
                            showIndex = findIndexWithText(box5, "Drama");
                            if (showIndex == -1) {
                                showIndex = findIndexWithText(box5, "TV Show");
                            }
                            if (showIndex == -1) {
                                showIndex = findIndexWithText(box5, "Special");
                            }
                            if (showIndex == -1) {
                                showIndex = findIndexWithText(box5, "Movie");
                            }
                            showEpisodesIndex = findIndexWithText(box5, "Episodes");
                            showCountryIndex = findIndexWithText(box5, "Country");
                            showRatingIndex = findIndexWithText(box5, "Score");

                            if (showIndex != -1) {
                                showName = box5.get(showIndex).text();
                                showName = showName.substring(showName.indexOf(":") + 1).trim();
                            }

                            if (showEpisodesIndex != -1) {
                                showEpisodes = box5.get(showEpisodesIndex).text(); //10
                                showEpisodes = showEpisodes.substring(showEpisodes.indexOf(":") + 1).trim();
                            }

                            if (showCountryIndex != -1) {
                                showCountry = box5.get(showCountryIndex).text(); //9
                                showCountry = showCountry.substring(showCountry.indexOf(":") + 1).trim();
                            }

                            if (showRatingIndex != -1) {
                                showRating = box5.get(showRatingIndex).text();//16
                                showRating = showRating.substring(showRating.indexOf(":") + 1, showRating.indexOf("(")).trim();
                                if (showRating.contains("N/A")) {
                                    showRating = getString(R.string._0_0);
                                }
                            }
                            dataNumberForWorkedON++;
                            dataLoadInDramaRecycler++;
                            DramaListModel dramaListModel = new DramaListModel(showName, showCountry, showEpisodes, showRating, showImage, String.valueOf(peopleWorkIdModelArrayList.size()), peopleWorkIdModelArrayList.get(i), String.valueOf(dataNumberForWorkedON));

                            viewAllWorkArrayList.add(dramaListModel);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //UI Thread work here
                    handler.post(() -> {
                        //UI Thread work here
                        topAiringAdapterActivity.notifyItemInserted(viewAllWorkArrayList.size());
                        topAiringAdapterActivity.hideLoading();
                        isBottom = false;
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.post(() -> {
                    //UI Thread work here

                });
            });

        } else {
            topAiringAdapterActivity.showEnd();
            recyclerView_for_top_airing.smoothScrollToPosition(viewAllWorkArrayList.size() + 1);
        }


    }

    private void scrapCastAndDetails(String dramaId) {

        castListModelArrayList = new ArrayList<>();

        castUrl = "https://mydramalist.com" + dramaId + "/cast";

        executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {
                Document doc = Jsoup.connect(castUrl).get();
                Elements boxMain = doc.select("div.box.cast-credits").select("div.box-body");

                Elements directorList = boxMain.select("h3.header.b-b.p-b:contains(Director) + ul.list.no-border.p-b.clear li.list-item");
                if (!directorList.isEmpty()) {
                    for (Element directorElement : directorList) {
                        String ImageUrl = directorElement.select("div.p-r.p-l-0 img.img-responsive").attr("src");
                        String Name = directorElement.select("a.text-primary b").text();
                        String Id = directorElement.select("a.text-primary").attr("href");

                        CastListModel castListModel = new CastListModel(Name, "", "Director", ImageUrl, Id);
                        castListModelArrayList.add(castListModel);

                    }
                }

                Elements producerList = boxMain.select("h3.header.b-b.p-b:contains(Director) + ul.list.no-border.p-b.clear li.list-item");
                if (!producerList.isEmpty()) {
                    for (Element producerElement : producerList) {
                        String ImageUrl = producerElement.select("div.p-r.p-l-0 img.img-responsive").attr("src");
                        String Name = producerElement.select("a.text-primary b").text();
                        String Id = producerElement.select("a.text-primary").attr("href");

                        CastListModel castListModel = new CastListModel(Name, "", "Producer", ImageUrl, Id);
                        castListModelArrayList.add(castListModel);
                    }
                }

                Elements screenWriterList = boxMain.select("h3.header.b-b.p-b:contains(Screenwriter) + ul.list.no-border.p-b.clear li.list-item");
                if (!screenWriterList.isEmpty()) {
                    for (Element screenWriterElement : screenWriterList) {
                        String ImageUrl = screenWriterElement.select("div.p-r.p-l-0 img.img-responsive").attr("src");
                        String Name = screenWriterElement.select("a.text-primary b").text();
                        String Id = screenWriterElement.select("a.text-primary").attr("href");

                        CastListModel castListModel = new CastListModel(Name, "", "Screen Writer", ImageUrl, Id);
                        castListModelArrayList.add(castListModel);
                    }
                }

                Elements MainRoleList = boxMain.select("h3.header.b-b.p-b:contains(Main Role) + ul.list.no-border.p-b.clear li.list-item");
                if (!MainRoleList.isEmpty()) {
                    for (Element MainRoleElement : MainRoleList) {
                        String ImageUrl = MainRoleElement.select("div.p-r.p-l-0 img.img-responsive").attr("src");
                        String Name = MainRoleElement.select("a.text-primary b").text();

                        Elements characterNameElements = MainRoleElement.select("div.col-xs-9.col-sm-8.p-a-0 > div");
                        String characterName = "";
                        if (!characterNameElements.isEmpty()) {
                            characterName = MainRoleElement.select("div.col-xs-9.col-sm-8.p-a-0 > div").get(0).text();
                        }
                        String Id = MainRoleElement.select("a.text-primary").attr("href");

                        CastListModel castListModel = new CastListModel(Name, characterName, "Main Role", ImageUrl, Id);
                        castListModelArrayList.add(castListModel);
                    }
                }

                Elements SupportRoleList = boxMain.select("h3.header.b-b.p-b:contains(Support Role) + ul.list.no-border.p-b.clear li.list-item");
                if (!SupportRoleList.isEmpty()) {
                    for (Element SupportRoleElement : SupportRoleList) {
                        String ImageUrl = SupportRoleElement.select("div.p-r.p-l-0 img.img-responsive").attr("src");
                        String Name = SupportRoleElement.select("a.text-primary b").text();

                        Elements characterNameElements = SupportRoleElement.select("div.col-xs-9.col-sm-8.p-a-0 > div");
                        String characterName = "";
                        if (!characterNameElements.isEmpty()) {
                            characterName = SupportRoleElement.select("div.col-xs-9.col-sm-8.p-a-0 > div").get(0).text();
                        }
                        String Id = SupportRoleElement.select("a.text-primary").attr("href");

                        CastListModel castListModel = new CastListModel(Name, characterName, "Support Role", ImageUrl, Id);
                        castListModelArrayList.add(castListModel);
                    }
                }

                Elements GuestRoleList = boxMain.select("h3.header.b-b.p-b:contains(Guest Role) + ul.list.no-border.p-b.clear li.list-item");
                if (!GuestRoleList.isEmpty()) {
                    for (Element GuestRoleElement : GuestRoleList) {
                        String ImageUrl = GuestRoleElement.select("div.p-r.p-l-0 img.img-responsive").attr("src");
                        String Name = GuestRoleElement.select("a.text-primary b").text();

                        Elements characterNameElements = GuestRoleElement.select("div.col-xs-9.col-sm-8.p-a-0 > div");
                        String characterName = "";
                        if (!characterNameElements.isEmpty()) {
                            characterName = GuestRoleElement.select("div.col-xs-9.col-sm-8.p-a-0 > div").get(0).text();
                        }
                        String Id = GuestRoleElement.select("a.text-primary").attr("href");
                        CastListModel castListModel = new CastListModel(Name, characterName, "Guest Role", ImageUrl, Id);
                        castListModelArrayList.add(castListModel);
                    }
                }

                Elements MainHostList = boxMain.select("h3.header.b-b.p-b:contains(Main Host) + ul.list.no-border.p-b.clear li.list-item");
                if (!MainHostList.isEmpty()) {
                    for (Element MainHostElement : MainHostList) {
                        String ImageUrl = MainHostElement.select("div.p-r.p-l-0 img.img-responsive").attr("src");
                        String Name = MainHostElement.select("a.text-primary b").text();

                        Elements characterNameElements = MainHostElement.select("div.col-xs-9.col-sm-8.p-a-0 > div");
                        String characterName = "";
                        if (!characterNameElements.isEmpty()) {
                            characterName = MainHostElement.select("div.col-xs-9.col-sm-8.p-a-0 > div").get(0).text();
                        }
                        String Id = MainHostElement.select("a.text-primary").attr("href");

                        CastListModel castListModel = new CastListModel(Name, characterName, "Main Role", ImageUrl, Id);
                        castListModelArrayList.add(castListModel);
                    }
                }

                Elements RegularMemberList = boxMain.select("h3.header.b-b.p-b:contains(Regular Member) + ul.list.no-border.p-b.clear li.list-item");
                if (!RegularMemberList.isEmpty()) {
                    for (Element RegularMemberElement : RegularMemberList) {
                        String ImageUrl = RegularMemberElement.select("div.p-r.p-l-0 img.img-responsive").attr("src");
                        String Name = RegularMemberElement.select("a.text-primary b").text();

                        Elements characterNameElements = RegularMemberElement.select("div.col-xs-9.col-sm-8.p-a-0 > div");
                        String characterName = "";
                        if (!characterNameElements.isEmpty()) {
                            characterName = RegularMemberElement.select("div.col-xs-9.col-sm-8.p-a-0 > div").get(0).text();
                        }
                        String Id = RegularMemberElement.select("a.text-primary").attr("href");

                        CastListModel castListModel = new CastListModel(Name, characterName, "Main Role", ImageUrl, Id);
                        castListModelArrayList.add(castListModel);
                    }
                }

                Elements guestList = boxMain.select("h3.header.b-b.p-b:contains(Guest) + ul.list.no-border.p-b.clear li.list-item");
                if (!guestList.isEmpty()) {
                    for (Element guestElement : guestList) {
                        String ImageUrl = guestElement.select("div.p-r.p-l-0 img.img-responsive").attr("src");
                        String Name = guestElement.select("a.text-primary b").text();

                        Elements characterNameElements = guestElement.select("div.col-xs-9.col-sm-8.p-a-0 > div");
                        String characterName = "";
                        if (!characterNameElements.isEmpty()) {
                            characterName = guestElement.select("div.col-xs-9.col-sm-8.p-a-0 > div").get(0).text();
                        }
                        String Id = guestElement.select("a.text-primary").attr("href");

                        CastListModel castListModel = new CastListModel(Name, characterName, "Main Role", ImageUrl, Id);
                        castListModelArrayList.add(castListModel);
                    }
                }


                handler.post(this::loadCastData);

                // Log.e("box", boxMain.outerHtml());

            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });
    }

    private void loadCastData() {

        castAdapter = new CastAdapter(castListModelArrayList, ViewAllActivity.this, "fromViewAllActivity");

        Animation animation = AnimationUtils.loadAnimation(ViewAllActivity.this, R.anim.recyclerviewscroll_top_bottom); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_top_airing.setLayoutAnimation(controller);

        int itemWidth = getResources().getDimensionPixelSize(R.dimen.item_widthSearchActivity); // Define your item width
        //CenterItemDecoration itemDecoration = new CenterItemDecoration(this, 0); // Adjust margin as needed
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewAllActivity.this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView_for_top_airing.setLayoutManager(gridLayoutManager);
        //   recyclerView_for_top_airing.addItemDecoration(new GridSpacingItemDecoration(ViewAllActivity.this, itemWidth, 3));
        recyclerView_for_top_airing.setHasFixedSize(true);
        recyclerView_for_top_airing.setAdapter(castAdapter);
        recyclerView_for_top_airing.setVisibility(View.VISIBLE);
        main_ProgressBar.setVisibility(View.GONE);

    }

    private void scrapTotalResults() {

        dramaListModelArrayList = new ArrayList<>();
        //searchArrayList.clear();

        executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {
                Document doc = Jsoup.connect(link).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();
                Log.e("total Results", totalResults);

                if (!totalResults.equals("")) {
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

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        dataNumber++;
                        DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, totalEpisodes, dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                        dramaListModelArrayList.add(dramaListModel);

                    }
                    //  Log.e("e", String.valueOf(topAiringArrayList.size()));
                    //UI Thread work here
                    handler.post(this::topAll);

                } else {
                    handler.post(() -> {
                        main_ProgressBar.setVisibility(View.GONE);
                        textView_noResult_found.setVisibility(View.VISIBLE);
                    });
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });

    }

    private void topAll() {

        topAiringAdapterActivity = new ViewAllAdapterActivity(dramaListModelArrayList, ViewAllActivity.this);

        Animation animation = AnimationUtils.loadAnimation(ViewAllActivity.this, R.anim.recyclerviewscroll_top_bottom); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_top_airing.setLayoutAnimation(controller);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewAllActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView_for_top_airing.setLayoutManager(linearLayoutManager);
        recyclerView_for_top_airing.setHasFixedSize(true);
        recyclerView_for_top_airing.setAdapter(topAiringAdapterActivity);
        main_ProgressBar.setVisibility(View.GONE);

        total = dramaListModelArrayList.get(0).getTotalResults();

        totalResult = total.substring(0, total.indexOf("re")).trim();
        String setTotalResults = totalResult + " results";
        totalResults.setText(setTotalResults);

        recyclerView_for_top_airing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!isBottom) {
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isBottom = true;
                        CalculatePages();
                    }
                }

            }
        });


    }

    private void CalculatePages() {
        float totalResults = Integer.parseInt(totalResult);
        float totalPage = totalResults / 20;
        maxPageNumber = (int) (totalResults / 20);
        if (totalPage / maxPageNumber != 1) {
            maxPageNumber++;
            LoadNextPage();
        } else {
            LoadNextPage();
        }
    }

    private void LoadNextPage() {
        pageNumber++;
        if (pageNumber <= maxPageNumber) {
            topAiringAdapterActivity.showLoading();
            recyclerView_for_top_airing.smoothScrollToPosition(dramaListModelArrayList.size());
            String getShows = link + pageNumber;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    Document doc = Jsoup.connect(getShows).get();
                    Elements box = doc.select("div.m-t");
                    Elements box1 = doc.select("div.col-lg-8");
                    String totalResults = box1.select("p.m-b-sm").text();


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
                           exception.printStackTrace();
                        }

                        dataNumber++;
                        DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, totalEpisodes, dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                        dramaListModelArrayList.add(dramaListModel);

                    }

                    handler.post(() -> {
                        //UI Thread work here
                        topAiringAdapterActivity.notifyItemInserted(dramaListModelArrayList.size());
                        topAiringAdapterActivity.hideLoading();
                        isBottom = false;
                    });

                    //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.post(() -> {
                    //UI Thread work here

                });
            });
        }

        if (pageNumber == maxPageNumber) {
            topAiringAdapterActivity.showEnd();
            recyclerView_for_top_airing.smoothScrollToPosition(dramaListModelArrayList.size()); //before was +1
        }
    }

    private ArrayList<String> getDramaListFromPrefs() {
        String stringListStr = dramaPreferences.getString("watchingDramaList", null);

        if (stringListStr == null) {
            return null;
        }
        List<String> dramaList = Arrays.asList(stringListStr.split(","));
        return new ArrayList<>(dramaList);
    }


    public static int findIndexWithText(Elements listItems, String searchText) {
        for (int i = 0; i < listItems.size(); i++) {
            Element listItem = listItems.get(i);
            if (listItem.text().contains(searchText)) {
                return i; // Found the index
            }
        }
        return -1; // Not found
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}