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
import android.net.Uri;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
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
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pkmmte.view.CircularImageView;
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
import fmt.febulous.helper.PVCommentListView;
import fmt.febulous.helper.ViewImage;


public class PostView extends AppCompatActivity {


    JSONArray PostViewArray = null;

    private TextView PV_TITLE, PV_DESC, PV_USERNAME, PV_DATE, PV_REPORT;

    private LinearLayout PV_USER_PROFILE;

    private CallbackManager fb_callbackManager;

    private EditText ET_COMMENT;

    private ImageView PV_IMAGE;

    private CircularImageView PV_USERIMAGE;

    String comment, item_title, item_id, item_type, item_description, item_timestamp,
            item_username, item_user_image, item_image;

    int like_count, dislike_count, like = 0, dislike = 0;

    private LinearLayout PV_LL;

    private BasicFunctions basicFunctions;

    private Button B_DOWNLOAD_FILE;

    private ImageButton PV_COMMENT, PV_DELETE, PV_LIKE, PV_DISLIKE, PV_FACEBOOK, PV_TWITTER;

    private TextView PV_LIKE_COUNT;

    List<PostView.Comments> commentsList;
    private PVCommentListView COMMENT_LIST;
    CommentsListAdapter commentListAdapter;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        ImageButton BACK_BUTTON = findViewById(R.id.pv_back);

        BACK_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        basicFunctions = new BasicFunctions(this);

        AdView mAdView = findViewById(R.id.pv_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        COMMENT_LIST = findViewById(R.id.pv_recycler_view);

        PV_LL = findViewById(R.id.pv_item_LL);
        PV_IMAGE = findViewById(R.id.pv_item_image);
        PV_TITLE = findViewById(R.id.pv_item_title);
        PV_DESC = findViewById(R.id.pv_item_description);
        PV_USERNAME = findViewById(R.id.pv_item_username);
        PV_DATE = findViewById(R.id.pv_item_date);
        PV_USER_PROFILE = findViewById(R.id.pv_user_profile);
        PV_REPORT = findViewById(R.id.pv_report);

        PV_FACEBOOK = findViewById(R.id.pv_item_facebook);
        PV_TWITTER = findViewById(R.id.pv_item_twitter);

        ET_COMMENT = findViewById(R.id.pv_comment);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(ET_COMMENT.getWindowToken(), 0);

        ImageButton PV_COMMENT_DELETE = findViewById(R.id.pv_comment_cancel);

        PV_COMMENT_DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_COMMENT.setText("");

            }
        });

        PV_USERIMAGE = findViewById(R.id.pv_item_user_image);

        PV_DELETE = findViewById(R.id.pv_delete);

        PV_COMMENT = findViewById(R.id.pv_comment_send);

        B_DOWNLOAD_FILE = findViewById(R.id.pv_download_file);

        PV_LIKE = findViewById(R.id.pv_item_like);
        PV_DISLIKE = findViewById(R.id.pv_item_dislike);
        PV_LIKE_COUNT = findViewById(R.id.pv_item_like_count);

        fb_callbackManager = CallbackManager.Factory.create();

        Intent intent = getIntent();

        item_id = intent.getStringExtra("id");

        if(basicFunctions.isConnectingToInternet())
            getSpecificPostData();

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){

                        case DialogInterface.BUTTON_POSITIVE:

                            if(basicFunctions.isConnectingToInternet())
                                getSpecificPostData();

                            else {

                                Toast.makeText(PostView.this,
                                        "No Internet Connection. Try again later !",
                                        Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            Toast.makeText(PostView.this,
                                    "No Internet Connection. Try again later !",
                                    Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(PostView.this);
            builder.setMessage("No Internet Connection. Try again ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }

    }


    private void getSpecificPostData() {

        pDialog = ProgressDialog.show(this, "", "Fetching Post ... ", false, false);

        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_POST_ID + item_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showListPosts(response);

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


    protected void showListPosts(String response) {

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
            item_user_image = jsonObject.getString(basicFunctions.KEY_USER_IMAGE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        getComments();

    }


    private void getLikes() {

        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_LIKES + item_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                likesShowJSON(response);

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


    private void likesShowJSON(String response) {

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

        getDislikes();

    }


    private void getDislikes() {


        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_DISLIKES + item_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dislikesShowJSON(response);

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


    private void dislikesShowJSON(String response) {

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

        PV_LIKE_COUNT.setText(String.valueOf(like_count - dislike_count));

        pDialog.dismiss();

    }


    private void getComments() {


        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_COMMENTS + item_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                commentsShowJSON(response);

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


    private void commentsShowJSON(String response) {

        commentsList = new ArrayList<>();

        commentListAdapter = new CommentsListAdapter(this, commentsList);

        String pv_username;
        String pv_userimage;
        String pv_comment;
        String pv_timestamp;

        int j = 0;

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(basicFunctions.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {

                JSONObject commentData = result.getJSONObject(i);

                pv_username = commentData.getString(basicFunctions.KEY_USER_USERNAME);
                pv_userimage = commentData.getString(basicFunctions.KEY_USER_IMAGE);
                pv_comment = commentData.getString(basicFunctions.KEY_POST_COMMENT);
                pv_timestamp = commentData.getString(basicFunctions.KEY_POST_TIMESTAMP);

                j++;

                Comments comments = new Comments();

                comments.setUsername(pv_username);
                comments.setUser_image(pv_userimage);
                comments.setComment(pv_comment);
                comments.setDate(pv_timestamp);

                commentsList.add(comments);

                commentListAdapter.notifyDataSetChanged();


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        switch (item_type) {

            case "GK":

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGK));
                break;

            case "QNA":

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorQnA));
                break;

            case "Materials":

                PV_LL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorStudyMaterials));
                B_DOWNLOAD_FILE.setVisibility(View.VISIBLE);

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

        B_DOWNLOAD_FILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int MyVersion = Build.VERSION.SDK_INT;

                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (basicFunctions.HasStoragePermission()) {

                        new DownloadFileFromURL().execute(BasicFunctions.FILE_URL + item_description);

                    } else {

                        requestStoragePermission();

                    }
                } else
                    new DownloadFileFromURL().execute(BasicFunctions.FILE_URL + item_description);

            }
        });


        if (!item_image.equals("")) {

            PV_IMAGE.setVisibility(View.VISIBLE);

            PV_IMAGE.setBackground(null);
            byte[] decodedString_2 = Base64.decode(item_image, Base64.DEFAULT);
            Bitmap decodedByte_2 = BitmapFactory.decodeByteArray(decodedString_2, 0, decodedString_2.length);

            PV_IMAGE.setImageBitmap(decodedByte_2);

        } else {

            PV_IMAGE.setVisibility(View.GONE);
            PV_IMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
            PV_IMAGE.setImageResource(R.drawable.app_userbackground);

        }

        PV_USERNAME.setText(item_username);

        PV_DATE.setText(BasicFunctions.getTimeStamp(item_timestamp));


        if (!item_user_image.equals("")) {

            PV_USERIMAGE.setBackground(null);
            byte[] decodedString_1 = Base64.decode(item_user_image, Base64.DEFAULT);
            Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

            PV_USERIMAGE.setImageBitmap(decodedByte_1);

        } else {

            PV_USERIMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.me_profile, null));
            PV_USERIMAGE.setImageResource(R.drawable.app_userbackground);

        }

        PV_COMMENT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                postComment();
            }
        });

        PV_IMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PostView.this, ViewImage.class);

                if (!item_image.equals("")) {

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
                } else
                    intent.putExtra("image", "noimage");

                startActivity(intent);
            }
        });


        if (item_username.equals(basicFunctions.getUser_name()))
            PV_DELETE.setVisibility(View.VISIBLE);

        else PV_DELETE.setVisibility(View.GONE);

        PV_DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deletePost();
            }
        });

        PV_LIKE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (like != 1) {

                    like();
                    PV_LIKE.setBackgroundResource(R.drawable.pv_liked);
                    like = 1;

                } else
                    Toast.makeText(PostView.this, "You have already liked this post !", Toast.LENGTH_SHORT).show();
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

        PV_USER_PROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostView.this, Profile.class);
                intent.putExtra("type", item_username);
                startActivity(intent);

            }
        });

        if (item_username.equals(basicFunctions.getUser_name()))
            PV_REPORT.setVisibility(View.GONE);

        PV_REPORT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reportPost(item_id, item_title);
            }
        });

        PV_FACEBOOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareOnFacebook();

            }
        });

        PV_TWITTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareOnTwitter();

            }
        });

        if (j != 0) {

            COMMENT_LIST.setVisibility(View.VISIBLE);
            COMMENT_LIST.setAdapter(commentListAdapter);

        } else COMMENT_LIST.setVisibility(View.GONE);

        getLikes();

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

                    new DownloadFileFromURL().execute(BasicFunctions.FILE_URL + item_description);

                } else {

                    Toast.makeText(PostView.this, "Kindly grant Storage permission to continue !", Toast.LENGTH_LONG).show();

                }

                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void shareOnFacebook() {

        String app_id = "com.facebook.katana";

        if(basicFunctions.isAppInstalled(app_id)) {

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

        }

    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fb_callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void shareOnTwitter() {

        String app_id = "com.twitter.android";

        if(basicFunctions.isAppInstalled(app_id)) {

            TweetComposer.Builder builder = new TweetComposer.Builder(PostView.this)
                    .text("FebBook - " + item_title + " : " + item_description);
            builder.show();

        } else {

            try {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_id)));

            } catch (android.content.ActivityNotFoundException anfe) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_id)));

            }

            Toast.makeText(getApplicationContext(),"Kindly install the Twitter App to enable Twitter Post Sharing !",Toast.LENGTH_LONG).show();

        }

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

            if (convertView == null) {
                assert inflater != null;
                convertView = inflater.inflate(R.layout.activity_post_view_comment_item, parent, false);
            }

            TextView mUsername = convertView.findViewById(R.id.pv_list_item_username);
            TextView mComment = convertView.findViewById(R.id.pv_list_item_comment);
            TextView mDate = convertView.findViewById(R.id.pv_list_item_date);
            CircularImageView mUserImage = convertView.findViewById(R.id.pv_list_item_userimage);
            LinearLayout mUserProfile = convertView.findViewById(R.id.pv_user_profile);
            ImageButton DELETE = convertView.findViewById(R.id.pv_delete);
            TextView mReport = convertView.findViewById(R.id.pv_report);

            final Comments comments = commentItems.get(position);

            if (comments.getUsername().equals(basicFunctions.getUser_name()))
                DELETE.setVisibility(View.VISIBLE);

            else DELETE.setVisibility(View.GONE);

            mUsername.setText(comments.getUsername());

            mComment.setText(comments.getComment());

            mDate.setText(BasicFunctions.getTimeStamp(comments.getDate()));

            if (!comments.getUser_image().equals("")) {

                mUserImage.setBackground(null);
                byte[] decodedString_1 = Base64.decode(comments.getUser_image(), Base64.DEFAULT);
                Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

                mUserImage.setImageBitmap(Bitmap.createScaledBitmap(decodedByte_1, 60, 60, false));

            } else {

                mUserImage.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.me_profile, null));
                mUserImage.setImageResource(R.drawable.app_userbackground);

            }

            mUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(PostView.this, Profile.class);
                    intent.putExtra("type", comments.getUsername());
                    startActivity(intent);
                    finish();
                }
            });

            DELETE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    deleteComment(comments.getComment());
                }
            });

            if (comments.getUsername().equals(basicFunctions.getUser_name()))
                mReport.setVisibility(View.GONE);

            mReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    reportComment(item_id, comments.getComment());
                }
            });

            return convertView;
        }

    }


    private void reportPost(final String item_id, final String item_title) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "reportpost";

                        basicFunctions.performTask(method, item_id, "P : " + item_title);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(), "Reporting Cancelled !", Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to report this post ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    private void reportComment(final String item_id, final String item_title) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "reportcomment";

                        basicFunctions.performTask(method, item_id, "C : " + item_title);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(), "Reporting Cancelled !", Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to report this comment ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }


    private void postComment() {

        comment = ET_COMMENT.getText().toString();

        if (TextUtils.isEmpty(comment)) {

            ET_COMMENT.setError("Type in a Comment !");

        } else {

            String method = "comment";

            basicFunctions.performTask(method, item_id, comment);

            ET_COMMENT.setText("");

            getComments();

        }

    }


    private void like() {

        String method = "like";

        basicFunctions.performTask(method, item_id, "");

        getLikes();

    }


    private void dislike() {

        String method = "dislike";

        basicFunctions.performTask(method, item_id, "");

        getLikes();

    }


    private void deleteComment(final String comment) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "deletecomment";

                        basicFunctions.performTask(method, item_id, comment);

                        getComments();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(), "Deletion Cancelled !", Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this Comment ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    private void deletePost() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "deletepost";

                        basicFunctions.performTask(method, item_id, "");

                        Intent intent = new Intent(PostView.this, HomePage.class);
                        startActivity(intent);
                        finish();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(), "Deletion Cancelled !", Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this Post ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PostView.this);
            pDialog.setMessage("Downloading the File ... ");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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

                int fileLength = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                @SuppressLint("SdCardPath") File dir = new File("/sdcard/FebBook");
                dir.mkdir();

                @SuppressLint("SdCardPath")
                OutputStream output = new FileOutputStream("/sdcard/FebBook/" + item_description);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {

                    total += count;

                    publishProgress("" + (int)((total*100) / fileLength));

                    output.write(data, 0, count);

                }

                output.flush();

                output.close();
                input.close();

            } catch (final Exception e) {

                PostView.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(PostView.this,"Error : "+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {

            pDialog.setProgress(Integer.parseInt(progress[0]));

        }

        @Override
        protected void onPostExecute(String file_url) {

            pDialog.dismiss();

            Toast.makeText(PostView.this, "The File has been downloaded to /sdcard/FebBook/ !", Toast.LENGTH_LONG).show();

            @SuppressLint("SdCardPath") File file = new File("/sdcard/FebBook/" + item_description);

            String[] fileTokens = item_description.split("\\.(?=[^\\.]+$)");

            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileTokens[1]);

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), mime);
            startActivityForResult(intent, 10);

        }

    }


    private class Comments {

        private String username, user_image, comment, date;

        private Comments() {}

        private String getUsername() {
            return username;
        }

        private void setUsername(String username) {
            this.username = username;
        }

        private String getUser_image() {
            return user_image;
        }

        private void setUser_image(String user_image) {
            this.user_image = user_image;
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

}