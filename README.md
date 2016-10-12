# DataLoadingLayout
## Description
DataLoadingLayout is a simple library for Android. It's a layout to show current data loading status(loading, load success,load error)
A custom view for data loading status on Android
一个根据当前数据加载状态，显示对应界面（加载中，加载成功、加载失败）提示的简单控件

## Usage

#Setp 1:Add the dependency:
### gradle:
open the build.gradle file of your module, at the dependencies function add the below code:
```groovy
	compile 'com.lvleo:data-loading-layout:0.1.0'
```

#Setp 2:Add the view to your layout.xml file:

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
#Setp 3: The activity implements DataLoadingLayout.OnViewTouchListener, after init the view setDataView and setOnMyViewTouchListener,
final Override the onTouchUp() function. like this:

```groovy
public class MainActivity extends AppCompatActivity implements DataLoadingLayout.OnViewTouchListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView txtResult;

    private DataLoadingLayout mLoadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResult = (TextView) findViewById(R.id.txt_result);

        mLoadingLayout = (DataLoadingLayout) findViewById(R.id.loading_layout);

        mLoadingLayout.setDataView(txtResult);

        mLoadingLayout.setOnMyViewTouchListener(this);

        mLoadingLayout.loadSuccess("Demo演示");
    }

    @Override
    public void onTouchUp() {
        // if data load error ,can get data again by touch the view
    }
}
```

Finally, if you still don't know how to use it, you can view the example code.

# XML attributes
| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|statusTextSize|dimension|14|the status string's font size|
|statusTextColor|color|Color.GRAY|the status string's color|
|statusText|string|No Data|the status default content|
|loadingBarColor|color|Color.GRAY|the ProgressBar's color|
|loadingBarSize|dimension|the ProgressBar's size|


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