cmake_minimum_required(VERSION 3.4.1)

# 支持c++编译
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        media-audio-edit

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/Audio_main.c
        src/main/cpp/AudioMix.c
        src/main/cpp/Wav.c
        )

# 导入系统库
find_library(log-lib log)
find_library(android-lib android)

target_link_libraries( # 多库之间链接
        media-audio-edit

        ${log-lib}
        ${android-lib}
        )