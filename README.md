# EpMedia
基于FFmpeg开发的视频处理框架，简单易用，体积小，帮助使用者快速实现视频处理功能。包含以下功能：剪辑，裁剪，旋转，镜像，合并，分离，转码，添加LOGO，添加滤镜，添加背景音乐。</br>

目前还在完善和修复一些bug,如果使用中遇到问题请联系我:yangjie10930@sina.cn。
## 使用方法:
* build.gradle里添加:
```Java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
* 添加gradle依赖:
```Java
compile 'com.github.yangjie10930:EpMedia:v0.3'
```
## 单个视频处理:
* 创建待处理视频:
```Java
EpVideo epVideo = new EpVideo(url);
```
* 剪辑
```Java
//一个参数为剪辑的起始时间，第二个参数为持续时间,单位：秒
epVideo.clip(1,2);//从第一秒开始，剪辑两秒
```
* 裁剪
```Java
//参数分别是裁剪的宽，高，起始位置X，起始位置Y
epVideo.crop(480,360,0,0);
```
* 旋转和镜像
```Java
//第一个参数为旋转角度，第二个参数为是否镜像，仅支持90,180,270度旋转
epVideo.rotation(90,true);
```
* 添加文字
```Java
//参数分别是添加位置的X,Y坐标,文字的字号(单位px),文字颜色,字体文件的路径,内容
epVideo.addText(10,10,35,"red",ttfPath,text);
```
* 添加logo
```Java
//添加图片类
//参数为图片路径,X,Y,图片的宽,高,是否是动图(仅支持png,jpg,gif图片,如果是gif图片,最后一个参数为true)
EpDraw epDraw = new EpDraw(filePath,10,10,50,50,false);
epVideo.addDraw(epDraw);
```
* 添加自定义滤镜
```Java
//自定义滤镜，ffmpeg命令支持的滤镜都支持
//具体内容参见ffmpeg filter官网：http://www.ffmpeg.org/ffmpeg-filters.html
//举例 String filter = "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";//底片效果
epVideo.addFilter(filter);
```
* 处理单个视频
```Java
EpVideo epVideo = new EpVideo(url);
EpEditor epEditor = new EpEditor(this);
//输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outFile);
outputOption.width = 480;//输出视频宽，如果不设置则为原始视频宽高
outputOption.height = 360;//输出视频高度
outputOption.frameRate = 30;//输出视频帧率,默认30
outputOption.bitRate = 10;//输出视频码率,默认10
epEditor.exec(epVideo, outputOption, new OnEditorListener() {
			@Override
			public void onSuccess() {
				
			}

			@Override
			public void onFailure() {

			}
});
```
* 添加背景音乐
```Java
EpEditor epEditor = new EpEditor(this);
//参数分别是视频路径，音频路径，输出路径,原始视频音量(1为100%,0.7为70%,以此类推),添加音频音量
epEditor.music(videoPath, audioPath, outfilePath, 1, 0.7, new OnEditorListener() {
			@Override
			public void onSuccess() {
				
			}

			@Override
			public void onFailure() {

			}
});
```
## 多个视频处理&合并
* 合并视频
```Java
ArrayList<EpVideo> epVideos = new ArrayList<>();
epVideos.add(new EpVideo(url));//视频1
epVideos.add(new EpVideo(url2));//视频2
epVideos.add(new EpVideo(url3));//视频3
EpEditor epEditor = new EpEditor(this);
//输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outFile);
outputOption.width = 480;//输出视频宽，默认480
outputOption.height = 360;//输出视频高度,默认360
outputOption.frameRate = 30;//输出视频帧率,默认30
outputOption.bitRate = 10;//输出视频码率,默认10
epEditor.merge(epVideos, outputOption, new OnEditorListener() {
	@Override
	public void onSuccess() {

	}

	@Override
	public void onFailure() {

	}
});
```
* 无损合并视频(对视频格式严格，需要分辨率，帧率，码率都相同)
```Java
ArrayList<EpVideo> epVideos = new ArrayList<>();
epVideos.add(new EpVideo(url));//视频1
epVideos.add(new EpVideo(url2));//视频2
epVideos.add(new EpVideo(url3));//视频3
EpEditor epEditor = new EpEditor(this);
epEditor.mergeByLc(epVideos, new EpEditor.OutputOption(outFile), new OnEditorListener() {
		@Override
		public void onSuccess() {

		}

		@Override
		public void onFailure() {

		}
});
```
