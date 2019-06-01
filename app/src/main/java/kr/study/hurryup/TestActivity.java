package kr.study.hurryup;

import android.graphics.Color;
import android.media.AudioManager;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestActivity extends AppCompatActivity {
    private EditText editText_ip_address;
    private SeekBar seekBar_sense;
    private SeekBar seekBar_sound;
    private RadioGroup radio_vibe_strength;

    private RadioButton rbt_off;
    private RadioButton rbt_weak;
    private RadioButton rbt_strong;

    private TextView txt_ip;
    private TextView txt_sense;
    private TextView txt_sound;
    private TextView txt_vibe;

    private Button btn_connect;

    private SocketTask socketTask;
    private boolean connection = false;
    private Socket socket;

    final int PORT = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        editText_ip_address = findViewById(R.id.input_ip);
        seekBar_sense = findViewById(R.id.seekBar_sense);
        seekBar_sound = findViewById(R.id.seekBar_sound);
        radio_vibe_strength = findViewById(R.id.Group_vib);

        txt_ip = findViewById(R.id.txt_ip);
        txt_sense = findViewById(R.id.sensitivity_text);
        txt_sound = findViewById(R.id.sound_text);
        txt_vibe = findViewById(R.id.vibration_text);

        rbt_off = findViewById(R.id.rbt_off);
        rbt_weak = findViewById(R.id.rbt_weak);
        rbt_strong = findViewById(R.id.rbt_strong);

        btn_connect = findViewById(R.id.btn_connect);

        final Button btn_ok = findViewById(R.id.btn_ok);

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        final int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 2/3; // 파이썬에서 음량이 최대 10이라 10으로 조정
        seekBar_sound.setMax(nMax);
        seekBar_sense.setMax(10); // 민감도 최대 10

        editText_ip_address.setText(getIpAddress()); // 저장된 IP 불러오기
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

        txt_ip.setTextColor(Color.parseColor("#7f8c8d"));  // 색 깔 들 회색으로 교체
        txt_sense.setTextColor(Color.parseColor("#7f8c8d"));
        txt_sound.setTextColor(Color.parseColor("#7f8c8d"));
        txt_vibe.setTextColor(Color.parseColor("#7f8c8d"));

        for (int i = 0; i < radio_vibe_strength.getChildCount(); i++) {     //라디오 그룹 false
            radio_vibe_strength.getChildAt(i).setEnabled(false);
        }
        seekBar_sound.setEnabled(false);
        seekBar_sense.setEnabled(false);


        btn_connect.setOnClickListener(new View.OnClickListener() { // Connect 버튼 클릭
            String message;
            int result = 0;

            @Override
            public void onClick(View v) {
                if (!connection) {
                    message = "connect to server";
                    socketTask = new SocketTask(TestActivity.this, PORT, message);
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
        });



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
                    socketTask = new SocketTask(TestActivity.this, PORT, message);
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

                socketTask = new SocketTask(TestActivity.this, PORT, message);
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

                socketTask = new SocketTask(TestActivity.this, PORT, message);
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
                    socketTask = new SocketTask(TestActivity.this, PORT, "exit");
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
    }

    public void SetEnableUI() {
        for (int i = 0; i < radio_vibe_strength.getChildCount(); i++) {
            radio_vibe_strength.getChildAt(i).setEnabled(true);
        }
        seekBar_sound.setEnabled(true);
        seekBar_sense.setEnabled(true);

        txt_ip.setTextColor(Color.parseColor("#00acee"));
        txt_sense.setTextColor(Color.parseColor("#00acee"));
        txt_sound.setTextColor(Color.parseColor("#00acee"));
        txt_vibe.setTextColor(Color.parseColor("#00acee"));
        btn_connect.setEnabled(false);
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
        ((OptionData) this.getApplication()).setIp_address(editText_ip_address.getText().toString());
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
        private WeakReference<TestActivity> act;

        SocketTask(TestActivity context, int port, String message){
            dstPort = port;
            myMessage = message;
            act = new WeakReference<>(context);
            TestActivity activity = act.get();
            dstAddress = activity.editText_ip_address.getText().toString();
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            try {
                TestActivity activity = act.get(); // TestActivity 의 변수나 메소드에 접근하고 싶다면 activity. 으로 접근할 것

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

                ///////////////////////////////////////
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