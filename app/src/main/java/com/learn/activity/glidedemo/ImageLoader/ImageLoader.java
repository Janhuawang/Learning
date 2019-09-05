package com.learn.activity.glidedemo.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.learn.R;

import java.io.File;

/**
 * Created by 24706 on 2016/4/19.
 * 单例模式 继承了AImageLoader抽象类
 */
public class ImageLoader extends AImageLoader {

    //设置加载错误时的图片
    private static final int ERROR_IMAGE = R.drawable.load_failure;

    public static ImageLoader imageLoader;

    private ImageLoader() {

    }

    public static ImageLoader getInstance() {
        if (imageLoader == null) {
            synchronized (ImageLoader.class) {
                if (imageLoader == null) {
                    imageLoader = new ImageLoader();
                }
            }
        }
        return imageLoader;
    }

    /**
     * 加载普通网络图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    @Override
    public void loadImage(Context context, String url, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions = requestOptions.centerCrop();

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, String url, ImageView imageView, int width, int height) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions = requestOptions.centerCrop();
        requestOptions = requestOptions.override(width, height);

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * 如果需要设置请求优先级使用这个，不设置默认是Priority.NORMAL
     *
     * @param context
     * @param url
     * @param imageView
     * @param priority
     */
    @Override
    public void loadImage(Context context, String url, ImageView imageView, Priority priority) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions = requestOptions.centerCrop();
        requestOptions = requestOptions.priority(priority);

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * 加载网络图片,圆
     *
     * @param context
     * @param url
     * @param imageView
     */
    @Override
    public void loadCircleImage(Context context, String url, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions = requestOptions.centerCrop();
        requestOptions = requestOptions.transform(new GlideCircleTransform());

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * 加载网络图片,添加圆角
     *
     * @param context
     * @param url
     * @param imageView
     */
    @Override
    public void loadRoundImage(Context context, String url, ImageView imageView, int dp) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions.transform(new GlideRoundTransform(context, dp));
        requestOptions = requestOptions.centerCrop();

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * 监控加载过程
     *
     * @param context
     * @param url
     * @param loadingImageListener
     */
    @Override
    public void loadImage(Context context, String url, LoadingImageListener loadingImageListener) {
        MySimpleTarget mySimpleTarget = new MySimpleTarget(loadingImageListener);
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions = requestOptions.centerCrop();//centerCrop设置填充满imageview，可能有部分被裁剪掉，还有一种方式是fitCenter，将图片完整显示
        Glide.with(context).asBitmap().
                load(url)
                .apply(requestOptions)
                .into(mySimpleTarget);
    }


    /**
     * 从资源文件中加载图片
     *
     * @param context
     * @param sourceId
     * @param imageView
     */
    @Override
    public void loadImage(Context context, int sourceId, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions = requestOptions.centerCrop();

        Glide.with(context)
                .load(sourceId)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * 从文件中加载图片
     *
     * @param context
     * @param file
     * @param imageView
     */
    @Override
    public void loadImage(Context context, File file, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions = requestOptions.centerCrop();

        Glide.with(context)
                .load(file)
                .apply(requestOptions)
                .into(imageView);

    }

    /**
     * 从Uri中加载图片
     *
     * @param context
     * @param uri
     * @param imageView
     */
    @Override
    public void loadImage(Context context, Uri uri, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions = requestOptions.centerCrop();

        Glide.with(context)
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }


    /**
     * 从网络中加载Gif
     *
     * @param context
     * @param url
     * @param imageView
     */
    @Override
    public void loadGif(Context context, String url, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions = requestOptions.centerCrop();

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(imageView);

    }


    /**
     * 从资源文件中加载Gif
     *
     * @param context
     * @param sourceId
     * @param imageView
     */
    @Override
    public void loadGif(Context context, int sourceId, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions = requestOptions.centerCrop();

        Glide.with(context)
                .load(sourceId)
                .apply(requestOptions)
                .into(imageView);

    }

    /**
     * 从文件中加载Gif
     *
     * @param context
     * @param file
     * @param imageView
     */
    @Override
    public void loadGif(Context context, File file, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions = requestOptions.centerCrop();

        Glide.with(context)
                .load(file)
                .apply(requestOptions)
                .into(imageView);

    }

    /**
     * 从Uri中加载Gif
     *
     * @param context
     * @param uri
     * @param imageView
     */
    @Override
    public void loadGif(Context context, Uri uri, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().dontAnimate();
        requestOptions = requestOptions.placeholder(new ColorfulDrawable());
        requestOptions.error(ERROR_IMAGE);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions = requestOptions.centerCrop();

        Glide.with(context)
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }

    public class MySimpleTarget extends SimpleTarget<Bitmap> {

        private LoadingImageListener imageListener;

        public MySimpleTarget(LoadingImageListener imageListener) {
            this.imageListener = imageListener;
        }

        public MySimpleTarget(int width, int height, LoadingImageListener imageListener) {
            super(width, height);
            this.imageListener = imageListener;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            super.onLoadStarted(placeholder);
            imageListener.onLoadStart();
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            if (null != imageListener) {
                imageListener.onLoadSuccess(resource);
            }
        }
    }
}
