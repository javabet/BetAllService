package com.wisp.game.bet.recharge.common.commonInfo;

import javax.validation.constraints.Max;

public class Pagination
{
    private long CurrPage;
    private long PageSize;
    private long MaxCount;
    private long maxPageCount;

    public Pagination(long currPage,long pageSize,long maxCount)
    {
        this.setCurrPage(currPage);
        this.setPageSize(pageSize);
        this.setMaxCount(maxCount);

        this.set();
    }

    public void set()
    {
        if( PageSize < 0 )
        {
            PageSize = 15;
        }
        if( MaxCount < 0 )
        {
            MaxCount = 0;
        }

        maxPageCount = MaxCount / PageSize;

        if( MaxCount % PageSize > 0 )
        {
            maxPageCount += 1;
        }

        if( maxPageCount <= 0  )
        {
            maxPageCount = 1;
        }
    }

    public long getCurrPage()
    {
        return CurrPage;
    }

    public void setCurrPage(long currPage)
    {
        CurrPage = currPage;
    }

    public long getPageSize()
    {
        return PageSize;
    }

    public void setPageSize(long pageSize)
    {
        PageSize = pageSize;
    }

    public long getMaxCount()
    {
        return MaxCount;
    }

    public void setMaxCount(long maxCount)
    {
        MaxCount = maxCount;
    }

    public long getMaxPageCount()
    {
        return maxPageCount;
    }

    public void setMaxPageCount(long maxPageCount)
    {
        this.maxPageCount = maxPageCount;
    }

    public long getOffset()
    {
        if( CurrPage > maxPageCount )
        {
            return MaxCount;
        }

        return (CurrPage - 1) * PageSize;
    }
}
