#!/bin/bash
NDK=/Users/wjh/fast-shell/NDK/android-ndk-r17c
SYSROOT=$NDK/platforms/android-21/arch-arm
ISYSROOT=$NDK/sysroot
ASM=$ISYSROOT/usr/include/arm-linux-androideabi
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64
PREFIX=$(pwd)/android/armv7-a
CROSS_PREFIX=$TOOLCHAIN/bin/arm-linux-androideabi-
build_android()
{
    ./configure \
    --prefix=$PREFIX \
    --enable-shared \
    --disable-static \
    --disable-doc \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-avdevice \
    --disable-symver \
    --cross-prefix=$CROSS_PREFIX \
    --target-os=android \
    --arch=arm \
    --enable-cross-compile \
    --sysroot=$SYSROOT \
    --extra-cflags="-I$ASM -isysroot $ISYSROOT -D__ANDROID_API__=21 -Os -fpic -marm -march=armv7-a"
    make clean
    make
    make install
}
build_android
