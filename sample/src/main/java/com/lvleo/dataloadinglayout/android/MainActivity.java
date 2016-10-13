package com.lvleo.dataloadinglayout.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.lvleo.dataloadinglayout.DataLoadingLayout;

import org.json.JSONObject;

import java.net.SocketTimeoutException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class MainActivity extends AppCompatActivity implements DataLoadingLayout.OnViewTouchListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnNoData, btnDataLoadingSuccess, btnDataLoadingError;

    private TextView txtResult;

    private DataLoadingLayout mLoadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNoData = (Button) findViewById(R.id.btn_data_empty);
        btnDataLoadingSuccess = (Button) findViewById(R.id.btn_data_success);
        btnDataLoadingError = (Button) findViewById(R.id.btn_data_error);

        txtResult = (TextView) findViewById(R.id.txt_result);

        mLoadingLayout = (DataLoadingLayout) findViewById(R.id.loading_layout);

        mLoadingLayout.setDataView(txtResult);
        mLoadingLayout.setOnMyViewTouchListener(this);

        btnNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData("http://api.map.baidu.com/telematics/v3/weather?location=");
            }
        });
        btnDataLoadingSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData("http://api.map.baidu.com/telematics/v3/weather?location=无锡");
            }
        });
        btnDataLoadingError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData("http://api.map.baidu.com/telematics/v3/weathersssss?location=无锡");
            }
        });

        mLoadingLayout.loadSuccess("Demo演示");
    }

    @Override
    public void onTouchUp() {
        // if data load Error, can get data again by touch the view

        getData("http://api.map.baidu.com/telematics/v3/weather?location=无锡");

    }

    private void getData(String subUrl) {
        mLoadingLayout.loading();

        String url = subUrl + "&output=json&ak=6gYxFLrG9vipiq1bkQLnHhUH&" +
                "mcode=4C:45:2B:FC:13:89:0F:76:88:A8:D3:9F:69:F6:51:9C:BC:F6:9E:65;baidumapsdk.demo";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);
        client.setConnectTimeout(20 * 1000);
        client.setResponseTimeout(20 * 1000);
        client.setMaxRetriesAndTimeout(3, 20 * 1000);
        client.allowRetryExceptionClass(SocketTimeoutException.class);
        client.allowRetryExceptionClass(ConnectTimeoutException.class);
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, "onSuccess: response==" + response);
                if (response.optInt("error") == 0) {
                    mLoadingLayout.loadSuccess();

                    JSONObject object = response.optJSONArray("results").optJSONObject(0).
                            optJSONArray("weather_data").optJSONObject(0);

                    String weather = "今日天气\r\n" + object.optString("date") + "\r\n 温度：" +
                            object.optString("temperature") + "\r\n 风向：" + object.optString("wind");

                    txtResult.setText(weather);

                } else {
                    mLoadingLayout.loadSuccess("暂无数据,\n点击屏幕 重新加载 ");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                mLoadingLayout.loadError("服务器连接失败,\n点击屏幕 重新加载");

            }
        });
    }
}
