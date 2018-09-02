package com.example.android.medstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.medstore.data.MedContract.MedicationEntry;

public class MedCursorAdapter extends CursorAdapter {

    public MedCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.quantity);
        final ImageView NewImageView = view.findViewById(R.id.buy_cart);

        int idColumnIndex = cursor.getColumnIndex(MedicationEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(MedicationEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(MedicationEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(MedicationEntry.COLUMN_PRODUCT_QUANTITY);

        int medId = cursor.getInt(idColumnIndex);
        String medName = cursor.getString(nameColumnIndex);
        int medPrice = cursor.getInt(priceColumnIndex);
        final int medQuantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(medName);
        priceTextView.setText(String.valueOf(medPrice));
        quantityTextView.setText(String.valueOf(String.valueOf(medQuantity)));
        final Uri mCurrentCosUri = ContentUris.withAppendedId(MedicationEntry.CONTENT_URI, medId);


        NewImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medQuantity > 0) {
                    int currQunatity = medQuantity - 1;
                    quantityTextView.setText(String.valueOf(currQunatity));
                    ContentValues values = new ContentValues();
                    values.put(MedicationEntry.COLUMN_PRODUCT_QUANTITY, currQunatity);
                    context.getContentResolver().update(mCurrentCosUri, values, null, null);
                }
            }
        });

        if (quantityTextView.getText().equals("0")) {
           NewImageView.setVisibility(View.GONE);
        } else {
            NewImageView.setVisibility(View.VISIBLE);
        }

    }


}
