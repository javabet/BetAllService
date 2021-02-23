package com.wisp.game.bet.games.share.utils;

import com.wisp.game.bet.games.share.enums.CardTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09, //万1 - 9
//0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19, //条 1- 9
//0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29, //饼1 - K
//0x31,0x32,0x33,0x34,0x35,0x36,0x37 //风 东南西北 中发白
//0x41,0x42,0x43,0x44,0x45,0x46,0x47,0x48,//8张花牌 春夏秋冬 梅兰猪菊

public class MajiangCards {


    private static Logger logger = LoggerFactory.getLogger(MajiangCards.class);
    private static Map<Integer, List<CardType>> cardTypeMap = new HashMap<>();

    private static boolean initFlag = false;

    private static void init() {
        if (initFlag) {
            return;
        }

        initFlag = true;

        //108张
        List<CardType> cardTypeList = new ArrayList<>();
        cardTypeList.add(new CardType(0x01, 0x09, CardTypeEnum.CARD_TYPE_WANG));
        cardTypeList.add(new CardType(0x11, 0x19,CardTypeEnum.CARD_TYPE_TIAO));
        cardTypeList.add(new CardType(0x21, 0x29,CardTypeEnum.CARD_TYPE_CIRCLE));
        cardTypeMap.put(1, cardTypeList);

        //112张
        cardTypeList = new ArrayList<>();
        cardTypeList.add(new CardType(0x01, 0x09, CardTypeEnum.CARD_TYPE_WANG));
        cardTypeList.add(new CardType(0x11, 0x19,CardTypeEnum.CARD_TYPE_TIAO));
        cardTypeList.add(new CardType(0x21, 0x29,CardTypeEnum.CARD_TYPE_CIRCLE));
        cardTypeList.add(new CardType(0x31, 0x34,4,CardTypeEnum.CARD_TYPE_FEN,false));
        cardTypeMap.put(2, cardTypeList);

        //128张牌
        cardTypeList = new ArrayList<>();
        cardTypeList.add(new CardType(0x01, 0x09, CardTypeEnum.CARD_TYPE_WANG));
        cardTypeList.add(new CardType(0x11, 0x19,CardTypeEnum.CARD_TYPE_TIAO));
        cardTypeList.add(new CardType(0x21, 0x29,CardTypeEnum.CARD_TYPE_CIRCLE));
        cardTypeList.add(new CardType(0x31, 0x34,4,CardTypeEnum.CARD_TYPE_FEN,false));
        cardTypeList.add(new CardType(0x35, 0x37,4,CardTypeEnum.CARD_TYPE_ZI,false));
        cardTypeMap.put(2, cardTypeList);
    }

    public static void addCardTypes(int type,List<CardType> list)
    {
        cardTypeMap.put(type,list);
    }

    public static List<CardType>  getCardTypes( int type )
    {
        init();

        if( !cardTypeMap.containsKey(type) )
        {
            logger.error("the type is not exist:%d",type);
        }

        return cardTypeMap.get( type );
    }

    public static List<Integer> getCards(int type)
    {
        List<CardType> list = getCardTypes(type);
        List<Integer> cards = new ArrayList<>();

        for( CardType cardType : list )
        {
            for(int j = 0;  j < cardType.getRepeatNum();j++)
            {
                for(int i = cardType.getStart(); i <= cardType.getEnd(); i ++)
                {
                    cards.add(i);
                }
            }
        }

        return cards;
    }

    public static CardTypeEnum getCardType(int type,int card)
    {
        if( !cardTypeMap.containsKey(type) )
        {
            return CardTypeEnum.CARD_TYPE_EMPTY;
        }

        List<CardType> list = cardTypeMap.get(type);

        for(CardType cardType : list)
        {
            if( cardType.getStart() <= card && card <= cardType.getEnd() )
            {
                return cardType.getType();
            }
        }

        return CardTypeEnum.CARD_TYPE_EMPTY;
    }

}
