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

    private GameGuanyunProtocol.msg_create_room_param create_room_param;
    private int mjType;                             //麻将的牌类类型
    private int roomId;                             //房间号
    private int ownUid;                             //创建此房间的玩家
    private int numOfGames;                         //当前的轮数，从0开始
    private int button;                             //当前当庄位置
    private int nextButton;                         //下一把当庄位置
    private Set<Integer> gpsCheckSet;               //检查gps的玩家的集合
    private Set<Integer> readyMap;                  //是否准备好了
    private GameSttus gameSttus;                    //当前桌子的状态

    private int startTime = 0;                     //游戏开始时间
    private long nextStartTime = 0;                 //下局游戏开始时间戳

    private LogicCore logicCore;


    public LogicTable(int roomId) {
        playerMap = new HashMap<>();
        seats = new ArrayList<>();
        gpsCheckSet = new HashSet<>();
        readyMap = new HashSet<>();
        this.roomId = roomId;
        gameSttus = GameSttus.STATUS_INIT;
        this.mjType = 1;
    }

    public void heartheat(double elapsed)
    {
        int cur_tm_s = TimeHelper.Instance.get_cur_time();

        if( gameSttus == GameSttus.STATUS_RUN )
        {
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
        //readyMap.add(logicPlayer.getSeatIndex());
        this.playerMap.put(logicPlayer.get_pid(),logicPlayer);
        this.seats.add(freePos,logicPlayer);
        seatFlag |= 1 << freePos;

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    public MsgTypeDef.e_msg_result_def removePlayer( LogicPlayer logicPlayer )
    {
        if( gameSttus != GameSttus.STATUS_INIT )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        if( readyMap.contains( logicPlayer.getSeatIndex() ) )
        {
            readyMap.remove(logicPlayer.getSeatIndex());
        }

        if( gpsCheckSet.contains(logicPlayer.getSeatIndex()) )
        {
            gpsCheckSet.remove(logicPlayer.getSeatIndex());
        }

        if( playerMap.containsKey(logicPlayer.get_pid()) )
        {
            playerMap.remove(logicPlayer.get_pid());
        }


        seatFlag = ~seatFlag;
        seatFlag |= (1 << logicPlayer.getSeatIndex());
        seatFlag = ~seatFlag;

        GameGuanyunProtocol.packetl2c_leave_room_result.Builder builder =  GameGuanyunProtocol.packetl2c_leave_room_result.newBuilder();
        builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success);
        builder.setSeatPos(logicPlayer.getSeatIndex());
        broadcast_msg_to_client(builder);

        logicPlayer.setRoomId(0);
        logicPlayer.setSeatIndex(-1);

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    public MsgTypeDef.e_msg_result_def ready(int seatPos,boolean ready)
    {
        if( gameSttus != GameSttus.STATUS_INIT )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail ;
        }

        if( ready )
        {
            if( readyMap.contains(seatPos) )
            {
                return MsgTypeDef.e_msg_result_def.e_rmt_fail;
            }
            readyMap.add(seatPos);
        }
        else
        {
            if( !readyMap.contains(seatPos) )
            {
                return MsgTypeDef.e_msg_result_def.e_rmt_fail;
            }
            readyMap.remove(seatPos);
        }

        //广播某个玩家的准备
        GameGuanyunProtocol.packetl2c_ready_result.Builder builder = GameGuanyunProtocol.packetl2c_ready_result.newBuilder();
        builder.setReady(ready);
        builder.setSeatPos(seatPos);
        broadcast_msg_to_client(builder);

        if( readyMap.size() == 4 )
        {
            gameSttus = GameSttus.STATUS_RUN;
            int cur_tm_s = TimeHelper.Instance.get_cur_time();
            startTime =  cur_tm_s + 11;     //10s后游戏开始

            GameGuanyunProtocol.packetl2c_game_start_nt.Builder startBuilder = GameGuanyunProtocol.packetl2c_game_start_nt.newBuilder();
            startBuilder.setStartTm(startTime);
            broadcast_msg_to_client(startBuilder);

            logicCore = new LogicCore(this);
            logicCore.init();
        }

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }


    public MsgTypeDef.e_msg_result_def skip(int seat_pos)
    {
        return logicCore.skip( seat_pos );
    }

    public MsgTypeDef.e_msg_result_def peng(int seat_pos)
    {
        return logicCore.peng(seat_pos);
    }

    public MsgTypeDef.e_msg_result_def outCard(int seatPos,int card,boolean isTing)
    {
        return logicCore.outCard( seatPos,card,isTing );
    }

    public MsgTypeDef.e_msg_result_def gang(int seat_pos,int card)
    {
        return logicCore.gang(seat_pos,card);
    }

    public MsgTypeDef.e_msg_result_def hu( int seatPos )
    {
        return logicCore.hu(seatPos);
    }

    public void get_scene_info_result(GameGuanyunProtocol.packetl2c_get_scene_info_result.Builder builder,int seatPos)
    {
        for(LogicPlayer logicPlayer : playerMap.values())
        {
            GameGuanyunProtocol.msg_user_info.Builder msg_user_info_builder = GameGuanyunProtocol.msg_user_info.newBuilder();
            msg_user_info_builder.setSex(0);
            msg_user_info_builder.setHeadIcon("");
            msg_user_info_builder.setUserName(logicPlayer.getGamePlayer().get_nickname());
            msg_user_info_builder.setSeatPos(logicPlayer.getSeatIndex());
            msg_user_info_builder.setPlayerId(logicPlayer.get_pid());
            msg_user_info_builder.setLineStatus(0);
            boolean readFlag = readyMap.contains(logicPlayer.getSeatIndex()) ;
            msg_user_info_builder.setReadyFlag(readFlag);
            builder.addUserInfos(msg_user_info_builder);
        }

        GameGuanyunProtocol.msg_base_room_rule_info.Builder msg_base_room_rule_info_builder = GameGuanyunProtocol.msg_base_room_rule_info.newBuilder();
        msg_base_room_rule_info_builder.setBaoType(create_room_param.getBaoType());
        msg_base_room_rule_info_builder.setCircleCount(create_room_param.getCircleCount());
        msg_base_room_rule_info_builder.setCardCost(10);
        msg_base_room_rule_info_builder.setBaseScore(create_room_param.getBaseScore());
        msg_base_room_rule_info_builder.setHuaType(create_room_param.getHuaType());
        msg_base_room_rule_info_builder.setBaoType(create_room_param.getBaoType());
        msg_base_room_rule_info_builder.setHuType(create_room_param.getHuaType());
        msg_base_room_rule_info_builder.setRoomId(getRoomId());
        msg_base_room_rule_info_builder.setCreatorId( ownUid );
        msg_base_room_rule_info_builder.setCreateTime(startTime);
        msg_base_room_rule_info_builder.setBaseHua(create_room_param.getBaseHua());
        builder.setBaseRoomRuleInfo(msg_base_room_rule_info_builder);


        //e_game_waiting					=	1;			//正在等待玩家的阶段
            //e_game_ready					=	2;			//玩家满了，正在准备阶段
            //e_game_gameing					=	3;			//游戏开始阶段
            //e_game_circle_over				=	4;			//一局结束时
            //e_game_game_over				=	5;			//游戏结束时

        if( gameSttus == GameSttus.STATUS_INIT )
        {
            //还未开始
            builder.setStatus(GameGuanyunProtocol.e_game_status_type.e_game_waiting);
        }
        else if( gameSttus == GameSttus.STATUS_RUN )
        {
            //正在玩中
            //builder.setStatus(GameGuanyunProtocol.e_game_status_type.e_game_gameing);
            //e_game_ready					=	2;			//玩家满了，正在准备阶段
            //e_game_gameing					=	3;			//游戏开始阶段
            //e_game_circle_over				=	4;			//一局结束时
            logicCore.set_scene_info_result(builder, seatPos );
        }
        else if( gameSttus == GameSttus.STATUS_END )
        {
            //结束
            builder.setStatus(GameGuanyunProtocol.e_game_status_type.e_game_game_over);
        }




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
            if( logicPlayer.getSeatIndex() == except_id )
            {
                continue;
            }
            pids.add(logicPlayer.get_pid());
        }

        return GameManager.Instance.broadcast_msg_to_client(pids,packetId,msg);
    }

    public int getFreePos()
    {
        for(int i = 0; i < 4;i ++)
        {
            int flag = 1 << i;

            if( (seatFlag & flag) != flag )
            {
                return i;
            }
        }

        return 0;
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


    public long getNextStartTime() {
        return nextStartTime;
    }

    public void setNextStartTime(long nextStartTime) {
        this.nextStartTime = nextStartTime;
    }

    public int getMjType() {
        return mjType;
    }

    public GameSttus getGameSttus() {
        return gameSttus;
    }

    public void setGameSttus(GameSttus gameSttus) {
        this.gameSttus = gameSttus;
    }

    public Map<Integer, LogicPlayer> getPlayerMap() {
        return playerMap;
    }

    public void setPlayerMap(Map<Integer, LogicPlayer> playerMap) {
        this.playerMap = playerMap;
    }

    public void setCreate_room_param(GameGuanyunProtocol.msg_create_room_param create_room_param)
    {
        this.create_room_param = create_room_param;
    }

    public GameGuanyunProtocol.msg_create_room_param getCreate_room_param()
    {
        return create_room_param;
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
