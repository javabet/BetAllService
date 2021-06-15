package com.wisp.game.bet.recharge.controller.system;

import com.wisp.core.persistence.Page;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.common.ErrorCode;
import com.wisp.game.bet.recharge.crud.recharge.ModeService;
import com.wisp.game.bet.recharge.crud.recharge.RoleService;
import com.wisp.game.bet.recharge.dao.entity.ModeEntity;
import com.wisp.game.bet.recharge.dao.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RoleController extends BaseController
{
    @Autowired
    private RoleService roleService;
    @Autowired
    private ModeService modeService;

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

    @RequestMapping(method = RequestMethod.POST,value = {"/",""})
    public Object Add(@Valid @RequestBody RoleEntity roleEntity)
    {
         int id =  roleService.save(roleEntity,true);

         return emptySucc();
    }

    @RequestMapping(method = RequestMethod.PUT,value = {"/",""})
    public Object Edit(@Valid @RequestBody RoleEntity roleEntity)
    {
        if( roleEntity.getId() == null )
        {
            return error(ErrorCode.ERR_PARAM.getCode());
        }

        roleEntity.setType(2);

        roleService.save(roleEntity);

        return emptySucc();
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public Object Del(@PathVariable("id") int id)
    {
        return null;
    }
}
