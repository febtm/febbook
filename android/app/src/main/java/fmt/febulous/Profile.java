package fmt.febulous;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import fmt.febulous.helper.BasicFunctions;
import fmt.febulous.helper.EndPoints;
import fmt.febulous.helper.ViewImage;


public class Profile extends AppCompatActivity
    implements ProfileFragment.onMyprofileFragmentItemSelected {


    JSONArray SearchProfileArray = null;

    private TextView MP_USERNAME, MP_ABOUT, MP_NAME, MP_DATE, MP_EMAIL, MP_GENDER,
            MP_COURSE, MP_USERTYPE, MP_DEPARTMENT, MP_COLLEGE;

    private ImageView MP_COVER;

    Button MP_CONTACT, MP_REPORT;

    private CircularImageView MP_USERIMAGE;

    private String mp_userid = "", mp_username = "", mp_userimage = "", mp_usercover = "";

    EditText ET_SEARCH;

    private String chat_room_id;

    String searchText;
    ListView search_list;
    ListViewAdapter SearchListAdapter;
    ArrayList<ItemList> searcharraylist = new ArrayList<>();

    String TYPE;

    public static String PROFILE_USERNAME;

    private BasicFunctions basicFunctions;

    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        }

        ImageButton leftMenuBtn = (ImageButton) findViewById(R.id.mp_left_menu_btn);

        leftMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Profile.this, HomePage.class);
                startActivity(intent);

            }
        });

        ImageButton rightMenuBtn = (ImageButton) findViewById(R.id.mp_right_menu_btn);

        rightMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Profile.this, ModifyProfile.class);
                startActivity(intent);
            }
        });


        MP_COVER = (ImageView) findViewById(R.id.mp_usercover);
        MP_USERNAME = (TextView) findViewById(R.id.mp_usernametext);
        MP_ABOUT = (TextView) findViewById(R.id.mp_userabouttext);
        MP_NAME = (TextView) findViewById(R.id.mp_nametext);
        MP_DATE = (TextView) findViewById(R.id.mp_dobtext);
        MP_GENDER = (TextView) findViewById(R.id.mp_gendertext);
        MP_EMAIL = (TextView) findViewById(R.id.mp_emailtext);
        MP_COURSE = (TextView) findViewById(R.id.mp_coursetext);
        MP_USERTYPE = (TextView) findViewById(R.id.mp_educationaldetailstext);
        MP_DEPARTMENT = (TextView) findViewById(R.id.mp_departmenttext);
        MP_COLLEGE = (TextView) findViewById(R.id.mp_collegenametext);
        MP_USERIMAGE = (CircularImageView) findViewById(R.id.mp_userimage);

        MP_CONTACT = (Button) findViewById(R.id.mp_contactuser);
        MP_REPORT = (Button) findViewById(R.id.mp_reportuser);

        search_list = (ListView) findViewById(R.id.mp_search_list);
        search_list.setVisibility(View.GONE);

        ET_SEARCH = (EditText) findViewById(R.id.mp_search_bar);

        basicFunctions = new BasicFunctions(this);

        Intent intent = getIntent();
        TYPE = intent.getStringExtra("type");

        if(TYPE.equals("SELF")) {
            PROFILE_USERNAME = basicFunctions.getUser_name();
            MP_CONTACT.setVisibility(View.GONE);
            MP_REPORT.setVisibility(View.GONE);
        }

        else if(basicFunctions.getUser_name().equals(TYPE)){
            PROFILE_USERNAME = TYPE;
            MP_CONTACT.setVisibility(View.GONE);
            MP_REPORT.setVisibility(View.GONE);
        }

        else {
            PROFILE_USERNAME = TYPE;
            MP_CONTACT.setVisibility(View.VISIBLE);
            MP_REPORT.setVisibility(View.VISIBLE);
        }

        MP_CONTACT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbchat();

            }
        });

        MP_REPORT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                report();
            }
        });


        MP_USERIMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Profile.this, ViewImage.class);

                if(!mp_userimage.equals("")) {

                    byte[] decodedString = Base64.decode(mp_userimage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    intent.putExtra("image", "large_image");

                    File cacheDir = getBaseContext().getCacheDir();
                    File f = new File(cacheDir, "large_image");

                    try {
                        FileOutputStream out = new FileOutputStream(f);
                        decodedByte.compress(
                                Bitmap.CompressFormat.JPEG,
                                100, out);
                        out.flush();
                        out.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else
                    intent.putExtra("image", "noimage");

                startActivity(intent);
            }
        });


        MP_COVER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Profile.this, ViewImage.class);

                if(!mp_usercover.equals("")) {

                    byte[] decodedString = Base64.decode(mp_usercover, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    intent.putExtra("image", "large_image");

                    File cacheDir = getBaseContext().getCacheDir();
                    File f = new File(cacheDir, "large_image");

                    try {
                        FileOutputStream out = new FileOutputStream(f);
                        decodedByte.compress(
                                Bitmap.CompressFormat.JPEG,
                                100, out);
                        out.flush();
                        out.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else
                    intent.putExtra("image", "noimage");

                startActivity(intent);
            }
        });


        if(basicFunctions.isConnectingToInternet())
            myprofile();

        else{

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(Profile.this, HomePage.class);
                            startActivity(intent);

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Network Failure : Please check your Internet Connection !")
                    .setPositiveButton("Try Again ... ", dialogClickListener).show();

        }

        ET_SEARCH.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                searchText = ET_SEARCH.getText().toString().toLowerCase(Locale.getDefault());

                if (TextUtils.isEmpty(searchText))
                    search_list.setVisibility(View.GONE);

                else {

                    searcharraylist.clear();
                    searchData(searchText, 0);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });


    }


    @Override
    public void onMyProfileFragmentItemSelected(String id) {

            Intent intent = new Intent(Profile.this, PostView.class);
            intent.putExtra("id", id);
            startActivity(intent);

    }


    private void myprofile() {

        pDialog = ProgressDialog.show(this, "", "Fetching Profile ... ", false, false);

        StringRequest stringRequest = new StringRequest(EndPoints.MY_PROFILE+PROFILE_USERNAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showJSON_Profile(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Profile.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Profile.this);
        requestQueue.add(stringRequest);
    }


    private void showJSON_Profile(String response) {

        String mp_userabout = "";
        String mp_email = "";
        String mp_name = "";
        String mp_gender = "";
        String mp_date = "";
        String mp_college = "";
        String mp_usertype = "";
        String mp_department = "";
        String mp_course = "";


        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(basicFunctions.JSON_ARRAY);
            JSONObject profileData = result.getJSONObject(0);

            mp_userid = profileData.getString(basicFunctions.KEY_USER_ID);
            mp_username = profileData.getString(basicFunctions.KEY_USER_USERNAME);
            mp_userimage = profileData.getString(basicFunctions.KEY_USER_IMAGE);
            mp_userabout = profileData.getString(basicFunctions.KEY_USER_ABOUT);
            mp_email = profileData.getString(basicFunctions.KEY_USER_EMAIL);
            mp_name = profileData.getString(basicFunctions.KEY_USER_NAME);
            mp_date = profileData.getString(basicFunctions.KEY_USER_DOB);
            mp_usercover = profileData.getString(basicFunctions.KEY_USER_COVERIMAGE);
            mp_gender = profileData.getString(basicFunctions.KEY_USER_GENDER);
            mp_college = profileData.getString(basicFunctions.KEY_USER_COLLEGE);
            mp_usertype = profileData.getString(basicFunctions.KEY_USER_TYPE);
            mp_department = profileData.getString(basicFunctions.KEY_USER_DEPARTMENT);
            mp_course = profileData.getString(basicFunctions.KEY_USER_COURSE);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        MP_USERNAME.setText(mp_username);
        MP_EMAIL.setText(mp_email);
        MP_COURSE.setText(mp_course);

        String ut = mp_usertype.toUpperCase() + "'S DETAILS";

        MP_USERTYPE.setText(ut);

        if(!mp_userabout.equals("")) MP_ABOUT.setText(mp_userabout);

        else MP_ABOUT.setText("-");

        if(!mp_name.equals("")) MP_NAME.setText(mp_name);

        else MP_NAME.setText("-");


        if(!mp_gender.equals("")) {

            if (mp_gender.equals("M"))
                MP_GENDER.setText(getResources().getString(R.string.male));

            else if (mp_gender.equals("F"))
                MP_GENDER.setText(getResources().getString(R.string.female));

        }

        else MP_GENDER.setText("-");


        if(!mp_date.equals("")) MP_DATE.setText(mp_date);

        else MP_DATE.setText("-");


        if(!mp_department.equals("")) MP_DEPARTMENT.setText(mp_department);

        else MP_DEPARTMENT.setText("-");


        if(!mp_college.equals("")) MP_COLLEGE.setText(mp_college);

        else MP_COLLEGE.setText("-");


        if(!mp_userimage.equals("")) {

            MP_USERIMAGE.setBackground(null);
            byte[] decodedString_1 = Base64.decode(mp_userimage, Base64.DEFAULT);
            Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

            MP_USERIMAGE.setImageBitmap(decodedByte_1);

        }

        else {

            MP_USERIMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_userimage, null));
            MP_USERIMAGE.setImageResource(R.drawable.app_userbackground);

        }


        if(!mp_usercover.equals("")) {

            MP_COVER.setBackground(null);
            byte[] decodedString_2 = Base64.decode(mp_usercover, Base64.DEFAULT);
            Bitmap decodedByte_2 = BitmapFactory.decodeByteArray(decodedString_2, 0, decodedString_2.length);

            MP_COVER.setImageBitmap(decodedByte_2);

        }

        else {

            MP_COVER.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
            MP_COVER.setImageResource(R.drawable.app_userbackground);

        }


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mp_root_layout, ProfileFragment.newInstance(), "PostList")
                .commit();


        pDialog.dismiss();

    }


    private void searchData(String searchText, final int index) {

        pDialog = ProgressDialog.show(this, "", "Fetching Profiles ... ", false, false);

        String url = EndPoints.SEARCH_FOR_PROFILES + index + "&searchText=" + searchText;

        url = url.replaceAll(" ", "%20");

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObj = new JSONObject(response);
                    SearchProfileArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

                    if (index == 0 && SearchProfileArray.length() == 0) {

                        search_list.setVisibility(View.GONE);
                        Toast.makeText(Profile.this, "No Profiles Found !", Toast.LENGTH_LONG).show();

                    }

                    else if(SearchProfileArray.length() == 0)
                        Toast.makeText(Profile.this, "No More Profiles Found !", Toast.LENGTH_LONG).show();

                    else
                        search_list.setVisibility(View.VISIBLE);


                    for (int i = 0; i < SearchProfileArray.length(); i++) {

                        JSONObject jsonObject = SearchProfileArray.getJSONObject(i);

                        ItemList itemList = new ItemList(jsonObject.getString(basicFunctions.KEY_USER_USERNAME),
                                jsonObject.getString(basicFunctions.KEY_USER_IMAGE),
                                jsonObject.getString(basicFunctions.KEY_USER_TYPE));

                        searcharraylist.add(itemList);

                    }

                    SearchListAdapter = new ListViewAdapter(Profile.this);
                    search_list.setAdapter(SearchListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Profile.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Profile.this);
        requestQueue.add(stringRequest);

        pDialog.dismiss();

    }


    private void report(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "reportuser";

                        basicFunctions.performTask(method, "", "U : " + PROFILE_USERNAME);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(Profile.this,"Reporting Cancelled !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setMessage("Are you sure you want to report this User ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }



    private class ChatBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;
        String data;

        ChatBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

                String method = params[0];
                String current_user_id = params[1];
                String user_id = params[2];


                try {

                    URL url = new URL(EndPoints.ADD_CHAT);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));


                    data = URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode(method, "UTF-8")
                            + "&" + URLEncoder.encode("current_user_id", "UTF-8") + "=" + URLEncoder.encode(current_user_id, "UTF-8")
                            + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();
                    InputStream IS = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                    String response = "";
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
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

            switch (result){

                case "A Chat request has been sent to this User !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    break;

                case "A Chat request has already been sent to this User !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    break;

                default:

                    chat_room_id = result;
                    login();

                    break;

            }
        }
    }


    private void dbchat() {

        String method = "chat_request";

        ChatBackgroundTask backgroundTask = new ChatBackgroundTask(this);

        backgroundTask.execute(method, basicFunctions.getUser_id(), mp_userid);

    }


    private void login() {

        Intent intent = new Intent(Profile.this, MessengerItem.class);

        intent.putExtra("chat_room_id", chat_room_id);
        intent.putExtra("userid", mp_userid);
        intent.putExtra("username", mp_username);

        startActivity(intent);

    }


    private class ListViewAdapter extends BaseAdapter {

        Context mContext;
        LayoutInflater inflater;

        private ListViewAdapter(Context context) {

            mContext = context;
            inflater = LayoutInflater.from(mContext);

        }

        private class ViewHolder {

            TextView TV_USERNAME;
            TextView TV_USERTYPE;
            CircularImageView IV_USERIMAGE;
            Button B_SHOW_MORE;

        }

        @Override
        public int getCount() {
            return searcharraylist.size();
        }

        @Override
        public ItemList getItem(int position) {
            return searcharraylist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public View getView(final int position, View view, ViewGroup parent) {

            final ViewHolder holder;

            if (view == null) {

                holder = new ViewHolder();
                view = inflater.inflate(R.layout.activity_homepage_search_list_item, parent, false);

                holder.TV_USERNAME = (TextView) view.findViewById(R.id.hp_search_list_title);
                holder.TV_USERTYPE = (TextView) view.findViewById(R.id.hp_search_list_type);
                holder.IV_USERIMAGE = (CircularImageView) view.findViewById(R.id.hp_search_list_image);
                holder.B_SHOW_MORE = (Button) view.findViewById(R.id.hp_search_list_more);

                view.setTag(holder);

            }

            else holder = (ViewHolder) view.getTag();

            holder.TV_USERNAME.setText(searcharraylist.get(position).getUsername());

            holder.TV_USERTYPE.setText(searcharraylist.get(position).getUsertype());

            if(!searcharraylist.get(position).getUserimage().equals("")) {

                holder.IV_USERIMAGE.setBackground(null);
                byte[] decodedString = Base64.decode(searcharraylist.get(position).getUserimage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                holder.IV_USERIMAGE.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 50, 50, false));

            }

            else {

                holder.IV_USERIMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_userimage, null));
                holder.IV_USERIMAGE.setImageResource(R.drawable.app_userbackground);

            }


            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Intent intent = new Intent(Profile.this, Profile.class);

                    intent.putExtra("type", searcharraylist.get(position).getUsername());

                    startActivity(intent);

                }
            });

            if(position == (searcharraylist.size() - 1))
                holder.B_SHOW_MORE.setVisibility(View.VISIBLE);

            holder.B_SHOW_MORE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    searchData(searchText, searcharraylist.size());
                }
            });

            return view;
        }

    }


    private class ItemList {

        private String username;
        private String usertype;
        private String userimage;

        private ItemList(String username, String userimage, String usertype) {

            this.username = username;
            this.usertype = usertype;
            this.userimage = userimage;

        }

        private String getUsertype() {
            return this.usertype;
        }

        private String getUsername() {
            return this.username;
        }

        private String getUserimage() {
            return this.userimage;
        }

    }

}