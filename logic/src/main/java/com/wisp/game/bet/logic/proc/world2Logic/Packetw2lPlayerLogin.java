package com.wisp.game.bet.logic.proc.world2Logic;

import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.IGameEngine;
import com.wisp.game.bet.logic.sshare.e_player_state;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import logic2world_protocols.Logic2WorldMsgType;
import logic2world_protocols.Logic2WorldProtocol;
import msg_type_def.MsgTypeDef;

import javax.annotation.Resource;

@IRequest
public class Packetw2lPlayerLogin extends DefaultRequestMessage<Logic2WorldProtocol.packetw2l_player_login, ServerPeer> {

    @Resource
    private IGameEngine gameEngine;

    @Override
    public boolean packet_process(ServerPeer peer, Logic2WorldProtocol.packetw2l_player_login msg) {
        GamePlayer gamePlayer = GamePlayerMgr.Instance.find_playerbyid( msg.getAccountInfo().getAid() );

        Logic2WorldProtocol.packetl2w_player_login_result.Builder builder = Logic2WorldProtocol.packetl2w_player_login_result.newBuilder();
        builder.setPlayerid(msg.getAccountInfo().getAid());

        if( gamePlayer != null )
        {
            GamePlayerMgr.Instance.reset_player(gamePlayer,msg.getSessionid());
            gamePlayer.set_state(e_player_state.e_ps_playing);
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_already_in_game);
            if(!gamePlayer.is_robot())
            {
                gamePlayer.world_id = peer.get_remote_id();
            }
        }
        else if(GameManager.Instance.is_shutdowning() && msg.getAccountInfoEx().getIsRobot() == false)
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_shutdown);
        }
        else
        {
           gamePlayer = new GamePlayer();
           gamePlayer.set_roomid(msg.getRoomid());
           gamePlayer.channelId = msg.getAccountInfo().getChannelId();
           gamePlayer.PlayerID = msg.getAccountInfo().getAid();
           gamePlayer.Gold = (int)msg.getAccountInfo().getGold();
           gamePlayer.NickName = msg.getAccountInfo().getNickname();
           gamePlayer.VIPLevel = msg.getAccountInfo().getViplvl();
           gamePlayer.PhotoFrame = msg.getAccountInfo().getCurPhotoFrameId();
           gamePlayer.Sex = msg.getAccountInfo().getSex();
           gamePlayer.CreateTime = msg.getAccountInfo().getCreateTime();
           gamePlayer.set_sessionid(msg.getSessionid());
           gamePlayer.Privilege = msg.getAccountInfo().getPrivilege();
           gamePlayer.IconCustom = msg.getAccountInfo().getIconCustom();
           gamePlayer.RoomCard = (int)msg.getAccountInfo().getRoomCard();


           gamePlayer.IsRobot = msg.getAccountInfoEx().getIsRobot();
           if( !gamePlayer.IsRobot)
           {
               gamePlayer.world_id = peer.get_remote_id();
           }

            if( gameEngine.player_enter_game(gamePlayer,msg.getRoomid()) )
            {
                gamePlayer.reset_gate();
                gamePlayer.set_state(e_player_state.e_ps_playing);
                GamePlayerMgr.Instance.add_player(gamePlayer);
                builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success);

                logger.info("add the player to the game_player_mgr");

                //机器人需要另外通知
                gamePlayer.reset_robot_life();
            }
            else
            {
                builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_room_notopen);
            }
        }

        peer.send_msg(builder);

        return true;
    }
}
