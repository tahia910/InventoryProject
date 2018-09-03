package com.example.android.inventoryproject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryproject.data.FruitContract.FruitEntry;

/**
 * {@link FruitCursorAdapter} is the adapter for the ListView used to display the information
 * from the database in the Main Activity.
 * It uses a {@link Cursor} of fruit data as its data source.
 */
public class FruitCursorAdapter extends CursorAdapter {

    Context context;

    /**
     * Constructs a new {@link FruitCursorAdapter}.
     *
     * @param context The context
     * @param cursor       The cursor from which to get the data.
     */
    public FruitCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
        this.context = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the data from the cursor to the given list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Set the variables for the TextViews from the layout.
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of the attributes we will use from the cursor.
        final int nameColumnIndex = cursor.getColumnIndex(FruitEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(FruitEntry.COLUMN_PRODUCT_PRICE_PER_KG);
        int quantityColumnIndex = cursor.getColumnIndex(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG);
        int idColumnIndex = cursor.getColumnIndex(FruitEntry._ID);

        // Get the attributes we need from the cursor.
        final String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);
        final int productId = cursor.getInt(idColumnIndex);

        // Update the TextViews with the attributes retrieved from the cursor.
        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);

        // Set the variable for the Sale Button.
        Button salesButton = (Button) view.findViewById(R.id.sales_button);
        salesButton.setFocusable(false);

        // When the button is clicked on, it will decrease by 1 the quantity of the current fruit.
        salesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an URI to define which fruit we are going to update.
                Uri updateUri = ContentUris.withAppendedId(FruitEntry
                        .CONTENT_URI, productId);

                // Get the fruit current quantity and decrease by 1.
                // The value can't be negative. If the quantity is already zero, don't decrease.
                int currentFruitQuantity = Integer.parseInt(productQuantity);
                if (currentFruitQuantity == 0) {
                    return;
                } else {
                    currentFruitQuantity = currentFruitQuantity - 1;
                }

                // Using the Content Provider, update the fruit quantity value.
                ContentValues values = new ContentValues();
                values.put(FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG, currentFruitQuantity);

                FruitCursorAdapter.this.context.getContentResolver().update(updateUri, values, FruitEntry
                        .COLUMN_PRODUCT_QUANTITY_IN_KG, null);
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}