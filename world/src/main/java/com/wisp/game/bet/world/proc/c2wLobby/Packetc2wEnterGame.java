package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.db.mongo.player.doc.OnlineRoomCardDoc;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.dbConfig.AgentInfoConfig;
import com.wisp.game.bet.world.gameMgr.GameEngineMgr;
import com.wisp.game.bet.world.proc.DefaultWorldRequestMessage;
import com.wisp.game.bet.world.unit.ServersManager;
import com.wisp.game.bet.world.unit.WorldPeer;
import msg_type_def.MsgTypeDef;

@IRequest(5004)
public class Packetc2wEnterGame extends DefaultWorldRequestMessage<Client2WorldProtocol.packetc2w_enter_game>
{
    @Override
    public boolean packet_process(WorldPeer peer, GamePlayer player, Client2WorldProtocol.packetc2w_enter_game msg)
    {
        Client2WorldProtocol.packetw2c_enter_game_result.Builder builder = Client2WorldProtocol.packetw2c_enter_game_result.newBuilder();

        int serverId = -1;
        MsgTypeDef.e_msg_result_def result = MsgTypeDef.e_msg_result_def.e_rmt_success;
        if (msg.hasRoomCardCfgBytes())
        {
            //创建房间，分配房号
            GameEngineMgr.GameInfoStruct gameInfoStruct = GameEngineMgr.Instance.get_game_info_struct(msg.getGameid());
            if (gameInfoStruct != null)
            {
                serverId = gameInfoStruct.serverId;
            }
            else
            {
                result = MsgTypeDef.e_msg_result_def.e_rmt_server_down;
            }
        }
        else if (msg.hasRoomcardNumber() && msg.getRoomcardNumber() > 0)
        {
            //加入房间,检测房号是否存在,以及ServerID
            OnlineRoomCardDoc onlineRoomCardDoc = GameEngineMgr.Instance.get_room_number_server_struct(msg.getRoomcardNumber());
            if (onlineRoomCardDoc != null)
            {
                serverId = onlineRoomCardDoc.getServerId();
                WorldPeer worldPeer = ServersManager.Instance.find_server(serverId);
                if (worldPeer == null)
                {
                    result = MsgTypeDef.e_msg_result_def.e_rmt_server_down;
                }
            }
            else
            {
                result = MsgTypeDef.e_msg_result_def.e_rmt_server_down;
            }
        }
        else
        {
            GameEngineMgr.GameInfoStruct gameInfoStruct = GameEngineMgr.Instance.get_game_info_struct(msg.getGameid());
        }

        if (result != MsgTypeDef.e_msg_result_def.e_rmt_success)
        {
            builder.setResult(result);
        }
        else if( serverId > 0 && AgentInfoConfig.Instance.hasGame(player.getPlayerInfoDoc().getChannelId(),msg.getGameid()))
        {
            int roomNum = -1;
            if( msg.hasRoomcardNumber() )
            {
                roomNum = msg.getRoomcardNumber();
            }
            if (player.join_game(msg.getGameid(), serverId, msg.getRoomid(),roomNum,msg.getRoomCardCfgBytes()))
            {
                return true;
            }
        }
        else
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_shutdown);
        }



        peer.send_msg_to_client(player.get_sessionid(), builder);

        return true;
    }
}
