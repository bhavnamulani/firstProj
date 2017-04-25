package com.androidframework.sqlite.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.androidframework.entities.ChatMessage;
import com.androidframework.entities.UserDO;
import com.androidframework.sqlite.framework.DatabaseManager;
import com.androidframework.sqlite.framework.DatabaseTemplate;

import java.util.ArrayList;

/**
 * Created by Administrator on 4/18/2017.
 */
public class UserTable implements DatabaseTemplate {
    private static final String TAG = UserTable.class.getSimpleName();

    public static final String USER_TABLE = "USER_TABLE";
    private final String COL_NAME = "name";
    private final String COL_USER_NAME = "username";
    private final String COL_EMAIL = "email";
    private final String COL_LAST_MSG_RECEIVED = "lastmsgreceived";
    private final String COL_UNREAD_MSG_COUNT = "unreadmsgcount";

    private final String[] PROJECTION = new String[]{
            COL_NAME, COL_USER_NAME, COL_EMAIL, COL_LAST_MSG_RECEIVED, COL_UNREAD_MSG_COUNT};

    private final String[] COLUMN_TYPE = new String[]{
            TEXT, TEXT, TEXT, TEXT, INTEGER};

    private DatabaseManager mDbManager;

    public UserTable(DatabaseManager databaseManager) {
        this.mDbManager = databaseManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + USER_TABLE + " (";
        for (int i = 0; i < PROJECTION.length; i++) {
            if (i < (PROJECTION.length - 1))
                sql += PROJECTION[i] + COLUMN_TYPE[i] + ",";
            else
                sql += PROJECTION[i] + COLUMN_TYPE[i] + ");";
        }
        db.execSQL(sql);
        Log.d(TAG, "onCreate: User table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    public void insertUser(UserDO userDO) {
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, userDO.getName());
        contentValues.put(COL_USER_NAME, userDO.getUsername());
        contentValues.put(COL_EMAIL, userDO.getEmail());
        db.insert(USER_TABLE, null, contentValues);
        Log.d(TAG, "insert##### " + userDO.getName());
    }

    public void insertAllUser(ArrayList<UserDO> userDOsList) {
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        for (int i = 0; i < userDOsList.size(); i++) {
            UserDO userDO = userDOsList.get(i);
            if (!checkIfUserAlreadyExist(userDO.getUsername())) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COL_NAME, userDO.getName());
                contentValues.put(COL_USER_NAME, userDO.getUsername());
                contentValues.put(COL_EMAIL, userDO.getEmail());
                db.insert(USER_TABLE, null, contentValues);
                Log.d(TAG, "insert##### " + userDO.getName());
            } else {
                updateUser(userDO);
            }
        }
    }

    private void updateUser(UserDO userDO) {
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, userDO.getName());
        contentValues.put(COL_USER_NAME, userDO.getUsername());
        contentValues.put(COL_EMAIL, userDO.getEmail());
        db.update(USER_TABLE, contentValues, COL_USER_NAME + " = ? ", new String[]{userDO.getUsername()});
        Log.d(TAG, "update##### " + userDO.getName());
    }

    private boolean checkIfUserAlreadyExist(String username) {
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " + COL_USER_NAME + " ='"
                + username + "'", null);

        if (c.moveToFirst()) {
            return true;
        }
        return false;
    }

    public ArrayList<UserDO> getAllUser() {
        ArrayList<UserDO> userDOArrayList = new ArrayList<UserDO>();
        String selectQuery = "SELECT  * FROM " + USER_TABLE;

        SQLiteDatabase db = mDbManager.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserDO userDO = new UserDO();
                userDO.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
                userDO.setUsername(cursor.getString(cursor.getColumnIndex(COL_USER_NAME)));
                userDO.setEmail(cursor.getString(cursor.getColumnIndex(COL_EMAIL)));
                userDO.setLastMessage(cursor.getString(cursor.getColumnIndex(COL_LAST_MSG_RECEIVED)));
                userDO.setUnreadMsgCount(cursor.getInt(cursor.getColumnIndex(COL_UNREAD_MSG_COUNT)));
                userDOArrayList.add(userDO);
            } while (cursor.moveToNext());
        }

        Log.d(TAG, "getAllUser: " + userDOArrayList.size());
        return userDOArrayList;
    }

    public void deleteAllUsers() {

    }

    public void deleteUser(String username) {

    }

    public int getUnreadMessageCount(String fromUserId) {
        String selectQuery = "SELECT  * FROM " + USER_TABLE
                + " WHERE " + COL_USER_NAME + " = '" + fromUserId + "'";
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                return cursor.getInt(cursor.getColumnIndex(COL_UNREAD_MSG_COUNT));
            } while (cursor.moveToNext());
        }
        return 0;
    }

    public void updateLastMsgAndMsgCount(String fromUserId, String lastMsg, int messageCount) {
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LAST_MSG_RECEIVED, lastMsg);
        contentValues.put(COL_UNREAD_MSG_COUNT, messageCount);
        int count = db.update(USER_TABLE, contentValues, COL_USER_NAME + " = ? ", new String[]{fromUserId});
        Log.d(TAG, "updateLastMsgAndMsgCount: NO of record updated >>" + count);
    }

    public void updateMsgCount(String fromUserId, int messageCount) {
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_UNREAD_MSG_COUNT, messageCount);
        int count = db.update(USER_TABLE, contentValues, COL_USER_NAME + " = ? ", new String[]{fromUserId});
        Log.d(TAG, "updateMsgCount: NO of record updated >>" + count);
    }

    public void updateLastMsg(String fromUserId, String lastMsg) {
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LAST_MSG_RECEIVED, lastMsg);
        int count = db.update(USER_TABLE, contentValues, COL_USER_NAME + " = ? ", new String[]{fromUserId});
        Log.d(TAG, "updateLastMsg: NO of record updated >>" + count);
    }

}
