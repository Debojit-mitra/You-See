package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.models.DramaMovieEpisodeSecondModel;
import com.bunny.entertainment.yousee.player.PlayerActivity;
import com.bunny.entertainment.yousee.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ShowsEpisodesAdapter2 extends RecyclerView.Adapter<ShowsEpisodesAdapter2.Holder>{

    ArrayList<DramaMovieEpisodeSecondModel> showMovieEpisodeSecondModelArrayList;
    Context context;
    private static final int REQUEST_WRITE_SETTINGS = 111;
    SharedPreferences settingsPreferences, dramaPreferences;
    String allowSystemWriteSettingsAsk;
    String showName, episodeLink, subtitleLink, showId, showIdMyAPI, releaseDate, showMediaType;
    boolean gotSubtitle = false;
    ProgressBar progressBar_Main;
    BottomSheetDialog bottomSheetDialog;
    Boolean isCanceled = false, fromContinueLayoutBtn = false;
    private ShowsEpisodesAdapter2.Holder holder;
    int actualPosition;


    public ShowsEpisodesAdapter2() {
    }

    public ShowsEpisodesAdapter2(ArrayList<DramaMovieEpisodeSecondModel> showMovieEpisodeSecondModelArrayList, Context context, String showName, String showId, String showIdMyAPI, String showMediaType) {
        this.showMovieEpisodeSecondModelArrayList = showMovieEpisodeSecondModelArrayList;
        this.context = context;
        this.showIdMyAPI = showIdMyAPI;
        this.showId = showId;
        this.showName = showName;
        this.showMediaType = showMediaType;
    }

    @NonNull
    @Override
    public ShowsEpisodesAdapter2.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_episode_layout, parent, false);
        if (context != null) {
            settingsPreferences = context.getSharedPreferences("settingsPreferences", Context.MODE_PRIVATE);
            dramaPreferences = context.getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
            allowSystemWriteSettingsAsk = settingsPreferences.getString("allowSystemWriteSettings", "true");
        }
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowsEpisodesAdapter2.Holder holder, int position) {
        this.holder = holder;

        actualPosition = holder.getAbsoluteAdapterPosition();

        holder.card_root_layout.startAnimation(AnimationUtils.loadAnimation(holder.card_root_layout.getContext(), R.anim.recyclerviewscroll_top_bottom));


        Glide.with(context).load(showMovieEpisodeSecondModelArrayList.get(actualPosition).getEpisodeImage()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.image_not_available)
                .into(holder.episodeThumbnail);

        Glide.with(context)
                .load(showMovieEpisodeSecondModelArrayList.get(actualPosition).getEpisodeImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new BlurTransformation(20, 2))
                .into(holder.ImageBackground);

        holder.episodeName.setText(showMovieEpisodeSecondModelArrayList.get(actualPosition).getEpisodeName());
        releaseDate = showMovieEpisodeSecondModelArrayList.get(actualPosition).getEpisodeReleaseDate();
        if (releaseDate != null && checkString(releaseDate)) {
            releaseDate = "Released on: " + releaseDate;
        }
        holder.episodeReleaseDate.setText(releaseDate);
        holder.episodeNoSeasonNo.setText(showMovieEpisodeSecondModelArrayList.get(actualPosition).getSeasonNoEpisodeNo());
        holder.totalNumber.setText(showMovieEpisodeSecondModelArrayList.get(actualPosition).getTotalEps());

        String getProgressIfAny = "";

        if(showMovieEpisodeSecondModelArrayList.get(actualPosition).getTotalEps() != null){
        //    System.out.println("here2122   "+showMovieEpisodeSecondModelArrayList.get(actualPosition).getTotalEps());
            getProgressIfAny = dramaPreferences.getString(showId+showMovieEpisodeSecondModelArrayList.get(actualPosition).getTotalEps(), null);
        }

        if (getProgressIfAny != null) {
            Log.e("heregetProgressnull", "yes");
            String seenPosition = getProgressIfAny.substring(0, getProgressIfAny.indexOf("/"));
            String totalDuration = getProgressIfAny.substring(getProgressIfAny.indexOf("/") + 1);
            float pro = Float.parseFloat(seenPosition) / Float.parseFloat(totalDuration);
            int progress = (int) (pro * 100);
            holder.progressBar_Continue_watching.setProgress(progress);
            holder.progressBar_Continue_watching.setVisibility(View.VISIBLE);
        }

        holder.card_root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (allowSystemWriteSettingsAsk.equals("true")) {
                    if (!hasWriteSettingsPermission()) {
                        //dialog box for write settings permission
                        AlertDialog.Builder writeSetting = new AlertDialog.Builder(context, R.style.RoundedCornersDialog);
                        View viewLogout = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
                        Button ok_button = viewLogout.findViewById(R.id.ok_button);
                        Button cancel_button = viewLogout.findViewById(R.id.cancel_button);
                        cancel_button.setVisibility(View.VISIBLE);
                        cancel_button.setText(R.string.refuse);
                        ok_button.setVisibility(View.VISIBLE);

                        TextView custom_textview = viewLogout.findViewById(R.id.custom_textview);
                        String custom_text = "To allow app to control brightness and volume within the app, write permission is required! If you dont want to allow now, you can also provide the permission later from settings.";
                        custom_textview.setText(custom_text);
                        custom_textview.setVisibility(View.VISIBLE);

                        writeSetting.setView(viewLogout);
                        AlertDialog alertDialog = writeSetting.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        //ok_button.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                        ok_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                settingsPreferences.edit().putString("allowSystemWriteSettings", "false").apply();
                                if (context != null) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                                    ((Activity) context).startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
                                }
                            }
                        });
                        cancel_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                settingsPreferences.edit().putString("allowSystemWriteSettings", "false").apply();
                            }
                        });
                        alertDialog.getWindow().setWindowAnimations(R.style.RoundedCornersDialog);
                        alertDialog.show();
                    } else {
                        settingsPreferences.edit().remove("allowSystemWriteSettings").apply();
                        settingsPreferences.edit().putString("allowSystemWriteSettings", "false").apply();
                    }

                } else if (allowSystemWriteSettingsAsk.equals("false")) {
                    Log.e("actual position", String.valueOf(holder.getAbsoluteAdapterPosition()));
                    bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
                    View bsView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_server_select_dialog, view.findViewById(R.id.main_bottomSheet_layout));
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
                    getEpisodeLink(view, holder);
                }


            }
        });

    }

    private void getEpisodeLink(View view, Holder holder) {

        if(!fromContinueLayoutBtn) {
            actualPosition = holder.getAbsoluteAdapterPosition();
        }


        if (context != null) {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                //Background work here

                String mainUrl = Constants.MY_CONSUMET_API + "meta/tmdb/watch/"+showMovieEpisodeSecondModelArrayList.get(actualPosition).getEpisodeId()+"?id="+showIdMyAPI;

                try {
                    RequestQueue queue = Volley.newRequestQueue(context);
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
                                    Toast.makeText(context, "No Link Found!", Toast.LENGTH_SHORT).show();
                                }

                                //progressBar_Main.setVisibility(View.GONE);
                                if(!isCanceled){
                                    bottomSheetDialog.dismiss();
                                    Intent intent = new Intent(context, PlayerActivity.class);
                                    intent.putExtra("whereFrom", "ShowsFragment");
                                    intent.putExtra("videoLink", episodeLink);
                                    intent.putExtra("showMediaType", showMediaType);
                                    Log.e("showMediaType", showMediaType);
                                    intent.putExtra("subtitleLink", subtitleLink);
                                    intent.putExtra("dramaNameAndEpisodeNo", showName+" - Episode "+showMovieEpisodeSecondModelArrayList.get(actualPosition).getSeasonNoEpisodeNo());
                                    intent.putExtra("dramaIdMyDramaList", showId);
                                    intent.putExtra("realEpisodeNo", showMovieEpisodeSecondModelArrayList.get(actualPosition).getTotalEps());
                                    context.startActivity(intent);



                                  /*  for (int i = 0; i <= 9; i++){
                                        System.out.println("Adapter "+showMovieEpisodeSecondModelArrayList.get(i).getTotalEps());
                                    }*/

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
                            try {
                                if (error.networkResponse != null && error.networkResponse.statusCode == 500){
                                    bottomSheetDialog.dismiss();
                                    Toast.makeText(context, "Maybe Server error! No sure\uD83D\uDE05", Toast.LENGTH_SHORT).show();
                                } else {
                                    //progressBar_Main.setVisibility(View.GONE);
                                    bottomSheetDialog.dismiss();
                                    Toast.makeText(context, "Server Not Available!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e){
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


    @Override
    public int getItemCount() {
        return showMovieEpisodeSecondModelArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        ImageView episodeThumbnail, ImageBackground;
        TextView episodeName, episodeReleaseDate, totalNumber, episodeNoSeasonNo;
        ProgressBar thumbnailProgress, progressBar_Continue_watching;
        CardView card_root_layout;

        public Holder(@NonNull View itemView) {
            super(itemView);

            episodeThumbnail = itemView.findViewById(R.id.episodeThumbnail);
            ImageBackground = itemView.findViewById(R.id.ImageBackground);
            episodeName = itemView.findViewById(R.id.episodeName);
            episodeReleaseDate = itemView.findViewById(R.id.episodeReleaseDate);
            totalNumber = itemView.findViewById(R.id.totalNumber);
            episodeNoSeasonNo = itemView.findViewById(R.id.episodeNoSeasonNo);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            progressBar_Continue_watching = itemView.findViewById(R.id.progressBar_Continue_watching);
            card_root_layout = itemView.findViewById(R.id.card_root_layout);
        }
    }

    private boolean hasWriteSettingsPermission() {
        return Settings.System.canWrite(context);
    }

    public static boolean checkString(String input) {
        String pattern = "^[0-9\\s\\p{Punct}]+$"; // Pattern for numbers, spaces, and punctuation
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }
    public void performClick(int position, String startEP) {
        fromContinueLayoutBtn = true;
        actualPosition = position - Integer.parseInt(startEP);
        holder.card_root_layout.performClick();

    }
}
