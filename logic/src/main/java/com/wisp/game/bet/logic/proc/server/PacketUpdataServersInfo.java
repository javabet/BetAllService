package com.wisp.game.bet.logic.proc.server;

import com.wisp.game.bet.logic.unit.BackstageManager;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@IRequest
public class PacketUpdataServersInfo extends DefaultRequestMessage<ServerProtocol.packet_updata_servers_info, ServerPeer> {
    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_updata_servers_info msg) {

        ConcurrentHashMap<Integer, ServerBase.server_info> simap = BackstageManager.Instance.SInfoMap;

        for( int i = 0; i < msg.getSinfosList().size();i++ )
        {
            ServerBase.server_info server_info =  simap.get(msg.getSinfos(i).getServerPort());
            if( server_info != null )
            {
                if( server_info.hasAttributes() )
                {
                   simap.put(msg.getSinfos(i).getServerPort(),msg.getSinfos(i));
                }
            }
            else
            {
                simap.put(msg.getSinfos(i).getServerPort(),msg.getSinfos(i));
            }
        }

        BackstageManager.Instance.connect_world();

        return true;
    }
}
