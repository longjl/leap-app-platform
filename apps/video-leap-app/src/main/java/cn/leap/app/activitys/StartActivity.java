package cn.leap.app.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import cn.leap.app.LeapApplication;
import cn.leap.app.R;
import cn.leap.app.bean.Users;
import cn.leap.app.common.Constants;

/**
 * Created by longjianlin on 14-9-3.
 */
public class StartActivity extends BaseActivity {
    private static String TAG = StartActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    public StartActivity() {
        super(R.string.main_title);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "*************************************** onCreate()");

        final View view = View.inflate(this, R.layout.start_layout, null);
        setContentView(view);
        getSlidingMenu().setSlidingEnabled(false);
        getSupportActionBar().hide();
        sharedPreferences = this.getSharedPreferences(Constants.LOGIN_PREFERENCES_KEY, Context.MODE_PRIVATE);
        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });
    }


    /**
     * 跳转到...
     */
    private void redirectTo() {
        Intent intent;
        String email = sharedPreferences.getString(Constants.EMAIL_PREFERENCES_KEY, null);
        if (email != null) {
            Users users = new Users();
            users.uid = Long.valueOf(sharedPreferences.getString(Constants.UID_PREFERENCES_KEY, "0"));
            users.email = email;
            users.nickname = sharedPreferences.getString(Constants.NICKNAME_PREFERENCES_KEY, null);
            LeapApplication.getInstance().setUsers(users);
            Log.i(TAG, "***********uid:" + users.uid + "-email:" + users.email + "-nickname:" + users.nickname);
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
