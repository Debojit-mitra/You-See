package com.bunny.entertainment.yousee.activities;

import static com.bunny.entertainment.yousee.utils.Constants.MYANIMELIST_URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.AnimeSearchAdapterActivity;
import com.bunny.entertainment.yousee.adapters.ShowsSearchAdapterActivity;
import com.bunny.entertainment.yousee.adapters.ShowsSearchAdapterActivity2;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.models.ShowsListModel;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;
import com.bunny.entertainment.yousee.utils.Constants;
import com.bunny.entertainment.yousee.utils.GridSpacingItemDecoration;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView_for_search;
    ProgressBar progressBar_search;
    TextInputEditText edittext_search;
    ArrayList<DramaListModel> searchArrayList;
    ArrayList<ShowsListModel> searchArrayList2;
    ArrayList<AnimeListModel> searchArrayList3;
    ShowsSearchAdapterActivity searchShowAdapterActivity;
    ShowsSearchAdapterActivity2 showsSearchAdapterActivity2;
    AnimeSearchAdapterActivity animeSearchAdapterActivity;
    ExecutorService executor;
    String searchUrl, searchText= "", oldText, whereFrom;
    boolean isSearching = false;
    SwitchMaterial eighteenPlus_switch;
    SharedPreferences dramaPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        whereFrom = getIntent().getStringExtra("whereFrom");

        recyclerView_for_search = findViewById(R.id.recyclerView_for_search);
        progressBar_search = findViewById(R.id.progressBar_search);
        edittext_search = findViewById(R.id.edittext_search);
        eighteenPlus_switch = findViewById(R.id.eighteenPlus_switch);

        if(whereFrom != null && whereFrom.contains("AnimeFragment")){
            eighteenPlus_switch.setVisibility(View.VISIBLE);
        }

        dramaPreferences = getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);

        edittext_search.requestFocus();

        edittext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchText = String.valueOf(editable).trim();
                if (!isSearching && !searchText.isEmpty()) {
                    searchMethod(searchText);
                }
            }
        });

        edittext_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (oldText != null) {
                        if (!oldText.equals(searchText)) {
                            searchMethod(searchText);
                        }
                    }
                    // Hide the keyboard
                    edittext_search.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edittext_search.getWindowToken(), 0);
                    return true; // Return true to indicate that the event is handled
                }

                return false;
            }
        });

        String eigthteenPlusSwitch = dramaPreferences.getString("18+SwitchAnimeSearch", null);
        if(eigthteenPlusSwitch != null && eigthteenPlusSwitch.contains("ON")){
            eighteenPlus_switch.setChecked(true);
        }

        eighteenPlus_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(eighteenPlus_switch.isChecked()){
                    dramaPreferences.edit().putString("18+SwitchAnimeSearch", "ON").apply();
                    if (!isSearching && !searchText.isEmpty()) {
                        searchMethod(searchText);
                    }
                } else {
                    dramaPreferences.edit().putString("18+SwitchAnimeSearch", "OFF").apply();
                    if (!isSearching && !searchText.isEmpty()) {
                        searchMethod(searchText);
                    }
                }
            }
        });




    }

    private void searchMethod(String searchText) {

        if(whereFrom.contains("DramaFragment")){
            scrapShowsDramaFragment(searchText);
        } else if (whereFrom.contains("ShowsFragment")) {
            scrapShowsShowsFragment(searchText);
        } else if (whereFrom.contains("AnimeFragment") && searchText.length() >= 3) {
            scrapAnimeAnimeFragment(searchText);
        }


        recyclerView_for_search.setVisibility(View.GONE);
        progressBar_search.setVisibility(View.VISIBLE);
        isSearching = true;
        if(whereFrom.contains("AnimeFragment") && searchText.length() < 3){
            isSearching = false;
            progressBar_search.setVisibility(View.GONE);
        }


    }

    private void scrapAnimeAnimeFragment(String searchText) {
        boolean switchState = eighteenPlus_switch.isChecked();
        if(switchState){
            searchUrl = MYANIMELIST_URL + "/anime.php?q=" + searchText + "&cat=anime";
        } else {
            searchUrl = MYANIMELIST_URL + "/anime.php?cat=anime&q="+ searchText +"&type=0&score=0&status=0&p=0&r=0&sm=0&sd=0&sy=0&em=0&ed=0&ey=0&c%5B%5D=a&c%5B%5D=b&c%5B%5D=c&c%5B%5D=f&genre_ex%5B%5D=9&genre_ex%5B%5D=49&genre_ex%5B%5D=12";
        }


        searchArrayList3 = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {

                Document doc = Jsoup.connect(searchUrl).get();
                Elements boxAllMainData = doc.select("div.js-categories-seasonal");
                Elements animeSearchData = boxAllMainData.select("tr");
                //System.out.println(animeSearchData.outerHtml());

                int total = 0;
                for (Element eachAnime : animeSearchData) {
                    total++;
                    if(total == 1){
                        continue;
                    }
                    Elements imageInfo = eachAnime.select("div.picSurround");
                    Elements details = eachAnime.select("td.borderClass");

                    String image = imageInfo.select("a.hoverinfo_trigger").select("img").attr("data-src");
                    if (image.isEmpty()) {
                        image = imageInfo.select("a.hoverinfo_trigger").select("img").attr("src");
                    }
                    String data1 = image.substring(0, image.indexOf(".net/") + 4);
                    String data2 = image.substring(image.indexOf("/images"), image.indexOf("?s="));
                    image = data1 + data2;


                    String titleMain = details.select("div.title").select("a.hoverinfo_trigger").text();

                    String malID = details.select("div.title").select("a").attr("href");
                    int startIndex = malID.indexOf("anime/") + "anime/".length();
                    int endIndex = malID.indexOf("/", startIndex);
                    malID = malID.substring(startIndex, endIndex);



                    String rating = details.select("td.borderClass.ac.bgColor0[width=50]").text();
                    if(rating.isEmpty()){
                        rating = details.select("td.borderClass.ac.bgColor1[width=50]").text();
                    }
                    if (rating.contains(".")) {
                        rating = rating.substring(0, 3);
                    }


                    String type = details.select("td.borderClass.ac.bgColor0[width=45]").text();
                    String episodes = details.select("td.borderClass.ac.bgColor0[width=40]").text();
                    if(type.isEmpty() || episodes.isEmpty()){
                         type = details.select("td.borderClass.ac.bgColor1[width=45]").text();
                         episodes = details.select("td.borderClass.ac.bgColor1[width=40]").text();
                    }
                    episodes = type + " (" + episodes + "eps)";
                    System.out.println(titleMain+" : "+episodes+" : "+rating);


                    AnimeListModel animeListModel = new AnimeListModel(titleMain, malID, rating, image, episodes, null, total);
                    //topAiringUpcomingSeasonModelArrayList.add(animeListModel);
                    searchArrayList3.add(animeListModel);
                }
                handler.post(this::showSearchedAnimeFragment);

            } catch (SocketTimeoutException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchActivity.this, "MyAnimeList Error! wait until website is fixed.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
            });
        });
    }

    private void showSearchedAnimeFragment() {

        animeSearchAdapterActivity = new AnimeSearchAdapterActivity(searchArrayList3, SearchActivity.this);

        Animation animation = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.recyclerviewscroll_left_right_fast); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_search.setLayoutAnimation(controller);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        int itemWidth = getResources().getDimensionPixelSize(R.dimen.item_widthSearchActivity); // Define your item width
        //CenterItemDecoration itemDecoration = new CenterItemDecoration(this, 0); // Adjust margin as needed
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView_for_search.setLayoutManager(gridLayoutManager);
        // recyclerView_for_search.addItemDecoration(new GridSpacingItemDecoration(SearchActivity.this, itemWidth, 3));
        //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
        progressBar_search.setVisibility(View.GONE);
        recyclerView_for_search.setAdapter(animeSearchAdapterActivity);
        recyclerView_for_search.setVisibility(View.VISIBLE);
        isSearching = false;
        oldText = searchText;
    }

    private void scrapShowsShowsFragment(String searchText) {

        String searchUrl = "https://api.themoviedb.org/3/search/multi?query="+searchText+"&include_adult=false&language=en-US&page=1";
        searchArrayList2 = new ArrayList<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here

            try {
                RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);

                Map<String, String> headers = new HashMap<>();
                headers.put("accept", "application/json");
                headers.put("Authorization", Constants.TMDB_ACCESS_TOKEN);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, searchUrl, new Response.Listener<String>() {
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
                                String imageUrl = "";
                                if(jsonObjectContent.has("poster_path")){
                                    imageUrl = jsonObjectContent.getString("poster_path");
                                    imageUrl = "https://image.tmdb.org/t/p/w200" + imageUrl;
                                }
                                String mediaType = "";
                                if(jsonObjectContent.has("media_type")){
                                    mediaType = jsonObjectContent.getString("media_type");
                                }

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
                                searchArrayList2.add(showsListModel);

                                //Log.e("dramaListModel", dramaListModel.getDramaName() + "   " + dramaListModel.getDramaRating() + "   " + dramaListModel.getDramaId());
                            }

                            handler.post(SearchActivity.this::showSearchedShowsFragment);

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


    }

    private void showSearchedShowsFragment() {
        showsSearchAdapterActivity2 = new ShowsSearchAdapterActivity2(searchArrayList2, SearchActivity.this);

        Animation animation = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.recyclerviewscroll_left_right_fast); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_search.setLayoutAnimation(controller);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        int itemWidth = getResources().getDimensionPixelSize(R.dimen.item_widthSearchActivity); // Define your item width
        //CenterItemDecoration itemDecoration = new CenterItemDecoration(this, 0); // Adjust margin as needed
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView_for_search.setLayoutManager(gridLayoutManager);
        // recyclerView_for_search.addItemDecoration(new GridSpacingItemDecoration(SearchActivity.this, itemWidth, 3));
        //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
        progressBar_search.setVisibility(View.GONE);
        recyclerView_for_search.setAdapter(showsSearchAdapterActivity2);
        recyclerView_for_search.setVisibility(View.VISIBLE);
        isSearching = false;
        oldText = searchText;
    }

    private void scrapShowsDramaFragment(String searchText) {

        //String searchUrl = "https://mydramalist.com/search?q=" + searchText + "&adv=titles&ty=68,83,86&so=relevance"; //for searching drama
        String searchUrl = "https://mydramalist.com/search?q=" + searchText + "&adv=titles&ty=68,77,83,86&re=1890,2023&so=relevance"; //for all

        searchArrayList = new ArrayList<>();
        //searchArrayList.clear();

        executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {
                Document doc = Jsoup.connect(searchUrl).get();
                Elements box = doc.select("div.m-t");
                Elements box1 = doc.select("div.col-lg-8");
                String totalResults = box1.select("p.m-b-sm").text();

                searchArrayList.clear();
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

                    } catch (Exception exception) {
                        Log.e("ExceptionInSearch", exception.getMessage());
                    }

                    dataNumber++;
                    Log.e("drama", dramaName+"   "+totalEpisodes);
                    DramaListModel dramaListModel = new DramaListModel(dramaName, dramaCountry, totalEpisodes, dramaRating, thumbImage, totalResults, myDramaListDramaId, String.valueOf(dataNumber));
                    searchArrayList.add(dramaListModel);


                }
                //  Log.e("e", String.valueOf(topAiringArrayList.size()));

                //UI Thread work here
                handler.post(this::showSearchedShows);


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });

    }

    private void showSearchedShows() {

        searchShowAdapterActivity = new ShowsSearchAdapterActivity(searchArrayList, SearchActivity.this);

        Animation animation = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.recyclerviewscroll_left_right_fast); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_search.setLayoutAnimation(controller);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        int itemWidth = getResources().getDimensionPixelSize(R.dimen.item_widthSearchActivity); // Define your item width
        //CenterItemDecoration itemDecoration = new CenterItemDecoration(this, 0); // Adjust margin as needed
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView_for_search.setLayoutManager(gridLayoutManager);
       // recyclerView_for_search.addItemDecoration(new GridSpacingItemDecoration(SearchActivity.this, itemWidth, 3));
        //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
        progressBar_search.setVisibility(View.GONE);
        recyclerView_for_search.setAdapter(searchShowAdapterActivity);
        recyclerView_for_search.setVisibility(View.VISIBLE);
        isSearching = false;
        oldText = searchText;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.nothing, R.anim.fadeout);
    }
}