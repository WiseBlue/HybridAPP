package com.example.administrator.hybridtestone;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/7/14.
 */

public class TestActivity extends AppCompatActivity {
    private WebView webView;
    private Button button;
    private String isType;
    private boolean isNetworkConnected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        webView=(WebView)findViewById(R.id.webview);

        WebSettings webSettings=webView.getSettings();
        //支持JS
        webSettings.setJavaScriptEnabled(true);
        //DOM storage API
        webSettings.setDomStorageEnabled(true);
        //设置默认字符编码
        webSettings.setDefaultTextEncodingName("UTF-8");
        //开启数据库功能
        webSettings.setDatabaseEnabled(true);
        //开webSettings.setAppCacheEnabled(true);
        //设置缓存大小
        //webSettings.setAppCacheMaxSize(1024*1024*8);启web缓存功能
        webSettings.setAppCacheEnabled(true);
        //设置缓存大小
        //webSettings.setAppCacheMaxSize(1024*1024*8);
        //设置数据库db文件目录,没用
        //webSettings.setDatabasePath();
        //设置缓存路径
        webSettings.setAppCachePath(getApplication().getCacheDir().toString());
        //设置在webview内部是否允许访问文件
        webSettings.setAllowFileAccess(true);
        //设置缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //设置webView是否使用viewport，false加载页面的宽度总是适应WebView控件宽度
        //true当前页面包含viewport属性标签，在标签中指定宽度值生效，如果页面不包含viewport标签
        //无法提供一个宽度值，这个时候该方法将被使用
        webSettings.setUseWideViewPort(false);
        //设置setViewviewClient自定义的继承于webviewclient的类来拦截一些JS
        webView.setWebViewClient(new MyWebViewClient());
        //提高渲染优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //阻塞图片让图片不显示
        webSettings.setBlockNetworkImage(true);
        //页面加载好，放开图片
        webSettings.setBlockNetworkImage(false);
        //此设置是否保存H5表单数据
        webSettings.setSaveFormData(false);
        //
        //webView.setWebChromeClient(new );
        //此设置为添加JS对象
        //webView.addJavascriptInterface(new TrueName(MainActivity.this),"personName");
    }

    //监听手机返回键并设置
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode==KeyEvent.KEYCODE_BACK)&&webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    //自定义的webviewclient
    class MyWebViewClient extends WebViewClient {
        //此方法可以使H5在webview中显示
        public boolean shouldOverrideUrlLoading(WebView view,String url){
            view.loadUrl(url);
            return true;
        }
        //访问url开始时候触发
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        //访问url结束时候触发
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        //重写该方法可以加载一些手机本地资源
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

        //与上方法一样
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }
    }


    //原生与JS进行通信
    /*
    *与JS交互弹出实名认证界面
     */
    public class TrueName{
        Context context;

        public TrueName(Context context){
            this.context=context;
        }

        //@JavascriptInterface
        /*public void trueName(){
            startActivity(new Intent(context,IdCardActivity.class));
        }
        *//**
         * 跳转身份证页面
         * String flag就是H5给原生传的参数，H5在JS中写入personName.trueName("小明"),原生就可以接收到信息
         *//*
        @JavascriptInterface
        public void trueName(String flag) {
            Log.i("cpf", "flag====" + flag);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, IdCardActivity.class);
            intent.putExtra("infoFlag", flag);
            intent.putExtra("userId", SyncStateContract.Constants._ID);
            startActivity(intent);
        }*/

    }

    //webview自定义缓存
    public WebResourceResponse shouldInterceptRequest(WebView view,String url){
        //读取当前webview正准备加载URL资源
        if (url.indexOf("?")!=-1){
            url=url.substring(0,url.lastIndexOf("?"));
        }

        try{
            //根据资源url获取一个你要缓存的本地文件名，一般是URl的MD5
            String resFileName=getResourcesFileName(url);
            if (TextUtils.isEmpty(resFileName)){
                return null;
            }
            if (url.lastIndexOf("html")!=-1){
                isType="text/html";
            }else if (url.lastIndexOf("png")!=-1){
                isType="image/png";
            }else if (url.lastIndexOf("js")!=-1){
                isType="text/javascript";
            }else if (url.lastIndexOf("css")!=-1){
                isType="text/css";
            }else if (url.lastIndexOf("ico")!=-1){
                isType="image/ico";
            }else if (url.lastIndexOf("jpg")!=-1){
                isType="image/jpg";
            }else {
                isType="application/json";
            }
            /*if (isNetworkConnected){
                if (url.indexOf())
            }*/
            DVDUrlCache urlCache=new DVDUrlCache(getApplicationContext(),isType);
            urlCache.register(url,resFileName,isType,"UTF-8",DVDUrlCache.ONE_DAY);
            Log.i("urlCache",url+"===="+resFileName+"==="+isType);
            if (isNetworkConnected){
                urlCache.load(url);
                return null;
            }else {
                return urlCache.load(url);
            }
        }catch (Exception e){

        }
        return null;
    }
    //同时用到了一个转换MD5名字的方法，如下
    public static String getResourcesFileName(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }


        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
