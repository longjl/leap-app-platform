package cn.leap.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.leap.app.R;
import cn.leap.app.bean.Courses;

/**
 * Created by longjianlin on 14-8-21.
 * V 1.0
 * *********************************
 * Desc:
 * *********************************
 */
public class CourseAdapter extends BaseAdapter {
    private Context mContent;
    private LayoutInflater inflater;
    private List<Courses> mList;//课程列表

    public CourseAdapter() {
    }


    //测试数据
    public Courses courses;

    public CourseAdapter(Context context, List<Courses> list) {
        mContent = context;
        inflater = LayoutInflater.from(mContent);
        // mList = list;

        mList = new ArrayList<Courses>();
        for (int i = 0; i < 10; i++) {
            courses = new Courses();
            courses.id = i + 20;
            courses.title = "吉他教学" + i;
            courses.desc = "一流的吉他教学，leap首发出品.";
            courses.thumb = "hello";
            courses.channel_name = "音乐";
            courses.update_at = "2014-08-09";
            courses.update_info = "更新至第5集";
            courses.video_total = 30;
            mList.add(courses);
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_item, null);

            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.iv_thumb = (ImageView) convertView.findViewById(R.id.iv_thumb);
            holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
            holder.tv_update_info = (TextView) convertView.findViewById(R.id.tv_update_info);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Courses c = mList.get(position);

        holder.tv_title.setText(c.title);
        holder.tv_desc.setText(c.desc);
        holder.tv_update_info.setText(c.update_info);

        return convertView;
    }


    class ViewHolder {
        public ImageView iv_thumb;//缩略图
        public TextView tv_title;//课程标题
        public TextView tv_desc;//课程描述
        public TextView tv_update_info;//更新信息
    }
}
