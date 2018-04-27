package atx.client.model;

import atx.client.enums.Const;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 飞狐 on 2018/4/22.
 */
public class AtxDriver {

    //初始化对象参数
    private DesiredCapabilities desiredCapabilities;

    //远程手机Http Host地址
    private String atxHost;

    //元素对象信息
    private JSONObject objInfo;

    //坐标
    private List<Float> coordinateInfo;

    //当前查找元素的信息
    private List<Object> searchEleInfo;

    public List<Object> getSearchEleInfo() {
        return searchEleInfo;
    }

    public void setSearchEleInfo(List<Object> searchEleInfo) {
        this.searchEleInfo = searchEleInfo;
    }

    public DesiredCapabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    public void setDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
    }


    public String getAtxHost() {
        return atxHost;
    }

    public void setAtxHost(String atxHost) {
        this.atxHost = "http://" + atxHost + ":" + Const.PORT_UI;
    }

    public JSONObject getObjInfo() {
        return objInfo;
    }

    public void setObjInfo(JSONObject objInfo) {
        this.objInfo = objInfo;
    }

    public List<Float> getCoordinateInfo() {
        return coordinateInfo;
    }

    public void setCoordinateInfo(List<Float> coordinateInfo) {
        if(CollectionUtils.isEmpty(coordinateInfo)) {
            List<Float> result = new ArrayList<Float>();
            if (this.objInfo != null) {
                JSONObject bounds = this.objInfo.getJSONObject("result").getJSONObject("bounds");
                Integer right = bounds.getInt("right");
                Integer left = bounds.getInt("left");
                Integer top = bounds.getInt("top");
                Integer bottom = bounds.getInt("bottom");

                Float x = 0f;
                Float y = 0f;

                y = Float.valueOf(String.valueOf((bottom - top) / 2 + top));

                x = Float.valueOf(String.valueOf((right - left) / 2 + left));

                result.add(x);
                result.add(y);
            }
            this.coordinateInfo = result;
        }else {
            this.coordinateInfo = coordinateInfo;
        }
    }
}
