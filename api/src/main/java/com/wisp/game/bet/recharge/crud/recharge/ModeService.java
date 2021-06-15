package com.wisp.game.bet.recharge.crud.recharge;

import com.wisp.core.service.CrudService;
import com.wisp.game.bet.recharge.dao.ModeDao;
import com.wisp.game.bet.recharge.dao.NodeDao;
import com.wisp.game.bet.recharge.dao.entity.ModeEntity;
import com.wisp.game.bet.recharge.dao.entity.NodeEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeService  extends CrudService<ModeDao, ModeEntity>
{
    public List<ModeEntity> findByKey(String key)
    {
        return dao.findByKey(key);
    }

    public int deleteChild(int parentId)
    {
        return dao.deleteChild(parentId);
    }
}
