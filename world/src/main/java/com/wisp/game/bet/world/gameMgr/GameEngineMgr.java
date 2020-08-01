package com.wisp.game.bet.world.gameMgr;

import com.wisp.game.bet.world.db.DbPlayer;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class GameEngineMgr  {
    public static GameEngineMgr Instance;

    public GameEngineMgr() {
        Instance = this;
    }

    public void remove_game_info(int gameId,int serverId)
    {
        Update update = new Update();

    }

    public void update_game_info(int gameId,int serverId )
    {
        this.update_game_info(gameId,serverId,true);
    }


    public void update_game_info(int gameId,int serverId,boolean add_player )
    {

    }
}
