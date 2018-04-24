package atx.client;

import atx.client.model.DesiredCapabilities;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by huqingen on 2018/4/24.
 */
public class FingerTest {

    AtxClient driver = new AtxClient();

    @Before
    public void setUp() throws Exception{
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setPackageName("com.tuotuo.solo");
        desiredCapabilities.setRemoteHost("192.168.2.81");

        driver.initDriver(desiredCapabilities);
    }

    @Test
    public void testNetE() throws Exception{

//        driver.elementByName("我的").click();
//        driver.elementByName("Finger Pro 私教中心").click();

//        driver.elementByXpath("//*[@text=\"更改\"]").click();

        driver.elementById("com.tuotuo.solo:id/rl_exchange_code").click();

        driver.elementByName("输入兑换码，兑换后即刻生效").sendKeys("3718a");

        driver.elementByName("确定兑换").click();

//        driver.elementByXpath("//*[@text=\"输入兑换码，兑换后即刻生效\"]").sendKeys("3718a");

//        driver.elementByXpath("//*[@text=\"确定兑换\"]").click();


    }
}
