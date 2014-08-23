package cn.leap.app.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.leap.app.R;
import cn.leap.app.adapter.CourseAdapter;
import cn.leap.app.adapter.ImageAdapter;
import cn.leap.app.widget.viewflow.CircleFlowIndicator;
import cn.leap.app.widget.viewflow.ViewFlow;

/**
 * Created by longjianlin on 14-8-21.
 * V 1.0
 * *********************************
 * Desc:
 * *********************************
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private ViewFlow viewFlow;
    private CircleFlowIndicator indic;

    private CourseAdapter adapter;
    private ListView lv_new;//最新 ListView


    private View home;
    private Context mContext;
    private SlidingMenu mSlidingmenu;

    public HomeFragment(Context context, SlidingMenu slidingMenu) {
        mContext = context;
        mSlidingmenu = slidingMenu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        home = inflater.inflate(R.layout.home_layout, null);
        lv_new = (ListView) home.findViewById(R.id.lv_new);
        viewFlow = (ViewFlow) home.findViewById(R.id.viewflow);
        indic = (CircleFlowIndicator) home.findViewById(R.id.viewflowindic);


        return home;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new CourseAdapter(getActivity(), null);
        lv_new.setAdapter(adapter);

        viewFlow.setAdapter(new ImageAdapter(getActivity()), 0);//0 表示从索引0开始（第一个开始）
        viewFlow.setFlowIndicator(indic);
        viewFlow.setSlidingMenu(mSlidingmenu);

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        viewFlow.onConfigurationChanged(newConfig);
    }
}
