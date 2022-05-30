package com.example.hua24;

import android.media.MediaMetadataRetriever;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class file_scan {


    public static void fileScan(String path){//扫描文件夹，将信息放在list里
        File file =new File(path);
        File[] files = file.listFiles();
        Map<String,Object> map;
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        if(files!=null){
            for(File item : files){
                //item.isHidden()判断文件是否为隐藏的文件
                if(item.isDirectory()&&!item.isHidden()){
                    //如果是文件夹进行再次扫描
                    fileScan(String.valueOf(item));
                }
                else if(item.getName().endsWith(".mp3")||item.getName().endsWith(".flac")||item.getName().endsWith(".m4a")||item.getName().endsWith(".wav")) {
                    //System.out.println(item.getName());
                    mediaMetadataRetriever.setDataSource(item.getAbsolutePath());
                    //System.out.println(item.length());
                    String s1 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    //System.out.println(s1);
                    map=new HashMap<String, Object>();
                    /*if(item.getName().indexOf("-")!=-1)
                        map.put("name",item.getName().substring(0,item.getName().lastIndexOf("-")));
                    else*/
                        map.put("name",item.getName().substring(0,item.getName().lastIndexOf(".")));
                    map.put("size",tools.change_size(item.length()));
                    map.put("time",s1);
                    map.put("path",item.getAbsolutePath());
                    Mydata.mylist.add(map);//listview使用的信息列表
                    Mydata.play_list1.add(item.getAbsolutePath());//播放列表，里面只有播放路径
                }
            }


        }
    }

}
