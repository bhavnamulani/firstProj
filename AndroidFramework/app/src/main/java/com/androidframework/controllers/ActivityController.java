package com.androidframework.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidframework.screens.chat.ChatActivity;
import com.androidframework.screens.login.LoginActivity;
import com.androidframework.screens.signup.SignUpActivity;
import com.androidframework.screens.userslist.UsersListActivity;

/**
 * This class is the launch Activities
 * Singleton class
 */
public class ActivityController {

    //parcel key to get the data in other activity
    public static final String KEY_PARCEL_EXTRAS = "extras";

    public static final int ACTIVITY_SIGN_UP = 0;
    public static final int ACTIVITY_USERS_LIST = 1;
    public static final int ACTIVITY_CHAT_SCREEN = 2;
    public static final int ACTIVITY_SIGN_IN = 3;


    public static ActivityController instance;


    public static ActivityController getInstance() {
        if (instance == null) {
            instance = new ActivityController();
        }
        return instance;
    }


    private Intent formActivityIntent(@NonNull Context context, int activityId) {
        Intent intent = null;
        switch (activityId) {
            case ACTIVITY_SIGN_UP:
                intent = new Intent(context, SignUpActivity.class);
                break;
            case ACTIVITY_USERS_LIST:
                intent = new Intent(context, UsersListActivity.class);
                break;
            case ACTIVITY_CHAT_SCREEN:
                intent = new Intent(context, ChatActivity.class);
                break;
            case ACTIVITY_SIGN_IN:
                intent = new Intent(context, LoginActivity.class);
                break;
        }
        return intent;
    }


    public void handleEvent(@NonNull Context context, int activityId) {
        Intent intent = formActivityIntent(context, activityId);
        context.startActivity(intent);
    }


    public void handleEvent(@NonNull Context context, int activityId, String extras) {
        Intent intent = formActivityIntent(context, activityId);
        intent.putExtra(KEY_PARCEL_EXTRAS, extras);
        context.startActivity(intent);
    }

    public void handleEvent(@NonNull Context context, int activityId, int extras) {
        Intent intent = formActivityIntent(context, activityId);
        intent.putExtra(KEY_PARCEL_EXTRAS, extras);
        context.startActivity(intent);
    }

    public void handleEvent(@NonNull Context context, int activityId, boolean extras) {
        Intent intent = formActivityIntent(context, activityId);
        intent.putExtra(KEY_PARCEL_EXTRAS, extras);
        context.startActivity(intent);
    }

    public void handleEvent(@NonNull Context context, int activityId, @Nullable Parcelable extras) {
        Intent intent = formActivityIntent(context, activityId);
        intent.putExtra(KEY_PARCEL_EXTRAS, extras);
        context.startActivity(intent);
    }


}
