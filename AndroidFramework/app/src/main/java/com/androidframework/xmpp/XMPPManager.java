package com.androidframework.xmpp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.androidframework.application.MyApplication;
import com.androidframework.constants.Constants;
import com.androidframework.entities.ChatMessage;
import com.androidframework.entities.Status;
import com.androidframework.entities.UserType;
import com.androidframework.pref.SharedPref;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Administrator on 4/19/2017.
 */
public class XMPPManager {
    public static XMPPManager sINSTANCE = null;
    private ProgressDialog loginDailog = null;
    protected XMPPConnection connection;
    private ArrayList<XMPPMessageCallback> xmppMessageCallbackList = new ArrayList<>();


    private XMPPManager() {
    }

    public static XMPPManager getINSTANCE() {
        if (sINSTANCE == null) {
            sINSTANCE = new XMPPManager();
        }
        return sINSTANCE;
    }


    public void connectXMPP(final XMPPConnectCallback xmppConnectCallback,
                            final Activity activity,
                            final String userName, final String pwd,
                            boolean isPopUpLoadingRequired) {

        if (!MyApplication.getInstance().checkConnection(activity)) {
            return;
        }
        try {
            if (isPopUpLoadingRequired) {
                loginDailog = ProgressDialog.show(activity,
                        "Connecting...", "Please wait...", false);
            }

            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    // Create a connection
                    ConnectionConfiguration connConfig = new ConnectionConfiguration(
                            Constants.HOST, Constants.PORT, Constants.SERVICE);
                    final XMPPConnection connection = new XMPPConnection(connConfig);


                    try {
                        connection.connect();
                 /*   Log.i("XMPPChatDemoActivity",
                            "Connected to " + connection.getHost());*/
                    } catch (XMPPException ex) {
                 /*   Log.e("XMPPChatDemoActivity", "Failed to connect to "
                            + connection.getHost());*/

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Server Error.",
                                        Toast.LENGTH_SHORT)
                                        .show();
                                if (xmppConnectCallback != null)
                                    xmppConnectCallback.onConnectXMPPResponse(false, userName, pwd);
                            }
                        });

                        Log.e("XMPPChatDemoActivity", ex.toString());
                        setConnection(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {

                        //SASLAuthentication.supportSASLMechanism("PLAIN", 0);
                        String user = userName;
                        if (!user.contains("@test-pc")) {
                            user = user + "@test-pc";
                        }
                        connection.login(user, pwd);
                        Log.i("XMPPChatDemoActivity",
                                "Logged in as " + connection.getUser());

                        // See if you are authenticated
                        System.out.println("Authenticated or not" + connection.isAuthenticated());

                        // Set the status to available
                        Presence presence = new Presence(Presence.Type.available);
                        connection.sendPacket(presence);
                        setConnection(connection);


                        Roster roster = connection.getRoster();
                        Collection<RosterEntry> entries = roster.getEntries();
                        for (RosterEntry entry : entries) {
                            Log.d("XMPPChatDemoActivity",
                                    "--------------------------------------");
                            Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
                            Log.d("XMPPChatDemoActivity",
                                    "User: " + entry.getUser());
                            Log.d("XMPPChatDemoActivity",
                                    "Name: " + entry.getName());
                            Log.d("XMPPChatDemoActivity",
                                    "Status: " + entry.getStatus());
                            Log.d("XMPPChatDemoActivity",
                                    "Type: " + entry.getType());
                            Presence entryPresence = roster.getPresence(entry
                                    .getUser());

                            Log.d("XMPPChatDemoActivity", "Presence Status: "
                                    + entryPresence.getStatus());
                            Log.d("XMPPChatDemoActivity", "Presence Type: "
                                    + entryPresence.getType());
                            Presence.Type type = entryPresence.getType();
                            if (type == Presence.Type.available)
                                Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
                            Log.d("XMPPChatDemoActivity", "Presence : "
                                    + entryPresence);

                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (xmppConnectCallback != null)
                                    xmppConnectCallback.onConnectXMPPResponse(true, userName, pwd);
                            }
                        });

                    } catch (XMPPException ex) {
                        Log.e("XMPPChatDemoActivity", "Failed to log in as "
                                + userName);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Username or password is " +
                                        "incorrect, " +
                                        "Please try again !!", Toast.LENGTH_SHORT).show();

                                if (xmppConnectCallback != null)
                                    xmppConnectCallback.onConnectXMPPResponse(false, userName, pwd);
                            }
                        });

                        Log.e("XMPPChatDemoActivity", ex.toString());
                        setConnection(null);
                    }
                    if (loginDailog != null) {
                        loginDailog.dismiss();
                    }

                }
            });
            t.start();
            if (loginDailog != null) {
                loginDailog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by Settings dialog when a connection is establised with the XMPP
     * server
     *
     * @param connection
     */
    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
        refreshPacketListener();
    }

    private void refreshPacketListener() {
        if (this.connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            this.connection.addPacketListener(packetListener, filter);
        }
    }

    private PacketListener packetListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            final Message message = (Message) packet;
            if (message.getBody() != null) {
                final String fromName = StringUtils.parseBareAddress(message
                        .getFrom());
                Log.i("XMPPChatDemoActivity", "Text Recieved " + message.getBody()
                        + " from " + fromName);
                Log.d("AbstractBaseActivity", "processPacket: " + this);

                addIncomingMsgToDatabase(fromName, message.getBody());

                if (xmppMessageCallbackList != null) {
                    for (XMPPMessageCallback xmppMessageCallback : xmppMessageCallbackList) {
                        xmppMessageCallback.onMessageReceived(fromName, message.getBody());
                    }
                }

            }
        }
    };

    public void sendMessage(String userId, String messageText) {
        if (connection != null) {
            Message msg = new Message(userId, Message.Type.chat);
            msg.setBody(messageText);
            addOutgoingMsgToDatabase(userId, messageText);
            connection.sendPacket(msg);
        }
    }


    private void addOutgoingMsgToDatabase(String toUserId, String messageText) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageText(messageText);
        chatMessage.setToUser(toUserId);

        SharedPref sharedPref = MyApplication.getInstance().getSharedPref();
        chatMessage.setFromUser(sharedPref.getString(SharedPref.KEY_USER_NAME));
        chatMessage.setMessageStatus(Status.SENT);
        chatMessage.setUserType(UserType.OTHER);
        chatMessage.setMessageTime(new Date().getTime());
        chatMessage.setRead(true);

        MyApplication.getInstance().getDatabaseManager().getMessageTable().insertMessages(chatMessage);
    }

    private void addIncomingMsgToDatabase(String toUserId, String messageText) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageText(messageText);
        SharedPref sharedPref = MyApplication.getInstance().getSharedPref();
        chatMessage.setToUser(sharedPref.getString(SharedPref.KEY_USER_NAME));
        chatMessage.setFromUser(toUserId);
        chatMessage.setMessageStatus(Status.SENT);
        chatMessage.setUserType(UserType.SELF);
        chatMessage.setMessageTime(new Date().getTime());
        chatMessage.setRead(false);
        MyApplication.getInstance().getDatabaseManager().getMessageTable().insertMessages(chatMessage);
    }

    public void registerXMPPCallback(XMPPMessageCallback xmppMessageCallback) {
        xmppMessageCallbackList.add(xmppMessageCallback);
    }

    public void unregisterXMPPCallback(XMPPMessageCallback xmppMessageCallback) {
        xmppMessageCallbackList.remove(xmppMessageCallback);
    }
}
