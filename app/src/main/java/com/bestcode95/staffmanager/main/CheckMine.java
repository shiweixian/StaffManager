package com.bestcode95.staffmanager.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bestcode95.staffmanager.R;
import com.bestcode95.staffmanager.login.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by mima123 on 15/9/8.
 */
public class CheckMine extends Activity {

    ImageButton quitBt = null;
    TextView nameText = null;
    TextView identityText = null;
    TextView telText = null;
    TextView salaryText = null;

    String name = null;
    String identity = null;
    String tel = null;
    String salary = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x113) {
                Bundle bundle = (Bundle) msg.obj;
                name = bundle.getString("name");
                identity = bundle.getString("identity");
                salary = bundle.getString("salary");
                tel = getIntent().getStringExtra(Constant.REFERRER_KEY);
                nameText.setText(name);
                identityText.setText(identity);
                telText.setText(tel);
                salaryText.setText(salary);
            } else if (msg.what == 0x110) {
                Toast.makeText(CheckMine.this, "抱歉,没有此人的数据", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x111) {
                Toast.makeText(CheckMine.this, "获取数据错误", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x112) {
                Toast.makeText(CheckMine.this, "查询不到此人的信息", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_mine);
        initView();
        tel = getIntent().getStringExtra(Constant.REFERRER_KEY);
        Log.e("tel",tel);
        loadData(Constant.STAFF_DETAIL_URL, tel);
    }

    /**
     * 初始化界面组件
     */
    private void initView() {
        quitBt = (ImageButton) findViewById(R.id.check_mine_quit_bt);
        nameText = (TextView) findViewById(R.id.check_mine_name);
        identityText = (TextView) findViewById(R.id.check_mine_identity);
        telText = (TextView) findViewById(R.id.check_mine_tel);
        salaryText = (TextView) findViewById(R.id.check_mine_salary);
        quitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckMine.this.finish();
            }
        });
    }

    /**
     * 从网络获取数据并展示
     *
     * @param url
     * @param tel
     */
    private void loadData(final String url, final String tel) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                //创建message对象
                Message message = new Message();
                try {
                    String json = Jsoup.connect(url).ignoreContentType(true).data(Constant.USERNAME_KEY, tel).method(Connection.Method.POST).execute().body();
                    Log.e("json", json);
                    JSONObject object = new JSONObject(json);
                    int status = object.getInt("result");
                    if (status == Constant.REQUEST_SUCCESS){
                        name = object.getString("username");
                        identity = object.getString("id");
                        salary = object.getString("salary");
                        Log.e("StaffDetail->loadData", name + " , " + identity + " , " + salary);

                        //创建bundle对象
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        bundle.putString("identity", identity);
                        bundle.putString("salary", salary);
                        message.obj = bundle;
                        message.what = 0x113;
                        handler.sendMessage(message);
                    }else {
                        message.what = 0x110;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    message.what = 0x111;
                    handler.sendMessage(message);
//                    e.printStackTrace();
                } catch (IOException e) {
                    message.what = 0x112;
                    handler.sendMessage(message);
//                    e.printStackTrace();
                }
            }

        }.start();
    }

}

