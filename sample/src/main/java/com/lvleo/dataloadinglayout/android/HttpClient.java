package com.lvleo.dataloadinglayout.android;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.net.SocketTimeoutException;

import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

/**
 * @Author: lvyongxu
 * @Date: 10:48 2015/10/13
 */
public class HttpClient {

    private static final String TAG = HttpClient.class.getSimpleName();

    //    private static final String BASE_URL = "http://soft.jsy118.com/jsy/";
    private static final String BASE_URL = "http://jsy118.com/";//正式服务器
//    http://soft.jsy118.com/jsy/jsyorder/getnosureorders
//    private static final String BASE_URL = "http://ts.do-wi.cn/nsh/appapi/";

    private static AsyncHttpClient client = new AsyncHttpClient();


    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(20 * 1000);
        client.setConnectTimeout(20 * 1000);
        client.setResponseTimeout(20 * 1000);
        client.setMaxRetriesAndTimeout(3, 20 * 1000);
        client.allowRetryExceptionClass(SocketTimeoutException.class);
        client.allowRetryExceptionClass(ConnectTimeoutException.class);
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void post(String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(20 * 1000);
        client.setConnectTimeout(20 * 1000);
        client.setResponseTimeout(20 * 1000);
        client.setMaxRetriesAndTimeout(3, 20 * 1000);
        client.allowRetryExceptionClass(SocketTimeoutException.class);
        client.allowRetryExceptionClass(ConnectTimeoutException.class);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.e(TAG, "Url===" + BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }

}
