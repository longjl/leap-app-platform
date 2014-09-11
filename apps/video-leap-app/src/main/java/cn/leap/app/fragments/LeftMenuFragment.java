package cn.leap.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.leap.app.LeapApplication;
import cn.leap.app.R;
import cn.leap.app.bean.Users;
import cn.leap.app.widget.ui.CircleImageView;


/**
 * Created by longjianlin on 14-7-30.
 * V 1.0
 * *********************************
 * Desc: 左侧menu
 * *********************************
 */
public class LeftMenuFragment extends Fragment implements View.OnClickListener {
    private TextView tv_uname;
    private CircleImageView civ_thumb;
    private LinearLayout ll_exit;
    private View view;
    private Users u;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.left_menu_layout, null);

        tv_uname = (TextView) view.findViewById(R.id.tv_uname);
        ll_exit = (LinearLayout) view.findViewById(R.id.ll_exit);
        civ_thumb = (CircleImageView) view.findViewById(R.id.civ_thumb);

        u = LeapApplication.getInstance().getUsers();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (u != null) {
            tv_uname.setText(u.nickname);
        }

        ll_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ll_exit.getId()) {
            LeapApplication.getInstance().signOut();
        }
    }
}
