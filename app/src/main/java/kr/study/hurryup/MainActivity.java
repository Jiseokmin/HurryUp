package kr.study.hurryup;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {
    String IP_ADDRESS;
    private String[] pose_exercise_list = {"코브라", "스트레칭2", "스트레칭3"};
    int PORT_NUMBER = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IP_ADDRESS = ((OptionData) this.getApplication()).getIp_address();

        ShimmerTextView toolbar_title = (ShimmerTextView) findViewById(R.id.toolbar_title);
        Shimmer shimmer_toolbar_title = new Shimmer();  ///타이틀 반짝 거리는거
        shimmer_toolbar_title.setDuration(1500);
        shimmer_toolbar_title.start(toolbar_title);



        //////4개의 이미지 버튼 생성///////

        final ImageButton imagebtn_start = (ImageButton) findViewById(R.id.start);
        final ImageButton imagebtn_seenow = (ImageButton) findViewById(R.id.seeNow);
        final ImageButton imagebtn_select = (ImageButton) findViewById(R.id.select);
        final ImageButton imagebtn_setting = (ImageButton) findViewById(R.id.setting);
        final FButton btn_test = (FButton)findViewById(R.id.btn_gotoTest);

        btn_test.setButtonColor(getResources().getColor(R.color.fbutton_color_twitter));




        //OptionData optionData = new OptionData("");
        OptionData optionData = (OptionData) getApplication();

        optionData.setIp_address("");
        optionData.setVibrator_strength(0);
        optionData.setSound_volume(0.5);
        optionData.setCorrection_Sensitivity(5);



        imagebtn_start.setOnClickListener(new View.OnClickListener() { // 자세 인식 시작
            @Override
            public void onClick(View v) {
                StartCheckPostureTask startCheckPostureTask = new StartCheckPostureTask(IP_ADDRESS, PORT_NUMBER, "toggle posture check");
                startCheckPostureTask.execute();
            }
        });

        imagebtn_seenow.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StreamingActivity.class);
                startActivity(intent);
            }
        });

        imagebtn_select.setOnClickListener(new View.OnClickListener() {        //자세 선택 화면으로 이동
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("스트레칭 자세를 선택하세요.")
                        .setItems(pose_exercise_list, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {

                                // working with rasp camera

                                Intent intent = new Intent(MainActivity.this, PictureActivity.class);
                                intent.putExtra("num",i);
                                MainActivity.this.startActivity(intent);
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            }
        });

        imagebtn_setting.setOnClickListener(new View.OnClickListener() {        //// 설정 화면으로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionActivity.class);
                //intent.putExtra("optionData", optionData);
                MainActivity.this.startActivity(intent);
            }
        });

        btn_test.setOnClickListener(new View.OnClickListener(){                 //// 테스트 화면(기존 옵션)
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                //intent.putExtra("optionData", optionData);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private static class StartCheckPostureTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        String myMessage;

        //constructor
        StartCheckPostureTask(String address, int port, String message) {
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
                response = byteArrayOutputStream.toString("UTF-8");

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }
            Log.w("message : ", myMessage);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
