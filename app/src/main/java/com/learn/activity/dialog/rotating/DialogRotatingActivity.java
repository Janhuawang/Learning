package com.learn.activity.dialog.rotating;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.learn.R;

/**
 * 旋转Dialog
 * 作者：wjh on 2019-07-22 19:31
 * <p>
 * https://github.com/ZhengShang/RotateDialogDemo
 */
public class DialogRotatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_list_item);

        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandlerPromptDialog aiVideoPromptDialog = new HandlerPromptDialog();
                aiVideoPromptDialog.setTitle("提示");
                aiVideoPromptDialog.setContent("课程管理者已设定作业的最低AI评分分数（%1$s分），请在线拍摄视频作业，课程管理者已设定作业的最低AI评分分数（%1$s分）");
                aiVideoPromptDialog.setButton("Yes", "No");
                aiVideoPromptDialog.show(getFragmentManager(), 3);
                aiVideoPromptDialog.setCallback(new HandlerPromptDialog.Callback() {
                    @Override
                    public void left() {
                        finish();
                    }

                    @Override
                    public void right() {
                    }
                });
            }
        });
    }


}