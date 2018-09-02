package com.nhatton.ggtalkvn.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nhatton.ggtalkvn.ThisApp;
import com.nhatton.ggtalkvn.data.DaoSession;

public abstract class BaseActivity extends AppCompatActivity {

    protected DaoSession daoSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daoSession = ((ThisApp) getApplication()).getDaoSession();
    }
}
