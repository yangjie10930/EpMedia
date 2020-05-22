//
// Created by YANGJIE8 on 2020/5/22.
//

#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "ffmpeg.h"
#include <pthread.h>
#include <string.h>

int ffmpeg_thread_run_cmd(int cmdnum,char **argv);

int ffmpeg_thread_cancel();

void ffmpeg_thread_callback(void (*cb)(int ret));