/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package cn.leap.app.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.leap.app.R;
import cn.leap.app.activitys.CoursesActivity;
import cn.leap.app.bean.Courses;
import cn.leap.app.common.Constants;
import cn.leap.app.network.RequestManager;
import cn.leap.app.widget.viewflowpager.salvage.RecyclingPagerAdapter;

/**
 * ImagePagerAdapter
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter implements View.OnTouchListener{
    private static final String TAG = ImagePagerAdapter.class.getSimpleName();
    private Context context;

    private LinkedList<Courses> mHotList;
    private ImageLoader imageLoader;
    private int size;
    private boolean isInfiniteLoop;
    private SlidingMenu mSlidingMenu;

    public ImagePagerAdapter(Context context, LinkedList<Courses> hotList, SlidingMenu slidingMenu) {
        this.context = context;
        this.mHotList = hotList;
        this.size = mHotList.size();
        this.isInfiniteLoop = false;
        this.imageLoader = RequestManager.getImageLoader();
        this.mSlidingMenu = slidingMenu;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : mHotList.size();
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.image_item, null);

            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.iv_thumb = (ImageView) convertView.findViewById(R.id.iv_thumb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Courses courses = mHotList.get(position);
        holder.tv_title.setText(courses.title);

        holder.iv_thumb.setOnClickListener(new ThumbOnClickListener(courses.id));
        holder.iv_thumb.setOnTouchListener(this);

        imageLoader.get(courses.thumb, new ImageLoader.ImageListener() {
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
        public ImageView iv_thumb; //缩略图
        public TextView tv_title;  //课程标题
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mSlidingMenu.setSlidingEnabled(false);
        return false;
    }


    class ThumbOnClickListener implements View.OnClickListener {
        private long course_id; //课程编号

        public ThumbOnClickListener(long id) {
            course_id = id;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, CoursesActivity.class);
            intent.putExtra(Constants.INTENT_COURSE_ID_KEY, String.valueOf(course_id));//课程编号
            context.startActivity(intent);
        }
    }

}
