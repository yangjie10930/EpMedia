package VideoHandle;

import android.app.Activity;
import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Jni.FFmpegCmd;
import Jni.FileUtils;
import Jni.TrackUtils;
import Jni.VideoUitls;

/**
 * 视频编辑器
 * Created by YangJie on 2017/5/18.
 */

public class EpEditor {

	private static final int DEFAULT_WIDTH = 480;//默认输出宽度
	private static final int DEFAULT_HEIGHT = 360;//默认输出高度

	private Context context;

	public enum Format {
		MP3, MP4
	}

	public EpEditor(Context context) {
		this.context = context;
	}

	/**
	 * 处理单个视频
	 *
	 * @param epVideo      需要处理的视频
	 * @param outputOption 输出选项配置
	 */
	public void exec(EpVideo epVideo, OutputOption outputOption, OnEditorListener onEditorListener) {
		boolean isFilter = false;
		ArrayList<EpDraw> epDraws = epVideo.getEpDraws();
		//开始处理
		StringBuilder cmd = new StringBuilder("-y");
		if (epVideo.getVideoClip()) {
			cmd.append(" -ss ").append(epVideo.getClipStart()).append(" -t ").append(epVideo.getClipDuration()).append(" -accurate_seek");
		}
		cmd.append(" -i ").append(epVideo.getVideoPath());
		//添加图片或者动图
		if (epDraws.size() > 0) {
			for (int i = 0; i < epDraws.size(); i++) {
				if (epDraws.get(i).isAnimation()) {
					cmd.append(" -ignore_loop 0");
				}
				cmd.append(" -i ").append(epDraws.get(i).getPicPath());
			}
			cmd.append(" -filter_complex [0:v]").append(epVideo.getFilters() != null ? epVideo.getFilters() + "," : "")
					.append("scale=").append(outputOption.width == 0 ? "iw" : outputOption.width).append(":")
					.append(outputOption.height == 0 ? "ih" : outputOption.height)
					.append(outputOption.width == 0 ? "" : ",setdar=" + outputOption.getSar()).append("[outv0];");
			for (int i = 0; i < epDraws.size(); i++) {
				cmd.append("[").append(i + 1).append(":0]").append(epDraws.get(i).getPicFilter()).append("scale=").append(epDraws.get(i).getPicWidth()).append(":")
						.append(epDraws.get(i).getPicHeight()).append("[outv").append(i + 1).append("];");
			}
			for (int i = 0; i < epDraws.size(); i++) {
				if (i == 0) {
					cmd.append("[outv").append(i).append("]").append("[outv").append(i + 1).append("]");
				} else {
					cmd.append("[outo").append(i - 1).append("]").append("[outv").append(i + 1).append("]");
				}
				cmd.append("overlay=").append(epDraws.get(i).getPicX()).append(":").append(epDraws.get(i).getPicY());
				if (epDraws.get(i).isAnimation()) {
					cmd.append(":shortest=1");
				}
				if (i < epDraws.size() - 1) {
					cmd.append("[outo").append(i).append("];");
				}
			}
			isFilter = true;
		} else {
			if (epVideo.getFilters() != null) {
				cmd.append(" -filter_complex ").append(epVideo.getFilters());
				isFilter = true;
			}
			//设置输出分辨率
			if (outputOption.width != 0) {
				if (epVideo.getFilters() != null) {
					cmd.append(",scale=").append(outputOption.width).append(":").append(outputOption.height)
							.append(",setdar=").append(outputOption.getSar());
				} else {
					cmd.append(" -filter_complex scale=").append(outputOption.width).append(":").append(outputOption.height)
							.append(",setdar=").append(outputOption.getSar());
					isFilter = true;
				}
			}
		}

		//输出选项
		cmd.append(outputOption.getOutputInfo());
		if (!isFilter && outputOption.getOutputInfo().isEmpty()) {
			cmd.append(" -vcodec copy -acodec copy");
		} else {
			cmd.append(" -preset superfast");
		}
		cmd.append(" ").append(outputOption.outPath);
		long duration = VideoUitls.getDuration(epVideo.getVideoPath());
		if (epVideo.getVideoClip()) {
			long clipTime = (long) ((epVideo.getClipDuration() - epVideo.getClipStart()) * 1000000);
			duration = clipTime < duration ? clipTime : duration;
		}
		//执行命令
		execCmd(cmd.toString(), duration, onEditorListener);
	}

	/**
	 * 合并多个视频
	 *
	 * @param epVideos     需要合并的视频集合
	 * @param outputOption 输出选项配置
	 */
	public void merge(List<EpVideo> epVideos, OutputOption outputOption, OnEditorListener onEditorListener) {
		//检测是否有无音轨视频
		boolean isNoAudioTrack = false;
		for (EpVideo epVideo : epVideos) {
			MediaExtractor mediaExtractor = new MediaExtractor();
			try {
				mediaExtractor.setDataSource(epVideo.getVideoPath());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			int at = TrackUtils.selectAudioTrack(mediaExtractor);
			if (at == -1) {
				isNoAudioTrack = true;
				mediaExtractor.release();
				break;
			}
			mediaExtractor.release();
		}
		//设置默认宽高
		outputOption.width = outputOption.width == 0 ? DEFAULT_WIDTH : outputOption.width;
		outputOption.height = outputOption.height == 0 ? DEFAULT_HEIGHT : outputOption.height;
		//判断数量
		if (epVideos.size() > 1) {
			StringBuilder cmd = new StringBuilder("-y");
			//添加输入标示
			for (EpVideo e : epVideos) {
				if (e.getVideoClip()) {
					cmd.append(" -ss ").append(e.getClipStart()).append(" -t ").append(e.getClipDuration()).append(" -accurate_seek");
				}
				cmd.append(" -i ").append(e.getVideoPath());
			}
			for (EpVideo e : epVideos) {
				ArrayList<EpDraw> epDraws = e.getEpDraws();
				if (epDraws.size() > 0) {
					for (EpDraw ep : epDraws) {
						if (ep.isAnimation()) cmd.append(" -ignore_loop 0");
						cmd.append(" -i ").append(ep.getPicPath());
					}
				}
			}
			//添加滤镜标识
			cmd.append(" -filter_complex ");
			for (int i = 0; i < epVideos.size(); i++) {
				StringBuilder filter = epVideos.get(i).getFilters() == null ? new StringBuilder("") : epVideos.get(i).getFilters().append(",");
				cmd.append("[").append(i).append(":v]").append(filter).append("scale=").append(outputOption.width).append(":").append(outputOption.height)
						.append(",setdar=").append(outputOption.getSar()).append("[outv").append(i).append("];");
			}
			//添加标记和处理宽高
			int drawNum = epVideos.size();//图标计数器
			for (int i = 0; i < epVideos.size(); i++) {
				for (int j = 0; j < epVideos.get(i).getEpDraws().size(); j++) {
					cmd.append("[").append(drawNum++).append(":0]").append(epVideos.get(i).getEpDraws().get(j).getPicFilter()).append("scale=")
							.append(epVideos.get(i).getEpDraws().get(j).getPicWidth()).append(":").append(epVideos.get(i).getEpDraws().get(j)
							.getPicHeight()).append("[p").append(i).append("a").append(j).append("];");
				}
			}
			//添加图标操作
			for (int i = 0; i < epVideos.size(); i++) {
				for (int j = 0; j < epVideos.get(i).getEpDraws().size(); j++) {
					cmd.append("[outv").append(i).append("][p").append(i).append("a").append(j).append("]overlay=")
							.append(epVideos.get(i).getEpDraws().get(j).getPicX()).append(":")
							.append(epVideos.get(i).getEpDraws().get(j).getPicY());
					if (epVideos.get(i).getEpDraws().get(j).isAnimation()) {
						cmd.append(":shortest=1");
					}
					cmd.append("[outv").append(i).append("];");
				}
			}
			//开始合成视频
			for (int i = 0; i < epVideos.size(); i++) {
				cmd.append("[outv").append(i).append("]");
			}
			cmd.append("concat=n=").append(epVideos.size()).append(":v=1:a=0[outv]");
			//是否添加音轨
			if (!isNoAudioTrack) {
				cmd.append(";");
				for (int i = 0; i < epVideos.size(); i++) {
					cmd.append("[").append(i).append(":a]");
				}
				cmd.append("concat=n=").append(epVideos.size()).append(":v=0:a=1[outa]");
			}
			cmd.append(" -map [outv]");
			if (!isNoAudioTrack) {
				cmd.append(" -map [outa]");
			}
			cmd.append(outputOption.getOutputInfo()).append(" -preset superfast ").append(outputOption.outPath);
			long duration = 0;
			for (EpVideo ep : epVideos) {
				long d = VideoUitls.getDuration(ep.getVideoPath());
				if (ep.getVideoClip()) {
					long clipTime = (long) ((ep.getClipDuration() - ep.getClipStart()) * 1000000);
					d = clipTime < d ? clipTime : d;
				}
				if (d != 0) {
					duration += d;
				} else {
					break;
				}
			}
			//执行命令
			execCmd(cmd.toString(), duration, onEditorListener);
		} else {
			throw new RuntimeException("Need more than one video");
		}
	}

	/**
	 * 无损合并多个视频
	 * <p>
	 * 注意：此方法要求视频格式非常严格，需要合并的视频必须分辨率相同，帧率和码率也得相同
	 *
	 * @param epVideos         需要合并的视频的集合
	 * @param outputOption     输出选项
	 * @param onEditorListener 回调监听
	 */
	public void mergeByLc(List<EpVideo> epVideos, OutputOption outputOption, final OnEditorListener onEditorListener) {
		String appDir = context.getFilesDir().getAbsolutePath() + "/EpVideos/";
		String fileName = "ffmpeg_concat.txt";
		List<String> videos = new ArrayList<>();
		for (EpVideo e : epVideos) {
			videos.add(e.getVideoPath());
		}
		FileUtils.writeTxtToFile(videos, appDir, fileName);
		String cmd = "-y -f concat -safe 0 -i " + appDir + fileName + " -c copy " + outputOption.outPath;
		long duration = 0;
		for (EpVideo ep : epVideos) {
			long d = VideoUitls.getDuration(ep.getVideoPath());
			if (d != 0) {
				duration += d;
			} else {
				break;
			}
		}
		execCmd(cmd, duration, onEditorListener);
	}

	/**
	 * 添加背景音乐
	 *
	 * @param videoin          视频文件
	 * @param audioin          音频文件
	 * @param output           输出路径
	 * @param videoVolume      视频原声音音量(例:0.7为70%)
	 * @param audioVolume      背景音乐音量(例:1.5为150%)
	 * @param onEditorListener 回调监听
	 */
	public void music(String videoin, String audioin, String output, float videoVolume, float audioVolume, OnEditorListener onEditorListener) {
		MediaExtractor mediaExtractor = new MediaExtractor();
		try {
			mediaExtractor.setDataSource(videoin);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		int at = TrackUtils.selectAudioTrack(mediaExtractor);
		mediaExtractor.release();
		String cmd;
		if (at == -1) {
			int vt = TrackUtils.selectVideoTrack(mediaExtractor);
			float duration = (float) mediaExtractor.getTrackFormat(vt).getLong(MediaFormat.KEY_DURATION) / 1000 / 1000;
			cmd = "-y -i " + videoin + " -ss 0 -t " + duration + " -i " + audioin + " -acodec copy -vcodec copy " + output;
		} else {
			cmd = "-y -i " + videoin + " -i " + audioin + " -filter_complex [0:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + videoVolume + "[a0];[1:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + audioVolume + "[a1];[a0][a1]amix=inputs=2:duration=first[aout] -map [aout] -ac 2 -c:v copy -map 0:v:0 " + output;
		}
		execCmd(cmd, 0, onEditorListener);
	}

	/**
	 * 音视频分离
	 *
	 * @param videoin          视频文件
	 * @param out              输出文件路径
	 * @param format           输出类型
	 * @param onEditorListener 回调监听
	 */
	public void demuxer(String videoin, String out, Format format, OnEditorListener onEditorListener) {
		String cmd = "-y -i " + videoin;
		switch (format) {
			case MP3:
				cmd += " -vn -acodec libmp3lame ";
				break;
			case MP4:
				cmd += " -vcodec copy -an ";
				break;
		}
		cmd += out;
		execCmd(cmd, 0, onEditorListener);
	}


	/**
	 * 输出选项设置
	 */
	public static class OutputOption {
		static final int ONE_TO_ONE = 1;// 1:1
		static final int FOUR_TO_THREE = 2;// 4:3
		static final int SIXTEEN_TO_NINE = 3;// 16:9
		static final int NINE_TO_SIXTEEN = 4;// 9:16
		static final int THREE_TO_FOUR = 5;// 3:4

		String outPath;//输出路径
		public int frameRate = 0;//帧率
		public int bitRate = 0;//比特率(一般设置10M)
		public String outFormat = "";//输出格式(目前暂时只支持mp4,x264,mp3,gif)
		private int width = 0;//输出宽度
		private int height = 0;//输出高度
		private int sar = 6;//输出宽高比

		public OutputOption(String outPath) {
			this.outPath = outPath;
		}

		/**
		 * 获取宽高比
		 *
		 * @return 1
		 */
		public String getSar() {
			String res;
			switch (sar) {
				case ONE_TO_ONE:
					res = "1/1";
					break;
				case FOUR_TO_THREE:
					res = "4/3";
					break;
				case THREE_TO_FOUR:
					res = "3/4";
					break;
				case SIXTEEN_TO_NINE:
					res = "16/9";
					break;
				case NINE_TO_SIXTEEN:
					res = "9/16";
					break;
				default:
					res = width + "/" + height;
					break;
			}
			return res;
		}

		public void setSar(int sar) {
			this.sar = sar;
		}

		/**
		 * 获取输出信息
		 *
		 * @return 1
		 */
		String getOutputInfo() {
			StringBuilder res = new StringBuilder();
			if (frameRate != 0) {
				res.append(" -r ").append(frameRate);
			}
			if (bitRate != 0) {
				res.append(" -b ").append(bitRate).append("M");
			}
			if (!outFormat.isEmpty()) {
				res.append(" -f ").append(outFormat);
			}
			return res.toString();
		}

		/**
		 * 设置宽度
		 *
		 * @param width 宽
		 */
		public void setWidth(int width) {
			if (width % 2 != 0) width -= 1;
			this.width = width;
		}

		/**
		 * 设置高度
		 *
		 * @param height 高
		 */
		public void setHeight(int height) {
			if (height % 2 != 0) height -= 1;
			this.height = height;
		}
	}

	/**
	 * 开始处理
	 *
	 * @param cmd 命令
	 */
	public void execCmd(String cmd, long duration, final OnEditorListener onEditorListener) {
		Log.v("ffmpeg", "cmd:" + cmd);
		cmd = "ffmpeg " + cmd;
		String[] cmds = cmd.split(" ");
		FFmpegCmd.exec(cmds, duration, new OnEditorListener() {
			@Override
			public void onSuccess() {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onEditorListener.onSuccess();
					}
				});
			}

			@Override
			public void onFailure() {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onEditorListener.onFailure();
					}
				});
			}

			@Override
			public void onProgress(final float progress) {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onEditorListener.onProgress(progress);
					}
				});
			}
		});
	}
}
