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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.EpisodeGroupsAdapter;
import com.bunny.entertainment.yousee.adapters.ShowsEpisodesAdapter2;
import com.bunny.entertainment.yousee.models.DramaMovieEpisodeSecondModel;
import com.bunny.entertainment.yousee.models.EpisodesGroups;
import com.bunny.entertainment.yousee.player.PlayerActivity;
import com.bunny.entertainment.yousee.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchFragment_2 extends Fragment implements EpisodeGroupsAdapter.OnButtonClickListener{

    SharedPreferences dramaPreferences;
    String showName, showImage, showIdTMDB, lastWatchedEp, showMediaType, showIdMyAPI;
    String mainUrl;
    /*for coontinueGetEP*/ String episodeLink, subtitleLink, showId; boolean gotSubtitle = false;  Boolean isCanceled = false;
    String episodeId, episodeTitle, episodeReleaseDate, seasonNoEpisodeNo;
    int totalEPs = 0, reloadTried = 0;
    ArrayList<DramaMovieEpisodeSecondModel> dramaMovieEpisodeSecondModelArrayList;
    ArrayList<EpisodesGroups> episodesGroupsArrayList;
    ShowsEpisodesAdapter2 showsEpisodesAdapter2;
    EpisodeGroupsAdapter episodeGroupsAdapter;
    RecyclerView recyclerView_for_episode, recyclerView_for_episodeGroups;
    ProgressBar progressBar_Main;
    TextView textView_show_selected;
    Animation startAnimation;
    CardView card_root_layout_Continue_watching;
    ImageView ImageBackground_Continue_watching;
    TextView episode_name_Continue_watching;
    ProgressBar progressBar_Continue_watching;
    Boolean isPaused = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watch_2, container, false);

        if (getActivity() != null) {
            dramaPreferences = getActivity().getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
            startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);

        }

        recyclerView_for_episode = view.findViewById(R.id.recyclerView_for_episode);
        recyclerView_for_episodeGroups = view.findViewById(R.id.recyclerView_for_episodeGroups);
        progressBar_Main = view.findViewById(R.id.progressBar_Main);
        textView_show_selected = view.findViewById(R.id.textView_show_selected);
        card_root_layout_Continue_watching = view.findViewById(R.id.card_root_layout_Continue_watching);
        ImageBackground_Continue_watching = card_root_layout_Continue_watching.findViewById(R.id.ImageBackground_Continue_watching);
        episode_name_Continue_watching = card_root_layout_Continue_watching.findViewById(R.id.episode_name_Continue_watching);
        progressBar_Continue_watching = card_root_layout_Continue_watching.findViewById(R.id.progressBar_Continue_watching);


        startCheckingVariable();


        return view;
    }


    private void startCheckingVariable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    showName = dramaPreferences.getString("showName", null);
                    showIdTMDB = dramaPreferences.getString("showIdTMDB", null);
                    lastWatchedEp = dramaPreferences.getString(showIdTMDB + "lastEpWatched", null);
                    showMediaType = dramaPreferences.getString("showMediaType", null);
                    showImage = dramaPreferences.getString("showImage", null);
                    while (showName == null) {
                        // Sleep for a short duration before checking again
                        try {
                            showName = dramaPreferences.getString("showName", null);
                            showIdTMDB = dramaPreferences.getString("showIdTMDB", null);
                            lastWatchedEp = dramaPreferences.getString(showIdTMDB + "lastEpWatched", null);
                            showMediaType = dramaPreferences.getString("showMediaType", null);
                            showImage = dramaPreferences.getString("showImage", null);
                            Thread.sleep(200); // You can adjust the duration as needed
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getEpisodes();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void getEpisodes() {

        if (showMediaType.contains("tv")) {
            mainUrl = Constants.MY_CONSUMET_API + "meta/tmdb/info/" + showIdTMDB + "?type=" + "Tv Series";
        } else if (showMediaType.contains("movie")) {
            mainUrl = Constants.MY_CONSUMET_API + "meta/tmdb/info/" + showIdTMDB + "?type=" + "movie";
        }

        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, mainUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("id")) {
                                    showIdMyAPI = jsonObject.getString("id");
                                    Log.e("showIdMyAPI", showIdMyAPI);
                                } else {
                                    Log.e("here1", "here1");
                                    noTitlesFound();
                                }

                                reloadTried = 0;

                                if (showMediaType.contains("tv")) {
                                    if (jsonObject.has("seasons")) {
                                        JSONArray jsonArraySeasons = jsonObject.getJSONArray("seasons");
                                        if (jsonArraySeasons.length() != 0) {
                                            dramaMovieEpisodeSecondModelArrayList = new ArrayList<>();
                                            for (int i = 0; i < jsonArraySeasons.length(); i++) {

                                                JSONObject jsonObjectSeason = jsonArraySeasons.getJSONObject(i);
                                                String showSeason = jsonObjectSeason.getString("season");


                                                if (jsonObjectSeason.has("episodes")) {
                                                    JSONArray jsonArrayEpisodes = jsonObjectSeason.getJSONArray("episodes");
                                                    Log.e("Episodes", String.valueOf(jsonArrayEpisodes.length()));
                                                    for (int j = 0; j < jsonArrayEpisodes.length(); j++) {
                                                        JSONObject jsonObjectEpisodes = jsonArrayEpisodes.getJSONObject(j);
                                                        if (jsonObjectEpisodes.has("id")) {
                                                            episodeId = jsonObjectEpisodes.getString("id");
                                                            episodeTitle = jsonObjectEpisodes.getString("title");
                                                            episodeReleaseDate = jsonObjectEpisodes.getString("releaseDate");
                                                            seasonNoEpisodeNo = jsonObjectEpisodes.getString("episode");
                                                            seasonNoEpisodeNo = "S" + showSeason + " : EP" + seasonNoEpisodeNo;
                                                            totalEPs++;
                                                            // Log.e("totalEPs", String.valueOf(totalEPs));
                                                            DramaMovieEpisodeSecondModel dramaMovieEpisodeSecondModel = new DramaMovieEpisodeSecondModel(episodeId, episodeTitle, seasonNoEpisodeNo, showImage, episodeReleaseDate, String.valueOf(totalEPs));
                                                            dramaMovieEpisodeSecondModelArrayList.add(dramaMovieEpisodeSecondModel);
                                                            //  System.out.println(dramaMovieEpisodeSecondModel);
                                                            //Log.e("episodeTitle", episodeTitle);
                                                        } else if(!jsonObjectEpisodes.has("id") && dramaMovieEpisodeSecondModelArrayList.size() == 0){
                                                            Log.e("here2", "here2");
                                                            noTitlesFound();
                                                        }
                                                    }
                                                } else if(!jsonObjectSeason.has("episodes") && dramaMovieEpisodeSecondModelArrayList.size() == 0){
                                                    Log.e("here3", "here3");
                                                    noTitlesFound();
                                                }
                                            }
                                            makeEpisodesGroups(dramaMovieEpisodeSecondModelArrayList);
                                          /*  handler.post(() -> {
                                                setEpisodeAdapter(dramaMovieEpisodeSecondModelArrayList);
                                            });*/
                                        }
                                    }
                                } else if (showMediaType.contains("movie")) {
                                    if (jsonObject.has("episodeId")) {
                                        dramaMovieEpisodeSecondModelArrayList = new ArrayList<>();
                                        episodeId = jsonObject.getString("episodeId");
                                        episodeTitle = jsonObject.getString("title");
                                        episodeReleaseDate = jsonObject.getString("releaseDate");
                                        totalEPs++;
                                        DramaMovieEpisodeSecondModel dramaMovieEpisodeSecondModel = new DramaMovieEpisodeSecondModel(episodeId, episodeTitle, "1", showImage, episodeReleaseDate, String.valueOf(totalEPs));
                                        dramaMovieEpisodeSecondModelArrayList.add(dramaMovieEpisodeSecondModel);
                                    } else {
                                        Log.e("here4", "here4");
                                        noTitlesFound();
                                    }
                                    handler.post(() -> {
                                        setMovieAdapter(dramaMovieEpisodeSecondModelArrayList);
                                    });
                                }

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                if (error.networkResponse.statusCode == 500) {
                                    Log.e("here5", "here5");
                                    if(reloadTried > 3){
                                        reloadTried++;
                                        getEpisodes();
                                    } else {
                                        noTitlesFound();
                                        Toast.makeText(requireActivity(), "Maybe Server error! No sure\uD83D\uDE05", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    progressBar_Main.setVisibility(View.GONE);
                                    Toast.makeText(requireActivity(), "Server Not Available!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("here6", "here6");
                                noTitlesFound();
                                Toast.makeText(requireActivity(), "Server Not Available!", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(stringRequest);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.post(() -> {
                    //UI Thread work here

                });
            });
        }
    }

    private void makeEpisodesGroups(ArrayList<DramaMovieEpisodeSecondModel> dramaMovieEpisodeSecondModelArrayList) {
        episodesGroupsArrayList = new ArrayList<>();

        int groupCount = (int) Math.ceil((double) dramaMovieEpisodeSecondModelArrayList.size() / Constants.MAX_GROUP_SIZE);

        for (int i = 0; i < groupCount; i++) {
            int startIndex = i *  Constants.MAX_GROUP_SIZE;
            int endIndex = Math.min(startIndex + Constants.MAX_GROUP_SIZE, dramaMovieEpisodeSecondModelArrayList.size());
            //Log.e("Index", "Start Index "+startIndex+"    End Index "+endIndex);
            List<DramaMovieEpisodeSecondModel> subList  = dramaMovieEpisodeSecondModelArrayList.subList(startIndex, endIndex);
            ArrayList<DramaMovieEpisodeSecondModel> groupEpisodes = new ArrayList<>(subList);
            EpisodesGroups group = new EpisodesGroups(String.valueOf(startIndex), String.valueOf(endIndex), groupEpisodes);
            episodesGroupsArrayList.add(group);
        }

        /*for (int i = 0; i < episodesGroupsArrayList.size(); i++){
            System.out.println(episodesGroupsArrayList.get(i).startEP+" : "+episodesGroupsArrayList.get(i).endEP+"    "+episodesGroupsArrayList.get(i).episodeGroupList);
        }*/
        if(getActivity() != null) {
            episodeGroupsAdapter = new EpisodeGroupsAdapter(getActivity(), episodesGroupsArrayList, this);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_episodeGroups.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_episodeGroups.setLayoutManager(linearLayoutManager);
            recyclerView_for_episodeGroups.setAdapter(episodeGroupsAdapter);
            recyclerView_for_episodeGroups.setVisibility(View.VISIBLE);
            recyclerView_for_episodeGroups.startAnimation(startAnimation);
        }




        if (lastWatchedEp != null) {
            int epNo = Integer.parseInt(lastWatchedEp.substring(0, lastWatchedEp.indexOf(",")).trim());
            for(int i = 0; i < episodesGroupsArrayList.size(); i++){
                if(epNo >= Integer.parseInt(episodesGroupsArrayList.get(i).getStartEP()) && epNo <= Integer.parseInt(episodesGroupsArrayList.get(i).getEndEP())){
                    setEpisodesAdapter(episodesGroupsArrayList.get(i).getStartEP(),  episodesGroupsArrayList.get(i).getEndEP());
                  //to scroll the episode groups to where the continue episode number is there
                    epNo = epNo - 1;
                    int groupNumber =  epNo / Constants.MAX_GROUP_SIZE + 1;
                    recyclerView_for_episodeGroups.scrollToPosition(groupNumber-1);
                    break;
                }
            }
        } else {
            setEpisodesAdapter(episodesGroupsArrayList.get(0).getStartEP(),  episodesGroupsArrayList.get(0).getEndEP());
        }


    }

    private void setEpisodesAdapter(String startEP, String endEP) {

        for (int i = 0; i < episodesGroupsArrayList.size(); i++) {
            //System.out.println(episodesGroupsArrayList.get(i).getStartEP());
            if (episodesGroupsArrayList.get(i).getStartEP().equals(startEP) && episodesGroupsArrayList.get(i).getEndEP().equals(endEP)){
                Log.e("hereInHere", "hahahaha");
                if (getActivity() != null) {
                    showsEpisodesAdapter2 = new ShowsEpisodesAdapter2(episodesGroupsArrayList.get(i).getEpisodeGroupList(), getActivity(), showName, showIdTMDB, showIdMyAPI, showMediaType);

                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_top_bottom); // Create your animation here
                    LayoutAnimationController controller = new LayoutAnimationController(animation);
                    recyclerView_for_episode.setLayoutAnimation(controller);
                    // Define your item width
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView_for_episode.setLayoutManager(linearLayoutManager);
                    recyclerView_for_episode.setAdapter(showsEpisodesAdapter2);
                    recyclerView_for_episode.setVisibility(View.VISIBLE);
                    recyclerView_for_episode.startAnimation(startAnimation);
                    textView_show_selected.setText(showName);
                    textView_show_selected.setVisibility(View.VISIBLE);
                    textView_show_selected.startAnimation(startAnimation);
                    progressBar_Main.setVisibility(View.GONE);
                    //showsEpisodesAdapter2.notifyDataSetChanged();


                    if (lastWatchedEp != null) {
                        try {
                            Log.e("lastWatchedEp", lastWatchedEp);
                            String playerPosition = lastWatchedEp.substring(lastWatchedEp.indexOf(",") + 1, lastWatchedEp.indexOf("/"));
                            int epNo = Integer.parseInt(lastWatchedEp.substring(0, lastWatchedEp.indexOf(",")).trim());
                            String episodeNo = "Continue Episode " + epNo;
                            String totalDuration = lastWatchedEp.substring(lastWatchedEp.indexOf("/") + 1);
                            episode_name_Continue_watching.setText(episodeNo);
                            float pro = Float.parseFloat(playerPosition) / Float.parseFloat(totalDuration);
                            int progress = (int) (pro * 100);
                            Log.e("progress", String.valueOf(progress));
                            progressBar_Continue_watching.setProgress(progress);
                            card_root_layout_Continue_watching.setVisibility(View.VISIBLE);
                            Glide.with(getActivity()).load(dramaMovieEpisodeSecondModelArrayList.get(epNo - 1).getEpisodeImage()).diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ImageBackground_Continue_watching);
                            card_root_layout_Continue_watching.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                              /*      if(epNo-1 >= Integer.parseInt(startEP) && epNo -1 <= Integer.parseInt(endEP)) {
                                        showsEpisodesAdapter2.performClick(epNo-1, startEP);
                                    } else {
                                        episodeGroupsAdapter.performClick(view, epNo-1);
                                    }*/

                                    continueClickedGetEpisodeLink(epNo-1);

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
               // break;
            }
        }
    }

    private void noTitlesFound() {
        progressBar_Main.setVisibility(View.GONE);
        String showNotAvailable = "No titles found!";
        textView_show_selected.setText(showNotAvailable);
        textView_show_selected.setVisibility(View.VISIBLE);
    }

    private void setMovieAdapter(ArrayList<DramaMovieEpisodeSecondModel> episodes) {

        if (episodes == null) {
            System.out.println("EPISODES NULL");
        }

        if (getActivity() != null && episodes != null) {
            showsEpisodesAdapter2 = new ShowsEpisodesAdapter2(episodes, getActivity(), showName, showIdTMDB, showIdMyAPI, showMediaType);

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_top_bottom); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_episode.setLayoutAnimation(controller);
            // Define your item width
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView_for_episode.setLayoutManager(linearLayoutManager);
            recyclerView_for_episode.setAdapter(showsEpisodesAdapter2);
            recyclerView_for_episode.setVisibility(View.VISIBLE);
            recyclerView_for_episode.startAnimation(startAnimation);
            textView_show_selected.setText(showName);
            textView_show_selected.setVisibility(View.VISIBLE);
            textView_show_selected.startAnimation(startAnimation);
            progressBar_Main.setVisibility(View.GONE);


            if (lastWatchedEp != null) {
                try {
                    Log.e("lastWatchedEp", lastWatchedEp);
                    String playerPosition = lastWatchedEp.substring(lastWatchedEp.indexOf(",") + 1, lastWatchedEp.indexOf("/"));
                    int epNo = Integer.parseInt(lastWatchedEp.substring(0, lastWatchedEp.indexOf(",")).trim());
                    String movieName = "Continue " + showName;
                    String totalDuration = lastWatchedEp.substring(lastWatchedEp.indexOf("/") + 1);
                    episode_name_Continue_watching.setText(movieName);
                    float pro = Float.parseFloat(playerPosition) / Float.parseFloat(totalDuration);
                    int progress = (int) (pro * 100);
                    Log.e("progress", String.valueOf(progress));
                    progressBar_Continue_watching.setProgress(progress);
                    card_root_layout_Continue_watching.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).load(dramaMovieEpisodeSecondModelArrayList.get(epNo - 1).getEpisodeImage()).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ImageBackground_Continue_watching);
                    card_root_layout_Continue_watching.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //continueButtonClickListener.onContinueButtonClick(epNo - 1);
                            // showsEpisodesAdapter2.performItemClick(view, epNo - 1);
                            showsEpisodesAdapter2.performClick(epNo - 1, "0");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPaused) {
            isPaused = false;
            totalEPs = 0;
            startCheckingVariable();
        }
    }

    @Override
    public void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    public void onButtonClicked(String startEP, String endEP) {
        Log.e("hereINButtonClicked", "yes");
        setEpisodesAdapter(startEP, endEP);
    }

    public void continueClickedGetEpisodeLink(int epNo){

        if (getActivity() != null) {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetTheme);
            View bsView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_server_select_dialog, getActivity().findViewById(R.id.main_bottomSheet_layout));
            Button cancel_button;
            NestedScrollView main_bottomSheet_layout;
            main_bottomSheet_layout = bsView.findViewById(R.id.main_bottomSheet_layout);
            cancel_button = bsView.findViewById(R.id.cancel_button);
            cancel_button.setVisibility(View.VISIBLE);
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isCanceled = true;
                    bottomSheetDialog.dismiss();
                }
            });
            main_bottomSheet_layout.setMinimumHeight(400);
            bottomSheetDialog.setContentView(bsView);
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.show();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                String mainUrl = Constants.MY_CONSUMET_API + "meta/tmdb/watch/"+dramaMovieEpisodeSecondModelArrayList.get(epNo).getEpisodeId()+"?id="+showIdMyAPI;

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, mainUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("sources")) {
                                    JSONArray jsonArraySources = jsonObject.getJSONArray("sources");

                                    for(int i = 0; i < jsonArraySources.length(); i++){

                                        JSONObject jsonObjectForQuality = jsonArraySources.getJSONObject(i);
                                        String quality = jsonObjectForQuality.getString("quality");
                                        if(quality.contains("auto")){
                                            episodeLink = jsonObjectForQuality.getString("url");
                                            break;
                                        }
                                    }

                                    if(jsonObject.has("subtitles")){
                                        JSONArray jsonArraySubtitles = jsonObject.getJSONArray("subtitles");
                                        for(int i = 0; i < jsonArraySubtitles.length(); i++){
                                            JSONObject jsonObjectForLang = jsonArraySubtitles.getJSONObject(i);
                                            String lang = jsonObjectForLang.getString("lang");
                                            if(lang.contains("English") || lang.contains("english")){
                                                subtitleLink = jsonObjectForLang.getString("url");
                                                gotSubtitle = true;
                                                break;
                                            }
                                        }

                                    }
                                } else {
                                    Toast.makeText(getActivity(), "No Link Found!", Toast.LENGTH_SHORT).show();
                                }

                                //progressBar_Main.setVisibility(View.GONE);
                                if(!isCanceled){
                                    bottomSheetDialog.dismiss();
                                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                                    intent.putExtra("whereFrom", "ShowsFragment");
                                    intent.putExtra("videoLink", episodeLink);
                                    intent.putExtra("showMediaType", showMediaType);
                                    Log.e("showMediaType", showMediaType);
                                    intent.putExtra("subtitleLink", subtitleLink);
                                    intent.putExtra("dramaNameAndEpisodeNo", showName+" - Episode "+dramaMovieEpisodeSecondModelArrayList.get(epNo).getSeasonNoEpisodeNo());
                                    intent.putExtra("dramaIdMyDramaList", showId);
                                    intent.putExtra("realEpisodeNo", dramaMovieEpisodeSecondModelArrayList.get(epNo).getTotalEps());
                                    requireActivity().startActivity(intent);

                                } else {
                                    isCanceled = false;
                                }

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse.statusCode == 500){
                                bottomSheetDialog.dismiss();
                                Toast.makeText(getActivity(), "Maybe Server error! No sure\uD83D\uDE05", Toast.LENGTH_SHORT).show();
                            } else {
                                //progressBar_Main.setVisibility(View.GONE);
                                bottomSheetDialog.dismiss();
                                Toast.makeText(getActivity(), "Server Not Available!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(stringRequest);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

}