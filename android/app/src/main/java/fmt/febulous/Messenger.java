package fmt.febulous;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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

import fmt.febulous.helper.Application;
import fmt.febulous.helper.BasicFunctions;
import fmt.febulous.helper.EndPoints;
import fmt.febulous.model.ChatRoom;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pkmmte.view.CircularImageView;


public class Messenger extends AppCompatActivity {


    private ArrayList<ChatRoom> chatRoomArrayList = new ArrayList<>();

    private ChatMainAdapter mAdapter;

    RecyclerView recyclerView;

    private BasicFunctions basicFunctions;

    private ProgressDialog pDialog;

    private static int flag;

    static int temp;

    private int load_over = 0;

    private static int array_position[];



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messenger);

        basicFunctions = new BasicFunctions(this);

        setTitle("\tFEBULOUS MESSENGER");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new ChatDividerItem(
                getApplicationContext()
        ));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ImageView line;

        line = (ImageView) findViewById(R.id.linedivider);

        line.setVisibility(View.GONE);

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

        if (basicFunctions.isConnectingToInternet()) {

            pDialog = ProgressDialog.show(Messenger.this, "", "Fetching Chat List ... ", false, false);

            basicFunctions.login();

            getInitialChatRooms();


        } else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(Messenger.this, Messenger.class);
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


    private void getInitialChatRooms() {

        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.CHAT_ROOMS + "/" + basicFunctions.getUser_id() + "/" + 0, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (!obj.getBoolean("error")) {

                        JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");

                        if(chatRoomsArray.length() > 0) {

                            ImageView line;
                            line = (ImageView) findViewById(R.id.linedivider);
                            line.setVisibility(View.VISIBLE);

                        }

                        else {

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

                            cr.setTotal(chatRoomArrayList.size());

                            if(i == chatRoomsArray.length() - 1)
                                pDialog.dismiss();

                        }

                        flag = 0;

                        array_position = new int[mAdapter.getItemCount()];

                        for (int i = 0; i < mAdapter.getItemCount(); i++)
                            array_position[i] = 0;

                        mAdapter.notifyDataSetChanged();

                    } else
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();

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


    private interface OnLoadMoreListener {
        void onLoadMore();
    }


    class ChatMainAdapter extends RecyclerView.Adapter<ViewHolder> {

        Context mContext;

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;


        public ChatMainAdapter(Context mContext) {

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

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    if (flag == 0) {

                        ChatRoom chatRoom = chatRoomArrayList.get(position);

                        Intent intent = new Intent(Messenger.this, MessengerItem.class);

                        intent.putExtra("chat_room_id", chatRoom.getId());

                        if (chatRoom.getUser1_name().equals(basicFunctions.getUser_name())) {

                            intent.putExtra("username", chatRoom.getUser2_name());
                            intent.putExtra("userid", chatRoom.getUser2_id());


                        } else {

                            intent.putExtra("username", chatRoom.getUser1_name());
                            intent.putExtra("userid", chatRoom.getUser1_id());

                        }

                        startActivity(intent);

                    } else {

                        if (array_position[position] == 0)
                            array_position[position] = 1;

                        else
                            array_position[position] = 0;

                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        public void setLoaded() {
            isLoading = false;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_messenger_list_row, parent, false);

            return new ViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            ChatRoom chatRoom = chatRoomArrayList.get(position);

            if(basicFunctions.getUser_name().equals(chatRoom.getUser1_name())) {

                holder.username.setText(chatRoom.getUser2_name());

                if(chatRoom.getUser2_online().equals("1"))
                    holder.useronline.setVisibility(View.VISIBLE);

                else
                    holder.useronline.setVisibility(View.GONE);

                if(chatRoom.getUser1_read().equals("1"))
                    holder.userread.setVisibility(View.VISIBLE);

                else
                    holder.userread.setVisibility(View.GONE);

                if(!chatRoom.getUser2_image().equals("")) {

                    holder.userimage.setBackground(null);

                    Bitmap bm;

                    byte[] decodedString = Base64.decode(chatRoom.getUser2_image(), Base64.DEFAULT);
                    bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    holder.userimage.setImageBitmap(Bitmap.createScaledBitmap(bm, 60, 60, false));

                }

                else {

                    holder.userimage.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.app_userimage, null));
                    holder.userimage.setImageResource(R.drawable.app_userbackground);

                }

            }

            else {

                holder.username.setText(chatRoom.getUser1_name());

                if(chatRoom.getUser1_online().equals("1"))
                    holder.useronline.setVisibility(View.VISIBLE);

                else
                    holder.useronline.setVisibility(View.GONE);

                if(chatRoom.getUser2_read().equals("1"))
                    holder.userread.setVisibility(View.VISIBLE);

                else
                    holder.userread.setVisibility(View.GONE);

                if(!chatRoom.getUser1_image().equals("")) {

                    holder.userimage.setBackground(null);

                    Bitmap bm;

                    byte[] decodedString = Base64.decode(chatRoom.getUser1_image(), Base64.DEFAULT);
                    bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    holder.userimage.setImageBitmap(Bitmap.createScaledBitmap(bm, 60, 60, false));

                }

                else {

                    holder.userimage.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.app_userimage, null));
                    holder.userimage.setImageResource(R.drawable.app_userbackground);

                }

            }

            if(flag == 1)  holder.delete.setVisibility(View.VISIBLE);

            else    holder.delete.setVisibility(View.GONE);

            if(array_position[position] == 1) {

                holder.delete.setBackgroundResource(R.drawable.app_delete_on);
                holder.delete.setText(null);

            }

            else {

                holder.delete.setBackgroundResource(R.drawable.app_delete_off);
                holder.delete.setText(null);

            }

            holder.timestamp.setText(basicFunctions.getTimeStamp(chatRoom.getTimestamp()));
        }


        @Override
        public int getItemCount() {
            return chatRoomArrayList.size();
        }


    }


    private void getMoreChatRooms(int index) {

        pDialog = ProgressDialog.show(Messenger.this, "", "Fetching Chat List ... ", false, false);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.CHAT_ROOMS + "/" + basicFunctions.getUser_id() + "/" + index, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (!obj.getBoolean("error")) {

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

                            cr.setTotal(chatRoomArrayList.size());

                            if(i == chatRoomsArray.length() - 1)
                                pDialog.dismiss();

                        }

                        flag = 0;

                        array_position = new int[mAdapter.getItemCount()];

                        for (int i = 0; i < mAdapter.getItemCount(); i++)
                            array_position[i] = 0;

                        mAdapter.notifyDataSetChanged();


                    } else
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();

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


    class ChatDividerItem extends RecyclerView.ItemDecoration {

        private Drawable mDivider;

        public ChatDividerItem(Context context) {
            mDivider = ContextCompat.getDrawable(context, R.drawable.messages_line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }



    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        if(id == R.id.delete)

            if(mAdapter.getItemCount()!=0) {

                menuItem.setChecked(!menuItem.isChecked());

                if (menuItem.isChecked()) {

                    menuItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.messages_next, null));

                    flag = 1;
                    mAdapter.notifyDataSetChanged();

                } else {

                    menuItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.messages_delete, null));

                    flag = 0;
                    temp = 0;

                    for(int i=0 ; i< array_position.length ; i++)
                        if(array_position[i] == 1){
                            ChatRoom chatRoom = chatRoomArrayList.get(i);

                            String method = "chat";
                            DeleteChatBackgroundTask deleteChatBackgroundTask = new DeleteChatBackgroundTask(Messenger.this);
                            deleteChatBackgroundTask.execute(method, chatRoom.getId(), basicFunctions.getUser_id());
                            temp++;
                        }

                    if(temp == 1) {

                        Toast.makeText(this, "Chat removed from Chat List !", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Messenger.this,Messenger.class);
                        startActivity(intent);

                    } else if(temp > 1) {

                        Toast.makeText(this, "Multiple Chats removed from Chat List !", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Messenger.this,Messenger.class);
                        startActivity(intent);
                    }

                    mAdapter.notifyDataSetChanged();

                }
            }

            else
                Toast.makeText(Messenger.this, "Your Chat List is empty !", Toast.LENGTH_LONG).show();

        return super.onOptionsItemSelected(menuItem);

    }


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

                    URL url = new URL(EndPoints.DELETE_CHAT);
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

                case "Chat removed from Chat List !":

                    pDialog.dismiss();
                    break;

                default:

                    Toast.makeText(Messenger.this, result, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                    break;


            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        return true;
    }



    private interface ClickListener {

        void onClick(View view, int position);

        void onLongClick(View view, int position);

    }


    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }


        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }


        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, timestamp;
        private ToggleButton delete;
        private ImageView useronline, userread;
        private CircularImageView userimage;

        public ViewHolder(View view) {
            super(view);

            username = (TextView) view.findViewById(R.id.username);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            userread = (ImageView) view.findViewById(R.id.userread);
            userimage = (CircularImageView)view.findViewById(R.id.userimage);
            useronline = (ImageView) view.findViewById(R.id.useronline);
            delete = (ToggleButton) view.findViewById(R.id.deletebutton);

        }
    }

}