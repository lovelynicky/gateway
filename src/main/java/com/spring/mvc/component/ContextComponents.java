package com.spring.mvc.component;

import com.spring.mvc.service.RepaymentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liluoqi on 15/7/23.
 */
@Component
public class ContextComponents {
    @Autowired
    private RepaymentPlanService repaymentPlanService;

    public RepaymentPlanService getRepaymentPlanService() {
        return repaymentPlanService;
    }
}
