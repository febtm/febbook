package fmt.febulous;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import fmt.febulous.helper.EndPoints;
import fmt.febulous.model.Post;


public class HomePageFragment extends Fragment {


    JSONArray HomePagePostArray = null;

    JSONArray SearchPostArray = null;

    private List<Post> mHomePagePosts = new ArrayList<>();

    private List<Post> mTempHomePagePosts = new ArrayList<>();

    private ProgressDialog pDialog;

    private Activity activity;
    private RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    private HomePageFragmentAdapter homePageFragmentAdapter;

    private FrameLayout.LayoutParams recyclerLayoutParams, homePageLayoutParams;

    private onHomepageFragmentItemSelected mListener;

    private FrameLayout HP_TOOLBAR;
    private LinearLayout HP_DASHBOARD;
    private FrameLayout HP_RECYCLER_LAYOUT;
    FrameLayout HP_LAYOUT;

    private BasicFunctions basicFunctions;

    private Post post;

    private EditText SEARCH_TEXT;

    String searchText;
    private ListView search_list;
    private ListViewAdapter SearchListAdapter;
    private ArrayList<ItemList> searcharraylist = new ArrayList<>();

    public int count;

    private int load_over = 0;


    private Spinner S_USERCOURSE, S_USERTYPE, S_POSTTYPE, S_DATE;

    ArrayAdapter<String> usertype, usercourse, posttype, date;


    protected static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    public HomePageFragment() {}


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof onHomepageFragmentItemSelected)
            mListener = (onHomepageFragmentItemSelected) context;

        else
            throw new ClassCastException(context.toString() + " must implement onHomepageFragmentItemSelected.");

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_homepage_fragment_list, container, false);

        activity = getActivity();

        HP_RECYCLER_LAYOUT = (FrameLayout) view.findViewById(R.id.hp_frag_recyclerLayout);
        recyclerLayoutParams = (FrameLayout.LayoutParams) HP_RECYCLER_LAYOUT.getLayoutParams();
        HP_LAYOUT = (FrameLayout) activity.findViewById(R.id.homepage_layout);
        homePageLayoutParams = (FrameLayout.LayoutParams) HP_LAYOUT.getLayoutParams();
        HP_DASHBOARD = (LinearLayout) activity.findViewById(R.id.homepage_dashboard);
        HP_TOOLBAR = (FrameLayout) activity.findViewById(R.id.homepage_toolbar);

        S_USERCOURSE = (Spinner) activity.findViewById(R.id.filters_post_usercourse_spinner);
        S_USERTYPE = (Spinner) activity.findViewById(R.id.filters_post_usertype_spinner);
        S_POSTTYPE = (Spinner) activity.findViewById(R.id.filters_post_type_spinner);
        S_DATE = (Spinner) activity.findViewById(R.id.filters_post_date_spinner);

        recyclerView = (RecyclerView) view.findViewById(R.id.hp_frag_recycler_view);

        basicFunctions = new BasicFunctions(activity);

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        post = new Post();

        search_list = (ListView) activity.findViewById(R.id.homepage_search_list);
        search_list.setVisibility(View.GONE);
        SEARCH_TEXT = (EditText) activity.findViewById(R.id.homepage_search_bar);

        usertype = new ArrayAdapter<>(
                activity, R.layout.activity_homepage_spinner, basicFunctions.USERTYPE
        );

        usertype.setDropDownViewResource(R.layout.activity_homepage_spinner);

        usercourse = new ArrayAdapter<>(
                activity, R.layout.activity_homepage_spinner, basicFunctions.USERCOURSE
        );

        usercourse.setDropDownViewResource(R.layout.activity_homepage_spinner);

        date = new ArrayAdapter<>(
                activity, R.layout.activity_homepage_spinner, basicFunctions.DATELIST
        );

        date.setDropDownViewResource(R.layout.activity_homepage_spinner);

        posttype = new ArrayAdapter<>(
                activity, R.layout.activity_homepage_spinner, basicFunctions.POSTLIST
        );

        posttype.setDropDownViewResource(R.layout.activity_homepage_spinner);


        S_POSTTYPE.setAdapter(posttype);

        S_USERTYPE.setAdapter(usertype);

        S_USERCOURSE.setAdapter(usercourse);

        S_DATE.setAdapter(date);


        getInitialPostsData();


        return view;

    }


    private void getInitialPostsData() {

        pDialog = ProgressDialog.show(activity, "", "Fetching Posts ... ", false, false);

        StringRequest stringRequest = new StringRequest(EndPoints.GET_ALL_POSTS + 0, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_InitalPosts(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }


    protected void showList_InitalPosts(String response) {

        String hp_id;
        String hp_title;
        String hp_desc;
        String hp_picture_1;
        String hp_type;
        String hp_timestamp;
        String hp_username;
        String hp_userimage;
        String hp_usercourse;
        String hp_usertype;

        try {
            JSONObject jsonObj = new JSONObject(response);
            HomePagePostArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            post.setTotal(0);
            post.setTempTotal(0);

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
                hp_usercourse = jsonObject.getString(basicFunctions.KEY_USER_COURSE);
                hp_usertype = jsonObject.getString(basicFunctions.KEY_USER_TYPE);

                Post data = new Post();

                data.setDetails(hp_id, hp_picture_1, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username,
                        hp_userimage, hp_usercourse, hp_usertype);

                data.setTempDetails(hp_id, hp_picture_1, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username,
                        hp_userimage, hp_usercourse, hp_usertype);

                mHomePagePosts.add(data);

                mTempHomePagePosts.add(data);

                data.setTotal(mHomePagePosts.size());

                data.setTempTotal(mTempHomePagePosts.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (post.getTotal() == 0)
            Toast.makeText(activity, "No Posts to display !", Toast.LENGTH_LONG).show();

        linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        homePageFragmentAdapter = new HomePageFragmentAdapter(activity);
        recyclerView.setAdapter(homePageFragmentAdapter);

        homePageFragmentAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                mHomePagePosts.add(null);
                homePageFragmentAdapter.notifyItemInserted(mHomePagePosts.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pDialog = ProgressDialog.show(activity, "", "Fetching Posts ... ", false, false);

                        mHomePagePosts.remove(mHomePagePosts.size() - 1);
                        homePageFragmentAdapter.notifyItemRemoved(mHomePagePosts.size());

                        int index = mHomePagePosts.size();

                        getMorePostsData(index);

                    }
                }, 1000);
            }
        });

        recyclerView.addOnScrollListener(new HidingScrollListener() {

            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }

        });

        SEARCH_TEXT.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                searchText = SEARCH_TEXT.getText().toString().toLowerCase(Locale.getDefault());

                if(TextUtils.isEmpty(searchText))
                    search_list.setVisibility(View.GONE);

                else{

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


        S_POSTTYPE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                FilterData();

                SortData();

                linearLayoutManager = new LinearLayoutManager(activity);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                homePageFragmentAdapter = new HomePageFragmentAdapter(activity);
                recyclerView.setAdapter(homePageFragmentAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        S_USERCOURSE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                FilterData();

                SortData();

                linearLayoutManager = new LinearLayoutManager(activity);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                homePageFragmentAdapter = new HomePageFragmentAdapter(activity);
                recyclerView.setAdapter(homePageFragmentAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        S_USERTYPE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                FilterData();

                SortData();

                linearLayoutManager = new LinearLayoutManager(activity);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                homePageFragmentAdapter = new HomePageFragmentAdapter(activity);
                recyclerView.setAdapter(homePageFragmentAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        S_DATE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                FilterData();

                SortData();

                linearLayoutManager = new LinearLayoutManager(activity);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                homePageFragmentAdapter = new HomePageFragmentAdapter(activity);
                recyclerView.setAdapter(homePageFragmentAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        pDialog.dismiss();

    }



    private void getMorePostsData(int index) {

        StringRequest stringRequest = new StringRequest(EndPoints.GET_ALL_POSTS + index, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_MorePosts(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
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
        String hp_usercourse;
        String hp_usertype;

        try {
            JSONObject jsonObj = new JSONObject(response);
            HomePagePostArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            if(HomePagePostArray.length() == 0) {

                Toast.makeText(activity, "No more Posts to display !", Toast.LENGTH_LONG).show();
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
                hp_usercourse = jsonObject.getString(basicFunctions.KEY_USER_COURSE);
                hp_usertype = jsonObject.getString(basicFunctions.KEY_USER_TYPE);

                Post data = new Post();

                data.setDetails(hp_id, hp_picture_1, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username,
                        hp_userimage, hp_usercourse, hp_usertype);

                data.setTempDetails(hp_id, hp_picture_1, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username,
                        hp_userimage, hp_usercourse, hp_usertype);

                mHomePagePosts.add(data);

                mTempHomePagePosts.add(data);

                data.setTotal(mHomePagePosts.size());

                data.setTempTotal(mTempHomePagePosts.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        FilterData();

        SortData();

        homePageFragmentAdapter.notifyDataSetChanged();
        homePageFragmentAdapter.setLoaded();

        pDialog.dismiss();

    }


    private class HomePageFragmentAdapter extends RecyclerView.Adapter<ViewHolder> {

      private LayoutInflater mLayoutInflater;
      private OnLoadMoreListener mOnLoadMoreListener;

      private boolean isLoading;
      private int visibleThreshold = 3;
      private int lastVisibleItem, totalItemCount;

      private HomePageFragmentAdapter(Context context) {

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

      @Override
      public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View view = mLayoutInflater.inflate(R.layout.activity_homepage_fragment_item, viewGroup, false);
            return new ViewHolder(view);

      }

      @Override
      public void onBindViewHolder(ViewHolder viewHolder, int position) {

          final Post data = mTempHomePagePosts.get(position);

          viewHolder.setData(data.getTempId(), data.getTempImage(),
                  data.getTempTitle(), data.getTempDescription(), data.getTempType(),
                  data.getTempTimestamp(), data.getTempUsername(), data.getTempUserimage());

          viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mListener.onHomePageFragmentItemSelected(data.getId());
            }
          });

      }

      @Override
      public int getItemCount() {
        return post.getTempTotal();
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

            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.hp_item_LL);
            mImageView = (ImageView) itemView.findViewById(R.id.hp_item_image);
            mTitleTextView = (TextView) itemView.findViewById(R.id.hp_item_title);
            mPostDateTextView = (TextView) itemView.findViewById(R.id.hp_item_date);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.hp_item_description);
            mUserNameTextView = (TextView) itemView.findViewById(R.id.hp_item_username);
            mUserImageView = (CircularImageView) itemView.findViewById(R.id.hp_item_userimage);
            mUserProfile = (LinearLayout) itemView.findViewById(R.id.hp_userprofile);
            mDeleteButton = (ImageButton) itemView.findViewById(R.id.hp_delete);
            mReportTextView = (TextView) itemView.findViewById(R.id.hp_report);

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

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorGK));
                    break;

                case "QNA":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorQnA));
                    break;

                case "Materials":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorStudyMaterials));
                    break;

                case "Teach":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorTeach));
                    break;

                case "Ideas":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorIdeas));
                    break;

                case "Events":

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorEvents));
                    break;

                default:

                    mLinearLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorGK));
                    break;

            }


            mTitleTextView.setText(title);
            mDescriptionTextView.setText(description);
            mPostDateTextView.setText(basicFunctions.getTimeStamp(timestamp));
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

                    Intent intent = new Intent(activity, Profile.class);
                    intent.putExtra("type", username);
                    startActivity(intent);

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

        pDialog = ProgressDialog.show(activity, "", "Fetching Posts ... ", false, false);

        String url = EndPoints.SEARCH_FOR_POSTS + index + "&searchText=" + searchText + "&searchType=all";

        url = url.replaceAll(" ", "%20");

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObj = new JSONObject(response);
                    SearchPostArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

                    if (index == 0 && SearchPostArray.length() == 0) {

                        search_list.setVisibility(View.GONE);
                        Toast.makeText(activity, "No Posts Found !", Toast.LENGTH_LONG).show();

                    }

                    else if(SearchPostArray.length() == 0)
                        Toast.makeText(activity, "No More Posts Found !", Toast.LENGTH_LONG).show();

                    else
                        search_list.setVisibility(View.VISIBLE);

                    for (int i = 0; i < SearchPostArray.length(); i++) {

                        JSONObject jsonObject = SearchPostArray.getJSONObject(i);

                        ItemList itemList = new ItemList(jsonObject.getString(basicFunctions.KEY_POST_ID),
                                jsonObject.getString(basicFunctions.KEY_POST_IMAGE),
                                jsonObject.getString(basicFunctions.KEY_POST_TITLE),
                                jsonObject.getString(basicFunctions.KEY_POST_TYPE));

                        searcharraylist.add(itemList);

                    }

                    SearchListAdapter = new ListViewAdapter(activity);
                    search_list.setAdapter(SearchListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
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
                view = inflater.inflate(R.layout.activity_homepage_search_list_item, parent, false);

                holder.IV_IMAGE = (CircularImageView) view.findViewById(R.id.hp_search_list_image);
                holder.TV_TITLE = (TextView) view.findViewById(R.id.hp_search_list_title);
                holder.TV_TYPE = (TextView) view.findViewById(R.id.hp_search_list_type);
                holder.B_SHOW_MORE = (Button) view.findViewById(R.id.hp_search_list_more);
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

                    Intent intent = new Intent(activity, PostView.class);
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


    private void FilterData(){

        count = 0;

        String posttype = S_POSTTYPE.getSelectedItem().toString().toLowerCase();

        String usercourse = S_USERCOURSE.getSelectedItem().toString().toLowerCase();

        String usertype = S_USERTYPE.getSelectedItem().toString().toLowerCase();

        for (int i = 0; i < post.getTotal(); i++) {

            Post data = mHomePagePosts.get(i);

            mTempHomePagePosts.add(i, data);

        }

        post.setTempTotal(post.getTotal());


        if(S_POSTTYPE.getSelectedItemPosition() == 0 && S_USERTYPE.getSelectedItemPosition() == 0 &&
                S_USERCOURSE.getSelectedItemPosition() != 0){

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                if (data.getTempUsercourse().toLowerCase().equals(usercourse)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);

        }


        else if(S_POSTTYPE.getSelectedItemPosition() == 0 && S_USERTYPE.getSelectedItemPosition() != 0 &&
                S_USERCOURSE.getSelectedItemPosition() == 0){

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                if (data.getTempUsertype().toLowerCase().equals(usertype)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);


        }


        else if(S_POSTTYPE.getSelectedItemPosition() == 0 && S_USERTYPE.getSelectedItemPosition() != 0 &&
                S_USERCOURSE.getSelectedItemPosition() != 0){


            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                if (data.getTempUsertype().toLowerCase().equals(usertype)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);


            count = 0;

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                if (data.getTempUsercourse().toLowerCase().equals(usercourse)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);

        }



        else if (S_POSTTYPE.getSelectedItemPosition() != 0 && S_USERTYPE.getSelectedItemPosition() == 0
                && S_USERCOURSE.getSelectedItemPosition() == 0) {

            String type;

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                switch(data.getTempType()){

                    case "GK":

                        type = "GENERAL DIGEST";
                        break;

                    case "QNA":

                        type = "Q N A SECTION";
                        break;

                    case "Materials":

                        type = "STUDY MATERIALS";
                        break;

                    case "Ideas":

                        type = "IDEAS GALORE";
                        break;

                    case "Teach":

                        type = "TEACH A TOPIC";
                        break;

                    case "Events":

                        type = "EVENTS AND INVITES";
                        break;

                    default:

                        type = "GENERAL DIGEST";
                        break;

                }

                if (type.toLowerCase().equals(posttype)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);

        }

        else if (S_POSTTYPE.getSelectedItemPosition() != 0 && S_USERTYPE.getSelectedItemPosition() == 0
                && S_USERCOURSE.getSelectedItemPosition() != 0) {

            String type;

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                switch(data.getTempType()){

                    case "GK":

                        type = "GENERAL DIGEST";
                        break;

                    case "QNA":

                        type = "Q N A SECTION";
                        break;

                    case "Materials":

                        type = "STUDY MATERIALS";
                        break;

                    case "Ideas":

                        type = "IDEAS GALORE";
                        break;

                    case "Teach":

                        type = "TEACH A TOPIC";
                        break;

                    case "Events":

                        type = "EVENTS AND INVITES";
                        break;

                    default:

                        type = "GENERAL DIGEST";
                        break;

                }


                if (type.toLowerCase().equals(posttype)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);


            count = 0;

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                if (data.getTempUsercourse().toLowerCase().equals(usercourse)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);

        }


        else if (S_POSTTYPE.getSelectedItemPosition() != 0 && S_USERTYPE.getSelectedItemPosition() != 0
                && S_USERCOURSE.getSelectedItemPosition() == 0) {

            String type;

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                switch(data.getTempType()){

                    case "GK":

                        type = "GENERAL DIGEST";
                        break;

                    case "QNA":

                        type = "Q N A SECTION";
                        break;

                    case "Materials":

                        type = "STUDY MATERIALS";
                        break;

                    case "Ideas":

                        type = "IDEAS GALORE";
                        break;

                    case "Teach":

                        type = "TEACH A TOPIC";
                        break;

                    case "Events":

                        type = "EVENTS AND INVITES";
                        break;

                    default:

                        type = "GENERAL DIGEST";
                        break;

                }


                if (type.toLowerCase().equals(posttype)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);


            count = 0;

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                if (data.getTempUsertype().toLowerCase().equals(usertype)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);

        }


        else if (S_POSTTYPE.getSelectedItemPosition() != 0 && S_USERTYPE.getSelectedItemPosition() != 0
                && S_USERCOURSE.getSelectedItemPosition() != 0) {

            String type;

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                switch(data.getTempType()){

                    case "GK":

                        type = "GENERAL DIGEST";
                        break;

                    case "QNA":

                        type = "Q N A SECTION";
                        break;

                    case "Materials":

                        type = "STUDY MATERIALS";
                        break;

                    case "Ideas":

                        type = "IDEAS GALORE";
                        break;

                    case "Teach":

                        type = "TEACH A TOPIC";
                        break;

                    case "Events":

                        type = "EVENTS AND INVITES";
                        break;

                    default:

                        type = "GENERAL DIGEST";
                        break;

                }


                if (type.toLowerCase().equals(posttype)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);


            count = 0;

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                if (data.getTempUsertype().toLowerCase().equals(usertype)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);


            count = 0;

            for (int i = 0; i < post.getTempTotal(); i++) {

                Post data = mTempHomePagePosts.get(i);

                if (data.getTempUsercourse().toLowerCase().equals(usercourse)) {

                    mTempHomePagePosts.set(count, data);

                    count++;

                }
            }

            post.setTempTotal(count);

        }

    }


    private void SortData(){

        if(S_DATE.getSelectedItemPosition() != 0)
        {

            if(S_DATE.getSelectedItemPosition() == 1) {

                mTempHomePagePosts = basicFunctions.doDateSort(mTempHomePagePosts, 0, post.getTempTotal());

            }

            else if(S_DATE.getSelectedItemPosition() == 2) {

                mTempHomePagePosts = basicFunctions.doDateSort(mTempHomePagePosts, 1, post.getTempTotal());

            }

        }

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

              Intent intent = new Intent(activity, HomePage.class);
              startActivity(intent);

              break;

            case DialogInterface.BUTTON_NEGATIVE:

              Toast.makeText(activity,"Deletion Cancelled !",Toast.LENGTH_LONG).show();

              break;
          }
        }
      };

      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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

              Toast.makeText(activity,"Reporting Cancelled !",Toast.LENGTH_LONG).show();

              break;
          }
        }
      };

      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setMessage("Are you sure you want to report this Post ?").setPositiveButton("Yes", dialogClickListener)
              .setNegativeButton("No", dialogClickListener).show();

    }


    public interface onHomepageFragmentItemSelected {
      void onHomePageFragmentItemSelected(String id);
    }


    private void hideViews() {

        if(post.getTempTotal() > 5) {

            HP_TOOLBAR.animate().translationY(-HP_TOOLBAR.getHeight()).setInterpolator(new AccelerateInterpolator(2));

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) HP_DASHBOARD.getLayoutParams();
            int buttonmargin = lp.bottomMargin;
            HP_DASHBOARD.animate().translationY(HP_DASHBOARD.getHeight() + buttonmargin).setInterpolator(new AccelerateInterpolator(2)).start();

            homePageLayoutParams.setMargins(0, 10, 0, 10);
            HP_RECYCLER_LAYOUT.setLayoutParams(homePageLayoutParams);

            search_list.setVisibility(View.GONE);

        }

    }


    private void showViews() {

        if(post.getTempTotal() > 5) {

            homePageLayoutParams.setMargins(0, 0, 0, 0);

            HP_TOOLBAR.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            HP_DASHBOARD.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            HP_RECYCLER_LAYOUT.setLayoutParams(recyclerLayoutParams);

            if (!SEARCH_TEXT.getText().toString().equals(""))
                search_list.setVisibility(View.VISIBLE);

        }

    }


    private abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

      private static final int HIDE_THRESHOLD = 20;
      private int scrolledDistance = 0;
      private boolean controlsVisible = true;

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {

          onHide();
          controlsVisible = false;
          scrolledDistance = 0;

        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {

          onShow();
          controlsVisible = true;
          scrolledDistance = 0;

        }

        if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {

          scrolledDistance += dy;

        }
      }

      public abstract void onHide();
      public abstract void onShow();

    }

}