package com.wisp.game.bet.sshare.convert;

public class TimeLong {
    private long millsSecond;

    public long getMillsSecond() {
        return millsSecond;
    }

    public void setMillsSecond(long millsSecond) {
        this.millsSecond = millsSecond;
    }

    public static TimeLong create()
    {
        return new TimeLong();
    }

    public static TimeLong create(long time)
    {
        TimeLong timeLong = new TimeLong();
        timeLong.setMillsSecond(time);
        return  timeLong;
    }
}
