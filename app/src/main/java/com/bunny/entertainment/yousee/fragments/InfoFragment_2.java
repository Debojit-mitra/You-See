package com.bunny.entertainment.yousee.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.CastAdapter;
import com.bunny.entertainment.yousee.adapters.InfoGenresAdapter;
import com.bunny.entertainment.yousee.adapters.WhereToWatchAdapter;
import com.bunny.entertainment.yousee.models.CastListModel;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.models.DramaMovieEpisodeSecondModel;
import com.bunny.entertainment.yousee.models.WhereToWatchModel;
import com.bunny.entertainment.yousee.utils.Constants;
import com.bunny.entertainment.yousee.utils.JsonUtils;
import com.flaviofaria.kenburnsview.KenBurnsView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class InfoFragment_2 extends Fragment {


    String showId, showUrl, mediaType, imdbIDRatingUrl;
    String showImage, showName, showSynopsis, showCountry, showEpisodes, showAired,
            showOriginalNetwork, showDuration, showRating, showPopularity, showCreatedBy, isAdultShow, showStatus, showNumberOfSeasons, imdbRating;

    KenBurnsView animatedImageBackground;
    ImageView showThumbnail;
    TextView textView_showRating, releasing_show, textView_showName, textView_showType, textView_showRating2, textView_showIMDBRating2,
            textView_showEpisodes, textView_showCountry, textView_showAired, textView_showDuration, textView_showPopularity, textView_OriginalNetwork, textView_Adult,
            textView_overview, textView_showSeasons, textView_showCreatedBy;
    ProgressBar thumbnailProgress, main_Progress, episodeProgressBar;
    RelativeLayout first_container, relativeNumberOfSeasons;
    LinearLayout second_container;
    ArrayList<String> genresArrayList;
    ArrayList<CastListModel> castListModelArrayList;
    ArrayList<WhereToWatchModel> whereToWatchModelArrayList;
    InfoGenresAdapter infoGenresAdapter;
    CastAdapter castAdapter;
    WhereToWatchAdapter whereToWatchAdapter;
    RecyclerView recyclerView_for_genres, recyclerView_for_cast, recyclerView_for_watchHere;
    Animation startAnimation, endAnimation;
    TextView textView_showGenres_text, textView_watchHere_text, episodeReleasing, episodeDateTimer, episodeAiringInfo;
    RelativeLayout third_container, fifth_container_text, view_all_cast_container;
    WebView webView;
    private static final long COUNTDOWN_INTERVAL = 1000;
    Handler handlerNew;
    String episodeNumber, releaseDateTime;
    CountDownTimer countDownTimer;
    boolean isPaused = false;
    SharedPreferences dramaPreferences;
    boolean isMovie = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_2, container, false);

        first_container = view.findViewById(R.id.first_container);
        second_container = view.findViewById(R.id.second_container);
        main_Progress = view.findViewById(R.id.main_Progress);
        recyclerView_for_genres = view.findViewById(R.id.recyclerView_for_genres);
        recyclerView_for_cast = view.findViewById(R.id.recyclerView_for_cast);
        textView_showGenres_text = view.findViewById(R.id.textView_showGenres_text);
        fifth_container_text = view.findViewById(R.id.fifth_container_text);
        view_all_cast_container = view.findViewById(R.id.view_all_cast_container);
        third_container = view.findViewById(R.id.third_container);
        recyclerView_for_watchHere = view.findViewById(R.id.recyclerView_for_watchHere);
        textView_watchHere_text = view.findViewById(R.id.textView_watchHere_text);


        animatedImageBackground = view.findViewById(R.id.animatedImageBackground);
        showThumbnail = view.findViewById(R.id.showThumbnail);
        textView_showRating = view.findViewById(R.id.textView_showRating);
        releasing_show = view.findViewById(R.id.releasing_show);
        textView_showName = view.findViewById(R.id.textView_showName);
        textView_showType = view.findViewById(R.id.textView_showType);
        textView_showRating2 = view.findViewById(R.id.textView_showRating2);
        textView_showIMDBRating2 = view.findViewById(R.id.textView_showIMDBRating2);
        textView_showEpisodes = view.findViewById(R.id.textView_showEpisodes);
        textView_showCountry = view.findViewById(R.id.textView_showCountry);
        textView_showAired = view.findViewById(R.id.textView_showAired);
        textView_showDuration = view.findViewById(R.id.textView_showDuration);
        textView_showPopularity = view.findViewById(R.id.textView_showPopularity);
        textView_OriginalNetwork = view.findViewById(R.id.textView_OriginalNetwork);
        textView_Adult = view.findViewById(R.id.textView_Adult);
        textView_overview = view.findViewById(R.id.textView_overview);
        textView_showSeasons = view.findViewById(R.id.textView_showSeasons);
        textView_showCreatedBy = view.findViewById(R.id.textView_showCreatedBy);
        relativeNumberOfSeasons = view.findViewById(R.id.relativeNumberOfSeasons);
        thumbnailProgress = view.findViewById(R.id.thumbnailProgress);
        episodeReleasing = view.findViewById(R.id.episodeReleasing);
        episodeDateTimer = view.findViewById(R.id.episodeDateTimer);
        episodeProgressBar = view.findViewById(R.id.episodeProgressBar);
        episodeAiringInfo = view.findViewById(R.id.episodeAiringInfo);


        if (getActivity() != null) {
            startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
            endAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
        }


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            showId = bundle.getString("dramaId");
            Log.e("showId", showId);
            mediaType = bundle.getString("mediaType");
        }

        if (getActivity() != null) {
            dramaPreferences = getActivity().getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
            dramaPreferences.edit().remove("showName").apply();
            dramaPreferences.edit().remove("showIdTMDB").apply();
            dramaPreferences.edit().remove("showMediaType").apply();
            dramaPreferences.edit().remove("showImage").apply();
        }


        LoadShowData();


        return view;
    }

    private void LoadShowData() {


        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    showUrl = Constants.TMDB_URL_MAIN + mediaType + "/" + showId + "?language=en-US";

                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, showUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                // JSONArray jsonArray = jsonObject.getJSONArray("results");

                                if (jsonObject.has("adult")) {
                                    isAdultShow = jsonObject.getString("adult");
                                    if (isAdultShow.contains("false")) {
                                        isAdultShow = "No";
                                    } else if (isAdultShow.contains("true")) {
                                        isAdultShow = "Yes";
                                    }
                                } else {
                                    isAdultShow = getString(R.string.n_a);
                                }

                                if (mediaType.contains("tv")) {

                                    if (jsonObject.has("episode_run_time")) {
                                        JSONArray jsonArrayShowDuration = jsonObject.getJSONArray("episode_run_time");
                                        if (jsonArrayShowDuration.length() != 0) {
                                            showDuration = jsonArrayShowDuration.getString(0) + " min";
                                        }
                                    }

                                    if (jsonObject.has("number_of_episodes")) {
                                        showEpisodes = jsonObject.getString("number_of_episodes");
                                    } else {
                                        showEpisodes = getString(R.string.n_a);
                                    }

                                } else if (mediaType.contains("movie")) {
                                    if (jsonObject.has("runtime")) {
                                        showDuration = jsonObject.getString("runtime") + " min";
                                    }

                                    showEpisodes = "1";
                                }

                                if (showDuration == null) {
                                    showDuration = getString(R.string.n_a);
                                }


                                String firstAired = "", lastAired = "";
                                if (jsonObject.has("first_air_date")) {
                                    firstAired = jsonObject.getString("first_air_date");
                                }
                                if (jsonObject.has("last_air_date")) {
                                    lastAired = jsonObject.getString("last_air_date");
                                }
                                if (!firstAired.isEmpty() && !lastAired.isEmpty()) {
                                    showAired = formatDate(firstAired) + " - " + formatDate(lastAired);
                                } else if (!firstAired.isEmpty()) {
                                    showAired = formatDate(firstAired);
                                } else if (jsonObject.has("release_date")) {
                                    showAired = formatDate(jsonObject.getString("release_date"));
                                } else {
                                    showAired = getString(R.string.n_a);
                                }

                                if (jsonObject.has("genres")) {
                                    genresArrayList = new ArrayList<>();
                                    JSONArray jsonArrayGenres = jsonObject.getJSONArray("genres");
                                    for (int i = 0; i < jsonArrayGenres.length(); i++) {
                                        JSONObject jsonObjectGenres = jsonArrayGenres.getJSONObject(i);
                                        String genres = jsonObjectGenres.getString("name");
                                        genresArrayList.add(genres);
                                    }
                                }

                                if (jsonObject.has("name")) {
                                    showName = jsonObject.getString("name");
                                } else if (jsonObject.has("title")) {
                                    showName = jsonObject.getString("title");
                                } else if (jsonObject.has("original_name")) {
                                    showName = jsonObject.getString("original_name");
                                } else if (jsonObject.has("original_title")) {
                                    showName = jsonObject.getString("original_title");
                                }

                                showImage = jsonObject.getString("poster_path");
                                showImage = "https://image.tmdb.org/t/p/w300" + showImage;

                                if (jsonObject.has("vote_average")) {
                                    showRating = jsonObject.getString("vote_average");
                                    showRating = showRating.substring(0, showRating.indexOf(".") + 2);
                                } else {
                                    showRating = getString(R.string.n_a);
                                }


                                if (jsonObject.has("overview")) {
                                    showSynopsis = jsonObject.getString("overview");
                                } else {
                                    showSynopsis = getString(R.string.n_a);
                                }

                                if (jsonObject.has("origin_country")) {
                                    JSONArray jsonArrayShowCountry = jsonObject.getJSONArray("origin_country");
                                    try {
                                        showCountry = jsonArrayShowCountry.getString(0);
                                        JSONObject jsonObjectForResults = JsonUtils.readJsonObjectFromRaw(getResources(), R.raw.country_code);
                                        JSONArray jsonArrayForResults = jsonObjectForResults.getJSONArray("results");
                                        for (int i = 0; i < jsonArrayForResults.length(); i++) {
                                            JSONObject jsonObjectCountryCodes = jsonArrayForResults.getJSONObject(i);
                                            if (jsonObjectCountryCodes.getString("code").equals(showCountry.toUpperCase(Locale.ENGLISH))) {
                                                showCountry = jsonObjectCountryCodes.getString("name");
                                                break;
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    showCountry = getString(R.string.n_a);
                                }

                                if (jsonObject.has("popularity")) {
                                    showPopularity = jsonObject.getString("popularity");
                                } else {
                                    showPopularity = getString(R.string.n_a);
                                }

                                if (jsonObject.has("created_by")) {
                                    JSONArray jsonArrayCreatedBy = jsonObject.getJSONArray("created_by");
                                    if (jsonArrayCreatedBy.length() != 0) {
                                        for (int i = 0; i < jsonArrayCreatedBy.length(); i++) {
                                            if (showCreatedBy == null) {
                                                JSONObject jsonObjectCreatedBy = jsonArrayCreatedBy.getJSONObject(i);
                                                showCreatedBy = jsonObjectCreatedBy.getString("name");
                                            } else {
                                                JSONObject jsonObjectCreatedBy = jsonArrayCreatedBy.getJSONObject(i);
                                                showCreatedBy = showCreatedBy + ", " + jsonObjectCreatedBy.getString("name");
                                            }
                                        }
                                    } else {
                                        showCreatedBy = getString(R.string.n_a);
                                    }
                                } else {
                                    showCreatedBy = getString(R.string.n_a);
                                }

                                if (jsonObject.has("production_companies")) {
                                    JSONArray jsonArrayProduction_companies = jsonObject.getJSONArray("production_companies");
                                    if (jsonArrayProduction_companies.length() != 0) {
                                        for (int i = 0; i < jsonArrayProduction_companies.length(); i++) {
                                            if (showOriginalNetwork == null) {
                                                JSONObject jsonObjectProduction_companies = jsonArrayProduction_companies.getJSONObject(i);
                                                showOriginalNetwork = jsonObjectProduction_companies.getString("name");
                                            } else {
                                                JSONObject jsonObjectProduction_companies = jsonArrayProduction_companies.getJSONObject(i);
                                                showOriginalNetwork = showOriginalNetwork + ", " + jsonObjectProduction_companies.getString("name");
                                            }
                                        }
                                    } else {
                                        showOriginalNetwork = getString(R.string.n_a);
                                    }
                                }

                                if (jsonObject.has("networks")) {
                                    JSONArray jsonArrayNetworks = jsonObject.getJSONArray("networks");
                                    if (jsonArrayNetworks.length() != 0) {
                                        whereToWatchModelArrayList = new ArrayList<>();
                                        for (int i = 0; i < jsonArrayNetworks.length(); i++) {
                                            String ottName, ottImageUrl = "", ottOrigin = "";
                                            JSONObject jsonObjectNetworks = jsonArrayNetworks.getJSONObject(i);
                                            ottName = jsonObjectNetworks.getString("name");
                                            if (jsonObjectNetworks.has("logo_path")) {
                                                ottImageUrl = jsonObjectNetworks.getString("logo_path");
                                                ottImageUrl = "https://image.tmdb.org/t/p/w300" + ottImageUrl;
                                            }
                                            if (jsonObjectNetworks.has("origin_country")) {
                                                ottOrigin = jsonObjectNetworks.getString("origin_country");
                                            }
                                            WhereToWatchModel whereToWatchModel = new WhereToWatchModel(ottName, ottImageUrl, ottOrigin);
                                            whereToWatchModelArrayList.add(whereToWatchModel);
                                        }
                                    }
                                }

                                if (jsonObject.has("status")) {
                                    showStatus = jsonObject.getString("status");
                                } else {
                                    showStatus = getString(R.string.n_a);
                                }

                                if (mediaType.contains("tv")) {
                                    if (jsonObject.has("number_of_seasons")) {
                                        showNumberOfSeasons = jsonObject.getString("number_of_seasons");
                                    } else {
                                        showNumberOfSeasons = getString(R.string.n_a);
                                    }
                                }

                                handler.post(() -> {
                                    setDataToSharedPref();
                                    setDataToUI();
                                });

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

                handler.post(() -> {
                    //UI Thread work here

                });
            });


            if (mediaType.contains("tv")) {
                imdbIDRatingUrl = "https://api.themoviedb.org/3/tv/" + showId + "/external_ids";
            } else if (mediaType.contains("movie")) {
                imdbIDRatingUrl = "https://api.themoviedb.org/3/movie/" + showId + "/external_ids";
            }

            ExecutorService executor2 = Executors.newSingleThreadExecutor();
            Handler handler2 = new Handler(Looper.getMainLooper());
            executor2.execute(() -> {
                //Background work here

                Map<String, String> headers = new HashMap<>();
                headers.put("accept", "application/json");
                headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, imdbIDRatingUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String imdbID = jsonObject.getString("imdb_id");

                                if (!imdbID.isEmpty()) {
                                    getIMDBRating(imdbID);
                                }

                                handler2.post(() -> {

                                });

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

                handler2.post(() -> {
                    //UI Thread work here

                });
            });


        }
    }

    private void getIMDBRating(String imdbID) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {

            try {

                String imdbUrl = "https://www.imdb.com/title/" + imdbID + "/ratings/";
                Document doc = Jsoup.connect(imdbUrl).get();
                Elements box = doc.select("div.sc-5931bdee-0.jtILVy").select("div.sc-5931bdee-2.fdOlOf").select("span.sc-5931bdee-1.jUnWeS");
                //String rating = box.select("img.img-responsive").attr("src"); //1
                if(!box.text().isEmpty()){
                    imdbRating = box.text()+"/10";
                    handler.post(() -> {
                        //UI Thread work here
                        textView_showIMDBRating2.setText(imdbRating);
                        textView_showIMDBRating2.startAnimation(startAnimation);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        });


    }

    private void setDataToUI() {


        if (getActivity() != null) {
            try {

                Glide.with(getActivity())
                        .load(showImage)
                        .transform(new BlurTransformation(20, 2))
                        .into(animatedImageBackground);

                Glide.with(getActivity()).load(showImage).placeholder(R.drawable.image_not_available).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }
                }).into(showThumbnail);


                textView_showName.setText(showName);
                textView_showName.startAnimation(startAnimation);

                if (mediaType.contains("movie")) {
                    textView_showType.setText(R.string.movie);
                    textView_showType.startAnimation(startAnimation);
                }
                if (mediaType.contains("tv")) {
                    if (showNumberOfSeasons != null) {
                        relativeNumberOfSeasons.setVisibility(View.VISIBLE);
                        textView_showSeasons.setText(showNumberOfSeasons);
                        textView_showSeasons.startAnimation(startAnimation);
                    }
                }

                textView_showRating.setText(showRating);
                textView_showRating.startAnimation(startAnimation);

                String dramaRating2 = showRating + "/10";
                textView_showRating2.setText(dramaRating2);
                textView_showRating2.startAnimation(startAnimation);

                textView_showEpisodes.setText(showEpisodes);
                textView_showEpisodes.startAnimation(startAnimation);

                textView_showCountry.setText(showCountry);
                textView_showCountry.startAnimation(startAnimation);

                textView_showAired.setText(showAired);
                textView_showAired.startAnimation(startAnimation);

                textView_showDuration.setText(showDuration);
                textView_showDuration.startAnimation(startAnimation);

                textView_showPopularity.setText(showPopularity);
                textView_showPopularity.startAnimation(startAnimation);

                textView_Adult.setText(isAdultShow);
                textView_Adult.startAnimation(startAnimation);

                releasing_show.setText(showStatus);
                releasing_show.startAnimation(startAnimation);

                textView_OriginalNetwork.setText(showOriginalNetwork);
                textView_OriginalNetwork.startAnimation(startAnimation);

                textView_overview.setText(showSynopsis);
                textView_overview.startAnimation(startAnimation);

                textView_showCreatedBy.setText(showCreatedBy);
                textView_showCreatedBy.startAnimation(startAnimation);

                if (genresArrayList != null) {
                    textView_showGenres_text.setVisibility(View.VISIBLE);
                    infoGenresAdapter = new InfoGenresAdapter(genresArrayList, getActivity(), "FromShowsFragment");
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView_for_genres.setLayoutManager(linearLayoutManager);
                    recyclerView_for_genres.setAdapter(infoGenresAdapter);
                    recyclerView_for_genres.setVisibility(View.VISIBLE);
                } else {
                    third_container.setVisibility(View.GONE);
                }


                //load where tp watch
                if (whereToWatchModelArrayList != null) {
                    whereToWatchAdapter = new WhereToWatchAdapter(whereToWatchModelArrayList, getActivity());
                    LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView_for_watchHere.setLayoutManager(linearLayoutManager3);
                    //recyclerView_for_cast.addItemDecoration(new CenteredHorizontalGridItemDecoration(requireContext(), itemWidth, 2));
                    recyclerView_for_watchHere.setAdapter(whereToWatchAdapter);
                    textView_watchHere_text.setVisibility(View.VISIBLE);
                    recyclerView_for_watchHere.setVisibility(View.VISIBLE);
                }


                main_Progress.setVisibility(View.GONE);
                first_container.setVisibility(View.VISIBLE);
                second_container.setVisibility(View.VISIBLE);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private void setDataToSharedPref() {
        if (getActivity() != null) {
            dramaPreferences.edit().putString("showName", showName).apply();
            dramaPreferences.edit().putString("showIdTMDB", showId).apply();
            dramaPreferences.edit().putString("showMediaType", mediaType).apply();
            dramaPreferences.edit().putString("showImage", showImage).apply();
        }
    }

    private String formatDate(String getDate) {
        try {
            // Define the input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
            Date inputDate = inputFormat.parse(getDate);
            if (inputDate != null) {
                return outputFormat.format(inputDate);
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle the error appropriately in your code
        }
    }

}