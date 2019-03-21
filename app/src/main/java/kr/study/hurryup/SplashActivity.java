package kr.study.hurryup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(1900);


        }catch (InterruptedException E) {
            E.printStackTrace();
        }

        startActivity(new Intent(this,MainActivity.class));
        finish();

    }
}
