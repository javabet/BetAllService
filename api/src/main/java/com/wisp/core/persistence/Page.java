package com.wisp.core.persistence;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分页类
 *
 * @param <T>
 * @author www.lbanma.com
 * @version 2013-7-2
 */
public class Page<T> {
    public static final String DB_NAME = "mysql";
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
    public static final long MAX_RESULT_COUNT = 100;
    private long start;
    private Long length;
    private long iTotalRecords;
    private long iTotalDisplayRecords;
    private List<T> data;

    public Page() {
        length = MAX_RESULT_COUNT;
    }

    public Page(long start, long length) {
        this.start = start;
        this.length = length;

    }

    public Page<T> setData(List<T> data) {
        this.data = data;
        if (data != null)
            this.iTotalRecords = data.size();
        return this;
    }

    public Page<T> setCount(long count) {
        this.iTotalDisplayRecords = count;
        return this;
    }

    public long getMaxResults() {
        return length;
    }

    public long getFirstResult() {
        return start;
    }

    public long getPageSize() {
        return length;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
    public void setLength(int length) {
        this.length = ((long) length);
    }

    public long getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(long iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public long getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(long iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public List<T> getData() {
        return data;
    }
}
