package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeAndQualityModel;
import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeLinkFinalModel;
import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeModel;
import com.bunny.entertainment.yousee.models.anime.ServerModelOne;
import com.bunny.entertainment.yousee.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AnimeEpisodesAdapter extends RecyclerView.Adapter<AnimeEpisodesAdapter.Holder> {

    ArrayList<AnimeEpisodeModel> animeEpisodeModelArrayList;
    ArrayList<AnimeEpisodeAndQualityModel> animeEpisodeAndQualityModels;
    Context context;
    private static final int REQUEST_WRITE_SETTINGS = 111;
    SharedPreferences settingsPreferences, animePreferences;
    String allowSystemWriteSettingsAsk;
    String animeName, episodeLink, animeId, myAnimeListID, animeType, subDub;
    ArrayList<AnimeEpisodeLinkFinalModel> episodeUrlsArraylist;
    ArrayList<ServerModelOne> consumetSourceServersArraylist;
    int s;
    AnimeEpisodeSelectAdapter animeEpisodeSelectAdapter;
    ProgressBar main_ProgressBar;
    BottomSheetDialog bottomSheetDialog;


    public AnimeEpisodesAdapter() {
    }

    public AnimeEpisodesAdapter(ArrayList<AnimeEpisodeModel> animeEpisodeModelArrayList, Context context, String animeName, String animeId, String myAnimeListID, String animeType) {
        this.animeEpisodeModelArrayList = animeEpisodeModelArrayList;
        this.context = context;
        this.animeName = animeName;
        this.animeId = animeId;
        this.myAnimeListID = myAnimeListID;
        this.animeType = animeType;
        if (context != null) {
            settingsPreferences = context.getSharedPreferences("settingsPreferences", Context.MODE_PRIVATE);
            animePreferences = context.getSharedPreferences("animePreferences", Context.MODE_PRIVATE);
            allowSystemWriteSettingsAsk = settingsPreferences.getString("allowSystemWriteSettings", "true");
        }
        String previouslySelectedSource = animePreferences.getString(animeName + "Source", null);
        consumetSourceServersArraylist = new ArrayList<>();
        if (previouslySelectedSource != null) {
            if (previouslySelectedSource.equals("Aniwatch(consumet)")) {
                ServerModelOne serverModelOne = new ServerModelOne("zoro", "vidcloud ");
                consumetSourceServersArraylist.add(serverModelOne);
                serverModelOne = new ServerModelOne("zoro", "vidstreaming");
                consumetSourceServersArraylist.add(serverModelOne);
            } else if (previouslySelectedSource.equals("Gogo(consumet)")) {
                ServerModelOne serverModelOne = new ServerModelOne("gogo", "gogocdn");
                consumetSourceServersArraylist.add(serverModelOne);
                serverModelOne = new ServerModelOne("gogo", "vidstreaming");
                consumetSourceServersArraylist.add(serverModelOne);
            }
        } else {
            ServerModelOne serverModelOne = new ServerModelOne("zoro", "vidcloud ");
            consumetSourceServersArraylist.add(serverModelOne);
            serverModelOne = new ServerModelOne("zoro", "vidstreaming");
            consumetSourceServersArraylist.add(serverModelOne);
        }
    }

    @NonNull
    @Override
    public AnimeEpisodesAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_episode_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeEpisodesAdapter.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        holder.card_root_layout.startAnimation(AnimationUtils.loadAnimation(holder.card_root_layout.getContext(), R.anim.recyclerviewscroll_top_bottom));


        Glide.with(context).load(animeEpisodeModelArrayList.get(actualPosition).getEpisodeImage()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
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
                .load(animeEpisodeModelArrayList.get(actualPosition).getEpisodeImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new BlurTransformation(20, 2))
                .into(holder.ImageBackground);

        holder.episodeName.setText(animeEpisodeModelArrayList.get(actualPosition).getEpisodeName());
        if(animeEpisodeModelArrayList.get(actualPosition).getSeasonNoEpisodeNo().toLowerCase().contains("episode")){
            holder.episodeNoSeasonNo.setText(animeEpisodeModelArrayList.get(actualPosition).getSeasonNoEpisodeNo());
        } else {
            String episode = "Episode "+animeEpisodeModelArrayList.get(actualPosition).getSeasonNoEpisodeNo();
            holder.episodeNoSeasonNo.setText(episode);
        }

        holder.episodeReleaseDate.setVisibility(View.GONE);
        holder.totalNumber.setText(animeEpisodeModelArrayList.get(actualPosition).getTotalEps());

        String getProgressIfAny = animePreferences.getString(myAnimeListID + animeEpisodeModelArrayList.get(actualPosition).getSeasonNoEpisodeNo().trim(), null);
        if (getProgressIfAny != null) {
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
                        String custom_text = "To allow app to control brightness and volume within the app, write permission is required! If you don`t want to allow now, you can also provide the permission later from settings.";
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
                    getEpisodeLink(view, actualPosition);
                }


            }
        });

    }

    private void getEpisodeLink(View view, int actualPosition) {
        String previouslySelectedSource = animePreferences.getString(animeName + "Source", null);
        if (previouslySelectedSource != null) {
            subDub = animePreferences.getString(myAnimeListID + "language", null);
            if (previouslySelectedSource.equals("Aniwatch(consumet)")) {
                episodeLink = Constants.MY_CONSUMET_API + "anime/zoro/watch?episodeId=" + animeEpisodeModelArrayList.get(actualPosition).getEpisodeId();
                changeSubDub();
            } else if (previouslySelectedSource.equals("Gogo(consumet)")) {
                episodeLink = Constants.MY_CONSUMET_API + "anime/gogoanime/watch/" + animeEpisodeModelArrayList.get(actualPosition).getEpisodeId();
            }
        } else {
            episodeLink = Constants.MY_CONSUMET_API + "anime/zoro/watch?episodeId=" + animeEpisodeModelArrayList.get(actualPosition).getEpisodeId();
            subDub = animePreferences.getString(myAnimeListID + "language", null);
            changeSubDub();
        }


        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
        View bsView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_server_select_dialog, view.findViewById(R.id.main_bottomSheet_layout));

        RecyclerView recyclerView_for_episode;
        TextView textView_selectServer;
        CheckBox make_default_checkBox;
        Button cancel_button;
        LinearLayout layout_auto_selected_server;

        recyclerView_for_episode = bsView.findViewById(R.id.recyclerView_for_custom);
        main_ProgressBar = bsView.findViewById(R.id.main_ProgressBar);
        textView_selectServer = bsView.findViewById(R.id.textView_custom);
        make_default_checkBox = bsView.findViewById(R.id.make_default_checkBox);
        make_default_checkBox.setVisibility(View.VISIBLE);
        cancel_button = bsView.findViewById(R.id.cancel_button);
        layout_auto_selected_server = bsView.findViewById(R.id.layout_auto_selected_server);


        if (animePreferences.getString("animeServerSelectedDefault" + myAnimeListID, null) != null) {
            if (animePreferences.getString("animeServerSelectedDefault" + myAnimeListID, null).contains("false")) {
                make_default_checkBox.setChecked(false);
            }
        }

        if (make_default_checkBox.isChecked()) {
            animePreferences.edit().putString("animeServerSelectedDefault" + myAnimeListID, "true").apply();
        }

        make_default_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    animePreferences.edit().putString("animeServerSelectedDefault" + myAnimeListID, "true").apply();
                } else {
                    animePreferences.edit().putString("animeServerSelectedDefault" + myAnimeListID, "false").apply();
                }
            }
        });

        if (animePreferences.getString("animeServerSelectedDefault" + myAnimeListID, null) != null && animePreferences.getString("animeServerSelectedDefault" + myAnimeListID, null).contains("true")) {
            String savedServer;
            if(previouslySelectedSource != null){
                savedServer = animePreferences.getString(myAnimeListID + previouslySelectedSource + "defaultSelectedServerIs", null);
            } else {
                savedServer = animePreferences.getString(myAnimeListID + "Aniwatch(consumet)" + "defaultSelectedServerIs", null);
            }
            if (savedServer != null) {
                main_ProgressBar.setVisibility(View.GONE);
                layout_auto_selected_server.setVisibility(View.VISIBLE);
                cancel_button.setVisibility(View.VISIBLE);
            }
        }

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(previouslySelectedSource != null){
                    animePreferences.edit().putString(myAnimeListID + previouslySelectedSource + "defaultSelectedServerIs", null).apply();
                } else {
                    animePreferences.edit().putString(myAnimeListID + "Aniwatch(consumet)" + "defaultSelectedServerIs", null).apply();
                }
                loopForServer(actualPosition, previouslySelectedSource);
                main_ProgressBar.setVisibility(View.VISIBLE);
                layout_auto_selected_server.setVisibility(View.GONE);
                cancel_button.setVisibility(View.GONE);
            }
        });

        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setContentView(bsView);
        bottomSheetDialog.show();


        episodeUrlsArraylist = new ArrayList<>();

       /* if (animePreferences.getString("animeServerSelectedDefault" + myAnimeListID, null) != null) {
            if (animePreferences.getString(myAnimeListID + "defaultSelectedServerIs", null) != null) {
                String serverSaved = animePreferences.getString(myAnimeListID + "defaultSelectedServerIs", null);

                if (serverSaved.contains("asianload")) {
                    fetchEpisodeUrlsForServer(0, actualPosition);
                } else if (serverSaved.contains("mixdrop")) {
                    fetchEpisodeUrlsForServer(1, actualPosition);
                } else if (serverSaved.contains("streamtape")) {
                    fetchEpisodeUrlsForServer(2, actualPosition);
                } else if (serverSaved.contains("streamsb")) {
                    fetchEpisodeUrlsForServer(3, actualPosition);
                }

            } else {
                loopForServer(actualPosition, previouslySelectedSource);
            }
        } else {
            loopForServer(actualPosition, previouslySelectedSource);
        }*/

        loopForServer(actualPosition, previouslySelectedSource);


        if (context != null) {
            System.out.println("Show Episode Adapter:DramaType:  " + animeType);

            animeEpisodeSelectAdapter = new AnimeEpisodeSelectAdapter(episodeUrlsArraylist, context, bottomSheetDialog, myAnimeListID, animeType, animeName);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView_for_episode.setLayoutManager(linearLayoutManager);
            recyclerView_for_episode.setAdapter(animeEpisodeSelectAdapter);
            recyclerView_for_episode.setVisibility(View.VISIBLE);
            textView_selectServer.setVisibility(View.VISIBLE);
        }
    }
    private void changeSubDub() {
        if (subDub!= null && subDub.equals("dub")){
            System.out.println("episodeLink: "+ episodeLink);
            if(episodeLink.contains("$both")){
                episodeLink = episodeLink.substring(0, episodeLink.indexOf("$both"));
            } else if (episodeLink.contains("$sub")) {
                episodeLink = episodeLink.substring(0, episodeLink.indexOf("$sub"));
            }

            episodeLink = episodeLink+"$dub";
        }
    }

    private void loopForServer(int actualPosition, String previouslySelectedSource) {

        for (s = 0; s < consumetSourceServersArraylist.size(); s++) {

            if (consumetSourceServersArraylist.get(s).getSiteName().equals("zoro")) {
                if (episodeLink.contains("&server=")) {
                    episodeLink = episodeLink.substring(0, episodeLink.indexOf("&server="));
                }
                episodeLink = episodeLink.trim() + "&server=" + consumetSourceServersArraylist.get(s).getServerName();
            } else if (consumetSourceServersArraylist.get(s).getSiteName().equals("gogo")) {
                if (episodeLink.contains("?server=")) {
                    episodeLink = episodeLink.substring(0, episodeLink.indexOf("?server="));
                }
                episodeLink = episodeLink.trim() + "?server=" + consumetSourceServersArraylist.get(s).getServerName();
            }

            fetchEpisodeUrlsForServer(s, actualPosition);
        }
    }

    private void fetchEpisodeUrlsForServer(int s, int actualPosition) {

        if (context != null) {
            try {

                RequestQueue queue = Volley.newRequestQueue(context);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, episodeLink, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("sources");
                            if (consumetSourceServersArraylist.get(s).getSiteName().equals("zoro")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String episodeUrl = jsonObject1.getString("url");
                                    String subtitleLink = "";
                                    if (jsonObject.has("subtitles")) {
                                        JSONArray jsonArraySubtitles = jsonObject.getJSONArray("subtitles");
                                        for (int j = 0; j < jsonArraySubtitles.length(); j++) {
                                            JSONObject jsonObjectForLang = jsonArraySubtitles.getJSONObject(j);
                                            String lang = jsonObjectForLang.getString("lang");
                                            if (lang.contains("English") || lang.contains("english")) {
                                                subtitleLink = jsonObjectForLang.getString("url");
                                                break;
                                            }
                                        }

                                    }

                                    if (!episodeUrl.equals("")) {
                                        AnimeEpisodeLinkFinalModel animeEpisodeLinkFinalModel = new AnimeEpisodeLinkFinalModel(animeName, episodeUrl, consumetSourceServersArraylist.get(s).getServerName(), consumetSourceServersArraylist.get(s).getSiteName(), animeEpisodeModelArrayList.get(actualPosition).getSeasonNoEpisodeNo(), subtitleLink);
                                        episodeUrlsArraylist.add(animeEpisodeLinkFinalModel);
                                        Log.e("episodeUrl", episodeUrlsArraylist.get(i).getEpisodeLink());
                                        animeEpisodeSelectAdapter.notifyItemInserted(episodeUrlsArraylist.size());
                                        main_ProgressBar.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                animeEpisodeAndQualityModels = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String episodeUrl = jsonObject1.getString("url");

                                    String quality = jsonObject1.getString("quality");
                                    AnimeEpisodeAndQualityModel animeEpisodeAndQualityModel = new AnimeEpisodeAndQualityModel(quality, episodeUrl);
                                    animeEpisodeAndQualityModels.add(animeEpisodeAndQualityModel);
                                }

                                AnimeEpisodeLinkFinalModel animeEpisodeLinkFinalModel = new AnimeEpisodeLinkFinalModel(animeName, animeEpisodeAndQualityModels, consumetSourceServersArraylist.get(s).getServerName(), consumetSourceServersArraylist.get(s).getSiteName(), animeEpisodeModelArrayList.get(actualPosition).getSeasonNoEpisodeNo(), "");
                                episodeUrlsArraylist.add(animeEpisodeLinkFinalModel);
                                // Log.e("episodeUrl", episodeUrlsArraylist.get(i).getEpisodeLink());
                                animeEpisodeSelectAdapter.notifyItemInserted(episodeUrlsArraylist.size());
                                main_ProgressBar.setVisibility(View.GONE);

                            }


                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (s == 0) {
                            if (error.networkResponse != null && error.networkResponse.statusCode == 500) {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(context, "Maybe Server error! No sure\uD83D\uDE05", Toast.LENGTH_SHORT).show();
                            } else {
                                //progressBar_Main.setVisibility(View.GONE);
                                bottomSheetDialog.dismiss();
                                Toast.makeText(context, "Server Not Available!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public int getItemCount() {
        return animeEpisodeModelArrayList.size();
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

    public void performItemClick(View view, int position) {
        getEpisodeLink(view, position);
    }
}
