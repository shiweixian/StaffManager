package com.bestcode95.staffmanager.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bestcode95.staffmanager.R;
import com.bestcode95.staffmanager.login.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mima123 on 15/9/8.
 */
public class StaffDetail extends Activity {

    SharedPreferences preferences;
    Button modifySalaryBt = null;
    ImageButton quitBt = null;
    TextView nameText = null;
    TextView identityText = null;
    TextView telText = null;
    TextView salaryText = null;

    String name = null;
    String identity = null;
    String tel = null;
    String salary = null;
    int position = -1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x113) {
                Bundle bundle = (Bundle) msg.obj;
                name = bundle.getString("name");
                identity = bundle.getString("identity");
                salary = bundle.getString("salary");
                nameText.setText(name);
                identityText.setText(identity);
                telText.setText(tel);
                salaryText.setText(salary);
            } else if (msg.what == 0x111) {
                showToast("获取数据错误");
            } else if (msg.what == 0x112) {
                showToast("查询不到此人的信息");
            }else if (msg.what == 0x115){
                showToast("修改工资成功!");
                salaryText.setText(salary);
            }else if (msg.what == 0x116){
                showToast("修改工资失败");
            }else if (msg.what == 0x117){
                showToast("数据出错");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_detail);
        initView();
        position = getIntent().getIntExtra(Constant.POSITION_KEY, -1);
        try {
            tel = getTel(position);
            loadData(Constant.STAFF_DETAIL_URL, tel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化界面组件
     */
    private void initView() {
        modifySalaryBt = (Button) findViewById(R.id.staff_detail_modify_salary_bt);
        quitBt = (ImageButton) findViewById(R.id.staff_detail_quit_bt);
        nameText = (TextView) findViewById(R.id.staff_detail_name);
        identityText = (TextView) findViewById(R.id.staff_detail_identity);
        telText = (TextView) findViewById(R.id.staff_detail_tel);
        salaryText = (TextView) findViewById(R.id.staff_detail_salary);
        modifySalaryBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        quitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaffDetail.this.finish();
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
                } catch (JSONException e) {
                    message.what = 0x111;
//                    e.printStackTrace();
                } catch (IOException e) {
                    message.what = 0x112;
//                    e.printStackTrace();
                }
            }

        }.start();
    }

    private String getTel(int position) throws IOException {
        preferences = getSharedPreferences(Constant.COUNT_SHARE_NAME, MODE_PRIVATE);
        List<String> telList = new ArrayList<>();
        int count = preferences.getInt(Constant.COUNT_KEY, -1);
        if (count == -1) {
            showToast("很抱歉,没有此人的数据");
            return "";
        }
        if (count == 1) count = 2;
        BufferedReader telReader = new BufferedReader(new InputStreamReader(new FileInputStream(Constant.checkStaffTelDir + (count - 1) + ".txt")));
        String str;
        while ((str = telReader.readLine()) != null) {
            telList.add(str);
        }
        String tel = telList.get(position);
        return tel;
    }

    private void uploadData(final String url, final String tel, final String salary){
        if (salary.isEmpty() || salary.equals("")){
            showToast("工资不能为空,请重新操作");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String json = Jsoup.connect(url).ignoreContentType(true).data("tel",tel).data("salary", salary).method(Connection.Method.POST).execute().body();
                    JSONObject object = new JSONObject(json);
                    int status = object.getInt(Constant.RESPONSE_KEY);
                    if (status == Constant.REQUEST_SUCCESS) {
                        handler.sendEmptyMessage(0x115);
                    } else if (status == Constant.REQUEST_FAILED) {
                        handler.sendEmptyMessage(0x116);
                    }
                } catch (IOException e) {
                    handler.sendEmptyMessage(0x117);
//                    e.printStackTrace();
                } catch (JSONException e) {
                    handler.sendEmptyMessage(0x111);
//                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 显示更改工资对话框
     */
    private void showDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(StaffDetail.this)
                .setTitle("请输入工资:")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        salary = editText.getText().toString();
                        uploadData(Constant.MODIFY_SALARY_URL,tel,salary);
                    }
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 显示提示信息
     *
     * @param content
     */
    private void showToast(String content) {
        Toast.makeText(StaffDetail.this, content, Toast.LENGTH_SHORT).show();
    }
}

