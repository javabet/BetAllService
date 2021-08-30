package com.wisp.game.bet.recharge.controller.system;

import com.wisp.core.interceptor.SkipAuthToken;
import com.wisp.core.persistence.Page;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.beanConfig.YmlConfig;
import com.wisp.game.bet.recharge.common.ErrorCode;
import com.wisp.game.bet.recharge.controller.RechargeBaseController;
import com.wisp.game.bet.recharge.crud.recharge.AdminService;
import com.wisp.game.bet.recharge.crud.recharge.ModeService;
import com.wisp.game.bet.recharge.crud.recharge.RoleService;
import com.wisp.game.bet.recharge.dao.entity.ModeEntity;
import com.wisp.game.bet.recharge.dao.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/role")
@ResponseResult
@Validated
@SkipAuthToken
public class RoleController extends RechargeBaseController
{
    @Autowired
    private RoleService roleService;
    @Autowired
    private ModeService modeService;

    @Autowired
    private YmlConfig ymlConfig;

    @Autowired
    private AdminService adminService;

    @RequestMapping(method = RequestMethod.GET,value = "/list")
    public Object List()
    {
        return null;
    }


    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Object Get(@PathVariable("id") int id)
    {
        RoleEntity roleEntity =  roleService.get(id);
        if( roleEntity == null )
        {
            return error(ErrorCode.ERR_DB_DATA.getCode());
        }

        Page<ModeEntity> page = new Page<>();
        page.setStart(0);
        page.setLength(1000l);
        List<ModeEntity> list = modeService.findList(page);

        Map<String,Object> map = new HashMap<>();
        map.put("Role",roleEntity);
        map.put("Mode",list);
        return map;
    }

    @Transactional("rechargeTransactionManger")
    @RequestMapping(method = RequestMethod.POST,value = {"/",""})
    public Object Add(@Valid @RequestBody RoleEntity roleEntity)
    {
        long adminUid = ymlConfig.getSystemAdminId();
        long adminId = getAdminId();
        if( adminId == NULL_ADMIN_ID )
        {
            return ErrorCode.ERR_TOKEN;
        }

        boolean permisFlag =  roleService.VerifyRolePermission(adminId,roleEntity.getPermission());
        if( permisFlag  == false )
        {
            return ErrorCode.ERR_RIGHT;
        }

        roleEntity.setAdminId( adminId );
        roleEntity.setCreateAdminId(adminId);
        roleEntity.setType(2);      //

         roleService.save(roleEntity,true);

         adminService.AppendCreateRole(adminId,roleEntity.getId());

         return emptySucc();
    }

    @Transactional("rechargeTransactionManger")
    @RequestMapping(method = RequestMethod.PUT,value = {"/",""})
    public Object Edit(@Valid @RequestBody RoleEntity roleEntity)
    {
        if( roleEntity.getId() == null )
        {
            return error(ErrorCode.ERR_PARAM.getCode());
        }

        long adminId = getAdminId();
        boolean permissionFlag = roleService.VerifyRolePermission( adminId,roleEntity.getPermission() );
        if( !permissionFlag  )
        {
            return ErrorCode.ERR_RIGHT;
        }

        roleEntity.setType(2);
        roleEntity.setAdminId( adminId );
        long adminUid = ymlConfig.getSystemAdminId();
        if( adminId != adminUid )
        {
            RoleEntity dbRoleEntity =  roleService.get(roleEntity.getId());
            if( dbRoleEntity == null )
            {
                return ErrorCode.ERR_NOT_SELF_ROLE;
            }
        }

        roleService.save(roleEntity);

        return emptySucc();
    }

    @Transactional("rechargeTransactionManger")
    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public Object Del(@PathVariable("id") int id)
    {
       if( id == 0 )
       {
           return ErrorCode.ERR_PARAM;
       }

       

        return null;
    }
}
