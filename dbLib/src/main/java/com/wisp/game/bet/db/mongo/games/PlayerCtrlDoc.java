package com.wisp.game.bet.db.mongo.games;

import com.mongodb.client.result.UpdateResult;
import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import com.wisp.game.bet.sshare.convert.TimeInt;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import java.util.ArrayList;
import java.util.List;

@Document(collection = "PlayerCtrl")
public class PlayerCtrlDoc extends BaseMongoDoc {

    @Field( targetType = FieldType.INT32) private int Key;
    @Field( targetType = FieldType.INT32) private int PlayerId;
    @Field( targetType = FieldType.INT32) private int Type;
    @Field( targetType = FieldType.DOUBLE) private double Rate;
    @Field( targetType = FieldType.INT32) private int CtrlGold;
    @Field( targetType = FieldType.INT32) private int CurrentGold;
    @Field( targetType = FieldType.INT32) private TimeInt StartTime;
    @Field( targetType = FieldType.INT32) private TimeInt EndTime;
    private List<Integer> Games;

    @Transient private int  check_time;

    public PlayerCtrlDoc() {
         StartTime = new TimeInt();
         EndTime = new TimeInt();
         Games = new ArrayList<>();
    }

    public int getKey() {
        return Key;
    }

    public void setKey(int key) {
        Key = key;
    }

    public int getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(int playerId) {
        PlayerId = playerId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public double getRate() {
        return Rate;
    }

    public void setRate(double rate) {
        Rate = rate;
    }

    public int getCtrlGold() {
        return CtrlGold;
    }

    public void setCtrlGold(int ctrlGold) {
        CtrlGold = ctrlGold;
    }

    public int getCurrentGold() {
        return CurrentGold;
    }

    public void setCurrentGold(int currentGold) {
        CurrentGold = currentGold;
    }

    public TimeInt getStartTime() {
        return StartTime;
    }

    public void setStartTime(TimeInt startTime) {
        StartTime = startTime;
    }

    public TimeInt getEndTime() {
        return EndTime;
    }

    public void setEndTime(TimeInt endTime) {
        EndTime = endTime;
    }

    public List<Integer> getGames() {
        return Games;
    }

    public void setGames(List<Integer> games) {
        Games = games;
    }

    public int getCheck_time() {
        return check_time;
    }

    public void setCheck_time(int check_time) {
        this.check_time = check_time;
    }


    public boolean store_game_object(MongoTemplate mongoTemplate)
    {
        Criteria criteria = Criteria.where("PlayerId").is(PlayerId).and("Key").is(Key);

        Update update = new Update();
        update.set("CurrentGold",CurrentGold);

        UpdateResult updateResult =  mongoTemplate.updateFirst(Query.query(criteria),update,this.getClass());

        if( updateResult.getModifiedCount() < 1  )
        {
             return false;
        }

        return true;
    }
}
