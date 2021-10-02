package com.paxees_daily_smart.paxees_news_app.Models;

import java.io.Serializable;

public class AdsSettingsModel implements Serializable {
    private String id;
    private String AdMobPublisherId;
    private String AdMobAppId;
    private String FacebookNativeBannerID;
    private String BannerAdMobId;
    private String BannerFacebookId;
    private String bannertype;
    private String InterstitialAdMobId;
    private String InterstitialFacebookId;
    private String interstitialtype;
    private String ClickbetweeninterstitialAd;
    private String NativeAdMobId;
    private String NativeFacebookId;
    private String nativetype;
    private String ItemsbetweenNativeAds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdMobPublisherId() {
        return AdMobPublisherId;
    }

    public void setAdMobPublisherId(String adMobPublisherId) {
        AdMobPublisherId = adMobPublisherId;
    }

    public String getAdMobAppId() {
        return AdMobAppId;
    }

    public void setAdMobAppId(String adMobAppId) {
        AdMobAppId = adMobAppId;
    }

    public String getFacebookNativeBannerID() {
        return FacebookNativeBannerID;
    }

    public void setFacebookNativeBannerID(String facebookNativeBannerID) {
        FacebookNativeBannerID = facebookNativeBannerID;
    }

    public String getBannerAdMobId() {
        return BannerAdMobId;
    }

    public void setBannerAdMobId(String bannerAdMobId) {
        BannerAdMobId = bannerAdMobId;
    }

    public String getBannerFacebookId() {
        return BannerFacebookId;
    }

    public void setBannerFacebookId(String bannerFacebookId) {
        BannerFacebookId = bannerFacebookId;
    }

    public String getBannertype() {
        return bannertype;
    }

    public void setBannertype(String bannertype) {
        this.bannertype = bannertype;
    }

    public String getInterstitialAdMobId() {
        return InterstitialAdMobId;
    }

    public void setInterstitialAdMobId(String interstitialAdMobId) {
        InterstitialAdMobId = interstitialAdMobId;
    }

    public String getInterstitialFacebookId() {
        return InterstitialFacebookId;
    }

    public void setInterstitialFacebookId(String interstitialFacebookId) {
        InterstitialFacebookId = interstitialFacebookId;
    }

    public String getInterstitialtype() {
        return interstitialtype;
    }

    public void setInterstitialtype(String interstitialtype) {
        this.interstitialtype = interstitialtype;
    }

    public String getClickbetweeninterstitialAd() {
        return ClickbetweeninterstitialAd;
    }

    public void setClickbetweeninterstitialAd(String clickbetweeninterstitialAd) {
        ClickbetweeninterstitialAd = clickbetweeninterstitialAd;
    }

    public String getNativeAdMobId() {
        return NativeAdMobId;
    }

    public void setNativeAdMobId(String nativeAdMobId) {
        NativeAdMobId = nativeAdMobId;
    }

    public String getNativeFacebookId() {
        return NativeFacebookId;
    }

    public void setNativeFacebookId(String nativeFacebookId) {
        NativeFacebookId = nativeFacebookId;
    }

    public String getNativetype() {
        return nativetype;
    }

    public void setNativetype(String nativetype) {
        this.nativetype = nativetype;
    }

    public String getItemsbetweenNativeAds() {
        return ItemsbetweenNativeAds;
    }

    public void setItemsbetweenNativeAds(String itemsbetweenNativeAds) {
        ItemsbetweenNativeAds = itemsbetweenNativeAds;
    }


}
