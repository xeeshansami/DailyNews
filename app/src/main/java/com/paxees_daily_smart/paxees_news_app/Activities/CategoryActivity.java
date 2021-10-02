package com.paxees_daily_smart.paxees_news_app.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.paxees_daily_smart.paxees_news_app.Adapters.CategoryListAdapter;
import com.paxees_daily_smart.paxees_news_app.CustomViews.ProgressBarDialog;
import com.paxees_daily_smart.paxees_news_app.Models.NewsItemModel;
import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.Config;
import com.paxees_daily_smart.paxees_news_app.Utilities.GeneralFunctions;
import com.paxees_daily_smart.paxees_news_app.Utilities.ShareData;
import com.paxees_daily_smart.paxees_news_app.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerviewCategories;
    private CategoryListAdapter categoryListAdapter;
    private ArrayList<NewsItemModel> recentNewsList = new ArrayList<>();
    private ShareData shareData;
    private ImageView ivBackPosts;
    private String userId = Config.EMPTY_STRING;
    private ProgressBarDialog progressBarDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        recyclerviewCategories = findViewById(R.id.recyclerviewCategories);
        ivBackPosts = findViewById(R.id.ivBackPosts);
        progressBarDialog = new ProgressBarDialog(this);
        recyclerviewCategories.setHasFixedSize(true);
        recyclerviewCategories.setLayoutManager(new LinearLayoutManager(this));
        shareData = new ShareData(this);
        userId = shareData.getCustomerId();

        if (!GeneralFunctions.isNetworkAvailable(CategoryActivity.this)) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), CategoryActivity.this);
        } else {
            getRecentPosts(userId);
        }
        ivBackPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void deleteRecentNewsPost(final String newsId) {
        try {
            progressBarDialog.showProgressBar(CategoryActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DELETE_REPORTER_POSTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(CategoryActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                GeneralFunctions.showToast(jsonObject.getString("message"), CategoryActivity.this);
                                categoryListAdapter.notifyDataSetChanged();
                                getRecentPosts(userId);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(CategoryActivity.this);
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
            VolleySingleton.getInstance(CategoryActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void getRecentPosts(final String userId) {
        try {
            progressBarDialog.showProgressBar(CategoryActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_REPORTER_POSTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(CategoryActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("news_data");
                                recentNewsList = NewsItemModel.fromJson(jsonArray);
                                categoryListAdapter = new CategoryListAdapter(CategoryActivity.this, recentNewsList, new CategoryListAdapter.DeleteRecentPostInterface() {
                                    @Override
                                    public void deleteRecentPostMethod(final String newsId) {
                                        AlertDialog.Builder builder;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            builder = new AlertDialog.Builder(CategoryActivity.this, R.style.MyDialogTheme);
                                        } else {
                                            builder = new AlertDialog.Builder(CategoryActivity.this);
                                        }
                                        builder.setTitle(getString(R.string.confirmation))
                                                .setMessage(getString(R.string.delete_post))
                                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (!GeneralFunctions.isNetworkAvailable(CategoryActivity.this)) {
                                                            GeneralFunctions.showToast(getString(R.string.no_internet), CategoryActivity.this);
                                                        } else {
                                                            dialog.dismiss();
                                                            if (!GeneralFunctions.isNetworkAvailable(CategoryActivity.this)) {
                                                                GeneralFunctions.showToast(getResources().getString(R.string.no_internet), CategoryActivity.this);
                                                            } else {
                                                                deleteRecentNewsPost(newsId);
                                                            }
                                                        }
                                                    }
                                                }).setNegativeButton(getString(R.string.no), null);
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                                    }

                                    @Override
                                    public void readMoreRecentPostMethod(String newsId, String status) {
                                        Intent intent = new Intent(CategoryActivity.this, NewsDetailsActivity.class);
                                        intent.putExtra("NewsId", newsId);
                                        intent.putExtra("Status", status);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void editRecentPostMethod(String newsId) {
                                        Intent intent = new Intent(CategoryActivity.this, PostNewsActivity.class);
                                        intent.putExtra("NewsId", newsId);
                                        intent.putExtra("EditNewsProfile", "RecentPostList");
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                recyclerviewCategories.setAdapter(categoryListAdapter);

                            } catch (Exception ex) {
                                Log.e("exception", ex.getMessage());
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(CategoryActivity.this);
                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", userId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(CategoryActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

}