LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE :=  libavutil
LOCAL_SRC_FILES := prebuilt/libavutil.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  libswresample
LOCAL_SRC_FILES := prebuilt/libswresample.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  libswscale
LOCAL_SRC_FILES := prebuilt/libswscale.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavcodec
LOCAL_SRC_FILES := prebuilt/libavcodec.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := libavformat
LOCAL_SRC_FILES := prebuilt/libavformat.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavfilter
LOCAL_SRC_FILES := prebuilt/libavfilter.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavdevice
LOCAL_SRC_FILES := prebuilt/libavdevice.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libpostproc
LOCAL_SRC_FILES := prebuilt/libpostproc.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm
LOCAL_MODULE := ffmpeg


LOCAL_SRC_FILES := Jni_FFmpegCmd.c\
                   cmdutils.c \
                   ffmpeg.c \
                   ffmpeg_opt.c \
                   ffmpeg_filter.c \
                   ffmpeg_thread.c

LOCAL_C_INCLUDES := E:\ijkPlayer\EpMedia\ffmpeg-3.3.4

LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid -lm -pthread -L$(SYSROOT)/usr/lib -latomic
LOCAL_SHARED_LIBRARIES := libavcodec libavfilter libavformat libavutil libswresample libswscale libavdevice libpostproc
LOCAL_CFLAGS := -D__STDC_CONSTANT_MACROS -Wno-sign-compare -Wno-switch -Wno-pointer-sign -DHAVE_NEON=1 -fPIC -DANDROI

include $(BUILD_SHARED_LIBRARY)