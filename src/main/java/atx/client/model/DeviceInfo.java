package atx.client.model;

/**
 * 设备信息
 * Created by 飞狐 on 2018/4/24.
 */
public class DeviceInfo {

    private String currentPackageName;//当前包名

    private Integer displayRotation;//0:竖屏，1:横屏

    private Integer displayHeight; //屏幕高度

    private Integer displayWidth; //屏幕宽度

    public String getCurrentPackageName() {
        return currentPackageName;
    }

    public void setCurrentPackageName(String currentPackageName) {
        this.currentPackageName = currentPackageName;
    }

    public Integer getDisplayRotation() {
        return displayRotation;
    }

    public void setDisplayRotation(Integer displayRotation) {
        this.displayRotation = displayRotation;
    }

    public Integer getDisplayHeight() {
        return displayHeight;
    }

    public void setDisplayHeight(Integer displayHeight) {
        this.displayHeight = displayHeight;
    }

    public Integer getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(Integer displayWidth) {
        this.displayWidth = displayWidth;
    }
}
