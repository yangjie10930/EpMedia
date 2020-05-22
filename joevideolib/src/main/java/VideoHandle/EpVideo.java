package VideoHandle;

import java.util.ArrayList;

/**
 * 视频处理类
 * Created by YangJie on 2017/5/18.
 */

public class EpVideo {

	private String videoPath;  //视频地址

	//剪辑
	private boolean isClip = false;//是否剪辑
	private float clipStart;//剪辑开始时间
	private float clipDuration;//剪辑时间

	//滤镜
	private StringBuilder filter;

	//特效
	private ArrayList<EpDraw> epPics;

	//裁剪
	private Crop mCrop;


	public EpVideo(String videoPath) {
		this.videoPath = videoPath;
		epPics = new ArrayList<>();
	}

	private StringBuilder getFilter() {
		if (filter == null || filter.toString().equals("")) {
			filter = new StringBuilder();
		} else {
			filter.append(",");
		}
		return filter;
	}

	/**
	 * 获取滤镜效果
	 *
	 * @return
	 */
	public StringBuilder getFilters() {
		return filter;
	}

	/**
	 * 获取视频路径
	 *
	 * @return
	 */
	public String getVideoPath() {
		return videoPath;
	}

	/**
	 * 获取剪辑信息
	 *
	 * @return
	 */
	public boolean getVideoClip() {
		return isClip;
	}

	/**
	 * 获取剪辑起始时间
	 *
	 * @return
	 */
	public float getClipStart() {
		return clipStart;
	}

	/**
	 * 获取剪辑持续时间
	 *
	 * @return
	 */
	public float getClipDuration() {
		return clipDuration;
	}

	/**
	 * 设置视频剪辑
	 *
	 * @param start    起始时间，单位秒
	 * @param duration 持续时间，单位秒
	 * @return
	 */
	public EpVideo clip(float start, float duration) {
		isClip = true;
		this.clipStart = start;
		this.clipDuration = duration;
		return this;
	}


	/**
	 * 设置旋转和镜像
	 *
	 * @param rotation 旋转角度(仅支持90,180,270度旋转)
	 * @param isFlip   是否镜像
	 * @return
	 */
	public EpVideo rotation(int rotation, boolean isFlip) {
		filter = getFilter();
		if (isFlip) {
			switch (rotation) {
				case 0:
					filter.append("hflip");
					break;
				case 90:
					filter.append("transpose=3");
					break;
				case 180:
					filter.append("vflip");
					break;
				case 270:
					filter.append("transpose=0");
					break;
			}
		} else {
			switch (rotation) {
				case 90:
					filter.append("transpose=2");
					break;
				case 180:
					filter.append("vflip,hflip");
					break;
				case 270:
					filter.append("transpose=1");
					break;
			}
		}
		return this;
	}

	/**
	 * 设置裁剪
	 *
	 * @param width  裁剪宽度
	 * @param height 裁剪高度
	 * @param x      起始位置X
	 * @param y      起始位置Y
	 * @return
	 */
	public EpVideo crop(float width, float height, float x, float y) {
		filter = getFilter();
		mCrop = new Crop(width,height,x,y);
		filter.append("crop=" + width + ":" + height + ":" + x + ":" + y);
		return this;
	}

	/**
	 * 获取裁剪信息
	 * @return
	 */
	public Crop getCrop(){
		return mCrop;
	}

	/**
	 * 为视频添加文字
	 *
	 * @param size  文字大小
	 * @param color 文字颜色(white,black,blue,red...)
	 * @param x     文字的x坐标
	 * @param y     文字的y坐标
	 * @param ttf   文字字体的路径
	 * @param text  添加的文字
	 * @deprecated 废弃，采用EpText参数
	 */
	@Deprecated
	public EpVideo addText(int x, int y, float size, String color, String ttf, String text) {
		filter = getFilter();
		filter.append("drawtext=fontfile=" + ttf + ":fontsize=" + size + ":fontcolor=" + color + ":x=" + x + ":y=" + y + ":text='" + text + "'");
		return this;
	}

	/**
	 * 为视频添加文字(新增可以控制显示周期)
	 * v1.0.0版本里暂停使用此方法
	 *
	 * @param epText  添加文字类
	 */
	private EpVideo addText(EpText epText) {
		filter = getFilter();
		filter.append(epText.getTextFitler());
		return this;
	}

	/**
	 * 为视频添加时间
	 *
	 * @param size  文字大小
	 * @param color 文字颜色(white,black,blue,red...)
	 * @param x     文字的x坐标
	 * @param y     文字的y坐标
	 * @param ttf   文字字体的路径
	 * @param type  时间类型(1==>hh:mm:ss,2==>yyyy-MM-dd hh:mm:ss,3==>yyyy年MM月dd日 hh时mm分ss秒)
	 */
	public EpVideo addTime(int x, int y, float size, String color, String ttf,int type){
		long time=System.currentTimeMillis()/1000;
		String  str=String.valueOf(time);
		filter = getFilter();
		String ts = "";
		switch (type){
			case 1:
				ts = "%{pts\\:localtime\\:" + str + "\\:%H\\\\\\:%M\\\\\\:%S}";
				break;
			case 2:
				ts = "%{pts\\:localtime\\:" + str + "}";
				break;
			case 3:
				ts = "%{pts\\:localtime\\:" + str + "\\:%Y\\\\年%m\\\\月%d\\\\日\n%H\\\\\\时%M\\\\\\分%S秒}";
				break;
		}
		filter.append("drawtext=fontfile=" + ttf + ":fontsize=" + size + ":fontcolor=" + color + ":x=" + x + ":y=" + y + ":text='"+ts+"'");
		return this;
	}

	/**
	 * 添加自定义滤镜效果
	 *
	 * @param ofi 命令符
	 * @return
	 */
	public EpVideo addFilter(String ofi) {
		filter = getFilter();
		filter.append(ofi);
		return this;
	}

	/**
	 * 为视频添加图片
	 *
	 * @param epDraw 添加的图片类
	 * @return
	 */
	public EpVideo addDraw(EpDraw epDraw) {
		epPics.add(epDraw);
		return this;
	}

	/**
	 * 获取添加的图片类
	 *
	 * @return
	 */
	public ArrayList<EpDraw> getEpDraws() {
		return epPics;
	}

	/**
	 * 裁剪信息类
	 */
	public class Crop {
		float width;
		float height;
		float x;
		float y;

		public Crop(float width, float height, float x, float y) {
			this.width = width;
			this.height = height;
			this.x = x;
			this.y = y;
		}

		public float getWidth() {
			return width;
		}

		public float getHeight() {
			return height;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}
	}
}
