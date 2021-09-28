
package com.daily_smart.news_app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;

public class MyCustomApplication extends Application {
    private static Context applicationContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        FirebaseApp.initializeApp(this);
        AudienceNetworkAds.initialize(this);
        MobileAds.initialize(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public static Context getContext() {
        return applicationContext;
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
