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
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fmt.febulous.helper.BasicFunctions;
import fmt.febulous.helper.EndPoints;
import fmt.febulous.model.Post;


public class ProfileFragment extends Fragment {


    JSONArray AllProfilePostsArray = null;

    public List<Post> mProfilePosts = new ArrayList<>();

    private MyProfileFragmentAdapter myProfileFragmentAdapter;

    private FrameLayout MP_TOOLBAR;
    private Button MP_POSTS;
    private Button MP_PROFILE;
    private FrameLayout MP_RECYCLER_LAYOUT;
    private FrameLayout MP_ROOT_LAYOUT;
    FrameLayout MP_LAYOUT;
    private ListView list;
    private EditText SEARCH_TEXT;
    private ScrollView MP_SCROLL;

    private LinearLayout MP_BUTTONS;

    private FrameLayout.LayoutParams recyclerLayoutParams,homePageLayoutParams;

    private BasicFunctions basicFunctions;

    private Post post;

    private onMyprofileFragmentItemSelected mListener;

    private Activity activity;

    private RecyclerView recyclerView;

    private ProgressDialog pDialog;

    private int load_over = 0;



    public static ProfileFragment newInstance() {
      return new ProfileFragment();
    }

    public ProfileFragment() {}



    @Override
    public void onAttach(Context context) {
      super.onAttach(context);

      if (context instanceof onMyprofileFragmentItemSelected) {

        mListener = (onMyprofileFragmentItemSelected) context;

      } else {

        throw new ClassCastException(context.toString() + " must implement onProfileFragmentItemSelected.");

      }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      final View view = inflater.inflate(R.layout.activity_homepage_fragment_list, container, false);

      activity = getActivity();

      MP_RECYCLER_LAYOUT = (FrameLayout) view.findViewById(R.id.hp_frag_recyclerLayout);
      recyclerLayoutParams = (FrameLayout.LayoutParams) MP_RECYCLER_LAYOUT.getLayoutParams();

      MP_ROOT_LAYOUT = (FrameLayout) activity.findViewById(R.id.mp_root_layout);
      MP_LAYOUT = (FrameLayout) activity.findViewById(R.id.mp_layout);
      homePageLayoutParams = (FrameLayout.LayoutParams) MP_LAYOUT.getLayoutParams();
      MP_BUTTONS = (LinearLayout) activity.findViewById(R.id.mp_buttons);
      MP_POSTS = (Button) activity.findViewById(R.id.mp_postsbutton);
      MP_PROFILE = (Button) activity.findViewById(R.id.mp_profilebutton);
      MP_TOOLBAR = (FrameLayout) activity.findViewById(R.id.mp_toolbar);
      list = (ListView) activity.findViewById(R.id.mp_search_list);
      SEARCH_TEXT = (EditText) activity.findViewById(R.id.mp_search_bar);
      MP_SCROLL = (ScrollView) activity.findViewById(R.id.mp_scrollview);

      basicFunctions = new BasicFunctions(activity);

      post = new Post();

      recyclerView = (RecyclerView) view.findViewById(R.id.hp_frag_recycler_view);

      getInitialProfilePostsData();

      return view;

    }



    private void getInitialProfilePostsData() {

        StringRequest stringRequest = new StringRequest(EndPoints.GET_POST_PROFILE + Profile.PROFILE_USERNAME + "&index=" + 0, new Response.Listener<String>() {
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
            AllProfilePostsArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            post.setTotal(0);
            post.setTempTotal(0);

            for (int i = 0; i < AllProfilePostsArray.length(); i++) {
                JSONObject jsonObject = AllProfilePostsArray.getJSONObject(i);

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

                data.setTempDetails(hp_id, hp_picture_1, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username, hp_userimage,
                        hp_usercourse, hp_usertype);

                mProfilePosts.add(data);

                data.setTempTotal(mProfilePosts.size());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        myProfileFragmentAdapter = new MyProfileFragmentAdapter(activity);
        recyclerView.setAdapter(myProfileFragmentAdapter);


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


        MP_POSTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MP_LAYOUT.setBackground(null);
                MP_SCROLL.setVisibility(View.GONE);
                MP_ROOT_LAYOUT.setVisibility(View.VISIBLE);
                MP_POSTS.setVisibility(View.GONE);
                MP_PROFILE.setVisibility(View.VISIBLE);

                if (post.getTempTotal() == 0)
                    Toast.makeText(activity, "No Posts to display !", Toast.LENGTH_LONG).show();

            }
        });


        MP_PROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MP_LAYOUT.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.app_background_2, null));
                MP_SCROLL.setVisibility(View.VISIBLE);
                MP_ROOT_LAYOUT.setVisibility(View.GONE);
                MP_POSTS.setVisibility(View.VISIBLE);
                MP_PROFILE.setVisibility(View.GONE);
            }
        });


        myProfileFragmentAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                mProfilePosts.add(null);
                myProfileFragmentAdapter.notifyItemInserted(mProfilePosts.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pDialog = ProgressDialog.show(activity, "", "Fetching Details ... ", false, false);

                        mProfilePosts.remove(mProfilePosts.size() - 1);
                        myProfileFragmentAdapter.notifyItemRemoved(mProfilePosts.size());

                        int index = mProfilePosts.size();

                        getMoreProfilePostsData(index);

                        myProfileFragmentAdapter.notifyDataSetChanged();
                        myProfileFragmentAdapter.setLoaded();

                    }
                }, 1000);
            }
        });

    }


    private void getMoreProfilePostsData(int index) {

        StringRequest stringRequest = new StringRequest(EndPoints.GET_POST_PROFILE + Profile.PROFILE_USERNAME + "&index=" + index, new Response.Listener<String>() {
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
            AllProfilePostsArray = jsonObj.getJSONArray(basicFunctions.JSON_ARRAY);

            if(AllProfilePostsArray.length() == 0) {

                Toast.makeText(activity, "No more Posts to display !", Toast.LENGTH_LONG).show();
                load_over = 1;

            }

            for (int i = 0; i < AllProfilePostsArray.length(); i++) {
                JSONObject jsonObject = AllProfilePostsArray.getJSONObject(i);

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

                data.setTempDetails(hp_id, hp_picture_1, hp_title, hp_desc,
                        hp_type, hp_timestamp, hp_username, hp_userimage,
                        hp_usercourse, hp_usertype);

                mProfilePosts.add(data);

                data.setTempTotal(mProfilePosts.size());


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        myProfileFragmentAdapter.notifyDataSetChanged();

        pDialog.dismiss();

    }


    private interface OnLoadMoreListener {
        void onLoadMore();
    }


    class MyProfileFragmentAdapter extends RecyclerView.Adapter<ViewHolder> {

        private LayoutInflater mLayoutInflater;
        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;

        public MyProfileFragmentAdapter(Context context) {

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


        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View view = mLayoutInflater.inflate(R.layout.activity_homepage_fragment_item, viewGroup, false);
            return new ViewHolder(view);

        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            final Post data = mProfilePosts.get(position);

            viewHolder.setData(data.getTempId(), data.getTempImage(),
                    data.getTempTitle(), data.getTempDescription(), data.getTempType(),
                    data.getTempTimestamp(), data.getTempUsername(), data.getTempUserimage());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMyProfileFragmentItemSelected(data.getTempId());
                }
            });

        }

        @Override
        public int getItemCount() {
            return post.getTempTotal();
        }

        public void setLoaded() {
            isLoading = false;
        }

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
                           String timestamp, final String username, String userimage) {

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
          byte[] decodedString_1 = Base64.decode(userimage, Base64.DEFAULT);
          Bitmap decodedByte_1 = BitmapFactory.decodeByteArray(decodedString_1, 0, decodedString_1.length);


          mUserImageView.setImageBitmap(Bitmap.createScaledBitmap(decodedByte_1, 60, 60, false));


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
        builder.setMessage("Are you sure you want to delete this post ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }




    private void report(final String item_id, final String item_title){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        String method = "reportpost";

                        basicFunctions.performTask(method, item_id, "P : " + item_title);

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



    public interface onMyprofileFragmentItemSelected {
      void onMyProfileFragmentItemSelected(String id);
    }



    private void hideViews() {

        if(post.getTempTotal() > 5) {


            MP_TOOLBAR.animate().translationY(-MP_TOOLBAR.getHeight()).setInterpolator(new AccelerateInterpolator(2));

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) MP_BUTTONS.getLayoutParams();
            int buttonmargin = lp.bottomMargin;

            MP_BUTTONS.animate().translationY(MP_BUTTONS.getHeight() + buttonmargin).setInterpolator(new AccelerateInterpolator(2)).start();

            MP_PROFILE.animate().translationY(MP_PROFILE.getHeight() + buttonmargin).setInterpolator(new AccelerateInterpolator(2)).start();

            homePageLayoutParams.setMargins(0, 10, 0, 10);
            MP_RECYCLER_LAYOUT.setLayoutParams(homePageLayoutParams);

            list.setVisibility(View.GONE);

        }

    }


    private void showViews() {

        if(post.getTempTotal() > 5) {

            homePageLayoutParams.setMargins(0, 0, 0, 0);

            MP_TOOLBAR.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            MP_BUTTONS.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            MP_PROFILE.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            MP_RECYCLER_LAYOUT.setLayoutParams(recyclerLayoutParams);

            if (!SEARCH_TEXT.getText().toString().equals(""))
                list.setVisibility(View.VISIBLE);

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