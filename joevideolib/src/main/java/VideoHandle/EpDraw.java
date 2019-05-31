package VideoHandle;

import android.support.annotation.IntRange;

/**
 * 添加特效类
 * Created by YangJie on 2017/5/23.
 */

public class EpDraw {

	private String picPath;//图片路径
	private int picX;//图片x的位置
	private int picY;//图片y的位置
	private float picWidth;//图片的宽
	private float picHeight;//图片的高
	private boolean isAnimation;//是否是动图
	private int angle=0;//图片的旋转角度
	private String time = "";//起始结束时间

	private String picFilter;//图片滤镜

	public EpDraw(String picPath, int picX, int picY, float picWidth, float picHeight, boolean isAnimation) {
		this.picPath = picPath;
		this.picX = picX;
		this.picY = picY;
		this.picWidth = picWidth;
		this.picHeight = picHeight;
		this.isAnimation = isAnimation;
	}

	public EpDraw(String picPath, int picX, int picY, float picWidth, float picHeight, boolean isAnimation,int start,int end) {
		this(picPath, picX, picY, picWidth, picHeight, isAnimation);
		time = ":enable=between(t\\," + start + "\\," + end + ")";
	}

	public EpDraw(String picPath, int picX, int picY, float picWidth, float picHeight, boolean isAnimation, int start,int end,@IntRange(from = 0,to = 360) int angle) {
		this(picPath, picX, picY, picWidth, picHeight, isAnimation,start,end);
		this.angle = angle;
	}

	public int getAngle() {
		return angle;
	}

	public String getPicPath() {
		return picPath;
	}

	public int getPicX() {
		return picX;
	}

	public int getPicY() {
		return picY;
	}

	public float getPicWidth() {
		return picWidth;
	}

	public float getPicHeight() {
		return picHeight;
	}

	public boolean isAnimation() {
		return isAnimation;
	}

	public String getPicFilter() {
		return picFilter == null ? "" : (picFilter + ",");
	}

	public String getTime() {
		return time;
	}

	public void setPicFilter(String picFilter) {
		this.picFilter = picFilter;
	}
}
