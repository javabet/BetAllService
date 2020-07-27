package com.wisp.game.bet.world.db;

import com.wisp.game.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbPlayer extends DbBase {

    public static DbPlayer Instance;

    public DbPlayer() {
        Instance = this;
    }
}
