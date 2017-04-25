package com.androidframework.sqlite.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.androidframework.entities.ChatMessage;
import com.androidframework.entities.Status;
import com.androidframework.entities.UserType;
import com.androidframework.sqlite.framework.DatabaseManager;
import com.androidframework.sqlite.framework.DatabaseTemplate;

import java.util.ArrayList;

/**
 * Created by Administrator on 4/18/2017.
 */
public class MsgTable implements DatabaseTemplate {

    private static final String TAG = MsgTable.class.getSimpleName();

    private DatabaseManager mDbManager;

    public static final String MSG_TABLE = "msg_table";
    private final String COL_MSG_BODY = "msg_body";
    private final String COL_FROM_USER = "from_user";
    private final String COL_TO_USER = "to_user";
    private final String COL_TIME = "time";
    private final String COL_READ = "read";
    private final String COL_USER_TYPE = "usertype";
    private final String COL_MSG_STATUS = "msgstatus";

    public MsgTable(DatabaseManager databaseManager) {
        this.mDbManager = databaseManager;
    }

    private final String[] PROJECTION = new String[]{
            COL_MSG_BODY, COL_FROM_USER, COL_TO_USER, COL_TIME, COL_READ, COL_USER_TYPE, COL_MSG_STATUS};


    private final String[] COLUMN_TYPE = new String[]{
            TEXT, TEXT, TEXT, TEXT, BOOLEAN, INTEGER, INTEGER};

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + MSG_TABLE + " (";
        for (int i = 0; i < PROJECTION.length; i++) {
            if (i < (PROJECTION.length - 1))
                sql += PROJECTION[i] + COLUMN_TYPE[i] + ",";
            else
                sql += PROJECTION[i] + COLUMN_TYPE[i] + ");";
        }
        db.execSQL(sql);
        Log.d(TAG, "onCreate: Msg table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MSG_TABLE);
        onCreate(db);
    }

    public void insertMessages(ChatMessage chatMessage) {
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MSG_BODY, chatMessage.getMessageText());
        contentValues.put(COL_FROM_USER, chatMessage.getFromUser());
        contentValues.put(COL_TO_USER, chatMessage.getToUser());
        contentValues.put(COL_TIME, chatMessage.getMessageTime());
        contentValues.put(COL_READ, chatMessage.isRead());

        if (chatMessage.getUserType() == UserType.OTHER) {
            contentValues.put(COL_USER_TYPE, 0);
        } else {
            contentValues.put(COL_USER_TYPE, 1);
        }

        if (chatMessage.getMessageStatus() == Status.SENT) {
            contentValues.put(COL_MSG_STATUS, 0);
        } else {
            contentValues.put(COL_MSG_STATUS, 1);
        }

        db.insert(MSG_TABLE, null, contentValues);
        Log.d(TAG, "insert<<<< " + chatMessage.getMessageText());

    }

    public ArrayList<ChatMessage> getMessages(String userID) {
        String selectQuery = "SELECT  * FROM " + MSG_TABLE + " WHERE "
                + COL_FROM_USER + " = '" + userID + "' OR " + COL_TO_USER + "='" + userID + "'";

        ArrayList<ChatMessage> chatMessagesList = new ArrayList<>();

        SQLiteDatabase db = mDbManager.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessageText(cursor.getString(cursor.getColumnIndex(COL_MSG_BODY)));
                chatMessage.setFromUser(cursor.getString(cursor.getColumnIndex(COL_FROM_USER)));
                chatMessage.setToUser(cursor.getString(cursor.getColumnIndex(COL_TO_USER)));
                chatMessage.setMessageTime(cursor.getLong(cursor.getColumnIndex(COL_TIME)));

                int userType = cursor.getInt(cursor.getColumnIndex(COL_USER_TYPE));
                if (userType == 0) {
                    chatMessage.setUserType(UserType.OTHER);
                } else {
                    chatMessage.setUserType(UserType.SELF);
                }

                int msgStatus = cursor.getInt(cursor.getColumnIndex(COL_MSG_STATUS));
                if (msgStatus == 0) {
                    chatMessage.setMessageStatus(Status.SENT);
                } else {
                    chatMessage.setMessageStatus(Status.DELIVERED);
                }

//                chatMessage.setRead(cursor.get(cursor.getColumnIndex(COL_TIME)));
                chatMessagesList.add(chatMessage);
            } while (cursor.moveToNext());
        }

        Log.d(TAG, "GetMessages: " + chatMessagesList.size());
        return chatMessagesList;
    }


}
