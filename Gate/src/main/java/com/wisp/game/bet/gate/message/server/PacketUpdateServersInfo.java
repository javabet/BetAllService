package com.wisp.game.bet.gate.message.server;

import com.wisp.game.bet.gate.services.BackstageManager;
import com.wisp.game.bet.gate.services.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import io.netty.channel.ChannelId;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;


@IRequest
public class PacketUpdateServersInfo extends DefaultRequestMessage<ServerProtocol.packet_updata_servers_info,ServerPeer> {


    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_updata_servers_info msg) {

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
