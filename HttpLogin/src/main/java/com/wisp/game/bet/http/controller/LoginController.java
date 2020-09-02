package com.wisp.game.bet.http.controller;

import com.mongodb.client.result.UpdateResult;
import com.wisp.game.bet.db.mongo.account.doc.AccountTableDoc;
import com.wisp.game.bet.db.mongo.config.doc.AgentInfoDoc;
import com.wisp.game.bet.http.common.ErrorCode;
import com.wisp.game.bet.http.common.LoginHelper;
import com.wisp.game.bet.http.db.DbAccount;
import com.wisp.game.bet.http.db.DbConfig;
import com.wisp.game.bet.http.manager.ServerListCache;
import com.wisp.game.bet.share.utils.MD5Util;
import com.wisp.game.core.random.RandomHandler;
import com.wisp.game.core.web.base.BaseController;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

//@RestController()
//@RequestMapping("/Web")
public class LoginController extends BaseController {

    public static String WEB_ACC = "8DB1C7CE26C2A748FA3627410DB0FB0F";

    @Autowired
    private ServerListCache serverListCache;

    @RequestMapping(method = {RequestMethod.POST})
    public Object weblogin2(@RequestParam("data") String data,@RequestParam("sign") String sign)
    {
        String genMd5 =  MD5Util.getMD5(data + WEB_ACC);
        if( sign != genMd5 )
        {
            return error(ErrorCode.ERR_MD5_ERR);
        }

        String[] datas = data.split(":");
        if( datas.length < 6 )
        {
            return error(ErrorCode.ERR_NORMAL_ERR,"Login_SystemError2");
        }

        String account = datas[0];
        String channelId = datas[1];
        String agentId = datas[2];
        String urlKey = datas[3];
        String remoteIP = datas[4];
        String packageType = datas[5];

        if( remoteIP.equals("") )
        {
            remoteIP = getIp();
        }

        if( channelId.equals("") )
        {
            channelId = "500001";
        }

        if( agentId.equals("") )
        {
            agentId = "1";
        }

        if( !account.equals("") )
        {
            String[] strs = account.split("_");
            if( strs.length < 2 )
            {
                return error(ErrorCode.ERR_NORMAL_ERR,"Login_SystemError3");
            }

            if( strs[0].equals("web") )
            {
                return error(ErrorCode.ERR_NORMAL_ERR,"Login_SystemError4");
            }
        }
        else
        {
            account = LoginHelper.buildWebAccount(agentId);
        }



        Criteria criteria = Criteria.where("ChannelId").is(channelId);
        boolean existFlag = DbConfig.Instance.getMongoTemplate().exists(Query.query(criteria), AgentInfoDoc.class);
        if(!existFlag)
        {
            return error(ErrorCode.ERR_NORMAL_ERR,"Login_SystemError5");
        }

        String randKey = RandomHandler.Instance.getRandomValue(1000000,10000000) + "";
        Date date = new Date();


        criteria = Criteria.where("Account").is(account);
        AccountTableDoc accountTableDoc =  DbAccount.Instance.getMongoTemplate().findOne(Query.query(criteria), AccountTableDoc.class);

        Update update = null;
        if( accountTableDoc == null )
        {
            accountTableDoc = new AccountTableDoc();
            accountTableDoc.setAccPhone("");
            accountTableDoc.setAccount(account);
            accountTableDoc.setAccDev("");
            accountTableDoc.setDeviceId("");
            accountTableDoc.setRegisterTime(date);
            accountTableDoc.setRegisterIp(remoteIP);
            accountTableDoc.setChannelId(channelId);
            accountTableDoc.setLevel(1);
            accountTableDoc.setPlatform("web");
            accountTableDoc.setDevicePlatform("web");
            try
            {
                accountTableDoc.setAgentId(Integer.valueOf(agentId));
            }
            catch (Exception ex)
            {
                accountTableDoc.setAgentId(1);
            }
        }


        accountTableDoc.setRandKey( randKey );
        accountTableDoc.setLastTime(date.getTime());
        accountTableDoc.setLastIp(remoteIP);
        accountTableDoc.setClientChannelId(channelId);

        criteria = Criteria.where("Account").is(account);
        Document updateDoc = accountTableDoc.to_bson(true);
        UpdateResult updateResult =  DbAccount.Instance.getMongoTemplate().upsert( Query.query(criteria),Update.fromDocument(updateDoc),AccountTableDoc.class );

        ServerListCache.ServerInfo serverInfo = serverListCache.AllocServer(account,"web");
        if( serverInfo == null )
        {
            return new LoginHelper().buildWebLoginMsg("Login_DownloadError");
        }
        else
        {
            String clientKey = randKey + ":" +  date.getTime();
            String realyKey = MD5Util.getMD5(clientKey);

            return new LoginHelper().buildWebLoginMsg(realyKey,true,account,serverInfo.getGateIp(),serverInfo.getGateWeb(),false,"");
        }
    }
}
