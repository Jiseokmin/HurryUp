package kr.study.hurryup;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import info.hoang8f.widget.FButton;

public class OptionActivity extends AppCompatActivity {

    private SeekBar seekBar_sense;
    private SeekBar seekBar_sound;
    private RadioGroup radio_vibe_strength;

    private RadioButton rbt_off;
    private RadioButton rbt_weak;
    private RadioButton rbt_strong;

    private TextView txt_sense;
    private TextView txt_sound;
    private TextView txt_vibe;

    private SocketTask socketTask;
    private boolean connection = false;
    private Socket socket;

    private static String str_ip;
    private static String tmp_str_ip;
    String message;

    final int PORT = 8888;
    int result = 0;
    static int once_connect =0;  //한 번 연결된 적 이 있으면 ip 찾는 거 안하고 저장해 놓은 ip 사용하여 바로 연결

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        checkAvailableConnection(); //현재 ip 주소 출력


        //Toast.makeText(this, str_ip, Toast.LENGTH_LONG).show();
        String[] array_ip = str_ip.split("\\.");  /// ip 를 . 단위로 자르기


        seekBar_sense = findViewById(R.id.seekBar_sense);
        seekBar_sound = findViewById(R.id.seekBar_sound);
        radio_vibe_strength = findViewById(R.id.Group_vib);

        txt_sense = findViewById(R.id.sensitivity_text);
        txt_sound = findViewById(R.id.sound_text);
        txt_vibe = findViewById(R.id.vibration_text);

        rbt_off = findViewById(R.id.rbt_off);
        rbt_weak = findViewById(R.id.rbt_weak);
        rbt_strong = findViewById(R.id.rbt_strong);

        final FButton btn_ok = (FButton)findViewById(R.id.btn_ok);
        final FButton btn_cancel = (FButton)findViewById(R.id.btn_cancel);

        btn_ok.setButtonColor(getResources().getColor(R.color.fbutton_color_orange));
        btn_cancel.setButtonColor(getResources().getColor(R.color.fbutton_color_orange));

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        final int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 2/3; // 파이썬에서 음량이 최대 10이라 10으로 조정
        seekBar_sound.setMax(nMax);
        seekBar_sense.setMax(10); // 민감도 최대 10

        seekBar_sense.setProgress(getCorrectionSensitivity()); // 저장된 민감도 불러오기
        seekBar_sound.setProgress(getSoundVolume()); // 저장된 사운드 볼륨 불러오기

        float vibration_strength = getVibratorStrength();

        if (vibration_strength == 1) {
            radio_vibe_strength.check(rbt_off.getId());
        }
        else if (vibration_strength == 0.6) {
            radio_vibe_strength.check(rbt_weak.getId());
        }
        else {
            radio_vibe_strength.check(rbt_strong.getId());
        }

        txt_sense.setTextColor(Color.parseColor("#7f8c8d"));// 색 깔 들 회색으로 교체
        txt_sound.setTextColor(Color.parseColor("#7f8c8d"));
        txt_vibe.setTextColor(Color.parseColor("#7f8c8d"));

        for (int i = 0; i < radio_vibe_strength.getChildCount(); i++) {     //라디오 그룹 false
            radio_vibe_strength.getChildAt(i).setEnabled(false);
        }
        seekBar_sound.setEnabled(false);
        seekBar_sense.setEnabled(false);

///////////////////////////ip 찾기 //////////////////////////////////////////////////////////

        if(once_connect ==0) {
            for (int i = 29; i < 256; i++) {        /// 0 부터 255 까지 ip 할당해서 맞는 주소 찾기
                //Toast.makeText(this, "connected ip: " + i, Toast.LENGTH_LONG).show();
                array_ip[3] = Integer.toString(i);
                str_ip = array_ip[0] + "." + array_ip[1] + "." + array_ip[2] + "." + array_ip[3];
                tmp_str_ip = str_ip;

                if (!connection) {
                    message = "connect to server";
                    socketTask = new SocketTask(OptionActivity.this, PORT, message);
                    try {
                        result = socketTask.execute().get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == 1) {
                        SetEnableUI();
                        tmp_str_ip = str_ip;    //다음에 또 ip를 찾지 않도록 ip 저장
                        once_connect = 1;
                        break;
                    }
                }
                else
                    continue;

            }
        }

        if(once_connect ==1) {

            if (!connection) {
                message = "connect to server";
                socketTask = new SocketTask(OptionActivity.this, PORT, message);
                try {
                    result = socketTask.execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result == 1) {
                    SetEnableUI();

                }
            }

        }
///////////////////////////////////////////////////////////////////////////////////////////////

        radio_vibe_strength.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // 진동 세기 설정
            String message;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (connection) {
                    if (checkedId == R.id.rbt_off) {
                        message = "vibration strength 0";
                    } else if (checkedId == R.id.rbt_weak) {
                        message = "vibration strength 0.6";
                    } else if (checkedId == R.id.rbt_strong) {
                        message = "vibration strength 1";
                    } else
                        return;
                    socketTask = new SocketTask(OptionActivity.this, PORT, message);
                    socketTask.execute();
                }
            }
        });


        seekBar_sense.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // 민감도 설정
            String message;
            private int sensitivity;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivity = seekBar_sense.getProgress();
                message = "correction sensitivity "+ sensitivity;

                socketTask = new SocketTask(OptionActivity.this, PORT, message);
                socketTask.execute();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekBar_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // 사운드 볼륨 설정
            String message;
            private int sound_volume;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);
                sound_volume = seekBar_sound.getProgress();
                message = "sound volume "+ sound_volume;

                socketTask = new SocketTask(OptionActivity.this, PORT, message);
                socketTask.execute();
                // Toast.makeText(getApplicationContext(), "출력할 문자열"+number_sound, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        btn_ok.setOnClickListener(new View.OnClickListener() { // OK 버튼 클릭시 서버 소켓 연결 끊고 옵션 저장 후 액티비티 종료
            int result = 0;
            @Override
            public void onClick(View v) {
                if (connection) {
                    socketTask = new SocketTask(OptionActivity.this, PORT, "exit");
                    try {
                        result = socketTask.execute().get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == -1) {
                        saveOptions();
                        finish();
                    }
                }
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//////////////////////////////// IP 주소 구하는 거 ////////////////////////////////////////////////////////////////////

    void checkAvailableConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable()) {

            WifiManager myWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
            int ipAddress = myWifiInfo.getIpAddress();
            System.out.println("WiFi address is "
                    + android.text.format.Formatter.formatIpAddress(ipAddress));

            str_ip = android.text.format.Formatter.formatIpAddress(ipAddress);



        } else if (mobile.isAvailable()) {

            GetLocalIpAddress();
            Toast.makeText(this, "3G Available", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No Network Available", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private String GetLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            return "ERROR Obtaining IP";
        }
        return "No IP Available";
    }

    //////////////////////////////// IP 주소 구하는 거 ////////////////////////////////////////////////////////////////////

    public void SetEnableUI() {
        for (int i = 0; i < radio_vibe_strength.getChildCount(); i++) {
            radio_vibe_strength.getChildAt(i).setEnabled(true);
        }
        seekBar_sound.setEnabled(true);
        seekBar_sense.setEnabled(true);

        txt_sense.setTextColor(Color.parseColor("#000000"));
        txt_sound.setTextColor(Color.parseColor("#000000"));
        txt_vibe.setTextColor(Color.parseColor("#000000"));
    }

    public String getIpAddress() {
        return ((OptionData) this.getApplication()).getIp_address();
    }

    public float getVibratorStrength() {
        return ((OptionData) this.getApplication()).getVibrator_strength();
    }

    public int getSoundVolume() {
        return ((OptionData) this.getApplication()).getSound_volume();
    }

    public int getCorrectionSensitivity() {
        return ((OptionData) this.getApplication()).getCorrection_Sensitivity();
    }

    private void saveOptions() {
        ((OptionData) this.getApplication()).setIp_address(str_ip);
        ((OptionData) this.getApplication()).setSound_volume(seekBar_sound.getProgress());
        ((OptionData) this.getApplication()).setCorrection_Sensitivity(seekBar_sense.getProgress());

        int id = radio_vibe_strength.getCheckedRadioButtonId();

        if (id == rbt_off.getId())
            ((OptionData) this.getApplication()).setVibrator_strength(0);
        else if (id == rbt_strong.getId())
            ((OptionData) this.getApplication()).setVibrator_strength(0.6f);
        else
            ((OptionData) this.getApplication()).setVibrator_strength(1.0f);
    }

    private static class SocketTask extends AsyncTask<Void, Void, Integer> {
        String dstAddress;
        int dstPort;
        String myMessage;

        BufferedReader b_reader;
        PrintWriter p_writer;
        private WeakReference<OptionActivity> act;

        SocketTask(OptionActivity context, int port, String message){
            dstPort = port;
            myMessage = message;
            act = new WeakReference<>(context);
            OptionActivity activity = act.get();
            dstAddress = tmp_str_ip;
            Log.wtf("ip_now : ", tmp_str_ip);
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            try {
                OptionActivity activity = act.get(); // OptionActivity 의 변수나 메소드에 접근하고 싶다면 activity. 으로 접근할 것

                if (!activity.connection) {
                    activity.socket = new Socket(dstAddress, dstPort);
                    // socket.setSoTimeout(5000);
                }

                b_reader = new BufferedReader(
                        new InputStreamReader(activity.socket.getInputStream()));
                p_writer = new PrintWriter(activity.socket.getOutputStream());

                p_writer.println(myMessage);
                p_writer.flush();
                Log.wtf("message : ", myMessage);

                String response = b_reader.readLine();
                Log.wtf("response : ", response);

                if (myMessage.equals("exit")) {
                    b_reader.close();
                    p_writer.close();
                    activity.socket.close();
                    activity.connection = false;
                    Log.wtf("State : ", "Disconnected");
                    return -1;
                }
                else if (!activity.connection) {
                    activity.connection = true;  ////연결 성공했을 때

                    return 1;
                }
                Log.wtf("State : ", "End");

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}