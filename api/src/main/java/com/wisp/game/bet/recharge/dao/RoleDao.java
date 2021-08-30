package com.wisp.game.bet.recharge.dao;

import com.wisp.core.persistence.CrudDao;
import com.wisp.core.persistence.MyBatisDao;
import com.wisp.game.bet.recharge.dao.entity.ModeEntity;
import com.wisp.game.bet.recharge.dao.entity.RoleEntity;
import org.apache.ibatis.annotations.Param;

import javax.management.relation.RoleList;
import java.util.List;

@MyBatisDao(tableName = "recharge_sys_role" )
public interface RoleDao extends CrudDao<RoleEntity>
{
    //查看admin_id的数据
    public List<RoleEntity> findListByAdminId(@Param("admin_id") long id);
}
