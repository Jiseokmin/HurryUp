package kr.study.hurryup;


import android.app.Application;
import android.content.Context;

public class OptionData extends Application {
    private String ip_address;

    public OptionData() {
    }

    public OptionData(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }
}