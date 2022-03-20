package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;

public class play_activity extends AppCompatActivity {
    public Activity activity;
    Myreceiver myreceiver = new Myreceiver();
    lyric_adapter lyric_adapter;
    IntentFilter intentFilter = new IntentFilter();
    NumberFormat numberFormat = NumberFormat.getInstance();
    String progress;
    byte[] image = null;
    Bitmap bitmap;
    ImageView imageView;
    ImageView imageView2;
    boolean activity_running;
    TextView show_time;
    TextView play_name;
    SeekBar seekBar;
    ListView listView;
    TextView total_time;
    ImageButton imageButton;
    Animation rotateAnimation;
    HorizontalScrollView horizontalScrollView;
    int x1=0,x2=0,y1=0,y2=0;//监听滑动获取到的坐标

    Handler handler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==10){
                update_progressbar_and_show_time();
            }
            if(msg.what==11){
                check_lyric();
            }
            if(msg.what==12){
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            }
            if(msg.what==13){
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }
    };
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity_running=true;
        activity=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);

        if (Build.VERSION.SDK_INT >= 21) {//判断版本，设置状态栏透明（透明度可调），没有判断会报错
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.argb(00, 00, 00, 00));
        }

        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);//添加图片旋转动画
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnimation.setInterpolator(lin);//匀速

        numberFormat.setMaximumFractionDigits(0);//取消小数部分

        show_time = (TextView) findViewById(R.id.show_time);
        play_name = (TextView) findViewById(R.id.name_song);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        listView=(ListView)findViewById(R.id.lyric);
        imageView=(ImageView)findViewById(R.id.play_background);
        imageView2=(ImageView)findViewById(R.id.picture);
        total_time = (TextView) findViewById(R.id.total_time);
        imageButton = (ImageButton) findViewById(R.id.pause);
        horizontalScrollView=(HorizontalScrollView)findViewById(R.id.test2);

        lyric_adapter=new lyric_adapter(activity, Mydata.song_lines);
        listView.setAdapter(lyric_adapter);
        Mydata.background(activity,imageView);

        update_progress_and_time();
        Resources resources = activity.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int screenWidth = dm.widthPixels;//获取屏幕宽度
        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.picture_page);
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
        linearParams.width=screenWidth;
        relativeLayout.setLayoutParams(linearParams);//设置图片界面充满屏幕宽
        Mydata.lyric_scan();
        LinearLayout.LayoutParams linearParams2 =(LinearLayout.LayoutParams) listView.getLayoutParams();
        linearParams2.width=screenWidth;
        relativeLayout.setLayoutParams(linearParams2);//设置歌词界面充满屏幕宽

        intentFilter.addAction("picture_change");
        intentFilter.addAction("play_change");
        registerReceiver(myreceiver, intentFilter);//接收广播注册

        update_pause_and_play_and_anime_control();//播放&暂停按钮检查
        update_progressbar_and_show_time();//进度条与实时时间刷新
        if(Mydata.path!=null) {
            update_picture();//图片检查
            update_play_name_and_total_time();//刷新当前播放的音乐名字
        }
        update_play_mode();//播放模式刷新
        total_time.setText(tools.change_time(Mydata.time_onesong));//显示当前播放的单首歌曲的总时间
        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    x1=(int)event.getX();
                    y1=(int)event.getY();
                    System.out.println("down");
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    Mydata.lyric_stop=false;
                    x2=(int)event.getX();
                    y2=(int)event.getY();
                    System.out.println(x1+","+y1);
                    System.out.println(x2+","+y2);
                    if(Mydata.direction(x1,x2,y1,y2)==1){
                        Message message3=Message.obtain();
                        message3.what=12;
                        handler.sendMessage(message3);
                        System.out.println("left");
                    }
                    if(Mydata.direction(x1,x2,y1,y2)==2){
                        Message message4=Message.obtain();
                        message4.what=13;
                        handler.sendMessage(message4);
                        System.out.println("right");
                    }


                }
                return false;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {//进度条控制
            @Override//进度条移动时触发
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Mydata.playing_progress = progress;//获取移动位置
            }

            @Override//按下触发
            public void onStartTrackingTouch(SeekBar seekBar) {
                Mydata.seekbar_stop = true;//让进度条停止刷新
            }

            @Override//松开触发
            public void onStopTrackingTouch(SeekBar seekBar) {
                Mydata.seekbar_stop = false;//进度条继续刷新
                Intent intent = new Intent("progress_change");
                sendBroadcast(intent);//发送跳转播放信号
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mydata.playing_progress_lyric=Integer.parseInt(Mydata.song_lines.get(position).get("start_time").toString());//跳转播放时间
                Intent intent = new Intent("progress_change_lyric");//歌词控制播放进度
                sendBroadcast(intent);


            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {//拖动歌词实现
            @Override
            public boolean onTouch(View v, MotionEvent event) {//标记，拖动时停止刷新
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Mydata.lyric_stop=true;
                    x1=(int)event.getX();
                    y1=(int)event.getY();
                    System.out.println("down");
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    Mydata.lyric_stop=false;
                    x2=(int)event.getX();
                    y2=(int)event.getY();
                    System.out.println(x1+","+y1);
                    System.out.println(x2+","+y2);
                    if(Mydata.direction(x1,x2,y1,y2)==1){
                        Message message3=Message.obtain();
                        message3.what=12;
                        handler.sendMessage(message3);
                        System.out.println("left");
                    }
                    if(Mydata.direction(x1,x2,y1,y2)==2){
                        Message message4=Message.obtain();
                        message4.what=13;
                        handler.sendMessage(message4);
                        System.out.println("right");
                    }
                }
                return false;
            }
        });
    }

    public class Myreceiver extends BroadcastReceiver {//监听来自music_service的广播

        @Override
        public void onReceive(Context context, Intent intent) {//有广播时调用此函数
            switch (intent.getAction()) {
                case "picture_change": {
                    update_picture();//图片检查
                    update_play_name_and_total_time();//刷新当前播放的音乐名字
                    Mydata.lyric_scan();//歌词检查
                    check_lyric();
                    break;
                }
                case "play_change":{
                    update_pause_and_play_and_anime_control();//播放&暂停按钮检查
                    break;
                }
            }

        }
    }

    public void update_progressbar_and_show_time() {//进度条与实时时间刷新
        show_time.setText(tools.change_time(Mydata.time));
        try{
            progress=numberFormat.format((float)Integer.parseInt(Mydata.time)/(float)Integer.parseInt(Mydata.time_onesong)*100);
        }catch (Exception e){
            progress="0";
        }
        //progress = numberFormat.format((float) Integer.parseInt(Mydata.time) / (float) Integer.parseInt(Mydata.time_onesong) * 100);
        seekBar.setProgress(Integer.parseInt(progress));
    }

    public void pause_or_play(View v) {//暂停音乐
        Intent intent = new Intent("check");
        sendBroadcast(intent);
    }

    public void next(View v) {//下一首
        Intent intent = new Intent("next");
        sendBroadcast(intent);
        check_lyric();
        update_progressbar_and_show_time();
        update_pause_and_play_and_anime_control();
    }

    public void previous(View v) {//上一首
        Intent intent = new Intent("previous");
        sendBroadcast(intent);
        check_lyric();
        update_progressbar_and_show_time();
        update_pause_and_play_and_anime_control();
    }

    public void update_play_name_and_total_time() {//刷新当前播放的音乐名字与时间
        if(Mydata.path!=null){
            if(play_name.getText().toString()!=Mydata.getname_from_path())
                play_name.setText(Mydata.getname_from_path());

            if(total_time.getText().toString()!=Mydata.time_onesong)
                total_time.setText(tools.change_time(Mydata.time_onesong));//刷新当前播放的音乐时间
        }

    }

    public void update_play_mode() {//播放模式刷新
        ImageView imageView = (ImageView) findViewById(R.id.mode);
        switch (Mydata.mode) {
            case "order": {
                imageView.setImageResource(R.mipmap.order);
                break;
            }
            case "loop": {
                imageView.setImageResource(R.mipmap.loop);
                break;
            }
            case "random": {
                imageView.setImageResource(R.mipmap.random);
                break;
            }
        }
    }

    public void back(View v) {//返回主界面
        finish();
        overridePendingTransition(0, android.R.anim.slide_out_right);//切换界面过渡效果
    }

    public void mode(View v) {//播放模式切换
        ImageView imageView = (ImageView) findViewById(R.id.mode);
        switch (Mydata.mode) {
            case "order": {
                Mydata.mode = "random";
                Mydata.Save_info(activity, "mode", "random");//保存信息
                imageView.setImageResource(R.mipmap.random);
                break;
            }
            case "loop": {
                Mydata.mode = "order";
                Mydata.Save_info(activity, "mode", "order");
                imageView.setImageResource(R.mipmap.order);
                break;
            }
            case "random": {
                Mydata.mode = "loop";
                Mydata.Save_info(activity, "mode", "loop");
                imageView.setImageResource(R.mipmap.loop);
                break;
            }

        }
    }

    public void show_list(View v) {
        list_in_play_adapter dialog_adapter;
        BottomSheetDialog dialog=new BottomSheetDialog(activity);
        View dialogView= LayoutInflater.from(activity).inflate(R.layout.list_in_play,null);
        ListView listView= (ListView) dialogView.findViewById(R.id.dialog_list);
        if(Mydata.list_switch)//判断显示的播放列表
            dialog_adapter=new list_in_play_adapter(activity,Mydata.play_list1);
        else
            dialog_adapter=new list_in_play_adapter(activity,Mydata.play_list2);
        listView.setAdapter(dialog_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Mydata.list_switch)
                    Mydata.path=Mydata.play_list1.get(position);
                else
                    Mydata.path=Mydata.play_list2.get(position);
                Intent intent=new Intent("play");
                sendBroadcast(intent);
            }
        });
        Resources resources = activity.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) listView.getLayoutParams();
        linearParams.height=screenHeight/2;
        listView.setLayoutParams(linearParams);//设置列表高度为一般屏幕高度
        dialog.setContentView(dialogView);//绑定布局
        dialog.setCancelable(false);//禁止拖动取消，防止与listview拖动冲突
        dialog.setCanceledOnTouchOutside(true);//允许点击外部或返回键取消
        dialog.show();//显示下拉的播放列表

    }

    public void onDestroy() {
        activity_running=false;
        Glide.get(activity).clearMemory();
        Mydata.recycle_bitmap();
        if(bitmap!=null&&!bitmap.isRecycled()){
            bitmap.recycle();
            bitmap=null;
        }
        System.gc();
        unregisterReceiver(myreceiver);
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {//系统返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(0, android.R.anim.slide_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void update_pause_and_play_and_anime_control() {//播放&暂停按钮检查&动画控制
        if (Mydata.pause == 1) {
            if(rotateAnimation != null) {
                imageView2.startAnimation(rotateAnimation);
            }  else {
                imageView2.setAnimation(rotateAnimation);
                imageView2.startAnimation(rotateAnimation);
            }
            imageButton.setImageResource(R.mipmap.show);
        } else {
            if(rotateAnimation != null){

                imageView2.clearAnimation();

            }

            imageButton.setImageResource(R.mipmap.pause);
        }
    }
    public void check_lyric(){
        if(Mydata.song_lines.size()<1){
            listView.setVisibility(View.GONE);
        }else{
            listView.setVisibility(View.VISIBLE);
        }
        if(!Mydata.lyric_stop){
            listView.post(new Runnable(){
                public void run(){
                    if(Mydata.get_lyrics_position()!=999)
                    listView.smoothScrollToPositionFromTop(Mydata.get_lyrics_position(),500);
                }
            });
            lyric_adapter.notifyDataSetChanged();
        }
    }
    @SuppressLint("CheckResult")
    public void update_picture() {//图片检查
        bitmap=null;
        if (Mydata.path_picture != null) {
            MediaMetadataRetriever metadata = new MediaMetadataRetriever();
            metadata.setDataSource(Mydata.path_picture);
            image = metadata.getEmbeddedPicture();
        }
        //imageView2 = (ImageView) findViewById(R.id.picture);
        if(image == null) {//使用默认图片
            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory
                    .Builder(300)
                    .setCrossFadeEnabled(true)
                    .build();
            Glide.with(activity)//设置图片
                    .load(R.mipmap.gif1)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA))
                    .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                    .into(new SimpleTarget<Drawable>() {
                              @Override
                              public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                                  if (drawable instanceof GifDrawable) {
                                      GifDrawable gifDrawable = (GifDrawable) drawable;
                                      gifDrawable.setLoopCount(1);
                                      imageView2.setImageDrawable(drawable);
                                      gifDrawable.start();
                                  }
                              }
                          });
            //imageView.setImageResource(R.mipmap.gif1);
        } else {
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
            Glide.with(activity)
                    .load(BitmapFactory.decodeByteArray(image, 0, image.length))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                    .into(imageView2);

        }
        Glide.get(getApplicationContext()).clearMemory();
    }
    private void update_progress_and_time(){
        new Thread(){
            @Override
            public void run() {
                while(Mydata.running&&activity_running){
                    if(Mydata.pause==1) {
                        if(!Mydata.seekbar_stop){
                            Message message1=Message.obtain();
                            message1.what=10;
                            handler.sendMessage(message1);
                        }
                        Message message2=Message.obtain();
                        message2.what=11;
                        handler.sendMessage(message2);
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