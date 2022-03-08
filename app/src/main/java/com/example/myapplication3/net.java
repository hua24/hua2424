package com.example.myapplication3;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class net {
    public static String getdata(String myurl){
        String result="";
        HttpURLConnection connection=null;
        InputStream inputStream;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = null;
        try {
            //建立连接
            URL url=new URL(myurl);
            connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            //获取输入流
            inputStream=connection.getInputStream();
            inputStreamReader=new InputStreamReader(inputStream);
            bufferedReader=new BufferedReader(inputStreamReader);
            stringBuilder=new StringBuilder();
            String line="";
            //逐行读数据
            while((line=bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            result=stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(connection!=null)
                connection.disconnect();
            if(inputStreamReader!=null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bufferedReader!=null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return result;
    }
    public static String getweatherofcity(String city){
        String result="";
        result=getdata("https://yiketianqi.com/api?unescape=1&version=v1&appid=74313549&appsecret=3Y5xLjIv&city="+city);
        return result;

    }
}
