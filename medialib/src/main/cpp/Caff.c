//
//  Caff.c
//  AudioMixDemo
//
//  Created by lych on 2019/12/16.
//  Copyright © 2019 UMU. All rights reserved.
//

#include "Caff.h"
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

const char *CaffTag = "caff";
const char *CDescTag = "desc";
const char *CDataTag = "data";

typedef long long __int64;

int CFReadTagChunk(FILE *file, CAF_CHUNK *data,size_t size,Cbool isBigEndian,const char *tag);

__int64 ntoh_int64(__int64 val)
{
    __int64 ret = val;
#if __BYTE_ORDER == __LITTLE_ENDIAN
    ret = (((__int64)BLSWAP_32((unsigned long)val)) << 32) | BLSWAP_32((unsigned long)(val>>32));
#endif
    return ret;
}
 
double ntoh_double(double val)
{
    double ret = val;
#if __BYTE_ORDER == __LITTLE_ENDIAN
    __int64 tmp = ntoh_int64(*((__int64*)&val));
    ret = *((double*)&tmp);
#endif
    return ret;
}

Cbool isCAFFile(FILE *file)
{
    Cbool ret = Cfalse;
    if (file) {
        CAF_HEADER caff;
        CFReadHeader(file, &caff, sizeof(caff));
        if (strncasecmp(caff.type,CaffTag,4) == 0) {
            ret = Ctrue;
        }
        fseek(file, 0, SEEK_SET);
    }
    return ret;
}

int CFReadHeader(FILE *file, CAF_HEADER *header,size_t size)
{
    if (file && header) {
        size_t ret = fread(header, size, 1, file);
        if (ret == 1) {
            return 0;
        }
    }
    return -1;
}

int CFReadFormat(FILE *file, CAF_FORMAT *format,size_t size,Cbool isBigEndian)
{
    if (file && format) {
        CAF_CHUNK desc;
        int ret = CFReadTagChunk(file,&desc,sizeof(desc), isBigEndian, CDescTag);
        if (ret == 0) {
            size_t ret = fread(format, size, 1, file);
            if (ret == 1) {
                if (isBigEndian) {
                    // do somthing.
                    format->mSampleRate = ntoh_double(format->mSampleRate);
                    format->mBitsPerChannel = BLSWAP_32(format->mBitsPerChannel);
                    format->mFramesPerPacket = BLSWAP_32(format->mFramesPerPacket);
                    format->mChannelsPerFrame = BLSWAP_32(format->mChannelsPerFrame);
                    format->mBytesPerPacket = BLSWAP_32(format->mBytesPerPacket);
                    format->mFormatFlags = BLSWAP_32(format->mFormatFlags);
                }
                return 0;
            }
        }
    }
    return -1;
}

int CFReadDataChunk(FILE *file, CAF_CHUNK *data,size_t size,Cbool isBigEndian)
{
    return CFReadTagChunk(file, data, size, isBigEndian, CDataTag);
}

int CFReadTagChunk(FILE *file, CAF_CHUNK *data,size_t size,Cbool isBigEndian,const char *tag)
{
    if (file && data) {
        while (!feof(file)) {
            size_t ret = fread(data, size, 1, file);
            if (ret == 1) {
                if (strncasecmp(data->type, tag,4) == 0) {
                    if (isBigEndian) {
                        data->mChunkSize.hi = BLSWAP_32(data->mChunkSize.hi);
                        // do somthing.
                    }
                    return 0;
                } else {
                    if (isBigEndian) {
                        data->mChunkSize.hi = BLSWAP_32(data->mChunkSize.hi);
                        // do somthing.
                    }
                    fseek(file, data->mChunkSize.hi, SEEK_CUR);
                }
            } else {
                break;
            }
        }
    }
    return -1;
}

int CFReadTag(FILE *file, const char *tag)
{
    if (file && tag) {
        CAF_CHUNK chunk = {0};
        while (!feof(file)) {
            size_t ret = fread(&chunk, sizeof(chunk), 1, file);
            if (ret == 1) {
                if (strncasecmp(chunk.type, tag,4) == 0) {
                    return 0;
                }
            } else {
                break;
            }
        }
    }
    return -1;
}
