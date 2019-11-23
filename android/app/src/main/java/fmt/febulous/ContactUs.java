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

import fmt.febulous.helper.BasicFunctions;


public class ContactUs extends AppCompatActivity {


    Button button_submit;

    private String c_email, c_name, c_subject, c_message;

    private EditText C_NAME, C_EMAIL, C_SUBJECT, C_MESSAGE;

    String actionbar_title;

    private BasicFunctions basicFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_us);

        actionbar_title="\tCONTACT US";
        setTitle(actionbar_title);

        basicFunctions = new BasicFunctions(this);

        C_NAME = (EditText) findViewById(R.id.c_name);

        C_EMAIL = (EditText) findViewById(R.id.c_email);

        C_SUBJECT = (EditText) findViewById(R.id.c_subject);

        C_MESSAGE=(EditText)findViewById(R.id.c_message);


        button_submit = (Button)findViewById(R.id.c_send);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c_name = C_NAME.getText().toString();

                c_email = C_EMAIL.getText().toString();

                c_subject = C_SUBJECT.getText().toString();

                c_message = C_MESSAGE.getText().toString();

                if (TextUtils.isEmpty(c_name)) {
                    C_NAME.setError("Type in your Name !");

                } else if (TextUtils.isEmpty(c_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(c_email).matches()) {
                    C_EMAIL.setError("Invalid Email-ID !");

                } else if (TextUtils.isEmpty(c_subject)) {
                    C_SUBJECT.setError("Type in your Subject !");

                } else if (TextUtils.isEmpty(c_message)) {
                    C_MESSAGE.setError("Type in your Message !");

                } else {

                    if(basicFunctions.isConnectingToInternet()) {

                        basicFunctions.sendEmail("ContactUs", c_name, c_email, c_subject, c_message);

                    }

                    else {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:

                                        Intent intent = new Intent(ContactUs.this, ContactUs.class);
                                        startActivity(intent);

                                        break;

                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(ContactUs.this);
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

}