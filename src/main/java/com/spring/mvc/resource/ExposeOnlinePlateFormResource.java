package com.spring.mvc.resource;

import com.spring.mvc.service.SinoChinaService;
import com.spring.mvc.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * 提供给二维码购买后即时发送订单给中外运，提供一个接口接收电商系统传过来的订单信息
 * Created by liluoqi on 15/5/5.
 */
@Component
@Path("ecoMarket")
public class ExposeOnlinePlateFormResource {

    private Logger logger = Logger.getLogger(ExposeOnlinePlateFormResource.class);

    @Autowired
    private SinoChinaService sinoChinaService;

    /**
     * orderNo其实传过来的是ID
     *
     * @param orderNo
     * @return
     */
    @Path("sendOrderToSino")
    @POST
    public void sendOrderToSino(@FormParam("orderNo") String orderNo) {
        logger.info(String.format("收到商城传过来的订单号:%s", orderNo));
        sinoChinaService.sendOrderToSinoChina(orderNo);
//        return "{'service_result':[{'business_no':'JKF_XGYX201505201553521790603186_9bccec6c-1ccc-41ed-bb82-bc67ebda0079_20150520155352_278','chk_mark':'2','notice_time':'2015-05-20 15:53:52','notice_content':'处理异常','business_type':'ORDER','way_bills':''}]}";
    }
}
