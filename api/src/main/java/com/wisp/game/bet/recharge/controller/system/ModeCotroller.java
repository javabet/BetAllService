package com.wisp.game.bet.recharge.controller.system;

import com.wisp.core.service.ResponseResult;
import com.wisp.core.utils.encrypt.MD5Util;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.common.ErrorCode;
import com.wisp.game.bet.recharge.crud.recharge.ModeService;
import com.wisp.game.bet.recharge.dao.entity.ModeEntity;
import com.wisp.game.bet.recharge.dao.entity.NodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/system/mode")
@ResponseResult
@Validated
public class ModeCotroller extends BaseController
{
    @Autowired
    private ModeService modeService;

    @RequestMapping(method = RequestMethod.GET,value = "/list")
    public Object List(@RequestParam(name = "KeyWord",defaultValue = "") String keyWord)
    {
        List<ModeEntity> list = modeService.findByKey(keyWord);

        //set redis
        return list;
    }

    @Transactional("rechargeTransactionManger")
    @RequestMapping(method = RequestMethod.POST,value = {"/",""})
    public Object Add(@Valid @RequestBody ModeEntity modeEntity)
    {
        if( modeEntity.getId()  != null && modeEntity.getId()  == 0 )
        {
            modeEntity.setId(null);
        }

        if( modeEntity.getType() == 0 )
        {
            modeEntity.setType(1);      //1:项目模块， 2：系统模块
        }

        if( modeEntity.getParentId() == 0 )
        {
            modeEntity.setParentId(-1);
        }

        modeEntity.setMd5(MD5Util.getMD5(modeEntity.getKey()));

        if( modeEntity.getParentId() > 0 )
        {
            ModeEntity PModeEntity =  modeService.get(modeEntity.getParentId());
            modeEntity.setMd5( MD5Util.getMD5( PModeEntity.getKey() + modeEntity.getKey() ) );
            modeEntity.setType(3);
        }

        modeService.save(modeEntity,true);

        if( modeEntity.getParentId() == -1 )
        {
            ModeEntity modeEntity1 = new ModeEntity();
            modeEntity1.setName("添加");
            modeEntity1.setType(3);
            modeEntity1.setKey("add");
            modeEntity1.setMd5(MD5Util.getMD5(modeEntity.getKey()+"add"));
            modeEntity1.setLogs(1);
            modeEntity1.setParentId(modeEntity.getIdInt());
            modeService.save(modeEntity1,true);

            ModeEntity modeEntity2 = new ModeEntity();
            modeEntity2.setName("编辑");
            modeEntity2.setType(3);
            modeEntity2.setKey("edit");
            modeEntity2.setMd5(MD5Util.getMD5(modeEntity.getKey()+"edit"));
            modeEntity2.setLogs(1);
            modeEntity2.setParentId(modeEntity.getIdInt());
            modeService.save(modeEntity2,true);

            {
                ModeEntity tmpModeEntity = new ModeEntity();
                tmpModeEntity.setName("删除");
                tmpModeEntity.setType(3);
                tmpModeEntity.setKey("del");
                tmpModeEntity.setMd5(MD5Util.getMD5(modeEntity.getKey()+"del"));
                tmpModeEntity.setLogs(1);
                tmpModeEntity.setParentId(modeEntity.getIdInt());
                modeService.save(tmpModeEntity,true);
            }

            {
                ModeEntity tmpModeEntity = new ModeEntity();
                tmpModeEntity.setName("列表");
                tmpModeEntity.setType(3);
                tmpModeEntity.setKey("list");
                tmpModeEntity.setMd5(MD5Util.getMD5(modeEntity.getKey()+"list"));
                tmpModeEntity.setLogs(2);
                tmpModeEntity.setParentId(modeEntity.getIdInt());
                modeService.save(tmpModeEntity,true);
            }

            {
                ModeEntity tmpModeEntity = new ModeEntity();
                tmpModeEntity.setName("查看");
                tmpModeEntity.setType(3);
                tmpModeEntity.setKey("get");
                tmpModeEntity.setMd5(MD5Util.getMD5(modeEntity.getKey()+"get"));
                tmpModeEntity.setLogs(2);
                tmpModeEntity.setParentId(modeEntity.getIdInt());
                modeService.save(tmpModeEntity,true);
            }
        }

        return emptySucc();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Object get(@PathVariable("id") int id )
    {
        ModeEntity modeEntity = modeService.get(id);

        return modeEntity;
    }

    @Transactional("rechargeTransactionManger")
    @RequestMapping(method = RequestMethod.PUT,value = {"/",""})
    public Object Edit(@Valid @RequestBody ModeEntity modeEntity)
    {
        ModeEntity modeEntity1 =  modeService.get(modeEntity.getId());
        if( modeEntity1 == null )
        {
            return error(ErrorCode.ERR_DB_DATA.getCode());
        }

        modeEntity1.setName(modeEntity.getName());
        modeEntity1.setType(modeEntity.getType());
        modeEntity1.setKey(modeEntity.getKey());
        modeEntity1.setParentId(modeEntity.getParentId());
        modeEntity1.setLogs(modeEntity.getLogs());
        modeEntity1.setDescription(modeEntity.getDescription());
        modeEntity1.setSort(modeEntity.getSort());
        modeEntity1.setNodeId(modeEntity.getNodeId());

        if(modeEntity1.getParentId() == 0)
        {
            modeEntity1.setParentId(-1);
        }
        modeEntity1.setMd5(MD5Util.getMD5(modeEntity.getKey()));

        if( modeEntity.getParentId() > 0 )
        {
            ModeEntity paretModeEntity = modeService.get(modeEntity.getParentId());
            modeEntity1.setMd5( paretModeEntity.getKey() + modeEntity1.getKey() );
            modeEntity1.setType(3);
        }

        modeService.save(modeEntity1);

        return emptySucc();
    }

    @Transactional("rechargeTransactionManger")
    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public Object Del(@PathVariable("id") int id)
    {
        ModeEntity modeEntity =  modeService.get(id);
        if( modeEntity == null )
        {
            return error( ErrorCode.ERR_DB_DATA.getCode() );
        }

        //先删除所有子节点
        int deleteRow =  modeService.deleteChild(id);

        //再删除主节点
        modeService.delete(id);

        return emptySucc();
    }

}
