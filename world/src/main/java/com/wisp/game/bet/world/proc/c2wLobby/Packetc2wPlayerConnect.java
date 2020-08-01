package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.world.unit.WorldPeer;
import com.wisp.game.bet.world.unit.WorldServer;
import com.wisp.game.bet.db.mongo.account.service.AccountTableServiceImpl;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.db.mongo.account.info.AccountTableDoc;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromSID;
import msg_type_def.MsgTypeDef;

@IRequest
public class Packetc2wPlayerConnect extends RequestMessageFromSID<Client2WorldProtocol.packetc2w_player_connect,WorldPeer> {

    private AccountTableServiceImpl accountTableService;

    @Override
    public boolean packet_process(WorldPeer peer, int sessionId, Client2WorldProtocol.packetc2w_player_connect msg) {

        if( accountTableService == null )
        {
            accountTableService = SpringContextHolder.getBean(AccountTableServiceImpl.class);
        }

        AccountTableDoc accountTableInfo = accountTableService.load_data(msg.getAccount());

        String csToken = "";        //此值没有赋值

        boolean ret = accountTableService.check_token(accountTableInfo,msg.getAccount(),msg.getToken(),msg.getSign(), WorldServer.Instance.get_serverid());
        if(ret)
        {
             GamePlayer p = GamePlayerMgr.Instance.find_player(msg.getAccount());
             if( p != null )
             {
                 if( p.get_sessionid() !=  sessionId )
                 {
                     Client2WorldProtocol.packetw2c_player_kick.Builder builder = Client2WorldProtocol.packetw2c_player_kick.newBuilder();
                     peer.send_msg(builder.build());

                     GamePlayerMgr.Instance.reset_player(p,sessionId);
                     p.set_sessionid(sessionId);
                     p.player_login(accountTableInfo,msg.getPlatform(),msg.getLoginPlatform(),true,msg.getToken());
                     return true;
                 }
             }
             else
             {
                 GamePlayer gamePlayer = GamePlayerMgr.Instance.find_player(sessionId);
                 if( gamePlayer != null )
                 {
                     gamePlayer.player_logout();
                     GamePlayerMgr.Instance.remove_session(sessionId);
                 }

                 gamePlayer = new GamePlayer();
                 gamePlayer.set_sessionid(sessionId);
                 gamePlayer.player_login(accountTableInfo,msg.getPlatform(),msg.getLoginPlatform(),false,csToken);
                 GamePlayerMgr.Instance.add_player(gamePlayer);
                 return true;
             }
        }
        else
        {
            Client2WorldProtocol.packetw2c_player_connect_result.Builder builder = Client2WorldProtocol.packetw2c_player_connect_result.newBuilder();
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail_VALUE);
            peer.send_msg(builder.build());
        }

        return true;
    }

}
