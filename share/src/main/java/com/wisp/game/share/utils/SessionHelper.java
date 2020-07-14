package com.wisp.game.share.utils;

public class SessionHelper {
    public static int get_sessionid(int gateId,int peerId)
    {
        int ret = gateId << 16;
        return ret + peerId;
    }

    public static int get_gateid( int sessionId )
    {
        return sessionId >> 16;
    }

    public static int get_peerid(int sessionId)
    {
        return sessionId & 0x0000ffff;
    }
}
