package com.wisp.game.bet.games.guanyuan.logic;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import game_guanyuan_protocols.GameGuanyunProtocol;
import msg_type_def.MsgTypeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LogicTable {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<Integer,LogicPlayer> playerMap;
    private List<LogicPlayer> seats;
    private int seatFlag = 0;               //坐位标识,如果有人，则为1，没人则为0


    private int roomId;                             //房间号
    private int ownUid;                             //创建此房间的玩家
    private int numOfGames;                         //当前的轮数，从0开始
    private int button;                             //当前当庄位置
    private int nextButton;                         //下一把当庄位置
    private Set<Integer> gpsCheckSet;               //检查gps的玩家的集合
    private GameSttus gameSttus;                    //当前桌子的状态

    private long startTime = 0;                     //游戏开始时间
    private long nextStartTime = 0;                 //下局游戏开始时间戳

    private LogicCore logicCore;


    public LogicTable(int roomId) {
        playerMap = new HashMap<>();
        seats = new ArrayList<>();
        gpsCheckSet = new HashSet<>();
        this.roomId = roomId;
        gameSttus = GameSttus.STATUS_INIT;
    }

    public void heartheat(double elapsed)
    {
        int cur_tm_s = TimeHelper.Instance.get_cur_time();
        if(  startTime > 0 && cur_tm_s >= startTime )
        {
            //如果时间到了，则游戏开始
            startTime = 0;

            for( LogicPlayer logicPlayer : seats )
            {
                if( !gpsCheckSet.contains(logicPlayer.get_pid()) )
                {
                    gpsCheckSet.add(logicPlayer.get_pid());
                }
            }
            logicCore.start();
        }
    }

    public void set_room_cfg(ByteString room_cfg)
    {
        GameGuanyunProtocol.msg_yuan_room_rule msg_yuan_room_rule;
        if(room_cfg != null )
        {
            try
            {
                msg_yuan_room_rule = GameGuanyunProtocol.msg_yuan_room_rule.parseFrom(room_cfg);
            }
            catch (Exception exception)
            {
                logger.error("player_enter_game parse room_cfg_has_error");
            }
        }
    }

    public void start()
    {
        logicCore = new LogicCore(this);
    }

    public boolean can_add_player(GamePlayer gamePlayer)
    {
        if( playerMap.containsKey(gamePlayer.get_playerid()) )
        {
            return false;
        }

        if( playerMap.size() >= 4 )
        {
            return false;
        }

        if( gameSttus != GameSttus.STATUS_INIT )
        {
            return false;
        }
        return true;
    }

    public MsgTypeDef.e_msg_result_def add_player(LogicPlayer logicPlayer)
    {
        int freePos = getFreePos();
        logicPlayer.setRoomId( this.roomId );
        logicPlayer.setSeatIndex(freePos);
        logicPlayer.setReady(true);
        this.playerMap.put(logicPlayer.get_pid(),logicPlayer);
        this.seats.add(freePos,logicPlayer);
        seatFlag |= 1 << freePos;

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    public int send_msg_to_client(Message.Builder builder,int seatIdx)
    {
        LogicPlayer logicPlayer = seats.get(seatIdx);
        return logicPlayer.send_msg_to_client(builder);
    }

    public  int broadcast_msg_to_client(Message.Builder builder)
    {
        return broadcast_msg_to_client( builder,0 );
    }

    public  int broadcast_msg_to_client(Message.Builder builder,int except_id)
    {
        Message msg = builder.build();
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());

        return broadcast_msg_to_client( packetId,msg,except_id );
    }

    private  int broadcast_msg_to_client(int packetId, Message msg,int except_id)
    {
        if( seats.size() <= 0 )
        {
            return -1;
        }

        List<Integer> pids = new ArrayList<>();
        for(LogicPlayer logicPlayer : seats)
        {
            pids.add(logicPlayer.get_pid());
        }

        return GameManager.Instance.broadcast_msg_to_client(pids,packetId,msg);
    }

    public int getFreePos()
    {
        for(int i = 0; i < 4;i ++)
        {
            int flag = 1 << i;

            if( (seatFlag & flag) == flag )
            {
                return i;
            }
        }

        return 0;
    }


    public Map<Integer, LogicPlayer> getPlayerMap() {
        return playerMap;
    }

    public void setPlayerMap(Map<Integer, LogicPlayer> playerMap) {
        this.playerMap = playerMap;
    }

    public List<LogicPlayer> getSeats() {
        return seats;
    }

    public void setSeats(List<LogicPlayer> seats) {
        this.seats = seats;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getOwnUid() {
        return ownUid;
    }

    public void setOwnUid(int ownUid) {
        this.ownUid = ownUid;
    }

    public int getNumOfGames() {
        return numOfGames;
    }

    public void setNumOfGames(int numOfGames) {
        this.numOfGames = numOfGames;
    }

    public int getButton() {
        return button;
    }

    public void setButton(int button) {
        this.button = button;
    }

    public int getNextButton() {
        return nextButton;
    }

    public void setNextButton(int nextButton) {
        this.nextButton = nextButton;
    }

    public Set<Integer> getGpsCheckSet() {
        return gpsCheckSet;
    }

    public void setGpsCheckSet(Set<Integer> gpsCheckSet) {
        this.gpsCheckSet = gpsCheckSet;
    }

    public LogicCore getLogicCore() {
        return logicCore;
    }

    public void setLogicCore(LogicCore logicCore) {
        this.logicCore = logicCore;
    }

    public GameSttus getGameSttus() {
        return gameSttus;
    }

    public void setGameSttus(GameSttus gameSttus) {
        this.gameSttus = gameSttus;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getNextStartTime() {
        return nextStartTime;
    }

    public void setNextStartTime(long nextStartTime) {
        this.nextStartTime = nextStartTime;
    }

    public enum GameSttus
    {
        STATUS_INIT(1),
        STATUS_RUN(2),
        STATUS_END(3),
        ;

        private GameSttus(int value) {
            this.value = value;
        }

        private int value;
        public final int getNumber() { return value; }
    }
}
