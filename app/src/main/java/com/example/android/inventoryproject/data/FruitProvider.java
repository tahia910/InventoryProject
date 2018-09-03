package com.example.android.inventoryproject.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryproject.data.FruitContract.FruitEntry;

/**
 * {@link ContentProvider} for the Inventory app.
 */
public class FruitProvider extends ContentProvider {
    // Tag for the log messages
    public static final String LOG_TAG = FruitProvider.class.getSimpleName();

    // URI matcher code for the content URI for the fruits table
    private static final int FRUITS = 100;

    // URI matcher code for the content URI for a single fruit in the fruits table
    private static final int FRUIT_ID = 101;

    // The UriMatcher will match a content URI to a corresponding code for the fruits table.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Initialize the UriMatcher with the possible path options.
    static {
        sUriMatcher.addURI(FruitContract.CONTENT_AUTHORITY, FruitContract.PATH_FRUITS, FRUITS);

        sUriMatcher.addURI(FruitContract.CONTENT_AUTHORITY, FruitContract.PATH_FRUITS + "/#",
                FRUIT_ID);
    }

    // Database helper object.
    private FruitDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new FruitDbHelper(getContext());
        return true;
    }

    /**
     * Query the database using the UriMatcher.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        // After receiving the cursor containing the URI, this switch tries the different path
        // options available.
        int match = sUriMatcher.match(uri);
        switch (match) {
            // This option will query the whole database.
            case FRUITS:
                cursor = database.query(FruitEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            // This option will query a single fruit, based on its ID.
            case FRUIT_ID:
                selection = FruitEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(FruitEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            // This option is in case none of the options above worked.
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor.
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        // Catch the error if the uri was not valid.
        switch (match) {
            case FRUITS:
                return insertFruit(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a fruit into the database with the given content values.
     * Return the new content URI for that specific row in the database.
     */
    private Uri insertFruit(Uri uri, ContentValues values) {

        // Check if all the values given are not null and catch the error if any is null.
        String name = values.getAsString(FruitEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = values.getAsInteger(FruitEntry.COLUMN_PRODUCT_PRICE_PER_KG);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        Integer quantity = values.getAsInteger(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }

        String supplierName = values.getAsString(FruitEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Product requires a supplier name");
        }

        String supplierPhoneNumber = values.getAsString(FruitEntry
                .COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Product requires a supplier phone number");
        }

        // Insert the new fruit with the given values.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(FruitEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the fruit content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[]
            selectionArgs) {
        final int match = sUriMatcher.match(uri);
        // Based on the URI, check if we are going to update one single fruit, the whole table or
        // catch an error if the URI is not valid.
        switch (match) {
            case FRUITS:
                return updateFruit(uri, contentValues, selection, selectionArgs);
            case FRUIT_ID:
                selection = FruitEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateFruit(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update a fruit into the database with the given content values.
     * Return the number of rows that were updated.
     */
    private int updateFruit(Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {

        // Check if any value have been changed to null, and catch the error if any is null.
        if (values.containsKey(FruitEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(FruitEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(FruitEntry.COLUMN_PRODUCT_PRICE_PER_KG)) {
            Integer price = values.getAsInteger(FruitEntry.COLUMN_PRODUCT_PRICE_PER_KG);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (values.containsKey(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG)) {
            Integer quantity = values.getAsInteger(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        if (values.containsKey(FruitEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(FruitEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Product requires a supplier name");
            }
        }

        if (values.containsKey(FruitEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(FruitEntry
                    .COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumber == null) {
                throw new IllegalArgumentException("Product requires a supplier name");
            }
        }

        // If there are no values to update, then don't try to update the database.
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, update the database, get the number of rows updated and notify all
        // listeners that the given URI has changed.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(FruitEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        // Based on the URI, check if we are going to delete one single fruit, the whole table or
        // catch an error if the URI is not valid.
        switch (match) {
            case FRUITS:
                rowsDeleted = database.delete(FruitEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FRUIT_ID:
                selection = FruitEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(FruitEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // Get the number of rows deleted and notify all listeners that the given URI has changed.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data in the content provider
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FRUITS:
                return FruitEntry.CONTENT_LIST_TYPE;
            case FRUIT_ID:
                return FruitEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}