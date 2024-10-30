package com.eventoscercanos.osmdroid;

import android.content.Context;

import com.eventoscercanos.osmdroid.syncSQLiteMySQL.provider.SQLiteBD;

/**
 * Created by Romi on 29/01/2018.
 */
public class BdSingleton {

    private static BdSingleton mBD;
    private SQLiteBD sqLiteBD;


    private BdSingleton(Context context){
       sqLiteBD = new SQLiteBD(context, "EVENTOSCERCANOS.db", null, 4);

    }

    public static synchronized BdSingleton getInstance(Context context) {
        if (mBD == null) {
            mBD = new BdSingleton(context);
        }
        return mBD;
    }

    public SQLiteBD getSqLiteBD(){

        return sqLiteBD;
    }
}
