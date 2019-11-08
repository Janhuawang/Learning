package com.learn.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * 路径
 * 作者：wjh on 2018/11/23 21:44
 * <p>
 * Android 7.0强制启用了被称作 StrictMode的策略，带来的影响就是你的App对外无法暴露file://类型的URI了。
 * 如果你使用Intent携带这样的URI去打开外部App(比如：打开系统相机拍照)，那么会抛出FileUriExposedException异常。
 * <p>
 * 官方给出解决这个问题的方案，就是使用FileProvider 使用步骤： 见：https://www.jianshu.com/p/61c09a321f2d
 * 1.manifest中申明FileProvider
 * 2.res/xml中定义对外暴露的文件夹路径
 * 3.生成content://类型的Uri
 * 4.给Uri授予临时权限
 * 5.使用Intent传递Uri
 */
public class UriUtil {

    public static Uri fromFile(Context context, File file) {
        if (context == null || file == null) {
            return null;
        }
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 对应的是Manifest定义的 authorities
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        context.grantUriPermission(context.getPackageName(), fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.grantUriPermission(context.getPackageName(), fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return fileUri;
    }


    /**
     * 判断url是否是本地目录
     *
     * @param url
     * @return
     */
    public static boolean isLocalUrl(String url) {
        if (url != null) {
            if (url.startsWith("/")) {
                return true;
            } else return !url.startsWith("http");
        }
        return true;
    }

}
