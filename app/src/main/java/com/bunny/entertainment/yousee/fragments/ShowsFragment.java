package com.bunny.entertainment.yousee.fragments;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.activities.GenresActivity;
import com.bunny.entertainment.yousee.activities.SearchActivity;
import com.bunny.entertainment.yousee.activities.ViewAllActivity;
import com.bunny.entertainment.yousee.adapters.ShowsAdapterFragment;
import com.bunny.entertainment.yousee.adapters.ShowsAdapterFragment2;
import com.bunny.entertainment.yousee.adapters.TopAiringAdapterFragment;
import com.bunny.entertainment.yousee.models.DramaDetailsModel;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.models.GenresModel;
import com.bunny.entertainment.yousee.models.ShowsListModel;
import com.bunny.entertainment.yousee.utils.Constants;
import com.bunny.entertainment.yousee.utils.DataHolder;
import com.bunny.entertainment.yousee.utils.InternetUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class ShowsFragment extends Fragment {

    ArrayList<DramaListModel> trendingAllArrayList;
    ArrayList<ShowsListModel> popularShowsArrayList;
    ArrayList<ShowsListModel> popularShowsViewAllArrayList;
    ArrayList<ShowsListModel> topRatedShowsArrayList;
    ArrayList<ShowsListModel> topRatedShowsViewAllArrayList;
    ArrayList<ShowsListModel> onTheAirShowsArrayList;
    ArrayList<ShowsListModel> onTheAirShowsViewAllArrayList;
    ArrayList<ShowsListModel> popularMoviesArrayList;
    ArrayList<ShowsListModel> popularMoviesViewAllArrayList;
    ArrayList<ShowsListModel> topRatedMoviesArrayList;
    ArrayList<ShowsListModel> topRatedMoviesViewAllArrayList;
    ArrayList<ShowsListModel> nowOnCinemasMoviesArrayList;
    ArrayList<ShowsListModel> nowOnCinemasMoviesViewAllArrayList;
    ArrayList<ShowsListModel> upcomingMoviesArrayList;
    ArrayList<ShowsListModel> upcomingMoviesViewAllArrayList;
    TopAiringAdapterFragment trendingAllAdapterFragment;
    ShowsAdapterFragment2 popularShowsAdapterFragment;
    ShowsAdapterFragment2 topRatedShowsAdapterFragment;
    ShowsAdapterFragment2 onTheAirShowsAdapterFragment;
    ShowsAdapterFragment2 popularMoviesAdapterFragment;
    ShowsAdapterFragment2 topRatedMoviesAdapterFragment;
    ShowsAdapterFragment2 nowOnCinemasMoviesAdapterFragment;
    ShowsAdapterFragment2 upcomingMoviesAdapterFragment;
    SnapHelper snapHelper;
    Boolean isUserScrolling = false;
    int currentItem = 0;
    Timer autoScrollTimer;
    RelativeLayout view_all_trending_all_container, view_all_popular_shows_container, view_all_top_rated_shows_container, view_all_on_the_air_shows_container,
            view_all_popular_movies_container, view_all_top_rated_movies_container, view_all_now_on_cinemas_movies_container, view_all_upcoming_movies_container;
    ProgressBar progressBar_trending_all, progressBar_popular_shows, progressBar_top_rated_shows, progressBar_on_the_air_shows, progressBar_popular_movies,
            progressBar_top_rated_movies, progressBar_now_on_cinemas_movies, progressBar_upcoming_movies;
    TextView trending_all_textview, popular_shows_textview, top_ratedShows_textview, on_the_air_Shows_textview, popular_Movies_textview, top_rated_Movies_textview,
            now_on_cinemas_Movies_textview, upcoming_Movies_textview;
    TextInputEditText edittext_search;
    TextInputLayout textview_search;
    RecyclerView recyclerView_for_trending_all, recyclerView_for_popular_shows, recyclerView_for_top_rated_shows, recyclerView_for_on_the_air_shows,
            recyclerView_for_popular_movies, recyclerView_for_top_rated_movies, recyclerView_for_now_on_cinemas_movies, recyclerView_for_upcoming_movies;
    SwipeRefreshLayout swipe_refresh_layout;
    Animation startAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shows, container, false);


        recyclerView_for_trending_all = view.findViewById(R.id.recyclerView_for_trending_all);
        progressBar_trending_all = view.findViewById(R.id.progressBar_trending_all);
        // view_all_trending_all_container = view.findViewById(R.id.view_all_trending_all_container);
        trending_all_textview = view.findViewById(R.id.trending_all_textview);

        recyclerView_for_popular_shows = view.findViewById(R.id.recyclerView_for_popular_shows);
        view_all_popular_shows_container = view.findViewById(R.id.view_all_popular_shows_container);
        popular_shows_textview = view.findViewById(R.id.popular_shows_textview);
        progressBar_popular_shows = view.findViewById(R.id.progressBar_popular_shows);

        recyclerView_for_top_rated_shows = view.findViewById(R.id.recyclerView_for_top_rated_shows);
        view_all_top_rated_shows_container = view.findViewById(R.id.view_all_top_rated_shows_container);
        top_ratedShows_textview = view.findViewById(R.id.top_ratedShows_textview);
        progressBar_top_rated_shows = view.findViewById(R.id.progressBar_top_rated_shows);

        recyclerView_for_on_the_air_shows = view.findViewById(R.id.recyclerView_for_on_the_air_shows);
        view_all_on_the_air_shows_container = view.findViewById(R.id.view_all_on_the_air_shows_container);
        on_the_air_Shows_textview = view.findViewById(R.id.on_the_air_Shows_textview);
        progressBar_on_the_air_shows = view.findViewById(R.id.progressBar_on_the_air_shows);

        recyclerView_for_popular_movies = view.findViewById(R.id.recyclerView_for_popular_movies);
        view_all_popular_movies_container = view.findViewById(R.id.view_all_popular_movies_container);
        popular_Movies_textview = view.findViewById(R.id.popular_Movies_textview);
        progressBar_popular_movies = view.findViewById(R.id.progressBar_popular_movies);

        recyclerView_for_top_rated_movies = view.findViewById(R.id.recyclerView_for_top_rated_movies);
        view_all_top_rated_movies_container = view.findViewById(R.id.view_all_top_rated_movies_container);
        top_rated_Movies_textview = view.findViewById(R.id.top_rated_Movies_textview);
        progressBar_top_rated_movies = view.findViewById(R.id.progressBar_top_rated_movies);

        recyclerView_for_now_on_cinemas_movies = view.findViewById(R.id.recyclerView_for_now_on_cinemas_movies);
        view_all_now_on_cinemas_movies_container = view.findViewById(R.id.view_all_now_on_cinemas_movies_container);
        now_on_cinemas_Movies_textview = view.findViewById(R.id.now_on_cinemas_Movies_textview);
        progressBar_now_on_cinemas_movies = view.findViewById(R.id.progressBar_now_on_cinemas_movies);

        recyclerView_for_upcoming_movies = view.findViewById(R.id.recyclerView_for_upcoming_movies);
        view_all_upcoming_movies_container = view.findViewById(R.id.view_all_upcoming_movies_container);
        upcoming_Movies_textview = view.findViewById(R.id.upcoming_Movies_textview);
        progressBar_upcoming_movies = view.findViewById(R.id.progressBar_upcoming_movies);


        edittext_search = view.findViewById(R.id.edittext_search);
        textview_search = view.findViewById(R.id.textview_search);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);

        startAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right_long);
        snapHelper = new PagerSnapHelper();

        if (getActivity() != null) {
            if (InternetUtils.isInternetAvailable(getActivity())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadTrendingAll();
                        loadPopularShows();
                        loadTopRatedShows();
                        loadOnTheAir();
                        loadPopularMovies();
                        loadTopRatedMovies();
                        loadNowOnCinemas();
                        loadUpcomingMovies();
                    }
                }, 150);
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
                if (getActivity() != null) {
                    if (InternetUtils.isInternetAvailable(getActivity())) {
                        loadTrendingAll();
                        loadPopularShows();
                        loadTopRatedShows();
                        loadOnTheAir();
                        loadPopularMovies();
                        loadTopRatedMovies();
                        loadNowOnCinemas();
                        loadUpcomingMovies();
                    }
                }
            }
        });
    }

    private void buttonsClickListener() {

        view_all_popular_shows_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "PopularShowsFragment");
                DataHolder.setPopularShowsListModelArrayList(popularShowsViewAllArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        view_all_top_rated_shows_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "TopRatedShowsFragment");
                DataHolder.setTopRatedShowsListModelArrayList(topRatedShowsViewAllArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        view_all_on_the_air_shows_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "OnTheAirShowsFragment");
                DataHolder.setOnTheAirShowsListModelArrayList(onTheAirShowsViewAllArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        view_all_popular_movies_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "PopularMoviesShowsFragment");
                DataHolder.setPopularMoviesListModelArrayList(popularMoviesViewAllArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        view_all_top_rated_movies_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "TopRatedMoviesShowsFragment");
                DataHolder.setTopRatedMoviesListModelArrayList(topRatedMoviesViewAllArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        view_all_now_on_cinemas_movies_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "NowOnCinemasMoviesShowsFragment");
                DataHolder.setNowOnCinemasMoviesListModelArrayList(nowOnCinemasMoviesViewAllArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        view_all_upcoming_movies_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "UpcomingMoviesShowsFragment");
                DataHolder.setUpcomingMoviesListModelArrayList(upcomingMoviesViewAllArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        edittext_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("whereFrom", "ShowsFragment");
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    private void loadUpcomingMovies() {
        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    String topRatedMovies= "https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=1&region=IN";

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, topRatedMovies, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            upcomingMoviesArrayList = new ArrayList<>();
                            upcomingMoviesViewAllArrayList = new ArrayList<>();

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
                                    String mediaType = "movie";
                                 /*   if(jsonObjectContent.has("media_type")){
                                        mediaType  = jsonObjectContent.getString("media_type");
                                    }*/
                                    String rating = "";
                                    if (jsonObjectContent.has("vote_average")){
                                        rating = jsonObjectContent.getString("vote_average");
                                        rating = rating.substring(0, rating.indexOf(".") + 2);
                                        if (!rating.contains(".")){
                                            rating = rating+".0";
                                        }
                                    }

                                    String totalPages = jsonObject.getString("total_pages");
                                    String totalResults = jsonObject.getString("total_results");

                                    ShowsListModel showsListModel = new ShowsListModel(name, rating, imageUrl, totalPages, totalResults, mediaType, TMDBid, String.valueOf(i + 1));
                                    upcomingMoviesArrayList.add(showsListModel);
                                    upcomingMoviesViewAllArrayList.add(showsListModel);
                                    //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                                }

                                upcomingMoviesShowData();

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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

                handler.post(() -> {
                    //UI Thread work here
                });
            });
            executor.shutdown();
        }
    }

    private void upcomingMoviesShowData() {

        if (getActivity() != null) {
            upcomingMoviesAdapterFragment = new ShowsAdapterFragment2(upcomingMoviesArrayList, getActivity());
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_upcoming_movies.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_upcoming_movies.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_upcoming_movies.setAdapter(upcomingMoviesAdapterFragment);
            view_all_upcoming_movies_container.setVisibility(View.VISIBLE);
            upcoming_Movies_textview.setVisibility(View.VISIBLE);
            progressBar_upcoming_movies.setVisibility(View.GONE);
        }
    }

    private void loadNowOnCinemas() {
        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    String topRatedMovies= "https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1&region=IN";

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, topRatedMovies, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            nowOnCinemasMoviesArrayList = new ArrayList<>();
                            nowOnCinemasMoviesViewAllArrayList = new ArrayList<>();

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
                                    String mediaType = "movie";
                                 /*   if(jsonObjectContent.has("media_type")){
                                        mediaType  = jsonObjectContent.getString("media_type");
                                    }*/
                                    String rating = "";
                                    if (jsonObjectContent.has("vote_average")){
                                        rating = jsonObjectContent.getString("vote_average");
                                        rating = rating.substring(0, rating.indexOf(".") + 2);
                                        if (!rating.contains(".")){
                                            rating = rating+".0";
                                        }
                                    }

                                    String totalPages = jsonObject.getString("total_pages");
                                    String totalResults = jsonObject.getString("total_results");

                                    ShowsListModel showsListModel = new ShowsListModel(name, rating, imageUrl, totalPages, totalResults, mediaType, TMDBid, String.valueOf(i + 1));
                                    nowOnCinemasMoviesArrayList.add(showsListModel);
                                    nowOnCinemasMoviesViewAllArrayList.add(showsListModel);
                                    //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                                }

                                nowOnCinemasMoviesShowData();

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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

                handler.post(() -> {
                    //UI Thread work here
                });
            });
            executor.shutdown();
        }
    }

    private void nowOnCinemasMoviesShowData() {
        if (getActivity() != null) {
            nowOnCinemasMoviesAdapterFragment = new ShowsAdapterFragment2(nowOnCinemasMoviesArrayList, getActivity());
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_now_on_cinemas_movies.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_now_on_cinemas_movies.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_now_on_cinemas_movies.setAdapter(nowOnCinemasMoviesAdapterFragment);
            view_all_now_on_cinemas_movies_container.setVisibility(View.VISIBLE);
            now_on_cinemas_Movies_textview.setVisibility(View.VISIBLE);
            progressBar_now_on_cinemas_movies.setVisibility(View.GONE);
        }
    }

    private void loadTopRatedMovies() {

        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    String topRatedMovies= "https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1&region=IN";

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, topRatedMovies, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            topRatedMoviesArrayList = new ArrayList<>();
                            topRatedMoviesViewAllArrayList = new ArrayList<>();

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
                                    String mediaType = "movie";
                                 /*   if(jsonObjectContent.has("media_type")){
                                        mediaType  = jsonObjectContent.getString("media_type");
                                    }*/
                                    String rating = "";
                                    if (jsonObjectContent.has("vote_average")){
                                        rating = jsonObjectContent.getString("vote_average");
                                        rating = rating.substring(0, rating.indexOf(".") + 2);
                                        if (!rating.contains(".")){
                                            rating = rating+".0";
                                        }
                                    }

                                    String totalPages = jsonObject.getString("total_pages");
                                    String totalResults = jsonObject.getString("total_results");

                                    ShowsListModel showsListModel = new ShowsListModel(name, rating, imageUrl, totalPages, totalResults, mediaType, TMDBid, String.valueOf(i + 1));
                                    topRatedMoviesArrayList.add(showsListModel);
                                    topRatedMoviesViewAllArrayList.add(showsListModel);
                                    //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                                }

                                topRatedMoviesShowData();

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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

                handler.post(() -> {
                    //UI Thread work here
                });
            });
            executor.shutdown();
        }
    }

    private void topRatedMoviesShowData() {
        if (getActivity() != null) {
            topRatedMoviesAdapterFragment = new ShowsAdapterFragment2(topRatedMoviesArrayList, getActivity());
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_top_rated_movies.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_top_rated_movies.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_top_rated_movies.setAdapter(topRatedMoviesAdapterFragment);
            view_all_top_rated_movies_container.setVisibility(View.VISIBLE);
            top_rated_Movies_textview.setVisibility(View.VISIBLE);
            progressBar_top_rated_movies.setVisibility(View.GONE);
        }
    }

    private void loadPopularMovies() {

        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    String popularMovies= "https://api.themoviedb.org/3/movie/popular?language=en-US&page=1&region=IN";

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, popularMovies, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            popularMoviesArrayList = new ArrayList<>();
                            popularMoviesViewAllArrayList = new ArrayList<>();

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
                                    String mediaType = "movie";
                                 /*   if(jsonObjectContent.has("media_type")){
                                        mediaType  = jsonObjectContent.getString("media_type");
                                    }*/
                                    String rating = "";
                                    if (jsonObjectContent.has("vote_average")){
                                        rating = jsonObjectContent.getString("vote_average");
                                        rating = rating.substring(0, rating.indexOf(".") + 2);
                                        if (!rating.contains(".")){
                                            rating = rating+".0";
                                        }
                                    }

                                    String totalPages = jsonObject.getString("total_pages");
                                    String totalResults = jsonObject.getString("total_results");

                                    ShowsListModel showsListModel = new ShowsListModel(name, rating, imageUrl, totalPages, totalResults, mediaType, TMDBid, String.valueOf(i + 1));
                                    popularMoviesArrayList.add(showsListModel);
                                    popularMoviesViewAllArrayList.add(showsListModel);
                                    //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                                }

                                popularMoviesShowData();

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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

                handler.post(() -> {
                    //UI Thread work here
                });
            });
            executor.shutdown();
        }
    }

    private void popularMoviesShowData() {
        if (getActivity() != null) {
            popularMoviesAdapterFragment = new ShowsAdapterFragment2(popularMoviesArrayList, getActivity());
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_popular_movies.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_popular_movies.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_popular_movies.setAdapter(popularMoviesAdapterFragment);
            view_all_popular_movies_container.setVisibility(View.VISIBLE);
            popular_Movies_textview.setVisibility(View.VISIBLE);
            progressBar_popular_movies.setVisibility(View.GONE);

        }
    }

    private void loadOnTheAir() {
        
        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    String onTheAir= "https://api.themoviedb.org/3/tv/on_the_air?language=en-US&page=1";

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, onTheAir, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            onTheAirShowsArrayList = new ArrayList<>();
                            onTheAirShowsViewAllArrayList = new ArrayList<>();

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
                                    String mediaType = "tv";
                                 /*   if(jsonObjectContent.has("media_type")){
                                        mediaType  = jsonObjectContent.getString("media_type");
                                    }*/
                                    String rating = "";
                                    if (jsonObjectContent.has("vote_average")){
                                        rating = jsonObjectContent.getString("vote_average");
                                        rating = rating.substring(0, rating.indexOf(".") + 2);
                                        if (!rating.contains(".")){
                                            rating = rating+".0";
                                        }
                                    }

                                    String totalPages = jsonObject.getString("total_pages");
                                    String totalResults = jsonObject.getString("total_results");

                                    ShowsListModel showsListModel = new ShowsListModel(name, rating, imageUrl, totalPages, totalResults, mediaType, TMDBid, String.valueOf(i + 1));
                                    onTheAirShowsArrayList.add(showsListModel);
                                    onTheAirShowsViewAllArrayList.add(showsListModel);
                                    //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                                }

                                onTheAirShowsData();

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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

                handler.post(() -> {
                    //UI Thread work here
                });
            });
            executor.shutdown();
        }
    }

    private void onTheAirShowsData() {
        if (getActivity() != null) {
            onTheAirShowsAdapterFragment = new ShowsAdapterFragment2(onTheAirShowsArrayList, getActivity());
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_on_the_air_shows.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_on_the_air_shows.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_on_the_air_shows.setAdapter(onTheAirShowsAdapterFragment);
            view_all_on_the_air_shows_container.setVisibility(View.VISIBLE);
            on_the_air_Shows_textview.setVisibility(View.VISIBLE);
            progressBar_on_the_air_shows.setVisibility(View.GONE);

        }
    }

    private void loadTopRatedShows() {

        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    String topRatedShows = "https://api.themoviedb.org/3/tv/top_rated?language=en-US&page=1";

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, topRatedShows, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            topRatedShowsArrayList = new ArrayList<>();
                            topRatedShowsViewAllArrayList = new ArrayList<>();

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
                                    String mediaType = "tv";
                                 /*   if(jsonObjectContent.has("media_type")){
                                        mediaType  = jsonObjectContent.getString("media_type");
                                    }*/
                                    String rating = "";
                                    if (jsonObjectContent.has("vote_average")){
                                        rating = jsonObjectContent.getString("vote_average");
                                        rating = rating.substring(0, rating.indexOf(".") + 2);
                                        if (!rating.contains(".")){
                                            rating = rating+".0";
                                        }
                                    }

                                    String totalPages = jsonObject.getString("total_pages");
                                    String totalResults = jsonObject.getString("total_results");

                                    ShowsListModel showsListModel = new ShowsListModel(name, rating, imageUrl, totalPages, totalResults, mediaType, TMDBid, String.valueOf(i + 1));
                                    topRatedShowsArrayList.add(showsListModel);
                                    topRatedShowsViewAllArrayList.add(showsListModel);
                                    //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                                }

                                showTopRatedShowsData();

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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

                handler.post(() -> {
                    //UI Thread work here

                });
            });
            executor.shutdown();
        }

    }

    private void showTopRatedShowsData() {
        if (getActivity() != null) {
            topRatedShowsAdapterFragment = new ShowsAdapterFragment2(topRatedShowsArrayList, getActivity());
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_top_rated_shows.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_top_rated_shows.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_top_rated_shows.setAdapter(topRatedShowsAdapterFragment);
            view_all_top_rated_shows_container.setVisibility(View.VISIBLE);
            top_ratedShows_textview.setVisibility(View.VISIBLE);
            progressBar_top_rated_shows.setVisibility(View.GONE);

        }
    }

    private void loadPopularShows() {

        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    String popularShows = "https://api.themoviedb.org/3/tv/popular?language=en-US&page=1";

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, popularShows, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            popularShowsArrayList = new ArrayList<>();
                            popularShowsViewAllArrayList = new ArrayList<>();

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
                                    String mediaType = "tv";
                                 /*   if(jsonObjectContent.has("media_type")){
                                        mediaType  = jsonObjectContent.getString("media_type");
                                    }*/
                                    String rating = "";
                                    if (jsonObjectContent.has("vote_average")){
                                        rating = jsonObjectContent.getString("vote_average");
                                        rating = rating.substring(0, rating.indexOf(".") + 2);
                                        if (!rating.contains(".")){
                                            rating = rating+".0";
                                        }
                                    }

                                    String totalPages = jsonObject.getString("total_pages");
                                    String totalResults = jsonObject.getString("total_results");

                                    ShowsListModel showsListModel = new ShowsListModel(name, rating, imageUrl, totalPages, totalResults, mediaType, TMDBid, String.valueOf(i + 1));
                                    popularShowsArrayList.add(showsListModel);
                                    popularShowsViewAllArrayList.add(showsListModel);
                                    //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                                }

                                showPopularShowsData();

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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

                handler.post(() -> {
                    //UI Thread work here

                });
            });
            executor.shutdown();
        }
    }

    private void showPopularShowsData() {

        if (getActivity() != null) {
            popularShowsAdapterFragment = new ShowsAdapterFragment2(popularShowsArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_popular_shows.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_popular_shows.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_popular_shows.setAdapter(popularShowsAdapterFragment);
            view_all_popular_shows_container.setVisibility(View.VISIBLE);
            popular_shows_textview.setVisibility(View.VISIBLE);
            progressBar_popular_shows.setVisibility(View.GONE);

        }

    }

    private void loadTrendingAll() {

        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    String trendingAll = "https://api.themoviedb.org/3/trending/all/day?language=en-US";

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, trendingAll, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            trendingAllArrayList = new ArrayList<>();

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
                                    String mediaType = jsonObjectContent.getString("media_type");
                                    imageUrl = "https://image.tmdb.org/t/p/w200" + imageUrl;
                                    String rating = jsonObjectContent.getString("vote_average");
                                    rating = rating.substring(0, rating.indexOf(".") + 2);

                                    DramaListModel showsListModel = new DramaListModel(name, "", "", rating, imageUrl, mediaType, TMDBid, String.valueOf(i + 1));
                                    trendingAllArrayList.add(showsListModel);
                                    //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                                }

                                showTrendingAllData();

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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

                handler.post(() -> {
                    //UI Thread work here

                });
            });
            executor.shutdown();
        }
    }

    private void showTrendingAllData() {

        if (getActivity() != null) {
            trendingAllAdapterFragment = new TopAiringAdapterFragment(trendingAllArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_trending_all.setLayoutAnimation(controller);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
            recyclerView_for_trending_all.setLayoutManager(gridLayoutManager);
            snapHelper.attachToRecyclerView(recyclerView_for_trending_all);
            trending_all_textview.setVisibility(View.VISIBLE);
            progressBar_trending_all.setVisibility(View.GONE);
            recyclerView_for_trending_all.setAdapter(trendingAllAdapterFragment);
            //view_all_trending_all_container.setVisibility(View.VISIBLE);

            //for genres random image using top airing images
            // DataHolder.setTopAiringDramaListModelArrayList(topAiringDramaListModelArrayList);

            startAutoScrollTimer();

            recyclerView_for_trending_all.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        }
        textview_search.setVisibility(View.VISIBLE);
        textview_search.startAnimation(startAnimation);
        swipe_refresh_layout.setRefreshing(false);

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
        if (trendingAllAdapterFragment != null && trendingAllAdapterFragment.getItemCount() > 0) {
            int nextItem = (currentItem + 1) % trendingAllAdapterFragment.getItemCount();
            recyclerView_for_trending_all.smoothScrollToPosition(nextItem);
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