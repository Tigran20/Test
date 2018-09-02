package com.example.android.medstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.medstore.data.MedContract.MedicationEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MED_LOADER = 8;

    private MedCursorAdapter cursorAdapter;
    private TextView listQuantityTV;
    private ImageView buyImage;
    private FloatingActionButton fab;
    private ListView medListView;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        fab = findViewById(R.id.fab);
        medListView = findViewById(R.id.list);
        emptyView = findViewById(R.id.empty_view);
        listQuantityTV = findViewById(R.id.quantity);
        buyImage= findViewById(R.id.buy_cart);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        medListView.setEmptyView(emptyView);
        cursorAdapter = new MedCursorAdapter(this, null);

        medListView.setAdapter(cursorAdapter);
        medListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, MedDetailsActivity.class);
                Uri currentMedUri = ContentUris.withAppendedId(MedicationEntry.CONTENT_URI, id);
                intent.setData(currentMedUri);
                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(MED_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MedicationEntry._ID,
                MedicationEntry.COLUMN_PRODUCT_NAME,
                MedicationEntry.COLUMN_PRODUCT_PRICE,
                MedicationEntry.COLUMN_PRODUCT_QUANTITY,
                MedicationEntry.COLUMN_SUPPLIER_NAME,
                MedicationEntry.COLUMN_CONTACTS};

        return new CursorLoader(this,
                MedicationEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
