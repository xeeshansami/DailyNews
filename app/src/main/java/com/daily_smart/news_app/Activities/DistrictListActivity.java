package com.daily_smart.news_app.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daily_smart.news_app.Adapters.DistrictLstAdapter;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
import com.daily_smart.news_app.Models.DistrictModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DistrictListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDistrictList;
    private DistrictLstAdapter districtLstAdapter;
    private ArrayList<DistrictModel> districtList = new ArrayList<>();
    private String triggeredController = Config.EMPTY_STRING;
    private ShareData shareData;
    private SearchView searchViewCategoriesList;
    private ProgressBarDialog progressBarDialog;
    private boolean districtClicked = true;
    private String StateId = Config.EMPTY_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district_list);
        recyclerViewDistrictList = findViewById(R.id.recyclerViewDistrictList);
        searchViewCategoriesList = findViewById(R.id.searchViewCategoriesList);
        shareData = new ShareData(this);
        progressBarDialog = new ProgressBarDialog(this);
        recyclerViewDistrictList.setHasFixedSize(true);
        recyclerViewDistrictList.setLayoutManager(new LinearLayoutManager(this));
        if (getIntent().hasExtra("StateId")) {
            StateId = getIntent().getStringExtra("StateId");
        }
        if (getIntent().hasExtra("TriggerController")) {
            triggeredController = getIntent().getStringExtra("TriggerController");
        }
        if (!GeneralFunctions.isNetworkAvailable(this)) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), this);
        } else {
            getDistrictList(StateId);
        }
        if (getIntent().hasExtra("EditProfile")) {
            triggeredController = getIntent().getStringExtra("EditProfile");
        }


        searchViewCategoriesList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                districtLstAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void getDistrictList(final String stateId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DISTRICT_LIST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(DistrictListActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("districts");
                                districtList = DistrictModel.fromJson(jsonArray);
                                districtLstAdapter = new DistrictLstAdapter(DistrictListActivity.this, districtList, new DistrictLstAdapter.DistrictClickEventInterface() {
                                    @Override
                                    public void districtClickEventMethod(String districtId, String districtName) {
                                        shareData.setDistrictName(districtName);
                                        shareData.setHomeDistrictId(districtId);
                                        printLength(districtName);
                                        if (triggeredController.equalsIgnoreCase("EditProfileDistrict")) {
                                            Intent intent = new Intent(DistrictListActivity.this, EditProfileActivity.class);
                                            intent.putExtra("DistrictId", districtId);
                                            intent.putExtra("DistrictName", districtName);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(DistrictListActivity.this, MainActivity.class);
                                            intent.putExtra("DistrictId", districtId);
                                            startActivity(intent);
                                        }
                                    }
                                });
                                recyclerViewDistrictList.setAdapter(districtLstAdapter);
                            } catch (Exception ex) {
                                Log.e("Exception", "" + ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(DistrictListActivity.this);
                                Log.e("VolleyError", "" + volleyError.networkResponse);
                                Log.e("VolleyError1", "" + volleyError.getMessage());
                            } catch (Exception e) {
                                Log.e("Exception", "" + e.getMessage());
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("state_id", stateId);
                    return params;
                }

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(this);
        } catch (Exception ex) {
            //todo
            Log.e("MainException", ex.getMessage());
        }
    }

    public static void printLength(String s) {
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(s);
        int count = 0;
        while (it.next() != BreakIterator.DONE) {
            count++;
        }
        Log.e("Grapheme length: ", + count+ " " + s.charAt(0));
    }
}