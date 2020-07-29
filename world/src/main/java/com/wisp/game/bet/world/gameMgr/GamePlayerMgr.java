package com.wisp.game.bet.world.gameMgr;

import com.wisp.game.bet.db.mongo.player.info.CommonConfigDoc;
import com.wisp.game.bet.db.mongo.player.info.OrderPlayerIdDoc;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.db.DbPlayer;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class GamePlayerMgr {
    public static GamePlayerMgr Instance;

    public GamePlayerMgr() {
        Instance = this;
    }


    public int get_player_count() {
        return 0;
    }

    public void leave_game(int gameServerId) {

    }

    public GamePlayer find_player(int sessionId)
    {
        return null;
    }

    public GamePlayer find_player(String account)
    {
        return null;
    }

    public void remove_session(int sessionId)
    {

    }

    public void reset_player(GamePlayer gamePlayer,int sessionId)
    {

    }

    public int generic_playerid()
    {
        Query query = new Query(Criteria.where("type").is("cur_index"));
        Update update = new Update();
        update.inc("value",1);
        CommonConfigDoc commonConfigDoc =  DbPlayer.Instance.getMongoTemplate().findAndModify(query,update, CommonConfigDoc.class);
        if( commonConfigDoc == null )
        {
            return 0;
        }

        OrderPlayerIdDoc orderlayerIdDoc =  DbPlayer.Instance.getMongoTemplate().findOne(new Query(Criteria.where("Index").is(commonConfigDoc.getValue())), OrderPlayerIdDoc.class);
        if( orderlayerIdDoc == null )
        {
            return 0;
        }

        return orderlayerIdDoc.getPid();
    }

}
