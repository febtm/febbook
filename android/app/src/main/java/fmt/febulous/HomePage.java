package fmt.febulous;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fmt.febulous.helper.BasicFunctions;
import fmt.febulous.helper.Menu;
import fmt.febulous.model.Post;


public class HomePage extends AppCompatActivity {


    private BasicFunctions basicFunctions;

    private Menu menu;

    ImageButton MENU_BUTTON, SEARCH_CANCEL;

    private ProgressDialog pDialog;

    private RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    JSONArray HomePagePostArray = null;

    private List<Post> mHomePagePosts = new ArrayList<>();

    private HomePagePostsAdapter homePagePostsAdapter;

    private Post post;

    private EditText SEARCH_BAR;

    JSONArray SearchPostArray = null;

    String searchText;
    private ListView search_list;
    private ListViewAdapter SearchListAdapter;
    private ArrayList<ItemList> searcharraylist = new ArrayList<>();

    private int load_over = 0;

    FloatingActionButton HP_FA_GK, HP_FA_IDEAS, HP_FA_MATERIALS, HP_FA_EVENTS, HP_FA_QNA, HP_FA_TAT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        menu = new Menu(HomePage.this);

        MENU_BUTTON = findViewById(R.id.hp_menu);

        MENU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.LeftDrawer.toggleLeftDrawer();

            }
        });

        SEARCH_BAR = findViewById(R.id.hp_search_bar);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(SEARCH_BAR.getWindowToken(), 0);

        SEARCH_CANCEL = findViewById(R.id.hp_search_cancel);

        search_list = findViewById(R.id.hp_search_list);

        search_list.setVisibility(View.GONE);

        basicFunctions = new BasicFunctions(this);

        AdView mAdView = findViewById(R.id.hp_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        recyclerView = findViewById(R.id.hp_recycler_view);

        recyclerView.setVisibility(View.VISIBLE);

        post = new Post();

        SEARCH_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SEARCH_BAR.setText("");

            }
        });

        HP_FA_EVENTS = findViewById(R.id.hp_fa_events);

        HP_FA_GK = findViewById(R.id.hp_fa_gk);

        HP_FA_IDEAS = findViewById(R.id.hp_fa_ideas);

        HP_FA_MATERIALS = findViewById(R.id.hp_fa_materials);

        HP_FA_TAT = findViewById(R.id.hp_fa_tat);

        HP_FA_QNA = findViewById(R.id.hp_fa_qna);

        HP_FA_GK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gk = new Intent(HomePage.this, PostItem.class);
                gk.putExtra("type", "GK");
                startActivity(gk);

            }
        });

        HP_FA_QNA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent qna = new Intent(HomePage.this, PostItem.class);
                qna.putExtra("type", "QNA");
                startActivity(qna);

            }
        });

        HP_FA_MATERIALS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent materials = new Intent(HomePage.this, PostItem.class);
                materials.putExtra("type", "Materials");
                startActivity(materials);

            }
        });

        HP_FA_IDEAS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ideas = new Intent(HomePage.this, PostItem.class);
                ideas.putExtra("type", "Ideas");
                startActivity(ideas);

            }
        });

        HP_FA_TAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent tat = new Intent(HomePage.this, PostItem.class);
                tat.putExtra("type", "Teach");
                startActivity(tat);

            }
        });

        HP_FA_EVENTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent events = new Intent(HomePage.this, PostItem.class);
                events.putExtra("type", "Events");
                startActivity(events);

            }
        });

        if(basicFunctions.isConnectingToInternet())
            getInitialPostsData();

        else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){

                        case DialogInterface.BUTTON_POSITIVE:

                            if(basicFunctions.isConnectingToInternet())
                                getInitialPostsData();

                            else {

                                Toast.makeText(HomePage.this,
                                        "No Internet Connection. Try again later !",
                                        Toast.LENGTH_LONG).show();

                                dialog.dismiss();

                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            Toast.makeText(HomePage.this,
                                    "No Internet Connection. Try again later !",
                                    Toast.LENGTH_LONG).show();

                            dialog.dismiss();

                            break;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
            builder.setMessage("No Internet Connection. Try again ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }

    }


    private void getInitialPostsData() {

        pDialog = ProgressDialog.show(HomePage.this, "", "Fetching Posts ... ", false, false);

        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_ALL_POSTS + 0, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_InitalPosts(response);

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


    protected void showList_InitalPosts(String response) {

        String hp_id;
        String hp_title;
        String hp_desc;
        String hp_picture;
        String hp_type;
        String hp_timestamp;
        String hp_username;
        String hp_userimage;

        try {
            JSONObject jsonObj = new JSONObject(response);
            HomePagePostArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            post.setTotal(0);

            for (int i = 0; i < HomePagePostArray.length(); i++) {

                JSONObject jsonObject = HomePagePostArray.getJSONObject(i);

                hp_id = jsonObject.getString(basicFunctions.KEY_POST_ID);
                hp_type = jsonObject.getString(basicFunctions.KEY_POST_TYPE);
                hp_title = jsonObject.getString(basicFunctions.KEY_POST_TITLE);
                hp_desc = jsonObject.getString(basicFunctions.KEY_POST_DESC);
                hp_picture = jsonObject.getString(basicFunctions.KEY_POST_IMAGE);
                hp_timestamp = jsonObject.getString(basicFunctions.KEY_POST_TIMESTAMP);
                hp_username = jsonObject.getString(basicFunctions.KEY_USER_USERNAME);
                hp_userimage = jsonObject.getString(basicFunctions.KEY_USER_IMAGE);

                Post data = new Post();

                data.setDetails(hp_id, hp_picture, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username, hp_userimage);

                mHomePagePosts.add(data);

                data.setTotal(mHomePagePosts.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (post.getTotal() == 0)
            Toast.makeText(HomePage.this, "No Posts to display !", Toast.LENGTH_LONG).show();

        linearLayoutManager = new LinearLayoutManager(HomePage.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        homePagePostsAdapter = new HomePagePostsAdapter(HomePage.this);
        recyclerView.setAdapter(homePagePostsAdapter);

        homePagePostsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                mHomePagePosts.add(null);
                homePagePostsAdapter.notifyItemInserted(mHomePagePosts.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pDialog = ProgressDialog.show(HomePage.this, "", "Fetching Posts ... ", false, false);

                        mHomePagePosts.remove(mHomePagePosts.size() - 1);
                        homePagePostsAdapter.notifyItemRemoved(mHomePagePosts.size());

                        int index = mHomePagePosts.size();

                        getMorePostsData(index);

                    }
                }, 1000);
            }
        });


        SEARCH_BAR.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                searchText = SEARCH_BAR.getText().toString().toLowerCase(Locale.getDefault());

                if(TextUtils.isEmpty(searchText)) {

                    search_list.setVisibility(View.GONE);

                    recyclerView.setVisibility(View.VISIBLE);

                }

                else {

                    searcharraylist.clear();
                    searchData(searchText, 0);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        pDialog.dismiss();

    }


    private class HomePagePostsAdapter extends RecyclerView.Adapter<ViewHolder> {

        private LayoutInflater mLayoutInflater;
        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;

        private HomePagePostsAdapter(Context context) {

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

        private void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

            View view = mLayoutInflater.inflate(R.layout.activity_hp_item, viewGroup, false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

            final Post data = mHomePagePosts.get(position);

            viewHolder.setData(data.getId(), data.getImage(),
                    data.getTitle(), data.getDescription(), data.getType(),
                    data.getTimestamp(), data.getUsername(), data.getUserimage());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(HomePage.this, PostView.class);
                    intent.putExtra("id", data.getId());
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return post.getTotal();
        }

        private void setLoaded() {
            isLoading = false;
        }

    }


    private interface OnLoadMoreListener {
        void onLoadMore();
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private CircularImageView mUserImageView;
        private LinearLayout mLinearLayout;
        private TextView mTitleTextView, mPostDateTextView,mDescriptionTextView, mUserNameTextView, mReportTextView;
        private LinearLayout mUserProfile;
        private ImageButton mDeleteButton;

        private ViewHolder(View itemView) {
            super(itemView);

            mLinearLayout = itemView.findViewById(R.id.hp_item_layout);
            mImageView = itemView.findViewById(R.id.hp_item_image);
            mTitleTextView = itemView.findViewById(R.id.hp_item_title);
            mPostDateTextView = itemView.findViewById(R.id.hp_item_date);
            mDescriptionTextView = itemView.findViewById(R.id.hp_item_description);
            mUserNameTextView = itemView.findViewById(R.id.hp_user_name);
            mUserImageView = itemView.findViewById(R.id.hp_user_image);
            mUserProfile = itemView.findViewById(R.id.hp_user);
            mDeleteButton = itemView.findViewById(R.id.hp_delete);
            mReportTextView = itemView.findViewById(R.id.hp_report);

        }

        private void setData(final String id,String image, final String title,String description, final String type,
                             String timestamp,final String username, String userimage) {

            if(username.equals(basicFunctions.getUser_name()))
                mDeleteButton.setVisibility(View.VISIBLE);

            else mDeleteButton.setVisibility(View.GONE);

            if(username.equals(basicFunctions.getUser_name()))
                mReportTextView.setVisibility(View.GONE);

            else mReportTextView.setVisibility(View.VISIBLE);

            switch(type){

                case "GK":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(HomePage.this, R.color.colorGK));
                    break;

                case "QNA":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(HomePage.this, R.color.colorQnA));
                    break;

                case "Materials":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(HomePage.this, R.color.colorStudyMaterials));
                    break;

                case "Teach":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(HomePage.this, R.color.colorTeach));
                    break;

                case "Ideas":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(HomePage.this, R.color.colorIdeas));
                    break;

                case "Events":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(HomePage.this, R.color.colorEvents));
                    break;

                default:

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(HomePage.this, R.color.colorGK));
                    break;

            }

            mTitleTextView.setText(title);
            mDescriptionTextView.setText(description);
            mPostDateTextView.setText(BasicFunctions.getTimeStamp(timestamp));
            mUserNameTextView.setText(username);

            if(!image.equals("")) {

                mImageView.setVisibility(View.VISIBLE);

                mImageView.setBackground(null);

                Bitmap bm;

                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                mImageView.setImageBitmap(Bitmap.createScaledBitmap(bm, 250, 250, false));

            }

            else {

                mImageView.setVisibility(View.GONE);
                mImageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
                mImageView.setImageResource(R.drawable.app_userbackground);

            }

            if(!userimage.equals("")) {

                mUserImageView.setBackground(null);

                byte[] decodedString = Base64.decode(userimage, Base64.DEFAULT);
                Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                mUserImageView.setImageBitmap(Bitmap.createScaledBitmap(bm, 60, 60, false));

            }

            else {

                mUserImageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_userimage, null));
                mUserImageView.setImageResource(R.drawable.app_userbackground);

            }

            mUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(HomePage.this, Profile.class);
                    intent.putExtra("type", username);
                    startActivity(intent);
                    HomePage.this.finish();

                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    delete_post(id);

                }
            });

            mReportTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    report(id, title);

                }
            });
        }
    }


    private void searchData(String searchText, final int index) {

        pDialog = ProgressDialog.show(HomePage.this, "", "Fetching Posts ... ", false, false);

        String url = BasicFunctions.SEARCH_FOR_POSTS + index + "&searchText=" + searchText + "&searchType=all";

        url = url.replaceAll(" ", "%20");

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObj = new JSONObject(response);
                    SearchPostArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

                    if (index == 0 && SearchPostArray.length() == 0) {

                        search_list.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        Toast.makeText(HomePage.this, "No Posts Found !", Toast.LENGTH_LONG).show();

                    }

                    else if(SearchPostArray.length() == 0)
                        Toast.makeText(HomePage.this, "No More Posts Found !", Toast.LENGTH_LONG).show();

                    else {

                        search_list.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    }

                    for (int i = 0; i < SearchPostArray.length(); i++) {

                        JSONObject jsonObject = SearchPostArray.getJSONObject(i);

                        ItemList itemList = new ItemList(jsonObject.getString(basicFunctions.KEY_POST_ID),
                                jsonObject.getString(basicFunctions.KEY_POST_IMAGE),
                                jsonObject.getString(basicFunctions.KEY_POST_TITLE),
                                jsonObject.getString(basicFunctions.KEY_POST_TYPE));

                        searcharraylist.add(itemList);

                    }

                    SearchListAdapter = new ListViewAdapter(HomePage.this);
                    search_list.setAdapter(SearchListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
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

        pDialog.dismiss();

    }


    private class ListViewAdapter extends BaseAdapter {

        Context mContext;
        LayoutInflater inflater;

        private ListViewAdapter(Context context) {
            mContext = context;
            inflater = LayoutInflater.from(mContext);
        }

        private class ViewHolder {
            TextView TV_TITLE;
            TextView TV_TYPE;
            CircularImageView IV_IMAGE;
            Button B_SHOW_MORE;
        }

        @Override
        public int getCount() {
            return searcharraylist.size();
        }

        @Override
        public ItemList getItem(int position) {
            return searcharraylist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {

            final ViewHolder holder;

            if (view == null) {

                holder = new ViewHolder();
                view = inflater.inflate(R.layout.activity_hp_search_item, parent, false);

                holder.IV_IMAGE = view.findViewById(R.id.hp_search_image);
                holder.TV_TITLE = view.findViewById(R.id.hp_search_title);
                holder.TV_TYPE = view.findViewById(R.id.hp_search_type);
                holder.TV_TYPE.setVisibility(View.VISIBLE);
                holder.B_SHOW_MORE = view.findViewById(R.id.hp_search_more);
                view.setTag(holder);

            }

            else  holder = (ViewHolder) view.getTag();

            holder.TV_TITLE.setText(searcharraylist.get(position).getTitle());

            String type;

            if(searcharraylist.get(position).getType().equals("GK"))  type = "General Digest";

            else if(searcharraylist.get(position).getType().equals("QNA"))  type = "Q N A Section";

            else if(searcharraylist.get(position).getType().equals("Materials"))  type = "Study Materials";

            else if(searcharraylist.get(position).getType().equals("Ideas"))  type = "Ideas Galore";

            else if(searcharraylist.get(position).getType().equals("Teach")) type = "Teach a Topic";

            else if(searcharraylist.get(position).getType().equals("Events")) type = "Events and Invites";

            else type = "General Digest";


            holder.TV_TYPE.setText(type);

            if(!searcharraylist.get(position).getImage().equals("")) {

                holder.IV_IMAGE.setBackground(null);

                byte[] decodedString = Base64.decode(searcharraylist.get(position).getImage(), Base64.DEFAULT);
                Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                holder.IV_IMAGE.setImageBitmap(Bitmap.createScaledBitmap(bm, 50, 50, false));

            }

            else {

                holder.IV_IMAGE.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
                holder.IV_IMAGE.setImageResource(R.drawable.app_userbackground);

            }


            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Intent intent = new Intent(HomePage.this, PostView.class);
                    intent.putExtra("id", searcharraylist.get(position).getId());
                    startActivity(intent);

                }
            });

            if(position == (searcharraylist.size() - 1))
                holder.B_SHOW_MORE.setVisibility(View.VISIBLE);

            holder.B_SHOW_MORE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    searchData(searchText, searcharraylist.size());
                }
            });

            return view;
        }

    }


    private void getMorePostsData(int index) {

        StringRequest stringRequest = new StringRequest(BasicFunctions.GET_ALL_POSTS + index, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_MorePosts(response);

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


    protected void showList_MorePosts(String response) {

        String hp_id;
        String hp_title;
        String hp_desc;
        String hp_picture_1;
        String hp_type;
        String hp_timestamp;
        String hp_username;
        String hp_userimage;

        try {

            JSONObject jsonObj = new JSONObject(response);
            HomePagePostArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            if(HomePagePostArray.length() == 0) {

                Toast.makeText(HomePage.this, "No more Posts to display !", Toast.LENGTH_LONG).show();
                load_over = 1;

            }

            for (int i = 0; i < HomePagePostArray.length(); i++) {

                JSONObject jsonObject = HomePagePostArray.getJSONObject(i);

                hp_id = jsonObject.getString(basicFunctions.KEY_POST_ID);
                hp_type = jsonObject.getString(basicFunctions.KEY_POST_TYPE);
                hp_title = jsonObject.getString(basicFunctions.KEY_POST_TITLE);
                hp_desc = jsonObject.getString(basicFunctions.KEY_POST_DESC);
                hp_picture_1 = jsonObject.getString(basicFunctions.KEY_POST_IMAGE);
                hp_timestamp = jsonObject.getString(basicFunctions.KEY_POST_TIMESTAMP);
                hp_username = jsonObject.getString(basicFunctions.KEY_USER_USERNAME);
                hp_userimage = jsonObject.getString(basicFunctions.KEY_USER_IMAGE);

                Post data = new Post();

                data.setDetails(hp_id, hp_picture_1, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username, hp_userimage);

                mHomePagePosts.add(data);

                data.setTotal(mHomePagePosts.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        homePagePostsAdapter.notifyDataSetChanged();
        homePagePostsAdapter.setLoaded();

        pDialog.dismiss();

    }

    private class ItemList {

        private String id;
        private String image;
        private String title;
        private String type;

        private ItemList(String id, String image, String title, String type) {

            this.id = id;
            this.image = image;
            this.title = title;
            this.type = type;

        }

        private String getId() {
            return this.id;
        }

        private String getImage() {
            return this.image;
        }

        private String getTitle() {
            return this.title;
        }

        private String getType() {
            return this.type;
        }

    }


    private void delete_post(final String item_id){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "deletepost";

                        basicFunctions.performTask(method, item_id, "");

                        Intent intent = new Intent(HomePage.this, HomePage.class);
                        startActivity(intent);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(HomePage.this,"Deletion Cancelled !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
        builder.setMessage("Are you sure you want to delete this Post ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }


    private void report(final String item_id, final String item_title){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "reportpost";

                        basicFunctions.performTask(method, item_id, "P : "+item_title);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Toast.makeText(HomePage.this,"Reporting Cancelled !",Toast.LENGTH_LONG).show();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
        builder.setMessage("Are you sure you want to report this Post ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

}