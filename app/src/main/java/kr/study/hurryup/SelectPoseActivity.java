package kr.study.hurryup;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SelectPoseActivity extends AppCompatActivity {

    private String[] pose_basic_list = {"공부 자세", "컴퓨터 자세", "..."};
    private String[] pose_exercise_list = {"거북목 방지 운동", "운동 자세2", "운동 자세3"};
    private String[] select_pose = {"자세1", "자세2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pose);

        final ImageButton imagebtn_basic_pose = (ImageButton) findViewById(R.id.pose_basic);
        final ImageButton imagebtn_exercise_pose = (ImageButton) findViewById(R.id.pose_exercise);
        final ImageButton imagebtn_teach_pose = (ImageButton) findViewById(R.id.pose_teach);

        imagebtn_basic_pose.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("인식할 기본자세를 선택하세요.")
                        .setItems(pose_basic_list, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                // i 번 선택시 내용
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            }
        });

        imagebtn_exercise_pose.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("인식할 운동자세를 선택하세요.")
                        .setItems(pose_exercise_list, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                // i 번 선택시 내용
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.show();
            }
        });


        imagebtn_teach_pose.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("배우고 싶은 자세를 선택하세요.")
                        .setItems(select_pose, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
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