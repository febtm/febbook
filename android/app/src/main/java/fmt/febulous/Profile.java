package fmt.febulous;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
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
import java.util.List;
import java.util.Locale;

import fmt.febulous.helper.BasicFunctions;
import fmt.febulous.helper.Menu;
import fmt.febulous.helper.ViewImage;
import fmt.febulous.model.Post;

import static android.view.View.GONE;


public class Profile extends AppCompatActivity {


    JSONArray SearchProfileArray = null;

    private TextView P_USERNAME, P_ABOUT, P_NAME, P_DATE, P_EMAIL, P_GENDER,
            P_COURSE, P_DEPARTMENT, P_COLLEGE;

    private ImageView P_COVER;

    Button P_CONTACT, P_REPORT;

    private CircularImageView P_USERIMAGE;

    private String p_userid = "", p_username = "", p_userimage = "", p_usercover = "";

    EditText ET_SEARCH;

    private String chat_room_id;

    String searchText;
    ListView search_list;
    ListViewAdapter SearchListAdapter;
    ArrayList<ItemList> searcharraylist = new ArrayList<>();

    String TYPE;

    public static String PROFILE_USERNAME;

    private Menu menu;

    ImageButton MENU_BUTTON, SEARCH_CANCEL;

    JSONArray AllProfilePostsArray = null;

    public List<Post> mProfilePosts = new ArrayList<>();

    private MyProfilePostsAdapter myProfilePostsAdapter;

    FloatingActionButton P_FA_EDIT_PROFILE, P_FA_VIEW_PROFILE, P_FA_VIEW_POSTS;

    private ScrollView MP_SCROLL;

    private BasicFunctions basicFunctions;

    private Post post;

    private RecyclerView recyclerView;

    private ProgressDialog pDialog;

    private int load_over = 0, show = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AdView mAdView = findViewById(R.id.p_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        menu = new Menu(Profile.this);

        MENU_BUTTON = findViewById(R.id.p_menu);

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.LeftDrawer.toggleLeftDrawer();

            }
        });


        P_FA_EDIT_PROFILE = findViewById(R.id.p_fa_edit_profile);
        P_FA_VIEW_PROFILE = findViewById(R.id.p_fa_view_profile);
        P_FA_VIEW_POSTS = findViewById(R.id.p_fa_view_posts);

        P_COVER = findViewById(R.id.p_user_cover);
        P_USERNAME = findViewById(R.id.p_username_text);
        P_ABOUT = findViewById(R.id.p_user_about_text);
        P_NAME = findViewById(R.id.p_name_text);
        P_DATE = findViewById(R.id.p_dob_text);
        P_GENDER = findViewById(R.id.p_gender_text);
        P_EMAIL = findViewById(R.id.p_email_text);
        P_COURSE = findViewById(R.id.p_course_text);
        P_DEPARTMENT = findViewById(R.id.p_department_text);
        P_COLLEGE = findViewById(R.id.p_college_name_text);
        P_USERIMAGE = findViewById(R.id.p_user_image);

        P_CONTACT = findViewById(R.id.p_contact_user);
        P_REPORT = findViewById(R.id.p_report_user);

        search_list = findViewById(R.id.p_search_list);
        search_list.setVisibility(GONE);

        ET_SEARCH = findViewById(R.id.p_search_bar);

        SEARCH_CANCEL = findViewById(R.id.p_search_cancel);

        SEARCH_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_SEARCH.setText("");

            }
        });

        basicFunctions = new BasicFunctions(this);

        Intent intent = getIntent();
        TYPE = intent.getStringExtra("type");

        if(TYPE.equals("SELF")) {
            PROFILE_USERNAME = basicFunctions.getUser_name();
            P_CONTACT.setVisibility(GONE);
            P_REPORT.setVisibility(GONE);
        }

        else if(basicFunctions.getUser_name().equals(TYPE)){
            PROFILE_USERNAME = TYPE;
            P_CONTACT.setVisibility(GONE);
            P_REPORT.setVisibility(GONE);
        }

        else {
            PROFILE_USERNAME = TYPE;
            P_CONTACT.setVisibility(View.VISIBLE);
            P_REPORT.setVisibility(View.VISIBLE);
        }

        P_CONTACT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbchat();

            }
        });

        P_REPORT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                report();
            }
        });

        MP_SCROLL = findViewById(R.id.p_user_info_scrollview);

        MP_SCROLL.setVisibility(View.VISIBLE);

        post = new Post();

        recyclerView = findViewById(R.id.p_recycler_view);

        recyclerView.setVisibility(GONE);

        P_USERIMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Profile.this, ViewImage.class);

                if(!p_userimage.equals("")) {

                    byte[] decodedString = Base64.decode(p_userimage, Base64.DEFAULT);
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


        P_COVER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Profile.this, ViewImage.class);

                if(!p_usercover.equals("")) {

                    byte[] decodedString = Base64.decode(p_usercover, Base64.DEFAULT);
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


        if(basicFunctions.isConnectingToInternet()) {

            fetchProfile();

        }
        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){

                        case DialogInterface.BUTTON_POSITIVE:

                            if(basicFunctions.isConnectingToInternet()) {

                                fetchProfile();
                                getInitialProfilePostsData();

                            }

                            else {

                                Toast.makeText(Profile.this,
                                        "No Internet Connection. Try again later !",
                                        Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            Toast.makeText(Profile.this,
                                    "No Internet Connection. Try again later !",
                                    Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
            builder.setMessage("No Internet Connection. Try again ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }


        ET_SEARCH.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                searchText = ET_SEARCH.getText().toString().toLowerCase(Locale.getDefault());

                if (TextUtils.isEmpty(searchText)) {

                    search_list.setVisibility(GONE);
                    recyclerView.setVisibility(GONE);
                    MP_SCROLL.setVisibility(View.VISIBLE);

                }

                else {

                    searcharraylist.clear();
                    searchProfiles(searchText, 0);

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

        P_FA_EDIT_PROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Profile.this, EditProfile.class);
                startActivity(intent);

            }
        });

        P_FA_VIEW_POSTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MP_SCROLL.setVisibility(GONE);
                search_list.setVisibility(GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if(show == 0) {

                    getInitialProfilePostsData();
                    show = 1;

                }

            }
        });

        P_FA_VIEW_PROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MP_SCROLL.setVisibility(View.VISIBLE);
                search_list.setVisibility(GONE);
                recyclerView.setVisibility(View.GONE);

            }
        });


    }


    private void fetchProfile() {

        pDialog = ProgressDialog.show(this, "", "Fetching Profile ... ", false, false);

        StringRequest stringRequest = new StringRequest(BasicFunctions.MY_PROFILE+PROFILE_USERNAME, new Response.Listener<String>() {
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

        String p_userabout = "";
        String p_email = "";
        String p_name = "";
        String p_gender = "";
        String p_date = "";
        String p_college = "";
        String p_department = "";
        String p_course = "";


        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(basicFunctions.JSON_ARRAY);
            JSONObject profileData = result.getJSONObject(0);

            p_userid = profileData.getString(basicFunctions.KEY_USER_ID);
            p_username = profileData.getString(basicFunctions.KEY_USER_USERNAME);
            p_userimage = profileData.getString(basicFunctions.KEY_USER_IMAGE);
            p_userabout = profileData.getString(basicFunctions.KEY_USER_ABOUT);
            p_email = profileData.getString(basicFunctions.KEY_USER_EMAIL);
            p_name = profileData.getString(basicFunctions.KEY_USER_NAME);
            p_date = profileData.getString(basicFunctions.KEY_USER_DOB);
            p_usercover = profileData.getString(basicFunctions.KEY_USER_COVERIMAGE);
            p_gender = profileData.getString(basicFunctions.KEY_USER_GENDER);
            p_college = profileData.getString(basicFunctions.KEY_USER_COLLEGE);
            p_department = profileData.getString(basicFunctions.KEY_USER_DEPARTMENT);
            p_course = profileData.getString(basicFunctions.KEY_USER_COURSE);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        P_USERNAME.setText(p_username);
        P_EMAIL.setText(p_email);
        P_COURSE.setText(p_course);

        if(!p_userabout.equals("")) P_ABOUT.setText(p_userabout);

        else P_ABOUT.setText("-");

        if(!p_name.equals("")) P_NAME.setText(p_name);

        else P_NAME.setText("-");

        if(!p_gender.equals("")) {

            if (p_gender.equals("M"))
                P_GENDER.setText(getResources().getString(R.string.male));

            else if (p_gender.equals("F"))
                P_GENDER.setText(getResources().getString(R.string.female));

        }

        else P_GENDER.setText("-");


        if(!p_date.equals("")) P_DATE.setText(p_date);

        else P_DATE.setText("-");


        if(!p_department.equals("")) P_DEPARTMENT.setText(p_department);

        else P_DEPARTMENT.setText("-");


        if(!p_college.equals("")) P_COLLEGE.setText(p_college);

        else P_COLLEGE.setText("-");


        if(!p_userimage.equals("")) {

            P_USERIMAGE.setBackground(null);
            byte[] decodedString_1 = Base64.decode(p_userimage, Base64.DEFAULT);
            Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

            P_USERIMAGE.setImageBitmap(decodedByte_1);

        }

        else {

            P_USERIMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_userimage, null));
            P_USERIMAGE.setImageResource(R.drawable.app_userbackground);

        }


        if(!p_usercover.equals("")) {

            P_COVER.setBackground(null);
            byte[] decodedString_2 = Base64.decode(p_usercover, Base64.DEFAULT);
            Bitmap decodedByte_2 = BitmapFactory.decodeByteArray(decodedString_2, 0, decodedString_2.length);

            P_COVER.setImageBitmap(decodedByte_2);

        }

        else {

            P_COVER.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
            P_COVER.setImageResource(R.drawable.app_userbackground);

        }

        pDialog.dismiss();

    }


    private void searchProfiles(String searchText, final int index) {

        pDialog = ProgressDialog.show(this, "", "Fetching Profiles ... ", false, false);

        String url = BasicFunctions.SEARCH_FOR_PROFILES + index + "&searchText=" + searchText;

        url = url.replaceAll(" ", "%20");

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObj = new JSONObject(response);
                    SearchProfileArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

                    if (index == 0 && SearchProfileArray.length() == 0) {

                        search_list.setVisibility(GONE);

                        if(show == 0) {

                            recyclerView.setVisibility(GONE);
                            MP_SCROLL.setVisibility(View.VISIBLE);

                        }

                        else {

                            recyclerView.setVisibility(View.VISIBLE);
                            MP_SCROLL.setVisibility(View.GONE);

                        }


                        Toast.makeText(Profile.this, "No Profiles Found !", Toast.LENGTH_LONG).show();

                    }

                    else if(SearchProfileArray.length() == 0)
                        Toast.makeText(Profile.this, "No More Profiles Found !", Toast.LENGTH_LONG).show();

                    else {

                        search_list.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(GONE);
                        MP_SCROLL.setVisibility(View.GONE);

                    }

                    for (int i = 0; i < SearchProfileArray.length(); i++) {

                        JSONObject jsonObject = SearchProfileArray.getJSONObject(i);

                        ItemList itemList = new ItemList(jsonObject.getString(basicFunctions.KEY_USER_USERNAME),
                                jsonObject.getString(basicFunctions.KEY_USER_IMAGE) );

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



    @SuppressLint("StaticFieldLeak")
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

                    URL url = new URL(BasicFunctions.ADD_CHAT);
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

        backgroundTask.execute(method, basicFunctions.getUser_id(), p_userid);

    }


    private void login() {

        Intent intent = new Intent(Profile.this, MessengerItem.class);

        intent.putExtra("chat_room_id", chat_room_id);
        intent.putExtra("userid", p_userid);
        intent.putExtra("username", p_username);
        intent.putExtra("userimage", p_userimage);

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
                view = inflater.inflate(R.layout.activity_hp_search_item, parent, false);

                holder.TV_USERNAME = view.findViewById(R.id.hp_search_title);
                holder.IV_USERIMAGE = view.findViewById(R.id.hp_search_image);
                holder.B_SHOW_MORE = view.findViewById(R.id.hp_search_more);

                view.setTag(holder);

            }

            else holder = (ViewHolder) view.getTag();

            holder.TV_USERNAME.setText(searcharraylist.get(position).getUsername());

            if(!searcharraylist.get(position).getUserimage().equals("")) {

                holder.IV_USERIMAGE.setBackground(null);
                byte[] decodedString = Base64.decode(searcharraylist.get(position).getUserimage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                holder.IV_USERIMAGE.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 50, 50, false));

            }

            else {

                holder.IV_USERIMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.me_profile, null));
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

                    searchProfiles(searchText, searcharraylist.size());
                }
            });

            return view;
        }

    }


    private class ItemList {

        private String username;
        private String userimage;

        private ItemList(String username, String userimage) {

            this.username = username;
            this.userimage = userimage;

        }

        private String getUsername() {
            return this.username;
        }

        private String getUserimage() {
            return this.userimage;
        }

    }


    private void getInitialProfilePostsData() {

        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_POST_PROFILE + Profile.PROFILE_USERNAME + "&index=" + 0, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_InitialPosts(response);

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



    protected void showList_InitialPosts(String response) {

        String hp_id;
        String hp_title;
        String hp_desc;
        String hp_picture_1;
        String hp_type;
        String hp_timestamp;
        String hp_username;
        String hp_userimage;


        try {
            JSONObject jsonObj = new JSONObject(response);
            AllProfilePostsArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            post.setTotal(0);

            for (int i = 0; i < AllProfilePostsArray.length(); i++) {
                JSONObject jsonObject = AllProfilePostsArray.getJSONObject(i);

                hp_id = jsonObject.getString(basicFunctions.KEY_POST_ID);
                hp_type = jsonObject.getString(basicFunctions.KEY_POST_TYPE);
                hp_title = jsonObject.getString(basicFunctions.KEY_POST_TITLE);
                hp_desc = jsonObject.getString(basicFunctions.KEY_POST_DESC);
                hp_picture_1 = jsonObject.getString(basicFunctions.KEY_POST_IMAGE);
                hp_timestamp = jsonObject.getString(basicFunctions.KEY_POST_TIMESTAMP);
                hp_username = jsonObject.getString(basicFunctions.KEY_USER_USERNAME);
                hp_userimage = jsonObject.getString(basicFunctions.KEY_USER_IMAGE);

                Post data = new Post();

                data.setDetails(hp_id, hp_picture_1, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username, hp_userimage);

                mProfilePosts.add(data);

                data.setTotal(mProfilePosts.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Profile.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        myProfilePostsAdapter = new MyProfilePostsAdapter(Profile.this);
        recyclerView.setAdapter(myProfilePostsAdapter);

        if (post.getTotal() == 0)
            Toast.makeText(Profile.this, "No Posts to display !", Toast.LENGTH_LONG).show();

        myProfilePostsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                mProfilePosts.add(null);
                myProfilePostsAdapter.notifyItemInserted(mProfilePosts.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pDialog = ProgressDialog.show(Profile.this, "", "Fetching Posts ... ", false, false);

                        mProfilePosts.remove(mProfilePosts.size() - 1);
                        myProfilePostsAdapter.notifyItemRemoved(mProfilePosts.size());

                        int index = mProfilePosts.size();

                        getMoreProfilePostsData(index);


                    }
                }, 1000);
            }
        });

    }


    private void getMoreProfilePostsData(int index) {

        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_POST_PROFILE + Profile.PROFILE_USERNAME + "&index=" + index, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_MorePosts(response);

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


    protected void showList_MorePosts(String response) {

        String hp_id;
        String hp_title;
        String hp_desc;
        String hp_picture_1;
        String hp_type;
        String hp_timestamp;
        String hp_username;
        String hp_userimage;


        try {
            JSONObject jsonObj = new JSONObject(response);
            AllProfilePostsArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            if(AllProfilePostsArray.length() == 0) {

                Toast.makeText(Profile.this, "No more Posts to display !", Toast.LENGTH_LONG).show();
                load_over = 1;

            }

            for (int i = 0; i < AllProfilePostsArray.length(); i++) {
                JSONObject jsonObject = AllProfilePostsArray.getJSONObject(i);

                hp_id = jsonObject.getString(basicFunctions.KEY_POST_ID);
                hp_type = jsonObject.getString(basicFunctions.KEY_POST_TYPE);
                hp_title = jsonObject.getString(basicFunctions.KEY_POST_TITLE);
                hp_desc = jsonObject.getString(basicFunctions.KEY_POST_DESC);
                hp_picture_1 = jsonObject.getString(basicFunctions.KEY_POST_IMAGE);
                hp_timestamp = jsonObject.getString(basicFunctions.KEY_POST_TIMESTAMP);
                hp_username = jsonObject.getString(basicFunctions.KEY_USER_USERNAME);
                hp_userimage = jsonObject.getString(basicFunctions.KEY_USER_IMAGE);

                Post data = new Post();

                data.setDetails(hp_id, hp_picture_1, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username, hp_userimage);

                mProfilePosts.add(data);

                data.setTotal(mProfilePosts.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        myProfilePostsAdapter.notifyDataSetChanged();
        myProfilePostsAdapter.setLoaded();

        pDialog.dismiss();

    }


    private interface OnLoadMoreListener {
        void onLoadMore();
    }


    private class MyProfilePostsAdapter extends RecyclerView.Adapter<ViewHolder> {

        private LayoutInflater mLayoutInflater;
        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;

        private MyProfilePostsAdapter(Context context) {

            mLayoutInflater = LayoutInflater.from(context);

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && load_over == 0) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });

        }

        private void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

            View view = mLayoutInflater.inflate(R.layout.activity_hp_item, viewGroup, false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

            final Post data = mProfilePosts.get(position);

            viewHolder.setData(data.getId(), data.getImage(),
                    data.getTitle(), data.getDescription(), data.getType(),
                    data.getTimestamp(), data.getUsername(), data.getUserimage());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Profile.this, PostView.class);
                    intent.putExtra("id", data.getId());
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return post.getTotal();
        }

        private void setLoaded() {
            isLoading = false;
        }

    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private ImageView mUserImageView;
        private LinearLayout mLinearLayout;
        private TextView mTitleTextView, mPostDateTextView,mDescriptionTextView, mUserNameTextView, mReportTextView;
        private LinearLayout mUserProfile;

        private ImageButton mDeleteButton;

        private ViewHolder(View itemView) {
            super(itemView);

            mLinearLayout = itemView.findViewById(R.id.hp_item_layout);
            mImageView = itemView.findViewById(R.id.hp_item_image);
            mTitleTextView = itemView.findViewById(R.id.hp_item_title);
            mPostDateTextView = itemView.findViewById(R.id.hp_item_date);
            mDescriptionTextView = itemView.findViewById(R.id.hp_item_description);
            mUserNameTextView = itemView.findViewById(R.id.hp_user_name);
            mUserImageView = itemView.findViewById(R.id.hp_user_image);
            mUserProfile = itemView.findViewById(R.id.hp_user);
            mDeleteButton = itemView.findViewById(R.id.hp_delete);
            mReportTextView = itemView.findViewById(R.id.hp_report);

        }

        private void setData(final String id,String image, final String title,String description, final String type,
                             String timestamp, final String username, String userimage) {

            if(username.equals(basicFunctions.getUser_name()))
                mDeleteButton.setVisibility(View.VISIBLE);

            else mDeleteButton.setVisibility(GONE);

            if(username.equals(basicFunctions.getUser_name()))
                mReportTextView.setVisibility(GONE);

            else mReportTextView.setVisibility(View.VISIBLE);


            switch(type){

                case "GK":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(Profile.this, R.color.colorGK));
                    break;

                case "QNA":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(Profile.this, R.color.colorQnA));
                    break;

                case "Materials":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(Profile.this, R.color.colorStudyMaterials));
                    break;

                case "Teach":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(Profile.this, R.color.colorTeach));
                    break;

                case "Ideas":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(Profile.this, R.color.colorIdeas));
                    break;

                case "Events":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(Profile.this, R.color.colorEvents));
                    break;

                default:

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(Profile.this, R.color.colorGK));
                    break;

            }

            mTitleTextView.setText(title);
            mDescriptionTextView.setText(description);
            mPostDateTextView.setText(BasicFunctions.getTimeStamp(timestamp));
            mUserNameTextView.setText(username);

            if(!image.equals("")) {

                mImageView.setVisibility(View.VISIBLE);

                mImageView.setBackground(null);
                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                mImageView.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 250, 250, false));

            }

            else {

                mImageView.setVisibility(GONE);
                mImageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
                mImageView.setImageResource(R.drawable.app_userbackground);

            }

            if(!userimage.equals("")) {

                mUserImageView.setBackground(null);
                byte[] decodedString_1 = Base64.decode(userimage, Base64.DEFAULT);
                Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

                mUserImageView.setImageBitmap(Bitmap.createScaledBitmap(decodedByte_1, 60, 60, false));


            }

            else {

                mUserImageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_userimage, null));
                mUserImageView.setImageResource(R.drawable.app_userbackground);

            }

            mUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Profile.this, Profile.class);
                    intent.putExtra("type", username);
                    startActivity(intent);

                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    delete_post(id);

                }
            });

            mReportTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    report(id, title);

                }
            });
        }
    }


    private void delete_post(final String item_id){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "deletepost";

                        basicFunctions.performTask(method, item_id, "");

                        Intent intent = new Intent(Profile.this, Profile.class);
                        startActivity(intent);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(Profile.this,"Deletion Cancelled !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setMessage("Are you sure you want to delete this Post ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }



    private void report(final String item_id, final String item_title){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "reportpost";

                        basicFunctions.performTask(method, item_id, "P : " + item_title);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(Profile.this,"Reporting Cancelled !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setMessage("Are you sure you want to report this Post ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

}