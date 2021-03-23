package com.wisp.game.bet.api.dao.pay;

import com.wisp.core.persistence.CrudDao;
import com.wisp.core.persistence.MyBatisDao;
import com.wisp.game.bet.api.dao.pay.entity.PayEntity;

@MyBatisDao(tableName = "pay" )
public interface PayDao extends CrudDao<PayEntity>
{
    public PayEntity findById(int id);
}
