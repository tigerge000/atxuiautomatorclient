package atx.client.common;

import atx.client.enums.Const;
import atx.client.enums.MaskNum;
import atx.client.enums.MethodEnum;
import atx.client.model.AtxDriver;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 元素对象
 * Created by 飞狐 on 2018/4/22.
 */
public class ElementObj {

    private AtxDriver driver;

    private OkHttpClientMethod okHttpClientMethod = OkHttpClientMethod.getInstance();

    public ElementObj(AtxDriver atxDriver){
        this.driver = atxDriver;
    }

    /**
     * 基础请求数据模型
     * @param method
     * @param params
     * @return
     */
    public static JSONObject baseRequestJson(String method, Object params){
        JSONObject requestJson = new JSONObject();
        requestJson.put("jsonrpc","2.0");
        requestJson.put("id", SortUtils.jsonrpcIdValue(method));
        requestJson.put("method",method);
        requestJson.put("params",params);

        return requestJson;
    }

    /**
     * 点击事件
     * @throws Exception
     */
    public void click() throws Exception{
        okHttpClientMethod.postByteMethod(driver.getAtxHost() + Const.BASE_URI,baseRequestJson(MethodEnum.CLICK.getValue(),driver.getCoordinateInfo()));
    }


    /**
     * 发送文本信息
     */
    public void sendKeys(String keys) throws Exception{
        List<Object> params = driver.getSearchEleInfo();
        params.add(keys);
        okHttpClientMethod.postByteMethod(driver.getAtxHost() + Const.BASE_URI,baseRequestJson(MethodEnum.SET_TEXT.getValue(),params));
    }


    /**
     * 根据子节点查找
     * @param searchParams
     */
    public void child(Map<String,Object> searchParams){
        List<Object> params = driver.getSearchEleInfo();

        JSONObject jsonObject = new JSONObject();
        List<Integer> fields = new ArrayList<Integer>();

        jsonObject.put("childOrSibling",new ArrayList<>());
        jsonObject.put("childOrSiblingSelector",new ArrayList<>());

        for (String key : searchParams.keySet()) {
            fields.add(MaskNum.iterationFindByDes(key).getValue());
            jsonObject.put(MaskNum.iterationFindByDes(key).getDes(),searchParams.get(key).toString());
        }

        jsonObject.put("mask", SortUtils.maskValue(fields));

        List<JSONObject> jsonObjectList = new ArrayList<>();
        jsonObjectList.add(jsonObject);



    }

}
