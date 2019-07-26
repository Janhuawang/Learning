package com.learn.dialog.rotating;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.learn.R;


/**
 * AI视频弹框提示
 * 作者：wjh on 2019-05-06 21:44
 */
public class HandlerPromptDialog extends BaseDefaultContentDialog {

    private Callback callback;
    private TextView tvTitle, tvContent, btLeft, btRight;
    private String title, content, left, right;

    public static HandlerPromptDialog newInstance() {
        HandlerPromptDialog aiVideoPromptDialog = new HandlerPromptDialog();
        return aiVideoPromptDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustomAIPrompt);
        LayoutInflater inflater = LayoutInflater.from(this.getActivity());
        View view = inflater.inflate(R.layout.dialog_rotating_handler_prompt, null);
        builder.setView(view);

        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        btLeft = view.findViewById(R.id.tv_positive);
        btRight = view.findViewById(R.id.tv_negative);
        btLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (callback != null) {
                    callback.right();
                }
            }
        });
        btRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (callback != null) {
                    callback.left();
                }
            }
        });

        setTitle(title);
        setContent(content);
        setButton(left, right);

        AlertDialog dialog = builder.setCancelable(false).create();
        return dialog;
    }

    public HandlerPromptDialog setTitle(String title) {
        this.title = title;
        if (tvTitle != null && title != null) {
            tvTitle.setText(title);
        }
        return this;
    }

    public HandlerPromptDialog setContent(String content) {
        this.content = content;
        if (tvContent != null && content != null) {
            tvContent.setText(content);
        }
        return this;
    }

    public HandlerPromptDialog setButton(String left, String right) {
        this.left = left;
        this.right = right;
        if (btLeft != null && left != null) {
            btLeft.setText(left);
        }
        if (btRight != null && right != null) {
            btRight.setText(right);
        }
        return this;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void left();

        void right();
    }
}
