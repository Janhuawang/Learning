//
//  AudioMix.c
//  UMU
//
//  Created by lych on 2019/12/13.
//  Copyright © 2019 UMU. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <unistd.h>
#include <string.h>
#include "AudioMix.h"
#include "Caff.h"

#define A_BUFFER_SIZE 1024
#define A_RESULT_ERROR (-1)
#define MAX_SHORT  32767
#define MIN_SHORT  (-32768)

struct MIX_VALUE {
    long mixLen;
    long fadeInLen;
    long fadeOutLen;
    long repeatPos;
};

typedef struct MIX_VALUE MIXVALUE;

int MixCaffFile(FILE *ifp, FILE *mfp, FILE *ofp, MPARAM param);

int MixWavFile(FILE *ifp, FILE *mfp, FILE *ofp, MPARAM param);

void AMixBytes(short i, short m, short *o, double *f);

void AFadeLengthAdjust(MPARAM param, long *fadeInLen, long *fadeOutLen, long mixDataLen);

void FileCopy(FILE *sfp, FILE *dfp, Uint32 length);

void AMixFileData(FILE *ifp, FILE *mfp, FILE *ofp, MPARAM param, MIXVALUE mValue);

int MixFile(const char *inputFile, const char *mixFile, const char *outputFile, MPARAM param) {
    if (outputFile) {
        unlink(outputFile);
    }
    int ret = 0;
    FILE *ifp = NULL, *mfp = NULL, *ofp = NULL;
    ifp = fopen(inputFile, "rb");
    if (ifp == NULL) {
        ret = A_RESULT_ERROR;
        goto MIX_DONE;
    }

    mfp = fopen(mixFile, "rb");
    if (mfp == NULL) {
        ret = A_RESULT_ERROR;
        goto MIX_DONE;
    }

    ofp = fopen(outputFile, "wb");
    if (ofp == NULL) {
        ret = A_RESULT_ERROR;
        goto MIX_DONE;
    }

    if (!isWAVFile(mfp)) {
        ret = A_RESULT_ERROR;
        goto MIX_DONE;
    }

    if (isWAVFile(ifp)) {
        ret = MixWavFile(ifp, mfp, ofp, param);
    } else if (isCAFFile(ifp)) {
        ret = MixCaffFile(ifp, mfp, ofp, param);
    }

    MIX_DONE:
    if (ifp) {
        fclose(ifp);
    }
    if (mfp) {
        fclose(mfp);
    }
    if (ofp) {
        fclose(ofp);
    }
    return ret;
}

int
ConvertPCMtoWAV(const char *inputFile, const char *outFile, Cbool smallEndia, WAVE_FORMAT *format) {
    int ret = 0;

    FILE *ifp = NULL, *ofp = NULL;
    if (inputFile && outFile && format) {
        ifp = fopen(inputFile, "rb");
        if (ifp == NULL) {
            ret = A_RESULT_ERROR;
            goto CONVERT_DONE;
        }

        ofp = fopen(outFile, "wb");
        if (ofp == NULL) {
            ret = A_RESULT_ERROR;
            goto CONVERT_DONE;
        }

        // data pos. when need loop, do seek here.
        fseek(ifp, 0, SEEK_END);
        Uint32 dwSize = (Uint32) ftell(ifp);
        fseek(ifp, 0, SEEK_SET);

        WAVE_HEADER iHeader = {0};
        memcpy(iHeader.riff, "RIFF", 4);
        memcpy(iHeader.riffType, "WAVE", 4);
        iHeader.dwSize = 36 + dwSize;

        WAVE_FORMAT iFormat = {0};
        memcpy(&iFormat, format, sizeof(iFormat));
        memcpy(iFormat.fccid, "fmt ", 4);

        WAVE_DATA iData = {0};
        memcpy(iData.fccid, "data", 4);
        iData.dwSize = dwSize;

        WFWriteHeader(ofp, &iHeader, sizeof(iHeader));
        WFWriteFormat(ofp, &iFormat, sizeof(iFormat));
        WFWriteData(ofp, &iData, sizeof(iData));

        if (smallEndia == Ctrue) {
            FileCopy(ifp, ofp, dwSize);
        } else {
            int iBuf[A_BUFFER_SIZE] = {0};
            size_t count = 0;
            while (!feof(ifp)) {
                count = fread(iBuf, 4, A_BUFFER_SIZE, ifp);
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        iBuf[i] = BLSWAP_16(iBuf[i]);
                    }
                    fwrite(iBuf, 4, count, ofp);
                }
            }
        }
    } else {
        ret = A_RESULT_ERROR;
    }

    CONVERT_DONE:
    if (ifp) {
        fclose(ifp);
    }
    if (ofp) {
        fclose(ofp);
    }
    return ret;
}

int MixCaffFile(FILE *ifp, FILE *mfp, FILE *ofp, MPARAM param) {
    int ret = 0;
    CAF_HEADER iHeader;
    CFReadHeader(ifp, &iHeader, sizeof(iHeader));
    // 大端字节序
    Cbool isBigEndian = (iHeader.mFileVersion != 1 ? Ctrue : Cfalse);
    CAF_FORMAT iFormat;
    CFReadFormat(ifp, &iFormat, sizeof(iFormat), isBigEndian);
    CAF_CHUNK iData;
    CFReadDataChunk(ifp, &iData, sizeof(iData), isBigEndian);

    WAVE_HEADER mHeader;
    WFReadHeader(mfp, &mHeader, sizeof(mHeader));
    WAVE_FORMAT mFormat;
    WFReadFormat(mfp, &mFormat, sizeof(mFormat));
    WAVE_DATA mData;
    WFReadData(mfp, &mData, sizeof(mData));

    fpos_t inPos, mixPos;
    fgetpos(ifp, &inPos);
    fgetpos(mfp, &mixPos);

    long dwSize = 0;
    if (iData.mChunkSize.hi > 0) {
        dwSize = iData.mChunkSize.hi;
    } else {
        fseek(ifp, 0, SEEK_END);
        dwSize = ftell(ifp);
        fseek(ifp, (long) inPos, SEEK_SET);
    }

    // Mix start offset, copy origin data.
    long startPos = param.startSec * iFormat.mSampleRate * iFormat.mChannelsPerFrame *
                    iFormat.mBitsPerChannel / 8 + inPos;
    if (startPos > 0) {
        fseek(ifp, 0, SEEK_SET);
        FileCopy(ifp, ofp, startPos);
    }

    long fadeInLen = 0, fadeOutLen = 0, mixDataLen = 0;
    if (param.fadeIn) {
        fadeInLen = param.fadeInSec * mFormat.dwBitRate;
    }
    if (param.fadeOut) {
        fadeOutLen = param.fadeOutSec * mFormat.dwBitRate;
    }
    if (param.fadeIn || param.fadeOut) {
        mixDataLen = param.loop ? (dwSize - startPos) : MIN_DATA((dwSize - startPos), mData.dwSize);
        AFadeLengthAdjust(param, &fadeInLen, &fadeOutLen, mixDataLen);
    }

    MIXVALUE v = {mixDataLen, fadeInLen, fadeOutLen, (long) mixPos};
    AMixFileData(ifp, mfp, ofp, param, v);

    return ret;
}

int MixWavFile(FILE *ifp, FILE *mfp, FILE *ofp, MPARAM param) {
    int ret = 0;

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

    WFWriteHeader(ofp, &iHeader, sizeof(iHeader));
    WFWriteFormat(ofp, &iFormat, sizeof(iFormat));
    WFWriteData(ofp, &iData, sizeof(iData));

    // Mix start offset, copy origin data.
    long startPos = param.startSec * iFormat.dwBitRate;
    if (startPos > 0) {
        FileCopy(ifp, ofp, startPos);
    }

    long fadeInLen = 0, fadeOutLen = 0, mixDataLen = 0;
    if (param.fadeIn) {
        fadeInLen = param.fadeInSec * mFormat.dwBitRate;
    }
    if (param.fadeOut) {
        fadeOutLen = param.fadeOutSec * mFormat.dwBitRate;
    }
    if (param.fadeIn || param.fadeOut) {
        mixDataLen = param.loop ? (iData.dwSize - startPos) : MIN_DATA((iData.dwSize - startPos),
                                                                       mData.dwSize);
        AFadeLengthAdjust(param, &fadeInLen, &fadeOutLen, mixDataLen);
    }

    MIXVALUE v = {mixDataLen, fadeInLen, fadeOutLen, (long) dataPos};
    AMixFileData(ifp, mfp, ofp, param, v);

    return ret;
}

void AMixFileData(FILE *ifp, FILE *mfp, FILE *ofp, MPARAM param, MIXVALUE mValue) {
    // Mix
    double f = 1;
    short data1, data2, data_mix = 0;
    size_t ret1, ret2, ret3;
    long mixingDataLen = 0;
    short iBuf[A_BUFFER_SIZE] = {0}, mBuf[A_BUFFER_SIZE] = {0}, oBuf[A_BUFFER_SIZE] = {0};

    Cbool hasOrigin = Cfalse;
    while (!feof(ifp)) {
        ret2 = fread(mBuf, 2, A_BUFFER_SIZE, mfp);
        if (ret2 == 0 && param.loop) {
            fseek(mfp, mValue.repeatPos, SEEK_SET);
            ret2 = fread(mBuf, 2, A_BUFFER_SIZE, mfp);
        }

        if (ret2 > 0) {
            ret1 = fread(iBuf, 2, ret2, ifp);
            for (int i = 0; i < ret1; i++) {
                data1 = iBuf[i];
                data2 = mBuf[i] * param.volumeRate; // 音量比
                // fade in
                if (param.fadeIn && mValue.fadeInLen > 0 && mixingDataLen <= mValue.fadeInLen) {
                    data2 = (data2 * mixingDataLen) / mValue.fadeInLen;
                }

                // fade out
                if (param.fadeOut && mValue.fadeOutLen > 0 &&
                    (mValue.mixLen - mixingDataLen) <= mValue.fadeOutLen) {
                    data2 = (data2 * (mValue.mixLen - mixingDataLen)) / mValue.fadeOutLen;
                }
                mixingDataLen += 2;
                AMixBytes(data1, data2, &data_mix, &f);
                oBuf[i] = data_mix;
            }
            ret3 = fwrite(oBuf, 2, ret1, ofp);
        } else {
            hasOrigin = Ctrue;
        }
        if (hasOrigin == Ctrue) { break; } // do copy.
    }
    // Copy remain data.
    if (hasOrigin == Ctrue) {
        FileCopy(ifp, ofp, Uint32_MAX);
    }
}

void AMixBytes(short i, short m, short *o, double *f) {
    int temp = (i + m) * (*f);
    if (temp > MAX_SHORT) {
        *f = (double) MAX_SHORT / (double) temp;
        temp = MAX_SHORT;
    }
    if (temp < MIN_SHORT) {
        *f = (double) MIN_SHORT / (double) temp;
        temp = MIN_SHORT;
    }
    if (*f < 1) {
        *f += ((double) 1 - *f) / (double) 32.0;
    }
    *o = (short) temp;
}

void AFadeLengthAdjust(MPARAM param, long *fadeInLen, long *fadeOutLen, long mixDataLen) {
    long oriFadeInLen = *fadeInLen;
    long oriFadeOutLen = *fadeOutLen;

    if (param.fadeIn && param.fadeOut) {
        if (oriFadeOutLen + oriFadeInLen > mixDataLen) {
            *fadeOutLen = mixDataLen / 2;
            *fadeInLen = mixDataLen / 2;
        }
    } else if (param.fadeOut) {
        if (oriFadeInLen > mixDataLen) {
            *fadeInLen = mixDataLen;
        }
    } else if (param.fadeIn) {
        if (oriFadeOutLen > mixDataLen) {
            *fadeOutLen = mixDataLen;
        }
    }
}

void FileCopy(FILE *sfp, FILE *dfp, Uint32 length) {
    if (sfp && dfp) {
        char buffer[A_BUFFER_SIZE] = {0};
        size_t c = 0;
        size_t len = MIN_DATA(length, A_BUFFER_SIZE);
        while (len > 0 && (c = fread(buffer, sizeof(char), len, sfp)) > 0) {
            fwrite(buffer, sizeof(char), c, dfp);
            length -= c;
            len = MIN_DATA(length, A_BUFFER_SIZE);
        }
    }
}
