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
    --disable-static \
    --disable-everything \
    --disable-doc \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-ffserver \
    --disable-symver \
    --disable-avresample \
    --enable-decoder=mpeg4 \
    --enable-decoder=h264 \
    --enable-decoder=aac \
    --enable-encoder=mpeg4 \
    --enable-encoder=h264 \
    --enable-encoder=aac \
    --enable-parser=aac \
    --enable-parser=h264 \
    --enable-parser=mpeg4 \
    --enable-demuxer=mp4 \
    --enable-demuxer=mov \
    --enable-demuxer=m4a \
    --enable-muxer=mp4 \
    --enable-muxer=mov \
    --enable-muxer=m4a \
    --enable-protocol=file \
    --enable-filters \
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
