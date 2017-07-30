package com.example.administrator.hybridtestone;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String url="http://www.ziyezhirongyao.xyz:8080/HybridTestOne/";
    private String TAG="MainActivity";
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView=(WebView)findViewById(R.id.webview);
    
        WebSettings webSettings=webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小


        webSettings.setAppCacheEnabled(true);
        //设置缓存大小
        //webSettings.setAppCacheMaxSize(1024*1024*8);


        webSettings.setAppCachePath(getApplication().getCacheDir().toString());
        //设置在webview内部是否允许访问文件
        Log.i(TAG,getApplication().getCacheDir().toString());
        webSettings.setAllowFileAccess(true);
        //设置缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.setWebViewClient(new MyWebViewClient());


        webView.loadUrl(url);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode==KeyEvent.KEYCODE_BACK)&&webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    class MyWebViewClient extends WebViewClient {
        //此方法可以使H5在webview中显示
        public boolean shouldOverrideUrlLoading(WebView view,String url){
            view.loadUrl(url);
            return true;
        }
        //访问url开始时候触发
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            webView.getSettings().setBlockNetworkImage(true);
        }

        //访问url结束时候触发
        public void onPageFinished(WebView view, String url) {
            webView.getSettings().setBlockNetworkImage(false);
        }


    }
}
