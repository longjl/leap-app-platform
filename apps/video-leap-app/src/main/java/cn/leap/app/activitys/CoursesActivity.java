package cn.leap.app.activitys;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.leap.app.BaseActivity;
import cn.leap.app.R;
import cn.leap.app.adapter.ImageAdapter;
import cn.leap.app.bean.Courses;
import cn.leap.app.common.Constants;
import cn.leap.app.network.RequestManager;
import cn.leap.app.util.DateUtil;

/**
 * course info
 * Created by longjianlin on 14-8-25.
 */
public class CoursesActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = CoursesActivity.class.getSimpleName();
    private ImageLoader imageLoader;

    private ImageView iv_thumb;                 //课程封面
    private TextView tv_title;                  //课程标题
    private TextView tv_teacher_name;           //教师名字
    private TextView tv_update_at;              //更新时间
    private TextView tv_intro;                  //老师简介
    private TextView tv_desc;                   //课程简介

    private ImageButton ib_start_learn;         //开始学习

    private String course_id;                   //课程编号

    public CoursesActivity() {
        super(R.string.main_title);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.i(TAG, "onCreate()");

        setContentView(R.layout.course_layout);
        getSlidingMenu().setSlidingEnabled(false);//静止SlidingMenu滑动

        iv_thumb = (ImageView) findViewById(R.id.iv_thumb);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_teacher_name = (TextView) findViewById(R.id.tv_teacher_name);
        tv_update_at = (TextView) findViewById(R.id.tv_update_at);
        tv_intro = (TextView) findViewById(R.id.tv_intro);
        tv_desc = (TextView) findViewById(R.id.tv_desc);

        ib_start_learn = (ImageButton) findViewById(R.id.ib_start_learn);
        ib_start_learn.setOnClickListener(this);

        initData();
    }


    private void initData() {
        imageLoader = RequestManager.getImageLoader();
        course_id = getIntent().getStringExtra(Constants.INTENT_COURSE_ID_KEY);
        getListOneCourse(course_id);
    }

    /**
     * 根据课程编号查询课程和老师信息
     */
    private void getListOneCourse(final String id) {
        String url = Constants.LIST_ONE_COURSE_URL + "?id=" + id;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String msg = response.optString(Constants.MSG_KEY);
                        if (!msg.equals(Constants.MSG_VALUE)) return;

                        JSONObject jsonObject = response.optJSONObject(Constants.DATA_KEY);
                        JSONArray jsonArray = jsonObject.optJSONArray(Constants.RECODERS_KEY);
                        if (jsonArray == null || jsonArray.length() == 0) {
                            Toast.makeText(CoursesActivity.this, R.string.not_found_course, Toast.LENGTH_SHORT).show();
                            onBackPressed();//返回
                            return;
                        }

                        JSONObject courseJson = jsonArray.optJSONObject(0);
                        Courses c = new Courses();
                        c.id = courseJson.optLong(Constants.ID_KEY);
                        c.title = courseJson.optString(Constants.TITLE_KEY);
                        c.desc = courseJson.optString(Constants.DESC_KEY);
                        c.thumb = courseJson.optString(Constants.THUMB_KEY);
                        c.teacher_name = courseJson.optString(Constants.TEACHER_NAME_KEY);
                        c.video_total = courseJson.optInt(Constants.VIDEO_TOTAL_KEY);
                        c.update_at = courseJson.optString(Constants.UPDATE_AT_KEY);
                        c.intro = courseJson.optString(Constants.INTRO_KEY);

                        tv_title.setText(c.title);
                        tv_desc.setText(c.desc);
                        tv_teacher_name.setText(c.teacher_name);
                        tv_update_at.setText(c.update_at);
                        tv_intro.setText(c.intro);

                        imageLoader.get(c.thumb, new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response != null) {
                                    iv_thumb.setImageBitmap(response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Image Load Error: " + error.getMessage());
                                iv_thumb.setImageResource(R.drawable.home_03);//图片加载失败 , 赋给一张默认的图片
                            }
                        });


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(CoursesActivity.this, R.string.network_exception, Toast.LENGTH_SHORT).show();
            }
        });
        RequestManager.addRequest(jsonObjReq, Constants.TAG_JSON_ONE_COURSE);
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
            Intent intent = new Intent(CoursesActivity.this, VideoPlayerActivity.class);
            intent.putExtra(Constants.INTENT_COURSE_ID_KEY, course_id);//课程编号
            startActivity(intent);
        }
    }
}
