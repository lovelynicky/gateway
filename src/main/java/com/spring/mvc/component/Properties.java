package com.spring.mvc.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 常量的配置文件config.properties的java代码获取类
 * Created by liluoqi on 15/5/3.
 */
@Component
public class Properties {
    @Value("${token.from.sino.china.url}")
    private String tokenFromSinoChinaUrl;

    @Value("${send.data.info.to.sino.china.url}")
    private String sendDataInfoToSinoChinaUrl;

    @Value("${app.id}")
    private String appId;

    @Value("${auth.code}")
    private String authCode;

    @Value("${send.order.function}")
    private String sendOrderFunction;

    @Value("${eco.port.aes.key}")
    private String ecoPortAesKey;

    @Value("${eco.port.public.key}")
    private String ecoPortPublicKey;

    @Value("${sino.logis.code}")
    private String sinoLogisCode;

    @Value("${sino.logis.name}")
    private String sinoLogisName;

    @Value("${pay.company.code}")
    private String payCompanyCode;

    @Value("${pay.company.name}")
    private String payCompanyName;

    @Value("${company.code}")
    private String companyCode;

    @Value("${commerce.code}")
    private String commerceCode;

    @Value("${company.name}")
    private String companyName;

    @Value("${commerce.name}")
    private String commerceName;

    @Value("${alipay.partner.id}")
    private String alipayPartnerId;

    @Value("${alipay.gateway}")
    private String alipayGateway;

    @Value("${alipay.partner.key}")
    private String alipayPartnerKey;

    @Value("${postal_fee_per_kilo}")
    private String postalFeePerKilo;

    @Value("${domestic.alipay.account}")
    private String domesticAlipayAccount;

    @Value("${domestic.alipay.web.gateway}")
    private String domesticAlipayWebGateway;

    @Value("${sms.reg.code}")
    private String SmsRegCode;

    @Value("${sms.password}")
    private String smsPassword;

    public String getTokenFromSinoChinaUrl() {
        return tokenFromSinoChinaUrl;
    }

    public String getSendDataInfoToSinoChinaUrl() {
        return sendDataInfoToSinoChinaUrl;
    }

    public String getAppId() {
        return appId;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getSendOrderFunction() {
        return sendOrderFunction;
    }

    public String getEcoPortAesKey() {
        return ecoPortAesKey;
    }

    public String getEcoPortPublicKey() {
        return ecoPortPublicKey;
    }

    public String getSinoLogisCode() {
        return sinoLogisCode;
    }

    public String getSinoLogisName() {
        return sinoLogisName;
    }

    public String getPayCompanyCode() {
        return payCompanyCode;
    }

    public String getPayCompanyName() {
        return payCompanyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public String getCommerceCode() {
        return commerceCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCommerceName() {
        return commerceName;
    }

    public String getAlipayPartnerId() {
        return alipayPartnerId;
    }

    public String getAlipayGateway() {
        return alipayGateway;
    }

    public String getAlipayPartnerKey() {
        return alipayPartnerKey;
    }

    public String getPostalFeePerKilo() {
        return postalFeePerKilo;
    }

    public String getDomesticAlipayAccount() {
        return domesticAlipayAccount;
    }

    public String getDomesticAlipayWebGateway() {
        return domesticAlipayWebGateway;
    }

    public String getSmsRegCode() {
        return SmsRegCode;
    }

    public String getSmsPassword() {
        return smsPassword;
    }
}
