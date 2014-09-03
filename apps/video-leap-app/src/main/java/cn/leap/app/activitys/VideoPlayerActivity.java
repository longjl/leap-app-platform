package cn.leap.app.activitys;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mcxiaoke.popupmenu.PopupMenuCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.leap.app.BaseActivity;
import cn.leap.app.R;
import cn.leap.app.adapter.VideoAdapter;
import cn.leap.app.bean.Videos;
import cn.leap.app.common.Constants;
import cn.leap.app.network.RequestManager;
import cn.leap.app.util.DateUtil;

/**
 * 播放器
 * Created by longjianlin on 14-8-25.
 */
public class VideoPlayerActivity extends BaseActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener
        , MediaPlayer.OnBufferingUpdateListener, AdapterView.OnItemClickListener {
    private static final String TAG = "VideoPlayerActivity";
    //private String videoUrl = "http://daily3gp.com/vids/family_guy_penis_car.3gp";//视频播放地址
    //private String videoUrl = "/sdcard/MIUI/Gallery/DemoVideo/XiaomiPhone.mp4";//视频播放地址
    //private String videoUrl = "http://video.leap.cn/20140810/86046d9b6fe0f9788112e02972457072.mp4";
    //private String videoUrl = "/sdcard/DCIM/Camera/20140819_173958.mp4";//视频播放地址
    private String videoUrl = "http://video.leap.cn/20140812/7dfced334689b427610ace78a958fb18_0.f4v";

    private RelativeLayout rl_loading;      //视频加载
    private ProgressBar pro_bar;            //缓冲加载

    private SurfaceView surfaceview;        //SurfaceView 视频画面输出
    private SeekBar seekBar;                //视频播放进度条
    private TextView tv_current_time;       //播放当前时间
    private TextView tv_duration;           //视频总时长
    private ImageButton ib_play;            //播放,暂停 按钮
    private LinearLayout ll_tools;          //视频播放器工具条
    private ImageButton ib_fullscreen;      //全屏
    private LinearLayout ll_content;        //内容容器
    private MediaPlayer mediaPlayer;        //MediaPlayer 播放器视频
    private Button btn_horsepower;          //马力
    private TextView tv_title;              //视频标题

    private ListView lv_videos;             //视频列表 listview

    private int currentPosition = 0;        //当前播放位置
    private int duration = 0;               //视频总时长
    private boolean isPlaying;              //是否正在播放视频
    private boolean isFullScreen = false;   //是否全屏

    private int videoWidth;                 //视频宽度
    private int videoHeight;                //视频高度
    private int videoPosition;              //当前播放视频的位置
    private int mHorsePowerStatus;           //马力播放状态  0:标清 , 1:高清

    private String course_id;               //课程编号


    public VideoPlayerActivity() {
        super(R.string.main_title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSlidingMenu().setSlidingEnabled(false);//静止滑动
        getSupportActionBar().hide();

        setContentView(R.layout.player_layout);

        lv_videos = (ListView) findViewById(R.id.lv_videos);

        rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
        pro_bar = (ProgressBar) findViewById(R.id.pro_bar);

        surfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_current_time = (TextView) findViewById(R.id.tv_currenttime);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ib_play = (ImageButton) findViewById(R.id.ib_play);
        ll_tools = (LinearLayout) findViewById(R.id.ll_tools);
        ib_fullscreen = (ImageButton) findViewById(R.id.ib_fullscreen);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        btn_horsepower = (Button) findViewById(R.id.btn_horsepower);

        //为SurfaceView添加回调函数
        surfaceview.getHolder().addCallback(this);

        ib_play.setOnClickListener(this);//播放,暂停按钮监听事件
        seekBar.setOnSeekBarChangeListener(this);//进度条监听事件
        ib_fullscreen.setOnClickListener(this);//全屏点击事件
        surfaceview.setOnClickListener(this);//点击事件
        btn_horsepower.setOnClickListener(this);//马力

        lv_videos.setOnItemClickListener(this);

        initData();
    }


    /**
     * *******************视频播放器 SurfaceView Callback 回调函数 start****************************
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "*************************** surfaceCreated(SurfaceHolder holder)");

        mediaPlayer = new MediaPlayer();
        //设置边播放变缓冲
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置显示视频的SurfaceHolder
        mediaPlayer.setDisplay(surfaceview.getHolder());
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "*************************** surfaceDestroyed(SurfaceHolder holder)");
        //销毁SurfaceHolder的时候记录当前的播放位置并停止播放
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
    }

    /**
     * *******************视频播放器 SurfaceView Callback 回调函数 end****************************
     */


    private void play(String videoUrl, int horsePowerStatus) {
        Log.i(TAG, "**********************  play(String videoUrl) , 调用开始播放方法.");
        try {
            surfaceview.setClickable(false);
            showVideoLoading();
            hideTools();
            pro_bar.setVisibility(View.GONE);

            mHorsePowerStatus = horsePowerStatus;//马力
            if (mHorsePowerStatus == 0) {
                btn_horsepower.setText(R.string.sd);
            } else if (mHorsePowerStatus == 1) {
                btn_horsepower.setText(R.string.hd);
            }

            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepareAsync();//开始装载
        } catch (IOException e) {
            Log.i(TAG, "********************** " + e.getMessage());
            //e.printStackTrace();
        }
    }


    /**
     * MediaPlayer 装载回调方法
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "********************** onPrepared(MediaPlayer mp) , 装载完成开始播放视频.");
        videoWidth = mediaPlayer.getVideoWidth();
        videoHeight = mediaPlayer.getVideoHeight();

        if (videoWidth > 0 && videoHeight > 0) {
            handler.sendEmptyMessage(1);
        } else {
            Toast.makeText(VideoPlayerActivity.this, "视频不能播放", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param msg
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:  //更新当前播放时间
                    if (mediaPlayer != null && mediaPlayer.isPlaying())
                        tv_current_time.setText(String.valueOf(DateUtil.dateFormat.format(mediaPlayer.getCurrentPosition())));
                    break;
                case 1:
                    hideVideoLoading();
                    surfaceview.setClickable(true);
                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentPosition); //按照初始位置播放
                    currentPosition = 0;
                    duration = mediaPlayer.getDuration();//获取视频总时长
                    tv_duration.setText(String.valueOf(DateUtil.dateFormat.format(duration)));
                    seekBar.setMax(duration);// 设置进度条的最大进度为视频流的最大播放时长

                    //开始线程，更新进度条的刻度
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                isPlaying = true;
                                while (isPlaying) {
                                    int current = mediaPlayer.getCurrentPosition();
                                    handler.sendEmptyMessage(0);
                                    seekBar.setProgress(current);
                                    sleep(500);
                                }
                            } catch (Exception e) {
                                Log.e("SeekBar setProgress error", e.getMessage());
                            }
                        }
                    }.start();
                    break;
                case 2:
                    if (videosList != null && videosList.size() > 0) {
                        play(videosList.get(videoPosition).sd, 0);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };


    /**
     * *******************进度条回调事件 SeekBar.OnSeekBarChangeListener start****************************
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 当进度条停止修改的时候触发
        // 取得当前进度条的刻度
        int progress = seekBar.getProgress();
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.seekTo(progress); // 设置当前播放的位置
        }
    }

    /**
     * *******************进度条回调事件 SeekBar.OnSeekBarChangeListener end****************************
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == ib_play.getId()) {//视频播放
            if (ib_play.isSelected()) {//播放
                ib_play.setImageResource(R.drawable.ib_stop);
                ib_play.setSelected(false);
                if (isPlaying) {
                    mediaPlayer.start();
                }
            } else {//暂停
                ib_play.setImageResource(R.drawable.ib_player);
                ib_play.setSelected(true);
                if (isPlaying) {
                    mediaPlayer.pause();
                }
            }
        } else if (v.getId() == ib_fullscreen.getId()) {//全屏播放
            if (isFullScreen) {//当前处于全屏状态
                ib_fullscreen.setImageResource(R.drawable.ib_no_fullscreen);
                isFullScreen = false;
                ll_content.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                getSupportActionBar().show();
                horsepowerVisibility(false);//隐藏马力
            } else {//不是处于全屏状态
                ib_fullscreen.setImageResource(R.drawable.ib_fullscreen);
                isFullScreen = true;
                ll_content.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
                getSupportActionBar().hide();
                horsepowerVisibility(true);//显示马力
            }
        } else if (v.getId() == surfaceview.getId()) {
            if (ll_tools.getVisibility() == View.GONE) {//隐藏
                showTools();
            } else if (ll_tools.getVisibility() == View.VISIBLE) {//显示
                hideTools();
            }
        } else if (v.getId() == btn_horsepower.getId()) {//马力
            showPopupMenu(btn_horsepower);
        }
    }

    /**
     * 防止横竖屏切换的时候重新加载Activity
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //Toast.makeText(VideoPlayerActivity.this, "new Config", Toast.LENGTH_SHORT).show();
    }


    /**
     * 销毁
     * onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "****************************onDestroy()");
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    /**
     * *******************播放完成 onCompletion ****************************
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        currentPosition = 0;

        if ((videoPosition + 1) < videosList.size()) {
            videoPosition += 1;

            videoAdapter.setSelectItem(videoPosition);
            videoAdapter.notifyDataSetInvalidated();

            handler.sendEmptyMessage(2);
        }
    }

    /**
     * 显示ProgressBar
     */
    private void showVideoLoading() {
        if (videosList != null && videosList.size() > 0) {
            tv_title.setText(videosList.get(videoPosition).title);
        }
        rl_loading.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏ProgressBar
     */
    private void hideVideoLoading() {
        if (rl_loading != null) rl_loading.setVisibility(View.GONE);
    }


    /**
     * 显示Tools
     */
    private void showTools() {
        if (ll_tools != null) {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            ll_tools.setVisibility(View.VISIBLE);
            ll_tools.startAnimation(animation);
        }
    }

    /**
     * 隐藏Tools
     */
    private void hideTools() {
        if (ll_tools != null) {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            ll_tools.setVisibility(View.GONE);
            ll_tools.startAnimation(animation);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                isPlaying = false;
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控返回键
            if (isFullScreen) {//当前处于全屏状态
                ib_fullscreen.setImageResource(R.drawable.ib_no_fullscreen);
                isFullScreen = false;
                ll_content.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                //getSupportActionBar().show();
                horsepowerVisibility(false);//隐藏马力
            } else {//返回上一级菜单
                isPlaying = false;
                onBackPressed();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * *******************视频播放错误监听 onError ****************************
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "*******************视频不能播放: onError(MediaPlayer mp, int what, int extra)");
        Log.i(TAG, "*******************videoPosition:" + videoPosition);

        //Toast.makeText(VideoPlayerActivity.this, "视频不能播放", Toast.LENGTH_SHORT).show();
        isPlaying = false;
        play(videosList.get(videoPosition).sd, 0);

        videoAdapter.setSelectItem(videoPosition);
        videoAdapter.notifyDataSetInvalidated();

        //mediaPlayer.release();//释放资源
        //onBackPressed();
        return false;
    }

    /**
     * 是否需要自动恢复播放，用于自动暂停，恢复播放
     * *******************视频信息监听 onInfo ****************************
     */
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START: //缓冲开始
                Log.i(TAG, "*******************onInfo()  缓冲开始");
                pro_bar.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END: //缓冲结束
                Log.i(TAG, "*******************onInfo()  缓冲结束");
                pro_bar.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    /**
     * 隐藏或显示马力
     * ******************* showHorsepower() ****************************
     */
    private void horsepowerVisibility(Boolean bool) {
        if (bool) {//显示马力
            btn_horsepower.setVisibility(View.VISIBLE);
        } else {//隐藏马力
            btn_horsepower.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }


    private void initData() {
        if (videosList == null || videosList.size() == 0) {
            videosList = new ArrayList<Videos>();
        }
        videosList.clear();

        course_id = getIntent().getStringExtra(Constants.INTENT_COURSE_ID_KEY);
        getListVideos(Long.valueOf(course_id));
    }


    /* ==========================================视频数据===========================================*/

    private List<Videos> videosList;
    private VideoAdapter videoAdapter;


    private void getListVideos(long course_id) {
        String url = Constants.LIST_VIDEOS_URL + "?course_id=" + course_id;
        JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String msg = response.optString(Constants.MSG_KEY);
                if (!msg.equals(Constants.MSG_VALUE)) return;


                JSONObject jsonObject = response.optJSONObject(Constants.DATA_KEY);
                JSONArray jsonArray = jsonObject.optJSONArray(Constants.RECODERS_KEY);

                if (jsonArray == null || jsonArray.length() == 0) {
                    Toast.makeText(VideoPlayerActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject videosJson = jsonArray.optJSONObject(i);
                    Videos videos = new Videos();
                    videos.id = videosJson.optLong(Constants.ID_KEY);
                    videos.title = videosJson.optString(Constants.TITLE_KEY);
                    videos.sd = videosJson.optString(Constants.SD_KEY);
                    videos.hd = videosJson.optString(Constants.HD_KEY);
                    videos.duration = videosJson.optString(Constants.DURATION_KEY);
                    videosList.add(videos);
                }

                videoAdapter = new VideoAdapter(VideoPlayerActivity.this, videosList);
                lv_videos.setAdapter(videoAdapter);

                handler.sendEmptyMessage(2);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VideoPlayerActivity.this, R.string.network_exception, Toast.LENGTH_SHORT).show();
            }
        }

        );

        RequestManager.addRequest(jsonObj, Constants.TAG_JSON_VIDEOS);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        videoAdapter.setSelectItem(position);
        videoAdapter.notifyDataSetInvalidated();
        if (videoPosition != position) {
            Log.i(TAG, "*******************videoPosition:" + position);
            mediaPlayer.reset();
            videoPosition = position;
            handler.sendEmptyMessage(2);
        }
    }

    /* *********************视频马力切换*********************** */

    /**
     * show PopupMenu
     *
     * @param view
     */
    private void showPopupMenu(View view) {
        final PopupMenuCompat.OnMenuItemClickListener onMenuItemClickListener =
                new PopupMenuCompat.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        if (item.getItemId() == R.id.sd) {
                            if (mHorsePowerStatus == 1) {
                                play(videosList.get(videoPosition).sd, 0);
                            }
                        } else if (item.getItemId() == R.id.hd) {
                            if (mHorsePowerStatus == 0) {
                                play(videosList.get(videoPosition).hd, 1);
                            }
                        }
                        return false;
                    }
                };
        PopupMenuCompat popupMenu = new PopupMenuCompat(this, view);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        popupMenu.inflate(R.menu.horsepower);
        popupMenu.show();
    }
}
