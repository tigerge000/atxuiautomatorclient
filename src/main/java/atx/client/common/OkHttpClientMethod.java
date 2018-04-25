package atx.client.common;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OKHttpClient基础方法
 * Created by 飞狐 on 2018/1/25.
 */
public class OkHttpClientMethod {

    private static final Log log = LogFactory.getLog(OkHttpClientMethod.class);

    private OkHttpClient mOkHttpClient;

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String,List<Cookie>>();

    public static OkHttpClientMethod mInstance;


    private Map<String, Object> headerParams;

    public Map<String, Object> getHeaderParams() {
        return headerParams;
    }

    public void setHeaderParams(Map<String, Object> headerParams) {
        this.headerParams = headerParams;
    }

    /**
     * 构造函数初始化
     */
    private OkHttpClientMethod(){

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(30, TimeUnit.SECONDS);//读取超时
        builder.connectTimeout(10,TimeUnit.SECONDS);//连接超时
        builder.writeTimeout(60,TimeUnit.SECONDS);//写入超时

        //协议
        List<Protocol> protocols = new ArrayList<Protocol>();
        protocols.add(Protocol.HTTP_1_1);
        protocols.add(Protocol.HTTP_2);


        builder.protocols(protocols);

        //ssl
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        builder.sslSocketFactory(sslParams.sSLSocketFactory,sslParams.trustManager);

        //cookie 自动存储
        builder.cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(),cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        });

        mOkHttpClient = builder.build();
    }

    /**
     * 单例实现
     * @return
     */
    public static OkHttpClientMethod getInstance(){
        if(mInstance == null){
            synchronized (OkHttpClientMethod.class){
                if(mInstance == null){
                    mInstance = new OkHttpClientMethod();
                }
            }
        }
        return mInstance;
    }


    /**
     * 设置Header头
     * @param headersParams
     * @return
     */
    private Headers setHeaders(Map<String,Object> headersParams){
        Headers headers = null;

        Headers.Builder headerBuilder = new Headers.Builder();

        if(headersParams != null){
            for (String key:headersParams.keySet()){
                headerBuilder.add(key,headersParams.get(key).toString());
            }
        }else {
            for (String key:getHeaderParams().keySet()){
                headerBuilder.add(key,getHeaderParams().get(key).toString());
            }

        }


        headers = headerBuilder.build();

        return headers;
    }


    /**
     * 设置get连接拼接参数
     * @param params
     * @return
     */
    private String setUrlParams(Map<String,Object> params){
        StringBuffer param = new StringBuffer();
        int i = 0;
        if(params == null){
            return param.toString();
        }
        for (String key : params.keySet()) {
            if (i == 0) {
                param.append("?");
            }
            else {
                param.append("&");
            }
            try {
                param.append(key).append("=").append(URLEncoder.encode(params.get(key).toString(),"UTF-8"));  //字符串拼接
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
        return param.toString();
    }

    /**
     * 设置post表单请求
     * @param params
     * @return
     */
    private RequestBody setPostBody(Map<String,Object> params){

        RequestBody body = null;

        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        if(params != null){
            for (String key:params.keySet()){
                formBodyBuilder.add(key,params.get(key).toString());
            }
        }

        body = formBodyBuilder.build();

        return body;
    }


    /**
     * head请求
     * @param url
     * @param urlParams
     * @param headerParams
     * @return
     */
    public Object headMethod(String url,Map<String, Object> urlParams, Map<String, Object> headerParams){
        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .get()
                .build();

        Call call = mOkHttpClient.newCall(request);

        JSONObject jsonResult = new JSONObject();
        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());
        try {
            Response response = call.execute();
            String result = InputStreamUtils.inputStreamTOString(response.body().byteStream(),"UTF-8");
            log.info("Http Result:" + result);
            if (response.isSuccessful()){
                jsonResult =  JSONObject.fromObject(result);
                return jsonResult;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResult;

    }


    /**
     * downLoad请求
     * @param url
     * @param saveDir
     * @return
     */
    public void downLoadMethod(final String url, final String saveDir){
        Request request = new Request.Builder()
                .url(url)
                .headers(setHeaders(new HashMap<String,Object>()))
                .get()
                .build();

        Call call = mOkHttpClient.newCall(request);

//        log.info("Http info:" + request.toString());
//        log.info("Http Header:" + request.headers().toString());
        try {

            Response response = call.execute();
            InputStream inputStream = null;
            byte[] buf = new byte[1024];
            int len = 0;
            FileOutputStream fileOutputStream = null;

            try {
                inputStream = response.body().byteStream();
                long total = response.body().contentLength();
                File file = new File(saveDir);

                fileOutputStream = new FileOutputStream(file);
                long sum = 0;
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                    sum += len;
                }
                fileOutputStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if(inputStream != null){
                        inputStream.close();
                    }
                }catch (IOException e){

                }

                try {
                    if(fileOutputStream != null){
                        fileOutputStream.close();
                    }
                }catch (IOException e){

                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     * downLoad请求
     * @param url
     * @param saveDir
     * @param urlParams
     * @param headerParams
     * @return
     */
    public void downLoadMethod(final String url, final String saveDir, Map<String, Object> urlParams, Map<String, Object> headerParams, final OnDownloadListener listener){
        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .get()
                .build();

        Call call = mOkHttpClient.newCall(request);

//        log.info("Http info:" + request.toString());
//        log.info("Http Header:" + request.headers().toString());
        try {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //下载失败
                    listener.onDownloadFailed();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = null;
                    byte[] buf = new byte[1024];
                    int len = 0;
                    FileOutputStream fileOutputStream = null;

                    try {
                        inputStream = response.body().byteStream();
                        long total = response.body().contentLength();
                        File file = new File(saveDir);

                        fileOutputStream = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = inputStream.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);
                            //下载中
                            listener.onDownloading(progress);
                        }
                        fileOutputStream.flush();
                        //下载完成
                        listener.onDownloadSuccess();
                    }catch (Exception e){
                        listener.onDownloadFailed();
                    }finally {
                        try {
                            if(inputStream != null){
                                inputStream.close();
                            }
                        }catch (IOException e){

                        }

                        try {
                            if(fileOutputStream != null){
                                fileOutputStream.close();
                            }
                        }catch (IOException e){

                        }
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    private String getNameFromUrl(String url) {

        if(url.contains("?")){

            return url.split("\\?")[0].substring(url.lastIndexOf("/") + 1).replace(".ts","_"+System.currentTimeMillis()+".ts");

        }
        return url.substring(url.lastIndexOf("/") + 1).replace(".ts","_"+System.currentTimeMillis()+".ts");
    }




    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }

    /**
     * Get请求
     * @param url
     * @param urlParams
     * @param headerParams
     * @return
     */
    public Object getMethod(String url,Map<String, Object> urlParams, Map<String, Object> headerParams){
        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .get()
                .build();

        Call call = mOkHttpClient.newCall(request);

        JSONObject jsonResult = new JSONObject();
//        log.info("Http info:" + request.toString());
//        log.info("Http Header:" + request.headers().toString());
        try {
            Response response = call.execute();
            String result = InputStreamUtils.inputStreamTOString(response.body().byteStream(),"UTF-8");
//            log.info("Http Result:" + result);
            if (response.isSuccessful()){
                try {
                    jsonResult =  JSONObject.fromObject(result);
                    return jsonResult;
                }catch (Exception e){

                    e.printStackTrace();
                    return result;
                }
            }else {
                log.error("请求返回体异常!!请检请求参数/token");
                jsonResult =  JSONObject.fromObject(result);
                return JSONObject.fromObject(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResult;

    }


    /**
     * 异步Get请求
     * @param url
     * @param urlParams
     * @param headerParams
     * @return
     */
    public void getAsyncMethod(String url,Map<String, Object> urlParams, Map<String, Object> headerParams,OkHttpClientCallBack okHttpRequestCallBack){
        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .get()
                .build();

        Call call = mOkHttpClient.newCall(request);

        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());

        try {

            call.enqueue(okHttpRequestCallBack);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 异步Get请求 原生
     * @param url
     * @param urlParams
     * @param headerParams
     * @return
     */
    public void getAsyncNativeMethod(String url,Map<String, Object> urlParams, Map<String, Object> headerParams,Callback callback){
        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .get()
                .build();

        Call call = mOkHttpClient.newCall(request);

        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());

        try {

            call.enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete
     * @param url
     * @param urlParams
     * @param bodyParams
     * @param headerParams
     * @return
     */
    public Object deleteMethod(String url, Map<String, Object> urlParams, Map<String, Object> bodyParams, Map<String, Object> headerParams){

        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .delete(setPostBody(bodyParams))
                .build();

        Call call = mOkHttpClient.newCall(request);

        JSONObject jsonResult = new JSONObject();
        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());
        log.info("Http params:" + request.body().toString());
        try {
            Response response = call.execute();

            String result = InputStreamUtils.inputStreamTOString(response.body().byteStream(),"UTF-8");
            log.info("Http Result:" + result);
            if (response.isSuccessful()){
                try {
                    jsonResult = JSONObject.fromObject(result);
                    return jsonResult;
                }catch (JSONException exception){
                    return result;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResult;
    }



    /**
     * @param url
     * @param bodyParams
     * @param headerParams
     * @return
     */
    public Object postMethod(String url,Map<String, Object> bodyParams, Map<String, Object> headerParams){
        return postMethod(url,null,bodyParams,headerParams);
    }



    /**
     * 表单形式 Post请求
     * @param url
     * @param urlParams
     * @param bodyParams
     * @param headerParams
     * @return
     */
    public Object postMethod(String url, Map<String, Object> urlParams, Map<String, Object> bodyParams, Map<String, Object> headerParams){

        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .post(setPostBody(bodyParams))
                .build();

        Call call = mOkHttpClient.newCall(request);

        JSONObject jsonResult = new JSONObject();
        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());
        log.info("Http params:" + request.body().toString());
        try {
            Response response = call.execute();

            String result = InputStreamUtils.inputStreamTOString(response.body().byteStream(),"UTF-8");
            log.info("Http Result:" + result);
            if (response.isSuccessful()){

                try {
                    jsonResult = JSONObject.fromObject(result);
                    return jsonResult;
                }catch (JSONException exception){
                    return result;
                }



            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResult;
    }


    /**
     * 异步Post请求原生
     * @param url
     * @param urlParams
     * @param bodyParams
     * @param headerParams
     * @return
     */
    public void postAsyncNativeMethod(String url, Map<String, Object> urlParams, Map<String, Object> bodyParams, Map<String, Object> headerParams,Callback callback){

        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .post(setPostBody(bodyParams))
                .build();

        Call call = mOkHttpClient.newCall(request);

        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());
        log.info("Http params:" + request.body().toString());
        try {
            call.enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 异步Post请求
     * @param url
     * @param urlParams
     * @param bodyParams
     * @param headerParams
     * @return
     */
    public void postAsyncMethod(String url, Map<String, Object> urlParams, Map<String, Object> bodyParams, Map<String, Object> headerParams,OkHttpClientCallBack okHttpRequestCallBack){

        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .post(setPostBody(bodyParams))
                .build();

        Call call = mOkHttpClient.newCall(request);

        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());
        log.info("Http params:" + request.body().toString());
        try {
            call.enqueue(okHttpRequestCallBack);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送Byte post请求
     * @param url
     * @param jsonParams
     * @return
     */
    public Object postByteMethod(String url,final Object jsonParams){
        return postByteMethod(url,null,jsonParams,new HashMap<String,Object>());
    }

    /**
     * 发送Byte post请求
     * @param url
     * @param urlParams
     * @param jsonParams
     * @param headerParams
     * @return
     */
    public Object postByteMethod(String url,final Map<String,Object> urlParams,final Object jsonParams,Map<String,Object> headerParams){

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),SortUtils.string2Unicode(jsonParams.toString()).getBytes());

        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .post(requestBody)
                .build();

        Call call = mOkHttpClient.newCall(request);

        JSONObject jsonResult = new JSONObject();
        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());
        log.info("Http params:" + jsonParams.toString());
        try {
            Response response = call.execute();
            String result = InputStreamUtils.inputStreamTOString(response.body().byteStream(),"UTF-8");
            log.info("Http Result:" + result);
            if (response.isSuccessful()){
                jsonResult =  JSONObject.fromObject(result);
                return jsonResult;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResult;
    }



    /**
     * 发送JSON post请求
     * @param url
     * @param jsonParams
     * @param headerParams
     * @return
     */
    public Object postJsonMethod(String url,Object jsonParams,Map<String,Object> headerParams){
        return postJsonMethod(url,null,jsonParams,headerParams);
    }


    /**
     * 发送JSON post请求
     * @param url
     * @param urlParams
     * @param jsonParams
     * @param headerParams
     * @return
     */
    public Object postJsonMethod(String url,Map<String,Object> urlParams,Object jsonParams,Map<String,Object> headerParams){

        RequestBody requestBody = null;

        if(jsonParams instanceof JSONObject) {
            requestBody = FormBody.create(MediaType.parse("application/json"),JSONObject.fromObject(jsonParams).toString().getBytes());
        }

        if(jsonParams instanceof JSONArray){
            requestBody = FormBody.create(MediaType.parse("application/json"),JSONArray.fromObject(jsonParams).toString().getBytes());
        }

        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .post(requestBody)
                .build();

        Call call = mOkHttpClient.newCall(request);

        JSONObject jsonResult = new JSONObject();
        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());
        log.info("Http params:" + jsonParams.toString());
        try {
            Response response = call.execute();
            String result = InputStreamUtils.inputStreamTOString(response.body().byteStream(),"UTF-8");
            log.info("Http Result:" + result);
            if (response.isSuccessful()){
                jsonResult =  JSONObject.fromObject(result);
                return jsonResult;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResult;
    }


    /**
     * 发送JSON 异步post请求
     * @param url
     * @param urlParams
     * @param jsonParams
     * @param headerParams
     * @return
     */
    public void postJsonAsyncMethod(String url,Map<String,Object> urlParams,Object jsonParams,Map<String,Object> headerParams,OkHttpClientCallBack okHttpRequestCallBack){
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"),JSONObject.fromObject(jsonParams).toString());
        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .post(requestBody)
                .build();

        Call call = mOkHttpClient.newCall(request);


        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());
        log.info("Http params:" + request.body().toString());
        try {
            call.enqueue(okHttpRequestCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * 发送JSON 异步post请求原生
     * @param url
     * @param urlParams
     * @param jsonParams
     * @param headerParams
     * @return
     */
    public void postJsonAsyncNativeMethod(String url,Map<String,Object> urlParams,Object jsonParams,Map<String,Object> headerParams,Callback callback){
        RequestBody requestBody = null;

        if(jsonParams instanceof JSONObject) {
            requestBody = FormBody.create(MediaType.parse("application/json"),JSONObject.fromObject(jsonParams).toString());
        }

        if(jsonParams instanceof JSONArray){
            requestBody = FormBody.create(MediaType.parse("application/json"),JSONArray.fromObject(jsonParams).toString());
        }
        Request request = new Request.Builder()
                .url(url + setUrlParams(urlParams))
                .headers(setHeaders(headerParams))
                .post(requestBody)
                .build();

        Call call = mOkHttpClient.newCall(request);

        log.info("Http info:" + request.toString());
        log.info("Http Header:" + request.headers().toString());
        log.info("Http params:" + request.body().toString());
        try {
            call.enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
