package com.wisp.game.bet.world.db;

import com.wisp.game.bet.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbGame extends DbBase {

    public static DbGame Instance;

    public DbGame() {
        Instance = this;
    }
}
