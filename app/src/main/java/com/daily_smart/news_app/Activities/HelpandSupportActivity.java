package com.daily_smart.news_app.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Volley.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HelpandSupportActivity extends AppCompatActivity {

    private EditText etMail, etSubject, etDescription;
    private TextView txtSubmitQuery;
    private ProgressBarDialog progressBarDialog;
    private ImageView ivBackSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpand_support);
        progressBarDialog = new ProgressBarDialog(this);
        ivBackSettings = findViewById(R.id.ivBackSettings);
        etMail = findViewById(R.id.etMail);
        etSubject = findViewById(R.id.etSubject);
        etDescription = findViewById(R.id.etDescription);
        txtSubmitQuery = findViewById(R.id.txtSubmitQuery);
        ivBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtSubmitQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputFields()) {
                    if (!GeneralFunctions.isNetworkAvailable(HelpandSupportActivity.this)) {
                        GeneralFunctions.showToast(getResources().getString(R.string.no_internet), HelpandSupportActivity.this);
                    } else {
                        submitQuery();
                    }
                }
            }
        });
    }

    private void submitQuery() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CONTACT_US,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBarDialog.dismissProgressBar(HelpandSupportActivity.this);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getString("status").equalsIgnoreCase("200")) {
                                    GeneralFunctions.showToast(jsonObject.getString("message"),HelpandSupportActivity.this);
                                    onBackPressed();
                                }else {
                                    GeneralFunctions.showToast(jsonObject.getString("message"),HelpandSupportActivity.this);
                                }

                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressBarDialog.dismissProgressBar(HelpandSupportActivity.this);
                            try {

                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", etMail.getText().toString().trim());
                    params.put("subject", etSubject.getText().toString().trim());
                    params.put("description", etDescription.getText().toString().trim());
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

    private boolean validateInputFields() {
        boolean flag = true;
        if (etMail.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.mail_required), HelpandSupportActivity.this);
            flag = false;
        } else if (etSubject.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.subject_required), HelpandSupportActivity.this);
            flag = false;
        } else if (etDescription.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.desc_required), HelpandSupportActivity.this);
            flag = false;
        }
        return flag;
    }
}