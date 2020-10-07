package com.wisp.game.bet.games.guanyuan.logic;

import java.util.*;

public class LogicTable {
    private Map<Integer,LogicPlayer> playerMap;
    private List<LogicPlayer> seats;
    private Set<Integer> gpsCheckSet;

    public LogicTable() {
        playerMap = new HashMap<>();
        seats = new ArrayList<>();
        gpsCheckSet = new HashSet<>();
    }
}
