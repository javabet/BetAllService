package com.wisp.game.bet.sshare.convert;

public class TimeInt  {
    private int secondTime;

    public int getSecondTime() {
        return secondTime;
    }

    public void setSecondTime(int secondTime) {
        this.secondTime = secondTime;
    }


    public static TimeInt create()
    {
        return new TimeInt();
    }

    public static TimeInt create(int time)
    {
        TimeInt timeInt = new TimeInt();
        timeInt.setSecondTime(time);
        return timeInt;
    }
}
