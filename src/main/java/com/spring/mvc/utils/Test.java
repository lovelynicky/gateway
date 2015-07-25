package com.spring.mvc.utils;


import com.spring.mvc.model.gson.SmsReturnGson;
import org.joda.time.DateTime;

/**
 * Created by liluoqi on 15/5/5.
 */
public class Test {
    public static void main(String[] args) {
        int a = 10;
        int b = 20;
        int c = a + b-- > 0 ? b-- : a;
        System.out.println("c=" + c + "b=" + b);
    }
}
