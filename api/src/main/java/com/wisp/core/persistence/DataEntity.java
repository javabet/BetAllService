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
    protected transient  Date UpdateTime;    // 更新日期
    protected transient int deleteFlag;    // 删除标记（0：正常；1：删除；2：审核）

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
}
