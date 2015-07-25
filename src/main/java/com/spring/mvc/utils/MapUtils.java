package com.spring.mvc.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liluoqi on 15/4/22.
 */
public class MapUtils {

    public static Map<Object,Object> convertToMap(Object... objects){
        try {
            Map<Object,Object> map=new HashMap<Object, Object>();
            if(objects.length%2!=0||objects.length==0){
                return map;
            }
            for(int index=0;index<objects.length-1;index=index+2){
                map.put(objects[index],objects[index+1]);
            }
            return map;
        }catch (Exception e){
            return new HashMap<Object, Object>();
        }
    }

    public static Map<String,String> convertStringToMap(String... objects){
        try {
            Map<String,String> map=new HashMap<String, String>();
            if(objects.length%2!=0||objects.length==0){
                return map;
            }
            for(int index=0;index<objects.length-1;index=index+2){
                map.put(objects[index],objects[index+1]);
            }
            return map;
        }catch (Exception e){
            return new HashMap<String, String>();
        }
    }
}
