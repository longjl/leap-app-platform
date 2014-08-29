/*
 * Copyright (C) 2011 Patrik �kerfeldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.LinkedList;

import cn.leap.app.R;
import cn.leap.app.activitys.CoursesActivity;
import cn.leap.app.bean.Courses;
import cn.leap.app.common.Constants;
import cn.leap.app.network.RequestManager;

public class ImageAdapter extends BaseAdapter {
    private static final String TAG = ImageAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private Context mContext;
    private LinkedList<Courses> mHotList;
    private ImageLoader imageLoader;

    public ImageAdapter(Context context, LinkedList<Courses> hotList) {
        mContext = context;
        mHotList = hotList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = RequestManager.getImageLoader();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;//返回很大的值使得getView中的position不断增大来实现循环
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.image_item, null);

            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.iv_thumb = (ImageView) convertView.findViewById(R.id.iv_thumb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Courses courses = mHotList.get(position % mHotList.size());
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

        holder.tv_title.setText(courses.title);
        holder.iv_thumb.setOnClickListener(new ThumbOnClickListener(courses.id));
        return convertView;
    }


    class ThumbOnClickListener implements View.OnClickListener {
        private long course_id; //课程编号

        public ThumbOnClickListener(long id) {
            course_id = id;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, CoursesActivity.class);
            intent.putExtra(Constants.INTENT_COURSE_ID_KEY, String.valueOf(course_id));//课程编号
            mContext.startActivity(intent);
        }
    }

    class ViewHolder {
        public ImageView iv_thumb; //缩略图
        public TextView tv_title;  //课程标题
    }


}
