package com.wisp.game.bet.recharge.common;

public enum UicCacheKey
{
    OAUTH2_REQUEST_TOKEN,
    OAUTH2_USERID_TOKEN,
    OAUTH2_TOKEN_INFO
    ;
    private final String value;
    UicCacheKey() {
        this.value = "UIC:" + name();
    }


    public String key() {
        return value;
    }

    public String key(Object...params) {
        StringBuilder key = new StringBuilder(value);
        if (params != null && params.length > 0) {
            for (Object param : params) {
                key.append(':');
                key.append(String.valueOf(param));
            }
        }
        return key.toString();
    }
}
