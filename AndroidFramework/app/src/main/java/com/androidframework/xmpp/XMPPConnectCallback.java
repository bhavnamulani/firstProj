package com.androidframework.xmpp;

public interface XMPPConnectCallback {
    void onConnectXMPPResponse(boolean isLoginSuccess, String userName, String pwd);
}
