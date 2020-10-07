package com.wisp.game.bet.games.guanyuan.mgr;

import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.IGameEngine;
import org.springframework.stereotype.Component;

@Component
public class GameEngine implements IGameEngine {
    @Override
    public boolean init_engine() {
        return false;
    }

    @Override
    public void heartbeat(double elapsed) {

    }

    @Override
    public void exit_engine() {

    }

    @Override
    public void notify_bot_state(boolean connected) {

    }

    @Override
    public boolean player_enter_game(GamePlayer gamePlayer, int roomId) {
        return false;
    }

    @Override
    public boolean player_leave_game(int playerid) {
        return false;
    }

    @Override
    public boolean player_leave_game(int playerid, boolean bforce) {
        return false;
    }

    @Override
    public boolean player_can_leave(int playerid) {
        return false;
    }

    @Override
    public int player_join_friend_game(GamePlayer gamePlayer, int friendId) {
        return 0;
    }

    @Override
    public void deduct_stock(int room_id, int gold) {

    }

    @Override
    public void zero_time_arrive() {

    }

    @Override
    public int get_gameid() {
        return 0;
    }

    @Override
    public void response_robot(int playerid, int tag) {

    }

    @Override
    public String get_game_type() {
        return null;
    }

    @Override
    public void set_room(int agentid, int roomid) {

    }

    @Override
    public void open_room(int agentid, boolean open) {

    }
}
