#!/bin/bash
NDK=/Users/wjh/fast-shell/NDK/android-ndk-r14b
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64
PLATFORM=$NDK/platforms/android-16/arch-arm
SYSROOT=$NDK/platforms/android-21/arch-arm64
CROSS_PREFIX=$TOOLCHAIN/bin/aarch64-linux-android-
HOST_PLATFORM=arm-linux
PREFIX=$(pwd)/android-build/libs
echo $NDK
echo $TOOLCHAIN
echo $PLATFORM
echo $PREFIX
function build_one
{
    ./configure \
         --prefix=$PREFIX \
         --disable-shared \
         --enable-static \
         --enable-pic \
         --disable-asm \
         --disable-cli \
         --host=$HOST_PLATFORM \
         --cross-prefix=$CROSS_PREFIX \
         --sysroot=$SYSROOT \
 
}
build_one
