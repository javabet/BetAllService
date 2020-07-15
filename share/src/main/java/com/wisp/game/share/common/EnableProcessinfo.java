package com.wisp.game.share.common;

public class EnableProcessinfo {

    public String get_processname()
    {
        return "";
    }

    private static long base_tick_count = 0;

    public static int get_tick_count()
    {
        if( base_tick_count == 0 )
        {
            base_tick_count = System.currentTimeMillis();
        }

        long cur_tm_s =  System.currentTimeMillis();

        return (int)(cur_tm_s - base_tick_count);
    }

}
