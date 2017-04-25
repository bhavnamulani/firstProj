package com.androidframework.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 4/6/2017.
 */
public class UserDO implements Parcelable {

    private String username;
    private String password;
    private String email;
    private String name;

    private int unreadMsgCount = 0;
    private String lastMessage;

    public UserDO() {

    }

    protected UserDO(Parcel in) {
        username = in.readString();
        password = in.readString();
        email = in.readString();
        name = in.readString();
        unreadMsgCount = in.readInt();
        lastMessage = in.readString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final Creator<UserDO> CREATOR = new Creator<UserDO>() {
        @Override
        public UserDO createFromParcel(Parcel in) {
            return new UserDO(in);
        }

        @Override
        public UserDO[] newArray(int size) {
            return new UserDO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeInt(unreadMsgCount);
        parcel.writeString(lastMessage);

    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
