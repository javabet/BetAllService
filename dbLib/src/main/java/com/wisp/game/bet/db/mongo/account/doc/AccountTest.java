package com.wisp.game.bet.db.mongo.account.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document(collection = "AccountTest" )
public class AccountTest extends BaseMongoDoc {

    @Field(targetType = FieldType.INT32)
    private int Mid;

    @Field(targetType = FieldType.DATE_TIME)
    private int time;

    public int getMid() {
        return Mid;
    }

    public void setMid(int mid) {
        Mid = mid;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
