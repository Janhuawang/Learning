package com.learn.dialog.rotating;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.learn.R;


/**
 *
 */
public class BaseDefaultContentDialog extends BaseDialogFragment {
    protected boolean isFirstCreateDialog = true; // 表示第一次初始化本DialogFragment
    private View mContent;
    private int mBeginDialogWidth;
    private int mBeginDialogHeight;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Window window = getDialog().getWindow();
                if (window != null) {
                    mContent = window.findViewById(android.R.id.content);
                    mBeginDialogWidth = mContent.getWidth();
                    mBeginDialogHeight = mContent.getHeight() + dp2px(24);

                    /*
                     * 由于showListener的调用时间比onResume还晚,所以需要在显示的时候,手动调用一次旋转.
                     */
                    setRotation(mRotation);
                }
            }
        });

    }

    @Override
    public void setRotation(int rotation) {
        final Size windowSize = WindowUtil.getWindowSize();
        if (getDialog() == null) {
            return;
        }
        final Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.background_transparent);
        if (window == null) {
            Log.e("TAG", "setRotation: window = null");
            return;
        }

        if (mContent == null) {
            return;
        }
        rotation = 3;

        int padding = dp2px(12) * 2;
        final int w, h;
        if (rotation == 1 || rotation == 3) {//横屏
            w = windowSize.getHeight() - padding;
            h = mBeginDialogHeight;
            window.setLayout(h, w);

            mContent.getLayoutParams().width = w;
            mContent.getLayoutParams().height = w;
            mContent.setLayoutParams(mContent.getLayoutParams());
        } else {
            w = mBeginDialogWidth;
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mContent.getLayoutParams().width = w - dp2px(24);
            mContent.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mContent.setLayoutParams(mContent.getLayoutParams());
        }

        int duration = isFirstCreateDialog ? 0 : 0;
        mContent.animate()
                .rotation(90 * (rotation))
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isFirstCreateDialog = false;
                    }
                });

    }

    private int dp2px(int dp) {
        return DipPixelUtil.dip2px(getActivity(), dp);
    }
}
