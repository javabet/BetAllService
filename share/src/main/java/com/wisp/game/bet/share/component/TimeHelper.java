package com.wisp.game.bet.share.component;

import org.springframework.stereotype.Component;

@Component
public class TimeHelper {

    private int m_startTime = 0;
    private int m_baseTime = 0;

    public static TimeHelper Instance;

    public TimeHelper() {
        TimeHelper.Instance = this;
    }

    public void set_base_time(int _t)
    {
        m_baseTime = _t;
        m_startTime = get_cur_seconds();
    }

    public int get_cur_time()
    {
        if( m_baseTime <= 0 )
        {
            return get_cur_seconds();
        }


        return m_baseTime + get_tick_count();
    }

    public int get_tick_count()
    {
        return get_cur_seconds() - m_startTime;
    }

    private int get_cur_seconds()
    {
        int mills =  Integer.valueOf( String.valueOf(System.currentTimeMillis())) ;

        return mills - mills % 1000;
    }
}
