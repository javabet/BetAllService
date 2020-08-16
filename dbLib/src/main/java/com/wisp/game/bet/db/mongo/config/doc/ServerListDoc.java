package com.wisp.game.bet.db.mongo.config.doc;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;

@Document("ServerList")
@TypeAlias("ServerList")
public class ServerListDoc implements Serializable {

    @Field( targetType = FieldType.INT32)
    private int ServerId;

    @Field( targetType = FieldType.INT32)
    private int ServerType;

    private String ServerIp;

    @Field( targetType = FieldType.INT32)
    private int Status;


    private String Host;
    private String HostWeb;

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

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public String getHostWeb() {
        return HostWeb;
    }

    public void setHostWeb(String hostWeb) {
        HostWeb = hostWeb;
    }
}
