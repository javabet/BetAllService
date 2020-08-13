package com.wisp.game.bet.games.share;

import com.wisp.game.bet.games.share.config.RMConfig;

public interface ICheckRoomCfgFun {

    public boolean check_roomcfg_fun(int agentId, int gameId, int roomId, RMConfig t, int templateId);
}
