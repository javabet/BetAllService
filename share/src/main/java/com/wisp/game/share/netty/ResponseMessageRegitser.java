package com.wisp.game.share.netty;

import com.google.protobuf.Message;
import com.wisp.game.share.utils.ProtocolClassUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ResponseMessageRegitser {
    public static ResponseMessageRegitser Instance;
    private ConcurrentHashMap<Class,Integer> response_map;

    public ResponseMessageRegitser() {
        Instance = this;

        response_map = new ConcurrentHashMap<>();
    }


    public int getProtocolIdByMessageClass(Message message)
    {
        Class clz = message.getClass();
        if( response_map.containsKey(clz) )
        {
            return response_map.get(clz);
        }

        synchronized ( clz )
        {
            if( response_map.containsKey(clz) )
            {
                return response_map.get(clz);
            }

            int protocolId = ProtocolClassUtils.getProtocolByClass(clz);

            response_map.put(clz,protocolId);

            return protocolId;
        }
    }


}
