package com.daily_smart.news_app.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daily_smart.news_app.Adapters.NewsCommentsAdapter;
import com.daily_smart.news_app.BuildConfig;
import com.daily_smart.news_app.CustomViews.CircleImageView;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
import com.daily_smart.news_app.Models.NewsCommentsModel;
import com.daily_smart.news_app.Models.NewsItemModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Utilities.TimeAgo;
import com.daily_smart.news_app.Volley.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private String title = Config.EMPTY_STRING;
    private String newsIdDetails = Config.EMPTY_STRING;
    private String newsTitle = Config.EMPTY_STRING;
    private TextView txtNewsTitle, txtNewsDescription, txtNewsPostedName, txtNewsFollow, txtTimeAgo,
            txtNewsPostedDistrict, txtShareContentFullImageNewsDetails, txtShareContentDesc;
    private ImageView ivNewsImage, ivNewsComments, ivNewsAddFavourites, ivNewsDetailsShare,
            ivSpeaker, ivPlayVideo, ivSpeakerStop, ivCommentsClose, ivPauseVideo, ivFullImageNewsDetails, ivThumbnail, ivHome;
    private LinearLayout layoutNewsItemParent, layoutBottom, layoutBottomImage, newsDetailsBottomAppName;
    private CardView card_view_for_image;
    private CircleImageView ivPostedNewsReporterImage;
    private FrameLayout layoutYouTube, layoutNewsDetailsVideo, layoutFullImageNewsDetails, layoutViewPager;
    private VideoView newDetailsVideoView;
    private WebView webView;
    private ProgressBarDialog progressBarDialog = null;
    private ArrayList<NewsItemModel> newsItemModelArrayList = new ArrayList<>();
    private NewsItemModel newsItemModel = null;
    private BottomSheetDialog bottomSheetDialog;
    private TextView txtCommentsCount, txtNoComments, txtNewsTitleComments, txtFullImageNewsDetailsNewsTitle;
    private EditText etComments;
    private LinearLayout layoutSendComments, newsDetailsLayoutBottomAppName, newsDetailsBottomAppNameViewPager;
    private View viewBottomSheetDialog;
    private ShareData shareData;
    private RecyclerView rvComments;
    private String userId = Config.EMPTY_STRING;
    private ArrayList<NewsCommentsModel> newsCommentsModelArrayList = new ArrayList<>();
    private NewsCommentsAdapter newsCommentsAdapter;
    private TextToSpeech textToSpeech;
    private FloatingActionButton fab, fabHome, fabViewPager;
    private String videoId = null;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 100;
    private CustomPagerAdapter customPagerAdapter;
    private ViewPager newsDetailsViewPager;
    private TextView[] dots;
    private LinearLayout layoutDots;
    private String favourited = "0";
    private YouTubePlayerView youtube_player_view;
    private Lifecycle lifecycle;
    private String youtubeLink = Config.EMPTY_STRING;
    private String videoLink = Config.EMPTY_STRING;
    private Uri uri = null;
    private String Status = Config.EMPTY_STRING;
    private TextView txtShareContentViewPagerNewsDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_details);
        if (getIntent().hasExtra("NewsId")) {
            newsIdDetails = getIntent().getStringExtra("NewsId");
        }
        if (getIntent().hasExtra("title")) {
            title = getIntent().getStringExtra("title");
        }
        uri = getIntent().getData();
        if (uri != null) {
            newsIdDetails = uri.getQueryParameter("nid");
        }
        progressBarDialog = new ProgressBarDialog(NewsDetailsActivity.this);
        shareData = new ShareData(this);
        if (shareData.getCustomerId().isEmpty()) {
            userId = "0";
        } else {
            userId = shareData.getCustomerId();
        }
        txtNewsTitle = findViewById(R.id.txtNewsTitle);
        txtNewsDescription = findViewById(R.id.txtNewsDescription);
        txtNewsPostedName = findViewById(R.id.txtNewsPostedName);
        ivNewsImage = findViewById(R.id.ivNewsImage);
        ivNewsComments = findViewById(R.id.ivNewsComments);
        ivNewsAddFavourites = findViewById(R.id.ivNewsAddFavourites);
        ivSpeaker = findViewById(R.id.ivSpeaker);
        ivSpeakerStop = findViewById(R.id.ivSpeakerStop);
        layoutNewsItemParent = findViewById(R.id.layoutNewsItemParent);
        layoutBottom = findViewById(R.id.layoutBottom);
        layoutBottomImage = findViewById(R.id.layoutBottomImage);
        txtNewsFollow = findViewById(R.id.txtNewsFollow);
        txtTimeAgo = findViewById(R.id.txtTimeAgo);
        ivNewsDetailsShare = findViewById(R.id.ivNewsDetailsShare);
        card_view_for_image = findViewById(R.id.card_view_for_image);
        ivPlayVideo = findViewById(R.id.ivPlayVideo);
        ivPauseVideo = findViewById(R.id.ivPauseVideo);
        layoutYouTube = findViewById(R.id.layoutYouTube);
        layoutNewsDetailsVideo = findViewById(R.id.layoutNewsDetailsVideo);
        layoutFullImageNewsDetails = findViewById(R.id.layoutFullImageNewsDetails);
        layoutViewPager = findViewById(R.id.layoutViewPager);
        ivFullImageNewsDetails = findViewById(R.id.ivFullImageNewsDetails);
        ivThumbnail = findViewById(R.id.ivThumbnail);
        ivHome = findViewById(R.id.ivHome);
        txtFullImageNewsDetailsNewsTitle = findViewById(R.id.txtFullImageNewsDetailsNewsTitle);
        newsDetailsBottomAppName = findViewById(R.id.newsDetailsBottomAppName);
        newDetailsVideoView = findViewById(R.id.newDetailsVideoView);
        txtNewsPostedDistrict = findViewById(R.id.txtNewsPostedDistrict);
        ivPostedNewsReporterImage = findViewById(R.id.ivPostedNewsReporterImage);
        newsDetailsLayoutBottomAppName = findViewById(R.id.newsDetailsLayoutBottomAppName);
        newsDetailsBottomAppNameViewPager = findViewById(R.id.newsDetailsBottomAppNameViewPager);
        txtShareContentDesc = findViewById(R.id.txtShareContentDesc);
        youtube_player_view = findViewById(R.id.youtube_player_view);
        fab = findViewById(R.id.fab);
        fabHome = findViewById(R.id.fabHome);
        fabViewPager = findViewById(R.id.fabViewPager);
        webView = findViewById(R.id.webView);
        txtShareContentViewPagerNewsDetails = findViewById(R.id.txtShareContentViewPagerNewsDetails);
        txtShareContentFullImageNewsDetails = findViewById(R.id.txtShareContentFullImageNewsDetails);
        txtShareContentDesc = findViewById(R.id.txtShareContentDesc);

        getLifecycle().addObserver(youtube_player_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        displayNewsDetails(newsIdDetails);

        if (getIntent().hasExtra("Status")) {
            Status = getIntent().getStringExtra("Status");
            Log.e("Status", Status);
            if (Status.equalsIgnoreCase("1") && !userId.equalsIgnoreCase("0")) {
                ivNewsDetailsShare.setVisibility(View.VISIBLE);
            } else {
                ivNewsDetailsShare.setVisibility(View.GONE);
            }
        }
        ivNewsComments.setOnClickListener(this);
        ivNewsAddFavourites.setOnClickListener(this);
        txtNewsFollow.setOnClickListener(this);
        ivSpeaker.setOnClickListener(this);
        ivSpeakerStop.setOnClickListener(this);
        fab.setOnClickListener(this);
        fabHome.setOnClickListener(this);
        fabViewPager.setOnClickListener(this);
        ivNewsDetailsShare.setOnClickListener(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "ramabhadra_regular.ttf");
        txtNewsTitle.setTypeface(typeface);
        txtShareContentDesc.setTypeface(typeface);
        txtFullImageNewsDetailsNewsTitle.setTypeface(typeface);
        txtShareContentFullImageNewsDetails.setTypeface(typeface);
        Typeface typefaceDesc = Typeface.createFromAsset(getAssets(), "mallanna_regular.ttf");
        txtNewsDescription.setTypeface(typefaceDesc);

        newsDetailsViewPager = findViewById(R.id.newsDetailsViewPager);
        layoutDots = findViewById(R.id.layoutDots);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("te", "IN");
                    int result = textToSpeech.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    }
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });
    }

    private void displayNewsDetails(final String newsId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.NEWS_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(NewsDetailsActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("news_data");
                                newsItemModelArrayList = NewsItemModel.fromJson(jsonArray);
                                newsItemModel = newsItemModelArrayList.get(0);
                                newsIdDetails = newsItemModel.getNews_id();
                                txtNewsTitle.setText(newsItemModel.getPostTitleTe());
                                txtFullImageNewsDetailsNewsTitle.setText(newsItemModel.getPostTitleTe());

                                TimeAgo timeAgo = new TimeAgo();
                                String timeAgoString = null;
                                timeAgoString = timeAgo.covertTimeToText(newsItemModel.getPostingDate());
                                txtTimeAgo.setText(timeAgoString);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Spannable spannable = (Spannable) Html.fromHtml(newsItemModel.getPostDetailsTe(), Html.FROM_HTML_MODE_COMPACT);
                                    for (URLSpan u : spannable.getSpans(0, spannable.length(), URLSpan.class)) {
                                        spannable.setSpan(new UnderlineSpan() {
                                            public void updateDrawState(TextPaint tp) {
                                                tp.setUnderlineText(false);
                                            }
                                        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0);
                                    }
                                    txtNewsDescription.setText(spannable);
                                    txtNewsDescription.setMovementMethod(LinkMovementMethod.getInstance());
                                } else {
                                    Spannable spannable = (Spannable) Html.fromHtml(newsItemModel.getPostDetailsTe());
                                    for (URLSpan u : spannable.getSpans(0, spannable.length(), URLSpan.class)) {
                                        spannable.setSpan(new UnderlineSpan() {
                                            public void updateDrawState(TextPaint tp) {
                                                tp.setUnderlineText(false);
                                            }
                                        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0);
                                    }
                                    txtNewsDescription.setText(spannable);
                                    txtNewsDescription.setMovementMethod(LinkMovementMethod.getInstance());
                                }
                                newsTitle = newsItemModel.getPostTitleTe();
                                try {
                                    if (!newsItemModel.getPostImages().get(0).getPostImage().isEmpty()) {
                                        Picasso.get()
                                                .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(0).getPostImage())
                                                .placeholder(R.drawable.default_placeholder)
                                                .error(R.drawable.default_placeholder)
                                                .into(ivNewsImage);
                                    }

                                    Picasso.get()
                                            .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(0).getPostImage())
                                            .placeholder(R.drawable.default_placeholder_fullimage)
                                            .error(R.drawable.default_placeholder_fullimage)
                                            .into(ivFullImageNewsDetails);

                                } catch (Exception e) {
                                    Log.e("Exception", e.getMessage());
                                    e.printStackTrace();
                                }
                                Picasso.get()
                                        .load(Config.PROFILE_IMAGE_URL + newsItemModel.getProfile_pic())
                                        .placeholder(R.drawable.defult_profile)
                                        .error(R.drawable.defult_profile)
                                        .into(ivPostedNewsReporterImage);

                                if (newsItemModel.getName().equalsIgnoreCase("Admin")) {
                                    txtNewsFollow.setVisibility(View.GONE);
                                    timeAgoString = timeAgo.covertTimeToText(newsItemModel.getPostingDate());
                                    /*if (newsItemModel.getScheduleDate().isEmpty() || newsItemModel.getScheduleDate().equalsIgnoreCase("null")) {
                                        timeAgoString = timeAgo.covertTimeToText(newsItemModel.getPostingDate());
                                    } else {
                                        timeAgoString = timeAgo.covertTimeToText(newsItemModel.getScheduleDate());
                                    }*/
                                    txtNewsPostedName.setText(timeAgoString);
                                    ivPostedNewsReporterImage.setVisibility(View.GONE);
                                    txtNewsFollow.setVisibility(View.GONE);
                                    txtNewsPostedDistrict.setVisibility(View.GONE);
                                } else {
                                    if (userId.equalsIgnoreCase(newsItemModel.getUser_id())) {
                                        txtNewsFollow.setVisibility(View.GONE);
                                    } else {
                                        txtNewsFollow.setVisibility(View.VISIBLE);
                                    }
                                    txtNewsPostedName.setText(newsItemModel.getName());
                                }

                                if (newsItemModel.getDistrictNameTe().isEmpty() || newsItemModel.getDistrictNameTe().equalsIgnoreCase("none")) {
                                    txtNewsPostedDistrict.setVisibility(View.GONE);
                                } else {
                                    txtNewsPostedDistrict.setVisibility(View.VISIBLE);
                                }

                                txtNewsPostedDistrict.setText(newsItemModel.getDistrictNameTe());
                                Typeface typeface = Typeface.createFromAsset(getAssets(), "ramabhadra_regular.ttf");
                                txtNewsPostedDistrict.setTypeface(typeface);

                                if (newsItemModel.getIs_fav().equalsIgnoreCase("1")) {
                                    favourited = newsItemModel.getIs_fav();
                                    ivNewsAddFavourites.setBackgroundResource(R.drawable.favorite_filled_blue);
                                } else {
                                    favourited = newsItemModel.getIs_fav();
                                    ivNewsAddFavourites.setBackgroundResource(R.drawable.favorite_blue_border);
                                }
                                if (newsItemModel.getIs_followers().equalsIgnoreCase("1")) {
                                    txtNewsFollow.setTextColor(getResources().getColor(R.color.white));
                                    txtNewsFollow.setText(getResources().getString(R.string.following));
                                } else {
                                    txtNewsFollow.setTextColor(getResources().getColor(R.color.white));
                                    txtNewsFollow.setText(getResources().getString(R.string.follow));
                                }

                                if (!newsItemModel.getYoutube_link().isEmpty() && !newsItemModel.getYoutube_link().equalsIgnoreCase("0")) {
                                    layoutYouTube.setVisibility(View.VISIBLE);
                                    ivNewsImage.setVisibility(View.GONE);
                                    txtTimeAgo.setVisibility(View.GONE);
                                    youtubeLink = newsItemModel.getYoutube_link();

                                    String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
                                    Pattern compiledPattern = Pattern.compile(pattern,
                                            Pattern.CASE_INSENSITIVE);
                                    Matcher matcher = compiledPattern.matcher(newsItemModel.getYoutube_link());
                                    if (matcher.find()) {
                                        videoId = matcher.group(1);
                                    }
                                    youtube_player_view.setEnableAutomaticInitialization(false);
                                    youtube_player_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                        @Override
                                        public void onReady(YouTubePlayer youTubePlayer) {
                                            youTubePlayer.loadVideo(videoId, 0);
                                        }
                                    });

                                    webView.loadData("<iframe src=\"https://www.youtube.com/embed/" + videoId + "?rel=0&showinfo=0\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\" allowTransparency=\"true\" allowFullScreen=\"true\"></iframe>", "text/html", "utf-8");
                                } else {
                                    layoutYouTube.setVisibility(View.GONE);
                                    ivNewsImage.setVisibility(View.VISIBLE);
                                    txtTimeAgo.setVisibility(View.VISIBLE);
                                }
                                if (!newsItemModel.getPostVideo().isEmpty()) {
                                    videoLink = newsItemModel.getPostVideo();
                                    RequestOptions requestOptions = new RequestOptions();
                                    Glide.with(NewsDetailsActivity.this)
                                            .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostVideo())
                                            .apply(requestOptions)
                                            .thumbnail(Glide.with(NewsDetailsActivity.this).load(Config.NEWS_IMAGE_URL + newsItemModel.getPostVideo()))
                                            .into(ivThumbnail);

                                    layoutNewsDetailsVideo.setVisibility(View.VISIBLE);
                                    layoutYouTube.setVisibility(View.GONE);
                                    ivNewsImage.setVisibility(View.GONE);
                                    txtTimeAgo.setVisibility(View.GONE);
                                    ivSpeaker.setVisibility(View.GONE);

                                    final MediaController mediaController = new MediaController(NewsDetailsActivity.this);
                                    Uri uri = Uri.parse(Config.NEWS_IMAGE_URL + newsItemModel.getPostVideo());
                                    newDetailsVideoView.setVideoURI(uri);
                                    newDetailsVideoView.requestFocus();
                                    newDetailsVideoView.seekTo(2);
                                    ivPlayVideo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            newDetailsVideoView.start();
                                            ivThumbnail.setVisibility(View.GONE);
                                            ivPlayVideo.setVisibility(View.GONE);
                                            ivPauseVideo.setVisibility(View.GONE);
                                            mediaController.show();
                                            newDetailsVideoView.start();
                                        }
                                    });
                                    ivPauseVideo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ivPlayVideo.setVisibility(View.VISIBLE);
                                            ivPauseVideo.setVisibility(View.GONE);
                                            newDetailsVideoView.pause();
                                        }
                                    });

                                    newDetailsVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mediaPlayer) {

                                            float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
                                            float screenRatio = newDetailsVideoView.getWidth() / (float)
                                                    newDetailsVideoView.getHeight();
                                            float scaleX = videoRatio / screenRatio;
                                            if (scaleX >= 1f) {
                                                newDetailsVideoView.setScaleX(scaleX);
                                            } else {
                                                newDetailsVideoView.setScaleY(1f / scaleX);
                                            }

                                            mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                                @Override
                                                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                                                    newDetailsVideoView.seekTo(2);
                                                    mediaController.setAnchorView(newDetailsVideoView);
                                                    newDetailsVideoView.setMediaController(mediaController);
                                                    if (mediaPlayer.isPlaying()) {
                                                        Log.e("isPlaying", "isPlaying");
                                                    } else {
                                                        Log.e("isPause", "isPause");
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    newDetailsVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            ivPlayVideo.setVisibility(View.VISIBLE);
                                            ivThumbnail.setVisibility(View.VISIBLE);
                                            ivPauseVideo.setVisibility(View.GONE);
                                        }
                                    });
                                }
                                if (newsItemModel.getPostImages().size() == 1 && newsItemModel.getFullscreen().equalsIgnoreCase("1") && newsItemModel.getPostVideo().isEmpty() && newsItemModel.getYoutube_link().isEmpty()) {
                                    layoutFullImageNewsDetails.setVisibility(View.VISIBLE);
                                    layoutNewsItemParent.setVisibility(View.GONE);
                                    ivPostedNewsReporterImage.setVisibility(View.GONE);
                                    txtNewsFollow.setVisibility(View.GONE);
                                    txtNewsPostedDistrict.setVisibility(View.GONE);
                                    ivSpeaker.setVisibility(View.GONE);
                                }
                                if (newsItemModel.getPostImages().size() > 1) {
                                    layoutViewPager.setVisibility(View.VISIBLE);
                                    layoutFullImageNewsDetails.setVisibility(View.GONE);
                                    layoutNewsItemParent.setVisibility(View.GONE);
                                    ivPostedNewsReporterImage.setVisibility(View.GONE);
                                    txtNewsFollow.setVisibility(View.GONE);
                                    txtNewsPostedDistrict.setVisibility(View.GONE);
                                    ivSpeaker.setVisibility(View.GONE);

                                    customPagerAdapter = new CustomPagerAdapter(NewsDetailsActivity.this, newsItemModelArrayList);
                                    newsDetailsViewPager.setAdapter(customPagerAdapter);
                                    newsDetailsViewPager.setPageMargin(20);
                                    newsDetailsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                        @Override
                                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                        }

                                        @Override
                                        public void onPageSelected(int position) {
                                            addDot(position, layoutDots);
                                            Log.e("addDot", "addDot");
                                        }

                                        @Override
                                        public void onPageScrollStateChanged(int state) {

                                        }
                                    });
                                    addDot(0, layoutDots);
                                }

                            } catch (
                                    Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(NewsDetailsActivity.this);
                                Log.e("volleyError", volleyError.getMessage());
                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("news_id", newsId);
                    params.put("user_id", userId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(NewsDetailsActivity.this).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(NewsDetailsActivity.this);
        } catch (Exception ex) {
            //todo
        }
    }

    private class CustomPagerAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<NewsItemModel> newsItemModelArrayList;

        public CustomPagerAdapter(Context context, ArrayList<NewsItemModel> newsItemModelArrayList) {
            this.mContext = context;
            this.newsItemModelArrayList = newsItemModelArrayList;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.multi_image_view_pager, container, false);
            ImageView ivMultiImage = view.findViewById(R.id.ivMultiImage);
            Picasso.get()
                    .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(position).getPostImage())
                    .placeholder(R.drawable.default_placeholder_fullimage)
                    .error(R.drawable.default_placeholder_fullimage)
                    .into(ivMultiImage);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return newsItemModel.getPostImages().size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }
    }

    private void addDot(int currentPage, LinearLayout layoutDotsLocal) {
        try {
            dots = new TextView[newsItemModel.getPostImages().size()];
//            dots = new TextView[newsItemModel1.getPostImages().size()];
            Log.e("dots", "--" + dots.length);
            layoutDotsLocal.removeAllViews();
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(NewsDetailsActivity.this);
                dots[i].setText(Html.fromHtml("&#9673;"));
                dots[i].setTextSize(18);
                //set default dot color
                dots[i].setTextColor(getResources().getColor(R.color.white));
                layoutDotsLocal.addView(dots[i]);
            }
            if (dots.length > 0) {
                Log.e("currentPage", "--" + currentPage);
                dots[currentPage].setTextColor(getResources().getColor(R.color.red));
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }


    @Override
    public void onBackPressed() {
        if (uri == null && title.isEmpty()) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(NewsDetailsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivNewsComments:
                bottomSheetDialog = new BottomSheetDialog(NewsDetailsActivity.this);
                View sheetView = NewsDetailsActivity.this.getLayoutInflater().inflate(R.layout.news_comments_bottomsheet_dialog, null);
                txtCommentsCount = sheetView.findViewById(R.id.txtCommentsCount);
                txtNewsTitleComments = sheetView.findViewById(R.id.txtNewsTitle);
                rvComments = sheetView.findViewById(R.id.rvComments);
                etComments = sheetView.findViewById(R.id.etComments);
                ivCommentsClose = sheetView.findViewById(R.id.ivCommentsClose);
                viewBottomSheetDialog = sheetView.findViewById(R.id.viewBottomSheetDialog);
                txtNoComments = sheetView.findViewById(R.id.txtNoComments);
                layoutSendComments = sheetView.findViewById(R.id.layoutSendComments);
                txtNewsTitleComments.setText(newsItemModel.getPostTitleTe());
                txtCommentsCount.setText(getResources().getString(R.string.comments) + " ( " + 0 + " )");

                Typeface typeface = Typeface.createFromAsset(getAssets(), "ramabhadra_regular.ttf");
                txtNewsTitleComments.setTypeface(typeface);

                layoutSendComments.setOnClickListener(this);
                ivCommentsClose.setOnClickListener(this);
                if (!GeneralFunctions.isNetworkAvailable(NewsDetailsActivity.this)) {
                    GeneralFunctions.showToast(getResources().getString(R.string.no_internet), NewsDetailsActivity.this);
                } else {
                    etComments.setText("");
                    getNewsComments(newsIdDetails, rvComments);
                }
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                        setupFullHeight(bottomSheetDialog);
                    }
                });
                bottomSheetDialog.show();
                break;
            case R.id.layoutSendComments:
                if (!GeneralFunctions.isNetworkAvailable(NewsDetailsActivity.this)) {
                    GeneralFunctions.showToast(getResources().getString(R.string.no_internet), NewsDetailsActivity.this);
                } else {
                    if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                        Intent intent = new Intent(NewsDetailsActivity.this, LoginActivity.class);
                        intent.putExtra("DistrictId", shareData.getHomeDistrictId());
                        startActivity(intent);
                    } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                        Intent intent = new Intent(NewsDetailsActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                    } else {
                        if (etComments.getText().toString().trim().isEmpty()) {
                            GeneralFunctions.showToast("Please write comment", NewsDetailsActivity.this);
                            return;
                        } else {
                            postComments(userId, newsIdDetails, etComments.getText().toString().trim());
                        }
                    }
                }
                break;
            case R.id.ivNewsAddFavourites:
                if (favourited.equalsIgnoreCase("0")) {
                    if (!GeneralFunctions.isNetworkAvailable(NewsDetailsActivity.this)) {
                        GeneralFunctions.showToast(getResources().getString(R.string.no_internet), NewsDetailsActivity.this);
                    } else {
                        if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                            Intent intent = new Intent(NewsDetailsActivity.this, LoginActivity.class);
                            intent.putExtra("DistrictId", shareData.getHomeDistrictId());
                            startActivity(intent);
                        } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                            Intent intent = new Intent(NewsDetailsActivity.this, EditProfileActivity.class);
                            startActivity(intent);
                        } else {
                            addFavourites(userId, newsIdDetails);
                        }
                    }

                } else {
                    if (!GeneralFunctions.isNetworkAvailable(NewsDetailsActivity.this)) {
                        GeneralFunctions.showToast(getResources().getString(R.string.no_internet), NewsDetailsActivity.this);
                    } else {
                        if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                            Intent intent = new Intent(NewsDetailsActivity.this, LoginActivity.class);
                            intent.putExtra("DistrictId", shareData.getHomeDistrictId());
                            startActivity(intent);
                        } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                            Intent intent = new Intent(NewsDetailsActivity.this, EditProfileActivity.class);
                            startActivity(intent);
                        } else {
                            removeFavourites(newsItemModel.getIs_fav_id());
                        }
                    }
                }
                break;
            case R.id.txtNewsFollow:
                if (newsItemModel.getIs_followers().equalsIgnoreCase("0")) {
                    if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                        Intent intent = new Intent(NewsDetailsActivity.this, LoginActivity.class);
                        intent.putExtra("DistrictId", shareData.getHomeDistrictId());
                        startActivity(intent);
                    } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                        Intent intent = new Intent(NewsDetailsActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                    } else {
                        if (!GeneralFunctions.isNetworkAvailable(NewsDetailsActivity.this)) {
                            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), NewsDetailsActivity.this);
                        } else {
                            txtNewsFollow.setText(getResources().getString(R.string.following));
                            addFollower(userId, newsItemModel.getUser_id());
                        }
                    }
                } else {
                    if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                        Intent intent = new Intent(NewsDetailsActivity.this, LoginActivity.class);
                        intent.putExtra("DistrictId", shareData.getHomeDistrictId());
                        startActivity(intent);
                    } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                        Intent intent = new Intent(NewsDetailsActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                    } else {
                        if (!GeneralFunctions.isNetworkAvailable(NewsDetailsActivity.this)) {
                            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), NewsDetailsActivity.this);
                        } else {
                            txtNewsFollow.setText(getResources().getString(R.string.follow));
                            removeFollowers(newsItemModel.getIs_followers_id());
                        }
                    }
                }
                break;
            case R.id.ivSpeaker:
                ivSpeakerStop.setVisibility(View.VISIBLE);
                ivSpeaker.setVisibility(View.GONE);
                String text = newsItemModel.getPostDetailsTe();
                if (text.equalsIgnoreCase("null") || text.isEmpty()) {
                    text = "Content not available";
                }
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.ivSpeakerStop:
                ivSpeakerStop.setVisibility(View.GONE);
                ivSpeaker.setVisibility(View.VISIBLE);
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                break;
            case R.id.ivNewsDetailsShare:
                if (!youtubeLink.isEmpty() || !videoLink.isEmpty()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, newsTitle + " - " + getString(R.string.app_name) + "\n" + "https://bharatshorts.in/news-details.php?nid=" + newsIdDetails);
                    try {
                        startActivity(Intent.createChooser(intent, "Share With"));
                    } catch (ActivityNotFoundException e) {
                        GeneralFunctions.showToast("No App Available", NewsDetailsActivity.this);
                    }
                } else {
                    int permissionCheck = ContextCompat.checkSelfPermission(NewsDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(NewsDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
                    } else {
                        newsDetailsLayoutBottomAppName.setVisibility(View.VISIBLE);
                        newsDetailsBottomAppNameViewPager.setVisibility(View.VISIBLE);
                        newsDetailsBottomAppName.setVisibility(View.VISIBLE);

                        final View decorView = this.getWindow().getDecorView();
                        final int uiOptions =
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                        decorView.setSystemUiVisibility(uiOptions);

                        fab.setVisibility(View.GONE);
                        fabHome.setVisibility(View.GONE);
                        fabViewPager.setVisibility(View.GONE);
                        layoutBottom.setVisibility(View.GONE);
                        txtTimeAgo.setVisibility(View.GONE);
                        ivPlayVideo.setVisibility(View.GONE);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                takeScreenShot(newsIdDetails, getWindow().getDecorView().getRootView(), newsTitle, newsDetailsLayoutBottomAppName, fab, layoutBottom, txtTimeAgo);
                            }
                        }, 150);

                    }
                }
                postShareCount(newsIdDetails);
                break;
            case R.id.fab:
            case R.id.fabHome:
            case R.id.fabViewPager:
                Intent intent = new Intent(NewsDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ivCommentsClose:
                bottomSheetDialog.dismiss();
                break;
            default:
                break;
        }
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void takeScreenShot(String newsIdDetails, View rootView, String newsTitle, LinearLayout newsDetailsLayoutBottomAppName, FloatingActionButton fab, LinearLayout layoutBottom, TextView txtTimeAgo) {

        //This is used to provide file name with Date a format
        Date date = new Date();
        CharSequence format = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date);

        //It will make sure to store file to given below Directory and If the file Directory dosen't exist then it will create it.
        try {
            File mainDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FilShare");
            if (!mainDir.exists()) {
                boolean mkdir = mainDir.mkdir();
            }

            //Providing file name along with Bitmap to capture screenview
            String path = mainDir + "/" + getResources().getString(R.string.app_name) + "-" + format + ".jpeg";
            rootView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);

//This logic is used to save file at given location with the given filename and compress the Image Quality.
            File imageFile = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

//Create New Method to take ScreenShot with the imageFile.
            shareScreenShot(newsIdDetails, imageFile, newsTitle, newsDetailsLayoutBottomAppName, fab, layoutBottom, txtTimeAgo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareScreenShot(String newsIdDetails, File imageFile, String newsTitle, LinearLayout newsDetailsLayoutBottomAppName, FloatingActionButton fab, LinearLayout layoutBottom, TextView txtTimeAgo) {
        newsDetailsLayoutBottomAppName.setVisibility(View.GONE);
        newsDetailsBottomAppNameViewPager.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
        fabHome.setVisibility(View.VISIBLE);
        ivPlayVideo.setVisibility(View.VISIBLE);
        fabViewPager.setVisibility(View.VISIBLE);
        layoutBottom.setVisibility(View.VISIBLE);
        txtTimeAgo.setVisibility(View.VISIBLE);
        newsDetailsBottomAppName.setVisibility(View.GONE);
        final View decorView = this.getWindow().getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Using sub-class of Content provider
        Uri uri = FileProvider.getUriForFile(
                NewsDetailsActivity.this,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                imageFile);

        //Explicit intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_TEXT, newsTitle + "\n" + "https://bharatshorts.in/" + newsIdDetails);
        intent.putExtra(Intent.EXTRA_TEXT, newsTitle + "\n" + "https://bharatshorts.in/news-details.php?nid=" + newsIdDetails);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        //It will show the application which are available to share Image; else Toast message will throw.
        try {
            this.startActivity(Intent.createChooser(intent, "Share With"));
        } catch (ActivityNotFoundException e) {
            GeneralFunctions.showToast("No App Available", NewsDetailsActivity.this);
        }
    }

    private void addFollower(final String userId, final String followerId) {
        try {
            progressBarDialog.showProgressBar(NewsDetailsActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ADD_FOLLOWER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(NewsDetailsActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, NewsDetailsActivity.this);
                                displayNewsDetails(newsIdDetails);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(NewsDetailsActivity.this);
                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", userId);
                    params.put("follower_id", followerId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(NewsDetailsActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }


    private void postComments(final String userId, final String newsId, final String comments) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.POST_COMMENTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, NewsDetailsActivity.this);
                                etComments.setText("");
                                getNewsComments(newsId, rvComments);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", userId);
                    params.put("news_id", newsId);
                    params.put("comment", comments);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(NewsDetailsActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void addFavourites(final String userId, final String newsId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ADD_FAVOURITES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, NewsDetailsActivity.this);
                                displayNewsDetails(newsIdDetails);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {

                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", userId);
                    params.put("news_id", newsId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(NewsDetailsActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void removeFavourites(final String favoriteId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REMOVE_FAVOURITES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, NewsDetailsActivity.this);
                                displayNewsDetails(newsIdDetails);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {

                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("favorite_id", favoriteId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(NewsDetailsActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void removeFollowers(final String followerId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REMOVE_FOLLOWER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, NewsDetailsActivity.this);
                                displayNewsDetails(newsIdDetails);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {

                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("follower_id", followerId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(NewsDetailsActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void getNewsComments(final String newsId, final RecyclerView rvComments) {
        try {
            progressBarDialog.showProgressBar(NewsDetailsActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_COMMENTS,
                    new Response.Listener<String>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(NewsDetailsActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("comments_data");
                                newsCommentsModelArrayList = NewsCommentsModel.fromJson(jsonArray);
                                if (newsCommentsModelArrayList.size() > 0) {
                                    txtCommentsCount.setText(getResources().getString(R.string.comments) + " ( " + newsCommentsModelArrayList.size() + " )");
                                    rvComments.setVisibility(View.VISIBLE);
                                    txtNoComments.setVisibility(View.GONE);
                                } else {
                                    rvComments.setVisibility(View.GONE);
                                    txtNoComments.setVisibility(View.VISIBLE);
                                }
                                /*newsCommentsAdapter = new NewsCommentsAdapter(NewsDetailsActivity.this, newsCommentsModelArrayList);
                                rvComments.setLayoutManager(new LinearLayoutManager(NewsDetailsActivity.this));
                                rvComments.setHasFixedSize(true);
                                rvComments.setAdapter(newsCommentsAdapter);
                                newsCommentsAdapter.notifyDataSetChanged();*/

                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(NewsDetailsActivity.this);
                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("news_id", newsId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(NewsDetailsActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void postShareCount(final String newsId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.POST_SHARE_COUNT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
//                                GeneralFunctions.showToast(jsonObject.getString("message"), NewsDetailsActivity.this);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {

                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("news_id", newsId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(NewsDetailsActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

}