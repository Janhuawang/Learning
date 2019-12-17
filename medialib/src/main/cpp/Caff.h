//
//  Caff.h
//  AudioMixDemo
//
//  Created by lych on 2019/12/16.
//  Copyright © 2019 UMU. All rights reserved.
//

#ifndef Caff_h
#define Caff_h
#ifdef __cplusplus
extern "C" {
#endif

#include <stdio.h>
#include "Macro.h"

struct CAF_FILE_HEADER {
    char type[4];
    short  mFileVersion;
    short  mFileFlags;
};
typedef struct CAF_FILE_HEADER CAF_HEADER;

struct CAF_CHUNK_HEADER
{
    char type[4];
    Sint64 mChunkSize;
};
typedef struct CAF_CHUNK_HEADER CAF_CHUNK;

struct CAF_FORMAT_CHUNK {
    double mSampleRate;
    Uint32  mFormatID;
    Uint32  mFormatFlags;
    Uint32  mBytesPerPacket;
    Uint32  mFramesPerPacket;
    Uint32  mChannelsPerFrame;
    Uint32  mBitsPerChannel;
};
typedef struct CAF_FORMAT_CHUNK CAF_FORMAT;

Cbool isCAFFile(FILE *file);

int CFReadHeader(FILE *file, CAF_HEADER *header,size_t size);

int CFReadFormat(FILE *file, CAF_FORMAT *format,size_t size,Cbool isBigEndian);

int CFReadDataChunk(FILE *file, CAF_CHUNK *data,size_t size,Cbool isBigEndian);
#ifdef __cplusplus
}
#endif
#endif /* Caff_h */
