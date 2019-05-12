package kr.study.hurryup;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SelectPoseActivity extends AppCompatActivity {

    private String[] pose_exercise_list = {"자세 교정 운동1", "자세 교정 운동2", "자세 교정 운동3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pose);


        final ImageButton imagebtn_exercise_pose = (ImageButton) findViewById(R.id.pose_exercise);

        imagebtn_exercise_pose.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("운동 자세를 선택하세요.")
                        .setItems(pose_exercise_list, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {

                                // working with rasp camera

                                Intent intent = new Intent(SelectPoseActivity.this, PictureActivity.class);
                                intent.putExtra("num",i);
                                SelectPoseActivity.this.startActivity(intent);
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            }
        });

    }
}
