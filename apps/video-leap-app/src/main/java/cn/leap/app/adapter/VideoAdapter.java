package cn.leap.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.leap.app.R;
import cn.leap.app.bean.Videos;

/**
 * Created by longjianlin on 14-9-1.
 */
public class VideoAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<Videos> mVideos;       //视频列表
    private int selectItem;             //选中位置

    public VideoAdapter(Context context, List<Videos> videos) {
        this.mContext = context;
        this.mVideos = videos;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mVideos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.player_video_item, null);
            holder = new ViewHolder();
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            holder.tv_studying = (TextView) convertView.findViewById(R.id.tv_studying);
            holder.iv_thumb = (ImageView) convertView.findViewById(R.id.iv_thumb);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Videos v = mVideos.get(position);

        holder.tv_title.setText(v.title != null && v.title.length() > 15 ? v.title.substring(0, 15) + "..." : v.title);
        holder.tv_duration.setText(mVideos.get(position).duration);

        if (selectItem == position) {
            holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.home_new_hot_bgcolor));
            holder.tv_duration.setTextColor(mContext.getResources().getColor(R.color.home_new_hot_bgcolor));

            holder.tv_studying.setVisibility(View.VISIBLE);
            holder.iv_thumb.setImageResource(R.drawable.video_item_presses);
        } else {
            holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.text_color));
            holder.tv_duration.setTextColor(mContext.getResources().getColor(R.color.text_color));

            holder.tv_studying.setVisibility(View.GONE);
            holder.iv_thumb.setImageResource(R.drawable.video_item);
        }
        return convertView;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder {
        public TextView tv_title;
        public TextView tv_duration;//时长
        public TextView tv_studying;//正在学习
        public ImageView iv_thumb;//正在播放缩略图
    }


}
