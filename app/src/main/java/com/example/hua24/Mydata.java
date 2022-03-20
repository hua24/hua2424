package com.example.hua24;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public class Mydata{
    static List<Map<String,Object>> mylist=new ArrayList<Map<String,Object>>();//扫描到的歌曲列表
    static List<String> play_list1=new ArrayList<>();//默认播放列表
    static List<String> play_list2=new ArrayList<>();//歌单播放列表
    static Bitmap bitmap;//背景
    static boolean list_switch=true;//播放列表选择
    static String name_song;//正在播放的音乐名字
    static String path;//正在播放的歌曲路径
    static String path_picture; //正在播放的音乐的图片
    static String time="0";//正在播放的时间（1s更新一次）
    static String time_onesong="1"; //正在播放的歌曲单首的时间
    static boolean seekbar_stop=false; //标记进度条按下的状态，防止拖动时更新进度条
    static boolean lyric_stop=false; //标记歌词按下的状态，防止拖动时更新歌词
    static int playing_progress=0;//记录进度条当前位置（拖动时不断改变）
    static int playing_progress_lyric=0;//单句歌词的开始时间
    static int pause=-1;//暂停按钮状态切换标记
    static boolean running=true;//程序运行标志
    static String mode="order";//播放模式
    static List<Map<String,Object>> song_lines=new ArrayList<Map<String,Object>>();//歌词
    static String song_artist; //歌手
    static String song_title; //标题
    static String song_album; //专辑
    static long song_offset; //偏移量
    static List<String> name_fromdatabase=new ArrayList<>();//数据库中查询到的名字集合
    static boolean setting_1=true;//返回键不退出应用



    public static void Save_info(Context context, String name, String value){//保存配置信息
        SharedPreferences sp =context.getSharedPreferences(name,MODE_PRIVATE);
        SharedPreferences.Editor ed=sp.edit();
        ed.putString(name,value);
        ed.commit();
    }
    public static String Load_info(Context context,String name,String defult){//调取配置信息
        SharedPreferences sp =context.getSharedPreferences(name,MODE_PRIVATE);
        String s=sp.getString(name,defult);
        return s;
    }
    public static void daoru(Activity activity,String path){//根据路径扫描文件夹，将得到的信息传递到list
        Mydata.mylist.clear();
        EditText editText=(EditText)activity.findViewById(R.id.edittext);
        if(path==null)
            file_scan.fileScan(editText.getText().toString());
        else
            file_scan.fileScan(path);
    }
    public static String next(List<String> list){//根据list获取下一首歌曲的路径
        int n=list.size();
        if(n<=0)
            return null;
        int p=list.indexOf(path);
        if(p==n-1||p<0)
            return list.get(0);
        else
            return list.get(p+1);
    }
    public static String previous(List<String> list){//根据list获取上一首歌曲的路径
        int n=list.size();
        if(n<=0)
            return null;
        int p=list.indexOf(path);
        if(p<=0)
            return list.get(n-1);
        else
            return list.get(p-1);
    }
    public static String random(List<String> list){//随机在list中挑选一个路径
        Random r=new Random();
        int n=list.size();
        if(n<=0)
            return null;
            return list.get(r.nextInt(n));
    }
    public static String loop(List<String> list){//直接返回当前的播放路径
        int n=list.size();

        if(n<=0)
            return null;
        int p=list.indexOf(path);
        if(p<0)
            return list.get(0);
        else
            return path;
    }
    public static String getname_from_path(){//将路径信息转化为名字
        if(Mydata.path.substring(Mydata.path.lastIndexOf("/")).contains("-")) {
            System.out.println(Mydata.path);
            name_song = Mydata.path.substring(Mydata.path.lastIndexOf("/") + 1, Mydata.path.lastIndexOf("-"));
        }
        else
            name_song=Mydata.path.substring(Mydata.path.lastIndexOf("/")+1,Mydata.path.lastIndexOf("."));
        return name_song;
    }
    public static String timefromstring(String str) {//从歌词中读取每一句的开始时间
        int minute = Integer.parseInt(str.substring(1, 3));
        int second = Integer.parseInt(str.substring(4,6));
        int millisecond = Integer.parseInt(str.substring(7,9));
        return String.valueOf(millisecond + second * 1000 + minute * 60 * 1000);
    }
    public static int get_lyrics_position(){//返回当前播放的歌词的位置
        int times=0;
        if(Mydata.time!=null)
            try{
                times=Integer.parseInt(Mydata.time);
            }catch(Exception e){
                return 0;
            }

        int position=0;
        if(Mydata.song_lines!=null)
        for(int i=0;i<Mydata.song_lines.size()-1;i++){
            if(Integer.parseInt(Mydata.song_lines.get(i).get("start_time").toString())<times&&Integer.parseInt(Mydata.song_lines.get(i+1).get("start_time").toString())>times){
                position=i;
            }
            if(Integer.parseInt(Mydata.song_lines.get(Mydata.song_lines.size()-1).get("start_time").toString())<times){
                position=999;
            }
        }
        return position;

    }
    public static void lyric_scan(){//扫描歌词，歌词与歌曲位于相同文件夹下
        Mydata.song_lines.clear();
        File file=null;
        if(Mydata.path!=null) {
            if (Mydata.path.contains(".flac")) {
                file = new File(Mydata.path.replace(".flac", ".lrc"));
            } else {
                file = new File(Mydata.path.replace(".mp3", ".lrc"));
            }
        }
        if (file != null && file.exists()) {
            try {
                setupLyricResource(new FileInputStream(file),"UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public static void setupLyricResource(InputStream inputStream, String charsetName) {//逐行读取歌词
        if(inputStream != null) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charsetName);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = null;
                while((line = reader.readLine()) != null) {
                    analyzeLyric(line);
                }
                reader.close();
                inputStream.close();
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void analyzeLyric(String line) {//逐行解析歌词内容
        int index = line.lastIndexOf("]");
        if(line != null && line.startsWith("[offset:")) {
            // 时间偏移量
            String string = line.substring(8, index).trim();
            Mydata.song_offset = Long.parseLong(string);
            return;
        }
        if(line != null && line.startsWith("[ti:")) {
            // title 标题
            String string = line.substring(4, index).trim();
            Mydata.song_title = string;
            return;
        }
        if(line != null && line.startsWith("[ar:")) {
            // artist 作者
            String string = line.substring(4, index).trim();
            Mydata.song_artist = string;
            return;
        }
        if(line != null && line.startsWith("[al:")) {
            // album 所属专辑
            String string = line.substring(4, index).trim();
            Mydata.song_album = string;
            return;
        }
        if(line != null && line.startsWith("[by:")) {
            return;
        }
        if(line != null && index == 9 && line.trim().length() > 10) {//trim去除前后空格与特殊字符
            // 歌词内容
            Map<String,Object> map=new HashMap<String, Object>();
            map.put("content",line.substring(10, line.length()));//歌词内容
            map.put("start_time",Mydata.timefromstring(line.substring(0, 10)));//歌词开始时间
            Mydata.song_lines.add(map);//添加进列表
        }
    }
    public static int get_song_position(String s){
        for(int i=0;i<Mydata.mylist.size();i++){
            if(Mydata.mylist.get(i).get("path").toString().contains(s))
                return i;
        }
        return 0;
    }

    public static void background(Activity activity,ImageView imageView){
        if(activity.fileList().length>0){
            try {
                FileInputStream fileInputStream=activity.openFileInput("bkground.jpg");
                Mydata.bitmap= BitmapFactory.decodeStream(fileInputStream);
                imageView.setImageBitmap(Mydata.bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
    public static void recycle_bitmap(){
        if(Mydata.bitmap!=null&&!Mydata.bitmap.isRecycled()){
            Mydata.bitmap.recycle();
            Mydata.bitmap=null;
        }
        System.gc();
    }
    public static int direction(int x1,int x2,int y1,int y2){
        int dx=x2-x1;
        int dy=y2-y1;
        if(Math.abs(dx)>Math.abs(dy)){
            if(dx>50)
                return 1;//left
            if(dx<-50)
                return 2;//right
            else
                return 0;
        }
        else
            return 0;
    }
}
