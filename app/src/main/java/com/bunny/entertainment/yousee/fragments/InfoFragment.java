package com.bunny.entertainment.yousee.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.activities.ViewAllActivity;
import com.bunny.entertainment.yousee.adapters.CastAdapter;
import com.bunny.entertainment.yousee.adapters.InfoGenresAdapter;
import com.bunny.entertainment.yousee.adapters.WhereToWatchAdapter;
import com.bunny.entertainment.yousee.models.CastListModel;
import com.bunny.entertainment.yousee.models.WhereToWatchModel;
import com.bunny.entertainment.yousee.utils.WorldTimeAPIHelper;
import com.flaviofaria.kenburnsview.KenBurnsView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class InfoFragment extends Fragment {

    String dramaId, timerString, dramaUrl;
    Date parsedAiredTill = null, parsedAiredFrom = null;
    String dramaType;
    String dramaImage, dramaName, dramaSynopsis, dramaCountry, dramaEpisodes, dramaAired, dramaAiredDay,
            dramaOriginalNetwork, dramaDuration, dramaContentRating, dramaRating, dramaRank, dramaPopularity,
            dramaNativeTitle, dramaNativeTitleInEnglish, dramaScreenWriter, dramaDirector, dramaGenres;

    KenBurnsView animatedImageBackground;
    ImageView dramaThumbnail;
    TextView textView_dramaRating, releasing_drama, textView_dramaName, textView_dramaType, textView_dramaRating2,
            textView_dramaEpisodes, textView_dramaCountry, textView_dramaAired, textView_dramaAiredON,
            textView_dramaDuration, textView_dramaRank, textView_dramaPopularity, textView_dramaNativeTitle,
            textView_dramaTitleEnglish, textView_dramaScreenWriter, textView_dramaDirector, textView_OriginalNetwork, textView_ContentRating;
    ProgressBar thumbnailProgress, main_Progress, episodeProgressBar;
    RelativeLayout first_container;
    LinearLayout second_container;
    ArrayList<String> genresArrayList;
    ArrayList<CastListModel> castListModelArrayList;
    ArrayList<WhereToWatchModel> whereToWatchModelArrayList;
    InfoGenresAdapter infoGenresAdapter;
    CastAdapter castAdapter;
    WhereToWatchAdapter whereToWatchAdapter;
    RecyclerView recyclerView_for_genres, recyclerView_for_cast, recyclerView_for_watchHere;
    Animation startAnimation, endAnimation;
    TextView textView_dramaGenres_text, textView_watchHere_text, episodeReleasing, episodeDateTimer, episodeAiringInfo;
    RelativeLayout third_container, fifth_container_text, view_all_cast_container;
    WebView webView;
    private static final long COUNTDOWN_INTERVAL = 1000;
    Handler handlerNew;
    String episodeNumber, releaseDateTime;
    CountDownTimer countDownTimer;
    boolean isPaused = false;
    SharedPreferences dramaPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        first_container = view.findViewById(R.id.first_container);
        second_container = view.findViewById(R.id.second_container);
        main_Progress = view.findViewById(R.id.main_Progress);
        recyclerView_for_genres = view.findViewById(R.id.recyclerView_for_genres);
        recyclerView_for_cast = view.findViewById(R.id.recyclerView_for_cast);
        textView_dramaGenres_text = view.findViewById(R.id.textView_dramaGenres_text);
        fifth_container_text = view.findViewById(R.id.fifth_container_text);
        view_all_cast_container = view.findViewById(R.id.view_all_cast_container);
        third_container = view.findViewById(R.id.third_container);
        recyclerView_for_watchHere = view.findViewById(R.id.recyclerView_for_watchHere);
        textView_watchHere_text = view.findViewById(R.id.textView_watchHere_text);

        webView = view.findViewById(R.id.webView);

        animatedImageBackground = view.findViewById(R.id.animatedImageBackground);
        dramaThumbnail = view.findViewById(R.id.dramaThumbnail);
        textView_dramaRating = view.findViewById(R.id.textView_dramaRating);
        releasing_drama = view.findViewById(R.id.releasing_drama);
        textView_dramaName = view.findViewById(R.id.textView_dramaName);
        textView_dramaType = view.findViewById(R.id.textView_dramaType);
        textView_dramaRating2 = view.findViewById(R.id.textView_dramaRating2);
        textView_dramaEpisodes = view.findViewById(R.id.textView_dramaEpisodes);
        textView_dramaCountry = view.findViewById(R.id.textView_dramaCountry);
        textView_dramaAired = view.findViewById(R.id.textView_dramaAired);
        textView_dramaAiredON = view.findViewById(R.id.textView_dramaAiredON);
        textView_dramaDuration = view.findViewById(R.id.textView_dramaDuration);
        textView_dramaRank = view.findViewById(R.id.textView_dramaRank);
        textView_dramaPopularity = view.findViewById(R.id.textView_dramaPopularity);
        textView_dramaTitleEnglish = view.findViewById(R.id.textView_dramaTitleEnglish);
        textView_dramaScreenWriter = view.findViewById(R.id.textView_dramaScreenWriter);
        textView_dramaNativeTitle = view.findViewById(R.id.textView_dramaNativeTitle);
        textView_dramaDirector = view.findViewById(R.id.textView_dramaDirector);
        textView_OriginalNetwork = view.findViewById(R.id.textView_OriginalNetwork);
        textView_ContentRating = view.findViewById(R.id.textView_ContentRating);
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
            dramaId = bundle.getString("dramaId");
        }

        if(getActivity() != null){
            dramaPreferences = getActivity().getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
            dramaPreferences.edit().remove("dramaName").apply();
            dramaPreferences.edit().remove("dramaAired").apply();
            dramaPreferences.edit().remove("dramaId").apply();
            dramaPreferences.edit().remove("dramaNativeTitleInEnglish").apply();
            dramaPreferences.edit().remove("dramaType").apply();
        }

        scrapDramaData();


        return view;
    }

    private void scrapDramaData() {

        dramaUrl = "https://mydramalist.com/" + dramaId;


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here


            try {
                int dramaNativeTitleIndex = 0, dramaNativeTitleInEnglishIndex = 0, dramaScreenWriterIndex = 0, dramaDirectorIndex = 0, dramaGenresIndex = 0, dramaIndex = 0, dramaCountryIndex = 0, dramaEpisodesIndex = 0, dramaAiredIndex = 0, dramaAiredDayIndex = 0, dramaOriginalNetworkIndex = 0,
                        dramaDurationIndex = 0, dramaContentRatingIndex = 0, dramaRatingIndex = 0, dramaRankIndex = 0, dramaPopularityIndex = 0;

                Document doc = Jsoup.connect(dramaUrl).get();
                Elements box = doc.select("div.col-sm-4");
                dramaImage = box.select("img.img-responsive").attr("src"); //1

                Elements box2 = doc.select("div.col-sm-8"); //main
                dramaSynopsis = box2.select("div.show-synopsis").select("p").select("span").text(); //2

                Elements box3 = box2.select("div.show-detailsxss").select("ul.list.m-a-0");
                Elements box4 = box3.select("li");

              /*  int i = 0;
                if(box4.get(i).text().contains("Related")){
                    Log.e("check", "true");
                    i++;
                }*/

                try {
                    dramaNativeTitleIndex = findIndexWithText(box4, "Native Title");
                    dramaNativeTitleInEnglishIndex = findIndexWithText(box4, "Also Known As");
                    dramaScreenWriterIndex = findIndexWithText(box4, "Screenwriter");
                    dramaDirectorIndex = findIndexWithText(box4, "Director");
                    dramaGenresIndex = findIndexWithText(box4, "Genres");

                } catch (Exception exception) {
                    Log.e("ExceptionInDramaData1", Objects.requireNonNull(exception.getMessage()));
                }

                if (dramaNativeTitleIndex != -1) {
                    dramaNativeTitle = box4.get(dramaNativeTitleIndex).text();
                    dramaNativeTitle = dramaNativeTitle.substring(dramaNativeTitle.indexOf(":") + 1);
                } else {
                    dramaNativeTitle = getString(R.string.n_a);
                }

                if (dramaNativeTitleInEnglishIndex != -1) {
                    dramaNativeTitleInEnglish = box4.get(dramaNativeTitleInEnglishIndex).text(); //4
                    dramaNativeTitleInEnglish = dramaNativeTitleInEnglish.substring(dramaNativeTitleInEnglish.indexOf(":") + 1);
                } else {
                    dramaNativeTitleInEnglish = getString(R.string.n_a);
                }

                if (dramaScreenWriterIndex != -1) {
                    dramaScreenWriter = box4.get(dramaScreenWriterIndex).text(); //4
                    dramaScreenWriter = dramaScreenWriter.substring(dramaScreenWriter.indexOf(":") + 1);
                } else {
                    dramaScreenWriter = getString(R.string.n_a);
                }

                if (dramaDirectorIndex != -1) {
                    dramaDirector = box4.get(dramaDirectorIndex).text(); //6
                    dramaDirector = dramaDirector.substring(dramaDirector.indexOf(":") + 1);
                } else {
                    dramaDirector = getString(R.string.n_a);
                }

                if (dramaGenresIndex != -1) {
                    dramaGenres = box4.get(dramaGenresIndex).text(); //7
                    dramaGenres = dramaGenres.substring(dramaGenres.indexOf(":") + 1);
                } else {
                    dramaGenres = getString(R.string.n_a);
                }


                Elements box5 = doc.select("div.col-lg-4"); //main
                Elements box6 = box5.select("div.box.clear.hidden-sm-down").select("div.box-body").select("ul.list").select("li");

                try {
                    dramaIndex = findIndexWithText(box6, "Drama");
                    if (dramaIndex == -1) {
                        dramaIndex = findIndexWithText(box6, "TV Show");
                        dramaType = "(TV Show)";
                    }
                    if (dramaIndex == -1) {
                        dramaIndex = findIndexWithText(box6, "Special");
                        dramaType = "(Special)";
                    }
                    if (dramaIndex == -1) {
                        dramaIndex = findIndexWithText(box6, "Movie");
                        dramaType = "(Movie)";
                    }
                    dramaCountryIndex = findIndexWithText(box6, "Country");
                    dramaEpisodesIndex = findIndexWithText(box6, "Episodes");
                    dramaAiredIndex = findIndexWithText(box6, "Aired");
                    if (dramaAiredIndex == -1) {
                        dramaAiredIndex = findIndexWithText(box6, "Airs");
                    }
                    if (dramaAiredIndex == -1) {
                        dramaAiredIndex = findIndexWithText(box6, "Release Date");
                    }
                    dramaAiredDayIndex = findIndexWithText(box6, "Aired On");
                    dramaOriginalNetworkIndex = findIndexWithText(box6, "Original Network");
                    dramaDurationIndex = findIndexWithText(box6, "Duration");
                    dramaContentRatingIndex = findIndexWithText(box6, "Content Rating:");
                    dramaRatingIndex = findIndexWithText(box6, "Score");
                    dramaRankIndex = findIndexWithText(box6, "Ranked");
                    dramaPopularityIndex = findIndexWithText(box6, "Popularity");
                } catch (Exception exception) {
                    Log.e("ExceptionInDramaData2", exception.getMessage());
                }


                if (dramaIndex != -1) {
                    dramaName = box6.get(dramaIndex).text();
                    dramaName = dramaName.substring(dramaName.indexOf(":") + 1).trim();
                } else {
                    dramaName = getString(R.string.n_a);
                }

                if (dramaCountryIndex != -1) {
                    dramaCountry = box6.get(dramaCountryIndex).text(); //9
                    dramaCountry = dramaCountry.substring(dramaCountry.indexOf(":") + 1).trim();
                } else {
                    dramaCountry = getString(R.string.n_a);
                }

                if (dramaEpisodesIndex != -1) {
                    dramaEpisodes = box6.get(dramaEpisodesIndex).text(); //10
                    dramaEpisodes = dramaEpisodes.substring(dramaEpisodes.indexOf(":") + 1).trim();
                } else {
                    dramaEpisodes = getString(R.string.n_a);
                }

                if (dramaAiredIndex != -1) {
                    dramaAired = box6.get(dramaAiredIndex).text(); //11
                    dramaAired = dramaAired.substring(dramaAired.indexOf(":") + 1).trim();
                } else {
                    dramaAired = getString(R.string.n_a);
                }

                if (dramaAiredDayIndex != -1) {
                    dramaAiredDay = box6.get(dramaAiredDayIndex).text(); //12
                    dramaAiredDay = dramaAiredDay.substring(dramaAiredDay.indexOf(":") + 1).trim();
                } else {
                    dramaAiredDay = getString(R.string.n_a);
                }

                if (dramaOriginalNetworkIndex != -1) {
                    dramaOriginalNetwork = box6.get(dramaOriginalNetworkIndex).text(); //13
                    dramaOriginalNetwork = dramaOriginalNetwork.substring(dramaOriginalNetwork.indexOf(":") + 1).trim();
                } else {
                    dramaOriginalNetwork = getString(R.string.n_a);
                }

                if (dramaDurationIndex != -1) {
                    dramaDuration = box6.get(dramaDurationIndex).text(); //14
                    dramaDuration = dramaDuration.substring(dramaDuration.indexOf(":") + 1).trim();
                    if (dramaDuration.contains(".")) {
                        dramaDuration = dramaDuration.substring(dramaDuration.indexOf(":") + 1, dramaDuration.indexOf(".")).trim();
                    } else {
                        dramaDuration = dramaDuration.substring(dramaDuration.indexOf(":") + 1).trim();
                    }
                } else {
                    dramaDuration = getString(R.string.n_a);
                }

                if (dramaContentRatingIndex != -1) {
                    dramaContentRating = box6.get(dramaContentRatingIndex).text(); //15
                    dramaContentRating = dramaContentRating.substring(dramaContentRating.indexOf(":") + 1).trim();
                } else {
                    dramaContentRating = getString(R.string.n_a);
                }

                if (dramaRatingIndex != -1) {
                    dramaRating = box6.get(dramaRatingIndex).text();//16
                    dramaRating = dramaRating.substring(dramaRating.indexOf(":") + 1, dramaRating.indexOf("(")).trim();
                    if (dramaRating.contains("N/A")) {
                        dramaRating = getString(R.string._0_0);
                    }
                } else {
                    dramaRating = getString(R.string.n_a);
                }

                if (dramaRankIndex != -1) {
                    dramaRank = box6.get(dramaRankIndex).text(); //17
                    dramaRank = dramaRank.substring(dramaRank.indexOf(":") + 1).trim();
                } else {
                    dramaRank = getString(R.string.n_a);
                }

                if (dramaPopularityIndex != 1) {
                    dramaPopularity = box6.get(dramaPopularityIndex).text(); //18
                    dramaPopularity = dramaPopularity.substring(dramaPopularity.indexOf(":") + 1).trim();
                } else {
                    dramaPopularity = getString(R.string.n_a);
                }


                String airedTill = "", airedFrom = "";
                if (dramaAired.contains("-")) {
                    airedTill = dramaAired.substring(dramaAired.indexOf("-") + 1).trim();
                    airedFrom = dramaAired.substring(0, dramaAired.indexOf("-"));
                } else {
                    airedTill = dramaAired.trim();
                }


                SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

                //checking if there is aired date or not
                if (!dramaAired.contains("-") && airedTill.contains("?")) {

                    parsedAiredTill = inputDateFormat.parse(airedTill);
                    WorldTimeAPIHelper.fetchWorldTime(new WorldTimeAPIHelper.WorldTimeListener() {
                        @Override
                        public void onWorldTimeFetched(Date currentDate) {
                            if (isSameDate(parsedAiredTill, currentDate)) {
                                releasing_drama.setText(R.string.releasing_today);
                            } else {
                                releasing_drama.setText(R.string.upcoming);
                            }
                        }
                    });

                } else if (dramaAired.trim().equals("") || containsOnlyDash(dramaAired.trim())) {
                    handler.post(() -> {
                        releasing_drama.setText(R.string.unknown);
                    });
                } else {

                    try {
                        if (!airedTill.contains("?")) {
                            parsedAiredTill = inputDateFormat.parse(airedTill);
                        }
                        if (!airedFrom.equals("") && !airedFrom.contains("?")) {
                            parsedAiredFrom = inputDateFormat.parse(airedFrom);
                        }
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }

                    // Fetch current date from WorldTimeAPI and checking release date
                    WorldTimeAPIHelper.fetchWorldTime(new WorldTimeAPIHelper.WorldTimeListener() {
                        @Override
                        public void onWorldTimeFetched(Date currentDate) {
                            String datePassed, gettingEpisodeAiringInfo = getString(R.string.getting_airing_info);

                            if (parsedAiredTill != null && isSameDate(parsedAiredTill, currentDate)) {
                                // Current date is same as input date
                                //runEpisodeReleaseCheck();
                                if(dramaType != null && dramaType.contains("(Movie)")){
                                    datePassed = "Releasing today";
                                } else{
                                    datePassed = "Completing today";
                                }
                                releasing_drama.setText(datePassed);
                                /*episodeAiringInfo.setText(gettingEpisodeAiringInfo);
                                episodeAiringInfo.setVisibility(View.VISIBLE);
                                episodeProgressBar.setVisibility(View.VISIBLE);*/
                            } else if (parsedAiredTill != null && parsedAiredFrom != null && parsedAiredTill.after(currentDate) && parsedAiredFrom.before(currentDate) || parsedAiredFrom != null && isSameDate(parsedAiredFrom, currentDate)) {
                                // Current date has passed the input date
                                datePassed = "Releasing";
                                runEpisodeReleaseSavedCheck(datePassed, gettingEpisodeAiringInfo);
                                //runEpisodeReleaseCheck();
                            } else if (parsedAiredTill != null && parsedAiredFrom == null && parsedAiredTill.after(currentDate)) {
                                datePassed = "Upcoming";
                                releasing_drama.setText(datePassed);
                            } else if (parsedAiredFrom != null && dramaAired.contains("?") && parsedAiredFrom.before(currentDate)) {
                                datePassed = "Releasing";
                                runEpisodeReleaseSavedCheck(datePassed, gettingEpisodeAiringInfo);
                                //runEpisodeReleaseCheck();
                            } else if (parsedAiredFrom != null && dramaAired.contains("?") && parsedAiredFrom.after(currentDate)) {
                                datePassed = "Upcoming";
                                releasing_drama.setText(datePassed);
                            } else if (parsedAiredFrom != null && parsedAiredTill != null && parsedAiredFrom.after(currentDate)) {
                                datePassed = "Upcoming";
                                releasing_drama.setText(datePassed);
                            } else {
                                if(dramaType != null && dramaType.contains("(Movie)")){
                                    datePassed = "Released";
                                } else{
                                    datePassed = "Completed";
                                }
                                releasing_drama.setText(datePassed);
                            }
                            if (getActivity() != null) {
                                Animation startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                                releasing_drama.startAnimation(startAnimation);
                            }
                        }
                    });

                }


                Elements box7 = doc.select("div.col-lg-8.col-md-8.col-rightx").select("div.box.clear").select("ul.list.no-border.p-b.credits");

                if (!box7.isEmpty()) {

                    castListModelArrayList = new ArrayList<>();

                    Elements liList = box7.select("li.list-item");
                    for (Element liElement : liList) {

                        String ImageUrl = liElement.select("div.credits-left img").attr("data-src");
                        String Name = liElement.select("a.text-primary b").text();
                        String Id = liElement.select("a.text-primary").attr("href");
                        String characterName = liElement.select("div.text-ellipsis small a.text-primary").text();
                        String role = liElement.select("small.text-muted").text();

                        CastListModel castListModel = new CastListModel(Name, characterName, role, ImageUrl, Id);
                        castListModelArrayList.add(castListModel);
                    }
                }


                Elements box8 = doc.select("div.col-lg-8.col-md-8.col-rightx").select(".box-body.wts .col-xs-12.col-lg-4.m-b-sm");
                if (!box8.isEmpty()) {
                    whereToWatchModelArrayList = new ArrayList<>();

                    for (Element platformList : box8) {
                        String name = platformList.select("div.col-xs-10.col-lg-9").select("b").text();
                        String imageUrl = platformList.select("div.col-xs-2.col-lg-3").select("img.img-responsive").attr("data-src");
                        String subscription = Objects.requireNonNull(platformList.select("div").last()).text();

                        WhereToWatchModel whereToWatchModel = new WhereToWatchModel(name, imageUrl, subscription);
                        whereToWatchModelArrayList.add(whereToWatchModel);

                    }
                }



                //Log.e("box", document.html());


                genresArrayList = new ArrayList<>();
                genresArrayList = extractWordsFromGenresString(dramaGenres);


                handler.post(() -> {
                    if (getActivity() != null) {
                        //load genres to UI
                        setDataToUI();
                        setDataToSharedPref();
                        if (genresArrayList != null) {
                            infoGenresAdapter = new InfoGenresAdapter(genresArrayList, getActivity(), "FromDramaFragment");
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                            recyclerView_for_genres.setLayoutManager(linearLayoutManager);
                            recyclerView_for_genres.setAdapter(infoGenresAdapter);
                            recyclerView_for_genres.setVisibility(View.VISIBLE);
                        }

                        //load cast to UI
                        if (castListModelArrayList != null) {
                            castAdapter = new CastAdapter(castListModelArrayList, getActivity(), "fromInfoFragment");
                            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                            recyclerView_for_cast.setLayoutManager(linearLayoutManager2);
                            //recyclerView_for_cast.addItemDecoration(new CenteredHorizontalGridItemDecoration(requireContext(), itemWidth, 2));
                            recyclerView_for_cast.setAdapter(castAdapter);
                            recyclerView_for_cast.setVisibility(View.VISIBLE);
                        } else {

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
                    }
                });


                //UI Thread work here
                handler.post(this::setDataToUI);


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });
    }

    private void runEpisodeReleaseSavedCheck(String datePassed, String gettingEpisodeAiringInfo) {
        String gotOldReleasedDate = dramaPreferences.getString(dramaId+"/Releasing", null);

        if(gotOldReleasedDate != null){
            String episodeNo = gotOldReleasedDate.substring(0, gotOldReleasedDate.indexOf("/"));
            String releaseDateTime = gotOldReleasedDate.substring(gotOldReleasedDate.indexOf("/")+1);
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.ENGLISH);
            try {
                Date targetDateTime = format.parse(releaseDateTime);
                Date currentDateTime = new Date();
                if (currentDateTime.after(targetDateTime)) {
                    dramaPreferences.edit().remove(dramaId+"/Releasing").apply();

                    runEpisodeReleaseCheck();
                } else {
                    gotEpisode(episodeNo, releaseDateTime);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            episodeAiringInfo.setText(gettingEpisodeAiringInfo);
            episodeAiringInfo.setVisibility(View.VISIBLE);
            episodeProgressBar.setVisibility(View.VISIBLE);
            releasing_drama.setText(datePassed);
            runEpisodeReleaseCheck();
        }
    }

    private void setDataToSharedPref() {
        if(getActivity() != null){
            dramaPreferences.edit().putString("dramaName", dramaName).apply();
            dramaPreferences.edit().putString("dramaAired", dramaAired).apply();
            dramaPreferences.edit().putString("dramaId", dramaId).apply();
            dramaPreferences.edit().putString("dramaNativeTitleInEnglish", dramaNativeTitleInEnglish).apply();
            dramaPreferences.edit().putString("dramaType", dramaType).apply();
            System.out.println("InfoFragment:DramaType:  "+dramaType);
        }
    }

    private void runEpisodeReleaseCheck() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void run() {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webView.getSettings().setBlockNetworkImage(true);
                webView.loadUrl(dramaUrl);

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        webView.loadUrl("javascript:window.android.getContent(document.getElementsByTagName('html')[0].innerHTML);");
                    }
                });
                webView.addJavascriptInterface(new JsInterface(), "android");
            }
        });
    }

    private class JsInterface {
        @android.webkit.JavascriptInterface
        public void getContent(String htmlContent) {
            Document document = Jsoup.parse(htmlContent);
            Element episodeInfo = document.selectFirst("#upcoming-episode");
            //String episodeNumber = "", releaseDateTime = "";
            if (episodeInfo != null) {
                episodeNumber = episodeInfo.select(".release-episode").text();
                releaseDateTime = episodeInfo.select(".release-date").text();
            }else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        episodeAiringInfo.startAnimation(endAnimation);
                        episodeAiringInfo.setVisibility(View.GONE);
                        episodeProgressBar.startAnimation(endAnimation);
                        episodeProgressBar.setVisibility(View.GONE);
                    }
                });
            }
            gotEpisode(episodeNumber, releaseDateTime);
        }
    }

    private void gotEpisode(String episodeNumber, String releaseDateTime) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ViewGroup parentView = (ViewGroup) webView.getParent();
                if (parentView != null) {
                    parentView.removeView(webView);
                }
                webView.destroy();
            }
        });

        //setting timer
        if (releaseDateTime != null) {

            if(!episodeNumber.isEmpty() || !releaseDateTime.isEmpty()){
                dramaPreferences.edit().putString(dramaId+"/Releasing", episodeNumber+"/"+releaseDateTime).apply();
            }
         //   Log.e("releaseDateTime", releaseDateTime);

            handlerNew = new Handler(Looper.getMainLooper());
            handlerNew.post(() -> {
                episodeReleasing.setText(episodeNumber);
                episodeProgressBar.setVisibility(View.GONE);
                episodeAiringInfo.setVisibility(View.GONE);
                episodeReleasing.setVisibility(View.VISIBLE);
                episodeReleasing.startAnimation(startAnimation);
                episodeDateTimer.setVisibility(View.VISIBLE);
                episodeDateTimer.startAnimation(startAnimation);
            });


            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.ENGLISH);
            Date targetDateTime = null;
            try {
                targetDateTime = sdf.parse(releaseDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Calculate the time difference in milliseconds between the current time and target time
            long currentTimeMillis = System.currentTimeMillis();
            long targetTimeMillis = targetDateTime != null ? targetDateTime.getTime() : 0;
            long timeDifferenceMillis = targetTimeMillis - currentTimeMillis;

            if (timeDifferenceMillis > 0) {
                // Create a CountDownTimer
                countDownTimer = new CountDownTimer(timeDifferenceMillis, COUNTDOWN_INTERVAL) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000;
                        long minutes = (seconds / 60) % 60;
                        long hours = (seconds / 3600) % 24;
                        long days = seconds / (3600 * 24);

                        timerString = days + " DAYS " + hours + " HRS " + minutes + " MINS " + seconds % 60 + " SECS";
                        handlerNew.post(() -> episodeDateTimer.setText(timerString));

                        //Log.e("Countdown", "Days: " + days + " Hours: " + hours + " Minutes: " + minutes + " Seconds: " + (seconds % 60));
                    }

                    @Override
                    public void onFinish() {
                        Log.d("Countdown", "Countdown finished");
                    }
                };

                // Start the countdown timer
                countDownTimer.start();
            } else {
                Log.d("Countdown", "Target time has already passed");
            }
        }
    }

    private void setDataToUI() {

        try {

            if(getActivity() != null){
                Glide.with(getActivity())
                        .load(dramaImage)
                        .transform(new BlurTransformation(20, 2))
                        .into(animatedImageBackground);

                Glide.with(getActivity()).load(dramaImage).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }
                }).into(dramaThumbnail);
            }


            textView_dramaName.setText(dramaName);
            textView_dramaName.startAnimation(startAnimation);

            textView_dramaType.setText(dramaType);
            textView_dramaType.startAnimation(startAnimation);

            textView_dramaRating.setText(dramaRating);
            textView_dramaName.startAnimation(startAnimation);

            String dramaRating2 = dramaRating + "/10";
            textView_dramaRating2.setText(dramaRating2);
            textView_dramaRating2.startAnimation(startAnimation);

            textView_dramaEpisodes.setText(dramaEpisodes);
            textView_dramaEpisodes.startAnimation(startAnimation);

            textView_dramaCountry.setText(dramaCountry);
            textView_dramaCountry.startAnimation(startAnimation);

            textView_dramaAired.setText(dramaAired);
            textView_dramaAired.startAnimation(startAnimation);

            textView_dramaAiredON.setText(dramaAiredDay);
            textView_dramaAiredON.startAnimation(startAnimation);

            textView_dramaDuration.setText(dramaDuration);
            textView_dramaDuration.startAnimation(startAnimation);

            textView_dramaRank.setText(dramaRank);
            textView_dramaRank.startAnimation(startAnimation);

            textView_dramaPopularity.setText(dramaPopularity);
            textView_dramaPopularity.startAnimation(startAnimation);

            textView_dramaNativeTitle.setText(dramaNativeTitle);
            textView_dramaNativeTitle.startAnimation(startAnimation);

            if(!dramaNativeTitleInEnglish.trim().equals("")){
                textView_dramaTitleEnglish.setText(dramaNativeTitleInEnglish);
            }else {
                textView_dramaTitleEnglish.setText(R.string.n_a);
            }
            textView_dramaTitleEnglish.startAnimation(startAnimation);

            textView_dramaScreenWriter.setText(dramaScreenWriter);
            textView_dramaScreenWriter.startAnimation(startAnimation);

            textView_dramaDirector.setText(dramaDirector);
            textView_dramaDirector.startAnimation(startAnimation);

            textView_OriginalNetwork.setText(dramaOriginalNetwork);
            textView_OriginalNetwork.startAnimation(startAnimation);

            textView_ContentRating.setText(dramaContentRating);
            textView_ContentRating.startAnimation(startAnimation);

            if(genresArrayList != null){
                textView_dramaGenres_text.setVisibility(View.VISIBLE);
            }else {
                third_container.setVisibility(View.GONE);
            }

            if(castListModelArrayList != null){
                fifth_container_text.setVisibility(View.VISIBLE);
            }


            main_Progress.setVisibility(View.GONE);
            first_container.setVisibility(View.VISIBLE);
            second_container.setVisibility(View.VISIBLE);

           // String stringListStr = dramaPreferences.getString("watchingDramaList", null);
           // Log.e("stringListStr", stringListStr);

            view_all_cast_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(requireActivity(), ViewAllActivity.class);
                    intent.putExtra("whereFrom", "CastDetails");
                    intent.putExtra("dramaName", dramaName);
                    intent.putExtra("dramaId", dramaId);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> extractWordsFromGenresString(String input) {
        ArrayList<String> wordList = new ArrayList<>();

        String[] words = input.split(",\\s*"); // Split by comma and optional spaces
        for (String word : words) {
            wordList.add(word.trim()); // Trim to remove any leading/trailing spaces
        }

        return wordList;
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

    private static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean containsOnlyDash(String str) {
        return str.matches("^-*$");
    }

    @Override
    public void onDestroy() {
        if(webView != null){
            ViewGroup parentView = (ViewGroup) webView.getParent();
            if (parentView != null) {
                parentView.removeView(webView);
            }
            webView.destroy();
        }


        super.onDestroy();
    }

    @Override
    public void onStop() {
        if(countDownTimer != null){
            countDownTimer.cancel();
            isPaused = true;
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if(countDownTimer != null){
            countDownTimer.cancel();
            isPaused = true;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if(isPaused){
            gotEpisode(episodeNumber, releaseDateTime);
        }
        super.onResume();
    }
}