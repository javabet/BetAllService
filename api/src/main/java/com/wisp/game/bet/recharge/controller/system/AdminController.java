package com.wisp.game.bet.recharge.controller.system;

import com.wisp.core.interceptor.SkipAuthToken;
import com.wisp.core.persistence.Page;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.utils.encrypt.MD5Util;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.beanConfig.YmlConfig;
import com.wisp.game.bet.recharge.common.ErrorCode;
import com.wisp.game.bet.recharge.common.commonInfo.Pagination;
import com.wisp.game.bet.recharge.common.commonInfo.QueryInfo;
import com.wisp.game.bet.recharge.common.commonInfo.QueryPage;
import com.wisp.game.bet.recharge.crud.recharge.AdminService;
import com.wisp.game.bet.recharge.dao.entity.AdminEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/admin")
@ResponseResult
@Validated
@SkipAuthToken
public class AdminController extends BaseController
{
    @Autowired
    private AdminService adminService;

    @Autowired
    private YmlConfig ymlConfig;

    /**
     * @return
     */
    @GetMapping("/list")
    public Object List(@RequestParam("Page") @Valid  long requestPage,
                       @RequestParam("PageSize") @Valid long pageSize,
                       @RequestParam("KeyWord") @Valid String keyWord,
                       @RequestParam(value = "BeginTime",required = false) Integer beginTime,
                       @RequestParam(value = "EndTime",required = false) Integer endTime)
    {


        QueryPage<AdminEntity> page = new QueryPage<>();
        if( beginTime != null )
        {
            page.setBeginTime(beginTime);
        }
        if( endTime != null )
        {
            page.setEndTime(endTime);
        }
        page.setKeyWord(keyWord);
        long count = adminService.count(page);

        Pagination pagination = new Pagination(requestPage,pageSize,count);
        pagination.setCurrPage(requestPage);
        pagination.setMaxCount((int)count);
        page.setStart( pagination.getOffset() );
        page.setLength( pageSize );

        List<AdminEntity> list = adminService.findList(page);


        Map<String,Object> map = new HashMap<>();
        map.put("Items",list);
        map.put("Pagination",pagination);

        return map;

    }

    @Transactional("rechargeTransactionManger")
    @PostMapping({"/",""})
    public Object Add(@Valid @RequestBody AdminEntity adminEntity)
    {
        adminEntity.setStatus(1);

        //开发者不能增加
        //TODO wisp

        //检查账号的唯一性
        if(!adminEntity.getUsername().equals(""))
        {
            if( adminEntity.getId() == null )
            {
                adminEntity.setId(0l);
            }
           boolean repeatFlag =  adminService.checkUsernameOnly(adminEntity.getUsername(),adminEntity.getId() );
            if( repeatFlag )
            {
                return error(ErrorCode.ERR_USER_NAME.getCode());
            }
        }

        if( adminEntity.getRole() != null && !adminEntity.getRole().equals("") )
        {
            adminEntity.setRole("," + adminEntity.getRole() + ",");
        }

        if( adminEntity.getCreateRole() == null )
        {
            adminEntity.setCreateRole("");
        }

        adminEntity.setParentId(-1);
        adminEntity.setParentTree(",-1,");
        adminEntity.setPassword(MD5Util.getMD5(adminEntity.getPassword() + ymlConfig.getPasswordSalt()));        //增加一个passwordSalt打乱md5码数据
        adminEntity.setCreateTime(new Date());

        adminService.save(adminEntity,true);
        adminEntity.setParentTree(adminEntity.getParentTree() + adminEntity.getId() + "," );

        AdminEntity cloneAdminEntity = new AdminEntity();
        cloneAdminEntity.setId(adminEntity.getId());
        cloneAdminEntity.setParentTree(adminEntity.getParentTree());
        adminService.save(cloneAdminEntity);
        //adminService.updateField(adminEntity,new String[]{"ParentTree"});

        return  emptySucc();
    }

    @GetMapping("/{id}")
    public Object Get(@PathVariable("id") int id)
    {
        AdminEntity adminEntity = adminService.get(id);
        if( adminEntity == null )
        {
            return error(ErrorCode.ERR_NO_DATA.getCode());
        }

        return adminEntity;
    }

    @Transactional("rechargeTransactionManger")
    @PutMapping({"/",""})
    public Object Edit(@Valid @RequestBody AdminEntity adminEntity)
    {
        AdminEntity dbAdminEntity = adminService.get(adminEntity.getId());
        if( dbAdminEntity == null )
        {
            return error(ErrorCode.ERR_USER_NAME.getCode());
        }

        if(!adminEntity.getPassword().equals("") )
        {
            adminEntity.setPassword( MD5Util.getMD5(adminEntity.getPassword() + "PasswordSalt") );
        }
        else
        {
            adminEntity.setPassword(dbAdminEntity.getPassword());
        }

        if( dbAdminEntity.getUsername() != adminEntity.getUsername() && dbAdminEntity.getUsername() != "" )
        {
            boolean repeateFlag = adminService.checkUsernameOnly(dbAdminEntity.getUsername(),dbAdminEntity.getId());
            if( repeateFlag )
            {
                return error(ErrorCode.ERR_USER_NAME.getCode());
            }
        }

        AdminEntity cloneAdminEntity = new AdminEntity();
        cloneAdminEntity.setId(adminEntity.getId());
        cloneAdminEntity.setUsername(adminEntity.getUsername());
        cloneAdminEntity.setPassword(adminEntity.getPassword());
        cloneAdminEntity.setName(adminEntity.getName());
        cloneAdminEntity.setRole(adminEntity.getRole());

        return adminEntity;
    }

    @Transactional("rechargeTransactionManger")
    @DeleteMapping({"/{id}"})
    public Object Del(@PathVariable("id") int id)
    {
        AdminEntity adminEntity =  adminService.get(id);
        if( adminEntity == null )
        {
            return error(ErrorCode.ERR_NO_DATA.getCode());
        }

        adminService.delete(id);

        return emptySucc();
    }
}
