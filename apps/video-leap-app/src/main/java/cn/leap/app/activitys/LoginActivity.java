package cn.leap.app.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import cn.leap.app.LeapApplication;
import cn.leap.app.R;
import cn.leap.app.bean.Users;
import cn.leap.app.common.Constants;
import cn.leap.app.network.RequestManager;

/**
 * login
 * Created by longjianlin on 14-9-4.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_email;
    private EditText et_pwd;
    private Button btn_login;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public LoginActivity() {
        super(R.string.main_title);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSlidingMenu().setSlidingEnabled(false);


        et_email = (EditText) findViewById(R.id.et_email);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        sharedPreferences = this.getSharedPreferences(Constants.LOGIN_PREFERENCES_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();//获取编辑器
    }


    @Override
    public void onClick(View v) {
        if (btn_login.getId() == v.getId()) {
            validate();
        }
    }


    /**
     * 用户数据非空验证
     *
     * @return
     */
    private void validate() {
        if (et_email.getText() == null || et_email.getText().length() == 0) {
            handler.sendEmptyMessage(0);
        } else if (et_pwd.getText() == null || et_pwd.getText().length() == 0) {
            handler.sendEmptyMessage(1);
        } else {
            handler.sendEmptyMessage(2);
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:  //user name is null
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    break;
                case 1:  //pwd is null
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    break;
                case 2:  //登录验证
                    login(et_email.getText().toString(), et_pwd.getText().toString());
                    break;
                case 3:
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 登录
     *
     * @param email
     * @param pwd
     */
    private void login(final String email, final String pwd) {
        String url = Constants.LOGIN_URL + "?email=" + email + "&password=" + pwd;
        JsonObjectRequest loginJsonObj = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String status = response.optString("status");
                if (Integer.parseInt(status) == 0) {//登录失败
                    Toast.makeText(LoginActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject dataJsonObj = response.optJSONObject("data");
                JSONObject userJsonObj = dataJsonObj.optJSONObject("user");

                long uid = userJsonObj.optLong("uid");

                editor.putString(Constants.UID_PREFERENCES_KEY, String.valueOf(uid));
                editor.putString(Constants.EMAIL_PREFERENCES_KEY, email);
                editor.putString(Constants.PWD_PREFERENCES_KEY, pwd);
                editor.putString(Constants.NICKNAME_PREFERENCES_KEY, userJsonObj.optString("nickname"));
                editor.commit();

                Users users = new Users();
                users.uid = uid;
                users.email = email;
                users.nickname = userJsonObj.optString("nickname");

                LeapApplication.getInstance().setUsers(users);

                handler.sendEmptyMessage(3);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, R.string.network_exception, Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestManager.addRequest(loginJsonObj, Constants.TAG_JSON_LOGIN);
    }


}
