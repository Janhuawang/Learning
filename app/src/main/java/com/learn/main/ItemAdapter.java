package com.learn.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learn.R;

import java.util.List;

/**
 * 适配器
 * 作者：wjh on 2019-07-22 19:20
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<ItemBean> lists;
    private Activity activity;

    public ItemAdapter(Activity activity, List<ItemBean> lists) {
        this.activity = activity;
        this.lists = lists;
    }

    //创建View,被LayoutManager所用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_main, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //数据的绑定
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ItemBean itemBean = lists.get(position);
        if (!TextUtils.isEmpty(itemBean.contentName)) {
            holder.textView.setText(itemBean.contentName);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(itemBean.activityName)) {
                        Intent intent = new Intent();
                        intent.setClassName(activity, itemBean.activityName);
                        activity.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    //自定义ViewHolder,包含item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_content);
        }
    }
}
