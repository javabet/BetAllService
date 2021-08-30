package com.wisp.game.bet.recharge.controller.system;

import com.wisp.core.cache.CacheKey;
import com.wisp.core.interceptor.SkipAuthToken;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.utils.APIUtils;
import com.wisp.core.utils.encrypt.MD5Util;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.beanConfig.YmlConfig;
import com.wisp.game.bet.recharge.common.ErrorCode;
import com.wisp.game.bet.recharge.common.UicCacheKey;
import com.wisp.game.bet.recharge.crud.recharge.AdminService;
import com.wisp.game.bet.recharge.crud.recharge.LoginService;
import com.wisp.game.bet.recharge.dao.dto.system.ReqAccessTokenDTO;
import com.wisp.game.bet.recharge.dao.dto.system.ReqLoginDTO;
import com.wisp.game.bet.recharge.dao.dto.system.ReqLogoutDTO;
import com.wisp.game.bet.recharge.dao.entity.AdminEntity;
import com.wisp.game.bet.recharge.dao.info.CacheUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@SkipAuthToken
@RequestMapping("/system/user")
@ResponseResult
@Validated
public class LoginController  extends BaseController
{
    @Autowired
    private AdminService adminService;

    @Autowired
    private YmlConfig ymlConfig;

    @Autowired
    private LoginService loginService;

    @PostMapping(value = {"/reqToken","/requestToken"})
    public Object requestToken(@Validated @RequestBody ReqLoginDTO reqLogin)
    {
        String pwd = MD5Util.getMD5(reqLogin.getPassword() + ymlConfig.getPasswordSalt());

        AdminEntity adminEntity =  adminService.getAdminByUserName(reqLogin.getUsername(),pwd);
        if( adminEntity == null )
        {
            return ErrorCode.ERR_ACCOUNT_PWD_ERR;
        }

        String requestToken = APIUtils.getUUID();
        cacheHander.set(UicCacheKey.OAUTH2_REQUEST_TOKEN.key(requestToken),adminEntity.getId(), CacheKey.MINUTE_5);

        return requestToken;
    }

    @PostMapping(value = "/login")
    public Object login(@Validated @RequestBody ReqAccessTokenDTO reqAccessTokenDTO)
    {
        Long adminId = cacheHander.get(UicCacheKey.OAUTH2_REQUEST_TOKEN.key(reqAccessTokenDTO.getToken()));
        if( adminId == null )
        {
            return ErrorCode.ERR_ACCESS_TOKEN;
        }

        AdminEntity adminEntity =  adminService.get(adminId);
        if( adminEntity == null )
        {
            return ErrorCode.ERR_NO_DATA;
        }

        String oldToken = cacheHander.get(UicCacheKey.OAUTH2_USERID_TOKEN.key(adminId));
        if( oldToken != null )
        {
            ReqLogoutDTO reqLogoutDTO = new ReqLogoutDTO();
            reqLogoutDTO.setToken(reqAccessTokenDTO.getToken());
            logout(reqLogoutDTO);
        }

        Map<String,Object> permissionMap =  loginService.GetNavPermission(adminEntity);
        if( permissionMap == null )
        {
            return ErrorCode.ERR_DB_SELECT;
        }

        String respToken = APIUtils.getUUID();

        CacheUserInfo userInfo = new CacheUserInfo();
        userInfo.setAdminId(adminId);

        //设置玩家的信息
        cacheHander.set(UicCacheKey.OAUTH2_TOKEN_INFO.key(respToken),userInfo);
        cacheHander.set(UicCacheKey.OAUTH2_USERID_TOKEN.key(adminId),respToken);

        //删除登陆的token信息
        cacheHander.delete(UicCacheKey.OAUTH2_REQUEST_TOKEN.key(adminId));


        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("Admin",adminId);
        returnMap.put("Nav",permissionMap.get("Nav"));
        returnMap.put("Role",permissionMap.get("Role"));
        returnMap.put("CRole",permissionMap.get("CRole"));;
        returnMap.put("Token",respToken);

        return returnMap;
    }

    @GetMapping(value = "/logout")
    public Object logout(@Validated @RequestBody ReqLogoutDTO reqLogoutDTO)
    {
        return emptySucc();
    }

    /**
     * 检测登录状态,并返回最新的token等信息
     * @return
     */
    @GetMapping(value = "/islogin")
    public Object IsLogin()
    {
        CacheUserInfo cacheUserInfo =  getCacheUserInfo();
        if( cacheUserInfo == null )
        {
            return ErrorCode.ERR_TIME_OUT;
        }

        Long adminId = cacheUserInfo.getAdminId();

        AdminEntity adminEntity =  adminService.get(adminId);
        if( adminEntity == null )
        {
            return ErrorCode.ERR_NO_DATA;
        }

        Map<String,Object> permissionMap =  loginService.GetNavPermission(adminEntity);
        if( permissionMap == null )
        {
            return ErrorCode.ERR_DB_SELECT;
        }

        //重新设置token
        String respToken = APIUtils.getUUID();
        CacheUserInfo userInfo = new CacheUserInfo();
        userInfo.setAdminId(adminId);
        //设置玩家的信息
        cacheHander.set(UicCacheKey.OAUTH2_TOKEN_INFO.key(respToken),userInfo);
        cacheHander.set(UicCacheKey.OAUTH2_USERID_TOKEN.key(adminId),respToken);

        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("Admin",adminId);
        returnMap.put("Nav",permissionMap.get("Nav"));
        returnMap.put("Role",permissionMap.get("Role"));
        returnMap.put("CRole",permissionMap.get("CRole"));;
        returnMap.put("Token",respToken);


        return returnMap;
    }


}
