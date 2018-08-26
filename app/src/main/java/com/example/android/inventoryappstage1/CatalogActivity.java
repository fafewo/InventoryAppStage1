package com.example.android.inventoryappstage1;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.example.android.inventoryappstage1.data.ProductContract;
import com.example.android.inventoryappstage1.data.ProductContract.ProductEntry;

import static com.example.android.inventoryappstage1.data.ProductProvider.LOG_TAG;

//for displaying the product list
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    ProductCursorAdapter mCursorAdapter;
    private Uri mCurrentProductUri;

    @Override
    protected void onCreate(Bundle savedInsanceState) {
        super.onCreate( savedInsanceState );
        setContentView( R.layout.activity_catalog );

        mCurrentProductUri = getIntent().getData();

        //make the fab open the EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( CatalogActivity.this, EditorActivity.class );
                startActivity( intent );
            }
        } );
        //finding the listview populated with the data from the products
        ListView prodListView = (ListView) findViewById( R.id.list_view_product );

        //set after find empty view to show the user what to do
        View emptyView = findViewById( R.id.empty_view );
        prodListView.setEmptyView( emptyView );
        //adapter to create a list item for each row of data, pass null for cursor as no data until loader finishes
        mCursorAdapter = new ProductCursorAdapter( this, null );
        prodListView.setAdapter( mCursorAdapter );

        //on item click listener
        prodListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent( CatalogActivity.this, DetailActivity.class );
                Uri currentProductUri = ContentUris.withAppendedId( ProductEntry.CONTENT_URI, id );
                intent.setData( currentProductUri );
                startActivity( intent );
            }
        } );
        //statrt loader
        getLoaderManager().initLoader( PRODUCT_LOADER, null, this );
    }

    private void deleteEveryProduct() {
        int productsDeleted = getContentResolver().delete( ProductEntry.CONTENT_URI, null, null );
        Log.v( "Catalog Activity", productsDeleted + " rows are now deleted from the Database" );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_catalog, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                deleteEveryProduct();
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //define projection that gives the columns from the table
        //for the detail view its every row
        //for the list view not every.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIERNAME,
                ProductEntry.COLUMN_PRODUCT_PHONENR
        };
        //loader to execute content provider query ona background thread
        return new CursorLoader( this, ProductEntry.CONTENT_URI, projection, null, null, null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //update cursor adapter with updated data
        mCursorAdapter.swapCursor( data );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor( null );

    }
}
