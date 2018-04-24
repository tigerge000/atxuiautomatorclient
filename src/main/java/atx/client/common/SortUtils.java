package atx.client.common;

import java.security.MessageDigest;
import java.util.List;

/**
 * request接口参数基础算法
 * Created by 飞狐 on 2018/4/19.
 */
public class SortUtils {


    /**
     * mask算法
     * 把所有要查找的元素二进制相加
     * @param fields
     * @return
     */
    public static Integer maskValue(List<Integer> fields){
        Integer result = 0;
        for(Integer field:fields){
            result += Integer.valueOf(field.toString(),10);
        }
        return result;
    }

    /**
     * 方法转换成jsonrpc id值的基础算法
     * @param method
     * @return
     */
    public static String jsonrpcIdValue(String method){

        String times = Long.toString(System.currentTimeMillis()*1000);

        String sourceValue = method + " at " + times.substring(0,10) + "." + times.substring(10);

        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = sourceValue.getBytes("UTF-8");
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
        // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);
            String str = Integer.toHexString(c);
            switch (4 - str.length()) {
                case 0:
                    unicode.append("\\u" + str);
                    break;
                case 1:
                    str = "0" + str;
                    unicode.append("\\u" + str);
                    break;
                case 2:
                case 3:
                default:
                    str = String.valueOf(c);
                    unicode.append(str);
                    break;
            }


        }
        return unicode.toString();
    }

}
