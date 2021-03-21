package com.wisp.game.bet.http.controller.xhGame.info;

public class PlayermentInfoResponse
{
    private String payUrl;
    private String orderId;

    public String getPayUrl()
    {
        return payUrl;
    }

    public void setPayUrl(String payUrl)
    {
        this.payUrl = payUrl;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }
}
