#!/bin/bash
NDK=/Users/wjh/fast-shell/NDK/android-ndk-r14b
ISYSROOT=$NDK/sysroot




MARCH="armv7-a"
ARCH="arm"
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64
SYSROOT=$NDK/platforms/android-21/arch-arm
CROSS_PREFIX=$TOOLCHAIN/bin/arm-linux-androideabi-
PREFIX=$(pwd)/android-build/libs/armv7-all
ASM=$ISYSROOT/usr/include/arm-linux-androideabi

#MARCH="armv8-a"
#ARCH="aarch64"
#TOOLCHAIN=$NDK/toolchains/aarch64-linux-android-4.9/prebuilt/darwin-x86_64
#SYSROOT=$NDK/platforms/android-21/arch-arm64
#CROSS_PREFIX=$TOOLCHAIN/bin/aarch64-linux-android-
#PREFIX=$(pwd)/android-build/libs/armv8-all
#ASM=$ISYSROOT/usr/include/aarch64-linux-android

build_android()
{
    ./configure \
    --prefix=$PREFIX \
    --enable-shared \
    --enable-small \
    --enable-postproc \
    --enable-gpl \
    --disable-static \
    --disable-doc \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-ffserver \
    --disable-symver \
    --cross-prefix=$CROSS_PREFIX \
    --target-os=android \
    --arch=$ARCH \
    --enable-cross-compile \
    --sysroot=$SYSROOT \
    --extra-cflags="-Os -fpic -march=$MARCH" \
    --extra-ldflags="$ADDI_LDFLAGS" \
    $ADDITIONAL_CONFIGURE_FLAG
    
     echo "======== > confligure_flag: $ADDITIONAL_CONFIGURE_FLAG"
     echo "======== > PREFIX: $PREFIX"
     echo "======== > CROSS_PREFIX: $CROSS_PREFIX"
    make clean
    make
    make install
}
build_android
