package fmt.febulous;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fmt.febulous.helper.Application;
import fmt.febulous.model.ChatMessage;
import fmt.febulous.model.ChatUser;
import fmt.febulous.helper.EndPoints;
import fmt.febulous.helper.BasicFunctions;


public class MessengerItem extends AppCompatActivity {


    private RecyclerView recyclerView;

    private BasicFunctions basicFunctions;

    ProgressDialog pDialog;

    private ChatRoomAdapter mAdapter;

    private ArrayList<ChatMessage> chatMessageArrayList = new ArrayList<>();

    private EditText inputMessage;

    Button btnSend;

    String n_chat_room_id, n_userid, n_username;

    String final_message_id;

    int mInterval = 5000;

    private Handler mHandler;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                getNewChatThread();

            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_item);

        basicFunctions = new BasicFunctions(this);

        inputMessage = (EditText) findViewById(R.id.message);

        btnSend = (Button) findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selfUserId = basicFunctions.getUser_id();

        mAdapter = new ChatRoomAdapter(this, selfUserId);

        recyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();

        n_chat_room_id = intent.getStringExtra("chat_room_id");
        n_userid = intent.getStringExtra("userid");
        n_username = intent.getStringExtra("username");

        setTitle("\t" + n_username.toUpperCase());

        if (n_chat_room_id == null) {

            Toast.makeText(getApplicationContext(), "Chat not found !", Toast.LENGTH_LONG).show();
            finish();

        }

        String method = "readchat";

        basicFunctions.performTask(method, n_chat_room_id, n_userid);

        getInitialChatThread();

    }


    void startRepeatingTask() {
        mStatusChecker.run();
    }


    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }


    private void getInitialChatThread() {

        pDialog = ProgressDialog.show(this, "", "Fetching Chat ... ", false, false);

        String endPoint = EndPoints.GET_CHAT_ROOM + n_chat_room_id + "&index=" + 0 + "&type=old";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray commentsObj = obj.getJSONArray("messages");

                    if(commentsObj.length() == 0) {

                        Toast.makeText(MessengerItem.this, "No Messages to display !", Toast.LENGTH_LONG).show();

                        final_message_id = "0";

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                mHandler = new Handler();
                                startRepeatingTask();

                            }
                        }, 0);

                        pDialog.dismiss();

                    }

                    for (int i = 0; i < commentsObj.length(); i++) {

                        JSONObject commentObj = (JSONObject) commentsObj.get(i);

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = commentObj.getJSONObject("user");
                        String userId = userObj.getString("user_id");
                        String userName = userObj.getString("username");
                        String userimage = userObj.getString("userimage");

                        final_message_id = obj.getJSONObject("final_message_id").getString("final_message_id");

                        ChatUser chatUser = new ChatUser(userId, userName, userimage);

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setId(commentId);
                        chatMessage.setMessage(commentText);
                        chatMessage.setCreatedAt(createdAt);
                        chatMessage.setChatUser(chatUser);

                        chatMessageArrayList.add(chatMessage);

                        if(i == (commentsObj.length() - 1)) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    mHandler = new Handler();
                                    startRepeatingTask();

                                }
                            }, mInterval);

                            pDialog.dismiss();

                        }

                        mAdapter.notifyDataSetChanged();

                        if (mAdapter.getItemCount() > 1)
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                    }

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

        Application.getInstance().addToRequestQueue(strReq);

    }


    private void getNewChatThread() {

        String endPoint = EndPoints.GET_CHAT_ROOM + n_chat_room_id + "&index=" + final_message_id + "&type=new";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray commentsObj = obj.getJSONArray("messages");

                    for (int i = 0; i < commentsObj.length(); i++) {

                        JSONObject commentObj = (JSONObject) commentsObj.get(i);

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = commentObj.getJSONObject("user");
                        String userId = userObj.getString("user_id");
                        String userName = userObj.getString("username");
                        String userimage = userObj.getString("userimage");

                        final_message_id = obj.getJSONObject("final_message_id").getString("final_message_id");

                        ChatUser chatUser = new ChatUser(userId, userName, userimage);

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setId(commentId);
                        chatMessage.setMessage(commentText);
                        chatMessage.setCreatedAt(createdAt);
                        chatMessage.setChatUser(chatUser);

                        chatMessageArrayList.add(chatMessage);

                        if(commentsObj.length() == 1)
                            playMessengerSound();

                        mAdapter.notifyDataSetChanged();

                        if(mAdapter.getItemCount() > 1)
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                    }

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

        Application.getInstance().addToRequestQueue(strReq);

    }


    private void sendMessage() {

        final String message = this.inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {

            Toast.makeText(getApplicationContext(), "Enter your Message !", Toast.LENGTH_LONG).show();
            return;

        }

        String method = "notification";

        basicFunctions.performTask(method, n_chat_room_id, n_userid);

        String endPoint = EndPoints.SEND_MESSAGE + n_chat_room_id + "&user_id=" + basicFunctions.getUser_id() + "&message=" + message;

        endPoint = endPoint.replaceAll(" ", "%20");

        this.inputMessage.setText("");

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONObject commentObj = obj.getJSONObject("message");

                    String commentId = commentObj.getString("message_id");
                    String commentText = commentObj.getString("message");
                    String createdAt = commentObj.getString("created_at");

                    JSONObject userObj = obj.getJSONObject("user");
                    String userId = userObj.getString("user_id");
                    String userName = userObj.getString("username");
                    String userimage = userObj.getString("userimage");

                    final_message_id = obj.getJSONObject("final_message_id").getString("final_message_id");

                    ChatUser chatUser = new ChatUser(userId, userName,userimage);

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(commentId);
                    chatMessage.setMessage(commentText);
                    chatMessage.setCreatedAt(createdAt);
                    chatMessage.setChatUser(chatUser);

                    chatMessageArrayList.add(chatMessage);

                    mAdapter.notifyDataSetChanged();

                    if(mAdapter.getItemCount() > 1)
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Json parse error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage() + ", Code: " + networkResponse, Toast.LENGTH_LONG).show();
                inputMessage.setText(message);
            }
        });

        strReq.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Application.getInstance().addToRequestQueue(strReq);

    }


    private void playMessengerSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + Application.getInstance().getApplicationContext().getPackageName() + "/raw/messenger_sound");
            Ringtone r = RingtoneManager.getRingtone(Application.getInstance().getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getOldChatThread() {

        pDialog = ProgressDialog.show(this, "", "Fetching Chat ... ", false, false);

        String endPoint = EndPoints.GET_CHAT_ROOM + n_chat_room_id + "&index=" + chatMessageArrayList.size() + "&type=old";

        final ArrayList<ChatMessage> chatTempMessageArrayList = new ArrayList<>();

        for(int i = 0; i < chatMessageArrayList.size(); i++)
            chatTempMessageArrayList.add(chatMessageArrayList.get(i));

        chatMessageArrayList.clear();


        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    JSONArray commentsObj = obj.getJSONArray("messages");

                    if(commentsObj.length() == 0) {

                        Toast.makeText(MessengerItem.this, "No More Old Messages to display !", Toast.LENGTH_LONG).show();

                        for(int j = 0; j < chatTempMessageArrayList.size(); j++)
                            chatMessageArrayList.add(chatTempMessageArrayList.get(j));

                        mAdapter.notifyDataSetChanged();

                        pDialog.dismiss();
                    }

                    for (int i = 0; i < commentsObj.length(); i++) {

                        JSONObject commentObj = (JSONObject) commentsObj.get(i);

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = commentObj.getJSONObject("user");
                        String userId = userObj.getString("user_id");
                        String userName = userObj.getString("username");
                        String userimage = userObj.getString("userimage");

                        final_message_id = obj.getJSONObject("final_message_id").getString("final_message_id");

                        ChatUser chatUser = new ChatUser(userId, userName, userimage);

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setId(commentId);
                        chatMessage.setMessage(commentText);
                        chatMessage.setCreatedAt(createdAt);
                        chatMessage.setChatUser(chatUser);

                        chatMessageArrayList.add(chatMessage);

                        if(i == (commentsObj.length() - 1)){

                            for(int j = 0; j < chatTempMessageArrayList.size(); j++)
                                chatMessageArrayList.add(chatTempMessageArrayList.get(j));

                            mAdapter.notifyDataSetChanged();

                            pDialog.dismiss();

                        }

                    }

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

        Application.getInstance().addToRequestQueue(strReq);

    }



    private class ChatRoomAdapter extends RecyclerView.Adapter<ViewHolder> {

        private String userId;

        private int SELF = 100;

        Context mContext;

        private ChatRoomAdapter(Context mContext, String userId) {

            this.mContext = mContext;
            this.userId = userId;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;

            if (viewType == SELF) {

                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_messenger_item_self, parent, false);

            } else {

                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_messenger_item_other, parent, false);

            }

            return new ViewHolder(itemView);
        }

        @Override
        public int getItemViewType(int position) {

            ChatMessage chatMessage = chatMessageArrayList.get(position);

            if (chatMessage.getChatUser().getId().equals(userId)) {

                return SELF;

            }

            return position;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            ChatMessage chatMessage = chatMessageArrayList.get(position);

            holder.message.setText(chatMessage.getMessage());

            String timestamp = basicFunctions.getTimeStamp(chatMessage.getCreatedAt());

            if(basicFunctions.getUser_name().equals(chatMessage.getChatUser().getUser_name())) {

                timestamp = "You, " + timestamp;

                if (!basicFunctions.getUser_image().equals("")) {

                    holder.user_message_image.setBackground(null);
                    byte[] decodedString = Base64.decode(basicFunctions.getUser_image(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.user_message_image.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 60, 60, false));

                } else {

                    holder.user_message_image.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.app_userimage, null));
                    holder.user_message_image.setImageResource(R.drawable.app_userbackground);
                }
            }

            else {

                timestamp = chatMessage.getChatUser().getUser_name() + ", " + timestamp;

                if (!chatMessage.getChatUser().getUser_image().equals("")) {

                    holder.user_message_image.setBackground(null);
                    byte[] decodedString = Base64.decode(chatMessage.getChatUser().getUser_image(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    holder.user_message_image.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 60, 60, false));

                } else {

                    holder.user_message_image.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.app_userimage, null));
                    holder.user_message_image.setImageResource(R.drawable.app_userbackground);

                }

            }

            if(position == 0)
                holder.fetch_old.setVisibility(View.VISIBLE);

            holder.fetch_old.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getOldChatThread();

                }
            });

            holder.timestamp.setText(timestamp);

        }

        @Override
        public int getItemCount() {
            return chatMessageArrayList.size();
        }

    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, timestamp;
        CircularImageView user_message_image;
        Button fetch_old;

        private ViewHolder(View view) {
            super(view);

            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            user_message_image = (CircularImageView)view.findViewById(R.id.chat_userimage);
            fetch_old = (Button) view.findViewById(R.id.fetch_old);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if(mHandler != null)
                    stopRepeatingTask();

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}