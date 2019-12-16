//
//  AudioMix.c
//  AudioMixDemo
//
//  Created by lych on 2019/12/13.
//  Copyright © 2019 Levi. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "AudioMix.h"

#define MAX_BUFFER_SIZE 1024
#define MIX_RESULT_ERROR (-1)
#define MAX_SHORT  32767
#define MIN_SHORT  (-32768)
#define MIN_DATA(a,b) ((a) > (b) ? (b) : (a))


int MixWavFile(FILE *ifp,char *mixFile,char *outputFile,MPARAM param);
void AMix(short i,short m,short *o,double *f);
void AFadeLengthAdjust(MPARAM param,long *fadeInLen,long *fadeOutLen,long mixDataLen);
void FileCopy(FILE *sfp, FILE *dfp, long length);

int MixFile(char *inputFile, char *mixFile,char *outputFile,MPARAM param)
{
    int ret = 0;
    FILE *ifp = fopen(inputFile, "rb");
    if (ifp == NULL) {
        ret = MIX_RESULT_ERROR;
        goto MIX_DONE;
    }
    
    if (isWAVFile(ifp)) {
        ret = MixWavFile(ifp, mixFile, outputFile,param);
    }
    
MIX_DONE:
    if (ifp) {
        fclose(ifp);
    }
    return ret;
}

int MixWavFile(FILE *ifp,char *mixFile,char *outputFile,MPARAM param) {
    int ret = 0;
    FILE *mfp, *ofp = NULL;
    mfp = fopen(mixFile, "rb");
    if (mfp == NULL) {
        ret = MIX_RESULT_ERROR;
        goto WAV_DONE;
    }
    
    ofp = fopen(outputFile, "wb");
    if (ofp == NULL) {
        ret = MIX_RESULT_ERROR;
        goto WAV_DONE;
    }
    
    WAVE_HEADER iHeader;
    WFReadHeader(ifp, &iHeader, sizeof(iHeader));
    WAVE_FORMAT iFormat;
    WFReadFormat(ifp, &iFormat, sizeof(iFormat));
    WAVE_DATA iData;
    WFReadData(ifp, &iData, sizeof(iData));
    
    WAVE_HEADER mHeader;
    WFReadHeader(mfp, &mHeader, sizeof(mHeader));
    WAVE_FORMAT mFormat;
    WFReadFormat(mfp, &mFormat, sizeof(mFormat));
    WAVE_DATA mData;
    WFReadData(mfp, &mData, sizeof(mData));
    // data pos. when need loop, do seek here.
    fpos_t dataPos;
    fgetpos(mfp, &dataPos);

    WFWriteHeader(ofp,&iHeader, sizeof(iHeader));
    WFWriteFormat(ofp, &iFormat, sizeof(iFormat));
    WFWriteData(ofp,&iData, sizeof(iData));

    // Mix start offset, copy origin data.
    long startPos = param.startSec * iFormat.dwBitRate;
    if (startPos > 0) {
        FileCopy(ifp, ofp,startPos);
    }
        
// TODO 渐入 渐出 音量比. 读写优化
    long fadeInLen = 0, fadeOutLen = 0,mixDataLen = 0;
    if (param.fadeIn) {
        fadeInLen = param.fadeInSec * mFormat.dwBitRate;
    }
    if (param.fadeOut) {
        fadeOutLen = param.fadeOutSec *mFormat.dwBitRate;
    }
    if (param.fadeIn || param.fadeOut) {
        mixDataLen = param.loop ? (iData.dwSize - startPos) : MIN_DATA((iData.dwSize - startPos), mData.dwSize);
        AFadeLengthAdjust(param, &fadeInLen, &fadeOutLen,mixDataLen);
    }
    
    // Mix
    double f=1;
    short data1,data2,data_mix = 0;
    size_t ret1,ret2,ret3;
    long mixingDataLen = 0;
    
    Cbool hasOrigin = Cfalse;
    while (!feof(ifp)) {
        ret1 = fread(&data1,2,1,ifp);
        ret2 = fread(&data2,2,1,mfp);
        if (ret2 == 0 && param.loop) {
            fseek(mfp, dataPos, SEEK_SET);
            ret2 = fread(&data2,2,1,mfp);
        }
        if (ret2 > 0) {
            data2 *= param.volumeRate; // 音量比
            // fade in
            if (param.fadeIn && fadeInLen > 0 && mixingDataLen <= fadeInLen) {
                data2 = (data2 * mixingDataLen) / fadeOutLen;
            }
            
            // fade out
            if (param.fadeOut && fadeOutLen > 0 && (mixDataLen - mixingDataLen) <= fadeOutLen) {
                data2 = (data2 * (mixDataLen - mixingDataLen)) / fadeOutLen;
            }
            mixingDataLen += 2;
            AMix(data1, data2, &data_mix, &f);
        } else {
            data_mix = data1;
            hasOrigin = Ctrue;
        }
        ret3 = fwrite(&data_mix, 2, 1, ofp);
        if (hasOrigin == Ctrue) {break;} // do copy.
    }
    // Copy remain data.
    if (hasOrigin == Ctrue) {
        FileCopy(ifp, ofp, Uint32_MAX);
    }
        
WAV_DONE:
    if (mfp) {
        fclose(mfp);
    }
    if (ofp) {
        fclose(ofp);
    }
    return ret;
}

void AMix(short i,short m,short *o,double *f)
{
    int temp = (i + m)*(*f);
    if (temp > MAX_SHORT) {
        *f = (double)MAX_SHORT/(double)temp;
        temp = MAX_SHORT;
    }
    if (temp < MIN_SHORT) {
        *f = (double)MIN_SHORT/(double)temp;
        temp = MIN_SHORT;
    }
    if (*f < 1) {
        *f += ((double)1-*f)/(double)32.0;
    }
    *o = (short)temp;
}

void AFadeLengthAdjust(MPARAM param,long *fadeInLen,long *fadeOutLen,long mixDataLen) {
    long oriFadeInLen = *fadeInLen;
    long oriFadeOutLen = *fadeOutLen;
    
    if (param.fadeIn && param.fadeOut) {
        if (oriFadeOutLen + oriFadeInLen > mixDataLen) {
            *fadeOutLen = mixDataLen/2;
            *fadeInLen = mixDataLen/2;
        }
    }else if (param.fadeOut) {
        if (oriFadeInLen > mixDataLen) {
            *fadeInLen = mixDataLen;
        }
    }else if (param.fadeIn) {
        if (oriFadeOutLen > mixDataLen) {
            *fadeOutLen = mixDataLen;
        }
    }
}

void FileCopy(FILE *sfp, FILE *dfp, long length)
{
    if (sfp && dfp) {
        char buffer[MAX_BUFFER_SIZE] = {0};
        size_t c = 0;
        size_t len = MIN_DATA(length,MAX_BUFFER_SIZE);
        while ((c = fread(buffer, sizeof(char), len, sfp)) > 0 ) {
            fwrite(buffer, sizeof(char),c, dfp);
            length -= c;
            len = MIN_DATA(length,MAX_BUFFER_SIZE);
        }
    }
}
