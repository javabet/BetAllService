package com.wisp.game.bet.http.controller;

import com.wisp.game.bet.db.mongo.account.doc.AccountTableDoc2;
import com.wisp.game.bet.db.mongo.player.doc.PlayerInfoDoc2;
import com.wisp.game.bet.http.controller.info.ErrorInfo;
import com.wisp.game.bet.http.db.DbAccount;
import com.wisp.game.bet.http.db.DbPay;
import com.wisp.game.bet.http.db.DbPlayer;
import com.wisp.game.bet.http.dbdoc.GameApiPayOrder2Doc;
import com.wisp.game.core.web.base.BaseController;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequestMapping("/test")
@RestController
public class TestController extends BaseController {


    @RequestMapping("/pay")
    public Object testPay()
    {

        Criteria criteria = Criteria.where("AgentId").is(3);
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

    @RequestMapping("/change")
    public Object testChangePlayerId()
    {
        Criteria criteria = Criteria.where("AgentId").is(2);
        List<AccountTableDoc2> list =  DbAccount.Instance.getMongoTemplate().find(Query.query(criteria), AccountTableDoc2.class);
        Map<String,AccountTableDoc2> map = new HashMap<String, AccountTableDoc2>();
        for( AccountTableDoc2 gameApiPayOrder2Doc : list )
        {
            if( gameApiPayOrder2Doc.getPlayerId() == 0 )
            {
                continue;
            }
            map.put(gameApiPayOrder2Doc.getAccount(),gameApiPayOrder2Doc);
        }

        List<PlayerInfoDoc2> list2 =  DbPlayer.Instance.getMongoTemplate().find(Query.query(criteria), PlayerInfoDoc2.class);

        List<ErrorInfo> erroorList = new ArrayList<ErrorInfo>();
        for(PlayerInfoDoc2 playerInfoDoc2 : list2)
        {
            if( map.containsKey(playerInfoDoc2.getAccount()) )
            {
                if( playerInfoDoc2.getPlayerId() == map.get(playerInfoDoc2.getAccount()).getPlayerId() )
                {
                    continue;
                }
                ErrorInfo errorInfo = new ErrorInfo();
                errorInfo.setAccountName(playerInfoDoc2.getAccount());
                errorInfo.setNewPlayerId(playerInfoDoc2.getPlayerId());
                errorInfo.setOldPlayId(map.get(playerInfoDoc2.getAccount()).getPlayerId());
                erroorList.add(errorInfo);
            }
        }

        return data(erroorList);
    }

    @RequestMapping("/filter")
    public Object filteSame()
    {
        Criteria criteria = Criteria.where("AgentId").is(2);
        List<AccountTableDoc2> list =  DbAccount.Instance.getMongoTemplate().find(Query.query(criteria), AccountTableDoc2.class);
        Map<String,AccountTableDoc2> map = new HashMap<String, AccountTableDoc2>();
        for( AccountTableDoc2 gameApiPayOrder2Doc : list )
        {
            if( gameApiPayOrder2Doc.getPlayerId() == 0 )
            {
                continue;
            }
            map.put(gameApiPayOrder2Doc.getAccount(),gameApiPayOrder2Doc);
        }

        List<PlayerInfoDoc2> list2 =  DbPlayer.Instance.getMongoTemplate().find(Query.query(criteria), PlayerInfoDoc2.class);

        Map<String,PlayerInfoDoc2> allPlayerAccountSet = new HashMap<String, PlayerInfoDoc2>();
        for(PlayerInfoDoc2 playerInfoDoc2 : list2)
        {
            allPlayerAccountSet.put(playerInfoDoc2.getAccount(),playerInfoDoc2);
        }

        List<String> list1 = new ArrayList<String>();

        //查找有重复的playerId
        for( String account:map.keySet() )
        {
            int oldPlayerId = map.get(account).getPlayerId();
            int newPlayerId = allPlayerAccountSet.get(account).getPlayerId();

            if( oldPlayerId == newPlayerId )
            {
                list1.add(account);
            }
        }

        return data(list1);
    }

    @RequestMapping("/third")
    public Object filterThirdPlayerId()
    {
        Map<Integer,String> playersMap = new HashMap<Integer, String>();
        playersMap.put(19615336,"jmj0222");
        playersMap.put(19188951,"luohaibao");
        playersMap.put(19103820,"lhsa63");
        playersMap.put(18185903,"sunkaisksk");
        playersMap.put(17942785,"dyg730605");

        playersMap.put(17877864,"jinsha888");
        playersMap.put(17670900,"943246120");
        playersMap.put(17658200,"138238802");
        playersMap.put(17484118,"lionking1122");
        playersMap.put(17379926,"a382613898");

        playersMap.put(17211804,"WX6098");
        playersMap.put(16801157,"pq0311");
        playersMap.put(16571859,"wpdd801212");
        playersMap.put(16345361,"lizyong12345");
        playersMap.put(15521384,"chenwenxi88");

        playersMap.put(15375048,"zyzzyz");
        playersMap.put(15359838,"8yong8");
        playersMap.put(14550131,"281372258");
        playersMap.put(14258675,"ludanqing1");
        playersMap.put(12417088,"dayu07");

        playersMap.put(10994919,"kkas601");
        playersMap.put(10742450,"139576");
        playersMap.put(10092360,"wangshaoyang");
        playersMap.put(8582205,"test19");
        playersMap.put(8521464,"yueyue6666");

        playersMap.put(8316006,"wx198686");
        playersMap.put(8216470,"xkb321");
        playersMap.put(6588813,"flq108807018");
        playersMap.put(6525585,"yuy2zh342");
        playersMap.put(6433101,"zhuenze1234");

        playersMap.put(5902172,"qiwenome");
        playersMap.put(5729507,"wjl6899");
        playersMap.put(5615079,"cjk124");
        playersMap.put(2984554,"feige113");
        playersMap.put(2931000,"jackychien");

        playersMap.put(2756652,"aplle555");
        playersMap.put(1987427,"ren313319");
        playersMap.put(1881326,"z179269");
        playersMap.put(1026268,"yan55131498");


        Criteria criteria = Criteria.where("AgentId").is(2);
        List<PlayerInfoDoc2> list2 =  DbPlayer.Instance.getMongoTemplate().find(Query.query(criteria), PlayerInfoDoc2.class);

        Map<Integer,PlayerInfoDoc2> allPlayerAccountSet = new HashMap<Integer, PlayerInfoDoc2>();
        for(PlayerInfoDoc2 playerInfoDoc2 : list2)
        {
            allPlayerAccountSet.put(playerInfoDoc2.getPlayerId(),playerInfoDoc2);
        }

        for(int thirdPlayerId:playersMap.keySet())
        {
            String newAccount = "web_2_SGGaming_"+ playersMap.get(thirdPlayerId);
            if( allPlayerAccountSet.containsKey(thirdPlayerId) )
            {
                PlayerInfoDoc2 playerInfoDoc2 = allPlayerAccountSet.get(thirdPlayerId);
                if( !newAccount.equals(playerInfoDoc2.getAccount()) )
                {
                    System.out.printf("not equal");
                }
            }
            else
            {
                System.out.printf("the playerId is not exist:" + thirdPlayerId);
            }
        }

        return error(0);

    }

}
