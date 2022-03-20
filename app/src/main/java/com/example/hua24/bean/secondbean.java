package com.example.hua24.bean;

import com.google.gson.annotations.SerializedName;

public class secondbean {
    @SerializedName("day")
    private String day;
    @SerializedName("date")
    private String date;
    @SerializedName("wea")
    private String wea;
    @SerializedName("wea_img")
    private String wea_img;
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWea() {
        return wea;
    }

    public void setWea(String wea) {
        this.wea = wea;
    }
    public String getWea_img() {
        return wea_img;
    }

    public void setWea_img(String wea_img) {
        this.wea_img = wea_img;
    }


}
