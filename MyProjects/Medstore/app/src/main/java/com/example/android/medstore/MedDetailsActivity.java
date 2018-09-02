package com.example.android.medstore;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.medstore.data.MedContract.MedicationEntry;

public class MedDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_MED_LOADER = 0;

    private Uri currentMedUri;

    private TextView nameTV;
    private TextView priceTV;
    private TextView quantityTV;
    private TextView supplierTV;
    private TextView contactsTV;

    private Button decreaseButton;
    private Button increaseButton;

    private Button callBtn;
    private boolean isMedHasChanged = false;
    private int quantity;

    public void addQuantity(View view) {
        quantity++;
        quantityTV.setText(String.valueOf(quantity));
    }

    public void decreaseQuantity(View view) {
        quantity--;
        if (quantity < 0) {
            quantity = 0;
        }
        quantityTV.setText(String.valueOf(quantity));
    }

    private void callMd() {
        if (!TextUtils.isEmpty(contactsTV.getText().toString())) {
            Intent intentCall = new Intent(Intent.ACTION_DIAL);
            intentCall.setData(Uri.parse("tel:" + contactsTV.getText().toString()));
            startActivity(intentCall);
        }
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            isMedHasChanged = true;
            return false;
        }
    };

    private Button.OnTouchListener buttonTouchListener = new Button.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            isMedHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        Intent intent = getIntent();
        currentMedUri = intent.getData();
        setTitle(getString(R.string.editor_activity_title_product_details));
        getLoaderManager().initLoader(EXISTING_MED_LOADER, null, this);

        nameTV = findViewById(R.id.prod_name_tv);
        priceTV = findViewById(R.id.prod_price_tv);
        quantityTV = findViewById(R.id.prod_quant_tv);
        supplierTV = findViewById(R.id.prod_sup_tv);
        contactsTV = findViewById(R.id.sup_cont_tv);
        callBtn = findViewById(R.id.order_button);

        nameTV.setOnTouchListener(touchListener);
        priceTV.setOnTouchListener(touchListener);
        quantityTV.setOnTouchListener(touchListener);
        supplierTV.setOnTouchListener(touchListener);
        contactsTV.setOnTouchListener(touchListener);

        increaseButton = findViewById(R.id.decrease_button);
        decreaseButton = findViewById(R.id.increase_button);

        increaseButton.setOnTouchListener(buttonTouchListener);
        decreaseButton.setOnTouchListener(buttonTouchListener);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMd();
            }
        });
    }


    private void saveProduct() {
        String nameString = nameTV.getText().toString().trim();
        String priceString = priceTV.getText().toString().trim();
        String quantityString = quantityTV.getText().toString().trim();
        String supplierString = supplierTV.getText().toString().trim();
        String contactsString = contactsTV.getText().toString().trim();

        if (currentMedUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(contactsString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MedicationEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(MedicationEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(MedicationEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(MedicationEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(MedicationEntry.COLUMN_CONTACTS, contactsString);

        if (currentMedUri == null) {
            Uri newUri = getContentResolver().insert(MedicationEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentMedUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentMedUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!isMedHasChanged) {
                    NavUtils.navigateUpFromSameTask(MedDetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(MedDetailsActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isMedHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                MedicationEntry._ID,
                MedicationEntry.COLUMN_PRODUCT_NAME,
                MedicationEntry.COLUMN_PRODUCT_PRICE,
                MedicationEntry.COLUMN_PRODUCT_QUANTITY,
                MedicationEntry.COLUMN_SUPPLIER_NAME,
                MedicationEntry.COLUMN_CONTACTS
        };

        return new CursorLoader(this,
                currentMedUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(MedicationEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(MedicationEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(MedicationEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(MedicationEntry.COLUMN_SUPPLIER_NAME);
            int contactsColumnIndex = cursor.getColumnIndex(MedicationEntry.COLUMN_CONTACTS);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String contacts = cursor.getString(contactsColumnIndex);

            nameTV.setText(name);
            priceTV.setText(String.valueOf(price));
            quantityTV.setText(String.valueOf(quantity));
            supplierTV.setText(supplier);
            contactsTV.setText(contacts);
            this.quantity = quantity;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameTV.setText("");
        priceTV.setText("");
        quantityTV.setText("");
        supplierTV.setText("");
        contactsTV.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentMedUri != null) {
            int rowsDeleted = getContentResolver().delete(currentMedUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
