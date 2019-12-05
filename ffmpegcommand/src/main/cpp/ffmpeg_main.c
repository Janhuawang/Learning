#include <jni.h>
#include <android/log.h>
#include "ffmpegc/ffmpeg.h"


static JavaVM *jvm = NULL;//java虚拟机
static jclass m_clazz = NULL; //当前类(面向java)
static JNIEnv *m_env = NULL;

JNIEXPORT jint JNICALL
Java_com_frank_command_FFmpegCmd_handle(JNIEnv *env, jclass obj, jobjectArray commands) {

    (*env)->GetJavaVM(env, &jvm);
    m_clazz = (*env)->NewGlobalRef(env, obj);
    m_env = env;

    int argc = (*env)->GetArrayLength(env, commands);
    char **argv = (char **) malloc(argc * sizeof(char *));

    __android_log_print(ANDROID_LOG_ERROR, "Kit", "argc %d\n", argc);

    int i;
    int result;
    for (i = 0; i < argc; i++) {
        jstring jstr = (jstring) (*env)->GetObjectArrayElement(env, commands, i);
        char *temp = (char *) (*env)->GetStringUTFChars(env, jstr, 0);
        argv[i] = malloc(1024);
        strcpy(argv[i], temp);
        __android_log_print(ANDROID_LOG_ERROR, "Kit", "argv %s\n", argv[i]);

        (*env)->ReleaseStringUTFChars(env, jstr, temp);
    }
    //执行ffmpeg命令
    result = ffmpeg_main(argc, argv);
    //释放内存
    for (i = 0; i < argc; i++) {
        free(argv[i]);
    }
    free(argv);
    return result;
}

void callJavaMethod(char *ret) {
    if (m_clazz != NULL) {
        // 获取方法ID (I)V指的是方法签名 通过javap -s -public FFmpegCmd 命令生成
        jmethodID methodID = (*m_env)->GetStaticMethodID(m_env, m_clazz, "onProgress", "(I)V");
        if (methodID != NULL) {
            // 调用该方法
            (*m_env)->CallStaticVoidMethod(m_env, m_clazz, methodID, 111);

            /*int result = 0;
    char timeStr[10] = "time=";
    char *q = strstr(ret, timeStr);
    if (q != NULL) {
        //日志信息中若包含"time="字符串
        char str[14] = {0};
        strncpy(str, q, 13);
        int h = (str[5] - '0') * 10 + (str[6] - '0');
        int m = (str[8] - '0') * 10 + (str[9] - '0');
        int s = (str[11] - '0') * 10 + (str[12] - '0');
        result = s + m * 60 + h * 60 * 60;

        //获取java方法
        jmethodID methodID = (*m_env)->GetStaticMethodID(m_env, m_clazz, "onProgress", "(I)V");
        //调用该方法
        (*m_env)->CallStaticVoidMethod(m_env, m_clazz, methodID, result);
    } else {
        return;
    }*/
            //已执行时长 result
        }
    }

}

