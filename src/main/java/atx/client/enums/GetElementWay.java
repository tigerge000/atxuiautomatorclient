package atx.client.enums;

/**
 * Created by 飞狐 on 2018/4/22.
 */
public enum GetElementWay {

    TEXT("text"),
    DESC("description"),
    RESOUCE_ID("resourceId"),
    CLASS_NAME("className");

    private String using;

    GetElementWay(String using) {
        this.using = using;
    }

    public String getUsing() {
        return this.using;
    }
}
