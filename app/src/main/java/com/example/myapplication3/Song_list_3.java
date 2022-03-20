package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Song_list_3 extends AppCompatActivity {
    public Activity activity;
    String tablename=null;
    mysqlite mysqlite;
    SQLiteDatabase sqLiteDatabase;
    public static List<String>picked=new ArrayList<>();
    ListView listView;
    Song_list_3_adapter gedan3_adapter;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list_3);

        if(Build.VERSION.SDK_INT >= 21) {//判断版本，设置状态栏透明（透明度可调），没有判断会报错
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.argb(25,00,00,00));
        }
        imageView=(ImageView)findViewById(R.id.gedan3_bg);
        listView=(ListView)findViewById(R.id.gedan3_listview);
        Mydata.background(activity,imageView);
        Intent intent=getIntent();
        tablename=intent.getStringExtra("tablename");
        picked.clear();
        gedan3_adapter=new Song_list_3_adapter(activity,Mydata.play_list1);
        listView.setAdapter(gedan3_adapter);
        mysqlite=new mysqlite(activity,"hua2424");
        sqLiteDatabase=mysqlite.getWritableDatabase();
    }
    public void gedan3_yes(View v){
        if(picked!=null)
        for(int i=0;i<picked.size();i++){
            sqlite_tools.insert_name(picked.get(i),tablename,sqLiteDatabase);//添加选中项进入数据库
        }
        finish();//返回
        overridePendingTransition(0, android.R.anim.slide_out_right);
    }
    public void gedan3_no(View v){
        finish();
        overridePendingTransition(0, android.R.anim.slide_out_right);
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