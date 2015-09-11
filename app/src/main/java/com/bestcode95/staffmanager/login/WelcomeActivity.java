package com.bestcode95.staffmanager.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.bestcode95.staffmanager.R;
import com.bestcode95.staffmanager.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mima123 on 15/8/14.
 */
public class WelcomeActivity extends Activity {

    private String username = null;
    private String password = null;

    SharedPreferences preferences;

    public static final String SHARE_NAME = "bosomcode";
    public static final String SHARE_USERNAME = "share_username";
    public static final String SHARE_PASSWORD = "share_password";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                Log.e("handler", "收到数据");
                Log.e("登录成功", "登录成功");
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra(Constant.REFERRER_KEY, username);
                startActivity(intent);
                WelcomeActivity.this.finish();
            } else {
                Intent intent = new Intent(WelcomeActivity.this, Login.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        ArrayList<String> list = loadInfo();
        if (list != null) {
            username = list.get(0);
            password = list.get(1);
            if (username.equals("") || password.equals("") || username == null || password == null) {
                Intent intent = new Intent(WelcomeActivity.this, Login.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            } else {
                LoginInNetwork(username, password);
            }
        }
    }


    /**
     * 读取登录信息
     *
     * @return
     */
    public ArrayList<String> loadInfo() {
        preferences = getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        if (preferences.getString(SHARE_PASSWORD, "") != null && preferences.getString(SHARE_USERNAME, "") != null) {
            ArrayList<String> list = new ArrayList<>();
            list.add(preferences.getString(SHARE_USERNAME, ""));
            list.add(preferences.getString(SHARE_PASSWORD, ""));
            return list;
        }
        return null;
    }


    /**
     * 从后台对比用户的登录信息
     *
     * @param username
     * @param password
     * @return
     */
    private void LoginInNetwork(final String username, final String password) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                /**
                 * 记得改回来账号,密码
                 */
                try {
                    String json = Jsoup.connect(Constant.LOGIN_URL).ignoreContentType(true).data(Constant.USERNAME_KEY, username).data(Constant.PASSWORD_KEY, password).method(Connection.Method.POST).execute().body();
                    JSONObject object = new JSONObject(json);
                    int status = object.getInt(Constant.RESPONSE_KEY);
                    Log.e("status", "" + status);
                    sleep(1000);
                    if (status == Constant.REQUEST_SUCCESS) {
                        handler.sendEmptyMessage(0x123);
                    } else {
                        handler.sendEmptyMessage(0x124);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void showTip(String content) {
        Toast.makeText(WelcomeActivity.this, content, Toast.LENGTH_SHORT).show();
    }
}
