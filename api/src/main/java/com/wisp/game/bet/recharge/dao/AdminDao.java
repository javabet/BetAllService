package com.wisp.game.bet.recharge.dao;

import com.wisp.core.persistence.CrudDao;
import com.wisp.core.persistence.MyBatisDao;
import com.wisp.game.bet.recharge.dao.entity.AdminEntity;
import org.apache.ibatis.annotations.Param;

@MyBatisDao(tableName = "recharge_sys_admin" )
public interface AdminDao  extends CrudDao<AdminEntity>
{
    AdminEntity getByKey(@Param("Username") String Username );


    AdminEntity getAdminByUserName(@Param("Username") String username,@Param("Password") String password );
}
