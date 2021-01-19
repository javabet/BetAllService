package com.wisp.game.bet.games.guanyuan.logic;

import com.google.protobuf.Message;
import com.wisp.game.bet.games.guanyuan.doc.GuanYunPlayerDoc;
import com.wisp.game.bet.logic.db.DbGame;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.IGamePHandler;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class LogicPlayer  implements IGamePHandler {

    private GamePlayer gamePlayer;


    private boolean ready = false;          //是否准备好了
    private int seatIndex;      //0,1,2,3   //玩家的位置
    private int roomId;

    public int send_msg_to_client(Message.Builder builder)
    {
        return gamePlayer.send_msg_to_client(builder);
    }


    public void enter_game(  )
    {
        if( !load_player() )
        {
            create_player();
        }
    }

    private boolean load_player()
    {
        GuanYunPlayerDoc guanYunPlayerDoc =  DbGame.Instance.getMongoTemplate().findOne(Query.query(Criteria.where("PlayerId").is(gamePlayer.get_playerid())), GuanYunPlayerDoc.class);
        if( guanYunPlayerDoc != null )
        {
            return true;
        }
        return false;
    }

    private boolean create_player()
    {
        GuanYunPlayerDoc guanYunPlayerDoc = new GuanYunPlayerDoc();
        guanYunPlayerDoc.setPlayerId(gamePlayer.get_playerid());
        DbGame.Instance.getMongoTemplate().insert(guanYunPlayerDoc);
        return true;
    }

    @Override
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    @Override
    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }



    public int get_pid()
    {
        return gamePlayer.get_playerid();
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(int seatIndex) {
        this.seatIndex = seatIndex;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
