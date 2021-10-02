package com.paxees_daily_smart.paxees_news_app.Adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.paxees_daily_smart.paxees_news_app.Activities.MainActivity;
import com.paxees_daily_smart.paxees_news_app.CustomViews.CircleImageView;
import com.paxees_daily_smart.paxees_news_app.Models.NewsItemModel;
import com.paxees_daily_smart.paxees_news_app.Models.PostImagesModel;
import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.Config;
import com.paxees_daily_smart.paxees_news_app.Utilities.ShareData;
import com.paxees_daily_smart.paxees_news_app.Utilities.TimeAgo;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeLatestNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private NewsItemClickedInterface newsItemClickedInterface;
    private ArrayList<NewsItemModel> newsItemModelArrayList = new ArrayList<>();
    private NewsItemModel newsItemModel1;
    private static final int IMAGE_VIEW_TYPE = 1;
    private static final int FULL_IMAGE_VIEW_TYPE = 2;
    private static final int VIEW_PAGER_VIEW_TYPE = 4;
    private static final int DEFAULT_VIEW_TYPE = 5;
    private static final int INTERSTITIAL_ADS_VIEW_TYPE = 6;
    private static final int NATIVE_VIEW = 7;
    private TextView[] dots;
    private ViewPager multiImageViewPager;
    public static final String DEVELOPER_KEY = Config.API_KEY;
    private String videoId = null;
    private String userId = Config.EMPTY_STRING;
    private ShareData shareData;
    private int selectedPosition = -1;
    private Lifecycle lifecycle;
    private SimpleExoPlayer exoPlayer;
    private InterstitialAd mInterstitialAd;
    private String mGoogleNativeId = Config.EMPTY_STRING;
    private TemplateView template;
    public TemplateView layoutNative;

    public HomeLatestNewsAdapter(Context context, ArrayList<NewsItemModel> newsItemModelArrayList, Lifecycle lifecycle,
                                 String googleNativeId, NewsItemClickedInterface newsItemClickedInterface) {
        this.mContext = context;
        this.newsItemModelArrayList = newsItemModelArrayList;
        this.newsItemClickedInterface = newsItemClickedInterface;
        this.lifecycle = lifecycle;
        this.mGoogleNativeId = googleNativeId;
        shareData = new ShareData(mContext);
        userId = shareData.getCustomerId();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_PAGER_VIEW_TYPE:
                View viewPagerViewType = layoutInflater.inflate(R.layout.view_pager_view_type, parent, false);
                viewHolder = new MultiImageViewHolder(viewPagerViewType);
                break;
            case IMAGE_VIEW_TYPE:
                View imageViewType = layoutInflater.inflate(R.layout.home_latest_news_list_item, parent, false);
                viewHolder = new ImageViewHolder(imageViewType);
                break;
            case FULL_IMAGE_VIEW_TYPE:
                View fullImageViewType = layoutInflater.inflate(R.layout.full_image_view_type, parent, false);
                viewHolder = new FullImageViewHolder(fullImageViewType);
                break;
            case DEFAULT_VIEW_TYPE:
                View defaultViewType = layoutInflater.inflate(R.layout.default_view_type, parent, false);
                viewHolder = new DefaultViewTypeViewHolder(defaultViewType);
                break;
//            case INTERSTITIAL_ADS_VIEW_TYPE:
//                View interstitialAdViewType = layoutInflater.inflate(R.layout.layout_interstitial_ads, parent, false);
//                viewHolder = new InterstitialAdsViewHolder(interstitialAdViewType);
//                break;
            case NATIVE_VIEW:
                View nativeAdViewType = layoutInflater.inflate(R.layout.layout_native_ads, parent, false);
                viewHolder = new NativeAdsViewHolder(nativeAdViewType);
                break;
        }
        return viewHolder;
    }

    private void prepareInterstitialAd(String interstitialAdMobId) {
        Log.i("CheckingInter", "is Working");
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(mContext, interstitialAdMobId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.e("onAdLoaded", "onAdLoaded");
                mInterstitialAd.show((MainActivity) mContext);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e("onAdLoaded", "onAdLoaded " + loadAdError.getMessage());
                // Handle the error
                mInterstitialAd = null;
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final NewsItemModel newsItemModel = newsItemModelArrayList.get(position);
//        newsTableRepository.insert(newsItemModel);
        newsItemModel1 = newsItemModelArrayList.get(position);
        if (position % 10 == 0 && position > 1) {
            Log.i("ScrollViewInit", " ads shown " + position);
            prepareInterstitialAd(mContext.getResources().getString(R.string.intersitialAds));
        }
        switch (holder.getItemViewType()) {
            case IMAGE_VIEW_TYPE:
                final ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                final ValueAnimator anim = ValueAnimator.ofFloat(1f, 1.5f);
                anim.setDuration(1000);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        imageViewHolder.ivQureka.setScaleX((Float) animation.getAnimatedValue());
                        imageViewHolder.ivQureka.setScaleY((Float) animation.getAnimatedValue());
                    }
                });
                anim.setRepeatCount(5);
                anim.setRepeatMode(ValueAnimator.REVERSE);
                anim.start();
                imageViewHolder.txtNewsTitle.setText(newsItemModel.getPostTitleTe());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Spannable spannable = (Spannable) Html.fromHtml(newsItemModel.getPostDetailsTe(), Html.FROM_HTML_MODE_COMPACT);
                    for (URLSpan u : spannable.getSpans(0, spannable.length(), URLSpan.class)) {
                        spannable.setSpan(new UnderlineSpan() {
                            public void updateDrawState(TextPaint tp) {
                                tp.setUnderlineText(false);
                            }
                        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0);
                    }
                    imageViewHolder.txtNewsDescription.setText(spannable);
                    imageViewHolder.txtNewsDescription.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    Spannable spannable = (Spannable) Html.fromHtml(newsItemModel.getPostDetailsTe());
                    for (URLSpan u : spannable.getSpans(0, spannable.length(), URLSpan.class)) {
                        spannable.setSpan(new UnderlineSpan() {
                            public void updateDrawState(TextPaint tp) {
                                tp.setUnderlineText(false);
                            }
                        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0);
                    }
                    imageViewHolder.txtNewsDescription.setText(spannable);
                    imageViewHolder.txtNewsDescription.setMovementMethod(LinkMovementMethod.getInstance());
                }
                imageViewHolder.txtNewsPostedName.setText(newsItemModel.getName());
                imageViewHolder.layoutBottom.setVisibility(View.VISIBLE);

                TimeAgo timeAgo = new TimeAgo();
                String timeAgoString = null;
                if (newsItemModel.getScheduleDate().matches("0000-00-00 00:00:00")) {
                    timeAgoString = timeAgo.covertTimeToText(newsItemModel.getPostingDate());
                } else {
                    timeAgoString = timeAgo.covertTimeToText(newsItemModel.getScheduleDate());
                }
                imageViewHolder.txtTimeAgo.setText(timeAgoString);
                if (newsItemModel.getYoutube_link().isEmpty() && newsItemModel.getPostVideo().isEmpty()) {
                    imageViewHolder.layoutYouTube.setVisibility(View.GONE);
                    imageViewHolder.layoutVideoHalfScreen.setVisibility(View.GONE);
                    imageViewHolder.layoutVideo.setVisibility(View.GONE);
                    imageViewHolder.ivNewsImage.setVisibility(View.VISIBLE);
                    imageViewHolder.txtTimeAgo.setVisibility(View.VISIBLE);
                } else if (!newsItemModel.getYoutube_link().isEmpty() && !newsItemModel.getYoutube_link().equalsIgnoreCase("0")) {
                    imageViewHolder.layoutYouTube.setVisibility(View.VISIBLE);
                    imageViewHolder.ivNewsImage.setVisibility(View.GONE);
                    imageViewHolder.txtTimeAgo.setVisibility(View.GONE);
                    imageViewHolder.layoutVideoHalfScreen.setVisibility(View.GONE);
                    imageViewHolder.layoutVideo.setVisibility(View.GONE);
                    String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
                    Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = compiledPattern.matcher(newsItemModel.getYoutube_link());
                    if (matcher.find()) {
                        videoId = matcher.group(1);
                    }
                    imageViewHolder.youtube_player_view.setEnableAutomaticInitialization(false);
                    imageViewHolder.youtube_player_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(YouTubePlayer youTubePlayer) {
                            youTubePlayer.cueVideo(videoId, 0);
                        }
                    });
//                    initYoutubePlayer(imageViewHolder.youtube_player_view, videoId);
                    Log.e("videoId", "--" + videoId);
                    imageViewHolder.webView.loadData("<iframe src=\"https://www.youtube.com/embed/" + videoId + "?rel=0&showinfo=0\" width=\"100%\" height=\"100%\" scrolling=\"no\" frameborder=\"0\" allowTransparency=\"true\" allowFullScreen=\"true\"></iframe>", "text/html", "utf-8");
                } else if (!newsItemModel.getPostVideo().isEmpty()) {
                    imageViewHolder.layoutVideoHalfScreen.setVisibility(View.VISIBLE);
                    imageViewHolder.layoutYouTube.setVisibility(View.GONE);
                    imageViewHolder.ivNewsImage.setVisibility(View.GONE);
                    imageViewHolder.txtTimeAgo.setVisibility(View.GONE);
//                    imageViewHolder.txtNewsTitle.setVisibility(View.GONE);
//                    imageViewHolder.txtNewsDescription.setVisibility(View.GONE);
                    try {
                        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                        exoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
                        Uri videouri = Uri.parse(Config.NEWS_IMAGE_URL + newsItemModel.getPostVideo());
                        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                        MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
                        imageViewHolder.simpleExoPlayer.setPlayer(exoPlayer);
                        exoPlayer.prepare(mediaSource);
                        exoPlayer.setPlayWhenReady(false);
                        initPlayer(imageViewHolder, position, exoPlayer, mediaSource, newsItemModel);
                    } catch (Exception e) {
                        Log.e("TAG", "Error : " + e.toString());
                    }
                }
                if (selectedPosition == position) {
                    imageViewHolder.ivSpeakerStop.setVisibility(View.VISIBLE);
                    imageViewHolder.ivSpeaker.setVisibility(View.GONE);
                } else {
                    imageViewHolder.ivSpeakerStop.setVisibility(View.GONE);
                    imageViewHolder.ivSpeaker.setVisibility(View.VISIBLE);
                }
                if (newsItemModel.getName().equalsIgnoreCase("Admin")) {
                    imageViewHolder.layoutQureka.setVisibility(View.VISIBLE);
                    imageViewHolder.layoutNewsReporterDetails.setVisibility(View.GONE);
                    imageViewHolder.txtTimeAgo.setVisibility(View.VISIBLE);
                    imageViewHolder.txtTimeAgo.setText(timeAgoString);
                    imageViewHolder.layoutQureka.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.playAndWin("1354.win.qureka.com");
                            }
                        }
                    });
                } else {
                    if (userId.equalsIgnoreCase(newsItemModel.getUser_id())) {
                        imageViewHolder.txtNewsFollow.setVisibility(View.GONE);
                    } else {
                        imageViewHolder.txtNewsFollow.setVisibility(View.VISIBLE);
                    }
                    imageViewHolder.layoutQureka.setVisibility(View.GONE);
                    imageViewHolder.layoutNewsReporterDetails.setVisibility(View.VISIBLE);
                    imageViewHolder.txtNewsPostedName.setTextSize(10);
                    imageViewHolder.txtTimeAgo.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load(Config.PROFILE_IMAGE_URL + newsItemModel.getProfile_pic())
                            .placeholder(R.drawable.defult_profile)
                            .error(R.drawable.defult_profile)
                            .into(imageViewHolder.ivPostedNewsReporterImage);
                    if (newsItemModel.getDistrictNameTe().isEmpty() || newsItemModel.getDistrictNameTe().equalsIgnoreCase("none")) {
                        imageViewHolder.txtNewsPostedDistrict.setVisibility(View.GONE);
                    } else {
                        imageViewHolder.txtNewsPostedDistrict.setVisibility(View.VISIBLE);
                    }
                }
                imageViewHolder.txtNewsPostedDistrict.setText(newsItemModel.getDistrictNameTe());

                if (!newsItemModel.getPostImages().get(0).getPostImage().isEmpty()) {
                    try {
                        Picasso.get()
                                .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(0).getPostImage())
                                .placeholder(R.drawable.default_placeholder)
                                .error(R.drawable.default_placeholder)
                                .into(imageViewHolder.ivNewsImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (newsItemModel.getIs_followers().equalsIgnoreCase("1")) {
                    imageViewHolder.txtNewsFollow.setTextColor(mContext.getResources().getColor(R.color.white));
                    imageViewHolder.txtNewsFollow.setText(mContext.getResources().getString(R.string.following));
                } else {
                    imageViewHolder.txtNewsFollow.setTextColor(mContext.getResources().getColor(R.color.white));
                    imageViewHolder.txtNewsFollow.setText(mContext.getResources().getString(R.string.follow));
                }

                if (newsItemModel.getIs_fav().equalsIgnoreCase("1")) {
                    imageViewHolder.ivNewsAddFavourites.setBackgroundResource(R.drawable.favorite_filled_blue);
                } else {
                    imageViewHolder.ivNewsAddFavourites.setBackgroundResource(R.drawable.favorite_blue_border);
                }
                imageViewHolder.txtNewsDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsItemClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe());
                        }
                    }
                });
                imageViewHolder.layoutNewsItemParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsItemClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe());
                        }
                    }
                });
                imageViewHolder.ivNewsComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsCommentsClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe());
                        }
                    }
                });
                imageViewHolder.ivNewsAddFavourites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemModel.getIs_fav().equalsIgnoreCase("0")) {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsAddFavouritesClickedMethod(position, newsItemModel.getNews_id());
                            }
                        } else {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsRemoveFavouritesClickedMethod(position, newsItemModel.getIs_fav_id());
                            }
                        }
                    }
                });
                imageViewHolder.ivNewsShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageViewHolder.viewBottom.setVisibility(View.GONE);
                        if (!newsItemModel.getYoutube_link().isEmpty() || !newsItemModel.getPostVideo().isEmpty()) {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsCommonShareVideoView(newsItemModel.getPostTitleTe(), newsItemModel.getNews_id());
                            }
                        } else {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsShareClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe(), imageViewHolder.layoutBottomImage, imageViewHolder.txtTimeAgo, imageViewHolder.layoutBottom, imageViewHolder.viewBottom);
                            }
                        }
                    }
                });

                imageViewHolder.ivSpeaker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageViewHolder.ivSpeakerStop.setVisibility(View.VISIBLE);
                        imageViewHolder.ivSpeaker.setVisibility(View.GONE);
                        selectedPosition = position;
                        String newsDescription = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newsDescription = String.valueOf(Html.fromHtml(newsItemModel.getPostDetailsTe(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            newsDescription = String.valueOf(Html.fromHtml(newsItemModel.getPostDetailsTe()));
                        }

                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsSpeakerClickedMethod(newsItemModel.getPostTitleTe(), newsDescription);
                        }
                    }
                });
                imageViewHolder.ivSpeakerStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageViewHolder.ivSpeakerStop.setVisibility(View.GONE);
                        imageViewHolder.ivSpeaker.setVisibility(View.VISIBLE);
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsSpeakerStopClickedMethod(newsItemModel.getPostTitleTe(), newsItemModel.getPostDetailsTe());
                        }
                    }
                });

                imageViewHolder.txtNewsFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemModel.getIs_followers().equalsIgnoreCase("0")) {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsFollowClickedMethod(position, newsItemModel.getUser_id());
                            }
                        } else {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsUnFollowClickedMethod(position, newsItemModel.getIs_followers_id());
                            }
                        }
                    }
                });
               /* Animation startAnimation = AnimationUtils.loadAnimation(mContext, R.anim.blink_animation);
                imageViewHolder.ivNewsShare.startAnimation(startAnimation);*/

                if (newsItemClickedInterface != null) {
                    newsItemClickedInterface.newsViewTypeMethod(String.valueOf(IMAGE_VIEW_TYPE), newsItemModel.getNews_id(), position);
                }

                if (newsItemClickedInterface != null) {
                    newsItemClickedInterface.newsTakeScreenshotMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe(), imageViewHolder.layoutBottomImage, imageViewHolder.txtTimeAgo, imageViewHolder.layoutBottom, imageViewHolder.viewBottom);
                }
                break;
            case FULL_IMAGE_VIEW_TYPE:
                final FullImageViewHolder fullImageViewHolder = (FullImageViewHolder) holder;
                fullImageViewHolder.txtFullImageNewsTitle.setText(newsItemModel.getPostTitleTe());
                timeAgo = new TimeAgo();
                if (newsItemModel.getScheduleDate().matches("0000-00-00 00:00:00")) {
                    timeAgoString = timeAgo.covertTimeToText(newsItemModel.getPostingDate());
                } else {
                    timeAgoString = timeAgo.covertTimeToText(newsItemModel.getScheduleDate());
                }
                fullImageViewHolder.txtTimesAgoFullImage.setText(timeAgoString);
                if (newsItemModel.getDistrictNameTe().isEmpty() || newsItemModel.getDistrictNameTe().equalsIgnoreCase("none")) {
                    fullImageViewHolder.txtDistrictNameFullImage.setVisibility(View.GONE);
                }
                fullImageViewHolder.txtDistrictNameFullImage.setText(newsItemModel.getDistrictNameTe());
                try {
                    Picasso.get()
                            .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(0).getPostImage())
                            .placeholder(R.drawable.default_placeholder_fullimage)
                            .error(R.drawable.default_placeholder_fullimage)
                            .into(fullImageViewHolder.ivFullImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (newsItemClickedInterface != null) {
                    newsItemClickedInterface.newsViewTypeMethod(String.valueOf(FULL_IMAGE_VIEW_TYPE), newsItemModel.getNews_id(), position);
                }
                fullImageViewHolder.ivWhatsApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fullImageViewHolder.txtFullImageNewsTitle.setVisibility(View.GONE);
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsShareFullImage(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe(), fullImageViewHolder.layoutFullImageBottom, fullImageViewHolder.layoutBottomFullImageAppName, "2");
                        }
                    }
                });
                fullImageViewHolder.ivShareFullImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fullImageViewHolder.txtFullImageNewsTitle.setVisibility(View.GONE);
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsShareClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe(), fullImageViewHolder.layoutBottomFullImageAppName, fullImageViewHolder.txtTimesAgoFullImage, fullImageViewHolder.layoutFullImageBottom, fullImageViewHolder.viewBottom);
                        }
                    }
                });
                fullImageViewHolder.ivFullImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsItemClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe());
                        }
                    }
                });

                break;
            case VIEW_PAGER_VIEW_TYPE:
                final MultiImageViewHolder multiImageViewHolder = (MultiImageViewHolder) holder;
                if (newsItemClickedInterface != null) {
                    newsItemClickedInterface.newsViewTypeMethod(String.valueOf(VIEW_PAGER_VIEW_TYPE), newsItemModel.getNews_id(), position);
                }
                timeAgo = new TimeAgo();
                if (newsItemModel.getScheduleDate().matches("0000-00-00 00:00:00")) {
                    timeAgoString = timeAgo.covertTimeToText(newsItemModel.getPostingDate());
                } else {
                    timeAgoString = timeAgo.covertTimeToText(newsItemModel.getScheduleDate());
                }
                multiImageViewHolder.txtTimesAgoMultiImageView.setText(timeAgoString);
                if (newsItemModel.getDistrictNameTe().isEmpty() || newsItemModel.getDistrictNameTe().equalsIgnoreCase("none")) {
                    multiImageViewHolder.txtDistrictNameMultiImage.setVisibility(View.GONE);
                }
                multiImageViewHolder.txtDistrictNameMultiImage.setText(newsItemModel.getDistrictNameTe());
                multiImageViewHolder.ivWhatsAppViewPager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsShareFullImage(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe(), multiImageViewHolder.layoutViewPagerBottom, multiImageViewHolder.layoutBottomViewPagerAppName, "2");
                        }
                    }
                });
                multiImageViewHolder.ivShareViewPager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsShareClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe(), multiImageViewHolder.layoutBottomViewPagerAppName, multiImageViewHolder.txtTimesAgoMultiImageView, multiImageViewHolder.layoutViewPagerBottom, multiImageViewHolder.viewBottom);
                        }
                    }
                });
                initViewPager(position, multiImageViewPager, newsItemModel.getPostImages(), multiImageViewHolder.layoutDots);
                break;
            case DEFAULT_VIEW_TYPE:
                final DefaultViewTypeViewHolder defaultViewTypeViewHolder = (DefaultViewTypeViewHolder) holder;
                defaultViewTypeViewHolder.txtNewsTitle.setText(newsItemModel.getPostTitleTe());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Spannable spannable = (Spannable) Html.fromHtml(newsItemModel.getPostDetailsTe(), Html.FROM_HTML_MODE_COMPACT);
                    for (URLSpan u : spannable.getSpans(0, spannable.length(), URLSpan.class)) {
                        spannable.setSpan(new UnderlineSpan() {
                            public void updateDrawState(TextPaint tp) {
                                tp.setUnderlineText(false);
                            }
                        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0);
                    }
                    defaultViewTypeViewHolder.txtNewsDescription.setText(spannable);
                    defaultViewTypeViewHolder.txtNewsDescription.setMovementMethod(LinkMovementMethod.getInstance());

                } else {
                    Spannable spannable = (Spannable) Html.fromHtml(newsItemModel.getPostDetailsTe());
                    for (URLSpan u : spannable.getSpans(0, spannable.length(), URLSpan.class)) {
                        spannable.setSpan(new UnderlineSpan() {
                            public void updateDrawState(TextPaint tp) {
                                tp.setUnderlineText(false);
                            }
                        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0);
                    }
                    defaultViewTypeViewHolder.txtNewsDescription.setText(spannable);
                    defaultViewTypeViewHolder.txtNewsDescription.setMovementMethod(LinkMovementMethod.getInstance());
                }
                defaultViewTypeViewHolder.txtNewsPostedName.setText(newsItemModel.getName());
                defaultViewTypeViewHolder.layoutBottom.setVisibility(View.VISIBLE);

                timeAgo = new TimeAgo();
                if (newsItemModel.getScheduleDate().matches("0000-00-00 00:00:00")) {
                    timeAgoString = timeAgo.covertTimeToText(newsItemModel.getPostingDate());
                } else {
                    timeAgoString = timeAgo.covertTimeToText(newsItemModel.getScheduleDate());
                }
                defaultViewTypeViewHolder.txtTimeAgo.setText(timeAgoString);

                if (selectedPosition == position) {
                    defaultViewTypeViewHolder.ivSpeakerStop.setVisibility(View.VISIBLE);
                    defaultViewTypeViewHolder.ivSpeaker.setVisibility(View.GONE);
                } else {
                    defaultViewTypeViewHolder.ivSpeakerStop.setVisibility(View.GONE);
                    defaultViewTypeViewHolder.ivSpeaker.setVisibility(View.VISIBLE);
                }
                if (newsItemModel.getName().equalsIgnoreCase("Admin")) {
                    defaultViewTypeViewHolder.txtNewsFollow.setVisibility(View.GONE);
                    defaultViewTypeViewHolder.txtNewsPostedName.setText(timeAgoString);
                    defaultViewTypeViewHolder.txtNewsPostedName.setGravity(Gravity.CENTER | Gravity.START);
//                    defaultViewTypeViewHolder.txtNewsPostedName.setTextColor(mContext.getResources().getColor(R.color.red));
                    defaultViewTypeViewHolder.txtTimeAgo.setVisibility(View.GONE);
                    defaultViewTypeViewHolder.ivPostedNewsReporterImage.setVisibility(View.GONE);
                } else {
                    if (userId.equalsIgnoreCase(newsItemModel.getUser_id())) {
                        defaultViewTypeViewHolder.txtNewsFollow.setVisibility(View.GONE);
                    } else {
                        defaultViewTypeViewHolder.txtNewsFollow.setVisibility(View.VISIBLE);
                    }
                    defaultViewTypeViewHolder.txtNewsPostedName.setVisibility(View.VISIBLE);
                    defaultViewTypeViewHolder.ivPostedNewsReporterImage.setVisibility(View.VISIBLE);
                    defaultViewTypeViewHolder.txtTimeAgo.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load(Config.PROFILE_IMAGE_URL + newsItemModel.getProfile_pic())
                            .placeholder(R.drawable.defult_profile)
                            .error(R.drawable.defult_profile)
                            .into(defaultViewTypeViewHolder.ivPostedNewsReporterImage);
                }

                if (newsItemModel.getDistrictNameTe().isEmpty() || newsItemModel.getDistrictNameTe().equalsIgnoreCase("none")) {
                    defaultViewTypeViewHolder.txtNewsPostedDistrict.setVisibility(View.GONE);
                } else {
                    defaultViewTypeViewHolder.txtNewsPostedDistrict.setVisibility(View.VISIBLE);
                }
                defaultViewTypeViewHolder.txtNewsPostedDistrict.setText(newsItemModel.getDistrictNameTe());
                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "lohit_telugu.ttf");
                defaultViewTypeViewHolder.txtNewsTitle.setTypeface(typeface);
                defaultViewTypeViewHolder.txtNewsPostedDistrict.setTypeface(typeface);
                defaultViewTypeViewHolder.txtNewsDescription.setTypeface(typeface);

                if (newsItemModel.getIs_followers().equalsIgnoreCase("1")) {
                    defaultViewTypeViewHolder.txtNewsFollow.setTextColor(mContext.getResources().getColor(R.color.white));
                    defaultViewTypeViewHolder.txtNewsFollow.setText(mContext.getResources().getString(R.string.following));
                } else {
                    defaultViewTypeViewHolder.txtNewsFollow.setTextColor(mContext.getResources().getColor(R.color.white));
                    defaultViewTypeViewHolder.txtNewsFollow.setText(mContext.getResources().getString(R.string.follow));
                }

                if (newsItemModel.getIs_fav().equalsIgnoreCase("1")) {
                    defaultViewTypeViewHolder.ivNewsAddFavourites.setBackgroundResource(R.drawable.favorite_filled_blue);
                } else {
                    defaultViewTypeViewHolder.ivNewsAddFavourites.setBackgroundResource(R.drawable.favorite_blue_border);
                }
                defaultViewTypeViewHolder.txtNewsDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsItemClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe());
                        }
                    }
                });
                defaultViewTypeViewHolder.layoutNewsItemParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsItemClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe());
                        }
                    }
                });
                defaultViewTypeViewHolder.ivNewsComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsCommentsClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe());
                        }
                    }
                });
                defaultViewTypeViewHolder.ivNewsAddFavourites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemModel.getIs_fav().equalsIgnoreCase("0")) {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsAddFavouritesClickedMethod(position, newsItemModel.getNews_id());
                            }
                        } else {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsRemoveFavouritesClickedMethod(position, newsItemModel.getIs_fav_id());
                            }
                        }
                    }
                });
                defaultViewTypeViewHolder.ivNewsShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defaultViewTypeViewHolder.viewBottom.setVisibility(View.GONE);
                        if (!newsItemModel.getYoutube_link().isEmpty() || !newsItemModel.getPostVideo().isEmpty()) {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsCommonShareVideoView(newsItemModel.getPostTitleTe(), newsItemModel.getNews_id());
                            }
                        } else {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsShareClickedMethod(newsItemModel.getNews_id(), newsItemModel.getPostTitleTe(), defaultViewTypeViewHolder.layoutBottomImage, defaultViewTypeViewHolder.txtTimeAgo, defaultViewTypeViewHolder.layoutBottom, defaultViewTypeViewHolder.viewBottom);
                            }
                        }
                    }
                });

                defaultViewTypeViewHolder.ivSpeaker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defaultViewTypeViewHolder.ivSpeakerStop.setVisibility(View.VISIBLE);
                        defaultViewTypeViewHolder.ivSpeaker.setVisibility(View.GONE);
                        selectedPosition = position;
                        String newsDescription = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newsDescription = String.valueOf(Html.fromHtml(newsItemModel.getPostDetailsTe(), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            newsDescription = String.valueOf(Html.fromHtml(newsItemModel.getPostDetailsTe()));
                        }

                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsSpeakerClickedMethod(newsItemModel.getPostTitleTe(), newsDescription);
                        }
                    }
                });
                defaultViewTypeViewHolder.ivSpeakerStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defaultViewTypeViewHolder.ivSpeakerStop.setVisibility(View.GONE);
                        defaultViewTypeViewHolder.ivSpeaker.setVisibility(View.VISIBLE);
                        if (newsItemClickedInterface != null) {
                            newsItemClickedInterface.newsSpeakerStopClickedMethod(newsItemModel.getPostTitleTe(), newsItemModel.getPostDetailsTe());
                        }
                    }
                });

                defaultViewTypeViewHolder.txtNewsFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newsItemModel.getIs_followers().equalsIgnoreCase("0")) {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsFollowClickedMethod(position, newsItemModel.getUser_id());
                            }
                        } else {
                            if (newsItemClickedInterface != null) {
                                newsItemClickedInterface.newsUnFollowClickedMethod(position, newsItemModel.getIs_followers_id());
                            }
                        }
                    }
                });
                if (newsItemClickedInterface != null) {
                    newsItemClickedInterface.newsViewTypeMethod(String.valueOf(IMAGE_VIEW_TYPE), newsItemModel.getNews_id(), position);
                }
                break;
          /*  case INTERSTITIAL_ADS_VIEW_TYPE:
                if (newsItemClickedInterface != null) {
                    newsItemClickedInterface.showInterstitialAd();
                }
                break;*/
            case NATIVE_VIEW:
                if (position % 5 == 0 && position > 1) {
                    if (newsItemClickedInterface != null) {
                        newsItemClickedInterface.showNativeAds();
                    }
                    setAdNative();
                }
                break;
        }
    }

    public void setAdNative() {
        AdLoader adLoader = new AdLoader.Builder(Objects.requireNonNull(mContext), mContext.getResources().getString(R.string.nativeAds))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        // Show the ad.
                        layoutNative.setNativeAd(nativeAd);
                    }

                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        Log.i("Adsshow", "failed");
                        // Handle the failure by logging, altering the UI, and so on.
//                    setAdNative();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void initViewPager(final int position, final ViewPager multiImageViewPager, final List<PostImagesModel> postImages, final LinearLayout layoutDots) {
        multiImageViewPager.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(mContext, postImages);
                multiImageViewPager.setAdapter(customPagerAdapter);
                multiImageViewPager.getAdapter().notifyDataSetChanged();
                multiImageViewPager.setPageMargin(20);
                multiImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        if (position > 1 && position % 5 == 0) {
                            Log.i("scrolllView", "scroll items " + position);
                        }
                    }

                    @Override
                    public void onPageSelected(int position) {
                        addDot(position, layoutDots, postImages);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                addDot(0, layoutDots, postImages);
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
            }
        });
    }

    private void initYoutubePlayer(final YouTubePlayerView youtube_player_view, final String videoId) {
        youtube_player_view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                Log.e("AttachYoutube", "AttachYoutube");
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                Log.e("DetachedYoutube", "DetachedYoutube");
            }
        });
    }

    private void initPlayer(final ImageViewHolder imageViewHolder, final int position, final SimpleExoPlayer exoPlayer, final MediaSource mediaSource, final NewsItemModel newsItemModel) {
        imageViewHolder.simpleExoPlayer.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                Log.e("AttachFromWindow", "AttachFromWindow");
                if (exoPlayer.getCurrentPosition() == position) {
                    exoPlayer.prepare(mediaSource);
                    exoPlayer.seekTo(0);
                    exoPlayer.setPlayWhenReady(true);
                }
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                Log.e("DetachedFromWindow", "onViewDetached");
//                exoPlayer.stop();
                exoPlayer.setPlayWhenReady(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsItemModelArrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNewsTitle, txtNewsDescription, txtNewsPostedName, txtNewsFollow, txtTimeAgo, txtNewsPostedDistrict, txtShareContentTitle, txtShareContentDesc;
        private ImageView ivNewsImage, ivNewsComments, ivNewsAddFavourites, ivNewsShare, ivSpeaker, ivPlayVideo, ivSpeakerStop, ivQureka;
        private LinearLayout layoutNewsItemParent, layoutBottom, layoutBottomImage, layoutNewsReporterDetails, layoutQureka;
        private FrameLayout layoutYouTube, layoutVideoHalfScreen;
        private CircleImageView ivPostedNewsReporterImage;
        private WebView webView;
        private View viewBottom;
        private TemplateView template;
        private VideoView fillViewVideoView;
        private YouTubePlayerView youtube_player_view;
        private ImageView ivWhatsAppVideoView, ivShareVideoView, ivPauseVideo, ivThumbnail;
        private ConstraintLayout layoutVideo;
        private LinearLayout layoutVideoViewBottom, layoutBottomVideoViewAppName;
        private TextView txtTimesAgoVideoView;
        private SimpleExoPlayerView simpleExoPlayer;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            template = itemView.findViewById(R.id.ad_native);
            txtNewsTitle = itemView.findViewById(R.id.txtNewsTitle);
            txtNewsDescription = itemView.findViewById(R.id.txtNewsDescription);
            txtNewsPostedName = itemView.findViewById(R.id.txtNewsPostedName);
            ivNewsImage = itemView.findViewById(R.id.ivNewsImage);
            ivNewsComments = itemView.findViewById(R.id.ivNewsComments);
            ivNewsAddFavourites = itemView.findViewById(R.id.ivNewsAddFavourites);
            ivSpeaker = itemView.findViewById(R.id.ivSpeaker);
            ivSpeakerStop = itemView.findViewById(R.id.ivSpeakerStop);
            layoutNewsItemParent = itemView.findViewById(R.id.layoutNewsItemParent);
            layoutBottom = itemView.findViewById(R.id.layoutBottom);
            layoutBottomImage = itemView.findViewById(R.id.layoutBottomImage);
            layoutNewsReporterDetails = itemView.findViewById(R.id.layoutNewsReporterDetails);
            layoutQureka = itemView.findViewById(R.id.layoutQureka);
            txtNewsFollow = itemView.findViewById(R.id.txtNewsFollow);
            txtTimeAgo = itemView.findViewById(R.id.txtTimeAgo);
            ivNewsShare = itemView.findViewById(R.id.ivNewsShare);
            ivPlayVideo = itemView.findViewById(R.id.ivPlayVideo);
            ivQureka = itemView.findViewById(R.id.ivQureka);
            layoutYouTube = itemView.findViewById(R.id.layoutYouTube);
            layoutVideoHalfScreen = itemView.findViewById(R.id.layoutVideoHalfScreen);
            txtNewsPostedDistrict = itemView.findViewById(R.id.txtNewsPostedDistrict);
            txtShareContentDesc = itemView.findViewById(R.id.txtShareContentDesc);
            ivPostedNewsReporterImage = itemView.findViewById(R.id.ivPostedNewsReporterImage);
            viewBottom = itemView.findViewById(R.id.viewBottom);
            youtube_player_view = itemView.findViewById(R.id.youtube_player_view);
            webView = itemView.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            youtube_player_view.getPlayerUiController().showBufferingProgress(false);
            lifecycle.addObserver(youtube_player_view);

            fillViewVideoView = itemView.findViewById(R.id.fillViewVideoView);
            ivPauseVideo = itemView.findViewById(R.id.ivPauseVideo);
            layoutVideo = itemView.findViewById(R.id.layoutVideo);
            layoutVideoViewBottom = itemView.findViewById(R.id.layoutVideoViewBottom);
            txtTimesAgoVideoView = itemView.findViewById(R.id.txtTimesAgoVideoView);
            ivWhatsAppVideoView = itemView.findViewById(R.id.ivWhatsAppVideoView);
            ivShareVideoView = itemView.findViewById(R.id.ivShareVideoView);
            txtShareContentDesc = itemView.findViewById(R.id.txtShareContentDesc);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            viewBottom = itemView.findViewById(R.id.viewBottom);
            layoutBottomVideoViewAppName = itemView.findViewById(R.id.layoutBottomVideoViewAppName);
            simpleExoPlayer = itemView.findViewById(R.id.simpleExoPlayer);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "mallanna_regular.ttf");
            Typeface typeface_title = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
            txtShareContentDesc.setTypeface(typeface_title);
            txtNewsTitle.setTypeface(typeface_title);
            txtNewsPostedDistrict.setTypeface(typeface);
            txtNewsDescription.setTypeface(typeface);
        }
    }

    public class FullImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFullImageView, ivWhatsApp, ivShareFullImage;
        private TextView txtFullImageNewsTitle, txtTimesAgoFullImage, txtDistrictNameFullImage, txtShareContentTitle, txtShareContentDesc;
        private FrameLayout layoutFullImage;
        private LinearLayout layoutFullImageBottom, layoutBottomFullImageAppName;
        private View viewBottom;

        public FullImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFullImageView = itemView.findViewById(R.id.ivFullImageView);
            layoutFullImageBottom = itemView.findViewById(R.id.layoutFullImageBottom);
            ivWhatsApp = itemView.findViewById(R.id.ivWhatsApp);
            ivShareFullImage = itemView.findViewById(R.id.ivShareFullImage);
            txtFullImageNewsTitle = itemView.findViewById(R.id.txtFullImageNewsTitle);
            txtTimesAgoFullImage = itemView.findViewById(R.id.txtTimesAgoFullImage);
            txtDistrictNameFullImage = itemView.findViewById(R.id.txtDistrictNameFullImage);
            layoutFullImage = itemView.findViewById(R.id.layoutFullImage);
            layoutBottomFullImageAppName = itemView.findViewById(R.id.layoutBottomFullImageAppName);
            txtShareContentDesc = itemView.findViewById(R.id.txtShareContentDesc);
            viewBottom = itemView.findViewById(R.id.viewBottom);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
            txtShareContentDesc.setTypeface(typeface);
            txtDistrictNameFullImage.setTypeface(typeface);

        }
    }

    public class DefaultViewTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNewsTitle, txtNewsDescription, txtNewsPostedName, txtNewsFollow, txtTimeAgo, txtNewsPostedDistrict, txtShareContentTitle, txtShareContentDesc;
        private ImageView ivNewsImage, ivNewsComments, ivNewsAddFavourites, ivNewsShare, ivSpeaker, ivPlayVideo, ivSpeakerStop;
        private LinearLayout layoutNewsItemParent, layoutBottom, layoutBottomImage;
        private FrameLayout layoutYouTube, layoutVideoHalfScreen;
        private CircleImageView ivPostedNewsReporterImage;
        private View viewBottom;

        public DefaultViewTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNewsTitle = itemView.findViewById(R.id.txtNewsTitle);
            txtNewsDescription = itemView.findViewById(R.id.txtNewsDescription);
            txtNewsPostedName = itemView.findViewById(R.id.txtNewsPostedName);
            ivNewsImage = itemView.findViewById(R.id.ivNewsImage);
            ivNewsComments = itemView.findViewById(R.id.ivNewsComments);
            ivNewsAddFavourites = itemView.findViewById(R.id.ivNewsAddFavourites);
            ivSpeaker = itemView.findViewById(R.id.ivSpeaker);
            ivSpeakerStop = itemView.findViewById(R.id.ivSpeakerStop);
            layoutNewsItemParent = itemView.findViewById(R.id.layoutNewsItemParent);
            layoutBottom = itemView.findViewById(R.id.layoutBottom);
            layoutBottomImage = itemView.findViewById(R.id.layoutBottomImage);
            txtNewsFollow = itemView.findViewById(R.id.txtNewsFollow);
            txtTimeAgo = itemView.findViewById(R.id.txtTimeAgo);
            ivNewsShare = itemView.findViewById(R.id.ivNewsShare);
            ivPlayVideo = itemView.findViewById(R.id.ivPlayVideo);
            layoutYouTube = itemView.findViewById(R.id.layoutYouTube);
            layoutVideoHalfScreen = itemView.findViewById(R.id.layoutVideoHalfScreen);
            txtNewsPostedDistrict = itemView.findViewById(R.id.txtNewsPostedDistrict);
            txtShareContentDesc = itemView.findViewById(R.id.txtShareContentDesc);
            ivPostedNewsReporterImage = itemView.findViewById(R.id.ivPostedNewsReporterImage);
            viewBottom = itemView.findViewById(R.id.viewBottom);


            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
            txtShareContentDesc.setTypeface(typeface);
        }
    }


    public class InterstitialAdsViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutInterstitial;

        public InterstitialAdsViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutInterstitial = itemView.findViewById(R.id.layoutInterstitial);
        }
    }

    public class NativeAdsViewHolder extends RecyclerView.ViewHolder {
        public NativeAdsViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutNative = itemView.findViewById(R.id.ad_native);
            NativeTemplateStyle styles = new
                    NativeTemplateStyle.Builder().build();
            layoutNative.setStyles(styles);
        }
    }

    public void setNativeAd(NativeAd nativeAd) {
        layoutNative.setNativeAd(nativeAd);
    }

    public class MultiImageViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout layoutViewPager;
        private TextView txtTimesAgoMultiImageView, txtDistrictNameMultiImage, txtShareContentTitle, txtShareContentDesc;
        private ImageView ivWhatsAppViewPager, ivShareViewPager;
        private LinearLayout layoutViewPagerBottom, layoutBottomViewPagerAppName, layoutDots;
        private View viewBottom;

        public MultiImageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutViewPager = itemView.findViewById(R.id.layoutViewPager);
            multiImageViewPager = itemView.findViewById(R.id.multiImageViewPager);
            txtTimesAgoMultiImageView = itemView.findViewById(R.id.txtTimesAgoMultiImageView);
            txtDistrictNameMultiImage = itemView.findViewById(R.id.txtDistrictNameMultiImage);
            layoutDots = itemView.findViewById(R.id.layoutDots);
            ivWhatsAppViewPager = itemView.findViewById(R.id.ivWhatsAppViewPager);
            layoutViewPagerBottom = itemView.findViewById(R.id.layoutViewPagerBottom);
            layoutBottomViewPagerAppName = itemView.findViewById(R.id.layoutBottomViewPagerAppName);
            txtShareContentDesc = itemView.findViewById(R.id.txtShareContentDesc);
            ivShareViewPager = itemView.findViewById(R.id.ivShareViewPager);
            viewBottom = itemView.findViewById(R.id.viewBottom);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
            txtShareContentDesc.setTypeface(typeface);
        }
    }

    private void addDot(int currentPage, LinearLayout layoutDotsLocal, List<PostImagesModel> postImages) {
        try {
            dots = new TextView[postImages.size()];
//            dots = new TextView[newsItemModel1.getPostImages().size()];
            Log.e("dots", "--" + dots.length);
            Log.e("dotsSize", "--" + postImages.size());
            layoutDotsLocal.removeAllViews();
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(mContext);
                dots[i].setText(Html.fromHtml("&#9673;"));
                dots[i].setTextSize(18);
                //set default dot color
                dots[i].setTextColor(mContext.getResources().getColor(R.color.white));
                layoutDotsLocal.addView(dots[i]);
            }
            if (dots.length > 0) {
                Log.e("currentPage", "--" + currentPage);
                dots[currentPage].setTextColor(mContext.getResources().getColor(R.color.red));
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    private class CustomPagerAdapter extends PagerAdapter {
        private Context mContext;
        private List<PostImagesModel> postImagesModelArrayList;

        public CustomPagerAdapter(Context context, List<PostImagesModel> postImagesModelArrayList) {
            this.mContext = context;
            this.postImagesModelArrayList = postImagesModelArrayList;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.multi_image_view_pager, container, false);
            ImageView ivMultiImage = view.findViewById(R.id.ivMultiImage);
            Picasso.get()
                    .load(Config.NEWS_IMAGE_URL + postImagesModelArrayList.get(position).getPostImage())
                    .placeholder(R.drawable.default_placeholder_fullimage)
                    .error(R.drawable.default_placeholder_fullimage)
                    .into(ivMultiImage);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (newsItemClickedInterface != null) {
                        newsItemClickedInterface.newsItemClickedMethod(newsItemModel1.getNews_id(), newsItemModel1.getPostTitleTe());
                    }
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return postImagesModelArrayList.size();
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
            return POSITION_NONE;
        }
    }


    public interface NewsItemClickedInterface {
        void newsItemClickedMethod(String newsId, String newsTitle);

        void newsCommentsClickedMethod(String newsId, String newsTitle);

        void newsFollowClickedMethod(int position, String followerId);

        void newsUnFollowClickedMethod(int position, String followerId);

        void newsAddFavouritesClickedMethod(int position, String newsId);

        void newsRemoveFavouritesClickedMethod(int position, String favoriteId);

        void newsShareClickedMethod(String newsId, String newsTitle, View bottomView, View timesAgoView, View layoutBottom, View bottomLineView);

        void newsTakeScreenshotMethod(String newsId, String newsTitle, View bottomView, View timesAgoView, View layoutBottom, View bottomLineView);

        void newsSpeakerClickedMethod(String newsTitle, String newsDescription);

        void newsSpeakerStopClickedMethod(String newsTitle, String newsDescription);

        void newsViewTypeMethod(String viewType, String newsId, int position);

        void newsWhatsAppShareVideoView(String newsTitle, String newsId);

        void newsCommonShareVideoView(String newsTitle, String newsId);

        void newsShareFullImage(String newsId, String newsTitle, View whatsAppView, View fullImageAppNameShare, String fullImageShare);

        void playAndWin(String url);

        void showInterstitialAd();

        void showNativeAds();
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 1 && position % 5 == 0) {
            return NATIVE_VIEW;
        } else {
            if ((!newsItemModelArrayList.get(position).getPostImages().get(0).getPostImage().isEmpty() && newsItemModelArrayList.get(position).getFullscreen().equalsIgnoreCase("0") && newsItemModelArrayList.get(position).getPostImages().size() == 1) || !newsItemModelArrayList.get(position).getYoutube_link().isEmpty() || !newsItemModelArrayList.get(position).getPostVideo().isEmpty()) {
                return IMAGE_VIEW_TYPE;
            } else if (!newsItemModelArrayList.get(position).getPostImages().get(0).getPostImage().isEmpty() && newsItemModelArrayList.get(position).getFullscreen().equalsIgnoreCase("1")) {
                return FULL_IMAGE_VIEW_TYPE;
            } else if (newsItemModelArrayList.get(position).getPostImages().size() > 1) {
                return VIEW_PAGER_VIEW_TYPE;
            }
            return DEFAULT_VIEW_TYPE;
        }
    }

    public void releasePlayer() {
        if (exoPlayer != null) {
            int state = exoPlayer.getPlaybackState();
            switch (state) {
                case SimpleExoPlayer.STATE_BUFFERING:
                    break;
                case SimpleExoPlayer.STATE_READY:
                    exoPlayer.setPlayWhenReady(false);
                    break;
                case SimpleExoPlayer.STATE_IDLE:
                    break;
                case SimpleExoPlayer.STATE_ENDED:
                    break;
            }
        }
    }
}
