package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.BackstageManager;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;


@IRequest
public class PacketUpdateServersInfo extends DefaultRequestMessage<ServerProtocol.packet_updata_servers_info,ServerPeer> {

    private int lastTime = 0;
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_updata_servers_info msg) {

        if( lastTime == 0 )
        {
            lastTime = TimeHelper.Instance.get_cur_time();
        }
        else
        {
            logger.info("gap:" + ( TimeHelper.Instance.get_cur_time() - lastTime ) );
            lastTime = TimeHelper.Instance.get_cur_time();
        }

        ConcurrentHashMap<Integer, ServerBase.server_info> sInfoMap =  BackstageManager.Instance.getSInfoMap();

        for(int i = 0; i < msg.getSinfosList().size();i++)
        {
            server_protocols.ServerBase.server_info server_info = msg.getSinfos(i);
            ServerBase.server_info server_info1 =  sInfoMap.get(server_info.getServerPort());
            if( server_info1 != null )
            {
                sInfoMap.put(server_info.getServerPort(),server_info);
            }
            else
            {
                sInfoMap.put(server_info.getServerPort(),server_info);
            }
        }

        BackstageManager.Instance.check_servers();

        return true;
    }
}
