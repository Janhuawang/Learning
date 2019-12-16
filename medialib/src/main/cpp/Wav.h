//
//  Wav.h
//  AudioMixDemo
//
//  Created by lych on 2019/12/13.
//  Copyright © 2019 Levi. All rights reserved.
//

#ifndef Wav_h
#define Wav_h
#ifdef __cplusplus
extern "C" {
#endif

#include <stdio.h>

#define Uint32_MAX   4294967295U
typedef unsigned int Uint32;
typedef enum {
    Ctrue = 1,
    Cfalse = 0,
}Cbool;

/**
 * WAV文件头44个字节
 */
struct RIFF_CHUNK  //头信息 12字节
{
    char riff[4];   // 0-3  riff 标识
    Uint32 dwSize;  // 文件大小
    char riffType[4];  // WAVE 标识
};

typedef struct RIFF_CHUNK WAVE_HEADER;

struct FORMAT_CHUNK  //
{
    char fccid[4]; //波形格式 'fmt ' exp.
    Uint32 dwSize; //过滤字节 一般为16.
    short wFormatTag; //格式种类。1 表示数据为线性pcm编码。
    short wChannels; //通道数
    Uint32 dwSamplesPerSec; //采样率
    Uint32 dwBitRate; //比特率 = dwSamplesPerSec * wChannels*wBitsPerSample/8
    short wBlock; //每个样本块长度 = wChannels * wBitsPerSample/8
    short wBitsPerSample; //每个采样点的位数
};

typedef struct FORMAT_CHUNK WAVE_FORMAT;

struct DATA_CHUNK
{
    char fccid[4]; // data 标识
    Uint32 dwSize; // 数据 大小
};

typedef struct DATA_CHUNK WAVE_DATA;

Cbool isWAVFile(FILE *file);

int WFReadHeader(FILE *file, WAVE_HEADER *header,size_t size);
int WFReadFormat(FILE *file, WAVE_FORMAT *format,size_t size);
int WFReadData(FILE *file, WAVE_DATA *data,size_t size);

int WFWriteHeader(FILE *file, WAVE_HEADER *header,size_t size);
int WFWriteFormat(FILE *file, WAVE_FORMAT *format,size_t size);
int WFWriteData(FILE *file, WAVE_DATA *data,size_t size);

#ifdef __cplusplus
}
#endif
#endif /* Wav_h */
