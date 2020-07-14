package com.wisp.game.db.account.interfaceService;

import com.wisp.game.db.account.info.AccountTableInfo;

public interface AcountService {
    AccountTableInfo findByAccount(String account);
}
