package com.wisp.game.bet.db.mongo.player.doc;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("OrderPlayerId")
public class OrderPlayerIdDoc implements Serializable {
    private int Pid;
    private int Index;

    public int getPid() {
        return Pid;
    }

    public void setPid(int pid) {
        Pid = pid;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }
}
