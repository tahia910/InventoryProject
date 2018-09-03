package com.example.android.inventoryproject.data;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

//This is the Contract class that defines the name of tables and constants.
public final class FruitContract {

    // Empty constructor to prevent misuse of the FruitContract class.
    private FruitContract() {}

    // Content Authority name, necessary for the Content Provider. We use the package
    // name of the app.
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryproject";

    // Base to create the URI for the Content Provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // One of the possible path added after the base URI for the Content Provider.
    public static final String PATH_FRUITS = "Fruits";

    /**
     * This is the inner class that defines constant values for the fruits database table.
     * Each entry in the table represents a single fruit.
     */
    public static final class FruitEntry implements BaseColumns {
        // The content URI to access the data in the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FRUITS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRUITS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRUITS;

        /** Name of database table for the fruits */
        public final static String TABLE_NAME = "Fruits";

        /**
         * Unique ID number for the fruit (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the fruit.
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="Name";

        /**
         * Price of the fruit per kilogram.
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE_PER_KG = "Price";

        /**
         * Price of fruit in kilogram.
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY_IN_KG = "Quantity";

        /**
         * Name of the supplier for this fruit.
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "SupplierName";

        /**
         * Phone number of the supplier for this fruit.
         * Type: TEXT
         * I used the type TEXT because the phone number format may differ depending on the
         * country.
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "SupplierPhoneNumber";
    }
}