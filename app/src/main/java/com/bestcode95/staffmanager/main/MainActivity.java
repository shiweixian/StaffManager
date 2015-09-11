package com.bestcode95.staffmanager.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bestcode95.staffmanager.R;
import com.bestcode95.staffmanager.login.Constant;
import com.bestcode95.staffmanager.login.Login;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {

    Button mCheckStaffBt = null;
    Button mAddStaffBt = null;
    Button mDeleteStaffBt = null;
    Button mCheckMineBt = null;
    Button mQuitAccountBt = null;
    String referrerTel = null;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mkDir();
        referrerTel = getIntent().getStringExtra(Constant.REFERRER_KEY);
        File file = new File(Constant.checkStaffNameDir);
        file.deleteOnExit();
        file = new File(Constant.checkStaffTelDir);
        file.delete();
        Log.e("deleteFile","文件已删除");
    }

    /**
     * 初始界面化组件
     */
    private void initView() {
        mCheckStaffBt = (Button) findViewById(R.id.main_check_staff_bt);
        mAddStaffBt = (Button) findViewById(R.id.main_add_staff_bt);
        mDeleteStaffBt = (Button) findViewById(R.id.main_delete_staff_bt);
        mCheckMineBt = (Button) findViewById(R.id.main_check_mine_bt);
        mQuitAccountBt = (Button) findViewById(R.id.main_quit_account_bt);
        mCheckStaffBt.setOnClickListener(this);
        mAddStaffBt.setOnClickListener(this);
        mDeleteStaffBt.setOnClickListener(this);
        mCheckMineBt.setOnClickListener(this);
        mQuitAccountBt.setOnClickListener(this);
    }

    /**
     * 按钮点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            //查看员工
            case R.id.main_check_staff_bt:
                intent = new Intent(MainActivity.this, CheckStaff.class);
                intent.putExtra(Constant.REFERRER_KEY, referrerTel);
                startActivity(intent);
                break;
            //添加员工
            case R.id.main_add_staff_bt:
                intent = new Intent(MainActivity.this, AddStaff.class);
                intent.putExtra(Constant.REFERRER_KEY,referrerTel);
                startActivity(intent);
                break;
            //删除员工
            case R.id.main_delete_staff_bt:
                intent = new Intent(MainActivity.this,DeleteStaff.class);
                intent.putExtra(Constant.REFERRER_KEY,referrerTel);
                startActivity(intent);
                break;
            //查看自身信息
            case R.id.main_check_mine_bt:
                intent = new Intent(MainActivity.this,CheckMine.class);
                intent.putExtra(Constant.REFERRER_KEY,referrerTel);
                startActivity(intent);
                break;
            //退出当前账号
            case R.id.main_quit_account_bt:
                quitAccount();
                break;
            default:
                break;

        }
    }

    /**
     * 退出当前账号
     */
    private void quitAccount() {
        preferences = getSharedPreferences(Login.SHARE_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.putString(Login.SHARE_USERNAME, null);
        editor.putString(Login.SHARE_PASSWORD, null);
        editor.commit();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    /**
     * 建立查看员工的文件夹
     */
    private void mkDir() {
        File checkDir = new File(Constant.checkStaffDir);
        File nameDir = new File(Constant.checkStaffNameDir);
        File telDir = new File(Constant.checkStaffTelDir);
        if (!checkDir.exists()) {
            checkDir.mkdirs();
        } else {
            Log.e("checkDir", "查看员工文件夹checkStaffDir已存在!");
        }
        if (!nameDir.exists()) {
            nameDir.mkdirs();
        } else {
            Log.e("checkDir", "查看员工文件夹nameDir已存在!");
        }
        if (!telDir.exists()) {
            telDir.mkdirs();
        } else {
            Log.e("checkDir", "查看员工文件夹telDir已存在!");
        }
    }
}
