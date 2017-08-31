package Jni;

import VideoHandle.OnEditorListener;

/**
 * Created by 杨杰 on 2017/2/14.
 */

public class FFmpegCmd {
	/**
	 * 加载所有相关链接库
	 */
	static {
		System.loadLibrary("avutil-55");
		System.loadLibrary("avcodec-57");
		System.loadLibrary("swresample-2");
		System.loadLibrary("avformat-57");
		System.loadLibrary("swscale-4");
		System.loadLibrary("avfilter-6");
		System.loadLibrary("avdevice-57");
		System.loadLibrary("ffmpeg");
	}

	private static OnEditorListener listener;
	private static long duration;

	/**
	 * 调用底层执行
	 *
	 * @param argc
	 * @param argv
	 * @return
	 */
	public static native int exec(int argc, String[] argv);

	public static void onExecuted(int ret) {
		if (listener != null) {
			if (ret == 0) {
				listener.onProgress(1);
				listener.onSuccess();
			} else {
				listener.onFailure();
			}
		}

	}

	public static void onProgress(float progress) {
		if (listener != null) {
			if (duration != 0) {
				listener.onProgress(progress / (duration / 1000000) * 0.95f);
			}
		}
	}


	/**
	 * 执行ffmoeg命令
	 *
	 * @param cmds
	 * @param listener
	 */
	public static void exec(String[] cmds, long duration, OnEditorListener listener) {
		FFmpegCmd.listener = listener;
		FFmpegCmd.duration = duration;
		exec(cmds.length, cmds);
	}
}
