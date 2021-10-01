package com.daily_smart.news_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daily_smart.news_app.Adapters.ProfileFollowingListAdapter;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
import com.daily_smart.news_app.Models.NewsCommentsModel;
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

public class ProfileFollowingListActivity extends AppCompatActivity {
    private TextView txtProfileFollowingCount, txtNoFollowingList;
    private RecyclerView rvProfileFollowing;
    private ArrayList<NewsCommentsModel> profileFollowingArrayList = new ArrayList<>();
    private ProfileFollowingListAdapter profileFollowingListAdapter;
    private ShareData shareData;
    private ProgressBarDialog progressBarDialog = null;
    private String followerString = Config.EMPTY_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_following_list);
        shareData = new ShareData(this);
        progressBarDialog = new ProgressBarDialog(this);
        txtProfileFollowingCount = findViewById(R.id.txtProfileFollowingCount);
        txtNoFollowingList = findViewById(R.id.txtNoFollowingList);
        rvProfileFollowing = findViewById(R.id.rvProfileFollowing);
        if (getIntent().hasExtra("Follower")) {
            followerString = getIntent().getStringExtra("Follower");
        }
        if (followerString.equalsIgnoreCase("Following")) {
            getFollowingList(shareData.getCustomerId());
            txtNoFollowingList.setText(getString(R.string.no_following));
        } else {
            getFollowersList(shareData.getCustomerId());
            txtNoFollowingList.setText(getString(R.string.no_followers));
        }
    }

    private void getFollowingList(final String userId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_FOLLOWING_LIST,
                    new Response.Listener<String>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(ProfileFollowingListActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                                    txtNoFollowingList.setVisibility(View.VISIBLE);
                                    rvProfileFollowing.setVisibility(View.GONE);
                                    txtProfileFollowingCount.setText(followerString + " ( " + 0 + " )");
                                } else {
                                    txtNoFollowingList.setVisibility(View.GONE);
                                    rvProfileFollowing.setVisibility(View.VISIBLE);
                                }
                                JSONArray jsonArray = jsonObject.getJSONArray("following_list");
                                profileFollowingArrayList = NewsCommentsModel.fromJson(jsonArray);
                                txtProfileFollowingCount.setText(followerString + " ( " + profileFollowingArrayList.size() + " )");
                                profileFollowingListAdapter = new ProfileFollowingListAdapter(ProfileFollowingListActivity.this, followerString, profileFollowingArrayList, new ProfileFollowingListAdapter.ProfileFollowingListUnFollowInterface() {
                                    @Override
                                    public void profileFollowingListUnFollowMethod(String followingId) {
                                        if (!GeneralFunctions.isNetworkAvailable(ProfileFollowingListActivity.this)) {
                                            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), ProfileFollowingListActivity.this);
                                        } else {
                                            followingListUnFollow(followingId);
                                        }
                                    }
                                });
                                rvProfileFollowing.setLayoutManager(new LinearLayoutManager(ProfileFollowingListActivity.this));
                                rvProfileFollowing.setHasFixedSize(true);
                                rvProfileFollowing.setAdapter(profileFollowingListAdapter);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(ProfileFollowingListActivity.this);
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
            VolleySingleton.getInstance(ProfileFollowingListActivity.this).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(ProfileFollowingListActivity.this);
        } catch (Exception ex) {
            //todo
        }
    }

    private void followingListUnFollow(final String followerId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REMOVE_FOLLOWER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBarDialog.dismissProgressBar(ProfileFollowingListActivity.this);
                            try {
                                getFollowingList(shareData.getCustomerId());
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(ProfileFollowingListActivity.this);
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
            VolleySingleton.getInstance(ProfileFollowingListActivity.this).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(ProfileFollowingListActivity.this);
        } catch (Exception ex) {
            //todo
        }
    }

    private void getFollowersList(final String userId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_FOLLOWERS_LIST,
                    new Response.Listener<String>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(ProfileFollowingListActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                                    txtNoFollowingList.setVisibility(View.VISIBLE);
                                    rvProfileFollowing.setVisibility(View.GONE);
                                    txtProfileFollowingCount.setText(followerString + " ( " + 0 + " )");
                                } else {
                                    txtNoFollowingList.setVisibility(View.GONE);
                                    rvProfileFollowing.setVisibility(View.VISIBLE);
                                }
                                JSONArray jsonArray = jsonObject.getJSONArray("followers_list");
                                profileFollowingArrayList = NewsCommentsModel.fromJson(jsonArray);
                                txtProfileFollowingCount.setText(followerString + " ( " + profileFollowingArrayList.size() + " )");
                                profileFollowingListAdapter = new ProfileFollowingListAdapter(ProfileFollowingListActivity.this,followerString, profileFollowingArrayList, new ProfileFollowingListAdapter.ProfileFollowingListUnFollowInterface() {
                                    @Override
                                    public void profileFollowingListUnFollowMethod(String followingId) {
                                        if (!GeneralFunctions.isNetworkAvailable(ProfileFollowingListActivity.this)) {
                                            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), ProfileFollowingListActivity.this);
                                        } else {
                                            followersListUnFollow(followingId);
                                        }
                                    }
                                });
                                rvProfileFollowing.setLayoutManager(new LinearLayoutManager(ProfileFollowingListActivity.this));
                                rvProfileFollowing.setHasFixedSize(true);
                                rvProfileFollowing.setAdapter(profileFollowingListAdapter);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(ProfileFollowingListActivity.this);
                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("follower_id", userId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(ProfileFollowingListActivity.this).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(ProfileFollowingListActivity.this);
        } catch (Exception ex) {
            //todo
        }
    }

    private void followersListUnFollow(final String followerId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REMOVE_FOLLOWER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBarDialog.dismissProgressBar(ProfileFollowingListActivity.this);
                            try {
                                getFollowersList(shareData.getCustomerId());
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(ProfileFollowingListActivity.this);
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
            VolleySingleton.getInstance(ProfileFollowingListActivity.this).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(ProfileFollowingListActivity.this);
        } catch (Exception ex) {
            //todo
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileFollowingListActivity.this,MainActivity.class);
        intent.putExtra("Followers","Followers");
        startActivity(intent);
    }
}