package atx.client.common;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Created by 飞狐 on 2018/1/28.
 */
public class OkHttpClientCallBack implements Callback {

    private static final Log LOG = LogFactory.getLog(OkHttpClientCallBack.class);

    private Response response;

    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void onFailure(Call call, IOException e){
        e.printStackTrace();
    }

    public void onResponse(Call call, Response response) throws IOException{
        this.response = response;
        if(response.isSuccessful()) {
            String result = null;
            try {
                result = InputStreamUtils.inputStreamTOString(response.body().byteStream(),"UTF-8");
                LOG.info(result);
                this.result = result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
