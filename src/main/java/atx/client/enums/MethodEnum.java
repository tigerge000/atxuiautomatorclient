package atx.client.enums;

/**
 * Created by 飞狐 on 2018/4/19.
 */
public enum MethodEnum {

    WAIT_FOR_EXISITS("waitForExists"),//查看元素是否存在
    OBJ_INFO("objInfo"),//查看元素信息
    CLICK("click"),//点击事件
    DEVICE_INFO("deviceInfo"),//查看设备信息
    DRAG_TO("dragTo"),//拖拽
    SET_TEXT("setText"),//设置文本信息
    DUMP_WINDOWS_HIERARCHY("dumpWindowHierarchy");//获取当前app的xml结构

    MethodEnum(String value) {
        this.value = value;
    }

    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
