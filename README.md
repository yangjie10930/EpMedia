[![](https://img.shields.io/badge/minSdkVersion-21-green.svg)](https://developer.android.google.cn) [![](https://img.shields.io/badge/FFmpeg-4.2.2-orange.svg)](https://ffmpeg.org/download.html#release_3.3) 
[![](https://jitpack.io/v/yangjie10930/EpMedia.svg)](https://jitpack.io/#yangjie10930/EpMedia)
#### 中文 / [English](https://github.com/yangjie10930/EpMedia/blob/master/Readme-English.md)

# EpMedia
基于FFmpeg开发的视频处理框架，简单易用，体积小，帮助使用者快速实现视频处理功能。包含以下功能：剪辑，裁剪，旋转，镜像，合并，分离，添加LOGO，添加滤镜，添加背景音乐，加速减速视频，倒放音视频。</br>

好用的话麻烦给个star,感谢您的支持与鼓励O(∩_∩)O

<a href="https://github.com/yangjie10930/EpMediaDemo" target="_blank">Demo点这里</a>   

##### V1.0.0版本更新说明
1. 更新FFmpeg至4.2.2版本，使用ndk-r21编译，采用clang工具;
1. 新增x86_64支持;
1. 移除添加文字的功能(由于依赖库编译的一些问题，该方法暂时无法使用，后续会加回，如果需要此功能，请使用v0.9.5版本);
1. 添加MediaCodec的支持。


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
compile 'com.github.yangjie10930:EpMedia:v1.0.1'
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
//参数分别是添加位置的X,Y坐标,文字的字号(单位px),文字颜色,字体文件的路径,内容，Time类为显示的起始时间和持续时间
epVideo.addText(10,10,35,"red",ttfPath,text);
epVideo.addText(new EpText(10,10,35,"red",ttfPath,text,new EpText.Time(3,5)));
```
* 添加logo
```Java
//添加图片类
//参数为图片路径,X,Y,图片的宽,高,是否是动图(仅支持png,jpg,gif图片,如果是gif图片,最后一个参数为true)
EpDraw epDraw = new EpDraw(filePath,10,10,50,50,false);
epVideo.addDraw(epDraw);
或
epVideo.addDraw(new EpDraw(filePath,10,10,50,50,false,3,5));//最后两个参数为显示的起始时间和持续时间
```
* 添加自定义滤镜
```Java
//自定义滤镜，ffmpeg命令支持的滤镜都支持
//详细效果可参考：http://blog.csdn.net/u012027644/article/details/77833484
//具体内容参见ffmpeg filter官网：http://www.ffmpeg.org/ffmpeg-filters.html
//举例 String filter = "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";//底片效果
epVideo.addFilter(filter);
```
* 处理单个视频
```Java
EpVideo epVideo = new EpVideo(url);
//输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outFile);
outputOption.width = 480;//输出视频宽，如果不设置则为原始视频宽高
outputOption.height = 360;//输出视频高度
outputOption.frameRate = 30;//输出视频帧率,默认30
outputOption.bitRate = 10;//输出视频码率,默认10
EpEditor.exec(epVideo, outputOption, new OnEditorListener());
```
* 添加背景音乐
```Java
//参数分别是视频路径，音频路径，输出路径,原始视频音量(1为100%,0.7为70%,以此类推),添加音频音量
EpEditor.music(videoPath, audioPath, outfilePath, 1, 0.7, new OnEditorListener());
```
* 分离音视频
```Java
//参数分别是视频路径，输出路径，输出类型
EpEditor.demuxer(videoPath, outfilePath,EpEditor.Format.MP3, new OnEditorListener());
```
* 视频变速
```Java
//参数分别是视频路径,输出路径,变速倍率（仅支持0.25-4倍),变速类型(VIDEO-视频(选择VIDEO的话则会屏蔽音频),AUDIO-音频,ALL-视频音频同时变速)
EpEditor.changePTS(videoPath, outfilePath, 2.0f, EpEditor.PTS.ALL, new OnEditorListener());
```
* 音视频倒放
```Java
//参数分别是视频路径,输出路径,视频是否倒放，音频是否倒放（两个都选true的话，音视频都倒放，视频ture音频false的话，输出倒放的无音频视频，视频false音频ture的话，输入倒放的音频，音频的倒放也用这个配置）
EpEditor.reverse(videoPath, outfilePath, true, true, new OnEditorListener());
```
* 视频转图片
```Java
//参数分别是视频路径,输出路径（路径用集合的形式，比如pic%03d.jpg,支持jpg和png两种图片格式）,输出图片的宽度，输出图片的高度，每秒输出图片数量（2的话就是每秒2张，0.5f的话就是每两秒一张）
EpEditor.video2pic(videoPath, outfilePath, 720, 1080, 2, new OnEditorListener());
```
* 图片转视频
```Java
//参数分别是图片集合路径,输出路径,输出视频的宽度，输出视频的高度，输出视频的帧率
EpEditor.pic2video(picPath, outfilePath, 480, 320, 30, new OnEditorListener());
```
## 多个视频处理&合并
* 合并视频（支持对要合并的视频进行其他处理操作）
```Java
ArrayList<EpVideo> epVideos = new ArrayList<>();
epVideos.add(new EpVideo(url));//视频1
epVideos.add(new EpVideo(url2));//视频2
epVideos.add(new EpVideo(url3));//视频3
//输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outFile);
outputOption.width = 480;//输出视频宽，默认480
outputOption.height = 360;//输出视频高度,默认360
outputOption.frameRate = 30;//输出视频帧率,默认30
outputOption.bitRate = 10;//输出视频码率,默认10
EpEditor.merge(epVideos, outputOption, new OnEditorListener());
```
* 无损合并视频(对视频格式严格，需要分辨率，帧率，码率都相同，不支持对要合并的视频进行其他处理操作，该方法合并速度很快，另：两段同格式的音频拼接也可使用该方法)
```Java
ArrayList<EpVideo> epVideos = new ArrayList<>();
epVideos.add(new EpVideo(url));//视频1
epVideos.add(new EpVideo(url2));//视频2
epVideos.add(new EpVideo(url3));//视频3
EpEditor.mergeByLc(epVideos, new EpEditor.OutputOption(outFile), new OnEditorListener());
```
## 自定义命令
* 输入ffmpeg命令即可（起头不用输ffmpeg,例子"-i input.mp4 -ss 0 -t 5 output.mp4",第二个参数为视频长度，单位微秒，可以填0）
```Java
EpEditor.execCmd("", 0, new OnEditorListener() {
	@Override
	public void onSuccess() {

	}

	@Override
	public void onFailure() {

	}

	@Override
	public void onProgress(float progress) {

	}
});
```
