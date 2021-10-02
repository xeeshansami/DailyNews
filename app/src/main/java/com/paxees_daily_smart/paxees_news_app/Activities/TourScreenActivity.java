package com.paxees_daily_smart.paxees_news_app.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.paxees_daily_smart.paxees_news_app.Models.TourScreenModel;
import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.Config;
import com.paxees_daily_smart.paxees_news_app.Utilities.GeneralFunctions;
import com.paxees_daily_smart.paxees_news_app.Utilities.ShareData;
import com.paxees_daily_smart.paxees_news_app.Volley.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TourScreenActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<TourScreenModel> tourScreenModelArrayList = new ArrayList<>();
    private ViewPager viewPager;
    private Button btnNext;
    private LinearLayout layoutDots;
    private CustomPagerAdapter customPagerAdapter;
    private TextView[] dot;
    private ShareData shareData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_screen);
        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        layoutDots = findViewById(R.id.layoutDots);
        shareData = new ShareData(this);
        if (!GeneralFunctions.isNetworkAvailable(this)) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), this);
        } else {
            getTourScreenList();
        }
        viewPager.setPageMargin(20);
        viewPager.addOnPageChangeListener(myOnPageChangeListener);
        btnNext.setOnClickListener(this);
    }

    ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        //declare key
        Boolean first = true;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (first && positionOffset == 0 && positionOffsetPixels == 0) {
                onPageSelected(0);
                addDot(0);
                first = false;
            }
        }

        @Override
        public void onPageSelected(int position) {
            addDot(position);
            //do what need
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void addDot(int position) {
        dot = new TextView[tourScreenModelArrayList.size()];
        layoutDots.removeAllViews();
        for (int i = 0; i < dot.length; i++) {
            dot[i] = new TextView(this);
            dot[i].setText(Html.fromHtml("&#9673;"));
            dot[i].setTextSize(20);
            //set default dot color
            dot[i].setTextColor(getResources().getColor(R.color.dark_grey));
            layoutDots.addView(dot[i]);
        }
        dot[position].setTextColor(getResources().getColor(R.color.red));
    }

    @Override
    public void onClick(View view) {
        if (shareData.getHomeDistrictId().isEmpty()) {
            startActivity(new Intent(TourScreenActivity.this, StatesActivity.class));
        } else {
            startActivity(new Intent(TourScreenActivity.this, MainActivity.class));
        }
        finish();
    }

    private static class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<TourScreenModel> tourScreenModelArrayList;

        public CustomPagerAdapter(Context context, ArrayList<TourScreenModel> tourScreenModelArrayList) {
            this.mContext = context;
            this.tourScreenModelArrayList = tourScreenModelArrayList;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.tour_screen_layout, container, false);
            ImageView ivTourScreen = view.findViewById(R.id.ivTourScreen);
            TextView txtTourScreen = view.findViewById(R.id.txtTourScreen);
            TourScreenModel tourScreenModel = tourScreenModelArrayList.get(position);
            Picasso.get()
                    .load(Config.SPLASH_IMAGE_URL + tourScreenModel.getImage())
                    .placeholder(R.drawable.tour_placeholder)
                    .error(R.drawable.tour_placeholder)
                    .into(ivTourScreen);
            txtTourScreen.setText(tourScreenModel.getDescription());
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return tourScreenModelArrayList.size();
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

    private void getTourScreenList() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.TOUR_SCREEN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("splash_data");
                                tourScreenModelArrayList = TourScreenModel.fromJson(jsonArray);
                                customPagerAdapter = new CustomPagerAdapter(TourScreenActivity.this, tourScreenModelArrayList);
                                viewPager.setAdapter(customPagerAdapter);
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
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
            Log.e("MainException", "" + ex.getMessage());
        }
    }

}