package com.wisp.game.bet.api.crud.log;

import com.wisp.core.service.CrudService;
import com.wisp.game.bet.api.dao.log.BindYbLogDao;
import com.wisp.game.bet.api.dao.log.entity.BindYbLogEntity;
import org.springframework.stereotype.Service;

@Service
public class BindYbService extends CrudService<BindYbLogDao, BindYbLogEntity>
{
    public BindYbLogEntity findLogById(int id)
    {
        BindYbLogEntity entity =  dao.findById(id);
        return entity;
    }
}
