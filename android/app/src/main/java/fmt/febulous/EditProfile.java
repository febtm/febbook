package fmt.febulous;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import fmt.febulous.helper.BasicFunctions;


public class EditProfile extends AppCompatActivity {


    CircularImageView I_USER_IMAGE;

    ImageView I_COVER_IMAGE;

    EditText ET_NAME, ET_USERNAME, ET_DOB, ET_EMAIL, ET_CPASS, ET_NPASS, ET_CNPASS, ET_ABOUT, ET_COLLEGE, ET_DEPARTMENT, ET_COURSE;

    CheckBox CB_C_SHOW, CB_N_SHOW, CB_CN_SHOW;

    ImageButton BACK_BUTTON, EP_USERNAME_CANCEL, EP_ABOUT_CANCEL, EP_NAME_CANCEL, EP_EMAIL_CANCEL, EP_DOB_CANCEL, EP_C_PASS_CANCEL,
                EP_N_PASS_CANCEL, EP_CN_PASS_CANCEL, EP_COLLEGE_CANCEL, EP_COURSE_CANCEL, EP_DEPARTMENT_CANCEL;

    Button B_EDIT;

    private int temp;

    private ProgressDialog pDialog;

    Switch S_GENDER;

    String username, gender, p_picture = "", date = "", email, about = "", course, cover = "",
            c_pass, npass, cn_password, old_username, old_email, department = "", college = "", name = "";

    private DatePickerDialog DatePickerDialog;

    private SimpleDateFormat dateFormatter;

    private BasicFunctions basicFunctions;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        basicFunctions = new BasicFunctions(this);

        BACK_BUTTON = findViewById(R.id.ep_back);

        BACK_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        CB_C_SHOW = findViewById(R.id.ep_c_show_password);
        CB_N_SHOW = findViewById(R.id.ep_n_show_password);
        CB_CN_SHOW = findViewById(R.id.ep_cn_show_password);

        ET_USERNAME = findViewById(R.id.ep_username_text);
        S_GENDER = findViewById(R.id.ep_gender_switch);
        ET_DOB = findViewById(R.id.ep_bdate_text);
        ET_EMAIL = findViewById(R.id.ep_email_text);
        ET_NAME = findViewById(R.id.ep_name_text);
        ET_COLLEGE = findViewById(R.id.ep_college_text);
        ET_DEPARTMENT = findViewById(R.id.ep_department_text);
        ET_CPASS = findViewById(R.id.ep_c_password_text);
        ET_NPASS = findViewById(R.id.ep_n_password_text);
        ET_CNPASS = findViewById(R.id.ep_cn_password_text);
        ET_COURSE = findViewById(R.id.ep_course_text);
        ET_ABOUT = findViewById(R.id.ep_about_text);

        EP_USERNAME_CANCEL = findViewById(R.id.ep_user_name_cancel);
        EP_ABOUT_CANCEL = findViewById(R.id.ep_about_cancel);
        EP_NAME_CANCEL = findViewById(R.id.ep_name_cancel);
        EP_EMAIL_CANCEL = findViewById(R.id.ep_email_cancel);
        EP_DOB_CANCEL = findViewById(R.id.ep_bdate_cancel);
        EP_C_PASS_CANCEL = findViewById(R.id.ep_c_password_cancel);
        EP_N_PASS_CANCEL = findViewById(R.id.ep_n_password_cancel);
        EP_CN_PASS_CANCEL = findViewById(R.id.ep_cn_password_cancel);
        EP_COURSE_CANCEL = findViewById(R.id.ep_course_cancel);
        EP_COLLEGE_CANCEL = findViewById(R.id.ep_college_cancel);
        EP_DEPARTMENT_CANCEL = findViewById(R.id.ep_department_cancel);


        EP_USERNAME_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_USERNAME.setText("");

            }
        });

        EP_ABOUT_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_ABOUT.setText("");

            }
        });

        EP_NAME_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_NAME.setText("");

            }
        });

        EP_EMAIL_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_EMAIL.setText("");

            }
        });

        EP_C_PASS_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_CPASS.setText("");

            }
        });

        EP_N_PASS_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_NPASS.setText("");

            }
        });

        EP_CN_PASS_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_CNPASS.setText("");

            }
        });

        EP_DOB_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_DOB.setText("");

            }
        });

        EP_COLLEGE_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_COLLEGE.setText("");

            }
        });

        EP_COURSE_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_COURSE.setText("");

            }
        });

        EP_DEPARTMENT_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ET_DEPARTMENT.setText("");

            }
        });



        B_EDIT = findViewById(R.id.ep_edit);

        I_USER_IMAGE = findViewById(R.id.ep_user_button);
        I_COVER_IMAGE = findViewById(R.id.ep_user_cover_button);


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        ET_DOB.setInputType(InputType.TYPE_NULL);
        ET_DOB.requestFocus();

        ET_DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.show();

            }
        });

        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                ET_DOB.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



        S_GENDER.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    gender = "F";
                } else {
                    gender = "M";
                }

            }
        });


        CB_C_SHOW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ET_CPASS.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    ET_CPASS.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        CB_N_SHOW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ET_NPASS.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    ET_NPASS.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        CB_CN_SHOW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    ET_CNPASS.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    ET_CNPASS.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        I_USER_IMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                temp = 1;

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


        I_COVER_IMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                temp = 2;

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

        B_EDIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editProfile();
            }
        });


        if(basicFunctions.isConnectingToInternet())
            fetchProfile();

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){

                        case DialogInterface.BUTTON_POSITIVE:

                            if(basicFunctions.isConnectingToInternet())
                                fetchProfile();

                            else {

                                Toast.makeText(EditProfile.this,
                                        "No Internet Connection. Try again later !",
                                        Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            Toast.makeText(EditProfile.this,
                                    "No Internet Connection. Try again later !",
                                    Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
            builder.setMessage("No Internet Connection. Try again ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }

    }


    private void fetchProfile() {

        pDialog = ProgressDialog.show(this, "", "Fetching Profile ... ", false, false);

        StringRequest stringRequest = new StringRequest(BasicFunctions.MY_PROFILE+ basicFunctions.getUser_name(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON_Profile(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfile.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(EditProfile.this);
        requestQueue.add(stringRequest);
    }


    private void showJSON_Profile(String response) {

         String mp_username = "", mp_userimage = "", mp_userabout = "",
                mp_name = "", mp_email = "", mp_date = "", mp_usercover = "",
                mp_gender = "", mp_college = "",
                mp_department = "", mp_course = "";


        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(basicFunctions.JSON_ARRAY);
            JSONObject profileData = result.getJSONObject(0);

            mp_username = profileData.getString(basicFunctions.KEY_USER_USERNAME);
            mp_userimage = profileData.getString(basicFunctions.KEY_USER_IMAGE);
            mp_userabout = profileData.getString(basicFunctions.KEY_USER_ABOUT);
            mp_email = profileData.getString(basicFunctions.KEY_USER_EMAIL);
            mp_name = profileData.getString(basicFunctions.KEY_USER_NAME);
            mp_date = profileData.getString(basicFunctions.KEY_USER_DOB);
            mp_usercover = profileData.getString(basicFunctions.KEY_USER_COVERIMAGE);
            mp_gender = profileData.getString(basicFunctions.KEY_USER_GENDER);
            mp_college = profileData.getString(basicFunctions.KEY_USER_COLLEGE);
            mp_department = profileData.getString(basicFunctions.KEY_USER_DEPARTMENT);
            mp_course = profileData.getString(basicFunctions.KEY_USER_COURSE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        ET_USERNAME.setText(mp_username);

        ET_ABOUT.setText(mp_userabout);

        ET_NAME.setText(mp_name);

        switch(mp_gender){

            case "M":

                S_GENDER.setChecked(false);
                break;

            case "":

                S_GENDER.setChecked(false);
                break;

            case "F":

                S_GENDER.setChecked(true);
                break;

        }


        ET_EMAIL.setText(mp_email);

        if(!mp_date.equals("0000-00-00"))
            ET_DOB.setText(mp_date);

        ET_DEPARTMENT.setText(mp_department);

        ET_COLLEGE.setText(mp_college);

        ET_COURSE.setText(mp_course);

        if(!mp_userimage.equals("")) {

            I_USER_IMAGE.setBackground(null);
            byte[] decodedString_1 = Base64.decode(mp_userimage, Base64.DEFAULT);
            Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

            I_USER_IMAGE.setImageBitmap(decodedByte_1);

            p_picture = mp_userimage;


        }

        else {

            I_USER_IMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_userimage, null));
            I_USER_IMAGE.setImageResource(R.drawable.app_userbackground);

        }

        if(!mp_usercover.equals("")) {

            I_COVER_IMAGE.setBackground(null);
            byte[] decodedString_2 = Base64.decode(mp_usercover, Base64.DEFAULT);
            Bitmap decodedByte_2 = BitmapFactory.decodeByteArray(decodedString_2, 0, decodedString_2.length);

            I_COVER_IMAGE.setImageBitmap(decodedByte_2);

            cover = mp_usercover;

        }

        else {

            I_COVER_IMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
            I_COVER_IMAGE.setImageResource(R.drawable.app_userbackground);
        }

        pDialog.dismiss();


    }


    private void requestCameraAndStoragePermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    selectImage();

                } else {

                    Toast.makeText(EditProfile.this, "Kindly grant Camera and Storage permission to continue !", Toast.LENGTH_LONG).show();

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void selectImage() {

        final CharSequence[] options = {"Take a Snap", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
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

                    if(this.temp == 1) {

                        I_USER_IMAGE.setBackground(null);

                        I_USER_IMAGE.setImageBitmap(bitmap);


                    }

                    else {

                        I_COVER_IMAGE.setBackground(null);

                        I_COVER_IMAGE.setImageBitmap(bitmap);


                    }

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

            } else if (requestCode == 2) {

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

                if(this.temp == 1) {

                    I_USER_IMAGE.setBackground(null);

                    I_USER_IMAGE.setImageBitmap(thumbnail);


                }

                else {

                    I_COVER_IMAGE.setBackground(null);

                    I_COVER_IMAGE.setImageBitmap(thumbnail);

                }
            }
        }
    }


    private void ConvertToString(Bitmap bitmapm) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 25, baos);

            byte[] byteArrayImage = baos.toByteArray();

            if(this.temp == 1)
                p_picture = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

            else
                cover = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void editProfile(){

        username = ET_USERNAME.getText().toString();
        email = ET_EMAIL.getText().toString();
        name = ET_NAME.getText().toString();
        department = ET_DEPARTMENT.getText().toString();
        college = ET_COLLEGE.getText().toString();
        npass = ET_NPASS.getText().toString();
        date = ET_DOB.getText().toString();
        c_pass = ET_CPASS.getText().toString();
        cn_password = ET_CNPASS.getText().toString();
        about = ET_ABOUT.getText().toString();
        course = ET_COURSE.getText().toString();

        old_username = basicFunctions.getUser_name();
        old_email = basicFunctions.getUser_email();

        if (S_GENDER.isChecked()) {

            gender = "F";
        } else {

            gender = "M";
        }

        if (TextUtils.isEmpty(username)) {
            ET_USERNAME.setError("Type in your Username !");

        } else if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ET_EMAIL.setError("Invalid Email-ID !");

        } else if (TextUtils.isEmpty(c_pass)) {
            ET_CPASS.setError("Type in your Current Password !");

        } else if (TextUtils.isEmpty(npass)) {
            ET_NPASS.setError("Type in your New Password !");

        } else if (TextUtils.isEmpty(cn_password)) {
            ET_CNPASS.setError("Confirm your New Password !");

        } else{

            String method = "profile";

            if (npass.equals(cn_password)) {

                EditProfileBackgroundTask modifyprofilebackgroundTask = new EditProfileBackgroundTask(this);
                modifyprofilebackgroundTask.execute(method, username, email, cn_password, p_picture, gender,
                        date, old_username, old_email, c_pass, name, college, department, about, course, cover);

            } else
                ET_CNPASS.setError("New Passwords do not match ! Re-enter your New password !");

        }

    }


    @SuppressLint("StaticFieldLeak")
    private class EditProfileBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;

        private ProgressDialog pDialog;

        EditProfileBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(EditProfile.this);
            pDialog.setMessage("Editing Profile ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("profile")) {

                String username = params[1];
                String email = params[2];
                String n_password = params[3];
                String picture = params[4];
                String gender = params[5];
                String date = params[6];
                String old_username = params[7];
                String old_email = params[8];
                String old_password = params[9];
                String name = params[10];
                String college = params[11];
                String department = params[12];
                String about = params[13];
                String course = params[14];
                String cover = params[15];

                try {

                    URL url = new URL(BasicFunctions.EDIT_PROFILE);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(basicFunctions.getUser_id(), "UTF-8")
                            + "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                            + "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                            + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(n_password, "UTF-8")
                            + "&" + URLEncoder.encode("picture", "UTF-8") + "=" + URLEncoder.encode(picture, "UTF-8")
                            + "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8")
                            + "&" + URLEncoder.encode("dob", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")
                            + "&" + URLEncoder.encode("old_username", "UTF-8") + "=" + URLEncoder.encode(old_username, "UTF-8")
                            + "&" + URLEncoder.encode("old_email", "UTF-8") + "=" + URLEncoder.encode(old_email, "UTF-8")
                            + "&" + URLEncoder.encode("old_password", "UTF-8") + "=" + URLEncoder.encode(old_password, "UTF-8")
                            + "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                            + "&" + URLEncoder.encode("college", "UTF-8") + "=" + URLEncoder.encode(college, "UTF-8")
                            + "&" + URLEncoder.encode("about", "UTF-8") + "=" + URLEncoder.encode(about, "UTF-8")
                            + "&" + URLEncoder.encode("cover", "UTF-8") + "=" + URLEncoder.encode(cover, "UTF-8")
                            + "&" + URLEncoder.encode("course", "UTF-8") + "=" + URLEncoder.encode(course, "UTF-8")
                            + "&" + URLEncoder.encode("department", "UTF-8") + "=" + URLEncoder.encode(department, "UTF-8");

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

                case "Your Profile has been edited successfully !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

                    basicFunctions.setUserDetails(basicFunctions.getUser_id(), username, p_picture, email, cn_password);

                    pDialog.dismiss();

                    Intent intent = new Intent(EditProfile.this, Profile.class);
                    intent.putExtra("type", "SELF");
                    startActivity(intent);
                    EditProfile.this.finish();

                    break;

                case "Username already exists ! Please enter a unique Username !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    break;

                case "The Current Password that you have entered is incorrect ! Please enter the correct password !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
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