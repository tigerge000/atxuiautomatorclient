package atx.client.enums;

/**
 * Created by 飞狐 on 2018/4/22.
 */
public class Const {
    //基础uri
    public static final String BASE_URI = "/jsonrpc/0";

    public static final String UIAUTOMAROR_URI = "/uiautomator";

    //接收shell命令 URI
    public static final String SHELL_URI = "/shell";

    //截图uri
    public static final String SCREENSHOT_URI = "/screenshot/0";

    //等待最长超时时间
    public static int WAIT_ElEMENT_TIMEOUT = 1000;

    //每个循环超时时间
    public static int WAIT_ELEMENT_TIME_INTERVAL = 200;

    public static String XML_PATH = "/xml/now.xml";

    public static Integer PORT_SHELL = 7912;
//
    public static Integer PORT_UI = 7912;

    //在uiautomator服务上增加了 shell接口
//    public static Integer PORT_SHELL = 9999;

    //直接调用uiautomator接口
//    public static Integer PORT_UI = 9008;

}
