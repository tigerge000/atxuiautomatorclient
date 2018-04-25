package atx.client.adb;

import atx.client.common.ShellUtils;
import atx.client.enums.Const;
import atx.client.model.AtxDriver;
import atx.client.model.DesiredCapabilities;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//定位元素
public class Position {
	// 匹配坐标值ֵ
	private Pattern pattern = Pattern.compile("([0-9]+)");
	// 获取系统临时文件存储目录
	private String tempFile = System.getProperty("java.io.tmpdir");
	private String temp = new File(tempFile + "/dumpfile").getAbsolutePath();

	private Map<Integer, String> attrib = null;
	@SuppressWarnings("rawtypes")
	private ArrayList<HashMap> attribs = null;
	private InputStream xml = null;
	private List<UiDump> dumps = null;

	private static Position mInstance;


	private static String currentActivity = "";

	private DesiredCapabilities desiredCapabilities;

	public Position(DesiredCapabilities desiredCapabilities) {
		this.desiredCapabilities = desiredCapabilities;
		File dumpFile = new File(temp);
		if (!dumpFile.exists()) {
			dumpFile.mkdir();
		}
	}

	/**
	 * 单例实现
	 * @return
	 */
	public static Position getInstance(DesiredCapabilities desiredCapabilities){
		if(mInstance == null){
			synchronized (Position.class){
				if(mInstance == null){
					mInstance = new Position(desiredCapabilities);
				}
			}
		}
		return mInstance;
	}


	// 获取设备当前界面的控件信息，并解析uidump.xml文件
	private void uidump() {
		File directory  = new File(".");
		String path = null;
		try {
			path = directory.getCanonicalPath();
			path = path + Const.XML_PATH;
			xml = new FileInputStream(new File(path));
		} catch (Exception e){
			e.printStackTrace();
		}
		dumps = new UiDumpService().getDumps(xml);
	}

	/**
	 * 智能等待元素
	 * @param attr
	 * @param value
	 * @param wait_time
	 * @return
	 */
	public boolean waitForElement(int attr,String value,int wait_time){
		if(wait_time < 1000){
			return false;
		}
		int count_time = 0;
		while (count_time < wait_time){
			count_time += 1000;
			ElementAdb element = findElement(attr,value);
			if(element != null){
				return true;
			}
		}
		return false;
	}


	/**
	 * 通过text定位单个元素
	 *
	 * @param text
	 * @return 返回元素位置坐标
	 */
	public ElementAdb findElementByText(String text) {
		return this.findElement(ElementAttribs.TEXT, text);
	}

	/**
	 * 通过text定位多个同属性的相同元素
	 *
	 * @param text
	 * @return 返回元素位置坐标集合
	 */
	public ArrayList<ElementAdb> findElementsByText(String text) {
		return this.findElements(ElementAttribs.TEXT, text);
	}

	/**
	 * 通过resource-id定位单个元素
	 *
	 * @param resourceId
	 * @return 返回元素位置坐标
	 */
	public ElementAdb findElementById(String resourceId) {
		return this.findElement(ElementAttribs.RESOURCE_ID, resourceId);
	}

	/**
	 * 通过resource-id定位多个同属性的相同元素
	 *
	 * @param resourceId
	 * @return 返回元素位置坐标集合
	 */
	public ArrayList<ElementAdb> findElementsById(String resourceId) {
		return this.findElements(ElementAttribs.RESOURCE_ID, resourceId);
	}

	/**
	 * 通过class定位单个元素
	 *
	 * @param className
	 * @return 返回元素位置坐标
	 */
	public ElementAdb findElementByClass(String className) {
		return this.findElement(ElementAttribs.CLASS, className);
	}

	/**
	 * 通过class定位多个同属性的相同元素
	 *
	 * @param className
	 * @return 返回元素位置坐标集合
	 */
	public ArrayList<ElementAdb> findElementsByClass(String className) {
		return this.findElements(ElementAttribs.CLASS, className);
	}

	/**
	 * 通过checked定位单个元素
	 *
	 * @param checked
	 * @return 返回元素位置坐标
	 */
	public ElementAdb findElementByChecked(String checked) {
		return this.findElement(ElementAttribs.CHECKED, checked);
	}

	/**
	 * 通过checked定位多个同属性的相同元素
	 *
	 * @param checked
	 * @return 返回元素位置坐标集合
	 */
	public ArrayList<ElementAdb> findElementsByChecked(String checked) {
		return this.findElements(ElementAttribs.CHECKED, checked);
	}

	/**
	 * 通过checkable定位单个元素
	 *
	 * @param checkable
	 * @return 返回元素位置坐标
	 */
	public ElementAdb findElementByCheckable(String checkable) {
		return this.findElement(ElementAttribs.CHECKABLE, checkable);
	}

	/**
	 * 通过checkable定位多个同属性的相同元素
	 *
	 * @param checkable
	 * @return 返回元素位置坐标集合
	 */
	public ArrayList<ElementAdb> findElementsByCheckable(String checkable) {
		return this.findElements(ElementAttribs.CHECKABLE, checkable);
	}

	/**
	 * 通过content-desc定位单个元素
	 *
	 * @param contentdesc
	 * @return 返回元素位置坐标
	 */
	public ElementAdb findElementByContentdesc(String contentdesc) {
		return this.findElement(ElementAttribs.CONTENTDESC, contentdesc);
	}

	/**
	 * 通过content-desc定位多个同属性的相同元素
	 *
	 * @param contentdesc
	 * @return 返回元素位置坐标集合
	 */
	public ArrayList<ElementAdb> findElementsByContentdesc(String contentdesc) {
		return this.findElements(ElementAttribs.CONTENTDESC, contentdesc);
	}

	/**
	 * 通过clickable定位单个元素
	 *
	 * @param clickable
	 * @return 返回元素位置坐标
	 */
	public ElementAdb findElementByClickable(String clickable) {
		return this.findElement(ElementAttribs.CLICKABLE, clickable);
	}

	/**
	 * 通过clickable定位多个同属性的相同元素
	 *
	 * @param clickable
	 * @return 返回元素位置坐标集合
	 */
	public ArrayList<ElementAdb> findElementsByClickable(String clickable) {
		return this.findElements(ElementAttribs.CLICKABLE, clickable);
	}

	/**
	 * 通过text获取元素区域坐标，即bounds属性，方便截图对比功能
	 *
	 * @param text
	 * @return 返回元素区域坐标集合
	 */
	public ArrayList<int[]> getBoundsByText(String text) {
		return this.getBounds(ElementAttribs.TEXT, text);
	}

	/**
	 * 通过resource-id获取元素区域坐标，即bounds属性，方便截图对比功能
	 *
	 * @param resourceId
	 * @return 返回元素区域坐标集合
	 */
	public ArrayList<int[]> getBoundsByID(String resourceId) {
		return this.getBounds(ElementAttribs.RESOURCE_ID, resourceId);
	}

	/**
	 * 通过class获取元素区域坐标，即bounds属性，方便截图对比功能
	 *
	 * @param className
	 * @return 返回元素区域坐标集合
	 */
	public ArrayList<int[]> getBoundsByClass(String className) {
		return this.getBounds(ElementAttribs.CLASS, className);
	}

	/**
	 * 通过checked获取元素区域坐标，即bounds属性，方便截图对比功能
	 *
	 * @param checked
	 * @return 返回元素区域坐标集合
	 */
	public ArrayList<int[]> getBoundsByChecked(String checked) {
		return this.getBounds(ElementAttribs.CHECKED, checked);
	}

	/**
	 * 通过checkable获取元素区域坐标，即bounds属性，方便截图对比功能
	 *
	 * @param checkable
	 * @return 返回元素区域坐标集合
	 */
	public ArrayList<int[]> getBoundsByCheckable(String checkable) {
		return this.getBounds(ElementAttribs.CHECKABLE, checkable);
	}

	/**
	 * 通过content-desc获取元素区域坐标，即bounds属性，方便截图对比功能
	 *
	 * @param contentdesc
	 * @return 返回元素区域坐标集合
	 */
	public ArrayList<int[]> getBoundsByContentdesc(String contentdesc) {
		return this.getBounds(ElementAttribs.CONTENTDESC, contentdesc);
	}

	/**
	 * 通过clickable获取元素区域坐标，即bounds属性，方便截图对比功能
	 *
	 * @param clickable
	 * @return 返回元素区域坐标集合
	 */
	public ArrayList<int[]> getBoundsByClickable(String clickable) {
		return this.getBounds(ElementAttribs.CLICKABLE, clickable);
	}

	/**
	 * 通过resource-id获取元素text属性
	 *
	 * @param resourceId
	 * @return 返回text集合
	 */
	public ArrayList<String> getTextById(String resourceId) {
		return this.getText(ElementAttribs.RESOURCE_ID, resourceId);
	}

	/**
	 * 通过class获取元素text属性
	 *
	 * @param className
	 * @return 返回text集合
	 */
	public ArrayList<String> getTextByClass(String className) {
		return this.getText(ElementAttribs.CLASS, className);
	}

	private ElementAdb findElement(int att, String str) {

		String nowActivity = AdbDevice.getInstance(desiredCapabilities).getCurrentActivity();

		if(!currentActivity.equals(nowActivity)){
			ShellUtils.getInstance(desiredCapabilities).dumpHierarchy();
		}

		uidump();
		// 获取元素区域中心位置坐标
		CharSequence input = getAttrib(att, str).get(
				ElementAttribs.BOUNDS);

		if (input == null) {
			new TestException("未在当前界面找到元素(" + str + ")");
			return null;
		}

		Matcher mat = pattern.matcher(input);
		ArrayList<Integer> coords = new ArrayList<Integer>();
		while (mat.find()) {
			coords.add(new Integer(mat.group()));
		}

		int startX = coords.get(0);
		int startY = coords.get(1);
		int endX = coords.get(2);
		int endY = coords.get(3);

		int centerCoordsX = (endX - startX) / 2 + startX;
		int centerCoordsY = (endY - startY) / 2 + startY;

		ElementAdb element = new ElementAdb();

		element.setX(centerCoordsX);
		element.setY(centerCoordsY);

		return element;
	}

	@SuppressWarnings("rawtypes")
	private ArrayList<ElementAdb> findElements(int att, String str) {
		uidump();
		ArrayList<ElementAdb> elements = new ArrayList<ElementAdb>();
		ArrayList<HashMap> attribs = getAttribs(att, str);

		for (HashMap hashMap : attribs) {
			Matcher mat = pattern.matcher((String) hashMap
					.get(ElementAttribs.BOUNDS));
			ArrayList<Integer> coords = new ArrayList<Integer>();
			while (mat.find()) {
				coords.add(new Integer(mat.group()));
			}

			int startX = coords.get(0);
			int startY = coords.get(1);
			int endX = coords.get(2);
			int endY = coords.get(3);

			int centerCoordsX = (endX - startX) / 2 + startX;
			int centerCoordsY = (endY - startY) / 2 + startY;

			ElementAdb element = new ElementAdb();

			element.setX(centerCoordsX);
			element.setY(centerCoordsY);

			elements.add(element);
		}

		return elements;
	}

	@SuppressWarnings("rawtypes")
	private ArrayList<int[]> getBounds(int att, String str) {
		uidump();
		ArrayList<int[]> bounds = new ArrayList<int[]>();
		ArrayList<HashMap> attribs = getAttribs(att, str);

		for (HashMap hashMap : attribs) {
			Matcher mat = pattern.matcher((String) hashMap
					.get(ElementAttribs.BOUNDS));
			ArrayList<Integer> coords = new ArrayList<Integer>();
			while (mat.find()) {
				coords.add(new Integer(mat.group()));
			}

			int[] bound = new int[] { coords.get(0), coords.get(1),
					coords.get(2), coords.get(3) };

			bounds.add(bound);
		}

		return bounds;
	}

	// 获取元素text属性值ֵ
	@SuppressWarnings("rawtypes")
	private ArrayList<String> getText(int att, String str) {
		uidump();
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<HashMap> attribs = getAttribs(att, str);

		for (HashMap hashMap : attribs) {
			text.add((String) hashMap.get(ElementAttribs.TEXT));
		}

		return text;
	}

	// 获取单个元素属性值集合
	private HashMap<Integer, String> getAttrib(int att, String str) {
		attrib = new HashMap<Integer, String>();
		for (UiDump dump : dumps) {
			Boolean flag = false;
			switch (att) {
			case 0:
				flag = str.equals(dump.getText());
				break;
			case 1:
				flag = str.equals(dump.getResourceId());
				break;
			case 2:
				flag = str.equals(dump.getClassName());
				break;
			case 3:
				flag = str.equals(dump.getChecked());
				break;
			case 4:
				flag = str.equals(dump.getCheckable());
				break;
			case 5:
				flag = str.equals(dump.getContentDesc());
				break;
			case 6:
				flag = str.equals(dump.getClickable());
				break;
			default:
				break;
			}
			if (flag) {
				attrib.put(ElementAttribs.TEXT, dump.getText());
				attrib.put(ElementAttribs.RESOURCE_ID, dump.getResourceId());
				attrib.put(ElementAttribs.CLASS, dump.getClassName());
				attrib.put(ElementAttribs.CHECKED, dump.getChecked());
				attrib.put(ElementAttribs.CHECKABLE, dump.getCheckable());
				attrib.put(ElementAttribs.CONTENTDESC, dump.getContentDesc());
				attrib.put(ElementAttribs.CLICKABLE, dump.getCheckable());
				attrib.put(ElementAttribs.BOUNDS, dump.getBounds());

				break;
			}
		}

		return (HashMap<Integer, String>) attrib;
	}

	// 获取多个元素的属性值集合
	@SuppressWarnings("rawtypes")
	private ArrayList<HashMap> getAttribs(int att, String str) {
		HashMap<Integer, String> temp = null;
		attribs = new ArrayList<HashMap>();
		for (UiDump dump : dumps) {
			Boolean flag = false;
			switch (att) {
			case 0:
				flag = str.equals(dump.getText());
				break;
			case 1:
				flag = str.equals(dump.getResourceId());
				break;
			case 2:
				flag = str.equals(dump.getClassName());
				break;
			case 3:
				flag = str.equals(dump.getChecked());
				break;
			case 4:
				flag = str.equals(dump.getCheckable());
				break;
			case 5:
				flag = str.equals(dump.getContentDesc());
				break;
			case 6:
				flag = str.equals(dump.getClickable());
				break;
			default:
				break;
			}
			if (flag) {
				temp = new HashMap<Integer, String>();
				temp.put(ElementAttribs.TEXT, dump.getText());
				temp.put(ElementAttribs.RESOURCE_ID, dump.getResourceId());
				temp.put(ElementAttribs.CLASS, dump.getClassName());
				temp.put(ElementAttribs.CHECKED, dump.getChecked());
				temp.put(ElementAttribs.CHECKABLE, dump.getCheckable());
				temp.put(ElementAttribs.CONTENTDESC, dump.getContentDesc());
				temp.put(ElementAttribs.CLICKABLE, dump.getCheckable());
				temp.put(ElementAttribs.BOUNDS, dump.getBounds());

				attribs.add(temp);
			}
			temp = null;
		}

		return attribs;
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
