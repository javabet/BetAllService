package com.wisp.game.bet.db.mongo.games.doc;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.stereotype.Component;


@Document(collection = "GameRoomSet")
public class GameRoomSetDoc {


     @Field(targetType = FieldType.INT32) private int AgentId;
     @Field(targetType = FieldType.INT32) private int GameId;
     @Field(targetType = FieldType.INT32) private int RoomId;
     @Field(targetType = FieldType.INT32) private int Type;             //1：新增 2：停用房间
     @Field(targetType = FieldType.INT32) private int ServerId;

    @Field(targetType = FieldType.INT32)private  int TemplateId;
    @Field(targetType = FieldType.INT32)private int RoomNameType;
     private String RoomIDTxt;

    //int templateId = mongo_helper::instance().get_number_int(view["TemplateId"]);
    //int roomNameType = mongo_helper::instance().get_number_int(view["RoomNameType"]);
    //std::string roomIDTxt = mongo_helper::instance().get_string_field(view["RoomIDTxt"]);


    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public int getGameId() {
        return GameId;
    }

    public void setGameId(int gameId) {
        GameId = gameId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getServerId() {
        return ServerId;
    }

    public void setServerId(int serverId) {
        ServerId = serverId;
    }


    public int getTemplateId() {
        return TemplateId;
    }

    public void setTemplateId(int templateId) {
        TemplateId = templateId;
    }

    public int getRoomNameType() {
        return RoomNameType;
    }

    public void setRoomNameType(int roomNameType) {
        RoomNameType = roomNameType;
    }

    public String getRoomIDTxt() {
        return RoomIDTxt;
    }

    public void setRoomIDTxt(String roomIDTxt) {
        RoomIDTxt = roomIDTxt;
    }


    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int roomId) {
        RoomId = roomId;
    }
}
