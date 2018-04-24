package atx.client.common;

import atx.client.enums.Const;
import atx.client.model.AtxDriver;
import atx.client.model.DesiredCapabilities;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huqingen on 2018/4/22.
 */
public class Session {

    private AtxDriver driver;

    public static Session mInstance;

    public Session(AtxDriver driver){
        this.driver = driver;
    }


    /**
     * 单例实现
     * @return
     */
    public static Session getInstance(AtxDriver driver){
        if(mInstance == null){
            synchronized (Session.class){
                if(mInstance == null){
                    mInstance = new Session(driver);
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化session
     * @param desiredCapabilities
     */
    public void createSession(DesiredCapabilities desiredCapabilities){
        if(StringUtils.isNotEmpty(desiredCapabilities.getRemoteHost())){
            this.driver.setAtxHost(desiredCapabilities.getRemoteHost());
        }

        if(StringUtils.isNotEmpty(desiredCapabilities.getPackageName())){
            if(StringUtils.isNotEmpty(this.driver.getAtxHost())){
                startApp(this.driver.getAtxHost() + Const.SHELL_URI,desiredCapabilities.getPackageName());
            }
        }
    }


    /**
     * 启动packeName命令
     * @param packageName
     * @return
     */
    private String convertPackageNameShellCommand(String packageName){
        StringBuffer commandStr = new StringBuffer();
        commandStr.append("monkey -p ");
        commandStr.append(packageName);
        commandStr.append(" -c android.intent.category.LAUNCHER 1");

        return commandStr.toString();
    }

    private void startApp(String url ,String packageName){
        OkHttpClientMethod okHttpClientMethod = OkHttpClientMethod.getInstance();

        Map<String,Object> headers = new HashMap<String,Object>();
        headers.put("Content-Type","application/x-www-form-urlencoded");

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("command",convertPackageNameShellCommand(packageName));

        okHttpClientMethod.postMethod(url,params,headers);
    }
}
