package fmt.febulous.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import fmt.febulous.model.ChatUser;
import io.fabric.sdk.android.Fabric;


public class Application extends android.app.Application {


    private RequestQueue mRequestQueue;

    private static Application mInstance;

    private ChatPreferenceManager pref;

    BasicFunctions basicFunctions;


    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this;

        basicFunctions = new BasicFunctions(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(basicFunctions.TWITTER_KEY, basicFunctions.TWITTER_SECRET);

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Twitter(authConfig), new Crashlytics())
                .debuggable(true)
                .build();

        Fabric.with(fabric);

    }

    public static synchronized Application getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ChatPreferenceManager getPrefManager() {

        if (pref == null) {
            pref = new ChatPreferenceManager(this);
        }

        return pref;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);

    }


    public class ChatPreferenceManager {

        SharedPreferences CHAT_PREF;

        SharedPreferences.Editor CHAT_EDITOR;

        Context CHAT_CONTEXT;

        int CHAT_PRIVATE_MODE = 0;

        private static final String CHAT_PREF_NAME = "FEBULOUS";

        private static final String KEY_USER_ID = "user_id";
        private static final String KEY_USER_NAME = "username";
        private static final String KEY_USER_IMAGE = "userimage";

        public ChatPreferenceManager(Context context) {

            this.CHAT_CONTEXT = context;
            CHAT_PREF = CHAT_CONTEXT.getSharedPreferences(CHAT_PREF_NAME, CHAT_PRIVATE_MODE);
            CHAT_EDITOR = CHAT_PREF.edit();

        }

        public void storeUser(ChatUser chatUser) {

            CHAT_EDITOR.putString(KEY_USER_ID, chatUser.getId());
            CHAT_EDITOR.putString(KEY_USER_NAME, chatUser.getUser_name());
            CHAT_EDITOR.putString(KEY_USER_IMAGE, chatUser.getUser_image());
            CHAT_EDITOR.commit();

        }

        public ChatUser getUser() {

            if (CHAT_PREF.getString(KEY_USER_ID, null) != null) {

                String user_id, user_name, user_image;

                user_id = CHAT_PREF.getString(KEY_USER_ID, null);
                user_name = CHAT_PREF.getString(KEY_USER_NAME, null);
                user_image = CHAT_PREF.getString(KEY_USER_IMAGE, null);

                ChatUser chatUser = new ChatUser(user_id, user_name, user_image);
                return chatUser;
            }

            return null;
        }

    }

}