package com.wisp.game.bet.recharge.common.commonInfo;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

public class QueryInfo
{
    private String Keyword;

    @Required
    private int Page;

    @Required
    private int PageSize;

    private long BeginTime;

    private long EndTime;


    public String getKeyword()
    {
        return Keyword;
    }

    public void setKeyword(String keyword)
    {
        Keyword = keyword;
    }

    public int getPage()
    {
        return Page;
    }

    public void setPage(int page)
    {
        Page = page;
    }

    public int getPageSize()
    {
        return PageSize;
    }

    public void setPageSize(int pageSize)
    {
        PageSize = pageSize;
    }

    public long getBeginTime()
    {
        return BeginTime;
    }

    public void setBeginTime(long beginTime)
    {
        BeginTime = beginTime;
    }

    public long getEndTime()
    {
        return EndTime;
    }

    public void setEndTime(long endTime)
    {
        EndTime = endTime;
    }
}
