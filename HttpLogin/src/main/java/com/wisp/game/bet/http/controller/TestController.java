package com.wisp.game.bet.http.controller;

import com.wisp.game.bet.http.db.DbPay;
import com.wisp.game.bet.http.dbdoc.GameApiPayOrder2Doc;
import com.wisp.game.core.web.base.BaseController;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/test")
@RestController
public class TestController extends BaseController {


    @RequestMapping("/pay")
    public Object testPay()
    {

        Criteria criteria = Criteria.where("Agent").is(3);
        List<GameApiPayOrder2Doc> list =  DbPay.Instance.getMongoTemplate().find(Query.query(criteria), GameApiPayOrder2Doc.class);
        Map<String,GameApiPayOrder2Doc> map = new HashMap<String, GameApiPayOrder2Doc>();
        for( GameApiPayOrder2Doc gameApiPayOrder2Doc : list )
        {
            if( map.containsKey(gameApiPayOrder2Doc.getOrderId()) )
            {
                System.out.printf("the orderId is repeated:" + gameApiPayOrder2Doc.getOrderId() + "\n");
                continue;
            }
            map.put(gameApiPayOrder2Doc.getOrderId(),gameApiPayOrder2Doc);
        }

        return error(0);
    }
}
