package kr.study.hurryup;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);         ///// 맨 상단의 액션바 안보이게 하기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //////4개의 이미지 버튼 생성///////

        final ImageButton imagebtn_start = (ImageButton) findViewById(R.id.start);
        final ImageButton imagebtn_seenow = (ImageButton) findViewById(R.id.seeNow);
        final ImageButton imagebtn_select = (ImageButton) findViewById(R.id.select);
        final ImageButton imagebtn_setting = (ImageButton) findViewById(R.id.setting);



        imagebtn_select.setOnClickListener(new View.OnClickListener() {        //자세 선택 화면으로 이동

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(getApplicationContext(), SelectPoseActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        imagebtn_seenow.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StreamingActivity.class);
                startActivity(intent);
            }
        });

        imagebtn_setting.setOnClickListener(new View.OnClickListener() {        //// 설정 화면으로 이동

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(getApplicationContext(), OptionActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
