package com.spring.mvc.resource;

import com.google.gson.Gson;
import com.spring.mvc.service.PushPayInfoToAlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by liluoqi on 15/6/7.
 */
@Component
@Path("alipay")
public class PushPayNumberToAlipayResource {

    @Autowired
    private PushPayInfoToAlipayService pushPayInfoToAlipayService;
    /**
     * 推送支付信息给海关
     */
    @Path("push-payInfo")
    @GET
    @Produces(MediaType.TEXT_XML)
    public String pushPayInfoToAlipay(@QueryParam("orderId")int orderId){
        return new Gson().toJson(pushPayInfoToAlipayService.pushPayInfoAlipay(orderId));
    }
}
