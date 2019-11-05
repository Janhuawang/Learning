# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# 设置库的存放路径
set(JNI_LIBS_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs)

# 支持c++编译
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        media-command

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${JNI_LIBS_DIR}/include/ffmpeg/cmdutils.c
        ${JNI_LIBS_DIR}/include/ffmpeg/ffmpeg.c
        ${JNI_LIBS_DIR}/include/ffmpeg/ffmpeg_filter.c
        ${JNI_LIBS_DIR}/include/ffmpeg/ffmpeg_opt.c
        src/main/cpp/ffmpeg_cmd.c
        )

add_library(libavcodec
        SHARED
        IMPORTED)
set_target_properties(libavcodec
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavcodec.so)

add_library(libavdevice
        SHARED
        IMPORTED)
set_target_properties(libavdevice
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavdevice.so)

add_library(libavfilter
        SHARED
        IMPORTED)
set_target_properties(libavfilter
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavfilter.so)

add_library(libavformat
        SHARED
        IMPORTED)
set_target_properties(libavformat
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavformat.so)

#[[add_library(libavresample
        SHARED
        IMPORTED)
set_target_properties(libavresample
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavresample.so)]]

add_library(libavutil
        SHARED
        IMPORTED)
set_target_properties(libavutil
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libavutil.so)

add_library(libpostproc
        SHARED
        IMPORTED)
set_target_properties(libpostproc
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libpostproc.so)

add_library(libswresample
        SHARED
        IMPORTED)
set_target_properties(libswresample
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libswresample.so)

add_library(libswscale
        SHARED
        IMPORTED)
set_target_properties(libswscale
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/${ANDROID_ABI}/libswscale.so)

# 引入头文件路径include
include_directories(${JNI_LIBS_DIR}/include)

# 导入系统库
find_library(log-lib log)
find_library(android-lib android)

target_link_libraries( # 多库之间链接
        media-command
        libavcodec
        libavdevice
        libavfilter
        libavformat
        libavutil
        libpostproc
        libswresample
        libswscale

        ${log-lib}
        ${android-lib}
        )