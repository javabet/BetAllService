package com.wisp.game.bet.recharge.controller.system;

import com.wisp.core.service.ResponseResult;
import com.wisp.core.utils.encrypt.MD5Util;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.common.ErrorCode;
import com.wisp.game.bet.recharge.crud.recharge.AdminService;
import com.wisp.game.bet.recharge.dao.entity.AdminEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/system/login")
@ResponseResult
@Validated
public class LoginController  extends BaseController
{
    @Autowired
    private AdminService adminService;

    @PostMapping(value = "/login")
    public Object login(@NotNull @NotEmpty @RequestParam("UserName") String userName,
                       @NotNull @NotEmpty @RequestParam("Password") String password )
    {

        String pwd = MD5Util.getMD5(password + "passwordSalt");

        AdminEntity adminEntity =  adminService.getAdminByUserName(userName,password);
        if( adminEntity == null )
        {
            return error(ErrorCode.ERR_NO_DATA.getCode());
        }

        return emptySucc();
    }
}
