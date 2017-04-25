package com.androidframework.xmpp;

/**
 * Created by Administrator on 4/19/2017.
 */
public interface XMPPMessageCallback {
    void onMessageReceived(String fromUserName, String message);
}
