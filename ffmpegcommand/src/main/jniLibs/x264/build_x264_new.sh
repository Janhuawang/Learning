#!/bin/sh
NDK=/Users/wjh/fast-shell/NDK/android-ndk-r17c
API=21 #最低支持Android版本
HOST_PLATFORM=darwin-x86_64
cd x264
function build_one {
  OUTPUT=$(pwd)/"android"/"$CPU"
  echo "开始编译"
  echo "CPU = $CPU "
  echo "OUTPUT = $OUTPUT "
  echo "CROSS_PREFIX = $CROSS_PREFIX "
  echo "SYSROOT = $SYSROOT "
  echo "EXTRA_CFLAGS = $EXTRA_CFLAGS "
  echo "EXTRA_LDFLAGS = $EXTRA_LDFLAGS "
  ./configure \
  --prefix=$OUTPUT \
  --cross-prefix=$CROSS_PREFIX \
  --sysroot=$SYSROOT \
  --host=$HOST \
  --disable-asm \
  --disable-static \
  --enable-shared \
  --disable-opencl \
  --enable-pic \
  --disable-cli \
  --extra-cflags="$EXTRA_CFLAGS" \
  --extra-ldflags="$EXTRA_LDFLAGS"
   make clean
   make -j4
   make install
   echo "编译结束"
 }
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
               CROSS_PREFIX=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/$HOST_PLATFORM/bin/arm-linux-androideabi-
               SYSROOT=$NDK/platforms/android-$API/arch-arm/
               EXTRA_CFLAGS="-D__ANDROID_API__=$API -isysroot $NDK/sysroot -I$NDK/sysroot/usr/include/arm-linux-androideabi -Os -fPIC -marm"
               EXTRA_LDFLAGS="-marm"
               HOST=arm-linux
               build_one
          ;;
          "x86")
               CPU="x86"
               CROSS_PREFIX=$NDK/toolchains/x86-4.9/prebuilt/$HOST_PLATFORM/bin/i686-linux-android-
               SYSROOT=$NDK/platforms/android-$API/arch-x86/
               EXTRA_CFLAGS="-D__ANDROID_API__=$API -isysroot $NDK/sysroot -I$NDK/sysroot/usr/include/i686-linux-android -Os -fPIC"
               EXTRA_LDFLAGS=""
               HOST=i686-linux
               build_one
          ;;
     esac
done
