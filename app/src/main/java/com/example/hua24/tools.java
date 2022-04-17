package com.example.hua24;

public class tools {//将时间格式化
    public static String change_time(String time){
        if(time==null)
            return "0";
        int x=Integer.parseInt(time);
        int m=x/60000;
        int s=(x%60000)/1000;
        String Time;
        if(s<10)
            Time=m+":0"+s;
        else
            Time=m+":"+s;
        return Time;
    }
    public static String change_time2(int time){
        if(time==0)
            return "0";
        int x=time;
        int m=x/60000;
        int s=(x%60000)/1000;
        String Time;
        if(s<10)
            Time=m+":0"+s;
        else
            Time=m+":"+s;
        return Time;
    }
    public static String change_size(long size){//将文件大小格式化
        float Size=(float)size/(1024*1024);
        return String.format("%.2f",Size)+"MB";
    }
}
