package com.paxees_daily_smart.paxees_news_app.Activities;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.paxees_daily_smart.paxees_news_app.CustomViews.ProgressBarDialog;
import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.Config;
import com.paxees_daily_smart.paxees_news_app.Utilities.ShareData;
import com.paxees_daily_smart.paxees_news_app.Volley.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GeneralSettingsActivity extends AppCompatActivity {

    private TextView txtGeneralSettingsName, txtPageType;
    private ImageView ivBackSettings;
    private String Settings = Config.EMPTY_STRING;
    private String pageType = Config.EMPTY_STRING;
    private ShareData shareData;
    private ProgressBarDialog progressBarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);
        txtGeneralSettingsName = findViewById(R.id.txtGeneralSettingsName);
        txtPageType = findViewById(R.id.txtPageType);
        ivBackSettings = findViewById(R.id.ivBackSettings);
        shareData = new ShareData(this);
        progressBarDialog = new ProgressBarDialog(this);
        if (getIntent().hasExtra("Settings")) {
            Settings = getIntent().getStringExtra("Settings");
        }
        if (getIntent().hasExtra("PageType")) {
            pageType = getIntent().getStringExtra("PageType");
            getTermsAndPrivacyPage(pageType);
        }
        txtGeneralSettingsName.setText(Settings);
        ivBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getTermsAndPrivacyPage(final String pageType) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.PAGES_ABOUT_US,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBarDialog.dismissProgressBar(GeneralSettingsActivity.this);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String pageContent = jsonObject.getString("data");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    txtPageType.setText(Html.fromHtml(pageContent, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    txtPageType.setText(Html.fromHtml(pageContent));
                                }

                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressBarDialog.dismissProgressBar(GeneralSettingsActivity.this);
                            try {

                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("page_type", pageType);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(this);
        } catch (Exception ex) {
            //todo
        }
    }
}