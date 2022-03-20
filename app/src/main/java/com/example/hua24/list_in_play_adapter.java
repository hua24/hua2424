package com.example.hua24;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class list_in_play_adapter extends BaseAdapter {//播放界面的列表使用的适配器
    public LayoutInflater layoutInflater;
    public Context mycontext;
    public List<String> mylist;
    public list_in_play_adapter(Context context, List<String> list) {
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
            view = layoutInflater.inflate(R.layout.list_in_play_item, null);
        }
        else{
            view = convertView;
        }
        //绑定布局
        TextView textView_name = (TextView)view.findViewById(R.id.dialog_item);
        //设置内容
        textView_name.setText(mylist.get(position));
        return view;
    }
}
