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
        m_startTime = System.currentTimeMillis();
    }

    /**
     * 返回秒时间戳
     * @return
     */
    public int get_cur_time()
    {
        if( m_baseTime <= 0 )
        {
            return millToSecond(System.currentTimeMillis());
        }

        return millToSecond(m_baseTime + get_tick_count());
    }

    //相对的间隔数据
    private long get_tick_count()
    {
        return System.currentTimeMillis() - m_startTime;
    }


    /**
     * 返回毫秒时间戳
     * @return
     */
    public long get_cur_ms()
    {
        if( m_baseTime <= 0 )
        {
            return System.currentTimeMillis();
        }

        return   m_baseTime + get_tick_count();
    }

    private int millToSecond(long mills)
    {
        int transSecond = (int)(mills/1000);

        return transSecond;
    }
}
