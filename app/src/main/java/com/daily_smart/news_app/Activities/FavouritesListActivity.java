package com.daily_smart.news_app.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daily_smart.news_app.Adapters.FavouritesListAdapter;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
import com.daily_smart.news_app.Models.FavoritesModel;
import com.daily_smart.news_app.Models.NewsItemModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavouritesListActivity extends AppCompatActivity {

    private RecyclerView rvFavourites;
    private ArrayList<NewsItemModel> newsItemModelArrayList = new ArrayList<>();
    private TextView txtNoRecentFavourites;
    private FavouritesListAdapter favouritesListAdapter;
    private ShareData shareData;
    private String userId = Config.EMPTY_STRING;
    private ImageView ivBackFavorites;
    private ProgressBarDialog progressBarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_list);
        rvFavourites = findViewById(R.id.rvFavourites);
        txtNoRecentFavourites = findViewById(R.id.txtNoRecentFavourites);
        ivBackFavorites = findViewById(R.id.ivBackFavorites);
        shareData = new ShareData(this);
        userId = shareData.getCustomerId();
        rvFavourites.setHasFixedSize(true);
        progressBarDialog = new ProgressBarDialog(this);
        rvFavourites.setLayoutManager(new LinearLayoutManager(this));
        if (!GeneralFunctions.isNetworkAvailable(this)) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), this);
        } else {
            getFavouritesList(userId);
        }
        ivBackFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getFavouritesList(final String userId) {
        try {
            progressBarDialog.showProgressBar(FavouritesListActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LIST_OF_FAVOURITES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(FavouritesListActivity.this);
                                 JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                                    rvFavourites.setVisibility(View.GONE);
                                    txtNoRecentFavourites.setVisibility(View.VISIBLE);
                                }
                                JSONArray jsonArray = jsonObject.getJSONArray("favorites");
                                newsItemModelArrayList = NewsItemModel.fromJson(jsonArray);
                                if (newsItemModelArrayList.size() > 0) {
                                    rvFavourites.setVisibility(View.VISIBLE);
                                    txtNoRecentFavourites.setVisibility(View.GONE);
                                    favouritesListAdapter = new FavouritesListAdapter(FavouritesListActivity.this, newsItemModelArrayList, new FavouritesListAdapter.DeleteFavouriteInterface() {
                                        @Override
                                        public void deleteFavouriteMethod(final String favouriteId) {
                                            AlertDialog.Builder builder;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                builder = new AlertDialog.Builder(FavouritesListActivity.this, R.style.MyDialogTheme);
                                            } else {
                                                builder = new AlertDialog.Builder(FavouritesListActivity.this);
                                            }
                                            builder.setTitle(getString(R.string.confirmation))
                                                    .setMessage(getString(R.string.delete_favorite))
                                                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (!GeneralFunctions.isNetworkAvailable(FavouritesListActivity.this)) {
                                                                GeneralFunctions.showToast(getString(R.string.no_internet), FavouritesListActivity.this);
                                                            } else {
                                                                dialog.dismiss();
                                                                if (!GeneralFunctions.isNetworkAvailable(FavouritesListActivity.this)) {
                                                                    GeneralFunctions.showToast(getResources().getString(R.string.no_internet), FavouritesListActivity.this);
                                                                } else {
                                                                    deleteFavourite(favouriteId);
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
                                        public void readMoreFavouriteMethod(String newsId) {
                                            Intent intent = new Intent(FavouritesListActivity.this, NewsDetailsActivity.class);
                                            intent.putExtra("NewsId", newsId);
                                            startActivity(intent);
                                        }
                                    });
                                    rvFavourites.setAdapter(favouritesListAdapter);
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
                                progressBarDialog.dismissProgressBar(FavouritesListActivity.this);
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
            VolleySingleton.getInstance(FavouritesListActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void deleteFavourite(final String favouriteId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REMOVE_FAVOURITES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                GeneralFunctions.showToast(jsonObject.getString("message"), FavouritesListActivity.this);
                                favouritesListAdapter.notifyDataSetChanged();
                                getFavouritesList(userId);
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
                    params.put("favorite_id", favouriteId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(FavouritesListActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FavouritesListActivity.this, MainActivity.class);
        intent.putExtra("Followers", "Followers");
        startActivity(intent);
    }

}