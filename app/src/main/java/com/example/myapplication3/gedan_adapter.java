package com.example.myapplication3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class gedan_adapter extends BaseAdapter {
    public LayoutInflater layoutInflater;
    public Context mycontext;
    public List<String> mylist;
    public gedan_adapter (Context context, List<String> list) {
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
            view = layoutInflater.inflate(R.layout.gedan1, null);
        }
        else{
            view = convertView;
        }
        //绑定布局
        TextView textView_name = (TextView)view.findViewById(R.id.gedan_item);
        //设置内容
        textView_name.setText(mylist.get(position));
        return view;
    }
}
