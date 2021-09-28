package com.daily_smart.news_app.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ShareData {

    private static final String USER_PREFS = "USER_PREFS";
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;
    private String userToken = "userToken";
    private String authenticationDeviceId = "authenticationDeviceId";
    private String authenticationId = "authenticationId";
    private String customerId = "customerId";
    private String profileImage = "profileImage";
    private String townshipId = "townshipId";
    private String fcmToken = "fcmToken";
    private String districtId = "districtId";
    private String stateId = "stateId";
    private String homeDistrictId = "homeDistrictId";
    private String districtName = "districtName";
    private String phoneNumber = "phoneNumber";
    private String profileStatus = "profileStatus";
    private String notificationChecked = "notificationChecked";
    private String scrolledPosition = "scrolledPosition";
    private String backScrolledPosition = "backScrolledPosition";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public ShareData(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public String getUserToken() {
        return appSharedPrefs.getString(userToken, Config.EMPTY_STRING);
    }

    public void setUserToken(String UserToken) {
        prefsEditor.putString(userToken, UserToken).commit();
    }

    public String getAuthenticationDeviceId() {
        return appSharedPrefs.getString(authenticationDeviceId, Config.EMPTY_STRING);
    }

    public void setAuthenticationDeviceId(String AuthenticationDeviceId) {
        prefsEditor.putString(authenticationDeviceId, AuthenticationDeviceId).commit();
    }

    public String getAuthenticationId() {
        return appSharedPrefs.getString(authenticationId, Config.EMPTY_STRING);
    }

    public void setAuthenticationId(String AuthenticationId) {
        prefsEditor.putString(authenticationId, AuthenticationId).commit();
    }

    public String getCustomerId() {
        return appSharedPrefs.getString(customerId, Config.EMPTY_STRING);
    }

    public void setCustomerId(String CustomerId) {
        prefsEditor.putString(customerId, CustomerId).commit();
    }

    public String getProfileImage() {
        return appSharedPrefs.getString(profileImage, Config.EMPTY_STRING);
    }

    public void setProfileImage(String p_profileImage) {
        prefsEditor.putString(profileImage, p_profileImage).commit();
    }

    public String getTownshipId() {
        return appSharedPrefs.getString(townshipId, Config.EMPTY_STRING);
    }

    public void setTownshipId(String TownshipId) {
        prefsEditor.putString(townshipId, TownshipId).commit();
    }

    public void setFcmToken(String fcm_token) {
        prefsEditor.putString(fcmToken, fcm_token).commit();
    }

    public String getFcmToken() {
        return appSharedPrefs.getString(fcmToken, Config.EMPTY_STRING);
    }

    public void setDistrictId(String districtId1) {
        prefsEditor.putString(districtId, districtId1).commit();
    }

    public String getDistrictId() {
        return appSharedPrefs.getString(districtId, Config.EMPTY_STRING);
    }

    public void setStateId(String state_id) {
        prefsEditor.putString(stateId, state_id).commit();
    }

    public String getStateId() {
        return appSharedPrefs.getString(stateId, Config.EMPTY_STRING);
    }

    public void setHomeDistrictId(String districtId1) {
        prefsEditor.putString(homeDistrictId, districtId1).commit();
    }

    public String getHomeDistrictId() {
        return appSharedPrefs.getString(homeDistrictId, Config.EMPTY_STRING);
    }

    public void setPhoneNumber(String phoneNumber1) {
        prefsEditor.putString(phoneNumber, phoneNumber1).commit();
    }

    public String getPhoneNumber() {
        return appSharedPrefs.getString(phoneNumber, Config.EMPTY_STRING);
    }

    public void setDistrictName(String districtName1) {
        prefsEditor.putString(districtName, districtName1).commit();
    }

    public String getDistrictName() {
        return appSharedPrefs.getString(districtName, Config.EMPTY_STRING);
    }

    public void setProfileStatus(String profile_Status) {
        prefsEditor.putString(profileStatus, profile_Status).commit();
    }

    public String getProfileStatus() {
        return appSharedPrefs.getString(profileStatus, Config.EMPTY_STRING);
    }

    public void setNotificationChecked(String notification_checked) {
        prefsEditor.putString(notificationChecked, notification_checked).commit();
    }

    public String getNotificationChecked() {
        return appSharedPrefs.getString(notificationChecked, Config.EMPTY_STRING);
    }
    public void setScrolledPosition(String scrolled_position) {
        prefsEditor.putString(scrolledPosition, scrolled_position).commit();
    }

    public String getScrolledPosition() {
        return appSharedPrefs.getString(scrolledPosition, Config.EMPTY_STRING);
    }
    public void setBackScrolledPosition(String back_scrolled_position) {
        prefsEditor.putString(backScrolledPosition, back_scrolled_position).commit();
    }

    public String getBackScrolledPosition() {
        return appSharedPrefs.getString(backScrolledPosition, Config.EMPTY_STRING);
    }

    public void setShareDataEmpty() {
        prefsEditor.putString(customerId, Config.EMPTY_STRING).commit();
        prefsEditor.putString(userToken, Config.EMPTY_STRING).commit();
//        prefsEditor.putString(districtId, Config.EMPTY_STRING).commit();
        prefsEditor.putString(phoneNumber, Config.EMPTY_STRING).commit();
//        prefsEditor.putString(authenticationId, Config.EMPTY_STRING).commit();
//        prefsEditor.putString(authenticationDeviceId, Config.EMPTY_STRING).commit();
//        prefsEditor.putString(districtName, Config.EMPTY_STRING).commit();
//        prefsEditor.putString(profileImage, Config.EMPTY_STRING).commit();
        prefsEditor.putString(profileStatus, Config.EMPTY_STRING).commit();
//        prefsEditor.putString(notificationChecked, Config.EMPTY_STRING).commit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        prefsEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        prefsEditor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return appSharedPrefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}