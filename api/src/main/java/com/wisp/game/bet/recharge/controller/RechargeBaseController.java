package com.wisp.game.bet.recharge.controller;

import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.common.UicCacheKey;
import com.wisp.game.bet.recharge.dao.info.CacheUserInfo;

public class RechargeBaseController extends BaseController
{
    //出现此信息，则说明此数据为空
    protected static final long NULL_ADMIN_ID = -1000l;
    protected static final long ROOT_ID =   -1l;        //开发者账号（超级账号）

    public long getAdminId()
    {
        String token = getToken();
        CacheUserInfo userInfo = cacheHander.get(UicCacheKey.OAUTH2_TOKEN_INFO.key(token));
        if( userInfo == null )
        {
            //token失效了
            return NULL_ADMIN_ID;
        }

        return userInfo.getAdminId();
    }

}
