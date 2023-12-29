package com.bunny.entertainment.yousee.player;


import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeAndQualityModel;
import com.bunny.entertainment.yousee.utils.DataHolder;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlayerActivity extends AppCompatActivity {
    public static final int MINIMUM_DISTANCE = 60;
    String[] speed = {"0.25x", "0.5x", "Normal", "1.5x", "2x"};
    String urlVideoMain, subtitleLink;
    TextView qualityTxt, speedText, screenSizeTxt, rewindText, forwardText;
    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    //extra
    boolean start = false;
    boolean left, right;
    boolean swipe_move = false;
    boolean success = false;
    boolean singleTap = false;
    boolean isPaused = false;
    TextView vol_brt_text;
    ProgressBar vol_progress, brt_progress;
    LinearLayout vol_progress_container, brt_progress_container, vol_brt_text_container;
    ImageView vol_icon, brt_icon, lock, picture_in_picture;
    FrameLayout main_video_frame_layout;
    ScaleGestureDetector scaleGestureDetector;
    AudioManager audioManager;
    RelativeLayout double_tap_playpause, rew_touch_left, for_touch_right;
    private GestureDetector gestureDetector;
    ControlsMode controlsMode;
    Boolean lockControls = false;
    ConstraintLayout root_layout;
    DataSource.Factory dataSourceFactory;
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector trackSelector;
    private float baseX, baseY;
    private long diffX, diffY;
    private int device_height, device_width, brightness, media_volume;
    private ContentResolver contentResolver;
    private Window window;
    Boolean whenMinimizedPlaying = true;
    private float scale_factor = 1.0f;
    ProgressBar progressBar;
    Handler brightnessAndVolumeHandler = new Handler();
    Runnable brightnessAndVolumeRunnable;
    String seekToPositionSaved, seekToPositionSavedSharedPref, dramaShowAnimeId, whereFrom, dramaShowAnimeNameAndEpisodeNo, totalDuration, showMediaType, dramaShowAnimeType;
    SharedPreferences dramaPreferences, animePreferences;
    MediaItem mediaItem;
    int rewTime = 0, forTime = 0;
    String epNo;
    String realEpisodeNo;
    FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<AnimeEpisodeAndQualityModel> animeEpisodeAndQualityModelArrayList;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //hide notch and fill full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        setContentView(R.layout.activity_player);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(PlayerActivity.this);

        dramaPreferences = getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);

        whereFrom = getIntent().getStringExtra("whereFrom");
        if (whereFrom != null && whereFrom.contains("AnimeFragment")) {
            animePreferences = getSharedPreferences("animePreferences", Context.MODE_PRIVATE);
            urlVideoMain = getIntent().getStringExtra("videoLink");
            if (urlVideoMain != null && urlVideoMain.contains("DataHolder")) {
                animeEpisodeAndQualityModelArrayList = DataHolder.getAnimeEpisodeAndQualityModelArrayList();
            }
            dramaShowAnimeId = getIntent().getStringExtra("animeID");
            dramaShowAnimeNameAndEpisodeNo = getIntent().getStringExtra("animeNameAndEpisodeNo");
            dramaShowAnimeType = getIntent().getStringExtra("animeType");
        } else {
            urlVideoMain = getIntent().getStringExtra("videoLink");
            dramaShowAnimeId = getIntent().getStringExtra("dramaIdMyDramaList");
            dramaShowAnimeNameAndEpisodeNo = getIntent().getStringExtra("dramaNameAndEpisodeNo");
            realEpisodeNo = getIntent().getStringExtra("realEpisodeNo");
            showMediaType = getIntent().getStringExtra("showMediaType");
            dramaShowAnimeType = getIntent().getStringExtra("dramaType");
        }


        System.out.println(realEpisodeNo);

        if (dramaShowAnimeNameAndEpisodeNo != null) {
            epNo = dramaShowAnimeNameAndEpisodeNo.substring(dramaShowAnimeNameAndEpisodeNo.indexOf("ode") + 3).trim();
            Log.e("epNo", epNo);
        }


        if (showMediaType != null && showMediaType.contains("tv") && realEpisodeNo != null) {
            seekToPositionSavedSharedPref = dramaPreferences.getString(dramaShowAnimeId + realEpisodeNo, null);
            if (seekToPositionSavedSharedPref != null) {
                seekToPositionSavedSharedPref = seekToPositionSavedSharedPref.substring(0, seekToPositionSavedSharedPref.indexOf("/"));
                Log.e("seekToPositionSavedSharedPref", seekToPositionSavedSharedPref);
            }
        } else if (whereFrom.contains("AnimeFragment")) {
            seekToPositionSavedSharedPref = animePreferences.getString(dramaShowAnimeId + epNo, null);
            if (seekToPositionSavedSharedPref != null) {
                seekToPositionSavedSharedPref = seekToPositionSavedSharedPref.substring(0, seekToPositionSavedSharedPref.indexOf("/"));
                Log.e("seekToPositionSavedSharedPref", seekToPositionSavedSharedPref);
            }
        } else {
            seekToPositionSavedSharedPref = dramaPreferences.getString(dramaShowAnimeId + epNo, null);
            if (seekToPositionSavedSharedPref != null) {
                seekToPositionSavedSharedPref = seekToPositionSavedSharedPref.substring(0, seekToPositionSavedSharedPref.indexOf("/"));
                Log.e("seekToPositionSavedSharedPref", seekToPositionSavedSharedPref);
            }
        }


        //vol_text = findViewById(R.id.vol_text);
        vol_brt_text = findViewById(R.id.vol_brt_text);
        vol_progress = findViewById(R.id.vol_progress);
        brt_progress = findViewById(R.id.brt_progress);
        vol_progress_container = findViewById(R.id.vol_progress_container);
        brt_progress_container = findViewById(R.id.brt_progress_container);
        // vol_text_container = findViewById(R.id.vol_text_container);
        vol_brt_text_container = findViewById(R.id.vol_brt_text_container);
        vol_icon = findViewById(R.id.vol_icon);
        brt_icon = findViewById(R.id.brt_icon);
        double_tap_playpause = findViewById(R.id.double_tap_play_pause);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleDetector());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        root_layout = findViewById(R.id.root_layout);
        //exo_subtitle = findViewById(R.id.exo_subtitle);
        screenSizeTxt = findViewById(R.id.screenSizeTxt);
        speedText = findViewById(R.id.speedTxt);
        lock = findViewById(R.id.lock);
        progressBar = findViewById(R.id.progressBar);
        picture_in_picture = findViewById(R.id.picture_in_picture);
        main_video_frame_layout = findViewById(R.id.main_video_frame_layout);
        rew_touch_left = findViewById(R.id.rew_touch_left);
        for_touch_right = findViewById(R.id.for_touch_right);
        rewindText = findViewById(R.id.rewindText);
        forwardText = findViewById(R.id.forwardText);

        gestureDetector = new GestureDetector(this, new DoubleTapGestureListener());

        trackSelector = new DefaultTrackSelector(this);


        DefaultTrackSelector.Parameters newParameters = trackSelector.getParameters()
                .buildUpon()
                //.setForceLowestBitrate(true)

                .build();


        trackSelector.setParameters(newParameters);

        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(PlayerActivity.this, "ExoPlayer"));

        simpleExoPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();


        playerView = findViewById(R.id.exoPlayerView);
        playerView.setPlayer(simpleExoPlayer);

        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);


        if (whereFrom.contains("DramaFragment")) {
            mediaItem = MediaItem.fromUri(urlVideoMain);
            simpleExoPlayer.setMediaItem(mediaItem);
        } else if (whereFrom.contains("ShowsFragment")) {
            mediaItem = MediaItem.fromUri(urlVideoMain);
            subtitleLink = getIntent().getStringExtra("subtitleLink");
            if (subtitleLink != null) {
                Format textFormat = Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, C.SELECTION_FLAG_DEFAULT, null);
                MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
                MediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(subtitleLink), textFormat, 0);
                MergingMediaSource mergedSource = new MergingMediaSource(videoSource, subtitleSource);
                simpleExoPlayer.setMediaSource(mergedSource);
            } else {
                simpleExoPlayer.setMediaItem(mediaItem);
            }

        } else if (whereFrom.contains("AnimeFragment")) {
            if (urlVideoMain.contains("DataHolder")) {
                for (int i = 0; i < animeEpisodeAndQualityModelArrayList.size(); i++) {
                    if (animeEpisodeAndQualityModelArrayList.get(i).getEpQuality().equals("default")) {
                        mediaItem = MediaItem.fromUri(animeEpisodeAndQualityModelArrayList.get(i).getEpLink());
                    }
                }
                simpleExoPlayer.setMediaItem(mediaItem);
            } else {
                mediaItem = MediaItem.fromUri(urlVideoMain);
                subtitleLink = getIntent().getStringExtra("subtitleLink");
                if (subtitleLink != null) {
                    Format textFormat = Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, C.SELECTION_FLAG_DEFAULT, null);
                    MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
                    MediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(subtitleLink), textFormat, 0);
                    MergingMediaSource mergedSource = new MergingMediaSource(videoSource, subtitleSource);
                    simpleExoPlayer.setMediaSource(mergedSource);
                } else {
                    simpleExoPlayer.setMediaItem(mediaItem);
                }
            }
        }
        simpleExoPlayer.prepare();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!singleTap) {
                    singleTap = true;
                }
            }
        }, 4900);
        simpleExoPlayer.play();

        if (whereFrom.contains("ShowsFragment") && subtitleLink != null) {
            if (playerView.getSubtitleView() != null) {
                playerView.getSubtitleView().setVisibility(View.GONE);
            }
            TextView subtitleTextView = findViewById(R.id.subtitleTextView);
            simpleExoPlayer.addTextOutput(new TextOutput() {
                @Override
                public void onCues(List<Cue> cues) {

                    if (cues != null && !cues.isEmpty()) {

                        Log.e("subtitle", String.valueOf(cues.get(0)));
                        Cue firstCue = cues.get(0);

                        subtitleTextView.setText(firstCue.text);
                    } else {
                        TextView subtitleTextView = findViewById(R.id.subtitleTextView);
                        subtitleTextView.setText("");
                    }
                }
            });


        }


        if (seekToPositionSavedSharedPref != null) {
            Log.e("here", seekToPositionSavedSharedPref);
            simpleExoPlayer.seekTo(Long.parseLong(seekToPositionSavedSharedPref));
        }


        ImageView forwardBtn = playerView.findViewById(R.id.fwd);
        ImageView rewBtn = playerView.findViewById(R.id.rew);
        ImageView setting = playerView.findViewById(R.id.exo_track_selection_view);
        ImageView speedBtn = playerView.findViewById(R.id.exo_playback_speed);
        ImageView exoplayer_resize1 = playerView.findViewById(R.id.exoplayer_resize1);
        ImageView exoplayer_resize2 = playerView.findViewById(R.id.exoplayer_resize2);
        ImageView exoplayer_resize3 = playerView.findViewById(R.id.exoplayer_resize3);
        ImageView exoplayer_resize4 = playerView.findViewById(R.id.exoplayer_resize4);
        ImageView exoplayer_resize5 = playerView.findViewById(R.id.exoplayer_resize5);
        ImageView exoplayer_back = playerView.findViewById(R.id.backExo);
        qualityTxt = playerView.findViewById(R.id.qualityTxt);

        TextView titleExo = playerView.findViewById(R.id.titleMoviePlayer);
        //titleExo.setText(dramaNameAndEpisodeNo);

        System.out.println("dramatype: " + dramaShowAnimeType);


        if (dramaShowAnimeType != null && dramaShowAnimeType.contains("(Movie)") || showMediaType != null && showMediaType.contains("movie")) {
            String titleForMovie = dramaShowAnimeNameAndEpisodeNo.substring(0, dramaShowAnimeNameAndEpisodeNo.indexOf("-"));
            titleExo.setText(titleForMovie);
        } else {
            titleExo.setText(dramaShowAnimeNameAndEpisodeNo);
        }

        Log.e("dramaNameAndEpisodeNo", dramaShowAnimeNameAndEpisodeNo);

        exoplayer_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        exoplayer_resize1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.GONE);
                exoplayer_resize2.setVisibility(View.VISIBLE);
                exoplayer_resize3.setVisibility(View.GONE);
                exoplayer_resize4.setVisibility(View.GONE);
                exoplayer_resize5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                String screenSizeText = "Fill";
                screenSizeTxt.setText(screenSizeText);
                // Toast.makeText(MoviePlayerActivity.this, "Fill Mode", Toast.LENGTH_SHORT).show();


            }
        });
        exoplayer_resize2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.GONE);
                exoplayer_resize2.setVisibility(View.GONE);
                exoplayer_resize3.setVisibility(View.VISIBLE);
                exoplayer_resize4.setVisibility(View.GONE);
                exoplayer_resize5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                String screenSizeText = "Fixed\nwidth";
                screenSizeTxt.setText(screenSizeText);
                // Toast.makeText(MoviePlayerActivity.this, "Fixed Width", Toast.LENGTH_SHORT).show();


            }
        });
        exoplayer_resize3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.GONE);
                exoplayer_resize2.setVisibility(View.GONE);
                exoplayer_resize3.setVisibility(View.GONE);
                exoplayer_resize4.setVisibility(View.VISIBLE);
                exoplayer_resize5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                String screenSizeText = "Zoom";
                screenSizeTxt.setText(screenSizeText);
                //  Toast.makeText(MoviePlayerActivity.this, "Zoom Mode", Toast.LENGTH_SHORT).show();

            }
        });
        exoplayer_resize4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.GONE);
                exoplayer_resize2.setVisibility(View.GONE);
                exoplayer_resize3.setVisibility(View.GONE);
                exoplayer_resize4.setVisibility(View.GONE);
                exoplayer_resize5.setVisibility(View.VISIBLE);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                String screenSizeText = "Fixed\nheight";
                screenSizeTxt.setText(screenSizeText);
                //  Toast.makeText(MoviePlayerActivity.this, "Fixed Height", Toast.LENGTH_SHORT).show();

            }
        });
        exoplayer_resize5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.VISIBLE);
                exoplayer_resize2.setVisibility(View.GONE);
                exoplayer_resize3.setVisibility(View.GONE);
                exoplayer_resize4.setVisibility(View.GONE);
                exoplayer_resize5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                String screenSizeText = "Fit";
                screenSizeTxt.setText(screenSizeText);
                //  Toast.makeText(MoviePlayerActivity.this, "Fit Mode", Toast.LENGTH_SHORT).show();
            }
        });

        speedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
                builder.setTitle("Set Speed");
                builder.setItems(speed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]

                        if (which == 0) {
                            speedText.setText(R.string._0_25x);
                            PlaybackParameters param = new PlaybackParameters(0.5f);
                            simpleExoPlayer.setPlaybackParameters(param);
                        }
                        if (which == 1) {
                            speedText.setText(R.string._0_5x);
                            PlaybackParameters param = new PlaybackParameters(0.5f);
                            simpleExoPlayer.setPlaybackParameters(param);
                        }
                        if (which == 2) {
                            speedText.setText(R.string._1_0x);
                            PlaybackParameters param = new PlaybackParameters(1f);
                            simpleExoPlayer.setPlaybackParameters(param);
                        }
                        if (which == 3) {
                            speedText.setText(R.string._1_5x);
                            PlaybackParameters param = new PlaybackParameters(1.5f);
                            simpleExoPlayer.setPlaybackParameters(param);

                        }
                        if (which == 4) {
                            speedText.setText(R.string._2_0x);
                            PlaybackParameters param = new PlaybackParameters(2f);
                            simpleExoPlayer.setPlaybackParameters(param);
                        }


                    }
                });
                builder.show();


            }
        });


        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);


            }
        });
        rewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long num = simpleExoPlayer.getCurrentPosition() - 10000;
                if (num < 0) {
                    simpleExoPlayer.seekTo(0);
                } else {
                    simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
                }
            }
        });


        //  ImageView fullscreenButton = playerView.findViewById(R.id.fullscreen);
        //   fullscreenButton.setVisibility(View.VISIBLE);
//        fullscreenButton.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("SourceLockedOrientationActivity")
//            @Override
//            public void onClick(View view) {
//
//
//                int orientation = MoviePlayerActivity.this.getResources().getConfiguration().orientation;
//                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    // code for portrait mode
//
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
//
//                } else {
//                    // code for landscape mode
//
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//                }
//
//
//            }
//        });


        findViewById(R.id.exo_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleExoPlayer.play();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
        findViewById(R.id.exo_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleExoPlayer.pause();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });


        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onTracksChanged(@NonNull TrackGroupArray trackGroups, @NonNull TrackSelectionArray trackSelections) {
                Player.Listener.super.onTracksChanged(trackGroups, trackSelections);

                if (trackSelections.get(0) != null) {
                    String qltText = trackSelections.get(0).getFormat(0).height + "p";
                    /*if(trackSelections.get(0).getFormat(0).peakBitrate != -1){
                        String qltt = String.valueOf(trackSelections.get(0).getFormat(0).peakBitrate);
                        Log.e("qltt", qltt);
                    }*/

                    qualityTxt.setText(qltText);


                    if (trackSelections.get(0).getType() == C.TRACK_TYPE_VIDEO) {
                    }
                }
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == ExoPlayer.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (state == ExoPlayer.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerError(@NonNull ExoPlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PlayerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        playerView.setControllerAutoShow(true);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(trackSelector)) {
                    isShowingTrackSelectionDialog = true;
                    TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(trackSelector,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
                    trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
                }


            }
        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_width = displayMetrics.widthPixels;
        device_height = displayMetrics.heightPixels;
        float density = displayMetrics.densityDpi;

        playerView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!lockControls) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // playerView.showController();
                            start = true;
                            if (motionEvent.getX() < (device_width / 2)) {
                                left = true;
                                right = false;
                            } else if (motionEvent.getX() > (device_width / 2)) {
                                left = false;
                                right = true;
                            }
                            baseX = motionEvent.getX();
                            baseY = motionEvent.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (hasWriteSettingsPermission()) {
                                swipe_move = true;
                                brightnessAndVolumeHandler.removeCallbacks(brightnessAndVolumeRunnable);
                                diffX = (long) Math.ceil(motionEvent.getX() - baseX);
                                diffY = (long) Math.ceil(motionEvent.getY() - baseY);
                                double brightnessSpeed = 0.03;
                                if (Math.abs(diffY) > MINIMUM_DISTANCE) {
                                    if (motionEvent.getY() >= device_height - device_height * 0.85) {
                                        start = true;
                                        if (Math.abs(diffY) > Math.abs(diffX)) {
                                            //boolean value;
                                            //value = android.provider.Settings.System.canWrite(getApplicationContext());
                                            if (left) {
                                                playerView.setControllerHideOnTouch(false);
                                                contentResolver = getContentResolver();
                                                window = getWindow();
                                                try {
                                                    android.provider.Settings.System.putInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                                                            android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                                    brightness = android.provider.Settings.System.getInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS);
                                                } catch (
                                                        android.provider.Settings.SettingNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                               /* int new_brightness = (int) (brightness - (diffY * brightnessSpeed));
                                                if (new_brightness > 250) {
                                                    new_brightness = 250;
                                                } else if (new_brightness < 1) {
                                                    new_brightness = 1;
                                                }*/

                                                // Adjust brightness calculation
                                                int new_brightness = (int) (brightness - (diffY * brightnessSpeed));
                                                new_brightness = Math.max(1, Math.min(new_brightness, 250));

                                                double brt_percentage = Math.ceil((((double) new_brightness / (double) 250) * (double) 100));
                                                brt_progress_container.setVisibility(View.VISIBLE);
                                                vol_brt_text_container.setVisibility(View.VISIBLE);
                                                brt_progress.setProgress((int) brt_percentage);

                                                if (brt_percentage < 30) {
                                                    brt_icon.setImageResource(R.drawable.ic_brightness_low);
                                                } else if (brt_percentage > 30 && brt_percentage < 80) {
                                                    brt_icon.setImageResource(R.drawable.ic_brightness_moderate);
                                                } else if (brt_percentage > 80) {
                                                    brt_icon.setImageResource(R.drawable.ic_brightness);
                                                }
                                                String brightnessText = "Brightness: " + brt_percentage + "%";
                                                vol_brt_text.setText(brightnessText);
                                                android.provider.Settings.System.putInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS,
                                                        (new_brightness));
                                                WindowManager.LayoutParams layoutParams = window.getAttributes();
                                                layoutParams.screenBrightness = brightness / (float) 255;
                                                window.setAttributes(layoutParams);
                                            } else if (right) {
                                                playerView.setControllerHideOnTouch(false);
                                                vol_brt_text_container.setVisibility(View.VISIBLE);
                                                media_volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                                int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                                                int swipeChange = 1;
                                                int newMediaVolume = 0;

                                                if (diffY > 0) {
                                                    int newVolume = media_volume - swipeChange;
                                                    newMediaVolume = Math.max(newVolume, 0);
                                                } else if (diffY < 0) {
                                                    int newVolume = media_volume + swipeChange;
                                                    newMediaVolume = Math.min(newVolume, maxVol);
                                                }


                                                newMediaVolume = Math.max(0, Math.min(newMediaVolume, maxVol));

                                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                                        newMediaVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                                                double volPer = Math.ceil((((double) newMediaVolume / (double) maxVol) * (double) 100));
                                                String volumeText = "Volume: " + volPer + "%";
                                                vol_brt_text.setText(volumeText);
                                                if (volPer < 1) {
                                                    vol_icon.setImageResource(R.drawable.ic_volume_off);
                                                    vol_brt_text.setVisibility(View.VISIBLE);
                                                    vol_brt_text.setText(R.string.off);
                                                } else if (volPer >= 1) {
                                                    vol_icon.setImageResource(R.drawable.ic_volume);
                                                    vol_brt_text.setVisibility(View.VISIBLE);
                                                }
                                                vol_progress_container.setVisibility(View.VISIBLE);
                                                vol_progress.setProgress((int) volPer);
                                            }
                                            success = true;
                                        }
                                    }
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            swipe_move = false;
                            start = false;
                            brightnessAndVolumeRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (!swipe_move) {
                                        playerView.setControllerHideOnTouch(true);
                                        vol_progress_container.setVisibility(View.GONE);
                                        brt_progress_container.setVisibility(View.GONE);
                                        vol_brt_text_container.setVisibility(View.GONE);
                                        vol_brt_text_container.setVisibility(View.GONE);
                                        playerView.hideController();
                                    }
                                    if (!swipe_move && !singleTap) {
                                        playerView.hideController();
                                    }
                                }
                            };
                            brightnessAndVolumeHandler.postDelayed(brightnessAndVolumeRunnable, 1000);
                            break;
                    }
                    scaleGestureDetector.onTouchEvent(motionEvent);
                    gestureDetector.onTouchEvent(motionEvent);
                }
                return super.onTouch(view, motionEvent);
            }

            @Override
            public void onDoubleTouch() {
                super.onDoubleTouch();

               /* if (double_tap) {
                    simpleExoPlayer.setPlayWhenReady(true);
                    //double_tap_playpause.setVisibility(View.GONE);
                    double_tap = false;
                } else {
                    simpleExoPlayer.setPlayWhenReady(false);
                    //double_tap_playpause.setVisibility(View.VISIBLE);
                    double_tap = true;
                }*/
            }

            @Override
            public void onSingleTouch() {
                super.onSingleTouch();
                if (double_tap_playpause.getVisibility() == View.VISIBLE) {
                    double_tap_playpause.setVisibility(View.GONE);
                }
                if (rew_touch_left.getVisibility() == View.VISIBLE) {
                    rew_touch_left.setVisibility(View.INVISIBLE);
                }
                if (for_touch_right.getVisibility() == View.VISIBLE) {
                    for_touch_right.setVisibility(View.INVISIBLE);
                }
            }
        });

        playerView.setControllerShowTimeoutMs(3000);

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lockControls) {
                    lockControls = false;
                    controlsMode = ControlsMode.FULLSCREEN;
                    root_layout.setVisibility(View.VISIBLE);
                    lock.setImageResource(R.drawable.ic_unlock);
                    Toast.makeText(PlayerActivity.this, "UnLocked", Toast.LENGTH_SHORT).show();
                } else {
                    lockControls = true;
                    controlsMode = ControlsMode.LOCK;
                    root_layout.setVisibility(View.INVISIBLE);
                    lock.setImageResource(R.drawable.ic_lock);
                    Toast.makeText(PlayerActivity.this, "Locked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean hasWriteSettingsPermission() {
        return Settings.System.canWrite(PlayerActivity.this);
    }

    protected void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            trackSelector = null;
        }

    }

    protected void onPause() {
        if (simpleExoPlayer != null) {
            seekToPositionSaved = String.valueOf(simpleExoPlayer.getCurrentPosition());
            totalDuration = String.valueOf(simpleExoPlayer.getDuration());
            if (showMediaType != null && showMediaType.contains("tv") && realEpisodeNo != null) {
                dramaPreferences.edit().putString(dramaShowAnimeId + realEpisodeNo, seekToPositionSaved + "/" + totalDuration).apply();
                dramaPreferences.edit().putString(dramaShowAnimeId + "lastEpWatched", realEpisodeNo + "," + seekToPositionSaved + "/" + totalDuration).apply();
                Log.e("lastEpWatched", realEpisodeNo);
            } else if (whereFrom.contains("AnimeFragment")) {
                animePreferences.edit().putString(dramaShowAnimeId + epNo, seekToPositionSaved + "/" + totalDuration).apply();
                animePreferences.edit().putString(dramaShowAnimeId + "lastEpWatched", epNo + "," + seekToPositionSaved + "/" + totalDuration).apply();
            } else {
                String epNo = dramaShowAnimeNameAndEpisodeNo.substring(dramaShowAnimeNameAndEpisodeNo.indexOf("ode") + 3).trim();
                dramaPreferences.edit().putString(dramaShowAnimeId + epNo, seekToPositionSaved + "/" + totalDuration).apply();
                dramaPreferences.edit().putString(dramaShowAnimeId + "lastEpWatched", epNo + "," + seekToPositionSaved + "/" + totalDuration).apply();
            }

            addDramaToDramaList();
            if (simpleExoPlayer.isPlaying()) {
                whenMinimizedPlaying = true;
            } else {
                whenMinimizedPlaying = false;
            }
            simpleExoPlayer.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (simpleExoPlayer != null) {
            seekToPositionSaved = String.valueOf(simpleExoPlayer.getCurrentPosition());
            totalDuration = String.valueOf(simpleExoPlayer.getDuration());
            if (showMediaType != null && showMediaType.contains("tv") && realEpisodeNo != null) {
                dramaPreferences.edit().putString(dramaShowAnimeId + realEpisodeNo, seekToPositionSaved + "/" + totalDuration).apply();
                dramaPreferences.edit().putString(dramaShowAnimeId + "lastEpWatched", realEpisodeNo + "," + seekToPositionSaved + "/" + totalDuration).apply();
            } else if (whereFrom.contains("AnimeFragment")) {
                animePreferences.edit().putString(dramaShowAnimeId + epNo, seekToPositionSaved + "/" + totalDuration).apply();
                animePreferences.edit().putString(dramaShowAnimeId + "lastEpWatched", epNo + "," + seekToPositionSaved + "/" + totalDuration).apply();
            } else {
                String epNo = dramaShowAnimeNameAndEpisodeNo.substring(dramaShowAnimeNameAndEpisodeNo.indexOf("ode") + 3).trim();
                dramaPreferences.edit().putString(dramaShowAnimeId + epNo, seekToPositionSaved + "/" + totalDuration).apply();
                dramaPreferences.edit().putString(dramaShowAnimeId + "lastEpWatched", epNo + "," + seekToPositionSaved + "/" + totalDuration).apply();
            }

            addDramaToDramaList();

            //Log.e("letseeee", Objects.requireNonNull(dramaPreferences.getString(dramaShowAnimeId + "lastEpWatched", null)));
            //Log.e("dramaIdMyDramaList+lastEpWatched", dramaShowAnimeId + "lastEpWatched:      " + epNo + "," + seekToPositionSaved + "/" + simpleExoPlayer.getDuration());
        }
        super.onStop();
    }

    private void addDramaToDramaList() {
        ArrayList<String> dramaList = getDramaListFromPrefs();
        if (dramaList == null) {
            dramaList = new ArrayList<>();
        }

        if (whereFrom.contains("DramaFragment")) {
            if (!isDramaAlreadyAdded(dramaList, dramaShowAnimeId)) {
                dramaList.add(dramaShowAnimeId);
                saveDramaListToSharedPref(dramaList);
            } else {
                deleteOldDataSetNew(dramaList, dramaShowAnimeId);
            }
        } else if (whereFrom.contains("ShowsFragment")) {
            if (!isDramaAlreadyAdded(dramaList, dramaShowAnimeId + "@-" + showMediaType)) {
                dramaList.add(dramaShowAnimeId + "@-" + showMediaType);
                saveDramaListToSharedPref(dramaList);
            } else {
                deleteOldDataSetNew(dramaList, dramaShowAnimeId + "@-" + showMediaType);
            }
        } else if (whereFrom.contains("AnimeFragment")) {
            if (!isDramaAlreadyAdded(dramaList, dramaShowAnimeId + "*-" + dramaShowAnimeType)) {
                dramaList.add(dramaShowAnimeId + "*-" + dramaShowAnimeType);
                saveDramaListToSharedPref(dramaList);
            } else {
                deleteOldDataSetNew(dramaList, dramaShowAnimeId + "*-" + dramaShowAnimeType);
            }
        }
    }

    private void deleteOldDataSetNew(ArrayList<String> dramaList, String dramaShowAnimeId) {
        dramaList.remove(dramaShowAnimeId);
        dramaList.add(dramaShowAnimeId);
        saveDramaListToSharedPref(dramaList);
    }

    private void saveDramaListToSharedPref(ArrayList<String> dramaList) {

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : dramaList) {
            stringBuilder.append(s).append(",");
        }
        dramaPreferences.edit().putString("watchingDramaList", stringBuilder.toString()).apply();
    }

    private ArrayList<String> getDramaListFromPrefs() {
        String stringListStr = dramaPreferences.getString("watchingDramaList", null);

        if (stringListStr == null) {
            return null;
        }
        List<String> dramaList = Arrays.asList(stringListStr.split(","));
        return new ArrayList<>(dramaList);
    }

    private boolean isDramaAlreadyAdded(ArrayList<String> stringList, String searchString) {
        for (String s : stringList) {
            if (s.equals(searchString)) {
                return true;
            }
        }
        return false;
    }

    /*public boolean vpn() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();
                Log.d("DEBUG", "IFACE NAME: " + iface);
                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return false;
    }*/

    @Override
    protected void onResume() {
       /* if (vpn()) {
            new AlertDialog.Builder(this)
                    .setTitle("Vpn Blocked")
                    .setMessage("We Dont Allow to use VPN!")
                    .setCancelable(false)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        } else*/
        if (simpleExoPlayer != null) {
            if (whenMinimizedPlaying) {
                simpleExoPlayer.setPlayWhenReady(true);
            } else {
                simpleExoPlayer.setPlayWhenReady(false);
            }
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onStop();
        releasePlayer();
        finish();
    }

    public enum ControlsMode {
        LOCK, FULLSCREEN
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            seekToPositionSaved = String.valueOf(simpleExoPlayer.getCurrentPosition());
            totalDuration = String.valueOf(simpleExoPlayer.getDuration());
            if (showMediaType != null && showMediaType.contains("tv") && realEpisodeNo != null) {
                dramaPreferences.edit().putString(dramaShowAnimeId + realEpisodeNo, seekToPositionSaved + "/" + totalDuration).apply();
                dramaPreferences.edit().putString(dramaShowAnimeId + "lastEpWatched", realEpisodeNo + "," + seekToPositionSaved + "/" + totalDuration).apply();
            } else if (whereFrom.contains("AnimeFragment")) {
                animePreferences.edit().putString(dramaShowAnimeId + epNo, seekToPositionSaved + "/" + totalDuration).apply();
                animePreferences.edit().putString(dramaShowAnimeId + "lastEpWatched", epNo + "," + seekToPositionSaved + "/" + totalDuration).apply();
            } else {
                String epNo = dramaShowAnimeNameAndEpisodeNo.substring(dramaShowAnimeNameAndEpisodeNo.indexOf("ode") + 3).trim();
                dramaPreferences.edit().putString(dramaShowAnimeId + epNo, seekToPositionSaved + "/" + totalDuration).apply();
                dramaPreferences.edit().putString(dramaShowAnimeId + "lastEpWatched", epNo + "," + seekToPositionSaved + "/" + totalDuration).apply();
            }

            addDramaToDramaList();

            // Log.e("letseeee", Objects.requireNonNull(dramaPreferences.getString(dramaShowAnimeId + "lastEpWatched", null)));
            //  Log.e("dramaShowAnimeId+lastEpWatched", dramaShowAnimeId + "lastEpWatched:      " + epNo + "," + seekToPositionSaved + "/" + simpleExoPlayer.getDuration());
        }
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    private class ScaleDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale_factor *= detector.getScaleFactor();
            scale_factor = Math.max(0.5f, Math.min(scale_factor, 6.0f));

           /* zoom_container.setScaleX(scale_factor);
            zoom_container.setScaleY(scale_factor);
            int percentage = (int) (scale_factor * 100);
            String zoomText = percentage+"%";
            //zoom_perc.setText(zoomText);
            zoom_container.setVisibility(View.VISIBLE);*/

            vol_brt_text_container.setVisibility(View.GONE);
            vol_brt_text_container.setVisibility(View.GONE);
            brt_progress_container.setVisibility(View.GONE);
            vol_progress_container.setVisibility(View.GONE);

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            //zoomContainer.setVisibility(View.GONE);
            super.onScaleEnd(detector);
        }
    }

    private class DoubleTapGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean isHandlerLeftRunning = false;
        private boolean isHandlerRightRunning = false;
        private Handler handlerLeft = new Handler();
        private Handler handlerRight = new Handler();
        private Runnable runnableLeft;
        private Runnable runnableRight;

        private void showPulseAnimation(final View view) {
            Animation animation = AnimationUtils.loadAnimation(PlayerActivity.this, R.anim.pulse_animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.INVISIBLE);
                    playerView.hideController();
                    playerView.setControllerHideOnTouch(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Not needed
                }
            });
            view.startAnimation(animation);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!lockControls) {
                float x = e.getX();
                float viewWidth = playerView.getWidth();
                float centerX = viewWidth / 2;

                if (Math.abs(x - centerX) > centerX / 2) {
                    playerView.setControllerHideOnTouch(false);
                    if (x < viewWidth / 2) {
                        // Double tap on the left side (rewind)
                        if (isHandlerLeftRunning) {
                            handlerLeft.removeCallbacksAndMessages(null);
                        }
                        isHandlerLeftRunning = true;
                        rew_touch_left.setVisibility(View.VISIBLE);
                        showPulseAnimation(rew_touch_left);
                        long seekPosition = simpleExoPlayer.getCurrentPosition() - 10000;
                        simpleExoPlayer.seekTo(Math.max(seekPosition, 0));
                        rewTime += 10;
                        String rewTimeString = rewTime + " Sec";
                        rewindText.setText(rewTimeString);

                        runnableLeft = new Runnable() {
                            @Override
                            public void run() {
                                rew_touch_left.setVisibility(View.INVISIBLE);
                                playerView.hideController();
                                playerView.setControllerHideOnTouch(true);
                                isHandlerLeftRunning = false;
                                rewTime = 0;
                                rewindText.setText("");
                            }
                        };

                        handlerLeft.postDelayed(runnableLeft, 1000);
                    } else {
                        // Double tap on the right side (forward)
                        if (isHandlerRightRunning) {
                            handlerRight.removeCallbacksAndMessages(null);
                        }
                        isHandlerRightRunning = true;
                        for_touch_right.setVisibility(View.VISIBLE);
                        showPulseAnimation(for_touch_right);
                        long seekPosition = simpleExoPlayer.getCurrentPosition() + 10000;
                        simpleExoPlayer.seekTo(seekPosition);
                        forTime += 10;
                        String forTimeString = forTime + " Sec";
                        forwardText.setText(forTimeString);

                        runnableRight = new Runnable() {
                            @Override
                            public void run() {
                                for_touch_right.setVisibility(View.INVISIBLE);
                                playerView.hideController();
                                playerView.setControllerHideOnTouch(true);
                                isHandlerRightRunning = false;
                                forTime = 0;
                                forwardText.setText("");
                            }
                        };

                        handlerRight.postDelayed(runnableRight, 1000);
                    }
                } else {
                    if (simpleExoPlayer.isPlaying()) {
                        simpleExoPlayer.pause();
                    } else {
                        simpleExoPlayer.play();
                    }
                }
            }
            return false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}