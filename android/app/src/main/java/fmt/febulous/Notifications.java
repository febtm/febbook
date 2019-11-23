package fmt.febulous;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fmt.febulous.helper.BasicFunctions;
import fmt.febulous.helper.EndPoints;
import fmt.febulous.model.Notification;


public class Notifications extends AppCompatActivity {


    JSONArray NotificationArray = null;

    private List<Notification> mNotifications = new ArrayList<>();

    private RecyclerView recyclerView;

    String actionbar_title;

    public static int flag, temp;

    private int load_over = 0;

    private NotificationFragmentAdapter notificationFragmentAdapter;

    private ProgressDialog pDialog;

    private BasicFunctions basicFunctions;

    private Notification notification;

    private static int array_position[];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_fragment_list);

        actionbar_title="\tNOTIFICATIONS";
        setTitle(actionbar_title);

        basicFunctions = new BasicFunctions(this);

        notification = new Notification();


        if(basicFunctions.isConnectingToInternet())
            getInitialNotificationData();

        else{

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(Notifications.this, Notifications.class);
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



    private void getInitialNotificationData() {

        pDialog = ProgressDialog.show(this, "", "Fetching Notifications ... ", false, false);

        StringRequest stringRequest = new StringRequest(EndPoints.GET_ALL_NOTIFICATIONS+basicFunctions.getUser_id()+"&index="+0, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_InitialNotifications(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Notifications.this, error.getMessage(), Toast.LENGTH_LONG).show();
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



    protected void showList_InitialNotifications(String response) {

        String an_id;
        String an_chat_id ;
        String an_post_userread;
        String an_notify_userid ;
        String an_notify_username;
        String an_notify_userimage;
        String an_timestamp;

        try {
            JSONObject jsonObj = new JSONObject(response);
            NotificationArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            notification.setTotal(0);

            for (int i = 0; i < NotificationArray.length(); i++) {

                JSONObject jsonObject = NotificationArray.getJSONObject(i);

                an_id = jsonObject.getString(basicFunctions.KEY_POST_ID);
                an_chat_id = jsonObject.getString(basicFunctions.KEY_CHAT_ROOM_ID);
                an_post_userread = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_POST_USERREAD);
                an_notify_userid = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USER_ID);
                an_notify_username = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USERNAME);
                an_notify_userimage = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USERPICTURE);
                an_timestamp = jsonObject.getString(basicFunctions.KEY_POST_TIMESTAMP);

                Notification data = new Notification();

                data.setNotDetails(an_id, an_chat_id, an_post_userread, an_notify_userid,
                        an_notify_username, an_notify_userimage, an_timestamp);

                mNotifications.add(data);

                data.setTotal(mNotifications.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(notification.getTotal() == 0)
            Toast.makeText(Notifications.this, "Your Notification List is empty !", Toast.LENGTH_LONG).show();


        recyclerView = (RecyclerView) findViewById(R.id.no_frag_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationFragmentAdapter = new NotificationFragmentAdapter(this);
        recyclerView.setAdapter(notificationFragmentAdapter);


        notificationFragmentAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                mNotifications.add(null);
                notificationFragmentAdapter.notifyItemInserted(mNotifications.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pDialog = ProgressDialog.show(Notifications.this, "", "Fetching Notifications ... ", false, false);

                        mNotifications.remove(mNotifications.size() - 1);
                        notificationFragmentAdapter.notifyItemRemoved(mNotifications.size());

                        int index = mNotifications.size();

                        getMoreNotificationData(index);

                        notificationFragmentAdapter.notifyDataSetChanged();
                        notificationFragmentAdapter.setLoaded();

                    }
                }, 1000);
            }
        });

        pDialog.dismiss();

    }


    private void getMoreNotificationData(int index) {

        StringRequest stringRequest = new StringRequest(EndPoints.GET_ALL_NOTIFICATIONS+basicFunctions.getUser_id()+"&index="+index, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_MoreNotifications(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Notifications.this, error.getMessage(), Toast.LENGTH_LONG).show();
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



    protected void showList_MoreNotifications(String response) {

        String an_id;
        String an_chat_id;
        String an_post_userread;
        String an_notify_userid;
        String an_notify_username;
        String an_notify_userimage;
        String an_timestamp;

        try {
            JSONObject jsonObj = new JSONObject(response);
            NotificationArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            if(NotificationArray.length() == 0) {

                Toast.makeText(this, "No more Notifications to display !", Toast.LENGTH_LONG).show();
                load_over = 1;
            }

            for (int i = 0; i < NotificationArray.length(); i++) {

                JSONObject jsonObject = NotificationArray.getJSONObject(i);

                an_id = jsonObject.getString(basicFunctions.KEY_POST_ID);
                an_chat_id = jsonObject.getString(basicFunctions.KEY_CHAT_ROOM_ID);
                an_post_userread = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_POST_USERREAD);
                an_notify_userid = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USER_ID);
                an_notify_username = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USERNAME);
                an_notify_userimage = jsonObject.getString(basicFunctions.KEY_NOTIFICATION_USERPICTURE);
                an_timestamp = jsonObject.getString(basicFunctions.KEY_POST_TIMESTAMP);

                Notification data = new Notification();

                data.setNotDetails(an_id, an_chat_id, an_post_userread, an_notify_userid,
                        an_notify_username, an_notify_userimage, an_timestamp);

                mNotifications.add(data);

                data.setTotal(mNotifications.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        notificationFragmentAdapter.notifyDataSetChanged();

        pDialog.dismiss();

    }



    class NotificationFragmentAdapter extends RecyclerView.Adapter<ViewHolder> {

        private LayoutInflater mLayoutInflater;
        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;


        public NotificationFragmentAdapter(Context context) {

            mLayoutInflater = LayoutInflater.from(context);

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && load_over == 0) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.activity_notifications_fragment_item, viewGroup, false));
        }


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            final Notification data = mNotifications.get(position);

            viewHolder.setData(data.getChat_id(), data.getPost_userread(), data.getNotify_username(),
                    data.getNotify_userimage(), data.getTimestamp(), position);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (flag == 0)
                            onNotificationFragmentItemSelected(data.getId(), data.getChat_id(),
                                    data.getNotify_userid(), data.getNotify_username());

                        else {

                            if (array_position[position] == 0) {

                                if (data.getChat_id().equals("66666666"))
                                    Toast.makeText(Notifications.this, "Event Notification cannot be removed !", Toast.LENGTH_LONG).show();

                                else {
                                    array_position[position] = 1;
                                    viewHolder.delete.setText(null);
                                    viewHolder.delete.setBackgroundResource(R.drawable.app_delete_on);
                                }
                            } else {
                                array_position[position] = 0;
                                viewHolder.delete.setText(null);
                                viewHolder.delete.setBackgroundResource(R.drawable.app_delete_off);
                            }
                        }
                    }
                });

        }

        @Override
        public int getItemCount() {

            return notification.getTotal();

        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }


        public void setLoaded() {
            isLoading = false;
        }

    }


    private interface OnLoadMoreListener {
        void onLoadMore();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextTextView, mDateTextView;
        private ImageView mUserreadImageView;
        private CircularImageView mImageView;
        private ToggleButton delete;


        private ViewHolder(View itemView) {
            super(itemView);

            mTextTextView = (TextView) itemView.findViewById(R.id.no_item_text);
            mUserreadImageView = (ImageView) itemView.findViewById(R.id.no_item_read);
            mImageView = (CircularImageView) itemView.findViewById(R.id.no_item_image);
            mDateTextView = (TextView) itemView.findViewById(R.id.no_item_date);
            delete = (ToggleButton) itemView.findViewById(R.id.no_deletebutton);
            delete.setText(null);

        }

        private void setData(String chat_id, String post_userread, String notify_username,
                             String notify_userimage, String timestamp, final int position) {

            if(post_userread.equals("1"))
                mUserreadImageView.setVisibility(View.VISIBLE);

            else
                mUserreadImageView.setVisibility(View.GONE);

            String temp_timestamp = basicFunctions.getTimeStamp(timestamp);

            delete.setText(null);

            if(!notify_userimage.equals("")) {

                byte[] decodedString = Base64.decode(notify_userimage, Base64.DEFAULT);
                Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                mImageView.setBackground(null);
                mImageView.setImageBitmap(Bitmap.createScaledBitmap(bm, 60, 60, false));

            }

            else {

                mImageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_userimage, null));
                mImageView.setImageResource(R.drawable.app_userbackground);

            }

            mDateTextView.setText(temp_timestamp);

            if (flag == 1)  delete.setVisibility(View.VISIBLE);

            else  delete.setVisibility(View.GONE);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (delete.getText().equals("OFF")) {
                        delete.setText(null);
                        delete.setBackgroundResource(R.drawable.app_delete_off);
                        array_position[position] = 0;
                    } else {
                        delete.setText(null);
                        delete.setBackgroundResource(R.drawable.app_delete_on);
                        array_position[position] = 1;
                    }

                }
            });

            String text;

            switch (chat_id){

                case "99999999":

                    text = notify_username + " has commented on your Post !";
                    mTextTextView.setText(text);

                    break;

                case "88888888":

                    text = notify_username + " has liked your Post !";
                    mTextTextView.setText(text);

                    break;

                case "77777777":

                    text = notify_username + " has disliked your Post !";
                    mTextTextView.setText(text);

                    break;

                case "66666666":

                    text = notify_username + " has posted an Event in your Course !";
                    mTextTextView.setText(text);

                    break;

                case "55555555":

                    text = notify_username + " has sent you a Chat request !";
                    mTextTextView.setText(text);

                    break;


                case "55555551":

                    text = notify_username + " has accepted your Chat request !";
                    mTextTextView.setText(text);

                    break;

                case "55555552":

                    text = notify_username + " has rejected your Chat request !";
                    mTextTextView.setText(text);

                    break;

                default:

                    text = notify_username + " has sent you a Message !";
                    mTextTextView.setText(text);

                    break;

            }

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        if(id == R.id.delete)

            if(notificationFragmentAdapter.getItemCount()!=0) {

                menuItem.setChecked(!menuItem.isChecked());

                if (menuItem.isChecked()) {

                    menuItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.messages_next, null));

                    flag = 1;

                    array_position = new int[notificationFragmentAdapter.getItemCount()];

                    for(int i = 0; i < notificationFragmentAdapter.getItemCount(); i++)
                        array_position[i]=0;

                    notificationFragmentAdapter.notifyDataSetChanged();

                }

                else {

                    menuItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.messages_delete, null));

                    flag = 0;

                    temp = 0;

                    for(int i = 0 ; i < array_position.length ; i++)
                        if(array_position[i] == 1){

                            Notification data = mNotifications.get(i - temp);

                            String method = "deletenotification";
                            DeleteNotificationBackgroundTask nb = new DeleteNotificationBackgroundTask(Notifications.this);
                            nb.execute(method, data.getId(), data.getChat_id(),
                                    basicFunctions.getUser_id(), data.getNotify_userid());

                            data.deleteNot();

                            mNotifications.remove(i - temp);

                            temp++;
                        }

                    if(temp == 1)
                        Toast.makeText(Notifications.this, "Notification removed from Notification List !", Toast.LENGTH_LONG).show();

                    else if(temp > 1)
                        Toast.makeText(Notifications.this, "Multiple Notifications removed from Notification List !", Toast.LENGTH_LONG).show();

                    notificationFragmentAdapter.notifyDataSetChanged();
                }
            }

            else
                Toast.makeText(Notifications.this, "Your Notification List is empty !", Toast.LENGTH_LONG).show();

        return super.onOptionsItemSelected(menuItem);
    }


    private void onNotificationFragmentItemSelected(String id, String chat_id,
                                                   String notify_user_id, String notify_username) {

        String method = "readnotification";

        basicFunctions.performTask(method, id, notify_user_id);

        switch (chat_id){

            case "99999999":

                getPostData(id);
                break;

            case "88888888":

                getPostData(id);
                break;

            case "77777777":

                getPostData(id);
                break;


            case "66666666":

                getPostData(id);
                break;


            case "55555555":

                dbchat("request", basicFunctions.getUser_id(), notify_username, notify_user_id);
                break;


            case "55555551":

                dbchat("chat", basicFunctions.getUser_id(), notify_username, notify_user_id);
                break;


            case "55555552":

                Toast.makeText(this, notify_username+" has rejected Your Chat request !", Toast.LENGTH_LONG).show();
                break;


            default:

                if(basicFunctions.isConnectingToInternet())
                    login(chat_id, notify_username, notify_user_id);

                else{

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:

                                    Intent intent = new Intent(Notifications.this, HomePage.class);
                                    startActivity(intent);

                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Network Failure : Please check Your Internet Connection !")
                            .setPositiveButton("Try Again ... ", dialogClickListener).show();

                }

                break;
        }

    }


    private void dbchat(String method, final String current_user_id, final String username, final String user_id) {

        if(basicFunctions.isConnectingToInternet())

            if(method.equals("request")) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                ChatBackgroundTask backgroundTask = new ChatBackgroundTask(Notifications.this);

                                backgroundTask.execute("chat_accept", current_user_id, username, user_id);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                ChatBackgroundTask backgroundTask_2 = new ChatBackgroundTask(Notifications.this);

                                backgroundTask_2.execute("chat_reject", current_user_id, username, user_id);

                                break;
                        }
                    }


                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Notifications.this);
                builder.setMessage("Would you like to accept this User's Chat request ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }

            else if(method.equals("chat")){

                ChatBackgroundTask backgroundTask = new ChatBackgroundTask(Notifications.this);

                backgroundTask.execute("chat_accept", current_user_id, username, user_id);
            }


            else{

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                Intent intent = new Intent(Notifications.this, HomePage.class);
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


    private class ChatBackgroundTask extends AsyncTask<String, Void, String> {


        Context ctx;
        String data;

        private String method, current_user_id, username, user_id;

        ChatBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            method = params[0];
            current_user_id = params[1];
            username = params[2];
            user_id = params[3];


            try {

                URL url = new URL(EndPoints.ADD_CHAT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));


                data = URLEncoder.encode("method", "UTF-8") + "=" + URLEncoder.encode(method, "UTF-8")
                        + "&" + URLEncoder.encode("current_user_id", "UTF-8") + "=" + URLEncoder.encode(current_user_id, "UTF-8")
                        + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

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

            return null;

        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(String result) {

            if(result.equals("The Chat request has been rejected !"))
                Toast.makeText(Notifications.this, result,  Toast.LENGTH_LONG).show();

            else if(result.equals("The Chat request has been accepted !") && method.equals("chat_accept")) {

                Toast.makeText(Notifications.this, result, Toast.LENGTH_LONG).show();
                login(result, username, user_id);

            }
            else if(method.equals("chat_accept"))
                login(result, username, user_id);


        }

    }


    private void getPostData(String id) {

        Intent intent = new Intent(Notifications.this, PostView.class);
        intent.putExtra("id", id);
        startActivity(intent);

    }



    private void login(final String chat_id, final String username, final String user_id) {

        basicFunctions.login();

        Intent intent = new Intent(Notifications.this, MessengerItem.class);
        intent.putExtra("chat_room_id", chat_id);
        intent.putExtra("userid", user_id);
        intent.putExtra("username", username);

        startActivity(intent);

    }


    private class DeleteNotificationBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;

        private ProgressDialog pDialog;

        DeleteNotificationBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }


        @Override
        public void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Notifications.this);
            pDialog.setMessage("Removing Notification ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("deletenotification")) {

                String id = params[1];
                String chat_room_id = params[2];
                String user_id = params[3];
                String notify_user_id = params[4];


                try {

                    URL url = new URL(EndPoints.DELETE_NOTIFICATION);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));


                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                            + "&" + URLEncoder.encode("chat_room_id", "UTF-8") + "=" + URLEncoder.encode(chat_room_id, "UTF-8")
                            + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                            + "&" + URLEncoder.encode("notify_user_id", "UTF-8") + "=" + URLEncoder.encode(notify_user_id, "UTF-8");


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

            if(result.equals("Notification removed from Notification List !"))
                pDialog.dismiss();

        }
    }

}