package com.example.administrator.hybridtestone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.administrator.hybridtestone.libcore.io.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String isType;
    private String url="http://img.my.csdn.net/uploads/201309/01/1378037235_7476.jpg";
    //private String url="http://www.ziyezhirongyao.xyz:8080/HybridTestOne/";
    private String TAG="MainActivity";
    DiskLruCache mDiskLruCache = null;
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


        //webSettings.setAppCachePath(getApplication().getCacheDir().toString());
        //设置在webview内部是否允许访问文件
        //Log.i(TAG,getApplication().getCacheDir().toString());
        webSettings.setAllowFileAccess(true);
        //设置缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webSettings.setSupportZoom(true);//支持缩放
        webSettings.setSupportMultipleWindows(true);//设置显示缩放按钮

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

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            /**
             * 解决url_unknown_scheme问题
             */
            if( url.startsWith("http:") || url.startsWith("https:") ) {
                return false;
            }
            try{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity( intent );
            }catch(Exception e){}
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
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest request){
            if (url.indexOf("?") != -1) {
                url = url.substring(0, url.lastIndexOf("?"));
                //Log.i(TAG,url+"");
            }

            try {
                /**
                 * 判断该路径是否存在，不存在就创建
                 */
                if (url.lastIndexOf("html") != -1) {
                    isType = "text/html";
                } else if (url.lastIndexOf("png") != -1) {
                    isType = "image/png";
                } else if (url.lastIndexOf("js") != -1) {
                    isType = "text/javascript";
                } else if (url.lastIndexOf("css") != -1) {
                    isType = "text/css";
                } else if (url.lastIndexOf("ico") != -1) {
                    isType = "image/ico";
                } else if (url.lastIndexOf("jpg") != -1) {
                    isType = "image/jpg";
                } else {
                    isType = "application/json";
                }
                File cacheDir = getDiskCacheDir(getApplicationContext(), isType);
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                Log.i(TAG, cacheDir + "");
                /**
                 * open方法创建缓存实例
                 */
                if (mDiskLruCache == null) {
                    mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(getApplicationContext()), 1, 10 * 1024 * 1024);


                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            final DiskLruCache finalMDiskLruCache = mDiskLruCache;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**
                         * 使用DiskLruCache.Editor进行写入
                         */

                        String key = hashKeyForDisk(url);
                        DiskLruCache.Editor editor = finalMDiskLruCache.edit(key);
                        if (editor != null) {
                            OutputStream outputStream = editor.newOutputStream(0);
                            if (downloadUrlToStream(url, outputStream)) {
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                        }
                        /**
                         * 将内存中的操作记录同步到日志文件（也就是journal文件）当中
                         * 建议Activity的onPause()方法中去调用一次flush()方法
                         */


                        finalMDiskLruCache.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Log.i(TAG,"缓存完成");
            return null;

        }


    }
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    /**
     * 获取缓存路径
     * @param context
     * @param uniqueName 对于不同数据进行区分的唯一值
     * @return
     */

    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        /**
         * 当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，否则就调用getCacheDir()方法来获取缓存路径。
         */
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取app版本号，manifest中设置版本号
     * 每当版本号改变，缓存路径下存储的所有数据都会被清除掉
     * @param context
     * @return
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
    /**
     * 写入缓存
     * @param urlString
     * @param outputStream
     * @return
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
