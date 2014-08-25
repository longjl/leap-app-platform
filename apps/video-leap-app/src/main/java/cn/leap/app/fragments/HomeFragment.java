package cn.leap.app.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

import cn.leap.app.R;
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
public class HomeFragment extends Fragment implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener, PullToRefreshBase.OnLastItemVisibleListener {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private ViewFlow viewFlow;                              //滑动组件
    private CircleFlowIndicator indic;                      //滑动点组件
    private LinkedList<Courses> mListCourse;                 //列表数据
    private PullToRefreshListView mPullRefreshListView;     //PullToRefreshListView 下拉刷新组件
    private CourseAdapter adapter;                          //CourseAdapter 课程数据适配器
    private ListView listView;                              //ListView
    private View home;                                      //view layout
    private Context mContext;                               //Context
    private SlidingMenu mSlidingmenu;                       //SlidingMenu

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
        home = inflater.inflate(R.layout.home_layout, null);
        viewFlow = (ViewFlow) home.findViewById(R.id.viewflow);
        indic = (CircleFlowIndicator) home.findViewById(R.id.viewflowindic);
        mPullRefreshListView = (PullToRefreshListView) home.findViewById(R.id.pull_refresh_list);
        return home;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewFlow.setAdapter(new ImageAdapter(getActivity()), 0);//0 表示从索引0开始（第一个开始）
        viewFlow.setFlowIndicator(indic);
        viewFlow.setSlidingMenu(mSlidingmenu);

        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setOnLastItemVisibleListener(this);

        listView = mPullRefreshListView.getRefreshableView();

        if (mListCourse == null) {
            mListCourse = new LinkedList<Courses>();
        }
        requestCourseData(false);//请求数据
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        viewFlow.onConfigurationChanged(newConfig);
    }

    /**
     * 刷新
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        requestCourseData(true);
    }

    /**
     * last item
     */
    @Override
    public void onLastItemVisible() {
        Toast.makeText(mContext, R.string.end, Toast.LENGTH_SHORT).show();
    }


    /**
     * request data
     *
     * @param isRefresh 表示是否下拉刷新
     *                  true  是
     *                  false 否
     */
    private void requestCourseData(final boolean isRefresh) {
        JsonArrayRequest req = new JsonArrayRequest(Constants.URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        if (response != null && response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.optJSONObject(i);
                                Courses courses = new Courses();
                                courses.id = i + 1;
                                courses.title = obj.optString("name");
                                courses.desc = obj.optString("email");
                                courses.thumb = "http://image.leap.cn/back/2083aa72-289b-443c-a5ea-c3ff2da74241.png";
                                courses.update_info = "更新至第" + (10 + i) + "集";
                                if (isRefresh) {//如果是下拉刷新，那么需要把数据添加在最前面
                                    mListCourse.addFirst(courses);
                                } else {
                                    mListCourse.add(courses);
                                }
                                //Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (isRefresh && adapter != null) {//下拉刷新
                            Log.i(TAG, "PullRefresh is true");
                            Toast.makeText(mContext, "更新" + response.length() + "条数据", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.i(TAG, "PullRefresh is false");
                            adapter = new CourseAdapter(mContext, mListCourse);
                            listView.setAdapter(adapter);
                        }
                        mPullRefreshListView.onRefreshComplete(); //调用下拉刷新处理完成方法
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, R.string.network_exception, Toast.LENGTH_SHORT).show();
                        mPullRefreshListView.onRefreshComplete();
                    }
                });
        RequestManager.addRequest(req, Constants.TAG_JSON_ARRAY);
    }


}
