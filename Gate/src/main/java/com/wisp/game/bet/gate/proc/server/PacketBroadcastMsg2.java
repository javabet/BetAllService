package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.ClientManager;
import com.wisp.game.bet.gate.unit.GatePeer;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@IRequest
public class PacketBroadcastMsg2 extends DefaultRequestMessage<ServerProtocol.packet_broadcast_msg2, ServerPeer> {
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_broadcast_msg2 msg) {
        ConcurrentHashMap<Integer, GatePeer> cMap = ClientManager.Instance.get_map();

        for(GatePeer gatePeer : cMap.values())
        {
            if( msg.getGameSid() == 0 || msg.getGameSid() == gatePeer.logic_id )
            {
                gatePeer.send_msg(msg.getMsgpak().getMsgid(),msg.getMsgpak().getMsginfo());
            }
        }

        return true;
    }
}
