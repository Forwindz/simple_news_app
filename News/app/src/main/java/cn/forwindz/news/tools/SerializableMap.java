package cn.forwindz.news.tools;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/18.
 * 仅仅为了传输数据用
 */

public class SerializableMap<K,V> implements Serializable{

    public Map<K,V> myMap;
    public SerializableMap(Map<K,V> map){
        myMap=map;
    }
}
