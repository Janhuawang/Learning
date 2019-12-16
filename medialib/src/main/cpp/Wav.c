//
//  Wav.c
//  AudioMixDemo
//
//  Created by lych on 2019/12/13.
//  Copyright © 2019 Levi. All rights reserved.
//

#include "Wav.h"
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

const char *riffTag = "riff";
const char *wavTag = "wave";
const char *fmtTag = "fmt ";
const char *dataTag = "data";

Cbool isWAVFile(FILE *file)
{
    Cbool ret = Cfalse;
    if (file) {
        WAVE_HEADER riff;
        WFReadHeader(file, &riff, sizeof(riff));
        if (strncasecmp(riff.riff,riffTag,4) == 0 &&
            strncasecmp(riff.riffType, wavTag,4) == 0) {
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
            strncasecmp(format->fccid,fmtTag,4) == 0) {
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
                if (strncasecmp(data->fccid, dataTag,4) == 0) {
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
