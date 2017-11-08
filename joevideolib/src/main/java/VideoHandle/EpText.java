package VideoHandle;

/**
 * Created by Administrator on 2017/11/8.
 */

public class EpText {

	private String textFitler;

	/**
	 * @param x     文字起始位置X
	 * @param y     文字起始位置Y
	 * @param size  文字的大小
	 * @param color 文字的颜色
	 * @param ttf   文字的字体文件路径
	 * @param text  添加文字的内容
	 * @param time  起始结束时间(传null的时候为一直显示)
	 */
	public EpText(int x, int y, float size, Color color, String ttf, String text, Time time) {
		this.textFitler = "drawtext=fontfile=" + ttf + ":fontsize=" + size + ":fontcolor=" + color.getColor() + ":x=" + x + ":y=" + y + ":text='" + text + "'" + (time == null ? "" : time.getTime());
	}

	public String getTextFitler() {
		return textFitler;
	}

	/**
	 * 起始结束时间的类
	 */
	public static class Time {
		private String time;

		public Time(int start, int end) {
			this.time = ":enable=between(t\\," + start + "\\," + end + ")";
		}

		public String getTime() {
			return time;
		}
	}

	/**
	 * 颜色
	 */
	public enum Color {
		Red("Red"), Blue("Blue"), Yellow("Yellow"), Black("Black"), DarkBlue("DarkBlue"),
		Green("Green"), SkyBlue("SkyBlue"), Orange("Orange"), White("White"), Cyan("Cyan");
		private String color;

		Color(String color) {
			this.color = color;
		}

		public String getColor() {
			return color;
		}
	}
}
