//
// Created by YANGJIE8 on 2020/5/22.
//

#ifdef ANDROID
#include <android/log.h>
#ifndef LOG_TAG
#define  MY_TAG   "MYTAG"
#define  AV_TAG   "AVLOG"
#endif
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, MY_TAG, format, ##__VA_ARGS__)
#define LOGD(format, ...)  __android_log_print(ANDROID_LOG_DEBUG,  MY_TAG, format, ##__VA_ARGS__)
#define XLOGD(...)  __android_log_print(ANDROID_LOG_INFO,AV_TAG,__VA_ARGS__)
#define XLOGE(...)  __android_log_print(ANDROID_LOG_ERROR,AV_TAG,__VA_ARGS__)
#else
#define LOGE(format, ...)  printf(MY_TAG format "\n", ##__VA_ARGS__)
#define LOGD(format, ...)  printf(MY_TAG format "\n", ##__VA_ARGS__)
#define XLOGE(format, ...)  fprintf(stdout, AV_TAG ": " format "\n", ##__VA_ARGS__)
#define XLOGI(format, ...)  fprintf(stderr, AV_TAG ": " format "\n", ##__VA_ARGS__)
#endif
