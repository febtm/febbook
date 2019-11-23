package fmt.febulous;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.navdrawer.SimpleSideDrawer;


public class PostTemplate extends AppCompatActivity
    implements PostTemplateFragment.onPostrecyclerFragmentItemSelected {

    private SimpleSideDrawer LeftAndRightDrawers;

    Button IB_POST;

    public static String TYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_template);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        }

        Intent intent = getIntent();

        TYPE = intent.getStringExtra("type");

        IB_POST = (Button) findViewById(R.id.pt_post_button);

        LeftAndRightDrawers = new SimpleSideDrawer(this);
        LeftAndRightDrawers.setRightBehindContentView(R.layout.activity_post_template_filters);

        ImageButton leftMenuBtn = (ImageButton) findViewById(R.id.pt_left_menu_btn);

        leftMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton rightMenuBtn = (ImageButton) findViewById(R.id.pt_right_menu_btn);

        rightMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeftAndRightDrawers.toggleRightDrawer();

            }
        });


        IB_POST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostTemplate.this, PostItem.class);
                intent.putExtra("type", TYPE);
                startActivity(intent);
            }
        });


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pt_root_layout, PostTemplateFragment.newInstance(), "ItemsList")
                .commit();


    }

    @Override
    public void onPostRecyclerFragmentItemSelected(String id) {

            Intent intent = new Intent(PostTemplate.this, PostView.class);
            intent.putExtra("id", id);
            startActivity(intent);

    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }

    }

}