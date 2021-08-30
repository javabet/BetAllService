package com.wisp.game.bet.recharge.controller.system;

//import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.common.ErrorCode;
import com.wisp.game.bet.recharge.crud.recharge.NodeService;
import com.wisp.game.bet.recharge.dao.entity.NodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

@RestController
@RequestMapping("/system/node")
@ResponseResult
@Validated
public class NodeController extends BaseController
{
    @Autowired
    private NodeService nodeService;

    @RequestMapping(method = RequestMethod.GET,value = "/list")
    public Object List()
    {
        return nodeService.list();
    }

    @Transactional("rechargeTransactionManger")
    @PostMapping()
    public Object Add(@Valid @RequestBody NodeEntity nodeEntity)
    {
        if( nodeEntity.getParentId() == 0 )
        {
            nodeEntity.setParentId(-1);
        }

        nodeEntity.setParentTree(",-1,");
        if( nodeEntity.getParentId() > 0 )
        {
            NodeEntity parentNode = nodeService.get(nodeEntity.getParentId());
            if( parentNode == null )
            {
                return error(ErrorCode.ERR_DB_DATA.getCode());
            }
            nodeEntity.setParentTree(parentNode.getParentTree());
        }

        //更新ParentTree，链接自身的Id
        int insertNum =  nodeService.save(nodeEntity,true);
        if( insertNum == 0 )
        {
            return error(ErrorCode.ERR_DB_INSERT.getCode());
        }
        nodeEntity.setParentTree(nodeEntity.getParentTree()  +  nodeEntity.getId() + "");
        nodeService.save(nodeEntity);

        return emptySucc();
    }



    @RequestMapping( method = RequestMethod.GET,value = "/{id}")
    public Object Get(@PathVariable long id)
    {
        System.out.println("id:11" + id);

        NodeEntity nodeEntity = nodeService.get(id);
        return nodeEntity;
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/")
    public Object Edit(@Valid @RequestBody NodeEntity nodeEntity)
    {
        if( nodeEntity.getId() <= 0 )
        {
            return error(ErrorCode.ERR_PARAM_FIELD.getCode());
        }
        nodeService.save(nodeEntity);
        //TODO wisp
        //存入redis缓存
        return nodeEntity;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public Object Del(@PathVariable long id )
    {
        NodeEntity nodeEntity = nodeService.get(id);
        if( nodeEntity == null )
        {
            return error(ErrorCode.ERR_NO_DATA.getCode());
        }

        nodeService.delete(nodeEntity);

        return emptySucc();
    }
}
