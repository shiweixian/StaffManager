package com.bestcode95.staffmanager.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bestcode95.staffmanager.login.Constant;
import com.bestcode95.staffmanager.main.CheckStaff;
import com.bestcode95.staffmanager.main.StaffDetail;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mima123 on 15/8/7.
 */
public class ListBtAdapter extends BaseAdapter {

    /**
     * 内部类
     */
    private class ButtonViewHolder {
        TextView nameText;
        Button detailBt;
    }

    private ArrayList<HashMap<String, Object>> mAppList;
    private LayoutInflater mInflater;
    private CheckStaff mContext;
    private String[] keyString;
    private int[] valueViewId;
    private ButtonViewHolder holder;
    private int mResource;
//    private List<String> mList;

    /**
     * 构造方法
     *
     * @param context
     * @param appList
     * @param resource
     * @param from
     * @param to
     */
    public ListBtAdapter(Context context, ArrayList<HashMap<String, Object>> appList, int resource, String[] from, int[] to) {
        mContext = (CheckStaff) context;
        mAppList = appList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
//        mList = list;
//        Log.e("mList->size",""+mList.size());
        keyString = new String[from.length];
        valueViewId = new int[to.length];
        System.arraycopy(from, 0, keyString, 0, from.length);
        System.arraycopy(to, 0, valueViewId, 0, to.length);
    }

    @Override
    public int getCount() {
        return mAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppList.get(position);
    }

    public void removeItem(int position) {
        mAppList.remove(position);
    }

    public void removeAllItems() {
        for (int i = 0; i < mAppList.size(); i++) {
            mAppList.remove(0);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            holder = (ButtonViewHolder) convertView.getTag();
        } else {
            //初始化界面组件
            convertView = mInflater.inflate(mResource, null);
            holder = new ButtonViewHolder();
            holder.nameText = (TextView) convertView.findViewById(valueViewId[0]);
            holder.detailBt = (Button) convertView.findViewById(valueViewId[1]);
            convertView.setTag(holder);
        }
        HashMap<String, Object> appInfo = mAppList.get(position);
        if (appInfo != null) {
            String name = (String) appInfo.get(keyString[0]);
            holder.nameText.setText(name);
            holder.detailBt.setOnClickListener(new ListBtListener(position));
//            Log.e("holder->mList->size", "" + mList.size());
        }
        return convertView;
    }


    class ListBtListener implements View.OnClickListener {

        int position;
//        List<String> list;

        ListBtListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            int vid = v.getId();
            if (vid == holder.detailBt.getId()) {
                /*
                *书写按钮的监听事件
                 */
                //传递跳转的信息
                Log.e("listView", "跳转成功" + vid);
                Intent intent = new Intent(mContext, StaffDetail.class);
                intent.putExtra(Constant.POSITION_KEY, position);//"17865197355");
                mContext.startActivity(intent);
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
