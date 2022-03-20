package com.example.hua24;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Sleep_timing extends AppCompatActivity {
    public Activity activity;
    TimePicker timePicker;
    AlarmManager am;
    ImageView imageView;
    int hour;
    int minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep_timing);

        if(Build.VERSION.SDK_INT >= 21) {//判断版本，设置状态栏透明（透明度可调），没有判断会报错
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.argb(25,00,00,00));
        }

        imageView=(ImageView)findViewById(R.id.third_bg);
        timePicker=(TimePicker)findViewById(R.id.timing_picker);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Mydata.background(activity,imageView);
    }
    public void timing_no(View v){
        Intent intent = new Intent("tongzhi_shutdown");
        PendingIntent pi = PendingIntent.getBroadcast(activity, 0, intent, 0);
        am.cancel(pi);
        Toast.makeText(activity,"取消成功",Toast.LENGTH_LONG).show();
    }
    public void timing_yes(View v){
        timePicker=(TimePicker)findViewById(R.id.timing_picker);
        if (Build.VERSION.SDK_INT >= 23) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }
        if(hour>23)
            hour=23;
        if(minute>59)
            minute=59;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        Intent intent = new Intent("tongzhi_shutdown");
        PendingIntent pi = PendingIntent.getBroadcast(activity, 0, intent, 0);
        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+5000, pi);
        Toast.makeText(activity,"定时成功",Toast.LENGTH_LONG).show();
    }
    public void back_timing(View v){
        finish();
        overridePendingTransition(0, android.R.anim.slide_out_right);
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
        super.onDestroy();
    }
}