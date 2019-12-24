#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <malloc.h>
#include "AudioMix.h"
#include "Wav.h"


JNIEXPORT jint JNICALL
Java_com_medialib_audioeditc_AudioMain_mix(JNIEnv *env, jclass mainObj, jstring srcFile,
                                           jstring coverFile,
                                           jstring outPutFile, jobject paramObj) {

    char *srcFileChar = (*env)->GetStringUTFChars(env, srcFile, NULL);
    char *coverFileChar = (*env)->GetStringUTFChars(env, coverFile, NULL);
    char *outPutFileChar = (*env)->GetStringUTFChars(env, outPutFile, NULL);

    __android_log_print(ANDROID_LOG_ERROR, "Audio", "1 %s\n", srcFileChar);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "2 %s\n", coverFileChar);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "3 %s\n", outPutFileChar);

    jclass paramClazz = (*env)->GetObjectClass(env, paramObj);
    if (paramClazz == NULL) {
        return 0;
    }
    jfieldID testStrFID = (*env)->GetFieldID(env, paramClazz, "testStr", "Ljava/lang/String;");
    jstring testStr = (*env)->GetObjectField(env, paramObj, testStrFID);
    char *testStrChar = (*env)->GetStringUTFChars(env, testStr, NULL);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "testStrChar %s\n", testStrChar);
    (*env)->DeleteLocalRef(env, testStr);

    jfieldID fadeInId = (*env)->GetFieldID(env, paramClazz, "fadeIn", "Z");
    jboolean fadeIn = (*env)->GetBooleanField(env, paramObj, fadeInId);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "fadeIn %d\n", fadeIn ? 1 : 0);

    jfieldID fadeInSecID = (*env)->GetFieldID(env, paramClazz, "fadeInSec", "I");
    int fadeInSec = (*env)->GetIntField(env, paramObj, fadeInSecID);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "fadeInSec %d\n", fadeInSec);

    jfieldID fadeOutId = (*env)->GetFieldID(env, paramClazz, "fadeOut", "Z");
    jboolean fadeOut = (*env)->GetBooleanField(env, paramObj, fadeOutId);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "fadeOut %d\n", fadeOut ? 1 : 0);

    jfieldID fadeOutSecId = (*env)->GetFieldID(env, paramClazz, "fadeOutSec", "I");
    int fadeOutSec = (*env)->GetIntField(env, paramObj, fadeOutSecId);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "fadeOutSec %d\n", fadeOutSec);

    jfieldID loopId = (*env)->GetFieldID(env, paramClazz, "loop", "Z");
    jboolean loop = (*env)->GetBooleanField(env, paramObj, loopId);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "loop %d\n", loop ? 1 : 0);

    jfieldID startSecId = (*env)->GetFieldID(env, paramClazz, "startSec", "I");
    int startSec = (*env)->GetIntField(env, paramObj, startSecId);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "startSec %d\n", startSec);

    jfieldID volumeRateId = (*env)->GetFieldID(env, paramClazz, "volumeRate", "F");
    float volumeRate = (*env)->GetFloatField(env, paramObj, volumeRateId);
    __android_log_print(ANDROID_LOG_ERROR, "Audio", "volumeRate %f\n", volumeRate);


    MPARAM p = {0};
    p.fadeIn = fadeIn ? Ctrue : Cfalse;
    p.fadeInSec = fadeInSec;
    p.fadeOut = fadeOut ? Ctrue : Cfalse;
    p.fadeOutSec = fadeOutSec;
    p.loop = loop ? Ctrue : Cfalse;
    p.startSec = startSec;
    p.volumeRate = volumeRate;

    return MixFile(srcFileChar, coverFileChar, outPutFileChar, p);
}

JNIEXPORT jint JNICALL
Java_com_medialib_audioeditc_AudioMain_pcm16leToWav(JNIEnv *env, jclass mainObj, jstring pcmPath,
                                                    jstring wavPath) {

    char *pcmPathChar = (*env)->GetStringUTFChars(env, pcmPath, NULL);
    char *wavPathChar = (*env)->GetStringUTFChars(env, wavPath, NULL);

    WAVE_FORMAT format = {0};
    format.dwSize = 16;
    format.wFormatTag = 1;
    format.wChannels = 1;
    format.dwSamplesPerSec = 16000;
    format.wBitsPerSample = 32;
    format.wBlock = format.wChannels * format.wBitsPerSample / 8;
    format.dwBitRate = format.dwSamplesPerSec * format.wChannels * format.wBitsPerSample / 8;
    return ConvertPCMtoWAV(pcmPathChar, wavPathChar, &format);
}


JNIEXPORT jint JNICALL
Java_com_medialib_audioeditc_AudioMain_getWavHeadSize(JNIEnv *env, jclass mainObj,
                                                      jstring wavPath) {

    char *wavPathChar = (*env)->GetStringUTFChars(env, wavPath, NULL);
    Uint32 size = 0;
    int result = WFHeaderSize(wavPathChar, &size);
    if (result == 0) {
        return size;
    } else {
        return -1;
    }
}



