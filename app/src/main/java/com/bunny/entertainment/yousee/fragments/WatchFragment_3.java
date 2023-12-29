package com.bunny.entertainment.yousee.fragments;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.AnimeEpisodeGroupsAdapter;
import com.bunny.entertainment.yousee.adapters.AnimeEpisodesAdapter;
import com.bunny.entertainment.yousee.adapters.EpisodeGroupsAdapter;
import com.bunny.entertainment.yousee.adapters.ShowsEpisodesAdapter;
import com.bunny.entertainment.yousee.adapters.WrongTitleAdapter;
import com.bunny.entertainment.yousee.models.DramaMovieEpisodeSecondModel;
import com.bunny.entertainment.yousee.models.DramaMovieFirstModel;
import com.bunny.entertainment.yousee.models.EpisodesGroups;
import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeGroups;
import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeModel;
import com.bunny.entertainment.yousee.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class WatchFragment_3 extends Fragment implements WrongTitleAdapter.OnItemClickListener, AnimeEpisodeGroupsAdapter.OnButtonClickListener{

    private static final int REQUEST_WRITE_SETTINGS = 111;
    AutoCompleteTextView animeSource;
    SharedPreferences animePreferences;
    String animeName, myAnimeListID, showSelected, getOldTitleIfAny, animeSourceText, lastWatchedEp, animeType, subDub;
    String animeSourceLink, animeEpisodeLink;
    ArrayList<DramaMovieFirstModel> animeFirstModelArrayList;
    ArrayList<AnimeEpisodeModel> animeEpisodeModelArrayList;
    ArrayList<AnimeEpisodeGroups> episodesGroupsArrayList;
    AnimeEpisodeGroupsAdapter animeEpisodeGroupsAdapter;
    AnimeEpisodesAdapter animeEpisodesAdapter;
    WrongTitleAdapter wrongTitleAdapter;
    RecyclerView recyclerView_for_episode, recyclerView_for_episodeGroups;
    ProgressBar progressBar_Main;
    TextView textView_show_selected, textView_show_wrong_title;
    Animation startAnimation;
    int selectedTitle = 0;
    BottomSheetDialog bottomSheetDialog;
    Boolean isUsingAnimeName = true;
    ImageButton title_selected_info_btn;
    CardView card_root_layout_Continue_watching;
    ImageView ImageBackground_Continue_watching;
    TextView episode_name_Continue_watching;
    ProgressBar progressBar_Continue_watching, progressBar_Main_center;
    NestedScrollView main_nested_scrollView;
    SwitchMaterial sub_dub_switch;
    boolean isPaused = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watch_3, container, false);

        if (getActivity() != null) {
            animePreferences = getActivity().getSharedPreferences("animePreferences", Context.MODE_PRIVATE);
            startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);

        }


        recyclerView_for_episode = view.findViewById(R.id.recyclerView_for_episode);
        recyclerView_for_episodeGroups = view.findViewById(R.id.recyclerView_for_episodeGroups);
        main_nested_scrollView = view.findViewById(R.id.main_nested_scrollView);
        progressBar_Main = view.findViewById(R.id.progressBar_Main);
        progressBar_Main_center = view.findViewById(R.id.progressBar_Main_center);
        textView_show_selected = view.findViewById(R.id.textView_show_selected);
        sub_dub_switch = view.findViewById(R.id.sub_dub_switch);
        textView_show_wrong_title = view.findViewById(R.id.textView_show_wrong_title);
        title_selected_info_btn = view.findViewById(R.id.title_selected_info_btn);
        card_root_layout_Continue_watching = view.findViewById(R.id.card_root_layout_Continue_watching);
        ImageBackground_Continue_watching = card_root_layout_Continue_watching.findViewById(R.id.ImageBackground_Continue_watching);
        episode_name_Continue_watching = card_root_layout_Continue_watching.findViewById(R.id.episode_name_Continue_watching);
        progressBar_Continue_watching = card_root_layout_Continue_watching.findViewById(R.id.progressBar_Continue_watching);

        animeSource = view.findViewById(R.id.animeSource);
        animeSource.requestFocus();

        startCheckingVariable();

        Balloon balloon = new Balloon.Builder(getActivity())
                .setWidth(BalloonSizeSpec.WRAP)
                .setHeight(BalloonSizeSpec.WRAP)
                .setText(getString(R.string.always_check_tooltip_text))
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.mode))
                .setTextSize(15f)
                .setMarginLeft(20)
                .setMarginRight(20)
                .setPadding(10)
                .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.mode_inverse_extra))
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowPosition(0.5f)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setDismissWhenTouchOutside(true)
                .build();


        title_selected_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                balloon.showAlignBottom(title_selected_info_btn);
            }
        });


        return view;
    }

    private void startCheckingVariable() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    animeName = animePreferences.getString("animeName", null);
                    myAnimeListID = animePreferences.getString("myAnimeListID", null);
                    lastWatchedEp = animePreferences.getString(myAnimeListID + "lastEpWatched", null);
                    animeType = animePreferences.getString("animeType", null);
                    while (animeName == null) {
                        // Sleep for a short duration before checking again
                        try {
                            animeName = animePreferences.getString("animeName", null);
                            myAnimeListID = animePreferences.getString("myAnimeListID", null);
                            lastWatchedEp = animePreferences.getString(myAnimeListID + "lastEpWatched", null);
                            animeType = animePreferences.getString("animeType", null);
                            System.out.println("WatchFragment:DramaType:  " + animeType);
                            Thread.sleep(1000); // adjust the duration as needed
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setServerToSpinner();
                            }
                        });
                    }
                }
            }
        }).start();

    }

    private void setServerToSpinner() {

        if (getActivity() != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getActivity(),
                    R.array.anime_website_server_arrays,
                    R.layout.drop_down_spinner
            );

            adapter.setDropDownViewResource(R.layout.drop_down_layout);
            animeSource.setAdapter(adapter);
        }

        String previouslySelectedSource = animePreferences.getString(animeName + "Source", null);


        if (previouslySelectedSource != null) {
            if (previouslySelectedSource.equals("Aniwatch(consumet)")) {
                animeSourceText = getString(R.string.aniwatch_consumet);
                animeSource.setText(getString(R.string.aniwatch_consumet), false);
                checkIfSelectedTitlePreviously();
                serverChanged(0);
            } else if (previouslySelectedSource.equals("Gogo(consumet)")) {
                animeSourceText = getString(R.string.gogo_consumet);
                animeSource.setText(getString(R.string.gogo_consumet), false);
                checkIfSelectedTitlePreviously();
                serverChanged(1);
            }

        } else {
            animeSource.setText(getString(R.string.aniwatch_consumet), false);
            animeSourceText = getString(R.string.aniwatch_consumet);
            checkIfSelectedTitlePreviously();
            serverChanged(0);
        }


        animeSource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                progressBar_Main.setVisibility(View.VISIBLE);
                if (recyclerView_for_episode.getVisibility() == View.VISIBLE) {
                    if (animeEpisodeModelArrayList != null) {
                        animeEpisodeModelArrayList.clear();
                    }
                    recyclerView_for_episode.setVisibility(View.GONE);
                }
                animePreferences.edit().putString(animeName + "Source", item).apply();
                if(position == 0){
                    animeSourceText = getString(R.string.aniwatch_consumet);
                    checkIfSelectedTitlePreviously();
                } else if (position == 1) {
                    animeSourceText = getString(R.string.gogo_consumet);
                    checkIfSelectedTitlePreviously();
                }
                serverChanged(position);
                Log.e("serverSelected", item);
            }
        });

    }
    private void checkIfSelectedTitlePreviously() {
        getOldTitleIfAny = animePreferences.getString(myAnimeListID + animeSourceText, null);
        System.out.println("myAnimeListID + animeSourceText"+myAnimeListID + animeSourceText);
        if (getOldTitleIfAny != null) {
            selectedTitle = Integer.parseInt(getOldTitleIfAny);
            System.out.println("SelectedTitle: "+selectedTitle);
        }
    }

    private void serverChanged(int i) {

        if (i == 0) {
            if (animeName != null) {
               animeSourceLink = Constants.MY_CONSUMET_API + "anime/zoro/" + animeName;
            }
        } else if (i == 1) {
            if (animeName != null) {
                animeSourceLink = Constants.MY_CONSUMET_API + "anime/gogoanime/" + animeName;
                subDub = animePreferences.getString(myAnimeListID + "language", null);
                if(subDub != null && subDub.equals("dub")){
                    animeSourceLink = animeSourceLink + " " +subDub;
                }

            }
        }


        if (getActivity() != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                try {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, animeSourceLink, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            animeFirstModelArrayList = new ArrayList<>();

                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                String currentPage = jsonObject.getString("currentPage");
                                String hasNextPage = jsonObject.getString("hasNextPage");

                                JSONArray jsonArray = jsonObject.getJSONArray("results");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String id = jsonObject1.getString("id");
                                    String name = jsonObject1.getString("title");
                                    String image = jsonObject1.getString("image");

                                    DramaMovieFirstModel animeFirstModel = new DramaMovieFirstModel(id, name, image);
                                    animeFirstModelArrayList.add(animeFirstModel);
                                }

                                if (jsonArray.length() > 0) {
                                    loadEpisodesData(i);
                                } else {
                                    progressBar_Main.setVisibility(View.GONE);
                                    String showNotAvailable = "No titles found!";
                                    textView_show_selected.setText(showNotAvailable);
                                }

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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
    private void loadEpisodesData(int i) {
        showSelected = "Selected: " + animeFirstModelArrayList.get(selectedTitle).getDramaName();
        textView_show_selected.setText(showSelected);
        textView_show_selected.setSelected(true);//for marquee
        textView_show_selected.requestFocus();//for marquee

        if (animeFirstModelArrayList != null) {
            if (i == 0) {
                animeEpisodeLink = Constants.MY_CONSUMET_API + "anime/zoro/info?id=" + animeFirstModelArrayList.get(selectedTitle).getDramaId();
            } else if (i == 1) {
                animeEpisodeLink = Constants.MY_CONSUMET_API + "anime/gogoanime/info/" + animeFirstModelArrayList.get(selectedTitle).getDramaId();
            }

            if (getActivity() != null) {
                try {

                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, animeEpisodeLink, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            animeEpisodeModelArrayList = new ArrayList<>();

                            try {

                                JSONObject jsonObject = new JSONObject(response);


                                JSONArray jsonArray = jsonObject.getJSONArray("episodes");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String episodeId = jsonObject1.getString("id");
                                    String episodeNo = jsonObject1.getString("number");
                                    String episodeName = "";
                                    if(jsonObject1.has("title")){
                                        episodeName = jsonObject1.getString("title");
                                    }else {
                                        episodeName = "Episode "+episodeNo;
                                    }
                                    boolean isFiller;
                                    if (animeEpisodeLink.contains("zoro")) {
                                        isFiller = jsonObject1.getBoolean("isFiller");
                                        //Log.e("insideLoadEpisodesData", episodeId + "     " + episodeName + "     " + dramaMovieFirstModelArrayList.get(selectedTitle).getDramaImage() + "     " + episodeNo + "     " + episodeReleaseDate);
                                        AnimeEpisodeModel animeEpisodeModel = new AnimeEpisodeModel(episodeId, episodeName, episodeNo, animeFirstModelArrayList.get(selectedTitle).getDramaImage(), episodeNo);
                                        animeEpisodeModelArrayList.add(animeEpisodeModel);
                                        animeEpisodeModelArrayList.get(i).setFiller(isFiller);
                                    } else {
                                        //Log.e("insideLoadEpisodesData", episodeId + "     " + episodeName + "     " + dramaMovieFirstModelArrayList.get(selectedTitle).getDramaImage() + "     " + episodeNo);
                                        AnimeEpisodeModel animeEpisodeModel = new AnimeEpisodeModel(episodeId, episodeName, episodeNo, animeFirstModelArrayList.get(selectedTitle).getDramaImage(), episodeNo);
                                        animeEpisodeModelArrayList.add(animeEpisodeModel);
                                    }
                                }

                                //setEpisodeAdapter();
                                makeEpisodesGroups(animeEpisodeModelArrayList);

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(stringRequest);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void makeEpisodesGroups(ArrayList<AnimeEpisodeModel> animeEpisodeModelArrayList) {
        episodesGroupsArrayList = new ArrayList<>();
        int groupCount = (int) Math.ceil((double) animeEpisodeModelArrayList.size() / Constants.MAX_GROUP_SIZE);

        for (int i = 0; i < groupCount; i++) {
            int startIndex = i * Constants.MAX_GROUP_SIZE;
            int endIndex = Math.min(startIndex + Constants.MAX_GROUP_SIZE, animeEpisodeModelArrayList.size());
            //Log.e("Index", "Start Index "+startIndex+"    End Index "+endIndex);
            List<AnimeEpisodeModel> subList = animeEpisodeModelArrayList.subList(startIndex, endIndex);
            ArrayList<AnimeEpisodeModel> groupEpisodes = new ArrayList<>(subList);
            AnimeEpisodeGroups group = new AnimeEpisodeGroups(String.valueOf(startIndex), String.valueOf(endIndex), groupEpisodes);
            episodesGroupsArrayList.add(group);
        }

        /*for (int i = 0; i < episodesGroupsArrayList.size(); i++){
            System.out.println(episodesGroupsArrayList.get(i).startEP+" : "+episodesGroupsArrayList.get(i).endEP+"    "+episodesGroupsArrayList.get(i).episodeGroupList);
        }*/
        if (getActivity() != null) {
            animeEpisodeGroupsAdapter = new AnimeEpisodeGroupsAdapter(getActivity(), episodesGroupsArrayList, this);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_episodeGroups.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_episodeGroups.setLayoutManager(linearLayoutManager);
            recyclerView_for_episodeGroups.setAdapter(animeEpisodeGroupsAdapter);
            recyclerView_for_episodeGroups.setVisibility(View.VISIBLE);
            recyclerView_for_episodeGroups.startAnimation(startAnimation);
        }


        if (lastWatchedEp != null) {
            int epNo = Integer.parseInt(lastWatchedEp.substring(0, lastWatchedEp.indexOf(",")).trim());
            for (int i = 0; i < episodesGroupsArrayList.size(); i++) {
                if (epNo >= Integer.parseInt(episodesGroupsArrayList.get(i).getStartEP()) && epNo <= Integer.parseInt(episodesGroupsArrayList.get(i).getEndEP())) {
                    setEpisodeAdapter(episodesGroupsArrayList.get(i).getStartEP(), episodesGroupsArrayList.get(i).getEndEP());
                    //to scroll the episode groups to where the continue episode number is there
                    epNo = epNo - 1;
                    int groupNumber = epNo / Constants.MAX_GROUP_SIZE + 1;
                    recyclerView_for_episodeGroups.scrollToPosition(groupNumber - 1);
                    break;
                }
            }
        } else {
            setEpisodeAdapter(episodesGroupsArrayList.get(0).getStartEP(), episodesGroupsArrayList.get(0).getEndEP());
        }
    }

    private void setEpisodeAdapter(String startEP, String endEP) {
        if (getActivity() != null) {

            for (int i = 0; i < episodesGroupsArrayList.size(); i++) {
                if (episodesGroupsArrayList.get(i).getStartEP().equals(startEP) && episodesGroupsArrayList.get(i).getEndEP().equals(endEP)) {
                    animeEpisodesAdapter = new AnimeEpisodesAdapter(episodesGroupsArrayList.get(i).getEpisodeGroupListAnime(), getActivity(), animeName, animeFirstModelArrayList.get(0).getDramaId(), myAnimeListID, animeType);

                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_top_bottom); // Create your animation here
                    LayoutAnimationController controller = new LayoutAnimationController(animation);
                    recyclerView_for_episode.setLayoutAnimation(controller);
                    // Define your item width
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView_for_episode.setLayoutManager(linearLayoutManager);
                    recyclerView_for_episode.setAdapter(animeEpisodesAdapter);
                    recyclerView_for_episode.setVisibility(View.VISIBLE);
                    recyclerView_for_episode.startAnimation(startAnimation);
                    textView_show_selected.setVisibility(View.VISIBLE);
                    textView_show_selected.startAnimation(startAnimation);
                    sub_dub_switch.setVisibility(View.VISIBLE);
                    sub_dub_switch.startAnimation(startAnimation);
                    subDub = animePreferences.getString(myAnimeListID + "language", null);
                    if (subDub != null){
                        if (subDub.equals("dub")){
                            sub_dub_switch.setChecked(true);
                            sub_dub_switch.setText(R.string.dub);
                        } else if (subDub.equals("sub")) {
                            sub_dub_switch.setChecked(false);
                            sub_dub_switch.setText(R.string.sub);
                        }
                    } else {
                        subDub = "sub";
                    }
                    textView_show_wrong_title.setVisibility(View.VISIBLE);
                    textView_show_wrong_title.startAnimation(startAnimation);
                    title_selected_info_btn.setVisibility(View.VISIBLE);
                    title_selected_info_btn.startAnimation(startAnimation);
                    progressBar_Main.setVisibility(View.GONE);

                }
            }
            sub_dub_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        animePreferences.edit().putString(myAnimeListID + "language", "dub").apply();
                        subDub = "dub";
                        sub_dub_switch.setText(R.string.dub);
                        startCheckingVariable();
                    } else {
                        animePreferences.edit().putString(myAnimeListID + "language", "sub").apply();
                        subDub = "sub";
                        sub_dub_switch.setText(R.string.sub);
                        startCheckingVariable();
                    }
                }
            });
           /* System.out.println("WatchFragmentSetEpisodeAdapter:DramaType:  "+dramaType);
            showsEpisodesAdapter = new ShowsEpisodesAdapter(dramaMovieEpisodeSecondModelArrayList, getActivity(), dramaName, dramaMovieFirstModelArrayList.get(0).getDramaId(), dramaIdMydramaList, dramaType);

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_top_bottom); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_episode.setLayoutAnimation(controller);
            // Define your item width
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView_for_episode.setLayoutManager(linearLayoutManager);
            recyclerView_for_episode.setAdapter(showsEpisodesAdapter);
            recyclerView_for_episode.setVisibility(View.VISIBLE);
            recyclerView_for_episode.startAnimation(startAnimation);
            textView_show_selected.setVisibility(View.VISIBLE);
            textView_show_selected.startAnimation(startAnimation);
            textView_show_wrong_title.setVisibility(View.VISIBLE);
            textView_show_wrong_title.startAnimation(startAnimation);
            title_selected_info_btn.setVisibility(View.VISIBLE);
            title_selected_info_btn.startAnimation(startAnimation);
            progressBar_Main.setVisibility(View.GONE);

            Log.e("dramaIdMydramaList", dramaIdMydramaList);*/
        }

        if (lastWatchedEp != null) {
            Log.e("lastWatchedEp", lastWatchedEp);
            String playerPosition = lastWatchedEp.substring(lastWatchedEp.indexOf(",") + 1, lastWatchedEp.indexOf("/"));
            int epNo = Integer.parseInt(lastWatchedEp.substring(0, lastWatchedEp.indexOf(",")).trim());
            String episodeNo;
            if (animeType != null && animeType.contains("Movie")) {
                episodeNo = "Continue " + animeName;
            } else {
                episodeNo = "Continue Episode " + epNo;
            }
            String totalDuration = lastWatchedEp.substring(lastWatchedEp.indexOf("/") + 1);
            episode_name_Continue_watching.setText(episodeNo);
            float pro = Float.parseFloat(playerPosition) / Float.parseFloat(totalDuration);
            int progress = (int) (pro * 100);
            Log.e("progress", String.valueOf(progress));
            progressBar_Continue_watching.setProgress(progress);
            card_root_layout_Continue_watching.setVisibility(View.VISIBLE);
            Glide.with(ImageBackground_Continue_watching.getContext()).load(animeEpisodeModelArrayList.get(epNo - 1).getEpisodeImage()).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ImageBackground_Continue_watching);
            card_root_layout_Continue_watching.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //showsEpisodesAdapter.performItemClick(view, epNo - 1);
                    continueClickedGetEpisodeLink(view, epNo, startEP, endEP);
                }
            });
        }


        //dont touch this

        textView_show_wrong_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetTheme);
                    View bsView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_server_select_dialog, view.findViewById(R.id.main_bottomSheet_layout));

                    RecyclerView recyclerView_for_custom;
                    TextInputLayout textview_search;
                    TextInputEditText edittext_search;
                    ProgressBar main_ProgressBar;

                    recyclerView_for_custom = bsView.findViewById(R.id.recyclerView_for_custom);
                    main_ProgressBar = bsView.findViewById(R.id.main_ProgressBar);
                    textview_search = bsView.findViewById(R.id.textview_search);
                    textview_search.setVisibility(View.VISIBLE);
                    edittext_search = bsView.findViewById(R.id.edittext_search);

                    if (isUsingAnimeName) {
                        edittext_search.setText(animeName);
                    } else {
                        Log.e("here:", "watch_fragemnt3");
                       // edittext_search.setText(anime);
                    }


                    showOthersTitles(recyclerView_for_custom, main_ProgressBar, edittext_search);
                    bottomSheetDialog.setContentView(bsView);
                    bottomSheetDialog.show();
                }
            }
        });
    }

    private void showOthersTitles(RecyclerView recyclerViewForCustom, ProgressBar mainProgressBar, TextInputEditText edittextSearch) {
        if (getActivity() != null) {
            wrongTitleAdapter = new WrongTitleAdapter(animeFirstModelArrayList, getActivity(), this);

            int itemWidth = getResources().getDimensionPixelSize(R.dimen.item_widthSearchActivity); // Define your item width
            //CenterItemDecoration itemDecoration = new CenterItemDecoration(this, 0); // Adjust margin as needed
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
            recyclerViewForCustom.setLayoutManager(gridLayoutManager);
            //    recyclerView_for_custom.addItemDecoration(new GridSpacingItemDecoration(getActivity(), itemWidth, 3));
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerViewForCustom.setAdapter(wrongTitleAdapter);
            mainProgressBar.setVisibility(View.GONE);
            recyclerViewForCustom.setVisibility(View.VISIBLE);

        }
    }

    private void continueClickedGetEpisodeLink(View view, int epNo, String startEP, String endEP) {
        if (getActivity() != null) {
            if (epNo >= Integer.parseInt(startEP) && epNo <= Integer.parseInt(endEP)) {
                int pos = epNo - 1 - Integer.parseInt(startEP);
                animeEpisodesAdapter.performItemClick(view, pos);
                System.out.println("pos: epNo" + pos + " : " + epNo);
            } else {
                Handler handler = new Handler();

                epNo = epNo - 1;
                int epNo2 = epNo;
                int groupNumber = epNo / Constants.MAX_GROUP_SIZE + 1;
                System.out.println("groupNumber: " + groupNumber);
                animeEpisodeGroupsAdapter.performClick(view, groupNumber - 1);
                int staEP = Integer.parseInt(episodesGroupsArrayList.get(groupNumber - 1).getStartEP());
                int enEP = Integer.parseInt(episodesGroupsArrayList.get(groupNumber - 1).getEndEP());

                if (epNo2 >= staEP && epNo2 <= enEP) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = epNo2 - staEP;
                            System.out.println("epNo2: " + epNo2 + "   staEP: " + staEP + "    pos: " + pos + "   enEP: " + enEP);
                            progressBar_Main_center.setVisibility(View.GONE);
                            main_nested_scrollView.setEnabled(true);
                            animeEpisodesAdapter.performItemClick(view, pos);

                        }
                    }, 500);
                }
            }


        }
    }

    @Override
    public void onButtonClicked(String startEP, String endEP) {
        Log.e("hereINButtonClicked", "yes");
        setEpisodeAdapter(startEP, endEP);
    }

    @Override
    public void onItemClick(int position) {
        selectedTitle = position;
        System.out.println(myAnimeListID + animeSourceText+" : "+selectedTitle);
        animePreferences.edit().putString(myAnimeListID + animeSourceText, String.valueOf(selectedTitle)).apply();
        Log.e("selectedTitleonItemClick", String.valueOf(selectedTitle));
        progressBar_Main.setVisibility(View.VISIBLE);
        recyclerView_for_episode.setVisibility(View.GONE);
        setServerToSpinner();
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPaused) {
            startCheckingVariable();
        }
    }

}