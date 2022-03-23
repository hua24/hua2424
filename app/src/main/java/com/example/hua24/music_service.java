package com.example.hua24;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.io.IOException;

public class music_service extends Service {
    public MediaPlayer mediaPlayer=new MediaPlayer();
    boolean ready=false;//mediaplayer初始化标志
    NotificationManager nm;//通知栏
    Notification notification;//通知栏
    Notification.Builder mBuilder;
    byte[] image=null;
    myreceiver myreceiver =new myreceiver();
    public void onCreate(){
        //注册广播监听
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("play");
        intentFilter.addAction("progress_change");
        intentFilter.addAction("continue_to_play");
        intentFilter.addAction("previous");
        intentFilter.addAction("next");
        intentFilter.addAction("pause_or_play");
        intentFilter.addAction("check");
        intentFilter.addAction("tongzhi_next");
        intentFilter.addAction("tongzhi_previous");
        intentFilter.addAction("tongzhi_shutdown");
        intentFilter.addAction("play_change");
        intentFilter.addAction("picture_change");
        intentFilter.addAction("progress_change_lyric");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(myreceiver,intentFilter);

        new Thread(){//开启新线程，不断向外发送广播
            public void run(){//建立新线程
                Intent intent2=new Intent("picture_change");
                Intent intent3=new Intent("play_change");
                while(Mydata.running)//程序活动时执行
                    try {
                        if(mediaPlayer.isPlaying()){
                            if(Mydata.pause!=1) {
                                Mydata.pause = 1;//刷新播放状态
                                sendBroadcast(intent3);//发送状态改变信号
                            }
                            Mydata.time=null;
                            Mydata.time=String.valueOf(mediaPlayer.getCurrentPosition());//得到播放时的位置
                        }
                        else{
                            if(Mydata.pause!=-1) {
                                Mydata.pause=-1;//刷新播放状态
                                sendBroadcast(intent3);//发送状态改变信号
                            }
                        }
                        if(Mydata.path!=Mydata.path_picture){//检测图片是否与正在播放的音乐相符合，如果音乐改变，图片也随之改变
                            Mydata.path_picture=Mydata.path;
                            sendBroadcast(intent2);//发出检查图片的信号
                        }
                        Thread.sleep(1000);//睡眠1s
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                super.run();
            }
        }.start();
        super.onCreate();


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//播放完成监听
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
                if(Mydata.mode.equals("order"))//播放模式判断
                    if(Mydata.list_switch)
                        Mydata.path=Mydata.next(Mydata.play_list1);//默认播放列表（所有歌曲）
                    else
                        Mydata.path=Mydata.next(Mydata.play_list2);//自定义播放列表
                if(Mydata.mode.equals("random"))
                    if(Mydata.list_switch)
                        Mydata.path=Mydata.random(Mydata.play_list1);
                    else
                        Mydata.path=Mydata.random(Mydata.play_list2);
                if(Mydata.mode.equals("loop"))
                    if(Mydata.list_switch)
                        Mydata.path=Mydata.loop(Mydata.play_list1);
                    else
                        Mydata.path=Mydata.loop(Mydata.play_list2);
                play(0);

            }
        });
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        create_notification();//创建通知栏
        return Service.START_STICKY;//服务销毁后重启
    }
    public class myreceiver extends BroadcastReceiver {
        @Override//接收广播
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "play": {//收到播放指令
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.reset();
                    play(0);
                    break;
                }
                case "progress_change": {//收到进度条主动更新指令
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        float play_progress = (float) Mydata.playing_progress / 100 * Integer.parseInt(Mydata.time_onesong);//根据进度条位置得到歌曲应跳转的位置
                        play((int) play_progress);//寻找指定位置播放
                    }
                    break;
                }
                case "progress_change_lyric": {//点击歌词跳转播放
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        play(Mydata.playing_progress_lyric);//寻找指定位置播放
                    }
                    break;
                }
                case "check": {//暂停或播放
                    if(Mydata.pause==1){
                        pause();
                    }
                    else {
                        continue_to_play();
                    }
                    break;
                }
                case "previous": {//上一首
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    mediaPlayer.reset();
                    if(Mydata.list_switch)
                        Mydata.path = Mydata.previous(Mydata.play_list1);
                    else
                        Mydata.path = Mydata.previous(Mydata.play_list2);
                    play(0);
                    break;
                }
                case "next": {//下一首
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    mediaPlayer.reset();
                    System.out.println(Mydata.mode);
                    if(Mydata.mode.equals("order"))
                        if(Mydata.list_switch)
                            Mydata.path=Mydata.next(Mydata.play_list1);
                        else
                            Mydata.path=Mydata.next(Mydata.play_list2);
                    if(Mydata.mode.equals("random"))
                        if(Mydata.list_switch)
                            Mydata.path=Mydata.random(Mydata.play_list1);
                        else
                            Mydata.path=Mydata.random(Mydata.play_list2);
                    if(Mydata.mode.equals("loop"))
                        if(Mydata.list_switch)
                            Mydata.path=Mydata.loop(Mydata.play_list1);
                        else
                            Mydata.path=Mydata.loop(Mydata.play_list2);
                    play(0);
                    break;
                }
                case "tongzhi_shutdown": {//关闭程序
                    Activity_manager.shutdown();
                    break;
                }
                case "play_change":{
                    //System.out.println("2222222222222222");
                    check();
                    break;
                }
                case "picture_change":{
                    //System.out.println("1111111111111");
                    check_picture();
                    break;
                }
                case "android.intent.action.PHONE_STATE":{
                    if(Mydata.pause==1){
                        pause();
                    }
                    System.out.println("phonecall");
                    break;
                }
            }
        }

    }
    public void create_notification(){
        RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.notificaction);//绑定通知栏布局
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder=new Notification.Builder(this)
                .setContentTitle("a")
                .setContentText("b")
                .setSmallIcon(R.mipmap.pause);
        notification = mBuilder.build();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel nc=new NotificationChannel("1","mytongzhi",NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(nc);
            mBuilder.setChannelId("1");

        }
        //来自通知栏的广播
        Intent intent_tongzhi_next=new Intent("next");
        Intent intent_pause_or_play=new Intent("check");
        Intent intent_tongzhi_previous=new Intent("previous");
        Intent intent_tongzhi_shutdown=new Intent("tongzhi_shutdown");
        Intent intent_tongzhi_picture=new Intent();
        intent_tongzhi_picture.setClass(this,play_activity.class);
        PendingIntent pendingIntent_tongzhi_picture=PendingIntent.getActivities(this,0,new Intent[]{intent_tongzhi_picture},0);
        PendingIntent pendingIntent_tongzhi_next=PendingIntent.getBroadcast(this,0,intent_tongzhi_next,0);
        PendingIntent pendingIntent_tongzhi_previous=PendingIntent.getBroadcast(this,0,intent_tongzhi_previous,0);
        PendingIntent pendingIntent_pause_or_play=PendingIntent.getBroadcast(this,0,intent_pause_or_play,0);
        PendingIntent pendingIntent_tongzhi_shutdown=PendingIntent.getBroadcast(this,0,intent_tongzhi_shutdown,0);
        //按钮监听发送广播
        remoteViews.setOnClickPendingIntent(R.id.tongzhi_next,pendingIntent_tongzhi_next);
        remoteViews.setOnClickPendingIntent(R.id.tongzhi_pause_or_play,pendingIntent_pause_or_play);
        remoteViews.setOnClickPendingIntent(R.id.tongzhi_previous,pendingIntent_tongzhi_previous);
        remoteViews.setOnClickPendingIntent(R.id.tongzhi_shutdown,pendingIntent_tongzhi_shutdown);
        remoteViews.setOnClickPendingIntent(R.id.tongzhi_picture,pendingIntent_tongzhi_picture);
        mBuilder.setContent(remoteViews);
        mBuilder.setOngoing(true);
        nm.notify(1,notification);
        startForeground(1,notification);//前台服务，防止系统将其自动关闭
    }
    public void check_picture(){
        //bitmap=null;
        RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.notificaction);//绑定通知栏布局
        mBuilder.setContent(remoteViews);
        //remoteViews=new RemoteViews("com.example.myapplication3",R.layout.notificaction);//绑定通知栏布局
        MediaMetadataRetriever metadata=new MediaMetadataRetriever();
        if(Mydata.path_picture!=null) {
            metadata.setDataSource(Mydata.path_picture);
            image=metadata.getEmbeddedPicture();
        }
        if (image==null){//没有就用默认图片
            remoteViews.setImageViewResource(R.id.tongzhi_picture,R.mipmap.default_pictures);
        }else {
            //bitmap= BitmapFactory.decodeByteArray(image,0,image.length);
            remoteViews.setImageViewBitmap(R.id.tongzhi_picture,BitmapFactory.decodeByteArray(image,0,image.length));
            //bitmap=null;
        }
        if(Mydata.path!=null)//刷新播放的音乐名字
            remoteViews.setTextViewText(R.id.tongzhi_name,Mydata.getname_from_path());
        nm.notify(1,notification);
        System.out.println("11111");
    }
    public void check(){
        RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.notificaction);//绑定通知栏布局
        mBuilder.setContent(remoteViews);
        //remoteViews=new RemoteViews("com.example.myapplication3",R.layout.notificaction);//绑定通知栏布局
        if(Mydata.pause==1){//管理暂停按钮图片切换
            remoteViews.setImageViewResource(R.id.tongzhi_pause_or_play,R.mipmap.show_dark);
            //System.out.println("show");
        }
        else{
            //System.out.println("pause");
            remoteViews.setImageViewResource(R.id.tongzhi_pause_or_play,R.mipmap.pause_dark);
        }
        nm.notify(1,notification);
        System.out.println("22222");
    }
    public void play(int progress){//根据data里的路径播放音乐（procress指定位置播放，默认0）
        if(Mydata.path!=null)
        try {
            mediaPlayer.setDataSource(Mydata.path);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(progress);//跳转到设置的播放位置
            mediaPlayer.start();
            Mydata.lyric_scan();
            ready=true;//表示mediaplayer初始化完毕
            Mydata.time_onesong= String.valueOf(mediaPlayer.getDuration());//得到当前播放的音乐的时间长度
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void pause(){//暂停
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }
    public void continue_to_play(){//从暂停中恢复播放
        if(!mediaPlayer.isPlaying()&&ready)//防止未初始化就播放
            mediaPlayer.start();
    }
    public void onDestroy(){//销毁服务，关闭程序时调用
        if(mediaPlayer.isPlaying())
        mediaPlayer.stop();
        mediaPlayer.release();
        Mydata.running=false;
        unregisterReceiver(myreceiver);
        stopForeground(true);
        super.onDestroy();
        System.exit(0);
    }
    public IBinder onBind(Intent intent) {
        return null;
    }
}