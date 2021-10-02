package com.paxees_daily_smart.paxees_news_app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.paxees_daily_smart.paxees_news_app.Activities.NewsDetailsActivity;
import com.paxees_daily_smart.paxees_news_app.Adapters.TrendingAdapter;
import com.paxees_daily_smart.paxees_news_app.CustomViews.ProgressBarDialog;
import com.paxees_daily_smart.paxees_news_app.Models.AdsSettingsModel;
import com.paxees_daily_smart.paxees_news_app.Models.NewsItemModel;
import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.Config;
import com.paxees_daily_smart.paxees_news_app.Utilities.GeneralFunctions;
import com.paxees_daily_smart.paxees_news_app.Utilities.ShareData;
import com.paxees_daily_smart.paxees_news_app.Volley.VolleySingleton;
import com.google.rvadapter.AdmobNativeAdAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TrendingFragment extends Fragment {
    int currentItems, totalItems, scrollOutItems;
    boolean isScrolling = false;
    private RecyclerView recyclerViewTrending;
    private TrendingAdapter trendingAdapter;
    private List<NewsItemModel> newsItemModelArrayList = new ArrayList<>();
    private List<NewsItemModel> addNewsItemsList = new ArrayList<>();
    private ShareData shareData;
    private String userId = Config.EMPTY_STRING;
    private String homeDistrictId = Config.EMPTY_STRING;
    private ProgressBarDialog progressBarDialog;
    private TextView txtNoTrendingNews;
    private LinearLayout layoutInterstitialAds;
    private ProgressBar progressBar;
    //    private AdView adView;
//    private InterstitialAd interstitialAd;
//    private com.google.android.gms.ads.AdView googleAdView;
    private LinearLayout layoutBannerAds;
    public static final int ITEM_PER_AD = 4;
    private String googleBannerId = Config.EMPTY_STRING;
    LinearLayoutManager manager;

    public TrendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View trendingView = inflater.inflate(R.layout.fragment_trending, container, false);
        shareData = new ShareData(getActivity());
        if (shareData.getCustomerId().isEmpty()) {
            userId = "0";
        } else {
            userId = shareData.getCustomerId();
        }
        manager = new LinearLayoutManager(getActivity());
        progressBarDialog = new ProgressBarDialog(getActivity());
        homeDistrictId = shareData.getHomeDistrictId();
        progressBar = trendingView.findViewById(R.id.progressBar);
        recyclerViewTrending = trendingView.findViewById(R.id.recyclerViewTrending);
        txtNoTrendingNews = trendingView.findViewById(R.id.txtNoTrendingNews);
        layoutInterstitialAds = trendingView.findViewById(R.id.layoutInterstitialAds);
        layoutBannerAds = trendingView.findViewById(R.id.layoutBannerAds);
        recyclerViewTrending.setHasFixedSize(true);
        recyclerViewTrending.setLayoutManager(manager);
        recyclerViewTrending.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstCompletelyVisibleItemPosition();
                if (isScrolling ) {
                    isScrolling=false;
                    updateData(totalItems,totalItems+10);
                }
            }
        });
        if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
        } else {
            getAdsSettings();
            getTrendingNews(homeDistrictId);
        }
        return trendingView;
    }

    private void getTrendingNews(final String districtId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.TRENDING_NEWS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(getActivity());
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("news_data");

                                newsItemModelArrayList = NewsItemModel.fromJson(jsonArray);
                                if (newsItemModelArrayList.size() > 0) {
                                    recyclerViewTrending.setVisibility(View.VISIBLE);
                                    txtNoTrendingNews.setVisibility(View.GONE);
                                } else {
                                    recyclerViewTrending.setVisibility(View.GONE);
                                    txtNoTrendingNews.setVisibility(View.VISIBLE);
                                }
                                updateData(0,10);
                                trendingAdapter = new TrendingAdapter(getActivity(), addNewsItemsList, googleBannerId, new TrendingAdapter.TrendingNewsInterface() {
                                    @Override
                                    public void trendingNewsMethod(String newsId) {
                                        Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
                                        intent.putExtra("NewsId", newsId);
                                        startActivity(intent);
                                    }
                                });
                                AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(getActivity().getResources().getString(R.string.nativeAds), trendingAdapter,
                                        "small").adItemInterval(3).build();
                                recyclerViewTrending.setAdapter(admobNativeAdAdapter);
                                trendingAdapter.notifyDataSetChanged();
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
                                Log.e("volleyError", volleyError.getMessage());
                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
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

    private void updateData(int start,int EndNumb) {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = start; i < EndNumb; i++) {
                    addNewsItemsList.add(newsItemModelArrayList.get(i));
                    trendingAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }
        },2000);
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
                                    googleBannerId = adsSettingsModel.getBannerAdMobId();
                                    Log.i("BannerId", googleBannerId);
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

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    @Override
    public void onDestroy() {
       /* if (adView != null) {
            adView.destroy();
        }*/
        super.onDestroy();
    }
}