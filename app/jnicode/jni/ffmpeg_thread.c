//
// Created by YANGJIE8 on 2020/5/22.
//
#include "ffmpeg_thread.h"

pthread_t ntid;
char **argvs = NULL;
int num=0;


void *thread(void *arg)
{   //执行
    int result = ffmpeg_exec(num, argvs);
    return ((void *)0);
}
/**
 * 新建子线程执行ffmpeg命令
 */
int ffmpeg_thread_run_cmd(int cmdnum,char **argv){
    num=cmdnum;
    argvs=argv;

    int temp =pthread_create(&ntid,NULL,thread,NULL);
    if(temp!=0)
    {
        //LOGE("can't create thread: %s ",strerror(temp));
        return 1;
    }
    return 0;
}

static void (*ffmpeg_callback)(int ret);
/**
 * 注册线程回调
 */
void ffmpeg_thread_callback(void (*cb)(int ret)){
    ffmpeg_callback = cb;
}
/**
 * 退出线程
 */
void ffmpeg_thread_exit(int ret){
    if (ffmpeg_callback) {
        ffmpeg_callback(ret);
    }
    pthread_exit("ffmpeg_thread_exit");
}

int ffmpeg_thread_cancel(){
    return -1;
}

