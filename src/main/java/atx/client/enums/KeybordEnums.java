package atx.client.enums;

/**
 * Created by 飞狐 on 2018/4/26.
 */
public enum KeybordEnums {

    UNICODE("io.appium.android.ime/.UnicodeIME"),//原生输入法
    SOUGOU("com.sohu.inputmethod.sogou/.SogouIME"),//搜狗输入法
    YIJIA("com.baidu.input_yijia/.ImeService"),
    XIAOMI_SOUGOU("com.sohu.inputmethod.sogou.xiaomi/.SogouIME");//小米 搜狗输入法

    KeybordEnums(String value) {
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
