package com.androidframework.screens.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.androidframework.R;
import com.androidframework.application.MyApplication;
import com.androidframework.base_classes.AbstractBaseActivity;
import com.androidframework.constants.Constants;
import com.androidframework.controllers.ActivityController;
import com.androidframework.entities.ChatMessage;
import com.androidframework.entities.Status;
import com.androidframework.entities.UserDO;
import com.androidframework.entities.UserType;
import com.androidframework.pref.SharedPref;
import com.androidframework.utils.AndroidUtilities;
import com.androidframework.utils.NotificationCenter;
import com.androidframework.volly.ErrorResponse;
import com.androidframework.widgets.Emoji;
import com.androidframework.widgets.EmojiView;
import com.androidframework.widgets.SizeNotifierRelativeLayout;
import com.androidframework.xmpp.XMPPManager;
import com.androidframework.xmpp.XMPPMessageCallback;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AbstractBaseActivity implements
        SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate,
        NotificationCenter.NotificationCenterDelegate, XMPPMessageCallback {


    private ListView chatListView;
    private EditText chatEditText1;
    private ArrayList<ChatMessage> chatMessages;
    private ImageView enterChatView1, emojiButton;
    private ChatListAdapter listAdapter;
    private EmojiView emojiView;
    private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
    private boolean showingEmoji;
    private int keyboardHeight;
    private boolean keyboardVisible;
    private WindowManager.LayoutParams windowLayoutParams;

    private UserDO userDO;


    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if (v == chatEditText1) {
                    sendMessage(editText.getText().toString(), UserType.OTHER);
                }

                chatEditText1.setText("");

                return true;
            }
            return false;

        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == enterChatView1) {
                sendMessage(chatEditText1.getText().toString(), UserType.OTHER);
            }

            chatEditText1.setText("");

        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initActionBarView();
        userDO = getIntent().getParcelableExtra(ActivityController.KEY_PARCEL_EXTRAS);
        setTitle(userDO.getUsername());
        setSubTitle(userDO.getEmail());

        hideRightMostButton();

        AndroidUtilities.statusBarHeight = getStatusBarHeight();
        chatMessages = new ArrayList<>();
        chatListView = (ListView) findViewById(R.id.chat_list_view);
        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);
        // Hide the emoji on click of edit text
        chatEditText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingEmoji)
                    hideEmojiPopup();
            }
        });
        emojiButton = (ImageView) findViewById(R.id.emojiButton);
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmojiPopup(!showingEmoji);
            }
        });
        listAdapter = new ChatListAdapter(chatMessages, this);
        chatListView.setAdapter(listAdapter);
        chatEditText1.setOnKeyListener(keyListener);
        enterChatView1.setOnClickListener(clickListener);
        chatEditText1.addTextChangedListener(watcher1);
        sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.chat_layout);
        sizeNotifierRelativeLayout.delegate = this;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);

        XMPPManager.getINSTANCE().registerXMPPCallback(this);
        //loading chat message which are stored offline
        refreshMsgList();

        //resetting unread message count to zero
        MyApplication.getInstance().getDatabaseManager()
                .getUserTable().updateMsgCount(userDO.getUsername(), 0);
    }

    @Override
    public void onRightMostButtonClick() {

    }

    @Override
    public void onPermissionResult(int requestCode, boolean isGranted, Object extras) {

    }

    @Override
    public void onMessageReceived(final String fromUserName, final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(">>control");
                String openChatUserName = userDO.getUsername();
                if (fromUserName.equals(openChatUserName)) {
                    refreshMsgList();
                }
            }
        });

    }

    private void refreshMsgList() {
        String openChatUserName = userDO.getUsername();
        chatMessages = MyApplication.getInstance().getDatabaseManager()
                .getMessageTable().getMessages(openChatUserName);

        if (listAdapter != null) {
            listAdapter.refreshAapter(chatMessages);
            chatListView.setSelection(chatMessages.size() - 1);
        }
    }

    private void sendMessage(final String messageText, final UserType userType) {
        if (messageText.trim().length() == 0)
            return;

        XMPPManager.getINSTANCE().sendMessage(userDO.getUsername(), messageText);
        MyApplication.getInstance().getDatabaseManager()
                .getUserTable().updateLastMsgAndMsgCount(userDO.getUsername(), messageText, 0);
        refreshMsgList();
    }


    private Activity getActivity() {
        return this;
    }


    /**
     * Show or hide the emoji popup
     *
     * @param show
     */
    private void showEmojiPopup(boolean show) {
        showingEmoji = show;

        if (show) {
            if (emojiView == null) {
                if (getActivity() == null) {
                    return;
                }
                emojiView = new EmojiView(getActivity());

                emojiView.setListener(new EmojiView.Listener() {
                    public void onBackspace() {
                        chatEditText1.dispatchKeyEvent(new KeyEvent(0, 67));
                    }

                    public void onEmojiSelected(String symbol) {
                        int i = chatEditText1.getSelectionEnd();
                        if (i < 0) {
                            i = 0;
                        }
                        try {
                            CharSequence localCharSequence = Emoji.replaceEmoji(symbol,
                                    chatEditText1.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20));
                            chatEditText1.setText(chatEditText1.getText().insert(i, localCharSequence));
                            int j = i + localCharSequence.length();
                            chatEditText1.setSelection(j, j);
                        } catch (Exception e) {
                            Log.e(Constants.TAG, "Error showing emoji");
                        }
                    }
                });


                windowLayoutParams = new WindowManager.LayoutParams();
                windowLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                if (Build.VERSION.SDK_INT >= 21) {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                } else {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    windowLayoutParams.token = getActivity().getWindow().getDecorView().getWindowToken();
                }
                windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }

            final int currentHeight;

            if (keyboardHeight <= 0)
                keyboardHeight = MyApplication.getInstance().getSharedPreferences("emoji", 0)
                        .getInt("kbd_height", AndroidUtilities.dp(200));

            currentHeight = keyboardHeight;

            WindowManager wm = (WindowManager) MyApplication.getInstance()
                    .getSystemService(Activity.WINDOW_SERVICE);

            windowLayoutParams.height = currentHeight;
            windowLayoutParams.width = AndroidUtilities.displaySize.x;

            try {
                if (emojiView.getParent() != null) {
                    wm.removeViewImmediate(emojiView);
                }
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
            }

            try {
                wm.addView(emojiView, windowLayoutParams);
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
                return;
            }

            if (!keyboardVisible) {
                if (sizeNotifierRelativeLayout != null) {
                    sizeNotifierRelativeLayout.setPadding(0, 0, 0, currentHeight);
                }

                return;
            }

        } else {
            removeEmojiWindow();
            if (sizeNotifierRelativeLayout != null) {
                sizeNotifierRelativeLayout.post(new Runnable() {
                    public void run() {
                        if (sizeNotifierRelativeLayout != null) {
                            sizeNotifierRelativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                });
            }
        }


    }


    /**
     * Remove emoji window
     */
    private void removeEmojiWindow() {
        if (emojiView == null) {
            return;
        }
        try {
            if (emojiView.getParent() != null) {
                WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(emojiView);
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }


    /**
     * Hides the emoji popup
     */
    public void hideEmojiPopup() {
        if (showingEmoji) {
            showEmojiPopup(false);
        }
    }

    /**
     * Check if the emoji popup is showing
     *
     * @return
     */
    public boolean isEmojiPopupShowing() {
        return showingEmoji;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            if (emojiView != null) {
                emojiView.invalidateViews();
            }

            if (chatListView != null) {
                chatListView.invalidateViews();
            }
        }
    }

    @Override
    public void onSizeChanged(int height) {
        Rect localRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);

        WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService
                (Activity.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }

        if (height > AndroidUtilities.dp(50) && keyboardVisible) {
            keyboardHeight = height;
            MyApplication.getInstance().getSharedPreferences("emoji", 0).edit().putInt("kbd_height",
                    keyboardHeight).commit();
        }


        if (showingEmoji) {
            int newHeight = 0;

            newHeight = keyboardHeight;

            if (windowLayoutParams.width != AndroidUtilities.displaySize.x ||
                    windowLayoutParams.height != newHeight) {
                windowLayoutParams.width = AndroidUtilities.displaySize.x;
                windowLayoutParams.height = newHeight;

                wm.updateViewLayout(emojiView, windowLayoutParams);
                if (!keyboardVisible) {
                    sizeNotifierRelativeLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (sizeNotifierRelativeLayout != null) {
                                sizeNotifierRelativeLayout.setPadding(0, 0, 0,
                                        windowLayoutParams.height);
                                sizeNotifierRelativeLayout.requestLayout();
                            }
                        }
                    });
                }
            }
        }


        boolean oldValue = keyboardVisible;
        keyboardVisible = height > 0;
        if (keyboardVisible && sizeNotifierRelativeLayout.getPaddingBottom() > 0) {
            showEmojiPopup(false);
        } else if (!keyboardVisible && keyboardVisible != oldValue && showingEmoji) {
            showEmojiPopup(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        XMPPManager.getINSTANCE().unregisterXMPPCallback(this);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    /**
     * Get the system status bar height
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideEmojiPopup();
    }

    @Override
    public void onAPIHandlerResponse(int requestId, boolean isSuccess, Object result, ErrorResponse
            errorResponse) {

    }

    @Override
    public void onClick(View view) {

    }


}
