package kr.study.hurryup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PictureActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);         ///// 맨 상단의 액션바 안보이게 하기
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // 이전 화면에서 선택한 자세에 대한 그림 제공 부분
        ImageView picture = (ImageView)findViewById(R.id.imageView);
        TextView text = (TextView)findViewById(R.id.textView);
        text.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        int num = intent.getExtras().getInt("num"); ///받아온 intent 값

        text.setText(readText(num));                     // 이전 화면에서 선택한 자세에 대한 설명 제공 부분
        switch(num){
            case 0:
                picture.setImageResource(R.drawable.yoga_cobra);
                break;
            case 1:
                picture.setImageResource(R.drawable.yoga_dari);
                break;
        }                                                   //일단 테스트 용으로 2개 추가, 이후 더 추가 예정


    }

    String readText(int intent){                            // res/raw 폴더에 저장된 텍스트 파일을 읽어들이는 함수

        String data = null;
        InputStream inputStream;
        switch(intent){
            case 0:
                inputStream = getResources().openRawResource(R.raw.yoga_cobra);
                break;
            case 1:
                inputStream = getResources().openRawResource(R.raw.yoga_dari);
                break;
            default:
                inputStream = getResources().openRawResource(R.raw.defaulttext);
                break;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            data = new String(byteArrayOutputStream.toByteArray(),"MS949");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

}

