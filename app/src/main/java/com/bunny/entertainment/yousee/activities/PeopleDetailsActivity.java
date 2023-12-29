package com.bunny.entertainment.yousee.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.adapters.ShowsAdapterFragment;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.utils.DataHolder;
import com.flaviofaria.kenburnsview.KenBurnsView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PeopleDetailsActivity extends AppCompatActivity {

    String peopleId, peopleUrl, showUrl;
    String showImage, showName, showEpisodes, showCountry, showRating;
    RecyclerView recyclerView_for_shows;
    KenBurnsView animatedImageBackground;
    ImageView dramaThumbnail;
    String firstName, familyName, nativeName, alsoKnownAs, nationality, gender, born, age, description, imageUrl;
    ProgressBar thumbnailProgress;
    TextView textView_peopleName, textView_peopleFamilyName, textView_peopleNativeName, textView_peopleAlsoKnowAs, textView_peopleNationality, textView_peopleGender,
            textView_peopleBornOn, textView_peopleAge, textView_peopleDescription;
    ArrayList<String> peopleWorkIdModelArrayList;
    ArrayList<DramaListModel> viewAllWorkArrayList;
    ArrayList<DramaListModel> workArrayList;
    ShowsAdapterFragment showsAdapterFragment;
    ProgressBar main_ProgressBar, recycler_ProgressBar;
    ScrollView main_ScrollView;
    Animation startAnimation;
    RelativeLayout view_all_workedOn;
    ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setNavigationBarColor(ContextCompat.getColor(PeopleDetailsActivity.this, R.color.mode_inverse));
        setContentView(R.layout.activity_people_details);


        recyclerView_for_shows = findViewById(R.id.recyclerView_for_shows);
        animatedImageBackground = findViewById(R.id.animatedImageBackground);
        dramaThumbnail = findViewById(R.id.dramaThumbnail);
        thumbnailProgress = findViewById(R.id.thumbnailProgress);
        textView_peopleName = findViewById(R.id.textView_peopleName);
        textView_peopleFamilyName = findViewById(R.id.textView_peopleFamilyName);
        textView_peopleNativeName = findViewById(R.id.textView_peopleNativeName);
        textView_peopleAlsoKnowAs = findViewById(R.id.textView_peopleAlsoKnowAs);
        textView_peopleNationality = findViewById(R.id.textView_peopleNationality);
        textView_peopleGender = findViewById(R.id.textView_peopleGender);
        textView_peopleBornOn = findViewById(R.id.textView_peopleBornOn);
        textView_peopleAge = findViewById(R.id.textView_peopleAge);
        textView_peopleDescription = findViewById(R.id.textView_peopleDescription);
        main_ProgressBar = findViewById(R.id.main_ProgressBar);
        main_ScrollView = findViewById(R.id.main_ScrollView);
        recycler_ProgressBar = findViewById(R.id.recycler_ProgressBar);
        view_all_workedOn = findViewById(R.id.view_all_workedOn);


        startAnimation = AnimationUtils.loadAnimation(PeopleDetailsActivity.this, R.anim.fadein);

        peopleId = getIntent().getStringExtra("castId");

        if (peopleId != null) {
            scrapPeopleDetails();
        }


    }

    private void scrapPeopleDetails() {

        peopleUrl = "https://mydramalist.com" + peopleId;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {
                Document doc = Jsoup.connect(peopleUrl).get();
                Elements boxDetails = doc.select("div.col-lg-4.col-md-4").select("div.box.clear.hidden-sm-down")
                        .select("ul.list.m-b-0").select(".list-item");

                for (Element listItem : boxDetails) {
                    String text = listItem.text();
                    if (text.contains("First Name:")) {
                        firstName = text.replace("First Name:", "").trim();
                    } else if (text.contains("Family Name:")) {
                        familyName = text.replace("Family Name:", "").trim();
                    } else if (text.contains("Native name:")) {
                        nativeName = text.replace("Native name:", "").trim();
                    } else if (text.contains("Also Known as:")) {
                        alsoKnownAs = text.replace("Also Known as:", "").trim();
                    } else if (text.contains("Nationality:")) {
                        nationality = text.replace("Nationality:", "").trim();
                    } else if (text.contains("Gender:")) {
                        gender = text.replace("Gender:", "").trim();
                    } else if (text.contains("Born:")) {
                        born = text.replace("Born:", "").trim();
                    } else if (text.contains("Age:")) {
                        age = text.replace("Age:", "").trim();
                    }
                }

                Elements boxImage = doc.select("div.col-lg-4.col-md-4").select("div.box.hidden-sm-down").select("div.box-body img.img-responsive");
                imageUrl = boxImage.attr("src");

                Element boxDetails1 = doc.selectFirst("div.col-sm-8.col-lg-12.col-md-12");

                if (boxDetails1 != null && !boxDetails1.ownText().isEmpty()) {
                    description = boxDetails1.ownText().trim();
                    if (description.contains("(Source:")) {
                        description = description.substring(0, description.indexOf("(Source:"));
                    }
                }


                peopleWorkIdModelArrayList = new ArrayList<>();

                Elements tables = doc.select("div.col-lg-8.col-md-8").select("div.box-body table.film-list");
                for (Element table : tables) {
                    Elements rows = table.select("tbody tr");

                    for (Element row : rows) {
                        String dramaId = row.select("td.title a.text-primary").attr("href");
                        peopleWorkIdModelArrayList.add(dramaId);
                    }
                }

                handler.post(this::showPeopleDetails);

                //Log.e("box", rows.outerHtml());

            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });


    }

    private void showPeopleDetails() {

        Glide.with(PeopleDetailsActivity.this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new BlurTransformation(20, 2))
                .into(animatedImageBackground);

        Glide.with(PeopleDetailsActivity.this).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
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

        if (firstName != null) {
            textView_peopleName.setText(firstName);
        }
        if (familyName != null) {
            textView_peopleFamilyName.setText(familyName);
        }
        if (nativeName != null) {
            textView_peopleNativeName.setText(nativeName);
        }
        if (alsoKnownAs != null) {
            textView_peopleAlsoKnowAs.setText(alsoKnownAs);
        }
        if (nationality != null) {
            textView_peopleNationality.setText(nationality);
        }
        if (gender != null) {
            textView_peopleGender.setText(gender);
        }
        if (born != null) {
            textView_peopleBornOn.setText(born);
        }
        if (age != null) {
            textView_peopleAge.setText(age);
        }
        if (description != null) {
            textView_peopleDescription.setText(description);
        }

        main_ProgressBar.setVisibility(View.GONE);
        main_ScrollView.setVisibility(View.VISIBLE);
        main_ScrollView.startAnimation(startAnimation);

        scrapWorkedOn();

        // setRecyclerItems();
    }

    private void scrapWorkedOn() {


         executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            try {

                int dataNumber = 0;
                workArrayList = new ArrayList<>();
                viewAllWorkArrayList = new ArrayList<>();
                for (int i = 0; i < Math.min(peopleWorkIdModelArrayList.size(), 6); i++) {

                    showUrl = "https://mydramalist.com" + peopleWorkIdModelArrayList.get(i);
                    try {

                        int showIndex = 0, showEpisodesIndex = 0, showCountryIndex = 0, showRatingIndex = 0;

                        Document doc = Jsoup.connect(showUrl).get();
                        Elements box = doc.select("div.col-sm-4");
                        showImage = box.select("img.img-responsive").attr("src"); //1

                        Elements box5 = doc.select("div.col-lg-4").select("div.box.clear.hidden-sm-down").select("div.box-body").select("ul.list").select("li");
                        ; //main
                        showIndex = findIndexWithText(box5, "Drama");
                        if (showIndex == -1) {
                            showIndex = findIndexWithText(box5, "TV Show");
                        }
                        if (showIndex == -1) {
                            showIndex = findIndexWithText(box5, "Special");
                        }
                        if (showIndex == -1) {
                            showIndex = findIndexWithText(box5, "Movie");
                        }
                        showEpisodesIndex = findIndexWithText(box5, "Episodes");
                        showCountryIndex = findIndexWithText(box5, "Country");
                        showRatingIndex = findIndexWithText(box5, "Score");

                        if (showIndex != -1) {
                            showName = box5.get(showIndex).text();
                            showName = showName.substring(showName.indexOf(":") + 1).trim();
                        }

                        if (showEpisodesIndex != -1) {
                            showEpisodes = box5.get(showEpisodesIndex).text(); //10
                            showEpisodes = showEpisodes.substring(showEpisodes.indexOf(":") + 1).trim();
                        }

                        if (showCountryIndex != -1) {
                            showCountry = box5.get(showCountryIndex).text(); //9
                            showCountry = showCountry.substring(showCountry.indexOf(":") + 1).trim();
                        }

                        if (showRatingIndex != -1) {
                            showRating = box5.get(showRatingIndex).text();//16
                            showRating = showRating.substring(showRating.indexOf(":") + 1, showRating.indexOf("(")).trim();
                            if (showRating.contains("N/A")) {
                                showRating = getString(R.string._0_0);
                            }
                        }
                        dataNumber++;
                        DramaListModel dramaListModel = new DramaListModel(showName, showCountry, showEpisodes, showRating, showImage, String.valueOf(peopleWorkIdModelArrayList.size()), peopleWorkIdModelArrayList.get(i), String.valueOf(dataNumber));

                        workArrayList.add(dramaListModel);
                        viewAllWorkArrayList.add(dramaListModel);

                    } catch (Exception e) {
                        Log.e("ExceptionInPeopleDetailsActivity", e.getMessage());
                    }
                }
                DataHolder.setViewAllWorkArrayList(viewAllWorkArrayList);
                DataHolder.setPeopleWorkIdModelArrayList(peopleWorkIdModelArrayList);
                handler.post(this::setShowsInRecycler);


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here

            });
        });


    }

    private void setShowsInRecycler() {

        showsAdapterFragment = new ShowsAdapterFragment(workArrayList, PeopleDetailsActivity.this);

        Animation animation = AnimationUtils.loadAnimation(PeopleDetailsActivity.this, R.anim.recyclerviewscroll_left_right); // Create your animation here
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        recyclerView_for_shows.setLayoutAnimation(controller);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PeopleDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_for_shows.setLayoutManager(linearLayoutManager);
        //snapHelper.attachToRecyclerView(recyclerView_for_top_shows);
        recyclerView_for_shows.setAdapter(showsAdapterFragment);
        recycler_ProgressBar.setVisibility(View.GONE);
        recyclerView_for_shows.setVisibility(View.VISIBLE );
        view_all_workedOn.setVisibility(View.VISIBLE);


        view_all_workedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PeopleDetailsActivity.this, ViewAllActivity.class);
                intent.putExtra("whereFrom", "fromShowPeopleDetailsActivity");
                intent.putExtra("name", familyName+" "+firstName);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}