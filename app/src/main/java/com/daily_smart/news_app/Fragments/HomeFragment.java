package com.daily_smart.news_app.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daily_smart.news_app.Activities.EditProfileActivity;
import com.daily_smart.news_app.Activities.LoginActivity;
import com.daily_smart.news_app.Adapters.HomeCategoriesAdapter;
import com.daily_smart.news_app.Adapters.HomeLatestNewsAdapter;
import com.daily_smart.news_app.Adapters.NewsCommentsAdapter;
import com.daily_smart.news_app.BuildConfig;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
import com.daily_smart.news_app.CustomViews.SubCommentDialog;
import com.daily_smart.news_app.Models.AdsSettingsModel;
import com.daily_smart.news_app.Models.CategoriesModel;
import com.daily_smart.news_app.Models.NewsCommentsModel;
import com.daily_smart.news_app.Models.NewsItemModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Volley.VolleySingleton;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    private int intersitailCount=0;
    private RecyclerView recyclerViewCategories, recyclerViewLatestNews, rvComments;
    private HomeCategoriesAdapter homeCategoriesAdapter;
    private HomeLatestNewsAdapter homeLatestNewsAdapter;
    private NewsCommentsAdapter newsCommentsAdapter;
    private ArrayList<CategoriesModel> categoriesList = new ArrayList<>();
    private ArrayList<NewsItemModel> newsItemModelArrayList = new ArrayList<>();
    private ArrayList<NewsCommentsModel> newsCommentsModelArrayList = new ArrayList<>();
    private PassViewsToActivityListener passViewsToActivityListener;
    private String districtId = Config.EMPTY_STRING;
    private String profileDistrictId = Config.EMPTY_STRING;
    private String homeDistrictId = Config.EMPTY_STRING;
    private TextView txtNoNewsList;
    private LinearLayout layoutNewsBottomView;
    private TextView txtCommentsCount, txtNoComments, txtNewsTitle;
    private EditText etComments;
    private ImageView ivCommentsClose;
    private LinearLayout layoutSendComments;
    private View viewBottomSheetDialog;
    private BottomSheetDialog bottomSheetDialog;
    private ShareData shareData;
    private String userId = Config.EMPTY_STRING;
    private ProgressBarDialog progressBarDialog;
    private ArrayList<String> postImagesList = new ArrayList<>();
    private TextToSpeech textToSpeech;
    private String viewTypeInt = null;
    private String sharePath = Config.EMPTY_STRING;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 100;
    private String mPath = null;
    private String newsTitleString = Config.EMPTY_STRING;
    private String newsIdString = Config.EMPTY_STRING;
    private String fullImageShareString = Config.EMPTY_STRING;
    private View bottom_view = null;
    private View fullImage_app_name_share = null;
    private View times_ago_view = null;
    private View layout_bottom = null;
    private View view_line = null;
    private LinearLayoutManager linearLayoutManager;
    private int scrolledPosition = 0;
    private InterstitialAd mInterstitialAd;
    private NativeAd mNativeAd;
    private ScheduledExecutorService scheduledExecutorService;
    private SubCommentDialog subCommentDialog;
    private String commentsNewsTitle = Config.EMPTY_STRING;
    private String googleNativeId = Config.EMPTY_STRING;
    private View layoutNativeAds;
    TemplateView adTemplateNative;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            passViewsToActivityListener = (PassViewsToActivityListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's state here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("onCreateView", "onCreateView");

        // Inflate the layout for this fragment
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewCategories = homeView.findViewById(R.id.recyclerViewCategories);
        recyclerViewLatestNews = homeView.findViewById(R.id.recyclerViewLatestNews);
        txtNoNewsList = homeView.findViewById(R.id.txtNoNewsList);
        layoutNewsBottomView = homeView.findViewById(R.id.layoutNewsBottomView);
        adTemplateNative = homeView.findViewById(R.id.ad_native);
        passViewsToActivityListener.passViewsToActivityInitialMethod(recyclerViewCategories);
        shareData = new ShareData(getActivity());
        if (shareData.getCustomerId().isEmpty()) {
            userId = "0";
        } else {
            userId = shareData.getCustomerId();
        }
        progressBarDialog = new ProgressBarDialog(getActivity());
        homeDistrictId = shareData.getHomeDistrictId();
        profileDistrictId = shareData.getDistrictId();
        districtId = homeDistrictId;
        recyclerViewCategories.setHasFixedSize(true);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewLatestNews.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewLatestNews.setLayoutManager(linearLayoutManager);

        /*recyclerViewLatestNews.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llm = (LinearLayoutManager) recyclerViewLatestNews.getLayoutManager();
               int displayedposition = llm.findFirstVisibleItemPosition();
               if(displayedposition % 15!=0){
//                   Log.i("ScrollViewInit", "ads Show "+displayedposition);
//                   prepareInterstitialAd("ca-app-pub-3940256099942544/1033173712");
               }

            }
        });*/

        if (shareData.getAuthenticationId().isEmpty()) {
            deviceRegistration(shareData.getAuthenticationDeviceId(), shareData.getFcmToken());
        }
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewLatestNews);
        if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
        } else {
            getCategoriesList();
            getNewsBasedOnCategory("1", districtId);
        }

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mInterstitialAd.show(getActivity());
//                        prepareInterstitialAd();
                    }
                });
            }
        }, 60, 15, TimeUnit.SECONDS); // display every 15 seconds

        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
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

        recyclerViewLatestNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (textToSpeech != null) {
                        textToSpeech.stop();
                    }
                } else if (dy < 0) {
                    if (textToSpeech != null) {
                        textToSpeech.stop();
                    }
                }
            }
        });
        subCommentDialog = new SubCommentDialog(getActivity(), new SubCommentDialog.PostSubCommentsInterface() {
            @Override
            public void postSubCommentsMethod(Dialog dialog, String subComments, String userId, String newsId, String commentId) {
                if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                    GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                } else {
                    dialog.dismiss();
                    postSubComments(userId, newsId, subComments, commentId);
                }
            }

        });
//        layoutNativeAds = homeView.findViewById(R.id.ad_native);
//        MobileAds.initialize(homeView.getContext(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
//                AdLoader adLoader = new AdLoader.Builder(homeView.getContext(), "ca-app-pub-3940256099942544/2247696110")
//                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                            @Override
//                            public void onNativeAdLoaded(NativeAd nativeAd) {
//                                // Show the ad.
//                                NativeTemplateStyle styles = new
//                                        NativeTemplateStyle.Builder().build();
//                                TemplateView template = getActivity().findViewById(R.id.my_template);
//                                template.setStyles(styles);
//                                template.setNativeAd(nativeAd);
//                            }
//                        })
//                        .withAdListener(new AdListener() {
//                            @Override
//                            public void onAdFailedToLoad(LoadAdError adError) {
//                                // Handle the failure by logging, altering the UI, and so on.
//                            }
//                        })
//                        .withNativeAdOptions(new NativeAdOptions.Builder()
//                                // Methods in the NativeAdOptions.Builder class can be
//                                // used here to specify individual options settings.
//                                .build())
//                        .build();
//                adLoader.loadAd(new AdRequest.Builder().build());
//            }
//        });
        return homeView;
    }
    private void mapNativeAdView(NativeAd adFromGoogle, NativeAdView myAdView) {
        MediaView mediaView = myAdView.findViewById(R.id.ad_media);
        myAdView.setMediaView(mediaView);

        myAdView.setHeadlineView(myAdView.findViewById(R.id.ad_headline));
        myAdView.setBodyView(myAdView.findViewById(R.id.ad_body));
        myAdView.setCallToActionView(myAdView.findViewById(R.id.ad_call_to_action));
        myAdView.setIconView(myAdView.findViewById(R.id.ad_icon));
        myAdView.setPriceView(myAdView.findViewById(R.id.ad_price));
        myAdView.setStarRatingView(myAdView.findViewById(R.id.ad_rating));
        myAdView.setStoreView(myAdView.findViewById(R.id.ad_store));
        myAdView.setAdvertiserView(myAdView.findViewById(R.id.ad_advertiser));

        ((TextView) myAdView.getHeadlineView()).setText(adFromGoogle.getHeadline());

        if (adFromGoogle.getBody() == null) {
            myAdView.getBodyView().setVisibility(View.GONE);
        } else {
            ((TextView) myAdView.getBodyView()).setText(adFromGoogle.getBody());
        }

        if (adFromGoogle.getCallToAction() == null) {
            myAdView.getCallToActionView().setVisibility(View.GONE);
        } else {
            ((Button) myAdView.getCallToActionView()).setText(adFromGoogle.getCallToAction());
        }

        if (adFromGoogle.getIcon() == null) {
            myAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) myAdView.getIconView()).setImageDrawable(adFromGoogle.getIcon().getDrawable());
        }

        if (adFromGoogle.getPrice() == null) {
            myAdView.getPriceView().setVisibility(View.GONE);
        } else {
            ((TextView) myAdView.getPriceView()).setText(adFromGoogle.getPrice());
        }

        if (adFromGoogle.getStarRating() == null) {
            myAdView.getStarRatingView().setVisibility(View.GONE);
        } else {
            ((RatingBar) myAdView.getStarRatingView()).setRating(adFromGoogle.getStarRating().floatValue());
        }

        if (adFromGoogle.getStore() == null) {
            myAdView.getStoreView().setVisibility(View.GONE);
        } else {
            ((TextView) myAdView.getStoreView()).setText(adFromGoogle.getStore());
        }

        if (adFromGoogle.getAdvertiser() == null) {
            myAdView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            ((TextView) myAdView.getAdvertiserView()).setText(adFromGoogle.getAdvertiser());
        }
        myAdView.setNativeAd(adFromGoogle);
    }

    private void prepareNativeAd(final String nativeAdMobId) {
        Log.i("Checking","is Working");
//        MobileAds.initialize(getActivity());
//            @Override
//            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
//                NativeAdView nativeAdView = (NativeAdView) getLayoutInflater().inflate(R.layout.native_ad_layout, null);
//                mapNativeAdView(nativeAd, nativeAdView);
//
//                FrameLayout nativeAdLayout = getActivity().findViewById(R.id.id_native_ad);
//                nativeAdLayout.removeAllViews();
//                nativeAdLayout.addView(nativeAdView);
//
//            }
//
//
//        }).build();
//        adLoader.loadAd(new AdRequest.Builder().build());
//        MobileAds.initialize(Objects.requireNonNull(getContext()), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                //           }
//        });

    }


    private void deviceRegistration(final String deviceId, final String fcmToken) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DEVICE_REG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                shareData.setAuthenticationId("FCM");
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
                                Log.e("Exception", e.getMessage());
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("fcm_token", fcmToken);
                    params.put("device_id", deviceId);
                    params.put("district_id", homeDistrictId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void sendNewsViewCount(final String newsId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.NEWS_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void getCategoriesList() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.CATEGORIES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                categoriesList = CategoriesModel.fromJson(jsonArray);
                                homeCategoriesAdapter = new HomeCategoriesAdapter(getActivity(), categoriesList, new HomeCategoriesAdapter.CategoryClickedListener() {
                                    @Override
                                    public void categoryClickedMethod(String categoryId) {
                                        getNewsBasedOnCategory(categoryId, districtId);
                                    }
                                });

                                recyclerViewCategories.setAdapter(homeCategoriesAdapter);
                                homeCategoriesAdapter.notifyDataSetChanged();
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
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        } catch (Exception ex) {
            //todo
            Log.e("MainException", ex.getMessage());
        }
    }

    private void getNewsBasedOnCategory(final String categoryId, final String districtId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.NEWS_BASED_ON_CATEGORIES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(getActivity());
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("news_data");
                                newsItemModelArrayList = NewsItemModel.fromJson(jsonArray);
                                if (newsItemModelArrayList.size() > 0) {
                                    recyclerViewLatestNews.setVisibility(View.VISIBLE);
                                    layoutNewsBottomView.setVisibility(View.GONE);
                                    txtNoNewsList.setVisibility(View.GONE);
                                } else {
                                    recyclerViewLatestNews.setVisibility(View.GONE);
                                    layoutNewsBottomView.setVisibility(View.GONE);
                                    txtNoNewsList.setVisibility(View.VISIBLE);
                                }

                                homeLatestNewsAdapter = new HomeLatestNewsAdapter(getActivity(), newsItemModelArrayList, getActivity().getLifecycle(),googleNativeId, new HomeLatestNewsAdapter.NewsItemClickedInterface() {
                                    @Override
                                    public void newsItemClickedMethod(final String newsId, final String newsTitle) {
                                        passViewsToActivityListener.passViewsToActivityListenerMethod(recyclerViewCategories, recyclerViewLatestNews, "1");
                                    }

                                    @Override
                                    public void newsCommentsClickedMethod(final String newsId, final String newsTitle) {
                                        bottomSheetDialog = new BottomSheetDialog(getActivity());
                                        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.news_comments_bottomsheet_dialog, null);
                                        commentsNewsTitle = newsTitle;
                                        txtCommentsCount = sheetView.findViewById(R.id.txtCommentsCount);
                                        txtNewsTitle = sheetView.findViewById(R.id.txtNewsTitle);
                                        rvComments = sheetView.findViewById(R.id.rvComments);
                                        etComments = sheetView.findViewById(R.id.etComments);
                                        ivCommentsClose = sheetView.findViewById(R.id.ivCommentsClose);
                                        viewBottomSheetDialog = sheetView.findViewById(R.id.viewBottomSheetDialog);
                                        txtNoComments = sheetView.findViewById(R.id.txtNoComments);
                                        layoutSendComments = sheetView.findViewById(R.id.layoutSendComments);
                                        txtNewsTitle.setText(newsTitle);
                                        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "lohit_telugu.ttf");
                                        txtNewsTitle.setTypeface(typeface);

                                        ivCommentsClose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                bottomSheetDialog.dismiss();
                                            }
                                        });
                                        layoutSendComments.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                                    GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                                                } else {
                                                    if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                        intent.putExtra("DistrictId", districtId);
                                                        startActivity(intent);
                                                    } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                                                        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                                        startActivity(intent);
                                                    } else {
                                                        if (etComments.getText().toString().trim().isEmpty()) {
                                                            GeneralFunctions.showToast("Please write comment", getActivity());
                                                        } else {
                                                            postComments(userId, newsId, etComments.getText().toString().trim(), newsTitle);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                        bottomSheetDialog.setContentView(sheetView);
                                        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialogInterface) {
                                                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                                                setupFullHeight(bottomSheetDialog);
                                            }
                                        });
                                        bottomSheetDialog.show();
                                        etComments.setText("");
                                        getNewsComments(newsId, rvComments, newsTitle, userId);
                                    }

                                    @Override
                                    public void newsFollowClickedMethod(int position, String followerId) {
                                        if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            intent.putExtra("DistrictId", districtId);
                                            startActivity(intent);
                                        } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                                            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                            startActivity(intent);
                                        } else {
                                            if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                                GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                                            } else {
                                                addFollower(position, userId, followerId);
                                            }
                                        }
                                    }

                                    @Override
                                    public void newsUnFollowClickedMethod(int position, String followerId) {
                                        if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            intent.putExtra("DistrictId", districtId);
                                            startActivity(intent);
                                        } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                                            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                            startActivity(intent);
                                        } else {
                                            if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                                GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                                            } else {
                                                removeFollowers(position, followerId);
                                            }
                                        }
                                    }

                                    @Override
                                    public void newsAddFavouritesClickedMethod(int position, String newsId) {
                                        if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                                        } else {
                                            if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                intent.putExtra("DistrictId", districtId);
                                                startActivity(intent);
                                            } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                                                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                                startActivity(intent);
                                            } else {
                                                addFavourites(position, userId, newsId);
                                            }
                                        }
                                    }

                                    @Override
                                    public void newsRemoveFavouritesClickedMethod(int position, String favoriteId) {
                                        if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                                        } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                                            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                            startActivity(intent);
                                        } else {
                                            if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                intent.putExtra("DistrictId", districtId);
                                                startActivity(intent);
                                            } else {
                                                removeFavourites(position, favoriteId);
                                            }
                                        }
                                    }

                                    @Override
                                    public void newsShareClickedMethod(final String newsId, String newsTitle, final View bottomView, final View timesAgoView, final View layoutBottom, final View viewLine) {
                                        newsTitleString = newsTitle;
                                        newsIdString = newsId;
                                        bottom_view = bottomView;
                                        times_ago_view = timesAgoView;
                                        layout_bottom = layoutBottom;
                                        view_line = viewLine;
                                        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
                                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
                                        } else {
                                            bottomView.setVisibility(View.VISIBLE);
                                            timesAgoView.setVisibility(View.GONE);
                                            layoutBottom.setVisibility(View.GONE);
                                            viewLine.setVisibility(View.GONE);
                                            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                            final View decorView = getActivity().getWindow().getDecorView();
                                            final int uiOptions =
                                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                                            decorView.setSystemUiVisibility(uiOptions);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    takeScreenShot(newsId, getActivity().getWindow().getDecorView().getRootView(), newsTitleString, bottomView, timesAgoView, layoutBottom, viewLine);
                                                }
                                            }, 150);
                                        }
                                        postShareCount(newsId);
                                    }

                                    @Override
                                    public void newsTakeScreenshotMethod(final String newsId, String newsTitle, final View bottomView, final View timesAgoView, final View layoutBottom, final View viewLine) {
                                        newsTitleString = newsTitle;
                                        newsIdString = newsId;
                                        bottom_view = bottomView;
                                        times_ago_view = timesAgoView;
                                        layout_bottom = layoutBottom;
                                        view_line = viewLine;
                                        Log.e("NewsId", "--" + newsId);
                                    }

                                    @Override
                                    public void newsSpeakerClickedMethod(String newsTitle, String newsDescription) {
                                        String text = newsDescription;
                                        if (text.equalsIgnoreCase("null") || text.isEmpty()) {
                                            text = "Content not available";
                                        }
                                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                        homeLatestNewsAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void newsSpeakerStopClickedMethod(String newsTitle, String newsDescription) {
                                        if (textToSpeech != null) {
                                            textToSpeech.stop();
                                        }
                                    }

                                    @Override
                                    public void newsViewTypeMethod(String viewType, String newsId, int position) {
                                        viewTypeInt = viewType;
                                        sendNewsViewCount(newsId);
//                                        getAdsSettings();
                                        if (viewType.equalsIgnoreCase("2") || viewType.equalsIgnoreCase("4")) {
                                            passViewsToActivityListener.passViewsToActivityListenerMethod(recyclerViewCategories, recyclerViewLatestNews, viewTypeInt);
                                        }
                                    }

                                    @Override
                                    public void newsWhatsAppShareVideoView(String newsTitle, String newsId) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setPackage("com.whatsapp");
                                        intent.putExtra(Intent.EXTRA_TEXT, newsTitle + " - " + getString(R.string.app_name) + "\n" + "https://bharatshorts.in/news-details.php?nid=" + newsId);
                                        try {
                                            startActivity(intent);
                                        } catch (android.content.ActivityNotFoundException ex) {
                                            Log.e("ActivityNotFound", "ActivityNotFoundException");
                                        }
                                    }

                                    @Override
                                    public void newsCommonShareVideoView(String newsTitle, String newsId) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_TEXT, newsTitle + " - " + getString(R.string.app_name) + "\n" + "https://bharatshorts.in/news-details.php?nid=" + newsId);
                                        try {
                                            getActivity().startActivity(Intent.createChooser(intent, "Share With"));
                                        } catch (ActivityNotFoundException e) {
                                            GeneralFunctions.showToast("No App Available", getActivity());
                                        }
                                    }

                                    @Override
                                    public void newsShareFullImage(final String newsId, String newsTitle, final View whatsAppView, final View fullImageAppNameShare, String fullImageShare) {
                                        fullImageShareString = fullImageShare;
                                        newsTitleString = newsTitle;
                                        bottom_view = whatsAppView;
                                        newsIdString = newsId;
                                        fullImage_app_name_share = fullImageAppNameShare;
                                        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
                                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
                                        } else {
                                            whatsAppView.setVisibility(View.GONE);
                                            fullImageAppNameShare.setVisibility(View.VISIBLE);
                                            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                            final View decorView = getActivity().getWindow().getDecorView();
                                            final int uiOptions =
                                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                                            decorView.setSystemUiVisibility(uiOptions);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    boolean installed = appInstalledOrNot("com.whatsapp");
                                                    if (installed) {
                                                        try {
                                                            takeFullImageScreenShot(getActivity().getWindow().getDecorView().getRootView(), newsId, newsTitleString, whatsAppView, fullImageAppNameShare);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        GeneralFunctions.showAlert(getActivity(), "Currently WhatsApp not installed in your device");
                                                    }
                                                }
                                            }, 150);
                                        }
                                    }

                                    @Override
                                    public void playAndWin(String url) {
                                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                                            url = "http://" + url;
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(browserIntent);
                                    }

                                    @Override
                                    public void showInterstitialAd() {
                                        getAdsSettings();
                                    }

                                    @Override
                                    public void showNativeAds(){
                                        getAdsSettings();
                                    }
                                });
                                recyclerViewLatestNews.setAdapter(homeLatestNewsAdapter);
                                recyclerViewLatestNews.scrollToPosition(scrolledPosition);
                                homeLatestNewsAdapter.notifyDataSetChanged();
                            } catch (Exception ex) {
                                Log.e("Exception", ex.getMessage());
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(getActivity());
                                Log.e("volleyError", volleyError.getMessage());
                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    Log.e("user_id", "--" + userId);
                    params.put("category_id", categoryId);
                    params.put("district_id", districtId);
                    params.put("user_id", userId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(getActivity());
        } catch (Exception ex) {
            //todo
        }
    }

    private void getAdsSettings() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.ADS_SETTINGS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    AdsSettingsModel adsSettingsModel = new AdsSettingsModel();
                                    adsSettingsModel.setId(jsonObject.getString("id"));
                                    adsSettingsModel.setAdMobPublisherId(jsonObject.getString("AdMobPublisherId"));
                                    adsSettingsModel.setAdMobAppId(jsonObject.getString("AdMobAppId"));
                                    adsSettingsModel.setFacebookNativeBannerID(jsonObject.getString("FacebookNativeBannerID"));
                                    adsSettingsModel.setBannerAdMobId(jsonObject.getString("BannerAdMobId"));
                                    adsSettingsModel.setBannerFacebookId(jsonObject.getString("BannerFacebookId"));
                                    adsSettingsModel.setBannertype(jsonObject.getString("bannertype"));
                                    adsSettingsModel.setInterstitialAdMobId(jsonObject.getString("InterstitialAdMobId"));
                                    adsSettingsModel.setInterstitialFacebookId(jsonObject.getString("InterstitialFacebookId"));
                                    adsSettingsModel.setInterstitialtype(jsonObject.getString("interstitialtype"));
                                    adsSettingsModel.setClickbetweeninterstitialAd(jsonObject.getString("ClickbetweeninterstitialAd"));
                                    adsSettingsModel.setNativeAdMobId(jsonObject.getString("NativeAdMobId"));
                                    adsSettingsModel.setNativeFacebookId(jsonObject.getString("NativeFacebookId"));
                                    adsSettingsModel.setNativetype(jsonObject.getString("nativetype"));
                                    adsSettingsModel.setItemsbetweenNativeAds(jsonObject.getString("ItemsbetweenNativeAds"));
//                                    prepareInterstitialAd(adsSettingsModel.getInterstitialAdMobId());
//                                    prepareNativeAd(adsSettingsModel.getNativeAdMobId());
                                    Log.d("AdIdInters",adsSettingsModel.getInterstitialAdMobId());
                                    Log.d("AdIdNative",adsSettingsModel.getNativeAdMobId());
                                    googleNativeId = adsSettingsModel.getNativeAdMobId();
                                }
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
               /* @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", userId);
                    return params;
                }*/
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
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
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private boolean appInstalledOrNot(String packageName) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeScreenShot(newsIdString, getActivity().getWindow().getDecorView().getRootView(), newsTitleString, bottom_view, times_ago_view, layout_bottom, view_line);
                    if (fullImageShareString.equalsIgnoreCase("2")) {
                        takeFullImageScreenShot(getActivity().getWindow().getDecorView().getRootView(), newsIdString, newsTitleString, bottom_view, fullImage_app_name_share);
                    }
                } else {
                    GeneralFunctions.showToast("Permission not granted!", getActivity());
                }
                break;
            default:
                break;
        }
    }

    private void takeScreenShot(String newsId, View rootView, String newsTitleString, View bottomView,
                                View timesAgoView, View layoutBottom, View viewLine) {
        Date date = new Date();
        CharSequence format = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date);
        try {
            File mainDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FilShare");
            if (!mainDir.exists()) {
                boolean mkdir = mainDir.mkdir();
            }
            String path = mainDir + "/" + getResources().getString(R.string.app_name) + "-" + format + ".jpeg";
            rootView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);
            File imageFile = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            shareScreenShot(newsId, imageFile, newsTitleString, bottomView, timesAgoView, layoutBottom, viewLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareScreenShot(String newsId, File imageFile, String newsTitleString, View
            bottomView, View timesAgoView, View layoutBottom, View viewLine) {
        bottomView.setVisibility(View.GONE);
        timesAgoView.setVisibility(View.VISIBLE);
        layoutBottom.setVisibility(View.VISIBLE);
        viewLine.setVisibility(View.VISIBLE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View decorView = getActivity().getWindow().getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
        //Using sub-class of Content provider
        Uri uri = FileProvider.getUriForFile(
                getActivity(),
                BuildConfig.APPLICATION_ID + ".fileprovider",
                imageFile);
        //Explicit intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_SUBJECT, newsTitleString);
        intent.putExtra(Intent.EXTRA_TEXT, newsTitleString + "\n" + "https://bharatshorts.in/news-details.php?nid=" + newsId);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        //It will show the application which are available to share Image; else Toast message will throw.
        try {
            this.startActivity(Intent.createChooser(intent, "Share With"));
        } catch (ActivityNotFoundException e) {
            GeneralFunctions.showToast("No App Available", getActivity());
        }
    }

    private void takeFullImageScreenShot(View rootView, String newsId, String newsTitleString, View
            bottomView, View fullImageAppNameShare) {

        //This is used to provide file name with Date a format
        Date date = new Date();
        CharSequence format = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date);

        //It will make sure to store file to given below Directory and If the file Directory dosen't exist then it will create it.
        try {
            File mainDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FilShare");
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
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

//Create New Method to take ScreenShot with the imageFile.
            shareFullImageScreenShot(imageFile, newsId, newsTitleString, bottomView, fullImageAppNameShare);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void shareFullImageScreenShot(File imageFile, String newsId, String newsTitleString, View
            bottomView, View fullImageAppNameShare) {
        bottomView.setVisibility(View.VISIBLE);
        fullImageAppNameShare.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View decorView = getActivity().getWindow().getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
        //Using sub-class of Content provider
        Uri uri = FileProvider.getUriForFile(
                getActivity(),
                BuildConfig.APPLICATION_ID + ".fileprovider",
                imageFile);

        //Explicit intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT, newsTitleString + "\n" + "https://bharatshorts.in/news-details.php?nid=" + newsId);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("ActivityNotFound", "ActivityNotFoundException");
        }
    }

    private void addFollower(final int position, final String userId, final String followerId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ADD_FOLLOWER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, getActivity());
                                scrolledPosition = position;
                                getNewsBasedOnCategory("1", districtId);
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
                    params.put("follower_id", followerId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }


    private boolean checkAppInstall(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private void postComments(final String userId, final String newsId, final String comments,
                              final String newsTitle) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.POST_COMMENTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, getActivity());
                                etComments.setText("");
                                getNewsComments(newsId, rvComments, newsTitle, userId);
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void postSubComments(final String userId, final String newsId, final String comments, final String commentId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.POST_SUB_COMMENTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                getNewsComments(newsId, rvComments, "", userId);
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
                    params.put("sub_comment", comments);
                    params.put("comment_id", commentId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void addFavourites(final int position, final String userId, final String newsId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ADD_FAVOURITES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, getActivity());
                                scrolledPosition = position;
                                getNewsBasedOnCategory("1", districtId);
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void removeFavourites(final int position, final String favoriteId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REMOVE_FAVOURITES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, getActivity());
                                scrolledPosition = position;
                                getNewsBasedOnCategory("1", districtId);
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void removeFollowers(final int position, final String followerId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REMOVE_FOLLOWER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                GeneralFunctions.showToast(message, getActivity());
                                scrolledPosition = position;
                                getNewsBasedOnCategory("1", districtId);
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void getNewsComments(final String newsId, final RecyclerView rvComments, final String newsTitle, final String userId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_COMMENTS,
                    new Response.Listener<String>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(getActivity());
                                JSONObject jsonObject = new JSONObject(response);
//                                txtNewsTitle.setText(newsTitle);
                                txtCommentsCount.setText(getResources().getString(R.string.comments) + " ( " + 0 + " )");
                                newsCommentsModelArrayList.clear();
                                JSONArray jsonArray = jsonObject.getJSONArray("comments");
                                newsCommentsModelArrayList = NewsCommentsModel.fromJson(jsonArray);
                                if (newsCommentsModelArrayList.size() > 0) {
                                    txtCommentsCount.setText(getResources().getString(R.string.comments) + " ( " + newsCommentsModelArrayList.size() + " )");
                                    rvComments.setVisibility(View.VISIBLE);
                                    txtNoComments.setVisibility(View.GONE);
                                } else {
                                    rvComments.setVisibility(View.GONE);
                                    txtNoComments.setVisibility(View.VISIBLE);
                                }
                                newsCommentsAdapter = new NewsCommentsAdapter(getActivity(), newsCommentsModelArrayList, new NewsCommentsAdapter.CommentsLikeInterface() {
                                    @Override
                                    public void commentsLiked(String userId, String newsId, String commentId, String CommentString) {
                                        if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                                        } else {
                                            if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                intent.putExtra("DistrictId", districtId);
                                                startActivity(intent);
                                            } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                                                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                                startActivity(intent);
                                            } else {
                                                likeComment(userId, newsId, commentId, CommentString);
                                            }
                                        }
                                    }

                                    @Override
                                    public void commentsUnLiked(String userId, String newsId, String likeId, String CommentString) {
                                        if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                                        } else {
                                            if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                intent.putExtra("DistrictId", districtId);
                                                startActivity(intent);
                                            } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                                                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                                startActivity(intent);
                                            } else {
                                                unLikeComment(userId, newsId, likeId, CommentString);
                                            }
                                        }
                                    }

                                    @Override
                                    public void replyComments(String userId, String newsId, String commentId) {
                                        if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            intent.putExtra("DistrictId", districtId);
                                            startActivity(intent);
                                        } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                                            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                            startActivity(intent);
                                        } else {
                                            subCommentDialog.showLoginDialog(userId, newsId, commentId, commentsNewsTitle);
                                        }
                                    }
                                });
                                rvComments.setLayoutManager(new LinearLayoutManager(getActivity()));
                                rvComments.setHasFixedSize(true);
                                rvComments.setAdapter(newsCommentsAdapter);
                                newsCommentsAdapter.notifyDataSetChanged();

                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(getActivity());
                            } catch (Exception e) {

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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(getActivity());
        } catch (Exception ex) {
            //todo
        }
    }

    private void unLikeComment(final String userId, final String newsId, final String likeId, final String commentString) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UNLIKE_COMMENTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
//                                GeneralFunctions.showToast(jsonObject.getString("message"),getActivity());
                                if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                    GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                                } else {
                                    getNewsComments(newsId, rvComments, "", userId);
                                }
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
                    if (commentString.equalsIgnoreCase("Comment")) {
                        params.put("like_id", likeId);
                    } else {
                        params.put("sub_like_id", likeId);
                    }
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void likeComment(final String userId, final String newsId, final String commentId, final String commentString) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LIKE_COMMENTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
//                                GeneralFunctions.showToast(jsonObject.getString("message"),getActivity());
                                if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                    GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
                                } else {
                                    getNewsComments(newsId, rvComments, "", userId);
                                }
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
                    params.put("user_id", userId);
                    if (commentString.equalsIgnoreCase("Comment")) {
                        params.put("comment_id", commentId);
                    } else {
                        params.put("sub_comment_id", commentId);
                    }
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
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
//                                GeneralFunctions.showToast(jsonObject.getString("message"),getActivity());
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
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    public interface PassViewsToActivityListener {
        void passViewsToActivityListenerMethod(RecyclerView recyclerView, RecyclerView recyclerView1, String viewType);

        void passViewsToActivityInitialMethod(RecyclerView recyclerView);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStopFrag", "onStopFrag");
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("onStart", "onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroyFrag", "onDestroyFrag");
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scheduledExecutorService.shutdown();
        Log.e("onPauseFrag", "onPauseFrag");
        if (homeLatestNewsAdapter != null) {
            homeLatestNewsAdapter.releasePlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        scheduledExecutorService.shutdown();
        Log.e("onDestroyView", "onDestroyView");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResumeFrag", "onResumeFrag");
    }
}