#include <jni.h>
#include <android/log.h>
#include "ffmpeg/ffmpeg.h"

JNIEXPORT jint

JNICALL Java_com_frank_command_FFmpegCmd_handle
        (JNIEnv *env, jclass obj, jobjectArray commands) {
    int argc = (*env)->GetArrayLength(env, commands);
    char **argv = (char **) malloc(argc * sizeof(char *));

    __android_log_print(ANDROID_LOG_ERROR, "Kit", "argc %d\n", argc);

    int i;
    int result;
    for (i = 0; i < argc; i++) {
        jstring jstr = (jstring)(*env)->GetObjectArrayElement(env, commands, i);
        char *temp = (char *) (*env)->GetStringUTFChars(env, jstr, 0);
        argv[i] = malloc(1024);
        strcpy(argv[i], temp);
        __android_log_print(ANDROID_LOG_ERROR, "Kit", "argv %s\n", argv[i]);

        (*env)->ReleaseStringUTFChars(env, jstr, temp);
    }
    //执行ffmpeg命令
    result = run(argc, argv);
    //释放内存
    for (i = 0; i < argc; i++) {
        free(argv[i]);
    }
    free(argv);
    return result;
}