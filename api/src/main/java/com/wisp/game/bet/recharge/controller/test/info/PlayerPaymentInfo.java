package com.wisp.game.bet.recharge.controller.test.info;

public class PlayerPaymentInfo
{
    private boolean ownWebBrowser;
    private String orderId;
    private String payUrl;

    public boolean isOwnWebBrowser()
    {
        return ownWebBrowser;
    }

    public void setOwnWebBrowser(boolean ownWebBrowser)
    {
        this.ownWebBrowser = ownWebBrowser;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getPayUrl()
    {
        return payUrl;
    }

    public void setPayUrl(String payUrl)
    {
        this.payUrl = payUrl;
    }
}
