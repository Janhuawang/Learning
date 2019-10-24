package com.learn.activity.ffmpeg.util;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 作者：wjh on 2019-10-24 22:10
 */
public class FilePathUtil {
    public static final String PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "AvideoTest";
    public static final String OutDir = PATH + File.separator + "out";

    /**
     * 初始化路径
     */
    public static void initPath(Activity activity) {
        if (activity == null) {
            return;
        }

        File outFile = new File(OutDir);
        if (!outFile.exists()) {
            outFile.mkdirs();
        }

        File af = new File(PATH + File.separator + "a.mp4");
        if (!af.isFile()) {
            try {
                //InputStream is = this.getResources().openRawResource(R.raw.bjbj);
                InputStream is = activity.getResources().getAssets().open("a.mp4");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                FileOutputStream fos = new FileOutputStream(af);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File bf = new File(PATH + File.separator + "b.mp4");
        if (!bf.isFile()) {
            try {
                //InputStream is = this.getResources().openRawResource(R.raw.bjbj);
                InputStream is = activity.getResources().getAssets().open("a.mp4");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                FileOutputStream fos = new FileOutputStream(bf);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(activity, "文件初始完成！", Toast.LENGTH_SHORT).show();
    }
}
