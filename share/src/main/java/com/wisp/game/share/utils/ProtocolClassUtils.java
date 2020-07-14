package com.wisp.game.share.utils;

import java.lang.reflect.Method;

public class ProtocolClassUtils {

    public static int getProtocolByClass(Class<?> cls )
    {
        try
        {
            Method newBuildMethod = cls.getMethod("newBuilder");
            Object builder = newBuildMethod.invoke(null);
            Method packetIdMethod = builder.getClass().getMethod("getPacketId");
            Object getPacketIdMsg = packetIdMethod.invoke(builder);
            Method getNumberMethod = getPacketIdMsg.getClass().getMethod("getNumber");
            int protocolId = (int)getNumberMethod.invoke(getPacketIdMsg);

            return protocolId;
        }
        catch (Exception error)
        {
            return -1;
        }
    }


}
