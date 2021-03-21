package com.wisp.game.bet.games.guanyuan.logic;

import com.wisp.game.bet.db.mongo.logs.doc.MjRoomLogDoc;
import com.wisp.game.bet.db.mongo.logs.doc.MjRoundLogDoc;
import com.wisp.game.bet.games.guanyuan.logic.info.HuCircleInfo;
import com.wisp.game.bet.games.guanyuan.logic.info.HuCircleItemInfo;
import com.wisp.game.bet.games.guanyuan.logic.info.PlayerOperationInfo;
import com.wisp.game.bet.games.share.HuStrategy.HistoryActionInfo;
import com.wisp.game.bet.games.share.enums.CardTypeEnum;
import com.wisp.game.bet.games.share.common.HuPattern;
import com.wisp.game.bet.games.share.common.MaJiangPlayerData;
import com.wisp.game.bet.games.share.enums.HistoryActionEnum;
import com.wisp.game.bet.games.share.utils.MaJianUtils;
import com.wisp.game.bet.games.share.utils.MajiangCards;
import com.wisp.game.bet.logic.db.DbLog;
import com.wisp.game.bet.logic.sshare.IGamePlayer;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.core.random.RandomHandler;
import com.wisp.game.core.utils.CommonUtils;
import game_guanyuan_protocols.GameGuanyunProtocol;
import msg_type_def.MsgTypeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.HTML;
import java.sql.Time;
import java.util.*;

/**
 * <!--
 * 		//0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09, //万1 - 9
 * 		//0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19, //条 1- 9
 * 		//0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29, //饼1 - 9
 * 		//0x31,0x32,0x33,0x34,0x35,0x36,0x37 //风 东南西北  中发白
 * 		//0x41,0x42,0x43,0x44,0x45,0x46,0x47,0x48,//8张花牌 春夏秋冬 梅兰猪菊
 * 	 -->
 */
public class LogicCore {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<Integer> mahjongs;         //当前的牌的全部

    private int currentIndex = 0;       //现在麻将的位置
    private int lastTurn;               //前面的玩家的位置
    private int turn;                   //现在轮到谁的位置了
    private int lastCard;               //前面一激活的牌值
    private int chupai;                 //正在出牌的值
    protected int button;

    private List<MaJiangPlayerData> gameSeats;          //玩家列表
    private LogicTable logicTable;                      //桌子
    private QiangGangContext qiangGangContext;          //抢杠上下方
    private List<PlayerOperationInfo> operationInfos;       //需要进行的操作列表，用在抢杠胡时，

    private  List<HuCircleInfo> huCircleInfos;              //当前胡的信息
    private List<HistoryActionInfo> historyActionInfos;     //出牌历史
    private List<Integer> diceList;
    private GameState gameState;

    public LogicCore( LogicTable logicTable) {
        this.logicTable = logicTable;
        this.gameSeats = new ArrayList<>();
        historyActionInfos = new ArrayList<>();
        operationInfos = new ArrayList<>();
        huCircleInfos = new ArrayList<>();
    }

    public void init()
    {
        gameState = GameState.READING;
    }

    public void start()
    {
        gameState = GameState.RUN;

        logicTable.setButton(logicTable.getNextButton());
        this.button = logicTable.getButton();

        this.turn = 0;
        this.chupai = -1;

        this.logicTable.setNumOfGames(logicTable.getNumOfGames() + 1);

        for(int i = 0; i < 4;i ++)
        {
            MaJiangPlayerData maJiangPlayerData = new MaJiangPlayerData();
            maJiangPlayerData.setSeatIndex(i);
            this.gameSeats.add(maJiangPlayerData);
        }

        mahjongs = MajiangCards.getCards(logicTable.getMjType());

        //洗牌
        for (int i = 0; i < mahjongs.size(); i++)
        {
            int lastIndex = mahjongs.size() - 1 - i;
            int index = (int) Math.floor(Math.random() * lastIndex);
            int t = mahjongs.get(index);
            mahjongs.set(index, mahjongs.get(lastIndex));
            mahjongs.set(lastIndex, t);
        }

        testInitCards(  );

        logicTable.setGameSttus(LogicTable.GameSttus.STATUS_RUN);
        //发牌
        deal();

        //记录玩家的手牌
        for(MaJiangPlayerData maJiangPlayerData : gameSeats)
        {
            maJiangPlayerData.setInitHolds( CommonUtils.deepSimpleList(maJiangPlayerData.getHolds()) );
            maJiangPlayerData.setInitFlowerCards( CommonUtils.deepSimpleList(maJiangPlayerData.getInitFlowerCards()) );
        }

        diceList = new ArrayList<>(2);
        for(int i = 0; i < 2;i++)
        {
            int diceNum = RandomHandler.Instance.getRandomValue(1,6);
            diceList.add(diceNum);
        }
        for(MaJiangPlayerData maJiangPlayerData : gameSeats)
        {
            GameGuanyunProtocol.packetl2c_circle_start_nt.Builder circleStartNt =  GameGuanyunProtocol.packetl2c_circle_start_nt.newBuilder();
            circleStartNt.setGamingInfo( getGamingSceneInfo(maJiangPlayerData.getSeatIndex()) );
            logicTable.send_msg_to_client(circleStartNt,maJiangPlayerData.getSeatIndex());

            maJiangPlayerData.calcCardMask();
        }


        //进行听牌检查
        for( MaJiangPlayerData maJiangPlayerData : gameSeats )
        {
            if( maJiangPlayerData.getHolds().size() == 13 )
            {
                checkCanTingPai(maJiangPlayerData);
            }
            else
            {
                int lastCard = maJiangPlayerData.getHolds().remove(maJiangPlayerData.getHolds().size() - 1);
                maJiangPlayerData.getCountMap().put(lastCard,maJiangPlayerData.getCountMap().get(lastCard) - 1);

                checkCanTingPai(maJiangPlayerData);

                maJiangPlayerData.getHolds().add(lastCard);
                maJiangPlayerData.getCountMap().put(lastCard,maJiangPlayerData.getCountMap().get(lastCard) + 1);
            }
        }

        //通知玩家出牌
        MaJiangPlayerData turnSeat = gameSeats.get(turn);
        turnSeat.setCanChuPai(true);
        //setTurnIndexMsg(turn);

        checkCanJiaoTing(turnSeat);//检查叫听
        checkCanAnGang(turnSeat);//检查直杠
        checkCanHu(turnSeat,turnSeat.getHolds().get(turnSeat.getHolds().size() - 1));//检查是否能胡
        sendOperations(turnSeat,chupai);
    }



    private void deal()
    {
        this.currentIndex = 0;
        int seatIndex = turn;
        // 每人13张 一共 13*4 ＝ 52张 庄家多一张 53张
        for(int i = 0; i < 13 * 4; i ++)
        {
            List<Integer> list = mopaiWithHua(seatIndex);
            if( list.size() > 1 )
            {
                MaJiangPlayerData playerData = gameSeats.get(seatIndex);
                for(int j = 0; j < list.size() - 1;j++)     //最后一张牌不需要加入
                {
                    playerData.getFlowers().add(list.get(j));
                }
            }

            seatIndex ++;
            seatIndex %= 4;
        }

        // 庄家多摸最后一张
        // mopai(button);
        List<Integer> list = mopaiWithHua(button);
        if (list.size() > 1)
        {
            MaJiangPlayerData playerData = gameSeats.get(button);
            for (int m = 0; m < list.size() - 1; m++)
            {
                playerData.getFlowers().add(list.get(m));
            }
        }

        // 当前轮设置为庄家
        this.turn = button;
    }

    public void sendOperations( MaJiangPlayerData maJiangPlayerData )
    {
        sendOperations( maJiangPlayerData,-1 );
    }
    public void sendOperations( MaJiangPlayerData maJiangPlayerData,int card )
    {
        boolean flag = hasOperations(maJiangPlayerData);
        if( !flag )
        {
            return;
        }

        if( card == -1 )
        {
            card = maJiangPlayerData.getHolds().get(maJiangPlayerData.getHolds().size() - 1);
        }

        GameGuanyunProtocol.packetl2c_player_action_nt.Builder builder = GameGuanyunProtocol.packetl2c_player_action_nt.newBuilder();
        List<GameGuanyunProtocol.msg_action_type> msg_action_type_builder_list = getMsgActionTypeList(maJiangPlayerData);
        if( msg_action_type_builder_list.size() > 0 )
        {
            builder.addAllActionTypes(msg_action_type_builder_list);
        }

        if( builder.getActionTypesList().size() > 0 )
        {
            logicTable.send_msg_to_client(builder,maJiangPlayerData.getSeatIndex());
        }
    }

    private List<GameGuanyunProtocol.msg_action_type> getMsgActionTypeList(MaJiangPlayerData maJiangPlayerData)
    {
        List<GameGuanyunProtocol.msg_action_type> actionList = new ArrayList<>();
        if( maJiangPlayerData.isCanHu() )
        {
            GameGuanyunProtocol.msg_action_type.Builder msg_action_builder = GameGuanyunProtocol.msg_action_type.newBuilder();
            msg_action_builder.setActionType(GameGuanyunProtocol.e_player_ation_type.e_player_ation_hu);
            actionList.add(msg_action_builder.build());
        }

        if( maJiangPlayerData.isCanPeng() )
        {
            GameGuanyunProtocol.msg_action_type.Builder msg_action_builder = GameGuanyunProtocol.msg_action_type.newBuilder();
            msg_action_builder.setActionType(GameGuanyunProtocol.e_player_ation_type.e_player_ation_peng);
            actionList.add(msg_action_builder.build());
        }

        if( maJiangPlayerData.isCanAnGang() )
        {
            GameGuanyunProtocol.msg_action_type.Builder msg_action_builder = GameGuanyunProtocol.msg_action_type.newBuilder();
            msg_action_builder.setActionType(GameGuanyunProtocol.e_player_ation_type.e_player_ation_gang_an);
            msg_action_builder.addAllCards( maJiangPlayerData.getAngGangList() );
            actionList.add(msg_action_builder.build());
        }

        if( maJiangPlayerData.isCanDianGang() )
        {
            GameGuanyunProtocol.msg_action_type.Builder msg_action_builder = GameGuanyunProtocol.msg_action_type.newBuilder();
            msg_action_builder.setActionType(GameGuanyunProtocol.e_player_ation_type.e_player_ation_gang_ming);
            actionList.add(msg_action_builder.build());
        }

        if( maJiangPlayerData.isCanWanGang() )
        {
            GameGuanyunProtocol.msg_action_type.Builder msg_action_builder = GameGuanyunProtocol.msg_action_type.newBuilder();
            msg_action_builder.setActionType(GameGuanyunProtocol.e_player_ation_type.e_player_ation_gang_wang);
            msg_action_builder.addAllCards( maJiangPlayerData.getWangGangList() );
            actionList.add(msg_action_builder.build());
        }

        if( maJiangPlayerData.isCanJiaoTing() )
        {
            GameGuanyunProtocol.msg_action_type.Builder msg_action_builder = GameGuanyunProtocol.msg_action_type.newBuilder();
            msg_action_builder.setActionType(GameGuanyunProtocol.e_player_ation_type.e_player_ation_ting);
            actionList.add(msg_action_builder.build());
        }

        return actionList;
    }

    public MsgTypeDef.e_msg_result_def ready(int seatPos,boolean ready)
    {

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }





    public MsgTypeDef.e_msg_result_def skip(int seat_pos)
    {
        if( seat_pos < 0 || seat_pos >= 4 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        MaJiangPlayerData maJiangPlayerData = gameSeats.get(seat_pos);

       boolean canSkip = maJiangPlayerData.isCanAnGang()  || maJiangPlayerData.isCanJiaoTing() || maJiangPlayerData.isCanWanGang()
               || maJiangPlayerData.isCanDianGang() || maJiangPlayerData.isCanPeng() || maJiangPlayerData.isCanHu();

       if( !canSkip )
       {
           return  MsgTypeDef.e_msg_result_def.e_rmt_fail;
       }

//       GameGuanyunProtocol.packetl2c_action_result.Builder actionBuilder = GameGuanyunProtocol.packetl2c_action_result.newBuilder();
//       actionBuilder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success);
//       actionBuilder.setActionType(GameGuanyunProtocol.e_action_type.e_action_skip);
//       actionBuilder.setCard(chupai);
//       actionBuilder.setLeftCardNum(mahjongs.size() - currentIndex);
//       logicTable.send_msg_to_client(actionBuilder,seat_pos);

       //seatData.getLuoList().add(chupai);

        int cur_card = chupai;
        chupai = -1;

        saveHistory(seat_pos,HistoryActionEnum.HISTORY_ACTION_SKIP,-1);

        QiangGangContext tmpQiangGangContext = this.qiangGangContext;
        clearAllOption();

        if( tmpQiangGangContext != null && tmpQiangGangContext.getHuCnt() == 0 )
        {
            //可以抢杠时，选择过，则前面的玩家继续
            gang(qiangGangContext.getSeatPos(),qiangGangContext.getCard());
        }
        else if( maJiangPlayerData.getSeatIndex() == turn )
        {
            //自摸胡与自摸杠选择过的时候，需要玩家手动放弃
            //donothing
        }
        else
        {
            moveToNextUser();
            doUserMoPai();
        }


        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }


    public MsgTypeDef.e_msg_result_def outCard(int seatPos,int card,boolean isTing)
    {
        if( seatPos >= 4 || seatPos < 0 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        if( turn != seatPos )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        MaJiangPlayerData maJiangPlayerData = gameSeats.get(turn);

        if( !maJiangPlayerData.isCanChuPai() )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        int idx = maJiangPlayerData.getHolds().indexOf( card );
        if(  idx == -1 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        //判断是不是花，如果是花，则不能出牌
        CardTypeEnum cardTypeEnum = MajiangCards.getCardType(logicTable.getMjType(),card);
        if( cardTypeEnum == CardTypeEnum.CARD_TYPE_HUA )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        if( hasOperations(maJiangPlayerData) )
        {
            maJiangPlayerData.clearAllOptions();
            maJiangPlayerData.getLuoList().add(card);
            // 如果是在有操作时,直接选择了过,则要检查是否还有别的玩家可以胡的操作
            // 如果还有人可以操作，则等待
            if( operationInfos.size() > 0 )
            {
                 PlayerOperationInfo playerOperationInfo =  operationInfos.remove(0);
                 MaJiangPlayerData tmpPlayer = gameSeats.get(playerOperationInfo.getSeatPos());
                 tmpPlayer.setActionOperation(playerOperationInfo);
                 sendOperations( tmpPlayer,playerOperationInfo.getCard() );
                return MsgTypeDef.e_msg_result_def.e_rmt_success ;
            }
        }


        maJiangPlayerData.setCanChuPai(true);
        maJiangPlayerData.removeCard(card);
        saveHistory(seatPos,HistoryActionEnum.HISTORY_ACTION_CHUPAI,card);
        checkCanTingPai( maJiangPlayerData);

        //向所有人广播某个人出牌
        GameGuanyunProtocol.packetl2c_action_nt.Builder builder = GameGuanyunProtocol.packetl2c_action_nt.newBuilder();
        builder.setActionType(GameGuanyunProtocol.e_action_type.e_action_out_card);
        builder.setCard(card);
        builder.setSeatPos(seatPos);
        logicTable.broadcast_msg_to_client(builder);

        chupai = card;
        //出牌后的检查工作，检查，碰，杠，胡等操作
        int start_seatIndex = (turn + 1) % 4; // 从下一个玩家开始循环检查
        for(int i = 0; i < 4; i ++)
        {
            int seatIdx = (start_seatIndex + i) % 4;
            if( turn == seatIdx )
            {
                continue;
            }

            MaJiangPlayerData tmpPlayer = gameSeats.get(seatIdx);
            if( !tmpPlayer.isHued() )
            {
                checkCanPeng(tmpPlayer,card);
                checkCanDianGang(tmpPlayer,card);
            }
            checkCanHu(tmpPlayer,card);

            if( hasOperations(tmpPlayer) )
            {
                PlayerOperationInfo playerOperationInfo = new PlayerOperationInfo();
                playerOperationInfo.setCard(card);
                playerOperationInfo.setSeatPos(seatIdx);
                playerOperationInfo.setLinkPos(seatPos);
                if( tmpPlayer.isCanHu() )
                {
                    playerOperationInfo.addAction(HistoryActionEnum.HISTORY_ACTION_HU);
                }
                if( tmpPlayer.isCanPeng() )
                {
                    playerOperationInfo.addAction(HistoryActionEnum.HISTORY_ACTION_PENG);
                }
                if( tmpPlayer.isCanDianGang() )
                {
                    playerOperationInfo.addAction(HistoryActionEnum.HISTORY_ACTION_DIAN_GANG);
                }
                if(tmpPlayer.isCanWanGang())
                {
                    playerOperationInfo.addAction(HistoryActionEnum.HISTORY_ACTION_WANG_GANG);
                }
                if(tmpPlayer.isCanAnGang())
                {
                    playerOperationInfo.addAction(HistoryActionEnum.HISTORY_ACTION_AN_GANG);
                }
                operationInfos.add(playerOperationInfo);
            }
        }

        if( operationInfos.size() > 0 )
        {
            clearAllOption();
            PlayerOperationInfo playerOperationInfo = operationInfos.remove(0);
            MaJiangPlayerData tmpPlayer = gameSeats.get(playerOperationInfo.getSeatPos());
            tmpPlayer.setActionOperation(playerOperationInfo);
            sendOperations( tmpPlayer,playerOperationInfo.getCard() );
            //出完牌，其他玩家可以有额外的操作
            return MsgTypeDef.e_msg_result_def.e_rmt_success;
        }

        moveToNextUser();

        chupai = -1;
        doUserMoPai();

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    public MsgTypeDef.e_msg_result_def peng(int seatPos)
    {
        if( seatPos >= 4  || seatPos < 0 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        //自己出的牌，不能自己碰
        if( turn == seatPos )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        MaJiangPlayerData maJiangPlayerData = gameSeats.get(seatPos);

        return dopeng(maJiangPlayerData,chupai);
    }

    private MsgTypeDef.e_msg_result_def dopeng(MaJiangPlayerData maJiangPlayerData,int card)
    {
        if( !maJiangPlayerData.isCanPeng() )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        if( maJiangPlayerData.isHued() )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        if( !maJiangPlayerData.getCountMap().containsKey(chupai) )
        {
            return  MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        int count = maJiangPlayerData.getCountMap().get(chupai);
        if( count < 2 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        //maJiangPlayerData.clearAllOptions();
        //maJiangPlayerData.setCanChuPai(false);

        clearAllOption();

        //删除两张卡
        maJiangPlayerData.removeCard(card);
        maJiangPlayerData.removeCard(card);

        MaJiangPlayerData turnPlayer = gameSeats.get(turn);
        turnPlayer.removeLastOutCard();

        saveHistory(maJiangPlayerData.getSeatIndex(),HistoryActionEnum.HISTORY_ACTION_PENG,card);
        setTurnIndexMsg(maJiangPlayerData.getSeatIndex());

        //广播碰牌操作
        GameGuanyunProtocol.packetl2c_action_nt.Builder actionBuilder = GameGuanyunProtocol.packetl2c_action_nt.newBuilder();
        actionBuilder.setSeatPos(maJiangPlayerData.getSeatIndex());
        actionBuilder.setActionType(GameGuanyunProtocol.e_action_type.e_action_peng);
        actionBuilder.setCard(chupai);
        actionBuilder.setLinkPos(turn);
        logicTable.broadcast_msg_to_client(actionBuilder);

        turn = maJiangPlayerData.getSeatIndex();
        maJiangPlayerData.setCanChuPai(true);

        checkCanJiaoTing(maJiangPlayerData);
        checkCanAnGang(maJiangPlayerData);
        sendOperations(maJiangPlayerData);

        maJiangPlayerData.calcCardMask();

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    public MsgTypeDef.e_msg_result_def gang(int seat_pos,List<Integer> cards)
    {
        if( seat_pos >= 4  || seat_pos < 0 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        MaJiangPlayerData maJiangPlayerData = gameSeats.get(seat_pos);

        if( maJiangPlayerData.isHued() )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        int checkGangCard = 0;
        if( turn != seat_pos )
        {
            checkGangCard = chupai;
        }
        else
        {
            if( cards == null || cards.size() < 0 )
            {
                return MsgTypeDef.e_msg_result_def.e_rmt_fail;
            }
            checkGangCard = cards.get(0);
        }

        return gang( seat_pos,checkGangCard );
    }

    //杠
    public MsgTypeDef.e_msg_result_def gang(int seatPos,int card)
    {
        if( seatPos >= 4  || seatPos < 0 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        MaJiangPlayerData maJiangPlayerData = gameSeats.get(seatPos);

        if( maJiangPlayerData.isHued() )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        boolean canWangGang = false;
        if (turn == maJiangPlayerData.getSeatIndex())
        {
            canWangGang = onlyCheckCanWanGang(maJiangPlayerData, card);
        }

        if (!canWangGang && !maJiangPlayerData.isCanDianGang() && !maJiangPlayerData.isCanAnGang())
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        int cardNum = maJiangPlayerData.getCountMap().get(card);
        HistoryActionEnum historyActionEnum;
        if( cardNum == 4 )
        {
            //暗杠
            historyActionEnum = HistoryActionEnum.HISTORY_ACTION_AN_GANG;
        }
        else if( cardNum == 3 )
        {
            //明杠
            historyActionEnum = HistoryActionEnum.HISTORY_ACTION_HU_DIAN_GANG;
        }
        else if( canWangGang )
        {
            historyActionEnum = HistoryActionEnum.HISTORY_ACTION_WANG_GANG;
            //弯杠
            clearAllOption();
            operationInfos.clear();
            //检查是否可以抢杠
            for(int i = 0; i < 4;i ++)
            {
                if( i == seatPos )
                {
                    continue;
                }
                MaJiangPlayerData tmpPlayer = gameSeats.get(i);
                checkCanHu(tmpPlayer,card);
                if( tmpPlayer.isCanHu() )
                {
                    //抢杠成功
                    PlayerOperationInfo playerOperationInfo = new PlayerOperationInfo();
                    playerOperationInfo.addAction(HistoryActionEnum.HISTORY_ACTION_HU);
                    playerOperationInfo.setCard( card );
                    playerOperationInfo.setSeatPos(i);
                    operationInfos.add(playerOperationInfo);
                }
            }
            clearAllOption();

            if( operationInfos.size() > 0 )
            {
                PlayerOperationInfo playerOperationInfo = operationInfos.remove(0);           //去掉第一个，以用作抢
                qiangGangContext = new QiangGangContext();
                qiangGangContext.setCard(card);
                qiangGangContext.setSeatPos( seatPos );

                MaJiangPlayerData tmpPlayer = gameSeats.get(playerOperationInfo.getSeatPos());
                tmpPlayer.setCanHu(true);           //设置玩家可胡
                sendOperations(tmpPlayer,card);
                return MsgTypeDef.e_msg_result_def.e_rmt_success;
            }
        }
        else
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }


        chupai = -1;
        clearAllOption();
        maJiangPlayerData.setCanChuPai(false);
        doGang(turn,seatPos,card,historyActionEnum);

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    /**
     *
     * @param gangedPos 被杠的位置
     * @param gangPos   杠的位置
     * @param card
     * @param historyActionEnum
     */
    private void doGang(int gangedPos,int gangPos ,int card,HistoryActionEnum historyActionEnum)
    {
        //保存记录
        HistoryActionInfo historyActionInfo = new HistoryActionInfo();
        historyActionInfo.setAction(historyActionEnum);
        historyActionInfo.setCard(card);
        historyActionInfo.setSeatPos(gangPos);
        historyActionInfo.setLinkedSeatPos(gangedPos);
        saveHistory(historyActionInfo);

        MaJiangPlayerData gangPlayer = gameSeats.get(gangPos);
        MaJiangPlayerData gangedPlayer = gameSeats.get(gangedPos);

        if( historyActionEnum == HistoryActionEnum.HISTORY_ACTION_AN_GANG )
        {
            //暗杠扣玩家四张牌，其它扣一张牌
            gangPlayer.removeCard(card);
            gangPlayer.removeCard(card);
            gangPlayer.removeCard(card);
            gangPlayer.removeCard(card);
        }
        else
        {
            gangPlayer.removeCard(card);
        }

        //如果是点杠，则需要从对方的牌河走移走当前牌
        if( historyActionEnum == HistoryActionEnum.HISTORY_ACTION_DIAN_GANG )
        {
            gangedPlayer.removeLastOutCard();
        }

        checkCanTingPai( gangPlayer );

        //广播给所有玩家杠的操作
        GameGuanyunProtocol.packetl2c_action_nt.Builder builder = GameGuanyunProtocol.packetl2c_action_nt.newBuilder();
        builder.setCard(card);
        builder.setSeatPos(gangPos);
        builder.setActionType(GameGuanyunProtocol.e_action_type.e_action_gang);
        builder.setLinkPos( gangedPos );
        logicTable.broadcast_msg_to_client(builder);

        turn = gangPos;
        doUserMoPai();

        if(gangPlayer.isCanJiaoTing())
        {
            sendOperations(gangPlayer);
        }

        gangPlayer.calcCardMask();
    }

    public MsgTypeDef.e_msg_result_def hu(int seatPos)
    {
        MaJiangPlayerData maJiangPlayerData = gameSeats.get(seatPos);
        if( !maJiangPlayerData.isCanHu() )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        maJiangPlayerData.setHued(true);

        MaJiangPlayerData turnPlayer = gameSeats.get(turn);

        int huCard = chupai;  //被胡的那牌值
        int huedSeatPos = -1;           //被胡方，如果是自摸的话，此值为-1
        HistoryActionEnum historyActionEnum = HistoryActionEnum.HISTORY_ACTION_NULL;            //当前的胡的类型
        HistoryActionInfo historyActionInfo = new HistoryActionInfo();
        if( qiangGangContext != null )
        {
            //抢杠糊
            qiangGangContext.setHuCnt(qiangGangContext.getHuCnt() + 1);
            huedSeatPos = qiangGangContext.getSeatPos();
            huCard = qiangGangContext.getCard();

            //一炮多响时，只有第一个人拿走此牌
            if( IsHasHuHistory() )
            {
                turnPlayer.removeLastOutCard();
            }
            historyActionEnum = HistoryActionEnum.HISTORY_ACTION_HU_QIANG_GANG;
        }
        else if( chupai == -1 )
        {
            huCard = maJiangPlayerData.getHolds().get( maJiangPlayerData.getHolds().size() - 1 );
            huedSeatPos = -1;
            historyActionEnum = HistoryActionEnum.HISTORY_ACTION_HU_ZIMO;
        }
        else
        {
            //普通放炮胡
            huedSeatPos = turn;
            historyActionEnum = HistoryActionEnum.HISTORY_ACTION_HU;
        }


        historyActionInfo.setCard(huCard);
        historyActionInfo.setSeatPos( seatPos );
        historyActionInfo.setLinkedSeatPos( huedSeatPos );
        historyActionInfo.setAction(historyActionEnum);
        saveHistory(historyActionInfo);

        //保存当前局的输赢情况
        List<HuCircleItemInfo> huCircleList =  CalcGameScroe.calcScores(this);
        HuCircleInfo huCircleInfo = new HuCircleInfo();
        huCircleInfo.setCircleIdx(logicTable.getNumOfGames());
        huCircleInfo.setHuCircleItemInfoList( huCircleList );
        huCircleInfos.add(huCircleInfo);
        logicTable.addHuCircleInfo(huCircleInfo);

        List<Integer> scoreList = new ArrayList<>(4);
        for(int i = 0; i < 4;i++)
        {
            scoreList.add(0);
        }
        for( int i = 0; i < huCircleList.size();i++ )
        {
            HuCircleItemInfo huCircleItemInfo = huCircleList.get(i);
            scoreList.set( huCircleItemInfo.getSeatPos(),huCircleItemInfo.getScore() );
        }

        //广播胡牌信息
        GameGuanyunProtocol.packetl2c_hu_info_nt.Builder builder = GameGuanyunProtocol.packetl2c_hu_info_nt.newBuilder();
        HistoryActionInfo lastHuActionInfo = historyActionInfos.get(historyActionInfos.size() - 1);
        GameGuanyunProtocol.msg_history_action_info.Builder msg_history_action_info_builder = convertToProtoType( lastHuActionInfo );
        builder.setHistoryActionInfo(msg_history_action_info_builder);
        builder.addAllScores(scoreList);
        logicTable.broadcast_msg_to_client(builder);

        clearAllOption();



        //查看是否还有胡的情况
        if( operationInfos.size() > 0 )
        {
            PlayerOperationInfo playerOperationInfo = operationInfos.remove(0);
            MaJiangPlayerData tmpPlayer = gameSeats.get(playerOperationInfo.getSeatPos());
            tmpPlayer.setActionOperation(playerOperationInfo);
            sendOperations(maJiangPlayerData);
            return MsgTypeDef.e_msg_result_def.e_rmt_success ;
        }

        doGameOver(seatPos,false);

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    private void saveHistory(int seatPos,HistoryActionEnum actionEnum,int card)
    {
        //保存操作
        HistoryActionInfo historyActionInfo = new HistoryActionInfo();
        historyActionInfo.setAction(actionEnum);
        historyActionInfo.setSeatPos(seatPos);
        historyActionInfo.setCard(card);

        MaJiangPlayerData maJiangPlayerData = gameSeats.get(seatPos);
        maJiangPlayerData.addHistoryItem(historyActionInfo);

        //保存此局的记录
        historyActionInfos.add(historyActionInfo);
    }

    private void saveHistory(HistoryActionInfo historyActionInfo)
    {
        MaJiangPlayerData maJiangPlayerData = gameSeats.get(historyActionInfo.getSeatPos());
        maJiangPlayerData.addHistoryItem(historyActionInfo);

        //保存此局的记录
        historyActionInfos.add(historyActionInfo);
    }

    //向所有人广播指向
    private void setTurnIndexMsg(int seatPos)
    {
        int cur_tm_s = TimeHelper.Instance.get_cur_time();
        //广播指向
        GameGuanyunProtocol.packetl2c_trun_index_nt.Builder builder = GameGuanyunProtocol.packetl2c_trun_index_nt.newBuilder();
        builder.setTimeExpire( cur_tm_s + 10);
        builder.setPos(seatPos);
        builder.setLeftCardNum(mahjongs.size() - currentIndex);
        logicTable.broadcast_msg_to_client(builder);
    }

    /**
     * 检查是否能胡，
     * @param maJiangPlayerData
     * @param targetCard        额外增加的牌
     */
    public void checkCanHu( MaJiangPlayerData maJiangPlayerData,int targetCard )
    {
        maJiangPlayerData.setCanHu(false);
        //如果摸最后一张牌的人已经叫听，则打出的牌其他叫听的人不能胡;如果未叫听，则打出的牌其他人可以胡，默认不支持
        for (int key : maJiangPlayerData.getTingMap().keySet())
        {
            if (key != targetCard)
            {
                continue;
            }

            maJiangPlayerData.setCanHu(true);
            break;
        }
    }

    public void doUserMoPai()
    {
        chupai = -1;

        int cur_tm_s =  TimeHelper.Instance.get_cur_time();
        List<Integer> cardList = mopaiWithHua(turn);

        MaJiangPlayerData turnSeat = gameSeats.get(turn);
        turnSeat.setLastFangGangSeat(-1);
        turnSeat.getLuoList().clear();
        turnSeat.calcCardMask();

        for (int card : cardList)
        {
            if( card > 0 )
            {
                saveHistory(turn,HistoryActionEnum.HISTORY_ACTION_MOPAI,card);
            }
        }

        // 如果大于1,则表示有补花的情况
        if (cardList.size() > 1)
        {
            List<Integer> flowerCards = cardList.subList(0, cardList.size() - 1);
            turnSeat.getFlowers().addAll(flowerCards);

            // 向玩家发送补花的信息
            GameGuanyunProtocol.packetl2c_replace_flower_nt.Builder builder =  GameGuanyunProtocol.packetl2c_replace_flower_nt.newBuilder();
            builder.setSeatPos(turn);
            builder.addAllFlowerCards(flowerCards);
            logicTable.broadcast_msg_to_client(builder);
        }

        if (cardList.get(cardList.size() - 1) == -1)
        {
            // 牌摸完了，结束
            calculateResult();
            doGameOver(turn, false);
            return;
        }


        int card = cardList.get(cardList.size() - 1);
        // 向出牌方发送摸牌指令，提示其可以出牌了
        GameGuanyunProtocol.packetl2c_action_nt.Builder actionBuilder = GameGuanyunProtocol.packetl2c_action_nt.newBuilder();
        actionBuilder.setCard(card);
        actionBuilder.setActionType(GameGuanyunProtocol.e_action_type.e_action_mo_card);
        actionBuilder.setSeatPos(turn);
        logicTable.send_msg_to_client(actionBuilder,turn);

        //向其它人广播摸牌动作，没有牌值
        GameGuanyunProtocol.packetl2c_action_nt.Builder otherActionBuilder = GameGuanyunProtocol.packetl2c_action_nt.newBuilder();
        otherActionBuilder.setActionType(GameGuanyunProtocol.e_action_type.e_action_mo_card);
        otherActionBuilder.setSeatPos(turn);
        logicTable.broadcast_msg_to_client(otherActionBuilder,turn);

        // 向所有人广播指指向,并提示倒计时
        setTurnIndexMsg(turn);

        //检查是否可以叫听
        checkCanJiaoTing(turnSeat);

        if (!turnSeat.isHued())
        {
            checkCanAnGang(turnSeat);
            checkCanWanGang(turnSeat);
        }

        checkCanHu(turnSeat, card);

        turnSeat.setCanChuPai(true);

        sendOperations(turnSeat, chupai);
    }

    /**
     * 如果遇到花牌,则需要再摸一张牌,直到遇到普通牌或是风牌或是摸不到牌了
     * @param seatIndex
     * @return
     */
    protected List<Integer> mopaiWithHua(int seatIndex)
    {
        List<Integer> list = new ArrayList<>();


        while (true)
        {
            int card = mopai(seatIndex);
            list.add(card);
            if (card == -1)
            {
                break;
            }
            else
            {
                CardTypeEnum mjType = MajiangCards.getCardType(logicTable.getMjType(),card);
                if (mjType != CardTypeEnum.CARD_TYPE_HUA)
                {
                    break;
                }
            }
        }

        return list;
    }

    protected int mopai(int seatIndex)
    {
        if (currentIndex == mahjongs.size()) { return -1; }

        MaJiangPlayerData seatData = this.gameSeats.get(seatIndex);
        int card = mahjongs.get(currentIndex);

        currentIndex += 1;
        CardTypeEnum mjType = MajiangCards.getCardType(logicTable.getMjType(),card);
        if (mjType == CardTypeEnum.CARD_TYPE_HUA)
        {
            return card;
        }

        seatData.moCard(card);

        if( seatIndex == 0 )
        {
            logger.info("card:" + card);
        }


        return card;
    }

    public void calculateResult()
    {

    }

    public void doGameOver(int seatPos,boolean isForce)
    {
        this.gameState = GameState.CIRCLE_OVER;

        if( isForce )
        {
            //强制结束后计算情况
            endGameHandler(true,true);
            return;
        }

        boolean hasHuFlag = IsHasHuHistory();
        if( hasHuFlag )
        {
            //连庄 + 轮庄
            HistoryActionInfo historyActionInfo = getFirstHuHistoryActionInfo();
            if( logicTable.getButton() ==  historyActionInfo.getSeatPos() )
            {
                logicTable.setNextButton( logicTable.getButton() );
            }
            else
            {
                logicTable.setNextButton( (logicTable.getButton() + 1) %  4  );
            }
        }
        else
        {
            //流局，当前庄家的下一个玩家
            logicTable.setNextButton( (logicTable.getButton() + 1) %  4  );
        }

        if( logicTable.getNumOfGames() == 1 )
        {
            // 如果是第一局，需要扣除玩家的房卡数量
            logicTable.costRoomCard();
        }

        boolean isEnd = logicTable.getNumOfGames() >= logicTable.getCreate_room_param().getCircleCount();

        //测试，一局就结束
        isEnd = true;

        endGameHandler(isEnd,false);

    }

    private void endGameHandler(boolean isEnd,boolean isForce)
    {
        //保存当前局的日志信息
        int cur_tm_s = TimeHelper.Instance.get_cur_time();
        if( logicTable.getNumOfGames() == 1 )
        {
            logicTable.saveRoomInfoLog();
        }

        int cur_time_s = TimeHelper.Instance.get_cur_time();
        //发送当前局的一局结束信息
        Map<Integer,Integer> scoreMap = new HashMap<>();
        scoreMap.put(0,0);
        scoreMap.put(1,0);
        scoreMap.put(2,0);
        scoreMap.put(3,0);
        for(HuCircleInfo huCircleInfo : huCircleInfos)
        {
           for(HuCircleItemInfo huCircleItemInfo : huCircleInfo.getHuCircleItemInfoList())
           {
               if( !scoreMap.containsKey(huCircleItemInfo.getSeatPos()) )
               {
                   scoreMap.put(huCircleItemInfo.getSeatPos(),0);
               }
               int curScore = scoreMap.get(huCircleItemInfo.getSeatPos());
               scoreMap.put( huCircleItemInfo.getSeatPos(),curScore + huCircleItemInfo.getScore() );
           }
        }

        GameGuanyunProtocol.packetl2c_circle_result_nt.Builder circle_result_builder = GameGuanyunProtocol.packetl2c_circle_result_nt.newBuilder();
        GameGuanyunProtocol.msg_circle_over_info.Builder msg_circle_over_info_builder =  GameGuanyunProtocol.msg_circle_over_info.newBuilder();
        msg_circle_over_info_builder.setCircleNum( logicTable.getNumOfGames() );
        msg_circle_over_info_builder.setNextCircleStartTm( cur_time_s );
        for(int i = 0; i < 4; i ++)
        {
            MaJiangPlayerData playerData = gameSeats.get(i);
            GameGuanyunProtocol.msg_circle_over_detail.Builder msg_circle_over_detail_builder =  GameGuanyunProtocol.msg_circle_over_detail.newBuilder();
            msg_circle_over_detail_builder.setSeatPos(i);
            msg_circle_over_detail_builder.setFen(scoreMap.get(i));
            msg_circle_over_detail_builder.setHua(0);
            msg_circle_over_detail_builder.addAllHandCards( playerData.getHolds() );
            msg_circle_over_detail_builder.setTingCard( playerData.getTingCard() );
            msg_circle_over_detail_builder.addAllOutCards( playerData.getFolds() );
            msg_circle_over_detail_builder.addAllHuaCards( playerData.getFlowers() );
            for(int j = 0; j < playerData.getOutHistoryList().size();j++ )
            {
                HistoryActionInfo historyActionInfo = playerData.getOutHistoryList().get(i);
                GameGuanyunProtocol.msg_history_action_info.Builder msg_history_actiion_info_builder = convertToProtoType(historyActionInfo);
                msg_circle_over_detail_builder.addHistoryActionInfos(msg_history_actiion_info_builder);
            }
            msg_circle_over_info_builder.addDetail(msg_circle_over_detail_builder);
        }
        for(int i = 0; i < historyActionInfos.size();i++)
        {
            HistoryActionInfo historyActionInfo = historyActionInfos.get(i);
            boolean isHu = isHuActionType( historyActionInfo );
            if( isHu )
            {
                GameGuanyunProtocol.msg_history_action_info.Builder  history_builder = convertToProtoType(historyActionInfo);
                msg_circle_over_info_builder.addHuHistoryActionInfo( history_builder );
            }
        }
        circle_result_builder.setCircleInfo(msg_circle_over_info_builder);
        logicTable.broadcast_msg_to_client(circle_result_builder);

        //数据库保存当前局的对局信息
        List<Integer> scoreList = new ArrayList<>();
        scoreList.add( scoreMap.get(0) );
        scoreList.add( scoreMap.get(1) );
        scoreList.add( scoreMap.get(2) );
        scoreList.add( scoreMap.get(3) );
        MjRoundLogDoc mjRoundLogDoc = new MjRoundLogDoc();
        mjRoundLogDoc.setInitCards(mahjongs);
        mjRoundLogDoc.setCircleIdx(logicTable.getNumOfGames());
        mjRoundLogDoc.setCreateTimeNum(cur_time_s);
        mjRoundLogDoc.setCreateTime(new Date(cur_time_s*1000));
        mjRoundLogDoc.setRoomId(logicTable.getRoomId());
        mjRoundLogDoc.setRounds(Collections.singletonList(historyActionInfos));
        mjRoundLogDoc.setScroes( scoreList );
        DbLog.Instance.getMongoTemplate().insert(mjRoundLogDoc);

        if( !isEnd )
        {
            logicTable.setGameSttus(LogicTable.GameSttus.STATUS_INIT);
            logicTable.restartGame();
            return;
        }

        //发送游戏整体结束的消息
        GameGuanyunProtocol.packetl2c_game_over_nt.Builder game_over_builder = GameGuanyunProtocol.packetl2c_game_over_nt.newBuilder();
        GameGuanyunProtocol.msg_game_over_info.Builder game_over_info_builder = GameGuanyunProtocol.msg_game_over_info.newBuilder();
        game_over_info_builder.setCircleIdx(logicTable.getNumOfGames());
        game_over_info_builder.addScores(0);
        game_over_info_builder.addScores(0);
        game_over_info_builder.addScores(0);
        game_over_info_builder.addScores(0);
        game_over_builder.setMsgGameOverInfo(game_over_info_builder);
        logicTable.broadcast_msg_to_client(game_over_builder);

        logicTable.gameOver();
    }


    private void clearAllOption()
    {
        this.qiangGangContext = null;
        for (int i = 0; i < 4; i++)
        {
            gameSeats.get(i).clearAllOptions();
        }
    }

    protected void moveToNextUser()
    {
        turn++;
        turn %= 4;
    }


    private void add_init_card(int start,int end,List<Integer> cards)
    {
        for(int i = start;i <= end; i ++)
        {
            cards.add(i);
        }
    }

    public void set_scene_info_result(GameGuanyunProtocol.packetl2c_get_scene_info_result.Builder builder,int seatPos)
    {
        //e_game_ready					=	2;			//玩家满了，正在准备阶段
        //e_game_gameing					=	3;			//游戏开始阶段
        //e_game_circle_over				=	4;			//一局结束时

        if( gameState == GameState.READING )
        {
            builder.setStatus(GameGuanyunProtocol.e_game_status_type.e_game_ready);
        }
        else if( gameState == GameState.RUN )
        {
            builder.setStatus(GameGuanyunProtocol.e_game_status_type.e_game_gameing);
            builder.setGamingInfo( getGamingSceneInfo(seatPos) );
        }
        else if( gameState == GameState.CIRCLE_OVER )
        {
            builder.setStatus((GameGuanyunProtocol.e_game_status_type.e_game_circle_over));
        }
        else if( gameState == GameState.GAME_OVER )
        {
            builder.setStatus((GameGuanyunProtocol.e_game_status_type.e_game_game_over));
        }
    }

    private GameGuanyunProtocol.msg_status_gaming_info.Builder getGamingSceneInfo(int seatPos)
    {
        MaJiangPlayerData playerData = gameSeats.get(seatPos);
        GameGuanyunProtocol.msg_status_gaming_info.Builder builder = GameGuanyunProtocol.msg_status_gaming_info.newBuilder();
        builder.addAllHandHards(playerData.getHolds());
        builder.setHostPos( button );
        builder.setActivePos(turn);
        builder.setCircleIdx(logicTable.getNumOfGames());
        builder.setMyPos( seatPos );
        builder.addAllDiceList( diceList );

        if( historyActionInfos.size() > 0 )
        {
            HistoryActionInfo historyActionInfo = historyActionInfos.get(historyActionInfos.size() - 1);

            GameGuanyunProtocol.msg_history_action_info.Builder msg_history_action_info_builder = convertToProtoType(historyActionInfo);
            builder.setHistoryActionInfo(msg_history_action_info_builder);
        }


        for(int i = 0; i < 4;i++)
        {
            MaJiangPlayerData tmpPlayer = gameSeats.get(i);
            GameGuanyunProtocol.msg_room_info.Builder msg_room_info_builder = GameGuanyunProtocol.msg_room_info.newBuilder();
            msg_room_info_builder.setHandCardNum( tmpPlayer.getHolds().size() );
            msg_room_info_builder.addAllOutWallCards( tmpPlayer.getFolds() );
            msg_room_info_builder.addAllFlowerCards( tmpPlayer.getFlowers() );
            msg_room_info_builder.setTingCard( tmpPlayer.getTingCard() );

            for( HistoryActionInfo historyActionInfo : tmpPlayer.getOutHistoryList() )
            {
                GameGuanyunProtocol.msg_history_action_info.Builder msg_history_action_info_builder = convertToProtoType(historyActionInfo);
                if( historyActionInfo.getLinkCards() != null && historyActionInfo.getLinkCards().size() > 0 )
                {
                    msg_history_action_info_builder.addAllLinkdedCards( historyActionInfo.getLinkCards() );
                }
                msg_room_info_builder.addHistoryActionInfo(msg_history_action_info_builder);
            }

            builder.addRoomInfo(msg_room_info_builder);
        }

        List<GameGuanyunProtocol.msg_action_type> msg_action_type_list = getMsgActionTypeList(playerData);
        if( msg_action_type_list.size() > 0 )
        {
            builder.addAllActionTypes(msg_action_type_list);
        }

        return builder;
    }

    //各种check
    private void checkCanPeng( MaJiangPlayerData maJiangPlayerData,int card )
    {
        if( !maJiangPlayerData.getCountMap().containsKey(card) )
        {
            return;
        }

        int count = maJiangPlayerData.getCountMap().get(card);
        if( count < 2 )
        {
            return;
        }

        // 叫听后不能再碰了
        if( maJiangPlayerData.getTingCard() > 0)
        {
            return;
        }

        //如果是漏碰了，则也不能再碰了
        if( maJiangPlayerData.getLuoList().indexOf(card) >= 0 )
        {
            return;
        }

        maJiangPlayerData.setCanPeng(true);
    }

    private  void checkCanDianGang( MaJiangPlayerData maJiangPlayerData,int card )
    {
        boolean flag = onlyCheckCanDianGang(maJiangPlayerData, card);
        if (!flag) { return; }

        maJiangPlayerData.setCanDianGang(true);
    }

    protected boolean onlyCheckCanDianGang(MaJiangPlayerData maJiangPlayerData, int card)
    {
        if( mahjongs.size() <= currentIndex )
        {
            return  false;
        }

        if( !maJiangPlayerData.getCountMap().containsKey(card) )
        {
            return  false;
        }

        if( maJiangPlayerData.getCountMap().get(card) != 3 )
        {
            return false;
        }

        if( maJiangPlayerData.isCanJiaoTing() )
        {
            boolean isSameTing = checkSameTingType(maJiangPlayerData, card, 3);
            if (!isSameTing) { return false; }
        }

        return true;
    }

    public List<HistoryActionInfo> getHistoryActionInfos() {
        return historyActionInfos;
    }

    public List<MaJiangPlayerData> getGameSeats() {
        return gameSeats;
    }

    public void checkCanTingPai(MaJiangPlayerData maJiangPlayerData)
    {
        maJiangPlayerData.clearTingMap();

        MaJianUtils.checkAllTingPai(maJiangPlayerData,logicTable.getMjType(),-1);
    }

    //叫听时有3n+2张牌，看是否打出一张牌后能够成牌
    public void checkCanJiaoTing( MaJiangPlayerData maJiangPlayerData )
    {
        if( maJiangPlayerData.getHolds().size() % 3 != 2 )
        {
            return;
        }

        //保存检查叫听以前胡牌的听牌结构
        Map<Integer, HuPattern> oldTingMap = new HashMap<>();
        for(Map.Entry<Integer,HuPattern> entity : maJiangPlayerData.getTingMap().entrySet())
        {
            oldTingMap.put(entity.getKey(), entity.getValue());
        }


        //不能在循环的时候，删除数据，需要拿出来
        List<Integer> holdKeyList = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entity : maJiangPlayerData.getCountMap().entrySet())
        {
            if(entity.getValue() == 0)
            {
                continue;
            }
            holdKeyList.add(entity.getKey());
        }

        //能否听
        boolean canTingflag = false;
        for(int i = 0;i < holdKeyList.size();i++)
        {
            int card = holdKeyList.get(i);
            boolean canJiaoTing = checkCanJiaoTingSkipOne(maJiangPlayerData,card);
            if(canJiaoTing)
            {
                canTingflag = true;
                break;
            }
        }

        maJiangPlayerData.calcCardMask();

        //恢复检查叫听以前的听牌结构
        maJiangPlayerData.getTingMap().clear();
        maJiangPlayerData.getTingMap().putAll(oldTingMap);

        if(!canTingflag)
        {
            return;
        }

        maJiangPlayerData.setCanJiaoTing(true);
    }

    private boolean checkCanJiaoTingSkipOne(MaJiangPlayerData seatData,int card)
    {
        boolean canTingflag = false;
        int cnt = seatData.getCountMap().get(card);
        int index = seatData.getHolds().indexOf(card);

        if(index == -1)
        {
            logger.error("the count is not in map:");
            System.out.println("countMap:" + seatData.getCountMap());
            System.out.println( "hold:" + seatData.getHolds());
            return false;
        }

        seatData.getHolds().remove(index);
        seatData.getCountMap().put(card,  cnt - 1 );

        //是否需要检查叫听
        boolean needCheckCanJiaoTing = true;

        seatData.getTingMap().clear();
        if(needCheckCanJiaoTing)
        {
            MaJianUtils.checkAllTingPai(seatData, logicTable.getMjType(), -1);
        }

        seatData.getHolds().add(index, card);
        seatData.getCountMap().put(card,cnt);

        if(!needCheckCanJiaoTing)
        {
            return false;
        }

        // 听的类型 0：可以双头听,多头听， 1：只能听一个

        if( seatData.getTingMap().size() == 1 )
        {
            canTingflag = true;
        }


        return canTingflag;
    }

    public void checkCanAnGang(MaJiangPlayerData maJiangPlayerData)
    {
        boolean flag = onlyCheckCanAnGang(maJiangPlayerData, -1);
        if (!flag) { return; }
        maJiangPlayerData.setCanAnGang(true);
    }

    private boolean onlyCheckCanAnGang(MaJiangPlayerData seatData, int card)
    {
        if (mahjongs.size() <= currentIndex) { return false; }

        seatData.getAngGangList().clear();

        List<Integer> skipAngangList = new ArrayList<>();
        // 如果是在叫听的情况下，
        if (seatData.getTingCard() > 0)
        {
            // 先查找所以可以暗杠的牌
            List<Integer> anglist = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : seatData.getCountMap().entrySet())
            {
                if (entry.getValue() == 4)
                {
                    anglist.add(entry.getKey());
                }
            }

            if (card != -1 && seatData.getCountMap().get(card) == 3)
            {
                anglist.add(card);
            }

            // 如果去掉此杠牌还能听牌，并且听的牌与没有拿掉之前的一样，则认为此是可以杠
            for (int testCard : anglist)
            {
                if (seatData.getTingCard() > 0)
                {
                    boolean isSameTing = checkSameTingType(seatData, testCard, 4);
                    if (!isSameTing)
                    {
                        skipAngangList.add(testCard);
                    }
                }
            }
        }

        boolean anGangFlag = false;
        for (int key : seatData.getCountMap().keySet())
        {
            if (key != card && card != -1)
            {
                continue;
            }

            if (!seatData.getCountMap().containsKey(key))
            {
                continue;
            }
            int c = seatData.getCountMap().get(key);
            if (c == 4)
            {
                // 如果是需要过滤的暗杠的牌，则需要过滤掉
                if (skipAngangList.indexOf(key) >= 0)
                {
                    continue;
                }

                anGangFlag = true;
                seatData.getAngGangList().add(key);
            }
        }

        return anGangFlag;
    }

    protected void checkCanWanGang(MaJiangPlayerData seatData)
    {
        if (mahjongs.size() <= currentIndex) { return; }

        seatData.getWangGangList().clear();

        for (HistoryActionInfo mjAction : seatData.getPlayerHistoryList())
        {
            if (mjAction.getAction() == HistoryActionEnum.HISTORY_ACTION_PENG)
            {
                int card = mjAction.getCard();
                if (!seatData.getCountMap().containsKey(card))
                {
                    continue;
                }

                if (seatData.getCountMap().get(card) != 1)
                {
                    continue;
                }

                if (seatData.getTingCard() > 0)
                {
                    boolean isSameType = checkSameTingType(seatData, card, 1);
                    if (!isSameType)
                    {
                        continue;
                    }
                }

                seatData.setCanWanGang(true);
                seatData.getWangGangList().add(card);
            }
        }
    }

    protected boolean onlyCheckCanWanGang(MaJiangPlayerData seatData, int targetPai)
    {
        if (mahjongs.size() <= currentIndex) { return false; }

        if (!seatData.getCountMap().containsKey(targetPai)) { return false; }
        if (seatData.getCountMap().get(targetPai) != 1) { return false; }

        for (HistoryActionInfo mjAction : seatData.getPlayerHistoryList())
        {
            if (mjAction.getAction() == HistoryActionEnum.HISTORY_ACTION_PENG)
            {
                int card = mjAction.getCard();
                if (card == targetPai) { return true; }
            }
        }

        return false;
    }

    /**
     * 是杠的后,是否还听相同的牌,同样的牌型
     *
     * @param seatData
     * @param targetPai
     * @param
     * @return
     */
    protected boolean checkSameTingType(MaJiangPlayerData seatData, int targetPai, int checkNum)
    {
        // 叫听后，如果明杠，暗杠与补杠的可胡牌的牌型不能变
        if (seatData.getTingCard() == 0) { return true; }

        if (seatData.getTingMap().size() > 1) { return false; }

        int old_size = seatData.getCountMap().get(targetPai);

        if (checkNum == -1)
        {
            if (old_size != 3 && old_size != 4) { return false; }
        }
        else
        {
            if (old_size != checkNum) { return false; }
        }

        Map.Entry<Integer, HuPattern> entry = seatData.getTingMap().entrySet().iterator().next();
        // 记录换牌以前牌的结构
        int tingCard = entry.getKey();
        HuPattern huPattern = entry.getValue();

        if (tingCard == 0 || huPattern == null) { return true; }

        List<Integer> removeIndexList = new ArrayList<>(); // 移除的对象的索引位置，检测后还需要将将其还原的
        for (int i = 0; i < old_size; i++)
        {
            int idx = seatData.getHolds().indexOf(targetPai);
            removeIndexList.add(idx);
            seatData.getHolds().remove(idx);
        }

        seatData.getCountMap().put(targetPai, 0);
        seatData.getTingMap().clear();
        // 再次查询可以胡的数据
        MaJianUtils.checkAllTingPai(seatData, logicTable.getMjType(), -1);

        boolean flag = seatData.getTingMap().size() == 1;

        if (flag)
        {
            // 检查换牌前后的结构是否相同
            Map.Entry<Integer, HuPattern> oldentry = seatData.getTingMap().entrySet().iterator().next();

            if (oldentry.getKey() != tingCard || !oldentry.getValue().getPattern().equals(huPattern.getPattern()))
            {
                flag = false;
            }
        }

        // 还原换牌以前的听牌结构
        seatData.getCountMap().put(targetPai, old_size);
        while (removeIndexList.size() > 0)
        {
            seatData.getHolds().add(removeIndexList.remove(removeIndexList.size() - 1), targetPai);
        }

        seatData.getTingMap().put(tingCard, huPattern);

        return flag;
    }

    public boolean hasOperations(MaJiangPlayerData maJiangPlayerData)
    {
        if( maJiangPlayerData.isCanAnGang() ) { return true; }

        if (maJiangPlayerData.isCanWanGang()) { return true; }

        if (maJiangPlayerData.isCanDianGang()) { return true; }

        if (maJiangPlayerData.isCanPeng()) { return true; }

        if (maJiangPlayerData.isCanHu()) { return true; }

        return false;
    }

    private void testInitCards()
    {
        List<Integer> playerList1 = new ArrayList<>();
        playerList1.add(0x01);
        playerList1.add(0x01);
        playerList1.add(0x01);
        playerList1.add(0x02);
        playerList1.add(0x02);
        playerList1.add(0x02);
        playerList1.add(0x03);
        playerList1.add(0x03);
        playerList1.add(0x03);
        playerList1.add(0x04);
        playerList1.add(0x04);
        playerList1.add(0x04);
        playerList1.add(0x05);


        List<Integer> playerList2 = new ArrayList<>();
        playerList2.add(0x01);
        playerList2.add(0x02);
        playerList2.add(0x03);
        playerList2.add(0x04);
        playerList2.add(0x05);

        List<Integer> allReplaceCards = new ArrayList<>();
        allReplaceCards.addAll( playerList1 );
        allReplaceCards.addAll( playerList2 );

        for(int i = 0; i < allReplaceCards.size();i++)
        {
            int curCard = allReplaceCards.get(i);
            int findIdx = this.mahjongs.indexOf(curCard);
            this.mahjongs.remove(findIdx);
            this.mahjongs.add(curCard);
        }

        logger.info( "before:" + mahjongs.toString() );

        replaceCardList(this.mahjongs,playerList1,0);
        replaceCardList(this.mahjongs,playerList2,1);

        logger.info( "after:" +  mahjongs.toString() );
    }

    private void replaceCardList(List<Integer> sourceList,List<Integer> replaceList,int idx)
    {
        for( int i = 0; i < replaceList.size();i++ )
        {
            int curIdx = 4 * i + idx;
            int curCard = sourceList.get( curIdx );
            int replaceCard = replaceList.get(i);
            if( curCard == replaceCard )
            {
                continue;
            }

            sourceList.set(curIdx,replaceCard);
            //从后面找到一个替换的图牌，并替换为当前的卡
            int lastCardIdx = sourceList.lastIndexOf(replaceCard);
            sourceList.set(lastCardIdx,curCard);

            //logger.info("curIdx:" + curIdx + " curCard:" + curCard + " replaceCard:" + replaceCard + " lastCardIdx:" + lastCardIdx);
            //logger.info("replaceList:" + sourceList.toString());
        }
    }

    private boolean IsHasHuHistory()
    {
        //一炮多响时，只有第一个人拿走此牌
        if( historyActionInfos.size() == 0 )
        {
           return  false;
        }

        HistoryActionInfo lastHistoryActionInfo = historyActionInfos.get( historyActionInfos.size() - 1 );
        return isHuActionType( lastHistoryActionInfo );
    }

    //获得第一个胡的记录
    private HistoryActionInfo getFirstHuHistoryActionInfo()
    {
       for(int i = 0; i < historyActionInfos.size();i++)
       {
           if( isHuActionType( historyActionInfos.get(i) )  )
           {
               return historyActionInfos.get(i);
           }
       }

        return null;
    }

    //判断是否是胡的类型
    private boolean isHuActionType(HistoryActionInfo lastHistoryActionInfo)
    {
        boolean needRemoveCardFromPool = true;
        if(lastHistoryActionInfo.getAction() == HistoryActionEnum.HISTORY_ACTION_HU  ||
                lastHistoryActionInfo.getAction() == HistoryActionEnum.HISTORY_ACTION_HU_QIANG_GANG ||
                lastHistoryActionInfo.getAction() == HistoryActionEnum.HISTORY_ACTION_HU_ZIMO ||
                lastHistoryActionInfo.getAction() == HistoryActionEnum.HISTORY_ACTION_HU_DIAN_GANG ||
                lastHistoryActionInfo.getAction() == HistoryActionEnum.HISTORY_ACTION_HU_GABNG_PAO ||
                lastHistoryActionInfo.getAction() == HistoryActionEnum.HISTORY_ACTION_HU_FANG_PAO )
        {

            //如果上一个操作就是胡，则不需要
            needRemoveCardFromPool = false;
        }

        return needRemoveCardFromPool;
    }

    private GameGuanyunProtocol.msg_history_action_info.Builder convertToProtoType(HistoryActionInfo historyActionInfo)
    {
        GameGuanyunProtocol.msg_history_action_info.Builder msg_history_action_info_builder = GameGuanyunProtocol.msg_history_action_info.newBuilder();
        msg_history_action_info_builder.setActionType(GameGuanyunProtocol.e_history_action_type.valueOf( historyActionInfo.getAction().getValue() ));
        msg_history_action_info_builder.setCard(historyActionInfo.getCard());
        msg_history_action_info_builder.setLinkedPos(historyActionInfo.getLinkedSeatPos());
        msg_history_action_info_builder.setSeatPos( historyActionInfo.getSeatPos() );
        return msg_history_action_info_builder;
    }

    public enum GameState
    {
        READING(0),            // 四人齐了
        RUN(1),                 // 正在跑
        CIRCLE_OVER(2),          // 一局结束
        GAME_OVER(3)             //游戏结束
        ;

        private int state;
        private int getNumber()
        {
            return state;
        }

        private GameState(int value)
        {
            state = value;
        }
    }

}
