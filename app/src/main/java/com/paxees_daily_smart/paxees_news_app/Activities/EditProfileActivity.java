package com.paxees_daily_smart.paxees_news_app.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.paxees_daily_smart.paxees_news_app.CustomViews.CircleImageView;
import com.paxees_daily_smart.paxees_news_app.CustomViews.DistrictListDialog;
import com.paxees_daily_smart.paxees_news_app.CustomViews.ProgressBarDialog;
import com.paxees_daily_smart.paxees_news_app.Models.DistrictModel;
import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.Config;
import com.paxees_daily_smart.paxees_news_app.Utilities.GeneralFunctions;
import com.paxees_daily_smart.paxees_news_app.Utilities.ShareData;
import com.paxees_daily_smart.paxees_news_app.Volley.VolleyMultipartRequest;
import com.paxees_daily_smart.paxees_news_app.Volley.VolleySingleton;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivCloseEditProfile, ivDoneEditProfile, ivPickImage;
    private EditText etFullName, etMobileNumber, etEmail, etDistrict;
    private Uri mCropImageUri;
    private CircleImageView ivEditProfileImage;
    private byte[] imageBytes;
    private RelativeLayout idImagePicker;
    private CropImageView mCropImageView;
    private String imgBase64 = Config.EMPTY_STRING;
    private LinearLayout layoutParent;
    private ShareData shareData;
    private String userId = Config.EMPTY_STRING;
    private String districtId = Config.EMPTY_STRING;
    private String districtName = Config.EMPTY_STRING;
    private String profilePic = Config.EMPTY_STRING;
    private String district = Config.EMPTY_STRING;
    private ArrayList<DistrictModel> districtList = new ArrayList<>();
    private DistrictListDialog districtListDialog;
    private TextView txtCancelProfile, txtUpdateProfile;
    private ProgressBarDialog progressBarDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        shareData = new ShareData(this);
        ivCloseEditProfile = findViewById(R.id.ivCloseEditProfile);
        ivDoneEditProfile = findViewById(R.id.ivDoneEditProfile);
        ivEditProfileImage = findViewById(R.id.ivEditProfileImage);
        ivPickImage = findViewById(R.id.ivPickImage);
        etFullName = findViewById(R.id.etFullName);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etEmail = findViewById(R.id.etEmail);
        etDistrict = findViewById(R.id.etDistrict);
        idImagePicker = findViewById(R.id.idImagePicker);
        layoutParent = findViewById(R.id.layoutParent);
        txtCancelProfile = findViewById(R.id.txtCancelProfile);
        txtUpdateProfile = findViewById(R.id.txtUpdateProfile);
        progressBarDialog = new ProgressBarDialog(this);
        etMobileNumber.setEnabled(false);
        userId = shareData.getCustomerId();
        districtListDialog = new DistrictListDialog(this, new DistrictListDialog.DistrictListDialogInterface() {
            @Override
            public void districtListDialogMethod(String districtIdString, String districtNameString) {
                districtId = districtIdString;
                districtName = districtNameString;
                etDistrict.setText(districtName);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "ramabhadra_regular.ttf");
                etDistrict.setTypeface(typeface);
            }
        });

        if (getIntent().hasExtra("DistrictId")) {
            districtId = getIntent().getStringExtra("DistrictId");
        }
        if (getIntent().hasExtra("DistrictName")) {
            districtName = getIntent().getStringExtra("DistrictName");
            etDistrict.setText(districtName);
        }

        if (!GeneralFunctions.isNetworkAvailable(this)) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), this);
        } else {
            getProfileDetails(userId);
        }

//        getDistrictList();
        etMobileNumber.setText(shareData.getPhoneNumber());
        ivCloseEditProfile.setOnClickListener(this);
        ivDoneEditProfile.setOnClickListener(this);
        ivPickImage.setOnClickListener(this);
        etDistrict.setOnClickListener(this);
        txtUpdateProfile.setOnClickListener(this);
        txtCancelProfile.setOnClickListener(this);
    }

    private void getProfileDetails(final String userId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_PROFILE_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(EditProfileActivity.this);
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
                                progressBarDialog.dismissProgressBar(EditProfileActivity.this);

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
            VolleySingleton.getInstance(EditProfileActivity.this).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(EditProfileActivity.this);
        } catch (Exception ex) {
            //todo
        }
    }

    private void displayProfileDetails(JSONObject profileDataObject) {
        try {
            etFullName.setText(profileDataObject.getString("name"));
            districtName = profileDataObject.getString("DistrictNameTe");
            if (districtName.isEmpty()) {
                etDistrict.setText(districtName);
            }
            Typeface typeface = Typeface.createFromAsset(getAssets(), "ramabhadra_regular.ttf");
            etDistrict.setTypeface(typeface);
            etFullName.setTypeface(typeface);
            etMobileNumber.setText(profileDataObject.getString("phone_number"));
            etEmail.setText(profileDataObject.getString("AdminEmailId"));
            profilePic = profileDataObject.getString("profile_pic");
            if (districtId.isEmpty()) {
                districtId = profileDataObject.getString("district_id");
            }
            if (!profilePic.isEmpty()) {
                Glide.with(EditProfileActivity.this).load(Config.PROFILE_IMAGE_URL + profileDataObject.getString("profile_pic"))
                        .into(ivEditProfileImage);
                profilePic = profileDataObject.getString("profile_pic");
            } else if (!shareData.getProfileImage().isEmpty()) {
                Glide.with(EditProfileActivity.this).load(Config.PROFILE_IMAGE_URL + shareData.getProfileImage()).into(ivEditProfileImage);
                profilePic = shareData.getProfileImage();
            } else {
                Glide.with(EditProfileActivity.this).load(R.drawable.defult_profile).into(ivEditProfileImage);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtCancelProfile:
                onBackPressed();
                break;
            case R.id.ivPickImage:
//                startActivityForResult(getPickImageChooserIntent(), 200);
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                400);
                    }
                } else {
                    selectImage();
                }
                break;
            case R.id.etDistrict:
//                districtListDialog.showDistrictDialog(districtList);

                Intent intent = new Intent(EditProfileActivity.this, StatesActivity.class);
                intent.putExtra("EditProfileDistrict", "EditProfileDistrict");
                startActivity(intent);

                break;
            case R.id.txtUpdateProfile:
                if (validateInputFields()) {
                    if (!GeneralFunctions.isNetworkAvailable(EditProfileActivity.this)) {
                        GeneralFunctions.showToast(getResources().getString(R.string.no_internet), EditProfileActivity.this);
                    } else {
                        updateProfile(userId);
                    }
                }
                break;

            default:
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void updateProfile(final String userId) {
        try {
            progressBarDialog.showProgressBar(EditProfileActivity.this);
            VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.POST, Config.UPDATE_PROFILE_DETAILS,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                progressBarDialog.dismissProgressBar(EditProfileActivity.this);
                                String result = new String(response.data);
                                JSONObject jsonObject = new JSONObject(result);
                                GeneralFunctions.showToast(jsonObject.getString("message"), EditProfileActivity.this);
                                JSONObject profileObject = jsonObject.getJSONObject("profile_data");
                                shareData.setDistrictId(profileObject.getString("district_id"));
                                shareData.setProfileStatus(profileObject.getString("profile_status"));
                                if (profileObject.getString("profile_pic").isEmpty()) {
                                    shareData.setProfileImage(profilePic);
                                }
//                                shareData.setDistrictName(etDistrict.getText().toString().trim());
                                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                intent.putExtra("DistrictId", profileObject.getString("district_id"));
                                startActivity(intent);
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(EditProfileActivity.this);

                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", etFullName.getText().toString().trim());
                    params.put("mobileNumber", shareData.getPhoneNumber());
                    params.put("email", etEmail.getText().toString().trim());
                    params.put("district_id", districtId);
                    params.put("user_id", userId);
                    if (imgBase64 != null) {
                        params.put("profile_pic", imgBase64);
                    } else {
                        params.put("profile_pic", profilePic);
                    }
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    // Create a chooser intent to select the source to get image from.
    public Intent getPickImageChooserIntent() {
        Intent chooserIntent = null;
        try {
            Uri outputFileUri = getCaptureImageOutputUri();

            List<Intent> allIntents = new ArrayList<>();
            PackageManager packageManager = getPackageManager();

            // collect all camera intents
            Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                if (outputFileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                }
                allIntents.add(intent);
            }

            // collect all gallery intents
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
            for (ResolveInfo res : listGallery) {
                Intent intent = new Intent(galleryIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                allIntents.add(intent);
            }

            // the main intent is the last in the list
            Intent mainIntent = allIntents.get(allIntents.size() - 1);
            for (Intent intent : allIntents) {
                if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                    mainIntent = intent;
                    break;
                }
            }
            allIntents.remove(mainIntent);

            // Create a chooser from the main intent
            chooserIntent = Intent.createChooser(mainIntent, "Select Image");

            // Add all other intents
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return chooserIntent;
    }

    //Get URI to image received from capture by camera.
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        try {
            File getImage = getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return outputFileUri;
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), photo);
                    Log.e("tempUri", "--" + tempUri);
                    mCropImageUri = tempUri;
                    showCroppedImage(tempUri);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        Log.e("Uri", "--" + selectedImage);
                        mCropImageUri = selectedImage;
                        showCroppedImage(selectedImage);
                    }
                }
                break;
        }

        /*if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(EditProfileActivity.this, data);
            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(EditProfileActivity.this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                showCroppedImage(imageUri);
            }
        }*/
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(EditProfileActivity.this.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        }

       /* if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(EditProfileActivity.this);
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCroppedImage(mCropImageUri);
            }
        }*/
    }

    public void showCroppedImage(Uri imageUri) {
        try {
            layoutParent.setVisibility(View.GONE);
            idImagePicker.setVisibility(View.VISIBLE);
            final ImageView imgSelected = findViewById(R.id.imgSelected);
            //Manage image Crop
            mCropImageView = findViewById(R.id.imgCropImage);
            mCropImageView.setImageUriAsync(imageUri);
            ImageView btnCancel = findViewById(R.id.iBtnCancel);
            final ImageView btnSelect = findViewById(R.id.btnSelect);
            final ImageView btnRotate = findViewById(R.id.btnRotate);

            btnSelect.setVisibility(View.VISIBLE);
            imgSelected.setImageBitmap(null);
            imgSelected.setVisibility(View.GONE);
            mCropImageView.setVisibility(View.VISIBLE);
            mCropImageView.setAutoZoomEnabled(false);

            btnSelect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View popupView) {
                    Bitmap cropped = mCropImageView.getCroppedImage(800, 800);
                    if (cropped != null) {
                        imgBase64 = BitMapToString(cropped);
                        imageBytes = BitMapToByteArray(cropped);
                        imgSelected.setImageBitmap(cropped);
                        cropped = Bitmap.createScaledBitmap(cropped, 200, 200, true);
                        ivEditProfileImage.setImageBitmap(cropped);
                        cropped = null;
                        System.gc();
                        btnSelect.setVisibility(View.VISIBLE);
                        imgSelected.setVisibility(View.VISIBLE);
                        mCropImageView.setVisibility(View.GONE);
                    }
                    idImagePicker.setVisibility(View.GONE);
                    layoutParent.setVisibility(View.VISIBLE);
                }

            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View popupView) {
                    imgBase64 = "";
                    imgSelected.setVisibility(View.GONE);
                    idImagePicker.setVisibility(View.GONE);
                    layoutParent.setVisibility(View.VISIBLE);
                }
            });
            btnRotate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View popupView) {
                    mCropImageView.rotateImage(90);
                }
            });
        } catch (Exception ex) {
            Log.e("ExceptionCropped", ex.getMessage());
            //todo
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public byte[] BitMapToByteArray(Bitmap bitmap) {
        byte[] byteArray = null;
        try {
            System.gc();
            ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageBytes);
            byteArray = imageBytes.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    private boolean validateInputFields() {
        boolean flag = true;
        if (etDistrict.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.field_empty), EditProfileActivity.this);
            flag = false;
        } else if (etFullName.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.field_empty), EditProfileActivity.this);
            etFullName.requestFocus();
            flag = false;
        } else if (etEmail.getText().toString().trim().isEmpty()) {
            GeneralFunctions.showToast(getResources().getString(R.string.field_empty), EditProfileActivity.this);
            etEmail.requestFocus();
            flag = false;
        }
        return flag;
    }

    private void getDistrictList() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DISTRICT_LIST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("districts");
                                districtList = DistrictModel.fromJson(jsonArray);
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
                    params.put("state_id", shareData.getStateId());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
            Log.e("MainException", ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
        finish();
    }
}