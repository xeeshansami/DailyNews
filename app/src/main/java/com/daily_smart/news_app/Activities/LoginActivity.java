package com.daily_smart.news_app.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
import com.daily_smart.news_app.CustomViews.TermsConditionsDialog;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Volley.VolleySingleton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginMobileNumber;
    private TextView btnLogin, txtTermsAndCond;
    private ImageView ivGoogleSignIn;
    private FirebaseAuth firebaseAuth = null;
    private ShareData shareData;
    private CheckBox cbTermsAndConds;
    private String checkedTC = Config.EMPTY_STRING;
    private String userName = Config.EMPTY_STRING;
    private String userEmail = Config.EMPTY_STRING;
    private String verificationId = Config.EMPTY_STRING;
    private String DistrictId = Config.EMPTY_STRING;
    private String pageContent = Config.EMPTY_STRING;
    private ProgressBarDialog progressBarDialog = null;
    private GoogleSignInClient googleSignInClient = null;
    private static final int SIGN_IN = 100;
    private static final String TAG = "GoogleSignIn";
    private TermsConditionsDialog termsConditionsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        shareData = new ShareData(this);
        if (getIntent().hasExtra("DistrictId")) {
            DistrictId = getIntent().getStringExtra("DistrictId");
        }
        etLoginMobileNumber = findViewById(R.id.etLoginMobileNumber);
        ivGoogleSignIn = findViewById(R.id.ivGoogleSignIn);
        cbTermsAndConds = findViewById(R.id.cbTermsAndConds);
        btnLogin = findViewById(R.id.btnLogin);
        txtTermsAndCond = findViewById(R.id.txtTermsAndCond);
        termsConditionsDialog = new TermsConditionsDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBarDialog = new ProgressBarDialog(this);
        getTermsAndPrivacyPage();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateField()) {
                    if (!GeneralFunctions.isNetworkAvailable(LoginActivity.this)) {
                        GeneralFunctions.showToast(getString(R.string.no_internet), LoginActivity.this);

                    } else {
                        progressBarDialog.showProgressBar(LoginActivity.this);
                        startPhoneNumberVerification(etLoginMobileNumber.getText().toString().trim());
                    }
                }
            }
        });
        cbTermsAndConds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    checkedTC = "Checked";
                } else {
                    checkedTC = "";
                }
            }
        });

        SpannableString myString = new SpannableString(getString(R.string.agree_terms_conditions));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                termsConditionsDialog.showTermsCondDialog(pageContent);
            }
        };
        //For Click
        myString.setSpan(clickableSpan, 8, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //For UnderLine
        myString.setSpan(new UnderlineSpan(), 8, 26, 0);
        txtTermsAndCond.setText(myString);
        txtTermsAndCond.setMovementMethod(LinkMovementMethod.getInstance());
        txtTermsAndCond.setHighlightColor(Color.TRANSPARENT);

        ivGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkedTC.isEmpty()) {
                    GeneralFunctions.showToast(getResources().getString(R.string.required_terms_conds), LoginActivity.this);
                } else {
                    Intent intent = googleSignInClient.getSignInIntent();
                    startActivityForResult(intent, SIGN_IN);
                }
            }
        });
    }

    private void getTermsAndPrivacyPage() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.PAGES_ABOUT_US,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBarDialog.dismissProgressBar(LoginActivity.this);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                pageContent = jsonObject.getString("data");

                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressBarDialog.dismissProgressBar(LoginActivity.this);
                            try {

                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("page_type", "terms");
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.getEmail();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthGoogleAccount(account.getIdToken());

            } catch (ApiException e) {

            }
        }
    }

    private void firebaseAuthGoogleAccount(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            userName = user.getDisplayName();
                            userEmail = user.getEmail();
                            GeneralFunctions.showAlert(LoginActivity.this, getString(R.string.enter_phone_number));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    private void startPhoneNumberVerification(String mobileNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber("+91" + mobileNumber)       // Phone number to verify
                        .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            Log.e("CodeLogin", code);
            if (code != null) {
                progressBarDialog.dismissProgressBar(LoginActivity.this);
                signInWithPhoneAuthCredentials(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBarDialog.dismissProgressBar(LoginActivity.this);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            progressBarDialog.dismissProgressBar(LoginActivity.this);
            String mobileNumber = "+91" + etLoginMobileNumber.getText().toString().trim();
            Intent intent = new Intent(LoginActivity.this, VerifyOTPActivity.class);
            intent.putExtra("MobileNumber", mobileNumber);
            intent.putExtra("VerificationId", verificationId);
            startActivity(intent);
        }
    };

    private boolean validateField() {
        boolean flag = true;
        if (etLoginMobileNumber.getText().toString().trim().isEmpty() || etLoginMobileNumber.getText().toString().trim().length() < 10) {
            GeneralFunctions.showToast(getResources().getString(R.string.mobile_validation), LoginActivity.this);
            flag = false;
        } else if (checkedTC.isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.required_terms_conds), LoginActivity.this);
            flag = false;
        }
        return flag;
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (!GeneralFunctions.isNetworkAvailable(LoginActivity.this)) {
                        GeneralFunctions.showToast(getResources().getString(R.string.no_internet), LoginActivity.this);
                    } else {
                        verifyOtp(etLoginMobileNumber.getText().toString().trim());
                    }
                } else {
                    String errorMessage = "Something went wrong";
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Invalid code entered";
                    }
                    GeneralFunctions.showToast(errorMessage, LoginActivity.this);
                }
            }
        });
    }

    private void verifyOtp(final String mobileNumber) {
        try {
            progressBarDialog.showProgressBar(LoginActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.VERIFY_OTP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(LoginActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                String profileStatus = jsonObject.getString("profile_status");
                                shareData.setCustomerId(jsonObject.getString("user_id"));
                                shareData.setProfileStatus(jsonObject.getString("profile_status"));
                                JSONObject profileObjectData = jsonObject.getJSONObject("profile_data");
                                shareData.setPhoneNumber(profileObjectData.getString("phone_number"));
                                if (profileStatus.equalsIgnoreCase("1")) {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                } else {
                                    startActivity(new Intent(LoginActivity.this, EditProfileActivity.class));
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
                                progressBarDialog.dismissProgressBar(LoginActivity.this);

                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("mobileNumber", mobileNumber);
                    params.put("fcm_token", shareData.getFcmToken());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("DistrictId", DistrictId);
        startActivity(intent);
    }

}