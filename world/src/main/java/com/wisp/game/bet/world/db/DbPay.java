package com.wisp.game.bet.world.db;

import com.wisp.game.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbPay extends DbBase {

    public static DbPay Instance;

    public DbPay() {
        Instance = this;
    }
}
