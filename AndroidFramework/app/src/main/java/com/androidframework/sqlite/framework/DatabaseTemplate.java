package com.androidframework.sqlite.framework;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 4/18/2017.
 */
public interface DatabaseTemplate {
    // Database Column Type
    String TEXT_PRIMARY_KEY = " TEXT PRIMARY KEY";
    String TEXT = " TEXT";
    String INTEGER = " INTEGER";
    String BOOLEAN = " BOOLEAN";

    public void onCreate(SQLiteDatabase db);

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);


}
