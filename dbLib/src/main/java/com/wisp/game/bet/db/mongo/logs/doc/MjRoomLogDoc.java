package com.wisp.game.bet.db.mongo.logs.doc;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

//房间基本信息
@Document("MjRoomLogDoc")
public class MjRoomLogDoc implements Serializable
{
    private static final long serialVersionUID = -7477872226667749181L;

    @Field(targetType = FieldType.INT32)private int RoomId;
    @Field(targetType = FieldType.INT32) private int CreateTimeNum;
    @Field(targetType = FieldType.DATE_TIME)private Date CreateTime;
    private List<Map<String,Object>> Players;
    private Object RoomRule;
    private Object Scores;

    @Field(targetType = FieldType.INT32)private int EndTimeNum;
    @Field(targetType = FieldType.DATE_TIME) private Date EndTime;

    public int getRoomId()
    {
        return RoomId;
    }

    public void setRoomId(int roomId)
    {
        RoomId = roomId;
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

    public List<Map<String, Object>> getPlayers()
    {
        return Players;
    }

    public void setPlayers(List<Map<String, Object>> players)
    {
        Players = players;
    }

    public Object getRoomRule()
    {
        return RoomRule;
    }

    public void setRoomRule(Object roomRule)
    {
        RoomRule = roomRule;
    }

    public Object getScores()
    {
        return Scores;
    }

    public void setScores(Object scores)
    {
        Scores = scores;
    }

    public int getEndTimeNum()
    {
        return EndTimeNum;
    }

    public void setEndTimeNum(int endTimeNum)
    {
        EndTimeNum = endTimeNum;
    }

    public Date getEndTime()
    {
        return EndTime;
    }

    public void setEndTime(Date endTime)
    {
        EndTime = endTime;
    }
}
