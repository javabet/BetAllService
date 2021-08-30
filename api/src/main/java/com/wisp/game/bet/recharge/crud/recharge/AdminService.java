package com.wisp.game.bet.recharge.crud.recharge;

import com.wisp.core.persistence.Page;
import com.wisp.core.service.CrudService;
import com.wisp.game.bet.recharge.common.commonInfo.Pagination;
import com.wisp.game.bet.recharge.dao.AdminDao;
import com.wisp.game.bet.recharge.dao.entity.AdminEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

@Service
public class AdminService extends CrudService<AdminDao, AdminEntity>
{
    public AdminEntity getByKey(String username )
    {
        return dao.getByKey( username );
    }


    //非数据库操作
    public boolean checkUsernameOnly( String userName,long adminId )
    {

        return false;
    }


    public AdminEntity getAdminByUserName(String username,String password )
    {
        return dao.getAdminByUserName( username,password );
    }


    public boolean AppendCreateRole( long adminId,long roleId )
    {
        return true;
    }
}
