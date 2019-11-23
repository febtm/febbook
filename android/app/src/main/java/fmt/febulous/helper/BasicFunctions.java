package fmt.febulous.helper;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import fmt.febulous.model.Post;
import fmt.febulous.model.ChatUser;

/**
 * Created by Febin M Thomas on 5/6/2016.
 */

public class BasicFunctions {


    private Context mContext;

    private static String today;

    public final String KEY_USER_ID = "user_id";
    public final String KEY_USER_USERNAME = "username";
    public final String KEY_USER_PASSWORD = "password";
    public final String KEY_USER_IMAGE = "userimage";
    public final String KEY_USER_ABOUT = "userabout";
    public final String KEY_USER_NAME = "name";
    public final String KEY_USER_EMAIL = "email";
    public final String KEY_USER_DOB = "dob";
    public final String KEY_USER_COVERIMAGE = "usercover";
    public final String KEY_USER_GENDER = "gender";
    public final String KEY_USER_COLLEGE = "collegename";
    public final String KEY_USER_COURSE = "course";
    public final String KEY_USER_TYPE = "usertype";
    public final String KEY_USER_DEPARTMENT = "department";

    public final String KEY_NOTIFICATION_COUNT = "notification_count";
    public final String KEY_CHAT_COUNT = "chat_count";

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
    private SharedPreferences.Editor LOGIN_EDITOR;
    int LOGIN_PRIVATE_MODE = 0;
    private static final String LOGIN_PREF_NAME = "FEBULOUS";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";


    public String[] USERTYPE = new String[]{
            "USER TYPE",
            "GENERAL USER",
            "STUDENT",
            "TEACHER"
    };

    public String[] USERCOURSE = new String[]{
            "COURSE TYPE",
            "GENERAL",
            "ENGINEERING",
            "MEDICINE",
            "ARTS",
            "SCIENCE"
    };

    public String[] POSTLIST = new String[]{
            "POST TYPE",
            "GENERAL DIGEST",
            "Q N A SECTION",
            "STUDY MATERIALS",
            "TEACH A TOPIC",
            "IDEAS GALORE",
            "EVENTS AND INVITES"
    };

    public String[] DATELIST = new String[]{
            "DATE SORT",
            "EARLIEST TO LATEST",
            "LATEST TO EARLIEST"
    };

    public final String POST_TW = "My Post on @fmt_febulous";

    public final String FOUND_TW = "A Post from @fmt_febulous";


    public BasicFunctions(Context context) {

        this.mContext = context;
        LOGIN_PREFERENCE = mContext.getSharedPreferences(LOGIN_PREF_NAME, LOGIN_PRIVATE_MODE);
        LOGIN_EDITOR = LOGIN_PREFERENCE.edit();

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

    }


    public void setLogin(boolean isLoggedIn) {

        LOGIN_EDITOR.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        LOGIN_EDITOR.commit();

    }

    public boolean isLoggedIn(){
        return LOGIN_PREFERENCE.getBoolean(KEY_IS_LOGGED_IN, false);
    }


    public void setUserDetails(String user_id, String user_name, String user_image, String user_email, String user_password){

        LOGIN_EDITOR.putString(KEY_USER_ID, user_id);
        LOGIN_EDITOR.putString(KEY_USER_USERNAME, user_name);
        LOGIN_EDITOR.putString(KEY_USER_IMAGE, user_image);
        LOGIN_EDITOR.putString(KEY_USER_EMAIL, user_email);
        LOGIN_EDITOR.putString(KEY_USER_PASSWORD, user_password);

        LOGIN_EDITOR.commit();
    }


    public void setUserDataNull(){

        String user_email = LOGIN_PREFERENCE.getString(KEY_USER_EMAIL, "");
        String user_password = LOGIN_PREFERENCE.getString(KEY_USER_PASSWORD, "");

        LOGIN_EDITOR.clear();

        LOGIN_EDITOR.putString(KEY_USER_EMAIL, user_email);
        LOGIN_EDITOR.putString(KEY_USER_PASSWORD, user_password);

        LOGIN_EDITOR.commit();
    }


    public String getUser_id(){ return LOGIN_PREFERENCE.getString(KEY_USER_ID, "");}

    public String getUser_name(){ return LOGIN_PREFERENCE.getString(KEY_USER_USERNAME, "");}

    public String getUser_image(){ return LOGIN_PREFERENCE.getString(KEY_USER_IMAGE, "");}

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


    public void login() {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.CHAT_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (!obj.getBoolean("error")) {

                        JSONObject userObj = obj.getJSONObject("user");

                        ChatUser chatUser = new ChatUser(userObj.getString("user_id"),
                                userObj.getString("username"),
                                userObj.getString("userimage"));

                        Application.getInstance().getPrefManager().storeUser(chatUser);

                    } else
                        Toast.makeText(mContext, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {

                    Toast.makeText(mContext, "Json parse error : " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(mContext, "Volley error : " + error.getMessage() + ", Code : "+networkResponse, Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", getUser_id());
                return params;

            }
        };

        strReq.setRetryPolicy(

                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Application.getInstance().addToRequestQueue(strReq);

    }


    public static String getTimeStamp(String dateStr) {

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format1.parse(dateStr);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR_OF_DAY, 5);
            cal.add(Calendar.MINUTE, 30);

            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(cal.getTime());

            if(dateToday.equals(today))
            {
                format1 = new SimpleDateFormat("hh:mm a");
                String date1 = format1.format(cal.getTime());
                timestamp = "Today  ||  "+ date1;
            }

            else {

                format1 = new SimpleDateFormat("dd");
                String date1 = format1.format(cal.getTime());

                format1 = new SimpleDateFormat("MM");
                String date2 = format1.format(cal.getTime());

                format1 = new SimpleDateFormat("yyyy");
                String date3 = format1.format(cal.getTime());

                format1 = new SimpleDateFormat("hh:mm a");
                String date4 = format1.format(cal.getTime());


                timestamp = date1 + " | " + date2 + " | " + date3
                        + "  ||  " + date4;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;

    }


    public void sendEmail(String from, String temp_name, String temp_email, String temp_subject, String temp_message) {

        if(from.equals("Contact")) {

            String email = "fmt.febulous@gmail.com";

            String message, message1, message2;

            String subject = temp_name + " : " + temp_subject;

            message1 = "Name : " + temp_name + "\n\nEmail-Id : " + temp_email;
            message2 = "\n\nSubject : " + temp_subject + "\n\nMessage : " + temp_message;

            message = message1 + message2;

            EmailBackgroundTask emailBackgroundTask = new EmailBackgroundTask(mContext, 0, email, subject, message);

            emailBackgroundTask.execute();

        }

        else if(from.equals("ForgotPassword")){

            String message, message1, message2, message3, message4, message5;

            message1 = "Greetings " + temp_name + ",\n\n\n";
            message2 = "We have received a request from your account stating that you have forgotten your password. Kindly find your password below -> ";
            message3 = "\n\n" + "Your Password : " + temp_message + "\n\n";
            message4 = "If you did not initiate this password request, please contact us at : fmt.febulous@gmail.com to report the issue.";
            message5 = "\n\n\n" + "Thanks and Regards, \nThe Febulous Team";

            message = message1 + message2 + message3 + message4 + message5;

            EmailBackgroundTask emailBackgroundTask = new EmailBackgroundTask(mContext, 1, temp_email, temp_subject, message);

            emailBackgroundTask.execute();

        }

    }


    private class EmailBackgroundTask extends AsyncTask<Void,Void,Void> {

        private Context context;
        private Session session;

        private int which;
        private String email;
        private String subject;
        private String message;

        private static final String sender_username = "fmt.febulous@gmail.com";
        private static final String sender_password = "well,ifyougettoknowme";

        private ProgressDialog pDialog;

        private EmailBackgroundTask(Context context, int which, String email, String subject, String message){
            this.context = context;
            this.which = which;
            this.email = email;
            this.subject = subject;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(which == 0)
                pDialog = ProgressDialog.show(context,"","Sending Message ... ", false, false);

            else
                pDialog = ProgressDialog.show(context,"","Sending Recovery Email ... ", false, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

        }

        @Override
        protected Void doInBackground(Void... params) {
            Properties props = new Properties();

            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {

                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(sender_username, sender_password);
                        }
                    });

            try {
                MimeMessage mm = new MimeMessage(session);

                mm.setFrom(new InternetAddress(sender_username));
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                mm.setSubject(subject);
                mm.setText(message);

                Transport.send(mm);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    public List<Post> doDateSort(List<Post> mPosts, int flag, int total) {

        double arr[] = new double[total];

        for (int i = 0; i < total; i++) {

            Post post = mPosts.get(i);
            arr[i] = getTimestamp(post.getTempTimestamp());

        }

        if(flag == 0)
            mPosts = asc_quickSort(mPosts, arr, 0, total - 1);

        else
            mPosts = desc_quickSort(mPosts, arr, 0, total - 1);

        return mPosts;
    }



    private List<Post> asc_quickSort(List<Post> mPosts, double[] arr, int low, int high) {

        if (arr == null || arr.length == 0)
            return mPosts;

        if (low >= high)
            return mPosts;

        int middle = low + (high - low) / 2;
        double pivot = arr[middle];

        int i = low, j = high;
        while (i <= j) {
            while (arr[i] < pivot) {
                i++;
            }

            while (arr[j] > pivot) {
                j--;
            }
            if (i <= j) {
                double temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;

                mPosts = setValues(mPosts, i, j);

                i++;
                j--;
            }
        }

        if (low < j)
            mPosts = asc_quickSort(mPosts, arr, low, j);

        if (high > i)
            mPosts = asc_quickSort(mPosts, arr, i, high);

        return mPosts;
    }


    private List<Post> desc_quickSort(List<Post> mPosts, double[] arr, int low, int high) {

        if (arr == null || arr.length == 0)
            return mPosts;

        if (low >= high)
            return mPosts;

        int middle = low + (high - low) / 2;
        double pivot = arr[middle];

        int i = low, j = high;
        while (i <= j) {
            while (arr[i] > pivot) {
                i++;
            }
            while (arr[j] < pivot) {
                j--;
            }

            if (i <= j) {
                double temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                mPosts = setValues(mPosts, i, j);
                i++;
                j--;
            }
        }

        if (low < j)
            mPosts = desc_quickSort(mPosts, arr, low, j);

        if (high > i)
            mPosts = desc_quickSort(mPosts, arr, i, high);

        return mPosts;

    }


    private List<Post> setValues(List<Post> mPosts, int i, int j) {

        Post post_temp = mPosts.get(i);

        mPosts.set(i, mPosts.get(j));

        mPosts.set(j, post_temp);

        return mPosts;

    }

    private double getTimestamp(String str) {

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Timestamp timestamp = null;

        try {

            Date parsedDate = format1.parse(str);

            timestamp = new Timestamp(parsedDate.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp.getTime();

    }


    public void getCurrentUserData(String email) {

        String url = EndPoints.GET_CURRENT_USER + email;

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


    public void performTask(String method, String item_id, String item_title){

        PerformTaskBackgroundTask performtaskbackgroundTask = new PerformTaskBackgroundTask(mContext);
        performtaskbackgroundTask.execute(method, item_id, item_title, getUser_id());

    }

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

                URL url = new URL(EndPoints.BASIC_FUNCTIONS);
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

                String response = "";
                String line;

                while((line = bufferedReader.readLine())!=null)  {
                    response += line;
                }

                bufferedReader.close();
                httpURLConnection.disconnect();
                IS.close();

                return response;

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

                case "You have liked this post !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "You have disliked this post !":

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