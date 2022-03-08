package com.example.myapplication3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gedan3_adapter extends BaseAdapter {
    public LayoutInflater layoutInflater;
    public Context mycontext;
    public List<String> mylist;
    Map<Integer, Boolean> map=new HashMap<>();//标记checkbox选中状态，防止listview错误显示
    public gedan3_adapter (Context context, List<String> list) {
        layoutInflater = LayoutInflater.from(context);
        this.mycontext = context;
        this.mylist = list;
    }
    @Override
    public int getCount() {
        if(mylist==null)
            return 0;
        else
            return mylist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.gedan2, null);
        }
        else{
            view = convertView;
        }
        //绑定布局
        TextView textView_name = (TextView)view.findViewById(R.id.gedan2_textview);
        CheckBox checkBox= (CheckBox)view.findViewById(R.id.gedan2_checkbox);
        //设置内容
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    gedan3.picked.add(mylist.get(position));//添加选中的项进入列表
                    map.put(position,true);//标记位置
                }
                else {
                    gedan3.picked.remove(mylist.get(position));//移除选中项
                    map.remove(position);//移除位置
                }
            }
        });
        if(map!=null&&map.containsKey(position)){//当该位置有标记时
            checkBox.setChecked(true);//设置选中状态为真
        }else{
            checkBox.setChecked(false);
        }
        String s;//对路径进行裁剪，只显示名称
        if(mylist.get(position).substring(mylist.get(position).lastIndexOf("/")).contains("-")) {
            s=mylist.get(position).substring(mylist.get(position).lastIndexOf("/") + 1, mylist.get(position).lastIndexOf("-"));
        }
        else
            s=mylist.get(position).substring(mylist.get(position).lastIndexOf("/")+1,mylist.get(position).lastIndexOf("."));
        textView_name.setText(s);
        return view;
    }
}

