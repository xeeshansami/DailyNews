package com.daily_smart.news_app.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
import com.daily_smart.news_app.Models.TourScreenModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    private ShareData shareData;
    private TextView txtQuotation;
    private ProgressBarDialog progressBarDialog = null;
    private ArrayList<TourScreenModel> tourScreenModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        shareData = new ShareData(this);
        progressBarDialog = new ProgressBarDialog(this);
        txtQuotation = findViewById(R.id.txtQuotation);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "ramabhadra_regular.ttf");
        txtQuotation.setTypeface(typeface);

        if (!GeneralFunctions.isNetworkAvailable(this)) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), this);
        } else {
            getSplashQuotes();
        }
    }

    private void getSplashQuotes() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.TOUR_SCREEN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(SplashScreen.this);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("splash_data");
                                tourScreenModelArrayList = TourScreenModel.fromJson(jsonArray);
                                for (TourScreenModel tourScreenModel: tourScreenModelArrayList){
                                    tourScreenModel = tourScreenModelArrayList.get(0);
                                    txtQuotation.setText(tourScreenModel.getDescription());
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!shareData.isFirstTimeLaunch()) {
                                            if (!shareData.getHomeDistrictId().isEmpty()) {
                                                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                            } else {
                                                startActivity(new Intent(SplashScreen.this, StatesActivity.class));
                                            }
                                        } else {
                                            shareData.setFirstTimeLaunch(false);
                                            startActivity(new Intent(SplashScreen.this, TourScreenActivity.class));
                                        }
                                        finish();
                                    }
                                }, 2000);

                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(SplashScreen.this);
                            } catch (Exception e) {
                            }
                        }
                    }) {
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(SplashScreen.this);
        } catch (Exception ex) {
            //todo
            Log.e("MainException", "" + ex.getMessage());
        }
    }

}