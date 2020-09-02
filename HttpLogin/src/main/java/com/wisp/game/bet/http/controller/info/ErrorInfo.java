package com.wisp.game.bet.http.controller.info;

public class ErrorInfo {
    private String AccountName;
    private int oldPlayId;
    private int newPlayerId;

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public int getOldPlayId() {
        return oldPlayId;
    }

    public void setOldPlayId(int oldPlayId) {
        this.oldPlayId = oldPlayId;
    }

    public int getNewPlayerId() {
        return newPlayerId;
    }

    public void setNewPlayerId(int newPlayerId) {
        this.newPlayerId = newPlayerId;
    }
}
