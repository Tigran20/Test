package com.example.android.medstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.medstore.data.MedContract.MedicationEntry;

public class MedDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MedDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "medics.db";
    private static final int DATABASE_VERSION = 1;


    public MedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MED_TABLE = "CREATE TABLE " + MedicationEntry.TABLE_NAME + " ("
                + MedicationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MedicationEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + MedicationEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + MedicationEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + MedicationEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + MedicationEntry.COLUMN_CONTACTS + " INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_MED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
