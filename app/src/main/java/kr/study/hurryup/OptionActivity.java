package kr.study.hurryup;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class OptionActivity extends AppCompatActivity{

    Button btn_test;
    RadioGroup select_box;
    final String ip = "192.168.24.40";
    final int port = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        final SeekBar sb_vibe = (SeekBar) findViewById(R.id.seekBar_vibe);
        btn_test = (Button)findViewById(R.id.btn_test);
        select_box = (RadioGroup)findViewById(R.id.radioGroup);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Button clicked", "button clicked");
                int id = select_box.getCheckedRadioButtonId();
                RadioButton message = (RadioButton)findViewById(id);
                Log.w("Send to rasp", message.getText().toString());
                Vibe_test test = new Vibe_test(ip, port, message.getText().toString());
                test.execute();
            }
        });

        ///진동 seekbar change listener
        sb_vibe.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                String str_progress = String.valueOf(progress);

                //  Log.w("Send to rasp", String.valueOf((progress+3)/10));
                Vibe_test test = new Vibe_test(ip, port, str_progress);
                test.execute();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ////드래그 방지
        sb_vibe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    return false;
                }
                return true;

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




}



