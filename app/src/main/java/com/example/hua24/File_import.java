package com.example.hua24;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.zlylib.fileselectorlib.FileSelector;
import com.zlylib.fileselectorlib.utils.Const;

import java.util.ArrayList;

public class File_import extends AppCompatActivity {
    public Activity activity;
    ImageView imageView;
    EditText editText;
    ListView listView;
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
        Mydata.daoru(this,null);
        Song_list_info_adapter Song_list_info_adapter =new Song_list_info_adapter(this, Mydata.mylist);
        listView.setAdapter(Song_list_info_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s=Mydata.mylist.get(position).get("path").toString();
                System.out.println(s);
            }
        });
        Mydata.Save_info(this,"path",editText.getText().toString());//保存为默认路径
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

