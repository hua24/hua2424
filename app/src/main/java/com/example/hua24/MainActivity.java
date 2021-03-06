package com.example.hua24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.hua24.bean.weatherbean;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {
    Activity activity;
    Myreceiver myreceiver = new Myreceiver();
    IntentFilter intentFilter = new IntentFilter();
    NumberFormat numberFormat = NumberFormat.getInstance();
    ProgressBar progressBar;
    SwitchCompat mSwitch;
    SwitchCompat mSwitch2;
    Bitmap bitmap = null;
    byte[] image = null;
    ListView listView;
    String progress;
    EditText editText_search;
    ImageView imageView;
    ImageView imageView2;
    Song_list_info_adapter Song_list_info_adapter;
    mysqlite mysqlite;
    SQLiteDatabase sqLiteDatabase;
    boolean activity_running;
    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String weather = (String) msg.obj;//包含所有天气信息
                Gson gson = new Gson();
                weatherbean weatherbean = gson.fromJson(weather, com.example.hua24.bean.weatherbean.class);
                if (weatherbean == null)
                    return;
                String today = weatherbean.getCity() + weatherbean.getUpdatetime() + weatherbean.getSecondbeans().get(0).getWea_img();
                System.out.println(today);
                TextView textView1 = (TextView) findViewById(R.id.weather_today);
                TextView textView2 = (TextView) findViewById(R.id.weather_tomorrow);
                TextView textView3 = (TextView) findViewById(R.id.city);
                ImageView imageView = (ImageView) findViewById(R.id.img_weather);
                if (weatherbean.getSecondbeans().get(0).getWea() != null)
                    textView1.setText(weatherbean.getSecondbeans().get(0).getWea());
                if (weatherbean.getSecondbeans().get(1).getWea() != null)
                    textView2.setText(weatherbean.getSecondbeans().get(1).getWea());
                if (weatherbean.getCity() != null)
                    textView3.setText(weatherbean.getCity());
                imageView.setImageResource(getimgfromweather(weatherbean.getSecondbeans().get(0).getWea_img()));
            }
            if (msg.what == 2) {
                update_progressbar();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        activity_running = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {//判断版本，设置状态栏透明（透明度可调），没有判断会报错
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.argb(25, 00, 00, 00));
        }
        mysqlite=new mysqlite(activity,"hua2424");
        sqLiteDatabase=mysqlite.getWritableDatabase();
        listView = (ListView) findViewById(R.id.music_list2);
        imageView = (ImageView) findViewById(R.id.small_picture);
        imageView2 = (ImageView) findViewById(R.id.main_bg);
        editText_search = (EditText) findViewById(R.id.search_song);
        progressBar = (ProgressBar) findViewById(R.id.small_progressbar);
        requestPermissions();
        /*if (Mydata.Load_info(activity, "path", null) != null)//默认文件位置
            Mydata.daoru(activity, Mydata.Load_info(activity, "path", null));*/
        sqlite_tools.get_mylist_from_database(sqLiteDatabase);//从数据库中获取之前扫描到的列表
        if (Mydata.Load_info(activity, "weathershow", "no").equals("yes"))
            getweather(Mydata.Load_info(activity, "city", "婺源"));
        intentFilter.addAction("time_change");
        intentFilter.addAction("picture_change");
        intentFilter.addAction("play_change");
        registerReceiver(myreceiver, intentFilter);
        numberFormat.setMaximumFractionDigits(0);
        update_pause_and_play();
        update_progressbar();
        update_progress();
        Song_list_info_adapter = new Song_list_info_adapter(activity, Mydata.mylist);
        listView.setAdapter(Song_list_info_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mydata.list_switch = true;
                String s = Mydata.mylist.get(position).get("path").toString();
                String s1 = Mydata.mylist.get(position).get("time").toString();
                Mydata.path = s;
                Mydata.time_onesong = Integer.parseInt(s1);
                Intent intent = new Intent("play");
                sendBroadcast(intent);
                update_playing_name();
                update_pause_and_play();
            }
        });
        Intent intent = new Intent(activity, music_service.class);
        startService(intent);

        Mydata.mode = Mydata.Load_info(activity, "mode", "order");//默认播放模式
        Activity_manager.addActivity(activity);
        Mydata.background(activity, imageView2);
        mSwitch = (SwitchCompat) findViewById(R.id.setting_back);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Mydata.Save_info(activity, "back", "yes");
                } else {
                    Mydata.Save_info(activity, "back", "no");
                }
            }
        });
        if (Mydata.Load_info(activity, "back", "no").equals("yes")) {
            mSwitch.setChecked(true);
        } else
            mSwitch.setChecked(false);
        mSwitch2 = (SwitchCompat) findViewById(R.id.setting_weather);
        mSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Mydata.Save_info(activity, "weathershow", "yes");
                    getweather(Mydata.Load_info(activity, "city", "婺源"));
                } else {
                    Mydata.Save_info(activity, "weathershow", "no");
                }
            }
        });
        if (Mydata.Load_info(activity, "weathershow", "no").equals("yes")) {
            mSwitch2.setChecked(true);
        } else
            mSwitch2.setChecked(false);
        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {//监控键盘回车
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String s = editText_search.getText().toString();
                    if (s.length() > 0)
                        listView.setSelection(Mydata.get_song_position(s));
                    return true;
                }

                return false;
            }
        });
    }
    public void onRestart() {
        Song_list_info_adapter.notifyDataSetChanged();
        super.onRestart();
    }
    public class Myreceiver extends BroadcastReceiver {//监听来自music_service的广播

        @Override
        public void onReceive(Context context, Intent intent) {//有广播时调用此函数
            switch (intent.getAction()) {
                case "picture_change": {
                    update_playing_name();
                    update_picture();
                    break;
                }
                case "play_change": {
                    update_pause_and_play();
                    break;
                }
            }

        }
    }
    public void setting(View v){
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.setting_layout);
        if(linearLayout.getVisibility()==View.VISIBLE)
            linearLayout.setVisibility(View.GONE);
        else {
            TextView textView=(TextView)findViewById(R.id.setting_city);
            textView.setText(Mydata.Load_info(activity,"city","婺源"));
            linearLayout.setVisibility(View.VISIBLE);
        }


    }
    public void set_city(View v){
        showInput2();
    }
    public void showInput2() {
        EditText editText = new EditText(activity);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(activity).setTitle("输入城市名称").setView(editText)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String s=editText.getText().toString();
                        if(!s.equals("")) {//判断输入框是否为空
                            Mydata.Save_info(activity, "city", s);
                            TextView textView=(TextView)findViewById(R.id.setting_city);
                            textView.setText(s);
                            getweather(Mydata.Load_info(activity,"city","婺源"));
                        }
                    }
                });
        builder.create().show();
    }
    public void small_pause(View v){
        Intent intent = new Intent("check");
        sendBroadcast(intent);
    }
    public void small_next(View v){
        Intent intent = new Intent("next");
        sendBroadcast(intent);
    }
    public void update_progressbar(){//进度条刷新
            try{
                progress=numberFormat.format((float)Mydata.time /(float)Mydata.time_onesong *100);
            }catch (Exception e){
                progress="0";
            }
        progressBar.setProgress(Integer.parseInt(progress));
    }
    public void update_playing_name(){
        TextView test_name=(TextView)findViewById(R.id.small_name);
        test_name.setText(Mydata.getname_from_path());
    }
    public void update_pause_and_play() {//播放&暂停按钮检查
        ImageView imageView = (ImageView) findViewById(R.id.small_pause_or_play);
        if (Mydata.pause == 1) {
            imageView.setImageResource(R.mipmap.show_dark);
        } else {
            imageView.setImageResource(R.mipmap.pause_dark);
        }
    }
    public void onDestroy(){
        activity_running=false;
        Intent intent=new Intent(activity,music_service.class);
        stopService(intent);//解除服务绑定
        Mydata.recycle_bitmap();
        sqLiteDatabase.close();
        super.onDestroy();
    }
    public void search(View v){
        String s=editText_search.getText().toString();
        if(s.length()>0)
            listView.setSelection(Mydata.get_song_position(s));
    }
    public void open_side(View v){//打开侧边栏
        DrawerLayout drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(Gravity.LEFT);
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.setting_layout);
        if(linearLayout.getVisibility()==View.VISIBLE)
            linearLayout.setVisibility(View.GONE);

    }
    public void to_file_import(View v){//文件导入界面
        Intent intent =new Intent();
        intent.setClass(activity, File_import.class);
        startActivity(intent);
        overridePendingTransition(0,android.R.anim.slide_out_right);//界面过渡效果
    }
    public void to_sleep_timing(View v){//睡眠定时界面
        Intent intent =new Intent();
        intent.setClass(activity, Sleep_timing.class);
        startActivity(intent);
        overridePendingTransition(0,android.R.anim.slide_out_right);//界面过渡效果

    }
    public void songlist_manage(View v){//歌单管理界面
        Intent intent =new Intent();
        intent.setClass(activity, Song_list_1.class);
        startActivity(intent);
        overridePendingTransition(0,android.R.anim.slide_out_right);//界面过渡效果
    }
    public void to_playing(View v){//播放界面
        Intent intent =new Intent();
        intent.setClass(activity,play_activity.class);
        startActivity(intent);
        overridePendingTransition(0,android.R.anim.slide_out_right);//界面过渡效果
    }
    public void update_picture() {//图片检查
        if (Mydata.path_picture != null&&Mydata.is_exists(Mydata.path_picture)) {
                MediaMetadataRetriever metadata = new MediaMetadataRetriever();
                metadata.setDataSource(Mydata.path_picture);
                image = metadata.getEmbeddedPicture();
        }
        imageView = (ImageView) findViewById(R.id.small_picture);
        if (image == null) {//使用默认图片
            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
            Glide.with(activity)//设置图片
                    .load(R.mipmap.default_picture2)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA))
                    .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                    .into(imageView);
            Glide.get(getApplicationContext()).clearMemory();
            //imageView.setImageResource(R.mipmap.gif1);
        } else {
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
            Glide.with(activity)
                    .load(bitmap)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                    .into(imageView);
            Glide.get(getApplicationContext()).clearMemory();
            bitmap=null;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//系统返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(Mydata.Load_info(activity,"back","no").equals("yes")) {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }else {
                finish();
            }
        }
        overridePendingTransition(0, android.R.anim.slide_out_right);
        return super.onKeyDown(keyCode, event);
    }
    public void select_img(View v){//挑选背景图片
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            try {
                bytesToImageFile(readBytes(data.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public byte[] readBytes(Uri inUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(inUri);
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        byte[] buffer = new byte[16*1024];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
    private void bytesToImageFile(byte[] bytes) {//路径:/data/data/com.example.hua24/files/bkground.jpg
        try {
            FileOutputStream fos = openFileOutput("bkground.jpg",MODE_PRIVATE);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView imageView=(ImageView)findViewById(R.id.main_bg);
        Mydata.background(activity,imageView);
    }
    private void getweather(String cityname){//根据城市名称获取天气数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=Message.obtain();
                message.what=1;
                message.obj=net.getweatherofcity(cityname);
                handler.sendMessage(message);
            }
        }).start();
    }
    public int getimgfromweather(String weather){//为天气添加对应图片
        //xue、lei、shachen、wu、bingbao、yun、yu、yin、qing
        int result=0;
        switch (weather){
            case "xue":
                result=R.mipmap.xue;
                break;
            case "lei":
                result=R.mipmap.lei;
                break;
            case "shachen":
                result=R.mipmap.shachen;
                break;
            case "wu":
                result=R.mipmap.wu;
                break;
            case "bingbao":
                result=R.mipmap.bingbao;
                break;
            case "yun":
                result=R.mipmap.yun;
                break;
            case "yu":
                result=R.mipmap.yu;
                break;
            case "yin":
                result=R.mipmap.ying;
                break;
            case "qing":
                result=R.mipmap.qing;
                break;
            default:
                result=R.mipmap.qing;
                break;
        }
        return result;
    }
    private void requestPermissions() {
            //判断是否同意此权限
            if (checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ||checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE},
                            100);
            }
    }
    private void update_progress(){//发送更新进度条指令
        new Thread(){
            @Override
            public void run() {
                while(Mydata.running&&activity_running){
                    if(Mydata.pause==1) {
                        Message message1=Message.obtain();
                        message1.what=2;
                        handler.sendMessage(message1);
                    }
                    try {
                        Thread.sleep(1000);//睡眠1s
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        }.start();
    }
}