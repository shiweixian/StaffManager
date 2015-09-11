package com.bestcode95.staffmanager.main;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.bestcode95.staffmanager.R;
import com.bestcode95.staffmanager.login.Constant;
import com.bestcode95.staffmanager.utils.ListBtAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mima123 on 15/9/7.
 */
public class CheckStaff extends ListActivity implements View.OnClickListener {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private ImageButton quitBt = null;
    private ImageButton searchBt = null;
    private ImageButton backBt = null;
    private ListView listView = null;

    ListBtAdapter listBtAdapter = null;

    private List<String> nameList = new ArrayList<>();
    public static List<String> telList = new ArrayList<>();
    ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x124) {
                /**
                 * 为listView的adapter设置数据
                 */
                List<String> list = (List<String>) msg.obj;
                setListViewAdapter(listView, loadDataForListView(list));
                Log.e("telList", "" + telList.size());
                saveInfo(Constant.checkStaffNameDir + count + ".txt", Constant.checkStaffTelDir + count + ".txt");
                count += 1;
                saveCount(count);
                Log.e("count", "" + count);
            } else if (msg.what == 0x115) {
                showToast("网络异常");
            } else if (msg.what == 0x116) {
                showToast("很抱歉,网络出现异常(json)");
            } else if (msg.what == 0x117) {
                showToast("很抱歉,没有数据");
            } else if (msg.what == 0x118) {
                showToast("很抱歉,网络出现异常");
            }
        }
    };

    private int count = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_staff);
        initView();
        mkDir();

        gainDataFromInternet(Constant.CHECK_STAFF_URL, getIntent().getStringExtra(Constant.REFERRER_KEY), "",-1);
    }

    /**
     * 初始化界面组件
     */
    private void initView() {
        quitBt = (ImageButton) findViewById(R.id.check_staff_quit_bt);
        searchBt = (ImageButton) findViewById(R.id.check_staff_search_bt);
        backBt = (ImageButton) findViewById(R.id.check_staff_back_bt);
        listView = (ListView) findViewById(android.R.id.list);
        quitBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        backBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //退出当前界面
            case R.id.check_staff_quit_bt:
                this.finish();
                break;
            //搜索对话框
            case R.id.check_staff_search_bt:
                showDialog();
                break;
            //返回上一级
            case R.id.check_staff_back_bt:
                backMethod();
                break;
            default:
                break;
        }
    }

    /**
     * 显示搜索对话框
     */
    private void showDialog() {
        final EditText editText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckStaff.this)
                .setTitle("请输入你要搜索的名字:")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * 这个网址需要换
                         */
                        String content = editText.getText().toString();
                        gainDataFromInternet(Constant.CHECK_STAFF_SEARCH_URL,content,getIntent().getStringExtra(Constant.REFERRER_KEY) , -1);
                        Log.e("content", content);
                    }
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 从网络获取信息
     *
     * @param url 请求的网址
     * @param tel 可能是电话号码也可能是姓名
     */
    private void gainDataFromInternet(String url, String tel,String userTel, int position) {
        if (tel.equals("")) {
            if (position >= 0) {
                try {
                    count = preferences.getInt(Constant.COUNT_KEY, 1);
                    if (count >= 1) {
                        BufferedReader telReader;
                        if (count == 1) {
                            Log.e("count==1", "count==1");
                            telReader = new BufferedReader(new InputStreamReader(new FileInputStream(Constant.checkStaffTelDir + count + ".txt")));
                            count += 1;
                            saveCount(count);
                        } else {
                            telReader = new BufferedReader(new InputStreamReader(new FileInputStream(Constant.checkStaffTelDir + (count - 1) + ".txt")));
                        }
                        String str;
                        while ((str = telReader.readLine()) != null) {
                            telList.add(str);
                        }
                        tel = telList.get(position);
                        Log.e("telList->gainData", "" + telList.size());
                        httpThread(url, tel,userTel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            httpThread(url, tel,userTel);
        }

    }

    private void httpThread(final String url, final String tel, final String userTel) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    String json;
                    if (userTel.isEmpty() || userTel.equals(""))
                        json = Jsoup.connect(url).ignoreContentType(true).data("tel", tel).method(Connection.Method.POST).execute().body();
                    else
                        json = Jsoup.connect(url).ignoreContentType(true).data("username", tel).data("userTel", userTel).method(Connection.Method.POST).execute().body();
                    Log.e("json", json);
                    JSONObject object = new JSONObject(json);
                    int status = object.getInt(Constant.RESPONSE_KEY);
                    Log.e("status", "" + status);
                    if (status == Constant.REQUEST_SUCCESS) {
                        /**
                         *未填写json的键
                         */
                        JSONArray array = object.getJSONArray("members");

                        nameList.clear();
                        telList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object1 = array.getJSONObject(i);
                            String name = object1.getString("username");
                            nameList.add(name);
                            telList.add(object1.getString("tel"));
                        }
                        for (int i = 0; i < nameList.size(); i++) {
                            Log.e("nameList", nameList.get(i));
                        }

                        message.obj = nameList;
                        message.what = 0x124;
                        handler.sendMessage(message);
                    } else if (status == Constant.REQUEST_FAILED) {
                        message.what = 0x117;
                        handler.sendMessage(message);
                    } else {
                        message.what = 0x118;
                    }
                } catch (IOException e) {
                    message.what = 0x115;
                    handler.sendMessage(message);
                    Log.e("io", "io异常");
                } catch (JSONException e) {
                    message.what = 0x116;
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    private ArrayList<HashMap<String, Object>> loadDataForListView(List<String> nameList) {
        listItem.clear();
        for (int i = 0; i < nameList.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(Constant.NAME_FLAG, nameList.get(i));
            listItem.add(map);
        }
        return listItem;
    }

    /**
     * 返回上一级按钮调用的方法
     */
    private void backMethod() {
        nameList.clear();
        telList.clear();
        count = preferences.getInt(Constant.COUNT_KEY, 1);
        Log.e("back->count1", "" + count);
        if (count > 1) {
            count -= 1;
            saveCount(count);
            Log.e("back->count2", "" + count);
            try {
                if (count > 1) {
                    Log.e("查看", "方法被调用");
                    loadInfo(Constant.checkStaffNameDir + (count - 1) + ".txt", Constant.checkStaffTelDir + (count - 1) + ".txt");
                    Log.e("count->read", "" + count);
                    setListViewAdapter(listView, loadDataForListView(nameList));
                } else {
                    showToast("已经没有上一级了");
                }
            } catch (IOException e) {
                showToast("文件异常,请退出重试");
                e.printStackTrace();
            }
        } else {
            showToast("已经没有上一级了");
            count = 2;
            saveCount(count);
        }
    }

    /**
     * 保存信息到本地
     */
    private void saveInfo(String nameFileName, String telFileName) {
        File nameFile = new File(nameFileName);
        File telFile = new File(telFileName);
        try {
            FileWriter nameWriter = new FileWriter(nameFile);
            FileWriter telWriter = new FileWriter(telFile);
            for (int i = 0; i < nameList.size(); i++) {
                nameWriter.write(nameList.get(i) + "\n");
                telWriter.write(telList.get(i) + "\n");
                nameWriter.flush();
                telWriter.flush();
            }
            nameWriter.close();
            telWriter.close();
            nameList.clear();
            telList.clear();
            Log.e("Check->保存当及内容", "文件写入成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地缓存读取信息
     */
    private void loadInfo(String nameFileName, String telFileName) throws IOException {
        if (nameList == null) {
            nameList = new ArrayList<>();
        }
        if (telList == null) {
            telList = new ArrayList<>();
        }
        BufferedReader nameReader = new BufferedReader(new InputStreamReader(new FileInputStream(nameFileName)));
        BufferedReader telReader = new BufferedReader(new InputStreamReader(new FileInputStream(telFileName)));
        String str;
        while ((str = nameReader.readLine()) != null) {
            nameList.add(str);
        }
        while ((str = telReader.readLine()) != null) {
            telList.add(str);
        }
    }

    /**
     * 为listView设置适配器
     */
    private void setListViewAdapter(ListView
                                            listView, ArrayList<HashMap<String, Object>> data) {
        //生成适配器的Item和动态数组对应的元素
        listBtAdapter = new ListBtAdapter(this, data,
                R.layout.for_check_staff_listview, new String[]{Constant.NAME_FLAG,
                Constant.BUTTON_FLAG}, new int[]{R.id.check_staff_list_name,
                R.id.check_staff_list_detail_bt});
//        Log.e("list->size",""+list.size());
        listView.setAdapter(listBtAdapter);
    }

    /**
     * listView监听事件
     *
     * @param
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        l.getItemAtPosition(position);
        Log.e("onItemClick", "listView被点击");
        //在此书写listView的点击事件
        if (nameList == null) {
            nameList = new ArrayList<>();
        }
        if (telList == null) {
            telList = new ArrayList<>();
            Log.e("telList", "创建telList");
        }
        Log.e("onListItemClick", "" + count);
        gainDataFromInternet(Constant.CHECK_STAFF_URL, "","", position);
    }

    /**
     * 显示提示信息
     *
     * @param content
     */
    private void showToast(String content) {
        Toast.makeText(CheckStaff.this, content, Toast.LENGTH_SHORT).show();
    }


    /**
     * 建立查看员工的文件夹
     */
    private void mkDir() {
        File nameDir = new File(Constant.checkStaffNameDir);
        File telDir = new File(Constant.checkStaffTelDir);
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


    /**
     * 保存计数
     *
     * @param count
     */
    public void saveCount(int count) {
        preferences = getSharedPreferences(Constant.COUNT_SHARE_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.putInt(Constant.COUNT_KEY, count);
        editor.commit();
        Log.e("saveCount", "" + count);
    }

}
