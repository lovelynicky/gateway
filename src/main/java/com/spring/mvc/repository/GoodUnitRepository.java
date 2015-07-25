package com.spring.mvc.repository;

import com.spring.mvc.model.GoodUnitModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 获取数据库中商品单位字典表中的数据
 * Created by liluoqi on 15/5/3.
 */
@Repository
public class GoodUnitRepository extends BaseRepository<GoodUnitModel> {

    private static final String GET_GOOD_UNIT_BY_ID="getGoodUnitById";
    //todo should delete for this is only the test for using map as params passing to the mybatis sql sentence
    private static final String GET_GOOD_UNIT_BY_NAME_AND_CODE="getGoodUnitByNameAndCode";


    public GoodUnitModel getGoodUnitByUnitId(int id){
        return querySingleById(GET_GOOD_UNIT_BY_ID, id);
    }


    //todo should be deleted
    public List<GoodUnitModel> getGoodUnitByNameAndCode(Map map){
        return queryList(GET_GOOD_UNIT_BY_NAME_AND_CODE, map);
    }
}
