package atx.client;

import atx.client.common.SortUtils;
import atx.client.enums.Const;
import atx.client.enums.MaskNum;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huqingen on 2018/4/22.
 */
public class EnumsTest {

    @Test
    public void test1(){

        MaskNum maskNum = MaskNum.CHECKED;

        System.out.println(maskNum.name());

        System.out.println(maskNum.getValue());
        System.out.println(maskNum.getDes());

    }


    @Test
    public void test2(){
        try {
//            elementByXpath( "//*[@resource-id=\"com.netease.cloudmusic:id/a5g\"]");

//            elementByXpath("//node[@resource-id=\"com.netease.cloudmusic:id/a5g\"]");
//            elementByXpath("//*[@resource-id=\"com.netease.cloudmusic:id/gt\"]/android.widget.LinearLayout[1]");
            elementByXpath("//*[@resource-id=\"com.netease.cloudmusic:id/gt\"]/*[@class=\"android.widget.LinearLayout\"][1]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void elementByXpath(String xpath) throws Exception {

        File directory  = new File(".");
        String path = null;
        try {
            path = directory.getCanonicalPath();
            path = path + Const.XML_PATH;
        }catch (Exception e){
            e.printStackTrace();
        }

        Document document = new SAXReader().read(new File(path));

        List<Node> list = document.selectNodes(xpath);

        boolean isExist = false;

        if(list != null && list.size() == 0){
            isExist = true;
        }

        for (Node node : list){
            Element tempNode = (Element) node;

            String[] str = tempNode.attribute("bounds").getValue().split("[^0-9]");

             List<Integer> list1 = new ArrayList<Integer>();


            for(String s : str){
                if(!s.isEmpty()){
                    list1.add(Integer.valueOf(s));
                }
            }

            System.out.println(ArrayUtils.toString(list1));

            System.out.println(ArrayUtils.toString(Arrays.asList(tempNode.attribute("bounds").getValue()).get(0)));

        }


//        System.out.println(ArrayUtils.toString(document.getRootElement().attributes()));
//
//        ElementObj node = document.getRootElement();
//
//        listNodes(node);

    }


    /**
     * 遍历当前节点元素下面的所有(元素的)子节点
     *
     * @param node
     */
    public void listNodes(Element node) {
        System.out.println("当前节点的名称：：" + node.getName());
        // 获取当前节点的所有属性节点
        List<Attribute> list = node.attributes();
        // 遍历属性节点
        for (Attribute attr : list) {
            System.out.println(attr.getText() + "-----" + attr.getName()
                    + "---" + attr.getValue());
        }

        if (!(node.getTextTrim().equals(""))) {
            System.out.println("文本内容：：：：" + node.getText());
        }

        // 当前节点下面子节点迭代器
        Iterator<Element> it = node.elementIterator();
        // 遍历
        while (it.hasNext()) {
            // 获取某个子节点对象
            Element e = it.next();
            // 对子节点进行遍历
            listNodes(e);
        }
    }

    /**
     * 介绍Element中的element方法和elements方法的使用
     *
     * @param node
     */
    public void elementMethod(Element node) {
        // 获取node节点中，子节点的元素名称为supercars的元素节点。
        Element e = node.element("supercars");
        // 获取supercars元素节点中，子节点为carname的元素节点(可以看到只能获取第一个carname元素节点)
        Element carname = e.element("carname");

        System.out.println(e.getName() + "----" + carname.getText());

        // 获取supercars这个元素节点 中，所有子节点名称为carname元素的节点 。

        List<Element> carnames = e.elements("carname");
        for (Element cname : carnames) {
            System.out.println(cname.getText());
        }

        // 获取supercars这个元素节点 所有元素的子节点。
        List<Element> elements = e.elements();

        for (Element el : elements) {
            System.out.println(el.getText());
        }

    }


    /**
     *
     * [73,941][196,1038]
     * {"bottom":1038,"left":73,"right":196,"top":941}
     * 计算坐标
     */
    private void convertCoordinate(String boundsValue){

        String[] str = boundsValue.split("[^0-9]");

        List<Integer> list = new ArrayList<Integer>();

        for(String s : str){
            if(!s.isEmpty()){
                list.add(Integer.valueOf(s));
            }
        }
        List<Float> result = new ArrayList<Float>();

        Integer left = list.get(0);
        Integer top = list.get(1);
        Integer right = list.get(2);
        Integer bottom = list.get(3);

        Float x = 0f;
        Float y = 0f;

        y = Float.valueOf(String.valueOf((bottom - top) / 2 + top));

        x = Float.valueOf(String.valueOf((right - left) / 2 + left));

        result.add(x);
        result.add(y);

    }


    @Test
    public void test3() throws Exception{
        String str = "你好FM";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("params",str);

        System.out.println(SortUtils.string2Unicode(str));

        System.out.println(SortUtils.string2Unicode(JSONObject.fromObject(jsonObject.toString()).toString()));

    }
}
