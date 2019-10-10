package com.learn.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.learn.R;

/**
 * 作者：wjh on 2019-08-06 17:46
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        initView();
        initListener();
        initData();
    }

    protected int getLayoutId() {
        return R.layout.activity_list_item;
    }

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    public void onClick(View v){}
}
