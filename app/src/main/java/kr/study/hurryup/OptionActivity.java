package kr.study.hurryup;

import android.media.AudioManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import info.hoang8f.widget.FButton;

public class OptionActivity extends AppCompatActivity {
    private EditText editText_ip_address;

    final int PORT = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        final RadioGroup radio_vibe_strength = findViewById(R.id.Group_vib);
        final SeekBar sb_sound = findViewById(R.id.seekBar_sound);
        final FButton btn_ok = (FButton)findViewById(R.id.btn_ok);
        final FButton btn_cancel = (FButton)findViewById(R.id.btn_cancel);

        btn_ok.setButtonColor(getResources().getColor(R.color.fbutton_color_twitter));
        btn_cancel.setButtonColor(getResources().getColor(R.color.fbutton_color_twitter));

        //editText_ip_address = findViewById(R.id.input_ip);
        //editText_ip_address.setText(getIpAddress());

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        radio_vibe_strength.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            String message;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbt_weak){
                    message = "vibration strength 0.6";
                }
                else if(checkedId == R.id.rbt_strong){
                    message = "vibration strength 1";
                }
                else{
                    message = "toggle vibrator";
                }
                Log.w("message : ", message);
                VibrationStrengthTask vibrationStrengthTask = new VibrationStrengthTask(editText_ip_address.getText().toString(), PORT, message);
                vibrationStrengthTask.execute();
            }
        });

        sb_sound.setMax(nMax);
        sb_sound.setProgress(nCurrentVol);
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

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOptions();
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public String getIpAddress() {
        return ((OptionData) this.getApplication()).getIp_address();
    }

    private void saveOptions() {
        ((OptionData) this.getApplication()).setIp_address(editText_ip_address.getText().toString());
    }

    private static class VibrationStrengthTask extends AsyncTask<Void, Void,Void>{
        String dstAddress;
        int dstPort;
        String response = "";
        String myMessage;

        VibrationStrengthTask(String address, int port, String message){
            dstAddress = address;
            dstPort = port;
            myMessage = message;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Socket socket;
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
}