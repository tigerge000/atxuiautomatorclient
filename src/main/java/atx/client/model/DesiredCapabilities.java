package atx.client.model;

/**
 * 初始化对象数据模型
 * Created by 飞狐 on 2018/4/22.
 */
public class DesiredCapabilities {
    //平台名称
    private String platformName;

    //包名
    private String packageName;

    //远程手机地址
    private String remoteHost;

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }
}
