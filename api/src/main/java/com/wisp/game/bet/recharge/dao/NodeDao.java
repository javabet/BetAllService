package com.wisp.game.bet.recharge.dao;

import com.wisp.core.persistence.CrudDao;
import com.wisp.core.persistence.MyBatisDao;
import com.wisp.game.bet.recharge.dao.entity.NodeEntity;

import java.util.List;

@MyBatisDao(tableName = "recharge_sys_node" )
public interface NodeDao  extends CrudDao<NodeEntity>
{
    List<NodeEntity> list();
}
