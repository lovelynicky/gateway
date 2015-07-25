package com.spring.mvc.resource;

import com.google.gson.Gson;
import com.spring.mvc.service.PayService;
import com.spring.mvc.utils.Logger;
import com.spring.mvc.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by liluoqi on 15/7/3.
 */
@Component
@Path("pay")
public class PayResource {

    private Logger logger = Logger.getLogger(PayResource.class);

    @Autowired
    private PayService payService;

    @GET
    @Path("get-alipay-info")
    @Produces("application/json")
    public String getAlipayInfoByApplicantId(@QueryParam("applicantId") String applicantId,
                                             @QueryParam("callback") String callback) {
        try {
            logger.info(String.format("根据商户ID:%s获取支付宝支付信息", applicantId));
            return StringUtils.isEmptyOrNull(callback) ? new Gson().toJson(payService.getAlipayInfoByApplicantId(applicantId)) :
                    String.format("%s(%s)", callback, new Gson().toJson(payService.getAlipayInfoByApplicantId(applicantId)));
        } catch (Exception e) {
            logger.error(String.format("根据商户ID:%s获取支付宝支付信息异常", applicantId), e);
        }
        return StringUtils.isEmptyOrNull(callback) ? new Gson().toJson("") : String.format("%s(%s)", callback, new Gson().toJson(""));
    }

}
