package fmt.febulous;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fmt.febulous.helper.BasicFunctions;
import fmt.febulous.helper.EndPoints;


public class RecoverPassword extends AppCompatActivity {


    EditText ET_EMAIL;
    Button B_RESET;

    private String email;

    String fp_username, fp_password, subject;

    String actionbar_title;

    private BasicFunctions basicFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        actionbar_title="\tRECOVER PASSWORD";
        setTitle(actionbar_title);

        basicFunctions = new BasicFunctions(this);

        ET_EMAIL = (EditText) findViewById(R.id.fp_email);
        B_RESET = (Button) findViewById(R.id.fp_reset);

        B_RESET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = ET_EMAIL.getText().toString();

                if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    ET_EMAIL.setError("Invalid Email-Id !");

                } else {

                    if(basicFunctions.isConnectingToInternet())
                        getUserData(email);

                    else {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:

                                        Intent intent = new Intent(RecoverPassword.this, RecoverPassword.class);
                                        startActivity(intent);

                                        break;

                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(RecoverPassword.this);
                        builder.setMessage("Network Failure : Please check your Internet Connection !")
                                .setPositiveButton("Try Again ... ", dialogClickListener).show();

                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }


    private void getUserData(String temp_email) {

        StringRequest stringRequest = new StringRequest(EndPoints.FORGOT_PASSWORD+temp_email, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("An Account with this Email-Id does not exist !"))
                    Toast.makeText(RecoverPassword.this, response, Toast.LENGTH_LONG).show();

                else
                    showJSON(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RecoverPassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
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


    private void showJSON(String response) {

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(basicFunctions.JSON_ARRAY);
            JSONObject fpdata = result.getJSONObject(0);
            fp_username = fpdata.getString(basicFunctions.KEY_USER_USERNAME);
            fp_password = fpdata.getString(basicFunctions.KEY_USER_PASSWORD);

            subject = "Febulous : Did you forget your password ?";

            basicFunctions.sendEmail("ForgotPassword", fp_username, email, subject, fp_password);

            Intent intent = new Intent(RecoverPassword.this, Login.class);
            startActivity(intent);

            Toast.makeText(RecoverPassword.this,"A recovery email has been sent to your Email-Id !",Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}