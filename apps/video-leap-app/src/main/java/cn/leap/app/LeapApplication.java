package cn.leap.app;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.leap.app.activitys.BaseActivity;
import cn.leap.app.bean.Users;
import cn.leap.app.network.RequestManager;

/**
 * Created by longjianlin on 14-8-20.
 */
public class LeapApplication extends Application {
    private static final String TAG = LeapApplication.class.getSimpleName();
    private static List<BaseActivity> activityList;//activity list
    private static LeapApplication mApp; //creating a singleton
    private Users users;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");

        mApp = this;
        init();
    }

    public static synchronized LeapApplication getInstance() {
        return mApp;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users u) {
        this.users = u;
    }


    /**
     * init volley
     */
    private void init() {
        RequestManager.init(this);
    }

    /**
     * add activity
     */
    public static void addActivity(BaseActivity activity) {
        if (activityList == null || activityList.size() == 0) {
            activityList = new ArrayList<BaseActivity>();
        }
        activityList.add(activity);
    }

    /**
     * app sign out
     */
    public static void signOut() {
        if (activityList != null && activityList.size() > 0) {
            for (BaseActivity activity : activityList) {
                activity.finish();
            }
            System.exit(0);
        }
    }
}
