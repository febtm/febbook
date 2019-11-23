package fmt.febulous;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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
import fmt.febulous.helper.Menu;
import fmt.febulous.model.ChatRoom;


public class Messenger extends AppCompatActivity {


    private ArrayList<ChatRoom> chatRoomArrayList = new ArrayList<>();

    private ChatMainAdapter mAdapter;

    RecyclerView recyclerView;

    private BasicFunctions basicFunctions;

    private ProgressDialog pDialog;

    private int load_over = 0;


    ImageButton MENU_BUTTON;

    private Menu menu;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messenger);

        menu = new Menu(Messenger.this);

        basicFunctions = new BasicFunctions(this);

        AdView mAdView = findViewById(R.id.mes_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        MENU_BUTTON = findViewById(R.id.mes_menu);

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.LeftDrawer.toggleLeftDrawer();

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.mes_recycler_view);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ChatMainAdapter(this);

        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        int index = chatRoomArrayList.size();

                        getMoreChatRooms(index);

                        mAdapter.notifyDataSetChanged();
                        mAdapter.setLoaded();

                    }
                }, 1000);
            }
        });

        if(basicFunctions.isConnectingToInternet())
            fetchChat();

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){

                        case DialogInterface.BUTTON_POSITIVE:

                            if(basicFunctions.isConnectingToInternet())
                                fetchChat();

                            else {

                                Toast.makeText(Messenger.this,
                                        "No Internet Connection. Try again later !",
                                        Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            Toast.makeText(Messenger.this,
                                    "No Internet Connection. Try again later !",
                                    Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(Messenger.this);
            builder.setMessage("No Internet Connection. Try again ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }

    }


    private void fetchChat() {

        pDialog = ProgressDialog.show(Messenger.this, "", "Fetching Chat List ... ", false, false);

        getInitialChatRooms();

    }


    private void getInitialChatRooms() {

        StringRequest strReq = new StringRequest(Request.Method.GET,
                BasicFunctions.GET_ALL_CHAT_ROOMS + basicFunctions.getUser_id() + "&index=" + 0, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");

                    if(chatRoomsArray.length() <= 0) {

                        Toast.makeText(Messenger.this, "Your Chat List is empty !", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();

                    }

                    for (int i = 0; i < chatRoomsArray.length(); i++) {

                        JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                        ChatRoom cr = new ChatRoom();

                        cr.setId(chatRoomsObj.getString("chat_room_id"));
                        cr.setUser1_id(chatRoomsObj.getString("user1_id"));
                        cr.setUser1_name(chatRoomsObj.getString("user1_name"));
                        cr.setUser1_image(chatRoomsObj.getString("user1_image"));
                        cr.setUser1_online(chatRoomsObj.getString("user1_online"));
                        cr.setUser1_read(chatRoomsObj.getString("user1_read"));

                        cr.setUser2_id(chatRoomsObj.getString("user2_id"));
                        cr.setUser2_name(chatRoomsObj.getString("user2_name"));
                        cr.setUser2_image(chatRoomsObj.getString("user2_image"));
                        cr.setUser2_online(chatRoomsObj.getString("user2_online"));
                        cr.setUser2_read(chatRoomsObj.getString("user2_read"));

                        cr.setTimestamp(chatRoomsObj.getString("created_at"));

                        chatRoomArrayList.add(cr);

                        if(i == chatRoomsArray.length() - 1)
                            pDialog.dismiss();

                    }

                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Json parse error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401)
                    Toast.makeText(getApplicationContext(), "Volley error : " + error.getMessage() + ", Code : " + networkResponse, Toast.LENGTH_LONG).show();
            }
        });

        strReq.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        basicFunctions.addToRequestQueue(strReq);

    }


    private interface OnLoadMoreListener {
        void onLoadMore();
    }


    private class ChatMainAdapter extends RecyclerView.Adapter<ViewHolder> {

        Context mContext;

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;


        private ChatMainAdapter(Context mContext) {

            this.mContext = mContext;

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

        private void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        private void setLoaded() {
            isLoading = false;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_messenger_list_row, parent, false);

            return new ViewHolder(itemView);
        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            ChatRoom chatRoom = chatRoomArrayList.get(position);

            if(basicFunctions.getUser_name().equals(chatRoom.getUser1_name())) {

                holder.username.setText(chatRoom.getUser2_name());

                if(chatRoom.getUser2_online().equals("1"))
                    holder.user_online.setVisibility(View.VISIBLE);

                else
                    holder.user_online.setVisibility(View.GONE);

                if(chatRoom.getUser1_read().equals("1"))
                    holder.user_read.setVisibility(View.VISIBLE);

                else
                    holder.user_read.setVisibility(View.GONE);

                if(!chatRoom.getUser2_image().equals("")) {

                    holder.user_image.setBackground(null);
                    byte[] decodedString_1 = Base64.decode(chatRoom.getUser2_image(), Base64.DEFAULT);
                    Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

                    holder.user_image.setImageBitmap(decodedByte_1);

                }

                else {

                    holder.user_image.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.me_profile, null));
                    holder.user_image.setImageResource(R.drawable.app_userbackground);

                }

            }

            else {

                holder.username.setText(chatRoom.getUser1_name());

                if(chatRoom.getUser1_online().equals("1"))
                    holder.user_online.setVisibility(View.VISIBLE);

                else
                    holder.user_online.setVisibility(View.GONE);

                if(chatRoom.getUser2_read().equals("1"))
                    holder.user_read.setVisibility(View.VISIBLE);

                else
                    holder.user_read.setVisibility(View.GONE);

                if(!chatRoom.getUser1_image().equals("")) {

                    holder.user_image.setBackground(null);
                    byte[] decodedString_1 = Base64.decode(chatRoom.getUser1_image(), Base64.DEFAULT);
                    Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);

                    holder.user_image.setImageBitmap(decodedByte_1);

                }

                else {

                    holder.user_image.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.me_profile, null));
                    holder.user_image.setImageResource(R.drawable.app_userbackground);

                }

            }

            holder.timestamp.setText(BasicFunctions.getTimeStamp(chatRoom.getTimestamp()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ChatRoom chatRoom = chatRoomArrayList.get(position);

                    Intent intent = new Intent(Messenger.this, MessengerItem.class);

                    intent.putExtra("chat_room_id", chatRoom.getId());

                    if (chatRoom.getUser1_name().equals(basicFunctions.getUser_name())) {

                        intent.putExtra("username", chatRoom.getUser2_name());
                        intent.putExtra("userid", chatRoom.getUser2_id());
                        intent.putExtra("userimage", chatRoom.getUser2_image());

                    } else {

                        intent.putExtra("username", chatRoom.getUser1_name());
                        intent.putExtra("userid", chatRoom.getUser1_id());
                        intent.putExtra("userimage", chatRoom.getUser1_image());

                    }

                    startActivity(intent);

                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which){

                                case DialogInterface.BUTTON_POSITIVE:

                                    ChatRoom chatRoom = chatRoomArrayList.get(position);

                                    String method = "chat";
                                    DeleteChatBackgroundTask deleteChatBackgroundTask = new DeleteChatBackgroundTask(Messenger.this);
                                    deleteChatBackgroundTask.execute(method, chatRoom.getId(), basicFunctions.getUser_id());

                                    chatRoomArrayList.remove(chatRoom);

                                    Toast.makeText(Messenger.this, "Chat removed from Chat List !", Toast.LENGTH_LONG).show();

                                    mAdapter.notifyDataSetChanged();


                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    Toast.makeText(Messenger.this,"Deletion Cancelled !",Toast.LENGTH_LONG).show();

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(Messenger.this);
                    builder.setMessage("Are you sure you want to delete this Chat ?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            });

        }


        @Override
        public int getItemCount() {
            return chatRoomArrayList.size();
        }


    }


    private void getMoreChatRooms(int index) {

        pDialog = ProgressDialog.show(Messenger.this, "", "Fetching Chat List ... ", false, false);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                BasicFunctions.GET_ALL_CHAT_ROOMS + basicFunctions.getUser_id() + "&index=" + index, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");

                    if(chatRoomsArray.length() == 0) {

                        Toast.makeText(Messenger.this, "No more Chats to display !", Toast.LENGTH_LONG).show();
                        load_over = 1;
                        pDialog.dismiss();

                    }

                    for (int i = 0; i < chatRoomsArray.length(); i++) {

                        JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                        ChatRoom cr = new ChatRoom();

                        cr.setId(chatRoomsObj.getString("chat_room_id"));
                        cr.setUser1_id(chatRoomsObj.getString("user1_id"));
                        cr.setUser1_name(chatRoomsObj.getString("user1_name"));
                        cr.setUser1_image(chatRoomsObj.getString("user1_image"));
                        cr.setUser1_online(chatRoomsObj.getString("user1_online"));
                        cr.setUser1_read(chatRoomsObj.getString("user1_read"));

                        cr.setUser2_id(chatRoomsObj.getString("user2_id"));
                        cr.setUser2_name(chatRoomsObj.getString("user2_name"));
                        cr.setUser2_image(chatRoomsObj.getString("user2_image"));
                        cr.setUser2_online(chatRoomsObj.getString("user2_online"));
                        cr.setUser2_read(chatRoomsObj.getString("user2_read"));

                        cr.setTimestamp(chatRoomsObj.getString("created_at"));

                        chatRoomArrayList.add(cr);

                        if(i == chatRoomsArray.length() - 1)
                            pDialog.dismiss();

                    }

                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Json parse error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401)
                    Toast.makeText(getApplicationContext(), "Volley error : " + error.getMessage() + ", Code : " + networkResponse, Toast.LENGTH_LONG).show();
            }
        });

        strReq.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        basicFunctions.addToRequestQueue(strReq);

    }


    @SuppressLint("StaticFieldLeak")
    private class DeleteChatBackgroundTask extends AsyncTask<String, Void, String> {

        Context ctx;

        private ProgressDialog pDialog;

        DeleteChatBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }


        @Override
        public void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Messenger.this);
            pDialog.setMessage("Removing Chat ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String method = params[0];

            if (method.equals("chat")) {

                String chat_room_id = params[1];
                String user_id = params[2];

                try {

                    URL url = new URL(BasicFunctions.DELETE_CHAT);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("chat_room_id", "UTF-8") + "=" + URLEncoder.encode(chat_room_id, "UTF-8")
                            + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

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

                case "Chat removed from Chat List !":

                    pDialog.dismiss();

                    if(chatRoomArrayList.size() == 0)
                        Toast.makeText(Messenger.this, "Your Chat List is empty !", Toast.LENGTH_LONG).show();

                    break;

                default:

                    Toast.makeText(Messenger.this, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                    break;

            }

        }
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, timestamp;
        private ImageButton delete;
        private ImageView user_online, user_read, user_image;

        private ViewHolder(View view) {
            super(view);

            username = view.findViewById(R.id.mes_i_username);
            user_image = view.findViewById(R.id.mes_i_userimage);
            timestamp = view.findViewById(R.id.mes_i_timestamp);
            user_read = view.findViewById(R.id.mes_i_userread);
            user_online = view.findViewById(R.id.mes_i_useronline);
            delete = view.findViewById(R.id.mes_i_deletebutton);

        }
    }

}