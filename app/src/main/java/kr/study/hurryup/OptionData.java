package kr.study.hurryup;


import android.app.Application;
import android.content.Context;

public class OptionData extends Application {
    private String ip_address;
    private String rasp_ip;
    private float vibrator_strength;
    private int sound_volume;
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

    public String getRasp_ip_address() {
        return rasp_ip;
    }

    public void setRasp_ip_address(String rasp_ip) {
        this.rasp_ip = rasp_ip;
    }

    public float getVibrator_strength() {
        return vibrator_strength;
    }

    public void setVibrator_strength(float vibrator_strength) {
        this.vibrator_strength = vibrator_strength;
    }

    public int getSound_volume() {
        return sound_volume;
    }

    public void setSound_volume(int sound_volume) {
        this.sound_volume = sound_volume;
    }

    public int getCorrection_Sensitivity() {
        return correction_sensitivity;
    }

    public void setCorrection_Sensitivity(int correction_sensitivity) {
        this.correction_sensitivity = correction_sensitivity;
    }
}