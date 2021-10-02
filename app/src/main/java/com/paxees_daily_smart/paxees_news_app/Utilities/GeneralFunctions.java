package com.paxees_daily_smart.paxees_news_app.Utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.paxees_daily_smart.paxees_news_app.BuildConfig;
import com.paxees_daily_smart.paxees_news_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class GeneralFunctions {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private static int sTheme;
    private static List<String> ipAddresses = null;
    private Context mContext;
    private static int CODE_AUTHENTICATION_VERIFICATION = 241;

    private static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".myfileprovider";
    private static final String SHARED_FOLDER = "shared";

    public GeneralFunctions(Context mContext) {
        this.mContext = mContext;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected;
    }


    public static String isAuthenticatedHttpCode(String jsonResponse) {
        String status = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonResponse);
            status = String.valueOf(jsonObject.getInt("http_code"));
        } catch (JSONException e) {
            //handle exception
        }
        return status;
    }

    public static String isAuthenticatedMessage(String jsonResponse) {
        String message = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonResponse);
            message = jsonObject.getString("message");
        } catch (JSONException e) {
            //handle exception
        }
        return message;
    }


    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }


    public static GradientDrawable getRoundBackGround_text(String textColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(Color.parseColor(textColor));
        gd.setCornerRadius(5);
        gd.setStroke(4, Color.rgb(255, 255, 255));
        return gd;
    }

    public static String getSubstring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    public static String getDateString(String inputFormat, String outputFormat, String inputDate) {
        Date parsed = null;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (Exception e) {
            //LOGE(TAG, "ParseException - dateFormat");
        }
        return outputDate;
    }

    public static String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDbDate(String input) {
        String dbDate = null;
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = originalFormat.parse(input);
            dbDate = targetFormat.format(date);

        } catch (Exception ex) {
            // Handle Exception.
        }
        return dbDate;
    }

    public static String getDateFormat(String input) {
        String dbDate = null;
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(input);
            dbDate = targetFormat.format(date);

        } catch (Exception ex) {
            // Handle Exception.
        }
        return dbDate;
    }

    private static SecretKeySpec generate_AESKey(final String key, final String encoding) {
        try {
            final byte[] finalKey = new byte[16];
            int i = 0;
            for (byte b : key.getBytes(encoding))
                finalKey[i++ % 16] ^= b;
            return new SecretKeySpec(finalKey, "AES");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String aes_decrypt(String strEncrypted) {
        String strDecrypted = null;
        // Decrypt
        try {
            final Cipher decryptCipher = Cipher.getInstance("AES");
            decryptCipher.init(Cipher.DECRYPT_MODE, generate_AESKey("770A8A65DA156D24EE2A093277530142", "UTF-8"));
            byte[] decryptedTextBytes = Base64.decode(strEncrypted, Base64.NO_WRAP);
            strDecrypted = new String(decryptCipher.doFinal(decryptedTextBytes));
        } catch (Exception e) {
            //todo
        }
        return strDecrypted;
    }

    public static String aes_encrypt(String strText) {
        String strEncrypted = null;
        try {
            final Cipher encryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, generate_AESKey("770A8A65DA156D24EE2A093277530142", "UTF-8"));
            byte[] encryptedBytes = encryptCipher.doFinal(strText.getBytes("UTF-8"));
            strEncrypted = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            //todo
        }
        return strEncrypted;
    }

    public static File getCacheFolder(Context context) {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "cachefolder");
            if (!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }
        if (!cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir(); //get system cache folder
        }
        return cacheDir;
    }

    public static File getDataFolder(Context context) {
        File dataDir = null;
        if (Environment.getExternalStorageState().equals(Environment.DIRECTORY_DOWNLOADS)) {
            dataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }
        if (dataDir == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                dataDir = new File(Environment.getExternalStorageDirectory(), "mpowerhr");
                if (!dataDir.isDirectory()) {
                    dataDir.mkdirs();
                }
            }
        }
        return dataDir;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void storeImage(File fileDir, InputStream inputStream,
                                  String fileName) throws IOException {
        File file = new File(fileDir, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        inputStream.close();
        fos.close();
        bitmap.recycle();
        bitmap = null;
    }

    public static String stipEndOfString(String originalString, String pattern) {
        String tempString = Config.EMPTY_STRING;
        try {
            if (originalString.endsWith(pattern)) {
                tempString = originalString.substring(0, originalString.length() - pattern.length());
            } else {
                tempString = originalString;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return tempString;
    }

    public static void showLoader(final ProgressDialog progressDialog, final Activity activity, String message) {
        try {
            if (progressDialog != null) {
                progressDialog.setMessage(message);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }
            progressDialog.show();
        } catch (Exception ex) {
            //catch exception
        }
    }


    public static String truncate(String str, int len) {
        if (len <= 3) {
            return str;
        }

        if (str.length() > len) {
            return str.substring(0, (len - 2)) + "...";
        } else {
            return str;
        }
    }

    public static void hideLoader(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception ex) {
            Log.e("hideLoaderException", ex.getMessage());
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @SuppressLint("HardwareIds")
    public static String getDeviceId(Activity activity) {
        String strInfo = Config.EMPTY_STRING;
        try {
            // Check if the READ_PHONE_STATE permission is already available.
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.READ_PHONE_STATE)) {
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                }
            } else {
                strInfo = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
            }
        } catch (Exception ex) {
            //to do
        }
        return strInfo;
    }

    private static String fixNull(String strValue) {
        strValue = (strValue == null) ? Config.EMPTY_STRING : strValue;
        strValue = (strValue == "null") ? Config.EMPTY_STRING : strValue;
        return strValue;
    }


    public static boolean compareDates(String fromDate, String toDate) {
        SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");
        boolean result = false;
        try {
            if (dfDate.parse(fromDate).before(dfDate.parse(toDate))) {
                result = true;
            } else if (dfDate.parse(fromDate).equals(dfDate.parse(toDate))) {
                result = true;
            } else {
                result = false; //If start date is greater than  the end date
            }
        } catch (ParseException e) {
            // todo
        }
        return result;

    }


    public static boolean compareDateBetween(String checkDate, String fromDate, String toDate) {
        SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");
        boolean result = false;
        try {
            if (dfDate.parse(checkDate).before(dfDate.parse(fromDate))) {
                result = true;
            } else if (dfDate.parse(checkDate).after(dfDate.parse(toDate))) {
                result = true;
            } else {
                result = false; //If start date is greater than  the end date
            }
        } catch (ParseException e) {
            // todo
        }
        return result;

    }

    public static boolean compareTimes(String fromTime, String toTime) {
        SimpleDateFormat dfDate = new SimpleDateFormat("HH:mm");
        boolean result = true;
        try {
            Date inTime = dfDate.parse(fromTime);
            Date outTime = dfDate.parse(toTime);

            if (inTime.compareTo(outTime) > 0) {
                result = false;
            }
        } catch (ParseException e) {
            // todo
        }
        return result;

    }

    public static boolean compareTimesGreater(String fromTime, String toTime) {
        SimpleDateFormat dfDate = new SimpleDateFormat("HH:mm");
        boolean result = true;
        try {
            Date inTime = dfDate.parse(fromTime);
            Date outTime = dfDate.parse(toTime);

            if (inTime.compareTo(outTime) >= 0) {
                result = false;
            }
        } catch (ParseException e) {
            // todo
        }
        return result;

    }

    static byte[] getHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            //e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }

    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "x", new BigInteger(1, data));
    }

    public static String getSha2Hash(String partString) {
        String string = "4E2A05014ASC2" + partString;
        return bin2hex(getHash(string));
    }

    public static String getMd5Hash(String partString) {
        String string = "4E2A05014ASC2" + partString;
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static String roundTwoDecimals(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(value);
    }

    public static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void showAlert(Activity activity, String message) {
        AlertDialog.Builder builder;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.MyDialogTheme);
                builder = new AlertDialog.Builder(ctw);
            } else {
                builder = new AlertDialog.Builder(activity);
            }
            builder.setIcon(R.drawable.ic_check_box_checked).setTitle("Error")
                    .setMessage(message)
                    .setNegativeButton("OK", null).show();
        } catch (Exception ex) {
            // Catch exception here
        }
    }
}
