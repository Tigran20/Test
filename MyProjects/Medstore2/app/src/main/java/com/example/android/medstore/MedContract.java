package com.example.android.medstore;
import android.provider.BaseColumns;

public class MedContract {

    private MedContract() {

    }

    public static final class MedicationEntry implements BaseColumns {

        public final static String TABLE_NAME = "medics";

        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_CONTACTS = "contacts";
    }
}






