package cn.leap.app.activitys;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.actionbarsherlock.view.MenuItem;

import cn.leap.app.BaseActivity;
import cn.leap.app.R;

/**
 * course info
 * Created by longjianlin on 14-8-25.
 */
public class CoursesActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = CoursesActivity.class.getSimpleName();
    private ImageButton ib_start_learn;

    public CoursesActivity() {
        super(R.string.main_title);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.i(TAG, "onCreate()");

        setContentView(R.layout.course_layout);
        getSlidingMenu().setSlidingEnabled(false);//静止SlidingMenu滑动

        ib_start_learn = (ImageButton) findViewById(R.id.ib_start_learn);
        ib_start_learn.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == ib_start_learn.getId()) {//开始学习
            Intent intent = new Intent(CoursesActivity.this,VideoPlayerActivity.class);
            startActivity(intent);
        }
    }
}
