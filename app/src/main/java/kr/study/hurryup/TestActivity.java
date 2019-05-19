package kr.study.hurryup;

import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestActivity extends AppCompatActivity {
    private EditText editText_ip_address ;
    private SeekBar seekBar_sense;
    private SeekBar seekBar_sound;
    private static RadioGroup radio_vibe_strength;

    private static TextView txt_ip;
    private static TextView txt_sense;
    private static TextView txt_sound;
    private static TextView txt_vibe;



    SocketTask socketTask;
    static boolean connection = false;
    static Socket socket;

    final int PORT = 8888;
    public double number_sound = 0;        //음량 값 받기 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final Button btn_ok = findViewById(R.id.btn_ok);
        final Button btn_connect = findViewById(R.id.btn_connect);

        editText_ip_address = findViewById(R.id.input_ip);
        seekBar_sense = findViewById(R.id.seekBar_sense);
        seekBar_sound = findViewById(R.id.seekBar_sound);
        radio_vibe_strength = findViewById(R.id.Group_vib);

        txt_ip = (TextView)findViewById(R.id.txt_ip);
        txt_sense = (TextView)findViewById(R.id.sensitivity_text);
        txt_sound = (TextView)findViewById(R.id.sound_text);
        txt_vibe = (TextView)findViewById(R.id.vibration_text);


        editText_ip_address.setText(getIpAddress());
        seekBar_sense.setProgress(getVibratorStrength());
        seekBar_sound.setProgress(getSoundVolume());

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 2/3;   ///파이썬에서 음량이 최대 10이라 10으로 조정
        int nCurrentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


        seekBar_sense.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                if(connection == false) {
                    return true;
                }

                else if(connection == true) {
                    return false;
                }

                return false;
            }
        });


        seekBar_sound.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                if(connection == false) {
                    seekBar_sound.setProgress(0);
                    return true;
                }

                else if(connection == true) {
                    return false;
                }

                return false;
            }
        });


        if (!connection) {     //connection이 false 일 때
            txt_ip.setTextColor(Color.parseColor("#7f8c8d"));  // 색 깔 들 회색으로 교체
            txt_sense.setTextColor(Color.parseColor("#7f8c8d"));
            txt_sound.setTextColor(Color.parseColor("#7f8c8d"));
            txt_vibe.setTextColor(Color.parseColor("#7f8c8d"));
            for (int i = 0; i < radio_vibe_strength.getChildCount(); i++) {     //라디오 그룹 false
                radio_vibe_strength.getChildAt(i).setEnabled(false);
            };

            seekBar_sound.setProgress(0);  // 사운드 0 으로 설정
        }


        else if (connection == true) {     //connection이 true 일 때
            txt_ip.setTextColor(Color.parseColor("#00acee"));
            txt_sense.setTextColor(Color.parseColor("#00acee"));
            txt_sound.setTextColor(Color.parseColor("#00acee"));
            txt_vibe.setTextColor(Color.parseColor("#00acee"));
            for (int i = 0; i < radio_vibe_strength.getChildCount(); i++) {
                radio_vibe_strength.getChildAt(i).setEnabled(true);
            };

        }


        btn_connect.setOnClickListener(new View.OnClickListener() {
            String message;

            @Override
            public void onClick(View v) {
                if (!connection) {
                    message = "connect to server";
                    socketTask = new SocketTask(editText_ip_address.getText().toString(), PORT, message);
                    socketTask.execute();
                }
            }
        });


        radio_vibe_strength.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            String message;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.wtf("TAG", "CLICKED");
                if (connection) {
                    if (checkedId == R.id.rbt_off) {
                        message = "vibration strength 0";
                    } else if (checkedId == R.id.rbt_weak) {
                        message = "vibration strength 0.6";
                    } else if (checkedId == R.id.rbt_strong) {
                        message = "vibration strength 1";
                    } else
                        return;
                    socketTask = new SocketTask(editText_ip_address.getText().toString(), PORT, message);
                    socketTask.execute();
                }
            }
        });




///////////////////////////////////////////////소리 설정 //////////////////////////////
        seekBar_sound.setMax(nMax);
        seekBar_sound.setProgress(nCurrentVol);

        seekBar_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            String message;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);
                number_sound = seekBar_sound.getProgress() * 0.1;
                message = "sound volume "+ number_sound;

                socketTask = new SocketTask(editText_ip_address.getText().toString(), PORT, message);
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
        ///////////////////////////////////////////////소리 설정 //////////////////////////////




        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection) {
                    socketTask = new SocketTask(editText_ip_address.getText().toString(), PORT, "exit");
                    socketTask.execute();
                }
                saveOptions();
                finish();
            }
        });
    }

    public String getIpAddress() {
        return ((OptionData) this.getApplication()).getIp_address();
    }

    public int getVibratorStrength() {
        return ((OptionData) this.getApplication()).getVibrator_strength();
    }

    public int getSoundVolume() {
        return ((OptionData) this.getApplication()).getSound_volume();
    }

    private void saveOptions() {
        ((OptionData) this.getApplication()).setIp_address(editText_ip_address.getText().toString());
        ((OptionData) this.getApplication()).setVibrator_strength(seekBar_sense.getProgress());
        ((OptionData) this.getApplication()).setSound_volume(seekBar_sense.getProgress());
    }

    private static class SocketTask extends AsyncTask<Void, Void,Void> {
        String dstAddress;
        int dstPort;
        String myMessage;

        BufferedReader b_reader;
        PrintWriter p_writer;

        SocketTask(String address, int port, String message){
            dstAddress = address;
            dstPort = port;
            myMessage = message;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Log.wtf("State : ", "TRY");
                if (!connection) {
                    socket = new Socket(dstAddress, dstPort);
                    Log.wtf("State : ", "New Socket");
                    // socket.setSoTimeout(5000);
                }

                b_reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                p_writer = new PrintWriter(socket.getOutputStream());

                p_writer.println(myMessage);
                p_writer.flush();
                Log.wtf("message : ", myMessage);

                String response = b_reader.readLine();
                Log.wtf("response : ", response);

                if (myMessage.equals("exit")) {
                    b_reader.close();
                    p_writer.close();
                    socket.close();
                    connection = false;
                    Log.wtf("State : ", "Disconnected");
                }
                else if (!connection) {
                    connection = true;  ////연결 성공했을 때
                    Log.wtf("State : ", "Connected");

                    txt_ip.setTextColor(Color.parseColor("#00acee"));
                    txt_sense.setTextColor(Color.parseColor("#00acee"));
                    txt_sound.setTextColor(Color.parseColor("#00acee"));
                    txt_vibe.setTextColor(Color.parseColor("#00acee"));

                    for (int i = 0; i < radio_vibe_strength.getChildCount(); i++) {
                        radio_vibe_strength.getChildAt(i).setEnabled(true);
                    };
                }
                Log.wtf("State : ", "End");

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}