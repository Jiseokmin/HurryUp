package kr.study.hurryup;

import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class OptionActivity extends AppCompatActivity {

    Button btn_test;
    Button btn_input;
    RadioGroup select_box_test;
    RadioGroup select_box_vib;
    String ip;


    EditText input_ip;

    final int port = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);


        input_ip = (EditText)findViewById(R.id.editText_ip_address);
        input_ip.setText(((OptionData) this.getApplication()).getIp_address());

        btn_input = (Button)findViewById(R.id.btn_input);
        btn_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = input_ip.getText().toString();
            }
        });

        final SeekBar sb_sound = (SeekBar)findViewById(R.id.seekBar_sound);
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrntVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        btn_test = (Button)findViewById(R.id.btn_test);
        select_box_test = (RadioGroup)findViewById(R.id.radioGroup);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("IP : ", ip);
                Log.w("Button clicked", "button clicked");
                int id = select_box_test.getCheckedRadioButtonId();
                RadioButton message = (RadioButton)findViewById(id);
                Log.w("Send to rasp", message.getText().toString());
                Vibe_test test = new Vibe_test(ip, port, message.getText().toString());
                test.execute();
            }
        });

        select_box_vib = (RadioGroup)findViewById(R.id.Group_vib);
        select_box_vib.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            String message;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.w("IP : ", ip);
                if(checkedId == R.id.rbt_off){
                    message = "0";
                }
                else if(checkedId == R.id.rbt_weak){
                    message = "0.6";
                }
                else if(checkedId == R.id.rbt_strong){
                    message = "1";
                }
                else{
                    message = "1";
                }
                Log.w("message : ", message);
                Vibe_test test = new Vibe_test(ip, port, message);
                test.execute();
            }
        });

        sb_sound.setMax(nMax);
        sb_sound.setProgress(nCurrntVol);
        sb_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public class Vibe_test extends AsyncTask<Void, Void,Void>{
        String dstAddress;
        int dstPort;
        String response = "";
        String myMessage;

        Vibe_test(String addres, int port, String message){
            dstAddress = addres;
            dstPort = port;
            myMessage = message;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Socket socket = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                InputStream inputStream = socket.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                //송신
                OutputStream out = socket.getOutputStream();
                out.write(myMessage.getBytes());

                //수신
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                socket.close();
                response = "서버의 응답: " + byteArrayOutputStream.toString("UTF-8");

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        ((OptionData) this.getApplication()).setIp_address(input_ip.getText().toString());
        //Log.d("TAG", optionData.getIp_address());
        finish();
    }
}