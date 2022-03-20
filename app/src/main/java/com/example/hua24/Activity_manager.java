package com.example.hua24;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class Activity_manager {
    public static  List<Activity> activities = new ArrayList<Activity>();
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }
    public static void shutdown() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }
}
