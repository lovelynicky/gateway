package com.spring.mvc.service;

import com.google.gson.Gson;
import com.spring.mvc.component.Properties;
import com.spring.mvc.model.*;
import com.spring.mvc.model.gson.sinoChina.*;
import com.spring.mvc.repository.*;
import com.spring.mvc.model.serviceResult.SinoChinaServiceResultModel;
import com.spring.mvc.model.serviceResult.SinoChinaResultModel;
import com.spring.mvc.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.spring.mvc.model.OrderStatus.DELIVERED;
import static com.spring.mvc.model.OrderStatus.DONE;
import static com.spring.mvc.model.OrderStatus.FAIL;


/**
 * 同中外运对接的业务逻辑代码
 * Created by liluoqi on 15/4/24.
 */
@Service
public class SinoChinaService {

    private Logger logger = Logger.getLogger(SinoChinaService.class);
    private static final String DECLARE_SUCCESS = "1";

    @Autowired
    private Properties properties;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private OrderGoodRepository orderGoodRepository;
    @Autowired
    private GoodRepository goodRepository;
    @Autowired
    private LogisRepository logisRepository;
    @Autowired
    private GoodUnitRepository goodUnitRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private OrderSentHistoryRepository orderSentHistoryRepository;
    @Autowired
    private BusinessNoRepository businessNoRepository;
    @Autowired
    private SinoResultRecordRepository sinoResultRecordRepository;
    @Autowired
    private ZipRepository zipRepository;

    @Autowired
    private RepaymentPlanService repaymentPlanService;
    @Autowired
    private SmsService smsService;

    /**
     * 发送订单数据到中外运，即时发送
     * service方法会被job调用
     */
    public void sendOrderToSinoChina(String orderNo) {
        SinoChinaResultModel sinoChinaResult = SinoChinaResultModel.getDefaultErrorInstance();
        try {
            if (StringUtils.isEmptyOrNull(orderNo)) {
                sinoChinaResult.getService_result().get(0).setNotice_content(String.format("传过来的订单号:%s为空", orderNo));
                logger.error("传过来的订单号为空");
                return;
            }
            logger.info(String.format("接收到订单号为:%s的一笔订单需要发送给中外运", orderNo));

            OrderModel orderModel = orderRepository.getById(Integer.valueOf(orderNo));
            if (null == orderModel) {
                logger.warn(String.format("根据订单编号:%s找不到订单信息", orderNo));
                sinoChinaResult.getService_result().get(0).setNotice_content(String.format("订单orderNo:%s找不到", orderNo));
            } else if (OrderStatus.PAID.getCode() == orderModel.getStatus()) {
                logger.warn(String.format("订单编号:%s不是已支付不发送", orderNo));
                sinoChinaResult.getService_result().get(0).setNotice_content(String.format("订单orderNo:%s不是已支付", orderNo));
            } else {
                sendOrdersToSinoTrans(orderModel);
            }
        } catch (Exception e) {
            logger.error(String.format("发送中外运订单数据异常:%s", e.getMessage()), e);
        }
    }


    /**
     * 发送订单数据到中外运
     * service方法会被调用
     */
    public void sendOrdersToSinoTrans(OrderModel order) throws Exception {
        try {
            List<OrderSentHistoryModel> orderSentHistoryModels = orderSentHistoryRepository.getByOrderNo(order.getOrdersn());

            if (orderSentHistoryModels != null && orderSentHistoryModels.size() > 0) {
                logger.warn(String.format("订单号:%s已经发送给海外购了，不能重复发送", order.getOrdersn()));
                return;
            }
            if (StringUtils.isEmptyOrNull(order.getJyh())) {
                logger.error(String.format("订单号%s没有支付交易号，取消推送", order.getOrdersn()));
                return;
            }
            String businessNo = BusinessNoGenerator.generateSinoBusinessNo(order.getOrdersn(), properties.getAppId());
            Map map = MapUtils.convertToMap("app_id", properties.getAppId(), "auth_token", getTokenFromSinoChina(properties.getAppId(), properties.getAuthCode()), "custom_code", "2991",
                    "function", properties.getSendOrderFunction(), "business_no", businessNo,
                    "type", "0", "data", new Gson().toJson(new SinoChinaOrderRequestDataGson(constructOrderHeader(order),
                            constructOrderDetailList(order), constructGoodsPurchaser(order))));
            logger.info(String.format("发送到海外购的订单信息是:%s", new Gson().toJson(map)));
            SinoChinaResultModel result = HttpClientUtils.postForm(properties.getSendDataInfoToSinoChinaUrl(), map, SinoChinaResultModel.class);
            orderSentHistoryRepository.insertOrderSentHistory(new OrderSentHistoryModel(businessNo, order.getOrdersn()));
            businessNoRepository.insertBusinessNo(new BusinessNoHistoryModel(businessNo));
            logger.info(String.format("针对中外运返回的结果做处理，更新系统内部订单号:%s的状态", order.getOrdersn()));
            handleSyncResultFromSinoChina(result, order.getOrdersn());
        } catch (Exception e) {
            logger.error(String.format("发送中外运订单数据异常:%s", e.getMessage()), e);
            throw e;
        }
    }

    /**
     * spring @Transactional注解开启的事物发生异常不能catch住而不硬编码处理，不然spring不会捕获这个异常并发生回滚操作
     * 处理中外运的即时回执,目前是单个订单发送，所以返回的结果只包含一个订单的回执结果，循环次数为1
     */
    @Transactional
    public void handleSyncResultFromSinoChina(SinoChinaResultModel result, String orderNo) {
        logger.info(String.format("处理海外购返回的订单号:%s,同步回执:%s", orderNo, new Gson().toJson(result)));
        for (SinoChinaServiceResultModel singleResult : result.getService_result()) {
            orderRepository.updateOrderStatus(orderNo, DECLARE_SUCCESS.equals(singleResult.getChk_mark()) ? DONE.getCode() : FAIL.getCode());//待发货
            if (!StringUtils.isEmptyOrNull(singleResult.getWay_bills())) {
                orderRepository.updateOrderBillWays(orderNo, singleResult.getWay_bills());//更新运单号，将运单号写进去;
            }
            if (DECLARE_SUCCESS.equals(singleResult.getChk_mark())) {
                repaymentPlanService.addRepaymentPlan(orderNo);
                smsService.sendDeliverySms(orderNo);
            }
            sinoResultRecordRepository.insertSinoResultRecord(new SinoResultRecordModel(singleResult.getBusiness_no(), singleResult.getChk_mark(),
                    singleResult.getNotice_time(), singleResult.getNotice_content(), singleResult.getBusiness_type(), singleResult.getWay_bills()));
        }

    }

    /**
     * 处理电子口岸返回给中外运的异步回执,中外运在返回给我们,目前是单个订单发送，所以返回的结果只包含一个订单的回执结果，循环次数为1
     */
    @Transactional
    public String handleAsyncResultFromSinoChina(SinoChinaResultModel result) {
        for (SinoChinaServiceResultModel singleResult : result.getService_result()) {
            String businessNo = singleResult.getBusiness_no();
            List<OrderSentHistoryModel> orderSentHistoryModels = orderSentHistoryRepository.getByBusinessNo(businessNo);
            if (orderSentHistoryModels != null && orderSentHistoryModels.size() > 0) {
                for (OrderSentHistoryModel sentHistoryModel : orderSentHistoryModels) {
                    logger.info(String.format("异步回执,更新订单编号:%s的状态", sentHistoryModel.getOrderNo()));
                    orderRepository.updateOrderStatus(sentHistoryModel.getOrderNo(), DECLARE_SUCCESS.equals(singleResult.getChk_mark()) ? DONE.getCode() : FAIL.getCode());
                }
            }
            int insertResult = sinoResultRecordRepository.insertSinoResultRecord(new SinoResultRecordModel(singleResult.getBusiness_no(), singleResult.getChk_mark(),
                    singleResult.getNotice_time(), singleResult.getNotice_content(), singleResult.getBusiness_type(), singleResult.getWay_bills()));
            if (insertResult > 0) {
                return "success";
            }
        }
        return "fail";
    }

    private SinoChinaOrderHeaderGson constructOrderHeader(OrderModel order) {
        CompanyModel company = companyRepository.getCompany();
        AddressModel address = addressRepository.getAddressById(order.getAddressid());
        LogisModel logis = logisRepository.getLogisById(1);//目前只有一个
        SinoChinaOrderHeaderGson sinoChinaOrderHeaderGson = new SinoChinaOrderHeaderGson();
        sinoChinaOrderHeaderGson.setCompanyCode(company.getCompanycode());
        sinoChinaOrderHeaderGson.setCompanyName(company.getCompanyname());
        sinoChinaOrderHeaderGson.setConsignee(address.getRealname());
        sinoChinaOrderHeaderGson.setConsigneeAddress(address.getAddress());
//        sinoChinaOrderHeaderGson.setConsigneeEmail(StringUtils.emptyString());//非必输字段
        sinoChinaOrderHeaderGson.setConsigneeTel(address.getMobile());
        sinoChinaOrderHeaderGson.setConsigneeProvince(address.getProvince());
        sinoChinaOrderHeaderGson.setConsigneeCity(address.getCity());
        sinoChinaOrderHeaderGson.setConsigneeCounty(address.getArea());
        sinoChinaOrderHeaderGson.setCurrCode("142");//人民币的代码142
        sinoChinaOrderHeaderGson.seteCommerceCode(company.geteCommerceCode());
        sinoChinaOrderHeaderGson.seteCommerceName(company.geteCommerceName());
        sinoChinaOrderHeaderGson.setFeeAmount(0);//运费,包邮模式是0
        sinoChinaOrderHeaderGson.setIeFlag("I");//for temporary time use I
        sinoChinaOrderHeaderGson.setLogisCompanyCode(logis.getLogisCompanyCode() != null ? logis.getLogisCompanyCode() : properties.getSinoLogisCode());
        sinoChinaOrderHeaderGson.setLogisCompanyName(logis.getLogisCompanyName() != null ? logis.getLogisCompanyName() : properties.getSinoLogisName());
        sinoChinaOrderHeaderGson.setOrderGoodsAmount(Double.valueOf(order.getPrice()));
        sinoChinaOrderHeaderGson.setOrderNo(order.getOrdersn());
        sinoChinaOrderHeaderGson.setOrderTaxAmount(StringUtils.isEmptyOrNull(order.getXys()) ? 0 : Double.valueOf(order.getXys()));
        sinoChinaOrderHeaderGson.setOrderTotalAmount(Double.valueOf(order.getPrice()));
        sinoChinaOrderHeaderGson.setPayCompanyCode(company.getPayCompanyCode());
        sinoChinaOrderHeaderGson.setPayNumber(order.getJyh());
//        StringUtils.emptyString().equals(String.valueOf(order.getPaytype())) ? "03" : String.format("0%s", order.getPaytype())
        sinoChinaOrderHeaderGson.setPayType("02");
        sinoChinaOrderHeaderGson.setPostMode("3");//发货方式必填
        sinoChinaOrderHeaderGson.setPurchaserId(String.valueOf(order.getUid()));
        sinoChinaOrderHeaderGson.setSenderCountry(logis.getGb() != null ? countryRepository.getCountryByName(logis.getGb()).getCode() : "142");
        sinoChinaOrderHeaderGson.setSenderName(logis.getName() != null ? logis.getName() : company.getCompanyname());
        sinoChinaOrderHeaderGson.setTotalAmount(Double.valueOf(order.getPrice()));
        List<OrderGoodModel> orderGoodModels = orderGoodRepository.getOrderGoodsByOrderId(order.getId());
        int totalCount = 0;
        for (OrderGoodModel orderGood : orderGoodModels) {
            totalCount = totalCount + orderGood.getTotal() * Integer.valueOf(orderGood.getGn());
        }
        sinoChinaOrderHeaderGson.setTotalCount(totalCount);
        sinoChinaOrderHeaderGson.setTradeTime(DateUtils.formatDateToSeconds(DateUtils.formatUnixTimestamperToDate(order.getCreatetime())));
        ZipModel zipModel = zipRepository.getMostLikelyZipByCity(address.getCity(), address.getProvince());
        sinoChinaOrderHeaderGson.setZipCode(zipModel == null ? StringUtils.emptyString() : zipModel.getZipCode());//非必填
//        sinoChinaOrderHeaderGson.setWayBills("");//非必填
        sinoChinaOrderHeaderGson.setRate("1");//现在只使用人民币所以默认都是1
        sinoChinaOrderHeaderGson.setBonded(1);//1为保税模式
        sinoChinaOrderHeaderGson.setBillNoType("EMS");//非必填
        return sinoChinaOrderHeaderGson;
    }

    private List<SinoChinaOrderGson> constructOrderDetailList(OrderModel order) {
        List<SinoChinaOrderGson> orderList = new ArrayList<SinoChinaOrderGson>();
        Map<OrderGoodModel, GoodModel> goodIdsAndGoodInfoRelation = getGoodsIdAndGoodInfoContainInOrder(orderGoodRepository.getOrderGoodsByOrderId(order.getId()));
        Set<OrderGoodModel> goodIdSet = goodIdsAndGoodInfoRelation.keySet();
        int index = 1;
        for (OrderGoodModel orderGood : goodIdSet) {
            GoodModel good = goodIdsAndGoodInfoRelation.get(orderGood);
            SinoChinaOrderGson sinoChinaOrderGson = new SinoChinaOrderGson();
            sinoChinaOrderGson.setCodeTs(good.getParcelTaxCode());//关联到税率字典表
            sinoChinaOrderGson.setGoodsCount(orderGood.getTotal() * Integer.valueOf(orderGood.getGn()));
            sinoChinaOrderGson.setGoodsModel("无");
            sinoChinaOrderGson.setGoodsName(good.getTitle());
            sinoChinaOrderGson.setGoodsOrder(index);
            if (StringUtils.emptyString().equals(good.getDw())) {
                sinoChinaOrderGson.setGoodsUnit(StringUtils.emptyString());
            } else {
                GoodUnitModel goodUnitModel = goodUnitRepository.getGoodUnitByUnitId(Integer.valueOf(good.getDw()));
                sinoChinaOrderGson.setGoodsUnit(goodUnitModel == null ? StringUtils.emptyString() : goodUnitModel.getCode());
            }
            sinoChinaOrderGson.setGrossWeight(WeightUtils.formatGramToKiloGramDouble(good.getWeight()));//非必填
            CountryModel country = countryRepository.getCountryById(Integer.valueOf(good.getGuojia()));
            sinoChinaOrderGson.setOriginCountry(country != null ? country.getCode() : "300");//300代表欧洲
            sinoChinaOrderGson.setUnitPrice(MathUtils.divide(order.getPrice(), orderGood.getTotal() * Integer.valueOf(orderGood.getGn())));
            sinoChinaOrderGson.setGoodsItemNo(good.getGoodItemNo());//物料号
            orderList.add(sinoChinaOrderGson);
            index++;
        }
        return orderList;
    }

    private SinoGoodsPurchaserGson constructGoodsPurchaser(OrderModel order) {
//        McMemberModel purchaser = mcMemberRepository.getMcMemberByUid(order.getUid());
        AddressModel address = addressRepository.getAddressById(order.getAddressid());
        SinoGoodsPurchaserGson sinoGoodsPurchaserGson = new SinoGoodsPurchaserGson();
        sinoGoodsPurchaserGson.setId(String.valueOf(order.getUid()));
        sinoGoodsPurchaserGson.setName(address.getRealname());
        sinoGoodsPurchaserGson.setEmail("9999");//必填,写死是"9999"
//        sinoGoodsPurchaserGson.setAddress(address.getAddress());//非必填
//        sinoGoodsPurchaserGson.setPaperType("1");//非必填
//        sinoGoodsPurchaserGson.setPaperNumber("xxxxx");//非必填
        sinoGoodsPurchaserGson.setTelNumber(address.getMobile());
        return sinoGoodsPurchaserGson;
    }

    private Map<OrderGoodModel, GoodModel> getGoodsIdAndGoodInfoContainInOrder(List<OrderGoodModel> orderGoods) {
        Map<OrderGoodModel, GoodModel> relations = new HashMap<OrderGoodModel, GoodModel>();
        for (OrderGoodModel orderGood : orderGoods) {
            relations.put(orderGood, goodRepository.getGoodByGoodId(orderGood.getGoodsid()));
        }
        return relations;
    }

    /**
     * 从中外运获取token
     *
     * @param appId 海外购的程序ID
     * @param authCode 海外购的认证码
     * @return token as String
     */
    public String getTokenFromSinoChina(String appId, String authCode) {
        try {
            logger.info(String.format("try to get token from sino china interface : %s using appId:%s,auth_code:%s",
                    properties.getTokenFromSinoChinaUrl(), appId, authCode));
            Map map;
            if (StringUtils.isEmptyOrNull(appId) || StringUtils.isEmptyOrNull(authCode)) {
                map = MapUtils.convertToMap("app_id", properties.getAppId(), "auth_code", properties.getAuthCode());
            } else {
                map = MapUtils.convertToMap("app_id", appId, "auth_code", authCode);
            }
            return HttpClientUtils.postForm(properties.getTokenFromSinoChinaUrl(), map, String.class);
        } catch (Exception e) {
            logger.error("exception happens when get token from sino china", e);
            return StringUtils.emptyString();
        }
    }
}
