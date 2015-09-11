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
 * Created by mima123 on 15/9/10.
 */
public class DeleteStaff extends Activity implements View.OnClickListener {

    EditText staffIdEdit;
    EditText staffTelEdit;
    Button confirmBt;
    Button cancelBt;

    String staffId = null;
    String staffTel = null;
    String userTel = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x111) {
                showToast("删除成功!");
                DeleteStaff.this.finish();
            } else if (msg.what == 0x112)
                showToast("操作失败");
            else if (msg.what == 0x113)
                showToast("网络异常");
            else if (msg.what == 0x114)
                showToast("JSON异常");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_staff);
        initView();
        userTel = getIntent().getStringExtra(Constant.REFERRER_KEY);
    }

    /**
     * 初始化界面组件
     */
    private void initView() {
        staffIdEdit = (EditText) findViewById(R.id.delete_staff_id_edit);
        staffTelEdit = (EditText) findViewById(R.id.delete_staff_tel_edit);
        confirmBt = (Button) findViewById(R.id.delete_staff_confirm_bt);
        cancelBt = (Button) findViewById(R.id.delete_staff_cancel_bt);
        confirmBt.setOnClickListener(this);
        cancelBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_staff_confirm_bt:
                staffId = staffIdEdit.getText().toString();
                staffTel = staffTelEdit.getText().toString();
                if (verifyOk(staffId, staffTel)) {
                    uploadData(Constant.DELETE_STAFF_URL, staffId, staffTel, userTel);
                }
                break;
            case R.id.delete_staff_cancel_bt:
                this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 本地验证信息格式
     *
     * @param identity
     * @param tel
     * @return
     */
    private boolean verifyOk(String identity, String tel) {
        if (identity.isEmpty() || identity.equals("")) {
            showToast("身份证号不能为空");
            return false;
        } else if (tel.isEmpty() || tel.equals("")) {
            showToast("手机号不能为空");
            return false;
        } else if (!tel.matches("\\d{11}")) {
            showToast("手机号格式不正确");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 上传网络数据
     *
     * @param url
     * @param identity
     * @param tel
     * @param userTel
     */
    private void uploadData(final String url, final String identity, final String tel, final String userTel) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String json = Jsoup.connect(url).ignoreContentType(true).data("id", identity).data("tel", tel)
                            .data("userTel", userTel).method(Connection.Method.POST).execute().body();
                    JSONObject object = new JSONObject(json);
                    int status = object.getInt(Constant.RESPONSE_KEY);
                    if (status == Constant.REQUEST_SUCCESS) {
                        handler.sendEmptyMessage(0x111);
                    } else if (status == Constant.REQUEST_FAILED) {
                        handler.sendEmptyMessage(0x112);
                    }
                } catch (IOException e) {
                    handler.sendEmptyMessage(0x113);
//                    e.printStackTrace();
                } catch (JSONException e) {
                    handler.sendEmptyMessage(0x114);
//                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 显示提示信息
     *
     * @param content
     */
    private void showToast(String content) {
        Toast.makeText(DeleteStaff.this, content, Toast.LENGTH_SHORT).show();
    }
}
