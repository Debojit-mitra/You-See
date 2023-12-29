package com.bunny.entertainment.yousee.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.AnimeAdapterFragment;
import com.bunny.entertainment.yousee.adapters.AnimeCharacterListAdapter;
import com.bunny.entertainment.yousee.adapters.AnimeRelationAdapter;
import com.bunny.entertainment.yousee.adapters.CastAdapter;
import com.bunny.entertainment.yousee.adapters.InfoGenresAdapter;
import com.bunny.entertainment.yousee.adapters.WhereToWatchAdapter;
import com.bunny.entertainment.yousee.models.CastListModel;
import com.bunny.entertainment.yousee.models.WhereToWatchModel;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;
import com.bunny.entertainment.yousee.models.anime.AnimeRelationsModel;
import com.bunny.entertainment.yousee.models.anime.CharacterListModel;
import com.bunny.entertainment.yousee.utils.Constants;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class InfoFragment_3 extends Fragment {

    String malId, animeUrl, mediaType, istTimeTogether, timerString;
    String animeImage, animeName, animeNameNativeLang, animeSynopsis, animeEpisodes, animeAired, animeAiredON, animeRank, animeAlsoKnownAs,
            animeDuration, animePopularity, isAdultAnime, animeStatus, animePrequel, animeSequel, animeType, animeProducers, animeSource, animeDemographic,
            animeStudio, animeGenres, animeTheme, malRating, animeContentRating, animeYoutubeTrailerID;

    KenBurnsView animatedImageBackground;
    ImageView animeThumbnail, sequel_cardThumbnail, prequel_cardThumbnail;
    TextView textView_animeRating, textView_animeRating2, releasing_anime, textView_animeName, textView_animeType,
            textView_animeEpisodes, textView_animeAired, textView_animeDuration, textView_animePopularity, textView_animeRanked, textView_Adult,
            textView_overview, textView_animeSeasons, textView_animeTheme, textView_animeDemographic, textView_animeAiredON, textView_animeNativeTitle,
            textView_animeTitleEnglish, textView_animeProducers, textView_animeStudio, textView_animeSource, textView_ContentRating, textView_detailsBy_text,
            textView_animeRelations_text;
    ExpandableTextView textViewExpanded_animeSynopsis;
    ScrollView main_ScrollView;
    ProgressBar thumbnailProgress, main_Progress, episodeProgressBar, animeRelations_Progress, animeCharacter_Progress;
    RelativeLayout first_container, third_container, fifth_container_text, view_all_cast_container, fourth_container,fifth_container;
    LinearLayout second_container;
    ArrayList<String> genresArrayList;
    ArrayList<AnimeRelationsModel> animeRelationsModelArrayList;
    ArrayList<CharacterListModel> characterListModelArrayList;
    InfoGenresAdapter infoGenresAdapter;
    AnimeCharacterListAdapter animeCharacterListAdapter;
    AnimeRelationAdapter animeRelationAdapter;
    WhereToWatchAdapter whereToWatchAdapter;
    RecyclerView recyclerView_for_genres, recyclerView_for_cast, recyclerView_for_Relations, recyclerView_for_character;
    Animation startAnimation, endAnimation;
    TextView textView_animeGenres_text, textView_animeTrailer_text, textView_watchHere_text, episodeReleasing, episodeDateTimer, episodeAiringInfo;
    WebView webView;
    private static final long COUNTDOWN_INTERVAL = 1000;
    Handler handlerNew;
    String episodeNumber, releaseDateTime;
    CountDownTimer countDownTimer;
    boolean isPaused = false;
    SharedPreferences animePreferences;
    boolean isMovie = true, triedAnimeName = false, triedAnimeNameSynonym = false, isTriedAnimeNativeName = false;
    YouTubePlayerView youtube_player_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_3, container, false);

        main_ScrollView = view.findViewById(R.id.main_ScrollView);
        first_container = view.findViewById(R.id.first_container);
        second_container = view.findViewById(R.id.second_container);
        main_Progress = view.findViewById(R.id.main_Progress);
        animeRelations_Progress = view.findViewById(R.id.animeRelations_Progress);
        recyclerView_for_genres = view.findViewById(R.id.recyclerView_for_genres);
        recyclerView_for_Relations = view.findViewById(R.id.recyclerView_for_Relations);
        textView_animeRelations_text = view.findViewById(R.id.textView_animeRelations_text);
        textView_animeGenres_text = view.findViewById(R.id.textView_animeGenres_text);
        third_container = view.findViewById(R.id.third_container);
        youtube_player_view = view.findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youtube_player_view);
        fourth_container = view.findViewById(R.id.fourth_container);
        fifth_container = view.findViewById(R.id.fifth_container);
        recyclerView_for_character = view.findViewById(R.id.recyclerView_for_character);
        animeCharacter_Progress = view.findViewById(R.id.animeCharacter_Progress);

        //recyclerView_for_watchHere = view.findViewById(R.id.recyclerView_for_watchHere);
        //textView_watchHere_text = view.findViewById(R.id.textView_watchHere_text);

        //webView = view.findViewById(R.id.webView);

        animatedImageBackground = view.findViewById(R.id.animatedImageBackground);
        animeThumbnail = view.findViewById(R.id.animeThumbnail);
        textView_animeRating = view.findViewById(R.id.textView_animeRating);
        releasing_anime = view.findViewById(R.id.releasing_anime);
        textView_animeName = view.findViewById(R.id.textView_animeName);
        textView_animeType = view.findViewById(R.id.textView_animeType);
        textView_animeRating2 = view.findViewById(R.id.textView_animeRating2);
        textView_animeEpisodes = view.findViewById(R.id.textView_animeEpisodes);
        textView_animeAired = view.findViewById(R.id.textView_animeAired);
        textView_animeAiredON = view.findViewById(R.id.textView_animeAiredON);
        textView_animeDuration = view.findViewById(R.id.textView_animeDuration);
        textView_animeRanked = view.findViewById(R.id.textView_animeRanked);
        textView_animePopularity = view.findViewById(R.id.textView_animePopularity);
        textView_animeTitleEnglish = view.findViewById(R.id.textView_animeTitleEnglish);
        textView_animeProducers = view.findViewById(R.id.textView_animeProducers);
        textView_animeNativeTitle = view.findViewById(R.id.textView_animeNativeTitle);
        textView_animeTheme = view.findViewById(R.id.textView_animeTheme);
        textView_animeSource = view.findViewById(R.id.textView_animeSource);
        textView_animeStudio = view.findViewById(R.id.textView_animeStudio);
        textView_animeDemographic = view.findViewById(R.id.textView_animeDemographic);
        textView_ContentRating = view.findViewById(R.id.textView_ContentRating);
        textViewExpanded_animeSynopsis = main_ScrollView.findViewById(R.id.textViewExpanded_animeSynopsis);
        textView_detailsBy_text = view.findViewById(R.id.textView_detailsBy_text);
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
            malId = bundle.getString("malId");
            System.out.println(malId);
        }

        if (getActivity() != null) {
            animePreferences = getActivity().getSharedPreferences("animePreferences", Context.MODE_PRIVATE);
            animePreferences.edit().remove("animeName").apply();
            animePreferences.edit().remove("myAnimeListID").apply();
            animePreferences.edit().remove("animeType").apply();
        }

        textViewExpanded_animeSynopsis.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                if (isExpanded) {
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        int textY = textView.getLayout().getLineTop(textView.getLineCount()) + textView.getHeight() - (textView.getWidth() / 4);
                        main_ScrollView.smoothScrollTo(0, textY);
                    } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        int textY = textView.getLayout().getLineTop(textView.getLineCount()) + (textView.getHeight() * 3) + (textView.getHeight() / 2);
                        main_ScrollView.smoothScrollTo(0, textY);
                    }
                }
            }
        });


        scrapAnimeData();


        return view;
    }

    private void scrapAnimeData() {

        animeUrl = Constants.MYANIMELIST_URL + "/anime/" + malId;
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        ExecutorService executorService2 = Executors.newFixedThreadPool(1);
        ExecutorService executorService3 = Executors.newFixedThreadPool(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {

            try {

                Document doc = Jsoup.connect(animeUrl).get();
                Elements box = doc.select("div#contentWrapper");

                animeName = box.select("h1.title-name").text();
                //animeNameEnglish = box.select("p.title-english.title-inherit").text();

                Element imgElement = doc.selectFirst("img[itemprop=image]");
                if (imgElement != null) {
                    animeImage = imgElement.attr("data-src");
                }

                Elements divs = box.select("div.leftside").select("div.spaceit_pad");
                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Synonyms:")) {
                        animeAlsoKnownAs = text.replace("Synonyms:", "").trim();
                        break;
                    }
                }

                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Japanese:")) {
                        animeNameNativeLang = text.replace("Japanese:", "").trim();
                        break;
                    }
                }

                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Type:")) {
                        animeType = text.replace("Type:", "").trim();
                        break;
                    }
                }

                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Episodes:")) {
                        animeEpisodes = text.replace("Episodes:", "").trim();
                        break;
                    }
                }

                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Status:")) {
                        animeStatus = text.replace("Status:", "").trim();
                        break;
                    }
                }


                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Aired:")) {
                        animeAired = text.replace("Aired:", "").trim();
                        break;
                    }
                }

                if (animeName != null) {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            checkIfCurrentlyAiring();
                        }
                    });
                }

                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Broadcast:")) {
                        animeAiredON = text.replace("Broadcast:", "").trim();
                        timeConversion(animeAiredON);
                        break;
                    }
                }

                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Producers:")) {
                        animeProducers = text.replace("Producers:", "").trim();
                        break;
                    }
                }

                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Producers:")) {
                        animeProducers = text.replace("Producers:", "").trim();
                        break;
                    }
                }

                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Source:")) {
                        animeSource = text.replace("Source:", "").trim();
                        break;
                    }
                }

                for (Element div : divs) {
                    String text = div.text();
                    if (text.startsWith("Studios:")) {
                        animeStudio = text.replace("Studios:", "").trim();
                        break;
                    }
                }
                for (Element div : divs) {
                    Elements genresElement = div.select("span.dark_text:contains(Genres:)");
                    if (!genresElement.isEmpty()) {
                        Elements genreLinks = div.select("a[title]");
                        StringBuilder genres = new StringBuilder();
                        for (Element genreLink : genreLinks) {
                            genres.append(genreLink.text()).append(", ");
                        }
                        genresArrayList = InfoFragment.extractWordsFromGenresString(String.valueOf(genres));
                        break;
                    }
                }
                for (Element div : divs) {
                    Elements eleTheme = div.select("span.dark_text:contains(Theme:)");
                    if (!eleTheme.isEmpty()) {
                        Elements themeLinks = div.select("a[title]");
                        animeTheme = themeLinks.text();
                        break;
                    }
                }

                for (Element div : divs) {
                    Elements eleDemographic = div.select("span.dark_text:contains(Demographic:)");
                    if (!eleDemographic.isEmpty()) {
                        Elements demographicLinks = div.select("a[title]");
                        animeDemographic = demographicLinks.text();
                        break;
                    }
                }

                for (Element div : divs) {
                    Elements eleDuration = div.select("span.dark_text:contains(Duration:)");
                    if (!eleDuration.isEmpty()) {
                        animeDuration = div.text();
                        animeDuration = animeDuration.replace("Duration:", "").trim();
                        break;
                    }
                }

                for (Element div : divs) {
                    Elements eleRating = div.select("span.dark_text:contains(Rating:)");
                    if (!eleRating.isEmpty()) {
                        animeContentRating = div.text();
                        animeContentRating = animeContentRating.replace("Rating:", "").trim();
                        break;
                    }
                }

                Elements rightSide = doc.select("div.rightside");
                Elements rating_Rank_PopEle = rightSide.select("div.anime-detail-header-stats");
                malRating = rating_Rank_PopEle.select("div.fl-l.score").select("div.score-label").text();

                animeRank = rating_Rank_PopEle.select("div.di-ib.ml12").select("span.numbers.ranked strong").text();
                animePopularity = rating_Rank_PopEle.select("div.di-ib.ml12").select("span.numbers.popularity strong").text();

                Elements synopsisEle = rightSide.select("p[itemprop=description]");
                Element descriptionElement = synopsisEle.first();
                if (descriptionElement != null) {
                    animeSynopsis = descriptionElement.text();
                }

                Elements youtubeVideoIdElement = rightSide.select("div.anime-detail-header-video");
                animeYoutubeTrailerID = youtubeVideoIdElement.select("a.iframe").attr("href");
                if(animeYoutubeTrailerID.contains("youtube")){
                    animeYoutubeTrailerID = animeYoutubeTrailerID.substring(animeYoutubeTrailerID.indexOf("ed/")+3);
                    animeYoutubeTrailerID = animeYoutubeTrailerID.substring(0, animeYoutubeTrailerID.indexOf("?"));
                }


                Elements prequelSequelEle = rightSide.select("table.anime_detail_related_anime tr");
                for (Element row : prequelSequelEle) {
                    String animeRelationType = Objects.requireNonNull(row.selectFirst("td.ar.fw-n.borderClass")).text();
                    if (animeRelationType.contains(":")) {
                        animeRelationType = animeRelationType.replace(":", "");
                    }
                    String myAnimeListID = Objects.requireNonNull(row.selectFirst("td.borderClass a")).attr("href");
                    myAnimeListID = myAnimeListID.substring(myAnimeListID.indexOf("me/") + 3);
                    myAnimeListID = myAnimeListID.substring(0, myAnimeListID.indexOf("/"));
                    String animeTitle = Objects.requireNonNull(row.selectFirst("td.borderClass a")).text();

                    if (!animeRelationType.equals("Adaptation")) {
                        if (animeRelationsModelArrayList == null) {
                            animeRelationsModelArrayList = new ArrayList<>();
                        }
                        AnimeRelationsModel animeRelationsModel = new AnimeRelationsModel(myAnimeListID, animeRelationType, animeTitle);
                        animeRelationsModelArrayList.add(animeRelationsModel);
                    }
                }

                executorService2.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (animeRelationsModelArrayList != null && !animeRelationsModelArrayList.get(0).getAnimeRelationType().isEmpty()) {
                            handler.post(()-> {
                                textView_animeRelations_text.setVisibility(View.VISIBLE);
                                animeRelations_Progress.setVisibility(View.VISIBLE);
                            });
                            for (int i = 0; i < animeRelationsModelArrayList.size(); i++) {
                                String animeUrl = Constants.MYANIMELIST_URL + "/anime/" + animeRelationsModelArrayList.get(i).getMyAnimeListID();
                                try {
                                    Document doc = Jsoup.connect(animeUrl).get();
                                    Element imgElement = doc.selectFirst("img[itemprop=image]");
                                    String imageForRelation;
                                    if (imgElement != null) {
                                        imageForRelation = imgElement.attr("data-src");
                                        if (!imageForRelation.isEmpty()) {
                                            animeRelationsModelArrayList.get(i).setAnimeImage(imageForRelation);
                                        }
                                    }
                                    Elements divs = box.select("div.leftside").select("div.spaceit_pad");
                                    for (Element div : divs) {
                                        String text = div.text();
                                        String epsForRelation;
                                        if (text.startsWith("Episodes:")) {
                                            epsForRelation = text.replace("Episodes:", "").trim();
                                            if (!epsForRelation.isEmpty()) {
                                                animeRelationsModelArrayList.get(i).setAnimeEpisodes(epsForRelation);
                                            }
                                            break;
                                        }
                                    }
                                    Elements rightSide = doc.select("div.rightside");
                                    Elements rating_Rank_PopEle = rightSide.select("div.anime-detail-header-stats");
                                    String malRate = rating_Rank_PopEle.select("div.fl-l.score").select("div.score-label").text();
                                    animeRelationsModelArrayList.get(i).setAnimeRating(malRate);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            handler.post(() -> {
                                setAnimeRelationData();
                            });
                        }
                    }
                });
                executorService2.shutdown();

                executorService3.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Elements characterDetailsLeft = rightSide.select("div.detail-characters-list").select("div.left-column").eq(0);
                            characterListModelArrayList = new ArrayList<>();
                            getCharacterInfo(characterDetailsLeft);
                            Elements characterDetailsRight = rightSide.select("div.detail-characters-list").select("div.left-right").eq(0);
                            getCharacterInfo(characterDetailsRight);
                        } catch (Exception e){
                          e.printStackTrace();
                        };
                    }
                });
                executorService3.shutdown();
                handler.post(this::setDataToUI);


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();

    }

    private void getCharacterInfo(Elements characterDetails) {
        Handler handler = new Handler(Looper.getMainLooper());
        Elements tableEle = characterDetails.select("tbody tr");
        for (Element characterElement : tableEle) {
            Elements tds = characterElement.select("td");
            if(tds.size() > 2) {
                String characterName = tds.select(".h3_characters_voice_actors a").text();
                String role = tds.select(".spaceit_pad small").text();
                String imageURL = tds.select("img").attr("data-srcset");
                imageURL = imageURL.substring(imageURL.indexOf("1x,")+3);
                imageURL = imageURL.replace(" 2x","").trim();
                String characterLink = tds.select(".h3_characters_voice_actors a").attr("href");
                Uri uri = Uri.parse(characterLink);
                int segmentsCount = uri.getPathSegments().size();
                String characterId = uri.getPathSegments().get(segmentsCount - 2);
                CharacterListModel characterListModel = new CharacterListModel(characterName, role, imageURL, characterId);
                characterListModelArrayList.add(characterListModel);
            }
        }
        handler.post(this::setAnimeCharacterList);
    }

    private void setAnimeCharacterList() {
        if (getActivity() != null) {
            animeCharacterListAdapter = new AnimeCharacterListAdapter(characterListModelArrayList, getActivity());
            fifth_container.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_character.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_character.setLayoutManager(linearLayoutManager);
            //snapHelper.recyclerView_for_character(recyclerView_for_top_shows);
            recyclerView_for_character.setAdapter(animeCharacterListAdapter);
            animeCharacter_Progress.setVisibility(View.GONE);
            recyclerView_for_Relations.setVisibility(View.VISIBLE);
        }
    }

    private void setAnimeRelationData() {
        if (getActivity() != null) {
            animeRelationAdapter = new AnimeRelationAdapter(animeRelationsModelArrayList, getActivity());

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.recyclerviewscroll_left_right); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_Relations.setLayoutAnimation(controller);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView_for_Relations.setLayoutManager(linearLayoutManager);
            //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
            recyclerView_for_Relations.setAdapter(animeRelationAdapter);
            textView_animeRelations_text.setVisibility(View.VISIBLE);
            animeRelations_Progress.setVisibility(View.GONE);
            recyclerView_for_Relations.setVisibility(View.VISIBLE);
        }
    }

    private void checkIfCurrentlyAiring() {
        if (animeStatus.toLowerCase().contains("currently airing")) {
            runEpisodeReleaseSavedCheck();
        }
    }

    private void getNextEpisodeAiring() {
        String urlForMAlId = "";
        if (triedAnimeName && triedAnimeNameSynonym && !isTriedAnimeNativeName) {
            String animeSynonym = "";
            if (animeAlsoKnownAs != null && animeAlsoKnownAs.contains(",")) {
                animeSynonym = animeAlsoKnownAs.substring(0, animeAlsoKnownAs.indexOf(","));
                urlForMAlId = Constants.MY_CONSUMET_API + "meta/anilist/" + animeSynonym + "?page=1";
            } else {
                urlForMAlId = Constants.MY_CONSUMET_API + "meta/anilist/" + animeAlsoKnownAs + "?page=1";
            }
        } else if (triedAnimeNameSynonym && isTriedAnimeNativeName) {
            urlForMAlId = Constants.MY_CONSUMET_API + "meta/anilist/" + animeNameNativeLang + "?page=1";
        } else {
            urlForMAlId = Constants.MY_CONSUMET_API + "meta/anilist/" + animeName + "?page=1";
        }

        try {
            if (getActivity() != null) {
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, urlForMAlId, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");
                            if (jsonArrayResults.length() > 0) {
                                JSONObject firstObject = jsonArrayResults.getJSONObject(0);
                                int currentEpisodeCount = firstObject.getInt("currentEpisodeCount");
                                if (currentEpisodeCount != -1) {
                                    String eps = currentEpisodeCount + "/" + animeEpisodes;
                                    textView_animeEpisodes.setText(eps);
                                    runEpisodeReleaseCheck(currentEpisodeCount + 1);
                                }
                            } else {
                                episodeProgressBar.setVisibility(View.GONE);
                                episodeAiringInfo.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Couldn't get airing info. API error!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 500 && !triedAnimeNameSynonym) {
                                triedAnimeName = true;
                                triedAnimeNameSynonym = true;
                                getNextEpisodeAiring();
                            } else if (error.networkResponse.statusCode == 500 && !isTriedAnimeNativeName) {
                                isTriedAnimeNativeName = true;
                                getNextEpisodeAiring();
                            }
                            if (error.networkResponse.statusCode == 500 && isTriedAnimeNativeName) {
                                if (getActivity() != null) {
                                    episodeProgressBar.setVisibility(View.GONE);
                                    episodeAiringInfo.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Error getting airing info. API error!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        error.printStackTrace();
                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runEpisodeReleaseSavedCheck() {
        handlerNew = new Handler(Looper.getMainLooper());
        handlerNew.post(() -> {
            if (getActivity() != null) {
                if (episodeReleasing.getVisibility() == View.VISIBLE) {
                    episodeReleasing.setVisibility(View.GONE);
                    episodeDateTimer.setVisibility(View.GONE);
                }
                episodeAiringInfo.setText(getString(R.string.getting_airing_info));
                episodeAiringInfo.setVisibility(View.VISIBLE);
                episodeProgressBar.setVisibility(View.VISIBLE);
            }
        });
        String gotOldReleasedDate = animePreferences.getString("/anime/" + malId + "/Releasing", null);
        if (gotOldReleasedDate != null) {
            String episodeNo = gotOldReleasedDate.substring(gotOldReleasedDate.indexOf("ing") + 3, gotOldReleasedDate.indexOf("/&")).trim();
            String releaseDateTime = gotOldReleasedDate.substring(gotOldReleasedDate.indexOf("/&") + 2);
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm", Locale.ENGLISH);
            try {
                Date targetDateTime = format.parse(releaseDateTime);
                Date currentDateTime = new Date();
                if (currentDateTime.after(targetDateTime)) {
                    animePreferences.edit().remove("/anime/" + malId + "/Releasing").apply();
                    runEpisodeReleaseCheck(Integer.parseInt(episodeNo));
                } else {
                    getNextEpisodeAiring();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            getNextEpisodeAiring();
        }
    }

    private void runEpisodeReleaseCheck(int currentEpisodeCount) {

        handlerNew.post(() -> {
            String episodeReleasingNo = "Episode " + currentEpisodeCount + " of " + animeEpisodes + " airing on";
            episodeReleasing.setText(episodeReleasingNo);
            episodeProgressBar.setVisibility(View.GONE);
            episodeAiringInfo.setVisibility(View.GONE);
            episodeReleasing.setVisibility(View.VISIBLE);
            episodeReleasing.startAnimation(startAnimation);
            episodeDateTimer.setVisibility(View.VISIBLE);
            episodeDateTimer.startAnimation(startAnimation);
        });


        String day = istTimeTogether.substring(0, istTimeTogether.indexOf("at")).trim();
        day = day.substring(0, day.length() - 1);
        String time = istTimeTogether.substring(istTimeTogether.indexOf("at") + 2, istTimeTogether.indexOf("("));

        // Get the current date
        Calendar currentDate = Calendar.getInstance();
        int currentDay = currentDate.get(Calendar.DAY_OF_WEEK);
        int inputDayOfWeek = getCalendarDay(day);
        int daysToAdd = (inputDayOfWeek - currentDay + 7) % 7;
        currentDate.add(Calendar.DAY_OF_YEAR, daysToAdd);

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String formattedDate = formatter.format(currentDate.getTime());
        releaseDateTime = formattedDate + time;

        //saving the date and time
        if (!releaseDateTime.isEmpty()){
            animePreferences.edit().putString("/anime/" + malId + "/Releasing", currentEpisodeCount + "/&" + releaseDateTime).apply();
        }


        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm", Locale.ENGLISH);
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

    private void setDataToUI() {

        //setting for watch fragment
        animePreferences.edit().putString("myAnimeListID", malId).apply();
        animePreferences.edit().putString("animeType", animeType).apply();
        animePreferences.edit().putString("animeName", animeName).apply();

        //setting data to ui
        try {

            if (getActivity() != null) {
                Glide.with(getActivity())
                        .load(animeImage)
                        .transform(new BlurTransformation(20, 2))
                        .into(animatedImageBackground);

                Glide.with(getActivity()).load(animeImage).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }
                }).into(animeThumbnail);
            }


            if (animeName != null) {
                textView_animeName.setText(animeName);
                textView_animeNativeTitle.setText(animeName);
            }
            textView_animeName.startAnimation(startAnimation);
            textView_animeNativeTitle.startAnimation(startAnimation);

            if (malRating != null) {
                if (malRating.contains(".")) {
                    String malRating = this.malRating.substring(0, 3);
                    textView_animeRating.setText(malRating);
                }
                String malRating = this.malRating + "/10";
                textView_animeRating2.setText(malRating);
            }
            textView_animeRating.startAnimation(startAnimation);
            textView_animeRating2.startAnimation(startAnimation);

            if (!animeType.toLowerCase().contains("tv")) {
                if (animeType != null) {
                    textView_animeType.setText(animeType);
                }
                textView_animeRating2.startAnimation(startAnimation);
            }

            if (animeStatus != null) {
                releasing_anime.setText(animeStatus);
            }
            releasing_anime.startAnimation(startAnimation);

            if (animeAired != null) {
                textView_animeAired.setText(animeAired);
            }
            textView_animeAired.startAnimation(startAnimation);

            if (istTimeTogether != null) {
                textView_animeAiredON.setText(istTimeTogether);
            }
            textView_animeAiredON.startAnimation(startAnimation);

            if (animeDuration != null) {
                textView_animeDuration.setText(animeDuration);
            }
            textView_animeDuration.startAnimation(startAnimation);

            if (animeEpisodes != null) {
                textView_animeEpisodes.setText(animeEpisodes);
            }
            textView_animeEpisodes.startAnimation(startAnimation);

            if (animeDemographic != null) {
                textView_animeDemographic.setText(animeDemographic);
            }
            textView_animeDemographic.startAnimation(startAnimation);

            if (animeTheme != null) {
                textView_animeTheme.setText(animeTheme);
            }
            textView_animeTheme.startAnimation(startAnimation);

            if (animeAlsoKnownAs != null) {
                textView_animeTitleEnglish.setText(animeAlsoKnownAs);
            }
            textView_animeTitleEnglish.startAnimation(startAnimation);

            if (animePopularity != null) {
                textView_animePopularity.setText(animePopularity);
            }
            textView_animePopularity.startAnimation(startAnimation);

            if (animeRank != null) {
                textView_animeRanked.setText(animeRank);
            }
            textView_animeRanked.startAnimation(startAnimation);

            if (animeSource != null) {
                textView_animeSource.setText(animeSource);
            }
            textView_animeSource.startAnimation(startAnimation);

            if (animeStudio != null) {
                textView_animeStudio.setText(animeStudio);
            }
            textView_animeStudio.startAnimation(startAnimation);

            if (animeProducers != null) {
                textView_animeProducers.setText(animeProducers);
            }
            textView_animeProducers.startAnimation(startAnimation);

            if (animeContentRating != null) {
                textView_ContentRating.setText(animeContentRating);
            }
            textView_ContentRating.startAnimation(startAnimation);

            if (animeSynopsis != null) {
                if (animeSynopsis.contains("[Written by MAL Rewrite]")) {
                    animeSynopsis = animeSynopsis.replace("[Written by MAL Rewrite]", "").trim();
                }
                textViewExpanded_animeSynopsis.setText(animeSynopsis);
            }
            textViewExpanded_animeSynopsis.startAnimation(startAnimation);

            textView_detailsBy_text.setVisibility(View.VISIBLE);
            textView_detailsBy_text.startAnimation(startAnimation);

            if(animeYoutubeTrailerID != null && !animeYoutubeTrailerID.isEmpty()){
                fourth_container.setVisibility(View.VISIBLE);
                YouTubePlayerListener youTubePlayerListener = new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        super.onReady(youTubePlayer);
                        youTubePlayer.cueVideo(animeYoutubeTrailerID, 0);
                    }
                };
                youtube_player_view.initialize(youTubePlayerListener,true);
            }

            if (genresArrayList != null) {
                textView_animeGenres_text.setVisibility(View.VISIBLE);
                infoGenresAdapter = new InfoGenresAdapter(genresArrayList, getActivity(), "FromAnimeFragment");
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView_for_genres.setLayoutManager(linearLayoutManager);
                recyclerView_for_genres.setAdapter(infoGenresAdapter);
                recyclerView_for_genres.setVisibility(View.VISIBLE);
            } else {
                third_container.setVisibility(View.GONE);
            }

         /*   Random random = new Random();
            int randomNumber = random.nextInt(20);
            if (getActivity() != null && animePrequel != null && !animePrequel.isEmpty()) {
                prequel_card.setVisibility(View.VISIBLE);
                if (DataHolder.getTopAiringAnimeListModelArrayList() != null) {
                    Glide.with(getActivity())
                            .load(DataHolder.getTopAiringAnimeListModelArrayList().get(randomNumber).getAnimeThumbnail())
                            .transform(new BlurTransformation(8, 2))
                            .into(prequel_cardThumbnail);
                }
                prequel_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id = animePrequel.substring(animePrequel.indexOf("me/")+3);
                        id = id.substring(0, id.indexOf("/"));
                        Intent intent = new Intent(getActivity(), AnimeActivity.class);
                        intent.putExtra("malId", id);
                        startActivity(intent);
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }
            if (getActivity() != null && animeSequel != null && !animeSequel.isEmpty()) {
                sequel_card.setVisibility(View.VISIBLE);
                if (DataHolder.getTopAiringAnimeListModelArrayList() != null) {
                    Glide.with(getActivity())
                            .load(DataHolder.getTopAiringAnimeListModelArrayList().get(randomNumber).getAnimeThumbnail())
                            .transform(new BlurTransformation(8, 2))
                            .into(sequel_cardThumbnail);
                }
                sequel_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id = animeSequel.substring(animeSequel.indexOf("me/")+3);
                        id = id.substring(0, id.indexOf("/"));
                        Intent intent = new Intent(getActivity(), AnimeActivity.class);
                        intent.putExtra("malId", id);
                        startActivity(intent);
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }*/


            main_Progress.setVisibility(View.GONE);
            first_container.setVisibility(View.VISIBLE);
            second_container.setVisibility(View.VISIBLE);


        } catch (Exception e) {
            e.printStackTrace();
        }
        ;
    }

    private void timeConversion(String jstTime) {
        if (jstTime != null && jstTime.contains("at")) {
            String day = jstTime.substring(0, jstTime.indexOf("at")).trim();
            String istTime = "";
            jstTime = jstTime.substring(jstTime.indexOf("at") + 2, jstTime.indexOf("(")).trim();


            SimpleDateFormat jstFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            jstFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

            SimpleDateFormat istFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            istFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

            try {
                Date jstDate = jstFormat.parse(jstTime);
                if (jstDate != null) {
                    istTime = istFormat.format(jstDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            istTimeTogether = day + " at " + istTime + " (IST)";
        }
    }

    private static int getCalendarDay(String day) {
        switch (day.toLowerCase()) {
            case "sunday":
                return Calendar.SUNDAY;
            case "monday":
                return Calendar.MONDAY;
            case "tuesday":
                return Calendar.TUESDAY;
            case "wednesday":
                return Calendar.WEDNESDAY;
            case "thursday":
                return Calendar.THURSDAY;
            case "friday":
                return Calendar.FRIDAY;
            case "saturday":
                return Calendar.SATURDAY;
            default:
                throw new IllegalArgumentException("Invalid day: " + day);
        }
    }

    @Override
    public void onStop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isPaused = true;
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(youtube_player_view != null){
            youtube_player_view.release();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isPaused = true;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (isPaused) {
            runEpisodeReleaseSavedCheck();
        }
        super.onResume();
    }


}