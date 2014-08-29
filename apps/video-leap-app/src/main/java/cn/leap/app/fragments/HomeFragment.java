package cn.leap.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

import cn.leap.app.R;
import cn.leap.app.activitys.CoursesActivity;
import cn.leap.app.adapter.CourseAdapter;
import cn.leap.app.adapter.ImageAdapter;
import cn.leap.app.bean.Courses;
import cn.leap.app.common.Constants;
import cn.leap.app.network.RequestManager;
import cn.leap.app.widget.viewflow.CircleFlowIndicator;
import cn.leap.app.widget.viewflow.ViewFlow;

/**
 * Created by longjianlin on 14-8-21.
 */
public class HomeFragment extends Fragment implements
        PullToRefreshBase.OnRefreshListener2<ListView>,
        PullToRefreshBase.OnLastItemVisibleListener, AdapterView.OnItemClickListener {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private ViewFlow viewFlow;                              //滑动组件
    private CircleFlowIndicator indic;                      //滑动点组件

    private PullToRefreshListView mPullRefreshListView;     //PullToRefreshListView 下拉刷新组件
    private ListView listView;                              //ListView
    private View homeView;                                  //homeView layout

    private CourseAdapter adapter;                          //CourseAdapter 课程数据适配器
    private ImageAdapter imageAdapter;                      //ImageAdapter 图片轮播适配器

    private Context mContext;                               //Context
    private SlidingMenu mSlidingmenu;                       //SlidingMenu
    private ProgressBar pro_bar;                            //加载

    private LinkedList<Courses> mHomeList;                 //列表数据
    private LinkedList<Courses> mHotList;                  //课程推荐列表(轮播数据)

    /**
     * 构造方法
     *
     * @param context
     * @param slidingMenu
     */
    public HomeFragment(Context context, SlidingMenu slidingMenu) {
        mContext = context;
        mSlidingmenu = slidingMenu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.home_layout, null);
        pro_bar = (ProgressBar) homeView.findViewById(R.id.pro_bar);
        viewFlow = (ViewFlow) homeView.findViewById(R.id.viewflow);
        indic = (CircleFlowIndicator) homeView.findViewById(R.id.viewflowindic);
        mPullRefreshListView = (PullToRefreshListView) homeView.findViewById(R.id.pull_refresh_list);

        return homeView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setOnLastItemVisibleListener(this);
        mPullRefreshListView.setOnItemClickListener(this);

        listView = mPullRefreshListView.getRefreshableView();

        viewFlow.setFlowIndicator(indic);
        viewFlow.setSlidingMenu(mSlidingmenu);
        viewFlow.setTimeSpan(6000);
        viewFlow.setSelection(3 * 1000);    //设置初始位置

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (mHomeList == null) {
            mHomeList = new LinkedList<Courses>();
        }
        if (mHotList == null) {
            mHotList = new LinkedList<Courses>();
        }

        showProgressBar();//进度条
        getListHotCourse(); //推荐数据
        getListHomePageCourse(Constants.COURSE_ID_PARAM, Constants.NUM_PARAM, Constants.DOWN_PARAM); //课程列表
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        viewFlow.onConfigurationChanged(newConfig);
    }


    /**
     * last item
     * 混动到最底部
     */
    @Override
    public void onLastItemVisible() {
        // Toast.makeText(mContext, R.string.end, Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取首页课程列表信息
     *
     * @param id      课程编号(查询全部 id=0)
     * @param num     请求数量
     * @param gesture 手势(down:下拉 , up:上推)
     */
    private void getListHomePageCourse(final long id, final int num, final String gesture) {
        String url = Constants.LIST_HOME_PAGE_COURSES_URL + "?id=" + id + "&num=" + num + "&gesture=" + gesture;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mPullRefreshListView.onRefreshComplete(); //调用下拉刷新处理完成方法
                        hideProgressBar();//隐藏加载进度条

                        String msg = response.optString(Constants.MSG_KEY);
                        if (!msg.equals(Constants.MSG_VALUE)) return;

                        JSONObject jsonObject = response.optJSONObject(Constants.DATA_KEY);
                        JSONArray jsonArray = jsonObject.optJSONArray(Constants.RECODERS_KEY);
                        if (jsonArray == null || jsonArray.length() == 0) {
                            if (Constants.UP_PARAM.equals(gesture)) {
                                Toast.makeText(mContext, R.string.load_data_done, Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject courseJson = jsonArray.optJSONObject(i);
                            Courses c = new Courses();
                            c.id = courseJson.optLong(Constants.ID_KEY);
                            c.title = courseJson.optString(Constants.TITLE_KEY);
                            c.desc = courseJson.optString(Constants.DESC_KEY);
                            c.thumb = courseJson.optString(Constants.THUMB_KEY);
                            c.teacher_name = courseJson.optString(Constants.TEACHER_NAME_KEY);
                            c.video_total = courseJson.optInt(Constants.VIDEO_TOTAL_KEY);
                            c.update_at = courseJson.optString(Constants.UPDATE_AT_KEY);
                            c.intro = courseJson.optString(Constants.INTRO_KEY);

                            if (id == 0) {//首页加载(没有触发手势操作)
                                mHomeList.add(c);
                            } else if (Constants.DOWN_PARAM.equals(gesture)) {//下拉
                                mHomeList.addFirst(c);
                            } else if (Constants.UP_PARAM.equals(gesture)) {//上推
                                mHomeList.addLast(c);
                            }
                        }

                        if (id == 0 || adapter == null) {
                            adapter = new CourseAdapter(mContext, mHomeList);
                            listView.setAdapter(adapter);
                        } else if (Constants.DOWN_PARAM.equals(gesture) && adapter != null) {//下拉
                            Toast.makeText(mContext, "更新" + jsonArray.length() + "条数据", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        } else if (Constants.UP_PARAM.equals(gesture) && adapter != null) {//上推
                            adapter.notifyDataSetChanged();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, R.string.network_exception, Toast.LENGTH_SHORT).show();
                mPullRefreshListView.onRefreshComplete();
                hideProgressBar();//隐藏加载进度条
                //Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        RequestManager.addRequest(jsonObjReq, Constants.TAG_JSON_LIST_HOME_PAGE_COURSE);
    }

    /**
     * 获取课程推荐信息(用于图片轮播)
     * 默认获取10条
     */
    private void getListHotCourse() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Constants.LIST_HOT_COURSE_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String msg = response.optString(Constants.MSG_KEY);
                        if (!msg.equals(Constants.MSG_VALUE)) return;

                        JSONObject jsonObject = response.optJSONObject(Constants.DATA_KEY);
                        JSONArray jsonArray = jsonObject.optJSONArray(Constants.RECODERS_KEY);
                        if (jsonArray == null || jsonArray.length() == 0) return;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject courseJson = jsonArray.optJSONObject(i);
                            Courses c = new Courses();
                            c.id = courseJson.optLong(Constants.ID_KEY);
                            c.title = courseJson.optString(Constants.TITLE_KEY);
                            c.desc = courseJson.optString(Constants.DESC_KEY);
                            c.thumb = courseJson.optString(Constants.THUMB_KEY);
                            c.teacher_name = courseJson.optString(Constants.TEACHER_NAME_KEY);
                            c.video_total = courseJson.optInt(Constants.VIDEO_TOTAL_KEY);
                            c.update_at = courseJson.optString(Constants.UPDATE_AT_KEY);
                            c.intro = courseJson.optString(Constants.INTRO_KEY);

                            mHotList.add(c);
                        }

                        imageAdapter = new ImageAdapter(mContext, mHotList);
                        viewFlow.setAdapter(imageAdapter);//0 表示从索引0开始（第一个开始）
                        viewFlow.setmSideBuffer(mHotList.size());
                        viewFlow.startAutoFlowTimer();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                Toast.makeText(mContext, R.string.network_exception, Toast.LENGTH_SHORT).show();
            }
        });
        RequestManager.addRequest(jsonObjReq, Constants.TAG_JSON_HOT_COURSE);
    }


    /**
     * 下拉刷新
     *
     * @param refreshView
     */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mHomeList == null || mHomeList.size() == 0) { //如果课程列表信息为空,那么我们需要请求最新的数据
            getListHomePageCourse(Constants.COURSE_ID_PARAM, Constants.NUM_PARAM, Constants.DOWN_PARAM);
        } else {
            getListHomePageCourse(mHomeList.getFirst().id, Constants.NUM_PARAM, Constants.DOWN_PARAM);//执行下拉刷新
        }
    }


    /**
     * 上推刷新
     *
     * @param refreshView
     */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mHomeList == null || mHomeList.size() == 0) { //如果课程列表信息为空,那么我们需要请求最新的数据
            getListHomePageCourse(Constants.COURSE_ID_PARAM, Constants.NUM_PARAM, Constants.DOWN_PARAM);
        } else {
            getListHomePageCourse(mHomeList.getLast().id, Constants.NUM_PARAM, Constants.UP_PARAM);//执行下拉刷新
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick position is " + position);
        if (position == 0) return;

        Courses courses = (Courses) adapter.getItem(position - 1);
        Intent intent = new Intent(mContext, CoursesActivity.class);
        intent.putExtra(Constants.COURSE_ID_KEY, courses.id);
        startActivity(intent);
    }

    /**
     * 显示ProgressBar
     */
    private void showProgressBar() {
        if (pro_bar != null) pro_bar.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏ProgressBar
     */
    private void hideProgressBar() {
        if (pro_bar != null) pro_bar.setVisibility(View.GONE);
    }

}
