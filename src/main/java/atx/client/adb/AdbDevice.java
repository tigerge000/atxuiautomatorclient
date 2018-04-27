package atx.client.adb;

import atx.client.common.OkHttpClientMethod;
import atx.client.common.ShellUtils;
import atx.client.enums.AndroidKeyCode;
import atx.client.enums.Const;
import atx.client.model.AtxDriver;
import atx.client.model.DesiredCapabilities;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author xuxu, 274925460@qq.com
 *
 */
public class AdbDevice {

	private static String tempFile = System.getProperty("java.io.tmpdir");

	private static Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+)");

	private static Pattern STRING_PATTERN = Pattern.compile("([a-zA-Z0-9.]+/.[a-zA-Z0-9.]+)");

	private static Pattern PID_PATTERN = Pattern.compile("([\" \"][0-9]+)");

	private static AdbDevice mInstance;

	private DesiredCapabilities desiredCapabilities;

	private ShellUtils shellUtils;

	public AdbDevice(DesiredCapabilities desiredCapabilities) {
		this.desiredCapabilities = desiredCapabilities;
		shellUtils = ShellUtils.getInstance(desiredCapabilities);
	}

	/**
	 * 单例实现
	 * @return
	 */
	public static AdbDevice getInstance(DesiredCapabilities desiredCapabilities){
		if(mInstance == null){
			synchronized (AdbDevice.class){
				if(mInstance == null){
					mInstance = new AdbDevice(desiredCapabilities);
				}
			}
		}
		return mInstance;
	}


	/**
	 * 获取设备名称
	 * @return
	 */
	public String getDeviceName(){

		String deviceName = shellUtils.shellPost("getprop ro.product.model");

		return deviceName;
	}

	/**
	 * 获取设备的id号
	 *
	 * @return 返回设备id号
	 */
	public String getDeviceId() {
		String serialno = shellUtils.shellPost("getprop ro.boot.serialno");
		return serialno;
	}

	/**
	 * 获取设备中Android的版本号，如4.4.2
	 *
	 * @return 返回Android版本号
	 */
	public String getAndroidVersion() {
		String androidVersion = shellUtils.shellPost("getprop ro.build.version.release");
		return androidVersion;
	}

	/**
	 * 获取设备中SDK的版本号
	 *
	 * @return 返回SDK版本号
	 */
	public int getSdkVersion() {

		String sdkVersion = shellUtils.shellPost("getprop ro.build.version.sdk");

		return Integer.valueOf(sdkVersion.split("[^0-9]")[0]);
	}

	/**
	 * 获取设备屏幕的分辨率
	 *
	 * @return 返回分辨率数组
	 */
	public int[] getScreenResolution() {
		ArrayList<Integer> out = ReUtils.matchInteger(NUMBER_PATTERN, shellUtils.shellPost("dumpsys display | grep PhysicalDisplayInfo"));

		int[] resolution = new int[] { out.get(0), out.get(1) };

		return resolution;
	}

	/**
	 * 返回设备电池电量
	 *
	 * @return 返回电量数值
	 */
	public int getBatteryLevel() {
		String out = shellUtils.shellPost("dumpsys battery | grep level");

		int level = new Integer(out.split(": ")[1].split("[^0-9]")[0]);

		return level;
	}

	/**
	 * 返回设备电池温度
	 *
	 * @return 返回温度数值
	 */
	public double getBatteryTemp() {
		String out = shellUtils.shellPost("dumpsys battery | grep temperature");

		double temp = new Integer(out.split(": ")[1].split("[^0-9]")[0]) / 10.0;

		return temp;
	}

	/**
	 * 获取电池充电状态: 1 : BATTERY_STATUS_UNKNOWN, 未知状态 2 : BATTERY_STATUS_CHARGING,
	 * 充电状态 3 : BATTERY_STATUS_DISCHARGING, 放电状态 4 :
	 * BATTERY_STATUS_NOT_CHARGING, 未充电 5 : BATTERY_STATUS_FULL, 充电已满
	 *
	 * @return 返回状态数值
	 */
	public int getBatteryStatus() {
		String out = shellUtils.shellPost("dumpsys battery | grep status");

		int status = 100;

		if(StringUtils.isNotEmpty(out)) {
			status = new Integer(out.split(":")[1].split("[^0-9]")[1]);
		}

		return status;
	}

	/**
	 * 获取设备上当前界面的package和activity
	 *
	 * @return 返回package/activity
	 */
	public String getFocusedPackageAndActivity() {
		ArrayList<String> component = ReUtils.matchString(STRING_PATTERN, shellUtils.shellPost("dumpsys input | grep FocusedApplication"));

		// 会有FocusedApplication: <null>情况发生
		if (component.isEmpty()) {
			return ReUtils
					.matchString(STRING_PATTERN,
							shellUtils.shellPost("dumpsys window w | grep \\/ | grep name="))
					.get(0);
		}

		return component.get(0);
	}

	/**
	 * 获取设备上当前界面的包名
	 *
	 * @return 返回包名
	 */
	public String getCurrentPackageName() {
		return getFocusedPackageAndActivity().split("/")[0];
	}

	/**
	 * 获取设备上当前界面的activity
	 *
	 * @return 返回activity名
	 */
	public String getCurrentActivity() {
		return getFocusedPackageAndActivity().split("/")[1];
	}

	/**
	 *
	 * @param packageName
	 *            应用对应的包名
	 * @return 返回pid值
	 */
	public int getPid(String packageName) {
		Pattern pattern = PID_PATTERN;
		ArrayList<Integer> num = ReUtils.matchInteger(pattern, shellUtils.shellPost("ps | grep -w " + packageName));

		if (num.isEmpty()) {
			System.out.println("应用包名不存在或者进程未开启...");
		}

		return num.get(0);
	}

	/**
	 *
	 * @param pid
	 *            进程的pid值
	 * @return 进程被杀死，返回true，否则返回false
	 */
	public boolean killProcess(int pid) {
		String out = shellUtils.shellPost("kill " + pid);

		if (out.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 退出当前应用
	 *
	 */
	public void quitCurrentApp() {
		shellUtils.shellPost("am force-stop " + getCurrentPackageName());
	}

	/**
	 * 重置当前应用，清除当前应用的数据且重启该应用
	 *
	 */
	public void resetApp() {
		String component = getFocusedPackageAndActivity();
		clearAppDate(getCurrentPackageName());
		startActivity(component);

	}

	/**
	 * 获取设备中的系统应用列表
	 *
	 * @return 返回系统应用列表
	 */
	public ArrayList<String> getSystemAppList() {
		ArrayList<String> sysApp = new ArrayList<String>();
//		Process ps = ShellUtils.shell("pm list packages -s");
//		StringBuilder sb = new StringBuilder();
//		BufferedReader br = ShellUtils.shellOut(ps);

//		String line;
//		try {
//			while ((line = br.readLine()) != null) {
//				sb.append(line + System.getProperty("line.separator"));
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		Pattern pattern = Pattern.compile(("[a-z]+:[a-zA-Z0-9.]+"));
		ArrayList<String> result = ReUtils.matchString(pattern, shellUtils.shellPost("pm list packages -s"));

		for (String string : result) {
			sysApp.add(string.split(":")[1]);
		}

		return sysApp;
	}

	/**
	 * 获取设备中的第三方应用列表
	 *
	 * @return 返回第三方应用列表
	 */
	public ArrayList<String> getThirdAppList() {
		ArrayList<String> thirdApp = new ArrayList<String>();
//		Process ps = ShellUtils.shell("pm list packages -3");
//		StringBuilder sb = new StringBuilder();
//		BufferedReader br = ShellUtils.shellOut(ps);

//		String line;
//		try {
//			while ((line = br.readLine()) != null) {
//				sb.append(line + System.getProperty("line.separator"));
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		Pattern pattern = Pattern.compile(("[a-z]+:[a-zA-Z0-9.]+"));
		ArrayList<String> result = ReUtils.matchString(pattern, shellUtils.shellPost("pm list packages -3"));

		for (String string : result) {
			thirdApp.add(string.split(":")[1]);
		}

		return thirdApp;
	}

	/**
	 * 获取启动应用所花的时间
	 *
	 * @param component
	 *            package/activity
	 * @return 返回时间值
	 */
	public int getAppStartTotalTime(String component) {
		String out = shellUtils.shellPost("am start -W " + component + " | grep TotalTime");

		return new Integer(out.split(": ")[1]);
	}



	/**
	 * 判断应用是否已经安装
	 *
	 * @param packageName
	 *            应用的包名
	 * @return 已安装，返回true，否则返回false
	 */
	public boolean isInstalled(String packageName) {
		if (getThirdAppList().contains(packageName) || getSystemAppList().contains(packageName)) {
			return true;
		}
		return false;
	}

	/**
	 * 卸载指定应用
	 *
	 * @param packageName
	 *            应用包名，非apk名
	 */
	public void removeApp(String packageName) {
		shellUtils.shellPost("uninstall " + packageName);
	}

	/**
	 * 清除应用的用户数据
	 *
	 * @param packageName
	 *            应用的包名
	 * @return 清楚成功返回true, 否则返回false
	 */
	public boolean clearAppDate(String packageName) {
		if (shellUtils.shellPost("pm clear " + packageName).equals("Success")) {
			return true;
		}
		return false;
	}

	/**
	 * 重启设备
	 */
	public void reboot() {
		shellUtils.shellPost("reboot");
	}

	/**
	 * 重启设备进入fastboot模式
	 */
	public void fastboot() {
		shellUtils.shellPost("reboot bootloader");
	}

	/**
	 * 执行shell命令
	 *
	 * @param command
	 *            shell命令
	 * @return 返回执行命令后输出的内容
	 */
	public String shell(String command) {
		return shellUtils.shellPost(command);
	}

	/**
	 * 启动一个应用
	 *
	 * @param component
	 *            应用包名加主类名，packageName/Activity
	 */
	public void startActivity(String component) {
		shellUtils.shellPost("am start -n " + component);
	}

	/**
	 * 使用默认浏览器打开一个网页
	 *
	 * @param url
	 *            网页地址
	 */
	public void startWebpage(String url) {
		shellUtils.shellPost("am start -a android.intent.action.VIEW -d " + url);
	}

	/**
	 * 使用拨号器拨打号码
	 *
	 * @param number
	 *            电话号码
	 */
	public void callPhone(int number) {
		shellUtils.shellPost("am start -a android.intent.action.CALL -d tel:" + number);
	}

	/**
	 * 发送一个按键事件
	 *
	 * @param keycode
	 *            键值
	 */
	public void sendKeyEvent(int keycode) {
		shellUtils.shellPost("input keyevent " + keycode);
		sleep(500);
	}

	/**
	 * 发送一个点击事件
	 *
	 * @param x
	 *            x坐标
	 * @param y
	 *            y坐标
	 */
	public void click(int x, int y) {
		shellUtils.shellPost("input tap " + x + " " + y);
		sleep(500);
	}

	/**
	 * 发送一个点击事件
	 *
	 * @param x
	 *            x小于1，自动乘以分辨率转换为实际坐标，大于1，当做实际坐标处理
	 * @param y
	 *            y小于1，自动乘以分辨率转换为实际坐标，大于1，当做实际坐标处理
	 */
	public void click(double x, double y) {
		double[] coords = ratio(x, y);
		shellUtils.shellPost("input tap " + coords[0] + " " + coords[1]);
		sleep(500);
	}

	/**
	 * 发送一个点击事件
	 *
	 * @param e
	 *            元素对象
	 */
	public void click(ElementAdb e) {
		shellUtils.shellPost("input tap " + e.getX() + " " + e.getY());
		sleep(500);
	}

	/**
	 * 发送一个滑动事件
	 *
	 * @param startX
	 *            起始x坐标
	 * @param startY
	 *            起始y坐标
	 * @param endX
	 *            结束x坐标
	 * @param endY
	 *            结束y坐标
	 * @param ms
	 *            持续时间
	 */
	public void swipe(int startX, int startY, int endX, int endY, long ms) {

		String param = startX + "," + startY + "," + endX + "," + endY + "," + ms;
		writeScript("Drag(" + param + ")");
		runMonkey();

		/*
		 * if (getSdkVersion() < 19) { ShellUtils.shell("input swipe " + startX
		 * + " " + startY + " " + endX + " " + endY); } else { ShellUtils.shell(
		 * "input swipe " + startX + " " + startY + " " + endX + " " + endY +
		 * " " + ms); }
		 */

		sleep(500);
	}

	/**
	 * 发送一个滑动事件
	 *
	 * @param startX
	 *            起始x坐标
	 * @param startY
	 *            起始y坐标
	 * @param endX
	 *            结束x坐标
	 * @param endY
	 *            结束y坐标
	 * @param ms
	 *            持续时间
	 */
	public void swipe(double startX, double startY, double endX, double endY, long ms) {

		double[] coords = ratio(startX, startY, endX, endY);
		if (getSdkVersion() < 19) {
			shellUtils.shellPost("input swipe " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3]);
		} else {
			shellUtils.shellPost("input swipe " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + ms);
		}

		sleep(500);
	}

	/**
	 * 发送一个滑动事件
	 *
	 * @param e1
	 *            起始元素
	 * @param e2
	 *            终点元素
	 * @param ms
	 *            持续时间
	 */
	public void swipe(ElementAdb e1, ElementAdb e2, long ms) {
		String param = e1.getX() + "," + e1.getY() + "," + e2.getX() + "," + e2.getY() + "," + ms;
		writeScript("Drag(" + param + ")");
		runMonkey();

		/*
		 * if (getSdkVersion() < 19) { ShellUtils.shell("input swipe " +
		 * e1.getX() + " " + e1.getY() + " " + e2.getX() + " " + e2.getY()); }
		 * else { ShellUtils.shell("input swipe " + e1.getX() + " " + e1.getY()
		 * + " " + e2.getX() + " " + e2.getY() + " " + ms); }
		 */

		sleep(500);
	}

	/**
	 * 左滑屏幕
	 */
	public void swipeToLeft() {
		swipe(0.8, 0.5, 0.2, 0.5, 500);
	}

	/**
	 * 右滑屏幕
	 */
	public void swipeToRight() {
		swipe(0.2, 0.5, 0.8, 0.5, 500);
	}

	/**
	 * 上滑屏幕
	 */
	public void swipeToUp() {
		swipe(0.5, 0.7, 0.5, 0.3, 500);
	}

	/**
	 * 下滑屏幕
	 */
	public void swipeToDown() {
		swipe(0.5, 0.3, 0.5, 0.7, 500);
	}

	/**
	 * 发送一个长按事件
	 *
	 * @param x
	 *            x坐标
	 * @param y
	 *            y坐标
	 */
	public void longPress(int x, int y) {
		String param = "PressAndHold(" + x + "," + y + ",1500)";
		writeScript(param);
		runMonkey();
	}

	/**
	 * 发送一个长按事件
	 *
	 * @param x
	 *            x坐标
	 * @param y
	 *            y坐标
	 */
	public void longPress(double x, double y) {
		swipe(x, y, x, y, 1500);
	}

	/**
	 * 发送一个长按事件
	 *
	 * @param e
	 *            元素对象
	 */
	public void longPress(ElementAdb e) {
		String param = "PressAndHold(" + e.getX() + "," + e.getY() + ",1500)";
		writeScript(param);
		runMonkey();
	}

	/**
	 * 缩放事件
	 *
	 * @param startX1
	 *            第一起始点x坐标
	 * @param startY1
	 *            第一起始点y坐标
	 * @param endX1
	 *            第一终点x坐标
	 * @param endY1
	 *            第一终点y坐标
	 * @param startX2
	 *            第二起始点x坐标
	 * @param startY2
	 *            第二起始点y坐标
	 * @param endX2
	 *            第二终点x坐标
	 * @param endY2
	 *            第二终点y坐标
	 * @param ms
	 *            持续时间
	 */
	public void pinchZoom(int startX1, int startY1, int endX1, int endY1, int startX2, int startY2, int endX2,
			int endY2, long ms) {
		String param = startX1 + "," + startY1 + "," + endX1 + "," + endY1 + "," + startX2 + "," + startY2 + "," + endX2
				+ "," + endY2 + "," + ms;
		writeScript("PinchZoom(" + param + ")");
		runMonkey();
	}

	/**
	 * 发送一段文本，只支持英文，多个空格视为一个空格
	 *
	 * @param text
	 *            英文文本
	 */
	public void sendText(String text) {
		String[] str = text.split(" ");
		ArrayList<String> out = new ArrayList<String>();
		for (String string : str) {
			if (!string.equals("")) {
				out.add(string);
			}
		}

		int length = out.size();
		for (int i = 0; i < length; i++) {
			shellUtils.shellPost("input text " + out.get(i));
			sleep(100);
			if (i != length - 1) {
				sendKeyEvent(AndroidKeyCode.SPACE);
			}
		}
	}

	/**
	 * 清除文本
	 *
	 * @param text
	 *            清除文本框中的text
	 */
	public void clearText(String text) {
		int length = text.length();
		for (int i = length; i > 0; i--) {
			sendKeyEvent(AndroidKeyCode.BACKSPACE);
		}
	}

	private double[] ratio(double x, double y) {
		int[] display = getScreenResolution();
		double[] coords = new double[2];

		if (x < 1) {
			coords[0] = display[0] * x;
		} else {
			coords[0] = x;
		}

		if (y < 1) {
			coords[1] = display[1] * y;
		} else {
			coords[1] = y;
		}

		return coords;
	}

	private double[] ratio(double startX, double startY, double endX, double endY) {
		int[] display = getScreenResolution();
		double[] coords = new double[4];

		if (startX < 1) {
			coords[0] = display[0] * startX;
		} else {
			coords[0] = startX;
		}

		if (startY < 1) {
			coords[1] = display[1] * startY;
		} else {
			coords[1] = startY;
		}

		if (endX < 1) {
			coords[2] = display[0] * endX;
		} else {
			coords[2] = endX;
		}

		if (endY < 1) {
			coords[3] = display[1] * endY;
		} else {
			coords[3] = endY;
		}

		return coords;
	}

	private void runMonkey() {
		shell("monkey -f /data/local/tmp/monkey.txt 1");
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void writeScript(String param) {
		File file = null;
		try {
			file = createFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("count= 1\n").append("speed= 1.0\n").append("start data >>\n").append(param);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(sb.toString().getBytes("utf-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		shellUtils.shellPost("push " + file.getAbsolutePath() + " /data/local/tmp");
	}

	private File createFile() throws IOException {
		File file = new File(tempFile + "/monkey.txt");
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		} else {
			file.createNewFile();
		}

		return file;
	}
}
