package com.bunny.entertainment.yousee.activities;

import static com.bunny.entertainment.yousee.utils.Constants.MYANIMELIST_URL;
import static com.bunny.entertainment.yousee.utils.Constants.MY_DRAMALIST_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.AnimeCalendarViewPagerAdapter;
import com.bunny.entertainment.yousee.adapters.AnimeGenresViewPagerAdapter;
import com.bunny.entertainment.yousee.fragments.AnimeCalendarFragment;
import com.bunny.entertainment.yousee.models.anime.AnimeAllGenresModel;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalenderActivity extends AppCompatActivity implements AnimeCalendarFragment.ProgressBarListener {

    ProgressBar progressBar_calender;
    String scheduleLinkAnime = MYANIMELIST_URL + "/anime/season/schedule";
    String scheduleLinkDrama = MY_DRAMALIST_URL + "/episode-calendar";
    ArrayList<AnimeListModel> mondayArrayList;
    ArrayList<AnimeListModel> tuesdayArrayList;
    ArrayList<AnimeListModel> wednesdayArrayList;
    ArrayList<AnimeListModel> thursdayArrayList;
    ArrayList<AnimeListModel> fridayArrayList;
    ArrayList<AnimeListModel> saturdayArrayList;
    ArrayList<AnimeListModel> sundayArrayList;
    boolean myAnimeListLoadError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        String whereFrom = getIntent().getStringExtra("whereFrom");
        progressBar_calender = findViewById(R.id.progressBar_calender);

        mondayArrayList = new ArrayList<>();
        tuesdayArrayList = new ArrayList<>();
        wednesdayArrayList = new ArrayList<>();
        thursdayArrayList = new ArrayList<>();
        fridayArrayList = new ArrayList<>();
        saturdayArrayList = new ArrayList<>();
        sundayArrayList = new ArrayList<>();

        if (whereFrom != null) {
            if (whereFrom.contains("DramaFragment")) {
               // scrapCalenderDrama();
            } else if (whereFrom.contains("AnimeFragment")) {
                scrapCalenderAnime();
            }
        }

    }

    private void scrapCalenderDrama() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {

                Document doc = Jsoup.connect(scheduleLinkDrama).get();
                Elements calenderDivs = doc.select("div.js-categories-seasonal div.seasonal-anime-list");

                if (!calenderDivs.isEmpty()) {
                    scrapALL(calenderDivs, "monday", 0);
                    scrapALL(calenderDivs, "tuesday", 1);
                    scrapALL(calenderDivs, "wednesday", 2);
                    scrapALL(calenderDivs, "thursday", 3);
                    scrapALL(calenderDivs, "friday", 4);
                    scrapALL(calenderDivs, "saturday", 5);
                    scrapALL(calenderDivs, "sunday", 6);

                    //UI Thread work here
                    handler.post(this::showCalendarData);

                } else {
                    Toast.makeText(CalenderActivity.this, "MyAnimeList Error! Try refreshing or maybe MAL has changed stuff.", Toast.LENGTH_LONG).show();
                }

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (!myAnimeListLoadError) {
                            Toast.makeText(CalenderActivity.this, "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                            myAnimeListLoadError = true;
                        }
                       /* if(swipe_refresh_layout.isRefreshing()){
                            swipe_refresh_layout.setRefreshing(false);
                        }*/
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void scrapCalenderAnime() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {

                Document doc = Jsoup.connect(scheduleLinkAnime).get();
                Elements calenderDivs = doc.select("div.js-categories-seasonal div.seasonal-anime-list");

                if (!calenderDivs.isEmpty()) {
                    scrapALL(calenderDivs, "monday", 0);
                    scrapALL(calenderDivs, "tuesday", 1);
                    scrapALL(calenderDivs, "wednesday", 2);
                    scrapALL(calenderDivs, "thursday", 3);
                    scrapALL(calenderDivs, "friday", 4);
                    scrapALL(calenderDivs, "saturday", 5);
                    scrapALL(calenderDivs, "sunday", 6);

                    //UI Thread work here
                    handler.post(this::showCalendarData);

                } else {
                    Toast.makeText(CalenderActivity.this, "MyAnimeList Error! Try refreshing or maybe MAL has changed stuff.", Toast.LENGTH_LONG).show();
                }

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (!myAnimeListLoadError) {
                            Toast.makeText(CalenderActivity.this, "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
                            myAnimeListLoadError = true;
                        }
                       /* if(swipe_refresh_layout.isRefreshing()){
                            swipe_refresh_layout.setRefreshing(false);
                        }*/
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        });


    }

    private void showCalendarData() {

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);

        ArrayList<ArrayList<AnimeListModel>> listOfLists = new ArrayList<>();
        listOfLists.add(mondayArrayList);
        listOfLists.add(tuesdayArrayList);
        listOfLists.add(wednesdayArrayList);
        listOfLists.add(thursdayArrayList);
        listOfLists.add(fridayArrayList);
        listOfLists.add(saturdayArrayList);
        listOfLists.add(sundayArrayList);

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        List<String> dayTitles = Arrays.asList(
                getString(R.string.monday),
                getString(R.string.tuesday), getString(R.string.wednesday),
                getString(R.string.thursday), getString(R.string.friday),
                getString(R.string.saturday), getString(R.string.sunday)
        );

        ArrayList<ArrayList<AnimeListModel>> reorderedList = new ArrayList<>();
        List<String> reorderedTitles = new ArrayList<>();

        // Reorder lists and titles based on the current day
        for (int i = currentDay - 2; i < listOfLists.size(); i++) {
            if (i<0){ //because when sunday come i becomes -1
                i = 0;
            }
            reorderedList.add(listOfLists.get(i));
            reorderedTitles.add(dayTitles.get(i));
        }

        for (int i = 0; i < currentDay - 2; i++) {
            reorderedList.add(listOfLists.get(i));
            reorderedTitles.add(dayTitles.get(i));
        }

        AnimeCalendarViewPagerAdapter adapter = new AnimeCalendarViewPagerAdapter(this, reorderedList);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(reorderedTitles.get(position));
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        }).attach();

        viewPager2.setOffscreenPageLimit(7);
        viewPager2.setCurrentItem(0);

    }

    private void scrapALL(Elements calenderDivs, String day, int i) {
        int total = 0;
        Element dayDiv = calenderDivs.get(i);
        Elements dayDivData = dayDiv.select("div.js-anime-category-producer");
        for (Element element : dayDivData) {
            total++;
            Elements titleElements = element.select("div.title");
            Elements infoElement = element.select("div.prodsrc");
            String titleMain = titleElements.select("h2.h2_anime_title a.link-title").text();


            String malID = titleElements.select("div.title-text").select("h2.h2_anime_title").select("a").attr("href");
            int startIndex = malID.indexOf("anime/") + "anime/".length();
            int endIndex = malID.indexOf("/", startIndex);
            malID = malID.substring(startIndex, endIndex);

            String rating = titleElements.select("span.js-score").text();
            if (rating.contains(".")) {
                rating = rating.substring(0, 3);
            }

            Elements boxGenres = element.select("div.genres-inner");
            ArrayList<String> genresList = new ArrayList<>();
            for (Element genreElement : boxGenres) {
                String genreText = genreElement.text();
                genresList.add(genreText);
            }

            String image = element.select("div.image").select("a img").attr("data-src");
            if (image.isEmpty()) {
                image = element.select("div.image").select("a img").attr("src");
            }
            //System.out.println(image);

            Elements episodeSpans = infoElement.select("span.item");
            String episodes = "";
            for (Element span : episodeSpans) {
                String text = span.text();
                if (text.contains("eps") || text.contains("ep")) {
                    // Extract the episode count
                    episodes = text.split(" ")[0];
                }
            }

            AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, genresList, total);
            if (day.contains("monday")){
                mondayArrayList.add(animeListModel);
            } else if (day.contains("tuesday")) {
                tuesdayArrayList.add(animeListModel);
            } else if (day.contains("wednesday")) {
                wednesdayArrayList.add(animeListModel);
            } else if (day.contains("thursday")) {
                thursdayArrayList.add(animeListModel);
            } else if (day.contains("friday")) {
                fridayArrayList.add(animeListModel);
            } else if (day.contains("saturday")) {
                saturdayArrayList.add(animeListModel);
            } else if (day.contains("sunday")) {
                sundayArrayList.add(animeListModel);
            }

            //System.out.println();

        }

    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            progressBar_calender.setVisibility(View.VISIBLE);
        } else {
            progressBar_calender.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}