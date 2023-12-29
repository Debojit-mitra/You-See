package com.bunny.entertainment.yousee.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.activities.ViewAllActivity;
import com.bunny.entertainment.yousee.adapters.ContinueWatchingAdapter;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.utils.Constants;
import com.bunny.entertainment.yousee.utils.DataHolder;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomeFragment extends Fragment {


    RecyclerView recyclerView_for_watching;
    ProgressBar progressBar_watching;
    RelativeLayout view_all_watching_container;
    TextView userName_greetings_textview, watching_textview, startWatching_textview;
    SwipeRefreshLayout swipe_refresh_layout;
    SharedPreferences dramaPreferences;
    ArrayList<String> continueWatchingDramaIDsArrayListSaved;
    ArrayList<DramaListModel> continueWatchingDramaDetailsArrayList;
    ContinueWatchingAdapter continueWatchingAdapter;
    String oldDataCheck;
    int dataNumberForContinueWatching;
    int i = 0;
    boolean isPaused = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView_for_watching = view.findViewById(R.id.recyclerView_for_watching);
        progressBar_watching = view.findViewById(R.id.progressBar_watching);
        view_all_watching_container = view.findViewById(R.id.view_all_watching_container);
        userName_greetings_textview = view.findViewById(R.id.userName_greetings_textview);
        startWatching_textview = view.findViewById(R.id.startWatching_textview);
        watching_textview = view.findViewById(R.id.watching_textview);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);

        if (getActivity() != null) {
            dramaPreferences = getActivity().getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
        }

        setGreetings();
        ShowContinueWatching();
        buttons();


        return view;
    }

    private void setGreetings() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        // Set the greeting based on the time of day
        String greeting;
        if (hourOfDay >= 6 && hourOfDay < 12) {
            greeting = "Good morning!";
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            greeting = "Good afternoon!";
        } else if (hourOfDay >= 18) {
            greeting = "Good evening!";
        } else {
            greeting = "Hello!";
        }
        greeting = "Hi user, "+greeting;
        userName_greetings_textview.setText(greeting);
    }

    private void buttons() {

        view_all_watching_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                intent.putExtra("whereFrom", "continue watching");
                DataHolder.setContinueWatchingDramaDetailsArrayList(continueWatchingDramaDetailsArrayList);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataNumberForContinueWatching = 0;
                //progressBar_watching.setVisibility(View.VISIBLE);
                ShowContinueWatching();
            }
        });

    }

    private void ShowContinueWatching() {

        continueWatchingDramaIDsArrayListSaved = getDramaListFromPrefs();
        if(continueWatchingDramaIDsArrayListSaved != null &&  startWatching_textview.getVisibility() == View.VISIBLE){
            startWatching_textview.setVisibility(View.GONE);
        }

        if (continueWatchingDramaIDsArrayListSaved != null && getActivity() != null) {
            Collections.reverse(continueWatchingDramaIDsArrayListSaved);
            continueWatchingDramaDetailsArrayList = new ArrayList<>();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(this::showWatching);
            executor.execute(() -> {
                //Background work here
                try {

                    int limit = Math.min(5, continueWatchingDramaIDsArrayListSaved.size());
                    for (i = 0; i < limit; i++) {
                        System.out.println(continueWatchingDramaIDsArrayListSaved.get(i));
                        if (continueWatchingDramaIDsArrayListSaved.get(i).contains("@-")) {
                            String dataGot = continueWatchingDramaIDsArrayListSaved.get(i);
                            String showId = dataGot.substring(0, dataGot.indexOf("@"));
                            String showMediaType = dataGot.substring(dataGot.indexOf("-") + 1);
                           // System.out.println("dataGot: "+dataGot+"  "+showMediaType);


                            try {
                                RequestQueue queue = Volley.newRequestQueue(getActivity());

                                String showUrl = Constants.TMDB_URL_MAIN + showMediaType + "/" + showId + "?language=en-US";

                                System.out.println("dataGot: "+dataGot+"  "+showMediaType);

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

                                            if (showMediaType.equals("movie")) {
                                                showEpisodes = "1";
                                            } else {
                                                if (jsonObject.has("number_of_episodes")) {
                                                    showEpisodes = jsonObject.getString("number_of_episodes");
                                                } else {
                                                    showEpisodes = getString(R.string.n_a);
                                                }
                                            }

                                            showImage = jsonObject.getString("poster_path");
                                            showImage = "https://image.tmdb.org/t/p/w300" + showImage;

                                            if (jsonObject.has("vote_average")) {
                                                showRating = jsonObject.getString("vote_average");
                                                showRating = showRating.substring(0, showRating.indexOf(".") + 2);
                                            } else {
                                                showRating = getString(R.string.n_a);
                                            }

                                            if (i == 0) {
                                                oldDataCheck = continueWatchingDramaIDsArrayListSaved.get(0);
                                            }
                                           // System.out.println("showId: "+showId+" : "+showMediaType);
                                            dataNumberForContinueWatching++;
                                            DramaListModel dramaListModel = new DramaListModel(showName, "", showEpisodes, showRating, showImage, showMediaType, showId, String.valueOf(dataNumberForContinueWatching));
                                            continueWatchingDramaDetailsArrayList.add(dramaListModel);
                                            handler.post(() -> {
                                                try {
                                                    continueWatchingAdapter.notifyItemInserted(continueWatchingDramaDetailsArrayList.size());
                                                } catch (Exception e){
                                                    e.printStackTrace();
                                                }
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


                        } else if ((continueWatchingDramaIDsArrayListSaved.get(i).contains("*-"))){
                            String animeID = continueWatchingDramaIDsArrayListSaved.get(i);
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

                            if (i == 0) {
                                oldDataCheck = continueWatchingDramaIDsArrayListSaved.get(0);
                            }
                            // System.out.println("showId: "+showId+" : "+showMediaType);
                            dataNumberForContinueWatching++;
                            DramaListModel dramaListModel = new DramaListModel(animeName, "", animeEP, animeRating, animeImage, "Anime", animeID, String.valueOf(dataNumberForContinueWatching));
                            continueWatchingDramaDetailsArrayList.add(dramaListModel);
                            handler.post(() -> {
                                try {
                                    continueWatchingAdapter.notifyItemInserted(continueWatchingDramaDetailsArrayList.size());
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            String dramaUrl = Constants.MY_DRAMALIST_URL + continueWatchingDramaIDsArrayListSaved.get(i);
                         //   System.out.println("dataGotInDramaList: "+continueWatchingDramaIDsArrayListSaved.get(i));
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
                                Log.e("ExceptionInDramaData2", Objects.requireNonNull(exception.getMessage()));
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

                            if (i == 0) {
                                oldDataCheck = continueWatchingDramaIDsArrayListSaved.get(0);
                            }
                            dataNumberForContinueWatching++;
                            DramaListModel dramaListModel = new DramaListModel(dramaName, "", dramaEpisodes, showRating, dramaImage, "Drama", continueWatchingDramaIDsArrayListSaved.get(i), String.valueOf(dataNumberForContinueWatching));
                            continueWatchingDramaDetailsArrayList.add(dramaListModel);
                            handler.post(() -> {
                                try {
                                    continueWatchingAdapter.notifyItemInserted(continueWatchingDramaDetailsArrayList.size());
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                    if(i == limit){
                        handler.post(() -> {
                            continueWatchingAdapter.hideLoading();
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            executor.shutdown();


        } else {
            startWatching_textview.setVisibility(View.VISIBLE);
           // progressBar_watching.setVisibility(View.GONE);
            if(swipe_refresh_layout.isRefreshing()){
                swipe_refresh_layout.setRefreshing(false);
            }
        }


    }

    private void showWatching() {
        if (getActivity() != null) {
            continueWatchingAdapter = new ContinueWatchingAdapter(continueWatchingDramaDetailsArrayList, getActivity());
            continueWatchingAdapter.showLoading();
            continueWatchingAdapter.notifyItemInserted(continueWatchingDramaDetailsArrayList.size());
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_watching.setLayoutAnimation(controller);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_watching.setLayoutManager(linearLayoutManager);
            recyclerView_for_watching.setAdapter(continueWatchingAdapter);
            //progressBar_watching.setVisibility(View.GONE);
            recyclerView_for_watching.setVisibility(View.VISIBLE);

            watching_textview.setVisibility(View.VISIBLE);

            if (continueWatchingDramaIDsArrayListSaved.size() > 5) {
                continueWatchingAdapter.setOnDataLoadedListener(new ContinueWatchingAdapter.OnDataLoadedListener() {
                    @Override
                    public void onDataLoaded() {
                        // All data is loaded, make view_all_watching_container visible
                        view_all_watching_container.setVisibility(View.VISIBLE);
                    }
                });
            }

            if(swipe_refresh_layout.isRefreshing()){
                swipe_refresh_layout.setRefreshing(false);
            }
        }

        recyclerView_for_watching.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    swipe_refresh_layout.setEnabled(false);
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    swipe_refresh_layout.setEnabled(true);
                }
            }
        });

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
    public void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    public void onStop() {
        isPaused = true;
        super.onStop();
    }

    @Override
    public void onResume() {
        if (isPaused) {
            continueWatchingDramaIDsArrayListSaved = getDramaListFromPrefs();
            System.out.println(continueWatchingDramaIDsArrayListSaved);
            if (oldDataCheck != null && continueWatchingDramaIDsArrayListSaved != null) {
                if (!oldDataCheck.equals(continueWatchingDramaIDsArrayListSaved.get(0))) {
                    view_all_watching_container.setVisibility(View.GONE);
                    recyclerView_for_watching.setVisibility(View.GONE);
                    dataNumberForContinueWatching = 0;
                    ShowContinueWatching();
                    isPaused = false;
                }
            }
        }
        super.onResume();

    }
}