package com.wisp.game.bet.recharge.dao;

import com.wisp.core.persistence.CrudDao;
import com.wisp.core.persistence.MyBatisDao;
import com.wisp.game.bet.recharge.dao.entity.ModeEntity;
import com.wisp.game.bet.recharge.dao.entity.RoleEntity;

@MyBatisDao(tableName = "recharge_sys_role" )
public interface RoleDao extends CrudDao<RoleEntity>
{
}
