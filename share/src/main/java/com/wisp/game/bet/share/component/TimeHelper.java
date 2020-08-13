package com.wisp.game.bet.share.component;

import org.springframework.stereotype.Component;

@Component
public class TimeHelper {

    private long m_startTime = 0;
    private long m_baseTime = 0;

    public static TimeHelper Instance;

    public TimeHelper() {
        TimeHelper.Instance = this;
    }

    public void set_base_time(int _t)
    {
        m_baseTime = Integer.valueOf(_t).longValue() * 1000;
        m_startTime = get_cur_ms();
    }

    /**
     * 返回秒时间戳
     * @return
     */
    public int get_cur_time()
    {
        long cur_tm_ms = get_cur_ms();

        return millToSecond(cur_tm_ms);
    }

    private long get_tick_count()
    {
        return get_cur_ms() - m_startTime;
    }

    private int get_cur_seconds()
    {
        long cur_mills = System.currentTimeMillis();
        return millToSecond(cur_mills);
    }

    /**
     * 返回毫秒时间戳
     * @return
     */
    public long get_cur_ms()
    {
        if( m_baseTime <= 0 )
        {
            return get_cur_seconds();
        }

        long tm_ms = m_baseTime + get_tick_count();

        return  tm_ms;
    }

    private int millToSecond(long mills)
    {
        int transSecond = (int)(mills/1000);

        return transSecond;
    }
}
