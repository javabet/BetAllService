package com.wisp.game.bet.recharge.crud.recharge;

import com.wisp.core.persistence.Page;
import com.wisp.core.service.CrudService;
import com.wisp.game.bet.recharge.dao.NodeDao;
import com.wisp.game.bet.recharge.dao.entity.NodeEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeService extends CrudService<NodeDao, NodeEntity>
{
    public int add( NodeEntity entity )
    {
        return dao.insert(entity);
    }

    public List<NodeEntity> list()
    {
        Page<NodeEntity> page = new Page<NodeEntity>();
        page.setStart(0);
        page.setLength(1000l);
        List<NodeEntity> nodeEntities =  dao.findList(page);
        return nodeEntities;
    }

    public void insert(NodeEntity entity)
    {
        dao.insert(entity);
    }

    public int update(NodeEntity nodeEntity)
    {
        return dao.update(nodeEntity);
    }
}
