package com.wisp.core.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * 数据Entity类
 *
 * @author www.lbanma.com
 * @version 2014-05-16
 */
public abstract class DataEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    protected transient Date CreateTime;    // 创建日期
    protected  long CreateTimeNumber;      //创建日期时间戳
    protected transient  Date UpdateTime;           // 更新日期
    protected transient  long UpdateTimeNumber;     // 更新日期时间戳
    protected transient  int deleteFlag;    // 删除标记（0：正常；1：删除；2：审核）
    protected transient  Date DeleteTime;       //删除的时间
    protected transient  long DeleteTimeNumber; //删除的时间戳

    public DataEntity() {
        super();
        this.deleteFlag = DEL_FLAG_NORMAL;
    }

    public DataEntity(Long id) {
        super(id);
    }


    @Override
    public String getRedisField() {
        return null;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        this.CreateTime = createTime;
    }

    public Date getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.UpdateTime = updateTime;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public long getCreateTimeNumber()
    {
        return CreateTimeNumber;
    }

    public void setCreateTimeNumber(long createTimeNumber)
    {
        CreateTimeNumber = createTimeNumber;
    }

    public long getUpdateTimeNumber()
    {
        return UpdateTimeNumber;
    }

    public void setUpdateTimeNumber(long updateTimeNumber)
    {
        UpdateTimeNumber = updateTimeNumber;
    }

    public Date getDeleteTime()
    {
        return DeleteTime;
    }

    public void setDeleteTime(Date deleteTime)
    {
        DeleteTime = deleteTime;
    }

    public long getDeleteTimeNumber()
    {
        return DeleteTimeNumber;
    }

    public void setDeleteTimeNumber(long deleteTimeNumber)
    {
        DeleteTimeNumber = deleteTimeNumber;
    }
}
