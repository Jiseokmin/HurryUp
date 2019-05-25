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

        //text.setText(readText(num));                     // 이전 화면에서 선택한 자세에 대한 설명 제공 부분

        switch(num){
            case 0:
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(picture);
                Glide.with(this).load(R.drawable.cobra_ani3).into(picture);
                text.setText("[운동순서]"+"\n\n"+

                "1. 엎드려 누운 상태에서 두 다리를 가지런히 모으고 팔꿈치를 구부려 손을 바닥에 댄다."+"\n\n"+

                "2. 숨을 들이마시면서 팔꿈치를 펴서 상체를 세운다."+"\n\n"+

                "3. 머리와 가슴을 뒤로 젖힌다. 자세 유지하며 20~30초간 복식 호흡한다."+"\n\n"+

                "4. 편안히 호흡하며 15~30초간 자세를 유지한다. 숨을 내쉬며 바닥으로 돌아온다.");

                break;
            case 2:
                picture.setImageResource(R.drawable.yoga_dari);
                break;
            default:
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

