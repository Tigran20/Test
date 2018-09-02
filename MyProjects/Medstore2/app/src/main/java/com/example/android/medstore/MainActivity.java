package com.example.android.medstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {

    private Button callBtn;
    private MedDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MedDbHelper(this);
        callBtn = findViewById(R.id.new_button);

        callBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                insertData();
                displayDatabaseInfo();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                MedContract.MedicationEntry._ID,
                MedContract.MedicationEntry.COLUMN_PRODUCT_NAME,
                MedContract.MedicationEntry.COLUMN_PRODUCT_PRICE,
                MedContract.MedicationEntry.COLUMN_PRODUCT_QUANTITY,
                MedContract.MedicationEntry.COLUMN_SUPPLIER_NAME,
                MedContract.MedicationEntry.COLUMN_CONTACTS
        };

        Cursor cursor = db.query(MedContract.MedicationEntry.TABLE_NAME,
                projection,
                null, null, null, null, null);

        TextView displayView = findViewById(R.id.text_view);


        try {
            displayView.setText("The inventory table contains " + cursor.getCount() + " items.\n\n");
            displayView.append(
                    MedContract.MedicationEntry._ID + " - " +
                            MedContract.MedicationEntry.COLUMN_PRODUCT_NAME + " - " +
                            MedContract.MedicationEntry.COLUMN_PRODUCT_PRICE + " - " +
                            MedContract.MedicationEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                            MedContract.MedicationEntry.COLUMN_SUPPLIER_NAME + " - " +
                            MedContract.MedicationEntry.COLUMN_CONTACTS +
                            "\n");

            int idColumnIndex = cursor.getColumnIndex(MedContract.MedicationEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(MedContract.MedicationEntry.COLUMN_PRODUCT_NAME);
            int price = cursor.getColumnIndex(MedContract.MedicationEntry.COLUMN_PRODUCT_PRICE);
            int quantity = cursor.getColumnIndex(MedContract.MedicationEntry.COLUMN_PRODUCT_QUANTITY);
            int suplierName = cursor.getColumnIndex(MedContract.MedicationEntry.COLUMN_SUPPLIER_NAME);
            int contacts = cursor.getColumnIndex(MedContract.MedicationEntry.COLUMN_CONTACTS);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(price);
                int currentQuantity = cursor.getInt(quantity);
                String currentSuplierName = cursor.getString(suplierName);
                int currentContacts = cursor.getInt(contacts);

                displayView.append("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSuplierName + " - " +
                        currentContacts);
            }
        } finally {
            cursor.close();
        }
    }

    private void insertData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MedContract.MedicationEntry.COLUMN_PRODUCT_NAME, "DrugFSD");
        values.put(MedContract.MedicationEntry.COLUMN_PRODUCT_PRICE, 450);
        values.put(MedContract.MedicationEntry.COLUMN_PRODUCT_QUANTITY, 15);
        values.put(MedContract.MedicationEntry.COLUMN_SUPPLIER_NAME, "MillaBirke");
        values.put(MedContract.MedicationEntry.COLUMN_CONTACTS, 795131);
        db.insert(MedContract.MedicationEntry.TABLE_NAME,null, values);
    }
}