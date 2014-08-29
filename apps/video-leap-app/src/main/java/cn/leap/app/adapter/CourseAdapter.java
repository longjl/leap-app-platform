package cn.leap.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.LinkedList;

import cn.leap.app.R;
import cn.leap.app.bean.Courses;
import cn.leap.app.network.RequestManager;

/**
 * 课程数据适配器
 * Created by longjianlin on 14-8-21.
 */
public class CourseAdapter extends BaseAdapter {
    private static final String TAG = CourseAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater inflater;
    private LinkedList<Courses> mHomeList;//课程列表
    private ImageLoader imageLoader;


    public CourseAdapter(Context context, LinkedList<Courses> homeList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        mHomeList = homeList;
        imageLoader = RequestManager.getImageLoader();
    }

    @Override
    public int getCount() {
        return mHomeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mHomeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
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

        Courses c = mHomeList.get(position);
        holder.tv_title.setText(c.title != null && c.title.length() > 12 ? c.title.substring(0, 12) + "..." : c.title);
        holder.tv_desc.setText(c.desc != null && c.desc.length() > 30 ? c.desc.substring(0, 30) + "..." : c.desc);
        holder.tv_update_info.setText("更新至第" + c.video_total + "集");

        imageLoader.get(c.thumb, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response != null) {
                    holder.iv_thumb.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: " + error.getMessage());
                holder.iv_thumb.setImageResource(R.drawable.home_03);//图片加载失败 , 赋给一张默认的图片
            }
        });
        return convertView;
    }




    class ViewHolder {
        public ImageView iv_thumb;//缩略图
        public TextView tv_title;//课程标题
        public TextView tv_desc;//课程描述
        public TextView tv_update_info;//更新信息
    }
}
