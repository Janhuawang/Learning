#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <malloc.h>
#include "ffmpegc/ffmpeg.h"


static JavaVM *jvm = NULL;// java虚拟机
static jclass m_clazz = NULL; //当前类(面向java)
static JNIEnv *m_env = NULL;
static jint m_businessType;

JNIEXPORT jint JNICALL
Java_com_frank_command_FFmpegCmd_handle(JNIEnv *env, jclass obj, jobjectArray commands,
                                        jint businessType) {

    (*env)->GetJavaVM(env, &jvm);
    m_clazz = (*env)->NewGlobalRef(env, obj);
    m_env = env;
    m_businessType = businessType;

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

void callJavaMethod(char *line, int level) {
    if (level <= 24) { // 24以下的都是错误日志
        __android_log_write(ANDROID_LOG_ERROR, "error:", line);
    } else {

        if (m_clazz != NULL) {
            if (m_businessType == 1) { // 裁剪模块
                // 获取方法ID (I)V指的是方法签名 通过javap -s -public FFmpegCmd 命令生成
                jmethodID methodID = (*m_env)->GetStaticMethodID(m_env, m_clazz, "onProgress",
                                                                 "(Ljava/lang/String;I)V");
                if (methodID != NULL) {
                    char timeStr[16] = "dts";
                    char *q = strstr(line, timeStr); // 截取带dts的串
                    if (q != NULL) {
                        __android_log_write(ANDROID_LOG_DEBUG, "debug:", q);

                        const char *sep = ",";
                        char *p = strtok(q, sep);
                        const char *sep2 = "dts ";
                        char *p2 = strtok(p, sep2);

                        jstring result = (*m_env)->NewStringUTF(m_env, p2);
                        (*m_env)->CallStaticVoidMethod(m_env, m_clazz, methodID, result,
                                                       m_businessType);  // 调用该方法
                        (*m_env)->DeleteLocalRef(m_env, result);
                    }
                }
            }
        }
    }
}

void onDone() {
    if (m_clazz != NULL) {
        // 获取方法ID (I)V指的是方法签名 通过javap -s -public FFmpegCmd 命令生成
        jmethodID methodID = (*m_env)->GetStaticMethodID(m_env, m_clazz, "onDone", "()V");
        if (methodID != NULL) {
            (*m_env)->CallStaticVoidMethod(m_env, m_clazz, methodID);  // 调用该方法
        }
    }
}

void clear() {
//    (*m_env)->DeleteGlobalRef(m_env, m_clazz);
//    (*m_env)->DeleteGlobalRef(m_env, m_businessType);
//    (*jvm)->DetachCurrentThread(*jvm); // 解除与虚拟机的连接
//    (*jvm)->DestroyJavaVM(*jvm); // 卸载虚拟机
}

