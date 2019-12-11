#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <malloc.h>
#include "ffmpegc/ffmpeg.h"


static JavaVM *jvm = NULL;// java虚拟机
static jclass m_clazz = NULL; //当前类(面向java)
static JNIEnv *m_env = NULL;
static jint m_businessType;
static jmethodID methodID;

JNIEXPORT jint JNICALL
Java_com_frank_command_FFMPegMain_handle(JNIEnv *env, jclass obj, jobjectArray commands,
                                        jint businessType) {

    (*env)->GetJavaVM(env, &jvm);
    m_clazz = (*env)->NewGlobalRef(env, obj);
    m_env = env;
    m_businessType = businessType;

    // 获取方法ID (I)V指的是方法签名 通过javap -s -public FFmpegCmd 命令生成
    methodID = (*m_env)->GetStaticMethodID(m_env, m_clazz, "onCallback",
                                           "(Ljava/lang/String;I)V");

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

/**
 * 异常检查
 * @param env
 * @return
 */
int checkExc(JNIEnv *env) {
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env); // writes to logcat
        (*env)->ExceptionClear(env);
        return 1;
    }
    return -1;
}

/**
 * 抛出异常到java层
 * @param env
 * @param name
 * @param msg
 */
void JNU_ThrowByName(JNIEnv *env, const char *name, const char *msg) {
    // 查找异常类
    jclass cls = (*env)->FindClass(env, name);
    /* 如果这个异常类没有找到，VM会抛出一个NowClassDefFoundError异常 */
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, msg);  // 抛出指定名字的异常
    }
    /* 释放局部引用 */
    (*env)->DeleteLocalRef(env, cls);
}

void callJavaMethod(char *line, int level) {
    if (level <= 24) { // 24以下的都是错误日志
        __android_log_write(ANDROID_LOG_ERROR, "error:", line);
    } else {
//        __android_log_write(ANDROID_LOG_DEBUG, "debug:", line);

        if (m_clazz != NULL) {
            if (methodID != NULL) {

                char timeStr[16] = "dts";
                char *q = strstr(line, timeStr); // 截取带dts的串
                if (q != NULL) {
                    jstring result = (*m_env)->NewStringUTF(m_env, q);
                    if (result != NULL) {
                        (*m_env)->CallStaticVoidMethod(m_env, m_clazz, methodID, result,
                                                       m_businessType);  // 调用该方法
                        (*m_env)->DeleteLocalRef(m_env, result);
                    }
                }
            }
        }
    }
}

void clear() {
//    (*m_env)->DeleteGlobalRef(m_env, m_clazz);
//    (*m_env)->DeleteGlobalRef(m_env, m_businessType);
//    (*jvm)->DetachCurrentThread(*jvm); // 解除与虚拟机的连接
//    (*jvm)->DestroyJavaVM(*jvm); // 卸载虚拟机
}

