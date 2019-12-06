#!/bin/sh
NDK=/Users/wjh/fast-shell/NDK/android-ndk-r17c
API=21 #最低支持Android版本
HOST_PLATFORM=darwin-x86_64
cd ffmpeg-4.1.3
function build_one {
     OUTPUT=$(pwd)/"android"/"$CPU"
     echo "开始编译"
     echo "CPU = $CPU "
     echo "OUTPUT = $OUTPUT "
     echo "CROSS_PREFIX = $CROSS_PREFIX "
     echo "ARCH = $ARCH "
     echo "SYSROOT = $SYSROOT "
     echo "EXTRA_CFLAGS = $EXTRA_CFLAGS "
     echo "EXTRA_LDFLAGS = $EXTRA_LDFLAGS "
     ./configure \
     --prefix=$OUTPUT \
     --enable-gpl \
     --enable-debug=3 \
     --enable-libx264 \
     --enable-shared \
     --disable-asm \
     --disable-static \
     --disable-doc \
     --disable-ffmpeg \
     --disable-ffplay \
     --disable-ffprobe \
     --disable-avdevice \
     --disable-doc \
     --disable-symver \
     --cross-prefix=$CROSS_PREFIX \
     --target-os=android \
     --arch=$ARCH \
     --enable-cross-compile \
     --sysroot=$SYSROOT \
     --extra-cflags="$EXTRA_CFLAGS" \
     --extra-ldflags="$EXTRA_LDFLAGS" \
     $ADDITIONAL_CONFIGURE_FLAG
     make clean
     make -j4
     make install
     echo "编译结束"
 }

# CPUS="arm64-v8a armeabi armeabi-v7a mips mips64 x86 x86_64"
# CPUS="arm64-v8a armeabi-v7a x86 x86_64" #目前高级的ndk 要求支持这几种即可
CPUS="armeabi-v7a"
if [ "$*" ]
then
     CPUS="$*"   #如果有输入参数 则只编译 该架构
fi
echo "编译以下 架构 $CPUS"

for CPU_TEMP in $CPUS
do
     case $CPU_TEMP in
          "armeabi-v7a")
               CPU="armeabi-v7a"
               ARCH=armeabi-v7a
               X264_INCLUDE=/Users/wjh/fast-shell/new/x264/android/armeabi-v7a/include
               X264_LIB=/Users/wjh/fast-shell/new/x264/android/armeabi-v7a/lib
               
               CROSS_PREFIX=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/$HOST_PLATFORM/bin/arm-linux-androideabi-
               SYSROOT=$NDK/platforms/android-$API/arch-arm/
               EXTRA_CFLAGS="-I$X264_INCLUDE -isysroot $NDK/sysroot -I$NDK/sysroot/usr/include/arm-linux-androideabi -Os -fpic -marm"
               EXTRA_LDFLAGS="-L$X264_LIB -marm"
               build_one
          ;;
          "x86")
               CPU="x86"
               ARCH=x86
               X264_INCLUDE=/Users/johnwu/Documents/c_project/x264/x264/android/x86/include
               X264_LIB=/Users/johnwu/Documents/c_project/x264/x264/android/x86/lib
               CROSS_PREFIX=$NDK/toolchains/x86-4.9/prebuilt/$HOST_PLATFORM/bin/i686-linux-android-
               SYSROOT=$NDK/platforms/android-$API/arch-x86/
               EXTRA_CFLAGS="-I$X264_INCLUDE -isysroot $NDK/sysroot -I$NDK/sysroot/usr/include/i686-linux-android -Os -fpic"
               EXTRA_LDFLAGS="-L$X264_LIB"
               build_one
          ;;
     esac
done
