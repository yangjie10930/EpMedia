package com.joe.epmediademo.Application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Yj on 2017/8/24.
 */

public class MyApplication extends Application {

	private static String savePath;

	@Override
	public void onCreate() {
		super.onCreate();
		choseSavePath();
		copyFilesAssets(getApplicationContext(), "Ress", savePath);
	}

	private void choseSavePath() {
		savePath = Environment.getExternalStorageDirectory().getPath() + "/EpMedia/";
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static String getSavePath() {
		return savePath;
	}

	/**
	 * 从assets目录中复制文件到本地
	 *
	 * @param context Context
	 * @param oldPath String  原文件路径
	 * @param newPath String  复制后路径
	 */
	public static void copyFilesAssets(Context context, String oldPath, String newPath) {
		try {
			String[] fileNames = context.getAssets().list(oldPath);
			if (fileNames.length > 0) {
				File file = new File(newPath);
				file.mkdirs();
				for (String fileName : fileNames) {
					copyFilesAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
				}
			} else {
				InputStream is = context.getAssets().open(oldPath);
				File ff = new File(newPath);
				if (!ff.exists()) {
					FileOutputStream fos = new FileOutputStream(ff);
					byte[] buffer = new byte[1024];
					int byteCount;
					while ((byteCount = is.read(buffer)) != -1) {
						fos.write(buffer, 0, byteCount);
					}
					fos.flush();
					is.close();
					fos.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
