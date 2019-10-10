package com.learn.activity.glidedemo.GlidePhotoView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.learn.R;

import com.learn.widget.photoview.PhotoView;
import com.learn.widget.photoview.PhotoViewAttacher;

import java.util.ArrayList;

/**
 * 图片预览阅读模式
 */
public class BigPhotoActivity extends AppCompatActivity {
    private ArrayList<String> urls = new ArrayList<>();
    private HackyViewPager viewPager;
    private MyViewPagerAdapter adapter;
    private int currentPosition;
    private CircleIndicator circleIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_image_big_photo);
        circleIndicator = (CircleIndicator) findViewById(R.id.circleIndicator);
        this.urls = getIntent().getStringArrayListExtra(Common.BigPhotoActivity.URLS);
        if (urls.size() == 1) {
            circleIndicator.setVisibility(View.GONE);
        } else {
            circleIndicator.setVisibility(View.VISIBLE);
        }
        this.currentPosition = getIntent().getIntExtra(Common.BigPhotoActivity.CURRENT_POSITION, 0);

        init();

    }

    private void init() {
        viewPager = (HackyViewPager) findViewById(R.id.viewpager);
        viewPager.setBackgroundColor(Color.TRANSPARENT);
        adapter = new MyViewPagerAdapter(urls);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);
        circleIndicator.setViewPager(viewPager);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.photo_activity_out);
    }

    class MyViewPagerAdapter extends PagerAdapter {

        private PhotoView[] photoViews;
        private ArrayList<String> urls;

        public MyViewPagerAdapter(ArrayList<String> urls) {
            this.urls = urls;
            photoViews = new PhotoView[urls.size()];
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(photoViews[position]);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
           /* GlidePhotoView photoView = new GlidePhotoView(BigPhotoActivity.this, urls.get(position));
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    finish();
                }

                @Override
                public void onOutsidePhotoTap() {
                    finish();
                }
            });*/

            final PhotoView photoView = new PhotoView(container.getContext());
            photoViews[position] = photoView;

            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    photoView.setReadModel(!photoView.isReadModel());
                }

                @Override
                public void onOutsidePhotoTap() {
                    photoView.setReadModel(!photoView.isReadModel());
                }
            });

            RequestOptions requestOptions = new RequestOptions().dontAnimate();
            requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            if (com.bumptech.glide.util.Util.isValidDimensions(2048, 2048)) {
                requestOptions = requestOptions.override(2048, 2048);
            }
            requestOptions = requestOptions.fitCenter();

            Glide.with(BigPhotoActivity.this)
                    .load(urls.get(position)).apply(requestOptions)
                    .into(photoView);
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }


    }
}
