package com.daily_smart.news_app.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Volley.VolleySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private ImageView closeVerifyOTPDialog, doneVerifyOTPDialog;
    private EditText etOTP1, etOTP2, etOTP3, etOTP4, etOTP5, etOTP6;
    private TextView txtOTPMobileNumber, txtResendOTP, txtSecondsRemaining, txtCancel, txtVerify;
    private String mobileNumber;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth firebaseAuth;
    private String verificationId;
    private String modifiedMobileNumber = "";
    private ShareData shareData;
    private CountDownTimer countDownTimer = null;
    private ProgressBarDialog progressBarDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);
        firebaseAuth = FirebaseAuth.getInstance();
        shareData = new ShareData(this);
        closeVerifyOTPDialog = findViewById(R.id.closeVerifyOTPDialog);
        doneVerifyOTPDialog = findViewById(R.id.doneVerifyOTPDialog);
        etOTP1 = findViewById(R.id.etOTP1);
        etOTP2 = findViewById(R.id.etOTP2);
        etOTP3 = findViewById(R.id.etOTP3);
        etOTP4 = findViewById(R.id.etOTP4);
        etOTP5 = findViewById(R.id.etOTP5);
        etOTP6 = findViewById(R.id.etOTP6);
        txtOTPMobileNumber = findViewById(R.id.txtOTPMobileNumber);
        txtResendOTP = findViewById(R.id.txtResendOTP);
        txtSecondsRemaining = findViewById(R.id.txtSecondsRemaining);
        txtCancel = findViewById(R.id.txtCancel);
        txtVerify = findViewById(R.id.txtVerify);
        mobileNumber = getIntent().getStringExtra("MobileNumber");
        if (getIntent().hasExtra("VerificationId")) {
            verificationId = getIntent().getStringExtra("VerificationId");
        }
        txtOTPMobileNumber.setText(mobileNumber);
        progressBarDialog = new ProgressBarDialog(this);

        etOTP1.addTextChangedListener(this);
        etOTP2.addTextChangedListener(this);
        etOTP3.addTextChangedListener(this);
        etOTP4.addTextChangedListener(this);
        etOTP5.addTextChangedListener(this);
        etOTP6.addTextChangedListener(this);

        txtCancel.setOnClickListener(this);
        txtVerify.setOnClickListener(this);
        txtResendOTP.setOnClickListener(this);
        countDownTimer = new CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                txtSecondsRemaining.setText(getResources().getString(R.string.seconds_remaining) + " " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                txtResendOTP.setVisibility(View.VISIBLE);
                txtSecondsRemaining.setVisibility(View.GONE);
            }
        }.start();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                Log.e("message", "--" + message);
                //Do whatever you want with the code here
            }
        }
    };

    @Override
    public void onResume() {
//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVerify:
                if (validateInputFields()) {
                    String OTP = etOTP1.getText().toString().trim() + etOTP2.getText().toString().trim() + etOTP3.getText().toString().trim() +
                            etOTP4.getText().toString().trim() + etOTP5.getText().toString().trim() + etOTP6.getText().toString().trim();
                    Log.e("OTP", OTP);
                    progressBarDialog.showProgressBar(VerifyOTPActivity.this);
                    verifyVerificationCode(verificationId, OTP);
                }
                break;
            case R.id.txtCancel:
                onBackPressed();
                break;
            case R.id.txtResendOTP:
                if (!GeneralFunctions.isNetworkAvailable(VerifyOTPActivity.this)) {
                    GeneralFunctions.showToast(getString(R.string.no_internet), VerifyOTPActivity.this);
                } else {
                    countDownTimer.start();
                    txtResendOTP.setVisibility(View.GONE);
                    txtSecondsRemaining.setVisibility(View.VISIBLE);
                    GeneralFunctions.showToast(getResources().getString(R.string.resend_otp_clicked), VerifyOTPActivity.this);
                    resendVerificationCode(mobileNumber, mResendToken);
                }
                break;
            default:
                break;
        }
    }

    private void resendVerificationCode(String mobileNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(mobileNumber)       // Phone number to verify
                        .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            Log.e("code", code);
            if (code != null) {
                signInWithPhoneAuthCredentials(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            mResendToken = forceResendingToken;

        }
    };

    private void verifyVerificationCode(String verificationId, String verificationCode) {
        try {
            Log.e("verificationCode", verificationCode);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
            signInWithPhoneAuthCredentials(credential);
        } catch (Exception e) {
            GeneralFunctions.showToast(getResources().getString(R.string.wrong_otp), VerifyOTPActivity.this);
        }
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (!GeneralFunctions.isNetworkAvailable(VerifyOTPActivity.this)) {
                        GeneralFunctions.showToast(getResources().getString(R.string.no_internet), VerifyOTPActivity.this);
                    } else {
                        verifyOtp(mobileNumber);
                    }
                } else {
                    String errorMessage = "Something went wrong";
                    Log.e("error", "error");
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Invalid code entered";
                    }
                    GeneralFunctions.showToast(errorMessage, VerifyOTPActivity.this);
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (etOTP1.getText().hashCode() == editable.hashCode()) {
            if (editable.length() == 1) {
                etOTP2.requestFocus();
            }
        } else if (etOTP2.getText().hashCode() == editable.hashCode()) {
            if (editable.length() == 1) {
                etOTP3.requestFocus();
            } else {
                etOTP1.requestFocus();
            }
        } else if (etOTP3.getText().hashCode() == editable.hashCode()) {
            if (editable.length() == 1) {
                etOTP4.requestFocus();
            } else {
                etOTP2.requestFocus();
            }
        } else if (etOTP4.getText().hashCode() == editable.hashCode()) {
            if (editable.length() == 1) {
                etOTP5.requestFocus();
            } else {
                etOTP3.requestFocus();
            }
        } else if (etOTP5.getText().hashCode() == editable.hashCode()) {
            if (editable.length() == 1) {
                etOTP6.requestFocus();
            } else {
                etOTP4.requestFocus();
            }
        } else if (etOTP6.getText().hashCode() == editable.hashCode()) {
            if (editable.length() == 1) {
                etOTP6.requestFocus();
            } else {
                etOTP5.requestFocus();
            }
        }
    }

    private boolean validateInputFields() {
        boolean flag = true;
        if (etOTP1.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.field_empty), VerifyOTPActivity.this);
            flag = false;
        } else if (etOTP2.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.field_empty), VerifyOTPActivity.this);
            flag = false;
        } else if (etOTP3.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.field_empty), VerifyOTPActivity.this);
            flag = false;
        } else if (etOTP4.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.field_empty), VerifyOTPActivity.this);
            flag = false;
        } else if (etOTP5.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.field_empty), VerifyOTPActivity.this);
            flag = false;
        } else if (etOTP6.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.field_empty), VerifyOTPActivity.this);
            flag = false;
        }
        return flag;
    }

    private void verifyOtp(String mobileNumber) {
        if (mobileNumber.length() > 10) {
            modifiedMobileNumber = mobileNumber.substring(mobileNumber.length() - 10);
        }
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.VERIFY_OTP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(VerifyOTPActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                String responseMessage = jsonObject.getString("message");
                                String profileStatus = jsonObject.getString("profile_status");
                                shareData.setProfileStatus(profileStatus);
                                shareData.setCustomerId(jsonObject.getString("user_id"));
                                JSONObject profileObjectData = jsonObject.getJSONObject("profile_data");
                                shareData.setPhoneNumber(profileObjectData.getString("phone_number"));
                                if (profileStatus.equalsIgnoreCase("1")) {
                                    startActivity(new Intent(VerifyOTPActivity.this, MainActivity.class));
                                } else {
                                    Intent intent = new Intent(VerifyOTPActivity.this, EditProfileActivity.class);
                                    startActivity(intent);
                                }
                                finish();

                            } catch (Exception ex) {
                                Log.e("Exception", "--" + ex.getMessage());
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressBarDialog.dismissProgressBar(VerifyOTPActivity.this);
                            try {

                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("mobileNumber", modifiedMobileNumber);
                    params.put("fcm_token", shareData.getFcmToken());
                    params.put("device_id", shareData.getAuthenticationDeviceId());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }
}