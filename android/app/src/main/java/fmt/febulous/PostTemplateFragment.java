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


public class PostTemplateFragment extends Fragment {

    JSONArray SearchPostArray = null;

    JSONArray PostRecyclerArray = null;

    private List<Post> mRecyclerPosts = new ArrayList<>();

    private List<Post> mTempRecyclerPosts = new ArrayList<>();

    private PostTemplateFragmentAdapter postTemplateFragmentAdapter;

    private Activity activity;
    private RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    private FrameLayout PT_TOOLBAR;
    private LinearLayout PT_POST;
    private FrameLayout PT_RECYCLER_LAYOUT;
    FrameLayout PT_LAYOUT;

    String searchText;
    private ListView search_list;
    ListViewAdapter SearchListAdapter;
    ArrayList<ItemList> searcharraylist = new ArrayList<>();

    private EditText SEARCH_TEXT;

    private Spinner S_USERCOURSE, S_USERTYPE, S_DATE;

    ArrayAdapter<String> usertype, usercourse, date;

    private FrameLayout.LayoutParams recyclerLayoutParams,homePageLayoutParams;

    private onPostrecyclerFragmentItemSelected mListener;

    private BasicFunctions basicFunctions;

    private Post post;

    int count;

    private int load_over = 0;

    private ProgressDialog pDialog;



    public static PostTemplateFragment newInstance() {
      return new PostTemplateFragment();
    }

    public PostTemplateFragment() {}


    @Override
    public void onAttach(Context context) {
      super.onAttach(context);

      if (context instanceof onPostrecyclerFragmentItemSelected) {

        mListener = (onPostrecyclerFragmentItemSelected) context;

      } else {

        throw new ClassCastException(context.toString() + " must implement onPostrecyclerFragmentItemSelected.");

      }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      final View view = inflater.inflate(R.layout.activity_homepage_fragment_list, container, false);

      activity = getActivity();

      PT_RECYCLER_LAYOUT = (FrameLayout) view.findViewById(R.id.hp_frag_recyclerLayout);
      recyclerLayoutParams = (FrameLayout.LayoutParams) PT_RECYCLER_LAYOUT.getLayoutParams();
      PT_LAYOUT = (FrameLayout) activity.findViewById(R.id.pt_layout);
      homePageLayoutParams = (FrameLayout.LayoutParams) PT_LAYOUT.getLayoutParams();
      PT_POST = (LinearLayout) activity.findViewById(R.id.pt_post);
      PT_TOOLBAR = (FrameLayout) activity.findViewById(R.id.pt_toolbar);

      S_USERCOURSE = (Spinner) activity.findViewById(R.id.pr_filters_post_usercourse_spinner);
      S_USERTYPE = (Spinner) activity.findViewById(R.id.pr_filters_post_usertype_spinner);
      S_DATE = (Spinner) activity.findViewById(R.id.pr_filters_post_date_spinner);

      recyclerView = (RecyclerView) view.findViewById(R.id.hp_frag_recycler_view);

      basicFunctions = new BasicFunctions(activity);

      AdView mAdView = (AdView) view.findViewById(R.id.adView);
      AdRequest adRequest = new AdRequest.Builder().build();
      mAdView.loadAd(adRequest);

      post = new Post();

      search_list = (ListView) activity.findViewById(R.id.pt_search_list);
      search_list.setVisibility(View.GONE);
      SEARCH_TEXT = (EditText) activity.findViewById(R.id.pt_search_bar);

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

      S_USERTYPE.setAdapter(usertype);

      S_USERCOURSE.setAdapter(usercourse);

      S_DATE.setAdapter(date);


      if(basicFunctions.isConnectingToInternet())
          getInitialSpecificPostData();

      else{

          DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  switch (which){
                      case DialogInterface.BUTTON_POSITIVE:

                          Intent intent = new Intent(activity, HomePage.class);
                          startActivity(intent);

                          break;

                  }
              }
            };

          AlertDialog.Builder builder = new AlertDialog.Builder(activity);
          builder.setMessage("Network Failure : Please check your Internet Connection !")
                  .setPositiveButton("Try Again ... ", dialogClickListener).show();

      }

      return view;

    }


    private void getInitialSpecificPostData(){

        pDialog = ProgressDialog.show(activity, "", "Fetching Posts ... ", false, false);

        StringRequest stringRequest = new StringRequest(EndPoints.GET_POST_TYPE + PostTemplate.TYPE + "&index=" + 0, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showList_InitialPosts(response);

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


    protected void showList_InitialPosts(String response) {

        String hp_id ;
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

            PostRecyclerArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            post.setTotal(0);
            post.setTempTotal(0);

            for (int i = 0; i < PostRecyclerArray.length(); i++) {
                JSONObject jsonObject = PostRecyclerArray.getJSONObject(i);

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

                mRecyclerPosts.add(data);

                mTempRecyclerPosts.add(data);

                data.setTotal(mRecyclerPosts.size());

                data.setTempTotal(mTempRecyclerPosts.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (post.getTotal() == 0)
            Toast.makeText(activity, "No Posts to display !", Toast.LENGTH_LONG).show();

        linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        postTemplateFragmentAdapter = new PostTemplateFragmentAdapter(activity);
        recyclerView.setAdapter(postTemplateFragmentAdapter);

        postTemplateFragmentAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                mRecyclerPosts.add(null);
                postTemplateFragmentAdapter.notifyItemInserted(mRecyclerPosts.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pDialog = ProgressDialog.show(activity, "", "Fetching Posts ... ", false, false);

                        mRecyclerPosts.remove(mRecyclerPosts.size() - 1);
                        postTemplateFragmentAdapter.notifyItemRemoved(mRecyclerPosts.size());

                        int index = mRecyclerPosts.size();

                        getMoreSpecificPostData(index);

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

                if (TextUtils.isEmpty(searchText))
                    search_list.setVisibility(View.GONE);

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

        S_USERCOURSE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                FilterData();

                SortData();

                linearLayoutManager = new LinearLayoutManager(activity);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                postTemplateFragmentAdapter = new PostTemplateFragmentAdapter(activity);
                recyclerView.setAdapter(postTemplateFragmentAdapter);

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
                postTemplateFragmentAdapter = new PostTemplateFragmentAdapter(activity);
                recyclerView.setAdapter(postTemplateFragmentAdapter);

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
                postTemplateFragmentAdapter = new PostTemplateFragmentAdapter(activity);
                recyclerView.setAdapter(postTemplateFragmentAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        pDialog.dismiss();

    }


    private void getMoreSpecificPostData(int index){

        StringRequest stringRequest = new StringRequest(EndPoints.GET_POST_TYPE + PostTemplate.TYPE + "&index=" + index, new Response.Listener<String>() {
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
            PostRecyclerArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            if(PostRecyclerArray.length() == 0) {

                Toast.makeText(activity, "No more Posts to display !", Toast.LENGTH_LONG).show();
                load_over = 1;

            }

            for (int i = 0; i < PostRecyclerArray.length(); i++) {
                JSONObject jsonObject = PostRecyclerArray.getJSONObject(i);

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

                mRecyclerPosts.add(data);

                mTempRecyclerPosts.add(data);

                data.setTotal(mRecyclerPosts.size());

                data.setTempTotal(mTempRecyclerPosts.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        FilterData();

        SortData();

        postTemplateFragmentAdapter.notifyDataSetChanged();
        postTemplateFragmentAdapter.setLoaded();

        pDialog.dismiss();

    }


    private class PostTemplateFragmentAdapter extends RecyclerView.Adapter<ViewHolder> {

      private LayoutInflater mLayoutInflater;
      private OnLoadMoreListener mOnLoadMoreListener;

      private boolean isLoading;
      private int visibleThreshold = 3;
      private int lastVisibleItem, totalItemCount;

      private PostTemplateFragmentAdapter(Context context) {

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
      public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        final Post data = mTempRecyclerPosts.get(position);

        viewHolder.setData(data.getTempId(), data.getTempImage(),
              data.getTempTitle(), data.getTempDescription(), data.getTempType(),
              data.getTempTimestamp(), data.getTempUsername(), data.getTempUserimage());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPostRecyclerFragmentItemSelected(data.getTempId());
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


    class ViewHolder extends RecyclerView.ViewHolder {

      private ImageView mImageView;
      private ImageView mUserImageView;
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
        mUserImageView = (ImageView) itemView.findViewById(R.id.hp_item_userimage);
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
          byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
          Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

          mImageView.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 250, 250, false));

        }

        else {

            mImageView.setVisibility(View.GONE);
            mImageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.mp_usercover, null));
            mImageView.setImageResource(R.drawable.app_userbackground);

        }

        if(!userimage.equals("")) {

          mUserImageView.setBackground(null);
          byte[] decodedString = Base64.decode(userimage, Base64.DEFAULT);
          Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

          mUserImageView.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 60, 60, false));

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

        String url = EndPoints.SEARCH_FOR_POSTS + index + "&searchText=" + searchText + "&searchType=" + PostTemplate.TYPE;

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


    private void FilterData(){

    count = 0;

    String usercourse = S_USERCOURSE.getSelectedItem().toString().toLowerCase();

    String usertype = S_USERTYPE.getSelectedItem().toString().toLowerCase();

    for(int i = 0; i< post.getTotal(); i++) {

          Post data = mRecyclerPosts.get(i);

          mTempRecyclerPosts.add(i, data);

    }

    post.setTempTotal(post.getTotal());


    if(S_USERTYPE.getSelectedItemPosition() == 0 && S_USERCOURSE.getSelectedItemPosition() != 0){

      for (int i = 0; i < post.getTempTotal(); i++) {

          Post data = mTempRecyclerPosts.get(i);

          if (data.getTempUsercourse().toLowerCase().equals(usercourse)) {

              mTempRecyclerPosts.set(count, data);

              count++;

          }
      }

        post.setTempTotal(count);

    }


    else if(S_USERTYPE.getSelectedItemPosition() != 0 && S_USERCOURSE.getSelectedItemPosition() == 0){

      for (int i = 0; i < post.getTempTotal(); i++) {

          Post data = mTempRecyclerPosts.get(i);

          if (data.getTempUsertype().toLowerCase().equals(usertype)) {

              mTempRecyclerPosts.set(count, data);

              count++;

          }
      }

        post.setTempTotal(count);

    }

    else if(S_USERTYPE.getSelectedItemPosition() != 0 && S_USERCOURSE.getSelectedItemPosition() != 0){


      for (int i = 0; i < post.getTempTotal(); i++) {

          Post data = mTempRecyclerPosts.get(i);

          if (data.getTempUsertype().toLowerCase().equals(usertype)) {

              mTempRecyclerPosts.set(count, data);

              count++;

          }
      }

      post.setTempTotal(count);

      count = 0;

      for (int i = 0; i < post.getTempTotal(); i++) {

          Post data = mTempRecyclerPosts.get(i);

          if (data.getTempUsercourse().toLowerCase().equals(usercourse)) {

              mTempRecyclerPosts.set(count, data);

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

          mTempRecyclerPosts = basicFunctions.doDateSort(mTempRecyclerPosts, 0, post.getTempTotal());

      }

      else if(S_DATE.getSelectedItemPosition() == 2) {

          mTempRecyclerPosts = basicFunctions.doDateSort(mTempRecyclerPosts, 1, post.getTempTotal());

      }
    }
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

              holder.TV_TITLE = (TextView) view.findViewById(R.id.hp_search_list_title);
              holder.TV_TYPE = (TextView) view.findViewById(R.id.hp_search_list_type);
              holder.IV_IMAGE = (CircularImageView) view.findViewById(R.id.hp_search_list_image);
              holder.B_SHOW_MORE = (Button) view.findViewById(R.id.hp_search_list_more);
              view.setTag(holder);

          } else holder = (ViewHolder) view.getTag();

          holder.TV_TITLE.setText(searcharraylist.get(position).getTitle());

          holder.TV_TYPE.setVisibility(View.GONE);

          if (!searcharraylist.get(position).getImage().equals("")) {

              holder.IV_IMAGE.setBackground(null);
              byte[] decodedString = Base64.decode(searcharraylist.get(position).getImage(), Base64.DEFAULT);
              Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

              holder.IV_IMAGE.setImageBitmap(Bitmap.createScaledBitmap(decodedByte, 50, 50, false));

          } else {

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

    public String getId() {
      return this.id;
    }

    public String getImage() {
      return this.image;
    }

    public String getTitle() {
      return this.title;
    }

    public String getType() {
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


    public interface onPostrecyclerFragmentItemSelected {
        void onPostRecyclerFragmentItemSelected(String id);
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


    private void hideViews() {

        if(post.getTempTotal() > 5) {

            PT_TOOLBAR.animate().translationY(-PT_TOOLBAR.getHeight()).setInterpolator(new AccelerateInterpolator(2));

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) PT_POST.getLayoutParams();
            int buttonmargin = lp.bottomMargin;
            PT_POST.animate().translationY(PT_POST.getHeight() + buttonmargin).setInterpolator(new AccelerateInterpolator(2)).start();

            homePageLayoutParams.setMargins(0, 10, 0, 10);
            PT_RECYCLER_LAYOUT.setLayoutParams(homePageLayoutParams);

            search_list.setVisibility(View.GONE);

        }
    }


    private void showViews() {

        if(post.getTempTotal() > 5) {

            homePageLayoutParams.setMargins(0, 0, 0, 0);

            PT_TOOLBAR.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            PT_POST.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            PT_RECYCLER_LAYOUT.setLayoutParams(recyclerLayoutParams);

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