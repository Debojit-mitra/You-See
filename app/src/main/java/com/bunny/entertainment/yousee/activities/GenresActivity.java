package com.bunny.entertainment.yousee.activities;

import static com.bunny.entertainment.yousee.utils.Constants.MYANIMELIST_URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.AnimeGenresViewPagerAdapter;
import com.bunny.entertainment.yousee.adapters.DramaGenresViewPagerAdapter;
import com.bunny.entertainment.yousee.fragments.AnimeGenresFragment;
import com.bunny.entertainment.yousee.fragments.DramaGenresFragment;
import com.bunny.entertainment.yousee.models.GenresModel;
import com.bunny.entertainment.yousee.models.anime.AnimeAllGenresModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenresActivity extends AppCompatActivity implements AnimeGenresFragment.ProgressBarListener, DramaGenresFragment.ProgressBarListener {

    ArrayList<GenresModel> genresModelArrayList;
    boolean genresNpointLinkWorking = true;
    String genresJsonLink;
    String allGenresLink = MYANIMELIST_URL + "/anime.php";
    boolean myAnimeListLoadError;
    ArrayList<AnimeAllGenresModel> genresList;
    ArrayList<AnimeAllGenresModel> explicitGenresList;
    ArrayList<AnimeAllGenresModel> themesList;
    ArrayList<AnimeAllGenresModel> demographicsList;
    ArrayList<AnimeAllGenresModel> seasonWiseList;
    ProgressBar progressBar_genres;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        try {
            String whereFrom = getIntent().getStringExtra("whereFrom");
            progressBar_genres = findViewById(R.id.progressBar_genres);

            if (whereFrom != null) {
                if (whereFrom.contains("DramaFragment")) {
                    LoadGenresDrama();
                } else if (whereFrom.contains("AnimeFragment")) {
                    scrapGenresAnime();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void scrapGenresAnime() {

        genresList = new ArrayList<>();
        explicitGenresList = new ArrayList<>();
        themesList = new ArrayList<>();
        demographicsList = new ArrayList<>();
        seasonWiseList = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {

                Document doc = Jsoup.connect(allGenresLink).get();
                Elements genreDivs = doc.select(".anime-manga-search .genre-link");
                if (!genreDivs.isEmpty()) {

                    Element firstGenresDiv = genreDivs.get(0);
                    Elements genreLinks = firstGenresDiv.select(".genre-name-link");
                    for (Element element : genreLinks) {
                        String name = element.text();
                        int numberOfAnime = extractTextAndNumber(name);
                        name = name.replaceAll("\\((\\d+(,\\d+)*)\\)", "\n($1)");
                        String link = element.attr("href");

                        AnimeAllGenresModel animeAllGenresModel = new AnimeAllGenresModel(name, link, numberOfAnime);
                        genresList.add(animeAllGenresModel);
                    }

                    Element secondExplicitGenresDiv = genreDivs.get(1);
                    Elements explicitGenresLinks = secondExplicitGenresDiv.select(".genre-name-link");
                    for (Element element : explicitGenresLinks) {
                        String name = element.text();
                        int numberOfAnime = extractTextAndNumber(name);
                        name = name.replaceAll("\\((\\d+(,\\d+)*)\\)", "\n($1)");
                        String link = element.attr("href");

                        AnimeAllGenresModel animeAllGenresModel = new AnimeAllGenresModel(name, link, numberOfAnime);
                        explicitGenresList.add(animeAllGenresModel);
                    }

                    Element thirdThemesDiv = genreDivs.get(2);
                    Elements themesLinks = thirdThemesDiv.select(".genre-name-link");
                    for (Element element : themesLinks) {
                        String name = element.text();
                        int numberOfAnime = extractTextAndNumber(name);
                        name = name.replaceAll("\\((\\d+(,\\d+)*)\\)", "\n($1)");
                        String link = element.attr("href");


                        AnimeAllGenresModel animeAllGenresModel = new AnimeAllGenresModel(name, link, numberOfAnime);
                        themesList.add(animeAllGenresModel);
                    }

                    Element fourthThemesDiv = genreDivs.get(3);
                    Elements demographicsLinks = fourthThemesDiv.select(".genre-name-link");
                    for (Element element : demographicsLinks) {
                        String name = element.text();
                        int numberOfAnime = extractTextAndNumber(name);
                        name = name.replaceAll("\\((\\d+(,\\d+)*)\\)", "\n($1)");
                        String link = element.attr("href");


                        AnimeAllGenresModel animeAllGenresModel = new AnimeAllGenresModel(name, link, numberOfAnime);
                        demographicsList.add(animeAllGenresModel);
                    }

                    Element fifthSeasonWiseDiv = genreDivs.get(6);
                    Elements seasonWiseLinks = fifthSeasonWiseDiv.select(".genre-name-link");
                    for (Element element : seasonWiseLinks) {
                        String name = element.text();
                        int numberOfAnime = extractTextAndNumber(name);
                        name = name.replaceAll("\\((\\d+(,\\d+)*)\\)", "\n($1)");
                        String link = element.attr("href");


                        AnimeAllGenresModel animeAllGenresModel = new AnimeAllGenresModel(name, link, numberOfAnime);
                        seasonWiseList.add(animeAllGenresModel);
                    }


                    handler.post(this::LoadGenresAnime);

                } else {
                    Toast.makeText(GenresActivity.this, "MyAnimeList Error! Try refreshing or maybe MAL has changed stuff.", Toast.LENGTH_LONG).show();
                }

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (!myAnimeListLoadError) {
                            Toast.makeText(GenresActivity.this, "MyAnimeList Error! Try refreshing or wait until website is fixed.", Toast.LENGTH_LONG).show();
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

            handler.post(() -> {
                //UI Thread work here

            });
        });

    }

    private void LoadGenresAnime() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ArrayList<ArrayList<AnimeAllGenresModel>> listOfLists = new ArrayList<>();
        listOfLists.add(genresList);
        listOfLists.add(explicitGenresList);
        listOfLists.add(themesList);
        listOfLists.add(demographicsList);
        listOfLists.add(seasonWiseList);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        AnimeGenresViewPagerAdapter adapter = new AnimeGenresViewPagerAdapter(this, listOfLists);
        viewPager2.setAdapter(adapter);
        List<String> tabTitles = Arrays.asList(getString(R.string.genres), getString(R.string.explicit_genres),
                getString(R.string.based_on_theme), getString(R.string.based_on_demographics), getString(R.string.seasons));
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(tabTitles.get(position));
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        }).attach();
        viewPager2.setOffscreenPageLimit(5);
        viewPager2.setCurrentItem(0);
    }

    private void LoadGenresDrama() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                RequestQueue queue = Volley.newRequestQueue(GenresActivity.this);

                if (genresNpointLinkWorking) {
                    genresJsonLink = "https://api.npoint.io/ca2d69822c6fe6399ac6";
                } else {
                    genresJsonLink = "https://gist.githubusercontent.com/Debojit-mitra/29c695540cd831fc189bbca30a537be9/raw/YouSeeGenres.json";
                }


                StringRequest stringRequest = new StringRequest(Request.Method.GET, genresJsonLink, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        genresModelArrayList = new ArrayList<>();

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String genreName = jsonObject.getString("Genre");
                                String genreUrl = jsonObject.getString("URL");

                                GenresModel genresModel = new GenresModel(genreName, genreUrl);
                                genresModelArrayList.add(genresModel);
                            }

                            loadGenresData();

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GenresActivity.this, "Server Error! " + error.networkResponse, Toast.LENGTH_LONG).show();
                        genresNpointLinkWorking = false;
                        LoadGenresDrama();
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

    private void loadGenresData() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {

          /*  genresAdapter = new GenresAdapter(genresModelArrayList, GenresActivity.this);

            Animation animation = AnimationUtils.loadAnimation(GenresActivity.this, R.anim.recyclerviewscroll_top_bottom_fast); // Create your animation here
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            recyclerView_for_genres.setLayoutAnimation(controller);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(GenresActivity.this, 2, GridLayoutManager.VERTICAL, false);
            recyclerView_for_genres.setLayoutManager(gridLayoutManager);
            progressBar_genres.setVisibility(View.GONE);
            //linear1.setVisibility(View.VISIBLE);

            recyclerView_for_genres.setAdapter(genresAdapter);
            recyclerView_for_genres.setVisibility(View.VISIBLE);*/

            TabLayout tabLayout = findViewById(R.id.tabLayout);
            ArrayList<ArrayList<GenresModel>> listOfLists = new ArrayList<>();
            listOfLists.add(genresModelArrayList);

            ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
            DramaGenresViewPagerAdapter adapter = new DramaGenresViewPagerAdapter(this, listOfLists);
            viewPager2.setAdapter(adapter);
            new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
                tab.setText(getString(R.string.genres));
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            }).attach();


        });
    }

    // Method to extract text and number from the input string
    private int extractTextAndNumber(String input) {
        String inputWithoutCommas = input.replaceAll(",", "");
        Pattern pattern = Pattern.compile("\\(\\d+\\)");
        Matcher matcher = pattern.matcher(inputWithoutCommas);

        if (matcher.find()) {
            return Integer.parseInt(Objects.requireNonNull(matcher.group(0)).replaceAll("\\D", ""));
        } else {
            return -1;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            progressBar_genres.setVisibility(View.VISIBLE);
        } else {
            progressBar_genres.setVisibility(View.GONE);
        }
    }
}

