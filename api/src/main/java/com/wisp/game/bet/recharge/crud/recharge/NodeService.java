package com.wisp.game.bet.recharge.crud.recharge;

import com.google.gson.Gson;
import com.wisp.core.persistence.Page;
import com.wisp.core.service.CrudService;
import com.wisp.game.bet.recharge.dao.NodeDao;
import com.wisp.game.bet.recharge.dao.entity.NodeEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodeService extends CrudService<NodeDao, NodeEntity>
{
    public List<NodeEntity> list()
    {
        Page<NodeEntity> page = new Page<NodeEntity>();
        page.setStart(0);
        page.setLength(1000l);
        List<NodeEntity> nodeEntities =  dao.findList(page);

        System.out.println(new Gson().toJson(nodeEntities));

        return nodeEntities;
    }

    public void insert(NodeEntity entity)
    {
        dao.insert(entity);
    }


    public List<NodeEntity> findListByOrder()
    {
        return dao.findListByOrder();
    }
}
