package com.example.hua24;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class lyric_adapter extends BaseAdapter {
    public LayoutInflater layoutInflater;
    public Context mycontext;
    public List<Map<String,Object>> mylist;
    public lyric_adapter (Context context, List<Map<String,Object>> list) {
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
            view = layoutInflater.inflate(R.layout.lyric_item, null);
        }
        else{
            view = convertView;
        }
        //绑定布局
        TextView textView_name = (TextView)view.findViewById(R.id.textview_lyric);
        textView_name.setTextColor(Color.rgb(255,255, 255));//普通状态字体颜色
        textView_name.setTextSize(15);//普通状态字体大小
        if(Mydata.get_lyrics_position()==position){
            textView_name.setTextColor(Color.rgb(255,0, 0));//当前播放的歌词
            textView_name.setTextSize(20);//加大字体
        }

        //设置内容
        if(Mydata.song_lines.size()>1)
        textView_name.setText(Mydata.song_lines.get(position).get("content").toString());
        return view;
    }
}
