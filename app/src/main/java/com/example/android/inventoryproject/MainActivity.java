package com.example.android.inventoryproject;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryproject.data.FruitContract.FruitEntry;

/**
 * Main Activity displays the list of products (fruits) that are stored in the inventory.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {

    // Global variable for the Cursor Adapter used to display the database information through a
    // ListView.
    FruitCursorAdapter fruitCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Floating Action Button that will open the Editor Activity to add a new
        // fruit in the database.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        final ListView fruitListView = (ListView) findViewById(R.id.list_view_fruits);

        // Find and set an empty view that will be displayed if there is no fruits in the database.
        View emptyView = findViewById(R.id.empty_text_view);
        fruitListView.setEmptyView(emptyView);

        // Get the CursorAdapter to set up the ListView of fruits.
        fruitCursorAdapter = new FruitCursorAdapter(this, null);
        fruitListView.setAdapter(fruitCursorAdapter);

        // Define the action that will happen when an item of the ListView is clicked.
        // It will open the ViewDetailsActivity, to see the item details.
        fruitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Context context = fruitListView.getContext();
                Intent intent = new Intent(context, ViewDetailsActivity.class);
                Uri currentFruitUri = ContentUris.withAppendedId(FruitEntry.CONTENT_URI, id);
                intent.setData(currentFruitUri);
                context.startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     *  The loader will executive the ContentProvider's query method on a background thread.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies which columns from the database will be queried.
        String[] projection = {FruitEntry._ID, FruitEntry.COLUMN_PRODUCT_NAME, FruitEntry
                .COLUMN_PRODUCT_PRICE_PER_KG, FruitEntry.COLUMN_PRODUCT_QUANTITY_IN_KG,
                FruitEntry.COLUMN_PRODUCT_SUPPLIER_NAME, FruitEntry
                .COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};

        // Perform a query on the provider using the ContentResolver.
        // Use the {@link FruitEntry#CONTENT_URI} to access the fruit data.
        return new CursorLoader(this, FruitEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        fruitCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fruitCursorAdapter.swapCursor(null);
    }
}
