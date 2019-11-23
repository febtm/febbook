package fmt.febulous.helper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fmt.febulous.R;


public class ViewImage extends AppCompatActivity {


    String image;

    ImageView IMAGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_image);

        IMAGE = findViewById(R.id.vi_view_image);

        ImageButton BACK_BUTTON = findViewById(R.id.vi_back);

        BACK_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        Intent intent = getIntent();

        image = intent.getStringExtra("image");


        switch(image){

            case "large_image":

                IMAGE.setBackground(null);

                File cacheDir = getBaseContext().getCacheDir();

                File f = new File(cacheDir, image);

                FileInputStream fis = null;

                try {

                    fis = new FileInputStream(f);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Bitmap bitmap = BitmapFactory.decodeStream(fis);

                IMAGE.setImageBitmap(bitmap);

                break;

            case "noimage":

                IMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
                IMAGE.setImageResource(R.drawable.app_userbackground);

                break;

            default:

                IMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
                IMAGE.setImageResource(R.drawable.app_userbackground);

                break;

        }

    }

}