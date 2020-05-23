[![](https://img.shields.io/badge/minSdkVersion-21-green.svg)](https://developer.android.google.cn) [![](https://img.shields.io/badge/FFmpeg-4.2.2-orange.svg)](https://ffmpeg.org/download.html#release_3.3) 
[![](https://jitpack.io/v/yangjie10930/EpMedia.svg)](https://jitpack.io/#yangjie10930/EpMedia)
#### English / [中文](https://github.com/yangjie10930/EpMedia/blob/master/README.md)

# EpMedia
The video processing framework based on FFmpeg developed on Android is simple, easy to use, and small in size, helping users quickly realize video processing functions. Contains the following functions: editing, cropping, rotating, mirroring, merging, separating, variable speed, adding LOGO, adding filters, adding background music, accelerating and decelerating video, rewinding audio and video. 

<a href="https://github.com/yangjie10930/EpMediaDemo" target="_blank">Demo</a>   

##### V1.0.0 version update instructions
1. Update FFmpeg to version 4.2.2, compile with ndk-r21, use clang tool;
1. Added x86_64 support;
1. Removed the function of adding text (due to some problems of dependent library compilation, this method is temporarily unavailable, and will be added back later, if you need this function, please use v0.9.5 version);
1. Add MediaCodec support.


## How to use:
* Add in build.gradle:
```Java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
* Add gradle dependency:
```Java
compile 'com.github.yangjie10930:EpMedia:v1.0.1'
```
## Single video processing:
* create pending video:
```Java
EpVideo epVideo = new EpVideo(url);
```
* clip
```Java
//The first parameter is the start time of the clip, the second parameter is the duration, in seconds
epVideo.clip(1,2);//From the first second, edit for two seconds
```
* crop
```Java
//The parameters are the crop width, height, starting position X, starting position Y
epVideo.crop(480,360,0,0);
```
* rotate and mirror
```Java
//The first parameter is the rotation angle, the second parameter is whether to mirror, only supports 90,180,270 degree rotation
epVideo.rotation(90,true);
```
* add logo
```Java
// Add picture class
// The parameter is the image path, X, Y, the width and height of the image, whether it is a moving image (only png, jpg, gif images are supported, if it is a gif image, the last parameter is true)
EpDraw epDraw = new EpDraw(filePath,10,10,50,50,false);
epVideo.addDraw(epDraw);
```
or
```Java
epVideo.addDraw(new EpDraw(filePath,10,10,50,50,false,3,5));//The last two parameters are the displayed start time and duration
```
* Add custom filter
```Java
// Custom filters, all filters supported by ffmpeg command are supported
// For detailed results, please refer to: http://blog.csdn.net/u012027644/article/details/77833484
// For details, please refer to ffmpeg official website：http://www.ffmpeg.org/ffmpeg-filters.html
//Examples: String filter = "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";
epVideo.addFilter(filter);
```
* Processing a single video
```Java
EpVideo epVideo = new EpVideo(url);
//Output options, the parameter is the output file path (currently only supports mp4 format output)
EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outFile);
outputOption.width = 480;//The width and height of the output video, if not set, the original video width and height
outputOption.height = 360;//Output video height
outputOption.frameRate = 30;//frame rate, default 30
outputOption.bitRate = 10;//bit rate, default 10
EpEditor.exec(epVideo, outputOption, new OnEditorListener());
```
* Add background music
```Java
//The parameters are video path, audio path, output path, original video volume (1 is 100%, 0.7 is 70%, and so on), add audio volume
EpEditor.music(videoPath, audioPath, outfilePath, 1, 0.7, new OnEditorListener());
```
* Separate audio and video
```Java
//The parameters are the video path, output path, and output type
EpEditor.demuxer(videoPath, outfilePath,EpEditor.Format.MP3, new OnEditorListener());
```
* Change video playback speed
```Java
//The parameters are video path, output path, variable speed ratio (only supports 0.25-4 times), variable speed type (VIDEO-video (if VIDEO is selected, audio will be shielded), AUDIO-audio, ALL-video audio and variable speed)
EpEditor.changePTS(videoPath, outfilePath, 2.0f, EpEditor.PTS.ALL, new OnEditorListener());
```
* Rewind video
```Java
//The parameters are video path, output path, whether the video is reversed, and whether the audio is reversed (if both are true, the audio and video are reversed, if the video ture audio is false, the output is reversed without audio video, video false audio ture If it is, input the audio of the reverse playback, and the audio reverse playback also uses this configuration)
EpEditor.reverse(videoPath, outfilePath, true, true, new OnEditorListener());
```
* Video to image
```Java
//The parameters are the video path, the output path (the path is in the form of a collection, such as pic% 03d.jpg, supports both jpg and png image formats), the width of the output picture, the height of the output picture, and the number of output pictures per second (if 2 It ’s 2 frames per second, if 0.5f, it ’s one frame every two seconds.)
EpEditor.video2pic(videoPath, outfilePath, 720, 1080, 2, new OnEditorListener());
```
* Image to video
```Java
//The parameters are picture collection path, output path, output video width, output video height, output video frame rate
EpEditor.pic2video(picPath, outfilePath, 480, 320, 30, new OnEditorListener());
```
## Multiple video processing & merging
* Merge video (support other processing operations on the video to be merged)
```Java
ArrayList<EpVideo> epVideos = new ArrayList<>();
epVideos.add(new EpVideo(url));//Video 1
epVideos.add(new EpVideo(url2));//Video 2
epVideos.add(new EpVideo(url3));//Video 3
//Output options, the parameter is the output file path (currently only supports mp4 format output)
EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outFile);
outputOption.width = 480;//Output video width, default 480
outputOption.height = 360;//Output video height, default 360
outputOption.frameRate = 30;//Output video frame rate, default 30
outputOption.bitRate = 10;//Output video bit rate, default 10
EpEditor.merge(epVideos, outputOption, new OnEditorListener());
```
* Lossless merged video (strict on the video format, requiring the same resolution, frame rate, and bit rate. It does not support other processing operations on the video to be merged. The method of merging is very fast. Another: two pieces of audio in the same format are also spliced. (This method can be used)
```Java
ArrayList<EpVideo> epVideos = new ArrayList<>();
epVideos.add(new EpVideo(url));//Video 1
epVideos.add(new EpVideo(url2));//Video 2
epVideos.add(new EpVideo(url3));//Video 3
EpEditor.mergeByLc(epVideos, new EpEditor.OutputOption(outFile), new OnEditorListener());
```
## Custom commands
* Enter the ffmpeg command (you don't need to input ffmpeg at the beginning, for example "-i input.mp4 -ss 0 -t 5 output.mp4", the second parameter is the video length, the unit is microseconds, you can fill in 0)
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
