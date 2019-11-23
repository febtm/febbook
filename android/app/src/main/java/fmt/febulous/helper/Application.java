package fmt.febulous.helper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.MobileAds;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;


public class Application extends android.app.Application {


    private RequestQueue mRequestQueue;

    private static Application mInstance;

    BasicFunctions basicFunctions;


    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this;

        basicFunctions = new BasicFunctions(this);

        AppEventsLogger.activateApp(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(basicFunctions.TWITTER_KEY, basicFunctions.TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6196885651315287~5451223851");

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

    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);

    }
    
}