package com.example.issuer.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;


/**
 * Created by Administrator on 2016/3/1.
 */
public class MyHttpClient {
    static String result="";

    public static String requestByHttpGet(String url,String city) throws Exception {
        try {
            String uri = url+city;
            // 新建HttpGet对象
            HttpGet httpGet = new HttpGet(uri);
            // 获取HttpClient对象
            HttpClient httpClient = new DefaultHttpClient();
            // 获取HttpResponse实例
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.SO_TIMEOUT, 6000);
            HttpResponse httpResp = httpClient.execute(httpGet);
            // 判断是够请求成功
            Log.i("dsn", httpResp.getStatusLine().getStatusCode() + "");
            if (httpResp.getStatusLine().getStatusCode() == 200) {
                // 获取返回的数据
                return result = getJsonStringFromGZIP(httpResp);
            } else {
                Log.i("dsn", "HttpGet方式请求失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    // 格式解压
    private static String getJsonStringFromGZIP(HttpResponse response) {
        String jsonString = null;
        try {
            InputStream is = response.getEntity().getContent();
            BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(2);
            // 取前两个字节
            byte[] header = new byte[2];
            int result = bis.read(header);
            // reset输入流到开始位置
            bis.reset();
            // 判断是否是GZIP格式
            int headerData = getShort(header);
            if (result != -1 && headerData == 0x1f8b) {
                is = new GZIPInputStream(bis);
            } else {
                is = bis;
            }
            InputStreamReader reader = new InputStreamReader(is, "utf-8");
            char[] data = new char[100];
            int readSize;
            StringBuffer sb = new StringBuffer();
            while ((readSize = reader.read(data)) > 0) {
                sb.append(data, 0, readSize);
            }
            jsonString = sb.toString();
            bis.close();
            reader.close();
        } catch (Exception e) {

        }
        return jsonString;
    }

    private static int getShort(byte[] data) {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }
}
