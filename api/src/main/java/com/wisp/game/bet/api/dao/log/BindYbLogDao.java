package com.wisp.game.bet.api.dao.log;

import com.wisp.core.persistence.CrudDao;
import com.wisp.core.persistence.MyBatisDao;
import com.wisp.game.bet.api.dao.log.entity.BindYbLogEntity;
import org.apache.ibatis.annotations.Param;

@MyBatisDao(tableName = "bndyb_log")
public interface BindYbLogDao extends CrudDao<BindYbLogEntity>
{
    BindYbLogEntity findById(@Param("id") int id);
}
