package kr.study.hurryup;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static android.media.audiofx.AudioEffect.ERROR;

public class PictureActivity extends AppCompatActivity {

    TextToSpeech tts;
    Button btn_read;
    ImageView picture;
    TextView text;
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
        picture = (ImageView)findViewById(R.id.imageView);
        text = (TextView)findViewById(R.id.textView);
        text.setMovementMethod(new ScrollingMovementMethod());
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        Intent intent = getIntent();
        int num = intent.getExtras().getInt("num"); ///받아온 intent 값

        text.setText(readText(num));                     // 이전 화면에서 선택한 자세에 대한 설명 제공 부분

        switch(num){
            case 0:
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(picture);
                Glide.with(this).load(R.drawable.cobra_ani).into(picture);
                break;
            case 2:
                picture.setImageResource(R.drawable.yoga_dari);
                break;
            default:
                picture.setImageResource(R.drawable.yoga_dari);
                break;
        }                                                   //일단 테스트 용으로 2개 추가, 이후 더 추가 예정
        btn_read = (Button)findViewById(R.id.btn_read);
        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(text.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });


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

