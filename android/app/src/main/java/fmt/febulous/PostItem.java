package fmt.febulous;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

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
import fmt.febulous.helper.EndPoints;
import io.fabric.sdk.android.Fabric;


public class PostItem extends AppCompatActivity {


    Button button_publish;

    Button Image_button, File_button;
    ImageView Post_image;

    private int image_present = 0;

    String type1, title1, desc1, image1, facebook_share ="No", twitter_share = "No", TYPE;

    EditText title,desc;

    TextView type;

    private String uid1;

    String actionbar_title;

    private CallbackManager fb_callbackManager;

    private Switch share_on_fb, share_on_twitter;

    private BasicFunctions basicFunctions;

    private static final int PICK_FILE_REQUEST = 3;
    private String selectedFilePath;
    private ProgressDialog pDialog;
    PowerManager.WakeLock wakeLock;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        actionbar_title="\tPOST TO FEBULOUS";
        setTitle(actionbar_title);

        basicFunctions = new BasicFunctions(this);

        uid1 =  basicFunctions.getUser_id();

        type = (TextView) findViewById(R.id.post_type);
        title =(EditText)findViewById(R.id.post_title);
        desc =(EditText)findViewById(R.id.post_description);

        Image_button = (Button) findViewById(R.id.post_image_button);
        File_button = (Button) findViewById(R.id.post_file_button);
        Post_image = (ImageView) findViewById(R.id.post_image);

        share_on_fb = (Switch) findViewById(R.id.share_facebook);
        share_on_twitter = (Switch) findViewById(R.id.share_twitter);

        FacebookSdk.sdkInitialize(getApplicationContext());
        fb_callbackManager = CallbackManager.Factory.create();

        TwitterAuthConfig authConfig =  new TwitterAuthConfig(basicFunctions.TWITTER_KEY, basicFunctions.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        Intent intent = getIntent();
        TYPE = intent.getStringExtra("type");

        switch (TYPE){

            case "GK":

                type.setText(getResources().getString(R.string.general_digest));
                break;

            case "QNA":

                type.setText(getResources().getString(R.string.qna_section));
                break;

            case "Materials":

                type.setText(getResources().getString(R.string.study_materials));
                desc.setFocusable(false);

                desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(PostItem.this, "Description is disabled for Study Materials !", Toast.LENGTH_LONG).show();

                    }
                });

                File_button.setVisibility(View.VISIBLE);
                Image_button.setVisibility(View.GONE);

                break;

            case "Teach":

                type.setText(getResources().getString(R.string.teach_a_topic));
                break;

            case "Ideas":

                type.setText(getResources().getString(R.string.ideas_galore));
                break;

            case "Events":

                type.setText(getResources().getString(R.string.events_and_invites));
                break;

            default:

                type.setText(getResources().getString(R.string.general_digest));
                break;

        }


        File_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){

                            case DialogInterface.BUTTON_POSITIVE:

                                showFileChooser();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                Toast.makeText(PostItem.this, "Kindly grant Febulous the permission to use the External Storage !", Toast.LENGTH_LONG).show();

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(PostItem.this);
                builder.setMessage("Have you granted Febulous the permission to use the External Storage ?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });


        Image_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){

                            case DialogInterface.BUTTON_POSITIVE:

                                selectImage();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                Toast.makeText(PostItem.this, "Kindly grant Febulous the permission to use the Camera and the External Storage !", Toast.LENGTH_LONG).show();

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(PostItem.this);
                builder.setMessage("Have you granted Febulous the permission to use the Camera and the External Storage ?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        onClickButtonListener();

        share_on_fb.setChecked(false);

        share_on_fb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    facebook_share = "Yes";

                    if(title.getText().toString().equals("")) {

                        title.setError("Type in a Title for your Post !");
                        share_on_fb.setChecked(false);
                        facebook_share = "No";

                    }

                    else if(desc.getText().toString().equals("")) {

                        desc.setError("Type in a Description for your Post !");
                        share_on_fb.setChecked(false);
                        facebook_share = "No";

                    }

                    else
                        shareonFacebook();

                }

                else
                    facebook_share = "No";

            }
        });


        share_on_twitter.setChecked(false);

        share_on_twitter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    twitter_share = "Yes";

                    if(title.getText().toString().equals("")) {

                        title.setError("Type in a Title for your Post !");
                        share_on_twitter.setChecked(false);
                        twitter_share = "No";

                    }

                    else if(desc.getText().toString().equals("")) {

                        desc.setError("Type in a Description for your Post !");
                        share_on_twitter.setChecked(false);
                        twitter_share = "Yes";

                    }

                    else
                        shareOnTwitter();

                }

                else
                    twitter_share = "No";

            }
        });

    }


    private void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("file/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Add a File !"), PICK_FILE_REQUEST);
    }



    private int uploadFile(final String selectedFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {

            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PostItem.this, "File Doesn't Exist : " + selectedFilePath, Toast.LENGTH_LONG).show();
                }
            });
            return 0;

        } else {

            try {

                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(EndPoints.STUDY_MATERIALS);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

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
                            desc1 = fileName.replaceAll("[^A-Za-z0-9\\.]", "").toLowerCase().replaceAll(" ","");

                            if(basicFunctions.isConnectingToInternet()) {

                                PostAnItemBackgroundTask backgroundTask = new PostAnItemBackgroundTask(PostItem.this);
                                backgroundTask.execute("post", type1, title1, desc1, "", uid1, facebook_share, twitter_share);

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

                                AlertDialog.Builder builder = new AlertDialog.Builder(PostItem.this);
                                builder.setMessage("Network Failure : Please check your Internet Connection !")
                                        .setPositiveButton("Try Again ... ", dialogClickListener).show();

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
            return serverResponseCode;
        }

    }



    private void dbpost() {

        title1 = title.getText().toString();
        type1 = TYPE;

        if (TextUtils.isEmpty(title1)) {

            title.setError("Type in a Title for your Post !");

        } else {

            if (TYPE.equals("Materials")) {

                if (selectedFilePath != null) {
                    pDialog = ProgressDialog.show(PostItem.this, "", "Uploading File ... ", true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                uploadFile(selectedFilePath);

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

                desc1 = desc.getText().toString();

                if (TextUtils.isEmpty(desc1)) {

                    desc.setError("Type in a Description for your Post !");

                } else {


                    if(basicFunctions.isConnectingToInternet()) {

                        PostAnItemBackgroundTask backgroundTask = new PostAnItemBackgroundTask(this);

                        if (image_present == 0)
                            backgroundTask.execute("post", type1, title1, desc1, "", uid1, facebook_share, twitter_share);

                        else
                            backgroundTask.execute("post", type1, title1, desc1, image1, uid1, facebook_share, twitter_share);

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



    private void onClickButtonListener() {

        button_publish = (Button) findViewById(R.id.post_publish);
        button_publish.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        dbpost();

                    }
                }
        );

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

                    Post_image.setVisibility(View.VISIBLE);
                    Image_button.setText(getResources().getString(R.string.change_the_picture));
                    Post_image.setBackground(null);

                    Post_image.setImageBitmap(bitmap);

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
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                ConvertToString(thumbnail);

                Post_image.setVisibility(View.VISIBLE);
                Image_button.setText(getResources().getString(R.string.change_the_picture));
                Post_image.setBackground(null);

                Post_image.setImageBitmap(thumbnail);

            }

            else if (requestCode == PICK_FILE_REQUEST) {

                if (data == null) {
                    return;
                }

                int PROXIMITY_WAKE_LOCK = 32;
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PROXIMITY_WAKE_LOCK, "beam");
                wakeLock.acquire();

                Uri selectedFileUri = data.getData();
                selectedFilePath = BasicFunctions.getPath(this, selectedFileUri);

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    desc.setText(selectedFilePath);
                } else {
                    Toast.makeText(this, "Cannot upload file to server !", Toast.LENGTH_LONG).show();
                }
            }

        }

        fb_callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void ConvertToString(Bitmap bitmapm) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 25, baos);

            byte[] byteArrayImage = baos.toByteArray();
            image1 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            image_present = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void shareonFacebook() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

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
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    Toast.makeText(PostItem.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                                    exception.printStackTrace();
                                }
                            });

                            ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                                    .putString("og:type", "news.article")
                                    .putString("og:title", title.getText().toString())
                                    .putString("og:description", desc.getText().toString()).build();

                            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                                    .setActionType("news.publishes").putObject("article", object)
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
                        share_on_fb.setChecked(false);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Is the Facebook App installed on your device ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    private void shareOnTwitter(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        TweetComposer.Builder builder = new TweetComposer.Builder(PostItem.this)
                                .text(basicFunctions.POST_TW + " - " + title.getText().toString() + " : " + desc.getText().toString());
                        builder.show();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(getApplicationContext(),"Kindly install the Twitter App to enable Twitter Post Sharing!",Toast.LENGTH_LONG).show();
                        share_on_twitter.setChecked(false);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Is the Twitter App installed on your device ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

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
            pi_loading.setMessage("Posting to Febulous ... ");
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
                    URL url = new URL(EndPoints.POST);
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
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.equals("Your Post has been published !")) {
                Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

                pi_loading.dismiss();

                Intent intent = new Intent(PostItem.this, HomePage.class);
                startActivity(intent);

            } else if (result.equals("Posting Failed !")) {

                Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                pi_loading.dismiss();

            }
        }
    }

}