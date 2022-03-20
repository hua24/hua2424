package com.example.hua24;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Song_list_1 extends AppCompatActivity {
    public Activity activity;
    mysqlite mysqlite;
    SQLiteDatabase sqLiteDatabase;
    ListView listView;
    Song_list_1_adapter gedan_adapter;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list_1);

        if(Build.VERSION.SDK_INT >= 21) {//判断版本，设置状态栏透明（透明度可调），没有判断会报错
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.argb(25,00,00,00));
        }
        imageView=(ImageView)findViewById(R.id.gedan_bg);
        listView=(ListView)findViewById(R.id.gedan);
        Mydata.background(activity,imageView);
        mysqlite=new mysqlite(activity,"hua2424");
        sqLiteDatabase=mysqlite.getWritableDatabase();
        sqlite_tools.scan_table("name_list",sqLiteDatabase,Mydata.name_fromdatabase);//获取数据库中歌单名称列表
        gedan_adapter=new Song_list_1_adapter(activity,Mydata.name_fromdatabase);
        listView.setAdapter(gedan_adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override//歌单长按，实现重命名或删除
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater layoutInflater = LayoutInflater.from(activity);
                LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.song_list_longclick, null);
                TextView rename=(TextView)layout.findViewById(R.id.rename_gedan);
                TextView delete=(TextView)layout.findViewById(R.id.delete_gedan);
                Dialog dialog =new AlertDialog.Builder(activity).create();
                dialog.show();
                dialog.getWindow().setContentView(layout);
                rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInput(Mydata.name_fromdatabase.get(position));//弹出重命名输入框
                        dialog.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sqlite_tools.delete_table(Mydata.name_fromdatabase.get(position),sqLiteDatabase);//从数据库删除
                        sqlite_tools.scan_table("name_list",sqLiteDatabase,Mydata.name_fromdatabase);//扫描
                        gedan_adapter.notifyDataSetChanged();//重新显示
                        dialog.dismiss();
                    }
                });
                return true;//防止长按触发下方点击事件
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.putExtra("tablename",Mydata.name_fromdatabase.get(position));
                intent.setClass(activity, Song_list_2.class);//进入指定的歌单，显示歌单内的歌曲
                startActivity(intent);
                overridePendingTransition(0,android.R.anim.slide_out_right);//界面过渡效果
            }
        });

    }
    public void test1(View v){//新建歌单
        showInput2();
    }
    public void test2(View v){
        showcheck();
    }
    public void showInput(String name) {
        EditText editText = new EditText(activity);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity).setTitle("输入新名称").setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean same=false;
                        for(int n=0;n<Mydata.name_fromdatabase.size();n++)
                        if(editText.getText().toString().equals(Mydata.name_fromdatabase.get(n))){
                            same=true;
                            Toast.makeText(activity,"名称重复",Toast.LENGTH_SHORT).show();
                        }
                        if(!same){
                            sqlite_tools.rename_table(name,editText.getText().toString(),sqLiteDatabase);
                            sqlite_tools.scan_table("name_list",sqLiteDatabase,Mydata.name_fromdatabase);
                            gedan_adapter.notifyDataSetChanged();
                        }
                    }
                });
        builder.create().show();
    }
    public void showcheck() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity).setTitle("确定删除？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteDatabase("hua2424");//清空数据库
                        mysqlite=new mysqlite(activity,"hua2424");
                        sqLiteDatabase=mysqlite.getWritableDatabase();
                        sqlite_tools.scan_table("name_list",sqLiteDatabase,Mydata.name_fromdatabase);
                        gedan_adapter.notifyDataSetChanged();//刷新
                    }
                });
        builder.create().show();
    }
    public void showInput2() {
        EditText editText = new EditText(activity);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity).setTitle("输入名称").setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean same=false;
                        for(int n=0;n<Mydata.name_fromdatabase.size();n++)
                            if(editText.getText().toString().equals(Mydata.name_fromdatabase.get(n))){
                                same=true;
                                Toast.makeText(activity,"名称重复",Toast.LENGTH_SHORT).show();
                            }
                        if(!same){
                            String s=editText.getText().toString();
                            if(!s.equals(""))//判断输入框是否为空
                                sqlite_tools.create_table(s,sqLiteDatabase);//添加歌单名称进入数据库
                            sqlite_tools.scan_table("name_list",sqLiteDatabase,Mydata.name_fromdatabase);
                            gedan_adapter.notifyDataSetChanged();
                        }
                    }
                });
        builder.create().show();
    }
    public void back_gedan(View v){
        finish();
        overridePendingTransition(0,android.R.anim.slide_out_right);//界面过渡效果
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//系统返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(0, android.R.anim.slide_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }
    public void onDestroy(){
        Mydata.recycle_bitmap();
        sqLiteDatabase.close();
        super.onDestroy();
    }
}