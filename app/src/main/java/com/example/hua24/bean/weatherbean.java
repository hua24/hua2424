package com.example.hua24.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class weatherbean {
    @SerializedName("city")
    private String city;
    @SerializedName("update_time")
    private String updatetime;
    @SerializedName("data")
    private List<secondbean> secondbeans;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public List<secondbean> getSecondbeans() {
        return secondbeans;
    }

    public void setSecondbeans(List<secondbean> secondbeans) {
        this.secondbeans = secondbeans;
    }


}
