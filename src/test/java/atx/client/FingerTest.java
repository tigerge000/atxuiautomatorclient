package atx.client;

import atx.client.adb.AdbDevice;
import atx.client.adb.ElementAdb;
import atx.client.adb.ElementAttribs;
import atx.client.adb.Position;
import atx.client.enums.AndroidKeyCode;
import atx.client.enums.KeyEventEnum;
import atx.client.model.DesiredCapabilities;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by huqingen on 2018/4/24.
 */
public class FingerTest {

    private static int WAIT_TIMEOUT = 2000;

    AtxClient driver = new AtxClient();

    private DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

    @Before
    public void setUp() throws Exception{

        desiredCapabilities.setPackageName("com.tuotuo.solo");
        desiredCapabilities.setRemoteHost("192.168.2.81");

        driver.initDriver(desiredCapabilities);
    }

    @Test
    public void testNetE() throws Exception{

//        driver.elementByName("我的").click();
//        driver.elementByName("Finger Pro 私教中心").click();

//        driver.elementByXpath("//*[@text=\"更改\"]").click();

//        driver.elementById("com.tuotuo.solo:id/rl_exchange_code").click();

        driver.elementByName("输入兑换码，兑换后即刻生效").click();

        driver.press(KeyEventEnum.ENTER.getValue());

        driver.elementByName("确定兑换").click();

//        driver.elementByXpath("//*[@text=\"输入兑换码，兑换后即刻生效\"]").sendKeys("3718a");

//        driver.elementByXpath("//*[@text=\"确定兑换\"]").click();


    }

    /**
     * 使用adb方法进行测试
     */
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

}
