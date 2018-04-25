package atx.client.enums;

/**
 * Created by huqingen on 2018/4/25.
 */
public enum KeyEventEnum {

    HOME("home"),//点击 HOME
    BACK("back"),//点击 返回
    LEFT("left"),//
    RIGHT("right"),//
    UP("up"),//
    DOWN("down"),//
    CENTER("center"),//
    MENU("menu"),//
    SEARCH("search"),//点击 查询
    ENTER("enter"),//回车
    DELETE("delete"),//删除文件
    RECENT("recent"),//
    VOLUME_UP("volume_up"),//音量 +
    VOLUME_DWON("volume_down"),// 音量 -
    VOLUME_MUTE("volume_mute"),//静音
    CAMERA("camera"),//打开摄像头
    POWER("power");//点击 电源键

    KeyEventEnum(String value) {
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
