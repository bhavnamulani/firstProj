package com.androidframework.sqlite.framework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androidframework.sqlite.table.MsgTable;
import com.androidframework.sqlite.table.UserTable;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {

    public static final String VCHAT_DB = "VCHAT_DB";

    private ArrayList<DatabaseTemplate> tableList = new ArrayList<>();

    public DatabaseManager(Context context) {
        super(context, VCHAT_DB, null, 1);
        initTables();
    }

    private void initTables() {
        tableList.add(new MsgTable(this));
        tableList.add(new UserTable(this));
        //TODO add new tables here
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (DatabaseTemplate databaseTemplate : tableList) {
            databaseTemplate.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (DatabaseTemplate databaseTemplate : tableList) {
            databaseTemplate.onUpgrade(db, oldVersion, newVersion);
        }
    }

    public MsgTable getMessageTable() {
        return (MsgTable) tableList.get(0);
    }

    public UserTable getUserTable() {
        return (UserTable) tableList.get(1);
    }


//    public void addOrUpdateAirZoonHotSpot(AirZoonDo airZoonDo) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        Cursor c = db.rawQuery("SELECT * FROM " + AIRZOON_TABLE + " WHERE " + COL_ID + " ='"
// + airZoonDo.getId() + "'", null);
//        if (c.moveToFirst()) {
//            updateAirzoonHotspot(airZoonDo);
//        } else {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(COL_ID, airZoonDo.getId());
//            contentValues.put(COL_NAME, airZoonDo.getName());
//            contentValues.put(COL_ZIP, airZoonDo.getZip());
//            contentValues.put(COL_PHONE, airZoonDo.getPhone());
//            contentValues.put(COL_SPEED, airZoonDo.getSpeed());
//            contentValues.put(COL_IMAGE, airZoonDo.getImage());
//            contentValues.put(COL_ADDRESS_TWO, airZoonDo.getAddress2());
//            contentValues.put(COL_LONG, airZoonDo.getLng());
//            contentValues.put(COL_LAT, airZoonDo.getLat());
//            contentValues.put(COL_TYPE, airZoonDo.getType());
//            contentValues.put(COL_DATE, airZoonDo.getDate());
//            contentValues.put(COL_COUNTRY, airZoonDo.getCountry());
//            contentValues.put(COL_CITY, airZoonDo.getCity());
//            contentValues.put(COL_OPENING_TWO, airZoonDo.getOpening_two());
//            contentValues.put(COL_CATEGORY, airZoonDo.getCategory());
//            contentValues.put(COL_OPENING_ONE, airZoonDo.getOpening_one());
//            contentValues.put(COL_ADDRESS, airZoonDo.getAddress());
//            contentValues.put(COL_CAT_IMAGE, airZoonDo.getCategory_image());
//            contentValues.put(COL_FAV_COUNT, airZoonDo.getFav_count());
//            contentValues.put(COL_IS_FREE, airZoonDo.getIs_free());
//            contentValues.put(COL_FAVIOURATE, airZoonDo.isFaviourate());
//            db.insert(AIRZOON_TABLE, null, contentValues);
//
////            System.out.println(">>sid added>>" + airZoonDo.getId());
//        }
//    }
//
//    private boolean updateAirzoonHotspot(AirZoonDo airZoonDo) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_NAME, airZoonDo.getName());
//        contentValues.put(COL_ZIP, airZoonDo.getZip());
//        contentValues.put(COL_PHONE, airZoonDo.getPhone());
//        contentValues.put(COL_SPEED, airZoonDo.getSpeed());
//        contentValues.put(COL_IMAGE, airZoonDo.getImage());
//        contentValues.put(COL_ADDRESS_TWO, airZoonDo.getAddress2());
//        contentValues.put(COL_LONG, airZoonDo.getLng());
//        contentValues.put(COL_LAT, airZoonDo.getLat());
//        contentValues.put(COL_TYPE, airZoonDo.getType());
//        contentValues.put(COL_DATE, airZoonDo.getDate());
//        contentValues.put(COL_COUNTRY, airZoonDo.getCountry());
//        contentValues.put(COL_CITY, airZoonDo.getCity());
//        contentValues.put(COL_OPENING_TWO, airZoonDo.getOpening_two());
//        contentValues.put(COL_CATEGORY, airZoonDo.getCategory());
//        contentValues.put(COL_OPENING_ONE, airZoonDo.getOpening_one());
//        contentValues.put(COL_ADDRESS, airZoonDo.getAddress());
//        contentValues.put(COL_CAT_IMAGE, airZoonDo.getCategory_image());
//        contentValues.put(COL_FAV_COUNT, airZoonDo.getFav_count());
//        contentValues.put(COL_IS_FREE, airZoonDo.getIs_free());
////        contentValues.put(COL_FAVIOURATE, airZoonDo.isFaviourate());
//        db.update(AIRZOON_TABLE, contentValues, COL_ID + " = ? ", new String[]{airZoonDo.getId()});
//
////        System.out.println(">>sid updated >>" + airZoonDo.getId());
//
//        return true;
//    }
//
//    public boolean updateFav(AirZoonDo airZoonDo) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_FAVIOURATE, airZoonDo.isFaviourate());
//        int count = db.update(AIRZOON_TABLE, contentValues, COL_ID + " = ? ", new String[]{airZoonDo.getId()});
//
////        System.out.println(">>rec sid fav updated >>" + count);
//
//        return true;
//    }
//
//    public boolean removeAllFav() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_FAVIOURATE, false);
//        int count = db.update(AIRZOON_TABLE, contentValues, null, null);
//
////        System.out.println(">>rec sid fav updated count >>" + count);
//
//        return true;
//    }
//
//    public void deleteASpot(String airZoonId) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int count = db.delete(AIRZOON_TABLE, COL_ID + "=?", new String[]{airZoonId + ""});
////        System.out.println(">>sid deleted >> " + airZoonId + " >> " + count);
//    }
//
//    // Getting All Contacts
//    public ArrayList<AirZoonDo> getAllHotSpotList() {
//        ArrayList<AirZoonDo> hotSpotList = new ArrayList<AirZoonDo>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + AIRZOON_TABLE;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                AirZoonDo airZoonDo = new AirZoonDo();
//                airZoonDo.setId(cursor.getString(cursor.getColumnIndex(COL_ID)));
//                airZoonDo.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
//                airZoonDo.setZip(cursor.getString(cursor.getColumnIndex(COL_ZIP)));
//                airZoonDo.setPhone(cursor.getString(cursor.getColumnIndex(COL_PHONE)));
//                airZoonDo.setSpeed(cursor.getString(cursor.getColumnIndex(COL_SPEED)));
//                airZoonDo.setImage(cursor.getString(cursor.getColumnIndex(COL_IMAGE)));
//                airZoonDo.setAddress2(cursor.getString(cursor.getColumnIndex(COL_ADDRESS_TWO)));
//                airZoonDo.setLng(cursor.getString(cursor.getColumnIndex(COL_LONG)));
//                airZoonDo.setLat(cursor.getString(cursor.getColumnIndex(COL_LAT)));
//                airZoonDo.setType(cursor.getString(cursor.getColumnIndex(COL_TYPE)));
//                airZoonDo.setDate(cursor.getString(cursor.getColumnIndex(COL_DATE)));
//                airZoonDo.setCountry(cursor.getString(cursor.getColumnIndex(COL_COUNTRY)));
//                airZoonDo.setCity(cursor.getString(cursor.getColumnIndex(COL_CITY)));
//                airZoonDo.setOpening_two(cursor.getString(cursor.getColumnIndex(COL_OPENING_TWO)));
//                airZoonDo.setCategory(cursor.getString(cursor.getColumnIndex(COL_CATEGORY)));
//                airZoonDo.setOpening_one(cursor.getString(cursor.getColumnIndex(COL_OPENING_ONE)));
//                airZoonDo.setAddress(cursor.getString(cursor.getColumnIndex(COL_ADDRESS)));
//                airZoonDo.setCategory_image(cursor.getString(cursor.getColumnIndex(COL_CAT_IMAGE)));
//                airZoonDo.setFav_count(cursor.getString(cursor.getColumnIndex(COL_FAV_COUNT)));
//                airZoonDo.setIs_free(cursor.getString(cursor.getColumnIndex(COL_IS_FREE)));
//                airZoonDo.setFaviourate(cursor.getInt(cursor.getColumnIndex(COL_FAVIOURATE)) > 0);
//
//                hotSpotList.add(airZoonDo);
////                System.out.println(">>rec sid read>>" + airZoonDo.getId());
//            } while (cursor.moveToNext());
//        }
//
////        System.out.println(">>rec sid total read size>>" + hotSpotList.size());
//
//        return hotSpotList;
//    }
//
//    public int getTableCount() {
//        String countQuery = "SELECT  * FROM " + AIRZOON_TABLE;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        int cnt = cursor.getCount();
//        cursor.close();
//        return cnt;
//    }
}
