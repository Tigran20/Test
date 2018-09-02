package com.example.android.medstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.medstore.data.MedContract.MedicationEntry;

public class MedProvider extends ContentProvider {

    public static final String LOG_TAG = MedProvider.class.getSimpleName();
    private static final int MEDICS = 900;
    private static final int MED_ID = 901;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_MED, MEDICS);
        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_MED + "/#", MED_ID);
    }

    private MedDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new MedDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICS:
                cursor = database.query(MedicationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MED_ID:
                selection = MedicationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MedicationEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICS:
                return insertMed(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMed(Uri uri, ContentValues values) {
        String name = values.getAsString(MedicationEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = values.getAsInteger(MedicationEntry.COLUMN_PRODUCT_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Pet requires valid price");
        }

        Integer quantity = values.getAsInteger(MedicationEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        String supplier = values.getAsString(MedicationEntry.COLUMN_SUPPLIER_NAME);
        if (supplier == null) {
            throw new IllegalArgumentException("Product requires supplier name");
        }

        String contacts = values.getAsString(MedicationEntry.COLUMN_CONTACTS);
        if (contacts == null) {
            throw new IllegalArgumentException("Product requires supplier contacts");
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(MedicationEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICS:
                return updateMed(uri, contentValues, selection, selectionArgs);
            case MED_ID:
                selection = MedicationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMed(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMed(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(MedicationEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(MedicationEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(MedicationEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(MedicationEntry.COLUMN_PRODUCT_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (values.containsKey(MedicationEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(MedicationEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        if (values.containsKey(MedicationEntry.COLUMN_SUPPLIER_NAME)) {
            String supplier = values.getAsString(MedicationEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Product requires a supplier name");
            }
        }

        if (values.containsKey(MedicationEntry.COLUMN_CONTACTS)) {
            String contacts = values.getAsString(MedicationEntry.COLUMN_CONTACTS);
            if (contacts == null) {
                throw new IllegalArgumentException("Product requires valid supplier contacts");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated = database.update(MedicationEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICS:
                rowsDeleted = database.delete(MedicationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MED_ID:
                selection = MedicationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(MedicationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICS:
                return MedicationEntry.CONTENT_LIST_TYPE;
            case MED_ID:
                return MedicationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

}
