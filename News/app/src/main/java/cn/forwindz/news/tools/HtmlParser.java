package cn.forwindz.news.tools;

import android.text.Html;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Forwindz on 2017/4/15.
 * Prarse HTML
 */

public class HtmlParser {

    private String s;
    private int nowPos = 0;

    private ArrayList<Pattern> list;//存储预编译好的pattern
    private ArrayList<String> head, tail;

    public HtmlParser() {
        list = new ArrayList<>();
        head = new ArrayList<>();
        tail = new ArrayList<>();
    }

    public HtmlParser(String beginString) {
        this();
        s = beginString;
    }

    public HtmlParser(int initCap) {
        list = new ArrayList<>(initCap);
        head = new ArrayList<>(initCap);
        tail = new ArrayList<>(initCap);
    }

    public void preCompile(String head, String tail,boolean allowEnter) {
        Pattern pattern;
        if (allowEnter){
            pattern= Pattern.compile(head + "[\\s\\S]*?" + tail);
        }else {
            pattern= Pattern.compile(head + "(.*)" + tail);
        }
        list.add(pattern);
        this.head.add(head);
        this.tail.add(tail);
    }

    public void preCompile(String s1) {
        Pattern pattern = Pattern.compile(s1 + "(.*)");
        list.add(pattern);
        this.head.add(s1);
        this.tail.add("");
    }
/*
    public String findNext(String head,String tail){
        Pattern pattern = Pattern.compile(head+"(.*)"+tail);
        Matcher matcher = pattern.matcher(s);
        String temp;
        if(matcher.find(nowPos)){
            temp=matcher.group();
            nowPos=matcher.regionEnd();
            return temp.replace(head,"").replace(tail,"");
        }else{
            return null;
        }
    }*/

    public boolean moveToNext(int index) {
        Matcher matcher = list.get(index).matcher(s);
        if (matcher.find(nowPos)) {
            nowPos = matcher.end();
            return true;
        } else return false;
    }

    public String findNext(int index) {
        /*Matcher matcher = list.get(index).matcher(s);
        String temp;
        if (matcher.find(nowPos)) {
            temp = matcher.group();
            nowPos = matcher.end();
            return temp.replace(head.get(index), "").replace(tail.get(index), "");
        } else {
            return null;
        }*/
        return findAndContain(index,false,false);
    }

    public String findAndContain(int index,boolean containPrefix,boolean containSuffix){
        Matcher matcher = list.get(index).matcher(s);
        String temp;
        if (matcher.find(nowPos)) {
            temp = matcher.group();
            nowPos = matcher.end();
            if (!containPrefix){
                temp=temp.replace(head.get(index),"");
            }
            if (!containSuffix){
                temp=temp.replace(tail.get(index),"");
            }
            return temp;
        } else {
            return null;
        }
    }

    public void resetPos() {
        nowPos = 0;
    }

    public void setString(String s) {
        this.s = s;
        nowPos = 0;
    }

}
