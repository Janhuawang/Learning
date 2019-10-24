#include <jni.h>
#include "ffmpeg/ffmpeg.h"
#include <android/log.h>

JNIEXPORT jint JNICALL Java_com_frank_ffmpeglib_FFmpegKit_handle
        (JNIEnv *env, jclass obj, jobjectArray commands) {
    int argc = (*env)->GetArrayLength(env, commands);
    char *argv[argc];

    __android_log_print(ANDROID_LOG_ERROR, "Kit", "argc %d\n", argc);
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) (*env)->GetObjectArrayElement(env, commands, i);
        argv[i] = (char *) (*env)->GetStringUTFChars(env, js, 0);
        __android_log_print(ANDROID_LOG_ERROR, "Kit", "argv %s\n", argv[i]);
    }
    return run(argc, argv);
}
