package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.world.unit.WorldPeer;
import com.wisp.game.bet.world.unit.WorldServer;
import com.wisp.game.bet.world.db.mongo.account.service.AccountTableServiceImpl;
import com.wisp.game.core.SpringContextHolder;
import com.wisp.game.bet.world.db.mongo.account.info.AccountTableInfo;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.RequestMessageFromSID;

@IRequest
public class Packetc2wPlayerConnect extends RequestMessageFromSID<Client2WorldProtocol.packetc2w_player_connect,WorldPeer> {

    private AccountTableServiceImpl accountTableService;

    @Override
    public boolean packet_process(WorldPeer peer, int sessionId, Client2WorldProtocol.packetc2w_player_connect msg) {

        if( accountTableService == null )
        {
            accountTableService = SpringContextHolder.getBean(AccountTableServiceImpl.class);
        }

        AccountTableInfo accountTableInfo = accountTableService.load_data(msg.getAccount());

        boolean ret = accountTableService.check_token(accountTableInfo,msg.getAccount(),msg.getToken(),msg.getSign(), WorldServer.Instance.get_serverid());
        if(ret)
        {
             GamePlayer p = GamePlayerMgr.Instance.find_player(msg.getAccount());
             if( p.get_sessionid() !=  sessionId )
             {
                 Client2WorldProtocol.packetw2c_player_kick.Builder builder = Client2WorldProtocol.packetw2c_player_kick.newBuilder();
                 peer.send_msg(builder.build());

                 GamePlayerMgr.Instance.reset_player(p,sessionId);
                p.set_sessionid(sessionId);
                p.player_login(msg.getAccount(),msg.getPlatform(),msg.getLoginPlatform(),true,msg.getToken());
                 return true;
             }
        }
        else
        {

        }

        return true;
    }

}
