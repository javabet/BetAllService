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
    private int seatIndex;      //0,1,2,3   //玩家的位置
    private int roomId;
    private double lng;         ///经度
    private  double lat;        //纬度
    private String address;     //地址
    private  int gpsStatus;     //gps是否开启 0:未开启 1：已开启

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

        return guanYunPlayerDoc != null;
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


    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getGpsStatus() {
        return gpsStatus;
    }

    public void setGpsStatus(int gpsStatus) {
        this.gpsStatus = gpsStatus;
    }
}
