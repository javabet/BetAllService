package com.wisp.game.bet.recharge.dao;

import com.wisp.core.persistence.CrudDao;
import com.wisp.core.persistence.MyBatisDao;
import com.wisp.game.bet.recharge.dao.entity.ModeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao(tableName = "recharge_sys_mode" )
public interface ModeDao extends CrudDao<ModeEntity>
{
    List<ModeEntity> findByKey(@Param("KeyWord") String key);

    int  deleteChild( int parentId);
}
