package com.wisp.game.bet.recharge.controller.system;

import com.wisp.core.interceptor.SkipAuthToken;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.common.ErrorCode;
import com.wisp.game.bet.recharge.crud.recharge.ModeService;
import com.wisp.game.bet.recharge.crud.recharge.NodeService;
import com.wisp.game.bet.recharge.crud.recharge.RoleService;
import com.wisp.game.bet.recharge.dao.entity.ModeEntity;
import com.wisp.game.bet.recharge.dao.entity.NodeEntity;
import com.wisp.game.bet.recharge.dao.entity.RoleEntity;
import com.wisp.game.bet.recharge.dao.info.CacheUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/public")
@ResponseResult
@Validated
public class PublicController  extends BaseController
{
    @Autowired
    private RoleService roleService;

    @Autowired
    private ModeService modeService;

    @Autowired
    private NodeService nodeService;


    @GetMapping(value = "/config")
    public Object config(@RequestParam(value = "type") String type)
    {
        CacheUserInfo cacheUserInfo = getCacheUserInfo();
        if( cacheUserInfo == null )
        {
            return ErrorCode.ERR_LOGIN_FIRST;
        }

        Map<String,Object> returnMap = new HashMap<>();
        if( type == null || type == "" )
        {
            List<RoleEntity> roleEntityList =  roleService.getRoleList(cacheUserInfo.getAdminId());

            List<ModeEntity> modeEntityList =  modeService.findAll();

            List<NodeEntity> nodeEntityList =  nodeService.findListByOrder();

            returnMap.put("Role",roleEntityList);
            returnMap.put("Mode",modeEntityList);
            returnMap.put("Node",nodeEntityList);
        }
        else
        {
            if (type.equals("Admin"))
            {

            }
            else if (type.equals("Role"))
            {

            }
            else if (type.equals("Mode"))
            {

            }
            else if( type.equals("Node") )
            {

            }
        }

        return returnMap;
    }
}
