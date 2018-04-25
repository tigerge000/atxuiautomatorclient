基于@codeskyblue
https://github.com/openatx/uiautomator2
开源的atx app自动化解决方案，开发的java版atx-client

### 初始化:

```
@Before
public void setUp() throws Exception{
DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
desiredCapabilities.setPackageName("com.netease.cloudmusic");
desiredCapabilities.setRemoteHost("192.168.2.81");
driver.initDriver(desiredCapabilities);
}
```


### 目前元素查找实现以下方法:

#### elementByName
```
driver.elementByName("私人FM").click();
```
#### elementByDesc

```
driver.elementByDesc("转到上一层级").click();
```

#### elementById

```
driver.elementById("com.netease.cloudmusic:id/search_src_text").sendKeys(keys);
```
#### elementByMult
一个元素多个属性定位

```
Map<String,Object> songParams = new HashMap<String,Object>();
songParams.put(MaskNum.TEXT_STARTS_WITH.getDes(),"搜索");
songParams.put(MaskNum.CLASS_NAME.getDes(),"android.widget.TextView");
driver.elementByMult((songParams)).click();
```

#### elementByXpath
在原方法不支持xpath基础上增加 xpath的支持
```
String xpath = "//*[@resource-id=\"com.netease.cloudmusic:id/gt\"]/*[@class=\"android.widget.LinearLayout\"][4]/*[@class=\"android.widget.LinearLayout\"][1]/*[@class=\"android.widget.RelativeLayout\"][1]";
driver.elementByXpath(xpath).click();
```

#### 获取当前activity
```
driver.getCurrentActivity()
```

#### 获取APP activity的xml结构

```
driver.dumpHierarchy();
```
持续更新中....

#### Push and pull files（未完成）

#### Key Events（已完成）

```
driver.press(KeyEventEnum.VOLUME_DWON.getValue());
```

#### 截图

```
String fileName = "/Users/huqingen/Desktop/Finger/Git/test/atxuiautomatorclient/picture/1.jpg";
driver.takeScreenshot(fileName);
```

### 支持adb命令方法(也支持元素查找及点击)

```
private DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

@Before
public void setUp() throws Exception{

desiredCapabilities.setPackageName("com.tuotuo.solo");
desiredCapabilities.setRemoteHost("192.168.2.81");

driver.initDriver(desiredCapabilities);
}

@Test
public void testAdbEl(){
AdbDevice adbDevice = AdbDevice.getInstance(desiredCapabilities);
Position position = Position.getInstance(desiredCapabilities);

//获取设备信息

System.out.println("设备序列号: " + adbDevice.getDeviceId());
System.out.println("设备名称: " + adbDevice.getDeviceName());
int[] resolution = adbDevice.getScreenResolution();
System.out.println("设备屏幕分辨率: " + resolution[0] + "x" + resolution[1]);
System.out.println("设备Android版本: " + adbDevice.getAndroidVersion());
System.out.println("设备SDK版本: " + adbDevice.getSdkVersion());
System.out.println("设备电池状态： " + adbDevice.getBatteryStatus());
System.out.println("设备电池温度： " + adbDevice.getBatteryTemp());
System.out.println("设备电池电量： " + adbDevice.getBatteryLevel());


if(position.waitForElement(ElementAttribs.TEXT,"账号",WAIT_TIMEOUT)) {
ElementAdb e_search = position.findElementById("com.tuotuo.solo:id/rl_exchange_code");
adbDevice.click(e_search);
}

if(position.waitForElement(ElementAttribs.TEXT,"输入兑换码，兑换后即刻生效",WAIT_TIMEOUT)) {
ElementAdb e_search = position.findElementByText("输入兑换码，兑换后即刻生效");
adbDevice.click(e_search);
adbDevice.sendText("hahahahah");
driver.press(KeyEventEnum.ENTER.getValue());
ElementAdb e_txt = position.findElementByText("兑换码");
adbDevice.click(e_txt);
ElementAdb e_enter = position.findElementByText("确定兑换");
adbDevice.click(e_enter);
}
}
```

