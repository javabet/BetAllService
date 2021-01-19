package com.wisp.game.bet.games.guanyuan.logic;

import com.sun.xml.internal.ws.server.sei.TieHandler;
import com.wisp.game.bet.games.guanyuan.doc.GuanYunPlayerDoc;
import com.wisp.game.bet.games.share.common.CardTypeEnum;
import com.wisp.game.bet.games.share.common.MaJiangPlayerData;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.core.random.RandomHandler;
import com.wisp.game.core.utils.CommonUtils;
import game_guanyuan_protocols.GameGuanyuanDef;
import game_guanyuan_protocols.GameGuanyunProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
    private int turn;                   //现在轮到谁的位置了
    private int chupai;                 //正在出牌的值
    private int firstHuPai = -1;        //第一个胡牌的人
    protected int button;

    private int init_left_card = 0;         //现在余下牌的数量

    private List<MaJiangPlayerData> gameSeats;
    private LogicTable logicTable;

    public LogicCore( LogicTable logicTable) {
        this.logicTable = logicTable;
        this.gameSeats = new ArrayList<>();
    }

    public void start()
    {
        logicTable.setButton(logicTable.getNextButton());
        this.button = logicTable.getButton();

        this.turn = 0;
        this.chupai = -1;
        this.firstHuPai = -1;

        this.logicTable.setNumOfGames(logicTable.getNumOfGames() + 1);

        for(int i = 0; i < 4;i ++)
        {
            MaJiangPlayerData maJiangPlayerData = new MaJiangPlayerData();
            maJiangPlayerData.setSeatIndex(i);
            this.gameSeats.add(maJiangPlayerData);
        }

        List<Integer> cards = getInitCards();
        mahjongs = CommonUtils.deepClone(cards);

        //洗牌
        for (int i = 0; i < mahjongs.size(); i++)
        {
            int lastIndex = mahjongs.size() - 1 - i;
            int index = (int) Math.floor(Math.random() * lastIndex);
            int t = mahjongs.get(index);
            mahjongs.set(index, mahjongs.get(lastIndex));
            mahjongs.set(lastIndex, t);
        }

        logicTable.setGameSttus(LogicTable.GameSttus.STATUS_RUN);
        //发牌
        deal();

        init_left_card = mahjongs.size() - currentIndex;

        //记录玩家的手牌
        for(MaJiangPlayerData maJiangPlayerData : gameSeats)
        {
            maJiangPlayerData.setInitHolds( CommonUtils.deepSimpleList(maJiangPlayerData.getHolds()) );
            maJiangPlayerData.setInitFlowerCards( CommonUtils.deepSimpleList(maJiangPlayerData.getInitFlowerCards()) );
        }

        GameGuanyunProtocol.packetl2c_circle_start_nt.Builder circleStartNt =  GameGuanyunProtocol.packetl2c_circle_start_nt.newBuilder();
        circleStartNt.setHostPos(button);
        circleStartNt.setCircleCount(0);
        circleStartNt.setCircleInfo(0);
        logicTable.broadcast_msg_to_client(circleStartNt);

        for(MaJiangPlayerData maJiangPlayerData : gameSeats)
        {
            GameGuanyunProtocol.packetl2c_card_send_nt.Builder cardSendNtBuilder = GameGuanyunProtocol.packetl2c_card_send_nt.newBuilder();
            cardSendNtBuilder.addAllCards(maJiangPlayerData.getHolds());
            cardSendNtBuilder.setLeftCard(mahjongs.size() - currentIndex);
            logicTable.send_msg_to_client(cardSendNtBuilder,maJiangPlayerData.getSeatIndex());
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
                int duoyu = maJiangPlayerData.getHolds().remove(maJiangPlayerData.getHolds().size() - 1);
                maJiangPlayerData.getCountMap().put(duoyu,maJiangPlayerData.getCountMap().get(duoyu) - 1);

                checkCanTingPai(maJiangPlayerData);

                maJiangPlayerData.getHolds().add(duoyu);
                maJiangPlayerData.getCountMap().put(duoyu,maJiangPlayerData.getCountMap().get(duoyu) + 1);
            }
        }

        //通知玩家出牌
        MaJiangPlayerData turnSeat = gameSeats.get(turn);
        turnSeat.setCanChuPai(true);

        int cur_tm_s = TimeHelper.Instance.get_cur_time();

        GameGuanyunProtocol.packetl2c_action_nt.Builder actionNtBuilder = GameGuanyunProtocol.packetl2c_action_nt.newBuilder();
        actionNtBuilder.setPos(turn);
        actionNtBuilder.setLeftCardCount(mahjongs.size() - currentIndex);
        actionNtBuilder.setTimeExpire( cur_tm_s + 10 );
        logicTable.broadcast_msg_to_client(actionNtBuilder);

        //告诉玩家色子
        List<Integer> diceList = new ArrayList<>();
        for(int i = 0; i < 2;i ++)
        {
            int dice = RandomHandler.Instance.getRandomValue(1,6);
            diceList.add(dice);
        }

        GameGuanyunProtocol.packetl2c_dice_nt.Builder diceNtBuilder = GameGuanyunProtocol.packetl2c_dice_nt.newBuilder();
        diceNtBuilder.setPos(turn);
        diceNtBuilder.addAllDiceList(diceList);
        logicTable.broadcast_msg_to_client(diceNtBuilder);

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

    public void sendOperations( MaJiangPlayerData maJiangPlayerData,int card )
    {
        boolean flag = hasOperations(maJiangPlayerData);
        if( !flag )
        {
            return;
        }


    }

    public void checkCanTingPai(MaJiangPlayerData maJiangPlayerData)
    {

    }

    public void checkCanJiaoTing( MaJiangPlayerData maJiangPlayerData )
    {

    }

    public void checkCanAnGang(MaJiangPlayerData maJiangPlayerData)
    {

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

    /**
     * 检查是否能胡，
     * @param maJiangPlayerData
     * @param targetCard        额外增加的牌
     */
    public void checkCanHu( MaJiangPlayerData maJiangPlayerData,int targetCard )
    {

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
                CardTypeEnum mjType = getMjType(card);
                if (mjType != CardTypeEnum.CARD_TYPE_HUA)
                {
                    break;
                }
            }
        }

        return list;
    }

    //查找牌的类型
    protected CardTypeEnum getMjType(int card )
    {
        int type = card >> 4;
        if( type < 4 )
        {
            return CardTypeEnum.CARD_TYPE_NORMAL;
        }
        else if( type == 4 )
        {
            return CardTypeEnum.CARD_TYPE_FEN;
        }
        else if( type == 5 )
        {
            return CardTypeEnum.CARD_TYPE_HUA;
        }

        return CardTypeEnum.CARD_TYPE_NORMAL;
    }

    protected int mopai(int seatIndex)
    {
        if (currentIndex == mahjongs.size()) { return -1; }

        MaJiangPlayerData seatData = this.gameSeats.get(seatIndex);
        int card = mahjongs.get(currentIndex);

        currentIndex += 1;
        CardTypeEnum mjType = getMjType(card);
        if (mjType == CardTypeEnum.CARD_TYPE_HUA)
        {
            return card;
        }

        seatData.getHolds().add(card);
        if (seatData.getCountMap().containsKey(card))
        {
            seatData.getCountMap().put(card, seatData.getCountMap().get(card) + 1);
        }
        else
        {
            seatData.getCountMap().put(card, 1);
        }

        return card;
    }

    public List<Integer> getInitCards()
    {
        List<Integer> list = new ArrayList<>();
        add_init_card(0x01,0x09,list);
        add_init_card(0x11,0x19,list);
        add_init_card(0x21,0x29,list);

        add_init_card(0x31,0x34,list);

        return list;
    }

    private void add_init_card(int start,int end,List<Integer> cards)
    {
        for(int i = start;i <= end; i ++)
        {
            cards.add(i);
        }
    }

}
