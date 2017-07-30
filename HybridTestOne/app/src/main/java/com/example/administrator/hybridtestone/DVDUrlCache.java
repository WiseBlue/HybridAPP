package com.example.administrator.hybridtestone;

import android.content.Context;
import android.util.Log;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2017/7/12.
 */

public class DVDUrlCache {

    private static final String LOG_TAG = "DVDUrlCache";

    private static final long ONE_SECOND = 1000L;
    private static final long ONE_MINUTE = 60L * ONE_SECOND;
    static final long ONE_HOUR = 60 * ONE_MINUTE;
    static final long ONE_DAY = 24 * ONE_HOUR;
    static final long ONE_MONTH = 30 * ONE_DAY;


    private static final LinkedHashMap<String, Callable<Boolean>> queueMap = new LinkedHashMap<>();


    private static class CacheEntry {
        public String url;
        public String fileName;
        String mimeType;
        public String encoding;
        long maxAgeMillis;

        private CacheEntry(String url, String fileName,
                           String mimeType, String encoding, long maxAgeMillis) {
            this.url = url;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.encoding = encoding;
            this.maxAgeMillis = maxAgeMillis;
        }
    }

    private Map<String, CacheEntry> cacheEntries = new HashMap<>();
    private File rootDir = null;

    DVDUrlCache(Context context,String type) {
    //本地缓存路径，请在调试中自行修改
        this.rootDir =context.getExternalFilesDir(type);
    }

    public void register(String url, String cacheFileName,
                         String mimeType, String encoding,
                         long maxAgeMillis) {
        CacheEntry entry = new CacheEntry(url, cacheFileName, mimeType, encoding, maxAgeMillis);
        this.cacheEntries.put(url, entry);
    }

    public WebResourceResponse load(final String url) {
        try {
            final CacheEntry cacheEntry = this.cacheEntries.get(url);
            if (cacheEntry == null) {
                return null;
            }

            final File cachedFile = new File(this.rootDir.getPath() + File.separator + cacheEntry.fileName);
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "cachedFile is " + cachedFile);
            }
            if (cachedFile.exists() && isReadFromCache(url)) {
                //还没有下载完
                if (queueMap.containsKey(url)) {
                    return null;
                }
                long cacheEntryAge = System.currentTimeMillis() - cachedFile.lastModified();
                if (cacheEntryAge > cacheEntry.maxAgeMillis) {
                    cachedFile.delete();
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "Deleting from cache: " + url);
                    }
                    return null;
                }
                //cached file exists and is not too old. Return file.
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, url + " ### cache file : " + cachedFile.getAbsolutePath());
                }
                return new WebResourceResponse(
                        cacheEntry.mimeType, cacheEntry.encoding, new FileInputStream(cachedFile));
            } else {
                if (!queueMap.containsKey(url)) {
                    queueMap.put(url, new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return downloadAndStore(url, cacheEntry);
                        }
                    });
                    final FutureTask<Boolean> futureTask = ThreadPoolManager.getInstance().addTaskCallback(queueMap.get(url));
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (futureTask.get()) {
                                    if (BuildConfig.DEBUG) {
                                        Log.d(LOG_TAG, "remove " + url);
                                    }
                                    queueMap.remove(url);
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                Log.d(LOG_TAG, "", e);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error reading file over network: ", e);
        }
        return null;
    }

    private boolean downloadAndStore(final String url, final CacheEntry cacheEntry)
            throws IOException {
        FileOutputStream fileOutputStream = null;
        InputStream urlInput = null;
        try {
            URL urlObj = new URL(url);
            URLConnection urlConnection = urlObj.openConnection();
            urlInput = urlConnection.getInputStream();
            String tempFilePath = DVDUrlCache.this.rootDir.getPath() + File.separator + cacheEntry.fileName + ".temp";
            File tempFile = new File(tempFilePath);
            fileOutputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = urlInput.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.flush();
            File lastFile = new File(tempFilePath.replace(".temp", ""));
            boolean renameResult = tempFile.renameTo(lastFile);
            if (!renameResult) {
                Log.w(LOG_TAG, "rename file failed, " + tempFilePath);
            }
//            Log.d(LOG_TAG, "Cache file: " + cacheEntry.fileName + " stored. ");
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "", e);
        } finally {
            if (urlInput != null) {
                urlInput.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        return false;
    }

    private boolean isReadFromCache(String url) {
        return true;
    }

}