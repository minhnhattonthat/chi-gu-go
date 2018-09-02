package com.nhatton.ggtalkvn.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SoundDbService {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ROW_ID = "_id";

    private static final String TAG = "SoundDbService";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE = "create table IF NOT EXISTS sound_list" +
                    "(_id integer primary key autoincrement, description text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "sound_list";
    private static final int DATABASE_VERSION = 1;

    private final Context mContext;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param context the Context within which to work
     */
    public SoundDbService(Context context) {
        this.mContext = context;
    }

    /**
     * Open the sound database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public SoundDbService open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createSound(String description) {
        if (checkExist(description)) {
            return -1;
        } else {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_DESCRIPTION, description);
            return mDb.insert(DATABASE_TABLE, null, initialValues);
        }
    }

    private boolean checkExist(String description) {
        Cursor c = mDb.query(DATABASE_TABLE, new String[]{KEY_ROW_ID, KEY_DESCRIPTION},
                KEY_DESCRIPTION + "=" + "'" + description + "'", null, null, null, null);
        int count = c.getCount();
        c.close();
        return (count > 0);
    }

    public boolean deleteSound(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllSounds() {
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROW_ID, KEY_DESCRIPTION},
                null, null, null, null, null);
    }

    public Cursor fetchSound(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(DATABASE_TABLE, new String[]{KEY_ROW_ID, KEY_DESCRIPTION},
                KEY_ROW_ID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}
