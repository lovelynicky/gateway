package com.spring.mvc.resource;

import com.google.gson.Gson;
import com.spring.mvc.component.ContextComponents;
import com.spring.mvc.model.RepaymentPlanModel;
import com.spring.mvc.model.RepaymentPlanStatus;
import com.spring.mvc.model.serviceResult.SinoChinaResultModel;
import com.spring.mvc.repository.RepaymentPlanRepository;
import com.spring.mvc.service.PushPayInfoToAlipayService;
import com.spring.mvc.service.RepaymentPlanService;
import com.spring.mvc.utils.HttpClientUtils;
import com.spring.mvc.utils.MapUtils;
import com.spring.mvc.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Map;

import static com.spring.mvc.model.RepaymentPlanStatus.UNSETTLED;

/**
 * Created by liluoqi on 15/6/6.
 */
@Component
public class TestResource {

    @Autowired
    private RepaymentPlanService repaymentPlanService;

    private int userId;

    public TestResource() {
    }

    public TestResource(int userId, ContextComponents contextComponents) {
        this.userId = userId;
        this.repaymentPlanService = contextComponents.getRepaymentPlanService();
    }

    @GET
    @Path("print")
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@QueryParam("orderNo") String orderNo) {
        repaymentPlanService.addRepaymentPlan(orderNo);
        return !StringUtils.isEmptyOrNull(String.valueOf(userId)) ? new Gson().toJson(userId) : new Gson().toJson("无效的userId");
    }
}
