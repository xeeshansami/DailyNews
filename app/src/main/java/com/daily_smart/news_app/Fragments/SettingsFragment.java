package com.daily_smart.news_app.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daily_smart.news_app.Activities.GeneralSettingsActivity;
import com.daily_smart.news_app.Activities.HelpandSupportActivity;
import com.daily_smart.news_app.Activities.StatesActivity;
import com.daily_smart.news_app.CustomViews.LoginDialog;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Volley.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private CardView cardViewChangeDistrict, cardViewRateApp, cardViewAboutUs, cardViewShareApp, cardViewTerms,
            cardViewPrivacy, cardViewHelp, cardViewLogout;
    private TextView txtDistrictName;
    private ShareData shareData;
    private LoginDialog loginDialog;
    private SwitchCompat notificationSwitchButton,themeSwitchButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);
        cardViewChangeDistrict = settingsView.findViewById(R.id.cardViewChangeDistrict);
        txtDistrictName = settingsView.findViewById(R.id.txtDistrictName);
        cardViewLogout = settingsView.findViewById(R.id.cardViewLogout);
        cardViewRateApp = settingsView.findViewById(R.id.cardViewRateApp);
        cardViewAboutUs = settingsView.findViewById(R.id.cardViewAboutUs);
        cardViewTerms = settingsView.findViewById(R.id.cardViewTerms);
        cardViewPrivacy = settingsView.findViewById(R.id.cardViewPrivacy);
        cardViewHelp = settingsView.findViewById(R.id.cardViewHelp);
        cardViewShareApp = settingsView.findViewById(R.id.cardViewShareApp);
        notificationSwitchButton = settingsView.findViewById(R.id.notificationSwitchButton);
        themeSwitchButton = settingsView.findViewById(R.id.themeSwitchButton);
        shareData = new ShareData(getActivity());
        txtDistrictName.setText(shareData.getDistrictName());
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "ramabhadra_regular.ttf");
        txtDistrictName.setTypeface(typeface);

        loginDialog = new LoginDialog(getActivity());
        if (shareData.getCustomerId().isEmpty()) {
            cardViewLogout.setVisibility(View.GONE);
        } else {
            cardViewLogout.setVisibility(View.VISIBLE);
        }

        if (shareData.getNotificationChecked().equalsIgnoreCase("1")) {
            notificationSwitchButton.setChecked(true);
        } else if (shareData.getNotificationChecked().equalsIgnoreCase("0")) {
            notificationSwitchButton.setChecked(false);
        } else {
            notificationSwitchButton.setChecked(true);
            deviceRegistration(shareData.getFcmToken());
        }

        notificationSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    shareData.setNotificationChecked("1");
                    deviceRegistration(shareData.getFcmToken());
                } else {
                    shareData.setNotificationChecked("0");
                    deviceRegistration(Config.EMPTY_STRING);
                }
            }
        });
        themeSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });

        cardViewChangeDistrict.setOnClickListener(this);
        cardViewLogout.setOnClickListener(this);
        cardViewRateApp.setOnClickListener(this);
        cardViewAboutUs.setOnClickListener(this);
        cardViewTerms.setOnClickListener(this);
        cardViewPrivacy.setOnClickListener(this);
        cardViewHelp.setOnClickListener(this);
        cardViewShareApp.setOnClickListener(this);
        return settingsView;
    }

    private void deviceRegistration(final String fcmToken) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DEVICE_REG,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

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
                    params.put("device_id", shareData.getAuthenticationDeviceId());
                    params.put("district_id", shareData.getHomeDistrictId());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardViewChangeDistrict:
                Intent intent = new Intent(getActivity(), StatesActivity.class);
                startActivity(intent);
                break;
            case R.id.cardViewShareApp:
                shareApp();
                break;
            case R.id.cardViewAboutUs:
                intent = new Intent(getActivity(), GeneralSettingsActivity.class);
                intent.putExtra("Settings", getString(R.string.about_us));
                intent.putExtra("PageType", "aboutus");
                startActivity(intent);
                break;
            case R.id.cardViewTerms:
                intent = new Intent(getActivity(), GeneralSettingsActivity.class);
                intent.putExtra("Settings", getString(R.string.terms_conditions));
                intent.putExtra("PageType", "terms");
                startActivity(intent);
                break;
            case R.id.cardViewPrivacy:
                intent = new Intent(getActivity(), GeneralSettingsActivity.class);
                intent.putExtra("Settings", getString(R.string.privacy_policy));
                intent.putExtra("PageType", "policy");
                startActivity(intent);
                break;
            case R.id.cardViewHelp:
                intent = new Intent(getActivity(), HelpandSupportActivity.class);
                startActivity(intent);
                break;
            case R.id.cardViewRateApp:
                launchMarket();
                break;

            case R.id.cardViewLogout:
                loginDialog.showLoginDialog();
                break;
        }
    }

    private void shareApp() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "BharatShorts");
            intent.putExtra(Intent.EXTRA_TEXT, "BharatShorts Local news App" + "\n" + "https://bharatshorts.in/");
            intent.setType("text/plain");
            startActivity(intent);
        } catch (Exception e) {

        }
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            GeneralFunctions.showAlert(getActivity(), getString(R.string.unable_to_find_app));
        }
    }
}