package fmt.febulous.helper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fmt.febulous.R;


public class ViewImage extends AppCompatActivity {


    String actionbar_title, image;

    ImageView IMAGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_image);

        IMAGE = (ImageView) findViewById(R.id.iv_view_image);

        actionbar_title="\tVIEW IMAGE";

        setTitle(actionbar_title);

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
}