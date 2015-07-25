package com.spring.mvc.utils;

/**
 * Created by liluoqi on 15/4/29.
 */

import java.text.DecimalFormat;
import java.util.Date;

/**
 * businessNo生成器
 */
public class BusinessNoGenerator {

    public static String generateSinoBusinessNo(String orderNo, String appId) {
        return String.format("JKF_%s_%s_%s_%s", orderNo, appId,
                DateUtils.formatDateToSecondsWithoutDecoration(new Date()), RandomUtils.getThreeRandomNums());
    }

    public static String generateEcoBusinessNo(String orderNo) {
        return String.format("JKF_ECO_%s_%s_%s", orderNo,
                DateUtils.formatDateToSecondsWithoutDecoration(new Date()), RandomUtils.getThreeRandomNums());
    }

    public static String generateOrderNo() {
        return String.format("%s%s%s", "XGYX", DateUtils.formatDateToMilliSecondsWithoutDecoration(new Date()), RandomUtils.getSevenRandomNums());
    }

    public static String generateAlipayRequestNo() {
        return String.format("%s%s",
                DateUtils.formatDateToSecondsWithoutDecoration(new Date()), RandomUtils.getThreeRandomNums());
    }

    public static String generateSmsValidateCode() {
        return String.format("%s", RandomUtils.getSixRandomNums());
    }

    public static String generatePasswordResetCode() {
        return String.format("%s", RandomUtils.getSixRandomNums());
    }

    public static void main(String[] args) {
        System.out.println(generateOrderNo());
    }
}
