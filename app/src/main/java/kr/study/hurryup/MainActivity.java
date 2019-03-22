package kr.study.hurryup;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

        final ImageButton imagebtn_chicken = (ImageButton) findViewById(R.id.start);
        final ImageButton imagebtn_soju = (ImageButton) findViewById(R.id.seeNow);
        final ImageButton imagebtn_mic = (ImageButton) findViewById(R.id.select);
        final ImageButton imagebtn_basketball = (ImageButton) findViewById(R.id.setting);
    }
}
