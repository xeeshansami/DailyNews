package com.paxees_daily_smart.paxees_news_app.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.paxees_daily_smart.paxees_news_app.CustomViews.ProgressBarDialog;
import com.paxees_daily_smart.paxees_news_app.Models.DistrictModel;
import com.paxees_daily_smart.paxees_news_app.Models.DropdownList;
import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.Config;
import com.paxees_daily_smart.paxees_news_app.Utilities.GeneralFunctions;
import com.paxees_daily_smart.paxees_news_app.Utilities.ShareData;
import com.paxees_daily_smart.paxees_news_app.Volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatesActivity extends AppCompatActivity {
    private Spinner spinnerState;
    private TextView txtNextStates;
    private ShareData shareData;
    private ProgressBarDialog progressBarDialog;
    private ArrayList<DistrictModel> statesList = new ArrayList<>();
    private String stateId = Config.EMPTY_STRING;
    private String triggeredController = Config.EMPTY_STRING;
    private List<DropdownList> dropdownItems = new ArrayList<DropdownList>();
    private DropdownList dropdownItem = new DropdownList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_states);
        spinnerState = findViewById(R.id.spinnerState);
        txtNextStates = findViewById(R.id.txtNextStates);
        shareData = new ShareData(this);
        progressBarDialog = new ProgressBarDialog(this);

        if (!GeneralFunctions.isNetworkAvailable(this)) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), this);
        } else {
            getStatesList();
        }
        if (getIntent().hasExtra("EditProfile")) {
            triggeredController = getIntent().getStringExtra("EditProfile");
        }
        if (getIntent().hasExtra("EditProfileDistrict")) {
            triggeredController = getIntent().getStringExtra("EditProfileDistrict");
        }

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                stateId = statesList.get(position).getState_id();
                shareData.setStateId(stateId);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "ramabhadra_regular.ttf");
                ((TextView) view).setTypeface(typeface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtNextStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatesActivity.this, DistrictListActivity.class);
                intent.putExtra("StateId", stateId);
                intent.putExtra("TriggerController", triggeredController);
                startActivity(intent);
            }
        });
    }

    private void getStatesList() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.STATES_LIST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(StatesActivity.this);
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    DistrictModel districtModel = new DistrictModel();
                                    districtModel.setState_id(jsonObject.getString("state_id"));
                                    districtModel.setState_name_te(jsonObject.getString("state_name_te"));
                                    statesList.add(districtModel);
                                }
                                if (statesList.size() > 0) {
                                    populateStates(statesList);
                                }

                            } catch (Exception ex) {
                                Log.e("Exception", "" + ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(StatesActivity.this);
                            } catch (Exception e) {
                            }
                        }
                    }) {

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(this);
        } catch (Exception ex) {
            //todo
            Log.e("MainException", ex.getMessage());
        }
    }

    private void populateStates(ArrayList<DistrictModel> statesList) {
        dropdownItems = new ArrayList<DropdownList>();
        Integer position = 0;
        for (int i = 0; i < statesList.size(); i++) {
            position++;
            DistrictModel districtModel = statesList.get(i);
            String stateId = districtModel.getState_id();
            String name = districtModel.getState_name_te();
            dropdownItem = new DropdownList(position, name, stateId);
            dropdownItems.add(dropdownItem);
        }
        ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dropdownItems) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setText(dropdownItems.get(position).name);
                text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_20));
                text.setTextColor(Color.BLACK);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "ramabhadra_regular.ttf");
                text.setTypeface(typeface);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setText(dropdownItems.get(position).name);
                text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_20));
                text.setTextColor(Color.BLACK);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "ramabhadra_regular.ttf");
                text.setTypeface(typeface);
                return view;
            }

            @Override
            public int getCount() {
                return super.getCount();
            }

        };
        spinnerState.setAdapter(dataAdapter);
    }
}