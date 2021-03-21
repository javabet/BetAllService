package com.wisp.game.bet.db.mongo.logs.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//房间每局情况
@Document("MjCircleLogDoc")
public class MjRoundLogDoc implements Serializable
{
    private static final long serialVersionUID = -4992362321220581092L;

    @Field(targetType = FieldType.INT32)private int RoomId;
    @Field(targetType = FieldType.INT32)private int CircleIdx;
    @Field(targetType = FieldType.INT32)private int CreateTimeNum;
    @Field(targetType = FieldType.DATE_TIME)private Date CreateTime;
    private List<Integer> initCards;
    private List<Object> Rounds;
    private List<Integer> Scroes;

    public int getRoomId()
    {
        return RoomId;
    }

    public void setRoomId(int roomId)
    {
        RoomId = roomId;
    }

    public int getCircleIdx()
    {
        return CircleIdx;
    }

    public void setCircleIdx(int circleIdx)
    {
        CircleIdx = circleIdx;
    }

    public int getCreateTimeNum()
    {
        return CreateTimeNum;
    }

    public void setCreateTimeNum(int createTimeNum)
    {
        CreateTimeNum = createTimeNum;
    }

    public Date getCreateTime()
    {
        return CreateTime;
    }

    public void setCreateTime(Date createTime)
    {
        CreateTime = createTime;
    }

    public List<Object> getRounds()
    {
        return Rounds;
    }

    public void setRounds(List<Object> rounds)
    {
        Rounds = rounds;
    }

    public List<Integer> getScroes()
    {
        return Scroes;
    }

    public void setScroes(List<Integer> scroes)
    {
        Scroes = scroes;
    }

    public List<Integer> getInitCards()
    {
        return initCards;
    }

    public void setInitCards(List<Integer> initCards)
    {
        this.initCards = initCards;
    }
}
