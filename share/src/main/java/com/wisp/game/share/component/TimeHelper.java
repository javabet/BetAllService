package com.wisp.game.share.component;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TimeHelper {

    private Long m_startTime = 0l;
    private Long m_baseTime = 0l;

    public static TimeHelper Instance;

    public TimeHelper() {
        TimeHelper.Instance = this;
    }

    public void set_base_time(Long _t)
    {
        m_baseTime = _t;
        m_startTime = get_cur_seconds();
    }

    public Long get_cur_time()
    {
        if( m_baseTime <= 0 )
        {
            return get_cur_seconds();
        }


        return m_baseTime + get_tick_count();
    }

    public Long get_tick_count()
    {
        return get_cur_seconds() - m_startTime;
    }

    private Long get_cur_seconds()
    {
        Long mills =  System.currentTimeMillis();

        return mills - mills % 1000;
    }
}
