package com.lvleo.dataloadinglayout.android;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.lvleo.dataloadinglayout.DataLoadingLayout;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class MainActivity extends AppCompatActivity implements DataLoadingLayout.OnViewTouchListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnNoData, btnDataLoadingSuccess, btnDataLoadingError;

    private TextView txtResult;

    private DataLoadingLayout mLoadingLayout;

    static final String CIPHER_TYPE = "RSA/ECB/PKCS1Padding";
    static final String CIPHER_PROVIDER = "AndroidOpenSSL";
    KeyStore keyStore;
    String alias = "jrc";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            keyStore = KeyStore.getInstance("AndroidKeyStore");
//            keyStore.load(null);
//            Log.e(TAG, "createNewKeys: alias=" + alias);
//
//            boolean hasCreateAlias = keyStore.containsAlias(alias);
//            Log.e(TAG, "createNewKeys: hasCreateAlias=" + hasCreateAlias);
//
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//
//
//        String password = "123456";
//        String aa = encryptString(password);
//        decryptString(aa);

//        startAppModifyServiceMonitor();

        btnNoData = (Button) findViewById(R.id.btn_data_empty);
        btnDataLoadingSuccess = (Button) findViewById(R.id.btn_data_success);
        btnDataLoadingError = (Button) findViewById(R.id.btn_data_error);

        txtResult = (TextView) findViewById(R.id.txt_result);

        mLoadingLayout = (DataLoadingLayout) findViewById(R.id.loading_layout);

//        TextView textView = mLoadingLayout.getTextViewStatus();
//        textView.setTextColor(Color.RED);
//        textView.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_sad, 0, 0);

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

        txtResult.setText("点击切换上方的按钮查看实际效果");
//        mLoadingLayout.setStatusTopIcon(R.mipmap.ic_sad);

    }

    @Override
    public void onTouchUp() {
        // if data load Error or data is empty, can get data again by touch the vie
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
                    mLoadingLayout.loadSuccess("暂无数据\n点击屏幕 重新加载 ");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mLoadingLayout.loadError("服务器连接失败\n点击屏幕 重新加载");
            }
        });
    }

    /**
     * 开启程序安装卸载覆盖安装广播服务监听
     */
    public void startAppModifyServiceMonitor(){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.djy.restart.receiver",
                "com.djy.restart.receiver.MyService");
        intent.setComponent(componentName);
        startService(intent);
        Log.e(TAG, "======startAppModifyServiceMonitor======");
    }


    public String encryptString(String data) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Log.e(TAG, "encryptString: publicKey=" + publicKey);

            String initialText = data;
            if (initialText.isEmpty()) {
                Toast.makeText(this, "Enter text in the 'Initial Text' widget", Toast.LENGTH_LONG).show();
            }

            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(initialText.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            Log.e(TAG, "encryptString: Base64.encodeToString(vals, Base64.DEFAULT)=" +
                    Base64.encodeToString(vals, Base64.DEFAULT));

            return Base64.encodeToString(vals, Base64.DEFAULT);

        } catch (Exception e) {
            Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return "";

    }

    public void decryptString(String data) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            Log.e(TAG, "decryptString: privateKey=" + privateKey);


            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKey);

            String cipherText = data;
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String finalText = new String(bytes, 0, bytes.length, "UTF-8");


            Log.e(TAG, "decryptString: finalText=" + finalText);

        } catch (Exception e) {
            Toast.makeText(this, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }


}
