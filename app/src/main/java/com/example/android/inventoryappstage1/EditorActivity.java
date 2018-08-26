package com.example.android.inventoryappstage1;

import android.app.LoaderManager;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.android.inventoryappstage1.data.ProductContract;
import com.example.android.inventoryappstage1.data.ProductContract.ProductEntry;
import com.example.android.inventoryappstage1.data.ProductDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //identifier product data loader
    private static final int EXISTING_PRODUCTLOADER = 0;
    private Uri mCurrentProductUri;
    private EditText mProdNameEdit;
    private EditText mPriceEdit;
    private EditText mQuantityEdit;
    private EditText mSuppNameEdit;
    private EditText mSuppPhoneEdit;

    //flag to keep track if the product was edited (true) or not (false)
    private boolean mProductHasChanged = false;
    //listener to recognize touches to find out if a view is modified. therefore mProductHasChanged
    //needs to be set on true
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_editor );

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null){
            setTitle( getString( R.string.editor_add_product ) );
            //to not show the delete button; (nothing what's not created yet can be deleted)
            invalidateOptionsMenu();
        }else {
            setTitle( getString( R.string.editor_edit_product ) );
            //loader to read data from the database
           getLoaderManager().initLoader(EXISTING_PRODUCTLOADER,null, this);
        }
        //views to read user Input from
        mProdNameEdit = (EditText) findViewById( R.id.edit_product_name );
        mPriceEdit = (EditText) findViewById( R.id.edit_product_price );
        mQuantityEdit = (EditText) findViewById( R.id.edit_product_quantity );
        mSuppNameEdit = (EditText) findViewById( R.id.edit_supplier_name );
        mSuppPhoneEdit = (EditText) findViewById( R.id.edit_supplier_phonenumber );

        mProdNameEdit.setOnTouchListener( mTouchListener );
        mPriceEdit.setOnTouchListener( mTouchListener );
        mQuantityEdit.setOnTouchListener( mTouchListener );
        mSuppNameEdit.setOnTouchListener( mTouchListener );
        mSuppPhoneEdit.setOnTouchListener( mTouchListener );
    }
    //get user input and save new product into database
    private void saveProduct(){
        //Read from input fields
        //use trim to eliminate leading or trailing white spaces
        String nameString = mProdNameEdit.getText().toString().trim();
        String priceString = mPriceEdit.getText().toString().trim();
        int price = Integer.parseInt( priceString );
        String quantityString = mQuantityEdit.getText().toString().trim();
        int quantity = Integer.parseInt( quantityString );
        String suppliernameString = mSuppNameEdit.getText().toString().trim();
        String phonenrString = mSuppPhoneEdit.getText().toString().trim();
     //   int phonenr = Integer.parseInt( phonenrString );
        //create database helper
        ProductDbHelper mDbHelper = new ProductDbHelper( this );
        //get data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //create ContentValues object, column names are keys and product attributes from the editor are values
        ContentValues values = new ContentValues(  );
        values.put( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, price );
        values.put( ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put( ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIERNAME, suppliernameString );
        values.put( ProductContract.ProductEntry.COLUMN_PRODUCT_PHONENR, phonenrString );

      //determine if this is a new or existing product, to determine check mCurrentProductUri
        if (mCurrentProductUri == null){
            //this case new product following insert new product into provider returning the contetn uri
            Uri newUri = getContentResolver().insert( ProductEntry.CONTENT_URI,values);
            //showing toast if successful insertion or not
            if(newUri == null){
                //here for insertion error
                Toast.makeText( this, getString( R.string.editor_insert_product_failed ),
                        Toast.LENGTH_SHORT).show();
            }else{
                //here for successful insertion
                Toast.makeText( this, getString( R.string.editor_insert_product_successful ),
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            //this case is for the editing of an existing product
            int editUri = getContentResolver().update( mCurrentProductUri,values,null,null );
            //toasts for successful or unsuccessful editing of a product
            if (editUri==0){
                //unsuccessful editing
                Toast.makeText( this, getString( R.string.editor_update_product_failed ),
                        Toast.LENGTH_SHORT).show();
            }else{
                //successful editing
                Toast.makeText( this, getString( R.string.editor_insert_product_successful ),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_editor, menu );
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // this is performed after a click on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Response to a click on the "Save" button
            case R.id.action_save:
                // save product to database
                saveProduct();
                //exit activity
                finish();
                return true;
            // Response to a click on the "Delete" button
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                // Navigate back to parent activity when no changes made
                if(!mProductHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                //when unsaved changes set up a dialog
                DialogInterface.OnClickListener discardButtonClickListener=
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //when clicked on discard button
                        NavUtils.navigateUpFromSameTask( EditorActivity.this );
                    }
                };
                //dialog to show unsafed changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    // mehtod called when back button pressed
    @Override
    public void onBackPressed() {
        //handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIERNAME,
                ProductEntry.COLUMN_PRODUCT_PHONENR
        };
        //execute ContentProviders query method on a background thread
        return new CursorLoader( this,mCurrentProductUri,projection,null,null,null );
    }
    @Override
    public void onLoadFinished(Loader<Cursor>loader, Cursor cursor){
        //retutn when the cursor is null
        if (cursor == null || cursor.getCount()<1){
            return;
        }
        //reading data from the first row of the cursor
        if (cursor.moveToFirst()){
            //find the right columns that are interesting
            int nameColumnIndex = cursor.getColumnIndex( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME );
            int priceColumnIndex = cursor.getColumnIndex( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE );
            int quantityColumnIndex = cursor.getColumnIndex( ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY );
            int supplierColumnIndex = cursor.getColumnIndex( ProductEntry.COLUMN_PRODUCT_SUPPLIERNAME );
            int suppphoneColumnIndex = cursor.getColumnIndex( ProductEntry.COLUMN_PRODUCT_PHONENR );
            //to read the attributes from the current product
            String prodName = cursor.getString( nameColumnIndex );
            int prodPrice = cursor.getInt( priceColumnIndex );
            int prodQuantity = cursor.getInt( quantityColumnIndex );
            String prodSupname = cursor.getString( supplierColumnIndex );
            int prodsuppphone = cursor.getInt( suppphoneColumnIndex );
            //updateviews
            mProdNameEdit.setText( prodName );
            mPriceEdit.setText(Integer.toString( prodPrice ));
            mQuantityEdit.setText(Integer.toString( prodQuantity ));
            mSuppNameEdit.setText( prodSupname );
            mSuppPhoneEdit.setText(Integer.toString( prodsuppphone) );
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor>loader){
        //clear the input fields when loader is invalid
        mProdNameEdit.setText( "" );
        mPriceEdit.setText( "" );
        mQuantityEdit.setText( "" );
        mSuppNameEdit.setText( "" );
        mSuppPhoneEdit.setText( "" );
    }
    //dialog to warn the user of unsaved changes
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    //perform deletion
    private void deleteProduct() {
        //if thr product exists
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete( mCurrentProductUri, null, null );
            if (rowsDeleted == 0) {
                Toast.makeText( this, getString( R.string.editor_delete_product_failed ),
                        Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( this, getString( R.string.editor_delete_product_successful ),
                    Toast.LENGTH_SHORT ).show();
        }
    }
    finish();
    }

}
