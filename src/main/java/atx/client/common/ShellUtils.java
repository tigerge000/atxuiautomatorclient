package atx.client.common;

import atx.client.adb.AdbDevice;
import atx.client.enums.Const;
import atx.client.enums.MethodEnum;
import atx.client.model.DesiredCapabilities;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huqingen on 2018/4/25.
 */
public class ShellUtils {

    private DesiredCapabilities desiredCapabilities;

    private static ShellUtils mInstance;

    public ShellUtils(DesiredCapabilities desiredCapabilities){
        this.desiredCapabilities = desiredCapabilities;
    }

    /**
     * 单例实现
     * @return
     */
    public static ShellUtils getInstance(DesiredCapabilities desiredCapabilities){
        if(mInstance == null){
            synchronized (ShellUtils.class){
                if(mInstance == null){
                    mInstance = new ShellUtils(desiredCapabilities);
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送shell命令
     * @param command
     */
    public String shellPost(String command){
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("command",command);
        JSONObject result = JSONObject.fromObject(OkHttpClientMethod.getInstance().postMethod("http://" + desiredCapabilities.getRemoteHost() + ":7912"+ Const.SHELL_URI,params,new HashMap<String,Object>()));

        if(result.containsKey("output")){
            return result.getString("output");
        }
        return null;
    }

    /**
     * 获取当前app xml结构
     */
    public void dumpHierarchy(){
        List<Object> params = new ArrayList<Object>();
        params.add(false);
        params.add(null);
        JSONObject result = JSONObject.fromObject(OkHttpClientMethod.getInstance().postByteMethod("http://" + desiredCapabilities.getRemoteHost() + ":7912"+ Const.BASE_URI, ElementObj.baseRequestJson(MethodEnum.DUMP_WINDOWS_HIERARCHY.getValue(),params)));
        FileMethodUtils.generateXML(Const.XML_PATH,result.getString("result"));

    }

}
