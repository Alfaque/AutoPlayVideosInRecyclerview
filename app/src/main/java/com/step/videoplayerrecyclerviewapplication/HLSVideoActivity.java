package com.step.videoplayerrecyclerviewapplication;

import static pgc.elarn.pgcelearn.controller.utilities.ExtensionsKt.checkNetworkConnectivity;
import static pgc.elarn.pgcelearn.controller.utilities.ExtensionsKt.rorate_Clockwise;
import static pgc.elarn.pgcelearn.controller.utilities.ExtensionsKt.setAnaltyics;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Merlin;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import pgc.elarn.pgcelearn.controller.interfaces.ApiInterface;
import pgc.elarn.pgcelearn.controller.interfaces.NullCallback;
import pgc.elarn.pgcelearn.controller.networks.interfaces.APIInterface;
import pgc.elarn.pgcelearn.controller.networks.interfaces.HeaderInterceptor;
import pgc.elarn.pgcelearn.controller.utilities.AppConstantsKt;
import pgc.elarn.pgcelearn.controller.utilities.AppSingletons;
import pgc.elarn.pgcelearn.controller.utilities.ApplicationUtils;
import pgc.elarn.pgcelearn.controller.utilities.SharedPrefUtil;
import pgc.elarn.pgcelearn.databinding.ActivityTestingWebviewBinding;
import pgc.elarn.pgcelearn.model.AskQuestion.QuestionData;
import pgc.elarn.pgcelearn.model.AskQuestion.SendQuestionData;
import pgc.elarn.pgcelearn.model.SendVideoQuestionBody;
import pgc.elarn.pgcelearn.model.elearn.UserInfoModel;
import pgc.elarn.pgcelearn.model.personal.PersonalInfoData;
import pgc.elarn.pgcelearn.model.video.GetVideoTopicsModelItem;
import pgc.elarn.pgcelearn.view.ComplexPreferences;
import pgc.elarn.pgcelearn.view.activities.NewELUserMicro.Topic;
import pgc.elarn.pgcelearn.view.activities.NewELUserMicro.VideoTopic;
import pgc.elarn.pgcelearn.view.adapters.ElearnAdapters.YoutubeApiAdapter2;
import pgc.elarn.pgcelearn.view.dialogs.CustomAlertDialog;
import pgc.elarn.pgcelearn.view.el_fragments.Sender;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MUSA kHAN on 30-Apr-20.
 */

/*
*     implementation 'com.google.android.exoplayer:exoplayer:2.14.0'
* for hls videos
 * */

public class HLSVideoActivity extends AppCompatActivity implements View.OnClickListener, PlayerControlView.VisibilityListener, Player.EventListener, ExoSettingDialog.OnSettingSelected, YoutubeApiAdapter2.OnQuizClicked {


    private int VIDEO_QUALITY = 0;
    private float PLAY_BACK_SPEED = 1F;
    private Boolean isShowingTrackSelectionDialog = false;
    String[] speed = {};
    String qualityArray[] = {
    };

    private String currentTopic = "";
    private boolean isTeacherFound = false;
    QuestionData.Data.Data currentTeacherModel;
    private static final String TAG = "ELVideoActivity";
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    public String VIDEO_ID = "";
    private ArrayList<VideoTopic> list;
    private ArrayList<Topic> topicList = new ArrayList();
    private APIInterface apiInterface;


//    private ApiServicePGC staging;

    private Retrofit retrofit;
    private YoutubeApiAdapter2 adapter;
    private Toolbar toolbar;
    private Boolean intializePlayerFirstTime = false;
    private UserInfoModel userInfoModel;
    private String LINK = null;
    private ProgressBar progress;
    private ImageView round_img;
    private Context context;
    private Merlin merlin;
    private SharedPreferences sharedPreferences;
    private String topicId;
    private boolean firstTimeloading = true;
    private int currentVideoDuration = 6000, currentDurationFromError = 0;
    private LinearLayoutManager layoutManagerCustom;
    private YoutubeApiAdapter2.videoUrlCallBack videoLinkUrlCallback;
    private ImageView rewind, forward;
    private int lastSelectedCardPos = 0;
    private boolean wentBackground = false;


    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";
    public static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private PlayerView playerView;
    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private TrackGroupArray lastSeenTrackGroupArray;
    private PersonalInfoData loginValue = null;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;
    private CoordinatorLayout controlView;
    private ImageButton retryButton;
    private LinearLayout centerControl;
    private boolean mExoPlayerFullscreen = false;
    private Dialog mFullScreenDialog;
    private ImageView mFullScreenIcon;
    private SharedPrefUtil sharedPrefUtils = new SharedPrefUtil();
    private boolean isUserStudent = true;
    private ActivityTestingWebviewBinding binding;

    CustomAlertDialog customAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Fresco.initialize(this);
        try {
            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(
                    getApplicationContext(), "cp",
                    Context.MODE_PRIVATE
            );

            loginValue = complexPreferences.getObject("login_data", PersonalInfoData.class);

        } catch (Exception Ev) {


        }

        customAlertDialog = new CustomAlertDialog(ELVideoActivity.this);

        speed = getResources().getStringArray(R.array.play_back_array);
        qualityArray = getResources().getStringArray(R.array.quality_array);


        dataSourceFactory = buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing_webview);


        isUserStudent = sharedPrefUtils.getBooleanValue(this, "IS_USER_STUDENT");

        if (isUserStudent) {
            binding.questionLayout.setVisibility(View.VISIBLE);

        } else {
            binding.questionLayout.setVisibility(View.GONE);

        }


//        staging = ApiServicePGC.Companion.create2();
//        staging = ApiServicePGC.Companion.create5();// for staging
        getData();

        try {
            View rootView = findViewById(R.id.root);
            rootView.setOnClickListener(this);
            setupConnections(savedInstanceState);
        } catch (Exception e) {
        }

        binding.sunmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // submit question
                submitQuestion();


            }
        });


    }

    GetVideoTopicsModelItem model;
    String subjectName = "";

    private void getData() {

        Intent intent = getIntent();


//        if (intent.hasExtra("GetVideoTopicsModelItem")) {
//
//            String data = intent.getStringExtra("GetVideoTopicsModelItem");
//            model = new Gson().fromJson(data, GetVideoTopicsModelItem.class);
//            subjectName = getIntent().getStringExtra("sub_name");
//
//
//            Log.d(TAG, "getData: ");
////            getChapterTopicsSecure();
//
//
//        }

        VIDEO_ID = intent.getStringExtra("topicId");
        intent.getStringExtra("chapterName");
        subjectName = intent.getStringExtra("sub_name");
        userId = intent.getStringExtra("userId");

        Log.d(TAG, "getData: ");
    }


    private void submitQuestion() {
        if (TextUtils.isEmpty(binding.questionEdititext.getText().toString())) {
            Toast.makeText(this, getString(R.string.question), Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.questionEdititext.getText().toString().length() < 15) {
            Toast.makeText(this, getString(R.string.question), Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkNetworkConnectivity(this)) {
//            loader ?.visibility = View.VISIBLE
            if (isTeacherFound) {

                try {

                    SendVideoQuestionBody body = new SendVideoQuestionBody("",
                            "",
                            loginValue.getData().get(0).getRollNo(),
                            currentTeacherModel.getTeacher_ID(),
                            binding.questionEdititext.getText().toString(),
                            "",
                            currentTeacherModel.getSubject_ID(),
                            loginValue.getData().get(0).getClass_section_id(),
                            "S",
                            currentTopic,
                            "PGC");
//                    );

                    Log.d(TAG, "submitQuestion: ");


                    ApiInterface apiInterface = ApiInterface.Companion.create();

                    binding.loader2.setVisibility(View.VISIBLE);


                    Call<SendQuestionData> call = apiInterface.Send_QuestionEX("send_questionEx", body);
                    call.enqueue(new Callback<SendQuestionData>() {
                        @Override
                        public void onResponse(Call<SendQuestionData> call, Response<SendQuestionData> response) {
                            binding.loader2.setVisibility(View.GONE);
                            Log.d(TAG, "onResponse: ");
                            if (response.isSuccessful() && response.body() != null && response.code() == 200) {

                                SendQuestionData obj = response.body();
                                if (obj.getData().getStatus().equals("900")) {

                                    Log.d(TAG, "onResponse: ");
                                    Toast.makeText(ELVideoActivity.this, getString(R.string.question_sent), Toast.LENGTH_SHORT).show();
                                    binding.questionEdititext.setText("");

                                } else {

                                    Log.d(TAG, "onResponse: ");
                                    Toast.makeText(ELVideoActivity.this, "Something went Wrong. Try again", Toast.LENGTH_SHORT).show();

                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<SendQuestionData> call, Throwable t) {
                            binding.loader2.setVisibility(View.GONE);
                            Log.d(TAG, "onFailure: ");
                            Toast.makeText(ELVideoActivity.this, "SomeThing Went Wrong. Try again", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (Exception Ex) {

                    binding.loader2.setVisibility(View.GONE);
                    Toast.makeText(this, "something went wrong.", Toast.LENGTH_SHORT).show();


                }


            } else {
                Toast.makeText(this, "Teacher is unavailable", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Unable to populate subjects list : please check internet connection", Toast.LENGTH_SHORT).show();

        }
    }


    private DataSource.Factory buildDataSourceFactory() {

        String userAgent = Util.getUserAgent(this, "ExoPlayerPGC");

        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null /* listener */,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
        );


        return new DefaultDataSourceFactory(
                this,
                null /* listener */,
                httpDataSourceFactory//
        );


    }

    private void checkIfDurationExistsInCache() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.contains(topicId)) {
            currentVideoDuration = sharedPreferences.getInt(topicId, 0);
        }
    }


    private void shouldShowProgressBar(boolean show) {

        if (progress == null) {
            return;
        }

        if (show) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    private void
    setupConnections(Bundle savedInstanceState) {
        context = this;
        merlin = new Merlin.Builder().withAllCallbacks().build(this);
        toolbar = (Toolbar) findViewById(R.id.pgc_videos);
        userInfoModel = AppSingletons.getUserInfo();
        progress = (ProgressBar) findViewById(R.id.progressBar);
        round_img = (ImageView) findViewById(R.id.round_img);
        rorate_Clockwise(this, round_img);

        setToolbar();
        recyclerView = (RecyclerView) findViewById(R.id.rec);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManagerCustom = (LinearLayoutManager) recyclerView.getLayoutManager();

        shouldShowProgressBar(true);

        playerView = findViewById(R.id.player_view);
        playerView.setControllerVisibilityListener(this);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();
        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);

            VIDEO_QUALITY = savedInstanceState.getInt("VIDEO_QUALITY");
            PLAY_BACK_SPEED = savedInstanceState.getFloat("PLAY_BACK_SPEED");


        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }


//        binding.ratingBar.setVisibility(View.VISIBLE);
//        binding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//
//
//                if (fromUser) {
//                    upDateRating((int) rating, currentVideoTopic);
//                }
//
//            }
//        });


        try {
            callApi();
            getTeacher();
        } catch (Exception e) {

        }

        merlin.registerDisconnectable(() -> Toast.makeText(context, "Internet Connection lost...", Toast.LENGTH_SHORT).show());
        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                callApi();

            }
        });

    }

//    private void upDateRating(int rating, VideoTopic videoTopic) {
//
//
//        Sender model = new Sender(userId + ":" + videoTopic.getTopicId() + ":" + rating);
//
//        Call<GetRatingModel> call = staging.saveRating("General/getratingSave",
//                model);
//
//        call.enqueue(new Callback<GetRatingModel>() {
//            @Override
//            public void onResponse(Call<GetRatingModel> call, Response<GetRatingModel> response) {
//
//
//                if (response.isSuccessful() && response.body() != null && response.code() == 200) {
//                    // data is available
//                    Log.d(TAG, "onResponse: ");
//                    if (response.body().isEmpty()) {
//                        //data list is empty
//                        Log.d(TAG, "onResponse: ");
//                    } else {
//                        //data is available
//                        Log.d(TAG, "onResponse: ");
////                        int rating = response.body().get(0).getRating();
//
//
//                    }
//
//
//                } else {
//                    // no data is available
//                    Log.d(TAG, "onResponse: ");
//                }
//                Log.d(TAG, "onResponse: ");
//            }
//
//            @Override
//            public void onFailure(Call<GetRatingModel> call, Throwable t) {
//                Log.d(TAG, "onResponse: ");
//            }
//        });
//
//
//    }

    private void getTeacher() {

        if (DashboardActivity.teacherList != null && DashboardActivity.teacherList.size() > 0) {

            for (QuestionData.Data.Data model : DashboardActivity.teacherList) {
                Log.d(TAG, "getTeacher: ");

                if (ApplicationUtils.removeSpaces(model.getSubject_Name().toLowerCase())
                        .equals(ApplicationUtils.removeSpaces(subjectName.toLowerCase())) ||
                        ApplicationUtils.removeSpaces(model.getSubject_Name().toLowerCase())
                                .contains(ApplicationUtils.removeSpaces(subjectName.toLowerCase()))) {
                    isTeacherFound = true;
                    currentTeacherModel = model;
                    Log.d(TAG, "getTeacher: ");
                    break;
                } else {
                    Log.d(TAG, "getTeacher: ");
                }


            }
        } else {
            isTeacherFound = false;

        }
        Log.d(TAG, "getTeacher: ");


    }


    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    String userId = "";

    private void callApi() {

//        userId = sharedPrefUtils.getSharedPrefValue(this, "userId");

//        VideoRequestModel model = new VideoRequestModel(userId + ":" + VIDEO_ID);
//        VIDEO_ID = getIntent().getStringExtra("video_id");
//        final String video_topic_id = getIntent().getStringExtra("video_topic_id");
        list = new ArrayList<>();
        ApplicationUtils.setAppContext(this);
        if (ApplicationUtils.isNetworkAvailable()) {
            apiInterface = getClient().create(APIInterface.class);


//            Call<List<Topic>> call = staging.getTopics("General/GetUserTopicsEn",
//                    new Sender(userId + ":" + VIDEO_ID));
            Call<List<Topic>> call = apiInterface.fetchUserTopics("api/General/GetUserTopics",
                    new Sender("8bb3a510-0c2c-4cc5-9b9c-662c7f7e3007:" + VIDEO_ID));
            call.enqueue(new Callback<List<Topic>>() {
                @Override
                public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
//                    mProgressDialog.dismiss();
                    if (response.body() != null) {
                        List<Topic> list = response.body();

                        topicList = (ArrayList<Topic>) list;// for getting topicswatchid

                        if (list != null) {
                            if (list.size() == 0) {
                                Toast.makeText(ELVideoActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                                shouldShowProgressBar(false);
                            } else {
                                fetchChapterTopics(list);


                                //get user previous result

//                                getUserResult();

                                //get user previous result

                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Topic>> call, Throwable t) {
                    Toast.makeText(ELVideoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }


            });
        } else {
//
            Toast.makeText(ELVideoActivity.this, "Check your internet Connection", Toast.LENGTH_SHORT).show();
            shouldShowProgressBar(false);

        }
    }

    private void fetchChapterTopics(List<Topic> upperList) {

        ApplicationUtils.setAppContext(this);
        if (ApplicationUtils.isNetworkAvailable()) {
            apiInterface = getClient().create(APIInterface.class);
//            Call<List<VideoTopic>> call = staging.getChapterTopicsSecure("General/GetChapterTopics", new Sender(id));


            Call<List<VideoTopic>> call = apiInterface.fetchTopics("api/General/GetChapterTopics", new Sender(upperList.get(0).getId()));
            call.enqueue(new Callback<List<VideoTopic>>() {
                @Override
                public void onResponse(Call<List<VideoTopic>> call, Response<List<VideoTopic>> response) {
//                    mProgressDialog.dismiss();
                    if (response.body() != null) {
                        list = (ArrayList<VideoTopic>) response.body();
                        try {
                            if (list != null) {
                                if (list.size() == 0) {
                                    Toast.makeText(ELVideoActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                                    shouldShowProgressBar(false);
                                } else {


                                    String videoLink = list.get(0).getVideoLink();
                                    currentTopic = list.get(0).getFullName();


                                    list.get(0).setPlaying(true);


                                    LINK = ApplicationUtils.getFormattedVideoLink(videoLink);
//                                    LINK = ApplicationUtils.getFormattedVideoLink123(videoLink);
                                    initializePlayer(true, 0);
                                    adapter = new YoutubeApiAdapter2(ELVideoActivity.this, list, upperList, "", ELVideoActivity.this);
                                    recyclerView.setAdapter(adapter);
                                    videoLinkUrlCallback = new YoutubeApiAdapter2.videoUrlCallBack() {
                                        @Override
                                        public void onComplete(String LINK, String topicId, int po) {


//                                            checkIfPreviousVideoIsWasRated(currentVideoPositionInList, currentVideoTopic);


                                            currentVideoPositionInList = po;
//                                            totalVideoProgress = 0;
                                            ELVideoActivity.this.LINK = LINK;


                                            wentBackground = false;
                                            try {
                                                if (player != null && po != -1 && list.size() > 0) {


                                                    //updateList

                                                    for (int i = 0; i <= list.size() - 1; i++) {
                                                        list.get(i).setPlaying(false);
                                                    }
                                                    list.get(po).setPlaying(true);
                                                    adapter.setData(list);

                                                    //updateList


                                                    setAnaltyics((Activity) context,
                                                            false,
                                                            loginValue.getData().get(0).getRollNo(),
                                                            stringForTime((int) player.getDuration()),
                                                            stringForTime((int) player.getCurrentPosition()),
                                                            list.get(lastSelectedCardPos).getTopicId(),
                                                            list.get(lastSelectedCardPos).getFullName());

                                                }

                                            } catch (Exception ev) {
                                                Log.d(TAG, "onComplete: ");
                                            }


                                            lastSelectedCardPos = po;
                                            startPosition = 0;
                                            startAutoPlay = true;
                                            releasePlayerForNextVideo();
                                            shouldShowProgressBar(true);
                                            initializePlayer(true, po);

                                            currentTopic = list.get(lastSelectedCardPos).getFullName();


                                        }
                                    };
                                    adapter.
                                            setCallback(videoLinkUrlCallback);


                                    adapter.notifyDataSetChanged();
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            showToast("Something went wrong. Please try again.");


                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<VideoTopic>> call, Throwable t) {
                    Toast.makeText(ELVideoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }


            });
        } else {
//
            Toast.makeText(ELVideoActivity.this, "Check your internet Connection", Toast.LENGTH_SHORT).show();
            shouldShowProgressBar(false);

        }
    }

//    private void checkIfPreviousVideoIsWasRated(int currentVideoPositionInList, VideoTopic currentVideoTopic) {
//
//        if (ifVideoWasRated) {
//            // if previous video was already rated
//
//        } else {
//            // if previous video was not rated
//
//            showRatingDialog(currentVideoTopic);
//
//        }
//
//    }

//    private void showRatingDialog(VideoTopic currentVideoTopic) {
//
//        RatingDialog dialog = new RatingDialog(ELVideoActivity.this, new RatingDialog.OnRatingSelected() {
//            @Override
//            public void onOnRatingSelectedListener(int rating) {
//
//                upDateRating(rating, currentVideoTopic);
//
//
//            }
//        });
//
//        dialog.show();
//
//    }

//    Boolean ifVideoWasRated = false;

//    private void getRatingOfCurrentVideo() {
//
//        Sender model = new Sender(userId + ":" + currentVideoTopic.getTopicId());
//
//        Call<GetRatingModel> call = staging.getRating("General/getrating",
//                model);
//
//        call.enqueue(new Callback<GetRatingModel>() {
//            @Override
//            public void onResponse(Call<GetRatingModel> call, Response<GetRatingModel> response) {
//
//                Log.d(TAG, "onResponse: ");
//                ifVideoWasRated = false;
//
//                if (response.isSuccessful() && response.body() != null && response.code() == 200) {
//                    // data is available
//                    Log.d(TAG, "onResponse: ");
//
//                    if (response.body().isEmpty()) {
//                        //data list is empty
//                        Log.d(TAG, "onResponse: ");
//                        binding.ratingBar.setRating(0);
//                    } else {
//                        //data is available
//
//                        int rating = response.body().get(0).getRating();
//                        binding.ratingBar.setRating(rating);
//                        ifVideoWasRated = true;
//                    }
//
//
//                } else {
//                    // no data is available
//                    Log.d(TAG, "onResponse: ");
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetRatingModel> call, Throwable t) {
//                Log.d(TAG, "onResponse: ");
//                ifVideoWasRated = false;
//            }
//        });
//
//
//    }

    private Double stringForTime(int timeMs) {
        try {
            Double totalSeconds = Double.valueOf(timeMs / 1000);

            return totalSeconds;
        } catch (Exception e) {
        }
        return 0.0;
    }

    private Retrofit getClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(new HeaderInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://my.pgc.edu/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player != null) {
            try {
                setAnaltyics((Activity) context, false, loginValue.getData().get(0).getRollNo(), stringForTime((int) player.getDuration()), stringForTime((int) player.getCurrentPosition()), list.get(lastSelectedCardPos).getTopicId(), list.get(lastSelectedCardPos).getFullName());

            } catch (Exception exception) {

            }
        }
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.seekTo(0);
        }
        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pgc_student_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_password:

                break;

            case R.id.menu:

                Intent intent = new Intent(this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                ActivityCompat.finishAffinity(this);
                break;

            case R.id.menuRateApp:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstantsKt.getRATE_APP_URL())));
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menuShare:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "EL by PGC");
                    String sAux = "";
                    sAux = sAux + AppConstantsKt.getSHARE_APP_URL();
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }

                break;
            case R.id.menuLogout:
                logout();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {

        try {
//            handler.removeCallbacks(updateProgressAction);

        } catch (Exception e) {

        }


        finish();

        super.onDestroy();
    }


    private void logout() {

        NullCallback callback = new NullCallback() {
            @Override
            public void onComplete() {

                TryingToLogout();

            }

            @Override
            public void onCancel() {

            }
        };
        ApplicationUtils.getAcknowledgeDialog(context, "PGC", callback);


    }


    private void TryingToLogout() {

        userInfoModel.setUserLoginState("");
        userInfoModel.setUserName("");
        ApplicationUtils.clearLogin(ELVideoActivity.this);
        Intent intent = new Intent(ELVideoActivity.this, DashboardActivity.class);
//                intent.putExtra("student_pgc","0");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        ActivityCompat.finishAffinity(ELVideoActivity.this);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //resume tasks needing this permission
        }
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(TAG, "onPlayerError: ");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }


    @Override
    public void onStart() {
        super.onStart();

        if (Util.SDK_INT > 23) {
            initializePlayer(false, 0);
            if (playerView != null) {
                playerView.onResume();
            }
            if (mFullScreenDialog != null && mExoPlayerFullscreen) {
                initFullscreenDialog();
                openFullscreenDialog();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        merlin.bind();

        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer(false, 0);
            if (playerView != null) {
                playerView.onResume();
            }
            if (mFullScreenDialog != null && mExoPlayerFullscreen) {
                initFullscreenDialog();
                openFullscreenDialog();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        wentBackground = true;
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            if (mFullScreenDialog != null && mExoPlayerFullscreen) {
                clearDialog();
            }
            releasePlayer();
        }
    }

    private void clearDialog() {
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        ((FrameLayout) findViewById(R.id.root)).addView(playerView);
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_expand));


    }

    @Override
    public void onStop() {
        super.onStop();
        wentBackground = true;

        merlin.unbind();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            if (mFullScreenDialog != null && mExoPlayerFullscreen) {
                clearDialog();
            }
            releasePlayer();
        }
    }

    private void setScreenOrientation(int screenOrientationPortrait) {
        setRequestedOrientation(screenOrientationPortrait);
    }

    int currentVideoPositionInList = 0;

    private void initializePlayer(final boolean shouldSendAnalytics, final int pos) {
        try {
            if (player == null && LINK != null) {


//                totalVideoProgress = 0;// for experiment
                // this method will get models for insert api

                getModels(pos);

                // this method will get models for insert api
//                getRatingOfCurrentVideo();


                Uri uri = Uri.parse(LINK);

                if (Util.maybeRequestReadExternalStoragePermission(/* activity= */ this, uri)) {
                    // The player will be reinitialized if the permission is granted.
                    return;
                }


                lastSeenTrackGroupArray = null;

                trackSelector = new DefaultTrackSelector(this);
                player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();


                player.addListener(new PlayerEventListener());
                player.setPlayWhenReady(startAutoPlay);
                playerView.setPlayer(player);
                mediaSource = buildMediaSource(uri);


                if (mediaSource == null) {

                    showToast("TimeOut Error. Try again");

                    finish();


                }


            }

            if (player != null) {

                boolean haveStartPosition = startWindow != C.INDEX_UNSET;
                if (haveStartPosition) {
                    player.seekTo(startWindow, startPosition);
                }

                initCustomController();
                initQualityDialog();
                initFullscreenDialog();
                player.prepare(mediaSource, !haveStartPosition, false);


                String medium = getString(R.string.medium_quality);

                try {
                    setVideoQuality(VIDEO_QUALITY);
                    setPlayBackSpeed(PLAY_BACK_SPEED);

                    //                setVideoQuality(Integer.parseInt(medium));

                } catch (Exception e) {
                    Log.d(TAG, "initializePlayer: ");
                }


            }

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (player != null && shouldSendAnalytics) {
                        try {
                            setAnaltyics((Activity) context, false, loginValue.getData().get(0).getRollNo(), stringForTime((int) player.getDuration()), stringForTime((int) player.getCurrentPosition()), list.get(pos).getTopicId(), list.get(pos).getFullName());

                        } catch (Exception exception) {

                        }
                    }
                }
            }, 500);
        } catch (Exception e) {

            showToast("Something went wrong. Please try again.");

            finish();
        }
    }

//    VideoTopic currentVideoTopic;// for tpic id
//    Topic currenttopicWactch;

    private void getModels(int pos) {
        try {
//            currentVideoTopic = list.get(pos);
//            currenttopicWactch = topicList.get(pos);
        } catch (Exception e) {

        }
    }

    private void setVideoQuality(int quality) {


        DefaultTrackSelector.ParametersBuilder parameters = trackSelector.buildUponParameters()
                .setForceHighestSupportedBitrate(true);


        if (quality > 0) {
            parameters.setMaxVideoBitrate(quality);

        } else {

            int q = Integer.parseInt(getString(R.string.low_quality));
            parameters.setMaxVideoBitrate(q);


        }

        trackSelector.setParameters(parameters.build());

    }

    ExoSettingDialog exoSettingDialog;

    private void initQualityDialog() {

        exoSettingDialog = new ExoSettingDialog(
                this,
                qualityArray,
                VIDEO_QUALITY,
                speed,
                PLAY_BACK_SPEED,
                this
        );

    }

//    private final Runnable updateProgressAction = this::updateCustomProgressBar;
//
//    Handler handler = new Handler();


//    private void updateCustomProgressBar() {
//        try {
//            if (customSeekbar != null) {
//
//                long lastPosition = 0;
//
//
//                long duration = player == null ? 0 : player.getDuration();
//                long position = player == null ? 0 : player.getCurrentPosition();
////                if (!dragging) {
//                customSeekbar.setMax((int) duration);
//
//                customSeekbar.setProgress((int) position);
////                    customSeekbar.setProgress(progressBarValue(position));
////                }
//
//
////                int obj = Long.compare(lastPosition, position);
////                if (obj == 0) {
////                    // both values are equal
////                    Log.d(TAG, "checkForUpdateTime: both values are equal");
////                } else {
////                    Log.d(TAG, "checkForUpdateTime: lastPosition: " + lastPosition + "- position: " + position);
////
////                    lastPosition = position;
//
//                if (player.isPlaying()) {
//                    Log.d(TAG, "checkForUpdateTime: video playing");
//                    checkForUpdateTime();
//                } else {
//                    Log.d(TAG, "checkForUpdateTime: video is not playing");
//                }
//
////                checkForUpdateTime();
//
//
////                switch (player.getPlaybackState()) {
////
////                    case Player.STATE_BUFFERING:
////                        Log.d(TAG, "updateCustomProgressBar: STATE_BUFFERING");
////                        break;
////                    case Player.STATE_ENDED:
////                        Log.d(TAG, "updateCustomProgressBar: STATE_ENDED");
////                        break;
////                    case Player.STATE_READY:
////                        Log.d(TAG, "updateCustomProgressBar: STATE_READY");
////                        break;
////                    case Player.STATE_IDLE:
////                        Log.d(TAG, "updateCustomProgressBar: STATE_IDLE");
////                        break;
////                }
//
//
////                }
////                else if (obj > 0) {
//////                    lastPosition is greater then position
////
////                } else {
//////                    lastPosition is smaller then position
////
////                }
//
//
//                long bufferedPosition = player == null ? 0 : player.getBufferedPosition();
//                customSeekbar.setSecondaryProgress((int) bufferedPosition);
////                customSeekbar.setSecondaryProgress(progressBarValue(bufferedPosition));
//                // Remove scheduled updates.
//                handler.removeCallbacks(updateProgressAction);
//                // Schedule an update if necessary.
////                int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
////                if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
////                    long delayMs;
////                    if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
////                        delayMs = 1000 - (position % 1000);
////                        if (delayMs < 200) {
////                            delayMs += 1000;
////                        }
////                    } else {
////                        delayMs = 1000;
////                    }
//////                    handler.postDelayed(updateProgressAction, delayMs);
////                }
//
//                handler.postDelayed(updateProgressAction, 1000);
//
//
//            } else {
//                Log.d(TAG, "updateCustomProgressBar: ");
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "updateCustomProgressBar: ");
//        }
//    }

//    private void checkForUpdateTime() {
//        try {
//
//            totalVideoProgress = totalVideoProgress + videoSpeed;
//
//            String text = "";
//
//
//            if (totalVideoSeconds > 0) {
//
//                if (totalVideoProgress >= totalVideoSeconds) {
//                    // 25% video has been watched
//                    text = "25% video watched";
//
//                    if (!isApiRunning && topicList.get(currentVideoPositionInList).getEnableMcq()) {
//                        // mcqs is already enabled
//
//                    } else {
//                        // mcqs is not enabled
//                        runUpdateApi();
//
//                    }
//
//
//                } else {
//                    text = "current:" + totalVideoProgress
//                            + "-totalSeconds:" + totalVideoSeconds;
////                            + " - videoName:" + topicList.get(currentVideoPositionInList).getFullName();
//
//                }
//                Log.d(TAG, "checkForUpdateTime: " + text);
//
//
//            } else {
//                Log.d(TAG, "checkForUpdateTime: ");
//            }
//
//
//        } catch (Exception e) {
//            Log.d(TAG, "checkForUpdateTime: ");
//        }
//
//
//    }


//    SeekBar customSeekbar = null;
//
//    double totalVideoProgress = 0;
//    long totalVideoSeconds = 0;
//
//    double videoSpeed = 1.0;

    private void initCustomController() {

        controlView = playerView.findViewById(R.id.custom_controller);
        LinearLayout exoGoBack = controlView.findViewById(R.id.exo_exit);
        retryButton = controlView.findViewById(R.id.retry);

        rewind = controlView.findViewById(R.id.exo_rewind);
        forward = controlView.findViewById(R.id.exo_fastforword);


        ImageView setting = controlView.findViewById(R.id.exo_track_selection_view);
        ImageView speedBtn = controlView.findViewById(R.id.exo_playback_speed);


//        DefaultTimeBar exo_progress = controlView.findViewById(R.id.exo_progress);
//        exo_progress.setVisibility(View.GONE);
//
//
//        SeekBar customSeekbar = controlView.findViewById(R.id.exo_seekbar);
//        customSeekbar.setVisibility(View.VISIBLE);
//
//        this.customSeekbar = customSeekbar;
//        customSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean isFromUser) {
//
//                try {
//                    if (isFromUser) {
//                        //when user moves seekbar
//                        player.seekTo(i);
//                    } else {
//                        //when user does not move seekbar
////                        totalVideoProgress = totalVideoProgress + videoSpeed;
//                    }
//
////                    totalVideoProgress = getVideoCurrentSeconds();
////                    totalVideoProgress = totalVideoProgress + videoSpeed;
//
////                    String text = "";
//
//
////                    if (totalVideoSeconds > 0) {
////
//////                        Log.d(TAG, "onProgressChanged: " + text);
//////                        if (totalVideoProgress >= 15) {
////                        if (totalVideoProgress >= totalVideoSeconds) {
////                            // 25% video has been watched
////                            text = "25% video watched";
////
////                            if (!isApiRunning && topicList.get(currentVideoPositionInList).getEnableMcq()) {
////                                // mcqs is already enabled
//////                                Log.d(TAG, "onProgressChanged: ");
////                            } else {
////                                // mcqs is not enabled
////                                runUpdateApi();
////
////                            }
////
////
////                        } else {
////                            text = "current:" + totalVideoProgress
////                                    + "-totalSeconds:" + totalVideoSeconds
////                                    + " - videoName:" + topicList.get(currentVideoPositionInList).getFullName();
////
////                        }
////                        Log.d(TAG, "onProgressChanged: " + text);
////
////
////                    } else {
////                        Log.d(TAG, "onProgressChanged: ");
////                    }
//
//
//                } catch (Exception e) {
//
//                }
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });


        ImageView exoSettingBtn = controlView.findViewById(R.id.setting_imageview);


        exoSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exoSettingDialog.show();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

            }
        });

        speedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ELVideoActivity.this);
                builder.setTitle("Set Speed");
                builder.setItems(speed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == 0) {
                            PlaybackParameters param = new PlaybackParameters(0.5f);
                            player.setPlaybackParameters(param);
                        }
                        if (which == 1) {
                            PlaybackParameters param = new PlaybackParameters(0.5f);
                            player.setPlaybackParameters(param);
                        }
                        if (which == 2) {
                            PlaybackParameters param = new PlaybackParameters(1f);
                            player.setPlaybackParameters(param);
                        }
                        if (which == 3) {

                            PlaybackParameters param = new PlaybackParameters(1.5f);
                            player.setPlaybackParameters(param);
                        }
                        if (which == 4) {
                            PlaybackParameters param = new PlaybackParameters(2f);
                            player.setPlaybackParameters(param);
                        }
                    }
                });


                builder.show();


            }
        });


        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null) {

                    long current = player.getCurrentPosition();
                    long duration = player.getDuration();

                    if (current < duration) {

                        player.seekTo(player.getContentPosition() + 15000);


                    }

                }
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null) {

                    long current = player.getCurrentPosition();

                    if (current > 0) {
                        player.seekTo(player.getContentPosition() - 15000);

                    } else {
                        player.seekTo(0);

                    }

                }
            }
        });

        centerControl = controlView.findViewById(R.id.center_control);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);

        exoGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerControl.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.GONE);
                player.seekTo(0);
                player.setPlayWhenReady(true);
            }
        });

        initFullscreenButton();
    }

    private long getVideoCurrentSeconds() {
        try {

            long timeMs = player.getCurrentPosition();
            timeMs = (timeMs / 1000);

            return timeMs;

        } catch (Exception exception) {
            return 0;
        }

    }

    /**
     * Initialize player full screen
     */
    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            @Override
            public void onBackPressed() {
                if (mExoPlayerFullscreen) {
                    setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    closeFullscreenDialog();
                }
            }
        };
    }

    /**
     * Open player full screen
     */
    private void openFullscreenDialog() {
        getSupportActionBar().hide();
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_collapse));
        mExoPlayerFullscreen = true;

        mFullScreenDialog.show();
    }

    /**
     * Close player full screen
     */
    private void closeFullscreenDialog() {
        getSupportActionBar().show();
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        ((FrameLayout) findViewById(R.id.root)).addView(playerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_expand));
    }

    /**
     * Full screen button click listener
     */
    private void initFullscreenButton() {
        RelativeLayout mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!mExoPlayerFullscreen) {
                    setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    openFullscreenDialog();
                } else {
                    setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    closeFullscreenDialog();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
        outState.putInt("VIDEO_QUALITY", VIDEO_QUALITY);
        outState.putFloat("PLAY_BACK_SPEED", PLAY_BACK_SPEED);


    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    @Override
    public void onQualitySelectedListener(@NonNull String quality) {

        VIDEO_QUALITY = Integer.parseInt(quality);

        setVideoQuality(Integer.parseInt(quality));


    }

    @Override
    public void onSpeedSelectedListener(float speed) {
        PLAY_BACK_SPEED = speed;

//        videoSpeed = speed;

        setPlayBackSpeed(speed);

    }

    private void setPlayBackSpeed(float speed) {

        PlaybackParameters param = new PlaybackParameters(speed);
        player.setPlaybackParameters(param);
    }

    @Override
    public void onQuizClickedListener(Topic topic, VideoTopic videoTopic, boolean isEnabled) {

//        if (isEnabled) {
//            // mcqs is added
//
//            Intent intent = new Intent(ELVideoActivity.this, VideoMCQSActivity.class);
//            intent.putExtra("topicId", videoTopic.getTopicId());
//            intent.putExtra("videoName", videoTopic.getFullName());
//            intent.putExtra("subjectName", subjectName);
//            intent.putExtra("userId", userId);
//
//            startActivity(intent);
//
//        } else {
//            // mcqs is not enabled
//
//            customAlertDialog.show();
//        }

    }

    private class PlayerEventListener implements Player.Listener {


        private boolean isBehindLiveWindow(ExoPlaybackException e) {
            if (e.type != ExoPlaybackException.TYPE_SOURCE) {
                return false;
            }
            Throwable cause = e.getSourceException();
            while (cause != null) {
                if (cause instanceof BehindLiveWindowException) {
                    return true;
                }
                cause = cause.getCause();
            }
            return false;
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                showRetryButton();
                progress.setVisibility(View.GONE);
            } else if (playbackState == Player.STATE_BUFFERING) {
                progress.setVisibility(View.VISIBLE);
            } else if (playbackState == Player.STATE_READY) {
                if (player != null && !wentBackground) {
                    try {
                        setAnaltyics((Activity) context, false, loginValue.getData().get(0).getRollNo(), stringForTime((int) player.getDuration()), stringForTime((int) player.getCurrentPosition()), list.get(lastSelectedCardPos).getTopicId(), list.get(lastSelectedCardPos).getFullName());

                        // this will get 25% playback time
//                        getTime();
                        // this will get 25% playback time


//                        updateCustomProgressBar();


                        //run api this will tell server if this video is already played or not
//                        runInsertApi();
                        //run api this will tell server if this video is already played or not


                    } catch (Exception exception) {
                        Log.d(TAG, "onPlaybackStateChanged: ");
                    }
                }


            }
        }


        private void showRetryButton() {
            centerControl.setVisibility(View.GONE);
            retryButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            if (player.getPlaybackError() != null) {
                // The user has performed a seek whilst in the error state. Update the resume position so
                // that if the user then retries, playback resumes from the position to which they seeked.
                updateStartPosition();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                initializePlayer(false, 0);
            } else {
                updateStartPosition();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            if (trackGroups != lastSeenTrackGroupArray) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_video);
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_audio);
                    }
                }
                lastSeenTrackGroupArray = trackGroups;
            }
        }

    }

    ArrayList<Topic> preivousResultList = new ArrayList();

//    private void getUserResult() {
//
//        if (ApplicationUtils.isNetworkAvailable()) {
//
//
//            Call<List<Topic>> call = staging.getTopics("General/GetUserTopicsEnEx",
//                    new Sender(userId + ":" + VIDEO_ID));
//            call.enqueue(new Callback<List<Topic>>() {
//                @Override
//                public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
//                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
//                        preivousResultList = (ArrayList<Topic>) response.body();
//
//
//                        if (preivousResultList != null) {
//
//                            Log.d(TAG, "onResponse: ");
//                            if (preivousResultList.size() == 0) {
//                                Log.d(TAG, "onResponse: ");
//                            } else {
//                                Log.d(TAG, "onResponse: ");
//                            }
//                        } else {
//                            Log.d(TAG, "onResponse: ");
//
//                        }
//
//
//                        //run api this will tell server if this video is already played or not
////                        runInsertApi();
//                        //run api this will tell server if this video is already played or not
//
//
//                    } else {
//                        Log.d(TAG, "onResponse: ");
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<Topic>> call, Throwable t) {
//                    Log.d(TAG, "onFailure: ");
//                }
//
//
//            });
//        } else {
//
//        }
//    }
//
//    private void getTime() {
//        try {
//            long timeMs = player.getDuration();
//            totalVideoSeconds = (timeMs / 1000);
//
//            totalVideoSeconds = (totalVideoSeconds * 25) / 100;
////            totalVideoSeconds = (totalVideoSeconds * 25) / 100;
//
//
//            Log.d(TAG, "getTime: ");
//
//
//        } catch (Exception e) {
//            Log.d(TAG, "getTime: ");
//        }
//
//    }


//    Boolean isApiRunning = false;

//    private void runUpdateApi() {
//
//        isApiRunning = true;
//
//        Call<GetUpdateEnableMcqsModel> call = staging.updateTopicWatchEnableMcq("General/UpdateTopicWatchEnableMcq",
//                new Sender(userId + ":" + currentVideoTopic.getTopicId()));
//        call.enqueue(new Callback<GetUpdateEnableMcqsModel>() {
//            @Override
//            public void onResponse(Call<GetUpdateEnableMcqsModel> call, Response<GetUpdateEnableMcqsModel> response) {
//
//                Log.d(TAG, "onResponse: ");
//                isApiRunning = false;
//
//                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
//                    Log.d(TAG, "onResponse: ");
//
//
//                    topicList.get(currentVideoPositionInList).setEnableMcq(true);
//                    Log.d(TAG, "onResponse: ");
//                    getModels(currentVideoPositionInList);
//
//
//                    adapter.setData2(topicList);
//
//
//                } else {
//                    Log.d(TAG, "onResponse: ");
//
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(Call<GetUpdateEnableMcqsModel> call, Throwable t) {
//                isApiRunning = false;
//                Log.d(TAG, "onFailure: ");
//            }
//        });
//
//    }

//    private void runInsertApi() {
//
//        String result = "";
//
//        for (int i = 0; i < preivousResultList.size() - 1; i++) {
//            if (preivousResultList.get(i).getId().equals(currenttopicWactch.getId())) {
//
//                Log.d(TAG, "runInsertApi: ");
//
//                if (preivousResultList.get(i).getResult() != null) {
//                    result = preivousResultList.get(i).getResult();
//                    break;
//                } else {
//                    result = "";
//                }
//
//
//            }
//
//        }
//
//
//        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//
//        InsertVideoRequestModel model = new InsertVideoRequestModel(new Val(
//
//                100,
//                currenttopicWactch.getEnableMcq(),
//                sdf2.format(timestamp),
//                100,
//                result,
//                currentVideoTopic.getTopicId(),
//                UUID.randomUUID().toString(),
//                userId
//        ));
//
//
//        Call<Val> call = staging.insertVideo("TopicsWatched/Insert", model);
//
//        call.enqueue(new Callback<Val>() {
//            @Override
//            public void onResponse(Call<Val> call, Response<Val> response) {
//                Log.d(TAG, "onResponse: ");
//            }
//
//            @Override
//            public void onFailure(Call<Val> call, Throwable t) {
//                Log.d(TAG, "onFailure: ");
//            }
//        });
//
//    }


    @Override
    public void onPlaybackStateChanged(int state) {
        switch (state) {

            case Player.STATE_BUFFERING:
                progress.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                progress.setVisibility(View.GONE);
                break;
            case Player.STATE_IDLE:
                Log.d(TAG, "onPlaybackStateChanged: ");
                break;
            case Player.STATE_READY:
                Log.d(TAG, "onPlaybackStateChanged: ");
                break;
            default:
                break;
        }

    }

    class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {


        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException throwable) {
            String error = "Something Happened try again!";

            if (throwable.type == ExoPlaybackException.TYPE_SOURCE) {
                error = "Could not read the stream.";

            } else if (throwable.type == ExoPlaybackException.TYPE_RENDERER) {
                error = "Video Rendering error. Try Again ";

            } else if (throwable.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                error = "UnExpected Error. Try Again ";

            }
            return Pair.create(0, "");


        }
    }

    private void releasePlayerForNextVideo() {
        if (player != null) {
            player.release();
            player = null;
            mediaSource = null;
            trackSelector = null;
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            updateTrackSelectorParameters();
            updateStartPosition();
            player.release();
            player = null;
            mediaSource = null;
            trackSelector = null;
        }
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {

            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    private MediaSource buildMediaSource(Uri uri) {

//        return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);


        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(this, "Player"),
                null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true
        );

        DataSource.Factory dataSourceFactory = new
                DefaultDataSourceFactory(this, null, httpDataSourceFactory);


//        DefaultHttpDataSourceFactory
//        val dataSourceFactory: DataSource.Factory =
//            DefaultDataSourceFactory(this, "exoplayer-codelab")
//        return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);


//        return new
//                HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        @C.ContentType int type = Util.inferContentType(uri, null);

        if (type == C.TYPE_DASH) {

            return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        } else if (type == C.TYPE_SS) {

            return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        } else if (type == C.TYPE_HLS) {

            return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        } else {

            return null;

        }


    }
}