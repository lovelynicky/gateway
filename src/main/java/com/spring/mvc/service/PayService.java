package com.spring.mvc.service;

import com.spring.mvc.model.gson.AlipayPayInfoGson;
import com.spring.mvc.utils.Logger;
import com.spring.mvc.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Created by liluoqi on 15/7/3.
 */
@Service
public class PayService {

    private Logger logger = Logger.getLogger(PayService.class);

    public AlipayPayInfoGson getAlipayInfoByApplicantId(String applicantId) {
        if (StringUtils.isEmptyOrNull(applicantId)) {
            return new AlipayPayInfoGson();
        }
        return new AlipayPayInfoGson();
    }
}
