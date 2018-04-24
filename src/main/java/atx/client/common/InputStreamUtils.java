package atx.client.common;

/**
 * Created by huqingen on 16/5/26.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Andy.Chen
 * @mail Chenjunjun.ZJ@gmail.com
 *
 */
public class InputStreamUtils {

    final static int BUFFER_SIZE = 65535;

    /**
     * 将InputStream转换成String
     * @param in InputStream
     * @return String
     * @throws Exception
     *
     */
    public static String inputStreamTOString(InputStream in) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),"ISO-8859-1");
    }

    /**
     * 将InputStream转换成某种字符编码的String
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String inputStreamTOString(InputStream in,String encoding) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),encoding);
    }

    /**
     * 将String转换成InputStream
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream stringTOInputStream(String in) throws Exception{

        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));
        return is;
    }

    /**
     * 将InputStream转换成byte数组
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] inputStreamTOByte(InputStream in) throws IOException{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }

    /**
     * 将byte数组转换成InputStream
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream byteTOInputStream(byte[] in) throws Exception{

        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }

    /**
     * 将byte数组转换成String
     * @param in
     * @return
     * @throws Exception
     */
    public static String byteTOString(byte[] in) throws Exception{

        InputStream is = byteTOInputStream(in);
        return inputStreamTOString(is);
    }

}
