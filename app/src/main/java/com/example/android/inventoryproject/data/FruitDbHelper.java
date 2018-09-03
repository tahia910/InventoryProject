package com.example.android.inventoryproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryproject.data.FruitContract.FruitEntry;

/**
 * Database helper that manages database creation and version management.
 */
public class FruitDbHelper extends SQLiteOpenHelper {

    // Version of the database file.
    private static final int DATABASE_VERSION = 1;

    // Name of the database file.
    private static final String DATABASE_NAME = "inventory.db";

    // String containing the SQL statement to create the fruits table.
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FruitEntry.TABLE_NAME + " " +
            "(" + FruitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FruitEntry
            .COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " + FruitEntry
            .COLUMN_PRODUCT_PRICE_PER_KG + " INTEGER NOT NULL DEFAULT 1, " + FruitEntry
            .COLUMN_PRODUCT_QUANTITY_IN_KG + " INTEGER NOT NULL DEFAULT 1, " + FruitEntry
            .COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, " + FruitEntry
            .COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL" + ");";

    // This string is used to delete the table.
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FruitEntry
            .TABLE_NAME;

    /**
     * Constructs a new instance of {@link FruitDbHelper}.
     *
     * @param context of the app
     */
    public FruitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This method is called when the database is created.
    @Override
    public void onCreate(SQLiteDatabase FruitEntry) {
        FruitEntry.execSQL(SQL_CREATE_ENTRIES);
    }

    // This method is called when the database is upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase FruitEntry, int i, int i1) {
        FruitEntry.execSQL(SQL_DELETE_ENTRIES);
        onCreate(FruitEntry);
    }
}

