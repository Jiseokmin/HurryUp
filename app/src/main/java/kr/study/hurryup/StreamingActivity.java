package kr.study.hurryup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;


public class StreamingActivity extends AppCompatActivity {
    String RASP_IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        XWalkView xWalkWebView;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

        RASP_IP = ((OptionData) this.getApplication()).getRasp_ip_address();

        xWalkWebView = findViewById(R.id.xwalkWebView);
        xWalkWebView.getSettings().setLoadWithOverviewMode(true);
        xWalkWebView.getSettings().setUseWideViewPort(true);
        xWalkWebView.loadUrl("http://"+RASP_IP+":8080/stream/video.mjpeg");

        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
    }
}
