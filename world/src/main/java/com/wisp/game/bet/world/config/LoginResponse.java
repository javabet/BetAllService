package com.wisp.game.bet.world.config;

public class LoginResponse extends SendResponseData<LoginResponse.LoginResposeItem>  {

    public class LoginResposeItem
    {
        private String GameUrl;

        private double Balance;

        private String PlayerName;

        private int PlayerId;


        public String getGameUrl() {
            return GameUrl;
        }

        public void setGameUrl(String gameUrl) {
            GameUrl = gameUrl;
        }

        public double getBalance() {
            return Balance;
        }

        public void setBalance(double balance) {
            Balance = balance;
        }

        public String getPlayerName() {
            return PlayerName;
        }

        public void setPlayerName(String playerName) {
            PlayerName = playerName;
        }

        public int getPlayerId() {
            return PlayerId;
        }

        public void setPlayerId(int playerId) {
            PlayerId = playerId;
        }
    }
}
