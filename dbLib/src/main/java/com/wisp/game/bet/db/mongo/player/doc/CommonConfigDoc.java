package com.wisp.game.bet.db.mongo.player.doc;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("common_config")
public class CommonConfigDoc implements Serializable {
    private static final long serialVersionUID = -2193338475840916538L;
    private String type;
    private int value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
