package com.example.hua24;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hua24.bean.weatherbean;
import com.google.gson.Gson;
import com.zlylib.fileselectorlib.FileSelector;
import com.zlylib.fileselectorlib.utils.Const;

import java.util.ArrayList;

public class File_import extends AppCompatActivity {
    public Activity activity;
    ImageView imageView;
    EditText editText;
    ListView listView;
    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 111) {
                Mydata.daoru(activity,null);
                mysqlite mysqlite=new mysqlite(activity,"hua2424");
                SQLiteDatabase sqLiteDatabase=mysqlite.getWritableDatabase();
                sqlite_tools.create_table_mylist("mylist",sqLiteDatabase);
                Song_list_info_adapter Song_list_info_adapter =new Song_list_info_adapter(activity, Mydata.mylist);
                listView.setAdapter(Song_list_info_adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String s=Mydata.mylist.get(position).get("path").toString();
                        System.out.println(s);
                    }
                });
                Mydata.Save_info(activity,"path",editText.getText().toString());//保存为默认路径
                sqLiteDatabase.close();

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_import);
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.argb(17,00,00,00));
        }

        editText=(EditText)findViewById(R.id.edittext);
        imageView=(ImageView)findViewById(R.id.second_bg);
        listView=(ListView)findViewById(R.id.music_list);
        Mydata.background(activity,imageView);
        editText.setText(Mydata.Load_info(activity,"path",null));
    }
    public void open_file(View v){
        FileSelector.from(this).onlySelectFolder().requestCode(1).start();
    }
    public void secondtofirst(View v){
        finish();
        overridePendingTransition(0,android.R.anim.slide_out_right);//界面过渡效果
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//获取返回的uri
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            if(requestCode==1){
                ArrayList<String> essFileList = data.getStringArrayListExtra(Const.EXTRA_RESULT_SELECTION);
                StringBuilder builder = new StringBuilder();
                for (String file : essFileList) {
                    builder.append(file);
                }
                EditText editText=(EditText)findViewById(R.id.edittext);
                editText.setText(builder.toString());//显示在界面上
            }
    }
    public void daoru(View v){//获取界面上的path，给list赋值

        new Thread(){
            public void run(){
                super.run();
                Looper.prepare();
                Toast.makeText(activity,"导入中",Toast.LENGTH_LONG).show();
                Message message=Message.obtain();
                message.what=111;
                handler.sendMessage(message);
                Looper.loop();
            }
        }.start();


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

