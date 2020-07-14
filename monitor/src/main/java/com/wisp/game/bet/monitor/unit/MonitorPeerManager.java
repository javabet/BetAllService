package com.wisp.game.bet.monitor.unit;


import com.wisp.game.share.common.EnableObjectManager;
import io.netty.channel.ChannelId;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class MonitorPeerManager extends EnableObjectManager<ChannelId,MonitorPeer> {

    public static MonitorPeerManager Instance;
    public MonitorPeerManager() {
        MonitorPeerManager.Instance = this;
    }

    public void heartbeat(double elapsed)
    {
        Iterator<MonitorPeer> it = obj_map.values().iterator();
        while ( it.hasNext())
        {
            MonitorPeer monitorPeer = it.next();
            monitorPeer.heartbeat(elapsed);
        }
    }

    public boolean add_obj(ChannelId obj_id,MonitorPeer obj)
    {
        boolean ret =  super.add_obj(obj_id,obj);
        if(ret)
        {

        }
        return true;
    }

}
