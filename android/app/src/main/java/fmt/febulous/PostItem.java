package fmt.febulous;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
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
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import fmt.febulous.helper.BasicFunctions;


public class PostItem extends AppCompatActivity {

    Button B_POST, B_IMAGE, B_FILE;

    ImageView IV_IMAGE;

    private int imagePresent = 0;

    private boolean fileExists;

    String type, title, description, image, facebookShare ="No", twitterShare = "No", intentType;

    EditText ET_TITLE, ET_DESCRIPTION;

    TextView TV_TYPE;

    private String userId;

    private CallbackManager fb_callbackManager;

    private Switch SHARE_FB, SHARE_TWITTER;

    private BasicFunctions basicFunctions;

    private static final int PICK_FILE_REQUEST = 3;
    private String originalFilePath, serverFilePath;
    private String fileName;
    private int fileVersion;

    private ProgressDialog pDialog;

    PowerManager.WakeLock wakeLock;

    ImageButton BACK_BUTTON, PI_TITLE_CANCEL, PI_DESC_CANCEL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        basicFunctions = new BasicFunctions(this);

        BACK_BUTTON = findViewById(R.id.pi_back);

        BACK_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        AdView mAdView = findViewById(R.id.pi_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        userId =  basicFunctions.getUser_id();

        TV_TYPE = findViewById(R.id.pi_post_type);
        ET_TITLE = findViewById(R.id.pi_title);
        ET_DESCRIPTION = findViewById(R.id.pi_description);

        PI_TITLE_CANCEL = findViewById(R.id.pi_title_cancel);

        PI_TITLE_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ET_TITLE.setText("");

            }
        });

        PI_DESC_CANCEL = findViewById(R.id.pi_description_cancel);

        PI_DESC_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ET_DESCRIPTION.setText("");

            }
        });

        B_IMAGE = findViewById(R.id.pi_image_button);
        B_FILE = findViewById(R.id.pi_file_button);
        B_POST = findViewById(R.id.pi_post);
        IV_IMAGE = findViewById(R.id.post_image);

        SHARE_FB = findViewById(R.id.pi_share_facebook);
        SHARE_TWITTER = findViewById(R.id.pi_share_twitter);

        fb_callbackManager = CallbackManager.Factory.create();

        Intent intent = getIntent();
        intentType = intent.getStringExtra("type");

        switch (intentType){

            case "GK":

                TV_TYPE.setText(getResources().getString(R.string.hp_db_gk));
                break;

            case "QNA":

                TV_TYPE.setText(getResources().getString(R.string.hp_db_qna));
                break;

            case "Materials":

                TV_TYPE.setText(getResources().getString(R.string.hp_db_materials));
                ET_DESCRIPTION.setFocusable(false);

                ET_DESCRIPTION.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(PostItem.this, "Description is disabled for Study Materials !", Toast.LENGTH_LONG).show();

                    }
                });

                B_FILE.setVisibility(View.VISIBLE);
                B_IMAGE.setVisibility(View.GONE);

                break;

            case "Teach":

                TV_TYPE.setText(getResources().getString(R.string.hp_db_tat));
                break;

            case "Ideas":

                TV_TYPE.setText(getResources().getString(R.string.hp_db_ideas));
                break;

            case "Events":

                TV_TYPE.setText(getResources().getString(R.string.hp_db_events));
                break;

            default:

                TV_TYPE.setText(getResources().getString(R.string.hp_db_gk));
                break;

        }

        B_IMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int MyVersion = Build.VERSION.SDK_INT;

                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (basicFunctions.HasCameraAndStoragePermission()) {

                        selectImage();

                    }

                    else {

                        requestCameraAndStoragePermission();

                    }
                }

                else
                    selectImage();
            }
        });


        B_FILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int MyVersion = Build.VERSION.SDK_INT;

                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (basicFunctions.HasStoragePermission()) {

                        showFileChooser();

                    }

                    else {

                        requestStoragePermission();

                    }
                }

                else
                    showFileChooser();
            }
        });


        B_POST.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                dbPost();

            }
        });

        SHARE_FB.setChecked(false);

        SHARE_FB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    facebookShare = "Yes";

                    if(ET_TITLE.getText().toString().equals("")) {

                        ET_TITLE.setError("Type in a Title for your Post !");
                        SHARE_FB.setChecked(false);
                        facebookShare = "No";

                    }

                    else if(ET_DESCRIPTION.getText().toString().equals("")) {

                        ET_DESCRIPTION.setError("Type in a Description for your Post !");
                        SHARE_FB.setChecked(false);
                        facebookShare = "No";

                    }

                    else
                        shareOnFacebook();

                }

                else
                    facebookShare = "No";

            }
        });


        SHARE_TWITTER.setChecked(false);

        SHARE_TWITTER.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    twitterShare = "Yes";

                    if(ET_TITLE.getText().toString().equals("")) {

                        ET_TITLE.setError("Type in a Title for your Post !");
                        SHARE_TWITTER.setChecked(false);
                        twitterShare = "No";

                    }

                    else if(ET_DESCRIPTION.getText().toString().equals("")) {

                        ET_DESCRIPTION.setError("Type in a Description for your Post !");
                        SHARE_TWITTER.setChecked(false);
                        twitterShare = "Yes";

                    }

                    else
                        shareOnTwitter();

                }

                else
                    twitterShare = "No";

            }
        });

    }


    private void requestCameraAndStoragePermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

    }


    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case 101:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    selectImage();

                } else {

                    Toast.makeText(PostItem.this, "Kindly grant Camera and Storage permission to continue !", Toast.LENGTH_LONG).show();

                }

             break;

            case 102:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showFileChooser();

                } else {

                    Toast.makeText(PostItem.this, "Kindly grant Storage permission to continue !", Toast.LENGTH_LONG).show();

                }

                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Add a File !"), PICK_FILE_REQUEST);
    }


    private void uploadFile(final String originalFilePath, final String serverFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File selectedFile = new File(originalFilePath);

        String[] parts = serverFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {

            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PostItem.this, "File Doesn't Exist : " + originalFilePath, Toast.LENGTH_LONG).show();
                }
            });

        } else {

            try {

                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(BasicFunctions.STUDY_MATERIALS);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", serverFilePath);

                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + serverFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    try {

                        dataOutputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(PostItem.this, "Insufficient Memory !", Toast.LENGTH_LONG).show();
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                try{
                    serverResponseCode = connection.getResponseCode();
                }catch (OutOfMemoryError e){
                    Toast.makeText(PostItem.this, "Memory Insufficient !", Toast.LENGTH_LONG).show();
                }

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            description = fileName.replaceAll("[^A-Za-z0-9\\.]", "").toLowerCase().replaceAll(" ","");

                            if(basicFunctions.isConnectingToInternet())
                                postItem();

                            else {

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){

                                            case DialogInterface.BUTTON_POSITIVE:

                                                if(basicFunctions.isConnectingToInternet())
                                                    postItem();

                                                else {

                                                    Toast.makeText(PostItem.this,
                                                            "No Internet Connection. Try again later !",
                                                            Toast.LENGTH_LONG).show();

                                                    dialog.dismiss();

                                                }

                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:

                                                Toast.makeText(PostItem.this,
                                                        "No Internet Connection. Try again later !",
                                                        Toast.LENGTH_LONG).show();

                                                dialog.dismiss();

                                                break;

                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(PostItem.this);
                                builder.setMessage("No Internet Connection. Try again ?")
                                        .setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();

                            }

                        }
                    });
                }

                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

                if (wakeLock.isHeld()) {

                    wakeLock.release();
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostItem.this, "File Not Found !", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostItem.this, "URL Error !", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostItem.this, "Cannot Read/Write File !", Toast.LENGTH_LONG).show();
                    }
                });
            }

            pDialog.dismiss();
        }

    }


    private void postItem() {

        PostAnItemBackgroundTask backgroundTask = new PostAnItemBackgroundTask(PostItem.this);
        backgroundTask.execute("post", type, title, description, "", userId, facebookShare, twitterShare);

    }

    private void dbPost() {

        title = ET_TITLE.getText().toString();
        type = intentType;

        if (TextUtils.isEmpty(title)) {

            ET_TITLE.setError("Type in a Title for your Post !");

        } else {

            if (intentType.equals("Materials")) {

                if (originalFilePath != null) {

                    pDialog = ProgressDialog.show(PostItem.this, "", "Uploading File ... ", true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                uploadFile(originalFilePath, serverFilePath);

                            } catch (OutOfMemoryError e) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PostItem.this, "Insufficient Memory !", Toast.LENGTH_LONG).show();
                                    }
                                });
                                pDialog.dismiss();
                            }

                        }
                    }).start();
                } else {
                    Toast.makeText(PostItem.this, "Please choose a file !", Toast.LENGTH_LONG).show();
                }


            } else {

                description = ET_DESCRIPTION.getText().toString();

                if (TextUtils.isEmpty(description)) {

                    ET_DESCRIPTION.setError("Type in a Description for your Post !");

                } else {


                    if(basicFunctions.isConnectingToInternet()) {

                        PostAnItemBackgroundTask backgroundTask = new PostAnItemBackgroundTask(this);

                        if (imagePresent == 0)
                            backgroundTask.execute("post", type, title, description, "", userId, facebookShare, twitterShare);

                        else
                            backgroundTask.execute("post", type, title, description, image, userId, facebookShare, twitterShare);

                    }

                    else{

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:

                                        Intent intent = new Intent(PostItem.this, HomePage.class);
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
            }
        }
    }


    private void selectImage() {

        final CharSequence[] options = {"Take a Snap", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(PostItem.this);
        builder.setTitle("Add a Picture !");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take a Snap")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);

                } else if (options[item].equals("Choose from Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                File f = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp : f.listFiles()) {

                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;

                    }
                }

                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    ConvertToString(bitmap);

                    IV_IMAGE.setVisibility(View.VISIBLE);
                    B_IMAGE.setText(getResources().getString(R.string.change_the_picture));
                    IV_IMAGE.setBackground(null);

                    IV_IMAGE.setImageBitmap(bitmap);

                    String path = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";

                    f.delete();

                    OutputStream outFile;

                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

                    try {

                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                        outFile.flush();
                        outFile.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                assert selectedImage != null;
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                assert c != null;
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                ConvertToString(thumbnail);

                IV_IMAGE.setVisibility(View.VISIBLE);
                B_IMAGE.setText(getResources().getString(R.string.change_the_picture));
                IV_IMAGE.setBackground(null);

                IV_IMAGE.setImageBitmap(thumbnail);

            }

            else if (requestCode == PICK_FILE_REQUEST) {

                if (data == null) {
                    return;
                }

                int PROXIMITY_WAKE_LOCK = 32;
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                assert powerManager != null;
                wakeLock = powerManager.newWakeLock(PROXIMITY_WAKE_LOCK, "beam");
                wakeLock.acquire(10*60*1000L /*10 minutes*/);

                Uri selectedFileUri = data.getData();
                originalFilePath = BasicFunctions.getPath(this, selectedFileUri);

                if (originalFilePath != null && !originalFilePath.equals("")) {

                    pDialog = ProgressDialog.show(PostItem.this, "", "Fetching File ... ", true);

                    fileVersion = 1;
                    String[] parts = originalFilePath.split("/");
                    fileName = parts[parts.length - 1];
                    fileName = fileName.toLowerCase();

                    String[] fileTokens = fileName.split("\\.(?=[^\\.]+$)");
                    String fileBase = fileTokens[0];
                    String fileExtension = fileTokens[1];

                    doesServerHaveFile(parts, fileBase, fileExtension);

                } else {
                    Toast.makeText(this, "Cannot upload file to server !", Toast.LENGTH_LONG).show();
                }
            }
        }

        fb_callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void doesServerHaveFile(final String[] parts, final String fileBase, final String fileExtension) {

        String url = BasicFunctions.CHECK_FILE_EXISTS + fileName;

        url = url.replaceAll(" ", "");

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray(basicFunctions.JSON_ARRAY);
                    JSONObject fileData = result.getJSONObject(0);

                    fileExists = fileData.getString("fileExists").equals("Yes");

                    if(fileExists) {

                        fileName = fileBase + String.valueOf(fileVersion) + "." + fileExtension;

                        fileVersion++;

                        doesServerHaveFile(parts, fileBase, fileExtension);

                    }

                    else {

                        parts[parts.length - 1] = fileName;

                        serverFilePath = "";

                        for(int i = 0; i < parts.length; i++){

                            if(i < (parts.length-1))
                                serverFilePath += (parts[i] + "/");

                            else
                                serverFilePath += parts[i];

                        }

                        ET_DESCRIPTION.setText(fileName);

                        pDialog.dismiss();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostItem.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(PostItem.this);
        requestQueue.add(stringRequest);

    }


    private void ConvertToString(Bitmap bitmapm) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 25, baos);

            byte[] byteArrayImage = baos.toByteArray();
            image = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            imagePresent = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void shareOnFacebook() {

        String app_id = "com.facebook.katana";

        if(basicFunctions.isAppInstalled(app_id)) {

            ShareDialog shareDialog = new ShareDialog(PostItem.this);

            if (ShareDialog.canShow(ShareLinkContent.class)) {

                shareDialog.registerCallback(fb_callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(PostItem.this, "Facebook Share Successful !", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(PostItem.this, "Facebook Share Cancelled !", Toast.LENGTH_LONG).show();
                        SHARE_FB.setChecked(false);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(PostItem.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        exception.printStackTrace();
                    }
                });

                ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                        .putString("og:type", "news.article")
                        .putString("og:title", ET_TITLE.getText().toString())
                        .putString("og:description", ET_DESCRIPTION.getText().toString()).build();

                ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                        .setActionType("news.publishes").putObject("article", object)
                        .build();

                ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                        .setPreviewPropertyName("article")
                        .setAction(action)
                        .build();

                shareDialog.show(content);

            } else {

                try {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_id)));

                } catch (android.content.ActivityNotFoundException anfe) {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_id)));

                }

                SHARE_FB.setChecked(false);

                Toast.makeText(getApplicationContext(), "Kindly install the Facebook App to enable Facebook Post Sharing !", Toast.LENGTH_LONG).show();

            }
        }
    }


    private void shareOnTwitter(){

        String app_id = "com.twitter.android";

        if(basicFunctions.isAppInstalled(app_id)) {

            TweetComposer.Builder builder = new TweetComposer.Builder(PostItem.this)
                    .text("FebBook - " + ET_TITLE.getText().toString() + " : " + ET_DESCRIPTION.getText().toString());
            builder.show();

        } else {

            try {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_id)));

            } catch (android.content.ActivityNotFoundException anfe) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_id)));

            }

            Toast.makeText(getApplicationContext(),"Kindly install the Twitter App to enable Twitter Post Sharing !",Toast.LENGTH_LONG).show();

            SHARE_TWITTER.setChecked(false);

        }

    }


    @SuppressLint("StaticFieldLeak")
    private class PostAnItemBackgroundTask extends AsyncTask<String, Void, String> {

        private ProgressDialog pi_loading;

        Context ctx;
        String data;

        PostAnItemBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            pi_loading = new ProgressDialog(PostItem.this);
            pi_loading.setMessage("Posting to FebBook ... ");
            pi_loading.setIndeterminate(false);
            pi_loading.setCancelable(true);
            pi_loading.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("post")) {

                String type = params[1];
                String title = params[2];
                String desc = params[3];
                String image = params[4];
                String uid = params[5];
                String facebook_share = params[6];
                String twitter_share = params[7];

                try {
                    URL url = new URL(BasicFunctions.POST);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")
                            + "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8")
                            + "&" + URLEncoder.encode("desc", "UTF-8") + "=" + URLEncoder.encode(desc, "UTF-8")
                            + "&" + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8")
                            + "&" + URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8")
                            + "&" + URLEncoder.encode("facebook_share", "UTF-8") + "=" + URLEncoder.encode(facebook_share, "UTF-8")
                            + "&" + URLEncoder.encode("twitter_share", "UTF-8") + "=" + URLEncoder.encode(twitter_share, "UTF-8");


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

                case "Your Post has been published !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

                    pi_loading.dismiss();

                    Intent intent = new Intent(PostItem.this, HomePage.class);
                    startActivity(intent);

                    break;

                case "Posting Failed !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pi_loading.dismiss();

                    break;

                default:

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pi_loading.dismiss();

                    break;
            }
        }
    }
}