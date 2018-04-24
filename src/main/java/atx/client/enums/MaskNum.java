package atx.client.enums;

/**
 *
 * 元素对应的mask编号
 * Created by 飞狐 on 2018/4/19.
 */
public enum MaskNum {

    /** 元素相关 **/

    TEXT(0x01,"text"),
    TEXT_CONTAINS(0x02,"textContains"),
    TEXT_MATCHES(0x04,"textMatches"),
    TEXT_STARTS_WITH(0x08,"textStartsWith"),
    CLASS_NAME(0x10,"className"),
    CLASS_NAME_MATCHES(0x20,"classNameMatches"),
    DESCRIPTION(0x40,"description"),
    DESCRIPTION_CONTAINS(0x80,"descriptionContains"),
    DESCRIPTION_MATCHES(0x0100,"descriptionMatches"),
    DESCRIPTION_STARTS_WITH(0x0200,"descriptionStartsWith"),
    CHECKABLE(0x0400,"checkable"),
    CHECKED(0x0800,"checked"),
    CLICKABLE(0x1000,"clickable"),
    LONGCLICKABLE(0x2000,"longClickable"),
    SCROLLABLE(0x4000,"scrollable"),
    enabled(0x8000,"enabled"),
    ENABLED(0x010000,"focusable"),
    FOCUSED(0x020000,"focused"),
    SELECTED(0x040000,"selected"),
    PACKAGE_NAME(0x080000,"packageName"),
    PACKAGE_NAME_MATCHES(0x100000,"packageNameMatches"),
    RESOURCEID(0x200000,"resourceId"),
    RESOURCEID_MATCHES(0x400000,"resourceIdMatches"),
    INDEX(0x800000,"index"),
    INSTANCE(0x01000000,"instance"),
    ;

    MaskNum(Integer value,String des) {
        this.value = value;
        this.des = des;
    }


    private Integer value;

    private String des;

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    /**
     * 根据name查找
     * @param name
     * @return
     */
    public static MaskNum iterationFindByName(String name) {
        for (MaskNum suit : MaskNum.values()) {
            if (name.equals(suit.name())) {
                return suit;
            }
        }
        return null;
    }

    /**
     * 根据描述查找
     * @param des
     * @return
     */
    public static MaskNum iterationFindByDes(String des) {
        for (MaskNum suit : MaskNum.values()) {
            if (des.equals(suit.getDes())) {
                return suit;
            }
        }
        return null;
    }
}
