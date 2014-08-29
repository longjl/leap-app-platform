package cn.leap.app.common;


/**
 * Created by longjianlin on 14-8-24.
 */
public class Constants {
    private static final String BASE_HTTP = "http://172.16.37.32:11090";
    private static final String LOCAL_BASE_HTTP = "http://172.16.37.14:8102";

    //json tags (Tag used to cancel the request)
    public static final String TAG_JSON_LIST_HOME_PAGE_COURSE = "json_list_home_page_course";
    public static final String TAG_JSON_HOT_COURSE = "json_hot_course";
    public static final String TAG_JSON_ONE_COURSE = "json_one_course";


    //urls
    public static final String LIST_HOT_COURSE_URL = BASE_HTTP + "/course/mobilecourses/listHotCourse"; //推荐课程列表
    public static final String LIST_HOME_PAGE_COURSES_URL = BASE_HTTP + "/course/mobilecourses/listHomePageCourse";//首页课程信息列表
    public static final String LIST_ONE_COURSE_URL = BASE_HTTP + "/course/mobilecourses/listOneCourse";//根据课程编号查询课程和老师信息

    //params
    public static final long COURSE_ID_PARAM = 0;           //默认课程编号
    public static final int NUM_PARAM = 20;                 //默认查询课程数量
    public static final String DOWN_PARAM = "down";         //手势下拉
    public static final String UP_PARAM = "up";             //上推


    //course keys
    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String DESC_KEY = "desc";
    public static final String THUMB_KEY = "thumb";
    public static final String TEACHER_NAME_KEY = "teacher_name";
    public static final String VIDEO_TOTAL_KEY = "video_total";
    public static final String UPDATE_AT_KEY = "update_at";
    public static final String INTRO_KEY = "intro";

    public static final String MSG_KEY = "msg";
    public static final String MSG_VALUE = "Success";

    public static final String DATA_KEY = "data";
    public static final String RECODERS_KEY = "recoders";

    //intent keys
    public static final String INTENT_COURSE_ID_KEY = "course_id";


    //test urls
    public static final String URL = "http://api.androidhive.info/volley/person_array.json";


    //last update time
    public static final String LAST_UPDATE_TIME_KEY = "last_update_time";
    public static final String UPDATE_TIME_KEY = "update_time";
}

//private SharedPreferences sharedPreferences;
//private SharedPreferences.Editor editor;

//sharedPreferences = mContext.getSharedPreferences(Constants.LAST_UPDATE_TIME_KEY, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();//获取编辑器

//        String last_update_time = sharedPreferences.getString(Constants.UPDATE_TIME_KEY, null);
//        if (last_update_time == null || last_update_time.length() == 0) {//第一次下拉时间
//            last_update_time = String.valueOf(System.currentTimeMillis());
//            editor.putString(Constants.UPDATE_TIME_KEY, last_update_time);//时间戳
//            editor.commit();//提交修改
//        } else {
//            editor.putString(Constants.UPDATE_TIME_KEY, String.valueOf(System.currentTimeMillis()));//时间戳
//            editor.commit();//提交修改
//        }