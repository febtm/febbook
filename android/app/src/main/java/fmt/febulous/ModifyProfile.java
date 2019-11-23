package fmt.febulous;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import fmt.febulous.helper.EndPoints;


public class ModifyProfile extends AppCompatActivity {


    CircularImageView I_PIC_IMAGE;

    ImageView I_COVER_IMAGE;

    EditText ET_FULLNAME, ET_USERNAME, ET_DOB, ET_EMAIL, ET_CPASS, ET_NPASS, ET_CNPASS, ET_ABOUT, ET_COLLEGENAME, ET_DEPARTMENT;

    Spinner ET_USERTYPE, ET_COURSE;

    ArrayAdapter<String> user_type, user_course;

    String actionbar_title;

    Button modify_profile;

    private int temp;

    private ProgressDialog pDialog;

    Switch S_GENDER;

    String username, gender, p_picture = "", date = "", email, about = "", course, cover = "",
            cpass, npass, cnpass, oldusername, oldemail, usertype, department = "", collegename = "", fullname = "";

    private DatePickerDialog DatePickerDialog;

    private SimpleDateFormat dateFormatter;

    private BasicFunctions basicFunctions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        actionbar_title="\tMODIFY PROFILE";
        setTitle(actionbar_title);

        basicFunctions = new BasicFunctions(this);

        ET_USERNAME = (EditText) findViewById(R.id.ep_usernametext);
        S_GENDER = (Switch) findViewById(R.id.ep_genderswitch);
        ET_DOB = (EditText) findViewById(R.id.ep_bdatetext);
        ET_EMAIL = (EditText) findViewById(R.id.ep_emailtext);
        ET_FULLNAME = (EditText) findViewById(R.id.ep_fullnametext);
        ET_COLLEGENAME = (EditText) findViewById(R.id.ep_collegenametext);
        ET_DEPARTMENT = (EditText) findViewById(R.id.ep_departmentnametext);
        ET_CPASS = (EditText) findViewById(R.id.ep_cpasswordtext);
        ET_NPASS = (EditText) findViewById(R.id.ep_npasswordtext);
        ET_CNPASS = (EditText) findViewById(R.id.ep_cnpasswordtext);
        ET_COURSE = (Spinner) findViewById(R.id.ep_coursetext);
        ET_USERTYPE = (Spinner) findViewById(R.id.ep_usertypetext);
        ET_ABOUT = (EditText) findViewById(R.id.ep_abouttext);

        modify_profile = (Button) findViewById(R.id.ep_modify);

        I_PIC_IMAGE = (CircularImageView) findViewById(R.id.ep_userbutton);
        I_COVER_IMAGE = (ImageView) findViewById(R.id.ep_usercoverbutton);


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

        user_type = new ArrayAdapter<>(
                this, R.layout.activity_modify_profile_spinner, basicFunctions.USERTYPE
        );

        user_type.setDropDownViewResource(R.layout.activity_modify_profile_spinner);

        user_course = new ArrayAdapter<>(
                this, R.layout.activity_modify_profile_spinner, basicFunctions.USERCOURSE
        );

        user_course.setDropDownViewResource(R.layout.activity_modify_profile_spinner);


        ET_USERTYPE.setAdapter(user_type);

        ET_COURSE.setAdapter(user_course);


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


        I_PIC_IMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){

                            case DialogInterface.BUTTON_POSITIVE:

                                selectImage();
                                temp = 1;

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                Toast.makeText(ModifyProfile.this, "Kindly grant Febulous the permission to use the Camera and the External Storage !", Toast.LENGTH_LONG).show();

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyProfile.this);
                builder.setMessage("Have you granted Febulous the permission to use the Camera and the External Storage ?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });


        I_COVER_IMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){

                            case DialogInterface.BUTTON_POSITIVE:

                                selectImage();
                                temp = 2;

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                Toast.makeText(ModifyProfile.this, "Kindly grant Febulous the permission to use the Camera and the External Storage !", Toast.LENGTH_LONG).show();

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyProfile.this);
                builder.setMessage("Have you granted Febulous the permission to use the Camera and the External Storage ?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        modify_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modify_profile();
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

                            Intent intent = new Intent(ModifyProfile.this, HomePage.class);
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



    private void myprofile() {

        pDialog = ProgressDialog.show(this, "", "Fetching Profile ... ", false, false);

        StringRequest stringRequest = new StringRequest(EndPoints.MY_PROFILE+ basicFunctions.getUser_name(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON_Profile(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ModifyProfile.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(ModifyProfile.this);
        requestQueue.add(stringRequest);
    }



    private void showJSON_Profile(String response) {

         String mp_username = "", mp_userimage = "", mp_userabout = "",
                mp_name = "", mp_email = "", mp_date = "", mp_usercover = "",
                mp_gender = "", mp_college = "", mp_usertype = "",
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
            mp_usertype = profileData.getString(basicFunctions.KEY_USER_TYPE);
            mp_department = profileData.getString(basicFunctions.KEY_USER_DEPARTMENT);
            mp_course = profileData.getString(basicFunctions.KEY_USER_COURSE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        ET_USERNAME.setText(mp_username);

        ET_ABOUT.setText(mp_userabout);

        ET_FULLNAME.setText(mp_name);

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

        ET_COLLEGENAME.setText(mp_college);


        switch(mp_course){

            case "General":

                ET_COURSE.setSelection(1);
                break;

            case "Engineering":

                ET_COURSE.setSelection(2);
                break;

            case "Medicine":

                ET_COURSE.setSelection(3);
                break;

            case "Arts":

                ET_COURSE.setSelection(4);
                break;

            case "Science":

                ET_COURSE.setSelection(5);
                break;

            default:

                ET_COURSE.setSelection(0);
                break;

        }


        switch(mp_usertype){

            case "General User":

                ET_USERTYPE.setSelection(1);
                break;

            case "Student":

                ET_USERTYPE.setSelection(2);
                break;

            case "Teacher":

                ET_USERTYPE.setSelection(3);
                break;

            default:

                ET_USERTYPE.setSelection(0);
                break;

        }


        if(!mp_userimage.equals("")) {

            I_PIC_IMAGE.setBackground(null);
            byte[] decodedString_1 = Base64.decode(mp_userimage, Base64.DEFAULT);
            Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

            I_PIC_IMAGE.setImageBitmap(decodedByte_1);

            p_picture = mp_userimage;


        }

        else {

            I_PIC_IMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_userimage, null));
            I_PIC_IMAGE.setImageResource(R.drawable.app_userbackground);

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


    private void selectImage() {

        final CharSequence[] options = {"Take a Snap", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyProfile.this);
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

                        I_PIC_IMAGE.setBackground(null);

                        I_PIC_IMAGE.setImageBitmap(bitmap);


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
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                ConvertToString(thumbnail);

                if(this.temp == 1) {

                    I_PIC_IMAGE.setBackground(null);

                    I_PIC_IMAGE.setImageBitmap(thumbnail);


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



    private void modify_profile(){

        username = ET_USERNAME.getText().toString();
        email = ET_EMAIL.getText().toString();
        fullname = ET_FULLNAME.getText().toString();
        department = ET_DEPARTMENT.getText().toString();
        collegename = ET_COLLEGENAME.getText().toString();
        npass = ET_NPASS.getText().toString();
        date = ET_DOB.getText().toString();
        cpass = ET_CPASS.getText().toString();
        cnpass = ET_CNPASS.getText().toString();
        about = ET_ABOUT.getText().toString();
        course = ET_COURSE.getSelectedItem().toString();
        usertype = ET_USERTYPE.getSelectedItem().toString();

        oldusername = basicFunctions.getUser_name();
        oldemail = basicFunctions.getUser_email();

        if (S_GENDER.isChecked()) {

            gender = "F";
        } else {

            gender = "M";
        }


        if (TextUtils.isEmpty(username)) {
            ET_USERNAME.setError("Type in your Username !");

        } else if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ET_EMAIL.setError("Invalid Email-ID !");

        } else if (TextUtils.isEmpty(cpass)) {
            ET_CPASS.setError("Type in your Current Password !");

        } else if (TextUtils.isEmpty(npass)) {
            ET_NPASS.setError("Type in your New Password !");

        } else if (TextUtils.isEmpty(cnpass)) {
            ET_CNPASS.setError("Confirm your New Password !");

        } else if (usertype.equals("USER TYPE")) {
            Toast.makeText(ModifyProfile.this, "Kindly select your User Type!", Toast.LENGTH_LONG).show();

        } else if (course.equals("COURSE TYPE")) {
            Toast.makeText(ModifyProfile.this, "Kindly select your Course Type!", Toast.LENGTH_LONG).show();

        } else{

            String method = "profile";

            if (npass.equals(cnpass)) {
                ModifyProfileBackgroundTask modifyprofilebackgroundTask = new ModifyProfileBackgroundTask(this);

                modifyprofilebackgroundTask.execute(method, username, email, cnpass, p_picture, gender,
                        date, oldusername, oldemail, cpass, fullname, collegename, usertype, department, about, course, cover);

            } else
                ET_CNPASS.setError("New Passwords do not match ! Re-enter your New password !");

        }

    }



    private class ModifyProfileBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;

        private ProgressDialog pDialog;

        ModifyProfileBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ModifyProfile.this);
            pDialog.setMessage("Modifying Profile ... ");
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
                String npass = params[3];
                String picture = params[4];
                String gender = params[5];
                String date = params[6];
                String oldusername = params[7];
                String oldemail = params[8];
                String oldpass = params[9];
                String fullname = params[10];
                String collegename = params[11];
                String usertype = params[12];
                String department = params[13];
                String about = params[14];
                String course = params[15];
                String cover = params[16];

                try {

                    URL url = new URL(EndPoints.MODIFY_PROFILE);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(basicFunctions.getUser_id(), "UTF-8")
                            + "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                            + "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                            + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(npass, "UTF-8")
                            + "&" + URLEncoder.encode("picture", "UTF-8") + "=" + URLEncoder.encode(picture, "UTF-8")
                            + "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8")
                            + "&" + URLEncoder.encode("dob", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")
                            + "&" + URLEncoder.encode("oldusername", "UTF-8") + "=" + URLEncoder.encode(oldusername, "UTF-8")
                            + "&" + URLEncoder.encode("oldemail", "UTF-8") + "=" + URLEncoder.encode(oldemail, "UTF-8")
                            + "&" + URLEncoder.encode("oldpassword", "UTF-8") + "=" + URLEncoder.encode(oldpass, "UTF-8")
                            + "&" + URLEncoder.encode("fullname", "UTF-8") + "=" + URLEncoder.encode(fullname, "UTF-8")
                            + "&" + URLEncoder.encode("college", "UTF-8") + "=" + URLEncoder.encode(collegename, "UTF-8")
                            + "&" + URLEncoder.encode("usertype", "UTF-8") + "=" + URLEncoder.encode(usertype, "UTF-8")
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

                case "Your Profile has been edited Successfully !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

                    basicFunctions.setUserDetails(basicFunctions.getUser_id(), username, p_picture, email, cnpass);

                    pDialog.dismiss();

                    Intent intent = new Intent(ModifyProfile.this, HomePage.class);
                    startActivity(intent);

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