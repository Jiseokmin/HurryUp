package kr.study.hurryup;


import android.app.Application;
import android.content.Context;

public class OptionData extends Application {
    private String ip_address;
    private int vibrator_strength;
    private int sound_volume;

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

    public int getVibrator_strength() {
        return vibrator_strength;
    }

    public void setVibrator_strength(int vibrator_strength) {
        this.vibrator_strength = vibrator_strength;
    }

    public int getSound_volume() {
        return sound_volume;
    }

    public void setSound_volume(int sound_volume) {
        this.sound_volume = vibrator_strength;
    }
}