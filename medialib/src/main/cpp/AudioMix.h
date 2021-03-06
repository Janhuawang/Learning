//
//  AudioMix.h
//  UMU
//
//  Created by lych on 2019/12/13.
//  Copyright © 2019 UMU. All rights reserved.
//

#ifndef AudioMix_h
#define AudioMix_h
#ifdef __cplusplus
extern "C" {
#endif

#include "Macro.h"
#include "Wav.h"

struct MIX_PARAM
{
    float volumeRate;
    float startSec;
    Cbool loop;
    Cbool fadeIn;
    Cbool fadeOut;
    short fadeInSec;
    short fadeOutSec;
};

typedef struct MIX_PARAM MPARAM;

/**
 * inputFile 被合成文件
 * mixFile 合成文件
 * outputFile 输出文件
 * 混音起始位置
 * 是否循环
 */
int MixFile(const char *inputFile,const char *mixFile,const char *outputFile,MPARAM param);

/**
 * PCM TO WAV
 * Support PCM 小端字节序
 */
int ConvertPCMtoWAV(const char *inputFile, const char *outFile,Cbool smallEndia,WAVE_FORMAT *format);
#ifdef __cplusplus
}
#endif
#endif /* AudioMix_h */
