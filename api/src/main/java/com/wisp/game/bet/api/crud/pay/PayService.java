package com.wisp.game.bet.api.crud.pay;

import com.wisp.core.service.CrudService;
import com.wisp.game.bet.api.dao.pay.PayDao;
import com.wisp.game.bet.api.dao.pay.entity.PayEntity;
import org.springframework.stereotype.Service;

@Service
public class PayService extends CrudService<PayDao, PayEntity>
{
    public PayEntity findById(int id)
    {
        return dao.findById(id);
    }
}
