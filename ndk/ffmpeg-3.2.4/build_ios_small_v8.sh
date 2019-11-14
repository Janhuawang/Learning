#!/bin/bash
NDK=/Users/wjh/fast-shell/NDK/android-ndk-r14b
SYSROOT=$NDK/platforms/android-21/arch-arm
ISYSROOT=$NDK/sysroot
ASM=$ISYSROOT/usr/include/arm-linux-androideabi
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64
PREFIX=$(pwd)/ios/armv8-a
CROSS_PREFIX=$TOOLCHAIN/bin/arm-linux-androideabi-

ARCH="arm64"
PLATFORM="iPhoneOS"
XCRUN_SDK=`echo $PLATFORM | tr '[:upper:]' '[:lower:]'`
CC="xcrun -sdk $XCRUN_SDK clang"
AS="gas-preprocessor.pl -arch aarch64 -- $CC"
CONFIGURE_FLAGS="--enable-cross-compile --disable-debug --disable-programs \
--disable-doc --enable-pic"
CFLAGS="-arch $ARCH"
DEPLOYMENT_TARGET="8.0"
CFLAGS="$CFLAGS -mios-version-min=$DEPLOYMENT_TARGET -fembed-bitcode"
LDFLAGS="$CFLAGS"
build_android()
{
    ./configure \
     --target-os=darwin \
     --arch=$ARCH \
     --cc="$CC" \
     --as="$AS" \
     --enable-static \
     --disable-shared \
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
     $CONFIGURE_FLAGS \
     --extra-cflags="$CFLAGS" \
     --extra-ldflags="$LDFLAGS" \
     --prefix=$PREFIX

    make clean
    make
    make install
}
build_android
