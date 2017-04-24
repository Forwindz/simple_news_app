package cn.forwindz.news.tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Forwindz on 2017/4/15.
 * 一大堆功能
 */

public class HtmlTools {


    private final static String LOG_TAG = "HtmlTools";

    /*
     * 这个方法必须放在线程里run
     */
    @Nullable
    public static String getHtml(String url, boolean retryIfFail) {
        Log.d(LOG_TAG, "URL=" + url);
        StringBuffer sb;
        boolean success;
        HttpURLConnection c;
        //html
        do {
            sb = new StringBuffer(1024 * 64);//64kb
            try {
/*
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
                HttpsURLConnection con;
                con = (HttpsURLConnection) new URL(url).openConnection();
                con.setDoOutput(false);
                con.setDoInput(true);

                con.connect();
                */
                c = (HttpURLConnection) new URL(url).openConnection();
                //context.setConnectTimeout(20000);
                //context.setReadTimeout(30000);

                //context.setUseCaches(false);
                //context.setRequestMethod("GET");
                c.setDoOutput(true);
                c.setDoInput(true);
                c.addRequestProperty("User-Agent","Mozilla/5.0 (jsoup)");

                //context.setDoOutput(false);
                //context.setDoInput(true);
                //context.addRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                /*context.addRequestProperty("Accept-Encoding","gzip, deflate, sdch");
                context.addRequestProperty("Accept-Language","zh-CN,zh;q=0.8");;
                context.addRequestProperty("Cache-Control","max-age=0");
                context.addRequestProperty("Connection","keep-alive");
                context.addRequestProperty("Cookie","UOR=,news.sina.com.cn,; SINAGLOBAL=223.80.110.112_1492249312.260095; SUB=_2AkMvrWXQf8NxqwJRmP4XxGznb4t0zwvEieKZ8ZQLJRMyHRl-yD9kqnwhtRAP8-xHxojQUAwp-pZsvWLo2Gca7Q..; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9WW_FiUqOXJRg6i_b8a.Hdo7; U_TRS1=00000070.a3f040b7.58f1fd11.9a034592; Apache=223.80.110.112_1492316349.227097; SGUID=1492316505978_29754595; ULV=1492316506650:7:7:2:223.80.110.112_1492316349.227097:1492316504530; rotatecount=1; U_TRS2=00000070.34d17108.58f2f10f.605d28a6; SessionID=5ml7ida1rjjm0s53biieiia6r4; video_cookie=b; lxlrttp=1492109074");
                context.addRequestProperty("Host","www.sina.com.cn");
                context.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
                */
                        //"User-Agent:Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_2 like Mac OS X) App leWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile/11D257 Safari/9537.53");
                //context.addRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
                //context.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
                //context.addRequestProperty("Cache-Control", "max-age=0");
                //context.addRequestProperty("Connection", "keep-alive");
                //context.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                //context.addRequestProperty();
                //context.addRequestProperty();
                c.connect();
                Log.d(LOG_TAG, "connected");

                int status = c.getResponseCode();
                InputStream inputStream;
                if (status != HttpURLConnection.HTTP_OK) {
                    inputStream = c.getErrorStream();
                    //success = false;
                } else {
                    inputStream = c.getInputStream();
                }

                BufferedReader input = new BufferedReader(new InputStreamReader(
                        inputStream));
                char[] ch = new char[1024];
                int readLen;
                while ((readLen=input.read(ch)) > 0) {
                    //Log.d(LOG_TAG,""+String.valueOf(ch));
                    sb.append(ch,0,readLen);
                }
                c.disconnect();
                //Log.d(LOG_TAG, "" + sb);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            }
            if (!success) {
                try {
                    Thread.sleep((long) (2333 + Math.random() * 1234));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (retryIfFail && !success);
        Log.d(LOG_TAG, "len=" + sb.length());
        if (success) {
            return sb.toString();
        } else {
            Log.d(LOG_TAG, "fail to get");
            return null;
        }
    }

    @Nullable
    public static Bitmap getBitmapFromUrl(String urlString){
        if (urlString.startsWith("//"))urlString=urlString.replace("//","http://");
        //Log.d(LOG_TAG,"pic_url = "+urlString);
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL mUrl= new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap=BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Drawable getDrawableFromUrl(String urlString, Resources res){
        if (urlString.startsWith("//"))urlString=urlString.replace("//","https://");
        Bitmap bitmap = getBitmapFromUrl(urlString);
        return new BitmapDrawable(res,bitmap);
    }

}
