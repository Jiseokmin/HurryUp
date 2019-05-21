package kr.study.hurryup;


import android.app.Application;
import android.content.Context;

public class OptionData extends Application {
    private String ip_address;
    private float vibrator_strength;
    private double sound_volume;
    private int correction_sensitivity;

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

    public float getVibrator_strength() {
        return vibrator_strength;
    }

    public void setVibrator_strength(float vibrator_strength) {
        this.vibrator_strength = vibrator_strength;
    }

    public double getSound_volume() {
        return sound_volume;
    }

    public void setSound_volume(double sound_volume) {
        this.sound_volume = sound_volume;
    }

    public int getCorrection_Sensitivity() {
        return correction_sensitivity;
    }

    public void setCorrection_Sensitivity(int correction_sensitivity) {
        this.correction_sensitivity = correction_sensitivity;
    }
}