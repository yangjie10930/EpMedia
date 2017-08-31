package Jni;

import android.media.MediaExtractor;
import android.media.MediaFormat;

/**
 * Created by 杨杰 on 2017/3/21.
 * 获取音频或视频的信息
 */

public class VideoUitls {

	private VideoUitls() {
	}

	/**
	 * 获取视频信息
	 *
	 * @param url
	 * @return
	 */
	public static long getDuration(String url) {
		try {
			MediaExtractor mediaExtractor = new MediaExtractor();
			mediaExtractor.setDataSource(url);
			int videoExt = TrackUtils.selectVideoTrack(mediaExtractor);
			MediaFormat mediaFormat = mediaExtractor.getTrackFormat(videoExt);
			long res = mediaFormat.containsKey(MediaFormat.KEY_DURATION) ? mediaFormat.getLong(MediaFormat.KEY_DURATION) : 0;//时长
			mediaExtractor.release();
			return res;
		} catch (Exception e) {
			return 0;
		}
	}
}
