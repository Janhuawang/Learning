1、Ffmpeg源码编译时的总结：
尝试了最新版本4.2.1 与 旧版本3.3.2 的编译，编译过程都有各种头文件找不到的问题，核心问题是在配置这块，4.1.3版本以后需要用r18的NDK，FFmpeg默认是用GCC编译的，但升级到最新版4.2.1后改用了Clang交叉编译方式，所以版本在4.1.3以后的都要升级到r18 或者用r18以下的版本去编译4.1.3及以下的包。

2、可参考：https://codezjx.com/2019/06/21/compile-ffmpeg-android/#more

3、源码编译出来后可分为头文件跟源码库两部：.h + .so
可自定义JIN的通信的.c文件，.c的实现可调用.h头，代码完成后需要通过cmake配置将.so库链接起来，
最终打出java调用的.so。

4、.c文件的函数名需要与java中native方法对应上，不然会找不到。
名字为:包名_类名_方法名，例如：com.java.util下的a.class中的方法toB(),com_java_util_a_toB()。

5、FFmpeg版本下载地址: http://ffmpeg.org/releases/
   NDK版本下载地址: https://developer.android.com/ndk/downloads/older_releases.html

6、如果有报错的话可查看 ffmpeg-ffbuild-config.log，根据详情日志分析错误。