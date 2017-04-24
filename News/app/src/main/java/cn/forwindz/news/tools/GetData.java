package cn.forwindz.news.tools;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/15.
 */

public class GetData {

    private final static String LOG_TAG = "GetData";

    private static HtmlParser hp;

    static {
        hp = new HtmlParser(3);
        //hp.preCompile("<div class=\"f_slid_item\">","");//第一个项目开始
        //hp.preCompile("title=\"","\">");//标题
        //hp.preCompile("<img src=\"","\"");//图片链接
        //hp.preCompile("<dt class=\"f_card_dt\">");//卡片开始-------------------
        hp.preCompile("<a href=\"", "\" data-docid=\"",false);//链接
        hp.preCompile("\" data-src=\"","\" class=\"f_card_dt_img\"",false);//图片
        hp.preCompile("<h4 class=\"f_card_h4\">", "</h4>",false);//标题
        //================================================
        //正文筛选开始
        hp.preCompile("<meta property=\"article:published_time\" content=\"",
                "\" />",false);//时间
        hp.preCompile("<p class=\"art_t\">","<div id='",true);//正文
        Log.d(LOG_TAG, "compile complete");
    }


    public static List<Map<String, String>> getListData() {
        String html = HtmlTools.getHtml("https://news.sina.cn/?from=wap&HTTPS=1", false);
        //String html = HtmlTools.getHtml("http://news.sina.com.cn/", false);
        hp.setString(html);
        String temp;
        Map<String, String> map;
        List<Map<String, String>> list = new ArrayList<>(20);
        int i=0;
        while (true) {
            map = new HashMap<>();
            temp = hp.findNext(0);
            if (temp == null) break;
            temp=temp.replace("http://","https://").replace("vt=4&amp;pos=3","vt=4&pos=3&HTTPS=1");
            //http://news.sina.cn/gj/2017-04-16/detail-ifyeimqc4054512.d.html?vt=4&amp;pos=3
            //https://news.sina.cn/gj/2017-04-16/detail-ifyeimqc4054512.d.html?vt=4&pos=3&HTTPS=1
            //地址有所变化，需要纠正
            map.put("link", temp);
            //Log.d(LOG_TAG, "link=" + temp);
            temp = hp.findNext(1);
            if (temp == null) break;
            map.put("pic", temp);
            //Log.d(LOG_TAG, "pic=" + temp);
            temp = hp.findNext(2);
            if (temp == null) break;
            map.put("head", temp);
            if(i>=1&&temp.equals(list.get(i-1).get("head"))){
                continue;
            }
            //Log.d(LOG_TAG, "head=" + temp);
            //Log.d(LOG_TAG, "------------------------------");
            list.add(map);
            i++;
        }
        return list;
    }

    public static String getNewsContent(String url){
        String html=HtmlTools.getHtml(url,false);
        hp.setString(html);
        //logLongString(LOG_TAG,html+"==================================\n");
        String time=hp.findNext(3);
        hp.resetPos();
        String temp=hp.findAndContain(4,true,false);
        //logLongString(LOG_TAG,temp+"");
        return time+"\n"+temp;
    }

    public static void logLongString(String tag,String s){
        int len=s.length();
        int i=0;
        while(i<=len/4000){
            Log.d(tag,s.substring(i*4000,Math.min((i+1)*4000,len)));
            i++;
        }
    }
}
