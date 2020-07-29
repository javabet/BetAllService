package com.wisp.game.bet.world.proc.server;

import com.wisp.game.bet.world.unit.BackstageManager;
import com.wisp.game.bet.world.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@IRequest
public class PacketUpdataServersInfo extends DefaultRequestMessage<ServerProtocol.packet_updata_servers_info, ServerPeer> {
    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_updata_servers_info msg) {

        ConcurrentHashMap<Integer, ServerBase.server_info> sinfoMap =  BackstageManager.Instance.SInfoMap;

        java.util.List<server_protocols.ServerBase.server_info> sinfoList =  msg.getSinfosList();

        for(int i = 0;i < sinfoList.size();i++)
        {
             ServerBase.server_info server_info =  sinfoList.get(i);
             if( sinfoMap.containsKey(server_info.getServerPort()) )
             {
                 sinfoMap.put(server_info.getServerPort(),server_info);
             }
             else
             {
                 sinfoMap.put(server_info.getServerPort(),server_info);
             }
        }

        return true;
    }
}
