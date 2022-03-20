package com.example.hua24;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class Song_list_info_adapter extends BaseAdapter {//所有歌曲界面，显示歌曲信息
    public LayoutInflater layoutInflater;
    public Context mycontext;
    public List<Map<String,Object>> mylist;
    public Song_list_info_adapter(Context context, List<Map<String,Object>> list) {
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
            view = layoutInflater.inflate(R.layout.song_list_info_item, null);
        }
        else{
            view = convertView;
        }
        //绑定布局
        TextView textView_name = (TextView)view.findViewById(R.id.item_name);
        TextView textView_size = (TextView)view.findViewById(R.id.item_size);
        TextView textView_time = (TextView)view.findViewById(R.id.item_time);
        TextView textView_path = (TextView)view.findViewById(R.id.item_path);
        //设置内容
        textView_name.setText(mylist.get(position).get("name").toString());//歌曲名
        textView_size.setText(mylist.get(position).get("size").toString());//歌曲大小
        textView_time.setText(tools.change_time(mylist.get(position).get("time").toString()));//歌曲时长
        textView_path.setText(mylist.get(position).get("path").toString());//歌曲路径
        return view;
    }
}
