package com.wisp.game.bet.db.mongo.config.info;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("ServerList")
@TypeAlias("ServerList")
public class ServerListDoc implements Serializable {

    private int ServerId;

    private int ServerType;

    private String ServerIp;

    private int Status;

    public int getServerId() {
        return ServerId;
    }

    public void setServerId(int serverId) {
        ServerId = serverId;
    }

    public int getServerType() {
        return ServerType;
    }

    public void setServerType(int serverType) {
        ServerType = serverType;
    }

    public String getServerIp() {
        return ServerIp;
    }

    public void setServerIp(String serverIp) {
        ServerIp = serverIp;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
