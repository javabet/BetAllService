package com.wisp.game.bet.recharge.common.commonInfo;

import com.wisp.core.persistence.Page;

public class QueryPage<T> extends Page<T>
{

    private long beginTime;
    private long endTime;
    private String keyWord;

    public long getBeginTime()
    {
        return beginTime;
    }

    public void setBeginTime(long beginTime)
    {
        this.beginTime = beginTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    public String getKeyWord()
    {
        return keyWord;
    }

    public void setKeyWord(String keyWord)
    {
        this.keyWord = keyWord;
    }
}
