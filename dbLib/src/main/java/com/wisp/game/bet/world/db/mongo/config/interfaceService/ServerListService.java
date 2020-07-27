package com.wisp.game.bet.world.db.mongo.config.interfaceService;

import com.wisp.game.bet.world.db.mongo.config.info.ServerList;

import java.util.List;

public interface ServerListService {
    List<ServerList> findAll();
}
