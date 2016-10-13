# DataLoadingLayout
## Description
DataLoadingLayout is a simple library for Android. It's a layout to show current data loading status(loading, load success,load error)

##Screenshots
![](https://github.com/lvleo/DataLoadingLayoutAndroid/blob/master/screen/screen.gif)

## Usage

###Step 1:Add the dependency:

open the build.gradle file of your module, at the dependencies function add the below code:

```groovy
	compile 'com.lvleo:data-loading-layout:0.1.0'
```

###Step 2:Add the view to your layout.xml file:

```groovy
<com.lvleo.dataloadinglayout.DataLoadingLayout
        android:id="@+id/loading_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@id/layout_buttons"
        android:background="@android:color/darker_gray"
        app:loadingBarColor="@android:color/holo_purple"
        app:statusText="暂无数据"
        app:statusTextColor="@android:color/black"
        app:statusTextSize="16sp"
        />
```
###Step 3: The activity implements DataLoadingLayout.OnViewTouchListener, after init the view setDataView(view) and setOnMyViewTouchListener(this),final Override the onTouchUp() function. like this:

```groovy
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
                } else {
                    mLoadingLayout.loadSuccess("暂无数据");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                mLoadingLayout.loadError("服务器连接失败,\n点击当前页面重新获取数据");

            }
        });
    }
}
```

## XML attributes
| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|statusTextSize|dimension|14|the status string's font size|
|statusTextColor|color|Color.GRAY|the status string's color|
|statusText|string|No Data|the status default content|
|loadingBarColor|color|Color.BLUE|the ProgressBar's color|
|loadingBarSize|dimension|48|the ProgressBar's size|


##About me
[Weibo](http://weibo.com/2265549640)

License
-------

    Copyright 2016 lvleo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.