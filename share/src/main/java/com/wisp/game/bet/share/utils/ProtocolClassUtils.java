package com.wisp.game.bet.share.utils;

import com.google.protobuf.Message;

import java.lang.reflect.Method;

public class ProtocolClassUtils {

    public static int getProtocolByClass(Class<? extends Message> cls )
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
