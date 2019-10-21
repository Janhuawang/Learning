package com.learn.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件帮助类，与业务有关
 */
public class FileUtil {
    /**
     * 获取系统外部存储根目录
     *
     * @return
     */
    private static String getExternalRootPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath(); // 外部目录
        } else {
            return context.getFilesDir().getAbsolutePath(); // 内部目录
        }
    }

    /**
     * 长时间保存的文件目录
     *
     * @param context
     * @param names
     * @return
     */
    public static File getFileDir(Context context, String... names) {
        StringBuilder sb = new StringBuilder();

        String rootPath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalFilesDir = context.getExternalFilesDir("");
            if (externalFilesDir != null) {
                rootPath = externalFilesDir.getAbsolutePath();
            }
        }
        if (rootPath == null) {
            rootPath = context.getFilesDir().getAbsolutePath();
        }

        sb.append(rootPath);

        if (names != null) {
            for (String name : names) {
                if (TextUtils.isEmpty(name)) {
                    continue;
                }

                sb.append(File.separator).append(name);
            }
        }

        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    /**
     * 长时间保存的文件目录地址
     *
     * @param context
     * @param names
     * @return
     */
    public static String getFileDirPath(Context context, String... names) {
        return getFileDir(context, names).getAbsolutePath();
    }

    /**
     * 短时间保存的缓存目录
     *
     * @param context
     * @param names
     * @return
     */
    public static File getCacheDir(Context context, String... names) {
        StringBuilder sb = new StringBuilder();

        String rootPath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalFilesDir = context.getExternalCacheDir();
            if (externalFilesDir != null) {
                rootPath = externalFilesDir.getAbsolutePath();
            }
        }
        if (rootPath == null) {
            rootPath = context.getCacheDir().getAbsolutePath();
        }

        sb.append(rootPath);

        if (names != null) {
            for (String name : names) {

                if (TextUtils.isEmpty(name)) {
                    continue;
                }

                sb.append(File.separator).append(name);
            }
        }

        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    /**
     * 短时间保存的缓存目录地址
     *
     * @param context
     * @param names
     * @return
     */
    public static String getCacheDirPath(Context context, String... names) {
        return getCacheDir(context, names).getAbsolutePath();
    }

    /**
     * 获取系统相册目录，视频存储使用，参考：https://zhuanlan.zhihu.com/p/46533159
     *
     * @param context
     * @return
     */
    public static String getSystemDCIMPath(Context context) {
        return getDCIMPath(context, "Camera");
    }

    /**
     * 获取系统相册目录
     *
     * @param context
     * @param childFile
     * @return
     */
    public static String getDCIMPath(Context context, String childFile) {
        File dataDir;
        if (TextUtils.isEmpty(childFile)) {
            dataDir = new File(getExternalRootPath(context), Environment.DIRECTORY_DCIM);
        } else {
            dataDir = new File(new File(getExternalRootPath(context), Environment.DIRECTORY_DCIM), childFile);
        }
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        return dataDir.getAbsolutePath();
    }

    /**
     * 获取系统文件下载目录
     *
     * @param context
     * @return
     */
    public static String getDownloadPath(Context context) {
        File dataDir = new File(getExternalRootPath(context), Environment.DIRECTORY_DOWNLOADS);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        return dataDir.getAbsolutePath();
    }

    /**
     * 写入crash log文件
     *
     * @param context
     * @param throwable
     */
    public static void writeCrashLog(Context context, Throwable throwable) {
        try {
            String fileName = getFileDirPath(context, "crash").concat(File.separator).concat(TimeUtil.getNowTimeStr("yyyyMMddHHmm").concat(".txt"));
            FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);
            PrintStream printStream = new PrintStream(fileOutputStream);
            throwable.printStackTrace(printStream);
            printStream.flush();
            printStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总的缓存文件大小
     *
     * @param context
     * @return
     */
    public static String getTotalCacheSize(Context context) {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
            cacheSize += getFolderSize(context.getExternalFilesDir(""));
        }
        return getFormatSize(cacheSize);
    }

    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 清除缓存文件夹中的指定文件
     *
     * @param context
     */
    public static void clearCache(Context context) {
        deleteFile(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFile(context.getExternalCacheDir());
            deleteFile(context.getExternalFilesDir(""));
        }
    }

    private static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteFile(f);
            }
        }
        file.delete();
    }

    /**
     * 把图片文件复制到指定目录
     *
     * @param context
     * @param resId   drawable的图片资源ID
     */
    public static String resDrawable2File(Context context, int resId) {
        try {
            String filePath = getCacheDirPath(context, Environment.DIRECTORY_PICTURES)
                    .concat(File.separator).concat("res_").concat(String.valueOf(resId)).concat(".jpg");
            File file = new File(filePath);
            if (!file.exists()) {
                // 获得封装文件的InputStream对象
                InputStream is = context.getResources().openRawResource(resId);
                FileOutputStream fos = new FileOutputStream(filePath);

                byte[] buffer = new byte[4 * 1024];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件或文件夹
     *
     * @param path
     */
    public static void delete(String path) {
        delete(path, null);
    }

    /**
     * 删除文件或文件夹
     *
     * @param path
     */
    public static void delete(final String path, final FileDelCallback fileDelCallback) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(path);
                if (!file.exists()) {
                    return;
                }

                List<String> filePathList = new ArrayList<>();
                deleteFile(file, filePathList);

                if (fileDelCallback != null) {
                    fileDelCallback.done(filePathList);
                }
            }

            private void deleteFile(File file, List<String> filePathList) {
                if (file == null) {
                    return;
                }
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        deleteFile(f, filePathList);
                    }
                }
                file.delete();
                filePathList.add(file.getAbsolutePath());
            }
        }).start();
    }

    /**
     * 获取文件夹大小
     *
     * @return
     */
    public static String getKB(String filePath) {
        File file = new File(filePath);
        long fileLength = getFileDirectoryLength(0, file);
        return getFileSize(fileLength);
    }

    @NonNull
    public static String getFileSize(long fileLength) {
        DecimalFormat df = new DecimalFormat("#.#");
        String fileSizeString;
        if (fileLength < 1024) {
            fileSizeString = fileLength + "B";
        } else if (fileLength < 1048576) {
            fileSizeString = df.format((double) fileLength / 1024) + "KB";
        } else if (fileLength < 1073741824) {
            fileSizeString = df.format((double) fileLength / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileLength / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 获取一个文件夹的大小
     *
     * @param length
     * @param directory
     * @return 单位b
     */
    private static long getFileDirectoryLength(long length, File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isDirectory()) {
                    length = length + getFileDirectoryLength(length, item);
                } else {
                    length = length + item.length();
                }
            }
        }
        return length;
    }

    /**
     * 获取手机剩余可用空间
     *
     * @return
     */
    public static long getAvailSize() {
        File path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory();
        } else {
            path = Environment.getDataDirectory();
        }
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 将多个文件合成一个文件
     *
     * @param fileNames
     * @param targetFileName
     */
    public static void merge(ArrayList<String> fileNames, String targetFileName) {
        try {
            File fOut = new File(targetFileName);
            if (fOut.exists()) {
                fOut.delete();
            }
            FileOutputStream out = new FileOutputStream(fOut);
            for (String fileName : fileNames) {
                File fIn = new File(fileName);
                if (fIn.exists()) {
                    FileInputStream in = new FileInputStream(fIn);

                    int len;
                    byte[] buf = new byte[4 * 1024];
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean move(File srcFile, File desFile) {
        return srcFile != null && srcFile.exists() && desFile != null && srcFile.renameTo(desFile);
    }

    public static boolean copy(File srcFile, File desFile) {
        if (srcFile != null && srcFile.exists() && desFile != null) {
            try {
                // 保证目标文件存在
                if (!desFile.exists()) {
                    desFile.getParentFile().mkdirs();
                    desFile.createNewFile();
                }

                if (!desFile.exists()) {
                    return false;
                }

                InputStream is = new FileInputStream(srcFile); //读入原文件
                FileOutputStream fs = new FileOutputStream(desFile);
                int len;
                byte[] buffer = new byte[4 * 1024];
                while ((len = is.read(buffer)) != -1) {
                    fs.write(buffer, 0, len);
                }
                fs.close();
                is.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
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

    /**
     * 从文件中读取string
     *
     * @param path
     * @return
     */
    public static String readString(String path) {
        String str = "";
        File file = new File(path);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            // size为字串的长度，这里一次性读完
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            str = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    /**
     * 复制res/raw中的文件到指定目录
     *
     * @param context  上下文
     * @param id       资源ID
     * @param filePath
     */
    public static void copyFilesFromRaw(Context context, int id, String filePath) {
        InputStream inputStream = context.getResources().openRawResource(id);
        readInputStream(filePath, inputStream);
    }

    /**
     * 读取输入流中的数据写入输出流
     *
     * @param filePath    目标文件路径
     * @param inputStream 输入流
     */
    public static void readInputStream(String filePath, InputStream inputStream) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs();
                }
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[4 * 1024];
                // 3.开始读文件
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, len);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return 文件是否存在
     */
    public static boolean checkFileExist(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.e("FileUtil", path + "is null!");
        }
        File file = new File(path);
        if (!file.exists()) {
            Log.e("FileUtil", path + " is not exist!");
        }
        return true;
    }

    public interface FileDelCallback {
        void done(List<String> filePathList);
    }
}
