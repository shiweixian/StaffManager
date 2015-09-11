package com.bestcode95.staffmanager.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bestcode95.staffmanager.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by mima123 on 15/9/9.
 */
public class ModifyPassword extends Activity implements View.OnClickListener {

    EditText idEdit;
    EditText telEdit;
    EditText pwdEdit;
    EditText pwdConfirmEdit;
    Button confirmBt;
    Button cancelBt;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x133) {
                showToast("密码修改成功");
                Intent intent = new Intent(ModifyPassword.this, Login.class);
                startActivity(intent);
                ModifyPassword.this.finish();
            } else if (msg.what == 0x132) {
                showToast("网络出现异常");
            }else if(msg.what == 0x131){
                showToast("JSON异常");
            }else if(msg.what == 0x130){
                showToast("网络异常");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password);
        initView();
    }

    /**
     * 初始化界面组件
     */
    private void initView() {
        idEdit = (EditText) findViewById(R.id.fix_password_id_edit);
        telEdit = (EditText) findViewById(R.id.fix_password_tel_edit);
        pwdEdit = (EditText) findViewById(R.id.fix_password_pwd_edit);
        pwdConfirmEdit = (EditText) findViewById(R.id.fix_password_pwd_confirm_edit);
        confirmBt = (Button) findViewById(R.id.fix_password_confirm_bt);
        cancelBt = (Button) findViewById(R.id.fix_password_cancel_bt);
        confirmBt.setOnClickListener(this);
        cancelBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fix_password_confirm_bt:
                String id = idEdit.getText().toString();
                String tel = telEdit.getText().toString();
                String pwd = pwdEdit.getText().toString();
                String pwdConfirm = pwdConfirmEdit.getText().toString();
                if (verifyOk(id, tel, pwd, pwdConfirm)) {
                    uploadData(id, tel, pwd);
                }
                break;
            case R.id.fix_password_cancel_bt:
                Intent intent = new Intent(ModifyPassword.this,Login.class);
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 本地验证信息
     *
     * @param id
     * @param tel
     * @param pwd
     * @param pwdConfirm
     * @return
     */
    private boolean verifyOk(String id, String tel, String pwd, String pwdConfirm) {
        if (id == null || id.equals("")) {
            showToast("身份证号未填写");
            return false;
        } else if (tel == null || tel.equals("")) {
            showToast("手机号未填写");
            return false;
        } else if (pwd == null || pwd.equals("")) {
            showToast("请填写新密码");
            return false;
        } else if (pwdConfirm == null || pwdConfirm.equals("") || !pwd.equals(pwdConfirm)) {
            showToast("两次密码不一致");
            return false;
        }
        return true;
    }

    /**
     * 上传数据
     *
     * @param id
     * @param tel
     * @param pwd
     */
    private void uploadData(final String id, final String tel, final String pwd) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    String json = Jsoup.connect(Constant.MODIFY_PWD_URL).ignoreContentType(true).data("id", id).data("tel", tel).data("newPwd", pwd).method(Connection.Method.POST).execute().body();
                    Log.e("json", json);
                    JSONObject object = new JSONObject(json);
                    int status = object.getInt(Constant.RESPONSE_KEY);
                    Log.e("status", "" + status);
                    if (status == Constant.REQUEST_SUCCESS) {
                        handler.sendEmptyMessage(0x133);
                    } else {
                        handler.sendEmptyMessage(0x132);
                    }
                } catch (IOException e) {
                    handler.sendEmptyMessage(0x131);
//                    e.printStackTrace();
                } catch (JSONException e) {
                    handler.sendEmptyMessage(0x130);
//                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

}
