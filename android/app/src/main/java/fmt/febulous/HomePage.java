package fmt.febulous;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.navdrawer.SimpleSideDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import fmt.febulous.helper.BasicFunctions;
import fmt.febulous.helper.EndPoints;


public class HomePage extends AppCompatActivity
    implements HomePageFragment.onHomepageFragmentItemSelected{


    JSONArray CountArray = null;

    private BasicFunctions basicFunctions;

    private SimpleSideDrawer LeftAndRightDrawers;

    ListView leftMenuList;

    String[] leftNavMenuTitles;
    TypedArray leftNavMenuIcons;
    Button IB_DASHBOARD;

    ArrayList<MenuListItem> LeftMenuItemList;
    HomePage.MenuListAdapter LeftMenuListAdapter;

    private Integer unReadNotificationCount = 0;
    private Integer unReadMessageCount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        basicFunctions = new BasicFunctions(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        }

        if(basicFunctions.isConnectingToInternet())
            getCount();

        else{

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(HomePage.this, HomePage.class);
                            startActivity(intent);

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Network Failure : Please check your Internet Connection !")
                    .setPositiveButton("Try Again ... ", dialogClickListener).show();

        }

        LeftAndRightDrawers = new SimpleSideDrawer(this);
        LeftAndRightDrawers.setLeftBehindContentView(R.layout.activity_menu);
        LeftAndRightDrawers.setRightBehindContentView(R.layout.activity_homepage_filters);

        ImageButton leftMenuBtn = (ImageButton) findViewById(R.id.homepage_left_menu_btn);

        ImageButton rightMenuBtn = (ImageButton) findViewById(R.id.homepage_right_menu_btn);

        leftMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeftAndRightDrawers.toggleLeftDrawer();

            }
        });

        rightMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeftAndRightDrawers.toggleRightDrawer();

            }
        });


        IB_DASHBOARD = (Button) findViewById(R.id.homepage_dashboard_button);

        IB_DASHBOARD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomePage.this, Dashboard.class);
                startActivity(intent);
            }
        });

    }


    private void getCount() {

        StringRequest stringRequest = new StringRequest(EndPoints.GET_COUNT + basicFunctions.getUser_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_Count(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomePage.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(HomePage.this);
        requestQueue.add(stringRequest);
    }


    @SuppressWarnings("ResourceType")
    protected void showList_Count(String response) {

        String notification_count;
        String chat_count;

        try {
            JSONObject jsonObj = new JSONObject(response);
            CountArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);
            JSONObject jsonObject = CountArray.getJSONObject(0);

            notification_count = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_COUNT);
            chat_count = jsonObject.getString(basicFunctions.KEY_CHAT_COUNT);

            unReadNotificationCount = Integer.parseInt(notification_count);

            unReadMessageCount = Integer.parseInt(chat_count);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LeftMenuItemList = new ArrayList<>();
        leftNavMenuTitles = getResources().getStringArray(R.array.menu_titles);
        leftNavMenuIcons = getResources().obtainTypedArray(R.array.menu_icons);

        LeftMenuItemList.add(new MenuListItem(leftNavMenuTitles[0], leftNavMenuIcons.getResourceId(0, -1)));
        LeftMenuItemList.add(new MenuListItem(leftNavMenuTitles[1], leftNavMenuIcons.getResourceId(1, -1)));
        LeftMenuItemList.add(new MenuListItem(leftNavMenuTitles[2], leftNavMenuIcons.getResourceId(2, -1)));
        LeftMenuItemList.add(new MenuListItem(leftNavMenuTitles[3], leftNavMenuIcons.getResourceId(3, -1)));
        LeftMenuItemList.add(new MenuListItem(leftNavMenuTitles[4], leftNavMenuIcons.getResourceId(4, -1)));

        leftNavMenuIcons.recycle();

        leftMenuList = (ListView) findViewById(R.id.menu_list);
        LeftMenuListAdapter = new MenuListAdapter(this, LeftMenuItemList);
        leftMenuList.setAdapter(LeftMenuListAdapter);
        leftMenuList.setOnItemClickListener(new SlideMenuClickListener());


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homepage_root_layout, HomePageFragment.newInstance(), "ItemsList")
                .commit();

    }


    @Override
    public void onHomePageFragmentItemSelected(String id) {

        Intent intent = new Intent(HomePage.this, PostView.class);
        intent.putExtra("id", id);
        startActivity(intent);

    }


    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
                    displayView(position);
        }

    }


    private void displayView(int position) {

        switch (position) {

            case 0:

                Intent Profile = new Intent(HomePage.this, Profile.class);
                Profile.putExtra("type", "SELF");
                startActivity(Profile);

                break;

            case 1:

                Intent Chat = new Intent(HomePage.this, Messenger.class);
                startActivity(Chat);

                break;

            case 2:

                Intent Notification = new Intent(HomePage.this, Notifications.class);
                startActivity(Notification);

                break;

            case 3:

                Intent Contact = new Intent(HomePage.this, ContactUs.class);
                startActivity(Contact);

                break;

            case 4:

                LogoutBackgroundTask lb = new LogoutBackgroundTask(HomePage.this);
                lb.execute("logout", basicFunctions.getUser_id());

                break;

            default:

                break;
        }
    }


    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    private class MenuListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<HomePage.MenuListItem> menuListItems;

        private MenuListAdapter(Context context, ArrayList<HomePage.MenuListItem> menuListItems){

            this.context = context;
            this.menuListItems = menuListItems;
        }

        @Override
        public int getCount() {
            return menuListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return menuListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                LayoutInflater mInflater = (LayoutInflater)
                        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.activity_menu_list_item, parent, false);

            }


            ImageView imgIcon = (ImageView) convertView.findViewById(R.id.menu_list_item_icon);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.menu_list_item_title);

            TextView txtCount = (TextView) convertView.findViewById(R.id.menu_list_item_counter);

            imgIcon.setImageResource(menuListItems.get(position).getIcon());
            txtTitle.setText(menuListItems.get(position).getTitle());


            if(menuListItems.get(position).getTitle().equals("FEBULOUS MESSENGER"))
                if(!(unReadMessageCount == 0)){

                    txtCount.setVisibility(View.VISIBLE);

                    txtCount.setText(String.valueOf(unReadMessageCount));

            }

            if(menuListItems.get(position).getTitle().equals("NOTIFICATIONS"))
                if(!(unReadNotificationCount == 0)){

                    txtCount.setVisibility(View.VISIBLE);

                    txtCount.setText(String.valueOf(unReadNotificationCount));

            }

            return convertView;
        }
    }


    private class MenuListItem {

        private String title;
        private int icon;


        private MenuListItem(String title, int icon){

            this.title = title;
            this.icon = icon;

        }

        private String getTitle(){
            return this.title;
        }

        private int getIcon(){
            return this.icon;
        }

    }


    private class LogoutBackgroundTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        Context ctx;

        LogoutBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomePage.this);
            pDialog.setMessage("Logging Out ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("logout")) {

                String userid = params[1];

                try {

                    URL url = new URL(EndPoints.LOGOUT);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");

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
                    IS.close();
                    httpURLConnection.disconnect();
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

            switch (result) {

                case "Logout Successful !":

                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                    basicFunctions.setLogin(false);

                    basicFunctions.setUserDataNull();

                    Intent Logout = new Intent(HomePage.this, Login.class);
                    startActivity(Logout);

                    break;

                case "Logout Failed !":

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