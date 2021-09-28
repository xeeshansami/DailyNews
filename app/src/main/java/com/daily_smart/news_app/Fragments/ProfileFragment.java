package com.daily_smart.news_app.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.daily_smart.news_app.Activities.CategoryActivity;
import com.daily_smart.news_app.Activities.EditProfileActivity;
import com.daily_smart.news_app.Activities.FavouritesListActivity;
import com.daily_smart.news_app.Activities.NewsDetailsActivity;
import com.daily_smart.news_app.Activities.PostNewsActivity;
import com.daily_smart.news_app.Activities.ProfileFollowingListActivity;
import com.daily_smart.news_app.Adapters.RecentPostsAdapter;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
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


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView ivEditProfile, ivProfileImage;
    private ShareData shareData;
    private TextView txtProfileName, txtProfileDistrict, txtProfileMobileNumber, txtProfileEmail, txtNoRecentPosts, txtViewAll, txtFavCount, txtFollowersCount, txtFollowingCount;
    private String userId = Config.EMPTY_STRING;
    private String profilePic = Config.EMPTY_STRING;
    private RecyclerView rvRecentPosts;
    private ArrayList<NewsItemModel> newsItemModelArrayList = new ArrayList<>();
    private LinearLayout layoutProfileFavourites, layoutProfileFollowers, layoutProfileFollowing;
    private RecentPostsAdapter recentPostsAdapter;
    private String profileStatus = Config.EMPTY_STRING;
    private ProgressBarDialog progressBarDialog = null;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        ivEditProfile = profileView.findViewById(R.id.ivEditProfile);
        ivProfileImage = profileView.findViewById(R.id.ivProfileImage);
        txtProfileName = profileView.findViewById(R.id.txtProfileName);
        txtProfileMobileNumber = profileView.findViewById(R.id.txtProfileMobileNumber);
        txtProfileDistrict = profileView.findViewById(R.id.txtProfileDistrict);
        txtProfileEmail = profileView.findViewById(R.id.txtProfileEmail);
        rvRecentPosts = profileView.findViewById(R.id.rvRecentPosts);
        txtNoRecentPosts = profileView.findViewById(R.id.txtNoRecentPosts);
        txtViewAll = profileView.findViewById(R.id.txtViewAll);
        txtFavCount = profileView.findViewById(R.id.txtFavCount);
        txtFollowersCount = profileView.findViewById(R.id.txtFollowersCount);
        txtFollowingCount = profileView.findViewById(R.id.txtFollowingCount);
        layoutProfileFavourites = profileView.findViewById(R.id.layoutProfileFavourites);
        layoutProfileFollowers = profileView.findViewById(R.id.layoutProfileFollowers);
        layoutProfileFollowing = profileView.findViewById(R.id.layoutProfileFollowing);
        shareData = new ShareData(getActivity());
        userId = shareData.getCustomerId();
        progressBarDialog = new ProgressBarDialog(getActivity());
        rvRecentPosts.setHasFixedSize(true);
        rvRecentPosts.setLayoutManager(new LinearLayoutManager(getActivity()));

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "ramabhadra_regular.ttf");
        txtProfileDistrict.setTypeface(typeface);
        txtProfileName.setTypeface(typeface);

        ivEditProfile.setOnClickListener(this);
        txtViewAll.setOnClickListener(this);
        layoutProfileFavourites.setOnClickListener(this);
        layoutProfileFollowing.setOnClickListener(this);
        layoutProfileFollowers.setOnClickListener(this);
        if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
        } else {
            getProfileDetails(userId);
            getRecentPosts(userId);
        }
        return profileView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivEditProfile:
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("Name", txtProfileName.getText().toString().trim());
                intent.putExtra("EMail", txtProfileEmail.getText().toString().trim());
                intent.putExtra("MobileNumber", txtProfileMobileNumber.getText().toString().trim());
                intent.putExtra("DistrictName", txtProfileDistrict.getText().toString().trim());
                intent.putExtra("DistrictId", shareData.getDistrictId());
                intent.putExtra("ProfilePic", profilePic);
                startActivity(intent);
                break;
            case R.id.txtViewAll:
                Intent intentViewAll = new Intent(getActivity(), CategoryActivity.class);
                startActivity(intentViewAll);
                break;
            case R.id.layoutProfileFavourites:
                Intent intentFavourites = new Intent(getActivity(), FavouritesListActivity.class);
                startActivity(intentFavourites);
                break;
            case R.id.layoutProfileFollowing:
                Intent followingIntent = new Intent(getActivity(), ProfileFollowingListActivity.class);
                followingIntent.putExtra("Follower", "Following");
                startActivity(followingIntent);
                break;
            case R.id.layoutProfileFollowers:
                Intent followerIntent = new Intent(getActivity(), ProfileFollowingListActivity.class);
                followerIntent.putExtra("Follower", "Followers");
                startActivity(followerIntent);
                break;
            default:
                break;
        }
    }

    private void getProfileDetails(final String userId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_PROFILE_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(getActivity());
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject profileDataObject = jsonObject.getJSONObject("profile_data");
                                displayProfileDetails(profileDataObject);

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

    private void displayProfileDetails(JSONObject profileDataObject) {
        try {
            txtProfileName.setText(profileDataObject.getString("name"));
            txtProfileDistrict.setText(profileDataObject.getString("DistrictNameTe"));
            txtProfileMobileNumber.setText(profileDataObject.getString("phone_number"));
            txtProfileEmail.setText(profileDataObject.getString("AdminEmailId"));
            txtFavCount.setText(profileDataObject.getString("fv_count"));
            txtFollowersCount.setText(profileDataObject.getString("foll_count"));
            txtFollowingCount.setText(profileDataObject.getString("my_foll_count"));
            profileStatus = profileDataObject.getString("profile_status");
            shareData.setProfileStatus(profileStatus);
            profilePic = profileDataObject.getString("profile_pic");
            shareData.setDistrictId(profileDataObject.getString("district_id"));
            if (!profilePic.isEmpty()) {
                Glide.with(getActivity()).load(Config.PROFILE_IMAGE_URL + profileDataObject.getString("profile_pic"))
                        .into(ivProfileImage);
                profilePic = profileDataObject.getString("profile_pic");
            } else if (!shareData.getProfileImage().isEmpty()) {
                Glide.with(getActivity()).load(Config.PROFILE_IMAGE_URL + shareData.getProfileImage()).into(ivProfileImage);
                profilePic = shareData.getProfileImage();
            } else {
                Glide.with(getActivity()).load(R.drawable.defult_profile).into(ivProfileImage);
            }
        } catch (Exception e) {

        }
    }

    private void getRecentPosts(final String userId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_REPORTER_POSTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("news_data");
                                newsItemModelArrayList = NewsItemModel.fromJson(jsonArray);
                                if (newsItemModelArrayList.size() > 0) {
                                    rvRecentPosts.setVisibility(View.VISIBLE);
                                    txtViewAll.setVisibility(View.VISIBLE);
                                    txtNoRecentPosts.setVisibility(View.GONE);
                                } else {
                                    rvRecentPosts.setVisibility(View.GONE);
                                    txtViewAll.setVisibility(View.GONE);
                                    txtNoRecentPosts.setVisibility(View.VISIBLE);
                                }
                                recentPostsAdapter = new RecentPostsAdapter(getActivity(), newsItemModelArrayList, new RecentPostsAdapter.DeleteRecentPostInterface() {
                                    @Override
                                    public void deleteRecentPostMethod(final String newsId) {
                                        AlertDialog.Builder builder;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                                        } else {
                                            builder = new AlertDialog.Builder(getActivity());
                                        }
                                        builder.setTitle(getString(R.string.confirmation))
                                                .setMessage(getString(R.string.delete_post))
                                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                                            GeneralFunctions.showToast(getString(R.string.no_internet), getActivity());
                                                        } else {
                                                            dialog.dismiss();
                                                            if (!GeneralFunctions.isNetworkAvailable(getActivity())) {
                                                                GeneralFunctions.showToast(getResources().getString(R.string.no_internet), getActivity());
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
                                        Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
                                        intent.putExtra("NewsId", newsId);
                                        intent.putExtra("Status", status);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void editRecentPostMethod(String newsId) {
                                        Intent intent = new Intent(getActivity(), PostNewsActivity.class);
                                        intent.putExtra("NewsId", newsId);
                                        intent.putExtra("EditNewsProfile", "EditNewsProfile");
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                                rvRecentPosts.setAdapter(recentPostsAdapter);
                                recentPostsAdapter.notifyDataSetChanged();
                            } catch (Exception ex) {
                                // Catch exception here
                                Log.e("Exception", "" + ex.getMessage());
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

    private void deleteRecentNewsPost(final String newsId) {
        try {
            progressBarDialog.showProgressBar(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DELETE_REPORTER_POSTS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(getActivity());
                                JSONObject jsonObject = new JSONObject(response);
                                GeneralFunctions.showToast(jsonObject.getString("message"), getActivity());
                                recentPostsAdapter.notifyDataSetChanged();
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
                                progressBarDialog.dismissProgressBar(getActivity());
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
}