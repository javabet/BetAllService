package com.wisp.game.bet.monitor.unit;


import com.wisp.game.bet.share.common.EnableObjectManager;
import io.netty.channel.ChannelId;
import org.springframework.stereotype.Component;

import java.util.Iterator;


/**
 * 此类是不需要的，原来需要，是因为其分为两个过程，一个是创建前，一个是创建后
 * 现在使用netty,创建前的过程，并不存在，所以此类不需要了
 */
@Deprecated
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

}
