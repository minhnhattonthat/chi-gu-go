package com.nhatton.ggtalkvn;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.nhatton.ggtalkvn.data.DaoMaster;
import com.nhatton.ggtalkvn.data.DaoSession;

import org.greenrobot.greendao.database.Database;

import io.fabric.sdk.android.Fabric;

public class ThisApp extends MultiDexApplication {

    public static final String DATABASE_NAME = "Soundv1.db";
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DATABASE_NAME);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
