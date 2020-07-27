package com.wisp.game.bet.world.db.mongo.account.interfaceService;

import com.wisp.game.bet.world.db.mongo.account.info.AccountTableInfo;

public interface AcountService {
    AccountTableInfo findByAccount(String account);
}
