package fmt.febulous;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pkmmte.view.CircularImageView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import fmt.febulous.helper.BasicFunctions;
import fmt.febulous.helper.CommentsListView;
import fmt.febulous.helper.EndPoints;
import fmt.febulous.helper.ViewImage;
import io.fabric.sdk.android.Fabric;


public class PostView extends AppCompatActivity {

  JSONArray PostViewArray = null;

  private TextView PV_TITLE, PV_DESC, PV_USERNAME, PV_DATE, PV_REPORT;

  private LinearLayout PV_USERPROFILE, PV_LLDELETE;

  private ImageButton PV_DELETE;

  private CallbackManager fb_callbackManager;

  private EditText PV_COMMENT;

  private ImageView PV_IMAGE;

  private CircularImageView PV_USERIMAGE;

  String comment, item_title, item_id, item_type, item_description, item_timestamp,
          item_username, item_userimage, item_image;

  int like_count, dislike_count, like = 0, dislike = 0;

  private LinearLayout PV_LL;

  private BasicFunctions basicFunctions;

  private Button post_comment, DownloadFile_button;

  private ImageButton PV_LIKE, PV_DISLIKE, PV_FACEBOOK, PV_TWITTER;

  private TextView PV_LIKECOUNT;

  String actionbar_title;

  List<PostView.Comments> commentsList;
  private CommentsListView Comment_List;
  CommentsListAdapter Comment_List_adapter;

  private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post_view);

    actionbar_title="\tVIEW A POST";
    setTitle(actionbar_title);

    basicFunctions = new BasicFunctions(this);

    AdView mAdView = (AdView) findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);

    Comment_List = (CommentsListView) findViewById(R.id.pv_frag_recycler_view);

    PV_LL = (LinearLayout) findViewById(R.id.pv_item_LL);
    PV_IMAGE = (ImageView) findViewById(R.id.pv_item_image);
    PV_TITLE = (TextView) findViewById(R.id.pv_item_title);
    PV_DESC = (TextView) findViewById(R.id.pv_item_description);
    PV_USERNAME = (TextView) findViewById(R.id.pv_item_username);
    PV_DATE = (TextView) findViewById(R.id.pv_item_date);
    PV_USERPROFILE = (LinearLayout) findViewById(R.id.pv_userprofile);
    PV_REPORT = (TextView) findViewById(R.id.pv_report);

    PV_FACEBOOK = (ImageButton) findViewById(R.id.pv_item_facebook_share);
    PV_TWITTER = (ImageButton) findViewById(R.id.pv_item_twitter_share);

    PV_COMMENT = (EditText) findViewById(R.id.pv_user_comment);

    PV_USERIMAGE = (CircularImageView) findViewById(R.id.pv_item_userimage);

    PV_DELETE = (ImageButton) findViewById(R.id.pv_delete);

    PV_LLDELETE = (LinearLayout) findViewById(R.id.pv_LL_delete);

    post_comment = (Button) findViewById(R.id.pv_post_comment);
    DownloadFile_button = (Button) findViewById(R.id.pv_download_file);

    PV_LIKE = (ImageButton) findViewById(R.id.pv_item_like);
    PV_DISLIKE = (ImageButton) findViewById(R.id.pv_item_dislike);
    PV_LIKECOUNT = (TextView) findViewById(R.id.pv_item_like_count);


    FacebookSdk.sdkInitialize(getApplicationContext());
    fb_callbackManager = CallbackManager.Factory.create();


    Intent intent = getIntent();

    item_id = intent.getStringExtra("id");


    TwitterAuthConfig authConfig =  new TwitterAuthConfig(basicFunctions.TWITTER_KEY, basicFunctions.TWITTER_SECRET);
    Fabric.with(this, new Twitter(authConfig));

        if (basicFunctions.isConnectingToInternet())
            getSpecificPostData();

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(PostView.this, HomePage.class);
                            startActivity(intent);

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Network Failure : Please check your Internet Connection !")
                    .setPositiveButton("Try Again ... ", dialogClickListener).show();


        }

    }


    private void getSpecificPostData(){

        pDialog = ProgressDialog.show(this, "", "Fetching Post ... ", false, false);

        StringRequest stringRequest = new StringRequest(EndPoints.GET_POST_ID + item_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_posts(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostView.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(PostView.this);
        requestQueue.add(stringRequest);
    }




    protected void showList_posts(String response) {

        try {
            JSONObject jsonObj = new JSONObject(response);
            PostViewArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);
            JSONObject jsonObject = PostViewArray.getJSONObject(0);

            item_type = jsonObject.getString(basicFunctions.KEY_POST_TYPE);
            item_title = jsonObject.getString(basicFunctions.KEY_POST_TITLE);
            item_description = jsonObject.getString(basicFunctions.KEY_POST_DESC);
            item_image = jsonObject.getString(basicFunctions.KEY_POST_IMAGE);
            item_timestamp = jsonObject.getString(basicFunctions.KEY_POST_TIMESTAMP);
            item_username = jsonObject.getString(basicFunctions.KEY_USER_USERNAME);
            item_userimage = jsonObject.getString(basicFunctions.KEY_USER_IMAGE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        get_comments();


    }

    private class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PostView.this);
            pDialog.setMessage("Downloading the File ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                @SuppressLint("SdCardPath")
                OutputStream output = new FileOutputStream("/sdcard/Download/"+item_description);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"Error : "+ e.getMessage(),Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {

            Toast.makeText(PostView.this, "The File has been downloaded to /sdcard/Download/ !",Toast.LENGTH_LONG).show();
            pDialog.dismiss();
        }

    }


    private void get_likes() {

        StringRequest stringRequest = new StringRequest(EndPoints.GET_LIKES + item_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                likes_showJSON(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostView.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void likes_showJSON(String response) {

        String hp_user_id;

        like_count = 0;

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(basicFunctions.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {

                JSONObject commentData = result.getJSONObject(i);

                hp_user_id = commentData.getString(basicFunctions.KEY_USER_ID);

                    like_count++;

                    if (hp_user_id.equals(basicFunctions.getUser_id())) {
                        PV_LIKE.setBackgroundResource(R.drawable.pv_liked);
                        like = 1;
                    }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        get_dislikes();

    }


    private void get_dislikes() {


        StringRequest stringRequest = new StringRequest(EndPoints.GET_DISLIKES + item_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dislikes_showJSON(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostView.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void dislikes_showJSON(String response) {

        String hp_user_id;

        dislike_count = 0;

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(basicFunctions.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {

                JSONObject commentData = result.getJSONObject(i);

                hp_user_id = commentData.getString(basicFunctions.KEY_USER_ID);

                    dislike_count++;

                    if (hp_user_id.equals(basicFunctions.getUser_id())) {
                        PV_DISLIKE.setBackgroundResource(R.drawable.pv_disliked);
                        dislike = 1;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        PV_LIKECOUNT.setText(String.valueOf(like_count - dislike_count));

        pDialog.dismiss();

    }


    private void get_comments() {


        StringRequest stringRequest = new StringRequest(EndPoints.GET_COMMENTS + item_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                comments_showJSON(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostView.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void comments_showJSON(String response) {

        commentsList = new ArrayList<>();

        Comment_List_adapter = new CommentsListAdapter(this, commentsList);

        String hp_username;
        String hp_userimage;
        String hp_comment;
        String hp_timestamp;

        int j = 0;

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(basicFunctions.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {

                JSONObject commentData = result.getJSONObject(i);

                hp_username = commentData.getString(basicFunctions.KEY_USER_USERNAME);
                hp_userimage = commentData.getString(basicFunctions.KEY_USER_IMAGE);
                hp_comment = commentData.getString(basicFunctions.KEY_POST_COMMENT);
                hp_timestamp = commentData.getString(basicFunctions.KEY_POST_TIMESTAMP);

                j++;

                Comments comments = new Comments();

                comments.setUsername(hp_username);
                comments.setUserimage(hp_userimage);
                comments.setComment(hp_comment);
                comments.setDate(hp_timestamp);

                commentsList.add(comments);

                Comment_List_adapter.notifyDataSetChanged();


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        switch(item_type){

            case "GK":

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGK));
                break;

            case "QNA":

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorQnA));
                break;

            case "Materials":

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorStudyMaterials));
                DownloadFile_button.setVisibility(View.VISIBLE);

                break;

            case "Teach":

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTeach));
                break;

            case "Ideas":

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorIdeas));
                break;

            case "Events":

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorEvents));
                break;

            default:

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGK));
                break;

        }


        PV_TITLE.setText(item_title);

        PV_DESC.setText(item_description);

        DownloadFile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int MyVersion = Build.VERSION.SDK_INT;

                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (basicFunctions.HasStoragePermission()) {

                        new DownloadFileFromURL().execute(EndPoints.FILE_URL + item_description);

                    }

                    else {

                        requestStoragePermission();

                    }
                }

                else
                    new DownloadFileFromURL().execute(EndPoints.FILE_URL + item_description);

            }
        });


        if(!item_image.equals(""))
        {
            PV_IMAGE.setVisibility(View.VISIBLE);

            PV_IMAGE.setBackground(null);
            byte[] decodedString_2 = Base64.decode(item_image, Base64.DEFAULT);
            Bitmap decodedByte_2 = BitmapFactory.decodeByteArray(decodedString_2, 0, decodedString_2.length);

            PV_IMAGE.setImageBitmap(decodedByte_2);

        }

        else {

            PV_IMAGE.setVisibility(View.GONE);
            PV_IMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
            PV_IMAGE.setImageResource(R.drawable.app_userbackground);

        }

        PV_USERNAME.setText(item_username);

        PV_DATE.setText(basicFunctions.getTimeStamp(item_timestamp));


        if(!item_userimage.equals("")) {

            PV_USERIMAGE.setBackground(null);
            byte[] decodedString_1 = Base64.decode(item_userimage, Base64.DEFAULT);
            Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

            PV_USERIMAGE.setImageBitmap(decodedByte_1);

        }

        else {

            PV_USERIMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_userimage, null));
            PV_USERIMAGE.setImageResource(R.drawable.app_userbackground);

        }

        post_comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                post_comment();
            }
        });

        PV_IMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PostView.this, ViewImage.class);

                if(!item_image.equals("")) {

                    byte[] decodedString = Base64.decode(item_image, Base64.DEFAULT);
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


        if(item_username.equals(basicFunctions.getUser_name()))
            PV_LLDELETE.setVisibility(View.VISIBLE);

        else PV_LLDELETE.setVisibility(View.GONE);

        PV_DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                delete_post();
            }
        });

        PV_LIKE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(like!=1) {
                    like();
                    PV_LIKE.setBackgroundResource(R.drawable.pv_liked);
                    like = 1;
                }

                else
                    Toast.makeText(PostView.this,"You have already liked this post !",Toast.LENGTH_SHORT).show();
            }
        });

        PV_DISLIKE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (dislike != 1) {
                    dislike();
                    PV_DISLIKE.setBackgroundResource(R.drawable.pv_disliked);
                    dislike = 1;
                } else
                    Toast.makeText(PostView.this, "You have already disliked this post !", Toast.LENGTH_SHORT).show();
            }
        });

        PV_USERPROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostView.this, Profile.class);
                intent.putExtra("type", item_username);
                startActivity(intent);

            }
        });

        if(item_username.equals(basicFunctions.getUser_name()))
            PV_REPORT.setVisibility(View.GONE);

        PV_REPORT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                report_post(item_id, item_title);
            }
        });

        PV_FACEBOOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareonFacebook();

            }
        });

        PV_TWITTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareOnTwitter();

            }
        });

        if(j != 0) {
            Comment_List.setVisibility(View.VISIBLE);
            Comment_List.setAdapter(Comment_List_adapter);
        }

        else Comment_List.setVisibility(View.GONE);

        get_likes();

    }


    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case 101:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new DownloadFileFromURL().execute(EndPoints.FILE_URL + item_description);

                } else {

                    Toast.makeText(PostView.this, "Kindly grant Febulous the permission to use the External Storage !", Toast.LENGTH_LONG).show();

                }

                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void shareonFacebook(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        ShareDialog shareDialog = new ShareDialog(PostView.this);

                        if (ShareDialog.canShow(ShareLinkContent.class)) {

                            shareDialog.registerCallback(fb_callbackManager, new FacebookCallback<Sharer.Result>() {
                                @Override
                                public void onSuccess(Sharer.Result result) {
                                    Toast.makeText(PostView.this, "Facebook Share Successful !", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancel() {
                                    Toast.makeText(PostView.this, "Facebook Share Cancelled !", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    Toast.makeText(PostView.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                                    exception.printStackTrace();
                                }
                            });

                            ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                                    .putString("og:type", "news.article")
                                    .putString("og:title", item_title)
                                    .putString("og:description", item_description).build();

                            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                                    .setActionType("news.reads").putObject("article", object)
                                    .build();

                            ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                                    .setPreviewPropertyName("article")
                                    .setAction(action)
                                    .build();

                            shareDialog.show(content);

                        }


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(),"Kindly install the Facebook App to enable Facebook Post Sharing !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Is the Facebook App installed on your device ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fb_callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void shareOnTwitter(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        TweetComposer.Builder builder = new TweetComposer.Builder(PostView.this)
                                .text(basicFunctions.FOUND_TW + " - " + item_title + " : " + item_description);
                        builder.show();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(),"Kindly install the Twitter App to enable Twitter Post Sharing !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Is the Twitter App installed on your phone ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }



    private class CommentsListAdapter extends BaseAdapter {

        private Activity activity;
        private LayoutInflater inflater;
        private List<Comments> commentItems;

        private CommentsListAdapter(Activity activity, List<Comments> commentItems) {
            this.activity = activity;
            this.commentItems = commentItems;
        }

        @Override
        public int getCount() {
            return commentItems.size();
        }

        @Override
        public Object getItem(int location) {
            return commentItems.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null)
                convertView = inflater.inflate(R.layout.activity_post_view_comment_item, parent, false);


            TextView mUsername = (TextView) convertView.findViewById(R.id.pv_list_item_username);
            TextView mComment = (TextView) convertView.findViewById(R.id.pv_list_item_comment);
            TextView mDate = (TextView) convertView.findViewById(R.id.pv_list_item_date);
            CircularImageView mUserimage = (CircularImageView) convertView.findViewById(R.id.pv_list_item_userimage);
            LinearLayout mUserprofile = (LinearLayout) convertView.findViewById(R.id.pv_userprofile);
            ImageButton DELETE = (ImageButton) convertView.findViewById(R.id.pv_delete);
            TextView mReport = (TextView) convertView.findViewById(R.id.pv_report);

            final Comments m = commentItems.get(position);

            if(m.getUsername().equals(basicFunctions.getUser_name()))
                DELETE.setVisibility(View.VISIBLE);

            else DELETE.setVisibility(View.GONE);

            mUsername.setText(m.getUsername());

            mComment.setText(m.getComment());

            mDate.setText(basicFunctions.getTimeStamp(m.getDate()));

            if(!m.getUserimage().equals("")) {

                mUserimage.setBackground(null);
                byte[] decodedString_1 = Base64.decode(m.getUserimage(), Base64.DEFAULT);
                Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

                mUserimage.setImageBitmap(Bitmap.createScaledBitmap(decodedByte_1, 60, 60, false));

            }

            else {

                mUserimage.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_userimage, null));
                mUserimage.setImageResource(R.drawable.app_userbackground);

            }

            mUserprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(PostView.this, Profile.class);
                    intent.putExtra("type", m.getUsername());
                    startActivity(intent);
                }
            });

            DELETE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    delete_comment(m.getComment());
                }
            });

            if(m.getUsername().equals(basicFunctions.getUser_name()))
                mReport.setVisibility(View.GONE);

            mReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    report_comment(item_id, m.getComment());
                }
            });

            return convertView;
        }

    }


    private void report_post(final String item_id, final String item_title){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "reportpost";

                        basicFunctions.performTask(method, item_id, "P : " + item_title);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(),"Reporting Cancelled !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to report this post ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    private void report_comment(final String item_id, final String item_title){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "reportcomment";

                        basicFunctions.performTask(method, item_id, "C : " + item_title);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(),"Reporting Cancelled !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to report this comment ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }


    private class Comments {

        private String username, userimage, comment, date;

        private Comments() {}

        private String getUsername() {
            return username;
        }

        private void setUsername(String username) {
            this.username = username;
        }

        private String getUserimage() {
            return userimage;
        }

        private void setUserimage(String userimage) {
            this.userimage = userimage;
        }

        private String getComment() {
            return comment;
        }

        private void setComment(String comment) {
            this.comment = comment;
        }

        private String getDate() {
            return date;
        }

        private void setDate(String date) {
            this.date = date;
        }

    }


    private void post_comment(){

        comment = PV_COMMENT.getText().toString();

        if (TextUtils.isEmpty(comment)) {
            PV_COMMENT.setError("Type in a Comment !");

        } else {

            String method = "comment";

            basicFunctions.performTask(method, item_id, comment);

            PV_COMMENT.setText("");
            get_comments();

        }

    }


    private void like(){

        String method = "like";

        basicFunctions.performTask(method, item_id, "");

        get_likes();

    }


    private void dislike(){

        String method = "dislike";

        basicFunctions.performTask(method, item_id, "");

        get_likes();

    }


    private void delete_comment(final String comment){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "deletecomment";

                        basicFunctions.performTask(method, item_id, comment);

                        get_comments();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(),"Deletion Cancelled !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this Comment ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    private void delete_post(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "deletepost";

                        basicFunctions.performTask(method, item_id, "");

                        Intent intent = new Intent(PostView.this, HomePage.class);
                        startActivity(intent);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(),"Deletion Cancelled !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this Post ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

}