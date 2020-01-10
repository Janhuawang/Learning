//
//  Macro.h
//  UMU
//
//  Created by lych on 2019/12/16.
//  Copyright © 2019 UMU. All rights reserved.
//

#ifndef Macro_h
#define Macro_h
#ifdef __cplusplus
extern "C" {
#endif


#define MIN_DATA(a,b) ((a) > (b) ? (b) : (a))
#define MAX_DATA(a,b) ((a) < (b) ? (b) : (a))

#define Uint32_MAX   4294967295U

typedef unsigned int        Uint32;
typedef signed int          Sint32;
typedef unsigned short      Uint16;

typedef enum {
    Ctrue = 1,
    Cfalse = 0,
}Cbool;

struct Uwide {
  Uint32              lo;
  Sint32              hi;
};
typedef struct Uwide  Sint64;


#define BLSWAP_16(x) \
    (Uint16)((((Uint16)(x) & 0x00ff) << 8) | \
              (((Uint16)(x) & 0xff00) >> 8) \
             )
             
#define BLSWAP_32(x) \
    (Uint32)((((Uint32)(x) & 0xff000000) >> 24) | \
              (((Uint32)(x) & 0x00ff0000) >> 8) | \
              (((Uint32)(x) & 0x0000ff00) << 8) | \
              (((Uint32)(x) & 0x000000ff) << 24) \
             )
 

#ifdef __cplusplus
}
#endif
#endif /* Tool_h */
