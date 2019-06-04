package kr.study.hurryup;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {
    private String IP_ADDRESS, RASP_IP;
    private boolean check_posture_check = false;
    private String[] pose_exercise_list = {"코브라 자세", "목 스트레칭", "고양이 자세"};
    private int PORT_NUMBER = 8888;
    public int recog = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IP_ADDRESS = ((OptionData) this.getApplication()).getIp_address();
        RASP_IP = ((OptionData) this.getApplication()).getRasp_ip_address();

        ShimmerTextView toolbar_title = (ShimmerTextView) findViewById(R.id.toolbar_title);
        Shimmer shimmer_toolbar_title = new Shimmer();  ///타이틀 반짝 거리는거
        shimmer_toolbar_title.setDuration(1500);
        shimmer_toolbar_title.start(toolbar_title);



        //////4개의 이미지 버튼 생성///////

        final CardView imagebtn_start = (CardView) findViewById(R.id.start);
        final CardView imagebtn_seenow = (CardView) findViewById(R.id.seeNow);
        final CardView imagebtn_select = (CardView) findViewById(R.id.select);
        final CardView imagebtn_setting = (CardView) findViewById(R.id.setting);
        final CardView imagebtn_test= (CardView) findViewById(R.id.btn_gotoTest);

        final TextView recog_st_end= (TextView) findViewById(R.id.text_recog);

        //OptionData optionData = new OptionData("");
        OptionData optionData = (OptionData) getApplication();

        optionData.setIp_address("");
        optionData.setRasp_ip_address("");
        optionData.setVibrator_strength(0);
        optionData.setSound_volume(5);
        optionData.setCorrection_Sensitivity(5);



        imagebtn_start.setOnClickListener(new View.OnClickListener() { // 자세 인식 시작
            @Override
            public void onClick(View v) {
                IP_ADDRESS = ((OptionData) MainActivity.this.getApplication()).getIp_address();

                if( recog == 0 ) {
                    recog_st_end.setText("인식 종료");
                    recog = 1;
                }

                else if( recog == 1 ) {
                    recog_st_end.setText("인식 시작");
                    recog = 0;
                }

                StartCheckPostureTask startCheckPostureTask = new StartCheckPostureTask(MainActivity.this, IP_ADDRESS, PORT_NUMBER, "toggle posture check");
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

                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext(), R.style.MyAlertDialogStyle);
                dialog.setTitle("자세를 선택하세요!")
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

        imagebtn_test.setOnClickListener(new View.OnClickListener(){                 //// 테스트 화면(기존 옵션)
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                //intent.putExtra("optionData", optionData);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        StartCheckPostureTask startCheckPostureTask = new StartCheckPostureTask(MainActivity.this, IP_ADDRESS, PORT_NUMBER, "disable posture check");
        startCheckPostureTask.execute();
        finish();
    }

    private static class StartCheckPostureTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String myMessage;

        BufferedReader b_reader;
        PrintWriter p_writer;
        private WeakReference<MainActivity> act;
        Socket socket;

        StartCheckPostureTask(MainActivity context, String ip_address, int port, String message){
            dstPort = port;
            myMessage = message;
            act = new WeakReference<>(context);
            MainActivity activity = act.get();
            dstAddress = ip_address;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                MainActivity activity = act.get(); // TestActivity 의 변수나 메소드에 접근하고 싶다면 activity. 으로 접근할 것

                socket = new Socket(dstAddress, dstPort);
                socket.setSoTimeout(10000);

                b_reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                p_writer = new PrintWriter(socket.getOutputStream());

                p_writer.println(myMessage);
                p_writer.flush();
                Log.wtf("message : ", myMessage);

                String response = b_reader.readLine();
                Log.wtf("response : ", response);

                if (myMessage.equals("toggle posture check")) {
                    activity.check_posture_check = !activity.check_posture_check;
                }
                b_reader.close();
                p_writer.close();
                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
