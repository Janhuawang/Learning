//
//  Wav.c
//  UMU
//
//  Created by lych on 2019/12/13.
//  Copyright © 2019 UMU. All rights reserved.
//

#include "Wav.h"
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

const char *WRiffTag = "riff";
const char *WaveTag = "wave";
const char *WFmtTag = "fmt ";
const char *WDataTag = "data";

Cbool isWAVFile(FILE *file)
{
    Cbool ret = Cfalse;
    if (file) {
        WAVE_HEADER riff;
        WFReadHeader(file, &riff, sizeof(riff));
        if (strncasecmp(riff.riff,WRiffTag,4) == 0 &&
            strncasecmp(riff.riffType, WaveTag,4) == 0) {
            ret = Ctrue;
        }
        fseek(file, 0, SEEK_SET);
    }
    return ret;
}

int WFReadHeader(FILE *file, WAVE_HEADER *header,size_t size)
{
    if (file && header) {
        size_t ret = fread(header, size, 1, file);
        if (ret == 1) {
            return 0;
        }
    }
    return -1;
}

int WFReadFormat(FILE *file, WAVE_FORMAT *format,size_t size)
{
    if (file && format) {
        size_t ret = fread(format, size, 1, file);
        if (ret == 1 &&
            strncasecmp(format->fccid,WFmtTag,4) == 0) {
            return 0;
        }
    }
    return -1;
}

int WFReadData(FILE *file, WAVE_DATA *data,size_t size)
{
    if (file && data) {
        while (!feof(file)) {
            size_t ret = fread(data, size, 1, file);
            if (ret == 1) {
                if (strncasecmp(data->fccid, WDataTag,4) == 0) {
                    return 0;
                } else {
                    fseek(file, data->dwSize, SEEK_CUR);
                }
            } else {
                break;
            }
        }
    }
    return -1;
}

int WFWriteHeader(FILE *file, WAVE_HEADER *header,size_t size)
{
    if (file && header) {
        return (int)fwrite(header, size, 1, file);
    }
    return -1;
}

int WFWriteFormat(FILE *file, WAVE_FORMAT *format,size_t size)
{
    if (file && format) {
        return (int)fwrite(format, size, 1, file);
    }
    return -1;
}

int WFWriteData(FILE *file, WAVE_DATA *data,size_t size)
{
    if (file && data) {
        return (int)fwrite(data, size, 1, file);
    }
    return -1;
}

int WFHeaderSize(const char * file,Uint32 *size)
{
    int ret = 0;
    if (file) {
        FILE *ifp = fopen(file, "rb");
        if (ifp == NULL || (isWAVFile(ifp) == Cfalse)) {
            if (size) *size = 0;
            return -1;
        }
        WAVE_HEADER mHeader;
        WFReadHeader(ifp, &mHeader, sizeof(mHeader));
        WAVE_FORMAT mFormat;
        WFReadFormat(ifp, &mFormat, sizeof(mFormat));
        WAVE_DATA mData;
        WFReadData(ifp, &mData, sizeof(mData));
        // data pos. when need loop, do seek here.
        fpos_t dataPos;
        fgetpos(ifp, &dataPos);
        
        if(size) *size = (Uint32)dataPos;

        if (ifp) {
            fclose(ifp);
        }
    }
    return ret;
}
