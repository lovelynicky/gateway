package com.spring.mvc.utils;


import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liluoqi on 15/4/25.
 */
public class HttpClientUtils {
    private static Logger logger = Logger.getLogger(HttpClientUtils.class);
    private static RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
    private static CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

    /**
     * 调用中外运的接口发送业务数据并返回结果
     *
     * @param url  访问的URL
     * @param data 发送的数据，这个接口最终会以json的方式post过去
     * @return 返回json格式，仅仅满足于返回的数据格式是json的，其他的类型的没有封装
     */
    public static <T> T postJson(String url, Object data, Class<T> tClass) {
        try {
            logger.info(String.format("发送到中外运的业务数据是:%s", new Gson().toJson(data)));
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(new Gson().toJson(data)));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = StringUtils.emptyString();
            if (entity != null) {
                result = IOUtils.toString(entity.getContent(), "UTF-8");
            }
            httpPost.abort();
            logger.info(String.format("发送中外运订单返回结果是:%s", result));
            return new Gson().fromJson(result, tClass);
        } catch (UnsupportedEncodingException e) {
            logger.error(String.format("不支持的编码格式"), e);
        } catch (ClientProtocolException e) {
            logger.error(String.format("客户端协议异常"), e);
        } catch (IOException e) {
            logger.error(String.format("IO异常"), e);
        } catch (Exception e) {
            logger.error(String.format("发送中外运业务数据出现异常"), e);
        } finally {
//            try {
//                logger.info(String.format("尝试关闭客户端连接，释放http请求"));
//                httpClient.close();
//            } catch (IOException e) {
//                logger.warn(String.format("尝试关闭http连接发生IO异常"), e);
//            }
        }
        return null;
    }

    /**
     * 发送业务数据之前要调用此接口获取token信息，发送订单数据，该接口使用post form的形式。token的有效期只有两个小时
     *
     * @param url 中外运的获取token的url
     * @param map 表单数据，是一个map形式
     * @return 仅仅满足于返回的数据格式是json的，其他的类型的没有封装
     */
    public static <T> T postForm(String url, Map<String, String> map, Class<T> tClass) {
        try {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            for (String key : map.keySet()) {
                formParams.add(new BasicNameValuePair(key, map.get(key)));
            }
            HttpPost httpPost = new HttpPost(url);
            HttpEntity httpEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            httpPost.setEntity(httpEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = StringUtils.emptyString();
            if (entity != null) {
                result = IOUtils.toString(entity.getContent(), "UTF-8");
            }
            httpPost.abort();
            return new Gson().fromJson(result, tClass);
        } catch (UnsupportedEncodingException e) {
            logger.error(String.format("不支持的字符编码"), e);
        } catch (IOException e) {
            logger.error(String.format("IO异常"), e);
        } catch (Exception e) {
            logger.error(String.format("获取中外运token出现异常"), e);
        } finally {
//            try {
//                logger.info(String.format("尝试关闭客户端连接，释放http请求"));
//                httpClient.close();
//            } catch (IOException e) {
//                logger.warn(String.format("尝试关闭http连接发生IO异常"), e);
//                e.printStackTrace();
//            }
        }
        return null;
    }


    /**
     * 发送业务数据之前要调用此接口获取token信息，发送订单数据，该接口使用post form的形式。token的有效期只有两个小时
     *
     * @param url 中外运的获取token的url
     * @param map 表单数据，是一个map形式
     * @return 返回response里面的字符串
     */
    public static String postForm(String url, Map<String, String> map) {
        try {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            for (String key : map.keySet()) {
                formParams.add(new BasicNameValuePair(key, map.get(key)));
            }
            HttpPost httpPost = new HttpPost(url);
            HttpEntity httpEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            httpPost.setEntity(httpEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = StringUtils.emptyString();
            if (entity != null) {
                result = IOUtils.toString(entity.getContent(), "UTF-8");
            }
            httpPost.abort();
            return result;
        } catch (UnsupportedEncodingException e) {
            logger.error(String.format("不支持的字符编码"), e);
        } catch (IOException e) {
            logger.error(String.format("IO异常"), e);
        } catch (Exception e) {
            logger.error(String.format("获取中外运token出现异常"), e);
        } finally {
//            try {
//                logger.info(String.format("尝试关闭客户端连接，释放http请求"));
//                httpClient.close();
//            } catch (IOException e) {
//                logger.warn(String.format("尝试关闭http连接发生IO异常"), e);
//                e.printStackTrace();
//            }
        }
        return null;
    }


    /**
     * @param url 中外运的获取token的url
     * @param map 表单数据，是一个map形式
     * @return 返回 response
     */
    public static CloseableHttpResponse post(String url, Map<String, String> map) {
        try {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            for (String key : map.keySet()) {
                formParams.add(new BasicNameValuePair(key, map.get(key)));
            }
            HttpPost httpPost = new HttpPost(url);
            HttpEntity httpEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            httpPost.setEntity(httpEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            httpPost.abort();
            return response;
        } catch (UnsupportedEncodingException e) {
            logger.error(String.format("不支持的字符编码"), e);
        } catch (IOException e) {
            logger.error(String.format("IO异常"), e);
        } catch (Exception e) {
            logger.error(String.format("获取中外运token出现异常"), e);
        } finally {
//            try {
//                logger.info(String.format("尝试关闭客户端连接，释放http请求"));
//                httpClient.close();
//            } catch (IOException e) {
//                logger.warn(String.format("尝试关闭http连接发生IO异常"), e);
//                e.printStackTrace();
//            }
        }
        return null;
    }
}
