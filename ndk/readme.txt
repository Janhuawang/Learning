1、Ffmpeg源码编译时的总结：
尝试了最新版本4.2.1 与 旧版本3.3.2 的编译，编译过程都有各种头文件找不到的问题，核心问题是在配置这块，4.1.3版本以后需要用r18的NDK，FFmpeg默认是用GCC编译的，但升级到最新版4.2.1后改用了Clang交叉编译方式，所以版本在4.1.3以后的都要升级到r18 或者用r18以下的版本去编译4.1.3及以下的包。

2、可参考：https://codezjx.com/2019/06/21/compile-ffmpeg-android/#more

3、源码编译出来后可分为头文件跟源码库两部：.h + .so
可自定义JIN的通信的.c文件，.c的实现可调用.h头，代码完成后需要通过cmake配置将.so库链接起来，
最终打出java调用的.so。

4、.c文件的函数名需要与java中native方法对应上，不然会找不到。
名字为:包名_类名_方法名，例如：com.java.util下的a.class中的方法toB(),com_java_util_a_toB()。

5、FFMpeg版本下载地址: http://ffmpeg.org/releases/
   NDK版本下载地址: https://developer.android.com/ndk/downloads/older_releases.html

6、如果有报错的话可查看 ffmpeg-ffbuild-config.log，根据详情日志分析错误。


7、同样的脚本在低版本上编译不过：
参考地址：https://blog.csdn.net/jjwwmlp456/article/details/79614943
打开文件 ./configure
修改：
SLIBNAME_WITH_MAJOR='$(SLIBNAME).$(LIBMAJOR)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB) "$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_VERSION)'
SLIB_INSTALL_LINKS='$(SLIBNAME_WITH_MAJOR) $(SLIBNAME)'

改成：
SLIBNAME_WITH_MAJOR='$(SLIBPREF)$(FULLNAME)-$(LIBMAJOR)$(SLIBSUF)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_MAJOR)'
SLIB_INSTALL_LINKS='$(SLIBNAME)'


8、FFMPEG从功能上划分为几个模块，分别为核心工具（libutils）、媒体格式（libavformat）、编解码（libavcodec）、设备（libavdevice）和后处理（libavfilter, libswscale, libpostproc），分别负责提供公用的功能函数、实现多媒体文件的读包和写包、完成音视频的编解码、管理音视频设备的操作以及进行音视频后处理。
1、libavutil 是一个包含简化编程功能的库，其中包括随机数生成器，数据结构，数学代码，核心多媒体工具等更多东西。
2、libavcodec 是一个包含音频/视频解码器和编码器的库。
3、libavformat 是一个包含了多媒体格式的分离器和混流器的库。
4、libavdevice 是一个包含输入输出设备的库，用于捕捉和渲染很多来自常用的多媒体输入/输出软件框架的数据，包括Video4Linux，Video4Linux2，VfW和ALSA。
5、libavfilter 是一个包含媒体过滤器的库。AVFilter可以给视音频添加各种滤镜效果。可以给视频添加水印，给YUV数据加特效。
6、libswscale 是一个用于执行高度优化的图像缩放和颜色空间/像素格式转换操作的库。
7、libswresample 是一个用于执行高度优化的音频重采样，重新矩阵和取样格式转换操作的库。