package com.bestcode95.staffmanager.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class AddStaff extends Activity {

    EditText addName = null;
    EditText addIdentity = null;
    EditText addTel = null;
    EditText addSalary = null;
    Button uploadBt = null;
    Button cancelBt = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x111) {
                showToast("上传成功!");
                AddStaff.this.finish();
            } else if (msg.what == 0x112) {
                showToast("上传失败!" + msg);
            } else if (msg.what == 0x113) {
                showToast("网络IO异常");
            } else if (msg.what == 0x114) {
                showToast("网络JSON异常");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_staff);
        initView();
        //得到推荐人的电话号码
        final String referrerTel = getIntent().getStringExtra(Constant.REFERRER_KEY);

        uploadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addName.getText().toString();
                String identity = addIdentity.getText().toString();
                String tel = addTel.getText().toString();
                String salary = addSalary.getText().toString();
                if (verifyOk(name, identity, tel, salary))
                    uploadData(Constant.ADD_STAFF_URL, name, identity, tel, salary, referrerTel);
            }
        });
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddStaff.this.finish();
            }
        });
    }

    /**
     * 初始化界面组件
     */
    private void initView() {
        addName = (EditText) findViewById(R.id.add_staff_name_edit);
        addIdentity = (EditText) findViewById(R.id.add_staff_identity_edit);
        addTel = (EditText) findViewById(R.id.add_staff_tel_edit);
        addSalary = (EditText) findViewById(R.id.add_staff_salary_edit);
        uploadBt = (Button) findViewById(R.id.add_staff_upload_bt);
        cancelBt = (Button) findViewById(R.id.add_staff_cancel_bt);
    }

    /**
     * 本地验证信息格式
     *
     * @param name
     * @param identity
     * @param tel
     * @return
     */
    private boolean verifyOk(String name, String identity, String tel, String salary) {
        if (name.isEmpty() || name.equals("")) {
            showToast("名字不能为空");
            return false;
        } else if (identity.isEmpty() || identity.equals("")) {
            showToast("身份证号不能为空");
            return false;
        } else if (tel.isEmpty() || tel.equals("")) {
            showToast("手机号不能为空");
            return false;
        } else if (salary.isEmpty() || salary.equals("")) {
            showToast("工资不能为空");
            return false;
        } else if (!tel.matches("\\d{11}")) {
            showToast("手机号格式不正确");
            return false;
        } else {
            return true;
        }
    }


    /**
     * 新增员工
     *
     * @param url         请求的网址
     * @param name        姓名
     * @param identity    身份证号
     * @param tel         电话号码
     * @param salary      工资
     * @param referrerTel 添加人的电话号码
     */
    private void uploadData(final String url, final String name, final String identity, final String tel, final String salary, final String referrerTel) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String json = Jsoup.connect(url).ignoreContentType(true).data("id", identity).data("tel", tel)
                            .data("username", name).data("salary", salary).data("referrer", referrerTel).method(Connection.Method.POST).execute().body();
                    JSONObject object = new JSONObject(json);
                    int status = object.getInt(Constant.RESPONSE_KEY);
                    if (status == Constant.REQUEST_SUCCESS) {
                        handler.sendEmptyMessage(0x111);
                    } else if (status == Constant.REQUEST_FAILED) {
                        String msg = object.getString("msg");
                        Message message = new Message();
                        message.what = 0x112;
                        message.obj = msg;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    handler.sendEmptyMessage(0x113);
                    e.printStackTrace();
                } catch (JSONException e) {
                    handler.sendEmptyMessage(0x114);
//                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
