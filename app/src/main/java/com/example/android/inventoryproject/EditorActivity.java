package com.example.android.inventoryproject;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryproject.data.FruitContract;
import com.example.android.inventoryproject.data.FruitContract.FruitEntry;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {

    // Identifier for the fruit data loader.
    private static final int EXISTING_FRUIT_LOADER = 0;

    // Content URI for the existing fruit (null if it's a new fruit)
    private Uri currentFruitUri;

    // EditText fields to enter the fruit information.
    private EditText nameEditText;
    private EditText quantityEditText;
    private EditText priceEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneEditText;

    // Boolean flag that keeps track of whether the fruit has been edited (true) or not (false)
    private boolean fruitHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View.
     * If the user didn't modify any View, the boolean will become false.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            fruitHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // The EditorActivity is used both when we add a new fruit or when we edit an existing one.
        // Check first if an intent was used to launch the activity, to change the title
        // accordingly.
        Intent intent = getIntent();
        currentFruitUri = intent.getData();
        if (currentFruitUri == null) {
            // There was no intent, so we are adding a new fruit. Change the title.
            setTitle(getString(R.string.title_add_a_product));
            invalidateOptionsMenu();
        } else {
            // There was an intent, so we are modifying an existing fruit.
            // Change the title and get the fruit information.
            setTitle(getString(R.string.title_edit_a_product));
            getLoaderManager().initLoader(EXISTING_FRUIT_LOADER, null, this);
        }

        // Find the EditText views.
        nameEditText = (EditText) findViewById(R.id.edit_name);
        quantityEditText = (EditText) findViewById(R.id.edit_quantity);
        priceEditText = (EditText) findViewById(R.id.edit_price);
        supplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        supplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone);

        // Then set the OnTouchListeners for all the EditText views, to know if the user touched
        // them.
        nameEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneEditText.setOnTouchListener(touchListener);
    }


    /**
     * Get user input from editor and save the product into the database.
     */
    private void saveProduct() {
        // Get the user input in the EditText.
        String nameString = nameEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        // Check if the input are empty and set up a toast with a different message depending on
        // the situation, to warn the user that all information are necessary to save a fruit in
        // the database.
        if (currentFruitUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty
                (quantityString) && TextUtils.isEmpty(priceString)) {
            // Option for a new fruit with no information input.
            Toast.makeText(this, getString(R.string.editor_all_empty_information), Toast
                    .LENGTH_LONG).show();
        } else if (currentFruitUri == null && (TextUtils.isEmpty(nameString) || TextUtils.isEmpty
                (quantityString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty
                (supplierNameString) || TextUtils.isEmpty(supplierPhoneString))) {
            // Option for a new fruit with one or several information empty.
            Toast.makeText(this, getString(R.string.editor_empty_information), Toast.LENGTH_LONG)
                    .show();

        } else if (currentFruitUri != null && (TextUtils.isEmpty(nameString) || TextUtils.isEmpty
                (quantityString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty
                (supplierNameString) || TextUtils.isEmpty(supplierPhoneString))) {
            // Option for an existing fruit that has been modified and is now missing one or
            // several information.
            Toast.makeText(this, getString(R.string.editor_empty_information), Toast.LENGTH_LONG)
                    .show();

        } else {
            // If no information are missing, then proceed to create a ContentValues object to add
            // or edit the fruit to the database.
            ContentValues values = new ContentValues();
            values.put(FruitEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(FruitEntry.COLUMN_PRODUCT_PRICE_PER_KG, priceString);
            values.put(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG, quantityString);
            values.put(FruitEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
            values.put(FruitEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

            // Check if we are going to create a new fruit or edit an existing one by checking
            // the URI.
            if (currentFruitUri == null) {
                // Add a new fruit in the database. Create a new URI for the new fruit.
                Uri newUri = getContentResolver().insert(FruitContract.FruitEntry.CONTENT_URI,
                        values);
                // Inform the user if the operation has been successful or not.
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast
                            .LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Edit an existing fruit in the database. Check which row(s) will be affected.
                int rowsAffected = getContentResolver().update(currentFruitUri, values, null, null);

                // Inform the user if the operation has been successful or not.
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_product_failed), Toast
                            .LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If we are adding a new fruit, hide the Delete menu item.
        if (currentFruitUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * Defines what will happen when the user click on an option of the menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // Save the product into the database.
                saveProduct();
                break;
            case R.id.action_delete:
                // Show a dialog to confirm if the user really wants to delete the product.
                showDeleteConfirmationDialog();
                break;
            case android.R.id.home:
                // If the fruit was not modified, then go back to the previous activity.
                // When adding a new product, it will be the MainActivity.
                // When editing an existing product, it will be the ViewDetailsActivity.
                if (!fruitHasChanged) {
                    super.onBackPressed();
                    return true;
                }
                // If the fruit has been modified but the changes have not been saved, show a
                // dialog to confirm if the user really wants to leave the EditorActivity.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the fruit wasn't modified, then continue with handling back button press.
        if (!fruitHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise, show a dialog to confirm if the user really wants to leave the
        // EditorActivity without saving the changes.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     *  Dialog that confirms if the user wants to delete a product.
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
        // Once we deleted the product, we go to the MainActivity (instead of the
        // ViewDetailsActivity if we are coming from that activity).
        Intent intent = new Intent(EditorActivity.this, MainActivity.class);
        startActivity(intent);
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

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener
                                                  discardButtonClickListener) {
        // Create an AlertDialog.Builder. Set the message and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        // If the user clicks on the "Discard" button, then proceed to leave the EditorActivity
        // without saving the changes.
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        // If the user clicks  on the "Keep Editing" button, dismiss the dialog and continue
        // editing the product.
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}