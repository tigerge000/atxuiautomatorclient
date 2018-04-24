package atx.client.common;

import net.sf.json.JSONObject;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileMethodUtils {


	/**
	 * 生成xml文件
	 * @param filePath
	 * @param content
	 */
	public static void generateXML(String filePath, String content){

		File directory  = new File(".");
		String path = null;
		try {
		path = directory .getCanonicalPath();
		path = path + filePath;

		// 创建输出格式(OutputFormat对象)
		OutputFormat format = OutputFormat.createPrettyPrint();
		// 创建XMLWriter对象

			XMLWriter writer = new XMLWriter(new FileOutputStream(new File(path)), format);
			//设置不自动进行转义
			writer.setEscapeText(false);
			// 生成XML文件
			writer.write(content);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 递归方式获取指定路径，指定格式的文件
	 * @param fileDir
	 * @param fileType
	 * @return
	 */
	public static List<File> getFiles(File fileDir,String fileType){
		List<File> lfile = new ArrayList<File>();
		File[] fs = fileDir.listFiles();
		for(File f : fs){
			if(f.isFile()){
				if(fileType.equals(f.getName().substring(
						f.getName().lastIndexOf(".") + 1,
						f.getName().length()))) {
					lfile.add(f);
				}
			}else {
				List<File> ftemps = getFiles(f,fileType);
				lfile.addAll(ftemps);
			}
		}
		return lfile;
	}

	 /**
	  * 写txt文件
	  * @param content 文本内容信息
	  * @return filePath 文件路径
	  */
	public static boolean writeTxtFile(String content,String filePath) throws Exception{
		RandomAccessFile mm=null;
		boolean flag=false;
		FileOutputStream o=null;
		File directory  = new File(".");
		String path = null;
		path = directory .getCanonicalPath();
//		path += "\\" + filePath;//Windows专用
		path += filePath;
		File file =new File(path);
		if (!file.getParentFile().exists()){
			System.out.println("目标文件所在目录不存在，准备创建它！");
			if(!file.getParentFile().mkdirs()){
				System.out.println("创建目标文件所在目录失败！");
				}
		}else {
			if(!file.exists()){
				if(file.createNewFile()){
					System.out.println("创建单个文件" + path + "成功！");
				}
				else {
					System.out.println("创建单个文件" + path + "失败！");
				}
			}
		}


	  try {
		  o = new FileOutputStream(file);
	      o.write(content.getBytes("UTF-8"));
	      o.close();
	   flag=true;
	  } catch (Exception e) {
		   // TODO: handle exception
	   e.printStackTrace();
	  }finally{
	   if(mm!=null){
	    mm.close();
	   }
	  }
	  return flag;
	 }

	/**
	 * 读取txt文本内容
	 * 路径:resources下
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String readTxtViaResources(String filePath) throws IOException{
		String result = "";
		try {
			InputStream inputStream = FileMethodUtils.class.getClassLoader().getResourceAsStream(filePath);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			result = bufferedReader.readLine();
			String s = null;
			while ((s = bufferedReader.readLine()) != null) {//使用readLine方法，一次读一行
				result = result + "\n" + s;
			}
			bufferedReader.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		result = result.replaceAll("\r|\n", "");
		return result;
	}

	/**
	  * 读TXT文件内容
	  * @param filepath 文件路径
	  * @return
	 * @throws IOException
	  */
	public static String readTxtFile(String filepath) throws IOException{
		String result = "";
		File directory  = new File(".");
		String path = null;

		path = directory .getCanonicalPath();
		path = path + filepath;
		File file = new File(path);
		if (!file.exists()){
			return null;
		}
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
			String s = null;
			while((s = br.readLine())!=null){//使用readLine方法，一次读一行
				result = result + "\n" +s;
			}
			br.close();

		}catch(Exception e){
			e.printStackTrace();
		}

		result = result.replaceAll("\r|\n", "");
		return result;
   }
	/**
	 * 读TXT文件内容
	 * @param filepath 文件路径
	 * @return
	 * @throws IOException
	 */
	public static List<String> readTxtFileAsList(String filepath) throws IOException{
		String result = "";

		File directory  = new File(".");
		String path = null;
		path = directory .getCanonicalPath();
		path = path + filepath;
		File file = new File(path);
		if (!file.exists()){
			return null;
		}


		List<String> resultList = new ArrayList<String>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
			String s = null;
			while((s = br.readLine())!=null){//使用readLine方法，一次读一行
//				result = result + "\n" +s;
				resultList.add(s);
			}
			br.close();

		}catch(Exception e){
			e.printStackTrace();
		}
//		result = result.replaceAll("\r|\n", "");
		return resultList;
	}

	/**
	 * 读取以,分割的txt文件,并以JSONObject返回
	 *
	 */
	public static JSONObject ReadTxtAsJSON(String filepath) throws IOException{

		String result = "";
		File directory  = new File(".");
		String path = null;
		path = directory .getCanonicalPath();
		path = path + filepath;
		File file = new File(path);
		if (!file.exists()){
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
			String stringValue = null;
			while((stringValue = br.readLine())!=null){//使用readLine方法，一次读一行
				//获取,之前的数据
				int index = stringValue.indexOf(",");
				String key = stringValue.substring(0,index);
				String value = stringValue.substring(index+1);
				jsonObject.put(key,value);
			}
			br.close();

		}catch(Exception e){
			e.printStackTrace();
		}

		return jsonObject;

	}



	/**
	 * 删除单个文件
	 *
	 * @param filepath
	 *            被删除文件的路径+文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String filepath) throws IOException{
		Boolean flag = false;
//		File directory  = new File(".");
//		String path = null;
//		path = directory .getCanonicalPath();
//		path = path + filepath;
		File file = new File(filepath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 *
	 * @param filepath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String filepath) throws IOException{
		File directory  = new File(".");
		String path = null;
		path = directory .getCanonicalPath();
		path = path + filepath;
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		File dirFile = new File(path);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		Boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}
		if (!flag) {
			return false;
		}
		// 删除当前目录
//		if (dirFile.delete()) {
		return true;
//		} else {
//			return false;
//		}
	}

}
