package com.spring.mvc.service;

import com.spring.mvc.model.OrderModel;
import com.spring.mvc.model.gson.sinoChina.SinoChinaOrderGson;
import com.spring.mvc.model.gson.sinoChina.SinoChinaOrderHeaderGson;
import com.spring.mvc.model.gson.sinoChina.SinoGoodsPurchaserGson;
import com.spring.mvc.utils.BusinessNoGenerator;
import com.spring.mvc.utils.DateUtils;
import com.spring.mvc.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liluoqi on 15/5/8.
 */
public class SinoTestDataConstruction {

    public static SinoChinaOrderHeaderGson constructOrderHeader(OrderModel order) {
        SinoChinaOrderHeaderGson sinoChinaOrderHeaderModel = new SinoChinaOrderHeaderGson();
        sinoChinaOrderHeaderModel.setCompanyCode("PT15021201");
        sinoChinaOrderHeaderModel.setCompanyName("香港筠希电子商务科技有限公司");
        sinoChinaOrderHeaderModel.setConsignee("李罗琦");
        sinoChinaOrderHeaderModel.setConsigneeAddress("庐阳区长江西路大众巷");
        sinoChinaOrderHeaderModel.setConsigneeEmail("1285558992@qq.com");
        sinoChinaOrderHeaderModel.setConsigneeTel("15105515643");
        sinoChinaOrderHeaderModel.setConsigneeProvince("安徽");
        sinoChinaOrderHeaderModel.setConsigneeCity("合肥");
        sinoChinaOrderHeaderModel.setConsigneeCounty("蜀山区");
        sinoChinaOrderHeaderModel.setCurrCode("142");
        sinoChinaOrderHeaderModel.seteCommerceCode("DS15051101");
        sinoChinaOrderHeaderModel.seteCommerceName("香港筠希电子商务科技有限公司");
        sinoChinaOrderHeaderModel.setFeeAmount(15);
        sinoChinaOrderHeaderModel.setIeFlag("I");
        sinoChinaOrderHeaderModel.setLogisCompanyCode("3301510001");
        sinoChinaOrderHeaderModel.setLogisCompanyName("杭州中外运电子商务有限公司");
        sinoChinaOrderHeaderModel.setOrderGoodsAmount(200);
        sinoChinaOrderHeaderModel.setOrderNo(order.getOrdersn());
        sinoChinaOrderHeaderModel.setOrderTaxAmount(0);
        sinoChinaOrderHeaderModel.setOrderTotalAmount(0.08);
        sinoChinaOrderHeaderModel.setPayCompanyCode("ZF14021901");
        sinoChinaOrderHeaderModel.setPayNumber("2015051800001000690053284866");
        sinoChinaOrderHeaderModel.setPayType("03");
        sinoChinaOrderHeaderModel.setPostMode("3");//1快递小包，2快件，3EMS
        sinoChinaOrderHeaderModel.setPurchaserId("12");
        sinoChinaOrderHeaderModel.setSenderCountry("142");
        sinoChinaOrderHeaderModel.setSenderName("香港筠希信息科技有限公司");
        sinoChinaOrderHeaderModel.setTotalAmount(0.08);
        sinoChinaOrderHeaderModel.setTotalCount(1);
        sinoChinaOrderHeaderModel.setTradeTime(DateUtils.formatDateToSeconds(new Date()));
        sinoChinaOrderHeaderModel.setZipCode("230000");
//        sinoChinaOrderHeaderModel.setWayBills("");
        sinoChinaOrderHeaderModel.setRate("1");
        sinoChinaOrderHeaderModel.setBonded(1);
        sinoChinaOrderHeaderModel.setBillNoType("EMS"); //1快递小包，2快件，3EMS
        return sinoChinaOrderHeaderModel;
    }

    public static List<SinoChinaOrderGson> constructOrderDetailList() {
        List<SinoChinaOrderGson> orderList = new ArrayList<SinoChinaOrderGson>();
        SinoChinaOrderGson sinoChinaOrderModel = new SinoChinaOrderGson();
        sinoChinaOrderModel.setCodeTs("01000000");
        sinoChinaOrderModel.setGoodsCount(1);
//        sinoChinaOrderModel.setGoodsModel("无");
        sinoChinaOrderModel.setGoodsName("奥地利爱他美pre段800G");
        sinoChinaOrderModel.setGoodsOrder(1);
        sinoChinaOrderModel.setGoodsUnit("122");
        sinoChinaOrderModel.setGrossWeight(2.2);
        sinoChinaOrderModel.setOriginCountry("304");
        sinoChinaOrderModel.setUnitPrice(0.08);
        orderList.add(sinoChinaOrderModel);
        return orderList;
    }

    public static SinoGoodsPurchaserGson constructGoodsPurchaser() {
        SinoGoodsPurchaserGson sinoGoodsPurchaserModel = new SinoGoodsPurchaserGson();
        sinoGoodsPurchaserModel.setId("12");
        sinoGoodsPurchaserModel.setName("李罗琦");
        sinoGoodsPurchaserModel.setEmail("9999");//海关要求有值
//        sinoGoodsPurchaserModel.setAddress("合肥庐阳区长江西路大众巷");
//        sinoGoodsPurchaserModel.setPaperType("1");
//        sinoGoodsPurchaserModel.setPaperNumber("xxxxx");
        sinoGoodsPurchaserModel.setTelNumber("15105515643");
        return sinoGoodsPurchaserModel;
    }

}
