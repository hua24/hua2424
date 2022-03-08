package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class gadan2 extends AppCompatActivity {
    public Activity activity;
    String tablename=null;
    public static List<String> name=new ArrayList<>();
    public static List<String> picked=new ArrayList<>();
    public static boolean ckeckbox=false;
    mysqlite mysqlite;
    SQLiteDatabase sqLiteDatabase;
    ListView listView;
    gedan2_adapter gedan2_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity=this;
        if(Build.VERSION.SDK_INT >= 21) {//判断版本，设置状态栏透明（透明度可调），没有判断会报错
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.argb(25,00,00,00));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadan2);
        ImageView imageView=(ImageView)findViewById(R.id.gedan2_bg);
        Mydata.background(activity,imageView);
        Intent intent=getIntent();
        tablename=intent.getStringExtra("tablename");
        mysqlite=new mysqlite(activity,"hua2424");
        sqLiteDatabase=mysqlite.getWritableDatabase();
        sqlite_use.scan_table(tablename,sqLiteDatabase,name);
        gedan2_adapter=new gedan2_adapter(activity,name);
        listView=(ListView)findViewById(R.id.gedan2_listview);
        listView.setAdapter(gedan2_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sqlite_use.scan_table(tablename,sqLiteDatabase,Mydata.play_list2);//将指定的歌单的内容读取到播放列表
                Mydata.list_switch=false;//歌单列表切换为自定义
                Mydata.path=name.get(position);//获取当前选择的路径并进行播放
                Intent intent=new Intent("play");
                sendBroadcast(intent);//发出播放指令
            }
        });


    }
    protected void onRestart() {//从添加歌曲界面返回时调用，刷新添加后的信息
        sqlite_use.scan_table(tablename,sqLiteDatabase,name);
        gedan2_adapter=new gedan2_adapter(activity,name);
        listView=(ListView)findViewById(R.id.gedan2_listview);
        listView.setAdapter(gedan2_adapter);
        super.onRestart();
    }
    public void back_gedan2(View v){
        finish();
        overridePendingTransition(0,android.R.anim.slide_out_right);//界面过渡效果
    }
    public void gedan_add(View v){//跳转到添加歌曲的界面
        Intent intent=new Intent();
        intent.putExtra("tablename",tablename);//传递歌单名称
        intent.setClass(activity,gedan3.class);
        startActivity(intent);
        overridePendingTransition(0,android.R.anim.slide_out_right);//界面过渡效果

    }
    public void gedan_delete(View v){//删除歌单内的歌曲
        TextView textView1=(TextView)findViewById(R.id.no);
        TextView textView2=(TextView)findViewById(R.id.yes);
        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        ckeckbox=true;//标记，用来显示checkbox
        gedan2_adapter.notifyDataSetChanged();//刷新adapter与listview
    }
    public void gedan_play(View v){//播放当前歌单
        sqlite_use.scan_table(tablename,sqLiteDatabase,Mydata.play_list2);//歌单装载进播放列表
        if(Mydata.play_list2.size()>0){
            Mydata.list_switch=false;
            Intent intent = new Intent("next");
            sendBroadcast(intent);
        }
    }
    public void no(View v){//取消选择
        TextView textView1=(TextView)findViewById(R.id.no);
        TextView textView2=(TextView)findViewById(R.id.yes);
        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        ckeckbox=false;
        gedan2_adapter.notifyDataSetChanged();
    }
    public void yes(View v){//确定删除
        TextView textView1=(TextView)findViewById(R.id.no);
        TextView textView2=(TextView)findViewById(R.id.yes);
        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        if(picked!=null)
            for(int i=0;i<picked.size();i++){
                sqlite_use.delete_name(picked.get(i),tablename,sqLiteDatabase);//删除选中的歌曲路径
            }
        ckeckbox=false;
        sqlite_use.scan_table(tablename,sqLiteDatabase,name);
        gedan2_adapter=new gedan2_adapter(this,name);
        listView.setAdapter(gedan2_adapter);//刷新界面
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//系统返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(0, android.R.anim.slide_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }
    public void onDestroy() {
        Mydata.recycle_bitmap();
        super.onDestroy();
    }
}