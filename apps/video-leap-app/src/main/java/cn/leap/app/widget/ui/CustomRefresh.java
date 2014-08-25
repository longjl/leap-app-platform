package cn.leap.app.widget.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import cn.leap.app.R;

/**
 * 自定义刷新组件
 * Created by longjianlin on 14-8-24.
 */
public class CustomRefresh extends View {
    private ImageView refreshView;
    private Context mContext;
    private Animation animation;
    private View view;

    public CustomRefresh(Context context) {
        super(context);
        mContext = context;
    }

    public CustomRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        view = LayoutInflater.from(mContext).inflate(R.layout.custom_refresh, null);
        refreshView = (ImageView) view.findViewById(R.id.iv_refresh);
       // animation = AnimationUtils.loadAnimation(mContext, R.anim.refresh);
    }

    /**
     * show refresh
     */
    public void showRefresh() {
        if (animation != null) {
          //  refreshView.setAnimation(animation);
        }
    }

}
