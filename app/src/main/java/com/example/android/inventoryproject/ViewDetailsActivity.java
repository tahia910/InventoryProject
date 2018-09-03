package com.example.android.inventoryproject;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryproject.data.FruitContract;
import com.example.android.inventoryproject.data.FruitContract.FruitEntry;
import com.example.android.inventoryproject.data.FruitProvider;

/**
 * This activity is used to see all the details of a single product.
 */
public class ViewDetailsActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {

    // Identifier for the fruit data loader.
    private static final int EXISTING_FRUIT_LOADER = 0;

    // Content URI for the existing fruit (null if it's a new fruit)
    private Uri currentFruitUri;

    // TextViews that will be updated with the fruit information.
    private TextView nameEditText;
    private TextView quantityEditText;
    private TextView priceEditText;
    private TextView supplierNameEditText;
    private TextView supplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        // Set the title.
        setTitle(getString(R.string.title_view_details));

        // Retrieve the product URI from the intent that opened this activity.
        Intent intent = getIntent();
        currentFruitUri = intent.getData();
        getLoaderManager().initLoader(EXISTING_FRUIT_LOADER, null, this);

        // Find the TextViews that will be updated.
        nameEditText = (TextView) findViewById(R.id.edit_name);
        quantityEditText = (TextView) findViewById(R.id.edit_quantity);
        priceEditText = (TextView) findViewById(R.id.edit_price);
        supplierNameEditText = (TextView) findViewById(R.id.edit_supplier_name);
        supplierPhoneEditText = (TextView) findViewById(R.id.edit_supplier_phone);

        // Set up the product quantity's minus and plus buttons.
        // The minus button will decrease the product quantity by 1.
        Button minusButton = (Button) findViewById(R.id.minus_one_quantity_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityMinusOne();
            }
        });
        // The plus button will increase the product quantity by 1.
        Button plusButton = (Button) findViewById(R.id.plus_one_quantity_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityPlusOne();
            }
        });

        // Set up the order and delete buttons.
        // The order button will open a phone app to call the product supplier.
        Button orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderProductByPhone();
            }
        });

        // The delete button will delete the product currently viewed.
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    /**
     * This is the method used by the minus button to decrease the product quantity by 1.
     */
    public void quantityMinusOne() {
        // Define a projection that specifies which columns from the database will be queried.
        String[] projection = {FruitEntry._ID, FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG};
        // Perform a query on the provider using the ContentResolver. It is done on a background
        // thread with the CursorLoader.
        Cursor cursor = new CursorLoader(ViewDetailsActivity.this, currentFruitUri, projection,
                null, null, null).loadInBackground();

        // Move to the first row of the cursor and retrieve we need the data from it (the product
        // quantity).
        cursor.moveToFirst();
        int quantityColumnIndex = cursor.getColumnIndex(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG);
        int currentFruitQuantity = cursor.getInt(quantityColumnIndex);

        // The value can't be negative. If the quantity is already zero, don't decrease.
        if (currentFruitQuantity == 0) {
            return;
        } else {
            currentFruitQuantity = currentFruitQuantity - 1;
        }
        // Update the fruit quantity displayed on the screen.
        quantityEditText.setText(Integer.toString(currentFruitQuantity));

        // Using the Content Provider, update the fruit quantity value in the database.
        ContentValues values = new ContentValues();
        values.put(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG, currentFruitQuantity);
        getContentResolver().update(currentFruitUri, values, FruitEntry
                .COLUMN_PRODUCT_QUANTITY_IN_KG, null);
        cursor.close();
    }

    /**
     * This is the method used by the plus button to increase the product quantity by 1.
     */
    public void quantityPlusOne() {
        // Define a projection that specifies which columns from the database will be queried.
        String[] projection = {FruitEntry._ID, FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG};
        // Perform a query on the provider using the ContentResolver. It is done on a background
        // thread with the CursorLoader.
        Cursor cursor = new CursorLoader(ViewDetailsActivity.this, currentFruitUri, projection,
                null, null, null).loadInBackground();

        // Move to the first row of the cursor and retrieve the data we need from it (the product
        // quantity).
        cursor.moveToFirst();
        int quantityColumnIndex = cursor.getColumnIndex(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG);
        int currentFruitQuantity = cursor.getInt(quantityColumnIndex);

        // Increase the fruit quantity by 1.
        currentFruitQuantity = currentFruitQuantity + 1;
        // Update the fruit quantity displayed on the screen.
        quantityEditText.setText(Integer.toString(currentFruitQuantity));

        // Using the Content Provider, update the fruit quantity value in the database.
        ContentValues values = new ContentValues();
        values.put(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG, currentFruitQuantity);
        getContentResolver().update(currentFruitUri, values, FruitEntry
                .COLUMN_PRODUCT_QUANTITY_IN_KG, null);
        cursor.close();
    }

    /**
     * This is the method used by the order button to open a phone app and call the product
     * supplier.
     */
    public void orderProductByPhone() {
        // Define a projection that specifies which columns from the database will be queried.
        String[] projection = {FruitEntry._ID, FruitEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};
        // Perform a query on the provider using the ContentResolver. It is done on a background
        // thread with the CursorLoader.
        Cursor cursor = new CursorLoader(ViewDetailsActivity.this, currentFruitUri, projection,
                null, null, null).loadInBackground();

        // Move to the first row of the cursor and retrieve the data we need from it (the phone
        // number of the product supplier).
        cursor.moveToFirst();
        int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(FruitEntry
                .COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        String currentFruitSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

        // Using an intent to a phone app, we call the phone number we retrieved.
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + currentFruitSupplierPhoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    /**
     * The loader will executive the ContentProvider's query method on a background thread.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies which columns from the database will be queried.
        String[] projection = {FruitEntry._ID, FruitEntry.COLUMN_PRODUCT_NAME, FruitEntry
                .COLUMN_PRODUCT_PRICE_PER_KG, FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG,
                FruitEntry.COLUMN_PRODUCT_SUPPLIER_NAME, FruitEntry
                .COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};
        // Perform a query on the provider using the ContentResolver.
        return new CursorLoader(this, currentFruitUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Move to the first row of the cursor and retrieve the data from it.
        if (cursor.moveToFirst()) {
            // Find the columns of the attributes we will use from the cursor.
            int nameColumnIndex = cursor.getColumnIndex(FruitEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(FruitEntry
                    .COLUMN_PRODUCT_QUANTITY_IN_KG);
            int priceColumnIndex = cursor.getColumnIndex(FruitEntry.COLUMN_PRODUCT_PRICE_PER_KG);
            int supplierNameColumnIndex = cursor.getColumnIndex(FruitEntry
                    .COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(FruitEntry
                    .COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Get the attributes we need from the cursor.
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Update the EditText views with the attributes retrieved from the cursor.
            nameEditText.setText(name);
            quantityEditText.setText(Integer.toString(quantity));
            priceEditText.setText(Integer.toString(price));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        nameEditText.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_view_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    /**
     * Defines what will happen when the user click on an option of the menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                // Open the EditorActivity to edit the product currently viewed.
                Intent intent = new Intent(this, EditorActivity.class);
                intent.setData(currentFruitUri);
                startActivity(intent);
                return true;
            case android.R.id.home:
                // Go to the MainActivity (where the user can review all the products in the
                // database).
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Dialog that confirms if the user wants to delete a product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder. Set the message and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so the product is deleted.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    // User clicked the "Cancel" button, so dismiss the dialog
                    // and continue editing the product.
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete operation if this is an existing fruit.
        if (currentFruitUri != null) {
            // Use the ContentResolver to delete the pet at the given content URI.
            int rowsDeleted = getContentResolver().delete(currentFruitUri, null, null);

            // Inform the user if the operation has been successful or not.
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast
                        .LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast
                        .LENGTH_SHORT).show();
            }
        }
        finish();
    }
}