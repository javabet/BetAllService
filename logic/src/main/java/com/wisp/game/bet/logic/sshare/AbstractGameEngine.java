package com.wisp.game.bet.logic.sshare;

import com.google.protobuf.ByteString;
import com.wisp.game.bet.logic.gameObj.GamePlayer;

public abstract class AbstractGameEngine implements IGameEngine {

    //玩家进入游戏
    public boolean player_enter_game(GamePlayer gamePlayer, int roomId)
    {
        return false;
    }
    //玩家进入游戏
    public boolean player_enter_game(GamePlayer gamePlayer, int roomNum, int room_cfg_type, ByteString roomCardCfgByteString)
    {
        return false;
    }
}
