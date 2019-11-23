package fmt.febulous.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fmt.febulous.R;

import static com.facebook.FacebookSdk.getApplicationContext;

//Created by Febin M Thomas on 5/6/16.
//Updated by Febin M Thomas on 17/5/18.

public class BasicFunctions {


    private static final String BASE_URL = "https://febtech.000webhostapp.com/android_febbook/";
    public static final String ADD_CHAT = BASE_URL + "addChat.php";
    private static final String BASIC_FUNCTIONS = BASE_URL + "basicFunctions.php";
    public static final String CHECK_FILE_EXISTS = BASE_URL + "checkFileExists.php?fileName=";
    public static final String DELETE_CHAT = BASE_URL + "deleteChat.php";
    public static final String DELETE_NOTIFICATION = BASE_URL + "deleteNotification.php";
    public static final String EDIT_PROFILE = BASE_URL + "editProfile.php";
    public static final String FILE_URL = BASE_URL + "StudyMaterials/";
    public static final String GET_ALL_CHAT_ROOMS = BASE_URL + "getAllChatRooms.php?user_id=";
    public static final String GET_ALL_NOTIFICATIONS = BASE_URL + "getAllNotifications.php?current_user_id=";
    public static final String GET_ALL_POSTS = BASE_URL + "getAllPosts.php?index=";
    public static final String GET_CHAT_ROOM = BASE_URL + "getChatRoom.php?chat_room_id=";
    public static final String GET_COMMENTS = BASE_URL + "getComment.php?id=";
    static final String GET_COUNT = BASE_URL + "getCount.php?user_id=";
    private static final String GET_CURRENT_USER = BASE_URL + "getCurrentUser.php?email=";
    public static final String GET_DISLIKES = BASE_URL + "getDislike.php?id=";
    public static final String GET_LIKES = BASE_URL + "getLike.php?id=";
    public static final String GET_POST_ID = BASE_URL + "getPostId.php?post_id=";
    public static final String GET_POST_PROFILE = BASE_URL + "getPostProfile.php?post_user_name=";
    public static final String LOGIN = BASE_URL + "login.php";
    static final String LOGOUT = BASE_URL + "logout.php";
    public static final String MY_PROFILE = BASE_URL + "myProfile.php?username=";
    public static final String POST = BASE_URL + "post.php";
    public static final String SEARCH_FOR_POSTS = BASE_URL + "searchForPosts.php?index=";
    public static final String SEARCH_FOR_PROFILES = BASE_URL + "searchForProfiles.php?index=";
    private static final String SEND_EMAIL = BASE_URL + "sendEmail.php";
    public static final String SEND_MESSAGE = BASE_URL + "sendMessage.php?chat_room_id=";
    public static final String SIGNIN = BASE_URL + "signin.php";
    public static final String STUDY_MATERIALS = BASE_URL + "studyMaterials.php";

    private Context mContext;
    private RequestQueue mRequestQueue;
    private static String today;

    public final String KEY_USER_ID = "user_id";
    public final String KEY_USER_USERNAME = "username";
    private final String KEY_USER_PASSWORD = "password";
    public final String KEY_USER_IMAGE = "userimage";
    public final String KEY_USER_ABOUT = "userabout";
    public final String KEY_USER_NAME = "name";
    public final String KEY_USER_EMAIL = "email";
    public final String KEY_USER_DOB = "dob";
    public final String KEY_USER_COVERIMAGE = "usercover";
    public final String KEY_USER_GENDER = "gender";
    public final String KEY_USER_COLLEGE = "collegename";
    public final String KEY_USER_COURSE = "course";
    public final String KEY_USER_DEPARTMENT = "department";

    final String KEY_NOTIFICATION_COUNT = "notification_count";

    final String KEY_CHAT_COUNT = "chat_count";

    public final String KEY_POST_COMMENT = "comment";
    public final String KEY_POST_TIMESTAMP = "timestamp";
    public final String KEY_POST_ID = "post_id";
    public final String KEY_POST_TITLE = "post_title";
    public final String KEY_POST_TYPE = "post_type";
    public final String KEY_POST_DESC = "post_description";
    public final String KEY_POST_IMAGE = "post_image";
    public final String KEY_CHAT_ROOM_ID = "chat_room_id";
    public final String KEY_NOTIFICATION_POST_USERREAD = "post_user_read";
    public final String KEY_NOTIFICATION_USER_ID = "notification_user_id";
    public final String KEY_NOTIFICATION_USERNAME = "notification_username";
    public final String KEY_NOTIFICATION_USERPICTURE = "notification_userpicture";

    public final String JSON_ARRAY = "result";

    public final String TWITTER_KEY = "Sq84RCBzipnCW2x1QUlnHNNWG";
    public final String TWITTER_SECRET = "ehTEpvXXVNdtpJapViTNJCfjHn7INbOUfxVo3oSimOAPdojQJp";

    private SharedPreferences LOGIN_PREFERENCE;
    private SharedPreferences.Editor LOGIN_PREF_EDITOR;
    private static final String LOGIN_PREF_NAME = "LOGIN_PREF";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences FCM_NOT_PREFERENCE;
    private SharedPreferences.Editor FCM_NOT_PREF_EDITOR;
    private static final String FCM_NOT_PREF_NAME = "FCM_NOT_PREF";
    private static final String FCM_NOT_TOKEN = "FCM_NOT_TOKEN";
    private static final int FCM_NOT_SMALL_NOTIFICATION = 235;

    @SuppressLint("CommitPrefEdits")
    public BasicFunctions(Context context) {

        this.mContext = context;

        LOGIN_PREFERENCE = mContext.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE);

        LOGIN_PREF_EDITOR = LOGIN_PREFERENCE.edit();

        FCM_NOT_PREFERENCE = mContext.getSharedPreferences(FCM_NOT_PREF_NAME, Context.MODE_PRIVATE);

        FCM_NOT_PREF_EDITOR = FCM_NOT_PREFERENCE.edit();

        Calendar calendar = Calendar.getInstance();

        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

    }


    public void storeFCMNotDeviceToken(String token){

        FCM_NOT_PREFERENCE = mContext.getSharedPreferences(FCM_NOT_PREF_NAME, Context.MODE_PRIVATE);
        FCM_NOT_PREF_EDITOR.putString(FCM_NOT_TOKEN, token);
        FCM_NOT_PREF_EDITOR.commit();

    }


    public String getFCMNotDeviceToken(){

        FCM_NOT_PREFERENCE = mContext.getSharedPreferences(FCM_NOT_PREF_NAME, Context.MODE_PRIVATE);
        return  FCM_NOT_PREFERENCE.getString(FCM_NOT_TOKEN, null);

    }


    public boolean isAppInstalled(String app_id) {

        PackageManager pm = mContext.getPackageManager();

        try {

            pm.getPackageInfo(app_id, PackageManager.GET_ACTIVITIES);
            return true;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;

    }


    public void showSmallFCMNotification(String title, String message, Intent intent) {

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        FCM_NOT_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, title);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.me_notifications).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.me_notifications)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_logo_main))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(FCM_NOT_SMALL_NOTIFICATION, notification);

        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getApplicationContext().getPackageName() + "/raw/notification_sound");
            Ringtone r = RingtoneManager.getRingtone(mContext.getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setLogin(boolean isLoggedIn) {

        LOGIN_PREF_EDITOR.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        LOGIN_PREF_EDITOR.commit();

    }


    public boolean isLoggedIn(){
        return LOGIN_PREFERENCE.getBoolean(KEY_IS_LOGGED_IN, false);
    }


    public void setUserDetails(String user_id, String user_name, String user_image, String user_email, String user_password){

        LOGIN_PREF_EDITOR.putString(KEY_USER_ID, user_id);
        LOGIN_PREF_EDITOR.putString(KEY_USER_USERNAME, user_name);
        LOGIN_PREF_EDITOR.putString(KEY_USER_IMAGE, user_image);
        LOGIN_PREF_EDITOR.putString(KEY_USER_EMAIL, user_email);
        LOGIN_PREF_EDITOR.putString(KEY_USER_PASSWORD, user_password);

        LOGIN_PREF_EDITOR.commit();
    }


    void setUserDataNull(){

        String user_email = LOGIN_PREFERENCE.getString(KEY_USER_EMAIL, "");
        String user_password = LOGIN_PREFERENCE.getString(KEY_USER_PASSWORD, "");

        LOGIN_PREF_EDITOR.clear();

        FCM_NOT_PREF_EDITOR.clear();
        FCM_NOT_PREF_EDITOR.commit();

        LOGIN_PREF_EDITOR.putString(KEY_USER_EMAIL, user_email);
        LOGIN_PREF_EDITOR.putString(KEY_USER_PASSWORD, user_password);

        LOGIN_PREF_EDITOR.commit();

    }


    public String getUser_id(){ return LOGIN_PREFERENCE.getString(KEY_USER_ID, "");}

    public String getUser_name(){ return LOGIN_PREFERENCE.getString(KEY_USER_USERNAME, "");}

    public String getUser_email(){ return LOGIN_PREFERENCE.getString(KEY_USER_EMAIL, "");}

    public String getUser_password(){ return LOGIN_PREFERENCE.getString(KEY_USER_PASSWORD, "");}


    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }

            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }

            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }

        else if ("content".equalsIgnoreCase(uri.getScheme())) {


            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    private static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }


    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }


    public boolean isConnectingToInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            assert connectivityManager != null;
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;

            for (Network mNetwork : networks) {

                networkInfo = connectivityManager.getNetworkInfo(mNetwork);

                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }

        }else {

            if (connectivityManager != null) {

                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

                if (info != null) {

                    for (NetworkInfo anInfo : info) {

                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    @SuppressLint("SimpleDateFormat")
    public static String getTimeStamp(String dateStr) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format1.setTimeZone(TimeZone.getTimeZone("GMT"));

        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {

            Date date = format1.parse(dateStr);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            todayFormat.setTimeZone(TimeZone.getDefault());

            String dateToday = todayFormat.format(cal.getTime());

            if(dateToday.equals(today))
            {
                format1 = new SimpleDateFormat("HH:mm");
                format1.setTimeZone(TimeZone.getDefault());

                String date1 = format1.format(cal.getTime());
                timestamp = "Today  ||  "+ date1;
            }

            else {

                format1 = new SimpleDateFormat("dd");
                format1.setTimeZone(TimeZone.getDefault());
                String date1 = format1.format(cal.getTime());

                format1 = new SimpleDateFormat("MM");
                format1.setTimeZone(TimeZone.getDefault());
                String date2 = format1.format(cal.getTime());

                format1 = new SimpleDateFormat("yyyy");
                format1.setTimeZone(TimeZone.getDefault());
                String date3 = format1.format(cal.getTime());

                format1 = new SimpleDateFormat("HH:mm");
                format1.setTimeZone(TimeZone.getDefault());
                String date4 = format1.format(cal.getTime());

                timestamp = date1 + " | " + date2 + " | " + date3
                        + "  ||  " + date4;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;

    }


    public boolean HasCameraAndStoragePermission() {

        int cameraResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        int readExternalResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return ((cameraResult == PackageManager.PERMISSION_GRANTED)
                && (readExternalResult == PackageManager.PERMISSION_GRANTED)
                && (writeExternalResult == PackageManager.PERMISSION_GRANTED));

    }


    public boolean HasStoragePermission() {

        int readExternalResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return ((readExternalResult == PackageManager.PERMISSION_GRANTED)
                && (writeExternalResult == PackageManager.PERMISSION_GRANTED));

    }


    public void sendEmail(String from, String from_email, String to_email, String subject, String message) {

        String temp_subject, temp_message;

        switch (from) {

            case "ContactUs":

                temp_subject = to_email + " : " + subject;

                temp_message = "Application : FebBook" + "\n\nName : " + to_email + "\n\nEmail-Id : " + from_email;
                temp_message += "\n\nSubject : " + subject + "\n\nMessage : " + message;

                to_email = "fmt.febulous@gmail.com";

                subject = temp_subject;

                message = temp_message;

                break;

            case "RecoverPassword":

                temp_message = "Greetings " + from_email + ",\n\n";
                temp_message += "We have received a request from your account stating that you have forgotten your password. Kindly find your password below -> ";
                temp_message += "\n\n" + "Your Password : " + message + "\n\n";
                temp_message += "If you did not initiate this password request, please contact us at : fmt.febulous@gmail.com to report the issue.";
                temp_message += "\n\n" + "Regards, \nThe FebBook Team";

                from_email = "fmt.febulous@gmail.com";

                message = temp_message;

                break;

        }

        SendingEmailBackgroundTask emailBackgroundTask = new SendingEmailBackgroundTask(mContext);

        emailBackgroundTask.execute(from, from_email, to_email, subject, message);

    }


    @SuppressLint("StaticFieldLeak")
    private class SendingEmailBackgroundTask extends AsyncTask<String, Void, String> {

        private ProgressDialog bf_loading;

        Context ctx;
        String data;

        SendingEmailBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            bf_loading = new ProgressDialog(mContext);
            bf_loading.setMessage("Sending Email ... ");
            bf_loading.setIndeterminate(false);
            bf_loading.setCancelable(true);
            bf_loading.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];
            String from_email = params[1];
            String to_email = params[2];
            String subject = params[3];
            String message = params[4];

            try {

                URL url = new URL(SEND_EMAIL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                data = URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode(method, "UTF-8")
                        + "&" + URLEncoder.encode("from_email", "UTF-8") + "=" + URLEncoder.encode(from_email, "UTF-8")
                        + "&" + URLEncoder.encode("to_email", "UTF-8") + "=" + URLEncoder.encode(to_email, "UTF-8")
                        + "&" + URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8")
                        + "&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }

                bufferedReader.close();
                httpURLConnection.disconnect();
                IS.close();

                return response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            switch (result) {

                case "Thank You for your message, kindly wait until our Team responds to it !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

                case "An Account with this Email-Id does not exist !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

                case "A recovery email has been sent to your Email-Id !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

                case "Sending Email Failed !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

                default:

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    bf_loading.dismiss();

                    break;

            }
        }
    }


    public void getCurrentUserData(String email) {

        String url = BasicFunctions.GET_CURRENT_USER + email;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON_User(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }


    private void showJSON_User(String response) {

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
            JSONObject profileData = result.getJSONObject(0);

            setUserDetails(profileData.getString(KEY_USER_ID),
                    profileData.getString(KEY_USER_USERNAME),
                    profileData.getString(KEY_USER_IMAGE),
                    profileData.getString(KEY_USER_EMAIL),
                    profileData.getString(KEY_USER_PASSWORD));

            setLogin(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);

    }

    public void performTask(String method, String item_id, String item_title){

        PerformTaskBackgroundTask performtaskbackgroundTask = new PerformTaskBackgroundTask(mContext);
        performtaskbackgroundTask.execute(method, item_id, item_title, getUser_id());

    }

    @SuppressLint("StaticFieldLeak")
    private class PerformTaskBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;

        private ProgressDialog pDialog;

        PerformTaskBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ctx);
            pDialog.setMessage("Performing Task ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];
            String id = params[1];
            String comment = params[2];
            String user_id = params[3];

            try {

                URL url = new URL(BasicFunctions.BASIC_FUNCTIONS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode(method, "UTF-8")
                        + "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                        + "&" + URLEncoder.encode("comment", "UTF-8") + "=" + URLEncoder.encode(comment, "UTF-8")
                        + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS,"iso-8859-1"));

                StringBuilder response = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine())!=null)  {
                    response.append(line);
                }

                bufferedReader.close();
                httpURLConnection.disconnect();
                IS.close();

                return response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            switch(result){

                case "Your Post has been deleted !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "You have reported this Post !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "Your Comment has been posted !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "You have liked this Post !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "You have disliked this Post !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "Your Comment has been deleted !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "You have reported this Comment !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "This User has been reported !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "Notification sent successfully !":

                    pDialog.dismiss();
                    break;

                case "Success !":

                    pDialog.dismiss();
                    break;

                default:

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

            }
        }
    }
}